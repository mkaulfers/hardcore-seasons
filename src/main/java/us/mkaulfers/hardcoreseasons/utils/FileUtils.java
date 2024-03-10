package us.mkaulfers.hardcoreseasons.utils;

import java.io.File;

public class FileUtils {
    public static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    deleteRecursive(subFile);
                }
            }
        }
        file.delete();
    }
}
