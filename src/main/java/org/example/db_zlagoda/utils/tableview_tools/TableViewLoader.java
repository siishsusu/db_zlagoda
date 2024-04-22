package org.example.db_zlagoda.utils.tableview_tools;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.db_zlagoda.db_data.DatabaseManager;

import java.sql.Date;

public class TableViewLoader {
    public static void initProductsTable(TableView table, TableColumn upc,
                                         TableColumn name, TableColumn category, TableColumn amount, TableColumn price, TableColumn prom) {
        upc.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("upc"));
        name.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("name"));
        category.setCellValueFactory(new PropertyValueFactory<ProductItem, CategoryItem>("category"));
        amount.setCellValueFactory(new PropertyValueFactory<ProductItem, Integer>("amount"));
        price.setCellValueFactory(new PropertyValueFactory<ProductItem, Double>("price"));
        prom.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("onSale"));
    }

    public static void initAllProductsTable(TableView table, TableColumn id,
                                         TableColumn name, TableColumn category, TableColumn description) {
        id.setCellValueFactory(new PropertyValueFactory<ProductInfo, Integer>("id"));
        category.setCellValueFactory(new PropertyValueFactory<ProductInfo, String>("category"));
        name.setCellValueFactory(new PropertyValueFactory<ProductInfo, String>("name"));
        description.setCellValueFactory(new PropertyValueFactory<ProductInfo, String>("description"));
    }

    public static void initCategoryTable(TableView table, TableColumn id,
                                            TableColumn name) {
        id.setCellValueFactory(new PropertyValueFactory<CategoryItem, Integer>("id"));
        name.setCellValueFactory(new PropertyValueFactory<CategoryItem, String>("name"));
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
