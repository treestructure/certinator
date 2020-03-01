package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTreeView;
import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.model.ProjectModelType;
import com.treestructure.certinator.model.ProjectTreeModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ProjectWindowController implements Initializable {

    @Autowired
    private ApplicationContext context;

    @Value("classpath:/fxml/projectform.fxml")
    private Resource projectFormResource;

    @Value("classpath:/fxml/environmentform.fxml")
    private Resource environmentResource;

    @Autowired
    ViewState viewState;

    @FXML
    JFXTreeView projectTree;

    @FXML
    AnchorPane projectContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var root = new TreeItem<ProjectTreeModel>();


        var ddpm = TreeViewBuilder.buildTreeItem(
                ProjectTreeModel
                        .builder()
                        .type(ProjectModelType.PROJECT)
                        .displayName(new SimpleStringProperty("OPrA 3"))
                        .originalModel(new Project()).build(), root);

        TreeViewBuilder.buildTreeItem(
                ProjectTreeModel
                        .builder()
                        .type(ProjectModelType.ENVIRONMENT)
                        .displayName(new SimpleStringProperty("INT 1"))
                        .originalModel(new Environment()).build(), ddpm);

        TreeViewBuilder.buildTreeItem(
                ProjectTreeModel.builder()
                        .type(ProjectModelType.ENVIRONMENT)
                        .displayName(new SimpleStringProperty("INT 2"))
                        .originalModel(new Environment()).build(), ddpm);


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
                loadContentForEnvironment((Environment)newItem.getOriginalModel());
                break;
            case PROJECT:
                loadContentForProject((Project)newItem.getOriginalModel());
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

            projectContent.getChildren().clear();
            projectContent.getChildren().add(root);
            viewState.getSelectedProject().onNext(project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param environment
     */
    private void loadContentForEnvironment(Environment environment) {
        try {
            var fxmlLoader = new FXMLLoader(environmentResource.getURL());
            fxmlLoader.setControllerFactory(clazz -> context.getBean(clazz));
            AnchorPane root = fxmlLoader.load();

            projectContent.getChildren().clear();
            projectContent.getChildren().add(root);
            viewState.getSelectedEnvironment().onNext(environment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void chooseGitRoot(ActionEvent actionEvent) {
    }
}
