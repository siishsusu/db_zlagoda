package org.example.db_zlagoda.login_page;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.DatagramPacket;
import java.net.URL;
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
        for (Pair<TextField, Pair<Label, Tooltip>> field : requiredFields) {
            textField = field.getKey();
            textValidator = field.getValue().getKey();
            textValidatorTooltip = field.getValue().getValue();

            if (textField.getText().isBlank()) {
                textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            } else {
                textField.setStyle(null);

                if(textField == firstNameField || textField == lastNameField || textField == patronymicField || textField == cityField
                        || textField == streetField){
                    if(textField.getText().length() > 50){
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("This field must contain less than 50 characters.");
                        textValidatorTooltip.setText(textValidator.getText());
                    }else{
                        textValidator.setText("");
                    }
                }

                if(textField == zipCodeField){
                    if(textField.getText().length() > 9){
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                        textValidator.setText("This field must contain less than 9 characters.");
                        textValidatorTooltip.setText(textValidator.getText());
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
                            }
                        }
                    } else {
                        // Показати помилку про некоректне числове значення
                        textValidator.setText("Enter valid salary value.");
                        textValidatorTooltip.setText(textValidator.getText());
                        textField.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                    }
                }

            }

        }

        LocalDate today = LocalDate.now(), eighteenYearsAgo = today.minusYears(18);

        for(DatePicker pick : requiredDates){
            if(pick == birthDatePicker){
                if (pick.valueProperty().getValue() == null || pick.valueProperty().getValue().isAfter(eighteenYearsAgo)) {
                    pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                } else {
                    pick.setStyle(null);
                }
            }else if (pick == firstWorkDayPicker){
                if (pick.valueProperty().getValue() == null) {
                    pick.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
                } else {
                    pick.setStyle(null);
                }
            }
        }

        if (roleComBox.getSelectionModel().isEmpty()) {
            roleComBox.setStyle("-fx-border-color: RED; -fx-border-width: 2px");
            roleValidator.setText("Choose role");
            roleValidatorTT.setText(roleValidator.getText());
        } else {
            roleComBox.setStyle(null);
            System.out.println(roleComBox.getValue());
        }

    }
}
