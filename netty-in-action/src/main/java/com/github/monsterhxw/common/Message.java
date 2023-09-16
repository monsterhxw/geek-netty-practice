package com.github.monsterhxw.common;

import com.github.monsterhxw.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
public abstract class Message<T extends MessageBody> {

    private MessageHeader messageHeader;
    private T messageBody;

    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(messageHeader.getVersion());
        byteBuf.writeLong(messageHeader.getStreamId());
        byteBuf.writeInt(messageHeader.getOpCode());
        byteBuf.writeBytes(JsonUtil.toJson(messageBody).getBytes());
    }

    public abstract Class<T> getMessageBodyDecodeClass(int opcode);

    public void decode(ByteBuf byteBuf) {
        messageHeader = new MessageHeader();
        messageHeader.setVersion(byteBuf.readInt());
        messageHeader.setStreamId(byteBuf.readLong());
        messageHeader.setOpCode(byteBuf.readInt());

        Class<T> bodyClazz = getMessageBodyDecodeClass(messageHeader.getOpCode());
        String bodyStr = byteBuf.toString(StandardCharsets.UTF_8);
        messageBody = JsonUtil.fromJson(bodyStr, bodyClazz);
    }
}
