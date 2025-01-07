/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.properties;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.sap.bc.proj.jstartup.JStartupFramework;
import com.sap.engine.frame.NestedProperties;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.services.httpserver.interfaces.properties.HttpCompressedProperties;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.interfaces.properties.MimeMappings;
import com.sap.engine.services.httpserver.interfaces.properties.ProxyServersProperties;
import com.sap.engine.services.httpserver.lib.CookieParser;
import com.sap.engine.services.httpserver.lib.HttpParseUtil;
import com.sap.engine.services.httpserver.lib.Responses;
import com.sap.engine.services.httpserver.server.Constants;
import com.sap.engine.services.httpserver.server.HttpServerFrame;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.SmdAccessLog;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;

public class HttpPropertiesImpl extends RuntimeConfiguration implements HttpProperties {
	public static final int MIN_FILE_BUFFER_SIZE = 2048; //2K Minimal value for fileBufferSize property
  //log
  private static final String LOG_CLF_KEY = "LogCLF";
  private static final String FILE_BUFFER_SIZE_KEY = "FileBufferSize";
  private static final String FCA_SERVER_THREAD_COUNT = "FCAServerThreadCount";
  private static final String MIME_TYPES_KEY = "Mime";
  private static final String INFER_NAMES_KEY = "InferNames";
  private static final String MAX_FILE_LENGTH_FOR_CACHE_KEY = "MaxFileLengthForCache";
  private static final String CACHE_CONTROL_KEY = "CacheControl";
  private static final String SAP_CACHE_CONTROL_KEY = "SapCacheControl";
  private static final String PROTOCOL_HEADER_NAME_KEY = "ProtocolHeaderName";
  private static final String USE_SERVER_HEADER = "UseServerHeader";
  private static final String POOL_SIZE_KEY = "PoolSize";
  private static final String LOG_RESPONSE_TIME = "LogResponseTime";
  private static final String LOG_HEADER_VALUE = "LogHeaderValue";
  private static final String CLIENT_IP_HEADER_NAME_KEY = "ClientIpHeaderName";
  private static final String LOAD_BALANCING_COOKIE_PREFIX = "LoadBalancingCookiePrefix";
  private static final String PROXY_MAPPINGS_KEY = "ProxyMappings";
  public static final String GROUP_INFO_LOCATION = "GroupInfoLocation";
  public static final String URL_MAP_LOCATION = "UrlMapLocation";
  private static final String GROUP_INFO_REQUEST = "GroupInfoRequest";
  private static final String URL_MAP_REQUEST = "UrlMapRequest";
  private static final String URL_SESSION_TRACKING_FOR_ALL_COOKIES = "UrlSessionTrackingForAllCookies";
  private static final String LOG_IS_STATIC = "LogIsStatic";
  private static final String DETAILED_ERROR_RESPONSE = "DetailedErrorResponse";
  private static final String LOG_REQUEST_RESPONSE_HEADERS = "LogRequestResponseHeaders";
  private static final String TRACE_RESPONSE_TIME_ABOVE = "TraceResponseTimeAbove";
  private static final String LOG_REQUEST_MEMORY_TRACE = "LogRequestMemoryTrace";
  private static final String GENERATE_ERROR_REPORTS = "GenerateErrorReports";
  private static final String NEW_ERROR_REPORT_TIMEOUT ="GenerateNewErrorReportTimeout";
  private static final String TROUBLE_SHOOTING_GUIDE_URL = "TroubleShootingGuideURL";
  private static final String USE_CLIENT_OBJECTS_POOL = "UseClientObjectsPool";

  //gzip
  protected static final String GZIP_IMPLEMENTATION = "GZipOutputStreamImplementation";
  protected static final String ALWAYS_COMPRESS_KEY = "AlwaysCompressed";
  protected static final String NEVER_COMPRESS_KEY = "NeverCompressed";
  protected static final String COMPRESSED_OTHERS_KEY = "CompressedOthers";
  protected static final String MINIMUM_GZIP_LENGTH_KEY = "MinimumGZipLength";
  protected static final String MAXIMUM_COMPRESSED_URL_LENGTH_KEY = "MaximumCompressedURLLength";
  protected static final String SYSTEM_COOKIES_DATA_PROTECTION  = "SystemCookiesDataProtection";
  protected static final String SYSTEM_COOKIES_HTTPS_PROTECTION  = "SystemCookiesHTTPSProtection";

  protected static final int MAXIMUM_COMPRESSED_URL_LENGTH_DEFAULT = -1;
  public static final String[] CACHE_CONTROL_RESPONSE_DIRECTIVES = {"public", "private", "no-cache", "no-store", "no-transform",
      																															"must-revalidate", "proxy-revalidate", "max-age", "s-maxage"};
  private static final String CONSUMER_TYPE_IS_ALIAS = "ConsumerTypeIsAlias";
  private static final String ERROR_PAGE_TEMPLATE_LOCATION = "ErrorPageTemplateLocation";

  // Default values of the properties. Keep them equal with
  // values written in HTTP Provider service properties file
  private static final int FCA_SERVER_THREAD_COUNT_DEFAULT = 5;

  private static final String USE_POSTPONED_REQUEST_QUEUE = "UsePostponedRequestQueue";

  private static final String ENABLE_REQUEST_ACCOUNTING = "EnableRequestAccounting";
  private static final String USE_IPV6_FORMAT = "UseIPv6Format";

  //nested properties for the session size feature
  private static final String SESSION_SIZE_ENABLED="measureSessionSize/measurementEnabled";
  private static final String MAX_SESSION_SIZE_ALLOWED="measureSessionSize/maxSizeAllowed";
  private static final String MAX_GRAPH_NODES="measureSessionSize/libSettings/maxGraphNodes";
  private static final String MAX_GRAPH_DEPTH="measureSessionSize/libSettings/maxGraphDepth";
  private static final String SESSION_SIZE_FILTERS="measureSessionSize/libSettings/filters";
  private final String[] SESSION_SIZE_NESTED_PROPS={SESSION_SIZE_ENABLED, MAX_SESSION_SIZE_ALLOWED, MAX_GRAPH_DEPTH, MAX_GRAPH_NODES, SESSION_SIZE_FILTERS};
  //end of nested properties for the session size feature

