package com.treestructure.certinator.service;

import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.model.ui.EnvironmentTableComponentModel;
import com.treestructure.certinator.repository.EnvironmentRepository;
import com.treestructure.certinator.repository.ProjectRepository;
import javafx.beans.property.SimpleStringProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectPersistanceService {


    private final ProjectRepository projectRepository;

    private final EnvironmentRepository environmentRepository;

    public void storeEnvironmentFromViewModel(Project project, EnvironmentTableComponentModel viewModel) {
        Optional.ofNullable(viewModel.getOriginalModel())
                .ifPresentOrElse(originalModel -> {
                    originalModel.setName(viewModel.getName().get());
                    originalModel.setGitRootPath(viewModel.getGitRootPath().get());
                    originalModel.setServerPath(viewModel.getServerRootPath().get());
                    environmentRepository.save(originalModel);
                }, () -> {
                    var newEnvironment = new Environment();
                    newEnvironment.setName(viewModel.getName().get());
                    newEnvironment.setGitRootPath(viewModel.getGitRootPath().get());
                    newEnvironment.setServerPath(viewModel.getServerRootPath().get());
                    newEnvironment.setProject(project);
                    project.getEnvironments().add(newEnvironment);
                    var savedResult = environmentRepository.save(newEnvironment);
                    viewModel.setOriginalModel(savedResult);
                });
    }

    public List<EnvironmentTableComponentModel> getEnvironmentViewModels(Project project) {
        return project.getEnvironments().stream().map(env -> {
            return EnvironmentTableComponentModel.builder()
                    .name(new SimpleStringProperty(env.getName()))
                    .gitRootPath(new SimpleStringProperty((env.getGitRootPath())))
                    .serverRootPath(new SimpleStringProperty((env.getServerPath())))
                    .originalModel(env)
                    .build();
        }).collect(Collectors.toList());
    }

    public void deleteEnvironment(EnvironmentTableComponentModel viewModel) {
        environmentRepository.delete(viewModel.getOriginalModel());
    }
}
