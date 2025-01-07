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
package com.sap.engine.services.httpserver.lib;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.interfaces.ErrorPageTemplate;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.security.core.server.csi.util.StringUtils;

public class Responses {

  //The default error page fragments
  private static final ResourceBundle errorPageFragments = ResourceBundle.getBundle("com/sap/engine/services/httpserver/interfaces/errorPageFragments");


  //Error Page Fragments
  private static String errorPageTitle;
  private static String messageID;
  private static String correctionHints;
  private static String tsgUrl;
  private static String generateErrorReport;
  

  //Directory Listing Fragments
  private static String directoryListingTitle;
  private static String directoryListing;
  private static String directoryListingMessage;
  private static String tableList;
  private static String tableEnd;
  private static String directoryLine;
  private static String fileLine;
  private static String zero;

  
  //302 Found Fragments
  private static String _302FoundTitle;
  private static String _302Message;
  private static String _302Details;  
  

  //Common Fragments
  private static String space = " ";
  private static String doublespace = "  ";
  private static String htmlBegin;
  private static String htmlEnd;
  private static String headBegin;
  private static String headEnd;
  private static String headBody;
  private static String bodyBegin;
  private static String bodyEnd;
  private static String responseCodeServerVersion;//response code + server version
  private static String br;
  private static String mainMessage;
  private static String details;


  //Error page messages
  public static final String mess1 = errorPageFragments.getString("message1");
  public static final String mess2 = errorPageFragments.getString("message2");
  public static final String mess3 = errorPageFragments.getString("message3");
  public static final String mess4 = errorPageFragments.getString("message4");
  public static final String mess5 = errorPageFragments.getString("message5");
  public static final String mess6 = errorPageFragments.getString("message6");
  public static final String mess7 = errorPageFragments.getString("message7");
  public static final String mess8 = errorPageFragments.getString("message8");
  public static final String mess9 = errorPageFragments.getString("message9");
  public static final String mess10 = errorPageFragments.getString("message10");
  public static final String mess11 = errorPageFragments.getString("message11");
  public static final String mess12 = errorPageFragments.getString("message12");
  public static final String mess13 = errorPageFragments.getString("message13");
  public static final String mess14 = errorPageFragments.getString("message14");
  public static final String mess15 = errorPageFragments.getString("message15");
  public static final String mess16 = errorPageFragments.getString("message16");
  public static final String mess17 = errorPageFragments.getString("message17");
  public static final String mess18 = errorPageFragments.getString("message18");
  public static final String mess19 = errorPageFragments.getString("message19");
  public static final String mess20 = errorPageFragments.getString("message20");
  public static final String mess21 = errorPageFragments.getString("message21");
  public static final String mess22 = errorPageFragments.getString("message22");
  public static final String mess23 = errorPageFragments.getString("message23");
  public static final String mess24 = errorPageFragments.getString("message24");
  public static final String mess25 = errorPageFragments.getString("message25");
  public static final String mess26 = errorPageFragments.getString("message26");
  public static final String mess27 = errorPageFragments.getString("message27");
  public static final String mess28 = errorPageFragments.getString("message28");
  public static final String mess29 = errorPageFragments.getString("message29");
  public static final String mess30 = errorPageFragments.getString("message30");
  public static final String mess31 = errorPageFragments.getString("message31");
  public static final String mess32 = errorPageFragments.getString("message32");
  public static final String mess33 = errorPageFragments.getString("message33");
  public static final String mess34 = errorPageFragments.getString("message34");
  public static final String mess35 = errorPageFragments.getString("message35");
  public static final String mess36 = errorPageFragments.getString("message36");
  public static final String mess37 = errorPageFragments.getString("message37");
  public static final String mess38 = errorPageFragments.getString("message38");
  public static final String mess39 = errorPageFragments.getString("message39");
  public static final String mess40 = errorPageFragments.getString("message40");
  public static final String mess41 = errorPageFragments.getString("message41");
  public static final String mess42 = errorPageFragments.getString("message42");
  public static final String mess43 = errorPageFragments.getString("message43");
  public static final String mess44 = errorPageFragments.getString("message44");
  public static final String mess45 = errorPageFragments.getString("message45");
  public static final String mess46 = errorPageFragments.getString("message46");
  public static final String mess47 = errorPageFragments.getString("message47");
  public static final String mess48 = errorPageFragments.getString("message48");
  public static final String mess49 = errorPageFragments.getString("message49");
  public static final String mess50 = errorPageFragments.getString("message50");
  public static final String mess51 = errorPageFragments.getString("message51");
  public static final String mess52 = errorPageFragments.getString("message52");
  public static final String mess53 = errorPageFragments.getString("message53");
  public static final String mess54 = errorPageFragments.getString("message54");
  public static final String mess55 = errorPageFragments.getString("message55");
  public static final String mess56 = errorPageFragments.getString("message56");
  public static final String mess57 = errorPageFragments.getString("message57");
  public static final String mess58 = errorPageFragments.getString("message58");
  public static final String mess59 = errorPageFragments.getString("message59");
  public static final String mess60 = errorPageFragments.getString("message60");
  public static final String mess61 = errorPageFragments.getString("message61");
  public static final String mess62 = errorPageFragments.getString("message62");
  public static final String mess63 = errorPageFragments.getString("message63");
  public static final String mess64 = errorPageFragments.getString("message64");
  public static final String mess65 = errorPageFragments.getString("message65");
  public static final String mess66 = errorPageFragments.getString("message66");
  public static final String mess67 = errorPageFragments.getString("message67");
  public static final String mess68 = errorPageFragments.getString("message68");
  public static final String mess69 = errorPageFragments.getString("message69");
  public static final String mess70 = errorPageFragments.getString("message70");
  
