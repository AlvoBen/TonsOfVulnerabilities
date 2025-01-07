/****************************************************************************       //S2K 4
Apex Class Name  : MemberMbeService_S_HUM
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Controller to send amd recieve JSON request/response, parse the response and perform DML operation on objects.
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
* Pooja Wadhwa                              11/20/2015                    CCE Policy Logic addition
* Tanushree Hazari       21637              02/04/2016                    Logic change for CCE policy-CA ticket:5671774 
* Avinash Choubey                           02/25/2016                    Added processContinuationVobHistory and getWebInformationAndAccumsData for VOB Data Service
* Avinash Choubey        31077              04/22/2016                    Removing processContinuationVobHistory and getWebInformationAndAccumsData as part of accums Design Change.
* Praveen Kumar Parimi   95939              05/24/2017                    Including the condition N for updating the Has Vitality Flag as part of REQ - 318406 aka CA 6630956.
* Santhi Mandava         237088             05/31/2018                    Implemented member id platform encrption changes.
* Santhi Mandava                            04/15/2019                    Replaced "Member_Id_Value__c" with Name
* Prudhvi Pamarthi                          05/30/2019                    REQ-396330 Fix for CA Ticket - 8172941 -Go365 link does Not show the first time the policy tab Is opened
* Harish Gowda                              04/02/2020                    US - 903394 Added Humana.com link in MyHumana Details section on Policy Member page
* Ajay Chakradhar                           05/05/2021	                  REQ - 2184294 - Replacing constants with Consumer Management Constants
***************************************************************************************************/

