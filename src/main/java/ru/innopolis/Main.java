package ru.innopolis;

import java.io.File;

public class Main {
    private static final String OUTPUT_DIRECTORY = "C:\\soft\\textii";

    public static void main(String[] args) {
        File folder = new File(OUTPUT_DIRECTORY);
        File[] folderEntries = folder.listFiles();
        //чтение каждого файла в отдельном потоке
        for (File entry : folderEntries) {
            if (!entry.isDirectory()) {
                ReaderThread thread = new ReaderThread(entry);
                thread.start();
            }
        }
    }
}
