package com.github.monsterhxw.server.codec;

import com.github.monsterhxw.common.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
@ChannelHandler.Sharable
public class OrderProtocolEncoder extends MessageToMessageEncoder<ResponseMessage> {

    private OrderProtocolEncoder() {
    }

    public static OrderProtocolEncoder getInstance() {
        return OrderProtocolEncoderHolder.INSTANCE;
    }

    private static class OrderProtocolEncoderHolder {
        private static final OrderProtocolEncoder INSTANCE = new OrderProtocolEncoder();
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseMessage responseMessage, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        responseMessage.encode(buffer);
        out.add(buffer);
    }
}
