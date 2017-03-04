package org.tuxship.sshbackup.ui;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.apache.commons.lang3.StringUtils;
import org.tuxship.fx.nodes.PortTextField;

/**
 * Created by Matthias Ervens on 16.01.2017.
 */
public class InputVerifier {

    private InputVerifier() {}

    public static boolean verify(TextField uiHost, PortTextField uiPort,
                                 TextField uiUser, PasswordField uiPassword,
                                 TextField uiHostKey, TextArea uiCommand,
                                 TextField uiOutputFolder, TextField uiNamingScheme) {
        boolean valid = true;

        {
            String host = uiHost.getText();
            if(StringUtils.isEmpty(host)) {
                valid = false;
            }
        }

        return valid;
    }


}
