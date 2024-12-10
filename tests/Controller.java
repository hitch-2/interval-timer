package tests;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class Controller {
    @FXML
    private Button settingsButton;

    @FXML
    private VBox settingsPane;

    @FXML
    private TextField usernameField;

    @FXML
    private CheckBox darkModeCheckBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        // Изначально блок настроек скрыт
        settingsPane.setVisible(false);

        // Открытие блока настроек
        settingsButton.setOnAction(event -> settingsPane.setVisible(true));

        // Сохранение настроек
        saveButton.setOnAction(event -> {
            String username = usernameField.getText();
            boolean darkMode = darkModeCheckBox.isSelected();

            // Логика сохранения (здесь только вывод в консоль)
            System.out.println("Сохранено:");
            System.out.println("Имя пользователя: " + username);
            System.out.println("Тёмный режим: " + (darkMode ? "Включен" : "Выключен"));

            // Скрываем блок после сохранения
            settingsPane.setVisible(false);
        });

        // Закрытие меню настроек без сохранения
        closeButton.setOnAction(event -> settingsPane.setVisible(false));
    }
}
