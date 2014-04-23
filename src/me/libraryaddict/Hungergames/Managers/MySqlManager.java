package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.PlayerJoinThread;

/**
 * This class is basically useless -.-
 */
public class MySqlManager {
    private PlayerJoinThread joinThread;

    public PlayerJoinThread getPlayerJoinThread() {
        return joinThread;
    }

    public void startJoinThread() {
        joinThread = new PlayerJoinThread(HungergamesApi.getConfigManager().getMySqlConfig().isUseUUIDs());
        joinThread.start();
    }

}
