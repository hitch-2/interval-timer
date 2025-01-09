package src;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.File;

public class TimeMachineController extends Application {


    @FXML
    private Pane settingsPane;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button startButton, stopButton, resetButton, closeButton, saveButton, settingsButton;

    @FXML
    private Label timerLabel1, notificationLabel, arrow1, arrow2, colon2, colon3;

    @FXML
    private TextField MinuteInput, SecondInput, MinuteInput1, SecondInput1, MinuteInput2, SecondInput2;

    private Queue<Integer> intervalQueue = new LinkedList<>();
    private Timer timer;
    private boolean isTimerRunning = false;
    private int timeRemaining;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("interfacee.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Time Machine");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void startTimer(ActionEvent event) {
        try {
            // Если таймер не запущен, запускаем первый из очереди
            if (!isTimerRunning) {
                // Считываем значения из первого таймера
                getIntervalDuration(MinuteInput, SecondInput);
           
                // Считываем значения из второго таймера
                // getIntervalDuration(MinuteInput1, SecondInput1);
            
                // // Считываем значения из третьего таймера
                // getIntervalDuration(MinuteInput2, SecondInput2);

                startNextTimer();
            }
            else {
                switchToStart();
                stopTimer(event);
            }

        } catch (NumberFormatException e) {
            showNotification("Некорректный ввод! Введите число.");
        }   
    }

    private int getIntervalDuration(TextField minuteInput, TextField secondInput) {
        int duration = 0;
        if (!minuteInput.getText().isEmpty()) {
            int minutes = Integer.parseInt(minuteInput.getText());
            duration += minutes * 60;
        }
        if (!secondInput.getText().isEmpty()) {
            int seconds = Integer.parseInt(secondInput.getText());
            duration += seconds;
        }
        if (duration > 0) {
            intervalQueue.add(duration);
        }

        return duration;
    }

    private void startNextTimer() {
        if (!intervalQueue.isEmpty()) {
            isTimerRunning = true;
            timeRemaining = intervalQueue.poll(); // Получаем таймер из очереди

            switchToStop();

            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (timeRemaining > 0) {
                        Platform.runLater(() -> {
                            timeRemaining--; // Уменьшаем время
                            updateTimerLabel(timeRemaining); // Обновляем текстовое поле
                        });

                    } else {
                        timer.cancel();
                        isTimerRunning = false;

                        Platform.runLater(() -> {
                            if (intervalQueue.isEmpty()) {
                                // Если нет следующего таймера, очищаем все поля
                                clearTimerInputs();
                                showNotification("Все таймеры завершены!");
                                playSound(1);
                                playSound(2);
                                switchToStart();


                            } else if(intervalQueue.size() == 1){
                                startNextTimer();

                                // MinuteInput1.clear();
                                // SecondInput1.clear();

                                showNotification("Таймер завершен!");
                                playSound(1);
                                


                            } else if (intervalQueue.size() == 2) {
                                startNextTimer();
                                
                                // Перемещаем значения из третьего поля во второе
                                // MinuteInput1.setText(MinuteInput2.getText());
                                // SecondInput1.setText(SecondInput2.getText());
                                
                                // Очищаем третье поле
                                // MinuteInput2.clear();
                                // SecondInput2.clear();

                                showNotification("Таймер завершен!");
                                playSound(1);
                                


                            }
                            
                            else {
                                // Запускаем следующий таймер
                                startNextTimer();
                            }
                        });
                    }
                }
            };

