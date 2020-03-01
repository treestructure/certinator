package com.treestructure.certinator.service;


import com.treestructure.certinator.model.Project;
import io.reactivex.subjects.BehaviorSubject;
import org.springframework.stereotype.Component;

@Component
@Data
public class ViewStateService {



    private BehaviorSubject<Project> selectedProject;


}
