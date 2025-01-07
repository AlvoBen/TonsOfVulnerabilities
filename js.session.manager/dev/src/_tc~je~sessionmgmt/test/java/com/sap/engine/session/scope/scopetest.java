package com.sap.engine.session.scope;

import org.junit.Test;

import com.sap.engine.session.scope.exception.NotSupportedScopeTypeException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class ScopeTest{
  static Location ll = Location.getLocation(Scope.class);
  static{

    ll.setEffectiveSeverity(Severity.ALL);
  }

  @Test
  public void testScopeTypeRegistration() {
    try {
      registerScopeType("Test");
      checkScopeType("Test");
    } catch (Exception ex) {
      System.out.println("Ex: " + ex.getMessage()); //$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void testWithNullScope() {
    try {
      createObserver(null, null);
    } catch (Exception ex) {
      System.out.println("Ex: " + ex.getMessage());  //$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void testScopeCreation() {
    try {
      createObserver("TestId", "TestScopeType");
    } catch (Exception ex) {
      System.out.println("Ex: " + ex.getMessage()); //$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void testScopeBasicOperation() {
    try {
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      Scope scope = ob.getScope();
      scope.getScopeId();
      scope.getScopeType();
      scope.isTerminated();
      ob.toString();
    } catch (Exception ex) {
      System.out.println("Ex: " + ex.getMessage());  //$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void testScopeTermination(){
    try {
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      ob.terminateScope();
    } catch (Exception ex) {
      System.out.println("Ex: " + ex.getMessage());   //$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void testScopeResourceUnRegister(){

    try{
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      Scope scope = ob.getScope();
      ScopeManagedResourceImpl res = new ScopeManagedResourceImpl();
      scope.addScopeManagedResource(res);
      scope.removeScopeMangedResource(res);
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());    //$JL-SYS_OUT_ERR$

    }
  }

  @Test
  public void testResourcesFromObserver(){

    try{
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      Scope scope = ob.getScope();
      ScopeManagedResourceImpl res1 = new ScopeManagedResourceImpl();
      ScopeManagedResourceImpl res2 = new ScopeManagedResourceImpl();
      scope.addScopeManagedResource(res1);
      scope.addScopeManagedResource(res2);
      ob.clearResources();
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());  //$JL-SYS_OUT_ERR$

    }
  }

  @Test
  public void testScopeTypeUnRegister() {
    try {
      registerScopeType("TestType");
      unRegisterScopeType("TestType");
    } catch (Exception ex) {
      System.out.println("Ex: " + ex.getMessage());  //$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void testGetInScope(){
    try{
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      Scope.setContextScope(ob);
      Scope.getInScope();
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());  //$JL-SYS_OUT_ERR$

    }
  }

  @Test
  public void testRunInScope(){
    try{
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      Scope.setContextScope(ob);
      Scope.runInScope(new Thread(), ob.getScope());
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());  //$JL-SYS_OUT_ERR$

    }
  }

  @Test
  public void testRunInTerminatedScope(){
    try{
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      Scope.setContextScope(ob);
      ob.getScope().terminate();
      Scope.runInScope(new Thread(), ob.getScope());
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());  //$JL-SYS_OUT_ERR$

    }
  }

  @Test
  public void testGetContextScope(){
    try{
      ScopeObserver ob1 = createObserver("TestId1", "TestScopeType");
      Scope scope = Scope.setContextScope(ob1);
      Scope.getContextScope(scope.getScopeType());
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());//$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void testRegisterTwoScopes(){
    try{
      ScopeObserver ob1 = createObserver("TestId1", "TestScopeType");
      ScopeObserver ob2 = createObserver("TestId2", "TestScopeType");
      Scope scope = ob1.getScope();
      Scope.setContextScope(ob1);
      Scope.getContextScope(scope.getScopeType());
      Scope.setContextScope(ob2);
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());//$JL-SYS_OUT_ERR$

    }
  }

  @Test
  public void testRemoveContextScope(){
    try{
      ScopeObserver ob = createObserver("TestId", "TestScopeType");
      Scope scope = ob.getScope();
      Scope.setContextScope(ob);
      Scope.removeContextScope(scope.getScopeType());
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());//$JL-SYS_OUT_ERR$

    }
  }

  @Test
  public void testResources(){
    try{
      ScopeObserver observer = createObserver("test", "test");
      observer.getScope().addScopeManagedResource(new ScopeManagedResourceImpl());
      observer.getScope().addScopeManagedResource(new ScopeManagedResourceImpl());
      observer.getScope().addScopeManagedResource(new ScopeManagedResourceImpl());
      ScopeManagedResource.getResources(observer.getScope(), ScopeManagedResourceImpl.class);
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());//$JL-SYS_OUT_ERR$

    }

    try{
      ScopeObserver observer = createObserver("test", "test");
      ScopeManagedResource.getResources(observer.getScope(), ScopeManagedResourceImpl.class);
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());//$JL-SYS_OUT_ERR$

    }

    try{
      ScopeManagedResource.getResources(null, null);
    } catch(Exception ex){
      System.out.println("Ex: " + ex.getMessage());//$JL-SYS_OUT_ERR$

    }
  }

  

  private ScopeObserver createObserver(String id, String type) throws NotSupportedScopeTypeException {
    registerScopeType(type);
    return Scope.createScope(id, type);
  }

  private void registerScopeType(String type){
    Scope.registerScopeType(type); 
  }

  private void checkScopeType(String type){
    Scope.isRegisteredScopeType(type);
  }

  private void unRegisterScopeType(String type){
    Scope.unregisterScopeType(type);
  }



  class ScopeManagedResourceImpl extends ScopeManagedResource{


    public void scopeTerminated(Scope scope) {
    }
  }

}