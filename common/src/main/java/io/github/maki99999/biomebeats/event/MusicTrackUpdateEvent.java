package io.github.maki99999.biomebeats.event;

import io.github.maki99999.biomebeats.music.MusicTrack;

public record MusicTrackUpdateEvent(MusicTrack musicTrack) implements Event {}
