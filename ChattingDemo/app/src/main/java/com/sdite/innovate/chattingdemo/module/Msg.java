package com.sdite.innovate.chattingdemo.module;

import org.litepal.crud.DataSupport;

/**
 * Created by Sdite on 17/12/9.
 */

public class Msg extends DataSupport {

    public static final int TYPE_RECEIVED = 0;

    public static final int TYPE_SENT = 1;

    public static final int TYPE_SENT_PIC = 2;

    public static final int TYPE_RECEIVED_PIC = 3;

    private String content;

    private int type;

    private int isWho;


    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public void setIsWho(int isWho) {
        this.isWho = isWho;
    }

    public int getIsWho() {
        return isWho;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }



    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

}
