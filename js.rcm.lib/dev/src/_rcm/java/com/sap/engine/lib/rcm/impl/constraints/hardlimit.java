package com.sap.engine.lib.rcm.impl.constraints;

import com.sap.engine.lib.rcm.Constraint;
import com.sap.engine.lib.rcm.ResourceConsumer;

/**
 * Created by Asen Petrov.
 * IUser: I030789
 * Date: 2007-12-17
 * Time: 15:07:38
 */
public class HardLimit implements Constraint {

  private String id;
  private long limit;

  public  HardLimit(String id, long limit) {
    this.limit = limit;
    this.id = id;
  }

  public boolean preConsume(ResourceConsumer consumer, long currentUsage, long proposedUsage) {
     if (id == null || id.equals(consumer.getId())) {
       if (proposedUsage > limit)  {
         return false;
       }
     }
     return true;
  }
}
