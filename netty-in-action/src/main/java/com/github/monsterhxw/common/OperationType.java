package com.github.monsterhxw.common;

import com.github.monsterhxw.common.auth.AuthOperation;
import com.github.monsterhxw.common.auth.AuthOperationResult;
import com.github.monsterhxw.common.keepalive.KeepaliveOperation;
import com.github.monsterhxw.common.keepalive.KeepaliveOperationResult;
import com.github.monsterhxw.common.order.OrderOperation;
import com.github.monsterhxw.common.order.OrderOperationResult;
import lombok.Data;
import lombok.Getter;

import java.util.function.Predicate;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
public enum OperationType {

    AUTH(1, AuthOperation.class, AuthOperationResult.class),
    KEEPALIVE(2, KeepaliveOperation.class, KeepaliveOperationResult.class),
    ORDER(3, OrderOperation.class, OrderOperationResult.class);

    @Getter
    private int opCode;
    @Getter
    private Class<? extends Operation> operationClass;
    @Getter
    private Class<? extends OperationResult> operationResultClass;

    OperationType(int opCode, Class<? extends Operation> operationClass, Class<? extends OperationResult> operationResultClass) {
        this.opCode = opCode;
        this.operationClass = operationClass;
        this.operationResultClass = operationResultClass;
    }

    public static OperationType fromOpCode(int type) {
        return getOperationType(operationType -> operationType.opCode == type);
    }

    public static OperationType fromOperation(Operation operation) {
        return getOperationType(operationType -> operationType.operationClass == operation.getClass());
    }

    public static OperationType getOperationType(Predicate<OperationType> predicate) {
        OperationType[] values = values();
        for (OperationType operationType : values) {
            if (predicate.test(operationType)) {
                return operationType;
            }
        }
        throw new AssertionError("OperationType not found");
    }
}
