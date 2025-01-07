/**************************************************************************************************************************************************
Apex class Name    :  MemberPlanEligibility_LC_HUM
Version            :  1.0
Function           :  controller for Eligibility section in Member Plan
Test Class 		   :  MemberPlanEligibility_LT_HUM
Modification Log: 
Version Number 						Developer Name           				Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------
* 	1.0							Ankima Srivastava/Kajal Namdev                                        06/10/2021                  initial version
* 	2.0							Ankima Srivastava                                                     06/24/2021                  DF-3283	
* 	3.0							Ankima Srivastava                                                     06/29/2021                  Switch Implementation for Eligibility	
* 	4.0							Ankima Srivastava                                                     06/29/2021                  Correction in Switch Implementation for Eligibility
* 	5.0							Ankima Srivastava                                                     07/01/2021                  Cost Share Protected Field Implementation
* 	6.0							Ankima Srivastava                                                     07/09/2021                  Eligibility Fix for LIS
* 	7.0							Ankima Srivastava                                                     07/19/2021                  UAT Defect Fix for MTM
*	8.0							Ankima Srivastava                                                     07/26/2021                  Rollback for Cost Share Protection
*	9.0							Ankima Srivastava                                                     08/09/2021                  Constant Changes for MTM section - Eligibility
*	10.0						Ankima Srivastava                                                     09/01/2021                  Changes for Cost Share Protected Field
*	11.0						Ankima Srivastava                                                     09/06/2021                  Changes for Cost Share Protected Field
*	12.0						Kajal Namdev                                                          09/07/2021                  change in retrieveCostShareVal method
*	13.0						Ankima Srivastava                                                     09/08/2021                  Cost Share Changes
*	14.0						Ankima Srivastava                                                     09/08/2021                  Cost Share Rollback
*	15.0						Ankima Srivastava                                                     09/08/2021                  Cost Share Re-Deploy
*	16.0						Ankima Srivastava                                                     09/30/2021                  Eligibility by Product Type Changes
*	17.0						Ankima Srivastava                                                     10/07/2021                  Updates for Eligibility by Product Type Changes
*	18.0 						Nilanjana Sanyal                                    				  10/26/2022                  US-3813080: HUMExceptionHelper remediation for lightning consumer Management classes
*   19.0                        Swapnali Sonawane                                                     09/01/2023                  US: 5012557 Pharmacy - MTM Eligibility
********************************************************************************************************************************************************************************** */
public with sharing class MemberPlanEligibility_LC_HUM {
    public static Map<String,String> mapServiceResponse = NULL; 
    public static String sEligibleYesValue = GLOBAL_CONSTANT_LH_HUM.HUMEligibleYes;
    public static String sEligibleNoValue = GLOBAL_CONSTANT_LH_HUM.HUMEligibleNo;
    public static String sNoValue = GLOBAL_CONSTANT_LH_HUM.HUMNoValue;
    public static Map<String,Map<String,String>> mapEligibilityInfo;
    public static string sMemberCardId;
    public static string sGroupNumber;
    public static string sSubSourceKey;
    public static Map<String,String> stateMap= new Map<String,String>();
    public static Boolean isDualPlan=false;
    
    public static List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
     /*
    * Method Name   :    callCIMedMultipleMemberService
    * Return Type   :    Object
    * Parameters    :    MemberPlan Id
    */
    @AuraEnabled (continuation=true )
    public static object callCIMedMultipleMemberService(string memberPlanId){
        List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
        try{
            callServices(memberPlanId);
            Datetime dtCurrentDate = Datetime.now();
            String sCurrentDate = dtCurrentDate.format(GLOBAL_CONSTANT_LH_HUM.HUMDateFormat);
            HUM_Webservice_Callout__c oSvcHeaderCI = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_LH_HUM.POA_SERVICENAME);
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderCI.Timeout__c) / 10000;
            HttpRequest request = new HttpRequest();
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            if(!HUMUtilityHelper.isCRMFunctionalityON('HPIE Switch')){
                con = MemberPlanEligibility_LS_HUM.callGetMemberService(con,memberPlanId);
            }
            request = MemberPlanEligibility_LS_HUM.setupRequest(request, oSvcHeaderCI, sMemberCardId, sGroupNumber, sSubSourceKey, sCurrentDate, GLOBAL_CONSTANT_LH_HUM.HUMCharY);   
            String sRequest = con.addHttpRequest(request);
            lstMemberPlan =  MemberPlanDetail_LD_HUM.getMemberName(memberPlanId);
            con = MemberPlanEligibility_LS_HUM.callMBEPlusService(con,lstMemberPlan);
            stateMap.put('sRequest',sRequest);
            stateMap.put('sGroupNumber',sGroupNumber);
            stateMap.put('sSubSourceKey',sSubSourceKey);
            stateMap.put('sMemberPlanId',memberPlanId);
            con.state=stateMap;
            con.continuationMethod='processAllResponses';
            return con;
        }catch(Exception e){
             
            HUMExceptionHelper.logErrors(e, 'MemberPlanEligibility_LC_HUM', 'callCIMedMultipleMemberService');
            return null;
        }
        
    }
    
    
    /*
    * Method Name   :    callServices
    * Return Type   :    NA
    * Parameters    :    MemberPlan Id
    * Description   :	 Fetching values for the required input variables
    */
    public static void callServices(string memberPlanId)
    {       
        try{
            Map<String,String> memberPlanMap = new Map<String,String>();
            List<MemberPlan> lstPM = MemberPlanEligibility_LD_HUM.getMemberPlanRecords(memberPlanId);
            if (lstPM.size() > 0)
            {
                if(String.isNotBlank(lstPM[0].Member_Id_Base__c))    
                {
                    sMemberCardId = lstPM[0].Member_Id_Base__c;
                }  
            }
            
            MemberPlan memberPlanObj = MemberPlanEligibility_LD_HUM.getMemberPlan(memberPlanId);
            String sSourceKey = memberPlanObj.Plan.Source_Cust_Cov_Key__c;
            if(!String.isBlank(sSourceKey) && sSourceKey.length() >= 9)
            {
                sGroupNumber = sSourceKey.substring(0, 6);
                sSubSourceKey = sSourceKey.substring(6, 9);
            }
        }catch(Exception e){
            
            HUMExceptionHelper.logErrors(e, 'MemberPlanEligibility_LC_HUM', 'callServices');
        }
        
    } 
    
    /*
    * Method Name   :    processAllResponses
    * Return Type   :    Wrapper
    * Parameters    :    List<String>, Object
    * Description	:	Preparing Wrapper which includes section, fields and values
    */
    @AuraEnabled (continuation=true )
    public static EligibilityWrapper_DTO_HUM processAllResponses(List < string > labels, Object state)
    {
        EligibilityWrapper_DTO_HUM eligibilityObj = new EligibilityWrapper_DTO_HUM();
        try{
            Map<String,String> mapState=(map<String, String>)state;
            mapEligibilityInfo = new Map<String,Map<String,String>>();
            if(HUMUtilityHelper.isCRMFunctionalityON('2081552')){
                eligibilityObj.EligibilitySwitch=true;
                eligibilityObj.showEligibilitySections = MemberPlanEligibility_LD_HUM.showEligibSections(mapState.get('sMemberPlanId')); //new node for Eligibility Product Type scenerio
                if(eligibilityObj.showEligibilitySections){ 
                    HttpResponse response = Continuation.getResponse(mapState.get('sRequest'));
                    eligibilityObj.EligibilitySection = MemberPlanEligibility_LD_HUM.getEligibitySections();
                    eligibilityObj.EligibilityField = MemberPlanEligibility_LD_HUM.getEligibityFields();
                    if(response != null){
                        mapEligibilityInfo = responseParseCIMedMultiple(response,mapState.get('sGroupNumber'),mapState.get('sSubSourceKey'));
                    }
                    if(!HUMUtilityHelper.isCRMFunctionalityON('HPIE Switch')){
                        mapEligibilityInfo = retrieveMTMValue(labels,State);
                    }
                    eligibilityObj.EligibilityServiceResponse = mapEligibilityInfo;
                }
            }else{
                eligibilityObj.EligibilitySwitch=false;
            }
            if(HUMUtilityHelper.isCRMFunctionalityON('2160875')){ 
                 eligibilityObj.showCostShare=MemberPlanEligibility_LD_HUM.showCostShare(mapState.get('sMemberPlanId'));
                 eligibilityObj.costShareInfo = processResponseDualStatus(labels,State); 
            }else{
                 eligibilityObj.showCostShare=false;
                 eligibilityObj.showContractInfo = new List<String>();
            }
            List<String> contractSections = new List<String>();
            contractSections = MemberPlanEligibility_LD_HUM.showContractInfoSection(mapState.get('sMemberPlanId'));
            if(contractSections.Contains('checkDualPlan')){
               contractSections.clear();
               if(isDualPlan){
                    contractSections.add(System.Label.HUMSegmentCode);
                    contractSections.add(System.Label.HUMContractId);
                    contractSections.add(System.Label.pbpCodeHum);
               }
            }
            eligibilityObj.showContractInfo = contractSections; 
            eligibilityObj.MemPlanInfo = MemberPlanEligibility_LD_HUM.getMemberPlan(mapState.get('sMemberPlanId'));
        }catch(Exception e){
            
            HUMExceptionHelper.logErrors(e, 'MemberPlanEligibility_LC_HUM', 'processAllResponses');
        }
        return eligibilityObj;  
    }
    /*
    * Method Name   :    responseParseCIMedMultiple
    * Return Type   :    Map<String,Map<String,String>> 
    * Parameters    :    HttpResponse, String, String
    * Description	:	 Parsing response with respect to different sections
    */
    private static Map<String,Map<String,String>>  responseParseCIMedMultiple(HttpResponse sResponse,String sGroupNumber, String sSubSourceKey)
    {   
        mapEligibilityInfo = new Map<String,Map<String,String>>();
        try{
            Integer statusCode = sResponse.getStatusCode();
            if (statusCode == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM || statusCode == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)
            {
                HUMCIMedicareMemberResponseHelper oResponseCI = new HUMCIMedicareMemberResponseHelper();
                oResponseCI = parseCIResponse(sResponse.getBody());
                mapEligibilityInfo = callESRDService(oResponseCI);
                mapEligibilityInfo = callLISService(oResponseCI);
                mapEligibilityInfo = callLTSSService(oResponseCI);
               
                if(!String.isBlank(sGroupNumber) && !String.isBlank(sSubSourceKey)) { 
                    mapEligibilityInfo= callSNPService(oResponseCI);
                }
            }
        }catch(Exception e){
             
            HUMExceptionHelper.logErrors(e, 'MemberPlanEligibility_LC_HUM', 'responseParseCIMedMultiple');
        }
        return mapEligibilityInfo;
    }
     /*
    * Method Name   :    callESRDService
    * Return Type   :    Map<String,Map<String,String>> 
    * Parameters    :    Response
    * Description	:	 preparing map fpr ESRD section
    */
    private static Map<String,Map<String,String>> callESRDService(HUMCIMedicareMemberResponseHelper oESRDResponse)
    {
        mapServiceResponse = new Map<String,String>();
        mapEligibilityInfo = new Map<String,Map<String,String>>();
        String sEligible = '';
        try
        {
            If (NULL!= oESRDResponse && oESRDResponse.Errors.size() == 0)
            {
                if(!String.isBlank(oESRDResponse.sESRDIndicator) && (oESRDResponse.sESRDIndicator.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.HUMCharY)))
                {
                    sEligible = sEligibleYesValue;
                }
                else 
                {
                    sEligible = sEligibleNoValue;
                }
                
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Eligible,sEligible);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,sNoValue);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,sNoValue); 
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,sNoValue);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,sNoValue);
                mapEligibilityInfo.put(GLOBAL_CONSTANT_LH_HUM.ESRD,mapServiceResponse);  
            } 
            else
            {
                populateNoServiceResponse(GLOBAL_CONSTANT_LH_HUM.ESRD);    
            }
            
        }
        catch(Exception ex)
        {
             
            HUMExceptionHelper.logErrors(ex, 'MemberPlanEligibility_LC_HUM', 'callESRDService');
        }
        return mapEligibilityInfo;
    }
     /*
    * Method Name   :    callLTSSServic
    * Return Type   :    Map<String,Map<String,String>> 
    * Parameters    :    Response
    * Description	:	 preparing map for LTSS section
    */    
    @testvisible private static Map<String,Map<String,String>> callLTSSService(HUMCIMedicareMemberResponseHelper oResponseLTSS)
    {
        mapServiceResponse = new Map<String,String>();
        String sEligible = '';
        try
        {
            If (NULL!= oResponseLTSS && oResponseLTSS.Errors.size() == 0)
            {
                if(!String.isBlank(oResponseLTSS.sLTSSIndicator) && (oResponseLTSS.sLTSSIndicator.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.HUMCharY)))
                {
                    sEligible = sEligibleYesValue;
                }
                else     
                {
                    sEligible = sEligibleNoValue;
                }
                
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Eligible,sEligible);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,sNoValue);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,sNoValue);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,sNoValue);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,sNoValue);
                mapEligibilityInfo.put(GLOBAL_CONSTANT_LH_HUM.LTSS, mapServiceResponse);
            }
            else    
            {
                populateNoServiceResponse(GLOBAL_CONSTANT_LH_HUM.LTSS);    
            }
        }
        catch(Exception ex)
        {
            
            HUMExceptionHelper.logErrors(ex, 'MemberPlanEligibility_LC_HUM', 'callLTSSService');
        }
        return mapEligibilityInfo;
    }
     /*
    * Method Name   :    callLISServic
    * Return Type   :    Map<String,Map<String,String>> 
    * Parameters    :    Response
    * Description	:	 preparing map for LIS section
    */    
    @testvisible private static Map<String,Map<String,String>> callLISService(HUMCIMedicareMemberResponseHelper oResponseLISService)
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
                if(!String.isBlank(oResponseLISService.sLISIndicator) && (oResponseLISService.sLISIndicator.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.HUMCharY)))
                {
                    sLISEligible = sEligibleYesValue;
                    
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,sPercentCopay);
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,sNoValue);
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,oResponseLISService.sSubsidyEffectDate); 
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,oResponseLISService.sSubsidyEndDate);
                }  
                else 
                {
                    sLISEligible = sEligibleNoValue;
                    
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,'');
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,sNoValue);
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,''); 
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,'');
                }
                
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Eligible,sLISEligible);
                mapEligibilityInfo.put(GLOBAL_CONSTANT_LH_HUM.LIS,mapServiceResponse);
            }
            else
            {
                populateNoServiceResponse(GLOBAL_CONSTANT_LH_HUM.LIS); 
            }
        }
        catch(Exception ex)
        {
            
            HUMExceptionHelper.logErrors(ex, 'MemberPlanEligibility_LC_HUM', 'callLISService');
        }
        return mapEligibilityInfo;
    }
     /*
    * Method Name   :    callSNPServic
    * Return Type   :    Map<String,Map<String,String>> 
    * Parameters    :    Response
    * Description	:	 preparing map for SNP section
    */    
    @testvisible private static Map<String,Map<String,String>> callSNPService(HUMCIMedicareMemberResponseHelper oResponseSNP)
    {
        
        mapServiceResponse = new Map<String,String>();
        
        String sEligible = '';
        String sType = '';
        try
        {
            If (NULL!= oResponseSNP && oResponseSNP.Errors.size() == 0)
            {
                if((!String.isBlank(oResponseSNP.sSNPIndicator)) && (oResponseSNP.sSNPIndicator.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.HUMCharY))) 
                {
                    sEligible = sEligibleYesValue;
                    sType = oResponseSNP.sSNPTypeCode;
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,sType);
                    
                }   
                else 
                {
                    sEligible = sEligibleNoValue;
                    sType = '';
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,'');
                }
                
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Eligible,sEligible);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,sNoValue);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,sNoValue);
                mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,sNoValue);
                
                mapEligibilityInfo.put(GLOBAL_CONSTANT_LH_HUM.SNP,mapServiceResponse);
            }
            else
            {    
                populateNoServiceResponse(GLOBAL_CONSTANT_LH_HUM.SNP);
            }
        }
        catch(Exception ex)
        {
            
            HUMExceptionHelper.logErrors(ex, 'MemberPlanEligibility_LC_HUM', 'callSNPService');
        }
        return mapEligibilityInfo;
        
    }
     /*
    * Method Name   :    retrieveMTMValue
    * Return Type   :    Map<String,Map<String,String>> 
    * Parameters    :    List<Label>, Object
    * Description	:	 preparing map for MTM section
    */
    public static Map<String,Map<String,String>> retrieveMTMValue(List < string > labels, Object State)
    {
        Map<String,String> mapState=(map<String, String>)state;
        RightSourceGetMemberResponse_DTO_HUM.CustomerDetails oResponse = new RightSourceGetMemberResponse_DTO_HUM.CustomerDetails();
        HttpResponse getMemberMTMResponse = Continuation.getResponse(labels[0]);
        try
        {
            if (getMemberMTMResponse != null && ((getMemberMTMResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM || getMemberMTMResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM) && (!getMemberMTMResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                oResponse = prepareMTMDetails((RightSourceGetMemberResponse_DTO_HUM)System.JSON.deserialize(getMemberMTMResponse.getBody(),RightSourceGetMemberResponse_DTO_HUM.class));    
                mapServiceResponse = new Map<String,String>();
                
                String sEligible = sEligibleNoValue;
                
                If (NULL != oResponse)
                {
                    if(!String.isBlank(oResponse.CMRStatus))
                    {
                        if(oResponse.CMRStatus.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.HUMEligibleYes) || oResponse.CMRStatus.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.Do_Not_Contact) || oResponse.CMRStatus.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.MTMCompleted))
                        {
                            sEligible = sEligibleYesValue; 
                        }   
                        else if(oResponse.CMRStatus.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.HUMEligibleNo)) 
                        {
                            sEligible = sEligibleNoValue;    
                        }
                    }
                    
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Eligible,sEligible);
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,sNoValue);
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,sNoValue);
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,sNoValue);
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,sNoValue);
                    
                    mapEligibilityInfo.put(GLOBAL_CONSTANT_LH_HUM.MTM,mapServiceResponse);
                }
                else
                {    
                    populateNoServiceResponse(GLOBAL_CONSTANT_LH_HUM.MTM);
                }
            }else{
                 populateNoServiceResponse(GLOBAL_CONSTANT_LH_HUM.MTM);
            }
            
            
        }
        catch(Exception ex)
        {
			HUMExceptionHelper.logErrors(ex, 'MemberPlanEligibility_LC_HUM', 'retrieveMTMValue');
        } 
        Return mapEligibilityInfo;
    }
       
    /**
    * Returns RightSourceGetMemberResponse_DTO_HUM.CustomerDetails for CRMStatus details.
    * This method is to create the data for MTM from GetMemberResponseservice .
    *
    * @param    
    * @return  RightSourceGetMemberResponse_DTO_HUM.CustomerDetails which contains CRMStatus
    */
    public static RightSourceGetMemberResponse_DTO_HUM.CustomerDetails prepareMTMDetails(RightSourceGetMemberResponse_DTO_HUM oResponseGetMember)
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
    
    /*
    * Method Name   :    populateNoServiceResponse
    * Description   :    Method to populate the map if there is no response from the service. 
    * Return Type   :    void  
    * Parameters    :    section name
    */
     Public static void populateNoServiceResponse(String sServiceName)
    {
        mapServiceResponse = new Map<String,String>();
        mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Eligible,GLOBAL_CONSTANT_LH_HUM.HUMEligibleNo);
        if(sServiceName.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.SNP)){
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,'');           
        }else{
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.SNPType,sNoValue);
        }
        if(sServiceName.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.LIS)){
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,'');
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,'');
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,'');
        }else{
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Percent_Copay,sNoValue);
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.Effective_Date,sNoValue);
            mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.End_Date,sNoValue);
        }
        mapEligibilityInfo.put(sServiceName,mapServiceResponse);
    }
   
    
      /*
    * Method Name   :    processResponseDualStatus
    * Description   :    Method to retrieve yes/no for cost share value from the service. 
    * Return Type   :    String  
    * Parameters    :    List, Object
    */
    public static String processResponseDualStatus(List < string > labels, Object State)
    {
        AdditionalResponseWrapper_DTO_HUM returnWrapper = new AdditionalResponseWrapper_DTO_HUM();
        String isCostShare=GLOBAL_CONSTANT_LH_HUM.HUMEligibleNo;
        try 
        { 
            Map<String,String> mapState=(map<String, String>)state;
           
            if(mapState != null){
                HttpResponse sResponse = Continuation.getResponse(labels[2]);
                if(sResponse != null && (sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM && !sResponse.getbody().contains(GLOBAL_CONSTANT_HUM.FAULTCODE_ALERTS_HUM)))
                { 
                   DualEligibleDetailsResponse_H_HUM outResponse = (DualEligibleDetailsResponse_H_HUM)System.JSON.deserialize(sResponse.getbody(), DualEligibleDetailsResponse_H_HUM.class);                                             
                    if(outResponse != null && outResponse.GetMemberResponse != null && outResponse.GetMemberResponse.Body != null ){
                        DualEligibleDetails_DTO_HUM oMoopDetailParse = new DualEligibleDetails_DTO_HUM();
                        DualEligibleDetailsResponse_H_HUM.GetMemberResponse oMoopDetailResponse = outResponse.GetMemberResponse;
                        DualEligibleDetailsResponse_H_HUM.Body oMoopDetailBody = oMoopDetailResponse.Body;
                        if (oMoopDetailBody!=null && (oMoopDetailBody.DualStatusList != null && !oMoopDetailBody.DualStatusList.isEmpty()))
                        {
                            Date currentDate = System.today();
                            Date fromDate = currentDate.addMonths(-12);
                            for(DualEligibleDetailsResponse_H_HUM.DualStatusList oDualStatus: oMoopDetailBody.DualStatusList) {
                                Date effectiveDate, endDate;
                                effectiveDate = DualEligibleDetailsHelper_H_HUM.parseDateFormat(oDualStatus.EffectiveDate);
                                endDate = DualEligibleDetailsHelper_H_HUM.parseDateFormat(oDualStatus.EndDate);
                                if (effectiveDate != null && endDate != null)
                                {
                                    if (effectiveDate <= currentDate && endDate >= currentDate) 
                                        {
                                            if(String.valueOf(oDualStatus.Indicator)=='true'){
                                                isCostShare = GLOBAL_CONSTANT_LH_HUM.HUMEligibleYes;
                                            }
                                        }
                                }
                                 if (effectiveDate <= currentDate && endDate >= fromDate) 
                                {
                                    if(oDualStatus.Value != null && !oDualStatus.Value.equalsIgnoreCase('NO STATUS'))
                                    {
                                        isDualPlan = true;
                                    }
                                }
                            }
                        }
                   }    
                }else{
                    return isCostShare;
                }
            }
        }catch(Exception e){
			HUMExceptionHelper.logErrors(e, 'MemberPlanEligibility_LC_HUM' , 'processResponseDualStatus');
            return isCostShare;
        }
        return isCostShare;
    } 
   
    
   
}