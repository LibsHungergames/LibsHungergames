package me.libraryaddict.Hungergames.Types;


import me.libraryaddict.Hungergames.Configs.TranslationConfig;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public abstract class ClickInventory implements Listener {
    protected Inventory inv;
    protected String title = "Inventory";
    protected TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();

    public ClickInventory() {
        Bukkit.getPluginManager().registerEvents(this, HungergamesApi.getHungergames());
    }

    public void clone(Inventory newInv) {
        inv = Bukkit.createInventory(null, newInv.getSize(), title);
        inv.setContents(newInv.getContents());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

}
