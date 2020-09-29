package com.treestructure.certinator.ui;

import com.treestructure.certinator.model.ui.PasswordStoreComponentModel;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class PasswordStoreTable extends CrudTable<PasswordStoreComponentModel>{


    private BehaviorSubject<PasswordStoreComponentModel> openGitItem = BehaviorSubject.create();
    private BehaviorSubject<PasswordStoreComponentModel> openServerItem = BehaviorSubject.create();
    private BehaviorSubject<PasswordStoreComponentModel> selectGitPathItem =  BehaviorSubject.create();
    private BehaviorSubject<PasswordStoreComponentModel> selectServerPathItem =  BehaviorSubject.create();

    public PasswordStoreTable() {
        super();

        var gitPathStringColumn = new TableColumn<PasswordStoreComponentModel, String>("Path to Git");
        gitPathStringColumn.setCellValueFactory(data -> data.getValue().getGitPath());
        gitPathStringColumn.setPrefWidth(200);
        gitPathStringColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        var serverPathStringColumn = new TableColumn<PasswordStoreComponentModel, String>("Path on Server");
        serverPathStringColumn.setCellValueFactory(data -> data.getValue().getServerPath());
        serverPathStringColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serverPathStringColumn.setPrefWidth(200);

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
        passwordColumn.setCellFactory(PasswordFieldTableCell.forTableColumn());
        passwordColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getPassword().set(t.getNewValue());
                });

        var gitPathColumn = new TableColumn<PasswordStoreComponentModel, FontAwesomeIcon>("");
        gitPathColumn.setCellFactory(ActionIconTableCell.forTableColumn("FOLDER_OPEN", p -> {
            selectGitPathItem.onNext(p);
            return p;
        }));


        var serverPathColumn = new TableColumn<PasswordStoreComponentModel, FontAwesomeIcon>("");
        serverPathColumn.setCellFactory(ActionIconTableCell.forTableColumn("FOLDER_OPEN", p -> {
            selectServerPathItem.onNext(p);
            return p;
        }));


        var openGitColumn = new TableColumn<PasswordStoreComponentModel, FontAwesomeIcon>("");
        openGitColumn.setCellFactory(ActionIconTableCell.forTableColumn("EDIT", p -> {
            openGitItem.onNext(p);
            return p;
        }));

        var openServerColumn = new TableColumn<PasswordStoreComponentModel, FontAwesomeIcon>("");
        openServerColumn.setCellFactory(ActionIconTableCell.forTableColumn("EDIT", p -> {
            openServerItem.onNext(p);
            return p;
        }));

        storeTableView.getColumns().addAll(0, List.of(
                nameColumn,
                passwordColumn,
                gitPathStringColumn,
                gitPathColumn,
                openGitColumn,
                serverPathStringColumn,
                serverPathColumn,
                openServerColumn));
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
    public Observable<PasswordStoreComponentModel> gitStoreOpened() {
        return this.openGitItem;
    }
    public Observable<PasswordStoreComponentModel> serverStoreOpened() {
        return this.openServerItem;
    }
}
