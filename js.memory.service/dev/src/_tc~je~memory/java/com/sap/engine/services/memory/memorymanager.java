/* Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory;

/**
 * <code>MemoryManager</code> is used for managing usage of memory available for a JVM.
 * To allow <code>MemoryManager</code> to notify some other class about memory usage the other
 * class must implements <code>MemoryBalance</code> interface. When the object
 * is created it is registered by using <code>register</code> method of
 * MemoryManager class. Once the object is regestered it will be notified for
 * memory usage every time the usage changes from one level to other. These levels
 * are defined in advance.
 *
 * @author Doichin Tsvetkov, Iliyan Nenov
 * @version 4.0
 */
public interface MemoryManager {

  /**
   * Registers a <code>MemoryListener</code> object and starts to inform that object about
   * the level of usage of system memory.
   *
   * @param     listener - object that is going to be registered
   *
   * @return    an unique value used for unregistering this MemoryListener object
   */
  public int registerMemoryListener(MemoryListener listener);


  /**
   * Unregisters already registered MemoryListener.
   *
   * @param   listenerId - the value returned by <code>register</code> method.
   */
  public void unregisterMemoryListener(int listenerId);


  /**
   *	Accessor method.
   *
   * @return     The total memory of the JVM.
   */
  public long getMaxMemory();


  /**
   * Checks for free memory.
   *
   * @param   memory  The needed memory.
   * @return     True - if there is available memory.
   */
  public boolean isFree(long memory);


  /**
   * Calculates the JVM free memory.
   *
   * @return     The free memory.
   */
  public long getFreeMemory();

}

