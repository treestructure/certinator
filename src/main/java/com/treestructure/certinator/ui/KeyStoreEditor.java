package com.treestructure.certinator.ui;

import com.treestructure.certinator.model.ui.KeyStoreTableModel;
import com.treestructure.certinator.service.KeyStoreAnalyzerService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
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

    private String currentPath;
    private String currentPassword;

    private ObservableList<KeyStoreTableModel> keyStoreData = FXCollections.observableArrayList();

    public KeyStoreEditor(KeyStoreAnalyzerService keyStoreService, String path, String password) {
        super();

        currentPassword = password;
        currentPath = path;

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

        var deleteColumn = new TableColumn<KeyStoreTableModel, FontAwesomeIcon>("Delete");
        deleteColumn.setCellFactory(ActionIconTableCell.forTableColumn("TRASH", p -> {
            try {
                this.keyStoreAnalyzerService.deleteEntry(path, p.getAlias().get(), password);
                this.keyStoreTable.getItems().remove(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return p;
        }));
        keyStoreTable.getColumns().addAll(deleteColumn);

        keyStoreTable.prefHeightProperty().bind(rootBox.heightProperty());

        loadKeystore(path, password);

        rootBox.setOnDragOver(dragEvent -> dragEvent.acceptTransferModes(TransferMode.MOVE));
        rootBox.setOnDragDropped(dragEvent -> handleDragged(dragEvent));
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

    /**
     *
     * @param dragged
     */
    public void handleDragged(DragEvent dragged) {
        var board = dragged.getDragboard();
        if (board.hasFiles()) {
            board.getFiles().forEach(file -> {
                var path = file.getAbsoluteFile().getAbsolutePath();
                try {
                    keyStoreAnalyzerService.addEntry(currentPath, currentPassword, path);
                    loadKeystore(currentPath, currentPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
