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
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM employee WHERE id_employee = '" + employeeId + "'");

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
//        requiredFields.add(new Pair<>(userNameField, new Pair<>(usernameValidator, usernameValidatorTT)));
//        requiredFields.add(new Pair<>(passportField, new Pair<>(passwordValidator, passwordValidatorTT)));
//        requiredFields.add(new Pair<>(confPassportField, new Pair<>(confPasswordValidator, confPasswordValidatorTT)));

        requiredDates = new ArrayList<>();
        requiredDates.add(birthDatePicker);
        requiredDates.add(firstWorkDayPicker);
    }

    @FXML
    void Select(ActionEvent event) {

    }

    @FXML
    public void updateButtonOnAction(ActionEvent event) {
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

                if (textField == firstNameField || textField == lastNameField || textField == patronymicField || textField == cityField
                        || textField == streetField) {
                    if (textField.getText().length() > 50) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("Поле повинно містити менше 50 символів.");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    } else {
                        textValidator.setText("");
                    }
                }

                if (textField == zipCodeField) {
                    if (textField.getText().length() > 9) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("Поле повинно містити менше 9 символів.");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    } else {
                        textValidator.setText("");
                    }
                }

                if (textField == phoneNumberField) {
                    if (textField.getText().length() == 13 && textField.getText().startsWith("+380")) {

                    } else {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("Номер телефону повинен складатися з 13 символів  і починатися з \"+380\".");
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
                                textValidator.setText(null);
                                textValidatorTooltip.setText(textValidator.getText());
                            } else {
                                // Показати помилку про перевищення максимальної довжини
                                textValidator.setText("Кількість повинна бути не більше 13.");
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
                                textValidator.setText("Кількість повинна бути не більше 13 (максимум 4 символи після коми).");
                                textValidatorTooltip.setText(textValidator.getText());
                                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                                allFieldsValid = false;
                            }
                        }
                    } else {
                        // Показати помилку про некоректне числове значення
                        textValidator.setText("Введіть суму заробітньої плати.");
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
                } else if (pick == firstWorkDayPicker) {
                    LocalDate birthDate = birthDatePicker.getValue();
                    Period ageAtStartOfWork = Period.between(birthDate, selectedDate);
                    if (ageAtStartOfWork.getYears() < 18) {
                        pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allDatesSelectedAndValid = false;
                    }
                }
                else {
                    pick.setStyle(null);
                }
            }
        }
        boolean roleSelected = !roleComBox.getSelectionModel().isEmpty();
        if (!roleSelected) {
            roleComBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            roleValidator.setText("Посада повинна бути обрана.");
        } else {
            roleComBox.setStyle(null);
            roleValidator.setText("");
        }

        if (allFieldsValid && allDatesSelectedAndValid && roleSelected) {
            updateEmployee();
        }
    }

    public void updateEmployee(){
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

            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            // 2.1. Редагувати дані про працівників
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

            statement.close();
            connectDB.close();

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
