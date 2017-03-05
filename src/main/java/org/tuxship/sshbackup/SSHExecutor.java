package org.tuxship.sshbackup;

import javafx.beans.property.*;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthias Ervens on 16.01.2017.
 */
public class SSHExecutor {

    private static final Boolean SUCCESS = Boolean.TRUE;
    private static final Boolean FAILURE = Boolean.FALSE;

    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private int buffer_size = DEFAULT_BUFFER_SIZE;

    private BackupConfig config;


    private long readTotalBytes = 0L;
    private long durationMs = 0L;

    private DoubleProperty speedKBs;
    private LongProperty bytesDownloaded;

    private boolean canceled = false;

    public SSHExecutor(BackupConfig config) {
        this.config = config;
        speedKBs = new SimpleDoubleProperty();
        bytesDownloaded = new SimpleLongProperty(readTotalBytes);
    }

    public boolean download(String outputFile) {
        try (SSHClient ssh = new SSHClient()) {
            ssh.loadKnownHosts();
            ssh.addHostKeyVerifier(config.getHostKey());
            ssh.connect(config.getHost(), config.getPort());
            ssh.authPassword(config.getUser(), config.getPassword());

            try (Session session = ssh.startSession()) {
                final Session.Command cmd = session.exec(config.getCommand());

                readToFile(cmd.getInputStream(), outputFile);
//                System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());

                try {
                    cmd.join(5, TimeUnit.SECONDS);
                } catch(ConnectionException e) {
                    System.out.println("Exit status: " + cmd.getExitStatus());
                }

                session.close();
            } catch (IOException e) {
                e.printStackTrace();
                return FAILURE;
            }

            ssh.disconnect();
            ssh.close();
        } catch (IOException e) {
            e.printStackTrace();
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

            System.out.println("Reading in " + buffer_size + " byte chunks..");

            long start = chunkStart = System.currentTimeMillis();

            byte[] buffer = new byte[buffer_size];
            int read;
            while ((read = in.read(buffer)) != -1 && !canceled) {
                out.write(buffer, 0, read);

                readTotalBytes += read;
                deltaBytes += read;
                long deltaTime = System.currentTimeMillis() - chunkStart;

                if(deltaTime > 2500) {
                    speedKBs.set((double) deltaBytes / (double) deltaTime / 1024d * 1000d);
                    bytesDownloaded.set(readTotalBytes);

                    System.out.println(String.format("Speed: %.2f KiB/s", speedKBs.get()));

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

        System.out.println(String.format(
                "\nTransmitted %.2f KiB at %.2f KiB/s.\nThis took %.2f seconds.",
                readTotalBytes / 1024d, speedKBs.get(), durationMs / 1000d));
    }

    public ReadOnlyDoubleProperty speedKBsProperty() {
        return speedKBs;
    }

    public ReadOnlyLongProperty bytesDownloadedProperty() {
        return bytesDownloaded;
    }

    public int getBuffer_size() {
        return buffer_size;
    }
    public void setBuffer_size(int buffer_size) {
        this.buffer_size = buffer_size;
    }

    public void cancel() {
        canceled = true;
    }
}
