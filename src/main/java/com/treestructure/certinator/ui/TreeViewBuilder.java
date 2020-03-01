package com.treestructure.certinator.controller;


import com.jfoenix.controls.JFXTreeTableColumn;
import com.treestructure.certinator.model.ui.ProjectTreeModel;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import org.springframework.util.StringUtils;

import java.util.function.Function;

public class TreeViewBuilder {

    public static TreeItem<ProjectTreeModel> buildTreeItem(ProjectTreeModel model, TreeItem<ProjectTreeModel> parent) {
        return buildTreeItem(model, parent, null);
    }

    public static TreeItem<ProjectTreeModel> buildTreeItem(ProjectTreeModel model, TreeItem<ProjectTreeModel> parent, String styleClass) {
        var item = new TreeItem<>(model);
        if (!StringUtils.isEmpty(styleClass)) {
            item.getGraphic().getStyleClass().add(styleClass);
        }
        parent.getChildren().add(item);
        return item;
    }

    /**
     *
     * @param input
     * @param name
     * @param func
     * @param <T>
     * @return
     */
    public static <T> JFXTreeTableColumn buildColumn(T input, String name, Function<T, ObservableStringValue> func) {
        JFXTreeTableColumn<T, String> newColumn = new JFXTreeTableColumn<>(name);
        newColumn.setPrefWidth(150);
        newColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<T, String> param) -> {
            if (newColumn.validateValue(param)) {
                return func.apply(input);
            } else {
                return newColumn.getComputedValue(param);
            }
        });



        /*
        newColumn.setCellFactory((TreeTableColumn<T, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        newColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<T, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().age.set(t.getNewValue()));
         */

        return newColumn;
    }
}
