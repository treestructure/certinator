package com.treestructure.certinator.controller;


import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.Project;
import io.reactivex.subjects.BehaviorSubject;
import javafx.stage.Stage;
import lombok.Data;
import org.springframework.stereotype.Component;


/**
 * Shares the viewstate between
 */
@Component
@Data
public class ViewState {

    private Stage mainStage;

    private BehaviorSubject<Project> selectedProject = BehaviorSubject.create();

    private BehaviorSubject<Environment> selectedEnvironment = BehaviorSubject.create();


}
