/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.filters;

import java.io.IOException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.ServerChain;
import com.sap.engine.services.httpserver.chain.ServerFilter;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import static com.sap.engine.services.httpserver.lib.ResponseCodes.code_ok;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.entity_header_content_type_;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.entity_header_content_length_;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.response_header_server_;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.entity_header_date_;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderValues.text_palin_;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;

/**
 * This filter generates internal response about logon groups configuration.
 * These requests are sent by web dispatcher to retrive the current configuration of 
 * the logon groups on the J2EE server. The protocol(exact urls) is defined by http
 * service properties GroupInfoRequest and UrlMapRequest. The value of 'GroupInfoRequest'
 * should be the same as the one of wdisp/J2EE/group_info_location web dispatcher
 * profile parameter. Correspondingly, the value of 'UrlMapRequest' should be the same
 * as the one of wdisp/J2EE/url_map_location web dispatcher profile parameter.
 *
 * Responces have a format following the format of the configuration files for logon 
 * groups of the logon groups. On the request 'GroupInfoRequest', the J2EE Engine should
 * respond with icrgroups.txt file format and on 'UrlMapRequest' - urlinfo.txt file format.
 * The complete format and more information about web dispatcher can be found on  
 * http://aiokeh.wdf.sap.corp:1080/SAPIKS2/contentShow.sap?_CLASS=IWB_EXTHLP&_LOIO=87252C4142AEF623E10000000A155106&&TMP_IWB_TASK=PREVIEW2&RELEASE=700&LANGUAGE=EN
 * 
 * @author Violeta Uzunova (I024174)
 *
 */
public class LogonGroupsConfigRequestsFilter extends ServerFilter {
    
  private LogonGroupsManager logonGroupsManager;
  private String message;
  
  /**
   * This method is invoked to prepare the object for processing the request
   */
  public void init(FilterConfig config) throws FilterException {
    this.logonGroupsManager = ServiceContext.getServiceContext().getLogonGroupsManager();    
  }
  
  /**
   * The method is called by the chain each time a request/response pair 
   * is passed through the chain due to a client request for a resource at 
   * the end of the chain. The <code>Chain</code> passed in to this method
   * allows the <code>Filter</code> to pass on the request and response to
   * the next entity in the chain.
   *  
   * @param request
   * a <code>Request</code> object that contains the client request
   * 
   * @param response
   * a <code>Response</code> object that contains the response to the client
   * 
   * @param chain
   * a <code>Chain</code> object that gives access to surrounding scopes and 
   * allows request and response to be passed to the next <code>Filter</code>
   * 
   * @throws FilterException
   * if the request could not be processed
   * 
   * @throws java.io.IOException
   * if an input or output error is detected
   */
  public void process(HTTPRequest request, HTTPResponse response, ServerChain chain) throws FilterException, IOException {
    if (logonGroupsManager == null) {
      logonGroupsManager = ServiceContext.getServiceContext().getLogonGroupsManager();
    }
    HttpProperties httpProperties = chain.getServerScope().getHttpProperties();
    
    // GroupInfoRequest and UrlMapRequest service properties are empty - this feature is switched off => proceed along the chain 
    if (httpProperties.getGroupInfoRequest() != null && !httpProperties.getGroupInfoRequest().equals("") &&
        httpProperties.getUrlMapRequest() != null && !httpProperties.getUrlMapRequest().equals("")) {
      chain.proceed();      
    }
    
    if (request.getURLPath().equalsIgnoreCase(httpProperties.getGroupInfoRequest())) {
      // the url is equal to the value of the GroupInfoRequest service property => respond appropriate; 
      // if this is normal application request the application will be broken 
      message = logonGroupsManager.answerGroupInfoRequest();
      // TODO may be more elegant way to obtain the value for Server header
      setAllHeaders(response, message.length(), request.getClient().getRequestAnalizer().getHostDescriptor().getVersion());
      
      // TODO use sendResponse(String) when the interface is complete;
      // it should sent the resonse with this messageBody when all headers are already set 
      response.getRawResponse().makeAnswerMessage(message.getBytes());  
      return;      
    } else if (request.getURLPath().equalsIgnoreCase(httpProperties.getUrlMapRequest())) {
      // the url is equal to the value of the GroupInfoRequest service property => respond appropriate;
      // if this is normal application request the application will be broken 
      message = logonGroupsManager.answerUrlMapRequest(); 
      // TODO may be more elegant way to obtain the value for Server header
      setAllHeaders(response, message.length(), request.getClient().getRequestAnalizer().getHostDescriptor().getVersion());
      
      // TODO use sendResponse(String) when the interface is complete;
      // it should sent the resonse with this messageBody when all headers are already set
      response.getRawResponse().makeAnswerMessage(message.getBytes()); 
      return;       
    } else {
      chain.proceed(request, response);       
    }
  }
  
  /**
   * The method is called to finilize its work and clean up used resources 
   */
  public void destroy() {
    

  }

  /**
   * sets all necessary http headers for both config requests
   * 
   * @param response HTTPResponse
   * @param responseLength responce length
   * @param serverVersion   server version
   */
  private void setAllHeaders(HTTPResponse response, int responseLength, byte[] serverVersion) {
    response.getRawResponse().setResponseCode(code_ok); 
    response.getRawResponse().putHeader(entity_header_content_type_, text_palin_);
    // TODO - currently this method is overwritten by the similar in the rawResponse.sendResponse(message) 
    response.getRawResponse().getHeaders().putIntHeader(entity_header_content_length_, responseLength); 
    response.getRawResponse().getHeaders().putDateHeader(entity_header_date_);
    if (ServiceContext.getServiceContext().getHttpProperties().getUseServerHeader()) {
      response.getRawResponse().putHeader(response_header_server_, serverVersion);
    }    
  }

}
