package com.cuzz.webac.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequestOrderInfoDTO {

    //付款者UUID
    private String buyerName;

    private String payment;

    private String description;
    private Integer productAmount;

    private List<String> productList;

    private String userUUID;

    //元
    private Double money;

    private String userName;

    private String address;


}
