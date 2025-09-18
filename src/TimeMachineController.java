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
        System.out.println("1");
        try {
            // Если таймер не запущен, запускаем первый из очереди
            if (!isTimerRunning) {
                System.out.println("2");
                // Считываем значения из первого таймера
                processAllTimers(rootPane, intervalQueue);

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

    private void processAllTimers(AnchorPane rootPane, Queue<Integer> intervalQueue) {
        // Максимальное количество таймеров
        int maxTimers = 10;

        System.out.println("3");

        //! костыль удалить или исправить
        if (MinuteInput != null && SecondInput != null) {
        getIntervalDuration(MinuteInput, SecondInput);
        }

        for (int i = 1; i <= maxTimers; i++) {
            // Формируем ID для минутного и секундного текстового поля
            String minuteId = "MinuteInput-" + i;
            String secondId = "SecondInput-" + i;
    
            // Ищем текстовые поля по ID
            TextField minuteInput = (TextField) rootPane.lookup("#" + minuteId);
            TextField secondInput = (TextField) rootPane.lookup("#" + secondId);
    
            // Проверяем, существует ли таймер (текстовые поля могут быть не созданы)
            if (minuteInput != null && secondInput != null) {
                // Считаем продолжительность интервала для текущего таймера
                int duration = getIntervalDuration(minuteInput, secondInput);
    
                // Выводим продолжительность (для проверки, можно убрать)
                System.out.println("Timer " + i + ": " + duration + " seconds");
            }
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
        System.out.println("4");
        System.out.println(intervalQueue);

        if (!intervalQueue.isEmpty()) {
            System.out.println("5");
            
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
                                // Если все таймеры завершены, очищаем все поля
                                clearTimerInputs(); // Метод для очистки всех TextField
                                showNotification("Все таймеры завершены!");
                                playSound(1);
                                playSound(2);
                                switchToStart();
                            } else {
                                // Запускаем следующий таймер
                                startNextTimer();
                        
                                // Сдвигаем значения текстовых полей
                                shiftTimerInputs();
                        
                                showNotification("Таймер завершен!");
                                playSound(1);
                            }
                        });
                        
                    }
                }
            };

            timer.scheduleAtFixedRate(task, 0, 1000);
        }
    }

    private void shiftTimerInputs() {
        int maxTimers = 10;
    
        for (int i = 1; i < maxTimers; i++) {
            // Ищем текстовые поля для текущего и следующего таймера
            TextField currentMinuteInput = (TextField) rootPane.lookup("#MinuteInput-" + i);
            TextField currentSecondInput = (TextField) rootPane.lookup("#SecondInput-" + i);
    
            TextField nextMinuteInput = (TextField) rootPane.lookup("#MinuteInput-" + (i + 1));
            TextField nextSecondInput = (TextField) rootPane.lookup("#SecondInput-" + (i + 1));
    
            if (currentMinuteInput != null && currentSecondInput != null &&
                nextMinuteInput != null && nextSecondInput != null) {
                // Сдвигаем значения из следующего таймера в текущий
                currentMinuteInput.setText(nextMinuteInput.getText());
                currentSecondInput.setText(nextSecondInput.getText());
            }
        }
    
        // Очищаем последний таймер (максимальный номер)
        TextField lastMinuteInput = (TextField) rootPane.lookup("#MinuteInput-" + maxTimers);
        TextField lastSecondInput = (TextField) rootPane.lookup("#SecondInput-" + maxTimers);
    
        if (lastMinuteInput != null && lastSecondInput != null) {
            lastMinuteInput.clear();
            lastSecondInput.clear();
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

    // Значение сдвига
    double shift = 0.0;

    // Номер блока, для генерации уникальных ID
    int blockNumber = 1;

    int toggle = 0;

    public void add() {

        if (blockNumber == 10) {
            showNotification("ограничение в 10 таимеров");
            return; // Завершаем выполнение метода
        }

        String[] texts = {"00", ":", "00", "←"};  // Стрелка "←" и "00" как текстовые поля
        // Массив координат (top, left) для каждой Label или TextField
        double[][] positions = {
            {45.0, 212.0}, // Для первого Label/TextField ("00")
            {57.5, 260.0}, // Для второго Label (":")
            {45.0, 266.0}, // Для третьего Label/TextField ("00")
            {49.0, 181.0}  // Для четвёртого Label ("←")
        };


        // Добавляем сдвиг к координатам
        for (int i = 0; i < positions.length; i++) {
            positions[i][1] += shift; // Увеличиваем x-координату
        }

        shift = shift + 150.0;
        blockNumber++; // Генерация уникального ID

        // Создание и добавление Label или TextField в контейнер
        for (int i = 0; i < texts.length; i++) {

            if (texts[i].equals("00")) {
                // Создаём TextField для "00"
                TextField textField = new TextField();
                textField.setPromptText("00");

                textField.setAlignment(Pos.CENTER);
                textField.setPrefHeight(90.0);
                textField.setPrefWidth(75.0);
                textField.setMaxWidth(50); // Максимальная ширина для TextField

                
                if (toggle == 0) {
                    textField.setId("MinuteInput-" + blockNumber);
                    System.out.println("MinuteInput-" + blockNumber); //удалить!!
                    toggle++;
                } else {
                    textField.setId("SecondInput-" + blockNumber);
                    System.out.println("SecondInput-" + blockNumber); //удалить!!
                    toggle--;
                }
                

                textField.getStyleClass().add("class40"); // Применяем стиль для TextField

                // Устанавливаем позицию TextField
                AnchorPane.setTopAnchor(textField, positions[i][0]);
                AnchorPane.setLeftAnchor(textField, positions[i][1]);
                rootPane.getChildren().add(textField); // Добавляем в AnchorPane
                
            } else {

                // Создаём обычный Label для других элементов
                Label label = new Label(texts[i]);
                label.getStyleClass().add("signs"); // Применяем стиль для Label
                if(texts[i].equals(":")){
                    label.setId("colon-" + blockNumber);
                    System.out.println("colon-" + blockNumber); // удалить!!
                } else {
                    label.setId("arrow-" + blockNumber);
                    System.out.println("arrow-" + blockNumber); // удалить!!
                }


                // Устанавливаем позицию Label
                AnchorPane.setTopAnchor(label, positions[i][0]);
                AnchorPane.setLeftAnchor(label, positions[i][1]);
                rootPane.getChildren().add(label); // Добавляем Label в AnchorPane
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
