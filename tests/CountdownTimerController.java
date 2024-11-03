package tests;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class CountdownTimerController {

    @FXML
    private TextField timeField;

    @FXML
    private Button startButton;

    private Timeline timeline;
    private int secondsRemaining;

    @FXML
    private void onStartButtonClicked() {
        String[] timeParts = timeField.getText().split(":");

        if (timeParts.length == 2) {
            try {
                int minutes = Integer.parseInt(timeParts[0]);
                int seconds = Integer.parseInt(timeParts[1]);
                secondsRemaining = minutes * 60 + seconds;

                startCountdown();
            } catch (NumberFormatException e) {
                timeField.setText("Неверный формат");
            }
        } else {
            timeField.setText("Неверный формат");
        }
    }

    private void startCountdown() {
        if (timeline != null) {
            timeline.stop(); // останавливаем предыдущий таймер, если он был
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (secondsRemaining > 0) {
                secondsRemaining--;
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                timeField.setText(String.format("%02d:%02d", minutes, seconds));
            } else {
                timeline.stop();
                timeField.setText("Время вышло");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // бесконечный цикл
        timeline.play();
    }
}
