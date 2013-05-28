package me.libraryaddict.Hungergames.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class MapLoader {

    public static File convertToFile(File path, String[] mapPath) {
        for (String string : mapPath) {
            if (string.equalsIgnoreCase(".."))
                path = path.getParentFile();
            else
                path = new File(path.toString() + "/" + string + "/");
        }
        return path;
    }

    public static void loadMap(File mapDir, File dest) {
        List<File> maps = new ArrayList<File>();
        if (dest.exists())
            for (File file : dest.listFiles())
                if (file.isDirectory()) {
                    maps.add(file);
                }
        if (maps.size() > 0) {
            Collections.shuffle(maps, new Random());
            File toLoad = maps.get(0);
            copy(toLoad, dest);
            System.out.print(String.format(HungergamesApi.getTranslationManager().getLoggerSucessfullyLoadedMap(),
                    toLoad.getName()));
        } else
            System.out.print(String.format(HungergamesApi.getTranslationManager().getLoggerNoMapsFound(), mapDir.toString()));
    }

    public static void copyFile(File source, File destination) throws IOException {
        if (source.getName().equalsIgnoreCase("uid.dat"))
            return;
        if (destination.isDirectory())
            destination = new File(destination, source.getName());
        FileInputStream input = new FileInputStream(source);
        copyFile(input, destination);
    }

    public static void copyFile(InputStream input, File destination) throws IOException {
        OutputStream output = null;
        output = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int bytesRead = input.read(buffer);
        while (bytesRead >= 0) {
            output.write(buffer, 0, bytesRead);
            bytesRead = input.read(buffer);
        }
        input.close();
        output.close();
    }

    public static void copy(File from, File dest) {
        if (from.isFile()) {
            try {
                copyFile(from, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            for (File f : from.listFiles())
                copy(f, dest);
    }

    public static void clear(File file) {
        if (file.isFile())
            file.delete();
        else
            for (File f : file.listFiles())
                clear(f);
    }

}
