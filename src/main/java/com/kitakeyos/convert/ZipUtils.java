package com.kitakeyos.convert;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ZipUtils {

    private static final int BUFFER_SIZE = 8096;

    public static void unzipEntry(File srcZip, String name, File dst) throws Exception {
        ZipFile zip = new ZipFile(srcZip);
        FileHeader entry = zip.getFileHeader(name);
        if (entry == null) {
            throw new IOException("Entry '" + name + "' not found in zip: " + srcZip);
        }
        try (BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(entry));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE)) {
            byte[] data = new byte[BUFFER_SIZE];
            int read;
            while ((read = bis.read(data)) != -1) {
                bos.write(data, 0, read);
            }
        }
    }

    public static void zip(File src, File dest) throws Exception {
        ZipFile zip = new ZipFile(dest);
        zip.addFolder(src, new ZipParameters());
    }
}
