package com.road.sentin.core.except;


import com.road.sentin.core.rule.DegradeRule;

public class DegradeException extends BlockException {
    public DegradeException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public DegradeException(String ruleLimitApp, DegradeRule rule) {
        super(ruleLimitApp, rule);
    }

    public DegradeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DegradeException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
