package me.libraryaddict.Hungergames.Utilities;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

public class UpdateChecker {
    private String latestVersion;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void checkUpdate(String currentVersion) throws Exception {
        URL url = new URL("http://dev.bukkit.org/server-mods/hunger-games/files.rss");
        String title = "";
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream inputStream = url.openStream();
        if (inputStream != null) {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals("title")) {
                        event = eventReader.nextEvent();
                        title = event.asCharacters().getData();
                        continue;
                    }
                    if (event.asStartElement().getName().getLocalPart().equals("link")) {
                        event = eventReader.nextEvent();
                        event.asCharacters().getData();
                        continue;
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart().equals("item")) {
                        title = title.replace("Librarys Hungergames ", "").replace("Lib's Hungergames ", "");
                        if (checkHigher(currentVersion, title))
                            latestVersion = title;
                        break;
                    }
                }
            }
        }
    }

    private boolean checkHigher(String currentVersion, String newVersion) {
        String current = toReadable(currentVersion);
        String newVers = toReadable(newVersion);
        return current.compareTo(newVers) < 0;
    }

    public String toReadable(String version) {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version);
        version = "";
        for (String s : split)
            version += String.format("%4s", s);
        return version;
    }
}