  public static String getErrorResponse(ErrorData errorData, byte[] version, String webAlias,
    String messageId, String url, String tsg, String tsgSearch, ErrorPageTemplate errorPageTemplate) {
    
    if (errorPageTemplate == null) {
      errorPageTemplate = new ErrorPageTemplate();
    }
    
    String additionalDescription = errorData.getAdditionalMessage();
    if (additionalDescription == null || additionalDescription.equals("")) {
      additionalDescription = mess9;
    }

    String additionalMessage = errorData.getMessage();
    String hints = errorData.getSupportabilityData().getCorrectionHints();

    if (!errorData.isHtmlAllowed()) {
      additionalMessage = toHtmlView(additionalMessage);
      if (!additionalDescription.startsWith("HttpException") && !additionalDescription.startsWith("WebApplicationException")) {
        additionalDescription = toHtmlView(additionalDescription); 
      }
      if (hints != null && !hints.equals("")) {
        hints = toHtmlView(hints);
      }
    }
    
    String responseCode = new String(ResponseCodes.reasonBytes(errorData.getErrorCode(), webAlias));

    StringBuilder result = new StringBuilder();
    result.append(htmlBegin);
    result.append(headBegin);
    result.append(doublespace).append(errorPageTemplate.getErrorPageTitle() != null ? errorPageTemplate.getErrorPageTitle() : errorPageTitle);
    result.append(doublespace).append(errorPageTemplate.getHeadBodyFragment() != null ? errorPageTemplate.getHeadBodyFragment() : headBody);
    result.append(headEnd);
    result.append(errorPageTemplate.getBodyBeginFragment() != null ? errorPageTemplate.getBodyBeginFragment() : bodyBegin);
    if (!messageId.equals("")) {
      responseCode += " (" + messageId + ")";
    }    
    result.append(doublespace).append((errorPageTemplate.getResponseCodeServerVersionFragment() != null ? errorPageTemplate.getResponseCodeServerVersionFragment() : responseCodeServerVersion)
      .replace("{CODE}", errorData.getErrorCode() + "").replace("{REASON}", responseCode).replace("{SERVERVERSION}", new String(version)));
    result.append(doublespace).append(br);
    String mainMessageFragment = errorPageTemplate.getMainMessageFragment();
    result.append(doublespace).append((mainMessageFragment != null ? mainMessageFragment : mainMessage)
      .replace("{MESSAGE}", (mainMessageFragment != null ? additionalMessage : mess18 + space + additionalMessage)));
    
    if (!messageId.equals("")) { 
      result.append(doublespace).append((errorPageTemplate.getMessageIDFragment() != null ? errorPageTemplate.getMessageIDFragment() : messageID).replace("{MESSAGEID}", messageId));
      
      if (hints != null && !hints.equals("")) {
        result.append(doublespace).append((errorPageTemplate.getCorrectionHintsFragment() != null ? errorPageTemplate.getCorrectionHintsFragment() : correctionHints).replace("{HINTS}", hints));
      }
      
      if (!"disable".equals(tsg)) {
        if (!tsgSearch.equals(tsg)) {
          tsgSearch = tsgSearch + messageId;
        }
        result.append(doublespace).append((errorPageTemplate.getTsgUrlFragment() != null ? errorPageTemplate.getTsgUrlFragment() : tsgUrl)
          .replace("{URL}", tsgSearch).replace("{DISPLAY}", tsg));
      }
    } else {
      if (hints != null && !hints.equals("")) {
        result.append(doublespace).append((errorPageTemplate.getCorrectionHintsFragment() != null ? errorPageTemplate.getCorrectionHintsFragment() : correctionHints).replace("{HINTS}", hints));
      }
      
      if (!"disable".equals(tsg)) {
        result.append(doublespace).append((errorPageTemplate.getTsgUrlFragment() != null ? errorPageTemplate.getTsgUrlFragment() : tsgUrl)
          .replace("{URL}", tsg).replace("{DISPLAY}", tsg));
      }
    }

    if (url != null) {
      result.append(doublespace).append((errorPageTemplate.getGenerateErrorReportFragment() != null ? errorPageTemplate.getGenerateErrorReportFragment() : generateErrorReport)
        .replace("{URL}", url));
    } 
    
    result.append(doublespace).append((errorPageTemplate.getDetailsFragment() != null ? errorPageTemplate.getDetailsFragment() : details)
      .replace("{DETAILS}", additionalDescription));
    
    if (errorPageTemplate.getAdditionalDetailsFragment() != null) {
      result.append(doublespace).append(errorPageTemplate.getAdditionalDetailsFragment());
    }
    
    result.append(bodyEnd);
    result.append(htmlEnd);
    
    return result.toString();
  }

