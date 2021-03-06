package com.itdr.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itdr.config.ConstCode;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private Integer status;
    private T data;
    private String msg;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
//构造方法
    private ServerResponse(Integer status) {
        this.status = status;
    }

    private ServerResponse(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(Integer status, T data, String msg) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    private ServerResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(String msg) {
        this.status = ConstCode.DEFAULT_FAIL;
        this.msg = msg;
    }


    public static <T> ServerResponse successRS() {
        return new ServerResponse(ConstCode.DEFAULT_SUCCRSS);
    }

    public static <T> ServerResponse successRS(String msg) {
        return new ServerResponse(ConstCode.DEFAULT_SUCCRSS,msg);
    }

    //成功时的返回状态码，数据
    public static <T> ServerResponse successRS(T data) {
        return new ServerResponse(ConstCode.DEFAULT_SUCCRSS,data);
    }

    public static <T> ServerResponse successRS(Integer status, T data) {
        return new ServerResponse(status,data);
    }
    //成功的时候传入状态码、数据、信息
    public static <T> ServerResponse successRS(Integer status,T data, String msg) {
        return new ServerResponse(ConstCode.DEFAULT_SUCCRSS,data, msg);
    }
    //失败时的返回方法
    public static <T> ServerResponse defeatedRS(Integer errorCode, String errorMessage) {
        return new ServerResponse(errorCode, errorMessage);
    }

    public static <T> ServerResponse defeatedRS(String errorMessage) {
        return new ServerResponse(ConstCode.DEFAULT_FAIL,errorMessage);
    }

    @JsonIgnore
//让success不在json序列化结果之中
    public boolean isSuccess() {
        return this.status == ConstCode.DEFAULT_SUCCRSS;
    }
}
