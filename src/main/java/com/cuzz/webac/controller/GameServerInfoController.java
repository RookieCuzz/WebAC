package com.cuzz.webac.controller;

import com.cuzz.webac.model.vo.ServerPointInfoVO;
import com.cuzz.webac.service.GameInfoService;
import com.cuzz.webac.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/game")
//@ResponseBody
public class GameServerInfoController {
    @Resource
    GameInfoService gameInfoService;
    @GetMapping("/test")
    public boolean insertTest(){
//        gameInfoService.getMachineInfo();
//        gameInfoService.updatePlayerDayCount("11",null);
        return true;
    }

    @CrossOrigin
    @GetMapping("/GetProxies")
    public Result getProxies(){
        ArrayList<String> proxies = new ArrayList<>();
        proxies.add("集群X");
        proxies.add("集群A");
        proxies.add("集群Y");
        proxies.add("集群C");
        return Result.ok(proxies);
    }

    @CrossOrigin
    @GetMapping("/getProxyById")
    public Result getProxyById(@RequestParam("name")  String id){
        ArrayList<String> servers = new ArrayList<>();
        servers.add("子服1");
        servers.add("代理服核心");
        servers.add("子服2");
        servers.add("子服3");
        servers.add("子服4");
        servers.add("子服5");
        servers.add("子服6");
        return Result.ok(servers);
    }

    @CrossOrigin
    @GetMapping("/getServerInfoById")
    @ResponseBody
    public Result getServerInfoById(){
        List<ServerPointInfoVO> list = gameInfoService.getServerRuntimeInfo("test");
        return Result.ok(list);
    }



}
