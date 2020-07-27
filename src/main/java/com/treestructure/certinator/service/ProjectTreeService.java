package com.treestructure.certinator.service;

import com.jfoenix.controls.JFXTreeView;
import com.treestructure.certinator.controller.ViewState;
import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.model.ProjectModelType;
import com.treestructure.certinator.model.ui.ProjectTreeModel;
import com.treestructure.certinator.repository.ProjectRepository;
import com.treestructure.certinator.ui.TreeViewBuilder;
import io.reactivex.subjects.BehaviorSubject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
 * responsible for rendering updates of the treeview in
 * the project window
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectTreeService {

    private final ProjectRepository projectRepository;

    private final ViewState viewState;

    private JFXTreeView projectTree;

    @Getter
    private BehaviorSubject<TreeItem<ProjectTreeModel>> selectedTreeItem = BehaviorSubject.create();


    public void addEnvironmentToProject(Environment env) {
        TreeViewBuilder.buildTreeItem(ProjectTreeModel
                .builder()
                .displayName(new SimpleStringProperty(env.getName()))
                .type(ProjectModelType.ENVIRONMENT)
                .originalModel(env)
                .build(), getSelectedTreeItem().getValue());
        projectTree.refresh();
    }

    public void updateEnvironments() {
        getSelectedTreeItem().getValue().getChildren().clear();
        var originalProject = (Project)getSelectedTreeItem().getValue().getValue().getOriginalModel();
        originalProject.getEnvironments().forEach(e -> addEnvironmentToProject(e));

    }

    public void addProjectToTree() {
        var dummyProject = new Project();
        dummyProject.setName("New Project");
        dummyProject.setEnvironments(new ArrayList<>());
        var dummyToAdd = new TreeItem<ProjectTreeModel>();
        dummyToAdd.setValue(ProjectTreeModel.builder()
                .displayName(new SimpleStringProperty("New Project"))
                .type(ProjectModelType.PROJECT)
                .originalModel(dummyProject)
                .build());
        this.projectTree.getRoot().getChildren().add(dummyToAdd);

        viewState.getSelectedProject().onNext(dummyProject);

    }

    public void removeProjectFromTree() {
        projectTree.getRoot().getChildren().remove(getSelectedTreeItem());
    }


    public void renameSelected(String newName) {
        selectedTreeItem.getValue().getValue().getDisplayName().set(newName);
        projectTree.refresh();
    }


    public void setJfxTree(JFXTreeView treeView) {
        this.projectTree = treeView;
    }

    public void reload() {
        var root = new TreeItem<ProjectTreeModel>();
        projectRepository.findAll().forEach(proj -> {
            // add project
            var projNode = TreeViewBuilder.buildTreeItem(ProjectTreeModel
                    .builder()
                    .displayName(new SimpleStringProperty(proj.getName()))
                    .type(ProjectModelType.PROJECT)
                    .originalModel(proj)
                    .build(), root);

            // add environments for each projects
            proj.getEnvironments().forEach(env -> {
                TreeViewBuilder.buildTreeItem(ProjectTreeModel
                        .builder()
                        .displayName(new SimpleStringProperty(env.getName()))
                        .type(ProjectModelType.ENVIRONMENT)
                        .originalModel(env)
                        .build(), projNode);
            });
        });
        projectTree.setRoot(root);
        projectTree.setShowRoot(false);
    }





}
