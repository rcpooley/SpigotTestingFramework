package com.stframework.client;

import com.stframework.core.command.packet.PacketPlayerAction;

public class ClientPlayer {

    private CommandClient client;
    private String name;

    public ClientPlayer(CommandClient client, String name) {
        this.client = client;
        this.name = name;
    }

    public void sendMessage(String message) {
        client.getSocket().sendPacket(new PacketPlayerAction(name).sendMessage(message));
    }

    public void disconnect() {
        client.getSocket().sendPacket(new PacketPlayerAction(name).actionDisconnect());
    }
}
