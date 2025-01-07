package com.sap.engine.lib.xml.names;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      October 2001
 */
public interface XMLNameWordHandler {

  void start();


  void word(char[] a, int start, int end);


  void end();

}

