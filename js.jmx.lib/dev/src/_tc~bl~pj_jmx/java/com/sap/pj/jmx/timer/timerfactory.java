/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.pj.jmx.timer;

/**
 * Providers of <code>{@link com.sap.pj.jmx.timer.Timer com.sap.pj.jmx.timer.Timer}</code>
 * implementations have to  provide an implementation of <code>TimerFactory</code> too. The
 * <code>TimerFactory</code> is used by the constructor of <code>{@link javax.management.timer.Timer
 * javax.management.timer.Timer}</code> in the following way.
 * <pre>
 *     // get the class name of the TimerFactory implementation from system property "com.sap.pj.jmx.TimerFactory"
 *     String className = System.getProperty(com.sap.pj.jmx.ConstantDefinitions.TIMER_FACTORY_CLASS_PROPERTY);
 *     ...
 *     // try to load the class first using the thread context cl and if this fails using the cl
 *     // that loaded javax.management.timer.Timer
 *     Class clazz = classLoader.loadClass(className);
 *     ...
 *     // create an instance using the default constructor
 *     TimerFactory factory = (TimerFactory) clazz.newInstance();
 *     ...
 *     // get an instance of com.sap.pj.jmx.timer.Timer from the factory
 *     Timer timer = factry.getTimer();
 *     ...
 * </pre>
 *
 * @author d025700
 */
public interface TimerFactory {
  /**
   * Returns a <code>Timer</code> implementation.
   *
   * @return a timer.
   */
  public Timer getTimer();
}