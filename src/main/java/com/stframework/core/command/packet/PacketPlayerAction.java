package com.stframework.core.command.packet;

import java.util.Arrays;

public class PacketPlayerAction extends CommandPacket {

    public static PacketPlayerAction parse(String str) {
        String[] spl = str.split(":");
        String[] args = Arrays.copyOfRange(spl, 2, spl.length);
        return new PacketPlayerAction(spl[0], spl[1], args);
    }

    public static final String ACTION_DISCONNECT = "disconnect";
    public static final String ACTION_SENDMSG = "sendmsg";

    private String playerName;
    private String action;
    private String[] actionArgs;

    public PacketPlayerAction(String playerName) {
        this.playerName = playerName;
    }

    public PacketPlayerAction(String playerName, String action, String[] actionArgs) {
        this.playerName = playerName;
        this.action = action;
        this.actionArgs = actionArgs;
    }

    public PacketPlayerAction actionDisconnect() {
        action = ACTION_DISCONNECT;
        return this;
    }

    public PacketPlayerAction sendMessage(String message) {
        action = ACTION_SENDMSG;
        actionArgs = new String[]{message};
        return this;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getAction() {
        return action;
    }

    public String[] getActionArgs() {
        return actionArgs;
    }

    @Override
    public String encode() {
        StringBuilder builder = new StringBuilder(playerName).append(":").append(action);
        if (actionArgs != null) {
            for (String arg : actionArgs) {
                builder.append(":").append(arg);
            }
        }
        return builder.toString();
    }
}
