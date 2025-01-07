package com.sap.engine.cache.core.impl;

import com.sap.engine.cache.admin.RegionConfiguration;
import com.sap.engine.cache.job.Task;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.spi.policy.EvictionPolicy;

/**
 * @author Petev, Petio, i024139
 */
public class EvictionWorker implements Task {

  static final long serialVersionUID = -6561214791448357655L;
  
  public static final byte MODE_UNDEFINED = -1;
  public static final byte MODE_PASSIVE = 0;
  public static final byte MODE_BACKGROUND = 1;
  public static final byte MODE_ACTIVE = 2;
  public static final byte MODE_CRITICAL = 3;

  private byte scope;
  private String name;
  private int interval;
  private boolean repeatable;
  private transient EvictionPolicy eviction;
  private int count_L1;
  private int count_L2;
  private int count_L3;
  private int size_L1;
  private int size_L2;
  private int size_L3;
  private byte mode = MODE_UNDEFINED;
  private transient CacheRegionImpl region = null;

  private int activeCount = 0;

  public EvictionWorker(CacheRegionImpl region, int interval, boolean repeatable) {
    RegionConfiguration configuration = region.getRegionConfiguration();
    this.scope = configuration.getRegionScope();
    this.region = region;
    this.name = configuration.getName();
    this.interval = interval;
    this.repeatable = repeatable;
    this.eviction = configuration.getEvictionPolicy();
    this.count_L1 = configuration.getCountQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
    this.count_L2 = configuration.getCountQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
    this.count_L3 = configuration.getCountQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
    this.size_L1 = configuration.getSizeQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
    this.size_L2 = configuration.getSizeQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
    this.size_L3 = configuration.getSizeQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
  }
  
  public void resize() {
    RegionConfiguration configuration = region.getRegionConfiguration();
    this.count_L1 = configuration.getCountQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
    this.count_L2 = configuration.getCountQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
    this.count_L3 = configuration.getCountQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
    this.size_L1 = configuration.getSizeQuota(RegionConfigurationInfo.START_OF_EVICTION_THRESHOLD);
    this.size_L2 = configuration.getSizeQuota(RegionConfigurationInfo.UPPER_LIMIT_THRESHOLD);
    this.size_L3 = configuration.getSizeQuota(RegionConfigurationInfo.CRITICAL_LIMIT_THRESHOLD);
    active();
  }

  /**
   * Returns the name of the task. Must not exceed 40 characters.
   *
   * @return The name of the task
   */
  public String getName() {
    return this.name;
  }

  /**
   * Denotes if the task is a repeatable one. Repeatable tasks must provide a valid value for the interval
   * between repetitions. If a task is not repeatable. The interval means the time that will pass before
   * the task can be executed. Non-repeatable tasks are automatically unregistered after execution.
   *
   * @return True if the task is a repeatable one.
   *
   */
  public boolean repeatable() {
    return repeatable;
  }

  void stop() {
    repeatable = false;
  }

  /**
   * The interval between successive executions. For non-repeatable tasks the interval is meant for waiting
   * before the execution of the task.
   *
   * @return The interval in milliseconds.
   */
  public int getInterval() {
    return this.interval;
  }

  /**
   * @return The scope of the task. Can be
   * <code>SCOPE_EVERY_NODE</code> - The task will be executed on every node
   * <code>SCOPE_EVERY_MACHINE</code> - The task will be executed on one node per machine
   * <code>SCOPE_ONE_NODE</code> - The task will be executed only on one node of the cluster
   */
  public byte getScope() {
    return this.scope;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see     Thread#run()
   */
  public void run() {
    if (mode != MODE_CRITICAL) {
      
      byte oldMode = mode;
            
      int currentCount = eviction.getCount();
      int currentSize = eviction.getSize();
      
      byte cmode;
      byte smode;

      if (currentCount < count_L1) {
        cmode = MODE_PASSIVE;
      } else if (currentCount < count_L2) {
        cmode = MODE_BACKGROUND;
      } else {
        cmode = MODE_ACTIVE;
      }
      
      if (currentSize < size_L1) {
        smode = MODE_PASSIVE;
      } else if (currentSize < size_L2) {
        smode = MODE_BACKGROUND;
      } else {
        smode = MODE_ACTIVE;
      }
      
      mode = cmode < smode ? smode : cmode;
            
      if (oldMode == MODE_ACTIVE && mode == MODE_ACTIVE) {
        activeCount++;
        if (activeCount == 3) {
          mode = MODE_BACKGROUND;
          activeCount = 0;
        }
      }
      if (mode == MODE_BACKGROUND) {
        synchronized (region) {
          String key = eviction.choose();
          if (key != null) {
            region.evict(key);
          }
        }
      }
    }
  }

  public void active() {
    if (mode == MODE_ACTIVE || mode == MODE_CRITICAL) {
      synchronized (region) {
        String key = eviction.choose();
        if (key != null) {
          region.evict(key);
        }
      }
    }
    int currentCount = eviction.getCount();
    int currentSize = eviction.getSize();
    
    byte cmode = mode;
    byte smode = mode;

    if (currentCount < count_L1) {
      cmode = MODE_PASSIVE;
    } else if (currentCount < count_L2) {
      cmode = MODE_BACKGROUND;
    } else if (currentCount < count_L3) {
      cmode = MODE_ACTIVE;
    } else {
      cmode = MODE_CRITICAL;
    }
    
    if (currentSize < size_L1) {
      smode = MODE_PASSIVE;
    } else if (currentSize < size_L2) {
      smode = MODE_BACKGROUND;
    } else if (currentSize < size_L3) {
      smode = MODE_ACTIVE;
    } else {
      smode = MODE_CRITICAL;
    }
    
    byte newMode = cmode < smode ? smode : cmode;

    if (newMode == MODE_CRITICAL && mode != MODE_CRITICAL) {
      // if CRITICAL, evict until MODE_ACTIVE
      mode = newMode;
      while (mode == MODE_CRITICAL || mode == MODE_ACTIVE) {
        active();
      }
    } else {
      mode = newMode;
    }
    
  }

}
