/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.domains;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sap.engine.boot.SystemProperties;

/**
 * 
 * @version 7.0
 * @author  Stephan Zlatarev
 */
public class ConfigurationCodeSourceMap {

  private static String binDir = SystemProperties.getProperty("user.dir") + File.separator + ".." + File.separator + "bin";
  private static String appsDir;

  private static Hashtable codeSourceToAlias = new Hashtable();
  private static Hashtable codeSourceToConfiguration = new Hashtable();
  private static Hashtable configurationToRootCodeSource = new Hashtable();

  static {
    try {
      binDir = getCanonicalPath(binDir);
      codeSourceToConfiguration.put(new CodeSource(new File(binDir).toURL(), (Certificate[]) null), "SAP-J2EE-Engine");
      configurationToRootCodeSource.put("SAP-J2EE-Engine", binDir);

      appsDir = SystemProperties.getProperty("j2ee.engine.apps.folder");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getPolicyConfiguration(CodeSource codeSource) {
    String result = (String) codeSourceToConfiguration.get(codeSource);
    String codeSourcePath = null;

    if (result == null) {
      codeSourcePath = getCanonicalPath(codeSource);

      // search in known roots
      String key = null;
      Enumeration enumeration = configurationToRootCodeSource.keys();
      while (enumeration.hasMoreElements()) {
        key = (String) enumeration.nextElement();

        if (codeSourcePath.startsWith((String) configurationToRootCodeSource.get(key))) {
          result = key;
          break;
        }
      }

      // search for policy configuration pattern
      // TODO: this is only dummy implementation
      result = "apps";

      // remember result if not null
      if (result != null) {
        codeSourceToConfiguration.put(codeSource, result);
      }
    }

    return (result != null) ? result : codeSourcePath;
  }

  protected final static String getCodeSourceAlias(CodeSource codeSource) throws IOException {
    String result = (String) codeSourceToAlias.get(codeSource);

    if (result == null) {
      String path = getCanonicalPath(codeSource);

      if ((appsDir != null) && path.startsWith(appsDir)) {
        path = path.substring(appsDir.length() + 1);
      } else if (path.startsWith(binDir)) {
        path = path.substring(binDir.length() + 1);
      }
  
      result = PermissionsStorageUtils.encode(path);
      codeSourceToAlias.put(codeSource, result);
    }

    return result;
  }

  protected final static boolean isSystem(CodeSource codeSource) throws SecurityException {
    return (getCanonicalPath(codeSource).startsWith(binDir));
  }

  private final static String getCanonicalPath(CodeSource codeSource) {
      return getCanonicalPath(codeSource.getLocation().getFile());
  }

  private final static String getCanonicalPath(String file) {
    try {
      return new File(file).getCanonicalPath().replace('\\', '/');
    } catch (Exception exception) {
      // TODO: log this
      return "???";
    }
  }

}
