/*
 * Created on 2005-5-18 by Luchesar Cekov
 */
package com.sap.engine.services.deploy.ear;

import junit.framework.TestSuite;

import com.sap.engine.services.deploy.ear.jar.EARReaderGetDescriptorTest;
import com.sap.engine.services.deploy.ear.jar.EARReaderTest1;
import com.sap.engine.services.deploy.ear.jar.EarReaderCleanTest;
import com.sap.engine.services.deploy.ear.jar.EarReaderDetectorTest;
import com.sap.engine.services.deploy.ear.jar.SimpleEarDescriptorPopulatorTest;
import com.sap.engine.services.deploy.ear.jar.StandaloneModuleReader;

/**
 * @author Luchesar Cekov
 */
public class AllEarClassesTestSuite extends TestSuite {

  /**
   * 
   */
  public AllEarClassesTestSuite() {
    super();
    addTest(new EARReaderGetDescriptorTest());
    addTest(new EARReaderTest1());
    addTest(new SimpleEarDescriptorPopulatorTest());
  
  }
}