  public static byte[] getDirectoryHead(byte[] version, String directoryName, String parent) {
    directoryName = StringUtils.escapeToHTML(directoryName);
    StringBuilder result = new StringBuilder();
    result.append(htmlBegin);
    result.append(headBegin);
    result.append(doublespace).append(directoryListingTitle.replace("{DIRNAME}", directoryName));
    result.append(doublespace).append(headBody);
    result.append(headEnd);
    result.append(bodyBegin);
    result.append(doublespace).append(responseCodeServerVersion.replace("{CODE} &nbsp; {REASON}", directoryListing.replace("{DIRNAME}", directoryName)).replace("{SERVERVERSION}", new String(version))); 
    result.append(doublespace).append(br);
    result.append(doublespace).append(mainMessage.replace("{MESSAGE}", (parent == null ? "" : directoryListingMessage.replace("{PARRENT}", parent))));
    result.append(doublespace).append(tableList);
    return result.toString().getBytes();
  }

  public static byte[] getDirectoryLine(String fileName, String date) {
    fileName = StringUtils.escapeToHTML(fileName);
    StringBuilder result = new StringBuilder();
    result.append(doublespace).append(doublespace);
    result.append(directoryLine.replace("{URL}", fileName).replace("{DISPLAY}", fileName).replace("{DATE}", date));
    return (result.toString()).getBytes();
  }

  public static byte[] getFileLine(String fileName, String size, String date) {
    StringBuilder result = new StringBuilder();
    result.append(doublespace).append(doublespace);
    result.append(fileLine.replace("{URL}", StringUtils.escapeToAttributeValue(fileName)).replace("{DISPLAY}", StringUtils.escapeToHTML(fileName)).replace("{KB}", size).replace("{DATE}", date));
    return (result.toString()).getBytes();
  }

  public static byte[] getTableEnd() {
    StringBuilder result = new StringBuilder();
    result.append(doublespace).append(tableEnd);
    result.append(bodyEnd);
    result.append(htmlEnd);
    return result.toString().getBytes();
  }

  public static String getZero() {
    return zero;
  }

  public static String toHtmlView(String msg) {
    if (msg == null) {
      return null;
    }
    return StringUtils.escapeToHTML(msg);
  }

  public static byte[] generate302FoundBody(byte[] version, String location, String webAlias) {
    StringBuilder result = new StringBuilder();
    result.append(htmlBegin);
    result.append(headBegin);
    result.append(doublespace).append(_302FoundTitle);
    result.append(doublespace).append(headBody);
    result.append(headEnd);
    result.append(bodyBegin);
    result.append(doublespace).append(responseCodeServerVersion.replace("{CODE}", ResponseCodes.code_found + "").replace("{REASON}", 
      new String(ResponseCodes.reasonBytes(ResponseCodes.code_found, webAlias))).replace("{SERVERVERSION}", new String(version)));
    result.append(doublespace).append(br);
    result.append(mainMessage.replace("{MESSAGE}", _302Message));
    result.append(doublespace).append(details.replace("{DETAILS}", _302Details.replace("{URL}", location).replace("{DISPLAY}", location)));
    result.append(bodyEnd);
    result.append(htmlEnd);
    return result.toString().getBytes();
  }
  
