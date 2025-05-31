package org.tiktuzki.telegram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JniLoader {
    public static void loadTdLib() throws IOException {
        loadNativeLibraryFromResource();
    }

    public static void loadNativeLibraryFromResource() throws IOException {
        loadNativeLibraryFromResource("/libtdjni.dylib", "libtdjni", ".dylib");
    }

    public static void loadNativeLibraryFromResource(
            String resourcePath,
            String libPrefix,
            String libSuffix
    ) throws IOException {
        InputStream inputStream = JniLoader.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource " + resourcePath + " not found");
        }
        File temp = File.createTempFile(libPrefix, libSuffix);
        temp.deleteOnExit();
        try (InputStream input = inputStream;
             FileOutputStream output = new FileOutputStream(temp)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        System.load(temp.getAbsolutePath());
    }
}