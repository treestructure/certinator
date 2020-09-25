package com.treestructure.certinator.repository;

import com.treestructure.certinator.model.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {


    List<Project> findByName(String name);

    List<Project> findAllByOrderByName();


}
