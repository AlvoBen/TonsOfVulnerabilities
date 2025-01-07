/*****************************************************************************************************************************************************************************
Apex Class Name : AKANameFetch_S_HUM
Version         : 1.0
Created On      : 01/11/2016
Function        : This class will have the callout to SLLoginService to fetch the AKA Names.
Test Class      : AKANameFetch_ST_HUM

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi                              01/11/2011                  Original Version
********************************************************************************************************************************************************************************/
public class AKANameFetch_S_HUM 
{
 
    public String setRequestgetResponse(HttpRequest request, HUM_Webservice_Callout__c oSvcHeaderSLLogin , string sRequestBody)
    {
       String sAkaNameRtn = '';
        if(!String.isBlank(sRequestBody)&&(oSvcHeaderSLLogin!=null)&&(oSvcHeaderSLLogin.Active__c))
        {
            request.setMethod(oSvcHeaderSLLogin.Request_Type__c);
            sRequestBody = '{' +'\"'+'userId'+'\"' +':' +'\"' +sRequestBody+'\"' +'}';
            request.setBody(sRequestBody);
            if(!Test.isRunningTest() && String.isNotBlank(oSvcHeaderSLLogin.Certificate_Name__c))           
            {
                request.setClientCertificateName(oSvcHeaderSLLogin.Certificate_Name__c); 
            }                     
            if(String.isNotBlank(oSvcHeaderSLLogin.Content_Type__c))
            {
                request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaderSLLogin.Content_Type__c);
            }            
            request.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
            request.setEndpoint(oSvcHeaderSLLogin.End_Point_URL__c);
        
            //getting the response
            Http http = new Http();
            HTTPResponse res = http.send(request);
        
            //processing the obtained response
            if(res.getStatusCode() == HUMConstants.HTTP_OK || res.getStatusCode() == HUMConstants.HTTP_ACCEPTED) 
            {
            Map<String, Object> Mapresults = (Map<String, Object>)JSON.deserializeUntyped(res.getBody());            
            sAkaNameRtn = String.valueOf(Mapresults.get('akaName'));                
            }                     
        }
        return sAkaNameRtn;
    }
    }