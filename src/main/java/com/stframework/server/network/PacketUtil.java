package com.stframework.server.network;

import com.stframework.server.network.packet.SPacketEnableCompression;
import com.stframework.server.network.packet.SPacketLoginSuccess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PacketUtil {

    private static Map<Integer, Map<Integer, String>> dontCare;

    private static Map<Integer, String> get(int state) {
        if (!dontCare.containsKey(state)) dontCare.put(state, new HashMap<>());
        return dontCare.get(state);
    }

    static {
        dontCare = new HashMap<>();
        dontCare.get(0).put(35, "i-ub-i-2ub-s_16-bool");
    }

    private static class NumStr {
        public int num;
        public String str;

        public NumStr(int num, String str) {
            this.num = num;
            this.str = str;
        }

        public static NumStr parse(String str) {
            int i = 1;
            int j = 1;
            try {
                for (j = 1; j < str.length(); j++) {
                    i = Integer.parseInt(str.substring(0, j));
                }
            } catch (Exception e) {
                j--;
            }
            return new NumStr(i, str.substring(j));
        }
    }

    public static Packet getPacket(int state, int id) {
        System.out.println("Parsing packet [" + state + ", " + id + "]");

        /*if (get(state).containsKey(id)) {
            return new Packet() {
                @Override
                public void readPacketData(PacketBuffer buf) throws IOException {
                    String[] key = get(state).get(id).split("-");
                    for (String val : key) {
                        NumStr ns = NumStr.parse(val);
                        for (int i = 0; i < ns.num; i++) {
                            if (ns.str.startsWith("s_")) {
                                int ml = Integer.parseInt(ns.str.substring(2));
                                buf.readString(ml);
                                continue;
                            }

                            switch (ns.str) {
                                case "i":
                                    buf.readInt();
                                    break;
                                case "ub":
                                    buf.readUnsignedByte();
                                    break;
                                case "bool":
                                    buf.readBoolean();
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void writePacketData(PacketBuffer buf) throws IOException {
                }
            };
        }*/

        if (state == 0) {
            if (id == 35) {
                //return new SPacketJoinGame();
            }
        } else if (state == 2) {
            if (id == 2) {
                return new SPacketLoginSuccess();
            } else if (id == 3) {
                return new SPacketEnableCompression();
            }
        }

        return null;
    }

    public static int getPacketID(Packet packet) {
        return 0;
    }
}
