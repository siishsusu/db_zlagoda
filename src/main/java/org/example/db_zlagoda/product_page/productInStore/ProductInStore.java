package org.example.db_zlagoda.product_page.productInStore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.db_zlagoda.product_page.ProductPage;

import java.io.IOException;

public class ProductInStore extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(ProductPage.class.getResource("product-in-store-page-view.fxml"));
        Scene scene = new Scene(root);

//        scene.getStylesheets().add(this.getClass().getResource("product-page-style.css").toExternalForm());
        stage.setScene(scene);
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}