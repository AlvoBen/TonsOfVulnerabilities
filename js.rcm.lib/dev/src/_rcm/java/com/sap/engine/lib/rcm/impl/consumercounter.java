package com.sap.engine.lib.rcm.impl;

import java.util.concurrent.atomic.AtomicLong;

public class ConsumerCounter extends AtomicLong  {

    private String id;
    /* this is transient only to make the serialization JLin test happy. 
       This class is not intended to be serializable but happens to inherit Serializable from AtomicLong
     */
    private transient ResourceContextImpl context;

    public ConsumerCounter(String id, ResourceContextImpl context) {
       super();
       this.id = id;
       this.context = context;
       synchronized(this) {
        context.hardRefs.add(this);
       }
    }

    public ConsumerCounter(long initialValue, String id, ResourceContextImpl context) {
        super(initialValue);
        this.id = id;
        this.context =context;
        synchronized(this) {
          context.hardRefs.add(this);
        }
    }

    public long add(long delta) {
        long result = super.getAndAdd(delta);
        if (result == 0) {
           synchronized(this) {
             context.hardRefs.add(this);
           }
        }
        return result;
    }

    public long dec(long delta) {
      long result = super.addAndGet(-delta);
       if (result == 0) {
          synchronized(this) {
             if (get()==0) {
               context.hardRefs.remove(this);
             }
          }
       }
       return result;
    }
}
