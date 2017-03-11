package org.tuxship.sshbackup;

import org.springframework.util.StringUtils;

/**
 * Created by Matthias Ervens on 17.01.2017.
 */
public class BackupConfig {

    private String host;
    private Integer port;
    private String user;
    private String password;
    private String keyFile;
    private String hostKey;
    private String command;
    private String outputFolder;
    private String namingScheme;
    private Boolean askForPw;

    public BackupConfig(String host, Integer port, String user, String password, String keyFile, String hostKey, String command, String outputFolder, String namingScheme, Boolean askForPw) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.keyFile = keyFile;
        this.hostKey = hostKey;
        this.command = command;
        this.outputFolder = outputFolder;
        this.namingScheme = namingScheme;
        this.askForPw = askForPw;
    }

    public String getHost() {
        return host;
    }
    public Integer getPort() {
        return port;
    }
    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getKeyFile() {
        return keyFile;
    }
    public String getHostKey() {
        return hostKey;
    }
    public String getCommand() {
        return command;
    }

    public String getOutputFolder() {
        return outputFolder;
    }
    public String getNamingScheme() {
        return namingScheme;
    }

    public Boolean getAskForPw() {
        return askForPw;
    }

    @Deprecated
    public String getOutputFile() {
        return null;
    }
}
