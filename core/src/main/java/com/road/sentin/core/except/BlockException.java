package com.road.sentin.core.except;


import com.road.sentin.core.rule.AbstractRule;

public abstract class BlockException extends Exception {
    public static final String BLOCk_EXCEPTION_FLAG = "SentinelBlockException";
    public static RuntimeException THROW_OUT_EXCEPTION = new RuntimeException(BLOCk_EXCEPTION_FLAG);
    public static StackTraceElement[] stackTraceElements = new StackTraceElement[] {
        new StackTraceElement(BlockException.class.getName(), "block", "BlockException", 0)
    };

    static {
        THROW_OUT_EXCEPTION.setStackTrace(stackTraceElements);
    }

    protected AbstractRule rule;
    private String ruleLimitApp;

    public BlockException(String ruleLimitApp) {
        super();
        this.ruleLimitApp = ruleLimitApp;
    }

    public BlockException(String ruleLimitApp, AbstractRule rule) {
        super();
        this.ruleLimitApp = ruleLimitApp;
        this.rule = rule;
    }

    public BlockException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockException(String ruleLimitApp, String message) {
        super(message);
        this.ruleLimitApp = ruleLimitApp;
    }

    public BlockException(String ruleLimitApp, String message, AbstractRule rule) {
        super(message);
        this.ruleLimitApp = ruleLimitApp;
        this.rule = rule;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public String getRuleLimitApp() {
        return ruleLimitApp;
    }

    public void setRuleLimitApp(String ruleLimitApp) {
        this.ruleLimitApp = ruleLimitApp;
    }

    public static boolean isBlockException(Throwable t) {
        if (null == t) {
            return false;
        }
        int counter = 0;
        Throwable cause = t;
        while(cause != null &&counter++ < 50) {
            if ((cause instanceof BlockException) || (BLOCk_EXCEPTION_FLAG.equals(cause.getMessage()))) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    public AbstractRule getRule() {
        return rule;
    }
}
