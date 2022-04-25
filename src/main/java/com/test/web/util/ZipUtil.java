package com.test.web.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public void zip(String filePath, String zipFileName) {

        File sourceFile = new File(filePath);
        File[] fileLists = sourceFile.listFiles();

        FileOutputStream fos = null;
        FileInputStream fis = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(filePath + "/" + zipFileName);
            zos = new ZipOutputStream(fos);
            for (File file : fileLists) {
                if (!file.getName().contains(".jpg")) continue;

                fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int size = 0;
                while ((size = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, size);
                }

                fis.close();
                zos.closeEntry();
            }

            zos.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
                if (zos != null) zos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
