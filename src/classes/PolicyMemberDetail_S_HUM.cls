/*******************************************************************************************************************************
Apex Class Name : PolicyMemberDetail_S_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Service layer for PolicyMemberDetail_VF_HUM Page

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------

* Rajesh Keswani             118189                   07/31/2017                 REQ - 305662  Original version
* Anjali Kumar                                        08/28/2018                 REQ - 373406 Replace PBE1.2 with PBE1.3 in Policy Member Detail Page
* Shailesh Mali                                       01/24/2019                 REQ - 383577 Integration Story: Changes For EDI Group
* Firoja Begam                                        10/28/2020                 US-1279281- PR00095366 - MF 1 - new fields in policy details section (member policy page)
* Firoja Begam                                        12/12/2020                 US-1789465- PR00095366 - Accolade - SubGroupID Issue (INC0807696)
* Firoja Begam                                        02/10/2021                 US-1760349 - PR00095180 - CRM 6.01 Add PEO Indicator for MTV and CAS products  to Member Policy Page (copy)
* Ajay Chakradhar                           		  05/05/2021	             REQ - 2184294 - Replacing constants with Consumer Management Constants
*****************************************************************************************************************************************/
public with sharing class PolicyMemberDetail_S_HUM
{

    @TestVisible private static String sBodyVendor ='' ;
     private String sNoneValue = 'NONE';  
     private String sNullValue = 'Uninitialized';  
     private String sLVPlatForm =  ConsumerManagementConstants_HUM.VOB_PLATFORM_LV_HUM;
     private String sEMPlatForm =  ConsumerManagementConstants_HUM.VOB_PLATFORM_EM_HUM; 
     Public Boolean switchOnOffSubGroupId = false;
     Public Boolean switchOnOffPEOAssociate = false;
     Private String sDummyBenValue = 'XXXXXXXX';
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for MBE,PBE and GBE services .
     *
     * @param PBERequestDTO              PolicyMemberDetail_DTO_HUM class instance
     * @return                                continuation
     */
     public static Continuation SendRequestPBEService(PolicyMemberDetail_DTO_HUM PBERequestDTO)
    {
        try
        {
            Continuation con;
            if (string.isNotBlank(PBERequestDTO.ASOValue) && 'Y'.equalsIgnoreCase(PBERequestDTO.ASOValue)) con = callPBE(PBERequestDTO);
            else
            {
                List < Customized_Fully_Insured_Plans__mdt > insuredPlans = [SELECT Id, Label FROM Customized_Fully_Insured_Plans__mdt];
                boolean bPlanFound = false;
                if (insuredPlans != null && insuredPlans.size() > 0 && PBERequestDTO != null && string.isNotBlank(PBERequestDTO.groupMemberNumber))
                {
                    for (Customized_Fully_Insured_Plans__mdt obj: insuredPlans)
                    {
                        if (PBERequestDTO.groupMemberNumber == obj.Label)
                        {
                            bPlanFound = true;
                            break;
                        }
                    }
                }
                if (bPlanFound) con = callPBE(PBERequestDTO);
            }
            return con;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'PolicyMemberDetail_S_HUM', 'SendRequestPBEService');
            return Null;
        }
    }

	/**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for MBE,PBE and GBE services .
     *
     * @param PBERequestDTO              PolicyMemberDetail_DTO_HUM class instance
     * @return                                continuation
     */
    public static Continuation callPBE(PolicyMemberDetail_DTO_HUM PBERequestDTO)
    {
        HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(PBEServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        // Set callback method should be common for PBE services 
        con.continuationMethod = 'parsePBEResponse';
        // Build PBE Request 
        HttpRequest httpPBEReqObj = builtHttpRequest(PBEServiceDetails);
        httpPBEReqObj.setBody(JSON.serialize(PolicyMemberDetail_D_HUM.RequestStructurePBEService(PBERequestDTO)));
        con.state = PBERequestDTO.policyMemberID;
        // Add callout requests to continuation for all three requests
        con.addHttpRequest(httpPBEReqObj);
        return con;
    }


    /**
     * Returns responses of continuation object
     * <p>
     * parsePBEResponse Method to parse the service response
     *
     * @param List<string> labels .
     * @param  object State
     * @return Object
     */
    public static Object parsePBEResponse(List < string > labels, Object State)
    {
        Map<string,string> PBEMap;
        try
        {
            HttpResponse responsePBE = Continuation.getResponse(labels[0]);
            ProductBusinessEntityResponseV2_H_HUM oResponsePBE;         
            if (responsePBE != null && !responsePBE.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && ((responsePBE.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || responsePBE.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
				oResponsePBE = (ProductBusinessEntityResponseV2_H_HUM) System.JSON.deserialize(responsePBE.getBody(), ProductBusinessEntityResponseV2_H_HUM.class);
                PBEMap = PolicyMemberDetail_D_HUM.parsePBE(oResponsePBE);
            }
            else
            {
                // Fault Code
                string recordId = string.valueOf(state);
                Pharmacy_H_HUM.logHpFaultError(responsePBE,'PolicyMemberDetail_S_HUM', 'parsePBEResponse', recordId);
            }
            return PBEMap;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_S_HUM', 'parsePBEResponse');
            return null;
        }
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
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, ServiceObj.Content_Type__c);
                request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM, GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
    }
	
	/*
     * REQ-383577 - Integration for EDI Group 
     * Method use for, to send the request through continuation framework
     * @method - sendRequestEDI
     * @param - groupId
     * @return - Continuation
    */
	public static Continuation sendRequestEDI(String groupId){
        try{
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('GetEDIData');
			if(serviceDetails !=null){
				integer iTIMEOUT_INT_SEC = Integer.valueof(serviceDetails.Timeout__c) / 100;

				Continuation con = new Continuation(iTIMEOUT_INT_SEC) ;
				con.continuationMethod='processEDIResponse';    
				con.state = groupId;

				HttpRequest request = new HttpRequest();
			
				if(serviceDetails !=null && serviceDetails.Active__c ){
					request.setMethod(serviceDetails.Request_Type__c);
					request.setClientCertificateName(serviceDetails.Certificate_Name__c);
					request.setEndpoint(serviceDetails.End_Point_URL__c);
					request.setTimeout(Integer.valueOf(serviceDetails.Timeout__c));
					request.setBody(JSON.serialize(RequestStructureforEDI(groupId)));
				}
				String strResponse = con.addHttpRequest(request);
				return con;
			}
			else 
				return null;
        }
        catch(Exception e){
			HUMExceptionHelper.logErrors(e, 'PolicyMemberDetail_S_HUM', 'sendRequestEDI');
			return Null;
        }  
    }
    
	/*
     * REQ-383577 - Integration for EDI Group 
     * Returns responses of continuation object
     * @method - Callback Method to parse the EDI service response 
     * @param - label object and continuation state object
     * @return - Object
    */
    public static object processEDIResponse(List < string > labels, Object State){
       Map<string,List<string>> EDIMap = new Map<string,List<string>>();
        try{       	 
       	    HUMPolicyMember_EDIResponse_DTO getEDIResponse = new HUMPolicyMember_EDIResponse_DTO();
            HttpResponse sResponse = (labels != null ) ? Continuation.getResponse(labels[0]) : null;
            if (sResponse != null && !sResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && ((sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest())){
                getEDIResponse = (HUMPolicyMember_EDIResponse_DTO)System.JSON.deserialize(sResponse.getBody(),HUMPolicyMember_EDIResponse_DTO.class);
              
                if(getEDIResponse!=null)
        			EDIMap = parseEDIResponse(getEDIResponse);
        	    else
        	    	return null;
            }
            return EDIMap;
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_S_HUM', 'processEDIResponse');
			return null;
        }       
    }
    
	/*
     * REQ-383577 - Integration for EDI Group 
     * Method used for, to parse the EDIResponse and set the value in Map<string,List<string>> format to parse easily in remoting Callback
     * @method - parseEDIResponse 
     * @param - HUMPolicyMember_EDIResponse_DTOt
     * @return - Map<string,List<string>>
    */
    public static Map<string,List<string>> parseEDIResponse(HUMPolicyMember_EDIResponse_DTO ediResponse ){
    	Map<string,List<string>> EDIMap = new Map<string,List<string>>();
    	List<string> ediList = new List<String>();
    	string newLine = '\n\n';
		string nextVal = ',';
    	
      	if(ediResponse!=null){
        	for(HUMPolicyMember_EDIResponse_DTO.EdiGroupDetails e : ediResponse.GetEDIGroupInfoResponse.EdiGroupDetails ){        		
				if(e.EDIGroup !=null){
					ediList.add(e.EDIGroup);
        			ediList.add('Phone Number'+newLine);
	        		ediList.add('Email'+newLine);
        			
					if(e.PCP_PCDFlag!=null && e.PCP_PCDFlag != '') { ediList.add(e.PCP_PCDFlag); }
					if(e.Frequency !=null && e.Frequency !='' ) { ediList.add(e.Frequency); }
					if(e.Day1 !=null && e.Day1 !='') { ediList.add(e.Day1); }
					if(e.Day2 !=null && e.Day2 !='') { ediList.add(nextVal+e.Day2); }
					if(e.Day3 !=null && e.Day3 !='') { ediList.add(nextVal+e.Day3); }
					if(e.Day4 !=null && e.Day4 !='') { ediList.add(nextVal+e.Day4); }      
						
					EDIMap.put('EDIValue',ediList);   		   
        		}        		
      		}
			 return EDIMap; 
    	}
		else{
		    return null;
		}
    	   	    
    }
    
	/*
     * REQ-383577 - Integration for EDI Group 
     * Method is used for to create Request for EDI 
     * @method - RequestStructureforEDI 
     * @param - groupId
     * @return - HUMPolicyMember_EDIRequest_DTO
    */
	public static HUMPolicyMember_EDIRequest_DTO RequestStructureforEDI(string groupId){
        try{
			HUMPolicyMember_EDIRequest_DTO ediRequestDTO = new HUMPolicyMember_EDIRequest_DTO();
            HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest getEDIgroup = new HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest();
			HUMPolicyMember_EDIRequest_DTO.GroupDetail grpDetail = new HUMPolicyMember_EDIRequest_DTO.GroupDetail()  ;
         
			getEDIgroup.GroupDetail = new List<HUMPolicyMember_EDIRequest_DTO.GroupDetail>();
          
            grpDetail.Group_id = groupId;
            getEDIgroup.GroupDetail.add(grpDetail);
            ediRequestDTO.GetEDIGroupInfoRequest = getEDIgroup;
            return ediRequestDTO;           
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'PolicyMemberDetail_S_HUM', 'RequestStructureforEDI');
            return Null;
        }
    }
    /*
   * Method Name   :  setupRequest
   * Description   :  This method is used to form http request.
   * Return Type   :  HttpRequest 
   * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Plan Id)
   */
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderPolicy,Policy_Plan_Details__c policyPlan,Policy_Member__c policyMember)
    {
       HttpRequest orequest = new HttpRequest();    
        try
        {
          sBodyVendor = buildRequest(policyPlan,policyMember);
            if(String.isNotBlank(sBodyVendor))
            {
                request.setMethod(oSvcHeaderPolicy.Request_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oSvcHeaderPolicy.Content_Type__c); 
                request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM,GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBodyVendor);
                if(!Test.isRunningTest())    
                {
                  request.setClientCertificateName(oSvcHeaderPolicy.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcHeaderPolicy.Timeout__c));
                request.setEndpoint(oSvcHeaderPolicy.End_Point_URL__c);  
                orequest = request;
            }
            else
            {
               orequest = null;  
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_S_HUM', 'setupRequest');
            orequest = null;
        }
        return orequest; 
    }
    /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   buildRequest
    *  @param  
    *  @return string - Returns the serialized body of the request
    *  @throws NA
    */ 
    @testvisible private string buildRequest(Policy_Plan_Details__c policyPlan,Policy_Member__c policyMember)
    {    
        string sJsonbody = '';
        switchOnOffPEOAssociate = HUMUtilityHelper.isCRMFunctionalityON('1760349');
        try
        {
          GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM(); 
          GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
          //Replaced existing condition to fetch SubGroupID from 8 digits to 9 digits. For SubGroupId having more than 8 chars was not getting fetched properly. Hence replaced subString() to fetch 9 digits and for 8 chars will trim the space
           if(!String.isBlank(policyPlan.Source_Cust_Cov_Key__c))
           {
            grpDetailDTO.GroupId = (policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,6):policyMember.Group_Number__c);          
            grpDetailDTO.SubGroupId = (policyPlan.Platform__c == sEMPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,9).trim() : policyPlan.Platform__c ==   sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(9,12):'');
           }
          grpDetailDTO.BenefitPackageId = policyPlan.Platform__c == sEMPlatForm ? sDummyBenValue : ''; 
          grpDetailDTO.Platform = policyPlan.Platform__c;
          grpDetailDTO.StartDate =  (policyMember.Effective_Date__c <> null ? policyMember.Effective_Date__c : sNoneValue);                   
          grpDetailDTO.EndDate = policyMember.End_Date__c <> null ? policyMember.End_Date__c : sNoneValue;
          grpDetailDTO.BenefitSequenceNumber='';
          grpDetailDTO.DivisionStartPosition ='';
          grpDetailDTO.DivisionRecordsToReturn ='';
          grpDetailDTO.InclusionCriteria = ConsumerManagementConstants_HUM.GROUPSTRING;
          grpDetailDTO.VendorOfferProgramGroup = GlobalCommonConstants_HUM.STRING_TRUE;
          grpDetailDTO.VendorOfferProgramSubGroup = GlobalCommonConstants_HUM.STRING_TRUE;
          /****Added new Request param for EM Platform only for PEO Indicator Field ****/
          if(switchOnOffPEOAssociate){
          grpDetailDTO.Association = policyPlan.Platform__c == sEMPlatForm ? GlobalCommonConstants_HUM.STRING_TRUE : '';
          }else{
          grpDetailDTO.Association = null;
          }
          oRequest.GroupDetailDTO = grpDetailDTO;
          sJsonbody = JSON.serialize(oRequest);         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_S_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
}