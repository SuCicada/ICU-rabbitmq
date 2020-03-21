package org.peng.icu.rabbitmq.face;

import org.peng.icu.rabbitmq.tran.Rec;
import org.peng.icu.rabbitmq.tran.RecListener;
import org.peng.icu.rabbitmq.tran.Send;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ICUform {
    private JPanel panel1;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton button1;
    private String sendQue,recQue;

    public ICUform() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton(e);
            }
        });
    }

    private void sendButton(ActionEvent e) {
        String str = this.textField1.getText();
        Send.sendMSG(str);
        this.textArea1.append(str + "\n");
        this.textField1.setText("");
    }

    private void msgRec(String str) {
        this.textArea1.append(str);
    }

    private void docRec(byte[] cont) {

    }

    private void post1() {
        new Thread() {
            @Override
            public void run() {
                Rec.rec(new RecListener() {
                    @Override
                    public void msgRec(byte[] msg) {
                        String str = new String(msg);
                        ICUform.this.msgRec(str);
                    }

                    @Override
                    public void docRec(byte[] doc) {

                    }
                });
            }
        }.run();
    }

    private void post2() {
        String recQue = Rec.getRecqueueName();
        String sendQue = Send.getSendqueueName();
        this.recQue = recQue;
        this.sendQue = sendQue;
        this.textArea1.setText("你当前在"+sendQue+"通道发送数据");
        this.textArea1.append("\n");
        this.textArea1.append("你当前在"+recQue+"通道接受数据");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("I See You");
        ICUform icUform = new ICUform();
        frame.setContentPane(icUform.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        icUform.post1();
        icUform.post2();
    }
}
