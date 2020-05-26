package com.treestructure.certinator.model.ui;

import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class KeyStoreTableModel {

    private StringProperty alias;
    private StringProperty type;
    private StringProperty password;
    private StringProperty authority;
    private StringProperty expiary;
}
