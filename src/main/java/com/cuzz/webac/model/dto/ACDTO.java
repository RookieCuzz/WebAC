package com.cuzz.webac.model.dto;

import lombok.Data;

@Data
public class ACDTO {

    private String username;
    private String password;
    private String email;
    private String verificationCode;

}
