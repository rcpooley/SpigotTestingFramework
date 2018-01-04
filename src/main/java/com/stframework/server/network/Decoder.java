package com.stframework.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws IOException, InstantiationException, IllegalAccessException, Exception {
        if (byteBuf.readableBytes() != 0) {
            PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
            int packetID = packetbuffer.readVarInt();
            Packet packet = Packet.getPacket(packetID);

            if (packet == null) {
                throw new IOException("Bad packet id " + packetID);
            } else {
                packet.readPacketData(packetbuffer);

                if (packetbuffer.readableBytes() > 0) {
                    throw new IOException("Packet " + "/" + packetID + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + packetID);
                } else {
                    list.add(packet);
                }
            }
        }
    }
}
