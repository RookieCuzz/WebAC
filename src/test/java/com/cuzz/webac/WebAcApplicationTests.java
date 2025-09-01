package com.cuzz.webac;

import com.cuzz.webac.model.dto.TaskNode;
import com.cuzz.webac.utils.YamlTaskNodeParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import com.fasterxml.jackson.databind.ObjectMapper;

// 修改为不启动Web环境的测试，避免端口冲突
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class YamlTaskNodeParserTest {

    @Test
    void testParseYaml() throws Exception {
        // 使用绝对路径加载文件
        File file = new File("C:\\Users\\PC\\Desktop\\WebAC\\src\\main\\resources\\towncraft\\conversations\\task_1.yml");
        InputStream yamlStream = new FileInputStream(file);

        // 确保文件不为 null
        assertThat(yamlStream).isNotNull();

        // 解析 YAML 文件
        YamlTaskNodeParser parser = new YamlTaskNodeParser();
        TaskNode root = parser.parseYaml(yamlStream);

        // 确保解析后的节点不为 null
        assertThat(root).isNotNull();
        assertThat(root.getType()).isEqualTo("metadata");

        // 打印树结构
        System.out.println("打印任务树结构：");
        parser.printTree(root, "");

//        // 检查某个特定的节点
//        TaskNode welcomeNode = root.getSonNode().stream()
//                .filter(n -> "welcome".equals(n.getId()))
//                .findFirst().orElse(null);
//
//        assertThat(welcomeNode).isNotNull();
//        assertThat(welcomeNode.getText()).contains("嗨! %player%!");

        // **保存树结构到 JSON 文件**
        saveTreeToJson(root, "C:\\Users\\PC\\Desktop\\WebAC\\task_tree2.json");
    }

    /**
     * 保存任务树到 JSON 文件
     */
    private void saveTreeToJson(TaskNode root, String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), root);
            System.out.println("任务树已保存为 JSON：" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("保存 JSON 失败！");
        }
    }
}
