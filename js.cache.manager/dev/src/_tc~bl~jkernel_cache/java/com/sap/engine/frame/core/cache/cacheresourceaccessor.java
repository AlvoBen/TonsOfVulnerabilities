/*
* Copyright (c) 2005 by SAP AG, Walldorf.,
* http://www.sap.com
* All rights reserved.
*
* This software is the confidential and proprietary information
* of SAP AG, Walldorf. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms
* of the license agreement you entered into with SAP.
*
* Created on 2005.3.21
*/
package com.sap.engine.frame.core.cache;

import com.sap.localization.ResourceAccessor;

/**
 * @author Petio Petev, i024139, petio.petev@sap.com
 */
public class CacheResourceAccessor extends ResourceAccessor {

  static final long serialVersionUID = -846691821123751035L;
  
  private static final String BUNDLE_NAME = "com.sap.engine.frame.core.cache.CacheResourceBundle";
  private static final CacheResourceAccessor instance = new CacheResourceAccessor();

  private CacheResourceAccessor() {
    super(BUNDLE_NAME);
  }

  public static CacheResourceAccessor getInstance() {
    return instance;
  }
}
