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

import javax.management.timer.TimerNotification;
import java.util.Date;


/**
 * Describes a timer notification to be scheduled by an implementation of the <code>{@link
 * com.sap.pj.jmx.timer.Timer com.sap.pj.jmx.timer.Timer}</code> interface. Providers of
 * <code>com.sap.pj.jmx.timer.Timer</code> have to subclass this abstract class in order to adapt
 * it to their native notification/sheduler mechanism.
 *
 * @author d025700
 */
public abstract class AbstractTimerNotificationInfo {
  private final javax.management.timer.Timer timer;
  private long delay;
  private Date date;
  private final long period;
  private long occurences;
  protected final String type;
  protected final Object source;
  private final NumberGenerator sequenceNumberGenerator;
  protected final String msg;
  protected final Integer id;
  private final Object userData;

  /**
   * Creates a new AbstractTimerNotificationInfo object and initializes the properties with the
   * given values.
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
   * @param sequenceNumberGenerator The number generator of the JMX Timer that requested the
   *        creation of the notification info.
   * @param msg The timer notification detailed message.
   * @param id The ID of the timer notification.
   * @param userData The timer notification user data object.
   */
  public AbstractTimerNotificationInfo(final javax.management.timer.Timer timer, final long delay,
                                       final Date date, final long period, final long occurences,
                                       final String type,
                                       final NumberGenerator sequenceNumberGenerator,
                                       final String msg, final Integer id, final Object userData) {
    this.timer = timer;
    this.delay = delay;
    this.date = date;
    this.period = period;
    this.occurences = occurences;
    this.type = type;
    this.source = timer;
    this.sequenceNumberGenerator = sequenceNumberGenerator;
    this.msg = msg;
    this.id = id;
    this.userData = userData;
  }

  /**
   * Forwards the timer notification represented by this info to the JMX Timer {@link
   * javax.management.timer.Timer#sendNotification(javax.management.Notification)
   * <code>sendNotification(Notification)</code>}. This has to be called by the sheduler.
   */
  protected final void sendNotification() {
    TimerNotification notification = createNotification();
    notification.setSequenceNumber(sequenceNumberGenerator.getNextNumber());
    notification.setTimeStamp(date.getTime());
    notification.setUserData(userData);
    timer.sendNotification(notification);
  }

  /**
   * Creates an <i>empty</i> TimerNotification by calling <code>new TimerNotification(type, source,
   * 0, 0, msg, id)</code>. Subclasses can override this to create their own TimerNotifications
   * here. This is called by <code>sendNotification()</code> which fills in sequenceNumber,
   * timeStamp and userData afterwards.
   *
   * @return the newly created TimerNotification.
   */
  protected TimerNotification createNotification() {
    return new TimerNotification(type, source, 0, 0, msg, id);
  }

  /**
   * Updates a timer notification occurences and daly accoring to the current date. This is called
   * if the notification was registered with the JMX timer before it was started.
   *
   * @param now the current date.
   * @param sendPast indicates whether past events should be sent too.
   *
   * @return <code>false</code> in case of either a non-periodic event or the last occurence of a
   *         limited period, <code>true</code> otherwise.
   */
  public final boolean update(Date now, boolean sendPast) {
    synchronized (this) {
      while (now.after(date)) {
        // past notification - send if enabled
        if (sendPast) {
          sendNotification();
        }

        // periodic notification - update info
        if (period > 0L) {
          // either unlimited period or everything but last occurence found - update date and occurrences
          if ((occurences == 0L) || (occurences > 1L)) {
            date.setTime(date.getTime() + period);
            occurences = java.lang.Math.max(0L, occurences - 1L);

            continue;
          }
        }


        // was non-periodic or last occurence of a limited period - remove
        delay = -1;

        return false;
      }
      delay = date.getTime() - now.getTime();

      return true;
    }
  }

  /**
   * Returns the absolute start date of the notification.
   *
   * @return the start date.
   */
  public final Date getDate() {
    return date;
  }

  /**
   * Returns the start date of the notification relative from now.
   *
   * @return the relative start date.
   */
  public final long getDelay() {
    return delay;
  }

  /**
   * Returns the period.
   *
   * @return the period.
   */
  public final long getPeriod() {
    return period;
  }

  /**
   * Returns the number of occurences.
   *
   * @return the number of occurences.
   */
  public final long getOccurences() {
    return occurences;
  }

  /**
   * Returns the timer notification type.
   *
   * @return the timer notification type.
   */
  public final String getType() {
    return type;
  }

  /**
   * Returns the timer notification detailed message.
   *
   * @return the timer notification detailed message.
   */
  public final String getMessage() {
    return msg;
  }

  /**
   * Returns the timer notification ID.
   *
   * @return the timer notification ID.
   */
  public final Integer getID() {
    return id;
  }

  /**
   * Returns the timer notification user data.
   *
   * @return the timer notification user data.
   */
  public final Object getUserData() {
    return userData;
  }

  /**
   * Method getDisplayName.
   * @return String
   */
  protected String getDisplayName() {
    return "abstract-timer"; //$NON-NLS-1$
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[ JMX "); //$NON-NLS-1$
    sb.append(getDisplayName());
    sb.append(" |"); //$NON-NLS-1$
    sb.append(" id: "); //$NON-NLS-1$
    sb.append(id);
    sb.append(" date: "); //$NON-NLS-1$
    sb.append(date);
    sb.append(" period: "); //$NON-NLS-1$
    sb.append(period);
    sb.append(" occurences: "); //$NON-NLS-1$
    sb.append(occurences);
    sb.append(" source: "); //$NON-NLS-1$
    sb.append(source == null ? null : source.toString());
    sb.append(" type: "); //$NON-NLS-1$
    sb.append(type);
    sb.append(" msg: "); //$NON-NLS-1$
    sb.append(msg);
    sb.append(" ]"); //$NON-NLS-1$
    return sb.toString();
  }

}