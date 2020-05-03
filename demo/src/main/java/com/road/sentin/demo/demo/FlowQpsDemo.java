package com.road.sentin.demo.demo;



import com.road.sentin.core.controller.TrafficShapingController;
import com.road.sentin.core.data.TimeUtil;
import com.road.sentin.core.entry.Entry;
import com.road.sentin.core.except.BlockException;
import com.road.sentin.core.phy.Sphu;
import com.road.sentin.core.rule.FlowRule;
import com.road.sentin.core.rule.FlowRuleManager;
import com.road.sentin.core.rule.RuleConstant;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowQpsDemo {

    private static final String KEY = "abc";

    private static AtomicInteger pass = new AtomicInteger();
    private static AtomicInteger block = new AtomicInteger();
    private static AtomicInteger total = new AtomicInteger();

    private static volatile boolean stop = false;

    private static final int threadCount = 1;

    private static int seconds = 60 + 40;

    public static void main(String[] args) throws Exception {
        initFlowQpsRule();

        tick();
        // first make the system run on a very low condition
        simulateTraffic();

        System.out.println("===== begin to do flow control");
        System.out.println("only 20 requests per second can pass");

    }

    private static void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource(KEY);
        // set limit qps to 20
        rule1.setCount(10);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule1.setLimitApp("default");
        rules.add(rule1);
        FlowRuleManager.loadRules(rules);
        List<FlowRule> lsit = FlowRuleManager.getRules();
        for(FlowRule rule : lsit) {
            System.out.println(rule.getCount());
            System.out.println(rule.getGrade());
            TrafficShapingController controller = rule.getRater();
            System.out.println(controller.toString());
        }
        System.out.println("--------------");
    }

    private static void simulateTraffic() {
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new RunTask());
            t.setName("simulate-traffic-Task");
            t.start();
        }
    }

    private static void tick() {
        Thread timer = new Thread(new TimerTask());
        timer.setName("sentinel-timer-task");
        timer.start();
    }

    static class TimerTask implements Runnable {

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            System.out.println("begin to statistic!!!");

            long oldTotal = 0;
            long oldPass = 0;
            long oldBlock = 0;
            while (!stop) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
                long globalTotal = total.get();
                long oneSecondTotal = globalTotal - oldTotal;
                oldTotal = globalTotal;

                long globalPass = pass.get();
                long oneSecondPass = globalPass - oldPass;
                oldPass = globalPass;

                long globalBlock = block.get();
                long oneSecondBlock = globalBlock - oldBlock;
                oldBlock = globalBlock;

                System.out.println(seconds + " send qps is: " + oneSecondTotal);
                System.out.println(TimeUtil.currentTimeMills() + ", total:" + oneSecondTotal
                        + ", pass:" + oneSecondPass
                        + ", block:" + oneSecondBlock);

                if (seconds-- <= 0) {
                    stop = true;
                }
            }

            long cost = System.currentTimeMillis() - start;
            System.out.println("time cost: " + cost + " ms");
            System.out.println("total:" + total.get() + ", pass:" + pass.get()
                    + ", block:" + block.get());
            System.exit(0);
        }
    }

    static class RunTask implements Runnable {
        @Override
        public void run() {
            while (!stop) {
                Entry entry = null;

                try {
                    entry = Sphu.entry(KEY);
                    // token acquired, means pass
                    pass.addAndGet(1);
                } catch (BlockException e1) {
                    block.incrementAndGet();
                } catch (Exception e2) {
                    // biz exception
                } finally {
                    total.incrementAndGet();
                    if (entry != null) {
                        System.out.println("退出");
                        entry.exit();
                    }
                }

                Random random2 = new Random();
                try {
                    TimeUnit.MILLISECONDS.sleep(random2.nextInt(50));
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }
}

