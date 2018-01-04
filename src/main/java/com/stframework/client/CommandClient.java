package com.stframework.client;

import com.stframework.Main;
import com.stframework.core.Util;
import com.stframework.core.command.CommandSocket;
import com.stframework.core.command.PacketHandler;
import com.stframework.core.command.packet.CommandPacket;
import com.stframework.core.command.packet.PacketNewPlayer;
import com.stframework.core.command.packet.PacketServerCommand;
import com.stframework.core.command.packet.PacketSingleAction;

import java.io.IOException;
import java.net.Socket;

public class CommandClient {

    private CommandSocket socket;
    private volatile CommandPacket lastPacket;

    public CommandClient() {
        this("localhost", Main.DEFAULT_PORT);
    }

    public CommandClient(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            this.socket = new CommandSocket(socket, new ClientPacketHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientPacketHandler implements PacketHandler {
        @Override
        public void handlePacket(CommandSocket socket, CommandPacket packet) {
            lastPacket = packet;
        }
    }

    private CommandPacket waitForPacket() {
        return waitForPacket(-1);
    }

    private CommandPacket waitForPacket(long timeout) {
        long start = System.currentTimeMillis();
        while (lastPacket == null && (System.currentTimeMillis() - start < timeout || timeout == -1));
        CommandPacket tmp = lastPacket;
        lastPacket = null;
        return tmp;
    }

    public CommandSocket getSocket() {
        return socket;
    }

    public void cleanup() {
        socket.sendPacket(new PacketSingleAction(PacketSingleAction.CLEANUP));
        PacketSingleAction resp = (PacketSingleAction) Util.assertClass(waitForPacket(), PacketSingleAction.class);

        if (resp != null && resp.getAction().equals(PacketSingleAction.CLEANUP_COMPLETE)) {
            return;
        }
        System.out.println("Cleanup failed");
    }

    public ClientPlayer createPlayer(String name) {
        socket.sendPacket(new PacketNewPlayer(name));
        PacketSingleAction resp = (PacketSingleAction) Util.assertClass(waitForPacket(), PacketSingleAction.class);

        if (resp != null && resp.getAction().equals(PacketSingleAction.PLAYER_CREATED)) {
            return new ClientPlayer(this, name);
        }
        System.out.println("Failed to create player with name " + name);
        return null;
    }

    public void executeCommand(String cmd) {
        socket.sendPacket(new PacketServerCommand(cmd));
    }

    public void disconnect() {
        try {
            socket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
