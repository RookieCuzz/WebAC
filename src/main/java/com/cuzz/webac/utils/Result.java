package com.cuzz.webac.utils;

public class Result<T> {

    // 状态码
    private int code;

    // 返回消息
    private String message;

    // 返回的数据
    private T data;

    // 构造函数私有化
    private Result() {
    }

    // 成功的响应，返回数据
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);  // 成功状态码
        result.setMessage("成功");
        result.setData(data);
        return result;
    }

    // 成功的响应，不返回数据
    public static <T> Result<T> ok(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);  // 成功状态码
        result.setMessage(message);
        return result;
    }

    // 失败的响应，返回错误信息
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // Getters 和 Setters
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
