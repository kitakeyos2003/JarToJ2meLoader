package com.kitakeyos.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

public class AndroidProducer {

    private static byte[] instrument(final byte[] classFile, String classFileName) throws IllegalArgumentException {
        ClassReader cr = new ClassReader(classFile);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new AndroidClassVisitor(cw);
        if (!cr.getClassName().equals(classFileName)) {
            throw new IllegalArgumentException("Class name does not match path");
        }
        cr.accept(cv, ClassReader.SKIP_DEBUG);

        return cw.toByteArray();
    }

    public static void processJar(File jarInputFile, File jarOutputFile) throws Exception {
        HashMap<String, byte[]> resources = new HashMap<>();
        InputStream zis;
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jarOutputFile.toPath()))) {
            ZipFile zip = new ZipFile(jarInputFile);
            List<FileHeader> list = zip.getFileHeaders();
            for (FileHeader header : list) {
                // Some zip entries have zero length names
                if (header.getFileNameLength() > 0 && !header.isDirectory()) {
                    zis = zip.getInputStream(header);
                    String name = header.getFileName();
                    byte[] inBuffer = IOUtils.toByteArray(zis);
                    resources.put(name, inBuffer);
                    zis.close();
                }
            }

            for (String name : resources.keySet()) {
                byte[] inBuffer = resources.get(name);
                byte[] outBuffer = inBuffer;
                try {
                    if (name.endsWith(".class")) {
                        outBuffer = instrument(inBuffer, name.replace(".class", ""));
                    }
                    zos.putNextEntry(new ZipEntry(name));
                    zos.write(outBuffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