  private boolean logInCLF = false;
  private int fileBufferSize = 4096;
  private int fcaServerThreadCount = FCA_SERVER_THREAD_COUNT_DEFAULT;
  private MimeMappingsImpl mimeTypes = new MimeMappingsImpl();
  private String[] infernames = new String[] {"index.html", "index.htm", "default.html", "default.htm"};
  private int maxCacheFileSize = 16348;
  private long sapCacheValidationTime = 86400;
  private long cacheValidationTime = 86400;
  private String cacheValidationTimeString = "max-age=" + cacheValidationTime;
  private byte[] cacheValidationTimeBytes = cacheValidationTimeString.getBytes();
  private boolean useServerHeader = true;
  private String zoneSeparator = "~";
  private boolean logResponseTime = false;
	private String logHeaderValue = null;
  private boolean logIsStatic = false;
  private String proxyMappings = "";


  private ProxyServersPropertiesImpl proxyServersProperties = new ProxyServersPropertiesImpl();
  private HttpCompressedPropertiesImpl compressedProperties = new HttpCompressedPropertiesImpl();
  private int minPoolSize = 100;
  private int maxPoolSize = 5000;
  private int decreaseCapacityPoolSize = 200;
  private String clientIpHeaderName = "X-Forwarded-For";
  private String loadBalancingCookiePrefix = "saplb_";
  private String groupInfoLocation = null;
  private String urlMapLocation = null;
  private String groupInfoRequest="";
  private String urlMapRequest="";
  private LogonGroupsManager logonGroupsManager = null;
  private boolean urlSessionTrackingForAllCookies = false;
  private boolean detailedErrorResponse = false;
  private boolean logRequestResponseHeaders = false;
  private boolean systemCookiesDataProtection = true;
  private boolean systemCookiesHTTPSProtection  = false;
  private boolean disableURLSessionTracking = false;
  private int traceResponseTimeAbove = -1;
  private boolean enableMemoryTrace = false;
  private boolean generateErrorReports = false;
  private long generateNewErrorReportTimeout = 3600;
  private String troubleShootingGuideURL = "https://sdn.sap.com/irj/sdn/wiki?path=/display/JSTS/Home";
  private String troubleShootingGuideSearchURL = "https://sdn.sap.com/irj/sdn/advancedsearch?spaceKey=JSTS&query=";
  private boolean useClientObjectsPool = true;
  private boolean isConsumerTypeIsAlias = false;
  private String errorPageTemplateLocation = "";
  private boolean usePostponedRequestQueue = false;
  private boolean enableRequestAccounting = false;
  private boolean useIPv6Format = false;

  private boolean sessionSizeEnabled=false;
  private long maxSessionSizeAllowed = -1;
  private int maxGraphNodes = 10000;
  private int maxGraphDepth = -2;
  private ArrayList<String> sessionSizeFilters_parsed = null;

  public boolean logInCLF() {
    return logInCLF;
  }

  public int getFileBufferSize() {
    return fileBufferSize;
  }

  public int getFCAServerThreadCount() {
    return fcaServerThreadCount;
  }

  public int getMaxCacheFileSize() {
    return maxCacheFileSize;
  }

  public long getSapCacheValidationTime() {
    return sapCacheValidationTime;
  }

  public long getCacheValidationTime() {
    return cacheValidationTime;
  }

  public byte[] getCacheValidationTimeBytes() {
    return cacheValidationTimeBytes;
  }

  public String getCacheValidationTimeString() {
    return cacheValidationTimeString;
  }

  public String[] getInfernames() {
    return infernames;
  }

  public boolean getUseServerHeader() {
    return useServerHeader;
  }

  public String getZoneSeparator() {
    return zoneSeparator;
  }

  public void setZoneSeparator() {
    zoneSeparator = JStartupFramework.getParam("http/zone_separator");
  }

  public boolean logResponseTime() {
    return logResponseTime;
  }

  public String getLogHeaderValue() {
    return logHeaderValue;
  }

  public boolean logIsStatic() {
    return logIsStatic;
  }

  public MimeMappings getMimeMappings() {
    return mimeTypes;
  }

  public ProxyServersProperties getProxyServersProperties() {
    return proxyServersProperties;
  }

  public HttpCompressedProperties getCompressedProperties() {
    return compressedProperties;
  }

