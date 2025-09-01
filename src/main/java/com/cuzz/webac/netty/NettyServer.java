package com.cuzz.webac.netty;

import com.cuzz.webac.netty.handler.NettyWebSocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {



//    @Bean
    public ApplicationRunner nettyRunner() {
        return args -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup(2);
            EventLoopGroup workerGroup = new NioEventLoopGroup(20);

            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpServerCodec());
                                pipeline.addLast(new HttpObjectAggregator(8192));
                                pipeline.addLast(new ChunkedWriteHandler());
                                pipeline.addLast(new NettyWebSocketServerHandler()); // 自定义的WebSocket处理器
                            }
                        });

                ChannelFuture f = b.bind(28088).sync();
                System.out.println("WebSocket 服务器已启动，端口号：" + 28088);
                f.channel().closeFuture().sync();

            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        };
    }
}
