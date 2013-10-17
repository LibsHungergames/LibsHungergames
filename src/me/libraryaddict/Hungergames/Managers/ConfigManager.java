package me.libraryaddict.Hungergames.Managers;

import lombok.Data;
import me.libraryaddict.Hungergames.Configs.*;

@Data
public class ConfigManager {
    private FeastConfig feastConfig = new FeastConfig();
    private LoggerConfig loggerConfig = new LoggerConfig();
    private MainConfig mainConfig = new MainConfig();
    private MySqlConfig mySqlConfig = new MySqlConfig();
    private TranslationConfig translationsConfig = new TranslationConfig();
    private WinnersConfig winnersConfig = new WinnersConfig();

    public void loadConfigs() {
        for (BaseConfig config : new BaseConfig[] { mainConfig, translationsConfig, loggerConfig, feastConfig, mySqlConfig,
                winnersConfig }) {
            try {
                config.load();
                config.loadConfig();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
