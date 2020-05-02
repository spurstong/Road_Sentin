package com.road.sentin.core.except;


import com.road.sentin.core.rule.FlowRule;

public class FlowException extends BlockException {
    public FlowException(String ruleLimitApp) {
        super(ruleLimitApp);
    }
    public FlowException(String ruleLimitApp, FlowRule rule) {
        super(ruleLimitApp, rule);
    }

    public FlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }


}
