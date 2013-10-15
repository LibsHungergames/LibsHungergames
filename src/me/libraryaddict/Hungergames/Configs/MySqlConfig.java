package me.libraryaddict.Hungergames.Configs;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MySqlConfig extends BaseConfig {

    private String mysql_database = "database";
    private String mysql_host = "localhost";
    private String mysql_password = "password";
    private String mysql_username = "root";

    public MySqlConfig() {
        super("mysql");
    }

}
