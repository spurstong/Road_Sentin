package com.road.sentin.core.leap;




import com.road.sentin.core.data.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LeapArray<T> {
    // 一个时间窗口的时间长度
    protected int windowLengthInMs;
    // 时间窗口数组大小，秒级下是2个，分钟级下是60个
    protected int sampleCount;
    // 统计的时间间隔，1s或者60s
    protected int intervalInMs;

    protected final AtomicReferenceArray<WindowWrap<T>> array;

    private final ReentrantLock updateLock = new ReentrantLock();

    public LeapArray(int sampleCount,int intervalInMs) {
        this.windowLengthInMs = intervalInMs / sampleCount;
        this.intervalInMs = intervalInMs;
        this.array = new AtomicReferenceArray<>(sampleCount);
    }

    public abstract T newEmptyBucket(long timeMills);

    protected abstract  WindowWrap<T> resetWindowTo(WindowWrap<T> windowWrap, long startTime);

    private int calculateTimeIdx(long timeMills) {
        long timeId = timeMills / windowLengthInMs;
        return (int)(timeId % array.length());
    }

    protected long calcuteWindowStart(long timeMillis) {
        return timeMillis - timeMillis % windowLengthInMs;
    }

    public WindowWrap<T> currentWindow() {
        return currentWindow(TimeUtil.currentTimeMills());
    }

    public WindowWrap<T> currentWindow(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }
        int idx = calculateTimeIdx(timeMillis);
        long windowStart = calcuteWindowStart(timeMillis);
        while(true) {
            WindowWrap<T> old  = array.get(idx);
            if (old == null) {
                WindowWrap<T> window = new WindowWrap<T>(windowLengthInMs, windowStart, newEmptyBucket(timeMillis));
                if(array.compareAndSet(idx, null, window)) {
                    return window;
                } else {
                    Thread.yield();
                }
            } else if (windowStart == old.windowStart()) {
                return old;
            } else if (windowStart > old.windowStart()) {
                if (updateLock.tryLock()) {
                    try {
                        return resetWindowTo(old, windowStart);
                    } finally {
                        updateLock.unlock();
                    }
                } else {
                    Thread.yield();
                }
            } else if (windowStart < old.windowStart()) {
                // 理论上不会执行这一步
                return new WindowWrap<T>(windowLengthInMs, windowStart, newEmptyBucket(timeMillis));
            }
        }
    }

    public WindowWrap<T> getPreviousWindow() {
        return getPreviousWindow(TimeUtil.currentTimeMills());
    }

    public WindowWrap<T> getPreviousWindow(long timeMills) {
        if (timeMills < 0) {
            return null;
        }
        // 根据给定时间计算它的前一个时间窗口的位置
        int idx = calculateTimeIdx(timeMills-windowLengthInMs);
        timeMills = timeMills - windowLengthInMs;
        WindowWrap<T> wrap = array.get(idx);
        if(wrap == null || isWindowDeprecated(wrap)) {
            return null;
        }
        if(wrap.windowStart() + windowLengthInMs < (timeMills)) {
            return null;
        }
        return wrap;

    }

    public WindowWrap<T> getPreviousWindows() {
        return getPreviousWindow(TimeUtil.currentTimeMills());
    }

    public T getWindowValue(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }
        int idx = calculateTimeIdx(timeMillis);
        WindowWrap<T> bucket = array.get(idx);
        if (bucket == null || !bucket.isTimeInWindow(timeMillis)) {
            return null;
        }
        return bucket.value();
    }

    public boolean isWindowDeprecated(WindowWrap<T> windowWrap) {
        return isWindowDeprecated(TimeUtil.currentTimeMills(), windowWrap);
    }

    public boolean isWindowDeprecated(long time, WindowWrap<T> windowWrap) {
        return time - windowWrap.windowStart() > intervalInMs;
    }

    public List<WindowWrap<T>> list(long validTime) {
        int size = array.length();
        List<WindowWrap<T>> result = new ArrayList<>(size);
        for(int i=0; i<size; i++){
            WindowWrap<T> windowWrap = array.get(i);
            if (windowWrap == null || isWindowDeprecated(validTime, windowWrap)) {
                continue;
            }
            result.add(windowWrap);
        }
        return result;
    }

    public List<T> values() {
        return values(TimeUtil.currentTimeMills());
    }

    public List<T> values(long timeMills) {
        if (timeMills < 0) {
            return new ArrayList<>();
        }
        int size = array.length();
        List<T> result = new ArrayList<>(size);
        for(int i=0; i<size; i++) {
            WindowWrap<T> windowWrap = array.get(i);
            if (windowWrap == null || isWindowDeprecated(timeMills, windowWrap)) {
                continue;
            }
            result.add(windowWrap.value());
        }
        return result;
    }

    //获取滑动窗口的总间隔长度（以毫秒为单位）
    public int getIntervalInMs() {
        return intervalInMs;
    }

    public double getIntervalInSecond() {
        return intervalInMs / 1000.0;
    }

    public long currentWaiting() {
        return 0;
    }

    public void addWaiting(long time, int acquireCount) {
        throw  new UnsupportedOperationException();
    }
}
