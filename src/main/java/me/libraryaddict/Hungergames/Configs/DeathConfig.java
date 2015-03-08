package me.libraryaddict.Hungergames.Configs;

import java.lang.reflect.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.libraryaddict.Hungergames.Types.CustomDeathCause;
import me.libraryaddict.death.DeathCause;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeathConfig extends BaseConfig {

    private String[] ANVIL = new String[] { "%Killed% was squashed by a anvil" };
    private String[] BORDER = new String[] { "%Killed% foolishly ran outside the border" };
    private String[] CACTUS = new String[] { "%Killed% was pricked to death by a cactus" };
    private String[] CREEPER_EXPLOSION = new String[] { "%Killed% was caught in a creeper explosion" };;
    private String[] DROWN = new String[] { "%Killed% lacked the basic need to swim" };
    private String[] ENDERPEARL = new String[] { "%Killed% threw one too many enderpearls" };
    private String[] EXPLODED = new String[] { "%Killed% was caught in a explosion" };
    private String[] FALL = new String[] { "%Killed% fell to their death" };
    private String[] FIGHT = new String[] { "%Killed% was slain by %Killer%" };
    private String[] FIRE = new String[] { "%Killed% couldn't put the fire out" };
    private String[] FIREBALL = new String[] { "%Killed% was caught in a fireball" };
    private String[] LAVA = new String[] { "%Killed% took a lavary bath" };
    private String[] LIGHTNING = new String[] { "%Killed% was struck by lightning" };
    private String[] POTION = new String[] { "%Killed% was killed with potions" };
    private String[] PUSHED_FALL = new String[] { "%Killed% was knocked to their death by %Killer%" };
    private String[] PUSHED_VOID = new String[] { "%Killed% was knocked into the void by %Killer%" };
    private String[] QUIT = new String[] { "%Killed% was killed for leaving the game" };
    private String[] SHOT = new String[] { "%Killed% was shot by %Killer%" };
    private String[] SHOT_FALL = new String[] { "%Killed% was shot by %Killer% off a ledge" };
    private String[] SHOT_VOID = new String[] { "%Killed% was shot into the void by %Killer%" };
    private String[] STARVE = new String[] { "%Killed% starved to death" };;
    private String[] SUFFOCATION = new String[] { "%Killed% suffocated to death" };;
    private String[] SUICIDE = new String[] { "%Killed% commited suicide" };;
    private String[] THORNS = new String[] { "%Killed% was slain with thorns damage by %Killer%" };;
    private String[] UNKNOWN = new String[] { "%Killed% was slain by unknown means" };
    private String[] VOID = new String[] { "%Killed% fell into the void" };;
    private String[] WITHER = new String[] { "%Killed% drank a vial of wither poison" };;

    public DeathConfig() {
        super("deaths");
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        for (Field field : getClass().getDeclaredFields()) {
            try {
                if (field.getDeclaringClass() == getClass()) {
                    Field f;
                    try {
                        f = DeathCause.class.getField(field.getName());
                    } catch (NoSuchFieldException e) {
                        f = CustomDeathCause.class.getField(field.getName());
                    }
                    DeathCause cause = (DeathCause) f.get(null);
                    cause.clearDeathMessages();
                    cause.registerDeathMessage((String[]) field.get(this));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
