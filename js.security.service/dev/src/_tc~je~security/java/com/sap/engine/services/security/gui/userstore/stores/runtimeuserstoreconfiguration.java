package com.sap.engine.services.security.gui.userstore.stores;
import java.lang.reflect.Method;
import java.util.Properties;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Severity;

public class RuntimeUserStoreConfiguration implements UserStoreConfiguration {

  public final static long serialVersionUID = 2697243797387521990L;

  private String anonymous = null;
  private String name = null;
  private String description = null;
  private String userSpi = null;
  private String groupSpi = null;
  private String editor = null;
  private Properties properties = null;
  private UserStoreConfiguration configuration = null;
  private LoginModuleConfiguration[] loginModules = null;

  public RuntimeUserStoreConfiguration(UserStoreConfiguration configuration) {
    this.configuration = configuration;
  }

  public String getAnonymousUser() {
    return anonymous;
  }

  /**
   *  Returns the description of the user store.
   *
   * @return  printable text.
   */
  public String getDescription() {
    if (description != null) {
      return description;
    } else if (configuration != null) {
      return configuration.getDescription();
    } else {
      return null;
    }
  }


  /**
   *  Returns the display name of the user store.
   *
   * @return  display name.
   */
  public String getName() {
    if (name != null) {
      return name;
    } else if (configuration != null) {
      return configuration.getName();
    } else {
      return null;
    }
  }


  /**
   *  Returns the configured login modules for this user store.
   *
   * @return  an array of login module configurations.
   */
  public LoginModuleConfiguration[] getLoginModules() {
    if (loginModules != null) {
      return loginModules;
    } else if (configuration != null) {
      return configuration.getLoginModules();
    } else {
      return new LoginModuleConfiguration[0];
    }
  }


  /**
   *  Returns the class name of the user context spi for the user store.
   *
   * @return  class name.
   */
  public String getUserSpiClassName() {
    if (userSpi != null) {
      return userSpi;
    } else if (configuration != null) {
      return configuration.getUserSpiClassName();
    } else {
      return null;
    }
  }


  /**
   *  Returns the class name of the group context spi for the user store.
   *
   * @return  class name.
   */
  public String getGroupSpiClassName() {
    if (groupSpi != null) {
      return groupSpi;
    } else if (configuration != null) {
      return configuration.getGroupSpiClassName();
    } else {
      return null;
    }
  }


  /**
   *  Returns the properties of the user store.
   *
   * @return  the properties of the userstore.
   */
  public Properties getUserStoreProperties() {
    if (properties != null) {
      return properties;
    } else if (configuration != null) {
      return configuration.getUserStoreProperties();
    } else {
      return null;
    }
  }


  /**
   *  Returns the class name of the configuration editor for the user store.
   *
   * @return  class name.
   */
  public String getConfigurationEditorClassName() {
    if (editor != null) {
      return editor;
    } else if (configuration != null) {
      return configuration.getConfigurationEditorClassName();
    } else {
      return null;
    }
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setUserStoreProperties(Properties properties) {
    this.properties = properties;
  }

  public void setLoginModules(LoginModuleConfiguration[] loginModules) {
    this.loginModules = loginModules;
  }

  public void setUserSpiClassName(String className) {
    this.userSpi = className;
  }

  public void setGroupSpiClassName(String className) {
    this.groupSpi = className;
  }

  public void setConfigurationEditorClassName(String editor) {
    this.editor = editor;
  }

  public void setAnonymousUser(String anonymous) {
    this.anonymous = anonymous;
  }

  public void clearStartupConfiguration() {
    if (configuration == null) {
      return;
    }

    try {
      Method method = configuration.getClass().getMethod("clearStartupConfiguration", null);
      if (method != null) {
        method.invoke(configuration, null);
      }
    } catch (Throwable e) {
      if (SystemProperties.getBoolean("server")) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "clearStartupConfiguration", e);
      } else {
        // change by Vasil Panushev
        // use reflection to call the ExceptionHandler class to avoid NoClassDefFoundError when on serve side
        try {
          Class c = Class.forName("com.sap.engine.services.security.gui.ExceptionHandler");
          Method m = c.getDeclaredMethod("debug", new Class[] {String.class, Throwable.class});
          m.invoke(null, new Object[] {"Unable to forward clearStartupConfiguration request to wrapped configuration!", e});
        } catch (Exception exc) {
          // exception reporting through default channel failed -> dump on the console
          exc.printStackTrace();
          e.printStackTrace();
        }
      }
    }
  }
  
}