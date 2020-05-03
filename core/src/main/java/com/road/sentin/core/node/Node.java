package com.road.sentin.core.node;

public interface Node  extends OccupySupport{
    // 每一分钟的通过数
    long totalPass();
    // 每一分钟的总共的完成的请求数
    long totalSuccess();
    // 每一分钟拒绝的请求数
    long blockRequest();
    // 每分钟的异常数
    long totalException();
    // 每一秒的通过请求数
    double passQps();
    // 每一秒的拒绝请求数
    double blockQps();
    // 每一秒的通过数和拒绝数之和
    double totalQps();
    // 每一秒的完成数
    double successQps();
    // 每一秒的异常数
    double exceptionQps();
    // 每一秒的平均响应时间
    double avgRt();
    // 现在活跃的线程数
    int curThreadNum();
    // 上一个窗口的QPS
    double previousPassQps();

    void addPassRequest(int count);

    void increaseBlockQps(int count);

    void increaseExceptionQps(int count);

    void addRtAndSuccess(long rt, int success);
    void increseThreadNum();
    void decreaseThreadNum();
    void reset();
}
