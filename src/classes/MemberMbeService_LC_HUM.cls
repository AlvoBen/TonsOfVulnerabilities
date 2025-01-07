/****************************************************************************
Apex Class Name  : MemberMbeService_LC_HUM
Version          : 1.0  
Created Date     :05/24/2021
Function         : Controller to send amd recieve JSON request/response, parse the response and perform DML operation on objects.
Test Class       : MemberMbeService_LT_HUM
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                               05/24/2021                   Original Version
*Deepak khandelwal                            06/16/2023                    US_4525570_T1PRJ0865978 - MF25836 - C08, Consumer/CRM Lightning to pass full gen key number needed for web emulation (Lightning)
***************************************************************************************************/
    global with sharing class MemberMbeService_LC_HUM {
        Public Static Boolean bVitalityChange;
        
        global MemberMbeService_LC_HUM(){}
        
        Private Static String sHUM_VITALITY_ENTITY_ID = 'Vitality-Entity-ID';
        Private Static Integer sHUM_VOBONE = 1;
        /*
    * Method Name  : MemberMbeServiceHum ()
    * Description  : Method to call web service
    * return Type  : object
    * Parameters : String , String
    */
        @AuraEnabled(Continuation=true)
        global static Object getWebInformation(string sPolicyMemID)
        { 
            map<string, string> mapInput = new map<string, string>();
            ComponentValues oComponentValues = new ComponentValues();
            HUM_Webservice_Callout__c oSvcHeaders = new HUM_Webservice_Callout__c();
            try
            {
                String sMemAcctId = MemberMbeService_LD_HUM.fetchAccountId(sPolicyMemID);
                if(WebActivityDetails_LC_HUM.checkString(sMemAcctId) && WebActivityDetails_LC_HUM.checkString(sPolicyMemID))
                {
                    mapInput = WebActivityDetails_LC_HUM.getJsonInput(sMemAcctId, sPolicyMemID);
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
                        
                        oSvcHeaders = WebActivityDetails_LC_HUM.buildServiceCallout();
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
                        oServiceMemberMbe = WebActivityDetails_LC_HUM.getCacheVitalityInfo(sMemAcctId);
                        oServiceMemberMbe.humanaLink =  string.valueof(HUMConstants__c.getInstance('HUMMyHumana_Link').stringvalue__c);
                        oServiceMemberMbe.webEmulateLink =  string.valueof(HUMConstants__c.getInstance('HUMWebEmulate').stringvalue__c);
                        
                        if(oServiceMemberMbe != NULL){
                            return oServiceMemberMbe;
                        }
                        else{
                            return sendDefaultLinks();
                        }
                    }
                    return sendDefaultLinks();
                }
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','getWebInformation');
                return sendDefaultLinks();
            }
           return sendDefaultLinks();
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
                oSvcHeaders = WebActivityDetails_LC_HUM.buildServiceCallout();
                
                request.setMethod(oSvcHeaders.Request_Type__c);
                if(String.isNotBlank(oSvcHeaders.Content_Type__c))
                {
                    request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaders.Content_Type__c);
                }
                if(WebActivityDetails_LC_HUM.checkString(sIDValue) && WebActivityDetails_LC_HUM.checkString(sIDType) && WebActivityDetails_LC_HUM.checkString(sVitalityFlag) )
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
                
                if(WebActivityDetails_LC_HUM.checkString(JSONString))
                {
                    request.setBody(JSONString );
                }
                else{
                    return null;
                }
                
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
        @AuraEnabled
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
                        oMBEResponse.humanaLink =  string.valueof(HUMConstants__c.getInstance('HUMMyHumana_Link').stringvalue__c);
                        oMBEResponse.webEmulateLink =  string.valueof(HUMConstants__c.getInstance('HUMWebEmulate').stringvalue__c);
						oMBEResponse.MemberGenKey = new WebActivityDetails_H_HUM().getMemberGenKey(oCompValues.accountID);
                        return oMBEResponse ;
                    }
                } else{
                    return sendDefaultLinks();
                }
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','processContinuation');
                return sendDefaultLinks();
            }
            return sendDefaultLinks() ;
        }
        
        /*
    * Method Name  : parseResponse()
    * Description  : Method to parse service response.
    * return Type  : MemberMbe_H_HUM 
    * Parameters   : HttpResponse , ComponentValues ,MemberMbe_H_HUM 
    */
        @testVisible private static MemberMbe_H_HUM parseResponse(HttpResponse response,ComponentValues oCompValues,MemberMbe_H_HUM oMBEResponse)
        {
            MemberPlan oPolMem = new MemberPlan();
            List<MemberPlan> lstPolicyMember;
            if(string.isNotBlank(oCompValues.PolicyID)){   
                lstPolicyMember = MemberMbeService_LD_HUM.fetchPolicy(oCompValues.PolicyID);
            }
            if(lstPolicyMember!=null && lstPolicyMember.Size()>0) {
                oPolMem =  lstPolicyMember[0];
            }   
            
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
        public static Boolean IdentifyCCEPolicy(MemberMbe_H_HUM oMBEResponse, string sPolicyMemID, List<MemberPlan> lstPolicyMember)
        {
            Boolean CCEPolicy= false;
            if(oMBEResponse.VitalityEntityID == null || String.isblank(oMBEResponse.VitalityEntityID))
            {
                MemberPlan policyMemberObject = new MemberPlan();
                
                if(lstPolicyMember != null && lstPolicyMember.Size()>0)
                {
                    policyMemberObject = lstPolicyMember[0];
                }
                if(policyMemberObject.Policy_Platform__c=='NI')
                {
                    MemberPlan memberIDObject = new MemberPlan();
                    List<MemberPlan> lstMemberID;
                    lstMemberID = MemberMbeService_LD_HUM.fetchPolicy(sPolicyMemID);
                    if(lstMemberID != null && lstMemberID.Size() >0)
                    {
                        memberIDObject = lstMemberID[0];                        
                        oMBEResponse.VitalityEntityID = memberIDObject.Name;
                        if(memberIDObject.Name != null && String.isNotBlank(memberIDObject.Name))
                        {
                            CCEPolicy= true;
                        }
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
            MemberPlan[] oPolicyMember ;
            bVitalityChange = false;
            try
            {
                if(WebActivityDetails_LC_HUM.checkString(sPolicyMemID) )
                {
                    oPolicyMember = MemberMbeService_LD_HUM.fetchPolicy(sPolicyMemID);
                    if(ccePolicy == True)
                    {
                        oPolicyMember[0].Has_Vitality__c = oServiceMemberMbe.PolicyVitalityTrue ;
                        bVitalityChange = true;
                    }
                    
                    else if(oServiceMemberMbe.ListPoliciesWithVitalityProg != NULL && oPolicyMember.size() > 0)
                    {
                        for(MemberMBEPolicyList_H_HUM oMemberMBEPolicy : oServiceMemberMbe.ListPoliciesWithVitalityProg)
                        {
                            if( oMemberMBEPolicy.EffectiveDate == String.valueOf(oPolicyMember[0].EffectiveFrom) &&
                               oMemberMBEPolicy.EndDate == String.valueOf(oPolicyMember[0].EffectiveTo) &&
                               oMemberMBEPolicy.productLineCd == oPolicyMember[0].Plan.Product__r.ProductCode &&
                               oMemberMBEPolicy.MLOB == oPolicyMember[0].Plan.Product__r.Major_LOB__c )
                            {
                                oPolicyMember[0].Has_Vitality__c = oServiceMemberMbe.PolicyVitalityTrue ;
                                bVitalityChange = true;
                                break;
                            }
                            else oPolicyMember[0].Has_Vitality__c = oServiceMemberMbe.PolicyVitalityFalse ;
                        }
                    }
                }
                if(oPolicyMember.size() > 0){
                    UserAssociatedInformationPassword_LD_HUM.UpdateHasVitality(oPolicyMember);
                } 
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','UpdateHasVitality');
            }
            
            
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
                if(WebActivityDetails_LC_HUM.checkString(sAccountID) )
                    arrMemberCache = MemberMbeService_LD_HUM.fetchMemberCache(sAccountID);
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
                return sendDefaultLinks();
            }
        } 
        
         /*
    * Method Name  : sendDefaultLinks()
    * Description  : Method to return default links.
    * return Type  : MemberMbe_H_HUM
    * Parameters : NA
    */
        public static MemberMbe_H_HUM sendDefaultLinks(){
             MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
             oServiceMemberMbe.humanaLink =  string.valueof(HUMConstants__c.getInstance('HUMMyHumana_Link').stringvalue__c);
             oServiceMemberMbe.webEmulateLink =  string.valueof(HUMConstants__c.getInstance('HUMWebEmulate').stringvalue__c);
             return oServiceMemberMbe;
        }
        
    }