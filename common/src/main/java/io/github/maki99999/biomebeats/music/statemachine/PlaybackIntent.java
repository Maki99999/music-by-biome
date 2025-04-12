package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.music.MusicTrack;

sealed interface PlaybackIntent permits PlaybackIntent.Stop, PlaybackIntent.Pause, PlaybackIntent.Play, PlaybackIntent.PauseWithSong {

    record Stop() implements PlaybackIntent {}

    record Pause() implements PlaybackIntent {}

    record Play(MusicTrack song) implements PlaybackIntent {}

    record PauseWithSong(MusicTrack song) implements PlaybackIntent {}
}
