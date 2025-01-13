package com.cuzz.webac.utils;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SpawnCheckNumber {
    /**
     *     验证码数字
     */

    private String number;
    /**
     *     验证码长度
     */
    private Integer length=4;

    public SpawnCheckNumber() {
        StringBuffer number = new StringBuffer();
        while (number.length() != this.length) {
            number.append( (new Random().nextInt(10) ));
        }
        this.number= String.valueOf(number);
    }

    public String getNumber(){
        return this.number;
    }
}

