package io.github.maki99999.biomebeats.music;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    public void play(String filePath) {
        try {
            // Obtain an audio input stream from the file
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Get a sound clip resource
            Clip audioClip = AudioSystem.getClip();

            // Open audio clip and load samples from the audio input stream
            audioClip.open(audioStream);

            // Start the clip
            audioClip.start();

            // Keep the program running to let the sound play
            System.out.println("Playing audio...");
            Thread.sleep(audioClip.getMicrosecondLength() / 1000);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("The specified audio file is not supported.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.out.println("Audio line for playing back is unavailable.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error playing the audio file.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Playback interrupted.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage
        AudioPlayer player = new AudioPlayer();
        player.play("G:\\User\\Music\\Test.wav");
    }
}
