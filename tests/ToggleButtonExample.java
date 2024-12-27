package tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ToggleButtonExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Создаем кнопку
        Button toggleButton = new Button("Старт");
        
        // Устанавливаем начальное состояние
        boolean[] isStart = {true}; // Используем массив, чтобы переменная была доступна из лямбда-выражения

        // Добавляем обработчик событий
        toggleButton.setOnAction(event -> {
            if (isStart[0]) {
                toggleButton.setText("Стоп"); // Меняем текст
                System.out.println("Старт нажата, теперь Стоп"); // Выполняем действие "Старт"
            } else {
                toggleButton.setText("Старт"); // Меняем текст обратно
                System.out.println("Стоп нажата, теперь Старт"); // Выполняем действие "Стоп"
            }
            isStart[0] = !isStart[0]; // Переключаем состояние
        });

        // Устанавливаем сцену и показываем окно
        StackPane root = new StackPane(toggleButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Toggle Button Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
