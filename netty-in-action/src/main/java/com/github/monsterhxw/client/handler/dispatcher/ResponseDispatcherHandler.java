package com.github.monsterhxw.client.handler.dispatcher;

import com.github.monsterhxw.common.MessageHeader;
import com.github.monsterhxw.common.OperationResult;
import com.github.monsterhxw.common.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
public class ResponseDispatcherHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    private final RequestPendingCenter requestPendingCenter;

    public ResponseDispatcherHandler(RequestPendingCenter requestPendingCenter) {
        this.requestPendingCenter = requestPendingCenter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage responseMessage) throws Exception {
        MessageHeader header = responseMessage.getMessageHeader();
        OperationResult result = responseMessage.getMessageBody();

        requestPendingCenter.setSuccess(header.getStreamId(), result);
    }
}
