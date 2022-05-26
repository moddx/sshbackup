package org.tuxship.sshbackup.util;

import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tuxship.sshbackup.BackupConfig;
import org.tuxship.sshbackup.SSHExecutor;
import org.tuxship.sshbackup.ui.DownloadWindowController;
import org.tuxship.sshbackup.ui.DownloadWindowProvider;
import org.tuxship.sshbackup.ui.PasswordWindowController;
import org.tuxship.sshbackup.ui.PasswordWindowProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by Matthias Ervens on 25.02.2017.
 */
@Component
@Slf4j
public class DownloadInitiator {

    private final DownloadWindowProvider dWinProv;
    private final SSHExecutor sshExec;
    private final PasswordWindowProvider pwWinProv;

    private DownloadWindowController dWinCon;
    private PasswordWindowController pwWinCon;

    public DownloadInitiator(DownloadWindowProvider dWinProv, SSHExecutor sshExec, PasswordWindowProvider pwWinProv) {
        this.dWinProv = dWinProv;
        this.sshExec = sshExec;
        this.pwWinProv = pwWinProv;
    }


    public void init(BackupConfig config) {

        // todo implement naming scheme
        final String outputFile = config.getOutputFolder() + File.separator + config.getNamingScheme();

        Task<Boolean> dTask = new Task<>() {
            Runnable onCancel;

            @Override
            protected Boolean call() {
                onCancel = sshExec::cancel;
                boolean status = sshExec.download(outputFile);
                finished(status);
                return status;
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                onCancel.run();
                updateMessage("DownloadTask Cancelled!");
            }
        };

        // prepare SSHExecutor
        sshExec.init(config);

        sshExec.speedKBsProperty().addListener((observable, oldValue, newValue) -> updateSpeed((Double) newValue));
        sshExec.bytesDownloadedProperty().addListener((observable, oldValue, newValue) -> updateBytesDownloaded((Long) newValue));

        // ask for password if necessary
        if(config.getAskForPw()) {
            try {
                pwWinCon = pwWinProv.showAndWaitForWindow();
            } catch (IOException e) {
                log.error("Exception while opening the password prompt.", e);
            }
            String pw = pwWinCon.waitForPassword();
            if(StringUtils.isEmpty(pw)) return;
            config.setPassword(pw);
        }

        // open download window
        try {
            dWinCon = dWinProv.loadWindow();
        } catch (IOException e) {
            log.error("Exception while opening DownloadWindow.", e);
        }
        dWinCon.setFile(outputFile);
        dWinCon.setOnCancel(dTask::cancel);

        // start downloading
        Thread t = new Thread(dTask);
        t.setDaemon(true);
        t.start();
    }


    private void updateSpeed(Double speed) {
        Platform.runLater(() -> {
            if(dWinCon != null)
                dWinCon.updatedSpeed(speed);
        });
    }

    private void updateBytesDownloaded(Long bytes) {
        Platform.runLater(() -> {
            if(dWinCon != null)
                dWinCon.updateBytesDownloaded(bytes);
        });
    }

    private void finished(boolean status) {
        Platform.runLater(() -> {
            if (dWinCon != null)
                Platform.runLater(() -> dWinCon.setFinishedStatus(status));
        });
    }

}
