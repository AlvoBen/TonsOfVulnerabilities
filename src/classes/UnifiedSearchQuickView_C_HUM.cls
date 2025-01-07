/**********************************************************************************************************
Apex Class Name  : UnifiedSearchQuickView_C_HUM
Version          : 1.0
Created Date     : 12/21/2020
Function         : Used to call quickview service from coveo window
************************************************************************************************************
Modification Log:
* Developer Name                          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Akshay Pai                               12/21/2020                					 original Version
**************************************************************************************************************/
public with sharing class UnifiedSearchQuickView_C_HUM 
{
   public static String DocumentHtml{get;set;} 
   public String request='';
   
    /*
    * MethodName : fetchDocumentHtml
    * Async call to get the html data for quick view                                
    * @return Object
    */
   public Object fetchDocumentHtml()
   {
        try
        {
            HUMConstants__c oHUMCoveoOrgId = HUMConstants__c.getValues(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_ORG);
            HUM_Webservice_Callout__c oSetting = HUM_Webservice_Callout__c.getValues(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_SERVICE);
            
            String documentID = ApexPages.currentPage().getParameters().get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_DOCUMENTID);
            String encodedDocId = EncodingUtil.urlEncode(documentID, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UTF8);
            String searchToken = ApexPages.currentPage().getParameters().get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_SEARCHTOKEN);
            Continuation con = new Continuation(120);           
            con.continuationMethod = UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESS_QUICKVIEW_RESPONSE;

            HttpRequest req2 = new HttpRequest ();
            req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_AUTHORIZATION, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_BEARER+searchToken);
            req2.setEndpoint(oSetting.End_Point_URL__c+UNIFIEDSEARCH_CONSTANTS_HUM.STRING_HTMLURL);
            if(!test.isRunningTest() && String.isNotBlank(osetting.Certificate_Name__c) )
            {
                req2.setClientCertificateName(oSetting.Certificate_Name__c);
            }
            req2.setMethod(oSetting.Request_Type__c);
            req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CONTENTTYPE, oSetting.Content_Type__c); 
            req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_ORGANIZATIONID,oHUMCoveoOrgId.StringValue__c);
            req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIQUEID,encodedDocId);
            req2.setBody(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_EMPTYBODY);
            this.request = con.addHttpRequest(req2);
			return con;			
        }
       catch(Exception e)
       {
           UnifiedSearchQuickView_C_HUM.DocumentHtml= System.Label.UnifiedSearch_ErrMsg_QuickView;
           HUMExceptionHelper.logErrors(e, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHQUICKVIEW, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FETCHDOCUMENTHTML);
           return null;
       }
    }
    
    /*
    * MethodName : processQuickViewResponse
    * callback method for continuation                               
    * @return Object
    */
	public Object processQuickViewResponse()
	{
        try
        {
            HttpResponse res = Continuation.getResponse(this.request);
            if(res != null && ((res.getStatusCode() == UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_HTTP_OK_HUM || res.getStatusCode() == UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_HTTP_ACCEPTED_HUM) && (!res.getBody().containsIgnoreCase(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FAULT_CODE))))      
            {
                UnifiedSearchQuickView_C_HUM.DocumentHtml = res.getBody();
            }
            else
            {
                if(res.getBody().containsIgnoreCase(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FAULT_CODE))
                {
                    HUMCalloutUtilityHelper.logFaultError(res,UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHQUICKVIEW, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESS_QUICKVIEW_RESPONSE, '', '', UNIFIEDSEARCH_CONSTANTS_HUM.STRING_INFO);
                }
                else if(res.getStatusCode() > UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_HTTP_OK_HUM)
                {
                    Map < String, Object > responseErr = (Map < String, Object > ) JSON.deserializeUntyped(res.getBody());
					if(responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_MESSAGE) != null)
					{
                        String sType = ((String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TYPE) != null) ? (String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TYPE) : (String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_STATUSCODE);
						HUMExceptionHelper.logError((String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_MESSAGE), UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHQUICKVIEW, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESS_QUICKVIEW_RESPONSE, sType, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_INTEGRATIONERROR);
					}  
                }
                UnifiedSearchQuickView_C_HUM.DocumentHtml= System.Label.UnifiedSearch_ErrMsg_QuickView;
            }        	
        }
        catch(Exception e)
       {
           UnifiedSearchQuickView_C_HUM.DocumentHtml= System.Label.UnifiedSearch_ErrMsg_QuickView;
           HUMExceptionHelper.logErrors(e, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHQUICKVIEW, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESS_QUICKVIEW_RESPONSE);
       }
        return null;
	}
}