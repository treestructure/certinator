package com.treestructure.certinator.ui;

import com.treestructure.certinator.model.ui.KeyStoreTableModel;
import com.treestructure.certinator.service.KeyStoreAnalyzerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import java.io.IOException;
import java.util.Optional;

public class KeyStoreEditor  extends VBox {


    @FXML
    VBox rootBox;
    @FXML
    TableView keyStoreTable;

    KeyStoreAnalyzerService keyStoreAnalyzerService;

    @FXML
    TableColumn<KeyStoreTableModel, String> aliasColumn;
    @FXML
    TableColumn<KeyStoreTableModel, String> typeColumn;
    @FXML
    TableColumn<KeyStoreTableModel, String> authColumn;
    @FXML
    TableColumn<KeyStoreTableModel, String> expColumn;

    private ObservableList<KeyStoreTableModel> keyStoreData = FXCollections.observableArrayList();

    public KeyStoreEditor(KeyStoreAnalyzerService keyStoreService, String path, String password) {
        super();
        this.keyStoreAnalyzerService = keyStoreService;
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/keystoreeditor.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        aliasColumn.setCellValueFactory(data -> data.getValue().getAlias());
        typeColumn.setCellValueFactory(data -> data.getValue().getType());
        authColumn.setCellValueFactory(data -> data.getValue().getAuthority());
        expColumn.setCellValueFactory(data -> data.getValue().getExpiary());
        keyStoreTable.prefHeightProperty().bind(rootBox.heightProperty());

        loadKeystore(path, password);
    }

    public void loadKeystore(String path, String password) {
        Optional.ofNullable(keyStoreAnalyzerService.getAllCerts(path, password)).ifPresent(rawData -> {
            keyStoreData.clear();
            rawData.forEach((k, v) -> {
                var model = new KeyStoreTableModel();
                try {
                    var issuer = new LdapName(v.getIssuerDN().toString());
                    issuer.getRdns().stream().filter(r -> r.getType().equals("CN")).findFirst().ifPresent(issuerName -> {
                        model.setAuthority(new SimpleStringProperty(issuerName.getValue().toString()));
                    });
                    model.setAlias(new SimpleStringProperty(k));
                    model.setType(new SimpleStringProperty( v.getType()));
                    model.setExpiary(new SimpleStringProperty(v.getNotAfter().toString()));
                    keyStoreData.add(model);
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                }
            });
            keyStoreTable.setItems(keyStoreData);
        });


    }

}
