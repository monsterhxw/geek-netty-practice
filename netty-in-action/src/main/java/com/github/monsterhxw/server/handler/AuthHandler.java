package com.github.monsterhxw.server.handler;

import com.github.monsterhxw.common.Operation;
import com.github.monsterhxw.common.OperationResult;
import com.github.monsterhxw.common.RequestMessage;
import com.github.monsterhxw.common.ResponseMessage;
import com.github.monsterhxw.common.auth.AuthOperation;
import com.github.monsterhxw.common.auth.AuthOperationResult;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
@ChannelHandler.Sharable
@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private AuthHandler() {
    }

    public static AuthHandler getInstance() {
        return AuthHandlerHolder.INSTANCE;
    }

    private static class AuthHandlerHolder {
        private static final AuthHandler INSTANCE = new AuthHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        try {
            Operation operation = requestMessage.getMessageBody();
            boolean success = false;
            if (operation instanceof AuthOperation) {
                AuthOperation authOperation = (AuthOperation) operation;
                AuthOperationResult result = authOperation.execute();
                if (result.isPassAuth()) {
                    success = true;
                    log.info("pass auth");
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessageHeader(requestMessage.getMessageHeader());
                    responseMessage.setMessageBody(result);
                    ctx.writeAndFlush(responseMessage);
                } else {
                    log.error("fail to auth");
                }
            } else {
                log.error("expect first msg is auth.");
            }
            if (!success) {
                ctx.close();
            }
        } catch (Exception e) {
            log.error("exception happen for: " + e.getMessage(), e);
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }
    }
}
