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
    private Label timerLabel, timerLabel1, timerLabel2, notificationLabel;

    @FXML
    private TextField MinuteInput, SecondInput;

    private Queue<Integer> intervalQueue = new LinkedList<>();
    private Timer timer;
    private boolean isTimerRunning = false;
    private int timeRemaining;
    private static final int MAX_INTERVALS = 3;  // Лимит на количество таймеров

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

            if (!MinuteInput.getText().isEmpty()) {
                int minutes = Integer.parseInt(MinuteInput.getText());
                intervalDuration += minutes * 60;
            }

            if (!SecondInput.getText().isEmpty()) {
                int seconds = Integer.parseInt(SecondInput.getText());
                intervalDuration += seconds;
            }

            if (intervalDuration <= 0) {
                showNotification("Введите положительное время!");
                return;
            }

            if (intervalQueue.size() >= MAX_INTERVALS) {
                showNotification("Превышен лимит очереди!");
                return;
            }

            if (isTimerRunning) {
                intervalQueue.add(intervalDuration);
                showNotification("Таймер добавлен в очередь.");
                updateWaitingTimers();
            } else {
                startNewTimer(intervalDuration);
            }

        } catch (NumberFormatException e) {
            showNotification("Некорректный ввод! Введите число.");
        }
    }

    private void startNewTimer(int durationInSeconds) {
        timeRemaining = durationInSeconds;
        isTimerRunning = true;
        updateTimerLabel(timeRemaining);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    Platform.runLater(() -> updateTimerLabel(timeRemaining));
                    timeRemaining--;
                } else {
                    timer.cancel();
                    isTimerRunning = false;

                    Platform.runLater(() -> {
                        showNotification("Таймер завершен!");
                        checkNextInterval();
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void checkNextInterval() {
        if (!intervalQueue.isEmpty()) {
            int nextInterval = intervalQueue.poll();
            startNewTimer(nextInterval);
            updateWaitingTimers();
        } else {
            resetTimer(null);
            showNotification("Все таймеры завершены!");
        }
    }

    private void updateWaitingTimers() {
        // Отображает статусы двух ожидающих таймеров
        Integer[] nextTimers = intervalQueue.toArray(new Integer[0]);
        if (nextTimers.length > 0) {
            updateTimerLabel1(nextTimers[0]);
        } else {
            timerLabel1.setText("00:00");
        }

        if (nextTimers.length > 1) {
            updateTimerLabel2(nextTimers[1]);
        } else {
            timerLabel2.setText("00:00");
        }
    }

    @FXML
    private void stopTimer(ActionEvent event) {
        if (isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
            showNotification("Таймер остановлен!");
            intervalQueue.clear();
            updateWaitingTimers();
        }
    }

    @FXML
    private void resetTimer(ActionEvent event) {
        if (isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
        }
        timeRemaining = 0;
        timerLabel.setText("00:00");
        timerLabel1.setText("00:00");
        timerLabel2.setText("00:00");
        MinuteInput.clear();
        SecondInput.clear();
        notificationLabel.setText("");
        intervalQueue.clear();
    }

    private void updateTimerLabel(int timeRemaining) {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateTimerLabel1(int timeRemaining) {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel1.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateTimerLabel2(int timeRemaining) {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel2.setText(String.format("%02d:%02d", minutes, seconds));
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