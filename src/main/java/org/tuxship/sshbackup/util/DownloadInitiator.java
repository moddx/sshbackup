package org.tuxship.sshbackup.util;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tuxship.sshbackup.BackupConfig;
import org.tuxship.sshbackup.SSHExecutor;
import org.tuxship.sshbackup.ui.DownloadWindowController;
import org.tuxship.sshbackup.ui.DownloadWindowProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by Matthias Ervens on 25.02.2017.
 */
@Component
public class DownloadInitiator {

    @Autowired
    DownloadWindowProvider dWinProv;

    public void init(BackupConfig config) {

        // todo implement naming scheme
        String outputFile = config.getOutputFolder() + File.separator + config.getNamingScheme();


        // todo 1) open download window 2) start download 3) 'sync' progress / status
        try {
            final DownloadWindowController dWinCon = dWinProv.loadWindow();
            dWinCon.setFile(outputFile);


            Task<Boolean> dTask = new Task<Boolean>() {
                Runnable onCancel;

                @Override
                protected Boolean call() throws Exception {
                    SSHExecutor executor = new SSHExecutor(config);

                    executor.speedKBsProperty().addListener((observable, oldValue, newValue) -> {
                        Platform.runLater(() -> dWinCon.updatedSpeed((Double) newValue));
                    });
                    executor.bytesDownloadedProperty().addListener((observable, oldValue, newValue) -> {
                        Platform.runLater(() -> dWinCon.updateBytesDownloaded((Long) newValue));
                    });

                    onCancel = executor::cancel;

                    boolean status = executor.download(outputFile);
                    Platform.runLater(() -> dWinCon.setFinishedStatus(status));
                    return status;
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    onCancel.run();
                    updateMessage("DownloadTask Cancelled!");
                }
            };

            Thread t = new Thread(dTask);
            t.setDaemon(true);
            t.start();

            dWinCon.setOnCancel(() -> dTask.cancel());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
