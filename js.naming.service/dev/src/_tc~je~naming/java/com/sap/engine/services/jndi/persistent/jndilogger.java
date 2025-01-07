/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.jndi.persistent;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Category;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.engine.system.naming.provider.LogHelper;

/*
 * 
 * @author Elitsa Pancheva
 * @version 6.30
 */
public class JNDILogger implements LogHelper {

  Location loc = null;
  Category cat = null;

  public JNDILogger(Location loc, Category cat) {
    this.loc = loc;
    this.cat = cat;
  }

  public boolean toLogPathInLocation() {
    return loc.bePath();
  }

  public boolean toLogInfoInLocation() {
    return loc.beInfo();
  }
 
  public boolean toLogErrorInLocation() {
    return loc.beError();
  }

  public boolean toLogWarningInLocation() {
    return loc.beWarning();
  }

  public void logError(String message) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), message);
      loc.errorT(formater.toString());
    } //$JL-SEVERITY_TEST$
  }

  public void logError(String key, Object[] parameters) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), key, parameters);
      loc.errorT(formater.toString());
    } //$JL-SEVERITY_TEST$
  }


  public void logInfo(String message) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), message);
      loc.infoT(formater.toString());
    }
  }

  public void logInfo(String key, Object[] parameters) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), key, parameters);
      loc.infoT(formater.toString());
    }
  }
  
  public void logWarning(String message) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), message);
      loc.errorT(formater.toString());
    } //$JL-SEVERITY_TEST$
  }

  public void logWarning(String key, Object[] parameters) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), key, parameters);
      loc.errorT(formater.toString());
    } //$JL-SEVERITY_TEST$
  }

  public void logPath(String message) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), message);
      loc.pathT(formater.toString());
    }
  }

  public void logPath(String key, Object[] parameters) {
    if (loc != null) {
      LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), key, parameters);
      loc.pathT(formater.toString());
    }
  }

  public void logThrowable(Throwable throwable) {
    if (loc != null) {
      loc.throwing(throwable);
    }
  }


  public void logCatching(Throwable throwable) {
    if (loc != null) {
      loc.catching(throwable);
    }
  }

  public void logStringInPath(String s) {
    if (loc != null) {
      loc.pathT(s);
    }
  }

  public void logStringInInfo(String s) {
    if (loc != null) {
      loc.infoT(s);
    }
  }

}
