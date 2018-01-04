package com.stframework.server.player;

import net.minecraft.util.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerHandler {

    private Map<String, FakePlayer> players;

    public PlayerHandler() {
        players = new HashMap<>();
    }

    public Session createPlayer(String name) {
        FakePlayer fp = new FakePlayer(name);
        players.put(name, fp);
        return null;
    }

    public FakePlayer getPlayer(String name) {
        return players.get(name);
    }

    public void destroyPlayer(String name) {
        FakePlayer fp = players.get(name);
        if (fp == null) return;
        fp.disconnect();
        players.remove(name);
    }

    public void destroyAll() {
        new ArrayList<>(players.keySet()).forEach(this::destroyPlayer);
    }
}
