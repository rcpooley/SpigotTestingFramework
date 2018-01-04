package com.stframework.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

public class Encoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws IOException, Exception
    {
        PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
        packetbuffer.writeVarInt(PacketUtil.getPacketID(packet));
        packet.writePacketData(packetbuffer);
    }
}
