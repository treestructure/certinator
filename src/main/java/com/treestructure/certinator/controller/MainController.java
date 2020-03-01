package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.treestructure.certinator.CertinatorConfigProperties;
import com.treestructure.certinator.service.TruststoreCheckService;
import com.treestructure.certinator.service.TruststorebuilderService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class GeneratorController implements Initializable {

    @Autowired
    CertinatorConfigProperties config;

    @FXML
    private StackPane rootPane;

    @FXML
    private Text inputPath;

    @FXML
    private Text outputPath;

    @FXML
    private Text checkPath;

    @FXML
    private JFXTextField targetUrlField;

    @FXML
    private JFXPasswordField checkTruststorePassword;

    @FXML
    private JFXPasswordField enterPwd;

    @FXML
    private JFXPasswordField reenterPwd;

    private String trustStorePathForCheck;

    DirectoryChooser chooser = new DirectoryChooser();

    @Autowired
    TruststorebuilderService truststorebuilderService;

    @Autowired
    TruststoreCheckService truststoreCheckService;

    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputPath.setText(config.getCertificateInputPath());
        outputPath.setText(config.getTruststorePath());
        checkPath.setText("");
        targetUrlField.setText("https://");
    }

    public void chooseInputFolder() {
        stage = (Stage) rootPane.getScene().getWindow();
        chooser.setTitle("Choose Source Path for certificates to build truststore");
        Optional.ofNullable(chooser.showDialog(stage)).ifPresent(selectedPath -> {
            if (selectedPath.isDirectory()) {
                config.setCertificateInputPath(selectedPath.toString());
                inputPath.setText(selectedPath.toString());
            }
        });
    }

    public void chooseOutputFolder() {
        stage = (Stage) rootPane.getScene().getWindow();
        FileChooser outputChooser = new FileChooser();
        outputChooser.setTitle("Choose Destination Path for the truststore");

        Optional.ofNullable(outputChooser.showSaveDialog(stage)).ifPresent(selectedPath -> {
            config.setTruststorePath(selectedPath.toString());
            outputPath.setText(selectedPath.toString());
        });
    }

    public void exportTrustStore() {
        try {
            if (enterPwd.getText().equals(reenterPwd.getText())) {
                config.setDefaultTrustStorePassword(enterPwd.getText());
                var resultLog = truststorebuilderService.buildTrustStoreFromPath();

                DialogBuilder.build(rootPane, "Truststore generation successful",
                        String.join("\n", resultLog)).show();
            }
            else {
                DialogBuilder.build(rootPane,
                        "Password Error", "Passwords did not match!").show();
            }
        } catch (CertificateException | NoSuchAlgorithmException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error Generating Truststore");
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        catch (KeyStoreException e) {
            DialogBuilder.build(rootPane, "Internal Keystore Error", "KeyStore instance could not be created");
        }
        catch (IOException e) {
            DialogBuilder.build(rootPane,
                    "Path not present",
                    "The following path could not be found: "+ e.getMessage()).show();
        }
    }

    /**
     * executes a check of the trusttore
     */
    public void checkTrustStore() {
        String url = targetUrlField.getText();
        String password = checkTruststorePassword.getText();

        if (password.isEmpty()) {
            DialogBuilder.build(rootPane, "No Password provided",
                    "Please enter a password for the trustStore to check").show();

        }else if (this.trustStorePathForCheck == null || this.trustStorePathForCheck.isEmpty()) {
            DialogBuilder.build(rootPane, "No trustStore selected",
                    "Please select a truststore first").show();

        } else {
            var result = truststoreCheckService.checkForTrust(this.trustStorePathForCheck, url, password);
            if (result) {
                DialogBuilder.build(rootPane,
                        "Success",
                        "Secure connection established using the given truststore").show();
            } else {
                DialogBuilder.build(rootPane,
                        "Error",
                        "Connection Failed - No Trust could be established").show();
            }
        }


    }

    public void chooseTrustStoreForCheck() {
        stage = (Stage) rootPane.getScene().getWindow();
        FileChooser outputChooser = new FileChooser();
        outputChooser.setTitle("Choose Destination Path for the truststore");

        Optional.ofNullable(outputChooser.showOpenDialog(stage)).ifPresent(selectedPath -> {
            checkPath.setText(selectedPath.toString());
            this.trustStorePathForCheck = selectedPath.toString();
        });
    }


}
