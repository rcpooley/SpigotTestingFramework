package com.stframework.server.network;

import java.util.UUID;

public class GameProfile {

    private UUID uuid;
    private String name;

    public GameProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
