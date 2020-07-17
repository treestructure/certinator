package com.treestructure.certinator.service;

import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import com.treestructure.certinator.repository.EnvironmentRepository;
import com.treestructure.certinator.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProjectPersistanceServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    EnvironmentRepository environmentRepository;

    @InjectMocks
    ProjectPersistanceService serviceUnderTest;

    @Test
    void storeEnvironmentFromViewModelTest() {

        var demoEnv = new Environment();
        demoEnv.setGitRootPath("test git path");
        demoEnv.setServerPath("test server path");

        doReturn(List.of(demoEnv)).when(environmentRepository).findAll();
        serviceUnderTest.getEnvironmentViewModels(Mockito.mock(Project.class));


    }

    @Test
    void getEnvironmentViewModelsTest() {
    }
}
