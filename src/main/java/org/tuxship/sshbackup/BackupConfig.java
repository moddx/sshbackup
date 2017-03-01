package org.tuxship.sshbackup;

/**
 * Created by Matthias Ervens on 17.01.2017.
 */
public class BackupConfig {

    private String host;
    private Integer port;
    private String user;
    private String password;
    private String hostKey;
    private String command;
    private String outputFolder;
    private String namingScheme;

    public BackupConfig(String host, Integer port, String user, String password, String hostKey, String command, String outputFolder, String namingScheme) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.hostKey = hostKey;
        this.command = command;
        this.outputFolder = outputFolder;
        this.namingScheme = namingScheme;
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

    @Deprecated
    public String getOutputFile() {
        return null;
    }
}
