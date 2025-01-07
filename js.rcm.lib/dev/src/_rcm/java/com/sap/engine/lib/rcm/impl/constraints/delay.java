package com.sap.engine.lib.rcm.impl.constraints;

import com.sap.engine.lib.rcm.Constraint;
import com.sap.engine.lib.rcm.ResourceConsumer;
import com.sap.engine.lib.rcm.impl.ResourceContextImpl;

/**
 * Created by Asen Petrov.
 * IUser: I030789
 * Date: 2007-12-17
 * Time: 18:20:01
 */
public class Delay implements Constraint {
  private long limit;
  private long timeout;
  private long retryInterval;
  private ResourceContextImpl context;
  private String id;


  public Delay (ResourceContextImpl context, String id, long limit, long timeout, long retryInterval)  {
    this.context = context;
    this.id = id;
    this.limit = limit;
    this.timeout = timeout;
    this.retryInterval = retryInterval;
  }
  
  public boolean preConsume(ResourceConsumer consumer, long currentUsage, long proposedUsage) {
    if (id == null || id.equals(consumer.getId())) {
        long startTime = System.currentTimeMillis();
        long usage = 0;
        long increase = proposedUsage - currentUsage;
        while (System.currentTimeMillis() - startTime < timeout) {
          usage = context.getCurrentUsage(consumer.getId());
          if (usage + increase <= limit) {
            return true;
          } else {
            try {
              Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
               continue;
            }
          }
        }
    }
    return true;
  }
}
