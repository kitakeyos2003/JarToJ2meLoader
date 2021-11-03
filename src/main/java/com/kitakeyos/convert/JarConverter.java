package com.kitakeyos.convert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import com.android.dx.command.dexer.Main;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

public class JarConverter {

    private static final File dirTmp = new File("tmp");

    public static void convert(String pathToJar, String pathConverted) throws Exception {
        FileUtils.deleteDirectory(dirTmp);
        dirTmp.mkdir();
        File srcJar = new File(pathToJar);
        Descriptor manifest = loadManifest(srcJar);
        String icon = manifest.getIcon();
        File patchedJar = new File(dirTmp, "patched.jar");
        AndroidProducer.processJar(srcJar, patchedJar);
        try {
            Main.main(new String[]{"--no-optimize", "--output=" + dirTmp.getPath() + Config.MIDLET_DEX_FILE,
                patchedJar.getAbsolutePath()});
        } catch (Throwable e) {
            throw new ConvertException("Dexing error", e);
        }
        manifest.writeTo(new File(dirTmp, Config.MIDLET_MANIFEST_FILE));
        File iconFile = new File(dirTmp, Config.MIDLET_ICON_FILE);
        ZipUtils.unzipEntry(patchedJar, icon, iconFile);
        File res = new File(dirTmp, Config.MIDLET_RES_FILE);
        patchedJar.renameTo(res);
        File destFile = new File(manifest.getName());
        dirTmp.renameTo(destFile);
        File zip = new File(pathConverted, destFile.getName() + ".zip");
        FileUtils.deleteDirectory(zip);
        ZipUtils.zip(destFile, zip);
        FileUtils.deleteDirectory(dirTmp);
        FileUtils.deleteDirectory(destFile);
        System.out.println(manifest.getName() + " completed.");
    }

    private static Descriptor loadManifest(File jar) throws Exception {
        ZipFile zip = new ZipFile(jar);
        FileHeader manifest = zip.getFileHeader(JarFile.MANIFEST_NAME);
        if (manifest == null) {
            throw new IOException("JAR not have " + JarFile.MANIFEST_NAME);
        }
        try (ZipInputStream is = zip.getInputStream(manifest)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
            byte[] buf = new byte[4096];
            int read;
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            return new Descriptor(baos.toString(), false);
        }
    }
}
