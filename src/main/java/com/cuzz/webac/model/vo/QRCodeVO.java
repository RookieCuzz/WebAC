package com.cuzz.webac.model.vo;


import lombok.Data;

@Data
public class QRCodeVO {

    private String content;

    private String orderId;

    private String buyer;
}
