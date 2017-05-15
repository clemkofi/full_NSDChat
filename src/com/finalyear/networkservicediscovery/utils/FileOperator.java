package com.finalyear.networkservicediscovery.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by KayO on 15/05/2017.
 */
public class FileOperator {
    private static FileOperator ourInstance = new FileOperator();

    public static FileOperator getInstance() {
        return ourInstance;
    }

    private FileOperator() {
    }

    public String readFile(String path, String fileName, Charset encoding) throws IOException {
        byte[] encoded;
        if (fileExists(path, fileName + ".txt")) {
            encoded = Files.readAllBytes(Paths.get(path + fileName + ".txt"));
            return new String(encoded, encoding);
        } else {
            //create file
            File folder = new File(path);
            File f = new File(path + fileName + ".txt");

            folder.mkdirs();
            f.createNewFile();
            encoded = Files.readAllBytes(Paths.get(path + fileName + ".txt"));
            return new String(encoded, encoding);

        }
    }

    //possibly change to boolean
    public void writeFile(String path, String newText) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            String data = newText;
            File file = new File(path + ".txt");
            // if file doesn't exists, then create it
            if (!fileExists(path)) {
                file.createNewFile();
            }
            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(data);
            System.out.println("Written to storage file");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean fileExists(String path, String fileName) {
        File file = new File(path + fileName);
        return file.exists();
    }

    private boolean fileExists(String fullPath) {
        File file = new File(fullPath);
        return file.exists();
    }
}
