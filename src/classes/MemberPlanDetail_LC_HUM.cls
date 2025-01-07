/*******************************************************************************************************************************
Apex Class Name : MemberPlanDetail_LC_HUM
Version         : 1.0
Created On      : 05/01/2021
Function        : This is the controller class for Medicaid Plan Details section on the Policy Detail page
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Ashish Kumar                                           05/01/2021                  Original
*     2.0                    Joel George                                            05/18/2021                  PBE Service
*     3.0                    Joel George                                            06/04/2021                  GBE Service + EDI
*     4.0                    Joel George                                            06/10/2021                  Rollback PBE Service
*     5.0                    Joel George                                            06/25/2021                  Switch Implementation
*     6.0                    Joel George                                            07/01/2021                  Switch Implementation - wrapper changes
*	  7.0					 Kajal Namdev											06/21/2021					Added Other Insurance Service
* 	  9.0					 Muthukumar 											06/16/2023					CRM "Indigenous Indicator" for FL, LA, SC, and IL Medicaid States
*     8.0                    Santhi Mandava                                         06/15/2023                  US-4525682 - Medicaid info enhancements - Mothers medicaid id, admit date and discharge date
******************************************************************************************************************************************************************************/

public with sharing class MemberPlanDetail_LC_HUM {
    
    public static string sMemIdBase;
    public static string sDependentCode;
    public static MemberPlan oPolMem;
    public static Map<String,String> stateMap= new Map<String,String>();
    public static boolean bProductTypeCheck =false;
    public static string sRequest;
    public static MemberAncillaryResponse_DTO_HUM_V2 AncillaryInfo ;
    public static MBEResponseV1_DTO_HUM MbeInfo ;
    
    
    /* Method Name : initiateMbeRequest
* Description : Method to call the service on page load and display the data in Medicaid Policy Details section
* Return type : Object
* Parameter   : MemberPlan Id
*/
    @AuraEnabled(continuation=true)
    public static Object initiateMbeRequest(String memberPlanId) 
    {	
        try
        {	
            MBEPlusRequest_DTO_HUM oMbeReq= new MBEPlusRequest_DTO_HUM();
            Boolean bAncillary=false;
            
            infoSet(memberPlanId);
           
            if((bProductTypeCheck!=null && bProductTypeCheck))
            {    
                //As a part of REQ- 1918663 To fix error - Bulkify Apex Methods Using Collections In Methods - instead of passing Policy_Member__C object, now passing list of Policy_Member__C to the raceStructureMBEService method.
                List<MemberPlan> lstPolMem = new List<MemberPlan>();
                lstPolMem.add(oPolMem);
                
                oMbeReq = MemberPlanBenefits_LS_HUM.raceStructureMBEService(lstPolMem); 
                //method to check if the swtich is on or off and state condtion is matching
                bAncillary=checkSwitchOnOffWithState();
                
                if(oMbeReq!=null && bAncillary)
                {
                    HUM_Webservice_Callout__c oSvcMbe = HUM_Webservice_Callout__c.getValues('MBEPlusService'); 
                    integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcMbe.Timeout__c)/1000;
                    HttpRequest oRequest = MemberPlanBenefits_LS_HUM.builtHttpRequest(oSvcMbe);
                    Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                    oRequest.setBody(JSON.serialize(oMbeReq));
                    sRequest = con.addHttpRequest(oRequest);
                    stateMap.put('sRequest',sRequest);
                    stateMap.put('state',oPolMem.Issue_State__c);
                    stateMap.put('medicaidId',oPolMem.Medicaid_Id__c);
                    con.state=stateMap;
                    con.continuationMethod='processMbeResponse';
                    return con;  
                }
                else{
                    return fetchMedicaidId(oPolMem.Medicaid_Id__c);     
                }
            }
            else
            {	
                return hideMedicaidPlanCondition();
            }   
        } 
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LC_HUM', 'initiateMbeRequest');
            return hideMedicaidPlanCondition();
        }   
    }
    
    /*
* Method Name   :  processMbeResponse
* Description   :  Method is used to get response from Mbe service.
* Return Type   :  Object 
* Parameters    :  Object
*/
    @AuraEnabled
    public static Object processMbeResponse(Object state)
    {   
        Map<String,String> mapState=(map<String, String>)state;
        try 
        {	
            HttpResponse oResponse = Continuation.getResponse(mapState.get('sRequest'));
            if(oResponse != null)
            {
                return mbeResponseparse(oResponse,mapState);
            }
            else
            {
                return fetchMedicaidId(mapState.get('medicaidId'));     
            }
        }
        catch(Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'processMbeResponse');
            return fetchMedicaidId(mapState.get('medicaidId'));     
            
        }
        
    }
    
    /*
* Method Name   :  mbeResponseparse
* Description   :  Method is used to parse response from Mbe Service.
* Return Type   :  Object
* Parameters    :  HttpResponse oResponse and Map<String,String>
*/
    public static Object mbeResponseparse(HttpResponse oResponse, Map<String,String> stateMap)
    {	
        MedicaidPlanDetailsModel_LH_HUM medicaidWrapperMapping= new MedicaidPlanDetailsModel_LH_HUM();
        try	
        {	                        
            MbeInfo = new MBEResponseV1_DTO_HUM();     
            integer iStatusCode = oResponse.getStatusCode();
            if (iStatusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_OK_HUM || iStatusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)
            {    
                MbeInfo = MemberPlanBenefits_LS_HUM.parseMbeRaceresponse(oResponse.getBody(),stateMap.get('state')); 
                medicaidWrapperMapping.mbeData=MbeInfo;
                medicaidWrapperMapping.sMedicaidId= String.isNotBlank(stateMap.get('medicaidId')) ? stateMap.get('medicaidId') : '';
                medicaidWrapperMapping.mappingVsLabel=MedicaidPlanDetailsModel_LH_HUM.mbeResponseMapping(stateMap.get('state'));
                
                return medicaidWrapperMapping;
            }
            else{
                return fetchMedicaidId(stateMap.get('medicaidId'));     
            }
        }       
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'mbeResponseparse');
            return fetchMedicaidId(stateMap.get('medicaidId'));     
        }    
    }
    
    
    /* Method Name : initiateAncillaryRequest
* Description : Method to call the service on page load and display the data in Medicaid Policy Details section
* Return type : Continuation
* Parameter   : String memberPlan ID
*/
    @AuraEnabled(continuation=true)
    public static Object initiateAncillaryRequest(String memberPlanId) 
    {
        try
        {	
            MedicaidPlanDetailsModel_LH_HUM medicaidWrapperMapping= new MedicaidPlanDetailsModel_LH_HUM();
            //US 1481853 Member Ancillary Service call will get triggerred only for StateCode 'KY','LA','SC'
            Boolean bAncillary = false;
            infoSet(memberPlanId);
            
            if((bProductTypeCheck!=null && bProductTypeCheck)){
                
                //method to check if the swtich is on or off and state condtion is matching
                bAncillary=checkSwitchOnOffWithState();
                
                MemberAncillaryRequest_H_HUM_V2 oAncillaryReq= new MemberAncillaryRequest_H_HUM_V2();
                oAncillaryReq =  MemberPlanAncillary_LS_HUM.assignValuesToAncillaryReq(oPolMem , sMemIdBase, sDependentCode); 
                
                //Member Ancillary service call will get hold whenever 1481853 switch is ON and Issue State is not 'KY','LA','SC'   
                if (bAncillary && oAncillaryReq!=null)
                {  
                    HUM_Webservice_Callout__c oSvcAncillary = HUM_Webservice_Callout__c.getValues('GetMemberAncillary_V2');  
                    integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcAncillary.Timeout__c)/1000;
                    HttpRequest oRequest = new HttpRequest();
                    Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                    string sJsonRequest = JSON.Serialize(oAncillaryReq);              
                    oRequest = MemberPlanAncillary_LS_HUM.setupAncillaryRequest(oRequest, oSvcAncillary, sJsonRequest);
                    sRequest = con.addHttpRequest(oRequest); 
                    stateMap.put('sRequest',sRequest);
                    stateMap.put('state',oPolMem.Issue_State__c);
                    stateMap.put('medicaidId',oPolMem.Medicaid_Id__c);
                    con.state=stateMap;
                    con.continuationMethod='processAncillaryResponse';
                    return con;
                }     
                else
                {
                    return fetchMedicaidId(oPolMem.Medicaid_Id__c);     
                }
            }
            else{
                return hideMedicaidPlanCondition();
            }
        } 
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LC_HUM', 'initiateAncillaryRequest');
            return hideMedicaidPlanCondition();
        }   
    }
    
    
    /*
* Method Name   :  processAncillaryResponse
* Description   :  Method is used to get response from Ancillary service.
* Return Type   :  void 
* Parameters    :  NA
*/
    @AuraEnabled
    public static Object processAncillaryResponse(Object state)
    {   
        Map<String,String> mapState=(map<String, String>)state;
        try 
        {	
            HttpResponse oResponse = Continuation.getResponse(mapState.get('sRequest'));            
            if(oResponse != null)
            {
                return ancillaryResponseParse(oResponse,mapState);
            }
            else
            {
               MedicaidPlanDetailsModel_LH_HUM ancillaryWrapperMapping = fetchMedicaidId(mapState.get('medicaidId'));     
                return getNewbornDetails(mapState.get('state'),ancillaryWrapperMapping);  
            }
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LC_HUM', 'processAncillaryResponse');
             MedicaidPlanDetailsModel_LH_HUM ancillaryWrapperMapping = fetchMedicaidId(mapState.get('medicaidId'));     
            return getNewbornDetails(mapState.get('state'),ancillaryWrapperMapping);
            
        }        
    }
    
    public static MedicaidPlanDetailsModel_LH_HUM getNewbornDetails(String sStateVal , MedicaidPlanDetailsModel_LH_HUM ancillaryWrapperMapping){
        if(sStateVal == 'LA')
        {
            MemberAncillaryResponse_DTO_HUM_V2 AncillaryInfo = new MemberAncillaryResponse_DTO_HUM_V2();    
            AncillaryInfo.NewBornMothersReferenceId ='';
            AncillaryInfo.AdmitDate = '';
            AncillaryInfo.dischargeDate ='';
            ancillaryWrapperMapping.ancillaryData=AncillaryInfo;
            ancillaryWrapperMapping.mappingVsLabel=MedicaidPlanDetailsModel_LH_HUM.ancillaryResponseMapping(stateMap.get('state'));
        }else if(sStateVal == 'OH')
        {
            MemberAncillaryResponse_DTO_HUM_V2 AncillaryInfo = new MemberAncillaryResponse_DTO_HUM_V2();    
            AncillaryInfo.Indvalue='';
            AncillaryInfo.OhioRISEBeginDate=''; 
            AncillaryInfo.OhioRISEEndDate='';
            ancillaryWrapperMapping.ancillaryData=AncillaryInfo;
            ancillaryWrapperMapping.mappingVsLabel=MedicaidPlanDetailsModel_LH_HUM.ancillaryResponseMapping(stateMap.get('state'));
        }
        return ancillaryWrapperMapping;
    }
    
    /*
* Method Name   :  ancillaryResponseParse
* Description   :  Method is used to parse response from Ancillary Service.
* Return Type   :  Object
* Parameters    :  HttpResponse oResponse and Map<String, String>
*/
    @testvisible public static Object ancillaryResponseParse(HttpResponse oResponse,Map<String,String> stateMap)
    {
        MedicaidPlanDetailsModel_LH_HUM ancillaryWrapperMapping= new MedicaidPlanDetailsModel_LH_HUM();
            AncillaryInfo = new MemberAncillaryResponse_DTO_HUM_V2();     
            integer iStatusCode = oResponse.getStatusCode();
            if (iStatusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_OK_HUM || iStatusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)
            {
                AncillaryInfo = MemberPlanAncillary_LS_HUM.processResponse(oResponse.getbody());  
                ancillaryWrapperMapping.ancillaryData=AncillaryInfo;
                ancillaryWrapperMapping.sMedicaidId= String.isNotBlank(stateMap.get('medicaidId')) ? stateMap.get('medicaidId') : '';
                ancillaryWrapperMapping.mappingVsLabel=MedicaidPlanDetailsModel_LH_HUM.ancillaryResponseMapping(stateMap.get('state'));
                return ancillaryWrapperMapping;
            }
            else{
                ancillaryWrapperMapping = fetchMedicaidId(stateMap.get('medicaidId'));     
                return getNewbornDetails(stateMap.get('state'),ancillaryWrapperMapping);
            }   
    }
    
    /* Method Name : infoSet
* Description : Method to get all details required for the service
* Return type : void
* Parameter   : NA
*/
    public static void infoSet(String memberPlanId)
    {       
            list<MemberPlan> lstPolMem = MemberAncillaryRequest_LD_HUM.getMemberName(memberPlanId);
            if(!lstPolMem.isEmpty()){
                oPolMem = lstpolMem[0];
            }
            
            //Checking if the Product_Type__c is a Medicaid Plan
            if (lstpolMem!=null && !lstpolMem.isEmpty() && String.isNotBlank(lstpolMem[0].Product_Type__c) && oPolMem.Product_Type__c.equalsIgnoreCase('MCD')) {
                bProductTypeCheck = true; 
            }
            if(lstpolMem!=null && !lstpolMem.isEmpty() && lstpolMem[0].Member_Dependent_Code__c!=null){
                sDependentCode = lstpolMem[0].Member_Dependent_Code__c;
            } 
            
            if(lstpolMem!=null && !lstpolMem.isEmpty() && lstpolMem[0].Member_Id_base__c !=null) {
                sMemIdBase = lstpolMem[0].Member_Id_base__c;  
            }  
    }
    
    /* Method Name : fetchMedicaidId
* Description : This method will return MedicaidId. Applicable in three conditions- 1) When integration is failed 2) When state is not matching with KY,SC 3) When the switch is off
* Return type : MedicaidPlanDetailsModel_LH_HUM
* Parameter   : String 
*/
    public static MedicaidPlanDetailsModel_LH_HUM fetchMedicaidId(String medicaidId)
    {	
        MedicaidPlanDetailsModel_LH_HUM medicaidWrapperMapping= new MedicaidPlanDetailsModel_LH_HUM();
        medicaidWrapperMapping.sMedicaidId = String.isNotBlank(medicaidId) ? medicaidId:'' ;
        return medicaidWrapperMapping;
    }
    
    /* Method Name : hideMedicaidPlanCondition
* Description : This method will returns a wrapper with Boolean to check if the Product_Type__c is MCD
* Return type : MedicaidPlanDetailsModel_LH_HUM
* Parameter   : NA 
*/
    public static MedicaidPlanDetailsModel_LH_HUM hideMedicaidPlanCondition()
    {	
        MedicaidPlanDetailsModel_LH_HUM medicaidWrapperMapping= new MedicaidPlanDetailsModel_LH_HUM();
        medicaidWrapperMapping.bIsMCDPlan=false;
        return medicaidWrapperMapping;
    }
    
    /* Method Name : checkSwitchOnOffWithState
* Description : This method will returns a Boolean to check if the swtich is on or off with state condition
* Return type : Boolean
* Parameter   : NA 
*/   
    public static Boolean checkSwitchOnOffWithState()
    {
		Boolean switchValue = false;
        Ancillary_State_Code__c ancillaryStateCode = Ancillary_State_Code__c.getValues(oPolMem.Issue_State__c);
        if(HUMUtilityHelper.isCRMFunctionalityON('1481853') && (oPolMem.Issue_State__c !='SC')){
            //bAncillary sets to 'True' when StateCode 'KY', SC' or Else it sets to 'False' 
            switchValue = ancillaryStateCode == null ? false : true;    
        }
        if(HUMUtilityHelper.isCRMFunctionalityON('1114331') && oPolMem.Issue_State__c =='SC')
        {
            switchValue = true;
        }
	if(HUMUtilityHelper.isCRMFunctionalityON('4358477') && oPolMem.Issue_State__c =='OH') //change state to OH after testing added by Viswa 
        {
         switchValue = true;
	    
        }
		if(HUMUtilityHelper.isCRMFunctionalityON('4312372') && (oPolMem.Issue_State__c =='FL' || oPolMem.Issue_State__c =='IL' ||
        oPolMem.Issue_State__c =='SC' || oPolMem.Issue_State__c =='LA')){
            switchValue = true;
        }
        return switchValue;
    }
    
     @AuraEnabled
    public static List<MemberPlan> getAdditionalMemberPlan(String sRecId)
    {   
        List<MemberPlan> lstMemPlan = new List<MemberPlan>();
        lstMemPlan = MemberPlanDetail_LD_HUM.getMemberName(sRecId);
        return lstMemPlan ;
    } 

 
    //========================= Vendor Service ===============================
    /*
     * Method Name   :    GetGroupInfoFromGBE
     * Description   :    This method makes call to GBE when the Member policy page is loaded 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */  
     @AuraEnabled(continuation=true)
     public static Object GetGroupInfoFromGBE(String sRecId) 
    { 
        AdditionalResponseWrapper_DTO_HUM addRespWrapper = new AdditionalResponseWrapper_DTO_HUM();
        if(HUMUtilityHelper.isCRMFunctionalityON('2117860')) 
            return MemberPlanDetail_LS_HUM.GetGroupInfoFromGBE(sRecId);
        addRespWrapper.isOnSwitch = false;
        return addRespWrapper;
    }
    
      /*
     * Method Name   :    processResponseVendor
     * Description   :    This method process Http Response from GBE Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    @AuraEnabled
    public static object processResponseVendor(Object state)
    { 
        return MemberPlanDetail_LS_HUM.processResponseVendor(state);    
    }
    //========================= Vendor Service ===============================
    
    //========================= EDI Service Start ===============================
   
 /*
     * Method Name   :    getEDIData
     * Description   :    This method makes call to GBE when the Member policy page is loaded 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */ 
    @AuraEnabled(continuation=true)  
     public static Object getEDIData(String sRecId) 
    {
        AdditionalResponseWrapper_DTO_HUM addRespWrapper = new AdditionalResponseWrapper_DTO_HUM();
        if(HUMUtilityHelper.isCRMFunctionalityON('2117860'))
            return MemberPlanDetail_LS_HUM.getEDIData(sRecId);   
        addRespWrapper.isOnSwitch = false;
        return addRespWrapper;
 
    }
    
    /*
     * Method Name   :    processResponseVendor
     * Description   :    This method process Http Response from GBE Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    @AuraEnabled
    public static object processResponseEDI(Object state)
    {
        return MemberPlanDetail_LS_HUM.processResponseEDI(state);           
    }  

    //========================= EDI Service Stop ===============================  
    //========================= Other Insurance Start ===============================
   
    /*
     * Method Name   :    getOIData
     * Description   :    This method makes call to MBE when the Member policy page is loaded 
     * Return Type   :    Continuation
     * Parameters    :    MemberPlan Id
     */ 
    @AuraEnabled(continuation=true)  
     public static Object getOIData(String sRecId) 
    {
        AdditionalResponseWrapper_DTO_HUM  oPolMemberDetails = new AdditionalResponseWrapper_DTO_HUM();
        if(HUMUtilityHelper.isCRMFunctionalityON('2262025')){
            return MemberPlanDetail_LS_HUM.getOtherInsuranceData(sRecId); 
        }else{
            oPolMemberDetails.isOnSwitch = false;
            return oPolMemberDetails; 
        }   
        
    }
    
    /*
     * Method Name   :    processMBEOtherInsuranceResponse
     * Description   :    This method process Http Response from MBE Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    @AuraEnabled
    public static object processMBEOtherInsuranceResponse(Object state)
    {
        return MemberPlanDetail_LS_HUM.processMBEOtherInsuranceResponse(state);           
    } 

    //========================= Other Insurance Stop =============================== 
    /*
    * Method Name   :  getDualStatusData
    * Description   :  This method is used to call the Service on load of DualEligibleDetails_VF_HUM Page
    * Return Type   :  continuation
    * Parameters    :  NA
    */   
	@AuraEnabled(continuation=true)    
    public static Object getDualStatusData(String memberPlanId)
    { 
        AdditionalResponseWrapper_DTO_HUM  oPolMemberDetails = new AdditionalResponseWrapper_DTO_HUM();
        if(HUMUtilityHelper.isCRMFunctionalityON('2364782')){
            return MemberPlanDetail_LS_HUM.getDualStatusData(memberPlanId);
        }else{
            oPolMemberDetails.isOnSwitch = false;
            return oPolMemberDetails; 
        }   
        
    }
    

    @AuraEnabled (continuation=true) 
    public static object processResponse(Object state)
    {
        return MemberPlanDetail_LS_HUM.processResponseDualStatus(state);
    } 
}