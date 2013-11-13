package me.libraryaddict.Hungergames.Configs;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.libraryaddict.Hungergames.Listeners.LibsFeastManager;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.RandomItem;

@Data
@EqualsAndHashCode(callSuper = false)
public class FeastConfig extends BaseConfig {
    /**
     * How high should the feast layers be. This is not the ground
     */
    private int chestLayersHeight = 3;
    /**
     * Advertisements, by default. Used to display the times till the feast starts
     */
    private HashMap<Integer, String> feastAdvertisements = new HashMap<Integer, String>();
    /**
     * Is the feast enabled or not
     */
    private boolean feastEnabled = true;
    /**
     * The blocks that generate along with the chests and the feast insides. This covers the feast insides
     */
    private ItemStack feastFeastBlock = new ItemStack(Material.QUARTZ_BLOCK, 1, (byte) 1);
    /**
     * When does the feast chests generate. This is the 2nd generation
     */
    private int feastGenerateTime = 20 * 60;
    /**
     * First string is the type, second is the data value. This is the ground of the feast
     */
    private ItemStack feastGroundBlock = new ItemStack(Material.QUARTZ_BLOCK, 1, (byte) 0);
    /**
     * The block hidden inside the 2nd feast generation
     */
    private ItemStack feastInsides = new ItemStack(Material.TNT, 1, (byte) 0);
    /**
     * The max distance the feast will generate from spawn. Will be random from -100 to 100 modifier
     */
    private int feastMaxDistanceFromSpawn = 100;
    /**
     * The time the platform generates
     */
    private int feastPlatformGenerateTime = 15 * 60;
    /**
     * The radius of the feast
     */
    private int feastSize = 20;
    /**
     * The block the pillar corners should be
     */
    private ItemStack pillarCorners = new ItemStack(Material.QUARTZ_BLOCK, 1, (byte) 1);
    /**
     * The block the pillars insides should be
     */
    private ItemStack pillarInsides = new ItemStack(Material.GLASS);
    /**
     * Are pillars enabled
     */
    private boolean pillarsEnabled = true;
    /**
     * The loot in the feasts chests
     */
    private ArrayList<RandomItem> randomItems = HungergamesApi.getChestManager().getRandomItems();
    /**
     * Should the feast generation remove all nearby trees. Or just trees above it.
     */
    private boolean removeTrees = true;
    /**
     * Something something scoreboard countdown feast
     */
    private String scoreboardFeastStartingIn = ChatColor.GOLD + "Feast in";
    /**
     * Strings to add to the scoreboard
     */
    private HashMap<Integer, String> scoreboardStrings = new HashMap<Integer, String>();

    public FeastConfig() {
        super("feast");
        for (int i = 0; i < 4; i++) {
            getFeastAdvertisements().put((60 * 15) + (i * 60), ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
            getFeastAdvertisements().put((60 * 15) + (i * 60) + 1, ChatColor.RED + "Use /feast to fix your compass on it!");
        }
        getFeastAdvertisements().put((60 * 19) + 30, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
        getFeastAdvertisements().put((60 * 19) + 45, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
        getFeastAdvertisements().put((60 * 19) + 50, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
        for (int i = 1; i <= 5; i++)
            getFeastAdvertisements().put((60 * 20) - i, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
        getFeastAdvertisements().put(60 * 20, ChatColor.RED + "The feast has begun!");
        getScoreboardStrings().put(60 * 15, ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Prefeast");
        getScoreboardStrings().put(60 * 20, ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Looting feast");
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        if (this.isFeastEnabled()) {
            for (int i : getScoreboardStrings().keySet()) {
                ScoreboardManager.registerStage(i, getScoreboardStrings().get(i));
            }
            LibsFeastManager.getFeastManager().setEnabled(true);
        }
    }

}
