package com.treestructure.certinator.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Data
@Entity
public class PasswordStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String serverPath;
    private String gitPath;
    private String password;

    @ManyToOne
    @JoinColumn(name="environment_id", nullable = false)
    private Environment environment;

    /**
     * checks if repository and server version are equal
     * @return
     */
    public boolean checkForEquality() {
        return false;
    }
}
