package tests;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Scanner;

public class SoundPlayer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Введите команду (1 для воспроизведения звука):");
        
        String input = scanner.nextLine();
        
        if ("1".equals(input)) {
            playSound("sounds.set-timer-bell.wav"); // Замените "sound.wav" на путь к вашему звуковому файлу
        } else {
            System.out.println("Команда не распознана.");
        }
        
        scanner.close();
    }

    private static void playSound(String soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(SoundPlayer.class.getResource(soundFile));
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
