package com.stframework.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    public ByteBuf writeBytes(byte[] bytes) {
        return this.buf.writeBytes(bytes);
    }

    public PacketBuffer writeString(String string) {
        byte[] abyte = string.getBytes(StandardCharsets.UTF_8);

        if (abyte.length > 32767) {
            throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + 32767 + ")");
        } else {
            this.writeVarInt(abyte.length);
            this.writeBytes(abyte);
            return this;
        }
    }

    public String readString(int maxLength) {
        int i = this.readVarInt();

        if (i > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        } else if (i < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = this.toString(this.readerIndex(), i, StandardCharsets.UTF_8);
            this.readerIndex(this.readerIndex() + i);

            if (s.length() > maxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            } else {
                return s;
            }
        }
    }

    public int readerIndex() {
        return this.buf.readerIndex();
    }

    public ByteBuf readerIndex(int i) {
        return this.buf.readerIndex(i);
    }

    public ByteBuf writeShort(int i) {
        return this.buf.writeShort(i);
    }

    public String toString(int i, int j, Charset cs) {
        return this.buf.toString(i, j, cs);
    }

    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    public int readInt() {
        return this.buf.readInt();
    }

    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    public boolean readBoolean() {
        return this.buf.readBoolean();
    }
}
