/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sap.engine.services.httpserver.server.rcm;

/**
 *
 * @author I024157
 */
public interface RequestProcessorThreadFactory {
    
  /**
   * Factory method for <code>RequestProcessorThread</code> objects
   * @return new instance of RequestProcessorThread object
   */
  RequestProcessorThread getInstance();
    
    /**
     * Thread name customization. 
     * @return the Thread name used for all started threads
     */
    String threadGroup();
    
    /**
     * Dedicated or Polled  thread to be used 
     * @return true if the thread shold be started as a dedicated thread
     */
    boolean dedicated();

}
