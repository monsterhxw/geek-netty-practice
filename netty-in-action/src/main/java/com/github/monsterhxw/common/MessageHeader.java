package com.github.monsterhxw.common;

import lombok.Data;

/**
 * @author huangxuewei
 * @since 2023/9/11
 */
@Data
public class MessageHeader {

    private int version = 1;

    private int opCode;

    private long streamId;
}
