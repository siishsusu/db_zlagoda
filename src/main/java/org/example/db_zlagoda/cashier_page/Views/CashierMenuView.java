package org.example.db_zlagoda.cashier_page.Views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;

public class CashierMenuView extends Application {
    public static CashierMenuViewController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(CashierMenuView.class.getResource("cashier-menu-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Меню касира");
        stage.show();

        controller = loader.getController();
    }

    public static void main(String[] args) {
        launch();
    }
}
