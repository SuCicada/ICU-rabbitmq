package org.peng.icu.rabbitmq.event;

import org.peng.icu.rabbitmq.tran.RecListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DefaultRecListener implements RecListener {
    private void saveFile(byte[] fileByte, String savePath, String filename) {
        try {
            byte[] fileData = fileByte;
            String fileName = filename;
            if (fileName == null) {
                fileName = System.currentTimeMillis() + "noName";
                System.out.println("收到一个没有名称的文档,随机命名为: " + fileName);
            } else {
                System.out.println("收到一个文档 : " + fileName);
            }
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
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
    public void docRec(byte[] doc, String filename) {
        String savePath = "out";
        saveFile(doc, savePath, filename);
    }
}
