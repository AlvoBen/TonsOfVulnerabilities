/**************************************************************************************************************************************************
Apex class Name    :  MemberPlanEligibility_LC_HUM
Version            :  1.0
Function           :  Service Layer for MemberPlanEligibility_LC_HUM
Test Class 		   :  MemberPlanEligibility_LT_HUM
Modification Log: 
Developer Name           				Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ankima Srivastava/Kajal Namdev                                       	06/10/2021                  initial version
* Ankima Srivastava                                                     07/01/2021                  Cost Share protected Field implementation
* Ankima Srivastava                                                     07/22/2021                  Cost Share protected Field Rollback
* Ankima Srivastava                                                     09/13/2021                  Cost Share Implementation Rollback
* Ankima Srivastava                                                     09/20/2021                  Cost Share Implementation Re-Deploy
* Ashish Kumar                                                          01/10/2021                  Changing constants reference
************************************************************************************************************************************************* */
public with sharing class MemberPlanEligibility_LS_HUM {
   
    /**
     * Returns responses of continuation object
     * <p>
     * To call the get member service
     *
     * @param  
     * @return Continuation
     */
    @AuraEnabled (continuation = true)
    public static Continuation callGetMemberService(Continuation con, String MemId)
    {
        string netWorkValue;
        User objUser = MemberPlanEligibility_LD_HUM.userRec();
        If (objUser != null)  netWorkValue = objUser.Network_User_Id__c;
        MemberPlan memObj = MemberPlanEligibility_LD_HUM.getMemberPlan(MemId);
        Account acc = MemberPlanEligibility_LD_HUM.getAccountRecord(memObj.MemberId);
        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
        HttpRequest httpRSGetMemberReqObj = MemberPlanEligibility_LS_HUM.builtHttpRequest(RSServiceDetails);
        httpRSGetMemberReqObj.setBody(JSON.serialize(MemberPlanEligibility_LS_HUM.RequestStructureGetMemberResponse(acc.Enterprise_Id__c,netWorkValue)));
        String sMTMReq = con.addHttpRequest(httpRSGetMemberReqObj);
        return con;
    }
   
 	
     /**
     * Returns HttpRequest request with request body
     * <p>
    * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
     public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
            request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_RSS_SER_CONECTION, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_RSS_SER_KEEPALIVE);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_RSS_SER_SOAPACTION,'');
        }
        return request;
    }
    /**
     * Returns RightSourceGetMemberRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetMemberResponseservice .
     *
     * @param   String      Enterprice id 
     * @return  RightSourceGetMemberRequest_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
     */
    public static RightSourceGetMemberRequest_DTO_HUM RequestStructureGetMemberResponse(String memID, string networkID)
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
     public static HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcHeaderRTI, String sMemberCardID, String sGroupNumber, String sSubSourceKey, String sCurrentDate, String sChar)
    {
        HttpRequest oHTTPRequest = new HttpRequest();    
       
            CIMedicareMultipleMemberRequest_DTO_HUM oRequest = new CIMedicareMultipleMemberRequest_DTO_HUM();
            CIMedicareMultipleMemberRequest_DTO_HUM.GetMemberInfo oGetMemInfo = new CIMedicareMultipleMemberRequest_DTO_HUM.GetMemberInfo();
            List<CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails> lstCI = new List<CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails>();
            CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails oMedicareMember = new CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails();
            oMedicareMember.MedicareID = '';
            oMedicareMember.MemberCardID = sMemberCardID;
            oMedicareMember.GroupID = sGroupNumber;
            oMedicareMember.BenefitSequenceNumber = sSubSourceKey;
            oMedicareMember.AsOfDate = sCurrentDate;
            oMedicareMember.LISInfo = oMedicareMember.LTSSInfo = oMedicareMember.ESRDInfo = GLOBAL_CONSTANT_LH_HUM.HUMCharY;
            oMedicareMember.SPAPInfo = oMedicareMember.POAInfo = GLOBAL_CONSTANT_LH_HUM.HUMCharN;
            oMedicareMember.SNPInfo = sChar;
            lstCI.add(oMedicareMember);
            
            oGetMemInfo.MemberDetails = lstCI;
            oRequest.GetMemberInfo = oGetMemInfo;
            String sReqBody = Json.serialize(oRequest);
            if(String.isNotBlank(sReqBody))
            {
                request.setMethod(oSvcHeaderRTI.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_LH_HUM.CONTENT_TYPE_HUM, oSvcHeaderRTI.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_LH_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_LH_HUM.KEEP_ALIVE_HUM);
                if(!Test.isRunningTest())    
                {
                    request.setClientCertificateName(oSvcHeaderRTI.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcHeaderRTI.Timeout__c));
                request.setEndpoint(oSvcHeaderRTI.End_Point_URL__c);
                request.setBody(sReqBody);
                oHTTPRequest = request;
            } 
        
        
        return oHTTPRequest; 
    }
    /*
    *  This method is used to build Json request for MBEPlusService 
    *  @name   callMBEPlusService
    *  @param  Object, List
    *  @return string - Object
    */
    @AuraEnabled (continuation = true)
    public static Continuation callMBEPlusService(Continuation con, List<MemberPlan> lstMemberPlan)
    {
        if(!lstMemberPlan.IsEmpty())
            {
                String sPersonId = (lstMemberPlan[0].Member.Enterprise_Id__c != Null) ? lstMemberPlan[0].Member.Enterprise_Id__c : ''; 
                HUM_Webservice_Callout__c oSvcEncrypt = HUM_Webservice_Callout__c.getValues('MBEPLUSServiceREST'); 
                HttpRequest request = new HttpRequest();
                request = MemberPlanDetail_LS_HUM.setupDualStatusServiceRequest(request, oSvcEncrypt,sPersonId);
                con.addHttpRequest(request);
                return con;
            }
         else
            {
               return con;          
            }
    }


}