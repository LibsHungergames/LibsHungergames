package net.techcable.hungergames;

import lombok.*;

import org.bukkit.Sound;

@Getter
public enum SafeSounds {
    THUNDER("AMBIENCE_THUNDER", "ENTITY_LIGHTNING_THUNDER"),
    WITHER_SPAWN("WITHER_SPAWN", "ENTITY_WITHER_SPAWN"),
    LAVA_POP("LAVA_POP", "BLOCK_LAVA_POP"),
    CLICK("CLICK", "UI_BUTTON_CLICK"),
    ENDERMAN_TELEPORT("ENDERMEN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT"),
    IRONGOLEM_DEATH("IRONGOLEM_DEATH", "ENTITY_IRONGOLEM_DEATH");

    private final String oldName, newName;
    private final Sound bukkitSound;

    SafeSounds(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
        Sound bukkitSound;
        try {
            bukkitSound = Sound.valueOf(newName);
        } catch (IllegalArgumentException e) {
            try {
                bukkitSound = Sound.valueOf(oldName);
            } catch (IllegalArgumentException e2) {
                throw new AssertionError("No sound with name " + oldName + " or " + newName);
            }
        }
        this.bukkitSound = bukkitSound;
    }
}
