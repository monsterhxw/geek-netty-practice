package com.github.monsterhxw.client.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author huangxuewei
 * @since 2023/9/12
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    public OrderFrameDecoder() {
        super(10240, 0, 2, 0, 2);
    }
}
