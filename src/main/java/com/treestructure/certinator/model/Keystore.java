package com.treestructure.certinator.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Keystore {

    @Id
    @GeneratedValue
    private long id;
}
