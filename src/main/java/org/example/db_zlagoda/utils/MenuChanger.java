package org.example.db_zlagoda.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.db_zlagoda.cashier_page.Views.CashierMenuView;
import org.example.db_zlagoda.login_page.LoginPage;
import org.example.db_zlagoda.login_page.RegistrationPage;
import org.example.db_zlagoda.manager_page.ManagerPageController;
import org.example.db_zlagoda.manager_page.ManagerPageView;
import org.example.db_zlagoda.search.FindEmployee;
import org.example.db_zlagoda.update_employee_page.UpdateEmployee;

import java.io.IOException;
import java.net.URL;

public class MenuChanger {
    public enum LoaderClass {
        CashierView, ManagerView,
        LoginView, RegistrationView,
        FindEmployeeView, UpdateEmployeeView
    }
    public static void changeMenu(LoaderClass loaderClass, String fxml, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getURL(loaderClass, fxml));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(title);
        stage.show();
    }

    private static URL getURL(LoaderClass loader, String fxml) {
        switch (loader) {
            case CashierView -> {
                return CashierMenuView.class.getResource(fxml);
            }
            case ManagerView -> {
                return ManagerPageView.class.getResource(fxml);
            }
            case LoginView -> {
                return LoginPage.class.getResource(fxml);
            }
            case RegistrationView -> {
                return RegistrationPage.class.getResource(fxml);
            }
            case FindEmployeeView -> {
                return FindEmployee.class.getResource(fxml);
            }
            case UpdateEmployeeView -> {
                return UpdateEmployee.class.getResource(fxml);
            }
            default -> {
                return null;
            }
        }
    }
}
