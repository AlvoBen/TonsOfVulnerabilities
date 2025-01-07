/******************************************************************************************************************************
Apex Class Name  : DEAA_Process_S_HUM
Version          : 1.0 
Created Date     : November 20 2019
Function         : Service class to handle the ZAPI Service callout for DEAA Cases.                   
Test Class       : DEAA_Process_T_HUM
Modification Log :
   Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Mohammed Noor                           11/20/2019           Original version. 
*********************************************************************************************************************************/
public with sharing class DEAA_Process_S_HUM 
{
		
	/**
	* Invocable method called from the DEAA flow to process BAM service callout after finish of the template.
	*
	* @param    List<List<String>>	list containing CaseID and TemplateSubmissionID	
	* @return   void
	*/
	@InvocableMethod(label='processDEAATemplateAfterFinish' description='Process the DEAA template finish logic')		
	public static void processDEAATemplateAfterFinish(list<list<String>> lstParams)		
	{
		map<Id, set<MET_Milestone__c>> mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();
		try
		{		
			if(lstParams!=null && !lstParams.isEmpty() && !lstParams[0].isEmpty())
			{
				string sCaseID = lstParams[0][0];
				string sSubmissionID =  lstParams[0][1];
				if(String.isNotBlank(sCaseID) && String.isNotBlank(sSubmissionID))
				{
					list<Template_Submission_Data__c> lstTSubData = [SELECT Id, Name__c, Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c = :sSubmissionID AND Name__c = :GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTOPTION];									
					
					if(lstTSubData!=null && !lstTSubData.isEmpty() && String.isNotBlank(lstTSubData[0].value__c))
					{
						if(lstTSubData[0].value__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.DEAA_OPTION_YES))
						{
							mapCaseMilestones.put(sCaseID, new set<MET_Milestone__c>{new MET_Milestone__c(Name=GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTYES_MILESTONE)});							
						}
						else if(lstTSubData[0].value__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.DEAA_OPTION_NO))
						{
							mapCaseMilestones.put(sCaseID, new set<MET_Milestone__c>{new MET_Milestone__c(Name=GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTNO_MILESTONE)});							
						}
					}				
					if(mapCaseMilestones!=null && !mapCaseMilestones.isEmpty())
					{
						system.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));
					}
				}			
			}
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'DEAA_Process_S_HUM', 'processDEAATemplateAfterFinish');	
		}	
	}
	
	/**
	* Method to make callout to ZAPI service for DEAA Cases. 
	*
	* @param	sCaseNumber - CaseNumber to be sent to CI/CMS. 
	* @param	sRealtimeTransTimestamp - TimeStamp of the Member Interaction or Case Closure. 
	* @param	sE2ETrackId - Most latest TrackID on the Case.
	* @param	sOptOutRequired - Part D Opt Out Option. 	
	* @return   HttpResponse - response back from CI/CMS.
	*/
	public static HttpResponse callZAPIServiceForDEAA(String sCaseNumber, String sRealtimeTransTimestamp, String sE2ETrackId, String sOptOutRequired) 
	{		
		HttpResponse oDEAA_Response; 
		DEAA_ZAPIServiceRequest_DTO_HUM oDEAA_Request;
		HUM_Webservice_Callout__c oDEAA_SvcHeaders = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.DEAA_ZAPISERVICE);
		
		if(String.isNotBlank(sCaseNumber) && String.isNotBlank(sRealtimeTransTimestamp) && String.isNotBlank(sE2ETrackId) && String.isNotBlank(sOptOutRequired))
		{
			oDEAA_Request = createZAPIServiceRequest(sCaseNumber, sRealtimeTransTimestamp, sE2ETrackId, sOptOutRequired);			
			if(oDEAA_Request!=null && oDEAA_SvcHeaders!=null)
			{
				String sBody = JSON.serialize(oDEAA_Request);				
				if(String.isNotBlank(sBody))
				{				
					HttpRequest req = new HttpRequest();
					req.setEndpoint(oDEAA_SvcHeaders.End_Point_URL__c);
					req.setMethod(oDEAA_SvcHeaders.Request_Type__c);
					req.setTimeout(Integer.valueOf(oDEAA_SvcHeaders.Timeout__c));
					req.setBody(sBody);
					if (!Test.isRunningTest()) req.setClientCertificateName(oDEAA_SvcHeaders.Certificate_Name__c);
					req.setHeader(Label.HUMServiceTesterController_content_type, oDEAA_SvcHeaders.Content_Type__c);
					Http http = new Http();
					oDEAA_Response = http.send(req);
				}					
			}
		}		
		return oDEAA_Response;		
	}
	
	/**
	* Method to create the Request DTO for ZAPI Service 
	*
	* @param	sCaseNumber - CaseNumber to be sent to CI/CMS. 
	* @param	sRealtimeTransTimestamp - TimeStamp of the Member Interaction or Case Closure. 
	* @param	sE2ETrackId - Most latest TrackID on the Case.
	* @param	sOptOutRequired - Part D Opt Out Option. 	
	* @return   DEAA_ZAPIServiceRequest_DTO_HUM - request DTO for ZAPI service callout.
	*/
	public static DEAA_ZAPIServiceRequest_DTO_HUM createZAPIServiceRequest(String sCaseNumber, String sRealtimeTransTimestamp, String sE2ETrackId, String sOptOutRequired)
	{
		DEAA_ZAPIServiceRequest_DTO_HUM oRequest;
		
		if(String.isNotBlank(sCaseNumber) && String.isNotBlank(sRealtimeTransTimestamp) && String.isNotBlank(sE2ETrackId) && String.isNotBlank(sOptOutRequired))
		{
			oRequest = new DEAA_ZAPIServiceRequest_DTO_HUM();		
			DEAA_ZAPIServiceRequest_DTO_HUM.SavePartDOptOut oSavePartDOptOutRequest = new DEAA_ZAPIServiceRequest_DTO_HUM.SavePartDOptOut();
			oSavePartDOptOutRequest.CaseNumber = sCaseNumber;			
			oSavePartDOptOutRequest.RealtimeTransTimestamp = sRealtimeTransTimestamp;			
			oSavePartDOptOutRequest.E2ETrackId = sE2ETrackId;			
			oSavePartDOptOutRequest.OptOutRequired = sOptOutRequired;
			oRequest.SavePartDOptOutRequest = oSavePartDOptOutRequest;						
		}
		return oRequest;
	}

	/**
	* Future Method to enqueue DEAA Queueable apex job.
	*
	* @param	lstCases - list of case Ids for zAPI callout processing.
	* @return   void
	*/
	@future
	public static void enqueueDEAACases(list<string> lstCases)
	{
		if(lstCases!=null && !lstCases.isEmpty())			
			system.enqueueJob(new DEAA_Process_Q_HUM(lstCases));
	}
}