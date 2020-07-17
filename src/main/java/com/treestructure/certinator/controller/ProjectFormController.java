package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTextField;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.repository.ProjectRepository;
import com.treestructure.certinator.service.ProjectPersistanceService;
import com.treestructure.certinator.ui.EnvironmentTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectFormController implements Initializable {

    private Project selectedProject;
    
    @FXML
    JFXTextField projectNameField;
    @FXML
    EnvironmentTable environmentTable;


    private final ProjectRepository repository;

    private final ProjectPersistanceService projectPersistanceService;

    private final ViewState viewState;

    public void chooseGitRoot(ActionEvent actionEvent) {

    }

    public void saveProject(ActionEvent actionEvent) {
        selectedProject.setName(this.projectNameField.getText());
        repository.save(selectedProject);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.viewState.getSelectedProject().subscribe(project -> {
            this.selectedProject = project;
            this.projectNameField.setText(project.getName());
        });


        viewState.getSelectedProject().subscribe(project -> {
            this.selectedProject = project;
            this.projectNameField.setText(project.getName());

            environmentTable.storeData.setAll(projectPersistanceService.getEnvironmentViewModels(project));

            environmentTable.deleted().subscribe(viewModel -> projectPersistanceService.deleteEnvironment(viewModel));
            environmentTable.saved().subscribe(domainModel ->
                    projectPersistanceService.storeEnvironmentFromViewModel(project, domainModel));
        });
    }

}
