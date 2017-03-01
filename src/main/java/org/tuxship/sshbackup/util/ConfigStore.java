package org.tuxship.sshbackup.util;

import org.tuxship.sshbackup.BackupConfig;

/**
 * Created by Matthias Ervens on 25.02.2017.
 */
public interface ConfigStore {

    void store(BackupConfig config);
    BackupConfig load();

}
