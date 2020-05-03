package com.road.sentin.core.rule;



import com.road.sentin.core.context.Context;
import com.road.sentin.core.node.DefaultNode;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public abstract class AbstractRule implements Rule {
    private String resource;
    private String limitApp;
    public String getResource() {
        return resource;
    }

    public AbstractRule setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String getLimitApp() {
        return limitApp;
    }

    public AbstractRule setLimitApp(String limitApp) {
        this.limitApp = limitApp;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(!(obj instanceof AbstractRule)) {
            return false;
        }

        AbstractRule that = (AbstractRule)obj;
        if (resource != null ? !resource.equals(that.resource) : that.resource != null) {
            return false;
        }
        if (!limitAppEquals(limitApp, that.limitApp)) {
            return false;
        }
        return true;

    }

    private boolean limitAppEquals(String str1, String str2) {
        if ("".equals(str1)) {
            return RuleConstant.LIMIT_APP_DEFAULT.equals(str2);
        } else if (RuleConstant.LIMIT_APP_DEFAULT.equals(str1)) {
            return "".equals(str2) || str2 == null || str1.equals(str2);
        }
        if (str1 == null) {
            return str2 == null || RuleConstant.LIMIT_APP_DEFAULT.equals(str2);
        }
        return str1.equals(str2);
    }
    @Override
    public boolean passCheck(Context context, DefaultNode node, int count, Object... args) {
        return false;
    }

    @Override
    public int hashCode() {
        int result = resource != null ? resource.hashCode() : 0;
        if (!("".equals(limitApp) || RuleConstant.LIMIT_APP_DEFAULT.equals(limitApp) || limitApp == null)) {
            result = 31 * result + limitApp.hashCode();
        }
        return result;
    }
}
