/****************************************************************************************************
Apex Class Name  : UnifiedSearch_LC_HUM
Version          : 1.0
Created Date     : 12/02/2020
Function         : controller of UnifiedSearch_LWC_HUM LWC Component
Test class       : UnifiedSearch_LT_HUM
*****************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  Akshay Pai                                   12/02/2020                 original Version
*****************************************************************************************************/
public with sharing class UnifiedSearch_LC_HUM 
{
    private static HUM_Webservice_Callout__c oSetting = HUM_Webservice_Callout__c.getValues(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_SERVICE); 
    private static User objUser = [SELECT Id, Network_User_Id__c,Email FROM User where Id=:UserInfo.getUserId()];
    private static String oTokenDecryptionSwitch = (CRMFunctionality_ONOFF_Switch__c.getValues(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_DECRYPT).IsON__c) ? UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TRUE : UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FALSE;
   /*
    * MethodName : generateToken
    * This is the Service call to generate token.                                
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object generateToken()
    {
        try
        { 
            String retToken='';
            boolean bIsTokenAvailable = false;            

            if (Cache.Session.contains(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_COVEOTOKEN)) 
            {                  
                retToken = (String) Cache.Session.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_COVEOTOKEN);
                if(retToken != null && String.isNotBlank(retToken) && retToken != UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_ERROR){
                    bIsTokenAvailable = true; 
                    Map<String, String> retMap = new Map<String, String> ();                
                	retMap.put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TOKEN, retToken);
                	retMap.put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_DECRYPTTOKEN,oTokenDecryptionSwitch); 
                    return retMap;
                }                
            }            
            else if(!bIsTokenAvailable && oSetting != NULL && objUser.Network_User_Id__c != null)
            {                
                Continuation con = new Continuation(120);
                con.continuationMethod = UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSGETTOKENRESPONSE;
                HttpRequest coveoRequest = new HttpRequest();
                coveoRequest.setMethod(oSetting.Request_Type__c);
                coveoRequest.setEndpoint( oSetting.End_Point_URL__c + oSetting.Service_Name__c); 
                if(!test.isRunningTest() && String.isNotBlank(osetting.Certificate_Name__c) )
                {
                    coveoRequest.setClientCertificateName(oSetting.Certificate_Name__c);
                }
                coveoRequest.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CONTENTTYPE, oSetting.Content_Type__c);
                coveoRequest.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TSOID, objUser.Network_User_Id__c);
                coveoRequest.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_EMAIL, objUser.Email);
                coveoRequest.setBody(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_EMPTYBODY);
                con.addHttpRequest(coveoRequest);
                return con;                              
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_GENERATETOKEN);            
        }
        return null;
    }
    
   /*
    * processGetTokenResponse()
    * <p>
    * This is the call back method from Continuation to process the response of generateToken callout.
    * @param  labels    List of String                                
    * @return Object
    */
    @AuraEnabled
    public static Object processGetTokenResponse(List < String > labels) 
    {
        Map<String, String> retMap = new Map<String, String> (); 
        try
        {
            HttpResponse coveoResponse = Continuation.getResponse(labels[0]);
            if (coveoResponse != null && ((coveoResponse.getStatusCode() == UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_HTTP_OK_HUM || coveoResponse.getStatusCode() == UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_HTTP_ACCEPTED_HUM) && (!coveoResponse.getBody().containsIgnoreCase(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FAULT_CODE) && (String)coveoResponse.getBody() != UNIFIEDSEARCH_CONSTANTS_HUM.STRING_NO_REQUEST_HEADER_FOUND) ) )    
            {
                Map < String, Object > response = (Map < String, Object > ) JSON.deserializeUntyped(coveoResponse.getBody());
                if(response.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TOKEN) != null)
                {
                    Cache.Session.Put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_COVEOTOKEN,(string) response.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TOKEN));
                    retMap.put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TOKEN,(String) response.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TOKEN));
                    retMap.put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_DECRYPTTOKEN,oTokenDecryptionSwitch);                    
                }
            }
            else 
            {   
                retMap.put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TOKEN, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_ERROR);
                Cache.Session.Put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_COVEOTOKEN, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_ERROR);
                if(coveoResponse.getBody().containsIgnoreCase(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FAULT_CODE))
                {
                    HUMCalloutUtilityHelper.logFaultError(coveoResponse,UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSGETTOKENRESPONSE, '', '', UNIFIEDSEARCH_CONSTANTS_HUM.STRING_INFO);
                }
                else if(coveoResponse.getStatusCode() > 200)
                {
                    Map < String, Object > responseErr = (Map < String, Object > ) JSON.deserializeUntyped(coveoResponse.getBody());
                    if(responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_MESSAGE) != null)
                    {
                        String sType = ((String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TYPE) != null) ? (String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TYPE) : (String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_STATUSCODE);
                        HUMExceptionHelper.logError((String)responseErr.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_MESSAGE), UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSGETTOKENRESPONSE, sType, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_INTEGRATIONERROR);
                    }                   
                }
                else
                {
                    HUMExceptionHelper.logError(coveoResponse.getBody(), UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSGETTOKENRESPONSE, String.valueOf(coveoResponse.getStatusCode()), UNIFIEDSEARCH_CONSTANTS_HUM.STRING_INTEGRATIONERROR);
                }
            }
        }
        catch(Exception e)
        {
            Cache.Session.Put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_COVEOTOKEN, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_ERROR);
            retMap.put(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TOKEN, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_ERROR);
            HUMExceptionHelper.logErrors(e, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSGETTOKENRESPONSE);            
        }
        return retMap;
    }
    
   /*
    * searchQuery()
    * <p>
    * This method is called on each action performed on the Coveo window like search, sort, pagination, Select an article to view etc.
    * @param  NA                                
    * @return Object
    */
    @AuraEnabled (continuation=true)
    public static Object searchQuery(String params)
    {
        try
        {
            HUMConstants__c oHUMCoveoOrgId = HUMConstants__c.getValues(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCH_ORG);
            Map<String, Object> parsedParams = (Map<String, Object>)JSON.deserializeUntyped(params);
            Object requestData = parsedParams.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_REQUESTDATA);           
            Continuation SearchCon = new Continuation (120);           
            SearchCon.continuationMethod = UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSSEARCHQUERYRESPONSE;
            HttpRequest req2 = new HttpRequest ();            
            req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_AUTHORIZATION, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_BEARER+ parsedParams.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_ACCESSTOKEN));
            if(!test.isRunningTest() && String.isNotBlank(osetting.Certificate_Name__c) )
            {                
                req2.setClientCertificateName(oSetting.Certificate_Name__c);
            }     
            req2.setEndpoint(((string)parsedParams.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_URL)).replace(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FAKEURI, oSetting.End_Point_URL__c));
            req2.setMethod(oSetting.Request_Type__c);
            req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CONTENTTYPE, oSetting.Content_Type__c); 
            req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_ORGANIZATIONID,oHUMCoveoOrgId.StringValue__c);
            if(((string)parsedParams.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_URL)).contains(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_ANALYTICS))
            {
            	req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PRIORITIZEVISITORPARAM,UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TRUE);
                req2.setHeader(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_VISITOR,objUser.Network_User_Id__c);
            }
            req2.setBody(JSON.serialize(requestData));
            SearchCon.addHttpRequest(req2);
            return SearchCon;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_SEARCHQUERY);
            return null;
        }
    }
    
   /*
    * processSearchQueryResponse()
    * <p>
    * This is the call back method from Continuation to process the response of searchQuery callout.
    * @param  labels    List of String                                
    * @return Object
    */
    @AuraEnabled
    public Static Object processSearchQueryResponse(List<String> labels) 
    { 
        try
        {
            Object response;
            HttpResponse res2 = Continuation.getResponse(labels[0]);
            if (res2.getStatusCode() >= UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_HTTP_OK_HUM && res2.getStatusCode() < UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_HTTP_STATUS_CODE_300 && !res2.getBody().containsIgnoreCase(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FAULT_CODE))
            {
                response =  JSON.serialize(new Map < String, Object > {
                        UNIFIEDSEARCH_CONSTANTS_HUM.STRING_DURATION => UNIFIEDSEARCH_CONSTANTS_HUM.INTEGER_UNIFIEDSEARCHDURATION,
                        UNIFIEDSEARCH_CONSTANTS_HUM.STRING_DATA => JSON.deserializeUntyped(res2.getBody())
                        });
            }
            else 
            {               
                if(res2.getBody().containsIgnoreCase(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_FAULT_CODE))
                {
                    HUMCalloutUtilityHelper.logFaultError(res2,UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSSEARCHQUERYRESPONSE, '', '', UNIFIEDSEARCH_CONSTANTS_HUM.STRING_INFO);
                }
                else
                {
                    Map <String, Object> res2Err = (Map<String, Object> ) JSON.deserializeUntyped(res2.getBody());
                    if((String)res2Err.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_MESSAGE) != null)
                    {
                        HUMExceptionHelper.logError((String)res2Err.get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_MESSAGE), UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSSEARCHQUERYRESPONSE, String.valueOf(res2.getStatusCode()), UNIFIEDSEARCH_CONSTANTS_HUM.STRING_INTEGRATIONERROR);
                    }
                }
                response =  JSON.serialize(new Map < String, Object > {
                        UNIFIEDSEARCH_CONSTANTS_HUM.STRING_STATUSCODE => res2.getStatusCode(),
                        UNIFIEDSEARCH_CONSTANTS_HUM.STRING_DATA => JSON.deserializeUntyped(res2.getBody())
                        });
            }            
            return response;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CLASSNAME, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_PROCESSSEARCHQUERYRESPONSE);
            return null;
        }  
    }    
}