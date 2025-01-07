/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.usr;

import com.sap.engine.session.exec.CounterLimitException;
import java.io.IOException;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public interface ClientContext {
  void persistClientSession(ClientSession session) throws IOException;
  void removeClientSession(ClientSession session);
  void addSession(ClientSession session);
  void concernAdditionalStates(AdditionalNotification notificator);
  boolean isConcernedForAdditionalStates();
  void exitsThread();

  /**
   * Sets the max counter limit and resets the counter to 0
   * @param limit the limit / -1 value for no limit
   */
  public void setMaxCounterLimit(int limit);

  /**
   * Gets the counter limit
   * @return the limit
   */
  public int getMaxCounterLimit();

  /**
   * Increases the session counter by 1
   * @throws CounterLimitException if the counting limit is reached
   */
  public void increaseCounter() throws CounterLimitException;

  /**
   * Decreases the session counter by 1
   */
  public void decreaseCounter();

  /**
   * Gets the current counter value
   * @return the counter value
   */
  public int getCurrentCount();

  /**
   * Gets the left count
   * @return the left count
   */
  public int getLeftCount();

  /**
   * Logs out this current client
   */
  public void logout();

}
