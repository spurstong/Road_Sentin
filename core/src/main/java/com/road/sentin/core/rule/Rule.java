package com.road.sentin.core.rule;


import com.road.sentin.core.context.Context;
import com.road.sentin.core.node.DefaultNode;

public interface Rule {
    // 判断是否通过的公共接口
    boolean passCheck(Context context, DefaultNode node, int count, Object... args);
}
