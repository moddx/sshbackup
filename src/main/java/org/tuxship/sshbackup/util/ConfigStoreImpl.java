package org.tuxship.sshbackup.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tuxship.sshbackup.BackupConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Created by Matthias Ervens on 25.02.2017.
 */
@Service
@Slf4j
public class ConfigStoreImpl implements ConfigStore {

    @Value("${user.home}")
    private String userHome;

    private final Gson gson = new Gson();

    @Override
    public void store(BackupConfig config) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(
                getSaveFile().getCanonicalPath()), StandardCharsets.UTF_8)) {

            gson.toJson(config, writer);
        } catch (IOException e) {
            log.error("Exception while storing configuration.", e);
        }


    }

    @Override
    public BackupConfig load() {
        File saveFile = getSaveFile();
        if(!saveFile.exists()) return null;

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(saveFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, BackupConfig.class);
        } catch (IOException e) {
            log.error("Exception while loading configuration.", e);
            return null;
        }
    }


    private File getSaveFile() {
        return new File(userHome, ".sshbackup");
    }
}
