package com.github.monsterhxw.common.auth;

import com.github.monsterhxw.common.Operation;
import lombok.Data;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
public class AuthOperation extends Operation {

    private final String userName;
    private final String password;

    @Override
    public AuthOperationResult execute() {
        if ("admin".equalsIgnoreCase(userName)) {
            return new AuthOperationResult(true);
        }
        return new AuthOperationResult(false);
    }
}
