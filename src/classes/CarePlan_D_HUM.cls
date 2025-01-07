/******************************************************************************************************************
Apex class Name    : CarePlan_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for care plan related activities
Created On         : May 28 2021
Test Class         : CarePlanService_T_HUM

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        05/28/2021          Original Version
*   1.1            Moshitha Gunasekaran        07/27/2021          Made changes to add CarePlanSubject
*   1.2            Moshitha Gunasekaran        08/04/2021          User Story 2495909:  Made changes to add CarePlan SubType in query while getting careplans
*   1.3            Moshitha Gunasekaran        09/20/2021          User Story 2695849:  Member Case View - 60 days from the closed date
*   1.4            Moshitha Gunasekaran        11/10/2021          User Story 2854233:  Member Case View - 12 months from the closed date and Goals 100% should be passed in response
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CarePlan_D_HUM {
	
	public static list<String> lstCaseStatus = New List<String>{'Pending - Unassigned','Pending - Response','In Progress'};	
	public static list<String> lstClosedCaseStatus = New List<String>{'Closed'};	
	public static list<String> lstCarePlanSubTypes = New List<String>{'Life Coach Support', 'Tobacco Support', 'Weight Support', 'PHC Support', 'Financial Support'};	
	
	/*
	 * Method name : getCarePlans
	 * Description : This method is to get care plans based on inputs from Go365
	 * Return Type : GetCarePlanRequestDTO
	 * Parameter   : GetCarePlanResponseDTO
	 */
    public static GetCarePlanResponseDTO getCarePlans(GetCarePlanRequestDTO getCarePlanRequest)
    {
    	GetCarePlanResponseDTO getCarePlanResponse = new GetCarePlanResponseDTO();
    	
    	try
    	{
	    	if(string.isNotBlank(getCarePlanRequest.PersonId))
	        {
	        	list<Account> lstAccount = [SELECT Id, Name, Enterprise_ID__c,ETL_Record_Deleted__c, PersonHomePhone, PersonOtherPhone FROM Account WHERE Enterprise_ID__c = :getCarePlanRequest.PersonId and ETL_Record_Deleted__c = false limit 1];
	        	if(lstAccount != null && !lstAccount.isEmpty())
	        	{
	        		getCarePlanResponse.Success = true;
	        		getCarePlanResponse.MemberName = lstAccount[0].Name;
	        		getCarePlanResponse.MemberSFID = lstAccount[0].Id;
	        		if(string.isNotBlank(lstAccount[0].PersonHomePhone))
	        		{
	        			getCarePlanResponse.PhoneNumber = lstAccount[0].PersonHomePhone;
	        		}
	        		else
	        		{
	        			getCarePlanResponse.PhoneNumber = lstAccount[0].PersonOtherPhone;
	        		}
	        		if(string.isNotBlank(getCarePlanRequest.CarePlanType))
			        {
			        	getCarePlanResponse.CarePlans = PopulateCarePlans(lstAccount[0].Id, getCarePlanRequest.CarePlanType);
			        }
			        else
			        {
			        	getCarePlanResponse.Success = false;
			        	getCarePlanResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.CarePlanErrorMessage;
			        }
	        	} 
	        	else
	        	{
	        		getCarePlanResponse.Success = false;
		        	getCarePlanResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.PersonIdErrorMessage;
		        }
	        }
	        else
	        {
	        	getCarePlanResponse.Success = false;
	        	getCarePlanResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.PersonIdErrorMessage;
	        }
    	}
    	catch(Exception ex)
    	{
    		getCarePlanResponse.Success = false;
	        getCarePlanResponse.ErrorMessage = ex.getMessage();
	        List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CarePlan_D_HUM','getCarePlans');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            }
        }
        
    	return getCarePlanResponse;
    }
    
    /*
	 * Method name : IsCoachingEnrolled
	 * Description : This method is to check if member is enrolled for coaching from Go365
	 * Return Type : GetCarePlanRequestDTO
	 * Parameter   : GetCarePlanResponseDTO
	 */
    public static GetCarePlanResponseDTO IsCoachingEnrolled(GetCarePlanRequestDTO coachEnrollRequest)
    {
    	GetCarePlanResponseDTO isCoachEnrollResponse = new GetCarePlanResponseDTO();
    	
    	try
    	{
	    	if(string.isNotBlank(coachEnrollRequest.PersonId))
	        {
	        	list<Account> lstAccount = [SELECT Id, Name, Enterprise_ID__c,ETL_Record_Deleted__c, PersonHomePhone, PersonOtherPhone FROM Account WHERE Enterprise_ID__c = :coachEnrollRequest.PersonId and ETL_Record_Deleted__c = false limit 1];
	        	if(lstAccount != null && !lstAccount.isEmpty())
	        	{
	        		Id caseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_CH_HUM.sCarePlanRecord).getRecordTypeId();
			    	Id acctId = lstAccount[0].Id;
			    	list<case> lstCases = [select Id, RecordTypeId, OwnerId, Owner.Name, Type, SubType__c, Area_of_Focus__c, 
			    						Status, AccountId, ClosedDate from Case where AccountId = :acctId and RecordTypeId = :caseRecordType and SubType__c in :lstCarePlanSubTypes and 
			    						(Status in :lstCaseStatus or (Status in :lstClosedCaseStatus and ClosedDate >= :System.Today().addMonths(-12))) order by createddate desc];
			    	if(lstCases != null && !lstCases.isEmpty())
			    	{
			    		isCoachEnrollResponse.Enrolled = true;
		        	}
			    } 
	        	else
	        	{
	        		isCoachEnrollResponse.Enrolled = false;
		        	isCoachEnrollResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.PersonIdErrorMessage;
		        }
	        }
	        else
	        {
	        	isCoachEnrollResponse.Enrolled = false;
	        	isCoachEnrollResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.PersonIdErrorMessage;
	        }
    	}
    	catch(Exception ex)
    	{
    		isCoachEnrollResponse.Enrolled = false;
	        isCoachEnrollResponse.ErrorMessage = ex.getMessage();
	        List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CarePlan_D_HUM','getCarePlans');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            }
        }
        
    	return isCoachEnrollResponse;
    }
	
	/*
	 * Method name : PopulateCarePlans
	 * Description : This method is to populate care plans based on inputs from Go365
	 * Return Type : list<GetCarePlanResponseDTO.CarePlanDetailDTO
	 * Parameter   : Id, String
	 */
    private static list<GetCarePlanResponseDTO.CarePlanDetailDTO> PopulateCarePlans(Id acctId, string carePlanType)
    {
    	list<GetCarePlanResponseDTO.CarePlanDetailDTO> lstCarePlans = new list<GetCarePlanResponseDTO.CarePlanDetailDTO>();
    	
    	list<case> lstCases;
    	Id caseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_CH_HUM.sCarePlanRecord).getRecordTypeId();
    	if(carePlanType.equalsIgnoreCase('All'))
    	{
    		lstCases = [select Id, RecordTypeId, OwnerId, Owner.Name, Type, SubType__c, Area_of_Focus__c, AccountId, Status, CreatedDate, ClosedDate from Case where AccountId = :acctId and RecordTypeId = :caseRecordType
			            and SubType__c in :lstCarePlanSubTypes and (Status in :lstCaseStatus or (Status in :lstClosedCaseStatus and ClosedDate >= :System.Today().addMonths(-12))) order by createddate desc];
    	}
    	else
    	{
    		lstCases = [select Id, RecordTypeId, OwnerId, Owner.Name, Type, SubType__c, Area_of_Focus__c, AccountId, Status, CreatedDate, ClosedDate from Case where AccountId = :acctId and 
    					Type = :carePlanType and RecordTypeId = :caseRecordType and SubType__c in :lstCarePlanSubTypes and (Status in :lstCaseStatus or (Status in :lstClosedCaseStatus and ClosedDate >= :System.Today().addMonths(-12))) order by createddate desc];
    	}
    	
    	if(lstCases != null && !lstCases.isEmpty())
    	{
	    	set<string> setCaseIds = new set<string>();
	    	for(Case ocase :lstCases)
	    	{
	    		setCaseIds.add(ocase.Id);
	    	}
    	
	    	map<string,list<GetCarePlanResponseDTO.GoalDetailDTO>> mapCarePlanGoals = PopulateGoals(setCaseIds);
	    	map<string,list<GetCarePlanResponseDTO.MemberTaskDetailDTO>> mapCarePlanMemTasks = PopulateMemberTasks(setCaseIds);
	    	
	    	for(Case ocase :lstCases)
	    	{
	    		GetCarePlanResponseDTO.CarePlanDetailDTO oCarePlan = new GetCarePlanResponseDTO.CarePlanDetailDTO();
	    		oCarePlan.CarePlanId = oCase.Id;
	    		oCarePlan.CaseOwner = oCase.Owner.Name;
	    		oCarePlan.CarePlanType = oCase.Type;
			oCarePlan.CarePlanSubType = oCase.SubType__c;
	                oCarePlan.CarePlanSubject = oCase.Area_of_Focus__c;
	    		oCarePlan.AreaFocus = oCase.Area_of_Focus__c;
	    		oCarePlan.Status = oCase.Status;
	    		oCarePlan.CreatedDateTime = String.valueOf(oCase.CreatedDate);
	    		if(null != mapCarePlanGoals && mapCarePlanGoals.containsKey(oCase.Id)) oCarePlan.Goals = mapCarePlanGoals.get(oCase.Id);
	    		if(null != mapCarePlanMemTasks && mapCarePlanMemTasks.containsKey(oCase.Id)) oCarePlan.MemberTasks = mapCarePlanMemTasks.get(oCase.Id);
	    		lstCarePlans.add(oCarePlan);
	    	}
    	}
    	 
    	return lstCarePlans;
    }
	
	/*
	 * Method name : PopulateGoals
	 * Description : This method is to populate goals based on inputs from Go365
	 * Return Type : map<string,list<GetCarePlanResponseDTO.GoalDetailDTO>>
	 * Parameter   : set<String>
	 */
    private static map<string,list<GetCarePlanResponseDTO.GoalDetailDTO>> PopulateGoals(set<string> setCaseIds)
    {
    	map<string,list<GetCarePlanResponseDTO.GoalDetailDTO>> mapGoals = null;
    	
    	list<HealthCloudGA__CarePlanGoal__c> lstCarePlanGoals = [SELECT Id, Name, createddate, HealthCloudGA__Progress__c, HealthCloudGA__Category__c, 
    													HealthCloudGA__Description__c, HealthCloudGA__Priority__c, HealthCloudGA__SortOrder__c, 
    													HealthCloudGA__CarePlan__c FROM HealthCloudGA__CarePlanGoal__c where HealthCloudGA__CarePlan__c in :setCaseIds order by createddate desc];
    	
    												
        if(lstCarePlanGoals != null && !lstCarePlanGoals.isEmpty())
        {
        	mapGoals = new map<string,list<GetCarePlanResponseDTO.GoalDetailDTO>>();
        	
        	for(HealthCloudGA__CarePlanGoal__c oCarePlanGoal :lstCarePlanGoals)
        	{
        		GetCarePlanResponseDTO.GoalDetailDTO oGoalDetail = new GetCarePlanResponseDTO.GoalDetailDTO();
        		oGoalDetail.GoalId = oCarePlanGoal.Id;
        		oGoalDetail.GoalName = oCarePlanGoal.Name;
        		oGoalDetail.Progress = oCarePlanGoal.HealthCloudGA__Progress__c != null ? string.valueOf(oCarePlanGoal.HealthCloudGA__Progress__c) : '';
        		oGoalDetail.Category = oCarePlanGoal.HealthCloudGA__Category__c;
        		oGoalDetail.Description = oCarePlanGoal.HealthCloudGA__Description__c;
        		oGoalDetail.Priority = oCarePlanGoal.HealthCloudGA__Priority__c;
				oGoalDetail.SortOrder = oCarePlanGoal.HealthCloudGA__SortOrder__c != null ? string.valueOf(oCarePlanGoal.HealthCloudGA__SortOrder__c) : '';
				oGoalDetail.CreatedDateTime = String.valueOf(oCarePlanGoal.CreatedDate);
        		if(!mapGoals.containsKey(oCarePlanGoal.HealthCloudGA__CarePlan__c))
                {
                    mapGoals.put(oCarePlanGoal.HealthCloudGA__CarePlan__c,new list<GetCarePlanResponseDTO.GoalDetailDTO>{oGoalDetail});
                }
                else
                {
                    mapGoals.get(oCarePlanGoal.HealthCloudGA__CarePlan__c).add(oGoalDetail);
                }
            }
        }
        
    	
    	return mapGoals;
    }
	
	/*
	 * Method name : PopulateMemberTasks
	 * Description : This method is to populate Member Tasks based on inputs from Go365
	 * Return Type : map<string,list<GetCarePlanResponseDTO.MemberTaskDetailDTO>>
	 * Parameter   : set<String>
	 */
    private static map<string,list<GetCarePlanResponseDTO.MemberTaskDetailDTO>> PopulateMemberTasks(set<string> setCaseIds)
    {
    	map<string,list<GetCarePlanResponseDTO.MemberTaskDetailDTO>> mapMemberTasks = null;
		
    	Id taskRecordType = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_CH_HUM.sCoachMemberTask).getRecordTypeId();
    	
    	list<Task> lstMemberTasks = [SELECT Id, RecordTypeId, WhatId, Subject, ActivityDate, Status, Priority, 
    									Description, RecordType.Name, Createddate, CreatedById, CreatedBy.Name, HealthCloudGA__CarePlanGoal__c FROM Task where RecordTypeId = :taskRecordType and WhatId in :setCaseIds order by createddate desc ];
    									
        if(lstMemberTasks != null && !lstMemberTasks.isEmpty())
        {
        	mapMemberTasks = new map<string,list<GetCarePlanResponseDTO.MemberTaskDetailDTO>>();
        	
        	for(Task oMemberTask :lstMemberTasks)
        	{
				if(string.isNotBlank(oMemberTask.HealthCloudGA__CarePlanGoal__c))
				{
					GetCarePlanResponseDTO.MemberTaskDetailDTO oMemberTaskDetail = new GetCarePlanResponseDTO.MemberTaskDetailDTO();
					oMemberTaskDetail.TaskId = oMemberTask.Id;
					oMemberTaskDetail.GoalId = oMemberTask.HealthCloudGA__CarePlanGoal__c;
					oMemberTaskDetail.TaskType = oMemberTask.RecordType.Name;
					oMemberTaskDetail.AssignedTo = oMemberTask.CreatedBy.Name;
					oMemberTaskDetail.Subject = oMemberTask.Subject;
					oMemberTaskDetail.DueDate = oMemberTask.ActivityDate != null ? string.valueOf(oMemberTask.ActivityDate) : '';
					oMemberTaskDetail.Status = oMemberTask.Status;
					oMemberTaskDetail.Priority = oMemberTask.Priority;
					oMemberTaskDetail.Comments = oMemberTask.Description;
					oMemberTaskDetail.CreatedDateTime = String.valueOf(oMemberTask.CreatedDate);
					if(!mapMemberTasks.containsKey(oMemberTask.WhatId))
					{
						mapMemberTasks.put(oMemberTask.WhatId,new list<GetCarePlanResponseDTO.MemberTaskDetailDTO>{oMemberTaskDetail});
					}
					else
					{
						mapMemberTasks.get(oMemberTask.WhatId).add(oMemberTaskDetail);
					}
				}
        	}
        }
    	
    	return mapMemberTasks;
    }
}