package com.cuzz.webac.bot.objects.contact;

import com.cuzz.webac.bot.bot.BotAPI;
public class Friend {


    protected long user_id;


    protected String nickname;

    protected String remark;

    public long sendMsg(String msg, boolean... auto_escape) {
        return BotAPI.getInstance().sendPrivateMsg(user_id, msg, auto_escape);
    }

    public boolean delete() {
        return BotAPI.getInstance().deleteFriend(user_id);
    }

    public String getRemark() {
        return remark;
    }

    public String getNickName() {
        return nickname;
    }

    public long getUserID() {
        return user_id;
    }
}
