package org.tuxship.sshbackup.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.tuxship.sshbackup.MainApp;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Matthias Ervens on 17.01.2017.
 */
public class FXMLWindowLoader<T> {

    @Autowired
    ApplicationContext ctx;

    private String title;
    private URL fxml;
    private StageStyle style;
    private Stage owner;

    public FXMLWindowLoader(String title, URL fxml, StageStyle style) {
        this.title = title;
        this.fxml = fxml;
        this.style = style;
        this.owner = MainApp.getMainStage();
    }

    public FXMLWindowLoader(String title, URL fxml, StageStyle style, Stage owner) {
        this.title = title;
        this.fxml = fxml;
        this.style = style;
        this.owner = owner;
    }

    public T loadWindow() throws IOException {
        Stage view = new Stage(style);
        view.initModality(Modality.WINDOW_MODAL);
        view.initOwner(owner);
        if(title != null) view.setTitle(title);

        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setControllerFactory(aClass -> ctx.getBean(aClass));
        Scene scene = new Scene(loader.load());
//        applyCss(scene);
        view.setScene(scene);
        view.show();

        return loader.getController();
    }

    public T loadToStage(Stage stage) throws IOException {
        stage.initStyle(style);
        if(title != null) stage.setTitle(title);

        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setControllerFactory(aClass -> ctx.getBean(aClass));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();

        return loader.getController();
    }

}
