package com.cuzz.webac.service;


import com.cuzz.webac.dao.GameInfoDao;
import com.cuzz.webac.model.doo.GamePlayerDayCountDo;
import com.cuzz.webac.model.doo.GamePlayerHourCountDo;
import com.cuzz.webac.model.vo.ServerPointInfoVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameInfoService {

    @Resource
    GameInfoDao gameInfoDao;



    public GamePlayerDayCountDo getPlayerDayCountByDay(String dayTime){

        return null;

    }

    public List<ServerPointInfoVO> getServerRuntimeInfo(String period){
        return gameInfoDao.getServerPoints();
    }

    public void getMachineInfo(String period){

        Runtime runtime = Runtime.getRuntime();

        // JVM 总内存
        long totalMemory = runtime.totalMemory();
        // JVM 空闲内存
        long freeMemory = runtime.freeMemory();
        // JVM 已使用内存
        long usedMemory = totalMemory - freeMemory;
        // JVM 最大可用内存
        long maxMemory = runtime.maxMemory();

        System.out.println("JVM 总内存: " + totalMemory / 1024 / 1024 + " MB");
        System.out.println("JVM 空闲内存: " + freeMemory / 1024 / 1024 + " MB");
        System.out.println("JVM 已使用内存: " + usedMemory / 1024 / 1024 + " MB");
        System.out.println("JVM 最大可用内存: " + maxMemory / 1024 / 1024 + " MB");
    }

    public boolean updatePlayerDayCount(String dayTime, GamePlayerHourCountDo gamePlayerHourCountDo){


        return true;
    }
}
