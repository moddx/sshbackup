package org.tuxship.sshbackup;

import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.SecurityUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthias Ervens on 16.01.2017.
 */
@Named
@Scope("prototype")
@Slf4j
public class SSHExecutor {

    private static final Boolean SUCCESS = Boolean.TRUE;
    private static final Boolean FAILURE = Boolean.FALSE;

    @Value("${sshexec.buffer}")
    private int bufferSize;

    private BackupConfig config;

    private long readTotalBytes = 0L;
    private long durationMs = 0L;

    private DoubleProperty speedKBs;
    private LongProperty bytesDownloaded;

    private boolean cancelled = false;

    public void init(BackupConfig config) {
        this.config = config;
        readTotalBytes = 0L;
        durationMs = 0L;
        cancelled = false;
        speedKBs = new SimpleDoubleProperty();
        bytesDownloaded = new SimpleLongProperty(readTotalBytes);
    }

    public boolean download(String outputFile) {
        try (SSHClient ssh = new SSHClient()) {
            ssh.loadKnownHosts();
            ssh.addHostKeyVerifier(config.getHostKey());
            ssh.connect(config.getHost(), config.getPort());

            if(!StringUtils.isEmpty(config.getKeyFile())) {
                KeyProvider keys = ssh.loadKeys(config.getKeyFile(), config.getPassword());
                ssh.authPublickey(config.getUser(), keys);
            } else
                ssh.authPassword(config.getUser(), config.getPassword());

            try (Session session = ssh.startSession()) {
                final Session.Command cmd = session.exec(config.getCommand());

                readToFile(cmd.getInputStream(), outputFile);
//                System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());

                try {
                    cmd.join(5, TimeUnit.SECONDS);
                } catch(ConnectionException e) {
                    log.error("Exception while waiting for backup process to finish " +
                            "(exit: " + cmd.getExitStatus() + ")", e);
                }
            }

            ssh.disconnect();
        } catch (IOException e) {
            log.error("Exception during backup", e);
            return FAILURE;
        }

        return SUCCESS;
    }

    private void readToFile(InputStream in, String outputFile) throws IOException {
        readTotalBytes = 0L;
        durationMs = 0L;

        long chunkStart;
        long deltaBytes = 0L;

        try (OutputStream rawOut = new FileOutputStream(outputFile)) {
            OutputStream out = new BufferedOutputStream(rawOut);

            log.debug("Reading in {} byte chunks..", bufferSize);

            long start = chunkStart = System.currentTimeMillis();

            byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = in.read(buffer)) != -1 && !cancelled) {
                out.write(buffer, 0, read);

                readTotalBytes += read;
                deltaBytes += read;
                long deltaTime = System.currentTimeMillis() - chunkStart;

                if(deltaTime > 2500) {
                    speedKBs.set((double) deltaBytes / (double) deltaTime / 1024d * 1000d);
                    bytesDownloaded.set(readTotalBytes);

                    log.info(String.format("Speed: %.2f KiB/s", speedKBs.get()));

                    deltaBytes = 0L;
                    chunkStart =  System.currentTimeMillis();
                }
            }

            out.flush();
            out.close();

            durationMs = System.currentTimeMillis() - start;
        }

        bytesDownloaded.set(readTotalBytes);
        speedKBs.setValue((double) readTotalBytes / (double) durationMs / 1024d * 1000d);

        log.info(String.format(
                "\nTransmitted %.2f KiB at %.2f KiB/s.\nThis took %.2f seconds.",
                readTotalBytes / 1024d, speedKBs.get(), durationMs / 1000d));
    }

    public ReadOnlyDoubleProperty speedKBsProperty() {
        return speedKBs;
    }

    public ReadOnlyLongProperty bytesDownloadedProperty() {
        return bytesDownloaded;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void cancel() {
        cancelled = true;
    }
}
