package com.stframework.core.command;

import com.stframework.core.command.packet.CommandPacket;

public interface PacketHandler {

    void handlePacket(CommandSocket socket, CommandPacket packet);

}
