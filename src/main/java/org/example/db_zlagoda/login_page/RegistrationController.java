package org.example.db_zlagoda.login_page;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.db_zlagoda.DatabaseConnection;

import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {
    @FXML
    private ComboBox<String> roleComBox;

    @FXML
    private Button cancelButton;
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private Label firstNameLabel, lastNameLabel, patronymicLabel, convBoxLabel, salaryLabel, birthDateLabel, firstWorkDayLabel,
            phoneNumberLabel, cityLabel, streetLabel, zipCodeLabel, userNameLabel, passportLabel, confPassportLabel;

    @FXML
    private TextField firstNameField, lastNameField, patronymicField, salaryField, phoneNumberField, cityField,
            streetField, zipCodeField, userNameField, passportField, confPassportField;

    @FXML
    private Label firstNameValidator, lastNameValidator, salaryValidator, phoneNumValidator, cityValidator, streetValidator,
            zipCodeValidator, usernameValidator, passwordValidator, confPasswordValidator, roleValidator;

    @FXML
    private Tooltip firstNameValidatorTT, lastNameValidatorTT, salaryValidatorTT, phoneNumValidatorTT, cityValidatorTT, streetValidatorTT,
            zipCodeValidatorTT, usernameValidatorTT, passwordValidatorTT, confPasswordValidatorTT, roleValidatorTT;

    private List<Pair<TextField, Pair<Label, Tooltip>>> requiredFields;

    @FXML
    private DatePicker birthDatePicker, firstWorkDayPicker;

    private List<DatePicker> requiredDates;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearDatabase();
        fillDataBase();

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Менеджер",
                        "Касир"
                );
        roleComBox.setItems(options);

        requiredFields = new ArrayList<>();
        requiredFields.add(new Pair<>(firstNameField, new Pair<>(firstNameValidator, firstNameValidatorTT)));
        requiredFields.add(new Pair<>(lastNameField, new Pair<>(lastNameValidator, lastNameValidatorTT)));
        requiredFields.add(new Pair<>(salaryField, new Pair<>(salaryValidator, salaryValidatorTT)));
        requiredFields.add(new Pair<>(phoneNumberField, new Pair<>(phoneNumValidator, phoneNumValidatorTT)));
        requiredFields.add(new Pair<>(cityField, new Pair<>(cityValidator, cityValidatorTT)));
        requiredFields.add(new Pair<>(streetField, new Pair<>(streetValidator, streetValidatorTT)));
        requiredFields.add(new Pair<>(zipCodeField, new Pair<>(zipCodeValidator, zipCodeValidatorTT)));
        requiredFields.add(new Pair<>(userNameField, new Pair<>(usernameValidator, usernameValidatorTT)));
        requiredFields.add(new Pair<>(passportField, new Pair<>(passwordValidator, passwordValidatorTT)));
        requiredFields.add(new Pair<>(confPassportField, new Pair<>(confPasswordValidator, confPasswordValidatorTT)));

        requiredDates = new ArrayList<>();
        requiredDates.add(birthDatePicker);
        requiredDates.add(firstWorkDayPicker);

    }

    @FXML
    void Select(ActionEvent event) {
    }

    @FXML
    public void registerButtonOnAction(ActionEvent event) {
        TextField textField;
        Label textValidator;
        Tooltip textValidatorTooltip;
        boolean allFieldsValid = true;
        for (Pair<TextField, Pair<Label, Tooltip>> field : requiredFields) {
            textField = field.getKey();
            textValidator = field.getValue().getKey();
            textValidatorTooltip = field.getValue().getValue();

            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                allFieldsValid = false;
            } else {
                textField.setStyle(null);

                if(textField == firstNameField || textField == lastNameField || textField == patronymicField || textField == cityField
                        || textField == streetField){
                    if(textField.getText().length() > 50){
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("This field must contain less than 50 characters.");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    }else{
                        textValidator.setText("");
                    }
                }

                if(textField == zipCodeField){
                    if(textField.getText().length() > 9){
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("This field must contain less than 9 characters.");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    }else{
                        textValidator.setText("");
                    }
                }

                if(textField == phoneNumberField){
                    if (textField.getText().length() == 13 && textField.getText().startsWith("+380")) {

                    }else{
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("The phone number must contain 13 characters and start with \"+380\".");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    }
                }

                if (textField == salaryField) {
                    String text = textField.getText();
                    if (text.matches("^\\d+(\\.\\d+)?$")) {
                        textValidator.setText("");

                        int indexOfDecimal = text.indexOf('.');
                        if (indexOfDecimal == -1) {
                            if (text.length() <= 13) {
                                textField.setStyle(null);
                            } else {
                                // Показати помилку про перевищення максимальної довжини
                                textValidator.setText("The number of characters should not exceed 13.");
                                textValidatorTooltip.setText(textValidator.getText());
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                allFieldsValid = false;
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
                                textValidator.setText("The number of characters should not exceed 13 (4 decimal places).");
                                textValidatorTooltip.setText(textValidator.getText());
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                allFieldsValid = false;
                            }
                        }
                    } else {
                        // Показати помилку про некоректне числове значення
                        textValidator.setText("Enter valid salary value.");
                        textValidatorTooltip.setText(textValidator.getText());
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsValid = false;
                    }
                }

            }

        }

        LocalDate today = LocalDate.now(), eighteenYearsAgo = today.minusYears(18);
        boolean allDatesSelectedAndValid = true;
        for (DatePicker pick : requiredDates) {
            LocalDate selectedDate = pick.getValue();
            if (selectedDate == null) {
                pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                allDatesSelectedAndValid = false;
            } else {
                if (pick == birthDatePicker && selectedDate.isAfter(eighteenYearsAgo)) {
                    pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                    allDatesSelectedAndValid = false;
                } else {
                    pick.setStyle(null);
                }
            }
        }
        boolean roleSelected = !roleComBox.getSelectionModel().isEmpty();
        if (!roleSelected) {
            roleComBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            roleValidator.setText("Choose role");
        } else {
            roleComBox.setStyle(null);
            roleValidator.setText("");
        }

        if (allFieldsValid && allDatesSelectedAndValid && roleSelected) {
            registerEmployee();
        }

    }

    public static String hashPassword(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(password.getBytes());
            byte [] arr = md.digest();
            StringBuilder sb = new StringBuilder();

            for(byte b : arr){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }catch (Exception error){
            error.printStackTrace();
            error.getCause();
        }
        return null;
    }

    public void registerEmployee(){
//        clearDatabase();
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String patronymic = patronymicField.getText();
        String role = roleComBox.getValue().toString();
        String salary = salaryField.getText();
        LocalDate birthDate = birthDatePicker.getValue();
        LocalDate firstWorkDay = firstWorkDayPicker.getValue();
        String phoneNumber = phoneNumberField.getText();
        String city = cityField.getText();
        String street = streetField.getText();
        String zipCode = zipCodeField.getText();
        String username = userNameField.getText();
        String password = hashPassword(passportField.getText());

        try {
            String id_gen = generateRandomId(10);

            // 1.1. Додавати нові дані про працівників
            String insertUserQuery = "INSERT INTO employee (id_employee, empl_surname, empl_name, empl_patronymic, empl_role, salary, " +
                    "date_of_birth, date_of_start, phone_number, city, street, zip_code) " +
                    "VALUES ('" + id_gen + "', '" + lastName + "', '" + firstName + "', '" + patronymic + "', '" + role + "', '" + salary + "', '" +
                    Date.valueOf(birthDate) + "', '" + Date.valueOf(firstWorkDay) + "', '" + phoneNumber + "', '" + city + "', '" + street + "', '" +
                    zipCode +  "')";

            String insertLoginInfo = "INSERT INTO login_table (username, password, id_employee) " +
                    "VALUES ('" + username + "', '" + password + "', '" + id_gen + "')";

            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertUserQuery);
            statement.executeUpdate(insertLoginInfo);

            firstNameField.clear();
            lastNameField.clear();
            patronymicField.clear();
            roleComBox.getSelectionModel().clearSelection();
            salaryField.clear();
            birthDatePicker.setValue(null);
            firstWorkDayPicker.setValue(null);
            phoneNumberField.clear();
            cityField.clear();
            streetField.clear();
            zipCodeField.clear();
            userNameField.clear();
            passportField.clear();
            confPassportField.clear();

        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            try {
                connectDB.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearDatabase() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        try {
            String clearDatabaseLogin = "DELETE FROM login_table";
            String clearDatabaseQuery = "DELETE FROM employee";

            Statement statement = connectDB.createStatement();
            statement.executeUpdate(clearDatabaseLogin);
            statement.executeUpdate(clearDatabaseQuery);

        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            try {
                connectDB.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    public static String generateRandomId(int length) {
        StringBuilder idBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            idBuilder.append(randomChar);
        }
        return idBuilder.toString();
    }


    // -----------------------------------------------------------------------------------------------------------------
    public void fillDataBase(){
        String[] lastNames = {"Петренко", "Іваненко", "Коваленко", "Сидоренко", "Мельник", "Ковальчук", "Павленко",
                "Кравченко", "Шевченко", "Бойко", "Кузьменко", "Шевчук", "Савченко", "Мороз", "Бондаренко", "Ткаченко",
                "Кучеренко", "Захаренко", "Коваленко", "Романенко"};
        String[] firstNames = {"Олександр", "Іван", "Софія", "Марія", "Максим", "Анна", "Віктор", "Ольга", "Артем", "Юлія",
                "Василь", "Катерина", "Дмитро", "Тетяна", "Анастасія", "Петро", "Христина", "Сергій", "Наталія", "Андрій"};
        String[] middleNames = {"Олександрович", "Іванович", "Володимирівна", "Петрівна", "Миколайович", "Андріївна",
                "Сергійович", "Ярославівна", "Артемович", "Дмитрівна", "Васильович", "Віталіївна", "Ігорович",
                "Олегівна", "Владиславівна", "Романович", "Тарасівна", "Богданович", "Григоріївна", "Максимович"};
        String[] roles = {"Менеджер", "Касир"};
        double[] salaries = {8000.0, 8200.0, 8500.0, 8700.0, 9000.0, 9200.0, 9400.0, 9600.0, 9800.0, 10000.0, 6000.0, 6200.0, 6400.0, 6600.0, 6800.0, 7000.0, 7200.0, 7400.0, 7600.0, 7800.0};
        LocalDate[] birthDates = {
                LocalDate.of(1990, 5, 15), LocalDate.of(1985, 8, 25), LocalDate.of(1987, 12, 10), LocalDate.of(1992, 3, 6),
                LocalDate.of(1988, 9, 18), LocalDate.of(1995, 2, 3), LocalDate.of(1983, 7, 22), LocalDate.of(1997, 11, 14),
                LocalDate.of(1991, 4, 30), LocalDate.of(1986, 10, 8), LocalDate.of(1993, 6, 12), LocalDate.of(1989, 1, 28),
                LocalDate.of(1994, 5, 7), LocalDate.of(1984, 11, 20), LocalDate.of(1999, 8, 2), LocalDate.of(1982, 12, 17),
                LocalDate.of(1996, 3, 25), LocalDate.of(1980, 6, 5), LocalDate.of(1981, 9, 9), LocalDate.of(1998, 1, 1)
        };
        LocalDate[] firstWorkDays = {
                LocalDate.of(2010, 9, 20), LocalDate.of(2012, 7, 14), LocalDate.of(2008, 11, 30), LocalDate.of(2015, 4, 5),
                LocalDate.of(2007, 3, 12), LocalDate.of(2018, 5, 28), LocalDate.of(2004, 10, 3), LocalDate.of(2017, 9, 16),
                LocalDate.of(2009, 8, 8), LocalDate.of(2013, 8, 21), LocalDate.of(2017, 6, 19), LocalDate.of(2016, 12, 10),
                LocalDate.of(2023, 1, 22), LocalDate.of(2019, 11, 7), LocalDate.of(2023, 7, 2), LocalDate.of(2014, 10, 14),
                LocalDate.of(2022, 4, 18), LocalDate.of(2020, 2, 29), LocalDate.of(2001, 9, 4), LocalDate.of(2021, 3, 8)
        };
        String[] phoneNumbers = {
                "+380661234567", "+380990123456", "+380951234567", "+380981234567", "+380664567890", "+380997654321",
                "+380955432109", "+380986543210", "+380669876543", "+380992345678", "+380954321098", "+380983456789",
                "+380667890123", "+380998765432", "+380958765432", "+380987654321", "+380662345678", "+380994567890",
                "+380953210987", "+380982109876"
        };
        String[] cities = {
                "Київ", "Харків", "Одеса", "Дніпро", "Львів", "Запоріжжя", "Кривий Ріг", "Миколаїв", "Вінниця", "Херсон",
                "Полтава", "Чернігів", "Черкаси", "Житомир", "Суми", "Івано-Франківськ", "Тернопіль", "Кропивницький",
                "Ужгород", "Маріуполь"
        };
        String[] streets = {
                "Вулиця Шевченка", "Проспект Незалежності", "Вулиця Лесі Українки", "Вулиця Воздвиженська", "Вулиця Тараса Шевченка",
                "Вулиця Івана Франка", "Проспект Гагаріна", "Вулиця Миру", "Проспект Коцюбинського", "Вулиця Арнаутська",
                "Вулиця Героїв Крут", "Вулиця Джеймса Мейса", "Вулиця Степана Бандери", "Вулиця Юрія Вороного", "Вулиця Київська",
                "Вулиця Незалежності", "Вулиця Гетьмана Мазепи", "Вулиця Вокзальна", "Вулиця Івана Франка", "Вулиця М. Грушевського"
        };
        String[] zipCodes = {
                "01001", "02000", "03000", "04000", "05000", "06000", "07000", "08000", "09000", "10000", "11000", "12000",
                "13000", "14000", "15000", "16000", "17000", "18000", "19000", "20000"
        };
        String[] usernames = {
                "oleksandr_petrenko", "ivan_ivanenko", "sofiia_kovalenko", "maria_sydorenko", "maxym_melnyk", "anna_kovalchuk",
                "viktor_pavlenko", "olha_kravchenko", "artem_shevchenko", "yuliia_boiko", "vasyl_kuzmenko", "kateryna_shevchuk",
                "dmytro_savchenko", "tetiana_moroz", "anastasiia_bondarenko", "petro_tkachenko", "hryhorii_kucherenko",
                "nataliia_zakharenko", "andrii_kovalenko", "serhii_romanenko"
        };
        String[] passwords = {
                "qwerty123", "password123", "123456789", "letmein", "welcome123", "abc123", "123456", "password1", "passw0rd",
                "admin123", "qwertyuiop", "iloveyou", "football", "1234567", "123123", "111111", "12345678", "qwerty",
                "password", "1234"
        };
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            for (int i = 0; i < lastNames.length; i++) {
                String id_gen = generateRandomId(10);
                int randomRole = random.nextInt(roles.length);

                String insertQuery = "INSERT INTO employee (id_employee, empl_surname, empl_name, empl_patronymic, empl_role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code) " +
                        "VALUES ('" + id_gen + "', '" + lastNames[i] + "', '" + firstNames[i] + "', '" + middleNames[i] + "', '" + roles[randomRole] + "', " + salaries[i] + ", '" +
                        birthDates[i] + "', '" + firstWorkDays[i] + "', '" + phoneNumbers[i] + "', '" + cities[i] + "', '" + streets[i] + "', '" + zipCodes[i] + "')";


                String insertLoginInfo = "INSERT INTO login_table (username, password, id_employee) " +
                        "VALUES ('" + usernames[i] + "', '" + hashPassword(passwords[i]) + "', '" + id_gen + "')";

                statement.executeUpdate(insertQuery);
                statement.executeUpdate(insertLoginInfo);
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
