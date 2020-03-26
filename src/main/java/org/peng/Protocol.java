/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.peng;

import org.apache.commons.lang.ArrayUtils;

/**
 * 通讯协议v0.2
 * 客户端与服务端通讯 或 客户端与客户端通讯
 * @author peng
 */
public class Protocol {
    private final byte[] flaghead = {'$', '$', 'I', 'C', 'U', '0', '0', '2'};
    private byte[] flagmsg = new byte[]{' ',' '};
    private byte[] dataLen = new byte[]{' ',' ',' ',' ',' ',' ',' ',' ',' ',' '};
    private byte[] fileName = new byte[]{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '}; // 16
    private byte[] content;
    private byte[] user = new byte[]{' ',' ',' ',' ',' ',' ',' ',' '};
    private byte[] pwd = new byte[]{' ',' ',' ',' ',' ',' ',' ',' '};
    private final byte[] theEnd = "ok".getBytes();


    public byte[] toBytes() {
        byte[] b1;
        b1 = ArrayUtils.addAll(getFlaghead(), getFlagmsg());
        b1 = ArrayUtils.addAll(b1, getDataLen());
        b1 = ArrayUtils.addAll(b1, getFileName());
        b1 = ArrayUtils.addAll(b1, getContent());
        b1 = ArrayUtils.addAll(b1, getUser());
        b1 = ArrayUtils.addAll(b1, getPwd());
        b1 = ArrayUtils.addAll(b1, getTheEnd());
        return b1;
    }

    public byte[] getFlaghead() {
        return flaghead;
    }


    public byte[] getFlagmsg() {
        return flagmsg;
    }

    public void setFlagmsg(byte[] flagmsg) {
        this.flagmsg = flagmsg;
    }

    public byte[] getDataLen() {
        return dataLen;
    }

    public void setDataLen(byte[] dataLen) {
        this.dataLen = dataLen;
    }

    public byte[] getFileName() {
        return fileName;
    }

    public void setFileName(byte[] fileName) {
        if (fileName.length != 16){
            byte[] filename2 = new byte[]{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '};//16
            System.arraycopy(fileName,0,filename2,0,fileName.length);
            this.fileName = filename2;
        }else{
            this.fileName = fileName;
        }

    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
        int leng = content.length;
        //this.setDataLen(String.valueOf(leng).getBytes());
        byte[] b1 = this.strToArray(String.valueOf(leng), 10);
        this.setDataLen(b1);
    }

    public byte[] getUser() {
        return user;
    }

    public void setUser(byte[] user) {
        this.user = user;
    }

    public byte[] getPwd() {
        return pwd;
    }

    public void setPwd(byte[] pwd) {
        this.pwd = pwd;
    }

    public byte[] getTheEnd() {
        return theEnd;
    }

    // 将字符串转换成8个长度的char数组
    private byte[] strCharArray(String str) {
        byte[] cs = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        byte[] strs = str.getBytes();
        for (int i = 0; i < strs.length; i++) {
            cs[i] = strs[i];
        }
        return cs;
    }

    /**
     * 将字符串转换成byte数组，数组长度len
     *
     * @param str
     * @param len
     * @return byte[]
     */
    private byte[] strToArray(String str, int len) {
        byte[] cs = new byte[len];
        int i = 0;
        while (i < cs.length) {
            cs[i] = '0';
            i++;
        }
        byte[] strs = str.getBytes();
        for (i = len - strs.length; i < len; i++) {
            cs[i] = strs[i - len + strs.length];
        }
        return cs;
    }
}
