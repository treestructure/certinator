package com.treestructure.certinator.repository;

import com.treestructure.certinator.model.PasswordStore;
import org.springframework.data.repository.CrudRepository;

public interface PasswordStoreRepository extends CrudRepository<PasswordStore, Long> {
}
