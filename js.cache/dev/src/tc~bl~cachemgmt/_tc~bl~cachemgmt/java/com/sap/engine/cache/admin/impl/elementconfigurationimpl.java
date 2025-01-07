package com.sap.engine.cache.admin.impl;

import java.io.Serializable;

import com.sap.engine.cache.job.Background;
import com.sap.engine.cache.job.Task;
import com.sap.util.cache.ElementConfiguration;
import com.sap.util.cache.EvictionPeriod;

/**
 * @author Petev, Petio, i024139
 */
public class ElementConfigurationImpl implements ElementConfiguration, Serializable {
  
  private static Background background;

  private EvictionPeriod renewOn;
	private long ttl = -1;
  private long aet = -1;
  
  private RenewTask renewTask = null;
  
  static final long serialVersionUID = 2465617082472670936L;
  
  private class RenewTask implements Task, Serializable {
    
    static final long serialVersionUID = 5686009492989055693L;    
    
    private long renewPeriod;
    
    public void setRenewPeriod(long renewPeriod) {
      this.renewPeriod = renewPeriod;
      background.unregisterTask(this);
      background.registerTask(this);
    }

		public String getName() {
      return "AbsoluteEvictionTime renew: " + renewPeriod;
		}

		public boolean repeatable() {
			return true;
		}

		public int getInterval() {
			return (int)renewPeriod;
		}

		public byte getScope() {
			return 1;
		}

		public void run() {
      aet += renewPeriod;
		}

  }
  
  public ElementConfigurationImpl(Background background) {
    this.renewTask = this.new RenewTask();
    ElementConfigurationImpl.background = background;
  }

  /**
   * Sets the configured cached object time to live for the group
   *
   * @param interval The time (in milliseconds) that objects will be kept after putting them in the cache before nominating them for eviction
   * @throws IllegalArgumentException When interval is non-positive
   */
  public void setTimeToLive(long interval) {
    ttl = interval;
  }

  /**
   * Sets the absolute time of eviction for objects in the group
   *
   * @param time The moment in time (in milliseconds) that objects will be evicted
   * @throws IllegalArgumentException When time is set in the past
   */
  public void setAbsEvictionTime(long time) {
    aet = time;
  }

  /**
   * Returns the configured cached object time to live for the group
   *
   * @return The time (in milliseconds) that objects will be kept after putting them in the cache before nominating them for eviction
   */
  public long getTimeToLive() {
    return ttl;
  }

  /**
   * Returns the absolute time of eviction for objects in the group
   *
   * @return The moment in time (in milliseconds) that objects will be evicted
   */
  public long getAbsEvictionTime() {
    return aet;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setEvictionPeriod(com.sap.util.cache.EvictionPeriod)
   */
  public void setEvictionPeriod(EvictionPeriod period) {
    this.renewOn = period;
    this.renewTask.setRenewPeriod(renewOn.getMilliSeconds());
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setAbsEvictionTime(long, com.sap.util.cache.EvictionPeriod)
   */
  public void setAbsEvictionTime(long period, EvictionPeriod renew) {
    this.aet = period;
    this.renewOn = renew;
    this.renewTask.setRenewPeriod(renewOn.getMilliSeconds());
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setAbsEvictionTime(long, int, int, int)
   */
  public void setAbsEvictionTime(long arg0, int arg1, int arg2, int arg3) {
    this.aet= arg0;
    this.renewOn = new EvictionPeriod(arg1, arg2, arg3);
    this.renewTask.setRenewPeriod(renewOn.getMilliSeconds());
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfigurationInfo#getEvictionPeriod()
   */
  public EvictionPeriod getEvictionPeriod() {
    return renewOn;
  }
  
}
