package org.peng.icu.rabbitmq.tran;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;
import org.apache.commons.lang3.StringUtils;
import org.peng.Parse;
import org.peng.Protocol;
import org.peng.icu.rabbitmq.event.DefaultRecListener;
import org.peng.icu.rabbitmq.utils.RabbitUtil;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @ClassName Rec
 * @Date 2020/3/15 15:58
 * @Author pengyifu
 */
public class Rec {

    static String EXCHANGE_NAME = "logs.topic";
    static String TYPE = "topic";
    static String recqueueName = RabbitUtil.getrecQueueName();
    static String routingKey = "rec1";

    static private void saveFile(byte[] fileByte, String savePath) {
        try {
            int FILENAME_SIZE = 1024;
            byte[] f = Arrays.copyOfRange(fileByte, 0, 1024);
            byte[] fileData = Arrays.copyOfRange(fileByte, 1024, fileByte.length);
            String fileName = new String(f, UTF_8).trim();
            System.out.println(fileName);
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


    static private byte[] getFileBytes(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream in = new FileInputStream(file);
            int FILENAME_SIZE = 1024;
            int fileSize = in.available();
            byte[] data = new byte[FILENAME_SIZE + fileSize];

            byte[] fileName = file.getName().getBytes();
            System.arraycopy(fileName, 0, data, 0, fileName.length);

            in.read(data, FILENAME_SIZE, fileSize);
            in.close();

            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public void rec() {
        rec(new DefaultRecListener());
    }

    static public void rec(RecListener recListener) {
        try {
            Channel channel = RabbitUtil.buildChannel();

            boolean durable = true;
            channel.queueDeclare(recqueueName, durable, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Protocol protocol = null;
                Parse parse = new Parse();
                //String message = new String(delivery.getBody(), "UTF-8");
                byte[] msg = delivery.getBody();
                parse.setProtos(msg);
                protocol = parse.check();
                if (protocol == null) return;
                byte[] message = null;
                String flag = new String(protocol.getFlagmsg());

                if (flag.equals("MD")) {
                    message = protocol.getContent();
                    recListener.msgRec(message);
                } else if (flag.equals("DD")) {
                    //String savePath = "out";
                    byte[] filecontext = protocol.getContent();
                    recListener.docRec(filecontext);
                }
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            };

            boolean autoAck = false;
            channel.basicConsume(
                    recqueueName,
                    autoAck,
                    deliverCallback,
                    consumerTag -> {
                        System.out.println("what");
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
//
//        Channel channel = null;
//        try {
//            channel = RabbitUtil.buildChannel();
//
//            boolean durable = true;
//            channel.queueDeclare(recqueueName, durable, false, false, null);
//
//            Channel finalChannel = channel;
//            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                Protocol protocol = null;
//                Parse parse = new Parse();
//                //String message = new String(delivery.getBody(), "UTF-8");
//                byte[] msg = delivery.getBody();
//                parse.setProtos(msg);
//                protocol = parse.check();
//                if (protocol == null) return;
//                byte[] message = null;
//                String flag = new String(protocol.getFlagmsg());
//
//                if (flag.equals("MD")) {
//                    message = protocol.getContent();
//                    //System.out.println("--> ↘ " + message);
//                    recListener.msgRec(message);
//                } else if (flag.equals("DD")) {
//                    String savePath = "out";
//                    byte[] filecontext = protocol.getContent();
//                    //saveFile(filecontext,savePath);
//                    recListener.docRec(filecontext);
//                }
//
//                finalChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//            };
//
//            boolean autoAck = false;
//            channel.basicConsume(
//                    recqueueName,
//                    autoAck,
//                    deliverCallback,
//                    consumerTag -> {
//                        System.out.println("what.........................");
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//    }

    static private String date() {
        return new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
    }

    public static void main(String[] args) {
        System.out.printf("rec启动");
        System.out.println("程序在"+recqueueName+"通道上接受");
        rec();
        // 接受
        new Thread() {
            @Override
            public void run() {
                try {
                    byte by[] = new byte[10];
                    System.out.print("> ");
                    Scanner sc = new Scanner(System.in);
                    String bbs = sc.nextLine();

                    if (bbs.equals("exit")) {
                        System.exit(0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.run();
    }
}

