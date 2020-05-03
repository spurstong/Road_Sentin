package com.road.sentin.core.except;

// 表示资源入口和资源出口的顺序不匹配（对不匹配）。
public class ErrorEntryFreeException extends RuntimeException {
    public ErrorEntryFreeException(String s) {
        super(s);
    }
}
