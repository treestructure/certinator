package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTreeView;
import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.model.ui.ProjectTreeModel;
import com.treestructure.certinator.repository.ProjectRepository;
import com.treestructure.certinator.service.ProjectTreeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ProjectController implements Initializable {

    @Autowired
    private ApplicationContext context;

    @Value("classpath:/fxml/projectform.fxml")
    private Resource projectFormResource;

    @Value("classpath:/fxml/environmentform.fxml")
    private Resource environmentResource;

    @Autowired
    ViewState viewState;

    @Autowired
    ProjectTreeService projectTreeService;

    @Autowired
    ProjectRepository projectRepository;
    @FXML
    JFXTreeView projectTree;
    @FXML
    BorderPane rootPane;
    @FXML
    VBox formContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        projectTreeService.setJfxTree(projectTree);
        projectTreeService.reload();

        rootPane.heightProperty().addListener((o, old, newval) -> {
            projectTree.setPrefHeight(newval.doubleValue() - 80.0);
        });

        projectTree
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldItem, newItem) -> {
                updateProjectContent( (TreeItem<ProjectTreeModel>) newItem);
            });
    }

    private void updateProjectContent(TreeItem<ProjectTreeModel> newItem) {
        projectTreeService.getSelectedTreeItem().onNext(newItem);
        switch (newItem.getValue().getType()) {
            case ENVIRONMENT:
                loadContentForEnvironment(newItem.getValue());
                break;
            case PROJECT:
                loadContentForProject(newItem.getValue());
                break;
            default:

        }
        projectTree.refresh();
    }


    /**
     *
     * @param treeModel
     */
    private void loadContentForProject(ProjectTreeModel treeModel) {
        try {
            var fxmlLoader = new FXMLLoader(projectFormResource.getURL());
            fxmlLoader.setControllerFactory(clazz -> context.getBean(clazz));
            StackPane root = fxmlLoader.load();
            formContainer.getChildren().clear();
            formContainer.getChildren().add(root);
            viewState.getSelectedProject().onNext((Project)treeModel.getOriginalModel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param treeModel
     */
    private void loadContentForEnvironment(ProjectTreeModel treeModel) {
        try {
            var fxmlLoader = new FXMLLoader(environmentResource.getURL());
            fxmlLoader.setControllerFactory(clazz -> context.getBean(clazz));
            StackPane root = fxmlLoader.load();
            formContainer.getChildren().clear();
            formContainer.getChildren().add(root);
            var env = (Environment) treeModel.getOriginalModel();
            viewState.getSelectedEnvironment().onNext(env);
            viewState.getSelectedProject().onNext(env.getProject());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles click on add project button
     */
    public void addProject() {
        this.projectTreeService.addProjectToTree();
    }
}
