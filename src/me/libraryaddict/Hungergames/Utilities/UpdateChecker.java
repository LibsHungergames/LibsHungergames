package me.libraryaddict.Hungergames.Utilities;

import java.net.URL;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;

public class UpdateChecker {
    private String latestVersion;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void checkUpdate(String currentVersion) throws Exception {
        String version = ((CharacterData) ((Element) DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new URL("http://dev.bukkit.org/server-mods/hunger-games/files.rss").openStream())
                .getElementsByTagName("item").item(0)).getElementsByTagName("title").item(0).getFirstChild()).getData();
        if (checkHigher(currentVersion, version))
            latestVersion = version.replace("Lib's Hungergames ", "");
    }

    private boolean checkHigher(String currentVersion, String newVersion) {
        String current = toReadable(currentVersion);
        String newVers = toReadable(newVersion);
        return current.compareTo(newVers) < 0;
    }

    public String toReadable(String version) {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version.replace("Lib's Hungergames ", "").replace("v", ""));
        version = "";
        for (String s : split)
            version += String.format("%4s", s);
        return version;
    }
}
