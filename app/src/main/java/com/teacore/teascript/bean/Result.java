package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**结果实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-6
 */

@XStreamAlias("result")
public class Result implements Serializable{

    @XStreamAlias("code")
    private int code;

    @XStreamAlias("message")
    private String message;

    //code等于1，代表结果ok
    public boolean OK() {
        return code == 1;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

