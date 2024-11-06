package tests;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundPlayer {

    public static void main(String[] args) {
        // Укажите абсолютный путь к звуковому файлу
        playSound("C:\\Users\\Асус\\AppData\\Local\\Programs\\Python\\1main\\interval_timer\\sounds\\set-timer-bell.wav");
    }

    private static void playSound(String soundFilePath) {
        try {
            // Используем абсолютный путь к файлу
            File soundFile = new File(soundFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            
            // Ждем, пока звук закончится
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (Exception e) {
            System.out.println("Ошибка воспроизведения звука: " + e.getMessage());
        }
    }
}
