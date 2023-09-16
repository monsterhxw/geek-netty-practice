package com.github.monsterhxw.common.auth;

import com.github.monsterhxw.common.OperationResult;
import lombok.Data;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;
}
