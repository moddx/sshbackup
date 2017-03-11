package org.tuxship.sshbackup.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

/**
 * Created by Matthias Ervens on 10.03.2017.
 */
@Named
public class PasswordWindowController {

    private final Logger logger = LoggerFactory.getLogger(PasswordWindowController.class);

    @FXML
    private PasswordField uiPassword;

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;

    private final Long pwLock = 51364984135L;
    private boolean pwAcquired = false;
    private String password;

    @FXML
    private synchronized void initialize() {
        uiPassword.setText("");
        pwAcquired = false;
        password = null;
    }

    @FXML
    private void submit() {
        acquired(uiPassword.getText());
        close();
    }

    @FXML
    private void cancel() {
        acquired(null);
        close();
    }

    private void close() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void acquired(String pw) {
        synchronized (pwLock) {
            pwAcquired = true;
            password = pw;
            pwLock.notifyAll();
        }
    }

    public String waitForPassword() {
        try {
            synchronized (pwLock) {
                while (!pwAcquired)
                    pwLock.wait();
                String pw = password;
                password = null;
                pwAcquired = false;
                return pw;
            }
        } catch (InterruptedException e) {
            logger.error("Exception while waiting for user password.", e);
            return null;
        }
    }
}
