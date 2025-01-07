/**************************************************************************** 
Apex Class Name  : DCNServiceHumI 
Version          : 1.0  
Created Date     : September 16, 2015
Function         : used to get associated DCN number
Test Class       : DCNServiceTestHumI
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        26705             09/16/2015                    Original Version
***************************************************************************************************/
public with sharing class DCNServiceHumI 
{
   /*
    * Method Name : GetDCNList
    * Description : This method is used to call DCNservice and return response
    * Return type : DCNServiceResponseHelperHumI     
    * Paramater   : DCNServiceRequestHelperHumI
    */
    public static DCNServiceResponseHelperHumI GetDCNList(DCNServiceRequestHelperHumI oDCNRequest) 
    {
       DCNServiceResponseHelperHumI oDCNResponse = new DCNServiceResponseHelperHumI();
       try
       {
       	  HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues('DCNService');
       	  string sBody = JSON.serialize(oDCNRequest);
       	  if(oSvcHeaders.Active__c && sBody!=null)
       	  {
       	  	HttpResponse response = HUMCalloutUtilityHelper.callWebservice(oSvcHeaders.End_Point_URL__c, oSvcHeaders.Certificate_Name__c,sBody , oSvcHeaders.Request_Type__c, '',oSvcHeaders.Content_Type__c);
       	  	if(response.getStatusCode() == 200 || response.getStatusCode() == 202)
       	  	{
       	  	  	oDCNResponse = (DCNServiceResponseHelperHumI)System.JSON.deserialize(response.getbody(), DCNServiceResponseHelperHumI.class);
       	  	}
       	  }
       }
       catch(Exception e)
       {    
           HUMExceptionHelper.logErrors(e,'DCNServiceHumI','GetDCNList');
       } 
       return oDCNResponse;
    }
}