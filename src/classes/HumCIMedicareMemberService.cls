/**************************************************************************** 
Apex Class Name  : HUMCIMedicareMemberService
Version          : 1.0  
Created Date     : December 3, 2014
Function         : This class is used to build Rest Service for CI Mediacare Member service
Test Class       : HUMCIMedicareMemberServiceTest
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri        19768                               12/03/2014                 Original Version  
* Pradeep Veeravali            20004                               12/19/2014                 Changes as per StoredProcedure URL                                                                               
* Pradeep Veeravali            20058                               12/19/2014                 Changes for test class failure fix
* Pradeep Veeravali            20628                               01/27/2015                 Modified to add new Params in Request URL & retrieve response from new fields                        
* Pradeep Veeravali            20878                               02/11/2015                 Modified to remove EOEInfo Param from Request URL & EOE fields from Response    
* Pradeep Veeravali            22358                               04/13/2015                 Modified code to replace http GET with POST   
* Chaitanya Kumar                                                  07/18/2016                 Fixed Defect-23149 by encoding(UTF-8) Encrypted values-MedicardID
* Shruthi Karanth												   07/29/2016				  Changes to json structure
***************************************************************************************************/
public with sharing class HumCIMedicareMemberService
{
 
 private String sClassName = 'HumCIMedicareMemberService';
 private String sMethodName = ''; 
 
 public String sContent{get;set;}
 public CIMedicareMemberResponse_DTO_HUM CIMedicareMemberResponse{get;set;}   
 
  /*
    * This is a method to call CI Medicare Service
    * @param HUMAVFTemplateRequestHelper
    * @return HUMAVFTemplateResponseHelper
    */
 public HUMCIMedicareMemberResponseHelper callRestCIMedicareMemberService(String MedicareID, String MemberCardID, String GroupID, String BenefitSequenceNumber, String AsOfDate, String LISInfo,  String LTSSInfo,  String ESRDInfo,  String SPAPInfo,  String SNPInfo, String POAInfo)    
 {
	sMethodName = 'callRestCIMedicareMemberService';                       
	try
    {
		HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues(system.label.HUMCIMedicareMemberService);           
	    CIMedicareMemberResponse = new CIMedicareMemberResponse_DTO_HUM();
	    string sBody = setContent(MedicareID, MemberCardID, GroupID, BenefitSequenceNumber, AsOfDate, LISInfo, LTSSInfo, ESRDInfo, SPAPInfo, SNPInfo, POAInfo);
	           
	    if(oSvcHeaders.Active__c && sBody!=null)
	    {
	    	HttpResponse response = HUMCalloutUtilityHelper.callWebservice(oSvcHeaders.End_Point_URL__c, oSvcHeaders.Certificate_Name__c, sBody, oSvcHeaders.Request_Type__c, '', oSvcHeaders.Content_Type__c);
	        if(response.getStatusCode() == 200 || response.getStatusCode() == 202)
	        {
	        	CIMedicareMemberResponse = (CIMedicareMemberResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), CIMedicareMemberResponse_DTO_HUM.class);
	         }
	     }
	}
	catch(system.CalloutException e)
	{                      
		HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
		return null;             
	}                                
	return  setResponse(CIMedicareMemberResponse);               
 }  
 
  /*
    * This is a method to create request for CI Medicare Service
    * @param String,String,String,String,String,String,String,String,String,String
    * @return String
    */   
 public String setContent( String MedicareID, String MemberCardID, String GroupID, String BenefitSequenceNumber, String AsOfDate, String LISInfo,  String LTSSInfo,  String ESRDInfo,  String SPAPInfo,  String SNPInfo, String POAInfo)
 {
	String sMethodName = 'setContent';
	String sContent = '';
	try
	{ 
		CIMedicareMemberRequest_DTO_HUM oRequest = new CIMedicareMemberRequest_DTO_HUM();
		CIMedicareMemberRequest_DTO_HUM.CIMedicareMember oMedicareMember = new CIMedicareMemberRequest_DTO_HUM.CIMedicareMember();
		oMedicareMember.MedicareID = MedicareID;
		oMedicareMember.MemberCardID = MemberCardID;
		oMedicareMember.GroupID = GroupID;
		oMedicareMember.BenefitSequenceNumber = BenefitSequenceNumber;
		oMedicareMember.AsOfDate = AsOfDate;
		oMedicareMember.LISInfo = LISInfo;
		oMedicareMember.LTSSInfo = LTSSInfo;
		oMedicareMember.ESRDInfo = ESRDInfo;
		oMedicareMember.SPAPInfo = SPAPInfo;
		oMedicareMember.SNPInfo = SNPInfo;
		oMedicareMember.POAInfo = POAInfo;
		oRequest.CIMedicareMemberRequest = oMedicareMember;
		sContent = json.serialize(oRequest);           
	}
	catch(Exception e)
	{            
		HUMExceptionHelper.logErrors(e,sClassName,sMethodName);           
		return null;  
	}
       
	return sContent; 
 } 
 
 /*
    * This is a method to build response for CI Medicare Service
    * @param CIMedicareMemberResponse_DTO_HUM
    * @return HUMCIMedicareMemberResponseHelper
    */   
 private HUMCIMedicareMemberResponseHelper setResponse(CIMedicareMemberResponse_DTO_HUM oCIMedicareResponse) 
 {
	HUMCIMedicareMemberResponseHelper oResponse =  new HUMCIMedicareMemberResponseHelper();
	oResponse.sLISIndicator = oCIMedicareResponse.MedicareMemberInfoResponse.LISIndicator;
	oResponse.sSubsidyEffectDate = oCIMedicareResponse.MedicareMemberInfoResponse.SubsidyEffectDate;
	oResponse.sSubsidyEndDate = oCIMedicareResponse.MedicareMemberInfoResponse.SubsidyEndDate;
	oResponse.sLISSubsidyLevel = oCIMedicareResponse.MedicareMemberInfoResponse.LISSubsidyLevel;
	oResponse.sLISCopayCategory = oCIMedicareResponse.MedicareMemberInfoResponse.LISCopayCategory;
	oResponse.sLTSSIndicator = oCIMedicareResponse.MedicareMemberInfoResponse.LTSSIndicator;
	oResponse.sESRDIndicator = oCIMedicareResponse.MedicareMemberInfoResponse.ESRDIndicator;
	oResponse.sSPAPIndicator = oCIMedicareResponse.MedicareMemberInfoResponse.SPAPIndicator;
	oResponse.sSNPIndicator = oCIMedicareResponse.MedicareMemberInfoResponse.SNPIndicator;
	oResponse.sSNPTypeCode = oCIMedicareResponse.MedicareMemberInfoResponse.SNPTypeCode;
	oResponse.sPOAIndicator = oCIMedicareResponse.MedicareMemberInfoResponse.POAIndicator;
	oResponse.sPOALastName = oCIMedicareResponse.MedicareMemberInfoResponse.POALastName;
	oResponse.sPOAFirstName = oCIMedicareResponse.MedicareMemberInfoResponse.POAFirstName;
	oResponse.sPOAPhoneNumber = oCIMedicareResponse.MedicareMemberInfoResponse.POAPhoneNumber;
	oResponse.sPOAType = oCIMedicareResponse.MedicareMemberInfoResponse.POAType;
	oResponse.sPOAApplDate = oCIMedicareResponse.MedicareMemberInfoResponse.POAApplDate;
		
	List<HUMCIMedicareMemberResponseHelper.Error> lstError = new List<HUMCIMedicareMemberResponseHelper.Error>();
		
	if(oCIMedicareResponse.MedicareMemberInfoResponse.Errors.Error != null &&!oCIMedicareResponse.MedicareMemberInfoResponse.Errors.Error.isEmpty())	
	{
		for(Integer i = 0; i < oCIMedicareResponse.MedicareMemberInfoResponse.Errors.Error.size(); i++)
		{
			HUMCIMedicareMemberResponseHelper.Error err= new HUMCIMedicareMemberResponseHelper.Error();
			err.Code = oCIMedicareResponse.MedicareMemberInfoResponse.Errors.Error[i].Code;	
			err.Description = oCIMedicareResponse.MedicareMemberInfoResponse.Errors.Error[i].Description;
			lstError.add(err);
		}		
	}
	oResponse.Errors = lstError;
    	
    return oResponse;
 }                     
}