package com.treestructure.certinator.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class PasswordFieldTableCell<S, T> extends TableCell<S, T> {

    private PasswordField passwordField;
    private ObjectProperty<StringConverter<T>> converter;

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> var0) {
        return (var1) -> {
            return new TextFieldTableCell(var0);
        };
    }

    public PasswordFieldTableCell() {
        this((StringConverter)null);
    }

    public PasswordFieldTableCell(StringConverter<T> var1) {
        this.converter = new SimpleObjectProperty(this, "converter");
        this.getStyleClass().add("text-field-table-cell");
        this.setConverter(var1);
    }

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return this.converter;
    }

    public final void setConverter(StringConverter<T> var1) {
        this.converterProperty().set(var1);
    }

    public final StringConverter<T> getConverter() {
        return (StringConverter)this.converterProperty().get();
    }

    public void startEdit() {
        if (this.isEditable() && this.getTableView().isEditable() && this.getTableColumn().isEditable()) {
            super.startEdit();
            if (this.isEditing()) {
                if (this.passwordField == null) {
                    this.passwordField = CellUtils.createPasswordField(this, this.getConverter());
                }

                CellUtils.startEdit(this, this.getConverter(), (HBox)null, (Node)null, this.passwordField);
            }

        }
    }

    public void cancelEdit() {
        super.cancelEdit();
        CellUtils.cancelEdit(this, this.getConverter(), (Node)null);
    }

    public void updateItem(T var1, boolean var2) {
        super.updateItem(var1, var2);
        CellUtils.updateItem(this, this.getConverter(), (HBox)null, (Node)null, this.passwordField);
    }

}
