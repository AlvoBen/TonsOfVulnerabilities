/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.scope;

import com.sap.engine.core.Names;
import com.sap.engine.session.scope.exception.NotSupportedScopeTypeException;
import com.sap.engine.session.scope.exception.NoSuchScopeTypeException;
import com.sap.engine.session.scope.exception.AlreadyRunScopeException;
import com.sap.engine.session.scope.exception.ScopeTerminatedException;
import com.sap.engine.session.scope.exception.AlreadySetScopeException;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.trace.ThrTrace;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.HashSet;
import java.util.Set;
import java.text.MessageFormat;

/*
* Author: i024157 /Georgi Stanev/ 
*/
public final class Scope{

  public static Location loc = Location.getLocation(Scope.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public static final String CLIENT_CONTEXT_SCOPE_TYPE = "UC_ScopeType";
  public static final String CLIENT_CONTEXT_SCOPE_NAME = "UC_Sc";

  /* Registered Scope Types which is supported */
  private static HashSet<String> scopeTypes = new HashSet<String>();

  /* Scope Identifier */
  private String scopeId;

  /* Scope Type  */
  private String scopeType;

  /* kept reference to the Scope's observer */
  private ScopeObserver observer = null;

  private boolean terminated = false;

  /**
   * Create scope instance
   * @param scopeId scope id
   * @param scopeType scope type
   * @return the created Scope object
   * @throws com.sap.engine.session.scope.exception.NotSupportedScopeTypeException when the input
   *         scope type is not registered
   */
  public static ScopeObserver createScope(String scopeId, String scopeType) throws NotSupportedScopeTypeException{
    if(loc.bePath()){
      String msg = MessageFormat.format("Trying to create Scope with Id:{0} scopeType:{1}", scopeId, scopeType);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }

    if(scopeType == null || !(scopeTypes.contains(scopeType))){
      throw new NotSupportedScopeTypeException(MessageFormat.format("The Scope Type <{0}> is not registered.", scopeType));
    }

    Scope scope = new Scope(scopeId, scopeType);
    ScopeObserver observer = new ScopeObserver(scope);


    if(loc.bePath()){
      String msg = MessageFormat.format("Created :{0}", observer);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }

    return observer;

  }

  /**
   * For internal usage
   * @param id scopeId
   * @param observer which keeps refs to the scope
   */
  protected Scope(String id, ScopeObserver observer){
    this.observer = observer;
    this.scopeId = id;
    this.scopeType = this.observer.scopeType();
  }

  /**
   *
   * @param scopeType requested scope type
   * @return the scope from type scopeType associated with the current thread or null.
   * @throws com.sap.engine.session.scope.exception.NoSuchScopeTypeException if there is not registed
   * instance of the input scope type with current Execution Context
   */
  public static Scope getContextScope(String scopeType) throws NoSuchScopeTypeException {
    if(loc.bePath()){
      String msg = MessageFormat.format("Trying to get Scope with Type<{0}>. Current Execution Context is :{1}", scopeType, SessionExecContext.getExecutionContext());
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }

    Scope obs = SessionExecContext.getExecutionContext().getScope(scopeType);
    if(obs == null){
      throw new NoSuchScopeTypeException("Instance of Scope Type <" + scopeType + "> is not assigned with current Execution Context.");
    }

    if(loc.bePath()){
      String msg = "Found :" + obs;
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }

    return obs;

  }

  /**
   * Set the scope which is kept in ScopeObserver as ContextScope
   * @param owner Scope Owner
   * @return return kept Scope from the owner
   * @throws com.sap.engine.session.scope.exception.AlreadySetScopeException if there is
   * already set Scope in the Context with the same type
   */
  public static Scope setContextScope(ScopeObserver owner) throws AlreadySetScopeException{
    if(owner == null){
      return null;
    }
    if(loc.bePath()){
      String msg = MessageFormat.format("Trying to set ScopeOwner :{0}. Current Execution Context is :{1}" , owner, SessionExecContext.getExecutionContext());
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }
    if (owner.getScope() != null) {
      SessionExecContext.getExecutionContext().registerScope(owner.getScope().getScopeType(), owner);
    }
    return owner.getScope();
  }

  /**
   * try to remove the Scope with input scope type
   * @param type scope Type reg
   * @return if the scope is 
   */

  public static Scope removeContextScope(String type) throws NoSuchScopeTypeException {

    if(loc.bePath()){
      String msg = MessageFormat.format("Trying to remove Scope with type<{0}>", type);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }

    ScopeObserver tempObs = SessionExecContext.getExecutionContext().unregisterScope(type);
    if(tempObs == null){
      return null;
    }

    if(loc.bePath()){
      String msg = MessageFormat.format("Scope<{0}> is removed.", tempObs);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }

    return tempObs.getScope();
  }

  /**
   * This methods allow the Resource consumer to define "inScope" actions.
   * Using this methods allow the scope instance to be accessible
   * through getInScope() method during action.run() method.
   * @param action  the action that should be executed in the scope
   * @param scope the scope instance in which the action is executed
   * @throws com.sap.engine.session.scope.exception.AlreadyRunScopeException if already there is
   * run other scope
   * @throws com.sap.engine.session.scope.exception.ScopeTerminatedException if the input scope is
   * already terminated
   */
  public synchronized static void runInScope(Runnable action, Scope scope) throws AlreadyRunScopeException, ScopeTerminatedException{
    if(loc.bePath()){
      String msg = MessageFormat.format("Trying to run in the scope:{0} with runnable:{1}", scope, action);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      loc.pathT(msg);
    }

    /* check for terminated scope */
    if(scope != null && scope.isTerminated()){
      String msg = MessageFormat.format("The input Scope<{0}> is already terminated.", scope);
      if(loc.beDebug()){
        loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
      }
      throw new ScopeTerminatedException(msg);
    }

    Scope ranScope = SessionExecContext.getExecutionContext().getScopeForRun();

    /* check for already run other Scope */
    if(ranScope != null){
      if(loc.bePath()){
        String msg = MessageFormat.format("There is working action in Scope:{0}", ranScope);
        if (loc.beDebug()) {
          loc.traceThrowableT(Severity.DEBUG, msg, new ThrTrace());
        }
        loc.pathT(msg);
      }
      throw new AlreadyRunScopeException(MessageFormat.format("There is already Scope in run:{0}", ranScope));
    }
    
    SessionExecContext.getExecutionContext().setScopeForRun(scope);
    try{
      action.run();
    } catch(Throwable trh){
      if(loc.beDebug()){
        loc.traceThrowableT(Severity.DEBUG, "The Action<" + action + "> is interrupted", trh);
      }
    } finally{
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.DEBUG, "The action<" + action + "> is terminated.", new ThrTrace());
      }
      SessionExecContext.getExecutionContext().removeScopeForRun();
    }
    
  }

  /**
   * When it is called during the action executed with runInScope method this method returns the scope instance.
   * @return the scope in witch the "inScope" acction is executed or null.
   */
  public static Scope getInScope() {
    return SessionExecContext.getExecutionContext().getScopeForRun();
  }

  /**
   * register Scope Type
   * @param scopeType  - new Scope Type
   */
  public static synchronized void registerScopeType(String scopeType){
    scopeTypes.add(scopeType);
  }

  /**
   * unregister Scope Type
   * @param scopeType - type that the provider decide to unregister
   * @return if the Scope Type is successfully unregister
   */
  public static synchronized boolean unregisterScopeType(String scopeType){
    return scopeTypes.remove(scopeType);
  }

  /**
   * check if the input Scope Type is registered
   * @param scopeType for the check
   * @return true if the scope type is already registered
   */
  public static synchronized boolean isRegisteredScopeType(String scopeType){
    return scopeTypes.contains(scopeType);
  }

  /**
   *
   * @param scopeId scope Id
   * @param scopeType Scope Type which have to be registered
   */
  Scope(String scopeId, String scopeType){
    this.scopeId = scopeId;
    this.scopeType = scopeType;
  }

  /**
   * Returns the scope is
   * @return  scope id
   */
  public String getScopeId() {
    return this.scopeId;
  }

  /**
   * Returns the scope type
   * @return scope type
   */
  public String getScopeType() {
    return this.scopeType;
  }

  /**
   * Bind the resource to the scope
   * @param res resource to be boound
   */
  public synchronized void addScopeManagedResource(ScopeManagedResource res) {
    this.observer.addScopeManagedResource(res);
  }

  /**
   * UnBind the resource from the Scope
   * @param res resource which is redundant
   */
  public synchronized void removeScopeMangedResource(ScopeManagedResource res) {
    this.observer.removeScopeMangedResource(res);
  }

  void setObserver(ScopeObserver observer){
    this.observer = observer;
  }

  void clean(){
    this.observer = null;
  }

  public String toString(){
    return "Scope<" + this.hashCode() +"> id:" + this.scopeId + " scopeType:" + scopeType;
  }

  public boolean isTerminated(){
    return this.terminated;
  }

  protected void terminate(){
    this.clean();
    this.terminated = true;
  }

  protected Set getResources(){
    return this.observer.getScopeResources();
  }

}