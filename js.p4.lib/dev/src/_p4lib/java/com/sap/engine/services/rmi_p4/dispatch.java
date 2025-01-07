package com.sap.engine.services.rmi_p4;


/**
 * Dispatcher for the incomming requests.
 * implements Runnable in order to be run
 *
 * @author Georgy Stanev, Mladen Droshev
 * @version 7.10
 */
public interface Dispatch {

  public P4ObjectOutput getOutputStream();


  /**
   * @return input stream created from the request
   */
  public P4ObjectInput getInputStream();


  public DataOptInputStream getDataInputStream();


  public DataOptOutputStream getDataOutputStream();


  /**
   * closes created input stream
   */
  public void releaseInputStream();

}

