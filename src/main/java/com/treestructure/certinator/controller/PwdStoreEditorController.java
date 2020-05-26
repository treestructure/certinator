package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.treestructure.certinator.model.ui.PasswordStoreTableModel;
import com.treestructure.certinator.service.PasswordStoreService;
import com.treestructure.certinator.ui.ActionButtonTableCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class PwdStoreEditorController implements Initializable {

    @FXML
    JFXPasswordField keyStorePwd;
    @FXML
    VBox rootBox;
    @FXML
    Text keyStorePath;
    @FXML
    TableView<PasswordStoreTableModel> keyStoreTable;
    @FXML
    TableColumn<PasswordStoreTableModel, String> aliasColumn;
    @FXML
    TableColumn<PasswordStoreTableModel, String> valueColumn;

    PasswordStoreService passwordStoreService;

    private ObservableList<PasswordStoreTableModel> passwordStoreData = FXCollections.observableArrayList();

    public void chooseKeystore() {
        FileChooser outputChooser = new FileChooser();
        outputChooser.setTitle("Choose Destination Path for the truststore");

        Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
            keyStorePath.setText(selectedPath.toString());
        });
    }

    public PwdStoreEditorController(@Autowired PasswordStoreService passwordStoreService) {
        this.passwordStoreService = passwordStoreService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
                this.keyStoreTable.getItems().remove(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return p;
        }));
        var saveColumn = new TableColumn<PasswordStoreTableModel, Button>(" ");
        saveColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Save", p -> {
            try {
                this.passwordStoreService.updateEntry(
                        keyStorePath.getText(),
                        p.getAlias().get(),
                        p.getValue().get(),
                        keyStorePwd.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return p;
        }));

        keyStoreTable.getColumns().addAll(deleteColumn, saveColumn);
        keyStoreTable.setEditable(true);

        keyStoreTable.prefHeightProperty().bind(rootBox.heightProperty());
    }

    public void loadKeystore() {
        try {
            var rawData = passwordStoreService.getAllEntries(keyStorePath.getText(), keyStorePwd.getText());
            passwordStoreData.clear();
            rawData.forEach((k, v) -> {
                var model = new PasswordStoreTableModel();
                model.setAlias(new SimpleStringProperty(k));
                model.setValue(new SimpleStringProperty(v));
                passwordStoreData.add(model);
            });
            keyStoreTable.setItems(passwordStoreData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;


    }

    public void addEntryToKeyStore() {
        passwordStoreData.add(new PasswordStoreTableModel());
    }
}
