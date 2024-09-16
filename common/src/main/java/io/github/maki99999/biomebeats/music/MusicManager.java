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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicManager implements IMusicManager, StreamPlayerListener, ConfigChangeListener {
    private static final Collection<String> SUPPORTED_FILE_EXTENSIONS = List.of("wav", "au", "aiff", "aif", "aifc",
            "mp3", "ogg", "flac", "ape", "spx");

    private ExecutorService executorService;
    private JavaStreamPlayer javaStreamPlayer;
    private final RandomSource rdm = RandomSource.create();

    private Collection<MusicTrack> musicTracks = null;
    private Collection<MusicGroup> musicGroups = null;

    private MusicTrack currentMusicTrack = null;
    private Collection<MusicTrack> currentMusicTracks = new HashSet<>();
    private final EvictingQueue<MusicTrack> recentMusicTracks = EvictingQueue.create(5);

    private JavaStreamPlayer previewJavaStreamPlayer;
    private MusicTrack currentPreviewTrack;
    private final Collection<PreviewListener> previewListeners = new ArrayList<>();

    private boolean inPreviewMode = false;

    @Override
    public void init() {
        executorService = Executors.newSingleThreadExecutor();
        javaStreamPlayer = new JavaStreamPlayer();
        javaStreamPlayer.addStreamPlayerListener(this);
        initPreviewPlayer();
        Minecraft minecraft = Minecraft.getInstance();
        setVolume(minecraft.options.getSoundSourceVolume(SoundSource.MASTER)
                * minecraft.options.getSoundSourceVolume(SoundSource.MUSIC));
        findMusicTracksAndGroups();
        Constants.CONFIG_IO.addListener(this);
    }

    private void initPreviewPlayer() {
        previewJavaStreamPlayer = new JavaStreamPlayer();
        previewJavaStreamPlayer.addStreamPlayerListener(new StreamPlayerListener() {
            @Override
            public void opened(Object dataSource, Map<String, Object> properties) {
                Constants.LOG.debug(String.format("Opened preview stream player %s", dataSource));
            }

            @Override
            public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData,
                                 Map<String, Object> properties) {}

            @Override
            public void statusUpdated(StreamPlayerEvent event) {
                final Status status = event.getPlayerStatus();
                Constants.LOG.debug("New preview music player status: {}", status.name());
                if (status == Status.EOM) {
                    currentPreviewTrack = null;
                    previewListeners.forEach(listener -> listener.onPreviewChanged(null));
                }
            }
        });
    }

    @Override
    public void reloadMusicTracksAndGroups() {
        findMusicTracksAndGroups();
    }

    @Override
    public void playPreviewTrack(MusicTrack musicTrack) {
        if (currentPreviewTrack == musicTrack && previewJavaStreamPlayer.isPausedOrPausing()) {
            executorService.execute(previewJavaStreamPlayer::resume);
        } else {
            executorService.execute(() -> previewJavaStreamPlayer.play(musicTrack));
            currentPreviewTrack = musicTrack;
            previewListeners.forEach(listener -> listener.onPreviewChanged(musicTrack));
        }
    }

    @Override
    public void stopPreviewTrack() {
        executorService.submit(previewJavaStreamPlayer::stop);
    }

    @Override
    public void startPreviewMode() {
        pause();
        inPreviewMode = true;
    }

    @Override
    public void stopPreviewMode() {
        stopPreviewTrack();
        inPreviewMode = false;
    }

    @Override
    public void addPreviewListener(PreviewListener listener) {
        previewListeners.add(listener);
        listener.onPreviewChanged(currentPreviewTrack);
    }

    @Override
    public void removePreviewListener(PreviewListener listener) {
        previewListeners.remove(listener);
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
        } else if (javaStreamPlayer.isPausedOrPausing()) {
            executorService.submit(javaStreamPlayer::resume);
        }
    }

    @Override
    public void play(MusicTrack musicTrack) {
        if (!inPreviewMode) {
            executorService.submit(() -> javaStreamPlayer.play(musicTrack));
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
        executorService.submit(() -> javaStreamPlayer.setTargetGain(volume * 0.5f));
        executorService.submit(() -> previewJavaStreamPlayer.setTargetGain(volume * 0.5f));
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
    public void statusUpdated(final StreamPlayerEvent event) {
        final Status status = event.getPlayerStatus();
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

        Collection<MusicTrack> nonRecentMusicTracks = currentMusicTracks
                .stream()
                .filter(recentMusicTracks::contains)
                .toList();

        if (nonRecentMusicTracks.isEmpty()) {
            nonRecentMusicTracks = currentMusicTracks;
        }

        currentMusicTrack = nonRecentMusicTracks
                .stream()
                .skip(rdm.nextInt(0, nonRecentMusicTracks.size()))
                .findAny()
                .orElseThrow();

        recentMusicTracks.add(currentMusicTrack);
        play(currentMusicTrack);
    }

    private void findMusicTracksAndGroups() {
        Minecraft minecraft = Minecraft.getInstance();

        Map<String, Map<MusicGroup.Type, Collection<ResourceLocation>>> music = new TreeMap<>();
        musicGroups = new ArrayList<>();
        musicTracks = new HashSet<>();

        List<FileMusicTrack> fileMusicTracks;
        try {
            Files.createDirectories(Services.PLATFORM.getModConfigFolder().resolve(Constants.MUSIC_FOLDER));
            Path musicFolder = Services.PLATFORM.getModConfigFolder().resolve(Constants.MUSIC_FOLDER);

            try (var paths = Files.list(musicFolder)) {
                fileMusicTracks = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            String fileName = path.getFileName().toString().toLowerCase();
                            return SUPPORTED_FILE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
                        })
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
                music.computeIfAbsent(soundEvent.getNamespace(), k -> new HashMap<>())
                        .computeIfAbsent(MusicGroup.Type.BGM, k -> new ArrayList<>())
                        .add(soundEvent);
            } else if (soundEvent.getPath().contains("music_disc.")) {
                music.computeIfAbsent(soundEvent.getNamespace(), k -> new HashMap<>())
                        .computeIfAbsent(MusicGroup.Type.MUSIC_DISC, k -> new ArrayList<>())
                        .add(soundEvent);
            }
        }

        for (var musicEntry : music.entrySet()) {
            for (var musicByTypeEntry : musicEntry.getValue().entrySet()) {
                Collection<ResourceLocation> resourceLocations = musicByTypeEntry.getValue().stream()
                        .map(resourceLocation -> (MixinWeighedSoundEvents) minecraft.getSoundManager().getSoundEvent(resourceLocation)).filter(Objects::nonNull)
                        .flatMap(soundEvents -> soundEvents.list().stream())
                        .map(weightedSound -> weightedSound.getSound(rdm))
                        .map(Sound::getLocation)
                        .distinct()
                        .toList();

                Collection<ResourceLocationMusicTrack> groupMusicTracks = resourceLocations
                        .stream()
                        .map(ResourceLocationMusicTrack::new)
                        .toList();

                musicGroups.add(new MusicGroup(
                        musicEntry.getKey(),
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