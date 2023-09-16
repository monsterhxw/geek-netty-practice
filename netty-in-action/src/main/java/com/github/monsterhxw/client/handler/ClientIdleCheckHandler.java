package com.github.monsterhxw.client.handler;

import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
public class ClientIdleCheckHandler extends IdleStateHandler {

    public ClientIdleCheckHandler() {
        super(0, 5, 0);
    }
}
