package org.tuxship.sshbackup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Matthias Ervens on 17.01.2017.
 */
@AllArgsConstructor
@Getter
public class BackupConfig {

    private String host;
    private Integer port;
    private String user;
    @Setter
    private String password;
    private String keyFile;
    private String hostKey;
    private String command;
    private String outputFolder;
    private String namingScheme;
    private Boolean askForPw;

}
