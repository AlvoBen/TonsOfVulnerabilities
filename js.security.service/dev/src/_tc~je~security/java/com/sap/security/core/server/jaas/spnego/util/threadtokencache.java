package com.sap.security.core.server.jaas.spnego.util;


import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;

/**
 *  The purpose of this class is to prevent SPNego tokens to be
 *  processed twice in one specific thread. For this purpose, this
 *  class stores tokens with the thread as key in a hash map.
 *  Three measures try to ensure that this design cannot be abused
 *  for replay attacks:
 *  <ul>
 *   <li> Tokens can only be found within the proper
 *  thread, i.e. if a token yielded a positive authentication result
 *  in one thread and someone has eavesdropped the wire and tries to
 *  replay the communication, her request is probably processed by
 *  another thread. So even if the replayed request is processed by
 *  the J2EE engine at a time when the token object is still in the
 *  cache it will probably not be found because of the thread key.
 *   <li> The object has a configurable lifetime of 1000 milliseconds.
 *  After that requests for it return null.
 *   <li> After successful authentication or explicit failure the object
 *  is removed from the cache by the SPNegoLoginModule.
 *  </ul>
 */
public class ThreadTokenCache extends ShortLifetimeCache {
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_SPNEGO_LOCATION);

  /**
   * @param msecsUntilCleanup
   * @param itemLifetime
   */
  public ThreadTokenCache(int msecsUntilCleanup, int itemLifetime) {
    super(msecsUntilCleanup, itemLifetime, "ThreadTokenCache");
  }

  /**
   *  Registers that the given token has been processed by the current
   *  thread. The result is either the username (which means that this
   *  token ended the communication) or incomplete authentication
   *  (indicated by username==null). In either case, this information
   *  can be queried later in the thread.
   *  @param token
   *  @param username
   *  @deprecated 
   */
  public void put(String token, String username) {
    if (LOCATION.beInfo()) {
      LOCATION.infoT("Putting token " + token + " and username " + username + " in ThreadTokenCache.");
    }
    ThreadTokenCacheItem cacheItem = new ThreadTokenCacheItem(token, username, null);
    super.put(Thread.currentThread(), cacheItem);
  }

  /**
   *  Registers that the given token has been processed by the current
   *  thread. The result is either the username (which means that this
   *  token ended the communication) or incomplete authentication
   *  (indicated by username==null). In either case, this information
   *  can be queried later in the thread.
   *  @param token
   *  @param username
   *  @param kpn   
   *	
   */
  public void put(String token, String username, String kpn) {
    if (LOCATION.beInfo()) {
      LOCATION.infoT("Putting token " + token + " , userName " + username + " and kerberosPrincipalName = " + kpn + " in ThreadTokenCache.");
    }
    ThreadTokenCacheItem cacheItem = new ThreadTokenCacheItem(token, username, kpn);
    super.put(Thread.currentThread(), cacheItem);
  }

  /**
   *  checks whether the given token has already been processed in the
   *  current thread. 
   *  @param token SPNego token from the http request
   *  @return the status of the result this token yielded previously in the same request
   */
  public TokenStatus containsToken(String token) {
    Object obj = super.contains(Thread.currentThread());
    ThreadTokenCacheItem cacheItem = null;
    TokenStatus tokenStatus = null;

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Searching for token " + token + " in ThreadTokenCache.");
    }

    if (obj != null) {
      cacheItem = (ThreadTokenCacheItem) obj;
      if (cacheItem.keyToken.equals(token)) {
        tokenStatus = new TokenStatus(cacheItem.userName, cacheItem.kerberosPrincipalName, cacheItem.userName != null);
      }
    }

    return tokenStatus;
  }

  class ThreadTokenCacheItem {

    String keyToken;
    String userName = null;
    String kerberosPrincipalName = null;

    ThreadTokenCacheItem(String token, String username, String kpn) {
      keyToken = token;
      userName = username;
      kerberosPrincipalName = kpn;
    }
  }
}
