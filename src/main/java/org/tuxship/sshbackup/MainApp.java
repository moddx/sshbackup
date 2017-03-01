package org.tuxship.sshbackup;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tuxship.sshbackup.ui.MainWindowProvider;

import javax.inject.Named;

/**
 * Created by Matthias Ervens on 16.01.2017.
 */
@Named
public class MainApp extends Application {

    public static final String version = "0.1.0";

    private static Stage mainStage;
    private ApplicationContext ctx;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        ctx = new AnnotationConfigApplicationContext(MainConfiguration.class);

        MainWindowProvider winProv = ctx.getBean(MainWindowProvider.class);
        winProv.loadToStage(mainStage);

//        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Zimmerverwaltung.png")));
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);

    }
}
