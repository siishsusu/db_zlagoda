package org.example.db_zlagoda.product_page.productInStore;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.example.db_zlagoda.DatabaseConnection;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.db_zlagoda.login_page.RegistrationController.generateRandomId;

public class ProductInStoreAddUpdateController implements Initializable {
    @FXML
    private Button doUpdateButton;

    @FXML
    private TextField upcField, prodIDField, prodNameField, priceField, numOfProdField, promField;

    @FXML
    private ComboBox upcPromBox, promBox;

    @FXML
    private Label infoLabel;

    String UPC_prod;

    private List<TextField> textFieldList;

    private int MAX_VALUE_COUNT = 999999;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        upcField.setDisable(true);
        prodIDField.setDisable(true);
        prodNameField.setDisable(true);

        prodIDField.textProperty().addListener((observable, oldValue, option) -> {
            if (option != null){
                try {
                    DatabaseConnection connection = new DatabaseConnection();
                    Connection connectDB = connection.getConnection();
                    Statement statement = connectDB.createStatement();
                    // Витягнути інформацію за UPC з бази даних
                    ResultSet upc = statement.executeQuery("SELECT UPC " +
                            "FROM store_product " +
                            "WHERE id_product = '" + prodIDField.getText() + "' " +
                            "AND UPC <> '" + upcField.getText() + "' " +
                            "AND promotional_product = '1'");

                    if (upc.next()) {
                        String UPC_temp = upc.getString("UPC");
                        upcPromBox.getItems().add(String.valueOf(UPC_temp));
                        upcPromBox.setValue(UPC_temp);
                    }

                    upc.close();
                    statement.close();
                    connectDB.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    String number_before_update;
    @FXML
    public void doUpdateButtonOnAction(ActionEvent event) {
        boolean allFieldsFilled = true;
        for (TextField textField : textFieldList) {
            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                textField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
                allFieldsFilled = false;
            } else {
                textField.setStyle("");
                if (textField == numOfProdField) {
                    if (Integer.parseInt(textField.getText()) <= 0) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Кількість повинна бути додатньою"));
                        allFieldsFilled = false;
                    } else {
                        if (promBox.getValue().equals("1")){
                            try {
                                DatabaseConnection connection = new DatabaseConnection();
                                Connection connectDB = connection.getConnection();
                                Statement statement = connectDB.createStatement();
                                // Перевірити кількість товарів
                                ResultSet resultSet = statement.executeQuery("SELECT product_number " +
                                        "FROM store_product " +
                                        "WHERE id_product = '" + prodIDField.getText() + "' " +
                                        "AND UPC <> '" + UPC_prod + "' AND promotional_product = '0'");

                                if (resultSet.next()) {
                                    Integer number_of_products = Integer.parseInt(resultSet.getString("product_number"));
                                    if (number_of_products < (Integer.parseInt(numOfProdField.getText()) - Integer.parseInt(number_before_update))){
                                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                        textField.setTooltip(new Tooltip("Кількість акційного товару не повинна перевищувати кількість НЕакційного"));
                                        allFieldsFilled = false;
                                    }
                                } else {
                                    textField.setTooltip(new Tooltip(""));
                                    textField.setStyle("");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (textField == priceField) {
                    String text = textField.getText();
                    if (text.matches("^\\d+(\\.\\d+)?$")) {

                        int indexOfDecimal = text.indexOf('.');
                        if (indexOfDecimal == -1) {
                            if (text.length() <= 13) {
                                textField.setStyle(null);
                            } else {
                                // Показати помилку про перевищення максимальної довжини
                                textField.setTooltip(new Tooltip("Кількість символів не повинна перевищувати 13"));
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                allFieldsFilled = false;
                            }
                        } else {
                            String digitsBeforeDecimal = text.substring(0, indexOfDecimal);
                            String digitsAfterDecimal = text.substring(indexOfDecimal + 1);

                            int afterDecimal = digitsAfterDecimal.length();
                            int beforeDecimal = digitsBeforeDecimal.length();

                            if (beforeDecimal <= (13 - afterDecimal) && afterDecimal <= 4) {
                                textField.setStyle(null);
                            } else {
                                // Показати помилку про перевищення максимальної довжини перед або після коми
                                textField.setTooltip(new Tooltip("Кількість символів не повинна перевищувати 13 (4 символи після коми)"));
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                allFieldsFilled = false;
                            }
                        }
                    } else {
                        // Показати помилку про некоректне числове значення
                        textField.setTooltip(new Tooltip("Некоректне числове значення"));
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsFilled = false;
                    }
                }
            }
        }

        if (promBox.getValue().equals("0") || promBox.getValue().equals("1")) {
            boolean validProm = false;
            if (promBox.getValue().equals("0")) {
                try {
                    DatabaseConnection connection = new DatabaseConnection();
                    Connection connectDB = connection.getConnection();
                    Statement statement = connectDB.createStatement();
                    // Витягнути інформацію за UPC з бази даних
                    ResultSet resultSet = statement.executeQuery("SELECT * " +
                            "FROM store_product " +
                            "WHERE id_product = '" + prodIDField.getText() + "' " +
                            "AND UPC <> '" + UPC_prod + "' AND promotional_product = '0'");

                    if (resultSet.next()) {
                        validProm = false;
                        promBox.setTooltip(new Tooltip("Цей товар вже належить базі"));
                        promBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsFilled = false;
                    } else {
                        validProm = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    DatabaseConnection connection = new DatabaseConnection();
                    Connection connectDB = connection.getConnection();
                    Statement statement = connectDB.createStatement();
                    // Витягнути інформацію за UPC з бази даних
                    ResultSet resultSet = statement.executeQuery("SELECT * " +
                            "FROM store_product " +
                            "WHERE id_product = '" + prodIDField.getText() + "' " +
                            "AND promotional_product = '0' " +
                            "AND UPC <> '" + UPC_prod + "'");

                    if (resultSet.next()) {
                        validProm = true;
                    } else {
                        validProm = false;
                        promBox.setTooltip(new Tooltip("У базі немає такого неакційного товару"));
                        promBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsFilled = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (validProm){
                promBox.setTooltip(new Tooltip(""));
                promBox.setStyle("");
            }
        } else {
            promBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            promBox.setTooltip(new Tooltip("Значення повинно бути 1 або 0"));
            allFieldsFilled = false;
        }

        if (allFieldsFilled) {
            updateProduct();
        }
    }

    private void updateProduct() {
        try {
            String upcProm = (String) upcPromBox.getValue();
            String price = priceField.getText();
            String numOfProd = numOfProdField.getText();
            //String promotional = promField.getText();
            String promotional = (String) promBox.getValue();

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            // 2. Редагувати дані про товари в магазині
            String updateQuery;
            if (upcProm != null){
                // редагування, якщо є акційний товар
                updateQuery = "UPDATE store_product SET " +
                        "selling_price = '" + price + "', " +
                        "UPC_prom = '" + upcProm + "', " +
                        "product_number = '" + numOfProd + "', " +
                        "promotional_product = '" + promotional + "' " +
                        "WHERE UPC = '" + UPC_prod + "'";
            } else {
                // редагування, якщо немає акційного товару
                updateQuery = "UPDATE store_product SET " +
                        "selling_price = '" + price + "', " +
                        "product_number = '" + numOfProd + "', " +
                        "promotional_product = '" + promotional + "' " +
                        "WHERE UPC = '" + UPC_prod + "'";
            }
            statement.executeUpdate(updateQuery);

            ResultSet numberOf_no_prom = statement.executeQuery("SELECT product_number, UPC " +
                    "FROM store_product " +
                    "WHERE id_product = '" + prodIDField.getText() + "' " +
                    "AND UPC <> '" + UPC_prod + "' AND promotional_product = '0'");

            if (numberOf_no_prom.next()) {
                Integer number_of_products = Integer.parseInt(numberOf_no_prom.getString("product_number"));
                Integer newProdNum = number_of_products - (Integer.parseInt(numOfProd) - Integer.parseInt(number_before_update));

                if (newProdNum > 0){
                    // оновлення кількості НЕакційного товару, при додаванні акційного
                    updateQuery = "UPDATE store_product SET " +
                            "product_number = '" + newProdNum + "' " +
                            "WHERE UPC = '" + numberOf_no_prom.getString("UPC") + "'";
                    statement.executeUpdate(updateQuery);
                } else {
                    // якщо кількість акційного товару == кількості неакційного, то неакційний товар видаляється з
                    // магазину
                    String deleteProd = "DELETE FROM store_product "+
                            "WHERE UPC = '" + numberOf_no_prom.getString("UPC") + "'";
                    statement.executeUpdate(deleteProd);
                }
            } else {

            }

            statement.close();
            connectDB.close();

            Stage stage = (Stage) upcField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void doAddButtonOnAction(ActionEvent event) {
        boolean prom = false;
        boolean allFieldsFilled = true;
        for (TextField textField : textFieldList) {
            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                textField.setTooltip(new Tooltip("Поле не повинно бути пустим"));
                allFieldsFilled = false;
            } else {
                textField.setStyle("");
                if (textField == upcField) {
                    if (textField.getText().length() > 12) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textField.setTooltip(new Tooltip("Довжина тексту не повинна перевищувати 12 символів"));
                        allFieldsFilled = false;
                    } else {
                        boolean notInDb = false;
                        try {
                            DatabaseConnection connection = new DatabaseConnection();
                            Connection connectDB = connection.getConnection();
                            Statement statement = connectDB.createStatement();
                            // перевірка на унікальність UPC
                            ResultSet UPC_uniq = statement.executeQuery(
                                    "SELECT COUNT(*) " +
                                            "FROM store_product " +
                                            "WHERE UPC = '" + upcField.getText() + "'"
                            );
                            if (UPC_uniq.next()) {
                                int n = UPC_uniq.getInt(1);
                                if (n == 0) {
                                    notInDb = true;
                                } else {
                                    notInDb = false;
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        if (notInDb) {
                            textField.setTooltip(new Tooltip(""));
                            textField.setStyle("");
                        } else {
                            textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                            textField.setTooltip(new Tooltip("Товар з таким UPC вже є в магазині"));
                            allFieldsFilled = false;
                        }
                    }
                }

                if (textField == prodIDField) {
                    boolean inDb = false;
                    try {
                        DatabaseConnection connection = new DatabaseConnection();
                        Connection connectDB = connection.getConnection();
                        Statement statement = connectDB.createStatement();
                        // перевірка чи введена назва продукту відповідає введеному ІД продукту
                        ResultSet valid_id = statement.executeQuery(
                                "SELECT product_name " +
                                        "FROM product " +
                                        "WHERE id_product = '" + prodIDField.getText() + "'"
                        );
                        if (valid_id.next()) {
                            String str = valid_id.getString("product_name");

                            if (str != null) {
                                if (prodNameField.getText().equals(str)) {
                                    textField.setStyle(null);
                                    textField.setTooltip(new Tooltip(""));

                                    prodNameField.setStyle(null);
                                    prodNameField.setTooltip(new Tooltip(""));

                                    // пошук UPC товару в магазині, який посилається на товар з таким же ІД, як і
                                    // той, який додається в базу даних
                                    ResultSet not_prom = statement.executeQuery(
                                            "SELECT UPC " +
                                                    "FROM store_product " +
                                                    "WHERE id_product = '" + prodIDField.getText() + "' " +
                                                    "AND UPC <> '" + upcField.getText() + "'"
                                    );
                                    if (not_prom.next()) {
                                        prom = true;
                                    }
                                    inDb = true;

                                } else {
                                    textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                    textField.setTooltip(new Tooltip("ID та назва товар повинні відповідати одне одному"));

                                    prodNameField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                    prodNameField.setTooltip(new Tooltip("ID та назва товар повинні відповідати одне одному"));

                                    allFieldsFilled = false;
                                }
                            } else {
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                textField.setTooltip(new Tooltip("ID повинно належати наявному товару"));
                                allFieldsFilled = false;
                                inDb = false;
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (textField == priceField) {
                    String text = textField.getText();
                    if (text.matches("^\\d+(\\.\\d+)?$")) {

                        int indexOfDecimal = text.indexOf('.');
                        if (indexOfDecimal == -1) {
                            if (text.length() <= 13) {
                                textField.setStyle(null);
                            } else {
                                // Показати помилку про перевищення максимальної довжини
                                textField.setTooltip(new Tooltip("Кількість символів не повинна перевищувати 13"));
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                allFieldsFilled = false;
                            }
                        } else {
                            String digitsBeforeDecimal = text.substring(0, indexOfDecimal);
                            String digitsAfterDecimal = text.substring(indexOfDecimal + 1);

                            int afterDecimal = digitsAfterDecimal.length();
                            int beforeDecimal = digitsBeforeDecimal.length();

                            if (beforeDecimal <= (13 - afterDecimal) && afterDecimal <= 4) {
                                textField.setStyle(null);
                            } else {
                                // Показати помилку про перевищення максимальної довжини перед або після коми
                                textField.setTooltip(new Tooltip("Кількість символів не повинна перевищувати 13 (4 символи після коми)"));
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                allFieldsFilled = false;
                            }
                        }
                    } else {
                        // Показати помилку про некоректне числове значення
                        textField.setTooltip(new Tooltip("Некоректне числове значення"));
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsFilled = false;
                    }
                }
            }
        }
        if (promBox.getValue().equals("0") || promBox.getValue().equals("1")) {
            boolean validProm = false;
            if (promBox.getValue().equals("0")) {
                try {
                    DatabaseConnection connection = new DatabaseConnection();
                    Connection connectDB = connection.getConnection();
                    Statement statement = connectDB.createStatement();
                    // Витягнути інформацію за UPC з бази даних
                    ResultSet resultSet = statement.executeQuery("SELECT * " +
                            "FROM store_product " +
                            "WHERE id_product = '" + prodIDField.getText() + "' " +
                            "AND UPC <> '" + UPC_prod + "'  AND promotional_product = '0'");

                    if (resultSet.next()) {
                        validProm = false;
                        promBox.setTooltip(new Tooltip("Цей товар вже належить базі"));
                        promBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsFilled = false;
                    } else {
                        validProm = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    DatabaseConnection connection = new DatabaseConnection();
                    Connection connectDB = connection.getConnection();
                    Statement statement = connectDB.createStatement();
                    // Витягнути інформацію за UPC з бази даних
                    ResultSet resultSet = statement.executeQuery("SELECT * " +
                            "FROM store_product " +
                            "WHERE id_product = '" + prodIDField.getText() + "' " +
                            "AND promotional_product = '0' " +
                            "AND UPC <> '" + UPC_prod + "'");

                    if (resultSet.next()) {
                        validProm = true;
                    } else {
                        validProm = false;
                        promBox.setTooltip(new Tooltip("У базі немає такого неакційного товару"));
                        promBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsFilled = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (validProm) {
                promBox.setTooltip(new Tooltip(""));
                promBox.setStyle("");
            }
        } else {
            promBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            promBox.setTooltip(new Tooltip("Значення повинно бути 1 або 0"));
            allFieldsFilled = false;
        }

        if (Integer.parseInt(numOfProdField.getText()) > MAX_VALUE_COUNT){
            if (promBox.getValue().equals("1")){
                numOfProdField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                numOfProdField.setTooltip(new Tooltip("Кількість акційних товарів не може перевищувати кількість неакційних"));
                allFieldsFilled = false;
            }
        } else {
            numOfProdField.setStyle(null);
            numOfProdField.setTooltip(null);
        }

        if (allFieldsFilled) {
            addProduct(prom);
        }
    }

    private void addProduct(boolean promotionalProduct) {
        try {
            String upc = upcField.getText();
            String upcProm = (String) upcPromBox.getValue();
            String id_prod = prodIDField.getText();
            String price = priceField.getText();
            String numOfProd = numOfProdField.getText();
         //   String promotional = promField.getText();
            String promotional = (String) promBox.getValue();

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            // 1. Додавати дані про товари в магазині
            String addQuery = "";
            if (upcProm != null){
                // додавання товару, якщо існує посилання на акційний товар
                addQuery = "INSERT INTO store_product (UPC, UPC_prom, id_product, selling_price, product_number, promotional_product) " +
                        "VALUES ('" + upc + "', '" + upcProm + "', '" + id_prod + "', '" + price + "', '" + numOfProd + "', '" +
                        promotional + "')";
            } else {
                // додавання товару, якщо НЕ існує посилання на акційний товар
                addQuery = "INSERT INTO store_product (UPC, UPC_prom, id_product, selling_price, product_number, promotional_product) " +
                        "VALUES ('" + upc + "', null, '" + id_prod + "', '" + price + "', '" + numOfProd + "', '" +
                        promotional + "')";
            }
            statement.executeUpdate(addQuery);

            if (promotional.equals("1")){
                int n = Integer.parseInt(numOfProdField.getText());

                // пошук кількості НЕакційних товарів, при додаванні акційного
                ResultSet number_of_non_prom = statement.executeQuery("SELECT product_number, UPC " +
                        "FROM store_product " +
                        "WHERE id_product = '" + id_prod + "' " +
                        "AND promotional_product = '0'");
                int n_n_prom = 0;
                if (number_of_non_prom.next()){
                    n_n_prom = Integer.parseInt(number_of_non_prom.getString("product_number"));
                }

                int newProdNum = n_n_prom - n;

                if (newProdNum > 0){
                    // оновлення кількості НЕакційного товару, при додаванні акційного
                    String updateQuery = "UPDATE store_product SET " +
                            "product_number = '" + newProdNum + "' " +
                            "WHERE UPC = '" + number_of_non_prom.getString("UPC") + "'";
                    statement.executeUpdate(updateQuery);
                } else {
                    // якщо кількість акційного товару == кількості неакційного, то неакційний товар видаляється з
                    // магазину
                    String deleteProd = "DELETE FROM store_product "+
                            "WHERE UPC = '" + number_of_non_prom.getString("UPC") + "'";
                    statement.executeUpdate(deleteProd);
                }
            }

            if (promotionalProduct){
                // пошук UPC неакційного товару, при додаванні акційного
                ResultSet not_prom = statement.executeQuery(
                        "SELECT UPC " +
                                "FROM store_product " +
                                "WHERE id_product = '" + prodIDField.getText() + "' " +
                                "AND UPC <> '" + upcField.getText() + "' AND promotional_product = '0'"
                );
                if(not_prom.next()) {
                    String upc_this = not_prom.getString("UPC");
                    // оновлення UPC_prom поля в бд (при додаванні акційного товару (неакційний уже знаходиться в бд))
                    String updateQuery = "UPDATE store_product SET " +
                            "UPC_prom = '" + upcField.getText() + "' " +
                            "WHERE UPC = '" + upc_this + "'";
                    statement.executeUpdate(updateQuery);
                }
            }

            statement.close();
            connectDB.close();

            Stage stage = (Stage) upcField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        upcField.setDisable(true);
        prodIDField.setDisable(true);
        prodNameField.setDisable(true);

        number_before_update = numOfProdField.getText();
        doUpdateButton.setText("Оновити");

        infoLabel.setText("Оновлення інформації про товар в магазині");
      // textFieldList = Arrays.asList(priceField, numOfProdField, promField);
        textFieldList = Arrays.asList(priceField, numOfProdField);
        doUpdateButton.setOnAction(this::doUpdateButtonOnAction);
    }

    public void add() throws SQLException {
        upcField.setDisable(false);
        prodIDField.setDisable(false);
        prodNameField.setDisable(false);

        doUpdateButton.setText("Додати");

        String gener_upc = generateRandomId(10, true);
        upcField.setText(gener_upc);

        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Statement statement = connectDB.createStatement();
        // витягнення інформації про товар з бд
        ResultSet products_information = statement.executeQuery(
                "SELECT id_product, product_name " +
                        "FROM product " +
                        "WHERE id_product NOT IN " +
                        "(SELECT id_product FROM store_product WHERE promotional_product = '1') " +
                        "OR id_product NOT IN " +
                        "(SELECT id_product FROM store_product WHERE promotional_product = '0')");
        ArrayList<Integer> productIDs = new ArrayList<>();
        ArrayList<String> productNames = new ArrayList<>();
        while (products_information.next()) {
            productIDs.add(products_information.getInt("id_product"));
            productNames.add(products_information.getString("product_name"));
        }
        TextFields.bindAutoCompletion(prodIDField, productIDs);
        TextFields.bindAutoCompletion(prodNameField, productNames);

        prodIDField.textProperty().addListener((observable, oldValue, option) -> {
            if (option != null) {
                try {
                    // пошук назви товару за його id
                    ResultSet prod_name = statement.executeQuery(
                            "SELECT product_name " +
                                    "FROM product " +
                                    "WHERE id_product = '" + prodIDField.getText() + "'"
                    );
                    if(prod_name.next()) {
                        prodNameField.setText(prod_name.getString("product_name"));
                    }
                    // пошук ціни НЕакційного товару за його id
                    ResultSet price = statement.executeQuery(
                            "SELECT selling_price FROM store_product WHERE id_product = '" + prodIDField.getText() + "' " +
                                    "AND promotional_product = '0'"
                    );
                    if (price.next()) {
                        BigDecimal sellingPrice = price.getBigDecimal("selling_price");
                        BigDecimal discountedPrice = sellingPrice.multiply(new BigDecimal("0.8")).setScale(4, RoundingMode.HALF_UP);
                        priceField.setText(discountedPrice.toString());

                       // promField.setText("1");
                        promBox.setValue("1");
                    }
                    // пошук ціни акційного товару за його id
                    price = statement.executeQuery(
                            "SELECT selling_price FROM store_product WHERE id_product = '" + prodIDField.getText() + "' " +
                                    "AND promotional_product = '1'"
                    );
                    if (price.next()) {
                        BigDecimal sellingPrice = price.getBigDecimal("selling_price");
                        BigDecimal discountedPrice = sellingPrice.multiply(new BigDecimal(100)).divide(new BigDecimal(80)).setScale(4, RoundingMode.HALF_UP);
                        priceField.setText(discountedPrice.toString());

                       // promField.setText("0");
                        promBox.setValue("0");
                    }

                    if(promBox.getValue() == null){
                        promBox.setItems(FXCollections.observableArrayList("1", "0"));
                    }

                    // пошук кількості НЕакційного товару за його id
                    ResultSet count = statement.executeQuery(
                            "SELECT product_number FROM store_product " +
                                    "WHERE id_product = '" + prodIDField.getText() + "' " +
                                    "AND promotional_product = '0'"
                    );
                    if (count.next()) {
                        int numOfProd = count.getInt("product_number");
                        MAX_VALUE_COUNT = numOfProd;
                        numOfProdField.setText(String.valueOf(MAX_VALUE_COUNT));
                    }
//                    prodNameField.setDisable(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        prodNameField.textProperty().addListener((observable, oldValue, option) -> {
            if (option != null) {
                try {
                    // пошук id товару за його назвою
                    ResultSet prod_name = statement.executeQuery(
                            "SELECT id_product " +
                                    "FROM product " +
                                    "WHERE product_name = '" + prodNameField.getText() + "'"
                    );
                    if(prod_name.next()) {
                        prodIDField.setText(prod_name.getString("id_product"));
                    }
//                    prodIDField.setDisable(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        infoLabel.setText("Додавання інформації про товар в магазині");
      //  textFieldList = Arrays.asList(upcField, prodIDField, prodNameField, priceField, numOfProdField, promField);
        textFieldList = Arrays.asList(upcField, prodIDField, prodNameField, priceField, numOfProdField);
        doUpdateButton.setOnAction(this::doAddButtonOnAction);
    }

    public void fillFields (String UPC, boolean add) throws IOException {
        if (add == false){
            this.UPC_prod = UPC;
        }
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // Витягнути інформацію за UPC з бази даних
            if (add == false){
                ResultSet resultSet = statement.executeQuery("SELECT UPC, UPC_prom, " +
                        "store_product.id_product, product.product_name, " +
                        "selling_price, product_number, promotional_product " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "WHERE UPC = '" + UPC + "'");

                if (resultSet.next()) {
                    upcField.setText(resultSet.getString("UPC"));
                    upcPromBox.setValue(resultSet.getString("UPC_prom"));
                    prodIDField.setText(resultSet.getString("id_product"));
                    prodNameField.setText(resultSet.getString("product_name"));
                    priceField.setText(resultSet.getString("selling_price"));
                    numOfProdField.setText(resultSet.getString("product_number"));
                    //promField.setText(resultSet.getString("promotional_product"));
                    promBox.setValue(resultSet.getString("promotional_product"));

                }
            } else {
                ResultSet resultSet1 = statement.executeQuery(
                        "SELECT id_product, product_name FROM product WHERE id_product = '" + UPC + "'"
                );
                if (resultSet1.next()) {
                    prodIDField.setText(resultSet1.getString("id_product"));
                    prodNameField.setText(resultSet1.getString("product_name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
