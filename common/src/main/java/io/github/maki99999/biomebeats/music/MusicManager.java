package io.github.maki99999.biomebeats.music;

import com.google.common.collect.EvictingQueue;
import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.config.MusicTrackConfig;
import io.github.maki99999.biomebeats.mixin.MixinWeighedSoundEvents;
import io.github.maki99999.biomebeats.service.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MusicManager implements IMusicManager, StreamPlayerListener, ConfigChangeListener {
    private ExecutorService executorService;
    private JavaStreamPlayer javaStreamPlayer;
    private final RandomSource rdm = RandomSource.create();

    private Collection<MusicTrack> musicTracks = null;
    private Collection<MusicGroup> musicGroups = null;

    private MusicTrack currentMusicTrack = null;
    private Collection<MusicTrack> currentMusicTracks = new HashSet<>();
    private final EvictingQueue<MusicTrack> recentMusicTracks = EvictingQueue.create(5);

    @Override
    public void init() {
        executorService = Executors.newSingleThreadExecutor();
        javaStreamPlayer = new JavaStreamPlayer();
        javaStreamPlayer.addStreamPlayerListener(this);
        Minecraft minecraft = Minecraft.getInstance();
        setVolume(minecraft.options.getSoundSourceVolume(SoundSource.MASTER)
                * minecraft.options.getSoundSourceVolume(SoundSource.MUSIC));
        findMusicTracksAndGroups();
        Constants.CONFIG_IO.addListener(this);
    }

    @Override
    public void close() {
        if (javaStreamPlayer != null)
            javaStreamPlayer.close();
        if (executorService != null)
            executorService.close();
    }

    @Override
    public void setCurrentMusicTracks(Collection<MusicTrack> musicTracks) {
        currentMusicTracks = musicTracks;
        Constants.LOG.debug("Songs: {}", String.join(", ", musicTracks.stream().map(MusicTrack::getName).toList()));

        if (!musicTracks.contains(currentMusicTrack)) {
            playNext();
        }
    }

    @Override
    public void play(MusicTrack musicTrack) {
        Minecraft minecraft = Minecraft.getInstance();
        if (musicTrack instanceof ResourceLocationMusicTrack resourceLocationMusicTrack) {
            var fileResourceLocation = Sound.SOUND_LISTER.idToFile(resourceLocationMusicTrack.getResourceLocation());
            Optional<Resource> optionalResource = minecraft.getResourceManager().getResource(fileResourceLocation);

            if (optionalResource.isPresent()) {
                executorService.submit(() -> {
                    try {
                        javaStreamPlayer.stopOpenPlay(new BufferedInputStream(optionalResource.get().open()));
                    } catch (IOException e) {
                        Constants.LOG.error(e.getMessage(), e);
                    }
                });
            } else {
                Constants.LOG.error("Resource not found: {}", resourceLocationMusicTrack);
            }
        } else if (musicTrack instanceof FileMusicTrack fileMusicTrack) {
            executorService.submit(() -> javaStreamPlayer.stopOpenPlay(fileMusicTrack.getFile()));
        }
    }

    @Override
    public void stop() {
        executorService.submit(javaStreamPlayer::stop);
    }

    @Override
    public void pause() {
        executorService.submit(javaStreamPlayer::pause);
    }

    @Override
    public void resume() {
        executorService.submit(javaStreamPlayer::resume);
    }

    @Override
    public void setVolume(float volume) {
        executorService.submit(() -> javaStreamPlayer.setGain(volume * 0.5f));
    }

    @Override
    public Collection<? extends MusicTrack> getMusicTracks() {
        if (musicTracks == null) {
            Constants.LOG.error("Music tracks are not initialized yet!");
        }
        return musicTracks;
    }

    @Override
    public Collection<MusicGroup> getMusicGroups() {
        if (musicGroups == null) {
            Constants.LOG.error("Music groups are not initialized yet!");
        }
        return musicGroups;
    }

    @Override
    public void opened(final Object dataSource, final Map<String, Object> properties) {
        Constants.LOG.debug(String.format("Opened stream player %s", dataSource));
    }

    @Override
    public void progress(final int nEncodedBytes, final long microsecondPosition, final byte[] pcmData,
                         final Map<String, Object> properties) {}

    @Override
    public void statusUpdated(final StreamPlayerEvent streamPlayerEvent) {
        final Status status = streamPlayerEvent.getPlayerStatus();
        Constants.LOG.debug("New music player status: {}", status.name());
        if (status == Status.EOM) {
            playNext();
        }
    }

    @Override
    public MusicTrack getCurrentMusicTrack() {
        return currentMusicTrack;
    }

    private void playNext() {
        if (currentMusicTracks == null || currentMusicTracks.isEmpty()) {
            currentMusicTrack = null;
            executorService.submit(javaStreamPlayer::stop);
            return;
        }

        Collection<MusicTrack> musicTracksWithoutRecent = currentMusicTracks
                .stream()
                .filter(recentMusicTracks::contains)
                .toList();

        if (musicTracksWithoutRecent.isEmpty()) {
            musicTracksWithoutRecent = currentMusicTracks;
        }

        currentMusicTrack = musicTracksWithoutRecent
                .stream()
                .skip(rdm.nextInt(0, musicTracksWithoutRecent.size()))
                .findAny()
                .orElseThrow();

        recentMusicTracks.add(currentMusicTrack);
        play(currentMusicTrack);
    }

    private void findMusicTracksAndGroups() {
        Minecraft minecraft = Minecraft.getInstance();

        Map<String, Map<MusicGroup.Type, Collection<ResourceLocation>>> musicByTypeAndNamespace = new TreeMap<>();
        musicGroups = new ArrayList<>();
        musicTracks = new HashSet<>();

        List<FileMusicTrack> fileMusicTracks;
        try {
            Files.createDirectories(Services.PLATFORM.getModConfigFolder().resolve(Constants.MUSIC_FOLDER));
            Path musicFolder = Services.PLATFORM.getModConfigFolder().resolve(Constants.MUSIC_FOLDER);

            try (var paths = Files.list(musicFolder)) {
                fileMusicTracks = paths
                        .filter(Files::isRegularFile)
                        .map(path -> new FileMusicTrack(path.getFileName().toString()))
                        .toList();
            }
        } catch (Exception e) {
            Constants.LOG.error(e.getMessage(), e);
            fileMusicTracks = List.of();
        }

        musicGroups.add(new MusicGroup(fileMusicTracks));
        musicTracks.addAll(fileMusicTracks);

        for (var soundEvent : BuiltInRegistries.SOUND_EVENT.keySet()) {
            if (soundEvent.getPath().contains("music.")) {
                musicByTypeAndNamespace
                        .computeIfAbsent(soundEvent.getNamespace(), k -> new HashMap<>())
                        .computeIfAbsent(MusicGroup.Type.BGM, k -> new ArrayList<>())
                        .add(soundEvent);
            } else if (soundEvent.getPath().contains("music_disc.")) {
                musicByTypeAndNamespace
                        .computeIfAbsent(soundEvent.getNamespace(), k -> new HashMap<>())
                        .computeIfAbsent(MusicGroup.Type.MUSIC_DISC, k -> new ArrayList<>())
                        .add(soundEvent);
            }
        }

        List<Map.Entry<String, Map<MusicGroup.Type, Collection<ResourceLocation>>>> musicByTypeAndNamespaceSorted = musicByTypeAndNamespace.entrySet().stream()
                .sorted((e1, e2) -> {
                    if (e1.getKey().equals("minecraft")) return -1;
                    if (e2.getKey().equals("minecraft")) return 1;
                    return e1.getKey().compareTo(e2.getKey());
                })
                .toList();

        for (var musicByTypeAndNamespaceEntry : musicByTypeAndNamespaceSorted) {
            Map<MusicGroup.Type, Collection<ResourceLocation>> musicByTypeSorted = musicByTypeAndNamespaceEntry.getValue().entrySet().stream()
                    .sorted((entry1, entry2) -> {
                        if (entry1.getKey() == MusicGroup.Type.BGM) return -1;
                        if (entry2.getKey() == MusicGroup.Type.BGM) return 1;
                        return entry1.getKey().compareTo(entry2.getKey());
                    })
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            var random = RandomSource.create(); // needed to get the sounds
            for (var musicByTypeEntry : musicByTypeSorted.entrySet()) {
                Collection<ResourceLocation> resourceLocations = musicByTypeEntry.getValue().stream()
                        .map(resourceLocation -> (MixinWeighedSoundEvents) minecraft.getSoundManager().getSoundEvent(resourceLocation)).filter(Objects::nonNull)
                        .flatMap(mixinWeighedSoundEvents -> mixinWeighedSoundEvents.list().stream())
                        .map(weightedSound -> weightedSound.getSound(random))
                        .map(Sound::getLocation)
                        .distinct()
                        .sorted(Comparator.comparing(ResourceLocation::toString))
                        .toList();

                Collection<ResourceLocationMusicTrack> groupMusicTracks = resourceLocations
                        .stream()
                        .map(ResourceLocationMusicTrack::new)
                        .sorted(Comparator.comparing(MusicTrack::getName))
                        .toList();

                musicGroups.add(new MusicGroup(
                        musicByTypeAndNamespaceEntry.getKey(),
                        musicByTypeEntry.getKey(),
                        groupMusicTracks
                ));
                musicTracks.addAll(groupMusicTracks);
            }
        }
    }

    @Override
    public void beforeConfigChange(MainConfig config) {
        Map<String, MusicTrackConfig> musicTrackConfigById = config.getMusicTrackConfigById();
        for (MusicTrack musicTrack : musicTracks) {
            MusicTrackConfig musicTrackConfig = musicTrackConfigById.computeIfAbsent(musicTrack.getId(),
                    k -> new MusicTrackConfig());
            musicTrack.setCustomName(musicTrackConfig.getCustomName());
            musicTrack.setVolumeMultiplier(musicTrackConfig.getVolumeMultiplier());
        }
    }

    @Override
    public void afterConfigChange(MainConfig config) {
        if (musicTracks == null) {
            Constants.LOG.error("Config loaded before music tracks initialized!");
            return;
        }

        Map<String, MusicTrackConfig> musicTrackConfigById = config.getMusicTrackConfigById();
        for (MusicTrack musicTrack : musicTracks) {
            if (musicTrackConfigById.containsKey(musicTrack.getId())) {
                MusicTrackConfig musicTrackConfig = musicTrackConfigById.get(musicTrack.getId());
                musicTrack.setCustomName(musicTrackConfig.getCustomName());
                musicTrack.setVolumeMultiplier(musicTrackConfig.getVolumeMultiplier());
            }
        }
    }
}