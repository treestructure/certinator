package com.treestructure.certinator.model.ui;

import com.treestructure.certinator.model.Domain;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DomainTableComponentModel {
    private StringProperty url;
    private StringProperty name;
    private ObjectProperty<FontAwesomeIcon> icon;
    private Domain originalModel;
}
