package com.cuzz.webac.model.dto;


import lombok.Data;

@Data
public class SmsSendResultDTO {

    private boolean success;
    private int statusCode;
    private String body;

    private String code;
    public static SmsSendResultDTO of(boolean success, int statusCode, String body) {
        SmsSendResultDTO r = new SmsSendResultDTO();
        r.success = success;
        r.statusCode = statusCode;
        r.body = body;
        return r;
    }
    // getters
    public boolean isSuccess() { return success; }

}
