package me.libraryaddict.Hungergames.Managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import me.libraryaddict.Hungergames.Types.LibsProfileLookupCaller;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.bukkit.entity.Player;

public class ReflectionManager {
    private SimpleCommandMap commandMap;
    private String currentVersion;
    private boolean gameProfile = false;
    private Object propertyManager;

    public ReflectionManager() {
        try {
            commandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap")
                    .invoke(Bukkit.getServer());
            Object obj = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
            propertyManager = obj.getClass().getDeclaredMethod("getPropertyManager").invoke(obj);
            currentVersion = propertyManager.getClass().getPackage().getName();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
            gameProfile = true;
        } catch (Exception ex) {
        }
    }

    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    public Class getNmsClass(String className) {
        try {
            return Class.forName(currentVersion + "." + className);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    public Object getPropertiesConfig(String name, Object obj) {
        try {
            Properties properties = (Properties) propertyManager.getClass().getDeclaredField("properties").get(propertyManager);
            if (!properties.containsKey(name)) {
                properties.setProperty(name, "" + obj);
                savePropertiesConfig();
            }
            OptionSet options;
            Field opt = propertyManager.getClass().getDeclaredField("options");
            opt.setAccessible(true);
            options = (OptionSet) opt.get(propertyManager);
            if ((options != null) && (options.has(name)) && (!name.equals("online-mode"))) {
                return options.valueOf(name);
            }
            if (obj instanceof String)
                return properties.getProperty(name, (String) obj);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public Object grabProfileAddUUID(String playername) {
        try {
            Object minecraftServer = getNmsClass("MinecraftServer").getMethod("getServer").invoke(null);
            for (Method method : getNmsClass("MinecraftServer").getMethods()) {
                if (method.getReturnType().getSimpleName().equals("GameProfileRepository")) {
                    Object profileRepo = method.invoke(minecraftServer);
                    Object agent = Class.forName("net.minecraft.util.com.mojang.authlib.Agent").getField("MINECRAFT").get(null);
                    LibsProfileLookupCaller callback = new LibsProfileLookupCaller();
                    profileRepo
                            .getClass()
                            .getMethod("findProfilesByNames", String[].class, agent.getClass(),
                                    Class.forName("net.minecraft.util.com.mojang.authlib.ProfileLookupCallback"))
                            .invoke(profileRepo, new String[] { playername }, agent, callback);
                    return callback.getGameProfile();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean hasGameProfiles() {
        return gameProfile;
    }

    public void savePropertiesConfig() {
        try {
            propertyManager.getClass().getMethod("savePropertiesFile").invoke(propertyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendChunk(Player p, int x, int z) {
        try {
            Object obj = p.getClass().getDeclaredMethod("getHandle").invoke(p);
            List list = (List) obj.getClass().getField("chunkCoordIntPairQueue").get(obj);
            Constructor con = Class.forName(currentVersion + ".ChunkCoordIntPair").getConstructor(int.class, int.class);
            list.add(con.newInstance(x, z));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object setPropertiesConfig(String name, Object obj) {
        try {
            return propertyManager.getClass().getMethod("a", String.class, Object.class).invoke(propertyManager, name, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void setWidthHeight(Player p, float height, float width, float length) {
        try {
            Method handle = p.getClass().getMethod("getHandle");
            Class c = Class.forName(currentVersion + ".Entity");
            Field field1 = c.getDeclaredField("height");
            Field field2 = c.getDeclaredField("width");
            Field field3 = c.getDeclaredField("length");
            field1.setFloat(handle.invoke(p), (float) height);
            field2.setFloat(handle.invoke(p), (float) width);
            field3.setFloat(handle.invoke(p), (float) length);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
