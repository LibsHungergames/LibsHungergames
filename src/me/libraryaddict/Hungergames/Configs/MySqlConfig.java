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
    private boolean useUUIDs = true;
    private boolean mysqlEnabled = false;

    public MySqlConfig() {
        super("mysql");
        if (new File("plugins/LibsHungergames/config.yml").exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/LibsHungergames/config.yml"));
            if (config.contains("mysqlEnabled")) {
                mysqlEnabled = config.getBoolean("mysqlEnabled");
                config.set("mysqlEnabled", null);
                try {
                    config.save(new File("plugins/LibsHungergames/config.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
