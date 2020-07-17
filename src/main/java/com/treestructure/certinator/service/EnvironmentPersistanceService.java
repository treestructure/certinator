package com.treestructure.certinator.service;

import com.treestructure.certinator.model.Domain;
import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.model.KeyStore;
import com.treestructure.certinator.model.PasswordStore;
import com.treestructure.certinator.model.ui.DomainTableComponentModel;
import com.treestructure.certinator.model.ui.PasswordStoreComponentModel;
import com.treestructure.certinator.model.ui.StoreTableComponentModel;
import com.treestructure.certinator.repository.DomainRepository;
import com.treestructure.certinator.repository.EnvironmentRepository;
import com.treestructure.certinator.repository.KeyStoreRepository;
import com.treestructure.certinator.repository.PasswordStoreRepository;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnvironmentPersistanceService {


    @Autowired
    KeyStoreRepository keyStoreRepository;

    @Autowired
    EnvironmentRepository environmentRepository;

    @Autowired
    PasswordStoreRepository passwordStoreRepository;

    @Autowired
    DomainRepository domainRepository;

    public void storeKeyStoreFromViewModel(Environment environment, StoreTableComponentModel viewModel) {
        Optional.ofNullable(viewModel.getOriginalModel())
                .ifPresentOrElse(originalModel -> {
                    originalModel.setServerPath(viewModel.getServerPath().get());
                    originalModel.setGitPath(viewModel.getGitPath().get());
                    originalModel.setName(viewModel.getName().get());
                    originalModel.setPassword(viewModel.getPassword().get());
                    keyStoreRepository.save(originalModel);
                }, () -> {
                    var newKs = new KeyStore();
                    newKs.setName(viewModel.getName().get());
                    newKs.setGitPath(viewModel.getGitPath().get());
                    newKs.setServerPath(viewModel.getServerPath().get());
                    newKs.setPassword(viewModel.getPassword().get());
                    newKs.setEnvironment(environment);
                    environment.getKeyStores().add(newKs);

                    var savedResult = keyStoreRepository.save(newKs);
                    viewModel.setOriginalModel(savedResult);
                });
    }

    public void storeDomainFromViewModel(Environment environment, DomainTableComponentModel viewModel) {
        Optional.ofNullable(viewModel.getOriginalModel())
                .ifPresentOrElse(originalModel -> {
                    originalModel.setName(viewModel.getName().get());
                    originalModel.setUrl(viewModel.getUrl().get());
                    domainRepository.save(originalModel);
                }, () -> {
                    var domain = new Domain();
                    domain.setName(viewModel.getName().get());
                    domain.setUrl(viewModel.getUrl().get());

                    domain.setEnvironment(environment);
                    environment.getDomains().add(domain);
                    var savedResult = domainRepository.save(domain);
                    viewModel.setOriginalModel(savedResult);
                });
    }
    public void storePasswordStoreViewModel(Environment environment, PasswordStoreComponentModel viewModel) {
        Optional.ofNullable(viewModel.getOriginalModel())
                .ifPresentOrElse(originalModel -> {
                    originalModel.setName(viewModel.getName().get());
                    originalModel.setPassword(viewModel.getPassword().get());
                    originalModel.setServerPath(viewModel.getServerPath().get());
                    originalModel.setGitPath(viewModel.getGitPath().get());
                    passwordStoreRepository.save(originalModel);
                }, () -> {
                    var newPwdStore = new PasswordStore();
                    newPwdStore.setName(viewModel.getName().get());
                    newPwdStore.setGitPath(viewModel.getGitPath().get());
                    newPwdStore.setServerPath(viewModel.getServerPath().get());
                    newPwdStore.setPassword(viewModel.getPassword().get());
                    newPwdStore.setEnvironment(environment);
                    environment.getPasswordStores().add(newPwdStore);
                    var savedResult = passwordStoreRepository.save(newPwdStore);
                    viewModel.setOriginalModel(savedResult);
                });
    }

    public List<StoreTableComponentModel> getKeyStoreViewModels(Environment environment) {
        return environment.getKeyStores().stream().map(ks -> StoreTableComponentModel
                .builder()
                .name(new SimpleStringProperty(ks.getName()))
                .gitPath(new SimpleStringProperty(ks.getGitPath()))
                .password(new SimpleStringProperty(ks.getPassword()))
                .serverPath(new SimpleStringProperty(ks.getServerPath()))
                .originalModel(ks)
                .build())
                .collect(Collectors.toList());
    }

    public List<PasswordStoreComponentModel> getPasswordStoreModels(Environment environment) {
        return environment.getPasswordStores().stream().map(pwsStore -> PasswordStoreComponentModel
                .builder()
                .name(new SimpleStringProperty(pwsStore.getName()))
                .gitPath(new SimpleStringProperty(pwsStore.getGitPath()))
                .password(new SimpleStringProperty(pwsStore.getPassword()))
                .serverPath(new SimpleStringProperty(pwsStore.getServerPath()))
                .originalModel(pwsStore)
                .build())
                .collect(Collectors.toList());
    }

    public List<DomainTableComponentModel> getDomainViewModels(Environment environment) {
        return environment.getDomains().stream().map(domain -> {
            return DomainTableComponentModel
                    .builder()
                    .name(new SimpleStringProperty(domain.getName()))
                    .url(new SimpleStringProperty(domain.getUrl()))
                    .originalModel(domain)
                    .build();
        }).collect(Collectors.toList());
    }


    public void deleteKeyStore(StoreTableComponentModel viewModel) {
        keyStoreRepository.deleteById(viewModel.getOriginalModel().getId());
    }

    public void deleteDomain(DomainTableComponentModel viewModel) {
        domainRepository.delete(viewModel.getOriginalModel());
    }

    public void deletePasswordStore(PasswordStoreComponentModel viewModel) {
        passwordStoreRepository.delete(viewModel.getOriginalModel());
    }


}
