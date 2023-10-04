package io.github.maki99999.musicbybiome;

import net.minecraft.sounds.Music;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicProvider
{
    public static final Map<String, List<Music>> music  = new HashMap<>();

    public static void init()
    {
        //music.computeIfAbsent(BiomeTags.HAS_VILLAGE_SNOWY, key -> new ArrayList<>()).add(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES));
    }
}
