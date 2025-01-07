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
* Created on 2005.3.18
*/
package com.sap.engine.frame.core.cache;

import com.sap.exception.BaseException;
import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Location;

/**
 * @author Petio Petev, i024139, petio.petev@sap.com
 */
public class CacheContextException extends BaseException {
  
  static final long serialVersionUID = -746691821123751035L;
  
  public final static String CANNOT_PARSE_XML                = "kernel_7500";
  public final static String STREAM_CORRUPTED                = "kernel_7501";
  public final static String NO_CONFIGURATION_FOUND          = "kernel_7502";
  public final static String GLOBALS_MODIFIED                = "kernel_7503";
  public final static String COULD_NOT_CREATE_REGIONS        = "kernel_7504";
  public final static String COULD_NOT_DESTROY_REGIONS       = "kernel_7505";
  public final static String CACHE_EXCEPTION_ON_CREATE       = "kernel_7506";
  public final static String NULLPOINTER_EXCEPTION_ON_CREATE = "kernel_7507";
  public final static String EXCEPTION_DURING_DESTROY        = "kernel_7508";
  public final static String COMBINATOR_STORAGE_FAILED       = "kernel_7509";
  public final static String NO_SUCH_REGION                  = "kernel_7510";
  public final static String NO_SUCH_GROUP                   = "kernel_7511";
  public final static String NO_SUCH_KEY                     = "kernel_7512";
  
  private static ResourceAccessor _accessor = null;
  
  public static void setEnvironment() {
    _accessor = CacheResourceAccessor.getInstance();
  }
  
  public CacheContextException(Location location, ResourceAccessor accessor, String key) {
    super(location, accessor, key);
  }

  public CacheContextException(Location location, ResourceAccessor accessor, String key, Object[] params) {
    super(location, accessor, key, params);
  }
  
  public CacheContextException(Location location, ResourceAccessor accessor, String key, Throwable root) {
    super(location, accessor, key, root);
  }
  
  public CacheContextException(Location location, ResourceAccessor accessor, String key, Object[] params, Throwable root) {
    super(location, accessor, key, params, root);
  }
  
  public CacheContextException(Location location, String key) {
    super(location, _accessor, key);
  }

  public CacheContextException(Location location, String key, Object[] params) {
    super(location, _accessor, key, params);
  }
  
  public CacheContextException(Location location, String key, Throwable root) {
    super(location, _accessor, key, root);
  }
  
  public CacheContextException(Location location, String key, Object[] params, Throwable root) {
    super(location, _accessor, key, params, root);
  }
  
}
