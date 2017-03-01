package org.tuxship.sshbackup.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.tuxship.sshbackup.MainApp;

import javax.inject.Named;

/**
 * Created by Matthias Ervens on 17.01.2017.
 */
@Named
@Scope("prototype")
public class DownloadWindowController {

    @FXML ProgressIndicator uiProgress;
    @FXML ImageView uiSuccess;
    @FXML Label uiHeading;
    @FXML Text uiOutputFile;
    @FXML Label uiTotal;
    @FXML Label uiSpeed;
    @FXML Label uiDuration;
    @FXML Button btnCancel;
    @FXML Button btnClose;

    private static final Image imgSuccess = new Image(MainApp.class.getResourceAsStream("/commons-success.png"));
    private static final Image imgFailure = new Image(MainApp.class.getResourceAsStream("/gnome-important.png"));

    private Timeline timer;
    private long durationSec = 0;

    private Runnable cancelAction;


    @FXML
    private void initialize() {
        uiSuccess.setVisible(false);
        startTimer();
    }

    public void setFile(String file) {
        uiOutputFile.setText(file);
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(
                Duration.millis(1000),
                event -> {
                    durationSec++;
                    displayDuration(durationSec);
                })
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private void stopTimer() {
        timer.stop();
    }

    private void displayDuration(long durSecs) {
        String format;

        if(durSecs > 3600) {
            long hours = durSecs / 3600;
            long mins = (durSecs % 3600) / 60;
            long secs = durSecs % 60;
            format = String.format("%dh %d:%d", hours, mins, secs);
        } else if(durSecs > 60) {
            long mins = durSecs / 60;
            long secs = durSecs % 60;
            format = String.format("%d:%d", mins, secs);
        } else
            format = String.format("%d s", durSecs);

        uiDuration.setText(format);
    }

    public void updatedSpeed(Double kbs) {
        String format = (kbs < 1000d)
            ? String.format("%.2f KiB/s", kbs)
            : String.format("%.2f MiB/s", kbs / 1024d);

        uiSpeed.setText(format);
    }

    public void updateBytesDownloaded(Long bytes) {
        String format;

        if(bytes > 1099511627776d)
            format = String.format("%.2f TiB", bytes / 1099511627776d);
        else if(bytes > 1073741824d)
            format = String.format("%.2f GiB", bytes / 1073741824d);
        else if(bytes > 1048576d)
            format = String.format("%.2f MiB", bytes / 1048576d);
        else if(bytes > 1024d)
            format = String.format("%.2f KiB", bytes / 1024d);
        else
            format = String.format("%d B", bytes);


        uiTotal.setText(format);
    }

    public void setFinishedStatus(boolean success) {
        stopTimer();
        uiProgress.setProgress(1d);
        uiProgress.setVisible(false);

        uiSuccess.setVisible(true);
        uiSuccess.setImage(success ? imgSuccess : imgFailure);

        btnCancel.setDisable(true);
        btnClose.setDisable(false);

        String title = success ? "Backup finished" : "Backup failed!";
        uiHeading.setText(title);
        ((Stage) uiHeading.getScene().getWindow()).setTitle(title);
    }

    public DoubleProperty progressProperty() {
        return uiProgress.progressProperty();
    }

    public void setOnCancel(Runnable cancelAction) {
        this.cancelAction = cancelAction;
    }

    @FXML
    public void cancel() {
        if(cancelAction != null)
            cancelAction.run();

        setFinishedStatus(false);
    }

    @FXML
    public void close() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
