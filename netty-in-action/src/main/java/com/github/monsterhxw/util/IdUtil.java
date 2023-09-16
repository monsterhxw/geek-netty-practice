package com.github.monsterhxw.util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdUtil {

    private static final AtomicLong IDX = new AtomicLong();

    private IdUtil() {
    }

    public static long nextId() {
        return IDX.incrementAndGet();
    }

}
