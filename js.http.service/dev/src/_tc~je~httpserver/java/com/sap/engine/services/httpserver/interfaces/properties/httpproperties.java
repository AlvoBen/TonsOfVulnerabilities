package com.sap.engine.services.httpserver.interfaces.properties;

import java.util.ArrayList;

/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

public interface HttpProperties {
  public boolean logInCLF();

  public int getFileBufferSize() ;

  public int getFCAServerThreadCount();

  public int getMaxCacheFileSize();

  public long getSapCacheValidationTime();

  public long getCacheValidationTime();

  public byte[] getCacheValidationTimeBytes();

  public String getCacheValidationTimeString();

  public String[] getInfernames();

  public boolean getUseServerHeader();

  public String getZoneSeparator();

  public boolean logResponseTime();
  
  public String getLogHeaderValue();

  public boolean logIsStatic();

  public int getMinPoolSize();

  public int getMaxPoolSize();

  public int getDecreaseCapacityPoolSize();

  public MimeMappings getMimeMappings();

  public ProxyServersProperties getProxyServersProperties();

  public HttpCompressedProperties getCompressedProperties();

  public String getClientIpHeaderName();

  public String getLoadBalancingCookiePrefix();
 
  /**
   * Returns the value of the GroupInfoRequest service property
   * @return groupInfoRequest
   */
  public String getGroupInfoRequest();
  
  /**
   * Returns the value of the UrlMapRequest service property
   * @return urlMapRequest
   */
  public String getUrlMapRequest();
  
  public boolean urlSessionTrackingForAllCookies();
  
  public boolean isDetailedErrorResponse();
  
  public boolean isLogRequestResponseHeaders();
  
  public boolean getSystemCookiesDataProtection();
  
  /**
   * Returns the value of the SystemCookiesHTTPSProtection service property 
   */
  public boolean getSystemCookiesHTTPSProtection();
  
  public boolean isURLSessionTrackingDisabled();

  public int getTraceResponseTimeAbove();
  
  /**
   * Returns true if memory allocation statistic per request is enabled;
   * @return
   */
  public boolean isMemoryTraceEnabled();

  public boolean isGenerateErrorReports();
  
  public long getGenerateNewErrorReportTimeout();
  
  public String getTroubleShootingGuideURL();
  
  public String getTroubleShootingGuideSearchURL();
  
  public boolean isUseClientObjectPools();
  
  public boolean isConsumerTypeIsAlias();
  
  public String getErrorPageTemplateLocation();
  
  public boolean isRequestAccountingEnabled();

  public boolean isUsePostponedRequestQueue();
  
  public boolean isUseIPv6Format();
  
  public boolean isSessionSizeEnabled();
  
  public Long getMaxSessionSizeAllowed();
  
  public int getMaxGraphNodes();
  
  public int getMaxGraphDepth();
  
  public ArrayList<String> getSessionSizeFilters();
    
}
