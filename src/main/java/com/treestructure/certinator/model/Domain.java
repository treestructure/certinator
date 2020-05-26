package com.treestructure.certinator.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Domain {
    @Id
    @GeneratedValue
    private long id;

    private String url;

    private String name;

    @ManyToOne
    @JoinColumn(name="environment_id")
    private Environment environment;

}
