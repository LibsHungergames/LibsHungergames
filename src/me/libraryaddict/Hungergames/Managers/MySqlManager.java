package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.PlayerJoinThread;
import me.libraryaddict.Hungergames.Types.PlayerQuitThread;

/**
 * This class is basically useless -.-
 */
public class MySqlManager {
    private PlayerJoinThread joinThread;
    private PlayerQuitThread quitThread;

    public PlayerJoinThread getPlayerJoinThread() {
        return joinThread;
    }

    public PlayerQuitThread getPlayerQuitThread() {
        return quitThread;
    }

    public void startJoinThread() {
        joinThread = new PlayerJoinThread(HungergamesApi.getConfigManager().getMySqlConfig().isUseUUIDs());
        joinThread.start();
    }

    public void startQuitThread() {
        quitThread = new PlayerQuitThread(HungergamesApi.getConfigManager().getMySqlConfig().isUseUUIDs());
        quitThread.start();
    }

}
