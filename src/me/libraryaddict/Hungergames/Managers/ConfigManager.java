package me.libraryaddict.Hungergames.Managers;

import lombok.Data;
import me.libraryaddict.Hungergames.Configs.*;

@Data
public class ConfigManager {
    private FeastConfig feastConfig = new FeastConfig();
    private LoggerConfig loggerConfig = new LoggerConfig();
    private MainConfig mainConfig = new MainConfig();
    private TranslationConfig translationsConfig = new TranslationConfig();

    public void loadConfigs() {
        for (BaseConfig config : new BaseConfig[] { mainConfig, translationsConfig, loggerConfig, feastConfig }) {
            try {
                config.load();
                config.loadConfig();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
