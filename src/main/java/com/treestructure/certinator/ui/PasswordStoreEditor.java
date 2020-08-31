package com.treestructure.certinator.ui;

import com.jfoenix.controls.JFXPasswordField;
import com.treestructure.certinator.model.ui.PasswordStoreTableModel;
import com.treestructure.certinator.service.PasswordStoreService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class PasswordStoreEditor extends VBox {

    @FXML
    JFXPasswordField keyStorePwd;
    @FXML
    VBox rootBox;
    @FXML
    Text keyStorePath;
    @FXML
    TableView<PasswordStoreTableModel> pwdStoreTable;
    @FXML
    TableColumn<PasswordStoreTableModel, String> aliasColumn;
    @FXML
    TableColumn<PasswordStoreTableModel, String> valueColumn;

    PasswordStoreService passwordStoreService;

    private ObservableList<PasswordStoreTableModel> passwordStoreData = FXCollections.observableArrayList();

    public PasswordStoreEditor(PasswordStoreService passwordStoreService, String path, String password) {
        super();
        this.passwordStoreService = passwordStoreService;
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/passwordstoreeditor.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        aliasColumn.setCellValueFactory(data -> data.getValue().getAlias());
        aliasColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        aliasColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).setAlias(new SimpleStringProperty(t.getNewValue()));
                });

        valueColumn.setCellValueFactory(data -> data.getValue().getValue());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).setValue(new SimpleStringProperty(t.getNewValue()));
                });


        var deleteColumn = new TableColumn<PasswordStoreTableModel, Button>(" ");
        deleteColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Remove", p -> {
            try {
                this.passwordStoreService.deleteEntry(keyStorePath.getText(), p.getAlias().get(), keyStorePwd.getText());
                this.pwdStoreTable.getItems().remove(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return p;
        }));
        var saveColumn = new TableColumn<PasswordStoreTableModel, Button>(" ");
        saveColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Save", p -> {
            try {
                this.passwordStoreService.updateEntry(
                        path,
                        p.getAlias().get(),
                        p.getValue().get(),
                        password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return p;
        }));

        pwdStoreTable.getColumns().addAll(deleteColumn, saveColumn);
        pwdStoreTable.setEditable(true);

        pwdStoreTable.prefHeightProperty().bind(rootBox.heightProperty());

        loadKeystore(path, password);
    }

    public void loadKeystore(String path, String password) {
        try {
            var rawData = passwordStoreService.getAllEntries(path, password);
            passwordStoreData.clear();
            rawData.forEach((k, v) -> {
                var model = new PasswordStoreTableModel();
                model.setAlias(new SimpleStringProperty(k));
                model.setValue(new SimpleStringProperty(v));
                passwordStoreData.add(model);
            });
            pwdStoreTable.setItems(passwordStoreData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEntryToKeyStore() {
        passwordStoreData.add(new PasswordStoreTableModel());
    }


}
