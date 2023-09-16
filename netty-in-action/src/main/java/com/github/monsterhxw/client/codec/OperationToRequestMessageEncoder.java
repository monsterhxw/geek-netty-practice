package com.github.monsterhxw.client.codec;

import com.github.monsterhxw.common.Operation;
import com.github.monsterhxw.common.RequestMessage;
import com.github.monsterhxw.util.IdUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
        out.add(requestMessage);
    }
}
