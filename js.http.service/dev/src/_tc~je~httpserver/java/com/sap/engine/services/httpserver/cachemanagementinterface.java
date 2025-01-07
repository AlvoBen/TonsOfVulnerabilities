package com.sap.engine.services.httpserver;

/**
 * The interface provides methods for invalidation of the ICM server cache based
 * on different criteria.
 *   
 * Currently the communication between the ICM and http service is done via
 * cluster messages. Because of the message server limitation the feature is  
 * supported up to 2000 msgs/sek (invocations/sek) with the length of 500 bytes
 * each message
 * 
 * @author Violeta Uzunova(I024174) 
 */
public interface CacheManagementInterface {


  /**
   * Clears the ICM server cache based on the specified sap-isc-etag
   * 
   * @param sapIscEtag - the etag 
   */
  public void clearCacheByEtag(String sapIscEtag) throws java.rmi.RemoteException;
  
  /**
   * Clears the ICM server cache based on the specified etag prefix
   * Not implemented yet
   * 
   * @param etagPrefix - the etag prefix  
   */
  public void clearCacheByEtagPrefix(String etagPrefix) throws java.rmi.RemoteException;
  
    
  /**
   * Clears the ICM server cache based on the specified URL
   * Not implemented yet
   * 
   * @param URL - the url 
   */
  public void clearCacheByURL(String URL) throws java.rmi.RemoteException;
  
}
