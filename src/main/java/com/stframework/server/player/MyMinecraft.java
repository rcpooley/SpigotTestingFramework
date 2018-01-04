package com.stframework.server.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;

public class MyMinecraft extends Minecraft {

    private Session session;

    public MyMinecraft(GameConfiguration gameConfig) {
        super(gameConfig);
    }

    @Override
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}