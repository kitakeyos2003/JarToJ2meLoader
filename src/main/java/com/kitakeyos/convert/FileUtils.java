package com.kitakeyos.convert;

import java.io.File;

public class FileUtils {

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null && listFiles.length != 0) {
                for (File file : listFiles) {
                    deleteDirectory(file);
                }
            }
        }
        if (!dir.delete() && dir.exists()) {
            System.out.println("Can't delete file: " + dir);
        }
    }

}
