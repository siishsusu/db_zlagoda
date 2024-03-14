module org.example.db_zlagoda {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;


    opens org.example.db_zlagoda to javafx.fxml;
    exports org.example.db_zlagoda;
    exports org.example.db_zlagoda.login_page;
    opens org.example.db_zlagoda.login_page to javafx.fxml;
}