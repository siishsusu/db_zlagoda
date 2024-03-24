package org.example.db_zlagoda.cashier_page.Views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import org.example.db_zlagoda.cashier_page.Controllers.CashierMenuViewController;

public class CashierMenuView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Parent root = FXMLLoader.load(CashierMenuView.class.getResource("cashier-menu-view.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        CashierMenuViewController controller = loader.getController();
        System.out.println(controller);

    }

    public static void main(String[] args) {
        launch();
    }
}
