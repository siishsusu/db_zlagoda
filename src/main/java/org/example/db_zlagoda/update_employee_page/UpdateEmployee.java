package org.example.db_zlagoda.update_employee_page;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class UpdateEmployee extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(UpdateEmployee.class.getResource("update-employee-view.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(this.getClass().getResource("update-employee-style.css").toExternalForm());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
