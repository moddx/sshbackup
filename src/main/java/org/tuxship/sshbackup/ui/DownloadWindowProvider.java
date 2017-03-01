package org.tuxship.sshbackup.ui;

import javafx.stage.StageStyle;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

/**
 * Created by Matthias Ervens on 28.02.2017.
 */
@Named
@Scope("prototype")
public class DownloadWindowProvider extends FXMLWindowLoader<DownloadWindowController> {
    public DownloadWindowProvider(){
        super("Downloading backup..", DownloadWindowProvider.class.getResource("DownloadWindow.fxml"), StageStyle.DECORATED);
    }
}
