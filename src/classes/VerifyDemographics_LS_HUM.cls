/************************************************************************************************
Apex Class Name  : VerifyDemographics_LS_HUM 
Version          : 1.0 
Created Date     : 02/23/2021
Function         : Apex Generated Proxy class for PersonService WSDL
Test Class       : VerifyDemographics_LT_HUM
Modification Log:
Version Number   				Developer Name           		Code Review                   Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------------
* 1.0						Ankima Srivastava										 	02/23/2021				 initial version
* 2.0						Kajal Namdev                                             	05/07/2021               Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class
* 3.0 						Nilanjana Sanyal                      					 	10/26/2022               US-3813080: HUMExceptionHelper remediation for lightning consumer Management classes            US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
* 4.0                       Vishal Shinde                                               07/28/2023               US-4833019-Mail Order Management - Pharmacy - “Accounts” tab – Verify Demographics - Lightning
************************************************************************************************************************************************** */

Public with sharing Class VerifyDemographics_LS_HUM {
    public static String AccountId;
    public static String sBody ='' ;
    private static String sResult = '';
    public static String sRequestLabelPID='';
    public static string sUpdateRequest='';
    public static List<HUMVerifyFamilyDemoWrapperHelper> lstDispAccounts;
    public static List<HUMVerifyDemoGraphicsWrapperHelper> lstVerifyDemoGraphics;
    public static List<VerifyDemographicGetWrapper_DTO_HUM> lstVerifyDemoWrapper;
    private static List<HUMVerifyFamilyDemoWrapperHelper> lstSelectedMembers = new List<HUMVerifyFamilyDemoWrapperHelper>();
    private static Account oAccount = new Account();
    public static boolean bVerifyMessage;
    public static HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture oHUMFutureResponse;
    public static HUMVerifyFamilyCOBService.OIDemographicsOutputDTO oHUMDemographicsOutputDTO = new HUMVerifyFamilyCOBService.OIDemographicsOutputDTO();
    public static HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture oHUMDemographicFutureResponse;
    public static  Map<String,Object> mpOfCallback = new Map<String,Object>();
    public static String contstate1;
    public static HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture contstate2;
    public static String contstate3;
    public static Map<String,Object> contstate4;
    
     @AuraEnabled(continuation=true )
    public static Continuation getLastVerifyDatesRest(String recId)
    { 
      
      AccountId = recId;
      Continuation conGo = new Continuation(GLOBAL_CONSTANT_LH_HUM.cont);
     
        if(string.isNotBlank(AccountId))
        {
            HUM_Webservice_Callout__c oGVDService = HUM_Webservice_Callout__c.getValues('GetVerifyDemographics');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oGVDService.Timeout__c)/1000;
            try
            {  
                sBody = buildRequestGetVerifyDemographics(AccountId);
                if(string.isNotBlank(sBody))
                {
                    HttpRequest request = new HttpRequest();
                    request.setMethod(oGVDService.Request_Type__c); 
                    request.setHeader(GLOBAL_CONSTANT_LH_HUM.CONTENT_TYPE_HUM, oGVDService.Content_Type__c);  
                    request.setHeader(GLOBAL_CONSTANT_LH_HUM.SOAPACTION_HUM, '');    
                    request.setHeader(GLOBAL_CONSTANT_LH_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_LH_HUM.KEEP_ALIVE_HUM);
                    request.setBody(sBody);
                    if(!Test.isRunningTest())    request.setClientCertificateName(oGVDService.Certificate_Name__c);    
                    request.setTimeout(Integer.valueof(oGVDService.Timeout__c));
                    request.setEndpoint(oGVDService.End_Point_URL__c);
                    conGo.continuationMethod = 'processGetVerifyDemographicsResponse';
                    contstate1 = AccountId;
                    conGo.State = contstate1;
                    sRequestLabelPID = conGo.addHttpRequest(request);
                    //if(Test.isRunningTest())  throw new HUMCustomException('Error');
                }
            }
            Catch(exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'getLastVerifyDatesRest');
                return null;
            }
        }
        return conGo;
    }
    
    /*
    *  This method is used to build the required inputs needed for VerifyDemographics service
    *  @name   buildRequestGetVerifyDemographics
    *  @param  string sRecordId -  Account to get data from service
    *  @return string - Returns the Request body in Serialized form
    */
    @testvisible private static string buildRequestGetVerifyDemographics(string sRecordId)
    {   
        string sJsonBodyGo = '';
        try
        {
            if(string.isNotBlank(sRecordId))
            { 
                Account objAccount = [Select id,Enterprise_ID__c from Account where id =:sRecordId LIMIT 1];
                VerifyDemographicGetRequest_DTO_HUM oRequest = new VerifyDemographicGetRequest_DTO_HUM();
                VerifyDemographicGetRequest_DTO_HUM.GetverificationstatusRequest objGetVerificationStatus =  
                    new VerifyDemographicGetRequest_DTO_HUM.GetverificationstatusRequest();
                    objGetVerificationStatus.PersonId = (objAccount.Enterprise_ID__c != Null) ? objAccount.Enterprise_ID__c : '';
                VerifyDemographicGetRequest_DTO_HUM.RequestHeader objGetRequestHeader = 
                    new VerifyDemographicGetRequest_DTO_HUM.RequestHeader();
                    objGetRequestHeader.RequestID = String.valueOf((Math.random() * 100));
                    objGetRequestHeader.SessionID = UserInfo.getSessionId();
                oRequest.GetverificationstatusRequest = objGetVerificationStatus;
                oRequest.RequestHeader = objGetRequestHeader;
                sJsonBodyGo = JSON.serialize(oRequest);
            }
            else    sJsonBodyGo =  null;
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'buildRequestGetVerifyDemographics');
            sJsonBodyGo  =  NULL;
        }
        return sJsonBodyGo ;
    }
    
    /*
    *  This method is used to Parse response from getVerifyDemographics service
    *  @name   processGetVerifyDemographicsResponse
    *  @param  continuation label and state
    *  @return void
    */
     @AuraEnabled( continuation=true ) 
    public static list<VerifyDemographicGetWrapper_DTO_HUM> processGetVerifyDemographicsResponse(List < string > labels, Object State)  
    { 
        List<VerifyDemographicGetWrapper_DTO_HUM> lstVerifyDemographic = new List<VerifyDemographicGetWrapper_DTO_HUM >();
        try
        {  
            HttpResponse sResponse = Continuation.getResponse(labels[0]);
            if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.VOB_FAULT_CODE) && 
                (sResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)) || Test.isRunningTest())
            {
                VerifyDemographicGetResponse_DTO_HUM objResponse = new VerifyDemographicGetResponse_DTO_HUM();
                objResponse = (VerifyDemographicGetResponse_DTO_HUM)System.JSON.deserialize(sResponse.getBody(),VerifyDemographicGetResponse_DTO_HUM.class); 
                if (objResponse != null){ 
                    lstVerifyDemographic = processResponse(objResponse);
                } 
            }
            if(sResponse != null && sResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.VOB_FAULT_CODE)){
                return processDefaultVerifyResponse(); 
            }  else{
                return lstVerifyDemographic;    
            }    
        }        
        catch(Exception ex) {
		    HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'processGetVerifyDemographicsResponse');
            return null;
        }
    }
    
     /**
    * This method generates list of VerifyDemographic details.
    * @Nmae     processResponse
    * @param    VerifyDemographicResponse_DTO_HUM response
    * @return   List of VerifyDemographicWrapper_DTO_HUM
    */
    public static list<VerifyDemographicGetWrapper_DTO_HUM> processResponse(VerifyDemographicGetResponse_DTO_HUM resVerifyDemographic)
    {
        try
        {
          lstVerifyDemoWrapper = new list<VerifyDemographicGetWrapper_DTO_HUM>();
            VerifyDemographicGetWrapper_DTO_HUM objVerifyDemoWrapper =new VerifyDemographicGetWrapper_DTO_HUM();
            VerifyDemographicGetResponse_DTO_HUM.GetverificationstatusResponse objgetverification = 
                resVerifyDemographic.GetverificationstatusResponse;
            objVerifyDemoWrapper.iDaysSinceLastVerified = (NULL != objgetverification.demographics.lastVerifiedDate) ? Date.valueOf(objgetverification.demographics.lastVerifiedDate).daysBetween(system.Today()) : -1;
            if(objgetverification.demographics.verifiedBy != Null){
                List<User> lstUser = [Select Name,Network_User_Id__c from User where ( Network_User_Id__c  =: objgetverification.demographics.verifiedBy OR Name =: objgetverification.demographics.verifiedBy) Limit 1];    
                objVerifyDemoWrapper.sLastVerifiedBy = '';
                if(lstUser != null && !lstUser.isEmpty()){
                    objVerifyDemoWrapper.sLastVerifiedBy = (NULL != lstUser[0].Name) ? lstUser[0].Name : lstUser[0].Network_User_Id__c; 
                    objVerifyDemoWrapper.networkUserId = lstUser[0].Network_User_Id__c != null ? lstUser[0].Network_User_Id__c :'';   
                }
                if(lstUser.isEmpty()){
                    objVerifyDemoWrapper.sLastVerifiedBy = objgetverification.demographics.verifiedBy;   
                    objVerifyDemoWrapper.networkUserId = ''; 
                }
            }
            if(objgetverification.demographics.lastVerifiedDate != Null){
                objVerifyDemoWrapper.sLastVerifiedOn = '';
                List <String> splitDateTime = objgetverification.demographics.lastVerifiedDate.split('T');
                List <String> splitDates = splitDateTime[0].split('-');
                List <String> splitTime = splitDateTime[1].split(':');
                
                DateTime vDateTime = DateTime.newInstance(Integer.valueOf(splitDates[0]), Integer.valueOf(splitDates[1]), Integer.valueOf(splitDates[2]), Integer.valueOf(splitTime[0]), 0, 0);
                String formattedDateTime = vDateTime.format('EEE MMM dd hh:mm:ss');
                objVerifyDemoWrapper.sLastVerifiedOn = formattedDateTime +' GMT '+Integer.valueOf(splitDates[0]); 
            }
            bVerifyMessage = (objVerifyDemoWrapper.iDaysSinceLastVerified > 90) ? true : false;
            lstVerifyDemoWrapper.add(objVerifyDemoWrapper);
            return lstVerifyDemoWrapper;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'processResponse');
            return null;
        }
    }
    
     /**
    * This method generates list of VerifyDemographic with default details.
    * @Name     processDefaultVerifyResponse
    * @param    VerifyDemographicResponse_DTO_HUM response
    * @return   List of VerifyDemographicWrapper_DTO_HUM
    */
    public static list<VerifyDemographicGetWrapper_DTO_HUM> processDefaultVerifyResponse()
    {
         lstVerifyDemoWrapper = new list<VerifyDemographicGetWrapper_DTO_HUM>();
         VerifyDemographicGetWrapper_DTO_HUM objVerifyDemoWrapper =new VerifyDemographicGetWrapper_DTO_HUM();
            objVerifyDemoWrapper.sLastVerifiedBy = '';
            String defaultGMT = '1-01-01T00:00:00.000Z';
            if(defaultGMT != Null){ 
                List <String> splitDateTime = defaultGMT.split('T');
                List <String> splitDates = splitDateTime[0].split('-');
                List <String> splitTime = splitDateTime[1].split(':');
                
                DateTime vDateTime = DateTime.newInstance(Integer.valueOf(splitDates[0]), Integer.valueOf(splitDates[1]), Integer.valueOf(splitDates[2]), Integer.valueOf(splitTime[0]), 0, 0);
                String formattedDateTime = vDateTime.format('EEE MMM dd HH:mm:ss');
                objVerifyDemoWrapper.sLastVerifiedOn = formattedDateTime +' GMT '+Integer.valueOf(splitDates[0]); 
            }
            objVerifyDemoWrapper.iDaysSinceLastVerified = (NULL != defaultGMT) ? Date.valueOf(defaultGMT).daysBetween(system.Today()) : -1;
            bVerifyMessage = (objVerifyDemoWrapper.iDaysSinceLastVerified > 90) ? true : false;
            lstVerifyDemoWrapper.add(objVerifyDemoWrapper);
            return lstVerifyDemoWrapper;
      
    }
    
    //verifyDemographicsRest
     @AuraEnabled (continuation = true)
    public static Continuation verifyDemographicsRest(String recordId)
    {   
        AccountId = recordId;
        HUM_Webservice_Callout__c oUVDService = HUM_Webservice_Callout__c.getValues('UpdateVerifyDemographics');
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oUVDService.Timeout__c)/1000;
        Continuation conGo = new Continuation(iTIMEOUT_INT_SECS);
        if(string.isNotBlank(AccountId))
        {
                
                sBody = buildRequestUpdateVerifyDemographics(AccountId);
                if(string.isNotBlank(sBody))
                {
                    HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
                    HttpRequest request = new HttpRequest();
                    request.setMethod(oUVDService.Request_Type__c); 
                    request.setHeader(GLOBAL_CONSTANT_LH_HUM.CONTENT_TYPE_HUM, oUVDService.Content_Type__c);  
                    request.setHeader(GLOBAL_CONSTANT_LH_HUM.SOAPACTION_HUM, '');    
                    request.setHeader(GLOBAL_CONSTANT_LH_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_LH_HUM.KEEP_ALIVE_HUM);
                    request.setBody(sBody);
                    if(!Test.isRunningTest())    request.setClientCertificateName(oUVDService.Certificate_Name__c);    
                    request.setTimeout(Integer.valueof(oUVDService.Timeout__c));
                    request.setEndpoint(oUVDService.End_Point_URL__c);
                    conGo.continuationMethod = 'processUpdateVerifyDemographicsResponse';
                    contstate3 = AccountId;
                    conGo.state = contstate3;
                    conGo.addHttpRequest(request);
                    sUpdateRequest = conGo.addHttpRequest(request);
                }     
                else
                {
                    return null;
                }
            
        }
        return conGo;   
    } 
    
     /* 
    *  This method is used to build the required inputs needed for VerifyDemographics service
    *  @name   buildRequestUpdateVerifyDemographics
    *  @param  string sRecordId -  Account to get data from service
    *  @return string - Returns the Request body in Serialized form
    */ 
    @testvisible 
    private static string buildRequestUpdateVerifyDemographics(string sAccId)
    {
        string sJsonBodyGo = '';
        try
        {
            if(string.isNotBlank(sAccId))
            {   
                List<String> lstEnterpriseIds = new List<String>();
                VerifyDemographicUpdateRequest_DTO_HUM oRequest = new VerifyDemographicUpdateRequest_DTO_HUM();
                VerifyDemographicUpdateRequest_DTO_HUM.RequestHeader objRequestHeader = 
                    new VerifyDemographicUpdateRequest_DTO_HUM.RequestHeader();
                    objRequestHeader.RequestID = String.valueOf((Math.random() * 100));
                    objRequestHeader.SessionID = UserInfo.getSessionId();
                oRequest.RequestHeader = objRequestHeader;
                VerifyDemographicUpdateRequest_DTO_HUM.PutverificationstatusRequest objPutVerificationStatusRequest = 
                    new VerifyDemographicUpdateRequest_DTO_HUM.PutverificationstatusRequest();
                VerifyDemographicUpdateRequest_DTO_HUM.Demographics objDemographics =
                    new VerifyDemographicUpdateRequest_DTO_HUM.Demographics();
                    objDemographics.source = 'CRM';
                    objDemographics.lastVerifiedDate = String.valueOf(system.now().format('yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\''));
                    Id userID =  UserInfo.getuserID();
                    List<User> lstUser = VerifyDemographics_LD_HUM.getUserNetwork(userId);
                    objDemographics.verifiedBy = '';        
                    if(lstUser != null && !lstUser.isEmpty()){
                        objDemographics.verifiedBy = (NULL != lstUser[0].Name) ? lstUser[0].Name : lstUser[0].Network_User_Id__c;        
                    }
                objPutVerificationStatusRequest.demographics = objDemographics;
                
                List<Account> lstAccount = VerifyDemographics_LD_HUM.getAccount(sAccId);
                if(lstAccount != null && !lstAccount.isEmpty()){
                    lstEnterpriseIds.add(lstAccount[0].Enterprise_ID__c);
                }
                objPutVerificationStatusRequest.PersonId = lstEnterpriseIds;
                
                oRequest.PutverificationstatusRequest = objPutVerificationStatusRequest;
                sJsonBodyGo = JSON.serialize(oRequest);
                return sJsonBodyGo;    
            }
            else    sJsonBodyGo =  null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'buildRequestUpdateVerifyDemographics');
            sJsonBodyGo  =  NULL;
        }
        return sJsonBodyGo ;
    }
    /*
    *  This method is used to Parse response from VerifyDemographics service
    *  @name   processVerifyDemographicsResponse
    *  @param  List and object
    *  @return object
    */
    @AuraEnabled (continuation=true)
    public static object processUpdateVerifyDemographicsResponse(List<String> labels,object state)
    { 
        try{        
            HttpResponse sResponse = Continuation.getResponse(labels[0]);
            if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.VOB_FAULT_CODE) && 
                (sResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)) || Test.isRunningTest())
            {       
                VerifyDemographicUpdateResponse_DTO_HUM objResponse = new VerifyDemographicUpdateResponse_DTO_HUM();
                objResponse = (VerifyDemographicUpdateResponse_DTO_HUM)System.JSON.deserialize(sResponse.getbody(),VerifyDemographicUpdateResponse_DTO_HUM.class); 
                if (objResponse != null)
                { 
                    if(objResponse.PutVerificationStatusResponse.Status == 'success'){
                        return getLastVerifyDatesRest(string.valueOf(state));
                    }
                    if(objResponse.PutVerificationStatusResponse.Status == 'failure'){
                        return null;
                    }
                }
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'processUpdateVerifyDemographicsResponse');
            return null;
        }
        return getLastVerifyDatesRest(string.valueOf(state));
    }
     /*
    * Method Name   :    getLastVerifyDates()
    * Description   :    To hit the Async webservice GetLastVerifiedDates
    * Return Type   :    void 
    * Parameter     :    NA
    */
    @AuraEnabled( continuation=true)
    public static Continuation getLastVerifyDates(String recId)
    {
        lstDispAccounts = new List<HUMVerifyFamilyDemoWrapperHelper>();
        lstVerifyDemoGraphics = new List<HUMVerifyDemoGraphicsWrapperHelper>();
        lstVerifyDemoWrapper = new list<VerifyDemographicGetWrapper_DTO_HUM>();
        
            AccountId = recId;
            if(AccountId != NULL)
            {
                 oAccount = VerifyDemographics_LD_HUM.getMemAccount(AccountId);
            }
        
        HUMVerifyFamilyCOBService.soap oHUMSoap = new HUMVerifyFamilyCOBService.soap();
        HUM_Webservice_Callout__c oHUMWebServiceCall = HUM_Webservice_Callout__c.getValues('AddressVerification');
        oHUMSoap.clientCertName_x = oHUMWebServiceCall.Certificate_Name__c; 
        oHUMSoap.endpoint_x =  oHUMWebServiceCall.End_Point_URL__c;
        oHUMSoap.timeout_x = Integer.valueOf(oHUMWebServiceCall.Timeout__c);
        Integer TIMEOUT_INT_SECS = Integer.valueOf(oHUMWebServiceCall.Timeout__c) / 1000;
        Continuation cont = new Continuation(TIMEOUT_INT_SECS);
        HUMVerifyFamilyCOBService.OIDemographicsInputDTO oHUMOIDemographicsInputDTO = new HUMVerifyFamilyCOBService.OIDemographicsInputDTO();
        oHUMOIDemographicsInputDTO.Category = ''; 
        oHUMOIDemographicsInputDTO.PlatformCode = '';
        oHUMOIDemographicsInputDTO.MemberGenKey = long.valueOf((oAccount.Mbr_Gen_Key__c == NULL) ? '0' : oAccount.Mbr_Gen_Key__c);
        oHUMOIDemographicsInputDTO.EnterpriseId = (oAccount.Enterprise_ID__c == NULL) ? '0' : oAccount.Enterprise_ID__c;  
        Try
            {
                cont.continuationMethod = 'processGetLastVerifyDates';
                oHUMFutureResponse = oHUMSoap.beginGetLastVerifiedDates(cont,oHUMOIDemographicsInputDTO);
                contstate2 = oHUMFutureResponse;
                cont.state = contstate2;
           }
            Catch(exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'getLastVerifyDates');
            }
        Return cont;
    }
    /*
    * Method Name   :    processGetLastVerifyDates
    * Description   :    continuation call out response process
    * Return Type   :    Object
    * Parameter     :    VOID
    */
    @AuraEnabled @TestVisible
    public static List<HUMVerifyDemoGraphicsWrapperHelper> processGetLastVerifyDates(Object state) {
        HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture responseCallBack = (HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture)state;
      try
    {   
           oHUMDemographicsOutputDTO =(HUMVerifyFamilyCOBService.OIDemographicsOutputDTO)responseCallBack.getValue();
           return populateDemographics(oHUMDemographicsOutputDTO);
           }
      catch(exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'processGetLastVerifyDates');
                return null;
            }
       
   }
    /*
    * Method Name   :    populateDemographics
    * Description   :    Populates Last verified by ,Last Verified On in VerifyDemographics section
    * Return Type   :    void
    * Parameter     :    HUMVerifyFamilyCOBService.OIDemographicsOutputDTO
    */
    @AuraEnabled (continuation=true)  @TestVisible
    public static List<HUMVerifyDemoGraphicsWrapperHelper> populateDemographics( HUMVerifyFamilyCOBService.OIDemographicsOutputDTO oHUMDemographicsOutputDTO)
    {
        String sNetworkUserId = '';
        HUMVerifyDemoGraphicsWrapperHelper overify = new HUMVerifyDemoGraphicsWrapperHelper();
        overify.sLastVerifiedOn = (NULL != oHUMDemographicsOutputDTO.DemographicsLastVerifiedDate) ? Date.valueOf(oHUMDemographicsOutputDTO.DemographicsLastVerifiedDate) : NULL;
        overify.sLastVerifiedBy = '';
        if(NULL != oHUMDemographicsOutputDTO.DemographicsVerifiedBy)
        {
            sNetworkUserId = oHUMDemographicsOutputDTO.DemographicsVerifiedBy.trim();
            if(!String.isblank(sNetworkUserId))
            {
                try
                {
                    User oUser = VerifyDemographics_LD_HUM.getUser(sNetworkUserId);
                    if(oUser != NULL )  overify.sLastVerifiedBy = oUser.Name;
                }      
                catch(exception ex)
                {
                    overify.sLastVerifiedBy = sNetworkUserId;
                }        
            }
        }
        else
        {
            overify.sLastVerifiedBy = system.Label.HUMVerifyGetLastVerifyError;
        }
        overify.iDaysSinceLastVerified  = (NULL != overify.sLastVerifiedOn) ? Date.valueOf(String.valueOf(overify.sLastVerifiedOn)).daysBetween(system.Today()) : -1;
        bVerifyMessage = (overify.iDaysSinceLastVerified > GLOBAL_CONSTANT_LH_HUM.daysCheck) ? true : false;
        lstVerifyDemoGraphics = new List<HUMVerifyDemoGraphicsWrapperHelper>();
        lstVerifyDemoGraphics.add(overify);
        return lstVerifyDemoGraphics;
        
    }
    
     @AuraEnabled (continuation=true)
    public static Continuation verifyDemographics(String recId)
    {   
        lstDispAccounts = new List<HUMVerifyFamilyDemoWrapperHelper>();
        lstVerifyDemoGraphics = new List<HUMVerifyDemoGraphicsWrapperHelper>();
        lstVerifyDemoWrapper = new list<VerifyDemographicGetWrapper_DTO_HUM>();
        
            AccountId = recId;
            if(AccountId != NULL)
            {
                 oAccount = VerifyDemographics_LD_HUM.getMemAccount(AccountId);
            }
        
        HUMVerifyFamilyCOBService.soap oHUMSoap = new HUMVerifyFamilyCOBService.soap();
        HUM_Webservice_Callout__c oHUMWebServiceCall = HUM_Webservice_Callout__c.getValues('AddressVerification');
        oHUMSoap.clientCertName_x = oHUMWebServiceCall.Certificate_Name__c; 
        oHUMSoap.endpoint_x =  oHUMWebServiceCall.End_Point_URL__c;
        oHUMSoap.timeout_x = Integer.valueOf(oHUMWebServiceCall.Timeout__c);
        Integer TIMEOUT_INT_SECS = Integer.valueOf(oHUMWebServiceCall.Timeout__c) / 1000;
        lstSelectedMembers = new List<HUMVerifyFamilyDemoWrapperHelper>();
        String sActionType = Label.HUMServiceActionType;
        String sMemberId = '';
        String sTrimSize = Label.HUMServiceTrimSize;
        HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
        if(NULL != lstDispAccounts && !lstDispAccounts.isempty())
        {
            for (HUMVerifyFamilyDemoWrapperHelper oHUMVerifyFamilyDemoWrapperHelper : lstDispAccounts) 
            {
                if (oHUMVerifyFamilyDemoWrapperHelper.bSelected == true) lstSelectedMembers.add(oHUMVerifyFamilyDemoWrapperHelper); 
            }
        }
        HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO oPerDemLstVerifyInfo = getPersonDemoGraphicsInputDTO();
        HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO oUpdateMemDemoDetails = getUpdateMemberInputDTO();
        Continuation cont = new Continuation(TIMEOUT_INT_SECS);
        try
        {
            cont.continuationMethod = 'processverifyDemographics';
            oHUMDemographicFutureResponse = oHUMSoap.beginUpdateDemographics(cont,sActionType , oPerDemLstVerifyInfo, sMemberId , sTrimSize , oUpdateMemDemoDetails);    
            mpOfCallback.put('sAccId',AccountId);
            mpOfCallback.put('oHUMDemographicFutureResponse',oHUMDemographicFutureResponse);
            contstate4 = mpOfCallback;
            cont.state = contstate4; 
        }     
        catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'verifyDemographics');
        }
      return cont;   
    }
    /*
    * Method Name   :    processverifyDemographics
    * Description   :    continuation call out response process
    * Return Type   :    Object
    * Parameter     :    VOID
    */
   
    @AuraEnabled (continuation=true)
    public static Object processverifyDemographics(Object state) {
        Map<String,Object> mpOfMaintainState = (Map<String,Object>)state;
        HttpResponse sResponse = Continuation.getResponse('Continuation-1');
       HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture responseCallBack = (HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture)mpOfMaintainState.get('oHUMDemographicFutureResponse');
        try{
           sResult =(String)responseCallBack.getValue();
           return getLastVerifyDates(String.valueOf(mpOfMaintainState.get('sAccId')));
        }
        catch(exception ex)
            {                
                HUMExceptionHelper.logErrors(ex, 'VerifyDemographics_LS_HUM', 'processverifyDemographics');
               return null;
            }
    }
   
    /* 
    * Method Name  :    getPersonDemoGraphicsInputDTO
    * Description  :    Forming PersonDemographicsLastVerifiedInfoDTO to hit the webservice Update Demographics
    * Return Type  :    HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO
    * Parameter    :    NA
    */ 
    private static HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO getPersonDemoGraphicsInputDTO()
    {
        System.debug('Inside getPersonDemoGraphicsInputDTO');
        HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO oPerDemLstVerifyInfo = new  HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO();
        oPerDemLstVerifyInfo.SelectedMemberGenKey = '';
        HUMVerifyFamilyCOBService.PersonCoverageInfoDTO oPerCovInfo = getInputPersonCoverageInfoDTO();
        HUMVerifyFamilyCOBService.ArrayOfPersonCoverageInfoDTO  oArrPerCovInfo = new HUMVerifyFamilyCOBService.ArrayOfPersonCoverageInfoDTO();
        oArrPerCovInfo.PersonCoverageInfoDTO = new HUMVerifyFamilyCOBService.PersonCoverageInfoDTO[]{oPerCovInfo};                   
        HUMVerifyFamilyCOBService.PersonMemberInfoDTO oPerMemInfo = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO();
        HUMVerifyFamilyCOBService.ArrayOfPersonMemberInfoDTO oArrPerMemInfo = new HUMVerifyFamilyCOBService.ArrayOfPersonMemberInfoDTO();
        oArrPerMemInfo.PersonMemberInfoDTO = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO[]{};
        // As a part of defect(#156048) fix modified the code to pass multiple genkeys to the service.
        if(NULL != lstSelectedMembers && !lstSelectedMembers.isEmpty())
        {   
            for(integer i = 0 ; i < lstSelectedMembers.size() ; i++) 
            {
                HUMVerifyFamilyCOBService.PersonMemberInfoDTO oPerMemInfo1 = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO();
                oPerMemInfo1.MemberGenKey = lstSelectedMembers[i].Account.Mbr_Gen_Key__c;
                oPerMemInfo1.EnterpriseId = lstSelectedMembers[i].Account.Enterprise_ID__c;
                oArrPerMemInfo.PersonMemberInfoDTO.add(oPerMemInfo1);
            }
        }
        else
        {
            HUMVerifyFamilyCOBService.PersonMemberInfoDTO oPerMemInfo1 = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO();
             oPerMemInfo1.MemberGenKey = oAccount.Mbr_Gen_Key__c;
             oPerMemInfo1.EnterpriseId = oAccount.Enterprise_ID__c;
            oArrPerMemInfo.PersonMemberInfoDTO.add(oPerMemInfo1);
        }
        oPerMemInfo.DemographicMemberCoverageInfo = oArrPerCovInfo;
        oPerDemLstVerifyInfo.DemographicMemberInfo = oArrPerMemInfo;
        return oPerDemLstVerifyInfo; 
    }
    /*
    * Method Name  :    getUpdateMemberInputDTO
    * Description  :    Forming UpdatedMemberDemographicsDetailsDTO  to hit update Demographics webservice
    * Parameter    :    NA
    * Return Type  :    HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO 
    */ 
    private static HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO getUpdateMemberInputDTO()
    {
        HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO oUpdateMemDemoDetails = getInputUpdateMemDemoDetailsDTO(); 
        HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetails = getInputUpdateMemAddrDetailsDTO();
        HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberAddressDetailsDTO oUpdateMemAddDetails = new HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberAddressDetailsDTO();
        oUpdateMemAddDetails.UpdatedMemberAddressDetailsDTO = new HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO[]{}; 
        // As a part of defect(#156048) fix modified the code to pass multiple genkeys to the service.
        if(NULL != lstSelectedMembers && !lstSelectedMembers.isEmpty())
        {   
            for(integer i = 0 ; i < lstSelectedMembers.size() ; i++) 
            {
                HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetails1 = getInputUpdateMemAddrDetailsDTO();
                oUpdateMemAddrDetails1.MemGenKey = lstSelectedMembers[i].Account.Mbr_Gen_Key__c;
                oUpdateMemAddrDetails1.EnterpriseId = lstSelectedMembers[i].Account.Enterprise_ID__c;
                oUpdateMemAddDetails.UpdatedMemberAddressDetailsDTO.add(oUpdateMemAddrDetails1);
            }
        }
        else
        {
            HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetails1 = getInputUpdateMemAddrDetailsDTO();
            oUpdateMemAddrDetails1.MemGenKey = oAccount.Mbr_Gen_Key__c;
            oUpdateMemAddrDetails1.EnterpriseId = oAccount.Enterprise_ID__c;
            oUpdateMemAddDetails.UpdatedMemberAddressDetailsDTO.add(oUpdateMemAddrDetails1);
        }
        HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO oUpdateMemcovDetails = getInputUpdateMemcovDetailsDTO();           
        HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberCoverageDetailsDTO oArrPerCovInfoAdd = new HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberCoverageDetailsDTO();
        oArrPerCovInfoAdd.UpdatedMemberCoverageDetailsDTO = new HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO[]{oUpdateMemcovDetails};   
        HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO updateCoverageDetails;
        oUpdateMemDemoDetails.UpdatedMemberCoverageDetails = oArrPerCovInfoAdd;
        oUpdateMemDemoDetails.UpdatedMemberResidentialAddressDetails = new HUMVerifyFamilyCOBService.UpdatedMemberResidentialAddressDTO();    
        oUpdateMemDemoDetails.UpdatedMemberAddressDetails = oUpdateMemAddDetails; 
        return oUpdateMemDemoDetails;
    }   
    /*
    * Method Name  :    getInputPersonCoverageInfoDTO
    * Description  :    To get successful response from service passing
                      blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type  :    HUMVerifyFamilyCOBService.PersonCoverageInfoDTO  
    * Parameter    :    NA
    */
    private static HUMVerifyFamilyCOBService.PersonCoverageInfoDTO  getInputPersonCoverageInfoDTO() 
    {       
       HUMVerifyFamilyCOBService.PersonCoverageInfoDTO oPerCovInfotemp = new HUMVerifyFamilyCOBService.PersonCoverageInfoDTO();
       oPerCovInfotemp.Category = '';
       oPerCovInfotemp.MemberIDBase = '';
       oPerCovInfotemp.MemberIDDependentCode = '';
       oPerCovInfotemp.SrcPlatformCode = '';
       return oPerCovInfotemp;           
    }
    /*
    * Method Name   :    getInputUpdateMemDemoDetailsDTO
    * Description   :    To get successful response from service passing
                       blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type   :    HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO 
    * Parameter     :    NA
    */
    private static HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO getInputUpdateMemDemoDetailsDTO()
    {          
       Id userID =  UserInfo.getuserID();
       User oUser = [Select Network_User_Id__c from User where Id =: userID Limit 1];
       HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO oUpdateMemDemoDetailsTemp = new HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO(); 
       oUpdateMemDemoDetailsTemp.CntyCd = '';
       oUpdateMemDemoDetailsTemp.DOB = '';
       oUpdateMemDemoDetailsTemp.FirstName = '';
       oUpdateMemDemoDetailsTemp.Gender = '';
       oUpdateMemDemoDetailsTemp.GroupGenKeys = Label.HUMServiceGroupMemSubGenkeys;
       oUpdateMemDemoDetailsTemp.IsHUManaOneHCR = '';
       oUpdateMemDemoDetailsTemp.LastName = '';
       oUpdateMemDemoDetailsTemp.MiddleInit = '';
       oUpdateMemDemoDetailsTemp.SSN = '';
       oUpdateMemDemoDetailsTemp.TransType = '';
       oUpdateMemDemoDetailsTemp.UpdateType = Label.HUMServiceUpdateType;
       oUpdateMemDemoDetailsTemp.UpdateCnt = Label.HUMServiceUpdateCnt;
       if(NULL != lstSelectedMembers && !lstSelectedMembers.isEmpty())
       {
          oUpdateMemDemoDetailsTemp.UpdateCnt = String.valueof(lstSelectedMembers.size());
       }
       oUpdateMemDemoDetailsTemp.userID = '';
       if(oUser.Network_User_Id__c != NULL)
       {
           oUpdateMemDemoDetailsTemp.userID = oUser.Network_User_Id__c;
       }
       return oUpdateMemDemoDetailsTemp;         
    }
    /*
    * Method Name  :    getInputUpdateMemcovDetailsDTO
    * Description  :    To get successful response from service passing
                         blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type  :    HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO 
    * Parameter    :    NA
    */
    private static HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO getInputUpdateMemcovDetailsDTO()
    {
       HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO oUpdateMemcovDetailsTemp = new HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO();
       oUpdateMemcovDetailsTemp.MbrGenKey = Label.HUMServiceGroupMemSubGenkeys; 
       oUpdateMemcovDetailsTemp.SubGenKey = Label.HUMServiceGroupMemSubGenkeys; 
       oUpdateMemcovDetailsTemp.AE_ContractId = '';
       oUpdateMemcovDetailsTemp.AE_DepSequence = '';
       oUpdateMemcovDetailsTemp.AE_SrcGrpId = '';
       oUpdateMemcovDetailsTemp.AE_SrcPlatformCd = '';
       oUpdateMemcovDetailsTemp.AE_SubscrSequence = '';
       oUpdateMemcovDetailsTemp.Benefit = '';
       oUpdateMemcovDetailsTemp.CusCovSeq = '';
       oUpdateMemcovDetailsTemp.CusGenKey = '';
       oUpdateMemcovDetailsTemp.DepDOB = '';
       oUpdateMemcovDetailsTemp.DepFirstName = '';
       oUpdateMemcovDetailsTemp.DepLastName = '';
       oUpdateMemcovDetailsTemp.DepSSN = Label.HUMConstantZero;
       oUpdateMemcovDetailsTemp.EffDate = '';
       oUpdateMemcovDetailsTemp.EndDate = '';
       oUpdateMemcovDetailsTemp.MbrTermDate = '';
       oUpdateMemcovDetailsTemp.MbrTermReason = '';
       oUpdateMemcovDetailsTemp.Product = '';
       oUpdateMemcovDetailsTemp.RecordSeparator = '';
       oUpdateMemcovDetailsTemp.Status = '';
       oUpdateMemcovDetailsTemp.SubFirstName = '';
       oUpdateMemcovDetailsTemp.SubLastName = '';
       oUpdateMemcovDetailsTemp.SubSSN = Label.HUMConstantZero;
       oUpdateMemcovDetailsTemp.SubscriberInd = '';
       return oUpdateMemcovDetailsTemp;
    }
    /*
    * Method Name   :    getInputUpdateMemAddrDetailsDTO
    * Description   :    To get successful response from service passing
                        blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type   :    HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO 
    * Parameter     :    NA
    */
    private static HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO getInputUpdateMemAddrDetailsDTO()
    {
       HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetailsTemp = new HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO();
       oUpdateMemAddrDetailsTemp.Addr1 = '';
       oUpdateMemAddrDetailsTemp.Addr2 = '';
       oUpdateMemAddrDetailsTemp.City = '';
       oUpdateMemAddrDetailsTemp.FirstName = '';
       oUpdateMemAddrDetailsTemp.HomeEmail  = '';
       oUpdateMemAddrDetailsTemp.HomePhone = '';
       oUpdateMemAddrDetailsTemp.LastName = '';
       oUpdateMemAddrDetailsTemp.MemGenKey = '';
       oUpdateMemAddrDetailsTemp.EnterpriseId = '';
       oUpdateMemAddrDetailsTemp.MiddleInitial = '';
       oUpdateMemAddrDetailsTemp.State = ''; 
       oUpdateMemAddrDetailsTemp.WorkEmail = '';
       oUpdateMemAddrDetailsTemp.WorkPhone = '';
       oUpdateMemAddrDetailsTemp.WorkPhoneExt = '';
       oUpdateMemAddrDetailsTemp.ZipPlusCd = '';
       oUpdateMemAddrDetailsTemp.Zip = ''; 
       return oUpdateMemAddrDetailsTemp; 
    }
    
   
}