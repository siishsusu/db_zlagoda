package org.example.db_zlagoda.db_data;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.example.db_zlagoda.DatabaseConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager_mm {

    public static Connection getConnection() throws SQLException {
        DatabaseConnection dbc = new DatabaseConnection();
        return dbc.getConnection();
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void closeStatement(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    private static ResultSet executeQuery(String query) {
        DatabaseConnection dbc = new DatabaseConnection();
        Connection connection = dbc.getConnection();
        ResultSet resultSet = null;

        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    // пошук username в базі даних (якщо він є, то не можна додати ще одного користувача з ідентичним username)
    public static boolean checkUsernameAvailability(String username) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM login_table WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return !resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 1.1. Додавати нові дані про працівників
    public static void insertEmployee(String id, String lastName, String firstName, String patronymic, String role, String salary,
                                      java.sql.Date birthDate, java.sql.Date firstWorkDay, String phoneNumber, String city,
                                      String street, String zipCode, String username, String password) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO employee (id_employee, empl_surname, empl_name, empl_patronymic, empl_role, salary, " +
                             "date_of_birth, date_of_start, phone_number, city, street, zip_code) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, id);
            statement.setString(2, lastName);
            statement.setString(3, firstName);
            statement.setString(4, patronymic);
            statement.setString(5, role);
            statement.setString(6, salary);
            statement.setDate(7, birthDate);
            statement.setDate(8, firstWorkDay);
            statement.setString(9, phoneNumber);
            statement.setString(10, city);
            statement.setString(11, street);
            statement.setString(12, zipCode);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO login_table (username, password, id_employee) " +
                             "VALUES (?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1.2. Додавати нові дані про постійних клієнтів
    public static void insertCustomer(String id, String lastName, String firstName, String patronymic, String phone,
                                      String city, String street, String zipCode, String percent) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO customer_card (card_number, cust_surname, cust_name, cust_patronymic, phone_number, city, street, zip_code, percent) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, id);
            statement.setString(2, lastName);
            statement.setString(3, firstName);
            statement.setString(4, patronymic);
            statement.setString(5, phone);
            statement.setString(6, city);
            statement.setString(7, street);
            statement.setString(8, zipCode);
            statement.setString(9, percent);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1.3. Додавати нові дані про категорії товарів
    public static void insertCategory(String id, String name){
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO category (category_number, category_name) " +
                             "VALUES (?, ?)")) {
            statement.setString(1, id);
            statement.setString(2, name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1.4. Додавати нові дані про товари
    public static void insertProduct (String id_product, int category_number, String product_name,
                                      String characteristics){
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO product (id_product, category_number, product_name, characteristics) " +
                             "VALUES (?, ?, ?, ?)")) {
            statement.setString(1, id_product);
            statement.setInt(2, category_number);
            statement.setString(3, product_name);
            statement.setString(4, characteristics);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1.5. Додавати нові дані про товари у магазині
    public static void insertProductStore (String upc, String upcProm, String idProd, String price,
                                           String numOfProd, String promotional) {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String addQuery = "INSERT INTO store_product (UPC, UPC_prom, id_product, selling_price, products_number, promotional_product) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            preparedStatement.setString(1, upc);
            preparedStatement.setString(2, upcProm);
            preparedStatement.setString(3, idProd);
            preparedStatement.setString(4, price);
            preparedStatement.setString(5, numOfProd);
            preparedStatement.setString(6, promotional);
            preparedStatement.executeUpdate();

            if (promotional.equals("1")) {
                int n = Integer.parseInt(numOfProd);

                ResultSet numberOfNonPromotional = statement.executeQuery(
                        "SELECT products_number, UPC " +
                                "FROM store_product " +
                                "WHERE id_product = '" + idProd + "' " +
                                "AND promotional_product = '0'");

                int nNonProm = 0;
                if (numberOfNonPromotional.next()) {
                    nNonProm = Integer.parseInt(numberOfNonPromotional.getString("products_number"));
                }

                int newProdNum = nNonProm - n;

                if (newProdNum > 0) {
                    String updateQuery = "UPDATE store_product SET " +
                            "products_number = ? " +
                            "WHERE UPC = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, newProdNum);
                    updateStatement.setString(2, numberOfNonPromotional.getString("UPC"));
                    updateStatement.executeUpdate();
                } else {
                    String deleteProd = "DELETE FROM store_product " +
                            "WHERE UPC = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteProd);
                    deleteStatement.setString(1, numberOfNonPromotional.getString("UPC"));
                    deleteStatement.executeUpdate();
                }
            }

            if (promotional.equals("1")) {
                ResultSet nonPromotional = statement.executeQuery(
                        "SELECT UPC " +
                                "FROM store_product " +
                                "WHERE id_product = '" + idProd + "' " +
                                "AND UPC <> '" + upc + "' AND promotional_product = '0'");

                if (nonPromotional.next()) {
                    String upcNonProm = nonPromotional.getString("UPC");
                    String updateQuery = "UPDATE store_product SET " +
                            "UPC_prom = ? " +
                            "WHERE UPC = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, upc);
                    updateStatement.setString(2, upcNonProm);
                    updateStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2.1. Редагувати дані про працівників
    public static void updateEmployee(String employeeId, String newLastName, String newFirstName, String newPatronymic,
                                      String newRole, String newSalary, String newBirthDate, String newStartWorkDate,
                                      String newPhoneNumber, String newCity, String newStreet, String newZipCode,
                                      String newUsername) {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String updateQuery = "UPDATE employee SET " +
                    "empl_surname = '" + newLastName + "', " +
                    "empl_name = '" + newFirstName + "', " +
                    "empl_patronymic = '" + newPatronymic + "', " +
                    "empl_role = '" + newRole + "', " +
                    "salary = '" + newSalary + "', " +
                    "date_of_birth = '" + newBirthDate + "', " +
                    "date_of_start = '" + newStartWorkDate + "', " +
                    "phone_number = '" + newPhoneNumber + "', " +
                    "city = '" + newCity + "', " +
                    "street = '" + newStreet + "', " +
                    "zip_code = '" + newZipCode + "' " +
                    "WHERE id_employee = '" + employeeId + "'";

            statement.executeUpdate(updateQuery);

            String updateUsername = "UPDATE login_table " +
                    "SET username = '" + newUsername + "' " +
                    "WHERE id_employee = '" + employeeId + "'";
            statement.executeUpdate(updateUsername);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2.2. Редагувати дані про постійних клієнтів
    public static void updateCustomer(String id, String newLastName, String newFirstName, String newPatronymic,
                                      String newPhone, String newCity, String newStreet, String newZipCode,
                                      String newPercent) {
        try (Connection connection = getConnection()) {

            String updateQuery = "UPDATE customer_card SET " +
                    "cust_surname = ?, " +
                    "cust_name = ?, " +
                    "cust_patronymic = ?, " +
                    "phone_number = ?, " +
                    "city = ?, " +
                    "street = ?, " +
                    "zip_code = ?, " +
                    "percent = ? " +
                    "WHERE card_number = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newLastName);
            preparedStatement.setString(2, newFirstName);
            preparedStatement.setString(3, newPatronymic);
            preparedStatement.setString(4, newPhone);
            preparedStatement.setString(5, newCity);
            preparedStatement.setString(6, newStreet);
            preparedStatement.setString(7, newZipCode);
            preparedStatement.setString(8, newPercent);
            preparedStatement.setString(9, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2.3. Редагувати дані про категорії товарів
    public static void updateCategory (String category_number, String category_name) {
        try (Connection connection = getConnection()) {

            String updateQuery = "UPDATE category SET " +
                    "category_name = ? " +
                    "WHERE category_number = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, category_name);
            preparedStatement.setString(2, category_number);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2.4. Редагувати дані про товари
    public static void updateProd (String id, int newCategory, String newName,
                                      String characteristics) {
        try (Connection connection = getConnection()) {

            String updateQuery = "UPDATE product SET " +
                    "category_number = ?, " +
                    "product_name = ?, " +
                    "characteristics = ? " +
                    "WHERE id_product = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, newCategory);
            preparedStatement.setString(2, newName);
            preparedStatement.setString(3, characteristics);
            preparedStatement.setString(4, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2.5. Редагувати дані про товари у магазині;
    public static void updateProdInStore (String upc, String selling_price, String UPC_prom,
                                          String products_number, String promotional_product) {
        try (Connection connection = getConnection()) {

            String updateQuery = "";
            if (UPC_prom != null){
                updateQuery = "UPDATE store_product SET " +
                        "selling_price = ?, " +
                        "UPC_prom = ?, " +
                        "products_number = ?, " +
                        "promotional_product = ? " +
                        "WHERE UPC = ? AND UPC_prom = ?";
            } else {
                updateQuery = "UPDATE store_product SET " +
                        "selling_price = ?, " +
                        "products_number = ?, " +
                        "promotional_product = ? " +
                        "WHERE UPC = ? AND UPC_prom IS NULL";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

            preparedStatement.setString(1, selling_price);
            if (UPC_prom != null) {
                preparedStatement.setString(2, UPC_prom);
                preparedStatement.setString(3, products_number);
                preparedStatement.setString(4, promotional_product);
                preparedStatement.setString(5, upc);
                preparedStatement.setString(6, UPC_prom);
            } else {
                preparedStatement.setString(2, products_number);
                preparedStatement.setString(3, promotional_product);
                preparedStatement.setString(4, upc);
            }

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Видалення
    public static void delete(String query, String id) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkSmthForSmth(String id, String query){
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query
             )) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3.1. Видаляти дані про працівників
    public static void deleteEmployee (String id) {
        if (checkChecksForEmployee(id)) {
            setAlert("Неможливо видалити працівника, оскільки існують чеки створені ним.");
            return;
        }
        String deleteEmployee = "DELETE FROM employee WHERE id_employee = ?";
        delete(deleteEmployee, id);
    }

    private static boolean checkChecksForEmployee(String id){
        return checkSmthForSmth(id, "SELECT COUNT(*) FROM `check` WHERE id_employee = ?");
    }

    // 3.2. Видаляти дані про постійних клієнтів
    public static void deleteCustomer (String id) {
        if (checkChecksForCustomers(id)) {
            setAlert("Неможливо видалити постійного клієнта, оскільки існують чеки з його ім'ям.");
            return;
        }
        String deleteCustomer = "DELETE FROM customer_card WHERE card_number = ?";
        delete(deleteCustomer, id);
    }

    private static boolean checkChecksForCustomers(String id){
        return checkSmthForSmth(id, "SELECT COUNT(*) FROM `check` WHERE card_number = ?");
    }

    // 3.3. Видаляти дані про категорії товарів
    public static void deleteCategory(String number){
        String deleteCategory = "DELETE FROM category WHERE category_number = ?";
        delete(deleteCategory, number);
    }

    // 3.4. Видаляти дані про товари
    public static void deleteProduct(String id){
        if (checkShopForProduct(id)) {
            setAlert("Неможливо видалити товар, оскільки він є в наявності магазину.");
            return;
        }
        String deleteProduct = "DELETE FROM product WHERE id_product = ?";
        delete(deleteProduct, id);
    }

    private static boolean checkShopForProduct (String id){
        return checkSmthForSmth(id, "SELECT COUNT(*) FROM store_product WHERE id_product = ?");
    }

    // 3.5. Видаляти дані про товари у магазині
    public static void deleteProductInStore(String upc) {
        if (checkSalesForProduct(upc)) {
            setAlert("Неможливо видалити товар, оскільки існують зв'язані записи у таблиці sale.");
            return;
        }
        String deleteProduct = "DELETE FROM store_product WHERE UPC = ?";
        delete(deleteProduct, upc);
    }

    private static boolean checkSalesForProduct(String upc) {
        return checkSmthForSmth(upc, "SELECT COUNT(*) FROM sale WHERE UPC = ?");
    }

    // 3.6. Видаляти дані про чеки;
    public static void deleteCheck(String id){
        String deleteCheck = "DELETE FROM `check` WHERE check_number = ?";
        delete(deleteCheck, id);
    }

    // друкування звітів
    private static void printPDFReport(ResultSet data, String reportTitle,
                                       String[] columnTitles, String[] db_fields, String fileName)
            throws SQLException, IOException, com.itextpdf.text.DocumentException {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(null)) {
            Document document = new Document();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Зберегти PDF-файл");
            fileChooser.setInitialFileName(fileName + ".pdf");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File tempFile = fileChooser.showSaveDialog(null);
            if (tempFile != null) { // Перевіряємо, чи користувач обрав файл для збереження
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile));
                document.open();

                Font font = FontFactory.getFont("src/main/resources/fonts/Academy.ttf", BaseFont.IDENTITY_H, true);

                font.setSize(20);
                font.setStyle(Font.BOLD);
                Paragraph title = new Paragraph(reportTitle, font);

                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                PdfPTable table = new PdfPTable(columnTitles.length);
                table.setWidthPercentage(100);

                for (String columnTitle : columnTitles) {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                }

                font.setSize(12);
                font.setStyle(Font.NORMAL);
                while (data.next()) {
                    for (String columnTitle : db_fields) {
                        PdfPCell cell = new PdfPCell();
                        cell.setPhrase(new Phrase(data.getString(columnTitle), font));
                        table.addCell(cell);
                    }
                }

                table.completeRow();
                document.add(table);

                document.close();
                printerJob.endJob();
            }
            else {
                System.out.println("Користувач відмінив друк.");
            }
        }
    }


    // 4.1. Видруковувати звіти з інформацією про усіх працівників
    public static void printReportEmployee(int screen) {
        try {
            String initialName = "";
            ResultSet reportData = null;
            String reportTitle = "";

            if (screen == 0) {
                reportData = getEmployeeReportData();
                initialName = "Звіт всі працівники " + LocalDate.now();
                reportTitle = "Звіт з інформацією про всіх працівників";
            } else if (screen == 1) {
                reportData = getCashiersReportData();
                initialName = "Звіт всі касири " + LocalDate.now();
                reportTitle = "Звіт з інформацією про всіх касирів";
            } else if (screen == 2) {
                reportData = getThisEmployeeReportData(selectedEmployeeID);
                String fullName = getSelectedEmployeeFullName();
                initialName = "Звіт про працівника " + fullName + " " + LocalDate.now();
                reportTitle = "Звіт з інформацією про обраного працівника";
            }

            String[] titles = {"ID", "ПІБ", "Посада", "Заробітня плата", "Дата народження", "Дата початку роботи", "Номер телефону", "Адреса"};
            String[] dbFields = {"id_employee", "pib", "empl_role", "salary",
                    "date_of_birth", "date_of_start", "phone_number", "address"};

            if (reportData != null) {
                printPDFReport(reportData, reportTitle, titles, dbFields, initialName);
                reportData.close();
            } else {
                System.out.println("Немає даних для звіту.");
            }
        } catch (SQLException | IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }
    }

    private static String getSelectedEmployeeFullName() {
        String fullName = "";
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS pib " +
                            "FROM employee WHERE id_employee = '" + selectedEmployeeID + "'");
            if (resultSet.next()) {
                fullName = resultSet.getString("pib");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fullName;
    }

    private static String selectedEmployeeID = "";
    public static void printReportEmployee(String id){
        selectedEmployeeID = id;
        printReportEmployee(2);
    }

    private static ResultSet getEmployeeReportData() throws SQLException {
        return executeQuery(
                "SELECT id_employee, " +
                        "CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS pib, " +
                        "empl_role, " +
                        "salary, " +
                        "date_of_birth, " +
                        "date_of_start, " +
                        "phone_number, " +
                        "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address " +
                        "FROM employee " +
                        "ORDER BY pib;"
        );
    }

    private static ResultSet getCashiersReportData() throws SQLException {
        return executeQuery(
                "SELECT id_employee, " +
                        "CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS pib, " +
                        "empl_role, " +
                        "salary, " +
                        "date_of_birth, " +
                        "date_of_start, " +
                        "phone_number, " +
                        "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address " +
                        "FROM employee " +
                        "WHERE empl_role = 'Касир' " +
                        "ORDER BY pib");
    }

    private static ResultSet getThisEmployeeReportData(String id_empl) throws SQLException {
        return executeQuery(
                "SELECT id_employee, " +
                        "CONCAT(empl_surname, ' ', empl_name, ' ', empl_patronymic) AS pib, " +
                        "empl_role, " +
                        "salary, " +
                        "date_of_birth, " +
                        "date_of_start, " +
                        "phone_number, " +
                        "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address " +
                        "FROM employee " +
                        "WHERE id_employee = '" + id_empl + "'"
        );
    }

    // 4.2. Видруковувати звіти з інформацією про усіх постійних клієнтів
    public static void printReportCustomers () {
        try {
            String initialName = "";
            ResultSet reportData = null;
            String reportTitle = "";

            reportData = getCustomerReportData();
            initialName = "Звіт всі постійні покупці " + LocalDate.now();
            reportTitle = "Звіт з інформацією про всіх постійних покупців";

            String[] titles = {"Номер карти", "ПІБ", "Номер телефону", "Адреса", "Відсоток знижки"};
            String[] dbFields = {"card_number", "pib", "phone_number", "address", "percent"};

            if (reportData != null) {
                printPDFReport(reportData, reportTitle, titles, dbFields, initialName);
                reportData.close();
            } else {
                System.out.println("Немає даних для звіту.");
            }
        } catch (SQLException | IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet getCustomerReportData() {
        return executeQuery(
                "SELECT card_number, " +
                        "CONCAT(cust_surname, ' ', cust_name, ' ', cust_patronymic) AS pib, " +
                        "phone_number, " +
                        "CONCAT('м. ', city, ' вул. ', street, ' поштовий індекс: ', zip_code) AS address, " +
                        "percent " +
                        "FROM customer_card " +
                        "ORDER BY pib;"
        );
    }

    // 4.3. Видруковувати звіти з інформацією про усіх категорії товарів
    public static void printReportCategory () {
        try {
            String initialName = "";
            ResultSet reportData = null;
            String reportTitle = "";

            reportData = getCategoryReportData();
            initialName = "Звіт всі категорії " + LocalDate.now();
            reportTitle = "Звіт з інформацією про всі категорії";

            String[] titles = {"Номер категорії", "Назва категорії"};
            String[] dbFields = {"category_number", "category_name"};

            if (reportData != null) {
                printPDFReport(reportData, reportTitle, titles, dbFields, initialName);
                reportData.close();
            } else {
                System.out.println("Немає даних для звіту.");
            }
        } catch (SQLException | IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet getCategoryReportData() {
        return executeQuery(
                "SELECT * " +
                        "FROM category " +
                        "ORDER BY category_name;"
        );
    }

    // 4.4. Видруковувати звіти з інформацією про усіх товари
    public static void printReportProduct () {
        try {
            String initialName = "";
            ResultSet reportData = null;
            String reportTitle = "";

            reportData = getProductReportData();
            initialName = "Звіт всі товари " + LocalDate.now();
            reportTitle = "Звіт з інформацією про всі товари";

            String[] titles = {"ID", "Номер категорії", "Назва категорії", "Назва товару", "Характеристики"};
            String[] dbFields = {"id_product", "category_number", "category_name", "product_name", "characteristics"};

            if (reportData != null) {
                printPDFReport(reportData, reportTitle, titles, dbFields, initialName);
                reportData.close();
            } else {
                System.out.println("Немає даних для звіту.");
            }
        } catch (SQLException | IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet getProductReportData() {
        return executeQuery(
                "SELECT product.id_product, product.category_number, " +
                        "category.category_name, product.product_name, " +
                        "product.characteristics " +
                        "FROM product " +
                        "INNER JOIN category " +
                        "ON category.category_number = product.category_number " +
                        "ORDER BY product.product_name;"
        );
    }

    // 4.5. Видруковувати звіти з інформацією про усіх товари у магазині
    public static void printReportProductInStore (int scene) {
        try {
            String initialName = "";
            ResultSet reportData = null;
            String reportTitle = "";

            if(scene == 0) { // Видруковувати звіти з інформацією про усі товари у магазині
                reportData = getProductInStoreReportData();
                initialName = "Звіт всі товари в магазині " + LocalDate.now();
                reportTitle = "Звіт з інформацією про всі товари в магазині";
            } else if(scene == 1 || scene == 2) { // Видруковувати звіти з інформацією про усі АКЦІЙНІ товари у магазині
                reportData = getProductInStoreReportData_prom();
                initialName = "Звіт всі акційні товари в магазині " + LocalDate.now();
                reportTitle = "Звіт з інформацією про всі акційні товари в магазині";
            } else if(scene == 3 || scene == 4) { // Видруковувати звіти з інформацією про усі НЕАКЦІЙНІ товари у магазині
                reportData = getProductInStoreReportData_non_prom();
                initialName = "Звіт всі неакційні товари в магазині " + LocalDate.now();
                reportTitle = "Звіт з інформацією про всі неакційні товари в магазині";
            }

            String[] titles = {"UPC", "UPC акційного товару", "ID товару", "Назва товару", "Характеристики", "Ціна",
                    "Кількість товару", "Акційний"};
            String[] dbFields = {"UPC", "UPC_prom", "id_product", "product_name", "characteristics", "selling_price",
            "products_number", "promotional_product"};

            if (reportData != null) {
                printPDFReport(reportData, reportTitle, titles, dbFields, initialName);
                reportData.close();
            } else {
                System.out.println("Немає даних для звіту.");
            }
        } catch (SQLException | IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet getProductInStoreReportData() {
        return executeQuery(
                "SELECT UPC, UPC_prom, store_product.id_product, " +
                        "product.product_name, product.characteristics, selling_price, " +
                        "products_number, promotional_product " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "ORDER BY product_name;"
        );
    }

    private static ResultSet getProductInStoreReportData_prom() {
        return executeQuery(
                "SELECT UPC, UPC_prom, store_product.id_product, " +
                        "product.product_name, product.characteristics, selling_price, " +
                        "products_number, promotional_product " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "WHERE promotional_product = '1'" +
                        "ORDER BY product_name;"
        );
    }

    private static ResultSet getProductInStoreReportData_non_prom() {
        return executeQuery(
                "SELECT UPC, UPC_prom, store_product.id_product, " +
                        "product.product_name, product.characteristics, selling_price, " +
                        "products_number, promotional_product " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "WHERE promotional_product = '0'" +
                        "ORDER BY product_name;"
        );
    }

    // 4.6. Видруковувати звіти з інформацією про усіх чеки;
    public static void printReportCheck () {
        try {
            String initialName = "";
            ResultSet reportData = null;
            String reportTitle = "";

            reportData = getCheckReportData();
            initialName = "Звіт всі чеки " + LocalDate.now();
            reportTitle = "Звіт з інформацією про всі чеки";

            String[] titles = {"Номер чеку", "id працівника", "Номер карти клієнта", "Дата друку", "Загальна сума", "ПДВ"};
            String[] dbFields = {"check_number", "id_employee", "card_number", "print_date", "sum_total", "vat"};

            if (reportData != null) {
                printPDFReport(reportData, reportTitle, titles, dbFields, initialName);
                reportData.close();
            } else {
                System.out.println("Немає даних для звіту.");
            }
        } catch (SQLException | IOException | com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet getCheckReportData() {
        return executeQuery(
                "SELECT * " +
                        "FROM `check` " +
                        "ORDER BY check_number;"
        );
    }

    // 5. Отримати інформацію про усіх працівників, відсортованих за прізвищем;
    public static ResultSet getEmployeesInformation() throws SQLException {
        return executeQuery(
                "SELECT * " +
                        "FROM employee " +
                        "ORDER BY empl_surname"
        );
    }

    // 6. Отримати інформацію про усіх працівників, що займають посаду касира, відсортованих за прізвищем;
    public static ResultSet getCashiersInformation() throws SQLException {
        return executeQuery(
                "SELECT * " +
                        "FROM employee " +
                        "WHERE empl_role = 'Касир'" +
                        "ORDER BY empl_surname"
        );
    }

    // 7. Отримати інформацію про усіх постійних клієнтів, відсортованих за прізвищем;
    public static ResultSet getCustomersInformation() throws SQLException {
        return executeQuery(
                "SELECT card_number, " +
                        "CONCAT(cust_surname, ' ', cust_name, ' ', cust_patronymic) AS ПІБ, " +
                        "phone_number, " +
                        "CONCAT('м. ', city, ', вул. ', street, ', поштовий індекс : ', zip_code, percent) AS Адреса, " +
                        "percent " +
                        "FROM customer_card " +
                        "ORDER BY cust_surname"
        );
    }

    // 8. Отримати інформацію про усі категорії, відсортовані за назвою;
    public static ResultSet getCategoriesInformation () throws SQLException {
        return executeQuery(
                "SELECT * " +
                        "FROM category " +
                        "ORDER BY category_name"
        );
    }

    // 9. Отримати інформацію про усі товари, відсортовані за назвою;
    public static ResultSet getProductsInformation_name () throws SQLException {
        return executeQuery(
                "SELECT product.id_product, product.category_number, " +
                        "category.category_name, product.product_name, " +
                        "product.characteristics " +
                        "FROM product " +
                        "INNER JOIN category " +
                        "ON category.category_number = product.category_number " +
                        "ORDER BY product.product_name;"
        );
    }

    // товари відсортовані по імені, але лише ті, що є в магазині
    public static ResultSet getProductsInformation_quantity () throws SQLException {
        return executeQuery(
                "SELECT DISTINCT product.id_product, product.category_number, category.category_name, " +
                        "product.product_name, product.characteristics " +
                        "FROM product " +
                        "INNER JOIN category ON category.category_number = product.category_number " +
                        "INNER JOIN store_product ON store_product.id_product = product.id_product " +
                        "ORDER BY product.product_name;"
        );
    }

    // 10. Отримати інформацію про усі товари у магазині, відсортовані за кількістю;
    public static ResultSet getProductsInStoreInformation_quantity () throws SQLException {
        return executeQuery(
                "SELECT UPC, UPC_prom, store_product.id_product, " +
                        "product.product_name, selling_price, " +
                        "products_number, promotional_product " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "ORDER BY products_number;"
        );
    }

    // 11. За прізвищем працівника знайти його телефон та адресу;
    public static ResultSet getEmployeesInformation_bySurname(String surname) throws SQLException {
        return executeQuery(
                "SELECT empl_surname, city, street, phone_number " +
                        "FROM employee " +
                        "WHERE empl_surname = '" + surname + "'"
        );
    }

    // 12. Отримати інформацію про усіх постійних клієнтів, що мають карту клієнта із певним відсотком, посортованих за прізвищем;
    public static ResultSet getCustomersInformation_byPercent(String percent) throws SQLException {
        return executeQuery(
                "SELECT card_number, " +
                        "CONCAT(cust_surname, ' ', cust_name, ' ', cust_patronymic) AS ПІБ, " +
                        "phone_number, " +
                        "CONCAT('м. ', city, ', вул. ', street, ', поштовий індекс : ', zip_code, percent) AS Адреса, " +
                        "percent " +
                        "FROM customer_card " +
                        "WHERE percent = " + "'" + percent + "'" +
                        "ORDER BY cust_surname"
        );
    }

    // 13. Здійснити пошук усіх товарів, що належать певній категорії, відсортованих за назвою;
    public static ResultSet searchByCategory(String option, String value) throws SQLException {
        return executeQuery(
                "SELECT product.id_product, product.category_number, " +
                        "category.category_name, product.product_name, " +
                        "product.characteristics " +
                        "FROM product " +
                        "INNER JOIN category " +
                        "ON category.category_number = product.category_number " +
                        "WHERE category." + option + " = '" + value + "'" +
                        "ORDER BY product.product_name;"
        );
    }

    // 14. За UPC-товару знайти ціну продажу товару, кількість наявних одиниць товару, назву та характеристики товару;
    public static ResultSet searchByUPC(String UPC) throws SQLException {
        return executeQuery(
                "SELECT product.product_name, product.characteristics, " +
                        "selling_price, products_number " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "WHERE UPC = '" + UPC + "'"
        );
    }

    // 15. Отримати інформацію про усі акційні товари, відсортовані за кількістю одиниць товару/ за назвою;
    private static ResultSet get_name(char promotion) throws SQLException {
        return executeQuery(
                "SELECT UPC, UPC_prom, store_product.id_product, " +
                        "product.product_name, selling_price, " +
                        "products_number, promotional_product " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "WHERE promotional_product = '" + promotion + "' " +
                        "ORDER BY product_name, products_number;"
        );
    }

    private static ResultSet get_quantity(char promotion) throws SQLException {
        return executeQuery(
                "SELECT UPC, UPC_prom, store_product.id_product, " +
                        "product.product_name, selling_price, " +
                        "products_number, promotional_product " +
                        "FROM store_product " +
                        "INNER JOIN product " +
                        "ON product.id_product = store_product.id_product " +
                        "WHERE promotional_product = '" + promotion + "' " +
                        "ORDER BY products_number, product_name;"
        );
    }

    public static ResultSet getProductsInStoreInformation_prom_name () throws SQLException {
        return get_name('1');
    }

    public static ResultSet getProductsInStoreInformation_prom_quantity () throws SQLException {
        return get_quantity('1');
    }

    // 16. Отримати інформацію про усі не акційні товари, відсортовані за кількістю одиниць товару/ за назвою;
    public static ResultSet getProductsInStoreInformation_non_prom_name () throws SQLException {
        return get_name('0');
    }

    public static ResultSet getProductsInStoreInformation_non_prom_quantity () throws SQLException {
        return get_quantity('0');
    }

    // 17. Отримати інформацію про усі чеки, створені певним касиром за певний період часу (з
    // можливістю перегляду куплених товарів у цьому чеку, їх назви, к-сті та ціни);
    public static ResultSet checksByCashier(int scene, String id_empl_or_UPC, LocalDate startDate, LocalDate endDate) throws SQLException {
        ResultSet result = null;
        if (scene == 1) {
            result = executeQuery(
                    "SELECT * " +
                            "FROM `check` " +
                            "WHERE id_employee = '" + id_empl_or_UPC + "' " +
                            "AND print_date BETWEEN '" + startDate + "' " +
                            "AND '" + endDate + "'"
            );
        } else if (scene == 2) {
            result = executeQuery(
                    "SELECT * " +
                            "FROM `check` " +
                            "INNER JOIN sale ON sale.check_number = `check`.check_number " +
                            "WHERE UPC = '" + id_empl_or_UPC + "' " +
                            "AND print_date BETWEEN '" + startDate + "' " +
                            "AND '" + endDate + "'"
            );
        }
        return result;
    }

    // 18. Отримати інформацію про усі чеки, створені усіма касирами за певний період часу (з можливістю
    // перегляду куплених товарів у цьому чеку, їх назва, к-сті та ціни);
    public static ResultSet allChecks (LocalDate startDate, LocalDate endDate) throws SQLException {
        return executeQuery(
                "SELECT * " +
                        "FROM `check` " +
                        "WHERE print_date BETWEEN '" + startDate + "' " +
                        "AND '" + endDate + "'"
        );
    }

    // 19. Визначити загальну суму проданих товарів з чеків, створених певним касиром за певний період часу;
    public static ResultSet totalSum_byCashier(String id_empl, LocalDate startDate, LocalDate endDate) throws SQLException {
        return executeQuery(
                "SELECT SUM(sum_total) AS total_sales_price " +
                        "FROM `check` " +
                        "WHERE id_employee = '" + id_empl + "' " +
                        "AND print_date " +
                        "BETWEEN '" + startDate + "' " +
                        "AND '" + endDate + "'"
        );
    }

    // 20. Визначити загальну суму проданих товарів з чеків, створених усіма касиром за певний період часу;
    public static ResultSet totalSum_all (LocalDate startDate, LocalDate endDate) throws SQLException {
        return executeQuery(
                "SELECT SUM(sum_total) AS total_sales_price " +
                        "FROM `check` " +
                        "WHERE print_date " +
                        "BETWEEN '" + startDate + "' " +
                        "AND '" + endDate + "'"
        );
    }

    // 21. Визначити загальну кількість одиниць певного товару, проданого за певний період часу.
    public static ResultSet totalQuality(String upc, LocalDate startDate, LocalDate endDate) throws SQLException {
        return executeQuery(
                "SELECT SUM(s.product_number) AS total_quantity_sold " +
                        "FROM `check` c " +
                        "INNER JOIN sale s " +
                        "ON c.check_number = s.check_number " +
                        "WHERE s.UPC = '" + upc + "' AND print_date " +
                        "BETWEEN '" + startDate + "' " +
                        "AND '" + endDate + "'"
        );
    }


    // додаткові методи
    private static void setAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
