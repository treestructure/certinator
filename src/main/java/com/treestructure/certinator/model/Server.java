package com.treestructure.certinator.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Server {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String trustStorePath;
    private String keyStorePath;
}
