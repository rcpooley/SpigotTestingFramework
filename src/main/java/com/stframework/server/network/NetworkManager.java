package com.stframework.server.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.stframework.server.network.packet.SPacketEnableCompression;
import com.stframework.server.network.packet.SPacketLoginSuccess;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import net.minecraft.network.NettyCompressionDecoder;
import net.minecraft.network.NettyCompressionEncoder;

import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {

    private static final EventLoopGroup GROUP = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());

    public static final AttributeKey<Integer> STATE_ATTRIBUTE_KEY = AttributeKey.valueOf("protocol");

    public static NetworkManager connect(InetAddress address, int port) {
        NetworkManager manager = new NetworkManager();

        new Bootstrap().group(GROUP).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.config().setOption(ChannelOption.TCP_NODELAY, true);

                channel.pipeline()
                        .addLast("timeout", new ReadTimeoutHandler(30))
                        .addLast("splitter", new FrameDecoder())
                        .addLast("decoder", new Decoder())
                        .addLast("prepender", new FrameEncoder())
                        .addLast("encoder", new Encoder())
                        .addLast("packet_handler", manager);
            }
        }).channel(NioSocketChannel.class).connect(address, port).syncUninterruptibly();
        return manager;
    }

    private Channel channel;
    private Queue<Packet> packetQueue = Queues.newConcurrentLinkedQueue();
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        super.channelActive(context);
        this.channel = context.channel();
        this.setConnectionState(2);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        System.out.println("Got packet " + packet);
        if (packet instanceof SPacketEnableCompression) {
            setCompressionThreshold(((SPacketEnableCompression)packet).getCompressionThreshold());
        } else if (packet instanceof SPacketLoginSuccess) {
            setConnectionState(0);
        }
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public void sendPacket(Packet packet) {
        if (isChannelOpen()) {
            flushOutboundQueue();
            dispatchPacket(packet);
        } else {
            readWriteLock.writeLock().lock();

            try {
                packetQueue.add(packet);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    private void flushOutboundQueue() {
        if (!isChannelOpen()) return;

        readWriteLock.readLock().lock();

        try {
            while (!packetQueue.isEmpty()) {
                dispatchPacket(packetQueue.poll());
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private void dispatchPacket(Packet packet) {
        System.out.println("Sending packet " + packet);
        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet);
        } else {
            channel.eventLoop().execute(() -> NetworkManager.this.channel.writeAndFlush(packet));
        }
    }

    public void closeChannel()
    {
        if (this.channel.isOpen())
        {
            this.channel.close().awaitUninterruptibly();
        }
    }

    public void setCompressionThreshold(int threshold)
    {
        if (threshold >= 0)
        {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder)
            {
                ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionThreshold(threshold);
            }
            else
            {
                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(threshold));
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder)
            {
                ((NettyCompressionEncoder)this.channel.pipeline().get("compress")).setCompressionThreshold(threshold);
            }
            else
            {
                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(threshold));
            }
        }
        else
        {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder)
            {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder)
            {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void setConnectionState(int state)
    {
        this.channel.attr(STATE_ATTRIBUTE_KEY).set(state);
        this.channel.config().setAutoRead(true);
    }
}
