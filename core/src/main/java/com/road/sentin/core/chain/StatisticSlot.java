package com.road.sentin.core.chain;


import com.road.sentin.core.context.Constants;
import com.road.sentin.core.context.Context;
import com.road.sentin.core.data.TimeUtil;
import com.road.sentin.core.entry.EntryType;
import com.road.sentin.core.except.BlockException;
import com.road.sentin.core.except.PriorityWaitException;
import com.road.sentin.core.node.DefaultNode;
import com.road.sentin.core.resource.ResourceWrapper;
import com.sun.deploy.security.BlockedException;

public class StatisticSlot extends AbstracrLinkedProcessorSlot<DefaultNode> {
    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, boolean prioritized, Object... args) throws Throwable {
        try {
            fireEntry(context, resourceWrapper, node, count, prioritized, args);
            node.increseThreadNum();
            node.addPassRequest(count);
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increseThreadNum();
                context.getCurEntry().getOriginNode().addPassRequest(count);
            }
        } catch (PriorityWaitException ex) {
            node.increseThreadNum();
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increseThreadNum();
            }
        } catch (BlockException e) {
            context.getCurEntry().setError(e);
            node.increaseBlockQps(count);
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increaseBlockQps(count);
            }
            throw  e;
        } catch (Throwable e) {
            context.getCurEntry().setError(e);
            node.increaseExceptionQps(count);
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increaseExceptionQps(count);
            }
            throw e;
        }

    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        DefaultNode node = (DefaultNode)context.getCurNode();
        if (context.getCurEntry().getError() == null)  {
            long rt = TimeUtil.currentTimeMills() - context.getCurEntry().getCreateTime();
            node.addRtAndSuccess(rt, count);
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().addRtAndSuccess(rt, count);
            }
            node.decreaseThreadNum();
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().decreaseThreadNum();
            }
        }
        fireExit(context, resourceWrapper, count);
    }
}
