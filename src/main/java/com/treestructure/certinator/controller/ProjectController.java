package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTreeView;
import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.model.ProjectModelType;
import com.treestructure.certinator.model.ui.ProjectTreeModel;
import com.treestructure.certinator.repository.ProjectRepository;
import com.treestructure.certinator.ui.TreeViewBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
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
    ProjectRepository projectRepository;

    @FXML
    JFXTreeView projectTree;

    @FXML
    BorderPane rootPane;

    @FXML
    VBox formContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

        rootPane.heightProperty().addListener((o, old, newval) -> {
            projectTree.setPrefHeight(newval.doubleValue() - 80.0);
        });

        projectTree.setRoot(root);
        projectTree
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldItem, newItem) -> {
                    var model = (TreeItem<ProjectTreeModel>) newItem;
                    updateProjectContent(model.getValue());
                });
        projectTree.setShowRoot(false);

    }

    private void updateProjectContent(ProjectTreeModel newItem) {

        switch (newItem.getType()) {
            case ENVIRONMENT:
                loadContentForEnvironment((Environment) newItem.getOriginalModel());
                break;
            case PROJECT:
                loadContentForProject((Project) newItem.getOriginalModel());
                break;
            default:

        }

    }


    /**
     *
     * @param project
     */
    private void loadContentForProject(Project project) {
        try {
            var fxmlLoader = new FXMLLoader(projectFormResource.getURL());
            fxmlLoader.setControllerFactory(clazz -> context.getBean(clazz));
            AnchorPane root = fxmlLoader.load();
            formContainer.getChildren().clear();
            formContainer.getChildren().add(root);
            viewState.getSelectedProject().onNext(project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param environment
     */
    private void loadContentForEnvironment(Environment environment) {
        try {
            var fxmlLoader = new FXMLLoader(environmentResource.getURL());
            fxmlLoader.setControllerFactory(clazz -> context.getBean(clazz));
            StackPane root = fxmlLoader.load();
            formContainer.getChildren().clear();
            formContainer.getChildren().add(root);
            viewState.getSelectedEnvironment().onNext(environment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles click on add project button
     */
    public void addProject() {

        var dummyProject = new Project();
        dummyProject.setName("New Project");
        var dummyToAdd = new TreeItem<ProjectTreeModel>();
        dummyToAdd.setValue(ProjectTreeModel.builder()
                .displayName(new SimpleStringProperty("New Project"))
                .type(ProjectModelType.PROJECT)
                .originalModel(dummyProject)
                .build());
        this.projectTree.getRoot().getChildren().add(dummyToAdd);
    }
}
