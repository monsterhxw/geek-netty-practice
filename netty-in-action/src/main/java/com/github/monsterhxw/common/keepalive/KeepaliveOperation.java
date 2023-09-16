package com.github.monsterhxw.common.keepalive;

import com.github.monsterhxw.common.Operation;
import com.github.monsterhxw.common.OperationResult;
import lombok.Data;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
public class KeepaliveOperation extends Operation {

    private final long time = System.currentTimeMillis();

    @Override
    public OperationResult execute() {
        return new KeepaliveOperationResult(time);
    }
}
