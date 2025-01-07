package com.sap.engine.services.httpserver.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import com.sap.bc.proj.jstartup.icmadm.CacheInvalidator;
import com.sap.bc.proj.jstartup.icmadm.IcmAdmException;
import com.sap.engine.services.httpserver.CacheManagementInterface;  
import com.sap.engine.lib.http.cache.CacheAdmin;
import com.sap.tc.logging.LoggingUtilities;

public class CacheManagementInterfaceImpl implements CacheManagementInterface {
  
  public CacheManagementInterfaceImpl() {
  }

  /**
   * Invalidates the ICM server cache by the specified sap-isc-etag
   * 
   * @param sapIscEtag - the etag
   */
  public void clearCacheByEtag(String sapIscEtag) throws RemoteException {
    try {
      if (Log.LOCATION_HTTP.beDebug()) {
        Log.LOCATION_HTTP.debugT("Clear ICM server cache by etag [" + sapIscEtag + "]");
      }
      ArrayList<CacheInvalidator> locList = new ArrayList<CacheInvalidator>();
      CacheInvalidator invalElem = new CacheInvalidator(sapIscEtag, CacheInvalidator.Type.ETAG);
      locList.add(invalElem);
      CacheAdmin.invalidateCacheEntries(locList);
    } catch (IcmAdmException iae) {      
      Log.logError("ASJ.http.000385", "Cannot clear ICM server cache by [{0}] etag", new Object[]{sapIscEtag}, iae, null,
        LoggingUtilities.getDcNameByClassLoader(iae.getClass().getClassLoader()), "BC-JAS-SF");
      throw new RemoteException("Cannot clear ICM server cache by [" + sapIscEtag + "] etag.", iae);
    }
  }

  /**
   * Invalidates the ICM server cache entries by sap-isc-etag prefix
   * 
   * @param etagPrefix etag prefix
   */
  public void clearCacheByEtagPrefix(String etagPrefix) throws RemoteException {
    try {
      if (Log.LOCATION_HTTP.beDebug()) {
        Log.LOCATION_HTTP.debugT("Clear ICM server cache by etag perfix [" + etagPrefix + "]");
      }
      ArrayList<CacheInvalidator> locList = new ArrayList<CacheInvalidator>();
      CacheInvalidator invalElem = new CacheInvalidator(etagPrefix, CacheInvalidator.Type.ETAG_PREFIX);
      locList.add(invalElem);
      CacheAdmin.invalidateCacheEntries(locList);
    } catch (IcmAdmException iae) {      
      Log.logError("ASJ.http.000386", "Cannot clear ICM server cache by [{0}] etag prefix", new Object[]{etagPrefix}, iae, null,
        LoggingUtilities.getDcNameByClassLoader(iae.getClass().getClassLoader()), "BC-JAS-SF");
      throw new RemoteException("Cannot clear ICM server cache by [" + etagPrefix + "] etag prefix.", iae);
    }
  }

  /**
   * Invalidates the ICM server cache entries by specified URL
   * 
   * @param URL url
   */
  public void clearCacheByURL(String URL) throws RemoteException {    
    try {
      if (Log.LOCATION_HTTP.beDebug()) {
        Log.LOCATION_HTTP.debugT("Clear ICM server cache by URL [" + URL + "]");
      }
      ArrayList<CacheInvalidator> locList = new ArrayList<CacheInvalidator>();
      CacheInvalidator invalElem = new CacheInvalidator(URL, CacheInvalidator.Type.URL);
      locList.add(invalElem);
      CacheAdmin.invalidateCacheEntries(locList);
    } catch (IcmAdmException iae) {
      Log.logError("ASJ.http.0003867", "Cannot clear ICM server cache by [{0}] URL", new Object[]{URL}, iae, null,
        LoggingUtilities.getDcNameByClassLoader(iae.getClass().getClassLoader()), "BC-JAS-SF");        
      throw new RemoteException("Cannot clear ICM server cache by [" + URL + "] url.", iae);
    }    
  }
}
