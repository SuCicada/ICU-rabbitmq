
package org.peng;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 验证并解析收到的数据包
 *
 * @author peng
 */
public class Parse {
    private byte[] protos;
    private String type;
    private Protocol protocol = new Protocol();

    public Parse() {
    }

    ;

    public Parse(byte[] protocol) {
        this.protos = protocol;
    }

    /**
     * 验证数据是否符合协议格式
     *
     * @return null, 验证失败 ；Protocol01,验证通过
     */
    public Protocol check() {
        int datalen = 0;
        if (this.protos == null) return null;
        if (this.protos.length < 20) return null;

        byte[] flaghead1 = ArrayUtils.subarray(protos, 0, 8);  //标识头部

        byte[] flagmsg = ArrayUtils.subarray(protos, 8, 10);   // 信息部分的标识头
        byte[] dataLen = ArrayUtils.subarray(protos, 10, 20);    // 信息部分的内容部分
        datalen = Integer.parseInt(new String(dataLen));
        byte[] filename = ArrayUtils.subarray(protos, 20, 36);  //空行
        byte[] content = ArrayUtils.subarray(protos, 36, datalen + 36);  // 信息的主体内容部分
        byte[] username = ArrayUtils.subarray(protos, protos.length - 34, protos.length - 18); //用户名 or ip
        byte[] password = ArrayUtils.subarray(protos, protos.length - 18, protos.length - 2); //口令 or ip：port
        byte[] theEnd = ArrayUtils.subarray(protos, protos.length - 2, protos.length);


        if (!StringUtils.equals("$$ICU002",new String(flaghead1))) {
            return null;
        }

        String flag = new String(flagmsg);
        if ("MD".equals(flag)) {
            this.type = "MD";
        } else if ("DD".equals(flag)) {
            this.type = "DD";
        } else if ("TO".equals(flag)) {
            this.type = "TO";
        } else if ("RD".equals(flag)) {
            this.type = "RD";
        } else if ("SC".equals(flag)) {
            this.type = "SC";
        } else if ("SN".equals(flag)) {
            this.type = "SN";
        } else if ("CT".equals(flag)) {
            this.type = "CT";
        } else if ("FU".equals(flag)) {
            this.type = "FU";
        } else {
            this.type = null;
            return null;
        }

        protocol.setFlagmsg(flagmsg);
        protocol.setDataLen(dataLen);
        protocol.setContent(content);
        protocol.setFileName(filename);
        protocol.setUser(username);
        protocol.setPwd(password);

        return protocol;
    }




    public byte[] getProtos() {
        return protos;
    }

    public void setProtos(byte[] protos) {
        this.protos = protos;
    }

}
