package src;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class TimeMachineController extends Application {

    @FXML
    private Button startButton, stopButton, resetButton;

    @FXML
    private Label timerLabel1, timerLabel2, notificationLabel;

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
            int intervalDuration = 0;

            // Считываем значения из первого таймера
            if (!MinuteInput.getText().isEmpty()) {
                int minutes = Integer.parseInt(MinuteInput.getText());
                intervalDuration += minutes * 60;
            }

            if (!SecondInput.getText().isEmpty()) {
                int seconds = Integer.parseInt(SecondInput.getText());
                intervalDuration += seconds;
            }

            // Если введено время, добавляем в очередь
            if (intervalDuration > 0) {
                intervalQueue.add(intervalDuration);
                showNotification("Таймер добавлен в очередь.");
            }

            // Считываем значения из второго таймера
            int intervalDuration1 = getIntervalDuration(MinuteInput1, SecondInput1);
            if (intervalDuration1 > 0) {
                intervalQueue.add(intervalDuration1);
            }

            // Считываем значения из третьего таймера
            int intervalDuration2 = getIntervalDuration(MinuteInput2, SecondInput2);
            if (intervalDuration2 > 0) {
                intervalQueue.add(intervalDuration2);
            }

            // Если таймер не запущен, запускаем первый из очереди
            if (!isTimerRunning) {
                startNextTimer();
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
        return duration;
    }

    private void startNextTimer() {
        if (!intervalQueue.isEmpty()) {
            timeRemaining = intervalQueue.poll(); // Получаем таймер из очереди
            isTimerRunning = true;
            updateTimerLabel(timeRemaining); // Обновляем текущее время в текстовом поле

            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (timeRemaining > 0) {
                        Platform.runLater(() -> {
                            timeRemaining--; // Уменьшаем время
                            updateTimerLabel(timeRemaining); // Обновляем текстовое поле
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        timer.cancel();
                        isTimerRunning = false;

                        Platform.runLater(() -> {
                            showNotification("Таймер завершен!");
                            startNextTimer(); // Запускаем следующий таймер
                        });
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, 1000);
        } else {
            showNotification("Все таймеры завершены!");
        }
    }

    @FXML
    private void stopTimer(ActionEvent event) {
        if (isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
            showNotification("Таймер остановлен!");
        }
    }

    @FXML
    private void resetTimer(ActionEvent event) {
        if (isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
        }
        timeRemaining = 0;
        updateTimerLabel(timeRemaining); // Обнуляем текстовое поле
        MinuteInput.clear();
        SecondInput.clear();
        MinuteInput1.clear();
        SecondInput1.clear();
        MinuteInput2.clear();
        SecondInput2.clear();
        notificationLabel.setText("");
        intervalQueue.clear(); // Очищаем очередь
    }

    private void updateTimerLabel(int timeRemaining) {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        MinuteInput.setText(String.valueOf(minutes)); // Отображаем оставшиеся минуты
        SecondInput.setText(String.valueOf(seconds)); // Отображаем оставшиеся секунды
    }

    private void showNotification(String message) {
        notificationLabel.setText(message);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> notificationLabel.setText(""));
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
