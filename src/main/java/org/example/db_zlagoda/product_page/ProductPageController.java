package org.example.db_zlagoda.product_page;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import org.example.db_zlagoda.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class ProductPageController {
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private Button categoriesButton, productsStoreButton, productsButton;
    @FXML
    public void categoriesButtonOnAction (ActionEvent event) throws IOException {
        loadView("/org/example/db_zlagoda/product_page/category-page-view.fxml");
    }

    @FXML
    public void productsStoreButtonOnAction (ActionEvent event) throws IOException {
        loadView("/org/example/db_zlagoda/product_page/product-in-store-page-view.fxml");
    }

    @FXML
    public void productsButtonOnAction (ActionEvent event) throws IOException {
        loadView("/org/example/db_zlagoda/product_page/products-view.fxml");
    }

    private void loadView (String path){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            ScrollPane view = loader.load();

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);

            mainContainer.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
