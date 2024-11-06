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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class TimeMachineController extends Application {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button startButton, stopButton, resetButton;

    @FXML
    private Label timerLabel1, notificationLabel;

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
                getIntervalDuration(MinuteInput1, SecondInput1);
            
                // Считываем значения из третьего таймера
                getIntervalDuration(MinuteInput2, SecondInput2);

                startNextTimer();
            }
            else {
                showNotification("Таймер уже запущен");
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

                            } else if(intervalQueue.size() == 1){
                                startNextTimer();

                                MinuteInput1.clear();
                                SecondInput1.clear();

                                showNotification("Таймер завершен!");

                            } else if (intervalQueue.size() == 2) {
                                startNextTimer();
                                
                                // Перемещаем значения из третьего поля во второе
                                MinuteInput1.setText(MinuteInput2.getText());
                                SecondInput1.setText(SecondInput2.getText());
                                
                                // Очищаем третье поле
                                MinuteInput2.clear();
                                SecondInput2.clear();
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
        MinuteInput1.clear();
        SecondInput1.clear();
        MinuteInput2.clear();
        SecondInput2.clear();
    }

    @FXML
    private void stopTimer(ActionEvent event) {
        if (isTimerRunning) {
            timer.purge();
            isTimerRunning = false;
            showNotification("Таймер остановлен!"); 
        }
    }

    @FXML
    private void resetTimer(ActionEvent event) {
        timer.cancel();
        isTimerRunning = false;
        timeRemaining = 0;
        clearTimerInputs(); // Очищаем все поля ввода
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
                showNotification("ощибка с выводом сообщений");
            }
            Platform.runLater(() -> notificationLabel.setText(""));
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initialize() {
        // Добавляем обработчик события для перехода к следующему полю по нажатию Enter
        Platform.runLater(() -> rootPane.requestFocus());
    }
}
