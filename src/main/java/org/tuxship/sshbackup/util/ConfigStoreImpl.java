package org.tuxship.sshbackup.util;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tuxship.sshbackup.BackupConfig;
import sun.rmi.runtime.Log;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by Matthias Ervens on 25.02.2017.
 */
@Service
public class ConfigStoreImpl implements ConfigStore {

    private final Logger logger = LoggerFactory.getLogger(ConfigStore.class);

    @Value("${user.home}")
    private String userHome;

    private Gson gson = new Gson();

    @Override
    public void store(BackupConfig config) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(
                getSaveFile().getCanonicalPath()), "UTF-8")) {

            gson.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            logger.error("Exception while storing configuration.", e);
        }


    }

    @Override
    public BackupConfig load() {
        File saveFile = getSaveFile();
        if(!saveFile.exists()) return null;

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(saveFile), "UTF-8")) {
            return gson.fromJson(reader, BackupConfig.class);
        } catch (IOException e) {
            logger.error("Exception while loading configuration.", e);
            return null;
        }
    }


    private File getSaveFile() {
        return new File(userHome, ".sshbackup");
    }
}
