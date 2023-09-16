package com.github.monsterhxw.common.order;

import com.github.monsterhxw.common.Operation;
import com.github.monsterhxw.common.OperationResult;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
@Slf4j
public class OrderOperation extends Operation {

    private final int tableId;
    private final String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OperationResult execute() {
        log.info("Order's executing startup with orderRequest: " + this);
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        log.info("Order's executing complete");
        return new OrderOperationResult(tableId, dish, true);
    }
}
