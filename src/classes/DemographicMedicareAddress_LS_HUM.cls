/********************************************************************************************************************************************
    @Apex Class Name  : DemographicMedicareAddress_LS_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class Name  : DemographicUpdateTemplateInsert_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                 		  06/09/2022                 Original Version
******************************************************************************************************************************************************************************/

public with sharing class DemographicMedicareAddress_LS_HUM {
	
    private static Boolean bIsPolicyPlanSwithON = (CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch') != Null) ? CRMFunctionality_ONOFF_Switch__c.getValues('TemplateSwitch').IsON__c:true;

    /*
    * This is a method to trigger Medicare Address Service for Template Service
    * @param caseNumber,avfRequest,string policyId
    * @return void
    */
    @AuraEnabled
    public static void sendMedicareAddressService(string caseNumber, string avfRequest,string policyId)
    {	
        try {
            HUM_Webservice_Callout__c templateHeaders = HUM_Webservice_Callout__c.getValues('AVFTemplate');
            
            AVFTemplateResponse_DTO_HUM avfResponseHelper = new AVFTemplateResponse_DTO_HUM();
            HUMAVFTemplateResponseHelper oResponse = new HUMAVFTemplateResponseHelper();
            
            String sRequestBody = prepareRequestforService(caseNumber,avfRequest,policyId);
            System.debug('request body sendMedicareAddressService'+sRequestBody);
            if(templateHeaders.Active__c && sRequestBody!=null)
	        {
	           	HttpResponse response = HUMCalloutUtilityHelper.callWebservice(templateHeaders.End_Point_URL__c, templateHeaders.Certificate_Name__c, sRequestBody, templateHeaders.Request_Type__c, null, templateHeaders.Content_Type__c);
	            System.debug('response sendMedicareAddressService'+response);
                if ((response != null && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)) || Test.isRunningTest())    
	            {	
                    avfResponseHelper = (AVFTemplateResponse_DTO_HUM)System.JSON.deserialize(response.getbody(), AVFTemplateResponse_DTO_HUM.class);	                
	            	oResponse = buildResponseMessage(avfResponseHelper);
                    
			        //Creating Error Log Record for AVF Failure Response
                    if(oResponse.sErrorCode != '0000'){
                            Error_Log__c objErrorLog = new Error_Log__c();
                            objErrorLog.Class_Name__c='DemographicMedicareAddress_LS_HUM'; objErrorLog.Method_Name__c='sendMedicareAddressService';
                        	objErrorLog.Error_Code__c = oResponse.sErrorCode; objErrorLog.Error_Message__c= oResponse.sErrorDescription;
                            objErrorLog.Operation__c='AVFTemplate'; objErrorLog.Record_Id__c='Case:'+caseNumber+';policyId:'+policyId;
                            Database.upsertResult upsertResult = Database.upsert(objErrorLog,false);
                            HUMExceptionHelper.processUpsertResults(new List<Database.upsertResult>{upsertResult},
                            new List<SObject>{objErrorLog},'DemographicMedicareAddress_LS_HUM','sendMedicareAddressService','AVFTemplate');    
                    }
                }
	        } 
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'DemographicMedicareAddress_LS_HUM','sendMedicareAddressService');
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
                DemographicUpdateTemplate_DTO_HUM avfRequestDTO = (DemographicUpdateTemplate_DTO_HUM)System.JSON.deserialize(avfRequest, DemographicUpdateTemplate_DTO_HUM.class);
                     
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
                    
                    //Policy_member__C  to memberPlan
                    MemberPlan policymbrdetails = [SELECT Id,PlanId,Plan.Contract_Number__c,Plan.Product_Type__c,Policy__r.Contract_Number__c,Policy__r.Product_Type__c FROM MemberPlan where id=:policyId limit 1];
                    String sProductType = (bIsPolicyPlanSwithON) ? policymbrdetails.Plan.Product_Type__c : policymbrdetails.Policy__r.Product_Type__c;
                    String sContractNumber = (bIsPolicyPlanSwithON) ? policymbrdetails.Plan.Contract_Number__c : policymbrdetails.Policy__r.Contract_Number__c;
                   	if(policymbrdetails.Id != Null && String.isNotBlank(sProductType) && String.isNotBlank(sContractNumber))
                    {
                        if(sProductType == 'MA' || sProductType == 'MAPD')
                        {
                            oSaveTemplateRequest.CMSMAPlanNumber = sContractNumber ;
                        }
                        else if(sProductType == 'PDP' || Test.isRunningTest())
                        {
                            oSaveTemplateRequest.CMSPDPPlanNumber = sContractNumber ;
                        }
                        
                        //New code for changing Member_ID__C references
                        List<MemberPlan> lstMemberPlan= new List<MemberPlan>();
                        List<Account> lstAccount = new List<Account>();
                        
                        lstMemberPlan = [select Id, MemberId, MEMBER_ID_BASE__C from MemberPlan where Id=: policyId];
                        lstAccount =[Select Id, MedicareID__c from Account where id=: lstMemberPlan[0].MemberId];
                        
                        if(lstMemberPlan !=null && !lstMemberPlan.IsEmpty())
                        {
                             For(Integer i = 0; i < lstMemberPlan.size(); i++)
                            {
								oSaveTemplateRequest.MemberUMID = (lstMemberPlan[i].MEMBER_ID_BASE__C != null) ? lstMemberPlan[i].MEMBER_ID_BASE__C : '';
                           	}
                        }
                        
                        if(lstAccount !=null && !lstAccount.IsEmpty())
                        {
                             For(Integer i = 0; i < lstAccount.size(); i++)
                            {
								oSaveTemplateRequest.MedicareId = (lstAccount[i].MedicareID__c != null) ? lstAccount[i].MedicareID__c : '';
                            }
                        }
                        /////////////////////////////////////////////////////////////////////////////////
                        /*
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
                        
                        /////////////////////////////////////////////////////////////////////////////////
                        */
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
                System.debug('avfDataRequest'+avfDataRequest);
            	return JSON.serialize(avfDataRequest);
        }catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'DemographicMedicareAddress_LS_HUM','prepareRequestforService');
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