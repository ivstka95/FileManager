package com.example.ivan.filemanager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Иван on 08.02.2017.
 */

public class FileWriterReader {
    public static boolean remove(String s){
        Set<String> files = read();
        boolean removed = files.remove(s);
        try (FileWriter writer = new FileWriter("/sdcard/favorites/favorites.txt", false)) {
            for (String file : files) {
                writer.write(file);
                writer.append('\n');
            }
            writer.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return removed;
    }
    public static void write(String s) {
        Set<String> files = read();
        files.add(s);
        try (FileWriter writer = new FileWriter("/sdcard/favorites/favorites.txt", false)) {
            for (String file : files) {
                writer.write(file);
                writer.append('\n');
            }
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public static Set<String> read() {
        Set<String> files = new HashSet<String>();
        String s = "";
        try (FileReader reader = new FileReader("/sdcard/favorites/favorites.txt")) {
            int c;
            while ((c = reader.read()) != -1) {
                if (((char)c) != '\n')
                    s += (char) c;
                else {
                    files.add(s);
                    s = "";
                }
            }
            reader.close();
        } catch (IOException ex) {
        }
        return files;
    }
}
