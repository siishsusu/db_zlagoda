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
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Manager",
                        "Cashier"
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
            String clearDatabaseQuery = "DELETE FROM employee";
            Statement statement = connectDB.createStatement();
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
}
