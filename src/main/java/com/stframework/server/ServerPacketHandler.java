package com.stframework.server;

import com.stframework.core.command.CommandSocket;
import com.stframework.core.command.packet.*;
import com.stframework.core.command.PacketHandler;
import com.stframework.server.player.FakePlayer;

public class ServerPacketHandler implements PacketHandler {

    private CoreServer server;

    public ServerPacketHandler() {
        server = CoreServer.instance;
    }

    @Override
    public void handlePacket(CommandSocket socket, CommandPacket packet) {
        if (packet instanceof PacketServerCommand) {
            server.getServerWrap().write(((PacketServerCommand)packet).getCommand());
        } else if (packet instanceof PacketNewPlayer) {
            PacketNewPlayer pack = (PacketNewPlayer) packet;
            server.getPlayerHandler().createPlayer(pack.getName());
            socket.sendPacket(new PacketSingleAction(PacketSingleAction.PLAYER_CREATED));
        } else if (packet instanceof PacketSingleAction) {
            String action = ((PacketSingleAction)packet).getAction();
            if (action.equals(PacketSingleAction.CLEANUP)) {
                server.getPlayerHandler().destroyAll();

                new Thread(() -> {
                    server.getServerWrap().write("reload");
                    server.getServerWrap().waitForOutput("CONSOLE: Reload complete.");
                    socket.sendPacket(new PacketSingleAction(PacketSingleAction.CLEANUP_COMPLETE));
                }).start();
            }
        } else if (packet instanceof PacketPlayerAction) {
            PacketPlayerAction pack = (PacketPlayerAction) packet;
            String action = pack.getAction();
            if (action.equals(PacketPlayerAction.ACTION_DISCONNECT)) {
                server.getPlayerHandler().destroyPlayer(pack.getPlayerName());
            } else if (action.equals(PacketPlayerAction.ACTION_SENDMSG)) {
                FakePlayer fp = server.getPlayerHandler().getPlayer(pack.getPlayerName());
                if (fp != null) {
                    fp.sendMessage(pack.getActionArgs()[0]);
                }
            }
        } else if (packet instanceof PacketOutputWatch) {
            PacketOutputWatch pack = (PacketOutputWatch) packet;
            new Thread(() -> {
                server.getServerWrap().waitForOutput(pack.getOutput());
                socket.sendPacket(packet);
            }).start();
        }
    }
}
