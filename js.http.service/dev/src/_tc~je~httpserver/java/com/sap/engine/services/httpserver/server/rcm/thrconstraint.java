/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sap.engine.services.httpserver.server.rcm;

import com.sap.engine.lib.rcm.Constraint;
import com.sap.engine.lib.rcm.ResourceConsumer;

/**
 *
 * @author i024157
 */
public class ThrConstraint implements Constraint {

  private ThreadUsageMonitor monitor;
  private int maxPerConsumer;

  public ThrConstraint(int maxPerConsumer, ThreadUsageMonitor monitor) {
    this.maxPerConsumer = maxPerConsumer;
    this.monitor = monitor;
  }
    
    
    
  public boolean preConsume(ResourceConsumer cnsumer, long currentUsage, long proposedUsage) { 
    if (proposedUsage >= maxPerConsumer) {
      monitor.setUnavailable(cnsumer.getId());
    }
    boolean result = !(proposedUsage > maxPerConsumer);
    if (!result) {
      monitor.incNumberOfUnavailable(cnsumer.getId());
    }
    return result;
  }
    
}