            timer.scheduleAtFixedRate(task, 0, 1000);
        }
    }

    private void clearTimerInputs() {
        MinuteInput.clear();
        SecondInput.clear();
        // MinuteInput1.clear();
        // SecondInput1.clear();
        // MinuteInput2.clear();
        // SecondInput2.clear();
    }

    private void switchToStop() {

        startButton.setStyle(
            "-fx-background-color: #E94E77; " +  // Фон
            "-fx-text-fill: white; " +           // Цвет текста
            "-fx-font-size: 14px; " +            // Размер текста
            "-fx-min-width: 56px; " +            // Минимальная ширина
            "-fx-min-height: 40px; " +           // Минимальная высота
            "-fx-border-radius: 5px; " +         // Закругление границы
            "-fx-background-radius: 5px;"        // Закругление фона
        );
        
        startButton.setText("Стоп");
    }
    
    private void switchToStart() {
        startButton.setStyle(
            "-fx-background-color:  #5BBFBA;" +  // Фон кнопки
            "-fx-text-fill: white; " +           // Цвет текста
            "-fx-font-size: 14px; " +            // Размер текста
            "-fx-padding: 10px; " +              // Внутренние отступы
            "-fx-border-radius: 5px; " +         // Радиус границы
            "-fx-background-radius: 5px;"        // Радиус фона
        );
        startButton.setText("Старт");

    }

    @FXML
    private void stopTimer(ActionEvent event) {
        if(isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
            showNotification("Таймер остановлен!");
            intervalQueue.clear();
        }
    }

    @FXML
    private void resetTimer(ActionEvent event) {
        if(isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
            timeRemaining = 0;
        }
        clearTimerInputs(); // Очищаем все поля ввода
        notificationLabel.setText("");
        intervalQueue.clear(); // Очищаем очередь
        switchToStart();
    }

    private void updateTimerLabel(int timeRemaining) {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
    
        // Форматируем каждую часть отдельно
        String formattedMinutes = String.format("%02d", minutes);
        String formattedSeconds = String.format("%02d", seconds);
    
        // Устанавливаем значения в поля
        MinuteInput.setText(formattedMinutes); // Отображаем оставшиеся минуты
        SecondInput.setText(formattedSeconds); // Отображаем оставшиеся секунды
    }
    

    private void showNotification(String message) {
        notificationLabel.setText(message);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                showNotification("ощибка с выводом сообщений");
            }
            Platform.runLater(() -> notificationLabel.setText(""));
        }).start();
    }

    private static void playSound(int choose) {
        try {
            // Используем абсолютный путь к файлу
            File soundFile = new File((choose == 1) ? "sounds\\set-timer-bell.wav" : "sounds\\set-timer-bell3.wav");
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

    public int added = 0;

    public void add() {
        // Массив текста для меток и полей ввода
        String[] texts = {"00", ":", "00", "←"};

        // Массив начальных координат (top, left) для каждой Label или TextField
        double[][] positions = {
            {45.0, 212.0},  // Для первого Label/TextField ("00")
            {57.5, 260.0},  // Для второго Label (":")
            {45.0, 266.0},  // Для третьего Label/TextField ("00")
            {49.0, 181.0}   // Для четвёртого Label ("←")
        };

        // Начальная смещение по оси X (первоначальное положение)
        double offsetX = 0;

        // Номер блока, для генерации уникальных ID
        int blockNumber = 1;

        // Создание и добавление Label или TextField в контейнер
        for (int i = 0; i < texts.length; i++) {
            String elementId = texts[i] + blockNumber; // Генерация уникального ID

            if (texts[i].equals("00")) {
                // Создаём TextField для "00"
                TextField textField = new TextField();
                textField.setPromptText("00");

                textField.setAlignment(Pos.CENTER);
                textField.setPrefHeight(90.0);
                textField.setPrefWidth(75.0);
                textField.setMaxWidth(50); // Максимальная ширина для TextField
                textField.getStyleClass().add("class40"); // Применяем стиль для TextField

                // Устанавливаем уникальный ID
                textField.setId("textField" + elementId);

                // Устанавливаем позицию TextField с учетом смещения по оси X
                AnchorPane.setTopAnchor(textField, positions[i][0]);
                AnchorPane.setLeftAnchor(textField, positions[i][1] + offsetX);
                rootPane.getChildren().add(textField); // Добавляем в AnchorPane
            } else {
                // Создаём обычный Label для других элементов
                Label label = new Label(texts[i]);
                label.getStyleClass().add("signs"); // Применяем стиль для Label

                // Устанавливаем уникальный ID
                label.setId("label" + elementId);

                // Устанавливаем позицию Label с учетом смещения по оси X
                AnchorPane.setTopAnchor(label, positions[i][0]);
                AnchorPane.setLeftAnchor(label, positions[i][1] + offsetX);
                rootPane.getChildren().add(label); // Добавляем Label в AnchorPane
            }

            // Если это последний элемент в блоке, увеличиваем смещение по оси X для следующего блока
            if (i == texts.length - 1) {
                offsetX += 100;  // Сдвиг блока на 100 пикселей вправо для следующего блока
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void initialize() {
        // Изначально блок настроек скрыт, на будущее как скрывать в fxml: <TextField fx:id="MinuteInput1" visible="false" />
        settingsPane.setVisible(false);

        // Массив для полей ввода (TextField)
        // TextField[] inputFields = {MinuteInput1, SecondInput1, MinuteInput2, SecondInput2};

        // Массив для меток (Label)
        // Label[] labels = {arrow1, arrow2, colon2, colon3};

        // Скрываем все поля ввода
        // for (TextField input : inputFields) {
        //     input.setVisible(false);
        // }

        // Скрываем все метки
        // for (Label label : labels) {
        //     label.setVisible(false);
        // }


        // Открытие блока настроек
        settingsButton.setOnAction(event -> settingsPane.setVisible(true));

        // Сохранение настроек
        saveButton.setOnAction(event -> {

            // Логика сохранения (здесь только вывод в консоль)
            System.out.println("Сохранено:");

            // Скрываем блок после сохранения
            settingsPane.setVisible(false);
        });

        // Закрытие меню настроек без сохранения
        closeButton.setOnAction(event -> settingsPane.setVisible(false));
        Platform.runLater(() -> rootPane.requestFocus());
    }
}
