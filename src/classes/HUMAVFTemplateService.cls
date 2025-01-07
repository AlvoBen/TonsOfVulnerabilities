/**************************************************************************** 
Apex Class Name  : HUMAVFTemplateService
Version          : 1.0  
Created Date     : March 13, 2015
Function         : This class is used to build Rest Service for AVF Template service
Test Class       : HUMAVFTemplateServiceTest
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam            23024                              03/13/2015                 Original Version  
* Sharan Shanmugam            23115                              05/15/2015                 Moved custom setting to constant file.
* Shruthi Karanth			  34224								 07/29/2016					Changed to json 
***************************************************************************************************/

public with sharing class HUMAVFTemplateService
{
    /*
    * This is a method to call Template Service
    * @param HUMAVFTemplateRequestHelper
    * @return HUMAVFTemplateResponseHelper
    */
    public static HUMAVFTemplateResponseHelper getAVFTemplate(HUMAVFTemplateRequestHelper reqHelper) 
    {
        try
        {
            HUM_Webservice_Callout__c templateHeaders = HUM_Webservice_Callout__c.getValues('AVFTemplate');
            // Send the request, and return a response
            AVFTemplateResponse_DTO_HUM avfResponseHelper = new AVFTemplateResponse_DTO_HUM();
            HUMAVFTemplateResponseHelper oResponse = new HUMAVFTemplateResponseHelper();
            String sBody = json.serialize(buildRequestMessage(reqHelper));
            if(templateHeaders.Active__c && sBody!=null)
	           {
	           	HttpResponse response = HUMCalloutUtilityHelper.callWebservice(templateHeaders.End_Point_URL__c, templateHeaders.Certificate_Name__c, sBody, templateHeaders.Request_Type__c, null, templateHeaders.Content_Type__c);
	            if(response.getStatusCode() == 200 || response.getStatusCode() == 202)
	            {
	                avfResponseHelper = (AVFTemplateResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), AVFTemplateResponse_DTO_HUM.class);	                
	            	oResponse = buildResponseMessage(avfResponseHelper);
	            }
	        } 
            return oResponse;         
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMAVFTemplateService','getAVFTemplate');
            return null;
        }
    }
    
    /*
    * This is a method to construct request message to call Template Service
    * @param HUMAVFTemplateRequestHelper
    * @return AVFTemplateRequest_DTO_HUM
    */
    private static AVFTemplateRequest_DTO_HUM buildRequestMessage(HUMAVFTemplateRequestHelper reqHelper) 
    {
    	AVFTemplateRequest_DTO_HUM oReqHelper = new AVFTemplateRequest_DTO_HUM();
    	AVFTemplateRequest_DTO_HUM.SaveTemplateRequest oSampleTemplateRequest = new AVFTemplateRequest_DTO_HUM.SaveTemplateRequest();
    	
    	oSampleTemplateRequest.CaseNumber = reqHelper.CaseNumber;  //Should be of type String but as the value reaches its limit, this is changed into of type String
		oSampleTemplateRequest.EditSequenceNumber = reqHelper.EditSequenceNumber;
		oSampleTemplateRequest.HumanaAssociateName = reqHelper.HumanaAssociateName;
		oSampleTemplateRequest.PersonFirstName = reqHelper.PersonFirstName;
		oSampleTemplateRequest.PersonLastName = reqHelper.PersonLastName;
		oSampleTemplateRequest.LastSaveDate = reqHelper.LastSaveDate;
		oSampleTemplateRequest.MemberUMID = reqHelper.MemberUMID;
		oSampleTemplateRequest.MedicareId = reqHelper.MedicareId;
		oSampleTemplateRequest.EnterprisePersonID = reqHelper.EnterprisePersonID;
		oSampleTemplateRequest.CMSMAPlanNumber = reqHelper.CMSMAPlanNumber;
		oSampleTemplateRequest.CMSPDPPlanNumber = reqHelper.CMSPDPPlanNumber;
		oSampleTemplateRequest.PersonSpeakingWith = reqHelper.PersonSpeakingWith;
		oSampleTemplateRequest.RelationToMember = reqHelper.RelationToMember;
		oSampleTemplateRequest.PermanentResidentialAddress = reqHelper.PermanentResidentialAddress;
		oSampleTemplateRequest.PermanentResidentialCityName = reqHelper.PermanentResidentialCityName;
		oSampleTemplateRequest.PermanentResidentialCountyName = reqHelper.PermanentResidentialCountyName;
		oSampleTemplateRequest.PermanentResidentialStateCode = reqHelper.PermanentResidentialStateCode;
		oSampleTemplateRequest.PermanentResidentialZipCode = reqHelper.PermanentResidentialZipCode;
		oSampleTemplateRequest.PermanentResidentialZipPlusCode = reqHelper.PermanentResidentialZipPlusCode;
		oSampleTemplateRequest.PermanentResidentialPhoneNumber = reqHelper.PermanentResidentialPhoneNumber;
		oSampleTemplateRequest.PermanentResidentialStartDate = reqHelper.PermanentResidentialStartDate;
		oSampleTemplateRequest.MailingAddress = reqHelper.MailingAddress;
		oSampleTemplateRequest.MailingCityName = reqHelper.MailingCityName;
		oSampleTemplateRequest.MailingStateCode = reqHelper.MailingStateCode;
		oSampleTemplateRequest.MailingZipCode = reqHelper.MailingZipCode;
		oSampleTemplateRequest.MailingZipPlusCode = reqHelper.MailingZipPlusCode;
		oSampleTemplateRequest.TemporaryAddress = reqHelper.TemporaryAddress;
		oSampleTemplateRequest.TemporaryCityName = reqHelper.TemporaryCityName;
		oSampleTemplateRequest.TemporaryCountyName = reqHelper.TemporaryCountyName;
		oSampleTemplateRequest.TemporaryStateCode = reqHelper.TemporaryStateCode;
		oSampleTemplateRequest.TemporaryZipCode = reqHelper.TemporaryZipCode;
		oSampleTemplateRequest.TemporaryZipPlusCode = reqHelper.TemporaryZipPlusCode;
		oSampleTemplateRequest.TemporaryOSAStartDate = reqHelper.TemporaryOSAStartDate;
		oSampleTemplateRequest.TemporaryOSAEndDate = reqHelper.TemporaryOSAEndDate;
		oSampleTemplateRequest.E2ETrackID = reqHelper.E2ETrackID;
		oSampleTemplateRequest.E2EActivityID = reqHelper.E2EActivityID;
		oSampleTemplateRequest.E2EMilestoneID = reqHelper.E2EMilestoneID;
		
		oReqHelper.SaveTemplateRequest = oSampleTemplateRequest;
		
		return oReqHelper;

    }
    
    /*
    * This is a method to build the response message returned by Template Service
    * @param AVFTemplateResponse_DTO_HUM
    * @return HUMAVFTemplateResponseHelper
    */
    private static HUMAVFTemplateResponseHelper buildResponseMessage(AVFTemplateResponse_DTO_HUM resHelper) 
    {
    	HUMAVFTemplateResponseHelper oResponse = new HUMAVFTemplateResponseHelper();
    	if(resHelper.TemplateResponse.Errors.Error != null && !resHelper.TemplateResponse.Errors.Error.isEmpty())	
		{
	    	for(Integer i = 0; i < resHelper.TemplateResponse.Errors.Error.size(); i++)
	    	{
	    		oResponse.sErrorCode = resHelper.TemplateResponse.Errors.Error[i].Code;
	    		oResponse.sErrorDescription = resHelper.TemplateResponse.Errors.Error[i].Description;
	    	}
		}
    	return oResponse;	
    }
}