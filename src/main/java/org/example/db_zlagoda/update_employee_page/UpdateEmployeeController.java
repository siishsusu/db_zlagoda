package org.example.db_zlagoda.update_employee_page;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.db_zlagoda.db_data.DatabaseManager_mm.updateEmployee;

public class UpdateEmployeeController implements Initializable {
    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> roleComBox;

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

    private String employeeId;
    public void initData(String id) {
        employeeId = id;
        fillFields();
    }

    private void fillFields() {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            // Витягнути інформацію за ід з бази даних
            PreparedStatement preparedStatement = connectDB.prepareStatement(
                    "SELECT * FROM employee WHERE id_employee = ?"
            );
            preparedStatement.setString(1, employeeId);

            ResultSet resultSet = preparedStatement.executeQuery();

            PreparedStatement usernamePrSt = connectDB.prepareStatement(
                    "SELECT * FROM login_table WHERE id_employee = ?"
            );
            usernamePrSt.setString(1, employeeId);
            ResultSet usernameRs = usernamePrSt.executeQuery();

            if (resultSet.next()) {
                lastNameField.setText(resultSet.getString("empl_surname"));
                firstNameField.setText(resultSet.getString("empl_name"));
                patronymicField.setText(resultSet.getString("empl_patronymic"));
                roleComBox.setValue(resultSet.getString("empl_role"));
                salaryField.setText(resultSet.getString("salary"));
                birthDatePicker.setValue(LocalDate.parse(resultSet.getString("date_of_birth")));
                firstWorkDayPicker.setValue(LocalDate.parse(resultSet.getString("date_of_start")));
                phoneNumberField.setText(resultSet.getString("phone_number"));
                cityField.setText(resultSet.getString("city"));
                streetField.setText(resultSet.getString("street"));
                zipCodeField.setText(resultSet.getString("zip_code"));

                if (usernameRs.next()) {
                    userNameField.setText(usernameRs.getString("username"));
                }
                passportField.setDisable(true);
                confPassportField.setVisible(false);
            }

            resultSet.close();
            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        requiredDates = new ArrayList<>();
        requiredDates.add(birthDatePicker);
        requiredDates.add(firstWorkDayPicker);
    }

    @FXML
    void Select(ActionEvent event) {

    }

    @FXML
    public void updateButtonOnAction(ActionEvent event) {
        boolean allFieldsValid = validateFields();
        boolean allDatesSelectedAndValid = validateDates();
        boolean roleSelected = validateRole();

        if (allFieldsValid && allDatesSelectedAndValid && roleSelected) {
            update();
        }
    }

    private boolean validateDates() {
        boolean allDatesSelectedAndValid = true;
        LocalDate today = LocalDate.now(), eighteenYearsAgo = today.minusYears(18);

        for (DatePicker pick : requiredDates) {
            LocalDate selectedDate = pick.getValue();
            if (selectedDate == null || (pick == birthDatePicker && selectedDate.isAfter(eighteenYearsAgo))) {
                pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                allDatesSelectedAndValid = false;
            } else if (pick == firstWorkDayPicker) {
                LocalDate birthDate = birthDatePicker.getValue();
                Period ageAtStartOfWork = Period.between(birthDate, selectedDate);
                if (ageAtStartOfWork.getYears() < 18) {
                    pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                    allDatesSelectedAndValid = false;
                }
            } else {
                pick.setStyle(null);
            }
        }
        return allDatesSelectedAndValid;
    }

    private boolean validateFields() {
        boolean allFieldsValid = true;
        for (Pair<TextField, Pair<Label, Tooltip>> field : requiredFields) {
            TextField textField = field.getKey();
            Label textValidator = field.getValue().getKey();
            Tooltip textValidatorTooltip = field.getValue().getValue();

            if (textField.getText().isBlank()) {
                notValid(textField, textValidator, textValidatorTooltip, "Поле не може бути пустим.");
                allFieldsValid = false;
            } else {
                valid(textField, textValidator, textValidatorTooltip);
                validateFieldLength(textField, textValidator, textValidatorTooltip);
            }
        }
        return allFieldsValid;
    }

