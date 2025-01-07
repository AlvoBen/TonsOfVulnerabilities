package com.sap.engine.lib.rcm.impl;

import com.sap.engine.lib.rcm.ResourceContext;
import com.sap.engine.lib.rcm.Resource;
import com.sap.engine.lib.rcm.Constraint;
import com.sap.engine.lib.rcm.Notification;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.*;
import java.lang.ref.WeakReference;

/**
 * Created by Asen Petrov.
 * IUser: I030789
 * Date: 2007-12-17
 * Time: 15:01:22
 */
public class ResourceContextImpl implements ResourceContext {

  private String resourceName;
  private String consumerType;
  protected AtomicLong counter;
  protected Resource resource;
  protected Constraint[] constraints;
  protected Notification[] notifications;
  protected final WeakHashMap<String, WeakReference> mapTable;
  private volatile boolean isActive = false;
  protected final CopyOnWriteArrayList<ConsumerCounter> hardRefs = new CopyOnWriteArrayList<ConsumerCounter>();


  public ResourceContextImpl(String resourceName, String consumerType) {
    this.consumerType = consumerType;
    this.resourceName = resourceName;
    counter = new AtomicLong();
    constraints = new Constraint[0];
    notifications = new Notification[0];
    mapTable = new WeakHashMap<String, WeakReference>();
  }

  ConsumerCounter getCounter(String id) {
    synchronized(hardRefs) {
        ConsumerCounter counter = null;
        WeakReference reference = mapTable.get(id);
        if (reference != null) {
            counter = (ConsumerCounter)reference.get();
        }
        if (counter == null)  {
          counter = new ConsumerCounter(id, this);
          if (reference != null) {
            mapTable.remove(id);
          }
          mapTable.put(id, new WeakReference(counter));
        }
        return counter;
    }
  }

  public String getConsumerType() {
    return consumerType;
  }

  public Resource getResource() {
    return  resource;
  }

  public String getResourceName() {
    return resourceName;
  }

  public synchronized void addConstraint(Constraint constraint) {
      Constraint[] temp = new Constraint[constraints.length + 1];
      System.arraycopy(constraints, 0, temp, 0, constraints.length);
      temp[constraints.length] = constraint;
      constraints = temp;

  }

  public synchronized void addConstraints(Constraint[] constraints) {
      Constraint[] temp = new Constraint[this.constraints.length + constraints.length];
      System.arraycopy(this.constraints, 0, temp, 0, this.constraints.length);
      System.arraycopy(constraints, 0, temp, this.constraints.length, constraints.length);
      this.constraints = temp;
   }

  public synchronized void insertConstraint(Constraint constraint) {
      insertConstraint(constraint, 0);
  }

  public synchronized void insertConstraint(Constraint constraint, int index) {
      Constraint[] temp = new Constraint[constraints.length + 1];
      System.arraycopy(constraints, 0, temp, 0, index);
      temp[index] = constraint;
      System.arraycopy(constraints, index, temp, index + 1, constraints.length - index);
      constraints = temp;
  }

  public synchronized void removeConstraint(Constraint constraint) {
      int index = 0;
      for (int i = 0; i < constraints.length; i++)  {
        if (constraints[i].equals(constraint)) {
          index = i;
          break;
        }
      }
      Constraint[] temp = new Constraint[constraints.length - 1];
      System.arraycopy(constraints, 0, temp, 0, index);
      System.arraycopy(constraints, index + 1, temp, index, constraints.length - index + 1);
      constraints = temp;
  }

  public synchronized void addNotification(Notification notification) {
    Notification[] temp = new Notification[notifications.length + 1];
    System.arraycopy(notifications, 0, temp, 0, notifications.length);
    temp[notifications.length] = notification;
    notifications = temp;
  }

  public synchronized void removeNotification(Notification notification) {
    int index = 0;
      for (int i = 0; i < notifications.length; i++)  {
        if (notifications[i].equals(notification)) {
          index = i;
          break;
        }
      }
      Notification[] temp = new Notification[notifications.length - 1];
      System.arraycopy(notifications, 0, temp, 0, index);
      System.arraycopy(notifications, index + 1, temp, index, notifications.length - index + 1);
      notifications = temp;

  }

  public synchronized void removeConstraints() {
    constraints = new Constraint[0];
  }

  public long getCurrentUsage(String id) {
    AtomicLong counter;
    WeakReference weak = mapTable.get(id);
    if (weak != null) {
       counter = (ConsumerCounter)weak.get();
       if (counter != null) {
         return counter.longValue();
       }
    }
    return 0;
  }

  public long getTotalUsage() {
    return counter.get();
  }

  public  boolean isActive() {
    return isActive;
  }

  protected void setActive(boolean active) {
    isActive = active;
  }

}
