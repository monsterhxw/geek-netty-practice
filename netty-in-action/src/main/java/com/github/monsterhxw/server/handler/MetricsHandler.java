package com.github.monsterhxw.server.handler;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
@ChannelHandler.Sharable
public class MetricsHandler extends ChannelDuplexHandler {

    private MetricsHandler() {
    }

    public static MetricsHandler getInstance() {
        return MetricsHandlerHolder.INSTANCE;
    }

    private static class MetricsHandlerHolder {
        private static final MetricsHandler INSTANCE = new MetricsHandler();
    }

    private static final AtomicLong totalConnectionNumber = new AtomicLong();

    static {
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.register("totalConnectionNumber", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalConnectionNumber.longValue();
            }
        });

        ConsoleReporter.forRegistry(metricRegistry).build().start(10, TimeUnit.SECONDS);

        JmxReporter.forRegistry(metricRegistry).build().start();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        totalConnectionNumber.decrementAndGet();
        super.channelInactive(ctx);
    }
}
