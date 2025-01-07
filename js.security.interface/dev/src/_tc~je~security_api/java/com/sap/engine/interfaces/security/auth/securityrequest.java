/**
 * 
 */
package com.sap.engine.interfaces.security.auth;

import javax.servlet.ServletContext;

/**
 * This interface is to be implemented by the web container in the class that implements HttpServletRequest.
 * 
 * @author Svetlana Stancheva. Encho Solakov
 * @version 7.20
 */
public interface SecurityRequest { 

  /**
   * Constant used as key for storing original url in the session.
   * This parameter in the session is used for flag when restoring post parameters after form authentication.
   * The security should add this key with value original relative URL. Depending on the scenarios the web container will
   * restore the post parameters in case the url of the current request is equal to this stored value in the session.
   * If there is forwarded/included jsp/servlets the url of the parent (entry point) resource is used.
   * As this parameter is stored in the session - other application could access it.
   * When data are restored this value will be removed from the session.
   */
  public final static String ORIGINAL_URL = "sap.com/original_url_request";
  
  
  /**
   * Name of the key under which the post parameters are stored in the runtime session model.
   * This key should not be used as parameter key in methods:
   * {@link #storeDataInSessionRuntime()}, {@link #getDataFromSessionRuntime()}, {@link #removeDataFromSessionRuntime()}
   * for managing data in runtime session model.
   * This value is reserved for the web container.
   */
  public final static String POST_PARAMETERS_KEY_FOR_RUNTIME_SESSION = "POST_PARAMETERS_KEY_FOR_RUNTIME_SESSION";
  /**
   * Gets the ServletContext object for the web application.
   * 
   * @return The ServletContext object for the web application
   */
  public ServletContext getServletContext();
  
  /**
   * Stores Post parameters in runtime session model as byte array.
   * It is used for storing post parameters during post request to protected resource where authentication is needed.
   * The method is used in post parameters preservation scenarios (j_sec_check sap logon page)
   * and is called by Security after the authentication is done and HTTP session is created.
   * If there is no JSESSIONID and saplb cookie - it will be added to the response. 
   * If runtime model has expired or removed the data will be lost.
   * Data are restored by calling methods  {@link #restorePostDataBytes()} and {@link #restorePostDataBytes(byte[])}
   * which are called depending on the scenario by security or web container
   * @param postData data bytes which will be stored in runtime session model  
   * @throws IllegalArgumentException if postData is <code>null</code>
   * @see #storePostDataBytes()
   */
  public void storePostDataBytes(byte[] postData);
  
  /**
   * Stores Post parameters in runtime session model as byte array. The post parameters are taken from the request
   * internally by the Web Container. If there is no post data in the request an empty byte array is stored
   * It is used for storing post parameters during post request where authentication is needed.
   * The method is used in post parameters preservation scenarios (j_sec_check custom logon page, 
   * session prolongation) and called by Security 
   * If there is no JSESSIONID and saplb cookie - it will be added to the response. 
   * If runtime model has expired or removed the data will be lost.
   * Data are restored by calling methods  {@link #restorePostDataBytes()} and {@link #restorePostDataBytes(byte[])}
   * which are called depending on the scenario by security and web container
   * @see #storePostDataBytes(byte[])
   */
  public void storePostDataBytes();
  
  /**
   * Returns post data bytes from the request.
   * It is used in post parameters preservation scenarios (j_sec_check sap logon page, self submit sap logon page)
   * and called by Security to receive the post data bytes of the request.
   * If there is no post data in the request an empty byte array is returned.
   * After that these bytes are stored as hidden parameter in the response and are added again to one of the next
   * requests after successful authentication.  
   */
  public byte[] getPostDataBytes();
  
  
  /**
   * Restore post data in the request from http session. Add post parameters to the request if they exist in the http session.
   * If postData are null the response is error page with code 503.
   * It is used in post parameters preservation scenarios (session prolongation - security calls it,
   * j_sec_check custom logon page - web container calls it, j_sec_check sap logon page - web container calls it).
   * When this method is called the web container checks that necessary conditions for restoring post parameters are fulfilled
   * (the method is POST, the {@link #ORIGINAL_URL} attribute in the session is equal to the original URL, the data is available).
   * If post data have been lost because of problems with resources (runtime model has been lost) an error page will be
   * sent with error code 503. Also if data is not found in the runtime model for any reason 
   * (for example the JSESSIONID is different from one which data has been stored) an 503 server overload error is returned.
   * When the method is called the post data bytes are removed from runtime session model.
   * If this method is called twice for one request or if it is called when data has not been stored
   * (data can be stored with {@link #storePostDataBytes()} and {@link #storePostDataBytes(byte[])})
   * error page with 503 error code - server is overloaded will be returned. Because in these case the data will be expected and the data
   * will be missing.
   * <p>
   * If the current request has any post data bytes they will be ignored after calling this method.
   * </p>
   * @see #restorePostDataBytes(byte[])
   * 
   */
  public void restorePostDataBytes();
  
  /**
   * Restore post data bytes to the request from passed byte array. Add post parameters to the request and they are available for the application.
   * If <code>postData</code> is <code>null</code> the response is error page with code 503.
   * It is used in post parameters preservation scenarios (self submit sap logon page - security calls it)
   * <p>
   * If the current request has any post data bytes they will be ignored after calling this method.
   * </p>
   * @param postData
   * @see #restorePostDataBytes()
   */
  public void restorePostDataBytes(byte[] postData);
  
  /**
   * Stores data in session runtime model with a given key. If the runtime model is destroyed 
   * after that the data will be lost.
   * If there is no JSESSIONID and saplb cookies they will be added. The JSESSIONID is used for identifying the runtime model.
   * The method is used to store data in the runtime model before the creation of the application session.
   * This is a protection against DoS attacks 
   * @param key
   * @param data
   * @throws IllegalStateException if <code>key</code> is {@link #POST_PARAMETERS_KEY_FOR_RUNTIME_SESSION}.
   * @throws IllegalArgumentException if <code>key</code> is null or empty string; or data is <code>null</code> 
   */
  public void storeDataInSessionRuntime(String key, Object data);
  
  /**
   * Get data from session runtime model with a key. 
   * The method is used to get stored data from the runtime model before the creation of the application session.
   * This is a protection against DoS attacks.
   * @param key
   * @return
   * @throws IllegalStateException if <code>key</code> is {@link #POST_PARAMETERS_KEY_FOR_RUNTIME_SESSION}.
   * @throws IllegalArgumentException if key is null or empty string
   */
  public Object getDataFromSessionRuntime(String key);
  
  /**
   * Remove data from session runtime model with specific key. 
   * The method is used to remove stored data from the runtime model before the creation of the application session.
   * This is a protection against DoS attacks 
   * @param key
   * @return the corresponding data for the key.  
   * @throws IllegalStateException if <code>key</code> is {@link #POST_PARAMETERS_KEY_FOR_RUNTIME_SESSION}.
   * @throws IllegalArgumentException if key is null or empty string
   */
  public Object removeDataFromSessionRuntime(String key);
  
  /**
   * Returns an absolute URI with the given scheme and path and adequate
   * host and port.
   * <p/>
   * The method accepts only absolute paths, e.g. such that start with a
   * leading '/' and interprets them as relative to the servlet container root.
   *
   * @param scheme The required scheme. Allowed values are "http" and "https"
   * @param path   An absolute path that start with a leading '/'
   * @return An absolute URI or <code>null</code> in case that servlet container
   *         can not find adequate host and port
   */
  public String getURLForScheme(String scheme, String path);
}
