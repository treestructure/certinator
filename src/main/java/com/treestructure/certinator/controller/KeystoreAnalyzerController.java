package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.treestructure.certinator.model.ui.KeyStoreTableModel;
import com.treestructure.certinator.service.KeyStoreAnalyzerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class KeystoreWindowController implements Initializable {

    @FXML
    JFXPasswordField keyStorePwd;
    @FXML
    VBox rootBox;
    @FXML
    Text keyStorePath;
    @FXML
    TableView keyStoreTable;
    @FXML
    TableColumn<KeyStoreTableModel, String> aliasColumn;
    @FXML
    TableColumn<KeyStoreTableModel, String> typeColumn;
    @FXML
    TableColumn<KeyStoreTableModel, String> authColumn;
    @FXML
    TableColumn<KeyStoreTableModel, String> expColumn;


    @Autowired
    KeyStoreAnalyzerService service;

    private ObservableList<KeyStoreTableModel> keyStoreData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        aliasColumn.setCellValueFactory(data -> data.getValue().getAlias());
        typeColumn.setCellValueFactory(data -> data.getValue().getType());
        authColumn.setCellValueFactory(data -> data.getValue().getAuthority());
        expColumn.setCellValueFactory(data -> data.getValue().getExpiary());

        keyStoreTable.prefHeightProperty().bind(rootBox.heightProperty());
    }


    public void chooseKeystore() {
        FileChooser outputChooser = new FileChooser();
        outputChooser.setTitle("Choose Destination Path for the truststore");

        Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
            keyStorePath.setText(selectedPath.toString());
        });
    }

    public void loadKeystore() {
        var rawData = service.getAllCerts(keyStorePath.getText(), keyStorePwd.getText());

        keyStoreData.clear();
        rawData.forEach((k, v) -> {
            var model = new KeyStoreTableModel();
            try {
                var issuer = new LdapName(v.getIssuerDN().toString());
                var issuerName = issuer.getRdns().stream().filter(r -> r.getType().equals("CN")).findFirst().orElse(null);
                model.setAlias(new SimpleStringProperty(k));
                model.setAuthority(new SimpleStringProperty(issuerName.getValue().toString()));
                model.setType(new SimpleStringProperty( v.getType()));
                model.setExpiary(new SimpleStringProperty(v.getNotAfter().toString()));
                keyStoreData.add(model);
            } catch (InvalidNameException e) {
                e.printStackTrace();
            }


        });
        keyStoreTable.setItems(keyStoreData);
    }
}
