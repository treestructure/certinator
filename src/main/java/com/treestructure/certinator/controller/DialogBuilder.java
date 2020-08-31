package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.treestructure.certinator.service.KeyStoreAnalyzerService;
import com.treestructure.certinator.service.PasswordStoreService;
import com.treestructure.certinator.ui.KeyStoreEditor;
import com.treestructure.certinator.ui.PasswordStoreEditor;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class DialogBuilder {

    @Autowired
    private ApplicationContext context;

    @Autowired
    PasswordStoreService passwordStoreService;

    @Autowired
    KeyStoreAnalyzerService keyStoreAnalyzerService;

    public static JFXDialog build(StackPane pane, String heading, String text) {
        var content = new JFXDialogLayout();
        content.setHeading(new Text(heading));
        content.setBody(new Text(text));
        var dialog = new JFXDialog(pane, content,JFXDialog.DialogTransition.CENTER);
        dialog.getStyleClass().add("customDialog");
        var button=new JFXButton("Close");
        button.setOnAction(event -> dialog.close());
        content.setActions(button);
        return dialog;
    }


    public static JFXDialog buildPassordDailog(StackPane pane, String heading, String text) {
        var content = new JFXDialogLayout();
        content.setHeading(new Text(heading));

        var dialog = new JFXDialog(pane, content,JFXDialog.DialogTransition.CENTER);
        dialog.getStyleClass().add("customDialog");


        var container = new VBox();
        container.getChildren().add(new Text(text));

        var textField = new JFXTextField();
        content.setBody(container);
        var button = new JFXButton("Close");
        button.setOnAction(event -> dialog.close());
        content.setActions(button);
        return dialog;
    }

    public JFXDialog buildPwdStoreDialog(StackPane pane, String path, String password) {

        var pwdStoreEditor = new PasswordStoreEditor(passwordStoreService, path, password);
        var content = new JFXDialogLayout();

        content.setBody(pwdStoreEditor);
        var dialog = new JFXDialog(pane, content,JFXDialog.DialogTransition.TOP);
        dialog.getStyleClass().add("pwdStoreDialog");

        var button=new JFXButton("X");
        button.setOnAction(event -> dialog.close());
        content.setActions(button);

        return dialog;
    }


    public JFXDialog buildKeyStoreEditorDialog(StackPane pane, String path, String password) {

        var pwdStoreEditor = new KeyStoreEditor(keyStoreAnalyzerService, path, password);
        var content = new JFXDialogLayout();

        content.setBody(pwdStoreEditor);
        var dialog = new JFXDialog(pane, content,JFXDialog.DialogTransition.TOP);
        dialog.getStyleClass().add("pwdStoreDialog");

        var button=new JFXButton("X");
        button.setOnAction(event -> dialog.close());
        content.setActions(button);

        return dialog;
    }

    public JFXDialog buildTemplateDialog(StackPane pane, String heading, Resource templateToLoad) {
        var content = new JFXDialogLayout();
        content.setHeading(new Text(heading));

        var dialog = new JFXDialog(pane, content,JFXDialog.DialogTransition.CENTER);


        var formContainer = new VBox();
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(templateToLoad.getURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fxmlLoader.setControllerFactory(clazz -> context.getBean(clazz));
        VBox root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        formContainer.getChildren().clear();
        formContainer.getChildren().add(root);

        content.setBody(formContainer);

        return dialog;

    }
}
