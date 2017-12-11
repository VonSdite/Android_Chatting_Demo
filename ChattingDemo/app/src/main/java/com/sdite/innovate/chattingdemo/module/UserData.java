package com.sdite.innovate.chattingdemo.module;

import org.litepal.crud.DataSupport;

/**
 * Created by Sdite on 17/12/9.
 */

public class UserData extends DataSupport {
    private String userName;
    private String password;

    public String getUserName(){
        return userName;
    }

    public void setUserName(String name){
        userName = name;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
