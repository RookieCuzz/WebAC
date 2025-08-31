package com.cuzz.webac.model.dto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class TaskNode {

    //描述
    String description;
    Boolean leaf;
    //id
    String id;
    //文本内容
    String text;
    //对话条件
    String conditions;
    //触发事件
    String events;
    //npc节点还是玩家节点  或者元数据根节点 一颗树只有一颗元数据根节点(无id text events conditions)
    String type;

    Boolean isProcess=false;

    String pointers = "";
    List<TaskNode> sonNode=new ArrayList<>();


    //已经存在节点直接goto 例如祖先和其他树的节点  因为可能是其他树所以其结构并未知故用其id标识
    List<String> goToNode= new ArrayList<>();

}
