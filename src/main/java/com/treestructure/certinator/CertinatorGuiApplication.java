package com.treestructure.certinator;

import com.treestructure.certinator.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class CertinatorGuiApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(CertinatorApplication.class).run();
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
        applicationContext.getAutowireCapableBeanFactory().autowireBean(stage);
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
