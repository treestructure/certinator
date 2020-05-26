package com.treestructure.certinator;

import com.treestructure.certinator.event.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Value("classpath:/fxml/mainwindow.fxml")
    private Resource testResource;

    @Autowired
    private ApplicationContext context;


    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        try {
            var fxmlLoader = new FXMLLoader(testResource.getURL());
            fxmlLoader.setControllerFactory(clazz -> context.getBean(clazz));
            Parent parent = fxmlLoader.load();
            Stage stage = stageReadyEvent.getStage();

            stage.setScene(new Scene(parent, 800, 600));
            stage.setTitle("::::: Certinator ::::::");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
