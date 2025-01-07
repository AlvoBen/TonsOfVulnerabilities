/**************************************************************************************************************************
Component Name   : CaseLoggedInfo_CMP_HUM.component
Version          : 1.0
Created On       : 10/10/2017
Function         : VF Component to display Case Logged information table

Modification Log:
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------------
* Ashsih Upadhyay                                 11/10/2017                   Original version
* Pradeepkumar Dani          149184               11/17/2017                   re-factoring
* Sumit Pharande                                  28/12/2017                   Defect Fix-336409.0001
* Santhosh Ganji                                    04/19/2019                     REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
* Santhosh Ganji                                  05/24/2019                   REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
*  Vijaya Koriprolu                                 11/18/2019                 REQ-383958 added functionality for adding claim numbers on case edit page
*  Prasuna Pattabhi                                 07/28/2022                 Added functionality for displaying Lightning Case Logged details
****************************************************************************************************************************/
public with sharing class CaseLoggedInfoDataTable_C_HUM
{
	public string columnName{ get; set; }
	public string columnHeader{ get; set; }

	/**
	*  Constructor for the class
	*  @name CaseHistoryDataTable_C_HUM
	*  @param NA
	*  @return void
	*  @throws NA
	*/
	public CaseLoggedInfoDataTable_C_HUM()
	{
		columnName = JSON.serialize(new List<String>{ 'Id','subType', 'createdDate', 'createdBy', 'createdByQueue' });
		columnHeader = JSON.serialize(new List<String>{ 'Id','Type ' ,'Created Date', 'Created By' , 'Created by Queue' });
	}
	
	/*
	* Method Name : getJSONData
	* Description : This method is used to delete attachment log record.
	* Return type : void
	* Parameter   : NA
	*/
	@RemoteAction
		public static list <CaseLoggedInfo_DTO_HUM> getJSONData(String sCaseRecordId) {
		return CaseLoggedInfoDataTable_C_HUM.getLogDetails(sCaseRecordId);
	}

	/*
	* Method Name : getLogDetails
	* Description : This method is used to delete attachment log record.
	* Return type : void
	* Parameter   : NA
	*/
	public static List<CaseLoggedInfo_DTO_HUM> getLogDetails(String sCaseId)
	{
		Set<String> wqueues = new Set<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
		Set<String> chatQueues = new Set<String>(GLOBAL_CONSTANT_HUM.WEB_CHAT_WQUEUES);
		list<CaseLoggedInfo_DTO_HUM> loggedInfoList = new list<CaseLoggedInfo_DTO_HUM>();
		list<Attachment_Log__c> attachmentInfoList = [SELECT Id, Attachment_Sub_type__c, Created_BY__c,
            CreatedBy.Name, CreatedDate, CreatedBy.Current_Queue__c,CreatedByQueue__c,Source_System__c
            FROM Attachment_Log__c
            WHERE IsDeleted__c = false AND Case__c = :sCaseId AND Source_System__c in (:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM,:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM_LIGHTNING) AND Attachment_Type__c !=:GLOBAL_CONSTANT_HUM.CLAIMS_MANUAL_ENTRY ORDER BY Id DESC];
		Profile userProfile = [SELECT Name FROM Profile WHERE Id = :UserInfo.getProfileId() limit 1];
		Boolean bhideTheLink = false;
        Case oCase = [SELECT Id, Policy_Member__c,RecordType.name, Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin FROM Case WHERE id = :sCaseId];
        
        if(oCase !=null &&(oCase.Recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || (oCase.Policy_Member__c !=null && oCase.Policy_Member__r.Product__c == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY) || (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN)))
        {
          if(oCase.Recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || wqueues.contains(oCase.Owner_Queue__c) || wqueues.contains(oCase.Created_By_Queue__c) || (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
          {
            bhideTheLink = true;
          }
        }

		for (Attachment_Log__c ai : attachmentInfoList)
		{
			CaseLoggedInfo_DTO_HUM caseInfoDto = new CaseLoggedInfo_DTO_HUM();
			String viewLink = CaseDetailConstants_HUM.sLoggedInfoViewAction + ai.id + CaseDetailConstants_HUM.sComma + ai.Attachment_Sub_type__c + CaseDetailConstants_HUM.sComma + ai.CreatedBy.Current_Queue__c+CaseDetailConstants_HUM.sComma + ai.Source_System__c;
			String deleteLink = CaseDetailConstants_HUM.sLoggedInfoDelAction + ai.id;

			if (userProfile.Name == Label.HUMAgencyCCSupervisor || (Label.PharmacyUserSupervisorYes_HUM.equalsIgnoreCase(Pharmacy_H_HUM.isPharmacyUser()) && bhideTheLink))
			{
				caseInfoDto.id = viewLink + CaseDetailConstants_HUM.sActionSeparator + deleteLink;
			}
			else
			{
				caseInfoDto.id = viewLink + CaseDetailConstants_HUM.sActionSeparator + CaseDetailConstants_HUM.sEMPTY;
			}

			caseInfoDto.subType = ai.Attachment_Sub_type__c;
			caseInfoDto.createdDate = ai.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
			caseInfoDto.createdBy = ai.CreatedBy.Name + CaseDetailConstants_HUM.sSrcUp + ai.CreatedBy.Id;
			caseInfoDto.createdByQueue = ai.CreatedByQueue__c;
			loggedInfoList.add(caseInfoDto);
		}
		return loggedInfoList;
	}

	/*
	* Method Name : deleteLogAttachments
	* Description : This method is used to delete attachment log record.
	* Return type : void
	* Parameter   : NA
	*/
	@RemoteAction
	public static list<CaseLoggedInfo_DTO_HUM> deleteLogAttachments(String sAttchId, String sCaseRecordId)
	{
		if (String.IsNotBlank(sAttchId))
		{
			LoggingDataLogic_D_HUM.deleteLogDetails(sAttchId);
		}
		return CaseLoggedInfoDataTable_C_HUM.getLogDetails(sCaseRecordId);
	}
}