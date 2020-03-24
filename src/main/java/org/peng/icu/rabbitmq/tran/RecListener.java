package org.peng.icu.rabbitmq.tran;

public interface RecListener {
    void msgRec(byte[] msg);
    void docRec(byte[] doc,String filename);
}
