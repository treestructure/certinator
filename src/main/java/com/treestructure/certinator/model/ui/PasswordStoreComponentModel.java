package com.treestructure.certinator.model.ui;

import com.treestructure.certinator.model.PasswordStore;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PasswordStoreComponentModel {
    private StringProperty serverPath;
    private StringProperty gitPath;
    private StringProperty name;
    private StringProperty password;
    private PasswordStore originalModel;
}
