package org.tuxship.sshbackup.ui;

import javafx.stage.StageStyle;
import org.springframework.context.annotation.Scope;
import org.tuxship.sshbackup.MainApp;

import javax.inject.Named;

/**
 * Created by Matthias Ervens on 28.02.2017.
 */
@Named
@Scope("prototype")
public class MainWindowProvider extends FXMLWindowLoader<MainWindowController> {
    public MainWindowProvider(){
        super("SSHBackup v" + MainApp.version, MainWindowProvider.class.getResource("MainWindow.fxml"), StageStyle.DECORATED);
    }
}
