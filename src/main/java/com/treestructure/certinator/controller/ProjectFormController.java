package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTextField;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.repository.ProjectRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@Component
public class ProjectFormController implements Initializable {

    private Project selectedProject;
    
    @FXML
    JFXTextField projectNameField;

    @Autowired
    ProjectRepository repository;

    @Autowired
    ViewState viewState;

    ProjectFormController(@Autowired ViewState viewState, @Autowired ProjectRepository projectRepository) {
        this.repository = projectRepository;
        this.viewState = viewState;
        this.viewState.getSelectedProject().subscribe(project -> {
            this.selectedProject = project;
            this.projectNameField.setText(project.getName());
        });
    }



    public void chooseGitRoot(ActionEvent actionEvent) {

    }

    public void saveProject(ActionEvent actionEvent) {
        selectedProject.setName(this.projectNameField.getText());
        repository.save(selectedProject);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewState.getSelectedProject().subscribe(project -> {
            this.selectedProject = project;
            this.projectNameField.setText(project.getName());
        });
    }

}
