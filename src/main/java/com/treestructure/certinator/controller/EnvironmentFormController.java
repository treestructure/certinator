package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXTextField;
import com.treestructure.certinator.CertinatorConfigProperties;
import com.treestructure.certinator.repository.EnvironmentRepository;
import com.treestructure.certinator.service.EnvironmentPersistanceService;
import com.treestructure.certinator.service.KeyStoreAnalyzerService;
import com.treestructure.certinator.service.TruststoreCheckService;
import com.treestructure.certinator.ui.DomainTable;
import com.treestructure.certinator.ui.KeyStoreTable;
import com.treestructure.certinator.ui.PasswordStoreTable;
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

    private final CertinatorConfigProperties config;

    private final TruststoreCheckService trustStoreCheckService;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.viewState.getSelectedEnvironment().subscribe(environment -> {

            domainTable.storeData.setAll( environmentPersistanceService.getDomainViewModels(environment));
            keyStoreTable.storeData.setAll(environmentPersistanceService.getKeyStoreViewModels(environment));
            passwordStoreTable.storeData.setAll(environmentPersistanceService.getPasswordStoreModels(environment));

            envNameField.setText(environment.getName());

            domainTable.checkConnectivityClicked().subscribe(item -> {

                var pwsDialog = DialogBuilder.buildPassordDailog(mainPane, "Enter Password", "Please enter password here");
                pwsDialog.show();

                boolean trustChecked = false;
                for (var store : environment.getKeyStores()) {
                    this.trustStoreCheckService.checkForTrust(store.getServerPath(),item.getUrl().getValue(), "test");
                }

            });

            keyStoreTable.gitPathChanged().subscribe(item -> {
                FileChooser outputChooser = new FileChooser();
                outputChooser.setTitle("Choose Git Path of Keystore");

                Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
                    item.getGitPath().set(selectedPath.toString());
                });
            });

            passwordStoreTable.gitPathChanged().subscribe(item -> {
                FileChooser outputChooser = new FileChooser();
                outputChooser.setTitle("Choose Git Path of Password Store");

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

            passwordStoreTable.serverPathChanged().subscribe(item -> {
                FileChooser outputChooser = new FileChooser();
                outputChooser.setTitle("Choose Server Path of Password Store");

                Optional.ofNullable(outputChooser.showOpenDialog(null)).ifPresent(selectedPath -> {
                    item.getServerPath().set(selectedPath.toString());
                });
            });
            passwordStoreTable.passwordStoreOpened().subscribe(item -> {
                var pwdEditorDialog = dialogBuilder.buildTemplateDialog(mainPane, "test", pwsTemplateResource);
                pwdEditorDialog.show();
            });

            keyStoreTable.deleted().subscribe(viewModel -> environmentPersistanceService.deleteKeyStore(viewModel));
            keyStoreTable.saved().subscribe(viewModel -> {
                environmentPersistanceService.storeKeyStoreFromViewModel(environment, viewModel);
            });

            domainTable.deleted().subscribe(viewModel -> environmentPersistanceService.deleteDomain(viewModel));
            domainTable.saved().subscribe(domainModel ->
                    environmentPersistanceService.storeDomainFromViewModel(environment, domainModel));

            passwordStoreTable.deleted().subscribe(viewModel -> environmentPersistanceService.deletePasswordStore(viewModel));
            passwordStoreTable.saved().subscribe(domainModel ->
                    environmentPersistanceService.storePasswordStoreViewModel(environment, domainModel));
        });
    }

    public void saveEnvironment() {
        this.environmentRepository.save(this.viewState.getSelectedEnvironment().getValue());
    }
}
