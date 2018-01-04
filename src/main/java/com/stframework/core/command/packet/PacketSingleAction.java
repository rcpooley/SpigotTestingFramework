package com.stframework.core.command.packet;

public class PacketSingleAction extends CommandPacket {

    public static PacketSingleAction parse(String str) {
        return new PacketSingleAction(str);
    }

    public static final String CLEANUP = "cleanup";
    public static final String CLEANUP_COMPLETE = "cleanupComplete";
    public static final String PLAYER_CREATED = "playerCreated";

    private String action;

    public PacketSingleAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String encode() {
        return action;
    }
}
