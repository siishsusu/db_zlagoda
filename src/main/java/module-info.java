module org.example.db_zlagoda {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;

    opens org.example.db_zlagoda to javafx.fxml;
    exports org.example.db_zlagoda;
    exports org.example.db_zlagoda.login_page;
    exports org.example.db_zlagoda.cashier_page.Controllers;
    exports org.example.db_zlagoda.cashier_page.Views;
    exports org.example.db_zlagoda.db_data;
    exports org.example.db_zlagoda.tableview_tools;
    opens org.example.db_zlagoda.login_page to javafx.fxml;
    opens org.example.db_zlagoda.cashier_page.Controllers to javafx.fxml;
    opens org.example.db_zlagoda.cashier_page.Views to javafx.fxml;
    opens org.example.db_zlagoda.db_data to javafx.fxml;
    opens org.example.db_zlagoda.tableview_tools to javafx.fxml;
//    opens org.example.db_zlagoda.cashier_page to javafx.fxml;
//    exports org.example.db_zlagoda.cashier_page.Views;
//    opens org.example.db_zlagoda.cashier_page.Views to javafx.fxml;
}