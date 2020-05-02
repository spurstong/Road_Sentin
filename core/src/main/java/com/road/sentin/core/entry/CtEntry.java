package com.road.sentin.core.entry;


import com.road.sentin.core.chain.ProcessorSlot;
import com.road.sentin.core.context.Context;
import com.road.sentin.core.context.ContextUtil;
import com.road.sentin.core.context.NullContext;
import com.road.sentin.core.except.ErrorEntryFreeException;
import com.road.sentin.core.node.Node;
import com.road.sentin.core.resource.ResourceWrapper;

public class CtEntry extends Entry {
    protected Entry parent = null;
    protected Entry child = null;
    protected ProcessorSlot<Object> chain;
    protected Context context;


    public CtEntry(ResourceWrapper resourceWrapper, ProcessorSlot<Object> chain, Context context) {
        super(resourceWrapper);
        this.chain = chain;
        this.context = context;
        setUpEntryFor(context);
    }

    private void setUpEntryFor(Context context) {
        if (context instanceof NullContext) {
            return;
        }
        this.parent = context.getCurEntry();
        if (parent != null) {
            ((CtEntry)parent).child = this;
        }
        context.setCurEntry(this);
    }



    @Override
    public void exit(int count, Object... args) throws ErrorEntryFreeException {
        trueExit(count, args);
    }

    protected void exitForContext(Context context, int count, Object... args) throws ErrorEntryFreeException {
        if (context != null) {
            if (context instanceof  NullContext) {
                return;
            }
            if (context.getCurEntry() != this) {
                System.out.println("移除：" + context.getCurEntry());
                String curEntryNameInContext = context.getCurEntry() == null ? null : context.getCurEntry().getResourceWrapper().getName();
                CtEntry e = (CtEntry)context.getCurEntry();
                while(e != null) {
                    e.exit(count, args);
                    e = (CtEntry)e.parent;
                }
                String errorMessage = String.format("The order of entry exit can't be paired with the order of entry"
                        + ", current entry in context: <%s>, but expected: <%s>", curEntryNameInContext, resourceWrapper.getName());
                throw new ErrorEntryFreeException(errorMessage);
            } else {
                if (chain != null) {
                    chain.exit(context, resourceWrapper, count, args);
                }
                context.setCurEntry(parent);
                if (parent != null) {
                    ((CtEntry)parent).child = null;
                }
                if (parent == null) {
                    if (ContextUtil.isDefaultContext(context)) {
                        ContextUtil.exit();
                    }
                }
                clearEntryContext();
            }
        }

    }
    protected  void clearEntryContext() {
        this.context = null;
    }
    @Override
    protected Entry trueExit(int count, Object... args) throws ErrorEntryFreeException {
        exitForContext(context, count, args);
        return parent;
    }

    @Override
    public Node getLastNode() {
        return parent == null ? null : parent.getCurNode();
    }
}
