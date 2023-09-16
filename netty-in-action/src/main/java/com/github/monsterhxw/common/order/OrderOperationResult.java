package com.github.monsterhxw.common.order;

import com.github.monsterhxw.common.OperationResult;
import lombok.Data;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean completed;

    public OrderOperationResult(int tableId, String dish, boolean completed) {
        this.tableId = tableId;
        this.dish = dish;
        this.completed = completed;
    }
}
