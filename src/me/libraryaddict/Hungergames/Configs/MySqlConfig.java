package me.libraryaddict.Hungergames.Configs;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MySqlConfig extends BaseConfig {

    private String mysql_database = "database";
    private String mysql_host = "localhost";
    private String mysql_password = "password";
    private String mysql_username = "root";
    private boolean mysqlEnabled = false;
    private boolean useUUIDs = true;

    public MySqlConfig() {
        super("mysql");
        File file = new File("plugins/LibsHungergames/config.yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (config.contains("mysqlEnabled")) {
                mysqlEnabled = config.getBoolean("mysqlEnabled");
                config.set("mysqlEnabled", null);
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
