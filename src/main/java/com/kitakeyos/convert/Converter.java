package com.kitakeyos.convert;

import java.io.File;

public class Converter {

    public static void main(String[] args) {
        try {
            File convert = new File("input");
            File[] list = convert.listFiles();
            for (File file : list) {
                if (file.getName().endsWith(".jar")) {
                    JarConverter.convert(file.getPath(), "output");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
