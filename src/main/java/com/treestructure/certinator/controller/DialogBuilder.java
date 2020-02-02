package com.treestructure.certinator.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DialogBuilder {

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
}
