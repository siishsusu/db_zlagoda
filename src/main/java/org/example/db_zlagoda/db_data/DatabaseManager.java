package org.example.db_zlagoda.db_data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.db_zlagoda.tableview_tools.ClientTableItem;
import org.example.db_zlagoda.tableview_tools.ProductTableItem;

public class DatabaseManager {
    //TODO: implement actual data selection from database

    public static ObservableList<ProductTableItem> getProductTableItems() {
        ObservableList<ProductTableItem> list = FXCollections.observableArrayList(
                new ProductTableItem("928765287617", "product1", 82, "true"),
                new ProductTableItem("928765287617", "product2", 82, "true"),
                new ProductTableItem("928765287617", "product3", 82, "true"),
                new ProductTableItem("928765287617", "product4", 82, "true"),
                new ProductTableItem("928765287617", "product5", 82, "true"),
                new ProductTableItem("827491826572", "product6", 1, "true"),
                new ProductTableItem("827491826572", "product7", 23, "true"),
                new ProductTableItem("827491826572", "product8", 76, "true"),
                new ProductTableItem("827491826572", "product9", 1012, "true"),
                new ProductTableItem("827491826572", "product10", 51, "true"),
                new ProductTableItem("729152817298", "product11", 2938, "true"),
                new ProductTableItem("729152817298", "product12", 200, "true"),
                new ProductTableItem("729152817298", "product13", 31, "true"),
                new ProductTableItem("729152817298", "product14", 81, "true"),
                new ProductTableItem("729152817298", "product15", 2003, "true")
        );
        ObservableList<ProductTableItem> list2 = FXCollections.observableArrayList();
        list2.addAll(list);
        list2.addAll(list);
        list2.addAll(list);
        list2.addAll(list);

        return list2;
    }

    public static ObservableList<ClientTableItem> getClientTableItems() {
        ObservableList<ClientTableItem> list = FXCollections.observableArrayList(
                new ClientTableItem("928765287617", "12312", "82", "true", 0.5),
                new ClientTableItem("928765287617", "produ123123ct2", "82", "true", 0.5),
                new ClientTableItem("928765287617", "asdasd", "82", "true", 0.5),
                new ClientTableItem("928765287617", "asdadsda", "82", "true", 0.5),
                new ClientTableItem("928765287617", "zczxvc", "82", "true", 0.5),
                new ClientTableItem("827491826572", "xcvxcvxcv", "1", "true", 0.5),
                new ClientTableItem("827491826572", "kljdfgkldfjg", "23", "true", 0.5),
                new ClientTableItem("827491826572", "fjdslkmfskl", "76", "true", 0.5),
                new ClientTableItem("827491826572", "sjfklfnsd", "1012", "true", 0.5),
                new ClientTableItem("827491826572", "vnxcm,vxn", "51", "true", 0.5),
                new ClientTableItem("729152817298", "jfaskljdsak", "2938", "true", 0.5),
                new ClientTableItem("729152817298", "asdjklas", "200", "true", 0.5),
                new ClientTableItem("729152817298", "vncxm,nvmx,c", "31", "true", 0.5),
                new ClientTableItem("729152817298", "l;akd;ls", "81", "true", 0.5),
                new ClientTableItem("729152817298", "akjsbdasbd", "2003", "true", 0.5)
        );
        ObservableList<ClientTableItem> list2 = FXCollections.observableArrayList();
        list2.addAll(list);
        list2.addAll(list);
        list2.addAll(list);
        list2.addAll(list);

        return list2;
    }
}
