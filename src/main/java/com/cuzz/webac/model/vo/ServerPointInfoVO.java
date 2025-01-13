package com.cuzz.webac.model.vo;

import lombok.Data;

@Data

public class ServerPointInfoVO {

    // 时间点
    String timeBucket;
    //系统总内存
    String systemTotalMemory;

    //服务器的分配内存
    String processMemory;

    //平均服务器使用内存
    String avgUsedMemory;
    //服务器名字
    String serverName;
    // 平均服务器cpu占用
    String avgProcessCpuLoad;
    // 平均系统cpu占用
    String avgSystemCpuLoad;

}
