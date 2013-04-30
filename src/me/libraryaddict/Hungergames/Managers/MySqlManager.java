package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Types.PlayerJoinThread;

public class MySqlManager  {
    public String SQL_USER = "";
    public String SQL_PASS = "";
    public String SQL_DATA = "";
    public String SQL_HOST = "";
    private PlayerJoinThread joinThread;

    public void startJoinThread() {
        joinThread = new PlayerJoinThread(this);
        joinThread.start();
    }

    public PlayerJoinThread getPlayerJoinThread() {
        return joinThread;
    }

}
