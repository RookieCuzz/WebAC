package com.cuzz.webac.dao;

import com.cuzz.webac.mapper.LogItemDOMapper;
import com.cuzz.webac.model.vo.ServerPointInfoVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class GameInfoDao {

    @Resource
    LogItemDOMapper logItemDOMapper;

    public List<ServerPointInfoVO> getServerPoints(){

        List<ServerPointInfoVO> serverPointInfoVOS = logItemDOMapper.selectServerPointInfo();
        return serverPointInfoVOS;
    };

}
