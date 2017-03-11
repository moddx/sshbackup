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
public class PasswordWindowProvider extends FXMLWindowLoader<PasswordWindowController> {
    public PasswordWindowProvider(){
        super("Enter Password", PasswordWindowProvider.class.getResource("PasswordWindow.fxml"), StageStyle.DECORATED);
    }
}