  public int getMinPoolSize() {
    return minPoolSize;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public int getDecreaseCapacityPoolSize() {
    return decreaseCapacityPoolSize;
  }

  public String getClientIpHeaderName(){
    return clientIpHeaderName;
  }

  public String getLoadBalancingCookiePrefix() {
    return loadBalancingCookiePrefix;
  }

  public String getProxyMappings() {
    return proxyMappings;
  }

  public boolean urlSessionTrackingForAllCookies() {
    return urlSessionTrackingForAllCookies;
  }

  public boolean isSessionSizeEnabled() {
		return sessionSizeEnabled;
	}

  public Long getMaxSessionSizeAllowed() {
		return maxSessionSizeAllowed;
  }

  public int getMaxGraphNodes() {
		return maxGraphNodes;
  }

  public int getMaxGraphDepth() {
		return maxGraphDepth;
  }

  public ArrayList<String> getSessionSizeFilters() {
		return sessionSizeFilters_parsed;
  }

  /**
   * Returns the value of the GroupInfoRequest service property
   * @return groupInfoRequest
   */
  public String getGroupInfoRequest() {
    return groupInfoRequest;
  }

  /**
   * Returns the value of the UrlMapRequest service property
   * @return urlMapRequest
   */
  public String getUrlMapRequest() {
    return urlMapRequest;
  }

  public void setLogonGroupsManager(LogonGroupsManager logonGroupsManager) {
    this.logonGroupsManager = logonGroupsManager;
    logonGroupsManager.initFromServiceProperties(groupInfoLocation, urlMapLocation);
  }

  /**
   * Returns the value of the SystemCookiesHTTPSProtection service property
   */
  public boolean getSystemCookiesDataProtection() {
    return systemCookiesDataProtection ;
  }

  public boolean getSystemCookiesHTTPSProtection() {
    return systemCookiesHTTPSProtection;
  }

  public int getTraceResponseTimeAbove() {
    return traceResponseTimeAbove;
  }//end of getTraceResponseTimeAbove()

  public boolean isGenerateErrorReports() {
		return generateErrorReports;
	}//end of isGenerateErrorReports()

  public long getGenerateNewErrorReportTimeout(){
	  return generateNewErrorReportTimeout;
  }

  public String getTroubleShootingGuideURL() {
    return troubleShootingGuideURL;
  }//end of getTroubleShootingGuideURL()

  public String getTroubleShootingGuideSearchURL() {
    return troubleShootingGuideSearchURL;
  }//end of getTroubleShootingGuideSearchURL()

  public boolean isConsumerTypeIsAlias() {
    return isConsumerTypeIsAlias;
  }

  public String getErrorPageTemplateLocation() {
    return errorPageTemplateLocation;
  }//end of getErrorPageTemplate()


  private void updateSessionSizeNestedProperties(NestedProperties properties, boolean isInit){
	  for (String nestedProp:SESSION_SIZE_NESTED_PROPS){
		  if (properties.getProperty(nestedProp) != null){
			  setProperty(nestedProp, properties.getProperty(nestedProp), isInit);
		  }
	  }
  }

  /**
   *
   * @param properties
   * @param isInit - if true - server is starting and the property is not actually set  Exception should not be thrown
   * @return
   */
  public boolean setProperties(Properties properties, boolean isInit) {
    boolean needRestart = false;

    //reverse proxy mapping property is processed only on start of the service
    if (properties instanceof NestedProperties){
      updateOnlyReverseProxyNestedProperty((NestedProperties) properties);
      updateSessionSizeNestedProperties((NestedProperties) properties, isInit);
    }

    Enumeration en = properties.keys();
    while (en.hasMoreElements()) {
      String key = (String)en.nextElement();
      try {
        if (setProperty(key, (String)properties.get(key), isInit)) {
          needRestart = true;
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        Log.logError("ASJ.http.000140",
          "Cannot read and initialize property [{0}] of Http Provider service.", new Object[]{key}, e, null, null, null);
        if ( !isInit ){
          throw new IllegalArgumentException(e.getMessage());
        }
      }
    }
    return needRestart;
  }


  /**
   * Updates service runtime changeable properties. The properties set must be applied or
   * rejected if some of the values is not acceptable
   *
   * @param properties a set of changed service properties
   * @throws ServiceException if there is incorrect value and the hole set is not applied
   */
	public void updateProperties(Properties properties) throws ServiceException {
		try {
			setProperties(properties, false);
		} catch (IllegalArgumentException e) {
			throw new ServiceException(e);
		}
	}

	/**
   * Updates only the reverse proxy mapping property and creates reverse proxies classes
   * accordin ti which the mapping will be manipulated
   * @param np
   */
  private void updateOnlyReverseProxyNestedProperty(NestedProperties np){
    NestedProperties reverseProxyMappingsNestedP = np.getNestedProperties("ReverseProxyMappings");

    //if property does not exists do nothing
    if (reverseProxyMappingsNestedP == null) {
      return;
    } else {
      //get full nested property to parse it
      Properties allProperties = ServiceContext.getServiceContext().getApplicationServiceContext().getServiceState().getProperties();
      if (allProperties instanceof NestedProperties){
        reverseProxyMappingsNestedP = ((NestedProperties)allProperties).getNestedProperties("ReverseProxyMappings");
      } else {
        //this should be impossible
        return;
      }
    }

    //in case in all properties there is no reverseProxyMappings property clear existing configuration
    //TODO - check what happens when delete (restore to default) ReverseProxyMappings
    if (reverseProxyMappingsNestedP == null) {
      ReverseProxyMappings.icmPortConfigurationOrder.clear();
      return;
    }
    ReverseProxyMappings.icmPortConfigurationOrder.clear();//clear old values if there are any
    NestedProperties proxyConfigurations = reverseProxyMappingsNestedP.getNestedProperties("ProxyConfigurations");

    if (proxyConfigurations == null) {
      Log.logWarning("ASJ.http.000365",
          "Reverse Proxy Mappings property inaccuracy. " +
          "There is no reverse proxy configuration added. " +
          "Add proxy configurations for your reverse proxy." +
          "Otherwise the server will work with default configurations.",
          null, null, null);
      return;
    }

    String[] proxyConfigurationsElements = proxyConfigurations.getAllNestedPropertiesKeys();

    if (proxyConfigurationsElements == null || proxyConfigurationsElements.length ==0) {
      Log.logWarning("ASJ.http.000401",
          "Reverse Proxy Mappings property inaccuracy. " +
          "ProxyConfiguration's list is not found. Check ReverseProxyMappings/ProxyConfigurations.",
          null, null, null);
      return;
    }

    HashMap<String, ProxyConfiguration> proxyConfigurationsHashMap = new HashMap<String, ProxyConfiguration>();
    for (String proxyKey : proxyConfigurationsElements) {
      NestedProperties proxy = proxyConfigurations.getNestedProperties(proxyKey);

      if (proxy == null) {
        Log.logError("ASJ.http.000402", "Reverse Proxy Mappings property. Could not receive" +
        		" proxy configuration for alias [{0}].", new Object[]{proxyKey},null, null, null);
        continue;
      }

      String httpProxyPort = proxy.getProperty("HttpProxyPort");
      String httpsProxyPort = proxy.getProperty("HttpsProxyPort");
      String proxyHost = proxy.getProperty("ProxyHost");
      String iCMHTTPPort = proxy.getProperty("ICMHTTPPort");
      String iCMHTTPSPort = proxy.getProperty("ICMHTTPSPort");

      String sSLProxyRedirect = proxy.getProperty("SSLProxyRedirect");
      String sSLProxyReplace = proxy.getProperty("SSLProxyReplace");

      String httpProxyRedirect = proxy.getProperty("HttpProxyRedirect");
      String httpProxyReplace = proxy.getProperty("HttpProxyReplace");

      boolean override = new Boolean(proxy.getProperty("Override")).booleanValue();

      NestedProperties clientProtocol = proxy.getNestedProperties("ClientProtocolHeader");
      ClientProtocolHeader clientProtocolHeader = null;
      if (clientProtocol != null) {
        String headerName = clientProtocol.getProperty("HeaderName");
        String http = clientProtocol.getProperty("Http");
        String https = clientProtocol.getProperty("Https");
        try {
          clientProtocolHeader = new ClientProtocolHeader(headerName, http, https);
        }
        catch (IllegalArgumentException e) {
          Log.logWarning("ASJ.http.000367",
              "Wrong client protocol of the reverse proxy configuration.", e,
              null, null, null);
          clientProtocolHeader=null;
        }
      }

      ViaHeader viaHeader = null;
      NestedProperties searchedHeaderName = proxy.getNestedProperties("Header");
      if (searchedHeaderName != null) {
        String headerName = searchedHeaderName.getProperty("HeaderName");
        String headerValue = searchedHeaderName.getProperty("HeaderValue");
        try {
          viaHeader = new ViaHeader(headerName, headerValue);
        }
        catch (IllegalArgumentException e) {
          Log.logWarning("ASJ.http.000368",
              "Wrong value for via header of reverse proxy configuration.", e,
              null, null, null);
          viaHeader=null;
        }
      }

      //create ProxyConfiguration instance
      ProxyConfigurationBuilder pcBuilder = new ProxyConfigurationBuilder();
      try {
        pcBuilder.setProxyAlias(proxyKey);
      } catch (IllegalArgumentException e) {
        Log.logWarning("ASJ.http.000403",
            "Reverse Proxy Mappings property configuration inaccuracy. " +
            "Proxy alias could not be null or empty string. Please check reverse proxy configuration, proxy alaises.", e,
            null, null, null);
      }

      pcBuilder.setClientProtocol(clientProtocolHeader);
      pcBuilder.setHttpProxyPort(httpProxyPort);
      pcBuilder.setHttpsProxyPort(httpsProxyPort);
      pcBuilder.setIcmHttpPort(iCMHTTPPort);
      pcBuilder.setIcmHttpsPort(iCMHTTPSPort);
      pcBuilder.setOverride(override);
      pcBuilder.setProxyHost(proxyHost);
      pcBuilder.setSslProxyRedirect(sSLProxyRedirect);
      pcBuilder.setSslProxyReplace(sSLProxyReplace);
      pcBuilder.setViaHeader(viaHeader);
      pcBuilder.setHttpProxyRedirect(httpProxyRedirect);
      pcBuilder.setHttpProxyReplace(httpProxyReplace);

      ProxyConfiguration pconf = pcBuilder.build();
      proxyConfigurationsHashMap.put(proxyKey, pconf);

      if (LOCATION_HTTP.bePath()) {
        LOCATION_HTTP.pathT("HttpPropertiesImpl.updateOnlyReverseProxyNestedProperty(): Create proxy mapping configuration "
            + pconf + ".");
      }
    }

    //fill sslreidrect and replace for each proxy configuration
    //it should be in different loop because all the configuration should be created
    Iterator<String> iter = proxyConfigurationsHashMap.keySet().iterator();
    while ( iter.hasNext (  )  )  {
      ProxyConfiguration pc = proxyConfigurationsHashMap.get (iter.next());
      pc.fillRedirectReplace(proxyConfigurationsHashMap);
     }

    //get icm orders
    NestedProperties icmPortConfigurationOrderNestedP = reverseProxyMappingsNestedP.getNestedProperties("IcmPortConfigurationOrder");

    if (icmPortConfigurationOrderNestedP == null) {
      Log.logWarning("ASJ.http.000366",
          "Reverse Proxy Mappings property inaccuracy. There is no IcmPortConfigurationOrder for reverse proxy configuration. Add IcmPortConfigurationOrder for your configuration.",
          null, null, null);
      return;
    }

    Enumeration e = icmPortConfigurationOrderNestedP.keys();

    while(e.hasMoreElements()) {
      String icmPortKey =(String)(e.nextElement());
      String icmPortValue = icmPortConfigurationOrderNestedP.getProperty(icmPortKey);

      if (icmPortValue == null || icmPortValue.length() == 0) {
        Log.logWarning("ASJ.http.000404",
            "Reverse Proxy Mappings property inaccuracy. There is no added proxy configuration order for port [{0}]. " +
            "Please add proxy configuration order of the aliases or remove the port [{0}] from the IcmPortConfigurationOrder.",
            new Object[]{icmPortKey}, null, null, null);
        continue;
      }

      //splitting uses delimiter "," with all white spaces around ","
      String[] prxyConfAliases = icmPortValue.split("\\s*,\\s*");
      ProxyConfiguration[] proxConfs = new ProxyConfiguration[prxyConfAliases.length];
      for (int i=0; i<prxyConfAliases.length; i++) {
        proxConfs[i] = proxyConfigurationsHashMap.get(prxyConfAliases[i]);

        if (proxConfs[i] == null) {
          Log.logWarning("ASJ.http.000405",
              "Reverse Proxy Mappings property inaccuracy. For proxy configuration [{0}] in IcmPortConfigurationOrder there is no added ProxyConfiguration. " +
              "Please add proxy configuration with name (ProxyAlias) [{0}] or change it in IcmPortConfigurationOrder.",
              new Object[]{prxyConfAliases[i]}, null, null, null);
        }
      }

      ReverseProxyMappings.putIcmConfigurationOrder(icmPortKey, proxConfs);
    }
  }

  public boolean setProperty(String key, String value, boolean isInit) {
    boolean needRestart = false;
    if (LOG_CLF_KEY.equals(key)) {
      logInCLF = new Boolean(value).booleanValue();
    } else if (FILE_BUFFER_SIZE_KEY.equals(key)) {
      try {
        int newValue = (new Integer(value)).intValue();
        if (newValue >= MIN_FILE_BUFFER_SIZE) {
        	fileBufferSize = newValue;
        } else {
        	fileBufferSize = MIN_FILE_BUFFER_SIZE;
        	Log.logError("ASJ.http.000141",
        	  "Incorrect value [{0}] of property [{1}] of HTTP Provider service found: smaller than the minimal value [{2}]. " +
        	  "Will use the minimal value.", new Object[]{value, key, MIN_FILE_BUFFER_SIZE}, null, null, null);
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        fileBufferSize = 4096;
       Log.logError("ASJ.http.000142",
         "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
         "Will use default one [{2}].", new Object[]{value, key, fileBufferSize}, e, null, null, null);
      }
   } else if (FCA_SERVER_THREAD_COUNT.equals(key)) {
      try {
        fcaServerThreadCount = Integer.parseInt(value);
        needRestart = true;
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        fcaServerThreadCount = FCA_SERVER_THREAD_COUNT_DEFAULT;
        Log.logError("ASJ.http.000143",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, fcaServerThreadCount}, e, null, null, null);
        needRestart = true;
      }
    } else if (PROTOCOL_HEADER_NAME_KEY.equals(key)) {
      proxyServersProperties.setProtocolHeaderName(value.trim());
    } else if (GZIP_IMPLEMENTATION.equals(key)) {
      compressedProperties.setGZipImplementation(value);
    } else if (ALWAYS_COMPRESS_KEY.equals(key)) {
      compressedProperties.setAlwaysCompressedExtensions(HttpParseUtil.commpressedStringToExtensions(value.toLowerCase()));
      compressedProperties.setAlwaysCompressedMIMETypes(HttpParseUtil.commpressedStringToMIMEType(value.toLowerCase()));
      compressedProperties.initGzipSettings();
    } else if (NEVER_COMPRESS_KEY.equals(key)) {
      compressedProperties.setNeverCompressedExtensions(HttpParseUtil.commpressedStringToExtensions(value.toLowerCase()));
      compressedProperties.setNeverCompressedMIMETypes(HttpParseUtil.commpressedStringToMIMEType(value.toLowerCase()));
      compressedProperties.initGzipSettings();
    } else if (COMPRESSED_OTHERS_KEY.equals(key)) {
      compressedProperties.setCompressedOthers(new Boolean(value).booleanValue());
    } else if (MINIMUM_GZIP_LENGTH_KEY.equals(key)) {
      try {
        compressedProperties.setMinGZipLength(Integer.parseInt(value));
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        Log.logError("ASJ.http.000144",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. Will use default one [{2}].",
          new Object[]{value, key, compressedProperties.getMinGZipLength()}, e, null, null, null);
      }
    } else if (MAXIMUM_COMPRESSED_URL_LENGTH_KEY.equals(key)) {
      try {
        compressedProperties.setMaximumCompressURLLength(Integer.parseInt(value));
      } catch (Exception e) {
        compressedProperties.setMaximumCompressURLLength(MAXIMUM_COMPRESSED_URL_LENGTH_DEFAULT);
        Log.logError("ASJ.http.000145",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, MAXIMUM_COMPRESSED_URL_LENGTH_DEFAULT}, e, null, null, null);
      }
    } else if (MIME_TYPES_KEY.equalsIgnoreCase(key)) {
      mimeTypes.initMimeMappings(HttpParseUtil.stringToHashtable(value));
    } else if (SAP_CACHE_CONTROL_KEY.equalsIgnoreCase(key)) {
      try {
        sapCacheValidationTime = new Long(value).longValue();
      } catch (NumberFormatException e) {
        sapCacheValidationTime = 86400;
        Log.logError("ASJ.http.000146",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, sapCacheValidationTime}, e, null, null, null);
      }
    } else if (CACHE_CONTROL_KEY.equalsIgnoreCase(key)) {
      	parseCacheControl(value, isInit);
    } else if (MAX_FILE_LENGTH_FOR_CACHE_KEY.equalsIgnoreCase(key)) {
      try {
        maxCacheFileSize = new Integer(value).intValue();
        if (maxCacheFileSize <= -1) {
          maxCacheFileSize = 16348;
          Log.logError("ASJ.http.000147",
            "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
            "Will use default one [{2}].", new Object[]{value, key, maxCacheFileSize}, null, null, null);
        }
      } catch (Exception e) {
        maxCacheFileSize = 16348;
        Log.logError("ASJ.http.000148",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, maxCacheFileSize}, e, null, null, null);
      }
    } else if (INFER_NAMES_KEY.equalsIgnoreCase(key)) {
      try {
        infernames = HttpParseUtil.stringToArray(value);
      } catch (IllegalArgumentException e) {
        String infernamesAll = "";
        for (int i = 0; infernames != null && i < infernames.length; i++) {
          infernamesAll += infernames[i];
        }
        if (infernamesAll.endsWith(", ")) {
          infernamesAll = infernamesAll.substring(0, infernamesAll.length() - 2);
        }
        Log.logError("ASJ.http.000149",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, infernamesAll}, e, null, null, null);
      }
    } else if (USE_SERVER_HEADER.equals(key)) {
      try {
        useServerHeader = new Boolean(value).booleanValue();
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        useServerHeader = true;
        Log.logError("ASJ.http.000150",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, useServerHeader}, e, null, null, null);
      }
    } else if (POOL_SIZE_KEY.equals(key)) {
      try {
        boolean parsedSuccesfully = false;
        if (value.startsWith("{") && value.endsWith("}")) {
          value = value.substring(value.indexOf('{') + 1, value.lastIndexOf('}'));
          StringTokenizer valueST = new StringTokenizer(value, ",");
          if (valueST.countTokens() == 3) {
            int tempValue = Integer.valueOf(valueST.nextToken().trim()).intValue();
            if (tempValue != minPoolSize) {
              minPoolSize = tempValue;
              needRestart = true;
            }

            tempValue = Integer.valueOf(valueST.nextToken().trim()).intValue();
            if (tempValue != maxPoolSize) {
              maxPoolSize = tempValue;
              needRestart = true;
            }

            tempValue = Integer.valueOf(valueST.nextToken().trim()).intValue();
            if (tempValue != decreaseCapacityPoolSize) {
              decreaseCapacityPoolSize = tempValue;
              needRestart = true;
            }
            if (minPoolSize < 0 || maxPoolSize <= 0 || decreaseCapacityPoolSize <= 0 || maxPoolSize <= minPoolSize) {
              parsedSuccesfully = false;
            }
            parsedSuccesfully = true;
          }
        }
        if (!parsedSuccesfully) {
          Log.logWarning("ASJ.http.000068",
            "Incompatible value of the property [{0}]. The default value {100, 5000, 200} will be used.",
            new Object[]{key}, null, null, null);
          minPoolSize = 100;
          maxPoolSize = 5000;
          decreaseCapacityPoolSize = 200;
          needRestart = true;
        }
      } catch (NumberFormatException t) {
        Log.logError("ASJ.http.000151",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}, {3}, {4}].",
          new Object[]{value, key, minPoolSize, maxPoolSize, decreaseCapacityPoolSize}, t, null, null, null);
        minPoolSize = 100;
        maxPoolSize = 5000;
        decreaseCapacityPoolSize = 200;
        needRestart = true;
      }
    } else if(LOG_RESPONSE_TIME.equals(key)) {
      logResponseTime = new Boolean(value).booleanValue();
    } else if (LOG_HEADER_VALUE.equals(key)) {
      logHeaderValue = value.trim();
      if (logHeaderValue.equals("")) {
        logHeaderValue = null;
      }
      if( logHeaderValue == null || !logHeaderValue.equalsIgnoreCase(Constants.SMDHeader)) {
        SmdAccessLog.closeSMDLog();
      }
    } else if (LOG_IS_STATIC.equals(key)) {
      logIsStatic = new Boolean(value).booleanValue();
    } else if(CLIENT_IP_HEADER_NAME_KEY.equals(key)){
      try {
        clientIpHeaderName = value;
        if (clientIpHeaderName !=null){
          clientIpHeaderName = clientIpHeaderName.trim();
          if (clientIpHeaderName.length() == 0){
            clientIpHeaderName = null;
          }
        }
      } catch(Exception e) {
        clientIpHeaderName = null;
        Log.logError("ASJ.http.000152",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, clientIpHeaderName}, e, null, null, null);
      }
    } else if (LOAD_BALANCING_COOKIE_PREFIX.equals(key)) {
      loadBalancingCookiePrefix = value.trim();
      CookieParser.app_cookie_prefix = loadBalancingCookiePrefix;
      CookieParser.app_cookie_prefix_ = CookieParser.app_cookie_prefix.getBytes();
    } else if (PROXY_MAPPINGS_KEY.equals(key)) {
      try {
        proxyMappings = value.trim();
        HttpServerFrame.setProxyMappings(proxyMappings);
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        Log.logError("ASJ.http.000153",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [\"\"].", new Object[]{value, key, }, e, null, null, null);
        try {
          proxyMappings = "";
          HttpServerFrame.setProxyMappings(proxyMappings);
        } catch (OutOfMemoryError e1) {
          throw e1;
        } catch (ThreadDeath e1) {
          throw e1;
        } catch (Throwable e1) {
          Log.logError("ASJ.http.000154",
            "Incorrect value [{0}] of property [{1}] of Http Provider service found. " +
            "Will use default one [\"\"].", new Object[]{value, key}, e, null, null, null);
        }
      }
    } else if (GROUP_INFO_LOCATION.equals(key)) {
      groupInfoLocation = value.trim();
      // before it was - when the value was set to null, delete all logon groups;
      // the possible way now to do this is to remove all of them via UI because of the new concept to have DBConfig with higher prio
      if (urlMapLocation != null && !urlMapLocation.equals("") && groupInfoLocation != null && !groupInfoLocation.equals("")) {
        if (logonGroupsManager == null) {
          //service start -> it will be init later
        } else {
          if (!logonGroupsManager.logonGroupsExists()) {
            logonGroupsManager.initFromServiceProperties(groupInfoLocation, urlMapLocation);
          }
        }
      }
    } else if (URL_MAP_LOCATION.equals(key)) {
      urlMapLocation = value.trim();
      // before it was - when the value was set to null, delete all logon groups;
      // the possible way now to do this is to remove all of them via UI because of the new concept to have DBConfig with higher prio
      if (urlMapLocation != null && !urlMapLocation.equals("") && groupInfoLocation != null && !groupInfoLocation.equals("")) {
        if (logonGroupsManager == null) {
          //service start -> it will be init later
        } else {
          if (!logonGroupsManager.logonGroupsExists()) {
            logonGroupsManager.initFromServiceProperties(groupInfoLocation, urlMapLocation);
          }
        }
      }
    } else if (GROUP_INFO_REQUEST.equals(key)) {
      groupInfoRequest = value.trim();
    } else if (URL_MAP_REQUEST.equals(key)) {
      urlMapRequest = value.trim();
    } else if (URL_SESSION_TRACKING_FOR_ALL_COOKIES.equals(key)) {
      urlSessionTrackingForAllCookies = new Boolean(value).booleanValue();
    } else if (DETAILED_ERROR_RESPONSE.equals(key)){
    	detailedErrorResponse = new Boolean(value).booleanValue();
    	Log.setDetailetErrorResponse(detailedErrorResponse);
    } else if (LOG_REQUEST_RESPONSE_HEADERS.equals(key)){
    	logRequestResponseHeaders = new Boolean(value).booleanValue();
    } else if (SYSTEM_COOKIES_DATA_PROTECTION.equals(key)) {
      try {
        systemCookiesDataProtection = new Boolean(value).booleanValue();
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        systemCookiesDataProtection = true;
        Log.logError("ASJ.http.000155",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, systemCookiesDataProtection}, e, null, null, null);
      }
    } else if (SYSTEM_COOKIES_HTTPS_PROTECTION.equals(key)) {
      try {
        systemCookiesHTTPSProtection = new Boolean(value).booleanValue();
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        systemCookiesHTTPSProtection = false;
        Log.logError("ASJ.http.000229",
          "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
          "Will use default one [{2}].", new Object[]{value, key, systemCookiesHTTPSProtection}, e, null, null, null);
      }
    } else if (TRACE_RESPONSE_TIME_ABOVE.equals(key)) {
      traceResponseTimeAbove = new Integer(value).intValue();
    } else if (LOG_REQUEST_MEMORY_TRACE.equals(key)) {
      enableMemoryTrace = new Boolean(value).booleanValue();
    } else if (GENERATE_ERROR_REPORTS.equals(key)) {
    	generateErrorReports = new Boolean(value).booleanValue();
    }else if (NEW_ERROR_REPORT_TIMEOUT.equals(key)) {
    	generateNewErrorReportTimeout = new Long(value).longValue();
    } else if (TROUBLE_SHOOTING_GUIDE_URL.equals(key)) {
      setTSGInfo(value.trim());
    } else if (USE_CLIENT_OBJECTS_POOL.equals(key)) {
      useClientObjectsPool = new Boolean(value).booleanValue();
    } else if (USE_POSTPONED_REQUEST_QUEUE.equals(key)) {
      usePostponedRequestQueue = new Boolean(value).booleanValue();
    } else if (CONSUMER_TYPE_IS_ALIAS.equals(key)) {
      isConsumerTypeIsAlias = new Boolean(value).booleanValue();
    } else if (SESSION_SIZE_ENABLED.equals(key)) {
      try {
        sessionSizeEnabled = new Boolean(value).booleanValue();
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {        
        Log.logError("ASJ.http.000419", "Incorrect value [{0}] of property [{1}] " +
            "of HTTP Provider service found: {2}. The default one will be used [{3}].",
            new Object[]{sessionSizeEnabled, SESSION_SIZE_ENABLED, e.toString(), "false"},
            e, null, null, null);
        sessionSizeEnabled = false;
      }
    } else if (MAX_SESSION_SIZE_ALLOWED.equals(key)) {
      try {
        maxSessionSizeAllowed = new Long(value).longValue();
        if (maxSessionSizeAllowed < 0) {
          maxSessionSizeAllowed = -1;
        }
      } catch (NumberFormatException e) {
        Log.logError("ASJ.http.000417", "Incorrect value [{0}] of property [{1}] " +
        		"of HTTP Provider service found: {2}. The default one will be used [{3}].",
        		new Object[]{value, MAX_SESSION_SIZE_ALLOWED, e.toString(), "-1"},
        		e, null, null, null);
        maxSessionSizeAllowed = -1;
      }
    } else if (MAX_GRAPH_DEPTH.equals(key)) {
      try {
        int depth = new Integer(value).intValue();
        if(depth > 0 && depth <= 3){
          maxGraphDepth = -depth;
        } else {
          maxGraphDepth = -2;
          Log.logError("ASJ.http.000414", "Incorrect value [{0}] of property [{1}] " +
            "of HTTP Provider service found: out of the acceptable value range. " +
            "The default one will be used [{2}].", new Object[]{depth, MAX_GRAPH_DEPTH, "2"},
        		null, null, null);    	  
        }
      } catch (NumberFormatException e) {
        maxGraphDepth = -2;
        Log.logError("ASJ.http.000420", "Incorrect value [{0}] of property [{1}] " +
            "of HTTP Provider service found: {2}. The default one will be used [{3}].",
            new Object[]{value, MAX_GRAPH_DEPTH, e.toString(), "2"}, null, null, null); 
      }
    } else if (MAX_GRAPH_NODES.equals(key)) {
      try {
        int nodes = new Integer(value).intValue();
        if (nodes > 0 && nodes <= 200000){
          maxGraphNodes = nodes;
        } else {    	  
          Log.logError("ASJ.http.000415", "Incorrect value [{0}] of property [{1}] " +
            "of HTTP Provider service found: out of the acceptable value range. " +
            "The default one will be used [{2}].", new Object[]{nodes, MAX_GRAPH_NODES, "10000"},
            null, null, null);
        }
      } catch (NumberFormatException e) {
        maxGraphNodes = 10000;        
        Log.logError("ASJ.http.000421", "Incorrect value [{0}] of property [{1}] " +
            "of HTTP Provider service found: {2}. The default one will be used [{3}].",
            new Object[]{value, MAX_GRAPH_NODES, e.toString(), "10000"}, null, null, null);
      }
    } else if (SESSION_SIZE_FILTERS.equals(key)) {
    	if ("".equals(value)){
    	  //remove previously stored filters
    	  sessionSizeFilters_parsed = null;
    	} else if (!parseFilters(value)) {
    	  sessionSizeFilters_parsed = null;
        Log.logError("ASJ.http.000416", "Incorrect value [{0}] of property [{1}] " +
        		"of HTTP Provider service found. The default one will be used [{2}].",
        		new Object[]{value, SESSION_SIZE_FILTERS, ""}, null, null, null);
    	}
    } else if (ERROR_PAGE_TEMPLATE_LOCATION.equals(key)) {
      errorPageTemplateLocation = value.trim();
      if ("".equals(errorPageTemplateLocation)) {
        Responses.setDefaultErrorPageFragments();
      } else {
        try {
          Responses.setCustomErrorPageFragments(errorPageTemplateLocation);
        } catch (OutOfMemoryError e) {
          throw e;
        } catch (ThreadDeath e) {
          throw e;
        } catch (Throwable e) {
          errorPageTemplateLocation = "";
          Responses.setDefaultErrorPageFragments();
          Log.logError("ASJ.http.000216",
            "Incorrect value [{0}] of property [{1}] of HTTP Provider service found. " +
            "Will use default one [\"\"].", new Object[]{value, key}, e, null, null, null);
        }
      }
    } else if (ENABLE_REQUEST_ACCOUNTING.equals(key)) {
      enableRequestAccounting = new Boolean(value).booleanValue();
    } else if (USE_IPV6_FORMAT.equals(key)) {
      useIPv6Format = new Boolean(value).booleanValue();
    } else {

      //Do not print error if the property is Reverse proxy configuration - it is processed in method updateOnlyReverseProxyNestedProperty(NestedProperties)
      if (key == null || !key.startsWith("ReverseProxyMappings/")){
        Log.logError("ASJ.http.000156",
            "Unknown property read: [{0}], [{1}].", new Object[]{key, value}, null, null, null);
      }
    }
    return needRestart;
  }

  /**
   * Parses the value of SESSION_SIZE_FILTERS property - extracts the entries(filters) from the given value.
   * Each entry should end either with .class (for classes), or with .* for (packages).
   * Entries are separated with ", ".
   * Writes all entries in an sessionSizeFilters_parsed.
   *
   * @param value
   * @return false if the given value is not correct
   */
  private boolean parseFilters(String value){
	  ArrayList<String> filters=new ArrayList<String>();
	  value = value.trim();
	  if (value.indexOf(", ")>0){
		  Scanner scan = new Scanner(value).useDelimiter(", ");
		  while(scan.hasNext()){
				String next = scan.next().trim();
				if(next.endsWith(".class") || next.endsWith(".*")){
					filters.add(next);
				}else{
					return false;
				}
		  }
	  }else if (value.endsWith(".class")||value.endsWith(".*")){
		  filters.add(value);
	  }
	  if (!filters.isEmpty()){
		  sessionSizeFilters_parsed = filters;
		  return true;
	  }
	  return false;
  }
   	/**
   	 *
   	 * 		RFC 2616 HTTP/1.1 June, 1999
   	 * cache-response-directive =
   	 * "public" ; Section 14.9.1
   	 * | "private" [ "=" <"> 1#field-name <"> ] ; Section 14.9.1
   	 * | "no-cache" [ "=" <"> 1#field-name <"> ]; Section 14.9.1
   	 * | "no-store" ; Section 14.9.2
   	 * | "no-transform" ; Section 14.9.5
   	 * | "must-revalidate" ; Section 14.9.4
   	 * | "proxy-revalidate" ; Section 14.9.4
   	 * | "max-age" "=" delta-seconds ; Section 14.9.3
   	 * | "s-maxage" "=" delta-seconds ; Section 14.9.3
   	 * | cache-extension ; Section 14.9.6
   	 *
   	 * The directives should be comma separated, quoted or not. If a number is specified it is considered for value of max-age directive. If max-age is not specified in any way, default value 86400 is used. Directives can't be duplicated.
   	 * @param value
   	 */
   	private void parseCacheControl(String value, boolean isInit){
   		value= value.trim();
   		StringTokenizer tokenizer = new StringTokenizer(value,",");
   		boolean hasMaxAge = false;
   		//indicates if hasMaxAge is true from directive "max-age=number", not from standalone number
   		boolean maxAgeFromMaxAge = false;
   		StringBuffer result = new StringBuffer();

   		// the value of this variable will be set to cacheValidationTime if no errors occur
   		long tempCacheValidationTime = 86400;

   		while( tokenizer.hasMoreTokens() ){
   			String token = tokenizer.nextToken();
   			token = token.trim();

   			try{

   				Long.parseLong(token);
   				if( maxAgeFromMaxAge ){
   					//max-age has priority over standalone number. (Standalone numbers are taken for implicit values for max-age)
   					// 86400, max-age=1000 - max-age has bigger priority than 86400 so in Cache-Control header will be: max-age=1000
   					continue;
   				}
   				tempCacheValidationTime =  Long.parseLong(token);
   				hasMaxAge = true;
   				continue;
   			}catch(NumberFormatException e){
   				//$JL-EXC$ - ok, not a standalone number, we have to parse the string.
   			}

   			//check if this is key-value pair
   			if( token.indexOf("=") > 0 ){
   				String keyToken= token.substring(0,token.indexOf("="));
   				String valueToken = token.substring(token.indexOf("=")+1);
   				valueToken = stripQuotes(valueToken);
   				if( keyToken.equalsIgnoreCase("max-age")){
   					if( maxAgeFromMaxAge ){
   						Log.logError("ASJ.http.000157",
   						  "Invalid value for the CacheControl serivice property. " +
   						  "Duplicated directive max-age in the specified property value - [{0}]. Possible values of the property are all response directives of the Cache-Control header" +
   						  " (separated with comma if there are more then one entries) or any positive number, which denotes time period in seconds. Please specify correct value."
   						  , new Object[]{token}, null, null, null);
   						if( !isInit ){
   							throw new IllegalArgumentException("Invalid value for the Cache-Control serivice property. Duplicated directive max-age in the specified property value - ["+token+"]." +
   									" Possible values of the property are all response directives of the Cache-Control header" +
   									" (separated with comma if there are more then one entries) or any positive number, which denotes time period in seconds. Please specify correct value.");
   						}
   					}
   					tempCacheValidationTime =  Long.parseLong(valueToken);
   					hasMaxAge = true;
   					maxAgeFromMaxAge = true;
   					continue;
   				}
   			}else{
   				boolean valid = false;
   				for( int i = 0 ; i < CACHE_CONTROL_RESPONSE_DIRECTIVES.length; i++){
   					if( CACHE_CONTROL_RESPONSE_DIRECTIVES[i].equalsIgnoreCase(token)){
   						valid = true;
   						break;
   					}
   				}
   				if( !valid ){
   					//the directive is not valid, so will not present in the header
   					Log.logError("ASJ.http.000158",
   					  "Invalid value for the CacheControl serivice property. The specified directive [{0}] is not a valid one." +
   								" Possible values of the property are all response directives of the Cache-Control header (separated with comma " +
   								"if there are more then one entries) or any positive number, which denotes time period in seconds. Please specify correct value.", new Object[]{token}, null, null, null);
   					if( !isInit ){
   						throw new IllegalArgumentException("Invalid value for the CacheControl serivice property. The specified directive ["+token+"] is not a valid one." +
   								" Possible values of the property are all response directives of the Cache-Control header (separated with comma " +
   								"if there are more then one entries) or any positive number, which denotes time period in seconds. Please specify correct value. ");
   					}
   					continue;
   				}
   			}
   			addToBuffer(result, token);
   		}
   		if( ! hasMaxAge ){
   			// for backward compatibility max-age will always be presented
   			tempCacheValidationTime = 86400;
   		}
   		result.insert(0, "max-age=" + tempCacheValidationTime + (result.length()!=0?", ":"") );

   		cacheValidationTime = tempCacheValidationTime;
   		cacheValidationTimeString = result.toString();
   		cacheValidationTimeBytes = cacheValidationTimeString.getBytes();
   	}

   	private void addToBuffer(StringBuffer buffer, String value){
   		if( buffer.length() > 0 ){
   			buffer.append(", ");
   		}
   		buffer.append(value);
   	}

   	/**
   	 * removes quotes " or '
   	 * from the given String
   	 * @param value
   	 * @return the string without the quotes
   	 */
   	private final String stripQuotes(String value){
   		value = value.trim();
   		if( value.startsWith("'") || value.startsWith("\"")){
   			value = value.substring(1);
   		}
   		if( value.endsWith("'") || value.endsWith("\"")){
   			value = value.substring(0,value.length()-1);
   		}
   		return value;
   	}

	public boolean isDetailedErrorResponse() {
		return detailedErrorResponse;
	}

	public boolean isLogRequestResponseHeaders() {
		return logRequestResponseHeaders;
	}

	public boolean isUseIPv6Format() {
	  return useIPv6Format;
	}

	/**
	 * When false (default) the url session tracking is enabled for all applications;
	 * when true - no url session tracking nor saplb cookie in url rewriting is supported for better security.
	 * @return whether url session tracking is disabled
	 */
	public boolean isURLSessionTrackingDisabled() {
		return disableURLSessionTracking;
	}

  /**
   * Http service properties DisableURLSessionTracking is replaced with
   * instance param icm/HTTP/ASJava/disable_url_session_tracking. It is
   * read once during service start from the JStartupFramework and
   * cannot be changed runtime
   *
   * Its default value is false. In order to enable it you have to add
   * it in the profile file (ex. \\usr\sap\N27\SYS\profile\N27_JC60_sofD00001551A)
   *
   * If the parameter is true the url session tracking will be no more supported
   * for all applications
   *
   */
  public void setDisableURLSessionTracking() {
    String disableURLSessionTrackingString = null;
    try {
      disableURLSessionTrackingString = JStartupFramework.getParam("icm/HTTP/ASJava/disable_url_session_tracking");
      disableURLSessionTracking = new Boolean(disableURLSessionTrackingString).booleanValue();
    } catch (Exception e) {
      Log.logError("ASJ.http.000159", "Cannot convert the value of icm/HTTP/ASJava/disable_url_session_tracking parameter" +
        " to boolean value, received value from JStartupFramework: {0}" ,
        new Object[]{disableURLSessionTrackingString}, e, null, null, null);
      disableURLSessionTracking = false;
    }
  }

  public boolean isMemoryTraceEnabled() {
    return enableMemoryTrace;
  }

  public boolean isUseClientObjectPools() {
    return useClientObjectsPool;
  }

  public boolean isUsePostponedRequestQueue() {
    return usePostponedRequestQueue;
  }

  public void setTSGInfo(String value) {
    troubleShootingGuideURL = value;
    troubleShootingGuideSearchURL = value;
    if ("disable".equals(value)) {
      troubleShootingGuideSearchURL = "disable";
    } else if (value.indexOf("sdn") > -1) {
      troubleShootingGuideSearchURL = "https://sdn.sap.com/irj/sdn/advancedsearch?spaceKey=JSTSG&query=";
    } else if (value.indexOf("jst") > -1) {
      troubleShootingGuideSearchURL = "https://jst.wdf.sap.corp/dosearchsite.action?searchQuery.spaceKey=JSTTSG&searchQuery.queryString=";
    }
  }//end of setTSGInfo(String value)

  public boolean isRequestAccountingEnabled() {
    return enableRequestAccounting;
  }



}