    private void validateFieldLength(TextField textField, Label textValidator, Tooltip textValidatorTooltip) {
        boolean allFieldsValid = true;
        if (textField == firstNameField || textField == lastNameField || textField == patronymicField || textField == cityField
                || textField == streetField) {
            if (textField.getText().length() > 50) {
                notValid(textField,
                        textValidator,
                        textValidatorTooltip,
                        "Поле повинно містити менше 50 символів.");
                allFieldsValid = false;
            } else {
                valid(textField, textValidator, textValidatorTooltip);
            }
        }

        if (textField == zipCodeField) {
            if (textField.getText().length() > 9) {
                notValid(textField,
                        textValidator,
                        textValidatorTooltip,
                        "Поле повинно містити менше 9 символів.");
                allFieldsValid = false;
            } else {
                valid(textField, textValidator, textValidatorTooltip);
            }
        }

        if (textField == phoneNumberField) {
            if (textField.getText().length() == 13 && textField.getText().startsWith("+380")) {
               valid(textField, textValidator, textValidatorTooltip);
            } else {
                notValid(textField,
                        textValidator,
                        textValidatorTooltip,
                        "Номер телефону повинен складатися з 13 символів  і починатися з \"+380\".");
                allFieldsValid = false;
            }
        }

        if (textField == salaryField) {
            // Перевірка валідності суми зарплати
            String text = textField.getText();
            if (text.matches("^\\d+(\\.\\d+)?$")) {
                textValidator.setText("");

                int indexOfDecimal = text.indexOf('.');
                if (indexOfDecimal == -1) {
                    if (text.length() <= 13) {
                        valid(textField, textValidator, textValidatorTooltip);
                    } else {
                        // Показати помилку про перевищення максимальної довжини
                        notValid(textField,
                                textValidator,
                                textValidatorTooltip,
                                "Кількість повинна бути не більше 13.");
                        allFieldsValid = false;
                    }
                } else {
                    String digitsBeforeDecimal = text.substring(0, indexOfDecimal);
                    String digitsAfterDecimal = text.substring(indexOfDecimal + 1);

                    int afterDecimal = digitsAfterDecimal.length();
                    int beforeDecimal = digitsBeforeDecimal.length();

                    if (beforeDecimal <= (13 - afterDecimal) && afterDecimal <= 4) {
                        valid(textField, textValidator, textValidatorTooltip);
                    } else {
                        // Показати помилку про перевищення максимальної довжини перед або після коми
                        notValid(textField,
                                textValidator,
                                textValidatorTooltip,
                                "Кількість повинна бути не більше 13 (максимум 4 символи після коми).");
                        allFieldsValid = false;
                    }
                }
            } else {
                // Показати помилку про некоректне числове значення
                notValid(textField,
                        textValidator,
                        textValidatorTooltip,
                        "Введіть суму заробітньої плати.");
                allFieldsValid = false;
            }
        }
    }

    private void notValid(TextField textField, Label textValidator, Tooltip textValidatorTooltip, String errorMessage) {
        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
        textValidator.setText(errorMessage);
        textValidatorTooltip.setText(textValidator.getText());
    }

    private void valid(TextField textField, Label textValidator, Tooltip textValidatorTooltip) {
        textValidator.setText("");
        textValidatorTooltip.setText("");
        textField.setStyle(null);
    }

    private boolean validateRole() {
        boolean roleSelected = !roleComBox.getSelectionModel().isEmpty();
        if (!roleSelected) {
            roleComBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            roleValidator.setText("Посада повинна бути обрана.");
        } else {
            roleComBox.setStyle(null);
            roleValidator.setText("");
        }
        return roleSelected;
    }

    public void update(){
        try {
            String newLastName = lastNameField.getText();
            String newFirstName = firstNameField.getText();
            String newPatronymic = patronymicField.getText();
            String newRole = roleComBox.getValue();
            String newSalary = salaryField.getText();
            String newBirthDate = birthDatePicker.getValue().toString();
            String newStartWorkDate = firstWorkDayPicker.getValue().toString();
            String newPhoneNumber = phoneNumberField.getText();
            String newCity = cityField.getText();
            String newStreet = streetField.getText();
            String newZipCode = zipCodeField.getText();

            String newUsername = userNameField.getText();

            updateEmployee(employeeId, newLastName, newFirstName, newPatronymic, newRole, newSalary, newBirthDate, newStartWorkDate,
                    newPhoneNumber, newCity, newStreet, newZipCode, newUsername);

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
