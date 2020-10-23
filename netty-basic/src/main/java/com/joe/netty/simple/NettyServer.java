package com.joe.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author ckh
 * @create 10/23/20 11:31 AM
 */
public class NettyServer {

    public static final int PORT = 6668;

    public static void main(String[] args) throws InterruptedException {
        // create BossGroup and WorkerGroup thread group
        // 1. bossGroup just solve accept request
        // 2. business logic processing is completed by WorkerGroup
        // 3. both they are loop
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // create server starter configure
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    // setting two thread group
                    .group(bossGroup, workerGroup)
                    // using NioServerSocketChannel as server channel implement
                    .channel(NioServerSocketChannel.class)
                    // setting number of thread queue connections
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // Set keep alive connection
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // Set the processor for the Pipeline corresponding to the EventLoop of workerGroup
                    .childHandler(
                            // Create a channel initialization object (anonymous object)
                            new ChannelInitializer<SocketChannel>() {
                                /**
                                 * 给 pipeline 设置处理器
                                 *
                                 * @param ch
                                 * @throws Exception
                                 */
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    pipeline.addLast(new NettyServerHandler());
                                }
                            });

            System.out.println("... server is ready ...");

            // bind port and synchronized, start server
            ChannelFuture channelFuture = bootstrap.bind(PORT).sync();

            // listen closed channel
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
