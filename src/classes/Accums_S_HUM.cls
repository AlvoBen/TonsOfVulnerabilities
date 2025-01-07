/************************************************************************************************************************************************
Apex Class Name  : Accums_S_HUM
Version          : 1.0
Created Date     : Feb 26 2016
Function         : Class to return the mock data for Benefits Current table
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani                                  02/26/2016                    Original Version
* Avinash Choubey            31077	              04/18/2016                    Design Change in All Accums Req ( 237142, 246888 etc)
* Avinash Choubey            32583     		 	  07/12/2016          			Removed MBE Dependency for Gen Key REQ - 269975
*************************************************************************************************************************************************/

public class Accums_S_HUM  {
    
    /**
    * Returns an object of HttpRequest
    * <p>
    * Method to get the request Details from custom setting for Benefits History and current section
    *
    * @pram  HttpRequest object
    * @param  HUM_Webservice_Callout__c object
    * @param  request body in string format 
    * @return  HttpRequest object
    * @see
    */
    public static HttpRequest builtAccumsRequest(HttpRequest request,HUM_Webservice_Callout__c accumServiceObj,string sRequestBody)
    {
        if(!String.isBlank(sRequestBody) && accumServiceObj != null && accumServiceObj.Active__c)
        {
            request.setMethod(accumServiceObj.Request_Type__c);
            request.setBody(sRequestBody);
            if(!Test.isRunningTest() && String.isNotBlank(accumServiceObj.Certificate_Name__c))
            {
                request.setClientCertificateName(accumServiceObj.Certificate_Name__c);
            }
            if(String.isNotBlank(accumServiceObj.Content_Type__c))
            {
                request.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, accumServiceObj.Content_Type__c);
            }
            request.setHeader(GLOBAL_CONSTANT_HUM.Connection_HUM,GLOBAL_CONSTANT_HUM.Keep_alive_HUM);
            request.setEndpoint(accumServiceObj.End_Point_URL__c);
            request.setHeader(GLOBAL_CONSTANT_HUM.SOAPAction_HUM,'');
            if(accumServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(accumServiceObj.Timeout__c));
        }
        return request;
    }
 
}