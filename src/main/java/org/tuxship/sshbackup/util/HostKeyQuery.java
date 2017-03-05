package org.tuxship.sshbackup.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.TransportException;
import org.tuxship.sshbackup.BackupConfig;

import javax.inject.Named;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Matthias Ervens on 05.03.2017.
 */
@Named
public class HostKeyQuery {

    private static Pattern p = Pattern.compile("(([a-f0-9]{2}:)*[a-f0-9]{2})");

    public String query(BackupConfig config) {
        try (SSHClient ssh = new SSHClient()) {
            ssh.addHostKeyVerifier(config.getHostKey());
            try {
                ssh.connect(config.getHost(), config.getPort());
            } catch (TransportException e) {
                // Could not verify `ecdsa-sha2-nistp256` host key with fingerprint `f2:bf:6c:af:b1:ce:ba:39:e2:ae:2f:17:02:bd:09:bf` for `107.6.137.158` on port 22
                return Arrays.stream(e.getMessage().split("`")).filter(
                        s -> p.matcher(s).matches())
                        .findFirst().orElse(null);
            }
            ssh.disconnect();
            ssh.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
