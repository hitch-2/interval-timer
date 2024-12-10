package trash;

import javax.swing.*;
import java.awt.*;

public class WordDisplayApp {
    public static void main(String[] args) {
        // Создаем основное окно
        JFrame frame = new JFrame("Word Display App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Размер окна
        frame.setLayout(new BorderLayout());

        // Метка для отображения текущего слова
        JLabel wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 100)); // Большой размер шрифта
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wordLabel.setVerticalAlignment(SwingConstants.CENTER);
        frame.add(wordLabel, BorderLayout.CENTER);

        // Панель для ввода текста и управления
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        // Поле для ввода текста
        JTextArea textArea = new JTextArea(3, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель для ввода задержки и кнопки
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JLabel delayLabel = new JLabel("Задержка (мс): ");
        JTextField delayField = new JTextField(5);
        JButton startButton = new JButton("Старт");

        controlPanel.add(delayLabel);
        controlPanel.add(delayField);
        controlPanel.add(startButton);

        inputPanel.add(controlPanel, BorderLayout.SOUTH);

        // Добавляем панель в нижнюю часть окна
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Добавляем обработчик для кнопки "Старт"
        startButton.addActionListener(e -> {
            // Получаем текст и задержку
            String text = textArea.getText().trim();
            String delayText = delayField.getText().trim();

            if (text.isEmpty() || delayText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите текст и задержку!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int delay = Integer.parseInt(delayText);
                if (delay < 1) {
                    JOptionPane.showMessageDialog(frame, "Задержка должна быть не менее 1 мс!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Разбиваем текст на слова
                String[] words = text.split("\\s+");

                // Отображаем слова с заданной задержкой
                new Thread(() -> {
                    for (String word : words) {
                        SwingUtilities.invokeLater(() -> wordLabel.setText(word));
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    SwingUtilities.invokeLater(() -> wordLabel.setText("")); // Очистка после завершения
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите корректное число для задержки!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Отображаем окно
        frame.setVisible(true);
    }
}
