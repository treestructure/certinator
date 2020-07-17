package com.treestructure.certinator.ui;

import com.treestructure.certinator.model.ui.EnvironmentTableComponentModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class EnvironmentTable extends CrudTable<EnvironmentTableComponentModel> {

    public EnvironmentTable() {
        super();

        var nameColumn = new TableColumn<EnvironmentTableComponentModel, String>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().getName());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getName().set(t.getNewValue());
                });

        var gitPathColumn = new TableColumn<EnvironmentTableComponentModel, String>("Git Root Path");
        gitPathColumn.setCellValueFactory(data -> data.getValue().getGitRootPath());
        gitPathColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gitPathColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getGitRootPath().set(t.getNewValue());
                });

        var serverPathColumn = new TableColumn<EnvironmentTableComponentModel, String>("Server Path");
        serverPathColumn.setCellValueFactory(data -> data.getValue().getServerRootPath());
        serverPathColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serverPathColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getServerRootPath().set(t.getNewValue());
                });

        storeTableView.getColumns().addAll(0, List.of(nameColumn, gitPathColumn, serverPathColumn));
        storeTableView.setItems(storeData);


    }

    /**
     * adds an empty model to the table
     */
    public void addItem() {
        this.storeTableView.getItems().add(EnvironmentTableComponentModel.builder()
                .name(new SimpleStringProperty("New Environment"))
                .gitRootPath(new SimpleStringProperty("NOT SET"))
                .serverRootPath(new SimpleStringProperty("NOT SET"))
                .build());
    }
}
