package io.github.maki99999.biomebeats.music;

import java.util.Collection;

/**
 * Interface for a music player that can initialize, play, pause, resume, and manage music tracks.
 */
public interface IMusicManager {

    /**
     * Initializes the music player.
     */
    void init();

    /**
     * Closes the music player and performs cleanup operations.
     */
    void close();

    /**
     * Sets the current music tracks to be managed by the music player.
     *
     * @param musicTracks a collection of music tracks
     */
    void setCurrentMusicTracks(Collection<MusicTrack> musicTracks);

    /**
     * Plays a music track.
     *
     * @param musicTrack the music track to play
     */
    void play(MusicTrack musicTrack);

    /**
     * Stops the currently playing music track.
     */
    void stop();

    /**
     * Pauses the currently playing music track.
     */
    void pause();

    /**
     * Resumes playing the currently paused music track.
     */
    void resume();

    /**
     * Sets the volume of the music player.
     *
     * @param volume the volume level to set, typically between 0.0 (mute) and 1.0 (full volume)
     */
    void setVolume(float volume);

    /**
     * Returns a collection with all music tracks.
     *
     * @return A collection with all music tracks.
     */
    Collection<? extends MusicTrack> getMusicTracks();

    /**
     * Returns a collection with all music groups.
     *
     * @return A collection with all music groups.
     */
    Collection<MusicGroup> getMusicGroups();

    /**
     * Returns the music track that currently is playing.
     *
     * @return The music track that currently is playing.
     */
    MusicTrack getCurrentMusicTrack();

    /**
     * Resets the music manager.
     */
    void reloadMusicTracksAndGroups();

    void playPreviewTrack(MusicTrack musicTrack);

    void stopPreviewTrack();

    void addPreviewListener(PreviewListener listener);

    void removePreviewListener(PreviewListener listener);

    void startPreviewMode();

    void stopPreviewMode();
}