  public static void setCustomErrorPageFragments(String errorPageTemplateLocation) throws Exception {
    FileInputStream fis = null;
    ResourceBundle customErrorPageFragments = null;
    try {
      fis = new FileInputStream(errorPageTemplateLocation);
      customErrorPageFragments = new PropertyResourceBundle(fis);      
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          Log.traceWarning(LOCATION_HTTP, "ASJ.http.000388", 
            "Cannot close file [{0}].", e, null, null, null);
        }
      }
    }
    
    if (customErrorPageFragments != null) {
      //Error Page Fragments
      errorPageTitle = customErrorPageFragments.getString("error-page-title");
      messageID = customErrorPageFragments.getString("message-id");
      correctionHints = customErrorPageFragments.getString("correction-hints");
      tsgUrl = customErrorPageFragments.getString("tsg");
      generateErrorReport = customErrorPageFragments.getString("generate-error-report");
      
      //Directory Listing Fragments
      directoryListingTitle = customErrorPageFragments.getString("directory-listing-title");
      directoryListing = customErrorPageFragments.getString("directory-listing");
      directoryListingMessage = customErrorPageFragments.getString("directory-listing-message");
      tableList = customErrorPageFragments.getString("table-list");
      tableEnd = customErrorPageFragments.getString("table-end");
      directoryLine = customErrorPageFragments.getString("directory-line");
      fileLine = customErrorPageFragments.getString("file-line");
      zero = customErrorPageFragments.getString("zero");
      
      //302 Found Fragments
      _302FoundTitle = customErrorPageFragments.getString("302-found-title");
      _302Message = customErrorPageFragments.getString("302-message");
      _302Details = customErrorPageFragments.getString("302-details");
      
      //Common Fragments
      htmlBegin = customErrorPageFragments.getString("html-begin");
      htmlEnd = customErrorPageFragments.getString("html-end");
      headBegin = customErrorPageFragments.getString("head-begin");
      headEnd = customErrorPageFragments.getString("head-end");
      headBody = customErrorPageFragments.getString("head-body");
      bodyBegin = customErrorPageFragments.getString("body-begin");
      bodyEnd = customErrorPageFragments.getString("body-end");
      responseCodeServerVersion = customErrorPageFragments.getString("response-code-server-version");//response code + server version
      br = customErrorPageFragments.getString("br");
      mainMessage = customErrorPageFragments.getString("main-message");
      details = customErrorPageFragments.getString("details");
    }
  }
  
  public static void setDefaultErrorPageFragments() {
    //Error Page Fragments
    errorPageTitle = errorPageFragments.getString("error-page-title");
    messageID = errorPageFragments.getString("message-id");
    correctionHints = errorPageFragments.getString("correction-hints");
    tsgUrl = errorPageFragments.getString("tsg");
    generateErrorReport = errorPageFragments.getString("generate-error-report");   

    //Directory Listing Fragments
    directoryListingTitle = errorPageFragments.getString("directory-listing-title");
    directoryListing = errorPageFragments.getString("directory-listing");
    directoryListingMessage = errorPageFragments.getString("directory-listing-message");
    tableList = errorPageFragments.getString("table-list");
    tableEnd = errorPageFragments.getString("table-end");
    directoryLine = errorPageFragments.getString("directory-line");
    fileLine = errorPageFragments.getString("file-line");
    zero = errorPageFragments.getString("zero");
    
    //302 Found Fragments
    _302FoundTitle = errorPageFragments.getString("302-found-title");
    _302Message = errorPageFragments.getString("302-message");
    _302Details = errorPageFragments.getString("302-details");      

    //Common Fragments
    htmlBegin = errorPageFragments.getString("html-begin");
    htmlEnd = errorPageFragments.getString("html-end");
    headBegin = errorPageFragments.getString("head-begin");
    headEnd = errorPageFragments.getString("head-end");
    headBody = errorPageFragments.getString("head-body");
    bodyBegin = errorPageFragments.getString("body-begin");
    bodyEnd = errorPageFragments.getString("body-end");
    responseCodeServerVersion = errorPageFragments.getString("response-code-server-version");//response code + server version
    br = errorPageFragments.getString("br");
    mainMessage = errorPageFragments.getString("main-message");
    details = errorPageFragments.getString("details");
  }
  
}
