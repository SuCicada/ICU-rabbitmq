package org.peng.icu.rabbitmq.event;

import org.peng.icu.rabbitmq.tran.RecListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DefaultRecListener implements RecListener {
    private void saveFile(byte[] fileByte, String savePath) {
        try {
            int FILENAME_SIZE = 1024;
            byte[] f = Arrays.copyOfRange(fileByte, 0, 1024);
            byte[] fileData = Arrays.copyOfRange(fileByte, 1024, fileByte.length);
            String fileName = new String(f, UTF_8).trim();
            System.out.println(fileName);
            File saveDir = new File(savePath);
            if(!saveDir.exists()){
                saveDir.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new File(savePath + "/" + fileName));

            out.write(fileData);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void msgRec(byte[] msg) {
        System.out.println(new String(msg));
    }

    @Override
    public void docRec(byte[] doc) {
        String savePath = "out";
        saveFile(doc,savePath);
    }
}
