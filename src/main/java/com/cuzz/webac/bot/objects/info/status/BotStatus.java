package com.cuzz.webac.bot.objects.info.status;

public class BotStatus {
    protected boolean online;
    protected BotStatistic stat;

    public boolean isOnline() {
        return online;
    }

    public BotStatistic getStat() {
        return stat;
    }
}
