package com.github.monsterhxw.client.handler;

import com.github.monsterhxw.common.RequestMessage;
import com.github.monsterhxw.common.keepalive.KeepaliveOperation;
import com.github.monsterhxw.util.IdUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
@Slf4j
@ChannelHandler.Sharable
public class KeepaliveHandler extends ChannelInboundHandlerAdapter {

    private KeepaliveHandler() {
    }

    public static KeepaliveHandler getInstance() {
        return KeepaliveHandlerHolder.INSTANCE;
    }

    private static class KeepaliveHandlerHolder {
        private static final KeepaliveHandler INSTANCE = new KeepaliveHandler();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.info("write idle happen, so need to send keepalive to keep coonection not closed by server");
            KeepaliveOperation keepaliveOperation = new KeepaliveOperation();

            RequestMessage keepaliveRequestMessage = new RequestMessage(IdUtil.nextId(), keepaliveOperation);

            ctx.writeAndFlush(keepaliveRequestMessage);
        }

        super.userEventTriggered(ctx, evt);
    }
}
