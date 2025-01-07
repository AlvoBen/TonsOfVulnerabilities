/*****************************************************************************************************************************************************************************
Apex Class Name : AKANameFetch_ST_HUM
Version         : 1.0
Created On      : 01/14/2016
Function        : Test Class
Test Class      : Test Class for AKANameFetch_S_HUM

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi                              01/14/2011                  Original Version
********************************************************************************************************************************************************************************/
@isTest public class AKANameFetch_ST_HUM implements HttpCalloutMock 
{   
    private HttpResponse resp;   
    public AKANameFetch_ST_HUM (String testBody)
      {        
          resp = new HttpResponse();       
          resp.setBody(testBody);        
          resp.setStatusCode(200);
          System.assertEquals(200, resp.getStatusCode());        
          System.assertEquals(resp.getBody(), testBody);     
      }        
  
     public HTTPResponse respond(HTTPRequest req) 
      {   
          return resp;    
      } 
     public testmethod static void testHUMSLLoginServiceWS() 
      {       
          HUM_Webservice_Callout__c oSvcHeaderSLLogin = new HUM_Webservice_Callout__c(Content_Type__c='application/json',Request_Type__c='POST',Name='SL_Login_Service', Certificate_Name__c='Humana_GuidanceCare_Venus12', Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/SLLoginService/GetAKAName', Timeout__c='12000');
          HttpRequest req = new HttpRequest();         
          String testBody = '{' +'\"'+'userId'+'\"' +':' +'\"' +'PXP0288'+'\"' +'}';               
          HttpCalloutMock mock = new AKANameFetch_ST_HUM (testBody);        
          Test.setMock(HttpCalloutMock.class, mock);
          AKANameFetch_S_HUM callout = new AKANameFetch_S_HUM();       
          String retunAKA = callout.setRequestgetResponse(req,oSvcHeaderSLLogin,testBody);        
      }
}