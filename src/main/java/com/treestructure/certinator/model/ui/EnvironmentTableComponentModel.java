package com.treestructure.certinator.model.ui;

import com.treestructure.certinator.model.Environment;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class EnvironmentTableComponentModel {

    private StringProperty name;
    private StringProperty gitRootPath;
    private StringProperty serverRootPath;
    private Environment originalModel;

}
