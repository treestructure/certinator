package com.treestructure.certinator.ui;

import com.treestructure.certinator.model.ui.PasswordStoreComponentModel;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class PasswordStoreTable extends CrudTable<PasswordStoreComponentModel>{


    private BehaviorSubject<PasswordStoreComponentModel> openPassWordStoreItem = BehaviorSubject.create();
    private BehaviorSubject<PasswordStoreComponentModel> selectGitPathItem =  BehaviorSubject.create();
    private BehaviorSubject<PasswordStoreComponentModel> selectServerPathItem =  BehaviorSubject.create();

    public PasswordStoreTable() {
        super();

        var gitPathStringColumn = new TableColumn<PasswordStoreComponentModel, String>("Path to Git");
        gitPathStringColumn.setCellValueFactory(data -> data.getValue().getGitPath());

        var serverPathStringColumn = new TableColumn<PasswordStoreComponentModel, String>("Path on Server");
        serverPathStringColumn.setCellValueFactory(data -> data.getValue().getServerPath());

        var nameColumn = new TableColumn<PasswordStoreComponentModel, String>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().getName());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).setName(new SimpleStringProperty(t.getNewValue()));
                });

        var passwordColumn = new TableColumn<PasswordStoreComponentModel, String>("Password");
        passwordColumn.setCellValueFactory(data -> data.getValue().getPassword());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getPassword().set(t.getNewValue());
                });

        var gitPathColumn = new TableColumn<PasswordStoreComponentModel, Button>(" ");
        gitPathColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Git Path", p -> {
            selectGitPathItem.onNext(p);
            return p;
        }));


        var serverPathColumn = new TableColumn<PasswordStoreComponentModel, Button>(" ");
        serverPathColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Server Path", p -> {
            selectServerPathItem.onNext(p);
            return p;
        }));

        var openColumn = new TableColumn<PasswordStoreComponentModel, Button>(" ");
        openColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Open", p -> {
            openPassWordStoreItem.onNext(p);
            return p;
        }));

        storeTableView.getColumns().addAll(0, List.of(nameColumn, passwordColumn, gitPathStringColumn,
                serverPathStringColumn, gitPathColumn, serverPathColumn, openColumn));

        storeTableView.setItems(storeData);
    }



    /**
     * adds an item to
     */
    public void addItem() {
        this.storeTableView.getItems().add(PasswordStoreComponentModel.builder()
                .name(new SimpleStringProperty("New Password Store"))
                .password(new SimpleStringProperty("No Password yet"))
                .gitPath(new SimpleStringProperty("NOT SET"))
                .serverPath(new SimpleStringProperty("NOT SET"))
                .build());
    }

    public Observable<PasswordStoreComponentModel> gitPathChanged() {
        return this.selectGitPathItem;
    }
    public Observable<PasswordStoreComponentModel> serverPathChanged() {
        return this.selectServerPathItem;
    }
    public Observable<PasswordStoreComponentModel> passwordStoreOpened() {
        return this.openPassWordStoreItem;
    }
}
