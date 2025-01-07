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

import java.util.Date;


/**
 * This interface is used by the SAP implementation of <code>{@link javax.management.timer.Timer
 * javax.management.timer.Timer}</code> and allows for pluggable implementations of the JMX
 * timer service, for example one that uses the thread system of a J2EE server when running in a
 * server environment.
 *
 * @author d025700
 */
public interface Timer {
  /**
   * Schedules the given notification.
   *
   * @param notification the notification to schedule.
   */
  public void addNotification(AbstractTimerNotificationInfo notification);

  /**
   * Removes a previously sheduled notification.
   *
   * @param notification the notification to be removed.
   */
  public void removeNotification(AbstractTimerNotificationInfo notification);

  /**
   * Creates a new notification description suitable for this Timer implementation.
   *
   * @param timer a back-reference to the JMX timer service that requested the creation of the
   *        notification info.
   * @param delay date to start relative from now (in milliseconds).
   * @param date absolute date to start.
   * @param period The period of the notification (in milliseconds), 0 means the notification
   *        occures only once.
   * @param occurences The total number the timer notification will be emitted in case of a
   *        periodic notification. 0 means unlimited number of occurences.
   * @param type The timer notification type.
   * @param sequenceNumberGenerator The number generator of the JMX Timer that requested the creation of the
   *        notification info.
   * @param msg The timer notification detailed message.
   * @param id The ID of the timer notification.
   * @param userData The timer notification user data object.
   *
   * @return DOCUMENT ME!
   */
  public AbstractTimerNotificationInfo createNotificationInfo(javax.management.timer.Timer timer,
                                                              long delay, Date date, long period,
                                                              long occurences, String type,
                                                              NumberGenerator sequenceNumberGenerator,
                                                              String msg, Integer id,
                                                              Object userData);
}