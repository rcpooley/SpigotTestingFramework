package com.stframework.server.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;

public class PacketBuffer {

    public static int getVarIntSize(int input) {
        for (int i = 1; i < 5; ++i) {
            if ((input & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 5;
    }

    private ByteBuf buf;

    public PacketBuffer(ByteBuf wrapped) {
        this.buf = wrapped;
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public boolean release() {
        return buf.release();
    }

    public int readableBytes() {
        return buf.readableBytes();
    }

    public ByteBuf ensureWritable(int p_ensureWritable_1_) {
        return buf.ensureWritable(p_ensureWritable_1_);
    }

    public PacketBuffer writeVarInt(int input) {
        while ((input & -128) != 0) {
            this.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        this.writeByte(input);
        return this;
    }

    public ByteBuf writeByte(int i) {
        return buf.writeByte(i);
    }

    public ByteBuf writeBytes(ByteBuf buf, int i, int j) {
        return this.buf.writeBytes(buf, i, j);
    }
}
