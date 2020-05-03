package com.road.sentin.core.node;


import com.road.sentin.core.rule.RuleConstant;

public class IntervalProperty {
    public static volatile int INTERVAL = RuleConstant.DEFAULT_WINDOW_INTERVAL_MS;

    public static void updateInterval(int newInterval) {
        if (newInterval != INTERVAL) {
            INTERVAL = newInterval;
        }
    }
}
