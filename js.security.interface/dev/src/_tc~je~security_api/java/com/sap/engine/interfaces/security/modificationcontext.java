/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.deploy.container.AppConfigurationHandler;

/**
 *  Context used to bundle modification operations over security context.
 * The bundle contains all operation done between a call to beginModifications
 * and commit/rollbackModifications in the thread of the invocation of
 * beginModifications method.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.SecurityContext
 */
public interface ModificationContext {

  /**
   *  Defines a modifications bundle for the current thread.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public void beginModifications() throws SecurityException;


  /**
   *  Defines a modifications bundle for the given configuration instance.
   * In this case no commit will be invoked from the modifications context and no invocation
   * of <code>commitModifications</code> or <code>rollbackModifications</code> will be expected.
   *
   * @param  configuration  the configuration to associate operations bundle with.
   *
   * @return  a securty context all operations over which are done using the configuration instance
   *          and will be committed when the configuration handler is committed.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public SecurityContext beginModifications(Configuration configuration) throws SecurityException;

  /**
   *  Defines a modifications bundle for the given configuration instance and a wrapper of the
   * ConfigurationHandler from which the configuration has been derived.
   * In this case no commit will be invoked from the modifications context and no invocation
   * of <code>commitModifications</code> or <code>rollbackModifications</code> will be expected.
   *
   * @param  configHandler  the configuration handler to associate operations bundle with.
   * @param  configuration  the configuration to associate operations bundle with.
   *
   * @return  a securty context all operations over which are done using the configuration instance
   *          and will be committed when the configuration handler is committed.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public SecurityContext beginModifications(AppConfigurationHandler configHandler, Configuration configuration) throws SecurityException;

  /**
   *  Commits a modifications bundle for the current thread.
   *
   *  Does nothing if a configuration was provided on beginModifications
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public void commitModifications() throws SecurityException;


  /**
   *  Rollbacks a modifications bundle for the current thread.
   *
   *  Does nothing if a configuration was provided on beginModifications
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public void rollbackModifications() throws SecurityException;

}

