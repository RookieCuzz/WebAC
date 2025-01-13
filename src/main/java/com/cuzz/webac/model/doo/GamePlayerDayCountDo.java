package com.cuzz.webac.model.doo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;


@Builder
@Data
@Entity(value = "dayInfo",discriminator="GamePlayerDayCountDo")
@NoArgsConstructor  // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class GamePlayerDayCountDo {
    @Id
    private String timeDay;
    private String createTime;
    private Integer onlineMaxCount;
    //各服人数
    private HashMap<Integer,GamePlayerHourCountDo> perHourOnline;
}
