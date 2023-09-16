package com.github.monsterhxw.client;

import com.github.monsterhxw.client.codec.*;
import com.github.monsterhxw.client.handler.ClientIdleCheckHandler;
import com.github.monsterhxw.client.handler.KeepaliveHandler;
import com.github.monsterhxw.client.handler.dispatcher.OperationResultFuture;
import com.github.monsterhxw.client.handler.dispatcher.RequestPendingCenter;
import com.github.monsterhxw.client.handler.dispatcher.ResponseDispatcherHandler;
import com.github.monsterhxw.common.OperationResult;
import com.github.monsterhxw.common.RequestMessage;
import com.github.monsterhxw.common.auth.AuthOperation;
import com.github.monsterhxw.common.order.OrderOperation;
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
public class ClientV2 {

    public static void main(String[] args) throws SSLException {
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
        //下面这行，先直接信任自签证书，以避免没有看到ssl那节课程的同学运行不了；
        //学完ssl那节后，可以去掉下面这行代码，安装证书，安装方法参考课程，执行命令参考resources/ssl.txt里面
        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        SslContext sslContext = sslContextBuilder.build();

        KeepaliveHandler keepaliveHandler = KeepaliveHandler.getInstance();

        RequestPendingCenter requestPendingCenter = new RequestPendingCenter();


        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.channel(NioSocketChannel.class)
                    .group(group)
                    .option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1_000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientInitializer(sslContext, keepaliveHandler, requestPendingCenter));


            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();


            long streamId = IdUtil.nextId();
            AuthOperation authOperation = new AuthOperation("admin", "password");
            RequestMessage authRequestMessage = new RequestMessage(streamId, authOperation);
            OperationResultFuture authResultFuture = new OperationResultFuture();

            requestPendingCenter.put(streamId, authResultFuture);
            channelFuture.channel().writeAndFlush(authRequestMessage);

            OperationResult auhResult = authResultFuture.get();
            log.info("auth result: {}", auhResult);


            streamId = IdUtil.nextId();
            RequestMessage requestMessage = new RequestMessage(streamId, new OrderOperation(1001, "tudou"));
            OperationResultFuture orderResultFuture = new OperationResultFuture();
            requestPendingCenter.put(streamId, orderResultFuture);
            channelFuture.channel().writeAndFlush(requestMessage);

            // wait for result
            OperationResult orderResult = orderResultFuture.get();


            log.info("order result: {}", orderResult);

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
        private final RequestPendingCenter requestPendingCenter;

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new ClientIdleCheckHandler());

//            pipeline.addLast(sslContext.newHandler(ch.alloc()));

            pipeline.addLast(new OrderFrameDecoder());
            pipeline.addLast(new OrderFrameEncoder());

            pipeline.addLast(new OrderProtocolEncoder());
            pipeline.addLast(new OrderProtocolDecoder());

            pipeline.addLast(new ResponseDispatcherHandler(requestPendingCenter));

            pipeline.addLast(new OperationToRequestMessageEncoder());

            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        }
    }
}
