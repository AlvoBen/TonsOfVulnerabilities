package com.sap.engine.services.httpserver.lib;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.engine.services.httpserver.server.Log;

public class ParseUtilsTest {
  @Before
  public final void setUp() {
    Log.init();
  }
  
  @Test
  public final void testCanonicalize() {
    String path = ParseUtils.canonicalize("/ala/bala/portakala");
    assertEquals("/ala/bala/portakala", path);
    
    path = ParseUtils.canonicalize("/ala//bala//portakala");
    assertEquals("/ala/bala/portakala", path);
    
    path = ParseUtils.canonicalize("/ala/../bala");
    assertEquals("/bala", path);
    
    path = ParseUtils.canonicalize("/ala/../../bala/./portakala");
    assertEquals("/../bala/portakala", path);
    
    //path = ParseUtils.canonicalize("/ala/bala/portakala/../");
    //assertEquals("/ala/bala/", path);
    
    path = ParseUtils.canonicalize("/ala/./bala/./portakala");
    assertEquals("/ala/bala/portakala", path);
    
    //path = ParseUtils.canonicalize("/./ala/bala/portakala/.");
    //assertEquals("/ala/bala/portakala/", path);
  }
  
  @Test
  public final void testConvertAlias() {
    String alias = null;
    if (ParseUtils.separatorChar != '\\') {
      alias = ParseUtils.convertAlias("\\ala-bala\\portakala");
      assertTrue(alias.indexOf('\\') == -1);
      
      alias = ParseUtils.convertAlias("\\");
      assertTrue(alias.indexOf('\\') == -1);
    } else {
      alias = ParseUtils.convertAlias("/ala-bala/portakala");
      assertTrue(alias.indexOf('/') == -1);
      
      alias = ParseUtils.convertAlias("/");
      assertTrue(alias.indexOf('/') == -1);
    }
  }

  @Ignore // Can't be tested, because of the dependency on Log class
  public final void testErrorOnCRLF() {
    ParseUtils.errorOnCRLF("Ala-Bala\r\nPortokala");
  }

}
