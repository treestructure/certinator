package com.treestructure.certinator.repository;

import com.treestructure.certinator.model.KeyStore;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KeyStoreRepository extends CrudRepository<KeyStore, Long> {


    Optional<KeyStore> findByGitPath(String gitPath);

    Optional<KeyStore> findByServerPath(String serverPath);

}
