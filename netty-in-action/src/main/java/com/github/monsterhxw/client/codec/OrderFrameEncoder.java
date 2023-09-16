package com.github.monsterhxw.client.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
public class OrderFrameEncoder extends LengthFieldPrepender {

    public OrderFrameEncoder() {
        super(2);
    }
}
