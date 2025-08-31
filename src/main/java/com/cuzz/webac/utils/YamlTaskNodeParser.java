package com.cuzz.webac.utils;

import com.cuzz.webac.model.dto.TaskNode;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class YamlTaskNodeParser {


    Queue<TaskNode> queue= new LinkedList<>();
    Map<String, TaskNode> nodeMap = new HashMap<>();
    // 修改为实例方法
    public TaskNode parseYaml(InputStream yamlStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> rootMap = yaml.load(yamlStream);

        // 解析 "conversations" 节点
        Map<String, Object> conversations = (Map<String, Object>) rootMap.get("conversations");
        if (conversations == null || conversations.isEmpty()) {
            throw new IllegalArgumentException("YAML 文件中找不到 'conversations' 配置");
        }

        // 取第一个对话（默认按 key 获取）
        Map.Entry<String, Object> entry = conversations.entrySet().iterator().next();
        Map<String, Object> conversation = (Map<String, Object>) entry.getValue();

        return parseConversation(conversation, entry.getKey());
    }

    // 修改为实例方法
    private TaskNode parseConversation(Map<String, Object> conversation,String conversationId) {
        TaskNode root = new TaskNode();
        root.setType("metadata"); // 根节点（元数据）
        root.setDescription("");
        root.setId(conversationId);
        root.setDescription("对话id:conversationId,npc名称:"+conversation.get("quester"));
        // 解析 NPC 选项
        parseSection(conversation, "NPC_options", nodeMap);
        // 解析玩家选项
        parseSection(conversation, "player_options", nodeMap);
        nodeMap.keySet().forEach(System.out::println);
        // 设定根节点
        String first = (String) conversation.get("first");
        if (first != null) {
            for (String id : first.split(",")) {
                TaskNode startNode = nodeMap.get("NPC_options@"+id.trim());
                if (startNode != null) {
                    root.getSonNode().add(startNode);
                    queue.offer(startNode);
                }
            }
        }



        width();
        return root;
    }

    /**
     *  广度优先遍历
     */
    private void width(){

        while (!queue.isEmpty()){
            TaskNode poll = queue.poll();
            poll.setIsProcess(true);
            if (poll.getPointers()!=null&&!poll.getPointers().isBlank()){
                String[] pointers = poll.getPointers().split(",");
                for (String pointer : pointers) {

                    String targetType= poll.getType().equalsIgnoreCase("NPC_options")?"player_options":"NPC_options";
                    TaskNode child = nodeMap.get(targetType+"@"+pointer.trim());
                    if (child != null) {
                        if (child.getIsProcess()){
                            poll.getGoToNode().add(targetType+"@"+pointer.trim());
                        }else {
                            poll.getSonNode().add(child);
                            // 递归处理子节点
                            queue.offer(child);
                        }

                    }else {
                        poll.getGoToNode().add(targetType+"@"+pointer.trim());
                    }
                }
            }
        }

    }

    // 递归处理节点的指向关系
    private void addPointersToNode(TaskNode node, Map<String, TaskNode> nodeMap) {
//        if (node.getConditions() != null && node.getConditions().contains("!")) {
//            return; // 过滤掉未解锁的对话
//        }
        node.setIsProcess(true);
        // 获取 pointers，如果有，则递归解析
        if ( node.getPointers()!=null&&!node.getPointers().isBlank()) {
            String[] pointers = node.getPointers().split(",");
            for (String pointer : pointers) {

                String targetType= node.getType().equalsIgnoreCase("NPC_options")?"player_options":"NPC_options";
                TaskNode child = nodeMap.get(targetType+"@"+pointer.trim());
                if (child != null) {
                    if (child.getIsProcess()){
                        node.getGoToNode().add(targetType+"@"+pointer.trim());
                    }else {
                        node.getSonNode().add(child);
                        // 递归处理子节点
                        addPointersToNode(child, nodeMap);
                    }

                }
            }
        }
    }

    // 修改为实例方法
    private void parseSection(Map<String, Object> conversation, String sectionKey, Map<String, TaskNode> nodeMap) {
        Map<String, Map<String, Object>> section = (Map<String, Map<String, Object>>) conversation.get(sectionKey);
        if (section == null) {
            return;
        }

        for (Map.Entry<String, Map<String, Object>> entry : section.entrySet()) {
            String id = entry.getKey();
            Map<String, Object> data = entry.getValue();

            TaskNode node = new TaskNode();
            node.setId(id);
            node.setText((String) data.get("text"));
            node.setConditions((String) data.get("conditions"));
            node.setEvents((String) data.get("events"));
            node.setType(sectionKey);
            node.setPointers((String)data.get("pointers"));
            node.setSonNode(new ArrayList<>());
            node.setGoToNode(new ArrayList<>());

            nodeMap.put(sectionKey+"@"+id, node);
        }
    }

    // 修改为实例方法
    public void printTree(TaskNode node, String prefix) {
        System.out.println(prefix + "├── " + (node.getId() != null ? node.getId() : "ROOT") + ": " + node.getText());
        for (TaskNode child : node.getSonNode()) {
            printTree(child, prefix + "    ");
        }
    }

    // 测试时，可以通过创建实例来使用
    public static void main(String[] args) {
        InputStream yamlStream = YamlTaskNodeParser.class.getClassLoader().getResourceAsStream("dialogue.yml");
        if (yamlStream == null) {
            throw new RuntimeException("找不到 YAML 文件！");
        }

        // 创建实例
        YamlTaskNodeParser parser = new YamlTaskNodeParser();

        // 调用实例方法解析
        TaskNode root = parser.parseYaml(yamlStream);

        // 打印解析出的树形结构
        parser.printTree(root, "");
    }
}
