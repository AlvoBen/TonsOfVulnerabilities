package com.sap.engine.services.httpserver.server.errorreport;

import com.sap.engine.services.httpserver.interfaces.SupportabilityData;

/**
 * This class holds information that is needed when generating 
 * error reports triggered by the end user.
 * 
 * @author Violeta Georgieva
 */
public class ErrorReportInfoBean {
	private String clientIp = "";
	private String jsessionId = "";
	private long time = 0;
	private String webApp = "";
	private int responseCode = 0;
	private SupportabilityData supportabilityData = null;
	private String logIdISE500 = "";
	
	public ErrorReportInfoBean(String clientIp, String jsessionId,
			long time, String webApp, int responseCode, SupportabilityData supportabilityData) {
		this.clientIp = clientIp;
		this.jsessionId = jsessionId;
		this.time = time;
		this.webApp = webApp;
		this.responseCode = responseCode;
    this.supportabilityData = supportabilityData;
	}//end of constructor

	public String getClientIp() {
		return clientIp;
	}//end of clientIp
	
	public String getJsessionId() {
		return jsessionId;
	}//end of getJsessionId()
	
	public SupportabilityData getSupportabilityData() {
		return supportabilityData;
	}//end of getSupportabilityInfo()
	
	public long getTime() {
		return time;
	}//end of getTime()
	
	public String getWebApp() {
		return webApp;
	}//end of getWebApp()

  public int getResponseCode() {
    return responseCode;
  }//end of getResponseCode()
	
  public String getLogIdISE500() {
    return logIdISE500;
  }//end of getLogIdISE500()

  public void setLogIdISE500(String logIdISE500) {
    this.logIdISE500 = logIdISE500;
  }//end of setLogIdISE500(String logIdISE500)

  public static int getInternalCategorization(int errorCode, SupportabilityData supportabilityData, String aliasName, String applicationName) {
    String newline = "<br>";

    StringBuilder internalCategorization = new StringBuilder();
    internalCategorization.append("Response code = ").append(errorCode).append(newline);
    internalCategorization.append("Throwable hashcode = ").append(supportabilityData.getStackTrace().hashCode()).append(newline);
    internalCategorization.append("Message ID = ").append(supportabilityData.getMessageId()).append(newline);
    internalCategorization.append("DC name = ").append(supportabilityData.getDcName()).append(newline);
    internalCategorization.append("CSN component = ").append(supportabilityData.getCsnComponent()).append(newline);
    internalCategorization.append("Context root = ").append(aliasName).append(newline);
    internalCategorization.append("Application name = ").append(applicationName).append(newline);

    return (internalCategorization.toString()).hashCode();
  }//end of getInternalCategorization(int errorCode, SupportabilityData supportabilityData, String aliasName, String applicationName)

}//end of class
