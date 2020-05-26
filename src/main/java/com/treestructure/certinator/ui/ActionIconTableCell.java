package com.treestructure.certinator.ui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.Function;

public class ActionIconTableCell<S> extends TableCell<S, FontAwesomeIcon> {

    private final FontAwesomeIcon actionIcon;

    public ActionIconTableCell(String iconName, Function< S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        this.actionIcon = new FontAwesomeIcon();
        this.actionIcon.setGlyphName(iconName);
        this.actionIcon.setSize("25");
        this.actionIcon.getStyleClass().add("tableIcon");
        this.actionIcon.setOnMouseClicked(e -> function.apply(getCurrentItem()));
    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    public static <S> Callback<TableColumn<S, FontAwesomeIcon>, TableCell<S, FontAwesomeIcon>> forTableColumn(String iconName, Function< S, S> function) {
        return param -> new ActionIconTableCell<>(iconName, function);
    }

    @Override
    public void updateItem(FontAwesomeIcon item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionIcon);
        }
    }
}
