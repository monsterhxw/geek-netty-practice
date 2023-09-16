package com.github.monsterhxw.common;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
public class ResponseMessage extends Message<OperationResult> {

    @Override
    public Class getMessageBodyDecodeClass(int opcode) {
        return OperationType.fromOpCode(opcode).getOperationResultClass();
    }
}
