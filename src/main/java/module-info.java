module org.example.db_zlagoda {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires itext;
    requires itextpdf;

    opens org.example.db_zlagoda to javafx.fxml;
    exports org.example.db_zlagoda;
    exports org.example.db_zlagoda.login_page;
    exports org.example.db_zlagoda.cashier_page.Controllers;
    exports org.example.db_zlagoda.cashier_page.Views;
    exports org.example.db_zlagoda.db_data;
    exports org.example.db_zlagoda.utils.tableview_tools;
    exports org.example.db_zlagoda.utils.receipt_tools;
    opens org.example.db_zlagoda.login_page to javafx.fxml;
    opens org.example.db_zlagoda.cashier_page.Controllers to javafx.fxml;
    opens org.example.db_zlagoda.cashier_page.Views to javafx.fxml;
    opens org.example.db_zlagoda.db_data to javafx.fxml;
    opens org.example.db_zlagoda.utils.tableview_tools to javafx.fxml;
    opens org.example.db_zlagoda.utils.receipt_tools to javafx.fxml;
//    opens org.example.db_zlagoda.cashier_page to javafx.fxml;
//    exports org.example.db_zlagoda.cashier_page.Views;
//    opens org.example.db_zlagoda.cashier_page.Views to javafx.fxml;
    exports org.example.db_zlagoda.update_employee_page;
    opens org.example.db_zlagoda.update_employee_page to javafx.fxml;
    exports org.example.db_zlagoda.employees_list;
    opens org.example.db_zlagoda.employees_list to javafx.fxml;
    exports org.example.db_zlagoda.search;
    opens org.example.db_zlagoda.search to javafx.fxml;

    exports org.example.db_zlagoda.manager_page;
    opens org.example.db_zlagoda.manager_page to javafx.fxml;
    exports org.example.db_zlagoda.customers_list;
    opens org.example.db_zlagoda.customers_list to javafx.fxml;
    exports org.example.db_zlagoda.product_page;
    opens org.example.db_zlagoda.product_page to javafx.fxml;
    exports org.example.db_zlagoda.product_page.category;
    opens org.example.db_zlagoda.product_page.category to javafx.fxml;
    exports org.example.db_zlagoda.product_page.productInStore;
    opens org.example.db_zlagoda.product_page.productInStore to javafx.fxml;
    exports org.example.db_zlagoda.product_page.product;
    opens org.example.db_zlagoda.product_page.product to javafx.fxml;
}