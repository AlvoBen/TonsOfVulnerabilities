/*******************************************************************************************************************************************************************************************************************************
Apex Class Name  : HUMCalloutUtilityHelper
Version          : 1.0
Created Date     : May 07 2015
Function         : Here all the utility methods for the Web service call will be stored
Test Class       : HUMCalloutUtilityHelperTest
********************************************************************************************************************************************************************************************************************************
Modification Log:
* Developer Name                  Code Review             Date                      Description
*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma                     22901               05/07/2015                  Original Version 
* Sharan Shanmugam					23024       		05/12/2015					Added the parsing xml block.
* Gargey Sharma                     23473               05/26/2015                  Added methods to get child element from xml block
* Vishal Verma                      23473               06/02/2015                  Fixing Code Review Comments for Review # 23473
* Abhishek Maurya                   2308421             06/26/2021                  CRM Storefront SDoH: Action Required Prompt
********************************************************************************************************************************************************************************************************************************/
public with sharing class HUMCalloutUtilityHelper
{
   /*
     * Method Name : callWebservice
     * Description : Method to call the webservice 
     * Return type : HTTPResponse
     * Paramater   : String,String,String,String,String, String
     */
    public static HTTPResponse callWebservice(String sEndpoint, String certificate , String sbody ,String sSelectedMethod,String sSOAPAction,String sContent)
    {
        try
        {
            HttpRequest req = new HttpRequest();
            req.setMethod(sSelectedMethod );  
            if(String.isNotBlank(sContent)) { req.setHeader(Label.HUMServiceTesterController_content_type, sContent); }
            if(String.isNotBlank(sSOAPAction)) { req.setHeader(Label.HUMServiceTesterControllerSOAPAction, sSOAPAction); }    
            req.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
            req.setBody(sbody);
            if(!Test.isRunningTest() && String.isNotBlank(certificate)) { req.setClientCertificateName(certificate); }
            req.setEndpoint(sEndpoint);  
            Http http = new Http();
            HTTPResponse res = http.send(req);
            return res;
        }
        catch(Exception e)
        {   
            HUMExceptionHelper.logErrors(e,'HUMCalloutUtilityHelper','callWebservice');
            return null;
        }
    }
    
    /*
     * Method Name : parseXML
     * Description : Method to parse xml. 
     * Return type : Map<String,String>
     * Paramater   : XmlStreamReader, String
     */
    public static Map<String,String> parseXML(XmlStreamReader xsr , String sNode)
    {
        Map<String,String> respMap = new Map<String,String>();
        if(xsr != null && String.isNotblank(sNode))
        {
	        while(xsr.hasNext())
	        {
	            if (xsr.getEventType() == XmlTag.START_ELEMENT && sNode == xsr.getLocalName()) 
	            {    
	                respMap =  getAttribute(xsr);
	                break;
	            }    
	            xsr.next();
	        }
        }
        return respMap;
    }
    
    /*
     * Method Name : getAttribute
     * Description : Method to get the attributr for parsing the xml. 
     * Return type : Map<String,String>
     * Paramater   : XmlStreamReader, String
     */
    public static Map<String,String> getAttribute(XmlStreamReader xsr)
    {
        Map<String,String>  respMap = new Map<String,String>();
        for (Integer i = 0; i < xsr.getAttributeCount(); i ++) 
        { 
        	respMap.put(xsr.getAttributeLocalName(i),xsr.getAttributeValueAt(i));
        }
        return respMap;
    }
    
    /*
     * Method Name : fetchChildElements
     * Description : Method to get list of child elements. 
     * Return type : List<Dom.XMLNode>
     * Paramater   : Dom.XMLNode
     */
    public static List<Dom.XMLNode> fetchChildElements(Dom.XMLNode xmlNode)
    {
        List<Dom.XMLNode> lstChildNodes = new List<Dom.XMLNode>();
 	    if(xmlNode != null)
		{    		
	  		lstChildNodes.addAll(xmlNode.getChildElements());
	  	}
  		return lstChildNodes;
    }
    
    /*
     * Method Name : fetchChild
     * Description : Method to get child element for a node. 
     * Return type : String
     * Paramater   : Dom.XMLNode, String
     */
    public static string fetchChild(Dom.XMLNode xmlNode , String sNode)
    {
    	String sChild = '';
    	if(xmlNode != null && String.isNotblank(sNode))
    	{
        	sChild = xmlNode.getChildElement(sNode,null).getText();
    	}
    	return sChild;
    }


	/**
	* <p>
	* Method to populate value in the hidden variable
	*
	* @param  HttpResponse http response
	* @param  String Class  Name
	* @param  String Method Name
	* @param  String Record Id
	* @param  String Object
	* @param  String Severity
	* @return String
	*/
	public static void logFaultError(HttpResponse respFaultCode, String sClassName, String sMethodName, String sRecordId, String sObjectAPI, String sSeverity)
	{
		try
		{
			Error_Log__c objErrorLog = new Error_Log__c();
			objErrorLog.Class_Name__c = sClassName;
			objErrorLog.Method_Name__c = sMethodName;
			objErrorLog.Record_Id__c = sRecordId;
			objErrorLog.Object_Type__c = sObjectAPI;
			objErrorLog.Request_Sent__c = true;
			objErrorLog.Severity__c = sSeverity;
			objErrorLog.Type__c = GLOBAL_CONSTANT_HUM.Pharmacy_IntegrationError_HUM;
			if (respFaultCode != null)
			{
				objErrorLog.Status__c = String.valueof(respFaultCode.getStatusCode());
				if (respFaultCode.getBody() != null && !String.isBlank(respFaultCode.getBody()))
				{
					String sJsonResponse = respFaultCode.getBody();
					sJsonResponse = sJsonResponse.replace('-', '_');
					FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
					faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(sJsonResponse, FaultResponse_DTO_HUM.class);
					if (faultResponse.Fault != null && faultResponse.Fault.detail != null)
					{
						if (faultResponse.Fault.detail.errorInfo != null)
						{
							objErrorLog.Error_Code__c = faultResponse.Fault.detail.errorInfo.error_code;
							objErrorLog.Error_Message__c = faultResponse.Fault.detail.errorInfo.error_message;
						}
						if (faultResponse.Fault.detail.transactionInfo != null)
						{
							objErrorLog.Integration_Point__c = faultResponse.Fault.detail.transactionInfo.URL_in;
							objErrorLog.Operation__c = faultResponse.Fault.detail.transactionInfo.transaction_id;
						}
					}
				}
				else
				{
					objErrorLog.Error_Message__c = GLOBAL_CONSTANT_HUM.Pharmacy_Error_Status_Codes_HUM.containskey(respFaultCode.getStatusCode()) ? GLOBAL_CONSTANT_HUM.Pharmacy_Error_Status_Codes_HUM.get(respFaultCode.getStatusCode()) : '';
				}

			}
			insert objErrorLog;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'HUMCalloutUtilityHelper', 'logFaultError');
		}
	}
    /**
	* <p>
	* Method to populate value in the hidden variable
	*
	* @param  HttpResponse http response
	* @param  String Class  Name
	* @param  String Method Name
	* @param  String Record Id
	* @param  String Object
	* @param  String Severity
	* @return String
	*/
	public static Error_Log__c logBulkFaultError(HttpResponse respFaultCode, String sClassName, String sMethodName, String sRecordId, String sObjectAPI, String sSeverity)
	{
        Error_Log__c objErrorLog = new Error_Log__c();
		try
		{
			objErrorLog.Class_Name__c = sClassName;
			objErrorLog.Method_Name__c = sMethodName;
			objErrorLog.Record_Id__c = sRecordId;
			objErrorLog.Object_Type__c = sObjectAPI;
			objErrorLog.Request_Sent__c = true;
			objErrorLog.Severity__c = sSeverity;
			objErrorLog.Type__c = GLOBAL_CONSTANT_HUM.Pharmacy_IntegrationError_HUM;
			if (respFaultCode != null)
			{
				objErrorLog.Status__c = String.valueof(respFaultCode.getStatusCode());
				if (respFaultCode.getBody() != null && !String.isBlank(respFaultCode.getBody()))
				{
					String sJsonResponse = respFaultCode.getBody();
					sJsonResponse = sJsonResponse.replace('-', '_');
					FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
					faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(sJsonResponse, FaultResponse_DTO_HUM.class);
					if (faultResponse.Fault != null && faultResponse.Fault.detail != null)
					{
						if (faultResponse.Fault.detail.errorInfo != null)
						{
							objErrorLog.Error_Code__c = faultResponse.Fault.detail.errorInfo.error_code;
							objErrorLog.Error_Message__c = faultResponse.Fault.detail.errorInfo.error_message;
						}
						if (faultResponse.Fault.detail.transactionInfo != null)
						{
							objErrorLog.Integration_Point__c = faultResponse.Fault.detail.transactionInfo.URL_in;
							objErrorLog.Operation__c = faultResponse.Fault.detail.transactionInfo.transaction_id;
						}
					}
				}
				else
				{
					objErrorLog.Error_Message__c = GLOBAL_CONSTANT_HUM.Pharmacy_Error_Status_Codes_HUM.containskey(respFaultCode.getStatusCode()) ? GLOBAL_CONSTANT_HUM.Pharmacy_Error_Status_Codes_HUM.get(respFaultCode.getStatusCode()) : '';
				}

            }
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'HUMCalloutUtilityHelper', 'logFaultError');
		}
        return objErrorLog;
	}
}