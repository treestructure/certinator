package com.treestructure.certinator.service;

import com.treestructure.certinator.controller.ViewState;
import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.model.ui.EnvironmentTableComponentModel;
import com.treestructure.certinator.repository.EnvironmentRepository;
import com.treestructure.certinator.repository.ProjectRepository;
import javafx.beans.property.SimpleStringProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectPersistanceService {

    private final ViewState viewState;
    private final ProjectRepository projectRepository;

    private final EnvironmentRepository environmentRepository;

    public Environment storeEnvironmentFromViewModel(EnvironmentTableComponentModel viewModel) {

        var project = viewState.getSelectedProject().getValue();

        return Optional.ofNullable(viewModel.getOriginalModel())
                .map(originalModel -> {
                    originalModel.setName(viewModel.getName().get());
                    originalModel.setGitRootPath(viewModel.getGitRootPath().get());
                    originalModel.setServerPath(viewModel.getServerRootPath().get());
                    return environmentRepository.save(originalModel);

                }).orElseGet(() -> {
                    var newEnvironment = new Environment();
                    newEnvironment.setName(viewModel.getName().get());
                    newEnvironment.setGitRootPath(viewModel.getGitRootPath().get());
                    newEnvironment.setServerPath(viewModel.getServerRootPath().get());
                    newEnvironment.setProject(project);
                    newEnvironment.setPasswordStores(new ArrayList<>());
                    newEnvironment.setKeyStores(new ArrayList<>());
                    newEnvironment.setDomains(new ArrayList<>());
                    project.getEnvironments().add(newEnvironment);
                    var savedResult = environmentRepository.save(newEnvironment);
                    viewModel.setOriginalModel(savedResult);
                    return newEnvironment;
                });
    }

    public List<EnvironmentTableComponentModel> getEnvironmentViewModels(Project project) {
        return Optional.ofNullable(project.getEnvironments())
            .map(envs -> envs.stream().map(env ->
                 EnvironmentTableComponentModel.builder()
                         .name(new SimpleStringProperty(env.getName()))
                         .gitRootPath(new SimpleStringProperty((env.getGitRootPath())))
                         .serverRootPath(new SimpleStringProperty((env.getServerPath())))
                         .originalModel(env)
                         .build()
             ).collect(Collectors.toList()))
            .orElseGet(() -> Collections.emptyList());

    }

    public void deleteEnvironment(EnvironmentTableComponentModel viewModel) {
        environmentRepository.delete(viewModel.getOriginalModel());
    }

    public void deleteProject(Project p) {
        //p.getEnvironments().forEach(e -> environmentRepository.delete(e));
        projectRepository.delete(p);

    }
}
