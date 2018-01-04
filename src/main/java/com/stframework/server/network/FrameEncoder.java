package com.stframework.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FrameEncoder extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf1) throws Exception
    {
        int i = byteBuf.readableBytes();
        int j = PacketBuffer.getVarIntSize(i);

        if (j > 3)
        {
            throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
        }
        else
        {
            PacketBuffer packetbuffer = new PacketBuffer(byteBuf1);
            packetbuffer.ensureWritable(j + i);
            packetbuffer.writeVarInt(i);
            packetbuffer.writeBytes(byteBuf, byteBuf.readerIndex(), i);
        }
    }
}
