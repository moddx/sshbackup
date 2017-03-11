package org.tuxship.sshbackup.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.tuxship.fx.nodes.PortTextField;
import org.tuxship.sshbackup.BackupConfig;
import org.tuxship.sshbackup.MainApp;
import org.tuxship.sshbackup.util.ConfigStore;
import org.tuxship.sshbackup.util.ConfigVerifier;
import org.tuxship.sshbackup.util.DownloadInitiator;
import org.tuxship.sshbackup.util.HostKeyQuery;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;

/**
 * Created by Matthias Ervens on 16.01.2017.
 */
@Named
public class MainWindowController {

    @Autowired
    private ConfigStore configStore;

    @Autowired
    private DownloadInitiator initiator;

    @Autowired
    private HostKeyQuery hostKeyQuery;

    @FXML TextField uiHost;
    @FXML PortTextField uiPort;
    @FXML TextField uiUser;
    @FXML PasswordField uiPassword;
    @FXML TextField uiPrivKey;
    @FXML Button btnSelectPrivKey;
    @FXML CheckBox uiAskForPw;

    @FXML TextField uiHostKey;
    @FXML Button btnQueryKey;

    @FXML TextArea uiCommand;

    @FXML TextField uiOutputFolder;
    @FXML Button btnSelectFolder;
    @FXML TextField uiNamingScheme;

    @FXML Button btnBackup;

    @FXML Button btnSave;
    @FXML Button btnExit;

    private BackupConfig config;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            // load config
            config = configStore.load();

            // display config
            if(config != null) {
                uiHost.setText(config.getHost());
                uiPort.setText(config.getPort().toString());
                uiUser.setText(config.getUser());
                uiPassword.setText(config.getPassword());
                uiPrivKey.setText(config.getKeyFile());
                uiHostKey.setText(config.getHostKey());
                uiCommand.setText(config.getCommand());
                uiOutputFolder.setText(config.getOutputFolder());
                uiNamingScheme.setText(config.getNamingScheme());
                uiAskForPw.setSelected(config.getAskForPw());
            }
        });
    }

    @FXML
    private void selectFolder() {
        DirectoryChooser dChooser = new DirectoryChooser();
        dChooser.setTitle("Select target folder for backups");
        File folder = dChooser.showDialog(btnSelectFolder.getScene().getWindow());

        if(folder == null) return;

        try {
            uiOutputFolder.setText(folder.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backup() {
        BackupConfig c = buildConfig();
        // todo verifyOk
        if(!ConfigVerifier.verifyOk(c, this))
            return;

        config = c;
        initiator.init(config);
    }

    @FXML
    private void save() {
        BackupConfig c = buildConfig();
        if(!ConfigVerifier.verifyOk(c, this))
            return;

        config = c;
        configStore.store(c);
    }

    @FXML
    private void exit() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void selectPrivKey() {
        FileChooser fChooser = new FileChooser();
        if(StringUtils.isEmpty(uiPrivKey.getText()))
            fChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else {
            fChooser.setInitialDirectory(new File(uiPrivKey.getText()).getParentFile());
        }
        File selected = fChooser.showOpenDialog(MainApp.getMainStage());

        if(selected == null) return;

        try {
            uiPrivKey.setText(selected.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("Could not get canonical path of selected keyfile. aborting.");
            e.printStackTrace();
        }
    }

    @FXML
    private void queryKey() {
        String fp = hostKeyQuery.query(buildConfig());
        if(fp != null)
            uiHostKey.setText(fp);
    }

    private BackupConfig buildConfig() {
        return new BackupConfig(
                uiHost.getText(),
                Integer.parseInt(uiPort.getText()),
                uiUser.getText(),
                uiPassword.getText(),
                uiPrivKey.getText(),
                uiHostKey.getText(),
                uiCommand.getText(),
                uiOutputFolder.getText(),
                uiNamingScheme.getText(),
                uiAskForPw.isSelected()
        );
    }
}
