package com.treestructure.certinator.model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
public class Environment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String gitRootPath;
    private String serverPath;

    @ManyToOne
    @JoinColumn(name="project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "environment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    List<Domain> domains;

    @OneToMany(mappedBy = "environment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<KeyStore> keyStores;

    @OneToMany(mappedBy = "environment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    List<PasswordStore> passwordStores;




}
