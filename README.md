# SSHBackup
## Create backups from your linux server and download them on-the-fly.

SSHBackup uses simple SSH pipelining to retrieve the data and stores it locally.
### How it works

SSHBackup connects to your server via ssh and executes a command or script of your choosing. The command needs to pipe the backup data to stdout, in order for SSHBackup to pick it up and store it in a place of your choosing.

An example would be the following command, which stores all data in the root-directory in a tar-archive, and writes that to stdout.

`tar cfvz - / -C /`

![the User Interface](https://sshbackup.tuxship.org/img/sshbackup_1.png)

### Parameters

* Host
  * the hostname/ip of the server to backup
* Port
  * the SSH server-port (default: 22)
* User
  * the user to log into the server with
* Password
  * the user's password
* HostKey (fingerprint)
  * the fingerprint of the SSH server's host key, for verification
* Command
  * the command run on the server, which writes the backup to stdout

* OutputFolder
  * the folder to store the backup in
* NamingScheme
  * (currently) the file to store the backup in. (planned) a naming pattern


### Get it
Either `git clone` and build it yourself with `maven` or download a [windows build here](https://sshbackup.tuxship.org/builds/) (signed with key id: [F864BE90](http://pgp.mit.edu/pks/lookup?op=vindex&search=0x34E65F36F864BE90)) 


### Dependencies
SSHBackup makes use of the following libraries:

* [SSHJ](https://github.com/hierynomus/sshj) (Apache License 2.0)
* [Spring 4](https://github.com/spring-projects/spring-framework) (Apache License 2.0)
* [Gson](https://github.com/google/gson) (Apache License 2.0)
* [Commons Lang](https://commons.apache.org/proper/commons-lang/) (Apache License 2.0)
