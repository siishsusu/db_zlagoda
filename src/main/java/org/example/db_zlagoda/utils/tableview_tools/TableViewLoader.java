package org.example.db_zlagoda.utils.tableview_tools;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.db_zlagoda.db_data.DatabaseManager;

public class TableViewLoader {
    public static void initProductsTable(TableView table, TableColumn utc,
                                         TableColumn name, TableColumn amount, TableColumn price) {
        utc.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("utc"));
        name.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("name"));
        amount.setCellValueFactory(new PropertyValueFactory<ProductItem, Integer>("amount"));
        price.setCellValueFactory(new PropertyValueFactory<ProductItem, Double>("price"));
        table.setItems(DatabaseManager.getProductTableItems());
    }

    public static void initClientsTable(TableView table, TableColumn id,
                                        TableColumn name, TableColumn phone,
                                        TableColumn address, TableColumn discount) {
        id.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("card_id"));
        name.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("name"));
        phone.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("phone"));
        address.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("address"));
        discount.setCellValueFactory(new PropertyValueFactory<ClientItem, Double>("discount"));
        table.setItems(DatabaseManager.getClientTableItems());
    }

    public static void initReceiptTable(TableView table, TableColumn utc,
                                        TableColumn name, TableColumn amount, TableColumn price) {
        utc.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("utc"));
        name.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("name"));
        amount.setCellValueFactory(new PropertyValueFactory<ProductItem, Integer>("amount"));
        price.setCellValueFactory(new PropertyValueFactory<ProductItem, Double>("price"));

    }
}
