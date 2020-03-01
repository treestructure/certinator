package com.treestructure.certinator.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProjectTreeModel extends RecursiveTreeObject<ProjectTreeModel> {
    StringProperty displayName;
    ProjectModelType type;
    Object originalModel;


    public String toString() {
        return displayName.get();
    }


}
