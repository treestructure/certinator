package com.treestructure.certinator.ui;

import com.treestructure.certinator.model.ui.StoreTableComponentModel;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class KeyStoreTable extends CrudTable<StoreTableComponentModel> {

    @FXML
    TableView<StoreTableComponentModel> storeTableView;

    private BehaviorSubject<StoreTableComponentModel> selectGitPathItem =  BehaviorSubject.create();

    private BehaviorSubject<StoreTableComponentModel> selectServerPathItem =  BehaviorSubject.create();

    private BehaviorSubject<StoreTableComponentModel> syncStatusClickedItem =  BehaviorSubject.create();

    private BehaviorSubject<StoreTableComponentModel> containsOutdatedClickedItem =  BehaviorSubject.create();

    private BehaviorSubject<StoreTableComponentModel> openGitInKeyStoreEditorClickedItem =  BehaviorSubject.create();

    private BehaviorSubject<StoreTableComponentModel> openServerInKeyStoreEditorClickedItem =  BehaviorSubject.create();

    /**
     *
     */
    public KeyStoreTable() {
        super();

        var gitPathStringColumn = new TableColumn<StoreTableComponentModel, String>("Path to Git");
        gitPathStringColumn.setMaxWidth(200);
        gitPathStringColumn.setCellValueFactory(data -> data.getValue().getGitPath());

        var serverPathStringColumn = new TableColumn<StoreTableComponentModel, String>("Path on Server");
        serverPathStringColumn.setMaxWidth(200);
        serverPathStringColumn.setCellValueFactory(data -> data.getValue().getServerPath());

        var nameColumn = new TableColumn<StoreTableComponentModel, String>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().getName());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).setName(new SimpleStringProperty(t.getNewValue()));
                });

        var passwordColumn = new TableColumn<StoreTableComponentModel, String>("Password");
        passwordColumn.setCellValueFactory(data -> data.getValue().getPassword());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getPassword().set(t.getNewValue());
                });

        var gitPathColumn = new TableColumn<StoreTableComponentModel, FontAwesomeIcon>("Set Git Path");
        gitPathColumn.setCellFactory(ActionIconTableCell.forTableColumn("FOLDER_OPEN", p -> {
            selectGitPathItem.onNext(p);
            return p;
        }));


        var serverPathColumn = new TableColumn<StoreTableComponentModel, FontAwesomeIcon>("Set Server Path");
        serverPathColumn.setCellFactory(ActionIconTableCell.forTableColumn("FOLDER_OPEN", p -> {
            selectServerPathItem.onNext(p);
            return p;
        }));

        var syncStatusColumn = new TableColumn<StoreTableComponentModel, FontAwesomeIcon>("Check Equality");
        syncStatusColumn.setCellFactory(ActionIconTableCell.forTableColumn("CHECK", p -> {
            syncStatusClickedItem.onNext(p);
            return p;
        }));

        var containsOutdatedColumn = new TableColumn<StoreTableComponentModel, FontAwesomeIcon>("Check Outdated");
        containsOutdatedColumn.setCellFactory(ActionIconTableCell.forTableColumn("BOMB", p -> {
            containsOutdatedClickedItem.onNext(p);
            return p;
        }));

        var openGitInKeyStoreEditorColumn = new TableColumn<StoreTableComponentModel, FontAwesomeIcon>("");
        openGitInKeyStoreEditorColumn.setCellFactory(ActionIconTableCell.forTableColumn("EDIT", p -> {
            openGitInKeyStoreEditorClickedItem.onNext(p);
            return p;
        }));

        var openServerInKeyStoreEditorColumn = new TableColumn<StoreTableComponentModel, FontAwesomeIcon>("");
        openServerInKeyStoreEditorColumn.setCellFactory(ActionIconTableCell.forTableColumn("EDIT", p -> {
            openServerInKeyStoreEditorClickedItem.onNext(p);
            return p;
        }));
        storeTableView.getColumns().addAll(0, List.of(
                nameColumn, passwordColumn,
                gitPathStringColumn, gitPathColumn, openGitInKeyStoreEditorColumn,
                serverPathStringColumn, serverPathColumn, openServerInKeyStoreEditorColumn,
                syncStatusColumn, containsOutdatedColumn));
        storeTableView.setItems(storeData);
    }

    /**
     * adds an item to
     */
    public void addItem() {
        this.storeTableView.getItems().add(StoreTableComponentModel.builder()
                .name(new SimpleStringProperty("New Store"))
                .gitPath(new SimpleStringProperty("NOT SET"))
                .serverPath(new SimpleStringProperty("NOT SET"))
                .password(new SimpleStringProperty("NOT SET"))
                .build());
    }

    public Observable<StoreTableComponentModel> gitPathChanged() {
        return this.selectGitPathItem;
    }
    public Observable<StoreTableComponentModel> serverPathChanged() { return this.selectServerPathItem; }
    public Observable<StoreTableComponentModel> syncStatusClicked() {
        return this.syncStatusClickedItem;
    }
    public Observable<StoreTableComponentModel> containsOutdatedClicked() {
        return this.containsOutdatedClickedItem;
    }
    public Observable<StoreTableComponentModel> openGitInKeystoreEditorClicked() {
        return this.openGitInKeyStoreEditorClickedItem;
    }

    public Observable<StoreTableComponentModel> openServerInKeystoreEditorClicked() {
        return this.openServerInKeyStoreEditorClickedItem;
    }



}
