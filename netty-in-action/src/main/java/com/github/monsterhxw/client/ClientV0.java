package com.github.monsterhxw.client;

import com.github.monsterhxw.client.codec.OrderProtocolDecoder;
import com.github.monsterhxw.client.codec.OrderProtocolEncoder;
import com.github.monsterhxw.client.handler.ClientIdleCheckHandler;
import com.github.monsterhxw.client.handler.KeepaliveHandler;
import com.github.monsterhxw.common.RequestMessage;
import com.github.monsterhxw.common.auth.AuthOperation;
import com.github.monsterhxw.common.order.OrderOperation;
import com.github.monsterhxw.client.codec.OrderFrameDecoder;
import com.github.monsterhxw.client.codec.OrderFrameEncoder;
import com.github.monsterhxw.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
@Slf4j
public class ClientV0 {

    public static void main(String[] args) throws SSLException {
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
        //下面这行，先直接信任自签证书，以避免没有看到ssl那节课程的同学运行不了；
        //学完ssl那节后，可以去掉下面这行代码，安装证书，安装方法参考课程，执行命令参考resources/ssl.txt里面
        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        SslContext sslContext = sslContextBuilder.build();
        KeepaliveHandler keepaliveHandler = KeepaliveHandler.getInstance();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);


        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.channel(NioSocketChannel.class)
                    .group(group)
                    .option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1_000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientInitializer(sslContext, keepaliveHandler, loggingHandler));


            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();


            AuthOperation authOperation = new AuthOperation("admin", "password");
            RequestMessage authRequestMessage = new RequestMessage(IdUtil.nextId(), authOperation);

            channelFuture.channel().writeAndFlush(authRequestMessage);


            RequestMessage orderRequestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1001, "tudou"));

            channelFuture.channel().writeAndFlush(orderRequestMessage);


            channelFuture.channel().closeFuture().sync();
        } catch (Exception ignore) {
        } finally {
            group.shutdownGracefully();
        }
    }

    @Data
    private static class ClientInitializer extends ChannelInitializer<NioSocketChannel> {

        private final SslContext sslContext;
        private final KeepaliveHandler keepaliveHandler;
        private final LoggingHandler loggingHandler;

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new ClientIdleCheckHandler());

//            pipeline.addLast(sslContext.newHandler(ch.alloc()));

            pipeline.addLast(new OrderFrameDecoder());
            pipeline.addLast(new OrderFrameEncoder());

            pipeline.addLast(new OrderProtocolEncoder());
            pipeline.addLast(new OrderProtocolDecoder());

            pipeline.addLast(loggingHandler);
        }
    }
}
