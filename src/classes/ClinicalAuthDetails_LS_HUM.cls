/*****************************************************************************************
Apex class Name    : ClinicalAuthDetails_LS_HUM
Version            : 1.0
Function           : This service class is used to request Authsummary details 
Created On         : 07/19/2022
Test Class         : ClinicalAuthDetails_LT_HUM 

Modification Log:
*   Developer                   Code Review           Date               Description
* ------------------------------------------------------------------------------------------------------------------------------ 
*  Rajesh Narode                                    07/19/2022         3470586-Contact Information - Backend Code Conversion 
*  Sathish Babu                                     02/08/2022         User story 3400770 Diagnosis Codes.
********************************************************************************************************************************/

public with sharing class ClinicalAuthDetails_LS_HUM
{
    private AuthReferral_Query_LD_HUM oAuthReferral = new AuthReferral_Query_LD_HUM();
    public static Map<String, Map<String, Object>> standardMapForLogging = new Map<String, Map<String, Object>>();
    
    @AuraEnabled public Static ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapper{get;set;}
    @AuraEnabled public Static ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapperIP{get;set;}
    @AuraEnabled public Static ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapperOP{get;set;}
    @AuraEnabled public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProvider{get;set;}
    @AuraEnabled public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstDiagnosis{get;set;}
    @AuraEnabled public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProcedure{get;set;}
    @AuraEnabled public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProcedureOP{get;set;}
    @AuraEnabled public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstCommunicationRecords {get; set;}
    @AuraEnabled public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstLettersRecords {get; set;}
    public string sLettersPageNumber {get;set;}
    public string sCommunicationPageNumber {get;set;}
    public Boolean bServiceError{get;set;}
    public Boolean bSearchError{get;set;}
    public Boolean bAuthTypeIP{get;set;}
    public Boolean bAuthTypeOP{get;set;}
    public String sPolicyMemRecID {get; set;}
    public String standardMapvalue {get; set;}
    public String sAuthID{get;set;}
    public string sTotalCommunicationRecords {get;set;}
    public string sTotalLettersRecords {get;set;}      
    public string sEnterpriseID;
    public string sDateOfBirth;
    public list<string> lstFloatingLinks{get;set;}
    @testVisible private  String sBodyReq ='' ;
    private AuthReferral_Query_LD_HUM objdatalayer = new AuthReferral_Query_LD_HUM();
    private Integer count = 0;
    private Integer pageNum = 1;
    ID curentUserID = UserInfo.getUserId();
    public Account objAcct =  new Account();
    AuthReferral_Security_LH_HUM objAuthSecurity = new AuthReferral_Security_LH_HUM();
    AuthReferral_SecurityHO_LH_HUM cHomeOfficeSecurity=new AuthReferral_SecurityHO_LH_HUM();
    public Boolean bViewResults = true;
    public Boolean bShowMessage ;
    private List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthRefferal = new List<AuthRefferal_Wrapper_LDTO_HUM>();
    public Integer totalRecordFound = 0;
    public static string memPlanId{Get;Set;}
    
   
/* Method Name   :  setupGoButtonRequest
* Description   :  This method is used to form http request.
* Return Type   :  HttpRequest 
* Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Member Id)
*/
    
    public HttpRequest setupGoButtonRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderGoAuth,string sAuthNumber)
    {
        
        try
        { 
            sBodyReq = buildRequestGo(sAuthNumber);
            if(Null != sBodyReq)
            {
                request.setMethod(oSvcHeaderGoAuth.Request_Type__c);
                request.setBody(sBodyReq );
                request.setEndpoint(oSvcHeaderGoAuth.End_Point_URL__c);
                request.setClientCertificateName(oSvcHeaderGoAuth.Certificate_Name__c);
                request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaderGoAuth.Content_Type__c);
                return request;
            }
            else
            {
                return Null;  
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalAuthDetails_LS_HUM', 'setupGoButtonRequest');
            return Null;
        }
    }
    
    /*
* Method Name   :  buildRequestGo
* Description   :  This method Is used to build the required inputs need to hit the service
* Return Type   :  String
* Parameters    :  String(Auth Number)
*/
    Public string buildRequestGo(String sAuthNumber)
    {    
        
        string sJsonBodyGo = '';
        try
        {
            if(String.isNotBlank(sAuthNumber))
            { 
                ClinicalAuthSummaryRequest_H_HUM_V2 omain = New ClinicalAuthSummaryRequest_H_HUM_V2();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest oreq = New ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory oauthcategory = New ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData  oPageMetaData = New ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData();
                oreq.PageMetaData = oPageMetaData;
                omain.AuthSummaryRequest = oreq;
                sJsonBodyGo = json.serialize(omain);
                bViewResults = False;
                return sJsonBodyGo ;                
            }
            else
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalAuthDetails_LS_HUM', 'setupGoButtonRequest');
            return Null;
        }
        
    } 
    
   
    /*
* Method Name   :  buildWrapperList
* Description   :  This method is used to assign the response to variables to wrapper Class
* Return Type   :  List<AuthReferral_Wrapper_H_HUM>
* Parameters    :  List<ClinicalAuthSummaryResponse_H_HUM>
*/
    private List<AuthRefferal_Wrapper_LDTO_HUM> buildWrapperList(List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary> lstauth)
    {
        List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_LDTO_HUM>();
        for(ClinicalAuthSummaryResponse_H_HUM.AuthSummary obj:lstauth )
        {
            AuthRefferal_Wrapper_LDTO_HUM objwrapper = new AuthRefferal_Wrapper_LDTO_HUM();
            objwrapper.sAuthorizationOrReferralNumber = obj.AuthID;
            objwrapper.sAuthorizationType = obj.AuthType;
            objwrapper.sOverallStatus = obj.AuthStatus;
            
            if(obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_BHINP_HUM || obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_INP_HUM)
            {
                if(! String.isBlank(obj.InpatientDetails.AdmissionDate))     objwrapper.sAdmFirstDay = formateDate(obj.InpatientDetails.AdmissionDate);
                
                if(! String.isBlank(obj.InpatientDetails.DischargeDate))     objwrapper.sDischargeLastDay = formateDate(obj.InpatientDetails.DischargeDate);
                
                if(! String.isBlank(obj.InpatientDetails.AdmissionType))     objwrapper.sServiceType = obj.InpatientDetails.AdmissionType;
            }
            if(obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_OUP_HUM || obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_BHOUP_HUM)
            {
                if(! String.isBlank(obj.OutpatientDetails.FirstDay))       objwrapper.sAdmFirstDay = formateDate(obj.OutpatientDetails.FirstDay);
                
                if(! String.isBlank(obj.OutpatientDetails.LastDay))        objwrapper.sDischargeLastDay = formateDate(obj.OutpatientDetails.LastDay);
                
                if(! String.isBlank(obj.OutpatientDetails.ServiceType))    objwrapper.sServiceType = obj.OutpatientDetails.ServiceType;
            }
            objwrapper.sFacility = obj.FacilityProviderName;
            objwrapper.sRequestingrovider = obj.RequestingProviderName;
            objwrapper.sTreatingProvider = obj.TreatingProviderName;
            objwrapper.sGroupId = obj.GroupId;
            objwrapper.bIsAccessible  = true;
            objwrapper = updateUrl(objwrapper);
            lstAuthsummary.add(objwrapper);
        }
        return lstAuthsummary;
    }
    
    public AuthRefferal_Wrapper_LDTO_HUM updateUrl(AuthRefferal_Wrapper_LDTO_HUM authRefUrl){
        
        try{      
            
            if(authRefUrl != null){
                String authType = authRefUrl.sAuthorizationType;
                String authTypeJS;
                boolean bOnSearch = false;
                if(authType.equals('Inpatient')) {
                    authTypeJS = 'Auth:' + authRefUrl.sAuthorizationOrReferralNumber;
                } else if(authType.equals('Outpatient')) {
                    authTypeJS = 'Auth/Referral:' + authRefUrl.sAuthorizationOrReferralNumber; 
                }
                authRefUrl.sAtuhRefUrl = '/lightning/n/authsummary_medical_authorization_detail?'+
                    'AuthId='+authRefUrl.sAuthorizationOrReferralNumber+
                    '&RecId='+memPlanId+
                    '&search='+bOnSearch+
                    '&type=Authorizations'+
                    '&subtype='+authTypeJS;
            }
            
        }catch(Exception ex){
            return null;
        }
        return authRefUrl;
    }
    /*
* Method Name   :  checkSecurity
* Description   :  This method is used to pass the wraper list to security class to verify whether it is accessible for user or not.
* Return Type   :  List<AuthReferral_Wrapper_H_HUM>
* Parameters    :  List<AuthReferral_Wrapper_H_HUM>
*/
    private List<AuthRefferal_Wrapper_LDTO_HUM> checkSecurity(List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthsummary){
        return cHomeOfficeSecurity.getFilteredAuths(lstAuthsummary,curentUserID,objAcct.Id);
    }
    
    /*
* Method Name   :  checkAuthSecurity
* Description   :  This method is used to check the security on search of Authorization.
* Return Type   :  Boolean 
* Parameters    :  ClinicalAuthDetailsResponse_H_HUM 
*/
    public Boolean checkAuthSecurity(ClinicalAuthDetailsResponse_H_HUM oResponse,String sRecId)
    {
        Boolean bIsAccible = true;
        ClinicalAuthDetailsResponse_H_HUM.AuthorizationsResponse oAuthResponse = oResponse.AuthorizationsResponse;
        ClinicalAuthDetailsResponse_H_HUM.Authorizations oAuthorizations = oAuthResponse.Authorizations;
        List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
        
        if(oAuthorizations != null)
        {
            if(sRecId != null && string.isNotBlank(sRecId))
            {
                objAcct = AuthReferral_Query_LD_HUM.getMemberAccount(sRecId);  
            }
            lstAuthorization = oAuthorizations.Authorization;            
            List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_LDTO_HUM>();
            AuthRefferal_Wrapper_LDTO_HUM objauth = new AuthRefferal_Wrapper_LDTO_HUM();
            objauth.sAuthorizationOrReferralNumber = lstAuthorization[0].AuthID;
            objauth.sGroupId = lstAuthorization[0].GroupId;
            objauth.bIsAccessible = true;
            lstAuthsummary.add(objauth);
            lstAuthsummary = cHomeOfficeSecurity.getFilteredAuths(lstAuthsummary,curentUserID,objAcct.Id);
            if(lstAuthsummary!= null && !lstAuthsummary.isEmpty())
            {
                if(lstAuthsummary[0].bIsAccessible)
                {
                    bIsAccible = false;  
                }
                else
                {
                    bIsAccible = true;
                }
            }
        }
        return bIsAccible;
    }
    
    
    /**
*  This method is used to assign values to the request helper class
*  @name assignValuesToHelperV2
*  @param String, String - Selected authorization record id, selected object id
*  @return ClinicalAuthDetailsRequest_H_HUMV2 - Auth detail service request
*  @throws NA
*/
    @AuraEnabled
    public static ClinicalAuthDetailsRequest_LH_HUM assignValuesToHelperV2(string sAuthId, string sMemberPlanRecID)
    {
		List<Account> lstAccount = [Select Id,Enterprise_ID__c,Birthdate__c from Account where Id In (Select MemberId from MemberPlan where Id =: sMemberPlanRecID)]; 
        
        ClinicalAuthDetailsRequest_LH_HUM oclinicalAuthdetails = new ClinicalAuthDetailsRequest_LH_HUM ();     
        ClinicalAuthDetailsRequest_LH_HUM.AuthDetailRequest oAuthReqHum = new ClinicalAuthDetailsRequest_LH_HUM.AuthDetailRequest();     
        ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Communication oPageMetaData = new ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Communication();
        ClinicalAuthDetailsRequest_LH_HUM.Filter_Communication oFilterComm = new ClinicalAuthDetailsRequest_LH_HUM.Filter_Communication();
        ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Communication oSortDetailComm = new ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Communication();
        List<ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Communication> lstSortDetailComm = new List<ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Communication>();
        ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Letters oPageMetaDataLetters = new ClinicalAuthDetailsRequest_LH_HUM.PageMetaData_Letters();
        ClinicalAuthDetailsRequest_LH_HUM.Filter_Letters oFilterLetters = new ClinicalAuthDetailsRequest_LH_HUM.Filter_Letters();
        ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Letters oSortDetailLetters = new ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Letters();
        List<ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Letters> lstSortDetailLetters = new List<ClinicalAuthDetailsRequest_LH_HUM.SortDetail_Letters>();
        
        
        
        if(String.isNotBlank(sAuthId) )
        {       
            oAuthReqHum.Authid = sAuthId; 
            oAuthReqHum.MemberId = lstAccount[0].Enterprise_ID__c;
            oAuthReqHum.MemberIdType = GLOBAL_CONSTANT_HUM.HTTPREQUEST_PERSONID_HUM;
            oAuthReqHum.DateOfBirth = lstAccount[0].Birthdate__c;           
            oPageMetaData.PageSize = 50;
            oPageMetaData.PageNumber = 1;
            oAuthReqHum.PageMetaData_Communication = oPageMetaData;
            oAuthReqHum.Filter_Communication = oFilterComm;
            oAuthReqHum.SortDetail_Communication = lstSortDetailComm;   
            oPageMetaDataLetters.PageSize = 50;
            oPageMetaDataLetters.PageNumber = 1;
            oAuthReqHum.PageMetaData_Letters = oPageMetaDataLetters;
            oAuthReqHum.Filter_Letters  = oFilterLetters;
            oAuthReqHum.SortDetail_Letters =lstSortDetailLetters;      
            oclinicalAuthdetails.AuthDetailRequest= oAuthReqHum;
        }
        return oclinicalAuthdetails;     
    }
    
    /**
*  This method is used to generate the service request
*  @name setHttpRequest
*  @param HttpRequest , HUM_Webservice_Callout__c,String  - Request object,
custom setting record for auth detail service,request body
*  @return HttpRequest - Updated request object
*  @throws NA
*/
    public static HttpRequest setHttpRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcClinicalAuthDetail, String sRequestBody){
        oRequest.setMethod(oSvcClinicalAuthDetail.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcClinicalAuthDetail.End_Point_URL__c);
        if(!Test.isRunningTest()){ oRequest.setClientCertificateName(oSvcClinicalAuthDetail.Certificate_Name__c); }
        oRequest.setHeader(Label.HUMServiceTesterController_content_type, oSvcClinicalAuthDetail.Content_Type__c);
        return oRequest;
    }
    
    /**
*  This method is used to retreive Authorization Details from response
*  @name retrieveAuthDetail
*  @param List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> - List of auth details received from the service
*  @return ClinicalAuthDetailsWrapper_LDTO_HUM - Wrapper class object which will be used to display auth details.
*  @throws NA
*/ 
    public static ClinicalAuthDetailsWrapper_LDTO_HUM retrieveAuthDetail(List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> lstAuthDetail)
    {
        ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapper = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        if(lstAuthDetail != null && !lstAuthDetail.isEmpty())
        {
            oAuthDetailsWrapper.sAuthID = lstAuthDetail[0].AuthID;
            oAuthDetailsWrapper.sAuthStatus = lstAuthDetail[0].AuthStatus;
            standardMapForLogging.put('AuthStatus', new Map<String, Object>{'Overall Status' => oAuthDetailsWrapper.sAuthStatus});
            
            if(String.isNotBlank(lstAuthDetail[0].CreatedDate))    oAuthDetailsWrapper.sCreatedDate = formateDate(lstAuthDetail[0].CreatedDate);
            if(String.isNotBlank(lstAuthDetail[0].NextReviewDate))    oAuthDetailsWrapper.sNextReviewDate = formateDate(lstAuthDetail[0].NextReviewDate);  
            oAuthDetailsWrapper.sContactMethod = lstAuthDetail[0].ContactMethod;
            oAuthDetailsWrapper.sRequestType = lstAuthDetail[0].RequestType;
            oAuthDetailsWrapper.sAuthType = lstAuthDetail[0].AuthType;
            oAuthDetailsWrapper.sDenialReason   = lstAuthDetail[0].DenialReason;
            oAuthDetailsWrapper.sClaimsPaymentNotes = lstAuthDetail[0].ClaimPaymentNotes;                    
            standardMapForLogging.put('Auth Type', new Map<String, Object>{'Auth Type' => lstAuthDetail[0].AuthType});
            if(oauthDetailsWrapper.sAuthType == GLOBAL_CONSTANT_HUM.AUTHTYPE_BHINP_HUM || oauthDetailsWrapper.sAuthType == GLOBAL_CONSTANT_HUM.AUTHTYPE_INP_HUM)
            {
                standardMapForLogging.put('AuthID', new Map<String, Object>{'Auth/Referral #' => oAuthDetailsWrapper.sAuthID});
            }
            else
            {
                standardMapForLogging.put('AuthID', new Map<String, Object>{'Auth/Referral #' => oAuthDetailsWrapper.sAuthID});
            }
            oAuthDetailsWrapper.sContactMethod = lstAuthDetail[0].ContactMethod; 
            oAuthDetailsWrapper.sIsReferral  = lstAuthDetail[0].IsReferral; 
        }
        else 
        {
            oAuthDetailsWrapper = null;
        }
        return oAuthDetailsWrapper;
    }  
    
    /**
*  This method is used to retreive Referral Information for InPatient from response
*  @name retrieveReferralInformationIP
*  @param List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> - List of auth details received from the service
*  @return ClinicalAuthDetailsWrapper_LDTO_HUM - Wrapper class object which will be used to display auth details.
*  @throws NA
*/
    public static ClinicalAuthDetailsWrapper_LDTO_HUM retrieveReferralInformationIP(List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> lstAuthDetail)
    {
        ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapperIP = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetailsResponse_LH_HUM.InpatientDetails oInpatient = lstAuthDetail[0].InpatientDetails;
        
        if(String.isNotBlank(oInpatient.AdmissionDate))
        {
            oAuthDetailsWrapperIP.sAdmissionDate = formateDate(oInpatient.AdmissionDate);
            standardMapForLogging.put('AdmissionDate', new Map<String, Object>{'Admission Date' => oAuthDetailsWrapperIP.sAdmissionDate});
        }
        
        oAuthDetailsWrapperIP.sAdmissionType = oInpatient.AdmissionType;
        
        if(String.isNotBlank(oInpatient.LastCoveredDate))  oAuthDetailsWrapperIP.sLastCoveredDate = formateDate(oInpatient.LastCoveredDate);
        
        if(String.isNotBlank(oInpatient.DischargeDate))
        {
            oAuthDetailsWrapperIP.sDischargeDate = formateDate(oInpatient.DischargeDate);
            standardMapForLogging.put('DischargeDate', new Map<String, Object>{'Discharge Date' =>  oAuthDetailsWrapperIP.sDischargeDate});
        }
        
        oAuthDetailsWrapperIP.sTotalDaysApproved = oInpatient.TotalDaysApproved;
        return oAuthDetailsWrapperIP;
    }
    
    /**
*  This method is used to retreive Referral Information for OutPatient from response
*  @name retrieveReferralInformationOP
*  @param List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>  - List of auth details received from the service
*  @return List<ClinicalAuthDetailsWrapper_LDTO_HUM> -  Wrapper class object which will be used to display auth details
*  @throws NA
*/
    public static ClinicalAuthDetailsWrapper_LDTO_HUM retrieveReferralInformationOP(List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> lstAuthDetail)
    {
        
        ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapperOP = new ClinicalAuthDetailsWrapper_LDTO_HUM();
        ClinicalAuthDetailsResponse_LH_HUM.OutpatientDetails oOutpatient = lstAuthDetail[0].OutpatientDetails;
        oAuthDetailsWrapperOP.sServiceType = oOutpatient.ServiceType;
        standardMapForLogging.put('Service Type', new Map<String, Object>{'Service Type' =>  oAuthDetailsWrapperOP.sServiceType});
        
        if(String.isNotBlank(oOutpatient.FirstDay))
        {
            oAuthDetailsWrapperOP.sFirstDay = formateDate(oOutpatient.FirstDay);
            standardMapForLogging.put('FirstDay', new Map<String, Object>{'First Day' =>  oAuthDetailsWrapperOP.sFirstDay});
        }
        if(String.isNotBlank(oOutpatient.LastDay))
        {
            oAuthDetailsWrapperOP.sLastDay = formateDate(oOutpatient.LastDay);
            standardMapForLogging.put('LastDay', new Map<String, Object>{'Last Day' =>  oAuthDetailsWrapperOP.sLastDay});
        }
        
        If(String.isNotBlank(oOutpatient.TotalDaysApproved) && oOutpatient.TotalDaysApproved.isNumeric())
        {
            oAuthDetailsWrapperOP.iTotalDays = Integer.ValueOf(oOutpatient.TotalDaysApproved);
        }
        return oAuthDetailsWrapperOP;
    }
    
    /**
*  This method is used to retreive provider information for InPatient/OutPatient from response
*  @name retrieveProviderDetails
*  @param List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>  - List of auth details received from the service
*  @return List<ClinicalAuthDetailsWrapper_LDTO_HUM> - List of provider details
*  @throws NA
*/
    @AuraEnabled
    public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM> retrieveProviderDetails(String lstAuthDetailStr)
    {
        ClinicalAuthDetailsResponse_LH_HUM.Authorization lstAuthDetail = (ClinicalAuthDetailsResponse_LH_HUM.Authorization)JSON.deserialize(lstAuthDetailStr,ClinicalAuthDetailsResponse_LH_HUM.Authorization.class);
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProvider = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>(); 
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProvider1 = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>(3); 
        ClinicalAuthDetailsResponse_LH_HUM.ProviderDetails oProviderDetail = lstAuthDetail.ProviderDetails;
        List<ClinicalAuthDetailsResponse_LH_HUM.ProviderDetail> lstProviderDetail = oproviderdetail.ProviderDetail;
        for(ClinicalAuthDetailsResponse_LH_HUM.ProviderDetail oProvider : lstProviderDetail)
        {
            ClinicalAuthDetailsWrapper_LDTO_HUM oProv = new ClinicalAuthDetailsWrapper_LDTO_HUM();
            ClinicalAuthDetailsResponse_LH_HUM.AddressDetails oAddress = oProvider.AddressDetails;
            List<ClinicalAuthDetailsResponse_LH_HUM.Address> lstAddress = oAddress.Address;
            if(lstAddress != null && !lstAddress.isEmpty())
            {
                for(ClinicalAuthDetailsResponse_LH_HUM.Address objAdd : lstAddress)
                {
                    if(objAdd.AddressType == 'Select Service')
                    {
                        oProv.sReqProviderCity = objAdd.City; 
                        oProv.sReqProviderState = objAdd.State;
                        oProv.sReqProviderPhnNum = objAdd.PhoneNumber;
                        if(string.isBlank(objAdd.ZipCode_ext))
                        {
                            oProv.sReqProviderZipCode = objAdd.ZipCode;
                        }
                        else
                        {
                            oProv.sReqProviderZipCode = objAdd.ZipCode+'-'+objAdd.ZipCode_ext;
                        }
                        oProv.sReqProviderStreet = objAdd.Street_1+' ' +objAdd.Street_2; 
                        break;
                    }
                }
            }
            oProv.sReqProviderID = oProvider.Id;
            oProv.sReqProviderName = oProvider.Name;
            oProv.sReqProviderTaxID = oProvider.TaxID;
            oProv.sReqProviderPartStatus = oProvider.ParticipatingStatus;
            oProv.sReqProviderType = oProvider.Type;
            lstProvider.add(oProv);
            
        }
        return assignProviderDetails(lstProvider);   
    }  
    
    /**
*  This method is used to populate standard map which will be used in logging.
*  @name populateStandardMapForLoggingForProvider
*  @param ClinicalAuthDetailsWrapper_LDTO_HUM  - Selected auth detail record.
*  @return Void
*  @throws NA
*/
    
    @testVisible private void populateStandardMapForLoggingForProvider(ClinicalAuthDetailsWrapper_LDTO_HUM oProv) 
    {
        if(oProv.sReqProviderType.containsIgnoreCase('RequestingProvider'))
        {
            standardMapForLogging.put('Requesting Provider Name', new Map<String, Object>{'Requesting Provider' => oProv.sreqProviderName});
            standardMapForLogging.put('Requesting Provider TaxId', new Map<String, Object>{'Requesting Provider TaxId' =>  oProv.sReqProviderTaxID});
            standardMapForLogging.put('Requesting Provider Id', new Map<String, Object>{'Requesting Provider Id' => oProv.sReqProviderID});
        }
        else if(oProv.sReqProviderType.containsIgnoreCase('TreatingProvider'))
        {
            standardMapForLogging.put('Treating Provider Name', new Map<String, Object>{'Treating Provider' => oProv.sreqProviderName});
            standardMapForLogging.put('Treating Provider TaxId', new Map<String, Object>{'Treating Provider TaxId' =>  oProv.sReqProviderTaxID});
            standardMapForLogging.put('Treating Provider Id', new Map<String, Object>{'Treating Provider Id' => oProv.sReqProviderID});
        }
        else if(oProv.sReqProviderType.containsIgnoreCase('Facility'))
        {
            standardMapForLogging.put('Facility Provider Name', new Map<String, Object>{'Facility' => oProv.sreqProviderName});
            standardMapForLogging.put('Facility Provider TaxId', new Map<String, Object>{'Facility Provider TaxId' =>  oProv.sReqProviderTaxID});
            standardMapForLogging.put('Facility Provider Id', new Map<String, Object>{'Facility Provider Id' => oProv.sReqProviderID});
        }
    }
    
    /**
*  This method is used to assign provider type and sort the order by type
*  @name assignProviderDetails
*  @param List<ClinicalAuthDetailsWrapper_LDTO_HUM> - List of auth details received from the service
*  @return List<ClinicalAuthDetailsWrapper_LDTO_HUM> - List of auth details received from the service
*  @throws NA
*/ 
    @AuraEnabled
    public Static List<ClinicalAuthDetailsWrapper_LDTO_HUM>  assignProviderDetails(List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProvider ) 
    {
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProvider1 = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>(3); 
        for(ClinicalAuthDetailsWrapper_LDTO_HUM oprov : lstprovider)
        {
            if(oprov.sreqProviderType == GLOBAL_CONSTANT_HUM.AUTHDETAIL_REQUESTING_HUM+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM)
            {
                oprov.sreqProviderType = GLOBAL_CONSTANT_HUM.AUTHDETAIL_REQUESTING_HUM+' '+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM;
                lstprovider1.set(0,oprov);
            }
            else if(oprov.sreqProviderType == GLOBAL_CONSTANT_HUM.AUTHDETAIL_TREATING_HUM+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM)
            { 
                oprov.sreqProviderType = GLOBAL_CONSTANT_HUM.AUTHDETAIL_TREATING_HUM+' '+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM; 
                lstprovider1.set(1,oprov);  
            }
            else if(oprov.sreqProviderType == GLOBAL_CONSTANT_HUM.AUTHDETAIL_FACILITY_HUM)
            {
                oprov.sreqProviderType = GLOBAL_CONSTANT_HUM.AUTHDETAIL_FACILITY_HUM+' '+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM;
                lstprovider1.set(2,oprov);
            }
        } 
        return  lstprovider1;
    } 
    
    /**
*  This method is used to retreive diagnosis codes for inpatient/outpatient from response
*  @name retrieveDiagnosisCodes
*  @param List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> - Auth details received from service
*  @return List<ClinicalAuthDetailsWrapper_LDTO_HUM> - List of diagnosis codes
*  @throws NA
*/
    @AuraEnabled
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> retrieveDiagnosisCodes(string lstAuthDetailDia) 
    {
        ClinicalAuthDetailsResponse_LH_HUM.Authorization lstAuthDetail = (ClinicalAuthDetailsResponse_LH_HUM.Authorization)JSON.deserialize(lstAuthDetailDia,ClinicalAuthDetailsResponse_LH_HUM.Authorization.class);
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstDiagnosis = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        if(lstAuthDetail != null)
        {
            ClinicalAuthDetailsResponse_LH_HUM.DiagnosisDetails oDiagnosisDetail = lstAuthDetail.DiagnosisDetails;
            List<ClinicalAuthDetailsResponse_LH_HUM.DiagnosisDetail> lstDiagnosisDetail = oDiagnosisDetail.DiagnosisDetail;
            for(ClinicalAuthDetailsResponse_LH_HUM.DiagnosisDetail oDiagnosis : lstDiagnosisDetail)
            {
                ClinicalAuthDetailsWrapper_LDTO_HUM oDiag = new ClinicalAuthDetailsWrapper_LDTO_HUM();
                oDiag.sDiagnosisType = oDiagnosis.Type;
                oDiag.sDiagnosisCode = oDiagnosis.Code;
                oDiag.sDiagnosisDescription = oDiagnosis.Description;
                oDiag.sDiagnosisStatus = oDiagnosis.Status;
                lstDiagnosis.add(oDiag);
            }
        }
        return lstDiagnosis;
    }  
    
    /**
*  This method is used to retrieve procedure codes for in patient from response
*  @name retrieveProcedureCodes
*  @param List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> - Auth details received from service
*  @return List<ClinicalAuthDetailsWrapper_LDTO_HUM> - List of procedure codes
*  @throws NA
*/
    @AuraEnabled
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> retrieveProcedureCodes(string lstAuthDetailStr)
    {
        ClinicalAuthDetailsResponse_LH_HUM.Authorization lstAuthDetail = (ClinicalAuthDetailsResponse_LH_HUM.Authorization)JSON.deserialize(lstAuthDetailStr,ClinicalAuthDetailsResponse_LH_HUM.Authorization.class);
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProcedure = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        If(lstAuthDetail != null)
        {
            ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetails oProcedureDetail = lstAuthDetail.ProcedureDetails;
            List<ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetail> lstProcedureDetail = oProcedureDetail.ProcedureDetail;
            for(ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetail oProcedure : lstProcedureDetail)
            {
                ClinicalAuthDetailsWrapper_LDTO_HUM oProcedureCode = new ClinicalAuthDetailsWrapper_LDTO_HUM();
                oProcedureCode.sProcedureCode = oProcedure.Code;
                oProcedureCode.sProcedureDescription = oProcedure.Description;
                oProcedureCode.sProcedureStatus = oProcedure.Status;
				oProcedureCode.sProcedureRequestingUnits = '';
                oProcedureCode.sProcedureAuthorizedUnits = '';
                oProcedureCode.sProcedureTypeofUnits = '';
                lstProcedure.add(oProcedureCode);
            }
        }
        return lstProcedure;
    } 
    
    /**
*  This method is used to retrieve procedure codes for out patient from response
*  @name retrieveProcedureCodesOutPatient
*  @param List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> - Auth details received from service
*  @return List<ClinicalAuthDetailsWrapper_LDTO_HUM> - List of procedure codes
*  @throws NA
*/
    @AuraEnabled
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> retrieveProcedureCodesOutPatient(string lstAuthDetailStr)
    {
        ClinicalAuthDetailsResponse_LH_HUM.Authorization lstAuthDetail = (ClinicalAuthDetailsResponse_LH_HUM.Authorization)JSON.deserialize(lstAuthDetailStr,ClinicalAuthDetailsResponse_LH_HUM.Authorization.class);
        List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProcedureOP = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
        If(lstAuthDetail != null )
        {
            ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetails oProcedureDetailOP = lstAuthDetail.ProcedureDetails;
            List<ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetail> lstProcedureDetailOP = oproceduredetailOP.ProcedureDetail;
            for(ClinicalAuthDetailsResponse_LH_HUM.ProcedureDetail oProcedureOP : lstProcedureDetailOP)
            {
                ClinicalAuthDetailsWrapper_LDTO_HUM oProcedureCodeOP = new ClinicalAuthDetailsWrapper_LDTO_HUM();
                oProcedureCodeOP.sProcedureCode = oProcedureOP.Code;
                oProcedureCodeOP.sProcedureDescription = oProcedureOP.Description;
                oProcedureCodeOP.sProcedureRequestingUnits = oProcedureOP.Visit.RequestedUnits;
                oProcedureCodeOP.sProcedureAuthorizedUnits = oProcedureOP.Visit.ApprovedUnits;
                oProcedureCodeOP.sProcedureTypeofUnits = oProcedureOP.Visit.TypeOfUnits;
                
                if(oProcedureCodeOP.sProcedureTypeofUnits.containsIgnoreCase('NUMBER OF VISITS'))
                    standardMapForLogging.put('Number Of Visits', new Map<String, Object>{'Number Of Visits' => oProcedureCodeOP.sProcedureAuthorizedUnits});
                
                oProcedureCodeOP.sProcedureStatus = oProcedureOP.Status;
                lstProcedureOP.add(oProcedureCodeOP);
            }
        }
        return lstProcedureOP;
    } 
    
    /**
*  This method is used to format the date into MM/dd/yyyy
*  @name formateDate
*  @param String - Datetime value in string format
*  @return String  - Datetime value in MM/dd/yyyy format
*  @throws NA
*/
    private static string formateDate(String sDateTime)
    {
        String[] sArrayDate = (sDateTime.substringBefore('T')).split('-');
        String sDate = sArrayDate[1]+'/'+sArrayDate[2]+'/'+sArrayDate[0];
        return sDate;
    }
	
	 
     /**
*  This method is used to process response from Letters records service
*  @name responseWrapLettersRecs
*  @param HttpResponse, List<ClinicalAuthDetailsWrapper_LDTO_HUM> - response structure, list of Letters records fetched in earlier service call
*  @return List<ClinicalAuthDetailsWrapper_LDTO_HUM>
*  @throws NA
*/  
    
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> responseWrapLettersRecs(HttpResponse sResponse, List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstLettersRecords)
    {
        
        LettersRecordsResponse_LH_HUM oResponse = new LettersRecordsResponse_LH_HUM();
        if(String.isNotBlank(sResponse.getBody()))    oResponse = (LettersRecordsResponse_LH_HUM)JSON.deserialize(sResponse.getBody(),LettersRecordsResponse_LH_HUM.class);            
        if(oResponse != NULL)
        {
            if(oResponse.LettersResponse != null)
            {                   
                LettersRecordsResponse_LH_HUM.LettersResponse  oAuthResponse = oResponse.LettersResponse ;
                LettersRecordsResponse_LH_HUM.LettersRecordsList oAuthorizations = oAuthResponse.LettersRecordsList;
                List<LettersRecordsResponse_LH_HUM.LettersRecords> lstAuthorization = oAuthorizations.LettersRecords;                   
                List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstLettersRecordsWrapper = new List<ClinicalAuthDetailsWrapper_LDTO_HUM>();
                if(lstAuthorization != null && !lstAuthorization.isEmpty())
                {            
                    for(LettersRecordsResponse_LH_HUM.LettersRecords oLetters : lstAuthorization)
                    {
                        ClinicalAuthDetailsWrapper_LDTO_HUM oLetter = new ClinicalAuthDetailsWrapper_LDTO_HUM();                
                        oLetter.sLettersType = oLetters.LetterType;
                        oLetter.sLettersAssociatedAuthID = oLetters.AssociatedAuthID;
                        oLetter.sLettersAssociatedCaseID = oLetters.AssociatedCaseID;
                        oLetter.sLettersCreatedBy = oLetters.CreatedBy;
                        oLetter.sLettersCreatedDate = oLetters.CreatedDate;
                        oLetter.sLettersTemplateName = oLetters.TemplateName;
                        oLetter.sLettersStatus = oLetters.Status;
                        oLetter.sLettersFullfillmentStatus = oLetters.FullfillmentStatus;
                        oLetter.sLettersP2POfferedinLetter = oLetters.P2POfferedinLetter;
                        oLetter.sLettersLetterName = oLetters.LetterName;
                        oLetter.sLettersVoidRemarks = oLetters.VoidRemarks;
                        oLetter.sLettersLastModifiedBy = oLetters.LastModifiedBy;
                        if(oLetters.LastModifiedDate != null) oLetter.sLettersLastModifiedDate = oLetters.LastModifiedDate.format(); 
                        oLetter.sFilePath = oLetters.FilePath;                
                        lstLettersRecordsWrapper.add(oLetter);
                    }
                }        
                lstLettersRecords.addAll(lstLettersRecordsWrapper);        
            }
        }           
        return lstLettersRecords;
    }
	
	
    /**
*  This method is used to assign values to the request helper class
*  @name assignValuesToLettersReq
*  @param String, String, String - authorization id, policy member id, pagenumber
*  @return LettersRecordsRequest_LH_HUM - Letters records service request
*  @throws NA
*/
    
    public static LettersRecordsRequest_LH_HUM  assignValuesToLettersReq(String sAuthId, String sPolMemId, String sLettersPageNumber)
    {    
        LettersRecordsRequest_LH_HUM oLetterRequest = new LettersRecordsRequest_LH_HUM();
        LettersRecordsRequest_LH_HUM.GetLettersRequest oLetterReq = new LettersRecordsRequest_LH_HUM.GetLettersRequest();
        LettersRecordsRequest_LH_HUM.Filter oLetterFilter = new LettersRecordsRequest_LH_HUM.Filter();
         LettersRecordsRequest_LH_HUM.SortInfo oSortInfo = new LettersRecordsRequest_LH_HUM.SortInfo();
        LettersRecordsRequest_LH_HUM.PageMetaData oPageMetaData = new LettersRecordsRequest_LH_HUM.PageMetaData();
        
        Account oAccount = AuthReferral_Query_LD_HUM.getMemberAccount(sPolMemId);
        oLetterReq.MemberId = oAccount.Enterprise_ID__c;          
        oLetterReq.MemberIdType = GLOBAL_CONSTANT_HUM.HTTPREQUEST_PERSONID_HUM;
        oLetterReq.DateOfBirth = oAccount.Birthdate__c;
        oPageMetaData.PageSize = 50;
        oPageMetaData.PageNumber = Integer.ValueOf(sLettersPageNumber);
        oLetterReq.PageMetaData = oPageMetaData;
        oLetterFilter.EntityID = sAuthId;
        oSortInfo.Sequence = 1;
        oSortInfo.Order = 'DESC';
        oSortInfo.Criteria = 'ModifiedDate';
        oLetterReq.Filter = oLetterFilter;
        oLetterReq.SortDetail = new List<LettersRecordsRequest_LH_HUM.SortInfo>{oSortInfo};        
        oLetterRequest.GetLettersRequest= oLetterReq;  
        return oLetterRequest;
    }
    
	
	/**
*  This method is used to setup request for Letters records service
*  @name setHttpRequestLettersRecs
*  @param HttpRequest, HUM_Webservice_Callout__c, String sRequestBody
*  @return HttpRequest 
*  @throws NA
*/
    public static HttpRequest setHttpRequestLettersRecs(HttpRequest oRequest,HUM_Webservice_Callout__c oLettersRecordsService, String sRequestBody)
    {
        oRequest.setMethod(oLettersRecordsService.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oLettersRecordsService.End_Point_URL__c);
        if(!Test.isRunningTest())    
        {
            oRequest.setClientCertificateName(oLettersRecordsService.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oLettersRecordsService.Timeout__c));
        oRequest.setHeader('content-type', oLettersRecordsService.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');            
        return oRequest;
    }
    
     /**
	*  This method is used to assign values to the request helper class
	*  @name assignValuesToCommReq
	*  @param String, String, String - authorization id, policy member id, pagenumber
	*  @return CommunicationRecordsRequest_H_HUM - communication records service request
	*  @throws NA
	*/
    public static CommunicationRecordsRequest_H_HUM assignValuesToCommReq(String sAuthId,  String sCommunicationPageNumber, string sEnterpriseID, string sDateOfBirth)
    {
        CommunicationRecordsRequest_H_HUM oCommunicationRequest = new CommunicationRecordsRequest_H_HUM();
        CommunicationRecordsRequest_H_HUM.GetCommunicationRecordsRequest oCommRecsReq = new CommunicationRecordsRequest_H_HUM.GetCommunicationRecordsRequest();
        CommunicationRecordsRequest_H_HUM.Filter oCommRecFilter = new CommunicationRecordsRequest_H_HUM.Filter();
        CommunicationRecordsRequest_H_HUM.PageMetaData oPageMetaData = new CommunicationRecordsRequest_H_HUM.PageMetaData();
        
        oCommRecsReq.MemberId = sEnterpriseID;
        oCommRecsReq.MemberIdType = GLOBAL_CONSTANT_HUM.HTTPREQUEST_PERSONID_HUM;
        oCommRecsReq.DateOfBirth = sDateOfBirth;
        oPageMetaData.PageSize = 50; 
        oPageMetaData.PageNumber = Integer.ValueOf(sCommunicationPageNumber)+1;
        oCommRecsReq.PageMetaData = oPageMetaData;
        oCommRecFilter.EntityID = sAuthId;
        oCommRecsReq.Filter = oCommRecFilter;      
        oCommunicationRequest.GetCommunicationRecordsRequest = oCommRecsReq;
        return oCommunicationRequest;       
    }
    
	/**
	*  This method is used to setup request for communication records service
	*  @name setHttpRequestCommRecs
	*  @param HttpRequest, HUM_Webservice_Callout__c, String sRequestBody
	*  @return HttpRequest 
	*  @throws NA
	*/  
    public static HttpRequest setHttpRequestCommRecs(HttpRequest oRequest,HUM_Webservice_Callout__c oCommRecordsService, String sRequestBody)
    {
        oRequest.setMethod(oCommRecordsService.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oCommRecordsService.End_Point_URL__c);
        if(!Test.isRunningTest())    
        {
            oRequest.setClientCertificateName(oCommRecordsService.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oCommRecordsService.Timeout__c));
        oRequest.setHeader('content-type', oCommRecordsService.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');
        return oRequest;
    }
 }