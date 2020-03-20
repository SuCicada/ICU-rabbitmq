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
 * @ClassName Send
 * @Date 2020/3/15 17:46
 * @Author pengyifu
 */
public class Send {
    static String EXCHANGE_NAME = "logs.topic";
    static String TYPE = "topic";
    static String sendqueueName = RabbitUtil.getsendQueueName();
    static String recqueueName = RabbitUtil.getrecQueueName();
    static String routingKey = "file.jpg";

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

    /**
     * 发送文本字符串
     *
     * @param constr
     */
    static public void sendMSG(String constr) {
        Protocol protocol = new Protocol();
        protocol.setFlagmsg("MD".getBytes());
        protocol.setContent(constr.getBytes());

        //send(constr.getBytes());
        send(protocol);
    }

    /**
     * 发送文档对象
     *
     * @param filename
     */
    static public void sendDOC(String filename) {
        Protocol protocol = new Protocol();
        byte[] bytefile = getFileBytes(filename);
        protocol.setFlagmsg("DD".getBytes());
        protocol.setContent(bytefile);
        send(protocol);
    }

    static public void send(Protocol protocol) {
        send(protocol.toBytes());
    }

    static public void send(byte[] constr) {
        try {
            Channel channel = RabbitUtil.buildChannel();

            boolean durable = true;
            channel.queueDeclare(sendqueueName, durable, false, false, null);

            channel.basicPublish(
                    "",
                    sendqueueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    constr);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

//        try {
//            Channel channel = RabbitUtil.buildChannel();
//
//            channel.exchangeDeclare(EXCHANGE_NAME, TYPE);
//
//
//            channel.queueBind(sendqueueName, EXCHANGE_NAME, routingKey);
//
//            byte[] data = constr;
//
//            channel.basicPublish(
//                    EXCHANGE_NAME,
//                    routingKey,
//                    MessageProperties.PERSISTENT_TEXT_PLAIN,
//                    data);
//        } catch (TimeoutException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }



    static private String date() {
        return new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
    }

    public static void main(String[] args) {

        System.out.printf("send 启动");
        System.out.println("程序在"+sendqueueName+"通道上发送");


        // 发送
        new Thread() {
          int model = 0; /// model: 0 综合模式，可以发文字，可以发文档，需要在前面加标记，m： f:
                         /// 1 单一文本模式，只能发文字信息
                         /// 2 单一文档模式，只能发文档
            String[] ps = {">","m:>","f:>"};
            String[] pss ={"综合模式","文字聊天模式","文档模式"};

            @Override
            public void run() {
                while (true) {
                    try {
                        byte by[] = new byte[10];
                        System.out.print(ps[model]);
                        Scanner sc = new Scanner(System.in);
                        String bbs = sc.nextLine();

                        if (bbs.equals("?")){
                            System.out.println("exit 退出应用");
                            System.out.println("quit 退出当前模式，返回前综合模式");
                            System.out.println("file 进入文档模式");
                            System.out.println("msg  进入文本聊天模式");
                            continue;
                        }
                        if (bbs.equals("exit")) {
                            System.exit(0);
                            break;
                        }
                        if (bbs.equals("quit")) {
                            if (model > 0) {
                                model = 0;
                                System.out.println("现在是"+pss[model]);
                                System.out.println("程序在"+sendqueueName+"通道上发送");
                            }else{
                                System.out.println("不能再退了");
                            }
                            continue;
                        }else if (StringUtils.equals(bbs, "file")) {
                            // 进入发送文档模式
                            model = 2;
                            System.out.println("现在是"+pss[model]);
                            System.out.println("程序在"+sendqueueName+"通道上发送");

                            continue;
                        }else if (StringUtils.equals(bbs, "msg")) {
                            // 进入发送文档模式
                            model = 1;
                            System.out.println("现在是"+pss[model]);
                            System.out.println("程序在"+sendqueueName+"通道上发送");

                            continue;
                        } 

                        if (model == 0){
                            String b = bbs.substring(0,1);
                            if (!"mf".contains(b)){
                                System.out.println("文本错误，缺少标记");
                                continue;
                            }
                        }else if (model == 1){
                            bbs = "m:" + bbs;
                        }else if (model == 2){
                            bbs = "f:" + bbs;
                        }
                        String[] subbs = bbs.split(":");
                        String sub1 = subbs[0];
                        String sub2 = subbs[1];
                        if (StringUtils.equals(sub1, "f") || StringUtils.equals(sub1, "file")) {
                            // 发送文档
                            sendDOC(sub2);
                        } else if (StringUtils.equals(sub1, "m") || StringUtils.equals(sub1, "msg")) {
                            // 发送字符串
                            sendMSG(sub2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.run();
    }
}

