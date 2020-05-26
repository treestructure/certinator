package com.treestructure.certinator.model.ui;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PasswordStoreTableModel {

    private StringProperty alias;
    private StringProperty value;
}
