package com.cuzz.common.gameserverinfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class GameServerInfoMessage implements Serializable {
    private static final long serialVersionUID = 8849649245827190181L;


    //全服在线人数
    private int onlineSumCount;

    //各服人数
    private HashMap<String,Integer> perServerOnline;


}
