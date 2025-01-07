/*
 * Created on 2005.2.16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BenchConfig implements Serializable {

  static final long serialVersionUID = -946691823123751035L;
  
  Properties regionConfiguration = null;
  String storagePluginName       = null;
  String evictionPolicyName      = null;
  int threads                    = -1;
  int period                     = -1;
  int scope                      = -1;
  int operations                 = -1;
  int factor                     = -1;

}
