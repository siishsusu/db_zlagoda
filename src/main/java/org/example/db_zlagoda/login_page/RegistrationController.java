package org.example.db_zlagoda.login_page;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.db_zlagoda.db_data.DatabaseManager_mm.checkUsernameAvailability;
import static org.example.db_zlagoda.db_data.DatabaseManager_mm.insertEmployee;

public class RegistrationController implements Initializable {
    @FXML
    private ComboBox<String> roleComBox;

    @FXML
    private Button cancelButton, generateUsernameButton;
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

        generateUsernameButton.setDisable(true);

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

        if (!firstNameField.getText().isBlank() && !lastNameField.getText().isBlank()){
            generateUsernameButton.setDisable(false);
        }

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            lastNameField.textProperty().addListener((observable_last, oldValue_last, newValue_last) -> {
                if (!newValue_last.isBlank() && !newValue.isBlank()) {
                    generateUsernameButton.setDisable(false);
                } else {
                    generateUsernameButton.setDisable(true);
                }
            });
        });

        userNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean usernameAvailable = checkUsernameAvailability(newValue);
            if (!usernameAvailable) {
                userNameField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            } else {
                userNameField.setStyle(null);
            }
        });

    }

    @FXML
    void Select(ActionEvent event) {
    }

    @FXML
    public void generateUsernameButtonOnAction(ActionEvent event){
        String name = firstNameField.getText().toLowerCase();
        String surname = lastNameField.getText().toLowerCase();
        String username_gen = transliterateUkrainianToEnglish(name) + "_" + transliterateUkrainianToEnglish(surname);
        // Перевірка унікальності username
        boolean usernameAvailable = checkUsernameAvailability(username_gen);
        if(!usernameAvailable){
            username_gen += random.nextInt(5000);
        }else{
            userNameField.setStyle(null);
        }
        userNameField.setText(username_gen);
    }

    public static String transliterateUkrainianToEnglish(String input) {
        StringBuilder transliterated = new StringBuilder();

        String[][] ukrainianToEnglish = {
                {"а", "a"}, {"б", "b"}, {"в", "v"}, {"г", "h"}, {"д", "d"},
                {"е", "e"}, {"є", "ye"}, {"ж", "zh"}, {"з", "z"}, {"и", "y"},
                {"і", "i"}, {"ї", "yi"}, {"й", "y"}, {"к", "k"}, {"л", "l"},
                {"м", "m"}, {"н", "n"}, {"о", "o"}, {"п", "p"}, {"р", "r"},
                {"с", "s"}, {"т", "t"}, {"у", "u"}, {"ф", "f"}, {"х", "kh"},
                {"ц", "ts"}, {"ч", "ch"}, {"ш", "sh"}, {"щ", "shch"}, {"ь", ""},
                {"ю", "yu"}, {"я", "ya"}
        };

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            String currentLetter = Character.toString(currentChar);

            for (String[] pair : ukrainianToEnglish) {
                if (pair[0].equals(currentLetter)) {
                    transliterated.append(pair[1]);
                    break;
                }
            }
            if (transliterated.length() <= i) {
                transliterated.append(currentChar);
            }
        }
        return transliterated.toString();
    }

    @FXML
    public void registerButtonOnAction(ActionEvent event) {
        boolean allFieldsValid = validateFields();
        boolean allDatesSelectedAndValid = validateDates();
        boolean roleSelected = !roleComBox.getSelectionModel().isEmpty();

        if (allFieldsValid && allDatesSelectedAndValid && roleSelected) {
            registerEmployee();
        }

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        boolean allFieldsValid = true;
        for (Pair<TextField, Pair<Label, Tooltip>> field : requiredFields) {
            TextField textField = field.getKey();
            Label textValidator = field.getValue().getKey();
            Tooltip textValidatorTooltip = field.getValue().getValue();

            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                allFieldsValid = false;
            } else {
                textField.setStyle(null);

                if (textField == firstNameField || textField == lastNameField || textField == patronymicField ||
                        textField == cityField || textField == streetField) {
                    if (textField.getText().length() > 50) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("This field must contain less than 50 characters.");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    } else {
                        textValidator.setText("");
                    }
                }

                if (textField == zipCodeField) {
                    if (textField.getText().length() > 9) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("This field must contain less than 9 characters.");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    } else {
                        textValidator.setText("");
                    }
                }

                if (textField == phoneNumberField) {
                    String phoneRegex = "^\\+380\\d{9}$";
                    if (!textField.getText().matches(phoneRegex)) {
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("The phone number must start with \"+380\" and contain 13 digits.");
                        textValidatorTooltip.setText(textValidator.getText());
                        allFieldsValid = false;
                    } else {
                        textValidator.setText("");
                    }
                }

                if (textField == salaryField) {
                    String text = textField.getText();
                    if (!text.matches("^\\d+(\\.\\d+)?$")) {
                        textValidator.setText("Enter valid salary value.");
                        textValidatorTooltip.setText(textValidator.getText());
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsValid = false;
                    } else if (text.length() > 13 || (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 4)) {
                        textValidator.setText("The number of characters should not exceed 13 (4 decimal places).");
                        textValidatorTooltip.setText(textValidator.getText());
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        allFieldsValid = false;
                    } else {
                        textField.setStyle(null);
                        textValidator.setText("");
                    }
                }
            }
        }
        return allFieldsValid;
    }

    private boolean validateDates() {
        LocalDate today = LocalDate.now();
        LocalDate eighteenYearsAgo = today.minusYears(18);
        boolean allDatesSelectedAndValid = true;

        for (DatePicker pick : requiredDates) {
            LocalDate selectedDate = pick.getValue();
            if (selectedDate == null || (pick == birthDatePicker && selectedDate.isAfter(eighteenYearsAgo))) {
                pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                allDatesSelectedAndValid = false;
            } else {
                pick.setStyle(null);
            }
        }

        return allDatesSelectedAndValid;
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
            String id_gen = generateRandomId(10, false);

            insertEmployee(id_gen, lastName, firstName, patronymic, role, salary, Date.valueOf(birthDate),
                    Date.valueOf(firstWorkDay), phoneNumber, city, street, zipCode, username, password);

            clearFields();

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void clearFields(){
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
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String CHARACTERS_ON = "0123456789";
    private static final SecureRandom random = new SecureRandom();
    public static String generateRandomId(int length, boolean onlyNums) {
        StringBuilder idBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex; char randomChar;
            if (onlyNums == false) {
                randomIndex = random.nextInt(CHARACTERS.length());
                randomChar = CHARACTERS.charAt(randomIndex);
            } else {
                randomIndex = random.nextInt(CHARACTERS_ON.length());
                randomChar = CHARACTERS_ON.charAt(randomIndex);
            }
            idBuilder.append(randomChar);
        }
        return idBuilder.toString();
    }
}
