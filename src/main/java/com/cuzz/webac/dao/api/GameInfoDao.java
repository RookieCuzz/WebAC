package com.cuzz.webac.dao.api;


import com.cuzz.webac.model.doo.GamePlayerDayCountDo;
import lombok.extern.slf4j.Slf4j;

public interface GameInfoDao {

    public void connect();

    public void close();



    public Boolean updateDayInfo(GamePlayerDayCountDo gamePlayerDayCountDo);
    public GamePlayerDayCountDo getPlayerDayCountByDay(String dayTime);
}

