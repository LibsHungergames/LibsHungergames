package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Types.PlayerJoinThread;

public class MySqlManager  {
    private PlayerJoinThread joinThread;
    public String SQL_DATA = "";
    public String SQL_HOST = "";
    public String SQL_PASS = "";
    public String SQL_USER = "";

    public PlayerJoinThread getPlayerJoinThread() {
        return joinThread;
    }

    public void startJoinThread() {
        joinThread = new PlayerJoinThread();
        joinThread.start();
    }

}
