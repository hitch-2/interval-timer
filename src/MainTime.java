package src;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTime extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем FXML файл
        Parent root = FXMLLoader.load(getClass().getResource("interfacee.fxml")); 

        //  заголовок окна
        primaryStage.setTitle("JavaFX App");

        // Создаем сцену
        Scene scene = new Scene(root, 600, 400);

        // Устанавливаем сцену на окно
        primaryStage.setScene(scene);

        // Показываем окно
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Запуск JavaFX приложения
        launch(args);
    }
}
