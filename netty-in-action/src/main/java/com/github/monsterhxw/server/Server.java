package com.github.monsterhxw.server;

import com.github.monsterhxw.server.codec.OrderFrameDecoder;
import com.github.monsterhxw.server.codec.OrderFrameEncoder;
import com.github.monsterhxw.server.codec.OrderProtocolDecoder;
import com.github.monsterhxw.server.codec.OrderProtocolEncoder;
import com.github.monsterhxw.server.handler.AuthHandler;
import com.github.monsterhxw.server.handler.MetricsHandler;
import com.github.monsterhxw.server.handler.OrderServerProcessHandler;
import com.github.monsterhxw.server.handler.ServerIdleCheckHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Slf4j
public class Server {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        UnorderedThreadPoolEventExecutor businessGroup = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));
        // NioEventLoopGroup 不能作业务线程池，因为同一连接只会用一个线程
//        NioEventLoopGroup businessGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("business"));
        NioEventLoopGroup trafficShapingGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("trafficShaping"));

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            // log
            LoggingHandler debugLogHandler = new LoggingHandler(LogLevel.DEBUG);
            LoggingHandler infoLogHandler = new LoggingHandler(LogLevel.INFO);
            // ip filter
            IpSubnetFilterRule ipSubnetFilterRule = new IpSubnetFilterRule("127.1.1.1", 16, IpFilterRuleType.REJECT);
            RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(ipSubnetFilterRule);
            //trafficShaping
            GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(trafficShapingGroup, 10 * 1024 * 1024, 10 * 1024 * 1024);
            //ssl
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            log.info("certificate position:" + selfSignedCertificate.certificate().toString());
//            SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();

            ServerInitializer serverInitializer = new ServerInitializer(
                    businessGroup,
                    trafficShapingGroup,
                    debugLogHandler,
                    infoLogHandler,
                    ruleBasedIpFilter,
                    globalTrafficShapingHandler
//                    ,
//                    sslContext
            );

            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(NioChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(NioChannelOption.TCP_NODELAY, true)
                    .childHandler(serverInitializer);

            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (Exception ignore) {
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
            trafficShapingGroup.shutdownGracefully();
        }
    }

    @Data
    private static class ServerInitializer extends ChannelInitializer<NioSocketChannel> {

        private final UnorderedThreadPoolEventExecutor businessGroup;
        private final NioEventLoopGroup trafficShapingGroup;
        private final LoggingHandler debugLoggingHandler;
        private final LoggingHandler infoLoggingHandler;
        private final RuleBasedIpFilter ruleBasedIpFilter;
        private final GlobalTrafficShapingHandler globalTrafficShapingHandler;
//        private final SslContext sslContext;

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();


            pipeline.addLast("debugLog", debugLoggingHandler);

            pipeline.addLast("ipFilter", ruleBasedIpFilter);

            pipeline.addLast("trafficShapingHandler", globalTrafficShapingHandler);

            pipeline.addLast("metricHandler", MetricsHandler.getInstance());

            pipeline.addLast("idleHandler", new ServerIdleCheckHandler());

//            pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));

            pipeline.addLast("frameDecoder",  new OrderFrameDecoder());
            pipeline.addLast("frameEncoder", new OrderFrameEncoder());
            pipeline.addLast("protocolDecoder", OrderProtocolDecoder.getInstance());
            pipeline.addLast("protocolEncoder", OrderProtocolEncoder.getInstance());

            pipeline.addLast("infoLog", infoLoggingHandler);

            pipeline.addLast("flushEnhance", new FlushConsolidationHandler(10, true));

            pipeline.addLast("auth", AuthHandler.getInstance());

            pipeline.addLast(businessGroup, new OrderServerProcessHandler());
        }
    }
}
