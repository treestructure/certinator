package com.treestructure.certinator.ui;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class CrudTable<T> extends VBox {

    @FXML
    TableView<T> storeTableView;
    @FXML
    Text storeLabel;
    @FXML
    JFXButton addButton;

    public ObservableList<T> storeData = FXCollections.observableArrayList();

    protected BehaviorSubject<T> itemDeleted =  BehaviorSubject.create();
    protected BehaviorSubject<T> itemSaved = BehaviorSubject.create();

    public Observable<T> deleted() {
        return this.itemDeleted;
    }
    public Observable<T> saved() {
        return this.itemSaved;
    }


    public CrudTable() {
        super();

        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/crudtable.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        var deleteColumn = new TableColumn<T, FontAwesomeIcon>("Delete");
        deleteColumn.setCellFactory(ActionIconTableCell.forTableColumn("TRASH", p -> {
            this.storeTableView.getItems().remove(p);
            this.itemDeleted.onNext(p);
            return p;
        }));
        var saveColumn = new TableColumn<T, FontAwesomeIcon>("Save");
        saveColumn.setCellFactory(ActionIconTableCell.forTableColumn("SAVE", p -> {
            itemSaved.onNext(p);
            return p;
        }));

        storeTableView.getColumns().addAll(List.of(deleteColumn, saveColumn));
        storeTableView.setItems(storeData);
    }

    public void addItem() {}

    public final void setTableLabel(String text) {
        storeLabel.textProperty().set(text);
    }
    public final String getTableLabel() {
        return storeLabel.textProperty().get();
    }
    public final void setButtonLabel(String text) {
        addButton.textProperty().set(text);
    }
    public final String getButtonLabel() {
        return addButton.textProperty().get();
    }

    public final void setTableHeight(Double height) {
        storeTableView.minHeightProperty().set(height);
    }
    public final Double getTableHeight() {
        return storeTableView.minHeightProperty().get();
    }
}
