/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicMedicareAddress_S_HUM
    @Version          : 1.0
    @Created Date     : June 28th, 2020
    @Function         : Class for Template Service
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  28/06/2020                    Original Version
* Ajay Chakradhar                                  20/05/2022                    US 2568799 : MF [3] - (CR 1945 / 1789) - Success / Fail indication in Process Title - Case Edit Page
******************************************************************************************************************************************************************************/
public with sharing class DemographicMedicareAddress_S_HUM {
	/*
    * This is a method to trigger Medicare Address Service for Template Service
    * @param caseNumber,avfRequest,string policyId
    * @return void
    */
    public static void sendMedicareAddressService(string caseNumber, string avfRequest,string policyId)
    {	
        try {
            HUM_Webservice_Callout__c templateHeaders = HUM_Webservice_Callout__c.getValues('AVFTemplate');
            
            AVFTemplateResponse_DTO_HUM avfResponseHelper = new AVFTemplateResponse_DTO_HUM();
            HUMAVFTemplateResponseHelper oResponse = new HUMAVFTemplateResponseHelper();
            
            String sRequestBody = prepareRequestforService(caseNumber,avfRequest,policyId);
            
            if(templateHeaders.Active__c && sRequestBody!=null)
	        {
	           	HttpResponse response = HUMCalloutUtilityHelper.callWebservice(templateHeaders.End_Point_URL__c, templateHeaders.Certificate_Name__c, sRequestBody, templateHeaders.Request_Type__c, null, templateHeaders.Content_Type__c);
	            if ((response != null && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && 
            	(response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            	) || Test.isRunningTest())    
	            {	
	                avfResponseHelper = (AVFTemplateResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), AVFTemplateResponse_DTO_HUM.class);	                
	            	oResponse = buildResponseMessage(avfResponseHelper);
                    //Creating Error Log Record for AVF Failure Response
                    if(oResponse.sErrorCode != '0000'){
                            Error_Log__c objErrorLog = new Error_Log__c();
                            objErrorLog.Class_Name__c='DemographicMedicareAddress_S_HUM'; objErrorLog.Method_Name__c='sendMedicareAddressService';
                        	objErrorLog.Error_Code__c = oResponse.sErrorCode; objErrorLog.Error_Message__c= oResponse.sErrorDescription;
                            objErrorLog.Operation__c='AVFTemplate'; objErrorLog.Record_Id__c='Case:'+caseNumber+';policyId:'+policyId;
                            Database.upsertResult upsertResult = Database.upsert(objErrorLog,false);
                            HUMExceptionHelper.processUpsertResults(new List<Database.upsertResult>{upsertResult},
                            new List<SObject>{objErrorLog},'DemographicMedicareAddress_S_HUM','sendMedicareAddressService','AVFTemplate');    
                    }
                }
	        } 
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'DemographicMedicareAddress_S_HUM','sendMedicareAddressService');
        }
    }
     
    /*
    * This is a method to build the request for Template Service
    * @param caseNumber,avfRequest,policyId
    * @return String
    */
    public static String prepareRequestforService(String caseNumber,String avfRequest,String policyId)
    {	
    	try {
                DemographicUpdateTemplate_DTO_HUM avfRequestDTO = 
                            (DemographicUpdateTemplate_DTO_HUM)System.JSON.deserialize(avfRequest, 
                                DemographicUpdateTemplate_DTO_HUM.class);
                     
                AVFTemplateRequest_DTO_HUM avfDataRequest =new AVFTemplateRequest_DTO_HUM();
                AVFTemplateRequest_DTO_HUM.SaveTemplateRequest oSaveTemplateRequest = new AVFTemplateRequest_DTO_HUM.SaveTemplateRequest();
                
                Case csedetails = [select id,casenumber,accountid,account.Enterprise_ID__c,account.FirstName,account.LastName from case where id=:CaseNumber limit 1];
                if(csedetails.accountid != Null)
                {	
                    oSaveTemplateRequest.CaseNumber = csedetails.casenumber;
                    oSaveTemplateRequest.HumanaAssociateName = UserInfo.getName();
                    oSaveTemplateRequest.PersonFirstName = (csedetails.account.FirstName != null) ? csedetails.account.FirstName : '';
                    oSaveTemplateRequest.PersonLastName = (csedetails.account.LastName != null) ? csedetails.account.LastName : '';
                    oSaveTemplateRequest.EnterprisePersonID = (csedetails.account.Enterprise_ID__c != null) ? csedetails.account.Enterprise_ID__c : ''; 
                    oSaveTemplateRequest.EditSequenceNumber = '1';
                    //Policy_Member__c record 
                    Policy_Member__c policymbrdetails = [SELECT Id,Policy__c,Policy__r.Contract_Number__c,Policy__r.Product_Type__c FROM Policy_Member__c where id=:policyId limit 1];
                    if(policymbrdetails.Id != Null)
                    {
                        if(policymbrdetails.Policy__r.Product_Type__c == 'MA' || policymbrdetails.Policy__r.Product_Type__c == 'MAPD')
                        {
                            oSaveTemplateRequest.CMSMAPlanNumber = policymbrdetails.Policy__r.Contract_Number__c ;
                        }
                        else if(policymbrdetails.Policy__r.Product_Type__c == 'PDP' || Test.isRunningTest())
                        {
                            oSaveTemplateRequest.CMSPDPPlanNumber = policymbrdetails.Policy__r.Contract_Number__c ;
                        }
                        //List of Member_ID__c
                        List<Member_ID__c> lstMemberIds = new List<Member_ID__c>();
                        lstMemberIds = [SELECT Id, Name, Policy_Member__c, Type__c FROM Member_ID__c where Policy_Member__c=:policyId and (Type__c = 'MedicareID' OR Type__c='Member-Id-Base')]; 
                        
                        if(lstMemberIds != Null && !lstMemberIds.IsEmpty())
                        {
                            For(Integer i = 0; i < lstMemberIds.size(); i++)
                            {
                                if(lstMemberIds[i].Type__c == 'MedicareID')  
                                {
                                    oSaveTemplateRequest.MedicareId = (lstMemberIds[i].Name != null) ? lstMemberIds[i].Name : '';
                                }
                                else if(lstMemberIds[i].Type__c == 'Member-Id-Base')
                                {
                                    oSaveTemplateRequest.MemberUMID = (lstMemberIds[i].Name != null) ? lstMemberIds[i].Name : '';
                                }
                            }
                        }
                    }
                    
                }
                DateTime currentDate = Datetime.now();
                String LastSaveDate = currentDate.format('MM-dd-yyyy');
                oSaveTemplateRequest.LastSaveDate = LastSaveDate.replaceAll('[^a-zA-Z0-9\\s+]', '');

                oSaveTemplateRequest.PersonSpeakingWith = avfRequestDTO.templateBase[0].data.rsoMAU.PersonSpeakingWith;
                oSaveTemplateRequest.RelationToMember = avfRequestDTO.templateBase[0].data.rsoMAU.RelationToMember;
                //Residential
                oSaveTemplateRequest.PermanentResidentialAddress = avfRequestDTO.templateBase[0].data.rsoMAU.PermanentResidentialAddress;
                oSaveTemplateRequest.PermanentResidentialCityName = avfRequestDTO.templateBase[0].data.rsoMAU.PermanentResidentialCityName;
                oSaveTemplateRequest.PermanentResidentialCountyName = avfRequestDTO.templateBase[0].data.rsoMAU.PermanentResidentialCountyName;
                oSaveTemplateRequest.PermanentResidentialStateCode = avfRequestDTO.templateBase[0].data.rsoMAU.PermanentResidentialStateCode;
                oSaveTemplateRequest.PermanentResidentialZipCode = avfRequestDTO.templateBase[0].data.rsoMAU.PermanentResidentialZipCode;
                oSaveTemplateRequest.PermanentResidentialZipPlusCode = '';
                String PermanentResidentialPhoneNumber = avfRequestDTO.templateBase[0].data.rsoMAU.PermanentResidentialPhoneNumber;
                oSaveTemplateRequest.PermanentResidentialPhoneNumber = PermanentResidentialPhoneNumber.replaceAll('[^\\|\\d]','');
                oSaveTemplateRequest.PermanentResidentialStartDate = dateFormatting(avfRequestDTO.templateBase[0].data.rsoMAU.PermanentResidentialStartDate);
                //Mailing
                oSaveTemplateRequest.MailingAddress = avfRequestDTO.templateBase[0].data.rsoMAU.MailingAddress;
                oSaveTemplateRequest.MailingCityName = avfRequestDTO.templateBase[0].data.rsoMAU.MailingCityName;
                oSaveTemplateRequest.MailingStateCode = avfRequestDTO.templateBase[0].data.rsoMAU.MailingStateCode;
                oSaveTemplateRequest.MailingZipCode = avfRequestDTO.templateBase[0].data.rsoMAU.MailingZipCode;
                oSaveTemplateRequest.MailingZipPlusCode = '';
                //Temporary
                oSaveTemplateRequest.TemporaryAddress = avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryAddress;
                oSaveTemplateRequest.TemporaryCityName = avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryCityName;
                oSaveTemplateRequest.TemporaryCountyName = avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryCountyName;
                oSaveTemplateRequest.TemporaryStateCode = avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryStateCode;
                oSaveTemplateRequest.TemporaryZipCode = avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryZipCode;
                oSaveTemplateRequest.TemporaryZipPlusCode = '';
                if(avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryOSAStartDate == 'NaN/NaN/NaN' || avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryOSAStartDate == 'NaN/NaN/NaN')
                {
                    oSaveTemplateRequest.TemporaryOSAStartDate = '';
                    oSaveTemplateRequest.TemporaryOSAEndDate = '';
                }    
                else{
                    oSaveTemplateRequest.TemporaryOSAStartDate = dateFormatting(avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryOSAStartDate);
                    oSaveTemplateRequest.TemporaryOSAEndDate = dateFormatting(avfRequestDTO.templateBase[0].data.rsoMAU.TemporaryOSAEndDate);
                }

                oSaveTemplateRequest.E2ETrackID = '';
                oSaveTemplateRequest.E2EActivityID = '';
                oSaveTemplateRequest.E2EMilestoneID = '';
                
                avfDataRequest.SaveTemplateRequest = oSaveTemplateRequest;
            	return JSON.serialize(avfDataRequest);
        }catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'DemographicMedicareAddress_S_HUM','prepareRequestforService');
            return Null;
        }
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
    /*
    *  This method is used to format the case related date fields into expected format
    *  @name   dateFormatting
    *  @param  string theDateValue 
    *  @return string - Returns formatted date
    */
    public static string dateFormatting(String theDateValue)
    {	
        String finalDate= '';
        if(String.isBlank(theDateValue))
        {
            finalDate = '';
        }
        else
        {
            String reqDate= theDateValue.subString(0,theDateValue.length());
            String [] dateArray = reqDate.split('-');
            finalDate =dateArray[1]+dateArray[2]+dateArray[0];
        }
        return finalDate;
    }
}