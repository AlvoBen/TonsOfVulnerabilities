package com.sap.engine.services.security.server;

import java.lang.reflect.InvocationTargetException;

import com.sap.engine.interfaces.security.JACCContext;
import com.sap.engine.interfaces.security.JACCMigrationContext;
import com.sap.engine.interfaces.security.JACCUpdateContext;
import com.sap.engine.interfaces.security.JACCUndeployContext;

public class JACCContextImpl implements JACCContext {
  private String pc = null;
  private JACCUpdateContext updateContext = null;
  private JACCUndeployContext undeployContext = null;
  private JACCMigrationContext migrationContext = null;
  
  public JACCContextImpl(String pc) {
    this.pc = pc;

    try {
      Class updateClass = Class.forName("com.sap.security.core.server.ume.service.jacc.JACCUpdateContextImpl");
      updateContext = (JACCUpdateContext) updateClass.getConstructor(new Class[]{java.lang.String.class}).newInstance(new Object[]{pc});
    } catch (ClassNotFoundException cnf) {
      // log
      throw new SecurityException(cnf.getMessage());
    } catch (NoSuchMethodException nsm) {
      // log
      throw new SecurityException(nsm.getMessage());
    } catch (InstantiationException ie) {
      //log
      throw new SecurityException(ie.getMessage());
    } catch (IllegalAccessException iae) {
      // log
      throw new SecurityException(iae.getMessage());
    } catch (InvocationTargetException ite) {
      // log
      throw new SecurityException(ite.getMessage());
    }
   
    try {
      Class undeployClass = Class.forName("com.sap.security.core.server.ume.service.jacc.JACCUndeployContextImpl");
      undeployContext = (JACCUndeployContext) undeployClass.getConstructor(new Class[]{java.lang.String.class}).newInstance(new Object[]{pc});
    } catch (ClassNotFoundException cnf) {
      // log
      throw new SecurityException(cnf.getMessage());
    } catch (NoSuchMethodException nsm) {
      // log
      throw new SecurityException(nsm.getMessage());
    } catch (InstantiationException ie) {
      //log
      throw new SecurityException(ie.getMessage());
    } catch (IllegalAccessException iae) {
      // log
      throw new SecurityException(iae.getMessage());
    } catch (InvocationTargetException ite) {
      // log
      throw new SecurityException(ite.getMessage());
    }
       
    try {
      Class migrationClass = Class.forName("com.sap.security.core.server.ume.service.jacc.JACCMigrationContextImpl");
      migrationContext = (JACCMigrationContext) migrationClass.getConstructor(new Class[]{java.lang.String.class}).newInstance(new Object[]{pc});
    } catch (ClassNotFoundException cnf) {
      // log
      throw new SecurityException(cnf.getMessage());
    } catch (NoSuchMethodException nsm) {
      // log
      throw new SecurityException(nsm.getMessage());
    } catch (InstantiationException ie) {
      //log
      throw new SecurityException(ie.getMessage());
    } catch (IllegalAccessException iae) {
      // log
      throw new SecurityException(iae.getMessage());
    } catch (InvocationTargetException ite) {
      // log
      throw new SecurityException(ite.getMessage());
    }       
  }
  
  public JACCUpdateContext getUpdateContext() {
    return updateContext;
  }
  
  public JACCUndeployContext getUndeployContext() {
    return undeployContext;
  }
  
  public JACCMigrationContext getMigrationContext() {
    return migrationContext;
  }
}
