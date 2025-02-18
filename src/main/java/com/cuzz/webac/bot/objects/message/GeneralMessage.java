package com.cuzz.webac.bot.objects.message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralMessage {

    String  type;
    String sender;
    String token;
    String reciver;

    String content;

    @Override
    public String toString() {
        return "GeneralMessage{" +
                "type='" + type + '\'' +
                ", sender='" + sender + '\'' +
                ", token='" + token + '\'' +
                ", reciver='" + reciver + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public GeneralMessage(){};
    public GeneralMessage(JsonObject pack){
        //消息来源自QQ的逻辑
        Gson gson=new Gson();
        if (!pack.has("raw_message")){
            return;
        }
        JsonElement roleJson = pack.get("raw_message");
         if (!roleJson.toString().contains("[CQ:at,qq=3882996485,name=")){
             this.setType("none");
             return;
         }
        Pattern pattern = Pattern.compile("(?<=name=)[^,\\]]+");
        Matcher matcher = pattern.matcher(roleJson.getAsString());
        String roleName="不知道先生";
        if (matcher.find()) {
            roleName=matcher.group();
            this.setType("airequest");
        }else {
            this.setType("none");
            return;
        }
        JsonArray plainMsgList = pack.getAsJsonArray("message");
        StringBuilder stringBuilder = new StringBuilder();
        plainMsgList.forEach(
                str->{
                    if (str.toString().contains("\"type\":\"text\",\"data\"")){
                        // 解析 JSON
                        JsonObject jsonObject = gson.fromJson(str, JsonObject.class);

                        // 获取 data 对象
                        JsonObject dataObject = jsonObject.getAsJsonObject("data");

                        // 提取 text 字段的值
                        String text = dataObject.get("text").getAsString();
                        stringBuilder.append(text+"。");
                    }
                }
        );
        this.setContent(stringBuilder.toString());
        this.setReciver(roleName);
        this.setSender(pack.get("group_id").getAsString()+"@"+pack.get("user_id").getAsLong());
        this.setToken("123456");
    }

}
