package org.example.db_zlagoda.product_page;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProductPage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(ProductPage.class.getResource("product-page-view.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(this.getClass().getResource("product-page-style.css").toExternalForm());
        stage.setScene(scene);
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}