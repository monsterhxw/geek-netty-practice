package com.github.monsterhxw.common;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
public class RequestMessage extends Message<Operation> {

    public RequestMessage() {
    }

    public RequestMessage(long streamId, Operation operation) {
        MessageHeader header = new MessageHeader();
        header.setStreamId(streamId);
        header.setOpCode(OperationType.fromOperation(operation).getOpCode());

        super.setMessageHeader(header);
        super.setMessageBody(operation);
    }

    @Override
    public Class getMessageBodyDecodeClass(int opcode) {
        return OperationType.fromOpCode(opcode).getOperationClass();
    }
}
