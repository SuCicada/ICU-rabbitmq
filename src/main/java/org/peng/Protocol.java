/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.peng;

import org.apache.commons.lang.ArrayUtils;

/**
 * 通讯协议01
 * 客户端与服务端通讯 或 客户端与客户端通讯
 * <p>
 * 说明	协议内容	长度
 * 标识头部	$$FICU01	8
 * 序号	xxxxxxxx	8	链接服务器时，服务器分配的序号
 * 信息标识	MD999999	8	表示数据包是一个文字包，长度是999999
 * DD999999	8	表示数据包是一个文档包，长度是999999
 * HJ000000      8       标出心跳包，长度是000000,内容空
 * RD999999	8	请求发送文档数据
 * RCsn/d/m	8	发送文档内容，第n次发送/本地发送文档的长度/文档总长度
 * CT000000       8      表示有新来的要加入好友列表，内容里是新人的ip和port; 格式 ip:port
 * FU000000       8      表示向服务器索取好友,内容为空，服务器会话也是FU，表示返回好友列表，内容是好友列表。ip:port;ip:port;...
 * SN             8      请求登录
 * <p>
 * 空行		8
 * 内容	aaaaaaaaaa		如果是文字包，内容部分就是具体的文字，如果是文档包，
 * 内容部分就是文档的文件类型：文件名称，
 * 1例如：doc:病毒一出论.doc  。 jepg:蝙蝠全家照.jpg
 * 用户名      xxxxxxxx          8   如果是登录请求，这里回填写用户名口令
 * 口令        xxxxxxxx          8   否则，这里的值无效
 * 结束	ok
 *
 * @author peng
 */
public class Protocol {
    private final byte[] flaghead = {'$', '$', 'I', 'C', 'U', '0', '0', '2'};
    private byte[] index = new byte[]{' ',' ',' ',' ',' ',' ',' ',' '};
    private byte[] flagmsg = new byte[]{' ',' '};
    private byte[] dataLen = new byte[]{' ',' ',' ',' ',' ',' '};
    private byte[] spaces = new byte[]{' ',' ',' ',' ',' ',' ',' ',' '};
    private byte[] content;
    private byte[] user = new byte[]{' ',' ',' ',' ',' ',' ',' ',' '};
    private byte[] pwd = new byte[]{' ',' ',' ',' ',' ',' ',' ',' '};
    private final byte[] theEnd = "ok".getBytes();


    public byte[] toBytes() {
        byte[] b1;
        b1 = ArrayUtils.addAll(getFlaghead(), getIndex());
        b1 = ArrayUtils.addAll(b1, getFlagmsg());
        b1 = ArrayUtils.addAll(b1, getDataLen());
        b1 = ArrayUtils.addAll(b1, getSpaces());
        b1 = ArrayUtils.addAll(b1, getContent());
        b1 = ArrayUtils.addAll(b1, getUser());
        b1 = ArrayUtils.addAll(b1, getPwd());
        b1 = ArrayUtils.addAll(b1, getTheEnd());
        return b1;
    }

    public byte[] getFlaghead() {
        return flaghead;
    }

    public byte[] getIndex() {
        return index;
    }

    public void setIndex(byte[] index) {
        this.index = index;
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

    public byte[] getSpaces() {
        return spaces;
    }

    public void setSpaces(byte[] spaces) {
        this.spaces = spaces;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
        int leng = content.length;
        //this.setDataLen(String.valueOf(leng).getBytes());
        byte[] b1 = this.strToArray(String.valueOf(leng), 6);
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
