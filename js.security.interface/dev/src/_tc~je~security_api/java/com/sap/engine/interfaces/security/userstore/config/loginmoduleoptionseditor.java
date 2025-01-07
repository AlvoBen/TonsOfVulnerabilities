/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.config;

import java.util.Map;

/**
 *  Editor for login module options. All implementations MUST implement java.awt.Component.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public interface LoginModuleOptionsEditor {

  /**
   *  The GUI container uses this method to get the modified options of the edited login module.
   *
   * @return  the modified login module options.
   */
  public abstract Map getOptions();

  /**
   *  The GUI container invokes this method with the options of the login module to be edited.
   *
   * @param  options  the current options of the selected login module.
   */
  public abstract void setOptions(Map options);

}
