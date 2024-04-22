package org.example.db_zlagoda.cashier_page.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.db_zlagoda.utils.SaleFilter;
import org.example.db_zlagoda.utils.tableview_tools.CategoryItem;

public class ProductFiltersViewController {


    public ChoiceBox categoryChoiceBox;
    public ChoiceBox saleChoiceBox;
    public VBox viewContainer;

    public void initialize() {

//        ((Stage)viewContainer.getScene().getWindow()).initModality(Modality.APPLICATION_MODAL);

        ObservableList<String> saleFilter = FXCollections.observableArrayList("Усі товари", "Повна ціна", "Зі знижкою");
        ObservableList<CategoryItem> categoryFilter = FXCollections.observableArrayList(ControllerAccess.cashierMenuViewController.data.getCategories());
        categoryFilter.addFirst(null); //all categories

        categoryChoiceBox.setItems(categoryFilter);
        categoryChoiceBox.setValue(categoryFilter.getFirst());

        saleChoiceBox.setItems(saleFilter);
        saleChoiceBox.setValue("Усі товари");

    }

    @FXML
    private void updateFilters(ActionEvent actionEvent) {
        closeWindow();

        Object category = categoryChoiceBox.getValue();
        String sale = (String)saleChoiceBox.getValue();
        SaleFilter saleFilter;

        if(sale.equals("Зі знижкою")) saleFilter = SaleFilter.Sale;
        else if (sale.equals("Повна ціна")) saleFilter = SaleFilter.NonSale;
        else saleFilter = SaleFilter.All;

        ControllerAccess.cashierMenuViewController.setFilters(
                category == null ? null : (CategoryItem)category,
                saleFilter);
    }

    public void closeWindow() {
        ((Stage)viewContainer.getScene().getWindow()).close();
    }

    @FXML
    public void deleteFilters(ActionEvent actionEvent) {
        closeWindow();
        ControllerAccess.cashierMenuViewController.setFilters(
                null,
                SaleFilter.All);
    }

}
