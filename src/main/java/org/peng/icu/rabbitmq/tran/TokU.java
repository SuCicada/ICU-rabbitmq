package org.peng.icu.rabbitmq.tran;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;
import org.apache.commons.lang3.StringUtils;
//import utils.RabbitUtil;
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
public class TokU {
    static String EXCHANGE_NAME = "logs.topic";
    static String TYPE = "topic";
    static String sendqueueName = RabbitUtil.getsendQueueName();
    static String recqueueName = RabbitUtil.getrecQueueName();
    static String routingKey = "file.jpg";

      @Deprecated  private void saveFile(byte[] fileByte, String savePath) {
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


    @Deprecated private byte[] getFileBytes(String filePath) {
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
    @Deprecated private void sendMSG(String constr) {
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
    @Deprecated private void sendDOC(String filename) {
        Protocol protocol = new Protocol();
        byte[] bytefile = getFileBytes(filename);
        protocol.setFlagmsg("DD".getBytes());
        protocol.setContent(bytefile);
        send(protocol);
    }

    @Deprecated private void send(Protocol protocol) {
        send(protocol.toBytes());
    }

    @Deprecated private void send(byte[] constr) {
        try {
            Channel channel = RabbitUtil.buildChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, TYPE);


            channel.queueBind(sendqueueName, EXCHANGE_NAME, routingKey);

            byte[] data = constr;

            channel.basicPublish(
                    EXCHANGE_NAME,
                    routingKey,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    data);
        } catch (TimeoutException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @Deprecated private void rec() {
        rec(new DefaultRecListener());
    }

    @Deprecated private void rec(RecListener recListener) {

        Channel channel = null;
        try {
            channel = RabbitUtil.buildChannel();

            boolean durable = true;
            channel.queueDeclare(recqueueName, durable, false, false, null);

            Channel finalChannel = channel;
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
                    //System.out.println("--> ↘ " + message);
                    recListener.msgRec(message);
                } else if (flag.equals("DD")) {
                    String savePath = "out";
                    byte[] filecontext = protocol.getContent();
                    //saveFile(filecontext,savePath);
                    recListener.docRec(filecontext);
                }

                finalChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };

            boolean autoAck = false;
            channel.basicConsume(
                    recqueueName,
                    autoAck,
                    deliverCallback,
                    consumerTag -> {
                        System.out.println("what.........................");
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Deprecated private String date() {
        return new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
    }

    public static void main(String[] args) {


        String userdir = System.getProperties().getProperty("user.dir");
        System.out.println(userdir);

        if (args.length > 0){
            String arg = args[0];
            if (arg.equals("send")){
                Send.main(new String[]{""});
            }else if (arg.equals("rec"))
            {
                Rec.main(new String[]{""});
            }
        }

        System.out.printf("程序启动完成");

    }
}

