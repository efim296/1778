package ru.innopolis;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReaderThread extends Thread {
    private File inputFile;

    public ReaderThread(File inputFile) {
        this.inputFile = inputFile;
    }

    @Override
    public void run() {
        //чтение
        Map<String, String> messagesMap = new ConcurrentHashMap<>();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(inputFile), "UTF-8"))) {
//            System.out.println(file.getName());
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
                //ищем первое вхождение пробела - разделителя целевого каталога получателя и текста для получателя
                int index = inputLine.indexOf(" ");
                String path = inputLine.substring(0, index).concat("/").concat(inputFile.getName());
                String message = inputLine.substring(index + 1);
                //записываем получателя и сообщение в мап, если его не существует
                if (messagesMap.get(path) == null) {
                    messagesMap.put(path, message);
                } else {
                    // в противном случае прикрепляем сообщение к получателю
                    String msg = messagesMap.get(path);
                    messagesMap.put(path, message.concat(";").concat(msg));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //создание
        Map.Entry entry;
        Iterator iterator = messagesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            entry = (Map.Entry) iterator.next();
            //создание файла
            File outputFile = new File(entry.getKey().toString());
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //запись в файл
            try (Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), "UTF-8"))) {
                out.write(entry.getValue().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}