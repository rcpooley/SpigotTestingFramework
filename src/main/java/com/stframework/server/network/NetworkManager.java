package com.stframework.server.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetAddress;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {

    private static final EventLoopGroup GROUP = new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());

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
        }).channel(EpollSocketChannel.class).connect(address, port).syncUninterruptibly();

        return manager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        System.out.println("Got packet " + packet);
    }
}
