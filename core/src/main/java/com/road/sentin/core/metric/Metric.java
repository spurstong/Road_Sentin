package com.road.sentin.core.metric;

public interface Metric {
    // 成功计数
    long success();
    // 全部异常计数
    long exception();
    // 全部阻塞计数
    long block();
    // 全部通过计数
    long pass();
    // 全部响应时间
    long rt();
    // 增加当前的异常计数
    void addException(int n);
    // 增加当前阻塞计数
    void addBlock(int n);
    // 增加当前完成数
    void addSuccess(int n);
    // 增加当前通过数
    void addPass(int n);
    // 增加给定的响应时间
    void addRT(long rt);
    // 获取秒级下的滑动时间窗口长度
    double getWindowIntervalInsec();
    // 根据给定的时间获得通过量
    // 该操作不会执行刷新，所以不会产生新的bucket
    long getWindowPass(long timeMillis);
    // 增加抢占数
    void addOccupiedPass(int acquireCount);
    // 增加抢占的请求
    void addWaiting(long futureTime, int acquiereCount);
    // 获得等待的请求通过数
    long waiting();
    // 增加抢占的通过数
    long occupiedPass();
    long previousWindowBlock();
    long perviousWindowPass();
}
