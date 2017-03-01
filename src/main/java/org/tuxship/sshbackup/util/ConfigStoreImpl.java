package org.tuxship.sshbackup.util;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tuxship.sshbackup.BackupConfig;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by Matthias Ervens on 25.02.2017.
 */
@Service
public class ConfigStoreImpl implements ConfigStore {

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
            e.printStackTrace();
        }


    }

    @Override
    public BackupConfig load() {
        File saveFile = getSaveFile();
        if(!saveFile.exists()) return null;

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(saveFile), "UTF-8")) {
            return gson.fromJson(reader, BackupConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println("Error while loading json storage.");
        return null;
    }


    private File getSaveFile() {
        return new File(userHome, ".sshbackup");
    }
}
