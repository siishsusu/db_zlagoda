package org.example.db_zlagoda.utils.tableview_tools;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.db_zlagoda.db_data.DatabaseManager;

import java.sql.Date;

public class TableViewLoader {
    public static void initProductsTable(TableView table, TableColumn upc,
                                         TableColumn name, TableColumn amount, TableColumn price) {
        upc.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("upc"));
        name.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("name"));
        amount.setCellValueFactory(new PropertyValueFactory<ProductItem, Integer>("amount"));
        price.setCellValueFactory(new PropertyValueFactory<ProductItem, Double>("price"));
    }

    public static void initClientsTable(TableView table, TableColumn id,
                                        TableColumn name, TableColumn phone,
                                        TableColumn address, TableColumn discount) {
        id.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("card_id"));
        name.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("name"));
        phone.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("phone"));
        address.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("address"));
        discount.setCellValueFactory(new PropertyValueFactory<ClientItem, Double>("discount"));
    }

    public static void initReceiptHistoryTable(TableView table, TableColumn num,
                                        TableColumn date, TableColumn sum,
                                        TableColumn vat) {
        num.setCellValueFactory(new PropertyValueFactory<ClientItem, Integer>("id"));
        date.setCellValueFactory(new PropertyValueFactory<ClientItem, Date>("date"));
        sum.setCellValueFactory(new PropertyValueFactory<ClientItem, Double>("sum"));
        vat.setCellValueFactory(new PropertyValueFactory<ClientItem, String>("vat"));
    }
}
