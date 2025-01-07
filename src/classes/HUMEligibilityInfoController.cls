/*******************************************************************************************************************************
Apex Class Name : HUMEligibilityInfoController
Version         : 1.0
Created On      : 04/02/2015 
Function        : Class which acts a Controller for"EligibilityInfoPage" and provides Eligibility Information related to 
                  Policy Member which will be displayed On Policy Member Detail Page
Test Class      :  HUMEligibilityInfoControllerTest                
                  
Modification Log: 
* Developer Name           Code Review                 Date                        Description
********************************************************************************************************************************************************************************
* Harshith M L               22313                    04/02/2015                Original Version
* Tanushree Hazari           22313                    04/06/2015                Modified it as part of REQ-146803
* Kritika P Popat            22313                    04/06/2015                Modified it as part of REQ-148605
* SuryaKumari Medicherla     22313                    04/06/2015                Modified it as part of REQ-148609
* Shreya Choodamani          22313                    04/09/2015                Modified it as part of REQ-148610
* Manish Kumar               22538                    04/22/2015                Modified the code to fix the encryption issue related to RSO indicators
* Manish Kumar               22646                    04/27/2015                Modified the code for defect#180358.0001 to resolve service issue. 
                                                                                Lines modified : 351 & 359.
* Manish Kumar               23159                    05/18/2015                Modified the code for Medicare/Medicaid and LOB_Indicator to resolve service issue. 
                                                                                Code modified : Throughout the class 
* Manish Kumar               23831                    06/10/2015                Modified the code for defect#182167 to resolve service issue to fetch dual alerts.
                                                                                Code modified : Throughout the class
* Kritika Popat              24093                    06/18/2015                Modified class for Defect 187428.0001
* Kritika Popat              24147                    06/22/2015                Modified class for Defect 187756.0001
* Kritika Popat              24190                    06/23/2015                Fixed for resolving requirement change.
* Apoorv Jain                                         6/16/2016                 REQ - 237004 : Remvoed the DPASS service call from the controller
* Gargey Sharma              34890                    08/06/2016                REQ - 269734 : Ability to view AEC Care Manager Information in CRM
* Sharan Shanmugam                                    04/18/2017                REQ - 291107 : Ability to view MTM indicator
* Avinash Choubey            90004                    05/05/2017                REQ - 303396 : Show only LIS for Pharmacy User
* Vishal Verma               94518                    05/18/2017                Defect 278688.0001 : Updated MTM mapping
* Sharan Shanmugam           94518                    06/14/2017                REQ - 320594 : Added null checks To avoid exceptions
* Vishal Verma               114200                   07/27/2017                REQ - 323542 : Exception handling 
* Rajesh Keswani             116759                   07/20/2017                Implementation of REQ - 323389 - adding networkId in service request
* Sai Ayyagari               184451                   02/21/2018                REQ - 350853 Fix for HUMExceptionHelper, store the list of exceptions capture
* Sai Ayyagari               214956                   04/23/2018                REQ - 350870 Convert HumCIMedicareMemberService to Async
* Sunil Bukya                234629                   05/25/2018                REQ - 366198 Implemented Member_Id__c platform encryption changes.  
* Amol Patil                                          07/24/2018                REQ - 358964 Ability for CRM to display additional Care Managers 
* Amol Patil                                          08/27/2018                Defect 379959.0001 Modified phone number format
* Santhosh Ganji                                      31/01/2019                REQ - 378996: Added code to Unhide the existing ‘Eligibility Information’ section to pharmacy users
* Santhi Mandava                                      04/15/2019                Replaced "Member_Id_Value__c" with Name  
* Amar Gadewar                                        08/21/2019                REQ - 406850 AHT - All Users - Link to LIS Document from Policy Member page (RF)
* Prudhvi Pamarthi                                    08/21/2019                REQ - 403754 Fix for Eligibility section is not working properly in Policy detail page
* Ajay Chakradhar                           		  05/05/2021	            REQ - 2184294 - Replacing constants with Consumer Management Constants
* Vardhaman                                           06/07/2021                User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
* Ajay Chakradhar                                     07/02/2022                REQ - US 3210977 - Classic Updating Branding from LIS to Extra Help in CRM Service
**********************************************************************************************************************************************************************************/
public with sharing class HUMEligibilityInfoController 
{
    private String sMemberCardId ='';
    private String sCurrentDate = '';
    private String sType ='';
    private String sGroupNumber = '';
    private String sSubSourceKey = '';
    private String sMedicareName = 'MedicareID';
    private String sEligibleYesValue = 'Yes';
    private String sEligibleNoValue = 'No';
    private static final String sCharY = 'Y';
    private static final String sCharN = 'N';
    private static final Integer iZero = 0 ;
    private static final String sDateFormat = 'MM/dd/YYYY';
    private static final String sEligibleNo = 'No';
    private static final Integer iSix = 6;
    private static final Integer iNine = 9;
    public HumCIMedicareMemberService oHumCIMedicareMemberService = NULL;
    public Map<String,String> mapServiceResponse = NULL;
    public static String sNoValue = 'No Value';
    public string sLTSSReq = '';
    public string sMTMReq = '';
    public string sRecordId = '';
    public Policy_Member__c policyMemberInstance;
    public Boolean bIsPharmacyUser {get;set;}
    @TestVisible private String sRequest ='';
    public List<ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager> mngrDetails{ get; set; }
    public  list<ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager> objCareManagerList{ get; set; }
    public Boolean bTable{get;set;}
    Account acnt{get;set;}
    Private String sNONPHARMACY_USER_HUM = 'No';
    Private String sPHARMACY_LIS_HUM ='Extra Help';
    Private String sPHARMACY_MTM_HUM = 'Medication Therapy (MTM)';
    Private String sPOA_SERVICENAME = 'CIMedicareMultipleMemberDetails';
    Private String sRSO_MEMBER_TYPE = 'Member-Id-Base;';
    Private String sPOLICY_PLATFORM_INFO = 'LV;';
    private boolean bESAPILibSecurityfixSwitch {get;set;}
    
    //Capture Any DML Exceptions in errorLog object
        public List<Error_Log__c> listErrorLogs 
        {
            get 
            {
                if(listErrorLogs==null) {return new List<Error_Log__c>();}
                return listErrorLogs;
            }
            set;
        }
    /*
    * Constructor for the class
    * @param ApexPages.StandardController
    * @return 
    */
    public HUMEligibilityInfoController(ApexPages.StandardController controller) 
    {
        policyMemberInstance = (Policy_Member__c)controller.getRecord();
        bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
        bNoResponse = false;
        bIsPharmacyUser  = false; 
        if(Pharmacy_H_HUM.isPharmacyUser() != sNONPHARMACY_USER_HUM)       bIsPharmacyUser  = true;
        CallServices(policyMemberInstance);

        sLISComparisionMentorUrl = '';
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
        HUMCertificateURL__c oLISMentorLinkURL = HUMCertificateURL__c.getValues('LISMentorLinkURL');

        if(objHssURL != null && oLISMentorLinkURL != null) {
            sLISComparisionMentorUrl = objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(oLISMentorLinkURL.CertificateListURL__c));
        }
    }
    
    
    
    public Map<String,Map<String,String>> mapEligibilityInfo{ get; set; }
    public List<String> lstSectionName { get; set; }
    public List<String> lstFieldNames { get; set; }
    public Boolean bProcessFlag { get; set; } 
    public Boolean bNoResponse { get; set; }
    public String sLISComparisionMentorUrl { get; set; }
    
    
    /*
     * Method Name   :    CallServices
     * Description   :    Class to call the different services on page Load
     * Return Type   :    NA
     * Parameters    :    Standard Controller
    */
    public void CallServices(Policy_Member__c policyMemberInstance)
    {
        lstSectionName = new List<String>();
        lstFieldNames = new List<String>();
        List<HUM_Section_Priority__c> lstCustomPriority ;
        oHumCIMedicareMemberService = new HumCIMedicareMemberService();
        
        if(bIsPharmacyUser && Pharmacy_H_HUM.isHPTraditionalInsurancePSUser() == false)
        {
            lstCustomPriority = [SELECT Name,Priority__c FROM HUM_Section_Priority__c where name = : sPHARMACY_LIS_HUM limit 1];
        }
        else if(bIsPharmacyUser && Pharmacy_H_HUM.isHPTraditionalInsurancePSUser() == true)
        {
            lstCustomPriority = [SELECT Name,Priority__c FROM HUM_Section_Priority__c where name != : sPHARMACY_MTM_HUM ORDER By Priority__c ASC NULLS Last];
        }
        else
        {
            lstCustomPriority = [SELECT Name,Priority__c FROM HUM_Section_Priority__c ORDER By Priority__c ASC NULLS Last];
        }
        for(HUM_Section_Priority__c sectionPriorityInstance : lstCustomPriority)
        {
            lstSectionName.add(sectionPriorityInstance.Name);
        }
        
        if(HUMGetBusinessDescription.getPMBusinessDescription(policyMemberInstance.Id))
        {   
            mapEligibilityInfo = new Map<String,Map<String,String>>();
            bProcessFlag = true;
            // Querying Section Priority "Custom Setting" and storing it in the list( querying instead of using method inorder to sort by Priority)        
            // Querying Field Priority "Custom Setting" and storing it in the list( querying instead of using method inorder to sort by Priority)     
            List<HUM_Field_Priority__c> lstFieldPriority = [SELECT Name,Priority__c FROM HUM_Field_Priority__c ORDER By Priority__c ASC NULLS Last];
            for(HUM_Field_Priority__c fieldPriorityInstance : lstFieldPriority)
            {
                lstFieldNames.add(fieldPriorityInstance.Name);
            }
            
            for(integer i = 0; i < lstSectionName.size(); i++)  
            {
                populateEmptyService(lstSectionName[i]);
            }
            
            Datetime dtCurrentDate = Datetime.now();
            sCurrentDate = dtCurrentDate.format(sDateFormat);
            
            Set<String> setValidMemTypes = new Set<String>(sRSO_MEMBER_TYPE.split(';'));

            Policy_Member__c oPolicyMem = new Policy_Member__c();
            List<Policy_Member__c> lstPM = new List<Policy_Member__c>();

            Set<String> setValidPlatforms = new Set<String>(sPOLICY_PLATFORM_INFO.split(';'));
            lstPM = [Select Id, (Select Name From Member_Ids__r Where Type__c IN : setValidMemTypes) From Policy_Member__c Where Id =: policyMemberInstance.Id AND Policy__r.Platform__c IN : setValidPlatforms LIMIT 1];
            if (lstPM.size() > 0)
            {
                oPolicyMem = lstPM.get(0);
            }
            List<Member_ID__c> lstMemId = new List<Member_ID__c>();
            if(Null != oPolicyMem.Member_Ids__r)    
            {
                lstMemId = oPolicyMem.Member_Ids__r;
            }
            if(!(lstMemId.isEmpty()) && (!String.isBlank(lstMemId[iZero].Name)))    
            {
                sMemberCardId = oPolicyMem.Member_Ids__r[iZero].Name;
            }  
            String sSourceKey = [Select Policy__r.Source_Cust_Cov_Key__c From Policy_Member__c where Id =: policyMemberInstance.Id LIMIT 1].Policy__r.Source_Cust_Cov_Key__c;
            if(!String.isBlank(sSourceKey) && sSourceKey.length() >= iNine)
            {
                sGroupNumber = sSourceKey.substring(iZero, iSix);
                sSubSourceKey = sSourceKey.substring(iSix, iNine);
            }
        
        }
    }
   /*
    * Method Name   :  callCIMedMultipleMemberService
    * Description   :  This method is continuation call for CI Medicare Multiple service.
    * Return Type   :  Continuation
    * Parameters    :  NA
    */  
    
    public object callCIMedMultipleMemberService()
    {
        try
        {
        HUM_Webservice_Callout__c oSvcHeaderCI = HUM_Webservice_Callout__c.getValues(sPOA_SERVICENAME);
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderCI.Timeout__c) / 10000;
        HttpRequest request = new HttpRequest();
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        con = callGetMemberService(con);
            if((!String.isBlank(sMemberCardId)))
            {                
                if(!String.isBlank(sGroupNumber) && !String.isBlank(sSubSourceKey))    
                {
                    request = setupRequest(request, oSvcHeaderCI, sMemberCardID, sGroupNumber, sSubSourceKey, sCurrentDate, sCharY);   
                    con.continuationMethod='processAllResponses';
                    sRequest = con.addHttpRequest(request);
                }
                else    
                {
                    populateNoServiceResponse(HUM_Section_Priority__c.getInstance('Special Needs Plan (SNP)').Name);   
                }
                
            }
            else 
            {
                for(integer i = 0; i < lstSectionName.size(); i++)  
                {
                    populateEmptyService(lstSectionName[i]);
                }
                return null;
            }
        return con; 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'callCIMedMultipleMemberService');
            return null;
        } 
                 
    }
    
    /*
    * Method Name   :  setupRequest
    * Description   :  This method is used to frame request for CI service
    * Return Type   :  HttpRequest 
    * Parameters    :  HttpRequest, HUM_Webservice_Callout__c, String, String, String, String, String
    */
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcHeaderRTI, String sMemberCardID, String sGroupNumber, String sSubSourceKey, String sCurrentDate, String sChar)
    {
        HttpRequest oHTTPRequest = new HttpRequest();    
        try
        { 
            CIMedicareMultipleMemberRequest_DTO_HUM oRequest = new CIMedicareMultipleMemberRequest_DTO_HUM();
            CIMedicareMultipleMemberRequest_DTO_HUM.GetMemberInfo oGetMemInfo = new CIMedicareMultipleMemberRequest_DTO_HUM.GetMemberInfo();
            List<CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails> lstCI = new List<CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails>();
            CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails oMedicareMember = new CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails();
            oMedicareMember.MedicareID = '';
            oMedicareMember.MemberCardID = sMemberCardID;
            oMedicareMember.GroupID = sGroupNumber;
            oMedicareMember.BenefitSequenceNumber = sSubSourceKey;
            oMedicareMember.AsOfDate = sCurrentDate;
            oMedicareMember.LISInfo = oMedicareMember.LTSSInfo = oMedicareMember.ESRDInfo = sCharY;
            oMedicareMember.SPAPInfo = oMedicareMember.POAInfo = sCharN;
            oMedicareMember.SNPInfo = sChar;
            lstCI.add(oMedicareMember);
            
            oGetMemInfo.MemberDetails = lstCI;
            oRequest.GetMemberInfo = oGetMemInfo;
            String sReqBody = Json.serialize(oRequest);
            if(String.isNotBlank(sReqBody))
            {
                request.setMethod(oSvcHeaderRTI.Request_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oSvcHeaderRTI.Content_Type__c);  
                request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM,GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                if(!Test.isRunningTest())    
                {
                    request.setClientCertificateName(oSvcHeaderRTI.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcHeaderRTI.Timeout__c));
                request.setEndpoint(oSvcHeaderRTI.End_Point_URL__c);
                request.setBody(sReqBody);
                oHTTPRequest = request;
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'setupRequest');
            oHTTPRequest = null;
        }
        return oHTTPRequest; 
    }
    
   /*
    * Method Name   :  processAllResponses
    * Description   :  This method is to handle multiple calls at a time.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processAllResponses()
    {
       try 
        {
        processResponseCIMedMultiple();
        retrieveMTMValue();
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'processAllResponses');
         }
    }
    
    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    private void processResponseCIMedMultiple()
    {
        try 
        {   
            HttpResponse response = Continuation.getResponse(sRequest);
            responseParseCIMedMultiple(response); 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'processResponseCIMedMultiple');

        }
    }
    
    /*
    * Method Name   :  responseParseCIMedMultiple
    * Description   :  This method is used to check response
    * Return Type   :  void 
    * Parameters    :  HttpResponse obj
    */
    private void  responseParseCIMedMultiple(HttpResponse sResponse)
    { 
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == GlobalCommonConstants_HUM.HTTP_OK_HUM || statusCode == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)
        {
            HUMCIMedicareMemberResponseHelper oResponseCI = new HUMCIMedicareMemberResponseHelper();
            oResponseCI = parseCIResponse(sResponse.getBody());
            if(!bIsPharmacyUser || Pharmacy_H_HUM.isHPTraditionalInsurancePSUser() == true)
            {
                callESRDService(oResponseCI);
                callLTSSService(oResponseCI);
            }
            callLISService(oResponseCI);
            if(!String.isBlank(sGroupNumber) && !String.isBlank(sSubSourceKey))    callSNPService(policyMemberInstance,oResponseCI);  
        }
    }
    
    /*
    * Method Name   :  parseCIResponse
    * Description   :  This method is used to check response
    * Return Type   :  void 
    * Parameters    :  String
    */
    public HUMCIMedicareMemberResponseHelper parseCIResponse(String sResponse)
    {
        HUMCIMedicareMemberResponseHelper oResponseCI =  new HUMCIMedicareMemberResponseHelper();
        List<HUMCIMedicareMemberResponseHelper.Error> lstError = new List<HUMCIMedicareMemberResponseHelper.Error>();
        CIMedicareMultipleMemberResponse_DTO_HUM oResponse = (CIMedicareMultipleMemberResponse_DTO_HUM)System.JSON.deserialize(sResponse, CIMedicareMultipleMemberResponse_DTO_HUM.class);
        if(oResponse.CIMedicareResponse != null)
        {
            CIMedicareMultipleMemberResponse_DTO_HUM.CIMedicareResponse oCIResp = new CIMedicareMultipleMemberResponse_DTO_HUM.CIMedicareResponse();
            oCIResp = oResponse.CIMedicareResponse;
            List<CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo> lstGetMedicareMem = new List<CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo>();
            if(oCIResp.GetMedicareMemberInfo != Null && !oCIResp.GetMedicareMemberInfo.isEmpty())
            {
                lstGetMedicareMem = oCIResp.GetMedicareMemberInfo;
                if(lstGetMedicareMem != Null && !lstGetMedicareMem.isEmpty())
                {
                    CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo oGetMedicareMem = new CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo(); 
                    oGetMedicareMem  = lstGetMedicareMem[0];
                    CIMedicareMultipleMemberResponse_DTO_HUM.MedicareMemberLISSNPInfoResponse oMediMemResp = new CIMedicareMultipleMemberResponse_DTO_HUM.MedicareMemberLISSNPInfoResponse();
                    oMediMemResp = oGetMedicareMem.MedicareMemberLISSNPInfoResponse;
                    CIMedicareMultipleMemberResponse_DTO_HUM.Errors oErrors = new CIMedicareMultipleMemberResponse_DTO_HUM.Errors();
                    oErrors = oMediMemResp.Errors;
                    CIMedicareMultipleMemberResponse_DTO_HUM.Error oErr = new CIMedicareMultipleMemberResponse_DTO_HUM.Error();
                    oErr = oErrors.Error;
                    if(oErr == null || (string.isBlank(oErr.Code) && string.isBlank(oErr.Description)))
                    {
                        oResponseCI.sLISIndicator = oMediMemResp.LISIndicator;
                        oResponseCI.sSubsidyEffectDate = oMediMemResp.SubsidyEffectDate;
                        oResponseCI.sSubsidyEndDate = oMediMemResp.SubsidyEndDate;
                        oResponseCI.sLISSubsidyLevel = oMediMemResp.LISSubsidyLevel;
                        oResponseCI.sLISCopayCategory = oMediMemResp.LISCopayCategory;
                        oResponseCI.sLTSSIndicator = oMediMemResp.LTSSIndicator;
                        oResponseCI.sESRDIndicator = oMediMemResp.ESRDIndicator;
                        oResponseCI.sSPAPIndicator = oMediMemResp.SPAPIndicator;
                        oResponseCI.sSNPIndicator = oMediMemResp.SNPIndicator;
                        oResponseCI.sSNPTypeCode = oMediMemResp.SNPTypeCode;
                        oResponseCI.sPOAIndicator = oMediMemResp.POAIndicator;
                        oResponseCI.sPOALastName = oMediMemResp.POALastName;
                        oResponseCI.sPOAFirstName = oMediMemResp.POAFirstName;
                        oResponseCI.sPOAPhoneNumber = oMediMemResp.POAPhoneNumber;
                        oResponseCI.sPOAType = oMediMemResp.POAType;
                        oResponseCI.sPOAApplDate = oMediMemResp.POAApplDate; 
                    }
                    else
                    {
                        HUMCIMedicareMemberResponseHelper.Error err= new HUMCIMedicareMemberResponseHelper.Error();
                        err.Code = oErr.Code; 
                        err.Description = oErr.Description;
                        lstError.add(err);
                    }
                    oResponseCI.Errors = lstError;
                }
            }
        }
        return oResponseCI;
    }    
    
    /*
     * Method Name   :    populateEmptyService
     * Description   :    Method to populate Service if unexpected Medicare Name 
     * Return Type   :    void  
     * Parameters    :    void
    */  
    private void populateEmptyService(String sServiceName)
    {
        mapServiceResponse = new Map<String,String>();
        for(integer i = 0; i< lstFieldNames.size(); i++)
        {    
            mapServiceResponse.put(lstFieldNames[i],sNoValue);
        }
            
        mapEligibilityInfo.put(sServiceName,mapServiceResponse); 
    }
    
    /*
     * Method Name   :    populateNoServiceResponse
     * Description   :    Method to populate the map if there is no response from the service. 
     * Return Type   :    void  
     * Parameters    :    void
    */
    @testvisible private void populateNoServiceResponse(String sServiceName)
    {
        mapServiceResponse = new Map<String,String>();

        if(sServiceName.equalsIgnoreCase(HUM_Section_Priority__c.getInstance('Medication Therapy (MTM)').Name))
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Eligible').Name,'');
        else
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Eligible').Name,sEligibleNo);
        mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sNoValue);

        if(sServiceName.equalsIgnoreCase(HUM_Section_Priority__c.getInstance('Extra Help').Name) && (!bIsPharmacyUser || Pharmacy_H_HUM.isHPTraditionalInsurancePSUser() == true))
        {
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,'');
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,'');
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,'');
        }
        else
        {
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,sNoValue);
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,sNoValue);
            mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,sNoValue);   
        }
        mapEligibilityInfo.put(sServiceName,mapServiceResponse);
    }
    
    /*
     * Method Name   :    callLISService
     * Description   :    Method to call Service for LIS
     * Return Type   :    void
     * Parameters    :    void
    */    
    @testvisible private void callLISService(HUMCIMedicareMemberResponseHelper oResponseLISService)
    {
        mapServiceResponse = new Map<String,String>();
        String sLISEligible = '';
    
        try
        {
            string sPercentCopay = '';
            if(!String.isBlank(oResponseLISService.sLISSubsidyLevel))
            {
                sPercentCopay = oResponseLISService.sLISSubsidyLevel;
                if(!String.isBlank(oResponseLISService.sLISCopayCategory))
                {
                    sPercentCopay = sPercentCopay + '/ '+ oResponseLISService.sLISCopayCategory;
                }
            }
            If (oResponseLISService!= NULL && oResponseLISService.Errors.size() == 0)
            {
                if(!String.isBlank(oResponseLISService.sLISIndicator) && (oResponseLISService.sLISIndicator.equalsIgnoreCase(sCharY)))
                {
                    sLISEligible = sEligibleYesValue;
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,sPercentCopay);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sNoValue);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,oResponseLISService.sSubsidyEffectDate); 
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,oResponseLISService.sSubsidyEndDate);
                }  
                else 
                {
                    sLISEligible = sEligibleNoValue;
                    if(Pharmacy_H_HUM.isHPTraditionalInsurancePSUser() == true)
                    {
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,'');
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sNoValue);
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,''); 
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,'');
                    }
                    else{                    
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,sNoValue);
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sNoValue);
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,sNoValue); 
                      mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,sNoValue);
                    }
                 }
            
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Eligible').Name,sLISEligible);
                
                mapEligibilityInfo.put('Extra Help',mapServiceResponse);
            }
            else
            {
                populateNoServiceResponse(HUM_Section_Priority__c.getInstance('Extra Help').Name); 
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs =HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'callLISService');
        }
    } 


    /*
     * Method Name   :    callLTSSService
     * Description   :    Method to call Service for LTSS
     * Return Type   :    void  
     * Parameters    :    void
    */    
    @testvisible private void callLTSSService(HUMCIMedicareMemberResponseHelper oResponseLTSS)
    {
        mapServiceResponse = new Map<String,String>();
        String sEligible = '';
            
        try
        {
            If (NULL!= oResponseLTSS && oResponseLTSS.Errors.size() == 0)
            {
                if(!String.isBlank(oResponseLTSS.sLTSSIndicator) && (oResponseLTSS.sLTSSIndicator.equalsIgnoreCase(sCharY)))
                {
                    sEligible = sEligibleYesValue;  
                }
                else     
                {
                    sEligible = sEligibleNoValue; 
                }
                
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Eligible').Name,sEligible);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sNoValue);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,sNoValue);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,sNoValue);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,sNoValue);
            
                mapEligibilityInfo.put(HUM_Section_Priority__c.getInstance('Long Term Support Services (LTSS)').Name, mapServiceResponse);
            }
            else    
            {
                populateNoServiceResponse(HUM_Section_Priority__c.getInstance('Long Term Support Services (LTSS)').Name);    
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs =HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'callLTSSService');
        }
    }
    
    /*
     * Method Name   :    callESRDService
     * Description   :    Method to call Service for ESRD
     * Return Type   :    void
     * Parameters    :    void
    */    
    @testvisible private void callESRDService(HUMCIMedicareMemberResponseHelper oESRDResponse)
    {
        mapServiceResponse = new Map<String,String>();
        String sEligible = '';
        try
        {
            If (NULL!= oESRDResponse && oESRDResponse.Errors.size() == 0)
            {
                if(!String.isBlank(oESRDResponse.sESRDIndicator) && (oESRDResponse.sESRDIndicator.equalsIgnoreCase(sCharY)))
                {
                    sEligible = sEligibleYesValue;
                }
                else 
                {
                    sEligible = sEligibleNoValue;
                }
            
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Eligible').Name,sEligible);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,sNoValue);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,sNoValue);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sNoValue);
                mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,sNoValue);
                
                mapEligibilityInfo.put(HUM_Section_Priority__c.getInstance('End Stage Renal Disease (ESRD)').Name,mapServiceResponse);  
            } 
            else
            {
                populateNoServiceResponse(HUM_Section_Priority__c.getInstance('End Stage Renal Disease (ESRD)').Name);    
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs =HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'callESRDService');
        } 
    } 

    /*
     * Method Name   :    callSNPService
     * Description   :    Method to call Service for SNP
     * Return Type   :    void
     * Parameters    :    instance of Policy Member object type
    */    
    @testvisible private void callSNPService(Policy_Member__c policyMemberInstance, HUMCIMedicareMemberResponseHelper oResponseSNP)
    {
        if(Null != policyMemberInstance)
        {
            mapServiceResponse = new Map<String,String>();
            
            String sEligible = '';
            try
            {
                If (NULL!= oResponseSNP && oResponseSNP.Errors.size() == 0)
                {
                    if((!String.isBlank(oResponseSNP.sSNPIndicator)) && (oResponseSNP.sSNPIndicator.equalsIgnoreCase(sCharY))) 
                    {
                        sEligible = sEligibleYesValue;
                        sType = oResponseSNP.sSNPTypeCode;
                        mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sType);
                        
                    }   
                    else 
                    {
                        sEligible = sEligibleNoValue;
                        sType = sNoValue;
                        mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sType);
                    }
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Eligible').Name,sEligible);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,sNoValue);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,sNoValue);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,sNoValue);
                        
                    mapEligibilityInfo.put(HUM_Section_Priority__c.getInstance('Special Needs Plan (SNP)').Name,mapServiceResponse);
                }
                else
                {    
                    populateNoServiceResponse(HUM_Section_Priority__c.getInstance('Special Needs Plan (SNP)').Name);
                }
            }
            catch(Exception ex)
            {
                 HUMExceptionHelper.bRestrictLog=true;
                 listErrorLogs =HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'callSNPService');
            } 
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * To call the get member service
     *
     * @param  
     * @return Continuation
     */
    public Continuation callGetMemberService(Continuation con)
    {
        string netWorkValue = ApexPages.currentPage().getParameters().get('userNetworkId');
         netWorkValue = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(netWorkValue) : netWorkValue;
        Policy_Member__c oPolMem = [Select Id, Member__c From Policy_Member__c Where Id =: policyMemberInstance.Id];
        Account acc = [Select Id, Enterprise_Id__c, Birthdate__c from Account Where Id =: oPolMem.Member__c];
        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
        HttpRequest httpRSGetMemberReqObj = builtHttpRequest(RSServiceDetails);
        httpRSGetMemberReqObj.setBody(JSON.serialize(RequestStructureGetMemberResponse(acc.Enterprise_Id__c,netWorkValue)));
        sMTMReq = con.addHttpRequest(httpRSGetMemberReqObj);
        sRecordId = acc.id;
        return con;
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * retrieveMTMValue Method to parse the service response
     *
     * @param  
     * @return Object
     */
    public Object retrieveMTMValue()
    {
        RightSourceGetMemberResponse_DTO_HUM.CustomerDetails oResponse = new RightSourceGetMemberResponse_DTO_HUM.CustomerDetails();
        HttpResponse getMemberMTMResponse = Continuation.getResponse(sMTMReq);
        if (getMemberMTMResponse != null && ((getMemberMTMResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || getMemberMTMResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) && (!getMemberMTMResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
        {
            oResponse = prepareMTMDetails((RightSourceGetMemberResponse_DTO_HUM)System.JSON.deserialize(getMemberMTMResponse.getBody(),RightSourceGetMemberResponse_DTO_HUM.class));                        
            
            mapServiceResponse = new Map<String,String>();
            
            String sEligible ='';
            try
            {
                If (NULL != oResponse)
                {
                    if(!String.isBlank(oResponse.CMRStatus))
                    {
                        if(oResponse.CMRStatus.equalsIgnoreCase('Yes') || oResponse.CMRStatus.equalsIgnoreCase('Do Not Contact') || oResponse.CMRStatus.equalsIgnoreCase('Completed'))
                        {
                            sEligible = sEligibleYesValue;                        
                        }   
                        else if(oResponse.CMRStatus.equalsIgnoreCase('No')) 
                        {
                            sEligible = sEligibleNoValue;    
                        }
                    }
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Eligible').Name,sEligible);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Type').Name,sNoValue);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Percent/ Copay').Name,sNoValue);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('Effective Date').Name,sNoValue);
                    mapServiceResponse.put(HUM_Field_Priority__c.getInstance('End Date').Name,sNoValue);
                        
                    mapEligibilityInfo.put(HUM_Section_Priority__c.getInstance('Medication Therapy (MTM)').Name,mapServiceResponse);
                }
                else
                {    
                    populateNoServiceResponse(HUM_Section_Priority__c.getInstance('Medication Therapy (MTM)').Name);
                }
            }
            catch(Exception ex)
            {
                 HUMExceptionHelper.logErrors(ex, 'HUMEligibilityInfoController', 'retrieveMTMValue');
            } 
            
        }
        
        Return null;
    }
    
    /**
     * Returns RightSourceGetMemberResponse_DTO_HUM.CustomerDetails for CRMStatus details.
     * This method is to create the data for MTM from GetMemberResponseservice .
     *
     * @param    
     * @return  RightSourceGetMemberResponse_DTO_HUM.CustomerDetails which contains CRMStatus
     */
    public RightSourceGetMemberResponse_DTO_HUM.CustomerDetails prepareMTMDetails(RightSourceGetMemberResponse_DTO_HUM oResponseGetMember)
    {
        RightSourceGetMemberResponse_DTO_HUM.MemberReponse oResponse = new RightSourceGetMemberResponse_DTO_HUM.MemberReponse();
        if (oResponseGetMember != null && oResponseGetMember.GetMemberReponse != null)
        {
            RightSourceGetMemberResponse_DTO_HUM.MemberReponse memberRes = oResponseGetMember.GetMemberReponse;
            if (memberRes.Customers != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.CustomersDetail custObj = memberRes.Customers;
                if (custObj.Customer != null && custObj.Customer.size() > 0)
                {
                    if (custObj.Customer[0].CustomerDetail != null)
                    {
                        RightSourceGetMemberResponse_DTO_HUM.CustomerDetails custDetail = custObj.Customer[0].CustomerDetail;
                        return custDetail;  
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Returns RightSourceGetMemberRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetMemberResponseservice .
     *
     * @param   String      Enterprice id 
     * @return  RightSourceGetMemberRequest_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
     */
    public RightSourceGetMemberRequest_DTO_HUM RequestStructureGetMemberResponse(String memID, string networkID)
    {
        RightSourceGetMemberRequest_DTO_HUM oRequest = new RightSourceGetMemberRequest_DTO_HUM();
        RightSourceGetMemberRequest_DTO_HUM.MemberRequest memRequest = new RightSourceGetMemberRequest_DTO_HUM.MemberRequest();
        memRequest.EnterprisePersonID = memID;
        memRequest.IncludeActiveLogNotes ='false';
        memRequest.IncludeActiveAddresses = 'false';
        memRequest.IncludeInactiveAddresses = 'false';
        memRequest.IncludeFinances = 'false';
        memRequest.IncludeAccountAdjustments = 'false';
        memRequest.IncludeConsentStatus = 'false';
        memRequest.IncludeActiveCreditCards = 'false';
        memRequest.IncludeInactiveCreditCards = 'false';
        memRequest.IncludeStaleData = 'false';
        memRequest.IncludeCmrStatus = 'true';
        memRequest.LogNoteStartDate = '';
        memRequest.LogNoteEndDate = '';
        memRequest.CustomerServiceId = networkID;
        oRequest.GetMemberRequest = memRequest;
        return oRequest;
    }
    
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, ServiceObj.Content_Type__c);
            request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM, GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM,'');
        }
        return request;
    }
     /*
    * Method to retrieve Care Manager Details
    * @param 
    * @return Continuation
    */
    public Continuation retrieveCareManagerDetailById()
    {
        try
        {
            HUM_Webservice_Callout__c oCareMgr = HUM_Webservice_Callout__c.getValues('Clinical_CareManagerDetailsByMemberId');
            integer iTIMEOUT_INT_SECS = Integer.valueof(oCareMgr.Timeout__c) / 10000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'processManagerDetail';
            HttpRequest req = new HttpRequest();
            req.setMethod(oCareMgr.Request_Type__c);  
            req.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oCareMgr.Content_Type__c);
            req.setTimeout(iTIMEOUT_INT_SECS);
            req.setEndpoint(oCareMgr.End_Point_URL__c);      
            if (!Test.isRunningTest() && String.isNotBlank(oCareMgr.Certificate_Name__c)) req.setClientCertificateName(oCareMgr.Certificate_Name__c);
            ClinicalCareMngrDetailsByMbrIdReq_H_HUM oRequest = new ClinicalCareMngrDetailsByMbrIdReq_H_HUM();
            ClinicalCareMngrDetailsByMbrIdReq_H_HUM.CareManagerbyMemberIdRequest cmgrRequest = new ClinicalCareMngrDetailsByMbrIdReq_H_HUM.CareManagerbyMemberIdRequest();
            Policy_Member__c oPolMem = [Select Id, Member__c From Policy_Member__c Where Id =: policyMemberInstance.Id];
            if(oPolMem!=null){
            acnt = [Select Id, Enterprise_Id__c, Birthdate__c from Account Where Id =: oPolMem.Member__c];}
            if(acnt!= null){
            cmgrRequest.PersonID = acnt.Enterprise_Id__c;
            cmgrRequest.PersonIDType = 'SdrPersonID';
            cmgrRequest.DateOfBirth = acnt.Birthdate__c;
            }
            oRequest.CareManagerbyMemberIdRequest = cmgrRequest;
            string sbody = JSON.serialize(oRequest);
            req.setBody(sbody);
            sLTSSReq = con.addHttpRequest(req);
            return con;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMEligibilityInfoController', 'retrieveCareManagerDetailById');
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.CareManager_ERROR_HUM));
            return null;
        }
    }
    
    /*
    * Method to call service and parse the response
    * @param 
    * @return Object
    */
    public object processManagerDetail()
    {
        try
        {   
            HttpResponse response = Continuation.getResponse(sLTSSReq);
             if(response.getStatusCode() == 200 || response.getStatusCode() == 202)
            {
                bTable= true;
                ClinicalCareMngrDetailsByMbrIdResp_H_HUM oResponse = new ClinicalCareMngrDetailsByMbrIdResp_H_HUM();
                oResponse = (ClinicalCareMngrDetailsByMbrIdResp_H_HUM)System.JSON.deserialize(response.getbody(),ClinicalCareMngrDetailsByMbrIdResp_H_HUM.class);
                List<HumCareManager__c> ManagerData= HumCareManager__c.getall().values();
                if(oResponse!=null){                           
                        ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManagerDetails objCareManagerDetail = new ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManagerDetails();
                        objCareManagerDetail = oResponse.CareManagerbyMemberIdResponse.CareManagerDetails;
                        objCareManagerList = new  List<ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager>();
                        objCareManagerList  = objCareManagerDetail.CareManager;
                        list<ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager> resp=new list<ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager>();
                        mngrDetails=new list<ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager>();
                        map<string,ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager> mngrData=new map<string,ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager>();
                        ManagerData.SORT();
                        resp=oResponse.CareManagerbyMemberIdResponse.CareManagerDetails.CareManager; 
                        map<string,boolean>str=new  map<string,boolean>();
                        map<integer,string>strOrder=new  map<integer,string>();
                        for(integer iManager=0;iManager<ManagerData.size();iManager++){
                              str.put(ManagerData[iManager].type__c,false);
                              strOrder.put(iManager+1,ManagerData[iManager].type__c);
                          }
                        for(ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager respData : objCareManagerList ){
                              ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager ObjMangerData= new ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager();
                              ObjMangerData.Type=respData.type;
                              ObjMangerData.Name=respData.Name;
                              ObjMangerData.PhoneNumber = respData.PhoneNumber;
                              if(!String.isBlank(respData.PhoneNumber)){
                                  ObjMangerData.PhoneNumber='('+respData.PhoneNumber.substringBefore('-')+')'+' '+respData.PhoneNumber.substringAfter('-');
                              }
                              mngrData.put(respData.type,ObjMangerData);
                              str.put(respData.type,True);
                         }
                        for(string StrMngerType :str.keyset()){
                          if(!str.get(StrMngerType)){
                           ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager ObjMangerData= new ClinicalCareMngrDetailsByMbrIdResp_H_HUM.CareManager();
                              ObjMangerData.Type=StrMngerType;
                              ObjMangerData.Name='';
                              ObjMangerData.PhoneNumber='';
                              mngrData.put(StrMngerType,ObjMangerData);
                          }
                        }
                         for(integer intOrder: strOrder.keyset()){
                         mngrDetails.add(mngrData.get(strOrder.get(intOrder)));
                        }
                  }         
             }
            else
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.CareManager_ERROR_HUM));
            }
            return null;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMEligibilityInfoController', 'processManagerDetail');
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, system.label.CareManager_ERROR_HUM));
            return null;
        }
    }
}