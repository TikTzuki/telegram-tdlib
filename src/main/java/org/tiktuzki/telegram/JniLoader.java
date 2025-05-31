package org.tiktuzki.telegram;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JniLoader {
    public static boolean load() throws Exception {

        // Load the os-dependent library from the jar file
        String dbrNativeLibraryName = System.mapLibraryName("tdjni");
//        if (dbrNativeLibraryName != null && dbrNativeLibraryName.endsWith("dylib")) {
//            dbrNativeLibraryName = dbrNativeLibraryName.replace("dylib", "jnilib");
//        }

        String dbrNativeLibraryPath = "/org/tiktuzki/telegram/native";
        if (Utils.isWindows()) {
            dbrNativeLibraryPath = "/org/tiktuzki/telegram/native/win";
        } else if (Utils.isLinux()) {
            dbrNativeLibraryPath = "/org/tiktuzki/telegram/native/linux";
        } else if (Utils.isMac()) {
            dbrNativeLibraryPath = "/org/tiktuzki/telegram/native/macos";
        }

        if (JniLoader.class.getResource(dbrNativeLibraryPath + "/" + dbrNativeLibraryName) == null) {
            throw new Exception("Error loading native library: " + dbrNativeLibraryPath + "/" + dbrNativeLibraryName);
        }

        // Temporary library folder
        String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();

        // Extract resource files
        return extractResourceFiles(dbrNativeLibraryPath, dbrNativeLibraryName, tempFolder);
    }

    static boolean extractResourceFiles(String dbrNativeLibraryPath, String dbrNativeLibraryName, String tempFolder) throws IOException {
        String[] filenames = null;
        if (Utils.isWindows()) {
            filenames = new String[]{};
        } else if (Utils.isLinux()) {
            filenames = new String[]{};
        } else if (Utils.isMac()) {
            filenames = new String[]{"libtdjni.dylib"};
        }

        boolean ret = true;

        for (String file : filenames) {
            ret &= extractAndLoadLibraryFile(dbrNativeLibraryPath, file, tempFolder);
        }

        return ret;
    }

    private static boolean extractAndLoadLibraryFile(String libFolderForCurrentOS, String libraryFileName,
                                                     String targetFolder) {
        String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;

        String extractedLibFileName = libraryFileName;
        File extractedLibFile = new File(targetFolder, extractedLibFileName);

        try {
            if (extractedLibFile.exists()) {
                // test md5sum value
                String md5sum1 = md5sum(JniLoader.class.getResourceAsStream(nativeLibraryFilePath));
                String md5sum2 = md5sum(new FileInputStream(extractedLibFile));

                if (md5sum1.equals(md5sum2)) {
                    return loadNativeLibrary(targetFolder, extractedLibFileName);
                } else {
                    // remove old native library file
                    boolean deletionSucceeded = extractedLibFile.delete();
                    if (!deletionSucceeded) {
                        throw new IOException(
                                "failed to remove existing native library file: " + extractedLibFile.getAbsolutePath());
                    }
                }
            }

            InputStream reader = JniLoader.class.getResourceAsStream(nativeLibraryFilePath);
            FileOutputStream writer = new FileOutputStream(extractedLibFile);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (true) {
                assert reader != null;
                if ((bytesRead = reader.read(buffer)) == -1) break;
                writer.write(buffer, 0, bytesRead);
            }

            writer.close();
            reader.close();

            if (!System.getProperty("os.name").contains("Windows")) {
                try {
                    Runtime.getRuntime().exec(new String[]{"chmod", "755", extractedLibFile.getAbsolutePath()})
                            .waitFor();
                } catch (Throwable ignored) {
                }
            }

            return loadNativeLibrary(targetFolder, extractedLibFileName);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }

    }

    private static synchronized boolean loadNativeLibrary(String path, String name) {
        File libPath = new File(path, name);
        if (libPath.exists()) {
            try {
                System.load(new File(path, name).getAbsolutePath());
                return true;
            } catch (UnsatisfiedLinkError e) {
                System.err.println(e);
                return false;
            }

        } else
            return false;
    }

    static String md5sum(InputStream input) throws IOException {

        try (BufferedInputStream in = new BufferedInputStream(input)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            DigestInputStream digestInputStream = new DigestInputStream(in, digest);
            while (digestInputStream.read() >= 0) {
            }
            ByteArrayOutputStream md5out = new ByteArrayOutputStream();
            md5out.write(digest.digest());
            return md5out.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available: " + e);
        }
    }


//    public static void loadTdLib() throws IOException {
//        loadNativeLibraryFromResource();
//    }
//
//    public static void loadNativeLibraryFromResource() throws IOException {
//        loadNativeLibraryFromResource("libtdjni.dylib", "libtdjni", ".dylib");
//    }
//
//    public static void loadNativeLibraryFromResource(
//            String resourcePath,
//            String libPrefix,
//            String libSuffix
//    ) throws IOException {
//        InputStream inputStream = JniLoader.class.getResourceAsStream(resourcePath);
//        if (inputStream == null) {
//            throw new IOException("Resource " + resourcePath + " not found");
//        }
//        File temp = File.createTempFile(libPrefix, libSuffix);
//        temp.deleteOnExit();
//        try (InputStream input = inputStream;
//             FileOutputStream output = new FileOutputStream(temp)) {
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = input.read(buffer)) != -1) {
//                output.write(buffer, 0, bytesRead);
//            }
//        }
//        System.load(temp.getAbsolutePath());
//    }
}