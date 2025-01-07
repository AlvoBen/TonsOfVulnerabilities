/*****************************************************************************************
Apex class Name    : EligibilityInformation_LS_HUM
Version            : 1.0
Function           : service layer for retrieving eligibility info
Created Date       : 03/12/2021
Test Class         : EligibilityInformation_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Joel George                                            05/03/2021                  initial version
*Joel George                                            05/17/2021                  Added Policy Object change
****************************************************************************************************************/

public with sharing class EligibilityInformation_LS_HUM 
{
    private static String sMemberCardId ='';
    private static String sCurrentDate = '';
    private static String sGroupNumber = '';
    private static String sSubSourceKey = '';
    private static final String sCharY = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUMCharY;
    private static final String sCharN = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUMCharN;
    private static final String sDateFormat = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUMDateFormat;
    @TestVisible private static String sRequest ='';


   /*
    * Method Name   :  callCIMedMultipleMemberService
    * Description   :  This method is continuation call for CI Medicare Multiple service.
    * Return Type   :  Continuation
    * Parameters    :  NA
    */  
    @AuraEnabled(continuation=true)
    public static Object callCIMedMultipleMemberService(String recId)
    {
        try
        {
            Datetime dtCurrentDate = Datetime.now();
            sCurrentDate = dtCurrentDate.format(sDateFormat);            
            List<MemberPlan> lstMemberPlan = EligibilityInformation_LD_HUM.getMemberPlan(recId);
            
            if(!lstMemberPlan.IsEmpty())
            {
                sMemberCardId = lstMemberPlan[0].Member_Id_Base__c;
                String sSourceKey = lstMemberPlan[0].Plan.Source_Cust_Cov_Key__c;
                 if(!String.isBlank(sSourceKey) && sSourceKey.length() >= 9)
                {
                    sGroupNumber = sSourceKey.substring(0,6);
                    sSubSourceKey = sSourceKey.substring(6,9);
                }
                
            }
            
            if( !String.isBlank(sGroupNumber) && !String.isBlank(sSubSourceKey) && !String.isBlank(sMemberCardId) )    
            {
                HUM_Webservice_Callout__c oSvcHeaderCI = HUM_Webservice_Callout__c.getValues(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.POA_SERVICENAME);
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderCI.Timeout__c) / 10000;
                HttpRequest request = new HttpRequest();
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                request = setupRequest(request, oSvcHeaderCI, sMemberCardID, sGroupNumber, sSubSourceKey, sCurrentDate, sCharY);   
                con.continuationMethod='processAllResponses';
                sRequest = con.addHttpRequest(request);
                return con;
            }   
                
            return false;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'EligibilityInformation_LS_HUM', 'callCIMedMultipleMemberService');
            return null;
        }         
    }
    
     /*
    * Method Name   :  processAllResponses
    * Description   :  This method is to handle multiple calls at a time.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled
    public static Object processAllResponses(List<String> labels,object state)
    {
        HUMCIMedicareMemberResponseHelper oResponseCI = new HUMCIMedicareMemberResponseHelper();
        oResponseCI = responseParseCIMedMultiple(labels, state);

        if(oResponseCI.sLISIndicator =='Y')
            return true;
        return false;

    }
    
    /*
    * Method Name   :  setupRequest
    * Description   :  This method is used to frame request for CI service
    * Return Type   :  HttpRequest 
    * Parameters    :  HttpRequest, HUM_Webservice_Callout__c, String, String, String, String, String
    */
    public static HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcHeaderRTI, String sMemberCardID, String sGroupNumber, String sSubSourceKey, String sCurrentDate, String sChar)
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
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CONTENT_TYPE_HUM, oSvcHeaderRTI.Content_Type__c);  
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CONNECTION_HUM,GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.KEEP_ALIVE_HUM);
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
            HUMExceptionHelper.logErrors(ex, 'EligibilityInformation_LS_HUM', 'setupRequest');
            oHTTPRequest = null;
        }
        return oHTTPRequest; 
    }
    

    /*
    * Method Name   :  responseParseCIMedMultiple
    * Description   :  This method is used to check response
    * Return Type   :  void 
    * Parameters    :  HttpResponse obj
    */
    @AuraEnabled(continuation=true)
     public static HUMCIMedicareMemberResponseHelper  responseParseCIMedMultiple(List<String> labels,object state)
    {   
        HttpResponse sResponse = Continuation.getResponse(labels[0]);   
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_OK_HUM || statusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)
        {
            HUMCIMedicareMemberResponseHelper oResponseCI = new HUMCIMedicareMemberResponseHelper();
            oResponseCI = parseCIResponse(sResponse.getBody()); 
            return oResponseCI;

        }
        return null;
    }
    
    /*
    * Method Name   :  parseCIResponse
    * Description   :  This method is used to check response
    * Return Type   :  void 
    * Parameters    :  String
    */
    public static HUMCIMedicareMemberResponseHelper parseCIResponse(String sResponse)
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

   
}