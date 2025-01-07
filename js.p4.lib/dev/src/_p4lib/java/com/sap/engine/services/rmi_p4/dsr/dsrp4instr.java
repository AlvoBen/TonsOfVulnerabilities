package com.sap.engine.services.rmi_p4.dsr;

/**
 * This class provides methods to instrument P4 server critical points and to get needed content.
 * The instrumented points are:
 * - when P4 request starts
 * - when P4 request ends with reply or error reply.
 * 
 * @author Simeon Stefanov, Tsvetko Trendafilov
 */

public class DSRP4Instr {

    private static DSRP4Server p4Server = null;
    
    /**
     * This method is invoked when P4 request starts.
     * 
     * @param type - local (1) or between system (2) communication
     * @param from - host or IP or cluster ID
     * @param receivedBytes - received bytes from the request
     */
    public static void requestStart(DSRP4RequestContextImpl details) {
      if (p4Server != null) {
        details.invokedDSRMethod = "requestStart()";
        p4Server.requestStart(details);
      }
    }
    
    /**
     * This method is invoked when P4 call receives its reply or error reply (when finished)
     * 
     * @param sentBytes - sent bytes from the response
     */
    public static void requestEnd(DSRP4RequestContextImpl details) {
      if (p4Server != null) {
        details.invokedDSRMethod = "requestEnd()";
        p4Server.requestEnd(details);
      }
    }
    
    /**
     * This method is invoked by DSR service to register in P4.
     * Here DSR set real implementation of their listener in P4 service.
     */ 
    public static void registerP4Server(DSRP4Server real) {
      p4Server = real;
    }
    
    /**
     * This method is invoked by DSR service to unregister in P4. 
     * Here P4 service unregisters their implementation.
     * Note: Unregistration from P4 does not unregister DSR's 
     * passport from TreadManagerImpl in thread management. 
     */ 
    public static void unregisterP4Server() {
      p4Server = null;
    }
    
    /**
     * Checks if the DSR Listener is already registered.
     * @return true - if it is registered and 
     *         false - if it is not registered.
     */
    public static boolean isRegistered() {
      return p4Server != null;
    }
    
    /**
     * To be able to get currently registered DSR implementation;
     * Used in combination with isRegistere(), if only there were registered DSR
     * listener.
     * DSR was instrumented getting of this implementation is the only chance to 
     * restore original state after ATS test, or other scenario if unregister the listener.
     * 
     * @return Current registered DSR listener or null if DSR listener is not registered.
     */
    public static DSRP4Server getRegistered() {
      return p4Server;
    }
}