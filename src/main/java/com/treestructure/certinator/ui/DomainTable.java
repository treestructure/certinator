package com.treestructure.certinator.ui;

import com.treestructure.certinator.model.ui.DomainTableComponentModel;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class DomainTable extends CrudTable<DomainTableComponentModel> {

    protected BehaviorSubject<DomainTableComponentModel> checkConnectivity = BehaviorSubject.create();

    public DomainTable() {
        super();

        var nameColumn = new TableColumn<DomainTableComponentModel, String>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().getName());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getName().set(t.getNewValue());
                });

        var domainColumn = new TableColumn<DomainTableComponentModel, String>("Target URL");
        domainColumn.setCellValueFactory(data -> data.getValue().getUrl());
        domainColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        domainColumn.setOnEditCommit(
                t -> {
                    t.getTableView().getItems().get(
                            t.getTablePosition().getRow()).getUrl().set(t.getNewValue());
                });

        var checkConnectivityColumn = new TableColumn<DomainTableComponentModel, FontAwesomeIcon>("Check Connection");
        checkConnectivityColumn.setCellFactory(ActionIconTableCell.forTableColumn("CHECK_CIRCLE", p -> {
            checkConnectivity.onNext(p);
            return p;
        }));

        storeTableView.getColumns().addAll(0, List.of(nameColumn, domainColumn, checkConnectivityColumn));
        storeTableView.setItems(storeData);
    }

    /**
     * adds an item to
     */
    public void addItem() {
        this.storeTableView.getItems().add(DomainTableComponentModel.builder()
                .name(new SimpleStringProperty("New TARGET Domain"))
                .url(new SimpleStringProperty("NOT SET"))
                .build());
    }

    public Observable<DomainTableComponentModel> checkConnectivityClicked() {
        return this.checkConnectivity;
    }


}
