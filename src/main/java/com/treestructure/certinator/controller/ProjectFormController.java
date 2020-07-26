package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTextField;
import com.treestructure.certinator.repository.ProjectRepository;
import com.treestructure.certinator.service.ProjectPersistanceService;
import com.treestructure.certinator.service.ProjectTreeService;
import com.treestructure.certinator.ui.EnvironmentTable;
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

    @FXML
    JFXTextField projectNameField;
    @FXML
    EnvironmentTable environmentTable;


    private final ProjectRepository repository;

    private final ProjectPersistanceService projectPersistanceService;

    private final ViewState viewState;

    private final ProjectTreeService projectTreeService;

    public void chooseGitRoot() {

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.viewState.getSelectedProject().subscribe(project -> {
            this.projectNameField.setText(project.getName());
            environmentTable.storeData.setAll(projectPersistanceService.getEnvironmentViewModels(project));
        });
        bindEnvrionmentTable();
    }

    private void bindEnvrionmentTable() {
        environmentTable.deleted().subscribe(viewModel -> {
            projectPersistanceService.deleteEnvironment(viewModel);
            this.projectTreeService.updateEnvironments();
        });
        environmentTable.saved().subscribe(domainModel -> {
            var finalEnv = projectPersistanceService.storeEnvironmentFromViewModel(this.viewState.getSelectedProject().getValue(), domainModel);
            this.projectTreeService.updateEnvironments();
        });
    }

    public void saveProject() {
        this.viewState.getSelectedProject().getValue().setName(this.projectNameField.getText());
        projectTreeService.renameSelected(this.projectNameField.getText());
        repository.save(this.viewState.getSelectedProject().getValue());
    }


}
