package com.sap.engine.cache.admin.impl;

import com.sap.engine.cache.admin.Monitor;
import com.sap.util.cache.RegionConfigurationInfo;

/**
 * @author Petev, Petio, i024139
 */
public class LocalMonitor implements Monitor {

  private int size;
  private int attributesSize;
  private int namesSize;
  private int count;
  private int puts;
  private int modifications;
  private int removals;
  private int evictions;
  private int utilization;
  private int gets;
  private int successfulGets;
  private int localNot;
  private int instanceNot;
  private int clusterNot;
  private String name = null;

  private Monitor hook = null;
  
  public LocalMonitor(String name) {
    this.name = name;
    MonitorsAccessor.addMonitor(this);
  }

  /**
   * Gets the current total size of cached objects
   *
   * @return The current total size of cached objects in bytes in the region
   */
  public int size() {
    if (hook != null) {
      return hook.size();
    }
    synchronized (this) {
      return size;
    }
  }

  public int attributesSize() {
    if (hook != null) {
      return hook.attributesSize();
    }
    synchronized (this) {
      return attributesSize;
    }
  }

  public int namesSize() {
    if (hook != null) {
      return hook.namesSize();
    }
    synchronized (this) {
      return namesSize;
    }
  }

  public String name() {
    return name;
  }

  /**
   * Gets the current total count of cached objects
   *
   * @return The current total count of cached objects in the region
   */
  public int count() {
    if (hook != null) {
      return hook.count();
    }
    synchronized (this) {
      return count;
    }
  }

  /**
   * Returns the current hit rate in promilles (1000 * successful cached object gets / all cached objects gets)
   *
   * @return The current hit rate in the region
   */
  public int hitRate() {
    if (hook != null) {
      return hook.hitRate();
    }
    synchronized (this) {
      return gets == 0 ? 0 : (int) ((1000.0 * successfulGets) / gets);
    }
  }

  /**
   * Gets the total number of put operations since creation of the region
   *
   * @return Total number of put operations
   */
  public int puts() {
    if (hook != null) {
      return hook.puts();
    }
    synchronized (this) {
      return puts;
    }
  }

  /**
   * Gets the total number of modification (successive puts on the same cached object key) since creation of the region
   *
   * @return Total number of modification operations
   */
  public int modifications() {
    if (hook != null) {
      return hook.modifications();
    }
    synchronized (this) {
      return modifications;
    }
  }

  /**
   * Gets the total number of remove operations since creation of the region
   *
   * @return The total number of remove operations
   */
  public int removals() {
    if (hook != null) {
      return hook.removals();
    }
    synchronized (this) {
      return removals;
    }
  }

  /**
   * Gets the total number of evictions that eviction policy has made in the region
   *
   * @return The total number of evictions
   */
  public int evictions() {
    if (hook != null) {
      return hook.evictions();
    }
    synchronized (this) {
      return evictions;
    }
  }

  /**
   * Gets the utilization of the cache region - accesses (all kinds of operations) per minute
   * @return The cache region utilization
   */
  public int utilization() {
    if (hook != null) {
      return hook.utilization();
    }
    synchronized (this) {
      return utilization;
    }
  }


  public void setHook(Monitor hook) {
    this.hook = hook;
  }

  public Monitor getHook() {
    return hook;
  }

  public String toString() {
    synchronized(this) {
      StringBuffer buffer = new StringBuffer();
      buffer.append("\n[Region Monitor] {");
      buffer.append("\n  Name            : " + name());
      buffer.append("\n  Size            : " + size());
      buffer.append("\n  Attributes Size : " + attributesSize());
      buffer.append("\n  Names size      : " + namesSize());
      buffer.append("\n  Count           : " + count());
      buffer.append("\n  Hit Rate        : " + hitRate() / 10 + "." + hitRate() % 10);
      buffer.append("\n  Puts            : " + puts());
      buffer.append("\n  Modifications   : " + modifications());
      buffer.append("\n  Removals        : " + removals());
      buffer.append("\n  Evictions       : " + evictions());
      buffer.append("\n  Utilization     : " + utilization());
      buffer.append("\n  Gets            : " + gets());
      buffer.append("\n  Successful gets : " + hits());
      buffer.append("\n  I Notifications : " + instanceNot);
      buffer.append("\n  C Notifications : " + clusterNot);
      buffer.append("\n}");
      return buffer.toString();
    }
  }


  public void finalize() throws Throwable {
    super.finalize();
    MonitorsAccessor.removeMonitor(this);
  }

	public int gets() {
    if (hook != null) {
      return hook.gets();
    }
    synchronized (this) {
      return gets;
    }
	}

	public int hits() {
    if (hook != null) {
      return hook.hits();
    }
    synchronized (this) {
      return successfulGets;
    }
	}

  ////////////////////////////////////////////////////////////////////
  
  public void onModify(int oldSize, int oldAttrSize, int newSize, int newAttrSize) {
    if (hook == null) {
      synchronized (this) {
        size += newSize - oldSize;
        attributesSize += newAttrSize - oldAttrSize;
        modifications++;
        puts++;
      }
    } else {
      hook.onModify(oldSize, oldAttrSize, newSize, newAttrSize);
    }
  }

  public void onPut(int size, int attrSize, int nameSize) {
    if (hook == null) {
      synchronized (this) {
        count++;
        if (utilization < count) utilization = count;
        this.size += size;
        this.attributesSize += attrSize;
        this.namesSize += nameSize;
        puts++;
      }
    } else {
      hook.onPut(size, attrSize, nameSize);
    }
  }

  public void onRemove(int oldSize, int oldAttrSize, int nameSize) {
    if (hook == null) {
      synchronized (this) {
        count--;
        this.size -= oldSize;
        this.attributesSize -= oldAttrSize;
        this.namesSize -= nameSize;
        removals++;
      }
    } else {
      hook.onRemove(oldSize, oldAttrSize, nameSize);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.cache.admin.Monitor#onEvict(int, int, int)
   */
  public void onEvict(int oldSize, int oldAttrSize, int nameSize) {
    if (hook == null) {
      synchronized (this) {
        count--;
        this.size -= oldSize;
        this.attributesSize -= oldAttrSize;
        this.namesSize -= nameSize;
        evictions++;
      }
    } else {
      hook.onEvict(oldSize, oldAttrSize, nameSize);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.cache.admin.Monitor#onGet(boolean)
   */
  public void onGet(boolean success) {
    if (hook == null) {
      synchronized (this) {
        gets++;
        if (success) {
          successfulGets++;
        }
      }
    } else {
      hook.onGet(success);
    }
  }
  
  public void onNotify(int scope) {
    if (scope != RegionConfigurationInfo.SCOPE_NONE) {
      if (hook == null) {
        synchronized (this) {
          if (scope == RegionConfigurationInfo.SCOPE_LOCAL) {
            localNot++;
          } else if (scope == RegionConfigurationInfo.SCOPE_INSTANCE) {
            instanceNot++;
          } else if (scope == RegionConfigurationInfo.SCOPE_CLUSTER) {
            clusterNot++;
          }
        }
      } else {
        hook.onNotify(scope);
      }
    }
  }

}
