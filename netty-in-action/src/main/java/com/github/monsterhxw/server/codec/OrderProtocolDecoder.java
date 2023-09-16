package com.github.monsterhxw.server.codec;

import com.github.monsterhxw.common.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
@ChannelHandler.Sharable
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    private OrderProtocolDecoder() {
    }

    public static OrderProtocolDecoder getInstance() {
        return OrderProtocolDecoderHolder.INSTANCE;
    }

    private static class OrderProtocolDecoderHolder {
        private static final OrderProtocolDecoder INSTANCE = new OrderProtocolDecoder();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.decode(byteBuf);
        out.add(requestMessage);
    }
}
