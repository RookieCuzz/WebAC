package com.cuzz.webac.model.doo;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;


@Builder
@Data
@Embedded
@NoArgsConstructor  // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class GamePlayerHourCountDo {


    private String createTime;
    private Integer hour;
    private Integer onlineSumCount;

    //各服人数
    private HashMap<String,Integer> perServerOnline;
}
