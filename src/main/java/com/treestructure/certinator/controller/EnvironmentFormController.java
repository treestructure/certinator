package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTextField;
import com.treestructure.certinator.model.Environment;
import com.treestructure.certinator.repository.EnvironmentRepository;
import com.treestructure.certinator.service.EnvironmentPersistanceService;
import com.treestructure.certinator.service.KeyStoreAnalyzerService;
import com.treestructure.certinator.service.TruststoreCheckService;
import com.treestructure.certinator.ui.DomainTable;
import com.treestructure.certinator.ui.KeyStoreTable;
import com.treestructure.certinator.ui.PasswordStoreTable;
import io.reactivex.subjects.BehaviorSubject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@Slf4j
@RequiredArgsConstructor
public class EnvironmentFormController implements Initializable {

    @Value("classpath:/fxml/pwdstoreeditor.fxml")
    private Resource pwsTemplateResource;

    @FXML
    StackPane mainPane;
    @FXML
    DomainTable domainTable;
    @FXML
    PasswordStoreTable passwordStoreTable;
    @FXML
    KeyStoreTable keyStoreTable;
    @FXML
    KeyStoreAnalyzerService keyStoreAnalyzerService;
    @FXML
    VBox rootBox;
    @FXML
    JFXTextField envNameField;

    private final EnvironmentRepository environmentRepository;

    private final DialogBuilder dialogBuilder;

    private final ViewState viewState;

    private final EnvironmentPersistanceService environmentPersistanceService;

    private final TruststoreCheckService trustStoreCheckService;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        bindDomainTable(this.viewState.getSelectedEnvironment());
        bindKeyStoreTable(this.viewState.getSelectedEnvironment());
        bindPasswordStoreTable(this.viewState.getSelectedEnvironment());


        this.viewState.getSelectedEnvironment().subscribe(environment -> {
            envNameField.setText(environment.getName());
        });
    }

    /**
     * binds all event handlers of the domaintable
     * @param selectedEnvironment
     */
    private void bindDomainTable(BehaviorSubject<Environment> selectedEnvironment) {
        selectedEnvironment.subscribe(e -> environmentPersistanceService.getDomainViewModels(e));

        domainTable.checkConnectivityClicked().subscribe(item -> {
            var pwsDialog = DialogBuilder.buildPassordDailog(mainPane, "Enter Password", "Please enter password here");
            pwsDialog.show();

            boolean trustChecked = false;
            for (var store : selectedEnvironment.getValue().getKeyStores()) {
                this.trustStoreCheckService.checkForTrust(store.getServerPath(),item.getUrl().getValue(), "test");
            }

        });

        domainTable.deleted().subscribe(viewModel -> environmentPersistanceService.deleteDomain(viewModel));
        domainTable.saved().subscribe(domainModel ->
                environmentPersistanceService.storeDomainFromViewModel(selectedEnvironment.getValue(), domainModel));
    }

    /**
     *
     * @param selectedEnvironment
     */
    private void bindPasswordStoreTable(BehaviorSubject<Environment> selectedEnvironment) {
        selectedEnvironment.subscribe(e -> passwordStoreTable.storeData.setAll(environmentPersistanceService.getPasswordStoreModels(e)));
        passwordStoreTable.gitPathChanged().subscribe(item -> {
            FileChooser outputChooser = new FileChooser();
            outputChooser.setTitle("Choose Git Path of Password Store");

            Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
                item.getGitPath().set(selectedPath.toString());
            });
        });
        passwordStoreTable.serverPathChanged().subscribe(item -> {
            FileChooser outputChooser = new FileChooser();
            outputChooser.setTitle("Choose Server Path of Password Store");

            Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
                item.getServerPath().set(selectedPath.toString());
            });
        });
        passwordStoreTable.passwordStoreOpened().subscribe(item -> {
            var pwdEditorDialog = dialogBuilder.buildPwdStoreDialog(mainPane, item.getGitPath().getValue(), item.getPassword().getValue());
            pwdEditorDialog.show();
        });
        passwordStoreTable.deleted().subscribe(viewModel -> environmentPersistanceService.deletePasswordStore(viewModel));
        passwordStoreTable.saved().subscribe(domainModel ->
                environmentPersistanceService.storePasswordStoreViewModel(selectedEnvironment.getValue(), domainModel));
    }

    /**
     *
     * @param environment
     * @param selectedEnvironment
     */
    private void bindKeyStoreTable(BehaviorSubject<Environment> selectedEnvironment) {
        selectedEnvironment.subscribe(e -> keyStoreTable.storeData.setAll(environmentPersistanceService.getKeyStoreViewModels(e)));

        keyStoreTable.gitPathChanged().subscribe(item -> {
            FileChooser outputChooser = new FileChooser();
            outputChooser.setTitle("Choose Git Path of Keystore");

            Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
                item.getGitPath().set(selectedPath.toString());
            });
        });
        keyStoreTable.serverPathChanged().subscribe(item -> {
            FileChooser outputChooser = new FileChooser();
            outputChooser.setTitle("Choose Server Path of Keystore");

            Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
                item.getServerPath().set(selectedPath.toString());
            });
        });
        keyStoreTable.syncStatusClicked().subscribe(item -> {
            item.getOriginalModel().checkForEquality(keyStoreAnalyzerService);
        });
        keyStoreTable.openInKeystoreEditorClicked().subscribe(item -> {

        });
        keyStoreTable.deleted().subscribe(viewModel -> environmentPersistanceService.deleteKeyStore(viewModel));
        keyStoreTable.saved().subscribe(viewModel -> {
            environmentPersistanceService.storeKeyStoreFromViewModel(selectedEnvironment.getValue(), viewModel);
        });
    }

    public void saveEnvironment() {
        this.environmentRepository.save(this.viewState.getSelectedEnvironment().getValue());
    }
}
