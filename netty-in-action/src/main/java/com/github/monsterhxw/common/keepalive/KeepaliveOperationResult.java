package com.github.monsterhxw.common.keepalive;

import com.github.monsterhxw.common.OperationResult;
import lombok.Data;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;
}
