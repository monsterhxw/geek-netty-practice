package com.github.monsterhxw.client.handler.dispatcher;

import com.github.monsterhxw.common.OperationResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
public class RequestPendingCenter {

    private final ConcurrentHashMap<Long, OperationResultFuture> futureMap = new ConcurrentHashMap<>();

    public void put(long streamId, OperationResultFuture future) {
        this.futureMap.put(streamId, future);
    }

    public void setSuccess(long streamId, OperationResult operationResult) {
        OperationResultFuture operationResultFuture;
        if ((operationResultFuture = futureMap.remove(streamId)) != null) {
            operationResultFuture.setSuccess(operationResult);
        }
    }
}