global with sharing class MemberMbeService_S_HUM
{
    public static string sWebEmulateLink {get; set;}
    public static string sHumanaLink {get; set;}
    Public Static Boolean bVitalityChange;
    global MemberMbeService_S_HUM()
    {
        sWebEmulateLink = string.valueof(HUMConstants__c.getInstance('HUMWebEmulate').stringvalue__c);
        sHumanaLink = string.valueof(HUMConstants__c.getInstance('HUMMyHumana_Link').stringvalue__c);
    }
    Private Static String sHUM_VITALITY_ENTITY_ID = 'Vitality-Entity-ID';
    Private Static Integer sHUM_VOBONE = 1;
    /*
    * Method Name  : MemberMbeServiceHum ()
    * Description  : Method to call web service
    * return Type  : object
    * Parameters : String , String
    */
    @RemoteAction
    global static Object getWebInformation(string sMemAcctId, string sPolicyMemID)
    { 
        map<string, string> mapInput = new map<string, string>();
        ComponentValues oComponentValues = new ComponentValues();
        HUM_Webservice_Callout__c oSvcHeaders = new HUM_Webservice_Callout__c();
        try
        {
            if(WebActivityDetails_C_HUM.checkString(sMemAcctId) && WebActivityDetails_C_HUM.checkString(sPolicyMemID))
            {
                mapInput = WebActivityDetails_C_HUM.getJsonInput(sMemAcctId, sPolicyMemID);
                oComponentValues.accountID  = sMemAcctId;
                oComponentValues.PolicyID = sPolicyMemID;
                oComponentValues.VitalityFlagValue = mapInput.get(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM);
            }
            if(mapInput != NULL && mapInput.size() > 0)
            {
                if(mapInput.get(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM) == HUMMemWebActDetailsServiceConstants.ServiceStatusFull_HUM)
                {
                    HttpRequest request = new HttpRequest();
                    request = builtRequest(mapInput.get(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM) ,mapInput.get(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM) ,mapInput.get(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM) );
                   
                    oSvcHeaders = WebActivityDetails_C_HUM.buildServiceCallout();
                    if(oSvcHeaders!=null && oSvcHeaders.Active__c)
                    {
                        integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaders.Timeout__c)/1000;
                        Continuation continuous = new Continuation(iTIMEOUT_INT_SECS);
                        oComponentValues.continuationState = continuous.addHttpRequest(request);
                        continuous.state = oComponentValues ;
                        continuous.continuationMethod = HUMMemWebActDetailsServiceConstants.CONTINUATION_PROCESSRESPONSE_HUM;
                        return continuous;
                    }
                 } 
                else if(mapInput.get(HUMMemWebActDetailsServiceConstants.ServiceStatus_HUM) == HUMMemWebActDetailsServiceConstants.ServiceStatusNo_HUM )
                {
                    MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
                    oServiceMemberMbe = WebActivityDetails_C_HUM.getCacheVitalityInfo(sMemAcctId);
                    
                    if(oServiceMemberMbe != NULL)
                    return oServiceMemberMbe;
                    else  return NULL;
                }
                return NULL;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','getWebInformation');
            return NULL;
        }
        return NULL;
    }
    
    /*
    * Method Name  : builtRequest()
    * Description  : Method to create JSON request Input.
    * return Type  : HttpRequest
    * Parameters : String , String , String
    */
    public static HttpRequest builtRequest(string sVitalityFlag ,string sIDType,string sIDValue)
    {
        String JSONString;
        Map<String,object> mapEmp = new Map<String,object>();
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c oSvcHeaders = new HUM_Webservice_Callout__c();
        try
        {
            oSvcHeaders = WebActivityDetails_C_HUM.buildServiceCallout();
            
            request.setMethod(oSvcHeaders.Request_Type__c);
            if(String.isNotBlank(oSvcHeaders.Content_Type__c))
            {
                request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaders.Content_Type__c);
            }
            if(WebActivityDetails_C_HUM.checkString(sIDValue) && WebActivityDetails_C_HUM.checkString(sIDType) && WebActivityDetails_C_HUM.checkString(sVitalityFlag) )
            {
                if(sIDType == HUMMemWebActDetailsServiceConstants.ServicePersonIDInputValue_HUM )
                {
                    mapEmp.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM ,sIDType);
                    mapEmp.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM ,sIDValue);
                    mapEmp.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM ,sVitalityFlag );
                }
                else if(sIDType == HUMMemWebActDetailsServiceConstants.ServiceGenInputValue_HUM )
                {
                    mapEmp.put(HUMMemWebActDetailsServiceConstants.ServiceIDType_HUM ,sIDType);
                    mapEmp.put(HUMMemWebActDetailsServiceConstants.ServiceIDValue_HUM ,sIDValue);
                    mapEmp.put(HUMMemWebActDetailsServiceConstants.ServiceVitalityFlag_HUM ,sVitalityFlag );
                }
            }
            JSONString = JSON.serialize(mapEmp);
            
            if(WebActivityDetails_C_HUM.checkString(JSONString))
            {
                request.setBody(JSONString );
            }
            else return null;
            
            if(!Test.isRunningTest() && String.isNotBlank(oSvcHeaders.Certificate_Name__c))
            {
                request.setClientCertificateName(oSvcHeaders.Certificate_Name__c);
            }
            request.setEndpoint(oSvcHeaders.End_Point_URL__c);
            return request;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','builtRequest');
            return NULL;
        }
    }
    
    /*
    * Method Name  : processContinuation()
    * Description  : Method to get the JSON response.
    * return Type  : object
    * Parameters : Object
    */
    public static Object processContinuation(Object state)
    {
        ComponentValues oCompValues = (ComponentValues)state;
        try
        {
            if(state != null )
            {
                MemberMbe_H_HUM oMBEResponse = new MemberMbe_H_HUM();
                HttpResponse response = Continuation.getResponse(oCompValues.continuationState);
                
                if(response!=null && (response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM))
                {
                    oMBEResponse = parseResponse(response ,oCompValues,oMBEResponse );
                    return oMBEResponse ;
                }
            } else return NULL;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','processContinuation');
            return NULL;
        }
        return NULL ;
    }
    
    /*
    * Method Name  : parseResponse()
    * Description  : Method to parse service response.
    * return Type  : MemberMbe_H_HUM 
    * Parameters   : HttpResponse , ComponentValues ,MemberMbe_H_HUM 
    */
    @testVisible private static MemberMbe_H_HUM parseResponse(HttpResponse response,ComponentValues oCompValues,MemberMbe_H_HUM oMBEResponse)
    {
        Policy_Member__c oPolMem = new Policy_Member__c();
        List<Policy_Member__c> lstPolicyMember;
        if(string.isNotBlank(oCompValues.PolicyID))    lstPolicyMember    = [Select ID,Has_Vitality__c, Policy_Platform__c from Policy_Member__c where ID =: oCompValues.PolicyID];
        if(lstPolicyMember!=null && lstPolicyMember.Size()>0)    oPolMem =  lstPolicyMember[0];
        
        oMBEResponse = parseMbeResponseJSON(response);
        oMBEResponse.sVitalityFlagValue = oCompValues.VitalityFlagValue;
        Boolean CCEPolicy = IdentifyCCEPolicy(oMBEResponse,oCompValues.PolicyID,lstPolicyMember);
        UpsertcacheMember(oMBEResponse , oCompValues.accountID);
        if(String.isBlank(oPolMem.Has_Vitality__c)||oPolMem.Has_Vitality__c == 'N')
        {    
            UpdateHasVitality(oMBEResponse , oCompValues.PolicyID,CCEPolicy); 
            oMBEResponse.sVitalityUpdated = bVitalityChange;
        }
        return oMBEResponse;
    }
    
    /*
    * Method Name  : IdentifyCCEPolicy()
    * Description  : Method to Identify CCE Policies.
    * return Type  : boolean
    * Parameters : Object , string
    */
    public static Boolean IdentifyCCEPolicy(MemberMbe_H_HUM oMBEResponse, string sPolicyMemID, List<Policy_Member__c> lstPolicyMember)
    {
        Boolean CCEPolicy= false;
        if(oMBEResponse.VitalityEntityID == null || String.isblank(oMBEResponse.VitalityEntityID))
        {
            Policy_Member__c policyMemberObject = new Policy_Member__c();
             
            if(lstPolicyMember != null && lstPolicyMember.Size()>0)
            {
                policyMemberObject = lstPolicyMember[0];
            }
            if(policyMemberObject.Policy_Platform__c=='NI')
            {
                Member_ID__c memberIDObject = new Member_ID__c();
                List<Member_ID__c> lstMemberID;
                lstMemberID = [Select ID, Name ,Policy_Member__c,Type__c from Member_ID__c where Policy_Member__c =:sPolicyMemID and Type__c=: sHUM_VITALITY_ENTITY_ID LIMIT 1];
                if(lstMemberID != null && lstMemberID.Size() >0)
                {
                    memberIDObject = lstMemberID[0];                        
                    oMBEResponse.VitalityEntityID = memberIDObject.Name;
                    if(memberIDObject.Name != null && String.isNotBlank(memberIDObject.Name) && memberIDObject.Type__c == sHUM_VITALITY_ENTITY_ID) CCEPolicy= true;
                }
            }
        }
        return CCEPolicy;
    }
    
    
    /*
    * Method Name  : UpdateHasVitality()
    * Description  : Method to Update HasVitality field of Policy member object.
    * return Type  : null
    * Parameters : Object , string
    */
    public static void UpdateHasVitality(MemberMbe_H_HUM oServiceMemberMbe, string sPolicyMemID, boolean ccePolicy)
    {
        Policy_Member__c[] oPolicyMember ;
        bVitalityChange = false;
        try
        {
            if(WebActivityDetails_C_HUM.checkString(sPolicyMemID) )
            {
                oPolicyMember = [Select Has_Vitality__c, Policy__r.Product__r.Major_LOB__c, Effective_Date__c, End_Date__c, Policy__r.Product__r.ProductCode from Policy_Member__c where id=:sPolicyMemID];
                if(ccePolicy == True)
                {
                    oPolicyMember[0].Has_Vitality__c = oServiceMemberMbe.PolicyVitalityTrue ;
                    bVitalityChange = true;
                }
                
                else if(oServiceMemberMbe.ListPoliciesWithVitalityProg != NULL && oPolicyMember.size() > 0)
                {
                    for(MemberMBEPolicyList_H_HUM oMemberMBEPolicy : oServiceMemberMbe.ListPoliciesWithVitalityProg)
                    {
                        if( oMemberMBEPolicy.EffectiveDate == oPolicyMember[0].Effective_Date__c &&
                        oMemberMBEPolicy.EndDate == oPolicyMember[0].End_Date__c &&
                        oMemberMBEPolicy.productLineCd == oPolicyMember[0].Policy__r.Product__r.ProductCode &&
                        oMemberMBEPolicy.MLOB == oPolicyMember[0].Policy__r.Product__r.Major_LOB__c )
                        {
                            oPolicyMember[0].Has_Vitality__c = oServiceMemberMbe.PolicyVitalityTrue ;
                            bVitalityChange = true;
                            break;
                        }
                        else oPolicyMember[0].Has_Vitality__c = oServiceMemberMbe.PolicyVitalityFalse ;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','UpdateHasVitality');
        }
        
        if(oPolicyMember.size() > 0) update oPolicyMember ;
    }
    
    
    /*
    * Method Name  : UpsertcacheMember()
    * Description  : Method to Update HasVitality field of Policy member object.
    * return Type  : null
    * Parameters : Object , string
    */
    public static void UpsertcacheMember(MemberMbe_H_HUM oServiceMemberMbe, string sAccountID)
    {
        Datetime dtExpirationDateTime;
        Member_Cache__c[] arrMemberCache ;
        Member_Cache__c oMemberCache = new Member_Cache__c();
        try
        {
            dtExpirationDateTime= DateTime.newInstance(system.today(), time.newinstance(23,59,0,0));
            if(WebActivityDetails_C_HUM.checkString(sAccountID) )
            arrMemberCache = [select id,Web_Last_Login__c ,Cache_Expiration_Date_Vitality__c,Cache_Expiration_Date_Web_Last_Login__c,Cache_Expiration_Date_Is_Web_Registered__c , Cache_Expiration_Date_Member_Gen_Key__c from Member_Cache__c where AccountID__c =: sAccountID ];
            if( arrMemberCache.size() <= 0 )
            {
                if(oServiceMemberMbe != NULL )
                {
                    oMemberCache.Vitality_Entity_ID__c = oServiceMemberMbe.VitalityEntityID ;
                    oMemberCache.Cache_Expiration_Date_Vitality__c= system.now().adddays(sHUM_VOBONE);
                    oMemberCache.Member_Gen_Key__c = oServiceMemberMbe.MemberGenKey ;
                    oMemberCache.Cache_Expiration_Date_Member_Gen_Key__c = system.now().adddays(sHUM_VOBONE);
                    oMemberCache.Is_Web_Registered__c = oServiceMemberMbe.IsWebRegistered ;
                    oMemberCache.Cache_Expiration_Date_Is_Web_Registered__c = dtExpirationDateTime;
                    oMemberCache.Web_Last_Login__c = oServiceMemberMbe.LastLoginDateTime;
                    oMemberCache.Cache_Expiration_Date_Web_Last_Login__c= dtExpirationDateTime;
                    oMemberCache.AccountID__c = sAccountID;
                    upsert oMemberCache AccountID__c ;
                }
            }
            else if( arrMemberCache.size() > 0 )
            {
                if(arrMemberCache[0].Cache_Expiration_Date_Vitality__c > system.now() && arrMemberCache[0].Cache_Expiration_Date_Member_Gen_Key__c > system.now())
                {
                    if(arrMemberCache[0].Cache_Expiration_Date_Web_Last_Login__c < system.now() || arrMemberCache[0].Cache_Expiration_Date_Vitality__c < system.now())
                    {
                        arrMemberCache[0].Is_Web_Registered__c = oServiceMemberMbe.IsWebRegistered ;
                        arrMemberCache[0].Cache_Expiration_Date_Is_Web_Registered__c = dtExpirationDateTime;
                        arrMemberCache[0].Web_Last_Login__c = oServiceMemberMbe.LastLoginDateTime;
                        arrMemberCache[0].Cache_Expiration_Date_Web_Last_Login__c= dtExpirationDateTime;
                        arrMemberCache[0].Member_Gen_Key__c = oServiceMemberMbe.MemberGenKey ;
                        arrMemberCache[0].Cache_Expiration_Date_Member_Gen_Key__c = system.now().adddays(sHUM_VOBONE);
                        arrMemberCache[0].AccountID__c = sAccountID;
                        upsert arrMemberCache[0] AccountID__c;
                    }
                }
                else
                {
                    if(oServiceMemberMbe.sVitalityFlagValue == HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueYes_HUM )
                    {
                        arrMemberCache[0].Vitality_Entity_ID__c = oServiceMemberMbe.VitalityEntityID ;
                        arrMemberCache[0].Cache_Expiration_Date_Vitality__c= system.now().adddays(sHUM_VOBONE);
                        arrMemberCache[0].Member_Gen_Key__c = oServiceMemberMbe.MemberGenKey ;
                        arrMemberCache[0].Cache_Expiration_Date_Member_Gen_Key__c = system.now().adddays(sHUM_VOBONE);
                        arrMemberCache[0].Is_Web_Registered__c = oServiceMemberMbe.IsWebRegistered;
                        arrMemberCache[0].Cache_Expiration_Date_Is_Web_Registered__c = dtExpirationDateTime;
                        arrMemberCache[0].Web_Last_Login__c = oServiceMemberMbe.LastLoginDateTime;
                        arrMemberCache[0].Cache_Expiration_Date_Web_Last_Login__c= dtExpirationDateTime;
                        arrMemberCache[0].AccountID__c = sAccountID;
                        upsert arrMemberCache[0] AccountID__c ;
                    }
                    else if(oServiceMemberMbe.sVitalityFlagValue == HUMMemWebActDetailsServiceConstants.ServiceVitalityFlagValueNo_HUM )
                    {
                        arrMemberCache[0].Member_Gen_Key__c = oServiceMemberMbe.MemberGenKey ;
                        arrMemberCache[0].Cache_Expiration_Date_Member_Gen_Key__c = system.now().adddays(sHUM_VOBONE);
                        arrMemberCache[0].Is_Web_Registered__c = oServiceMemberMbe.IsWebRegistered;
                        arrMemberCache[0].Cache_Expiration_Date_Is_Web_Registered__c = dtExpirationDateTime;
                        arrMemberCache[0].Web_Last_Login__c = oServiceMemberMbe.LastLoginDateTime;
                        arrMemberCache[0].Cache_Expiration_Date_Web_Last_Login__c= dtExpirationDateTime;
                        arrMemberCache[0].AccountID__c = sAccountID;
                        upsert arrMemberCache[0] AccountID__c ;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','UpsertcacheMember');
        }
    }
    
    
    /*
    * Method Name  : parseMbeResponseJSON()
    * Description  : Method to parse the JSON response.
    * return Type  : object
    * Parameters : HttpResponse
    */
    public static MemberMbe_H_HUM parseMbeResponseJSON(HttpResponse response)
    {
        String sLastLoginDate;
        String[] arrLastLoginDate;
        MemberMbe_H_HUM oParsedMemberMbe = new MemberMbe_H_HUM();
        try
        {
            oParsedMemberMbe = (MemberMbe_H_HUM )System.JSON.deserialize(response.getbody(), MemberMbe_H_HUM.class);
            return oParsedMemberMbe ;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','parseMbeResponseJSON');
            return NULL;
        }
    }      
}