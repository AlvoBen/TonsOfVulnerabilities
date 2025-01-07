/******************************************************************************************************************
Apex class Name    : TrackService_H_HUM
Version            : 1.0
Function           : Helper Class to process the Milestone details and call the Medicare Enrollment Track Service
Created On         : June 15,2017
Test Class         : TrackService_T_HUM

Modification Log:
*  Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*  Lalit Jain                   114278          21/07/2017          Original version: REQ - 321922
*  Pradeepkumar Dani			114278			26/07/2017			Case Milestone related changes: REQ - 315927
*  Lalit Jain                   115508          01/08/2017          Added some additional fields in the request
*  Mohammed Noor                122577          23/08/2017          Fix to handle Unknown Member Cases properly 
*  Pradeepkumar Dani		    131258        	09/21/2017			Multiple task milestone enhancement.
*  Sumit Kumar                  206605          03/28/2018          Creditable Coverage service added
*  Manoj Chaurasia              202594          03/29/2018          REQ - 354470 Ability to display TAS combination in CRM
*  Mohammed Noor                230884          05/07/2018          REQ - 350470 - Election Type Code display for Medicare Enrollment Cases
* Suresh Gottipati              236924          5/31/2018           REQ - 236924 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field
*  Bharath Rajappan                             05/31/2018 			REQ - 361863 - send follow up due date to track grace period
*  Mohammed Noor                245750          06/19/2018          Fix the issue of Milestones not written for Multiple MET T/A/S entries. 
* Syed Mubarak									07/26/2018			Fix to send the previous subtype for stop milestone and TAS added with previous subtype
* Bharath Rajappan								09/28/2018			Fix Milestone multiple en-queue error by seperating the Crediatble Coverage case with Other Medicare case
*  Mohammed Noor		        350978			04/12/2019			Changes to support TRR Process BAM tracking 
* Santhi Mandava                                04/15/2019          Replaced "Member_Id_Value__c" with Name
* Mohammed Noor                 356538          05/17/2019          Add the missing Case fields in SOQL Query.
* Mohammed Noor                                 08/21/2019          REQ - 406743 - Fix Incorrect Error Log issue and handling of failures for Bulk Cases/Templates.
* Mohammed Noor                                 02/19/2020          REQ - 410422 - Changes done to call ZAPI service for DEAA cases.
* Pooja Kumbhar								    05/05/2021			REQ - 2254207 - case management checkkmarx remediation
------------------------------------------------------------------------------------------------------------------- */
public with sharing class TrackService_H_HUM
{
	private String sTaskID;
	private ID caseID;
	private Task oTaskRec;
	private map<Id, set<MET_Milestone__c>> mapCaseMilestones;
	private list<Id> lstTaskIds;
	private map<string, map<string, set<Enrollment_Track_History__c>>> mapCaseEnrollHistory;
	private set<Id> setCaseIDs;	//This variable is added specifically for DEAA cases. Do not use in future for other cases.

	/**
	* Constructor of the class to Instantiate the variables.
	*
	* @param    lstTaskIds					List of Task Record Ids
	* @param    mapCaseMilestonesParam      map of milestones set with key as caseId
	* @return   na
	*/
	public TrackService_H_HUM(list<Id> lstTaskIds, map<Id, set<MET_Milestone__c>> mapCaseMilestonesParam)
	{
	   
		this.lstTaskIds = lstTaskIds; 
		if (mapCaseMilestonesParam != null)
		{
			this.mapCaseMilestones = mapCaseMilestonesParam;	
		}
	}
	
	/**
	* Constructor of the class to Instantiate the variables.
	*
	* @param    mapCaseEnrollHistory		Map of case and its Enrollment History Recs by SourceID
	* @return   na
	*/
	public TrackService_H_HUM(map<string, map<string, set<Enrollment_Track_History__c>>> mapCaseEnrollHistory)
	{	   	
		if(mapCaseEnrollHistory!=null)
		{
			this.mapCaseEnrollHistory = mapCaseEnrollHistory;
		}
	}
	
	/**
	* Constructor of the class to Instantiate the variables.
	*
	* @param    sTaskID						Task record ID
	* @param    mapCaseMilestonesParam      map of milestones set with key as caseId
	* @return   na
	*/
	public TrackService_H_HUM(String sTaskID, map<Id, set<MET_Milestone__c>> mapCaseMilestonesParam)
	{
		this.sTaskID = sTaskID;
		if (mapCaseMilestonesParam != null)
		{
			this.mapCaseMilestones = mapCaseMilestonesParam;	
		}
	}

	/**
    * Constructor of the class to Instantiate the variables.
    * 
    * @param    setCaseIDs   set of CaseIDs 
	* @return   na
    */
	public TrackService_H_HUM(set<Id> setCaseIDs)
    {
		this.setCaseIDs = setCaseIDs;
	}

    /**
	Method to will call the WriteTrack method based on the Task or Case milestone details.
	*
	* @param    na
	* @return   na
	*/
	public void callTrackServiceBasedOnTaskOrMstoneSet()
	{
		list<Case> lstCaseRec;	
		Case oCaseRec;
		Enrollment_Track_History__c oTrackSerHis;	
		Member_ID__c oMemID;
		set<String> subTypesSet;
		map<Id, Member_ID__c> mapCaseMemebrSSN;
		try
		{
			if(mapCaseMilestones <> null && !mapCaseMilestones.isEmpty())
			{
				map<Id, Case> mapCases = new map<Id, Case>([Select CaseNumber, Type, SubType__c,Classification_Type__c, Policy_Member__c,Policy_Member__r.Name, Case.Account.FirstName, Case.Account.LastName, Medicare_Track_ID__c, CreatedDate, Status, Election_Type_Code__c,Guided_Process_Flag__c,Follow_up_Due_Date__c, RecordType.Name, Origin from Case where ID IN : mapCaseMilestones.keySet()]);
				if (lstTaskIds != null && !lstTaskIds.isEmpty()) mapCaseMilestones = getTaskMilestones(lstTaskIds.get(0), mapCases, mapCaseMilestones);
				mapCaseMemebrSSN = getCaseMemebrSSNMap(mapCases);
				subTypesSet = getMileStoneTSSubTypes();
				
                //Seperate creditable Coverage case from other case 
                Set<Id> CCcaseIdSet = new Set<Id>();
                Set<String> caseIdCCEligibleSet = new Set<String>();
				
				//Separate DEAA cases
                set<Id> setDEAACases = new set<Id>();
                set<string> setZAPICases = new set<string>();  

                //This will ensure that the case will call CICMS on case creation only. 
				//This will avoid CICMS call when task/Action source or Subtype change criteria
				if(lstTaskIds == null)
				{
                    for(Case caseObj : mapCases.Values())
                    {
                        if(caseObj.Status == GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE && caseObj.Subtype__c == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE && caseObj.Guided_Process_Flag__c) CCcaseIdSet.add(caseObj.Id);                        
						
						if(String.isNotBlank(caseObj.Origin) && GLOBAL_CONSTANT_HUM.DEAA_CASEORIGIN.equalsIgnoreCase(caseObj.Origin))	setDEAACases.add(caseObj.Id);
                    }
				
                    //Filter only eligible Creditable Coverage case from other case to avoid multiple equeue on task action source functionality.
				    //Check the Creditable Coverage - CICMS call is completed or Not.
                    if(CCcaseIdSet.size()>0)
                    {
                        for(Template_Submission_Data__c submissionDataObj : [SELECT Value__c,Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.ATTESTATION_TEMPLATE_NAME AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c IN :CCcaseIdSet AND Template_Field__r.Name = :GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_CICMSCALLFIELD])
                        {
						    //Submission data vlaue will be 'NO' if the CICMS call is not made
                            if(submissionDataObj.Value__c == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_NO) 
                            {
                                caseIdCCEligibleSet.add(submissionDataObj.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c);
                            }
                        }
                    }
					
					//filter only cases that have the DEAA template attached and the ZAPI service callout has not been made yet.
					if(setDEAACases!=null && !setDEAACases.isEmpty())
                    {
                    	for(Template_Submission_Data__c tSubData : [SELECT Id, Name__c, Value__c, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.DEAA_Template 
								  AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c IN :setDEAACases AND Name__c = :GLOBAL_CONSTANT_HUM.DEAA_ISZAPISERVICECALLED])
						{
							if(String.isNotBlank(tSubData.Value__c) && GLOBAL_CONSTANT_HUM.DEAA_OPTION_NO.equalsIgnoreCase(tSubData.Value__c))
								setZAPICases.add(tSubData.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c);	
						}
                    }
                }
				for(Id csId : mapCaseMilestones.keySet())
				{
					caseID = csId;	
					oCaseRec = mapCases.get(csId);
						oMemID = (mapCaseMemebrSSN.get(oCaseRec.Policy_Member__c) <> null) ? mapCaseMemebrSSN.get(oCaseRec.Policy_Member__c) : new Member_ID__c(Name = '');
                    
					
					set<MET_Milestone__c> mstoneNamesSet = mapCaseMilestones.get(csId);
					if(mstoneNamesSet <> null && !mstoneNamesSet.isEmpty())
					{
						for(MET_Milestone__c oMstone: mstoneNamesSet)
						{
							oTrackSerHis = getHistoryRecord(oCaseRec, oMstone, oMemID);
							if (writeMstoneToTrackService(oCaseRec, oMstone, oMemID, subTypesSet, oTrackSerHis,caseIdCCEligibleSet, setZAPICases))
							{
								mstoneNamesSet.remove(oMstone);
								if (mstoneNamesSet != null && !mstoneNamesSet.isEmpty()) mapCaseMilestones.put(csId, mstoneNamesSet);
								else if(lstTaskIds != null && !lstTaskIds.isEmpty())
								{
									lstTaskIds.remove(0);
									if(lstTaskIds.isEmpty()) mapCaseMilestones.remove(csId);
								}
								else mapCaseMilestones.remove(csId);
							}
							else //if any failure, remove all the remaining Milestones for the Case(as we need to chain the milestones) and enqueue for remaining Cases.
							{
								mapCaseMilestones.remove(csId);
							}	
							if (!Test.isRunningTest() && !mapCaseMilestones.isEmpty()) 
							{
								if(lstTaskIds != null && !lstTaskIds.isEmpty()) system.enqueueJob(new TrackService_Q_HUM(lstTaskIds, csId));
								else system.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));									
							}														
					        break;
						}
					}
					else if((mstoneNamesSet == null || mstoneNamesSet.isEmpty()) && lstTaskIds!= null && !lstTaskIds.isEmpty())
					{
						lstTaskIds.remove(0);
						if(lstTaskIds.isEmpty()) mapCaseMilestones.remove(csId);
						if (!Test.isRunningTest() && !mapCaseMilestones.isEmpty()) 
						{
							if(lstTaskIds != null && !lstTaskIds.isEmpty()) system.enqueueJob(new TrackService_Q_HUM(lstTaskIds, csId));
						}
									
					}
					break;
				}
			}
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'callTrackServiceBasedOnTaskOrMstoneSet');			
		}
	}
	
	/**
	* Method to get the milestones in the form of map<Id, set<MET_Milestone__c>>
	*
	* @param    sTaskID				Task record id
	* @param    mapCases			Map of cases with key as caseId
	* @param    mapCaseMilestones   Map of milestones sets with key as caseId
	* @return   mapCaseMilestones   Updated map of milestones sets with key as caseId
	*/
	private map<Id, set<MET_Milestone__c>> getTaskMilestones(string sTaskID, map<Id, Case> mapCases, map<Id, set<MET_Milestone__c>> mapCaseMilestones)
	{
		list<Task> lstTaskRec = [Select ID, MET_Task__c, MET_Source__c, MET_Action__c, WhatID from Task where ID =: sTaskID];
		oTaskRec = lstTaskRec.get(0);
		Case oCaseRec = mapCases.values().get(0);		
		list<MET_Milestone__c> mstonesList = [select ID,Name,Case_Subtype__c, Task__c, Action__c, Source_Name__c,MET_Milestone__c.Task__r.Name, MET_Milestone__c.Action__r.Name, MET_Milestone__c.Source_Name__r.Name from MET_Milestone__c where Case_Type__c =: oCaseRec.Type and Case_Subtype__c =:oCaseRec.SubType__c and Task__c =: oTaskRec.MET_Task__c and Source_Name__c =: oTaskRec.MET_Source__c and Action__c =: oTaskRec.MET_Action__c and Is_Active__c = true and Is_Milestone__c = true Limit 1];
        set<MET_Milestone__c> mstonesSet = new set<MET_Milestone__c>();
		for(MET_Milestone__c oMstone: mstonesList)
		{
			mstonesSet.add(oMstone);
		}
		mapCaseMilestones.put(oCaseRec.Id, mstonesSet);
		return mapCaseMilestones;
	}
	
	/**
	* Method to get SSN memeber_id__c record sObject in map structure
	*
	* @param    mapCases			Map of cases with key as caseId
	* @return   mapCaseMemebrSSN    Map of Memeber_ID__c with key as policy member
	*/
	private map<Id, Member_ID__c> getCaseMemebrSSNMap(map<Id, Case> mapCases)
	{
		map<Id, Member_ID__c> mapCaseMemebrSSN = new map<Id, Member_ID__c>();
		set<Id> setPolIds = new set<id>();
		try
		{
			if (mapCases <> null && !mapCases.isEmpty())
			{
				for (Case cs : mapCases.values())
				{
					if (cs.Policy_Member__c <> null) setPolIds.add(cs.Policy_Member__c);
				}
			}			
			if (!setPolIds.isEmpty())
			{
				
                    for (Member_ID__c memId : [Select  Name, Policy_Member__c from Member_ID__c where Policy_Member__c IN :setPolIds and ETL_Record_Deleted__c = false and Type__c = :GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM limit :limits.getLimitQueryRows()])
				 {
					mapCaseMemebrSSN.put(memId.Policy_Member__c, memId);
				 }
				
				
			}
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'getCaseMemebrSSNMap');
		}
		return mapCaseMemebrSSN;
	}
				
    /**
	* Method to send the milestone to Track Service and process the response.
	*
	* @param    oCaseRec        Case record sObject
	* @param    oMstone         Case record sObject
	* @param    oMemID			Case record sObject
	* @param    subTypesSet		Case record sObject
	* @param    oTrackSerHis	Case record sObject
	* @param    caseIdCCEligibleSet	set of Creditable Coverage caseIDs for CI/CMS callout.
	* @param    setZAPICases	set of DEAA caseIDs for ZAPI Service callout.
	* @return   boolean			Boolean result of the callout
	*/
	@TestVisible
    private Boolean writeMstoneToTrackService(Case oCaseRec, MET_Milestone__c oMstone, Member_ID__c oMemID, Set<String> subTypesSet, Enrollment_Track_History__c oTrackSerHis,Set<String> caseIdCCEligibleSet, set<string> setZAPICases)
    {
		TrackServiceWriteTrackResponse_DTO_HUM oRes;
		TrackServiceWriteTrackRequest_DTO_HUM oWriteTrackRequest;
		String sLOBCode = GLOBAL_CONSTANT_HUM.TRACKSERVICE_DEFAULT_LOBCODE_HUM;
        String sFirstName ='';
        String sLastName = '';
		try
		{
            String sSourceId = (oTrackSerHis.Source_Track_Id__c != null) ? oTrackSerHis.Source_Track_Id__c : (oCaseRec.Medicare_Track_ID__c != null) ? oCaseRec.Medicare_Track_ID__c : '' ;
            String sCaseNumber = oCaseRec.CaseNumber;
            String sType = (oCaseRec.Type != null) ? oCaseRec.Type : '';
            String sSubType = String.isNotBlank(oMstone.Case_Subtype__c) ? oMstone.Case_Subtype__c: oCaseRec.SubType__c;
			String sETCType = oCaseRec.Election_Type_Code__c;
            String sStatusCode = oCaseRec.Status.trim().left(7);
            String sMemberID = (oCaseRec.Policy_Member__c != null) ? oCaseRec.Policy_Member__r.Name : '';
            String sMilestoneTS = getMileStoneTSinEST(oCaseRec, subTypesSet);
            String sTask=(oMstone.Task__c!=null) ? oMstone.Task__r.Name:'';
            String sAction =(oMstone.Action__c!=null) ? oMstone.Action__r.Name:'';
            String sSource =(oMstone.Source_Name__c!=null) ? oMstone.Source_Name__r.Name:''; 
            String sFollowUpDueDate = (oCaseRec.Follow_up_Due_Date__c != Null && oCaseRec.RecordType.Name == GLOBAL_CONSTANT_HUM.MEDICARE_CASE) ? String.valueOf(oCaseRec.Follow_up_Due_Date__c) : ''  ;
            
            if (oCaseRec.Account != null) {
                sFirstName = (oCaseRec.Account.FirstName != null) ? oCaseRec.Account.FirstName : '';
                sLastName = (oCaseRec.Account.LastName != null) ? oCaseRec.Account.LastName : '';           
            }
            
            oWriteTrackRequest = TrackService_S_HUM.createWriteTrackRequest(sLOBCode, oMemID.Name , sSourceID, oMstone.Name, sMilestoneTS, sCaseNumber, sType, sSubType, sFirstName, sLastName, sStatusCode, sMemberID, sTask, sAction, sSource, sETCType, sFollowUpDueDate);
            
			Boolean bService = false;
			if (oWriteTrackRequest != null && oTrackSerHis != null)
            {
				 oRes = TrackService_S_HUM.callTrackServiceWriteCaseMethod(oWriteTrackRequest);
                bService = processResponse(oRes, oCaseRec, oTrackSerHis);
                    
                if(oCaseRec.Classification_Type__c == GLOBAL_CONSTANT_HUM.HUMCCPEnrollmentRSO && oCaseRec.Guided_Process_Flag__c && oCaseRec.Subtype__c == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE )
                {
                    //Enqueue CICMS Service 
                    if(!Test.isRunningTest() && bService && caseIdCCEligibleSet.contains(oCaseRec.Id)) System.enqueueJob(new CreditableCoverageServiceCall_H_HUM(oCaseRec.Id));
                }
				if(String.isNotBlank(oCaseRec.Origin) && GLOBAL_CONSTANT_HUM.DEAA_CASEORIGIN.equalsIgnoreCase(oCaseRec.Origin) && setZAPICases!=null 
                && !setZAPICases.isEmpty() && setZAPICases.contains(oCaseRec.Id))
                {
                	//Enqueue for zAPI Service callout irrespective of Track Service success or failure. The latest trackID on the case wil be
                	//to CI/CMS in zAPI Service callout  
                	if(!Test.isRunningTest())	System.enqueueJob(new DEAA_Process_Q_HUM(new list<string>{oCaseRec.Id}));
                }                
                 
                return bService;
            }
		}
		catch(Exception ex)
		{
			processResponse(null, null, oTrackSerHis); //Saving the track history record in case of exception
			HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'writeMstoneToTrackService');
		}
		return false;
	}	
		
    /**
	* Method to get the MileStoneTS value based on the SubType to be set in the Write Track Request
	*
	* @param    oCaseRec        Case record sObject
	* @param    subTypeSet      Set<String> set of subtypes
	* @return   String          Formatted date-time stamp
	*/
	@TestVisible	
    private String getMileStoneTSinEST(Case oCaseRec, set<String> subTypeSet)
    {
		DateTime dtMilestoneTS;
		if (oCaseRec.SubType__c != null && subTypeSet.contains(oCaseRec.SubType__c))
		{
			dtMilestoneTS = oCaseRec.CreatedDate;
		}
		else
		{
			dtMilestoneTS = datetime.now();
		}
		return dtMilestoneTS.format(GLOBAL_CONSTANT_HUM.TRACKSERVICE_MILESTONETS_FORMAT_HUM, GLOBAL_CONSTANT_HUM.TRACKSERVICE_MILESTONETS_TIMEZONE_HUM);	
	}	

    /**
	* Method to get the Sub Types for which Created Date field needs to set in MileStoneTS field in the Write Track Request
	*
	* @param    na
	* @return   subTypeSet        Set<String> set of all subtypes
	*/
	@TestVisible
    private set<String> getMileStoneTSSubTypes()
    {
		Set<String> subTypeSet = new Set<String>();
		list<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> mileStoneTSSubTypesList = MEDENROLLMENT_CASE_SUBTYPE_HUM__c.getAll().values();
		for (MEDENROLLMENT_CASE_SUBTYPE_HUM__c oSubType : mileStoneTSSubTypesList){
			if (oSubType.TrackSevice_MilestoneTS__c){
				subTypeSet.add(oSubType.SubType__c);				
			}
		}
		return subTypeSet;
	}	
	
    /**
	* This method will process the response received from web service call and 
	* update the Case Object's Medicare_Track_ID__c field and Track Service History object with Tracking ID received from response.
	*
	* @param    oRes			TrackServiceWriteTrackResponse_DTO_HUM response DTO object
	* @param    oCaseRec        Case record sObject
	* @param    oTrackSerHis    Enrollment_Track_History__c record sObject
	* @return   isSuccess       Boolean result of response parsing
	*/
    private boolean processResponse(TrackServiceWriteTrackResponse_DTO_HUM oRes, Case oCaseRec, Enrollment_Track_History__c oTrackSerHis)
    {
		String sRecTrackID;
		try
		{
			sRecTrackID = TrackService_S_HUM.parseWriteTrackResponse(oRes);

			if (String.IsNotBlank(sRecTrackID))
			{
                if(Schema.SObjectType.Enrollment_Track_History__c.fields.Recipient_Track_Id__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Recipient_Track_Id__c.isUpdateable()) oTrackSerHis.Recipient_Track_Id__c = sRecTrackID; 
                if(Schema.SObjectType.Enrollment_Track_History__c.fields.Status__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Status__c.isUpdateable()) oTrackSerHis.Status__c = GLOBAL_CONSTANT_HUM.TRACKSERVICE_SUCCESS_RESP_HUM; 
			}
			else
			{
                if(Schema.SObjectType.Enrollment_Track_History__c.fields.Status__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Status__c.isUpdateable()) oTrackSerHis.Status__c = GLOBAL_CONSTANT_HUM.TRACKSERVICE_ERROR_RESP_HUM; 
			}
			upsert oTrackSerHis;

			if (oTaskRec != null)
			{
                if(Schema.SObjectType.Task.fields.MET_Track__c.isUpdateable()) oTaskRec.MET_Track__c = oTrackSerHis.Id; 
				update oTaskRec; 
			}

			//There are chances of Case recod update because of some other concurrent update so we are inserting in the end.
			if (String.IsNotBlank(sRecTrackID)) {
                if(Schema.SObjectType.case.fields.Medicare_Track_ID__c.isUpdateable()) oCaseRec.Medicare_Track_ID__c = sRecTrackID; 
				Update oCaseRec;
			}
        }  
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'processResponse');
        }
		return String.IsNotBlank(sRecTrackID);
    }	
	
	/**
	* Method to get the new Track Service History object's record (No Insert only the record is initialized).
	*
	* @param    oCase			case record sObject
	* @param    oMstone			MET_Milestone__c record sObject
	* @param    oMemID			Member_Id__c record sObject
	* @return   oTrackSerHis    Enrollment_Track_History__c record sObject
	*/
	private Enrollment_Track_History__c getHistoryRecord(Case oCase, MET_Milestone__c oMstone, Member_Id__c oMemID)
	{
		Enrollment_Track_History__c oTrackSerHis;
		try
		{
			oTrackSerHis =  new Enrollment_Track_History__c();
            if(Schema.SObjectType.Enrollment_Track_History__c.fields.Case__c.isCreateable()) oTrackSerHis.Case__c =oCase.id; 
            if(Schema.SObjectType.Enrollment_Track_History__c.fields.Milestone_ID__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Milestone_ID__c.isUpdateable()) oTrackSerHis.Milestone_ID__c = oMstone.Name;    
            if(Schema.SObjectType.Enrollment_Track_History__c.fields.Milestone_MET_ID__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Milestone_MET_ID__c.isUpdateable()) oTrackSerHis.Milestone_MET_ID__c = oMstone.ID; 
            if(Schema.SObjectType.Enrollment_Track_History__c.fields.Source_track_ID__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Source_track_ID__c.isUpdateable()) oTrackSerHis.Source_track_ID__c = oCase.Medicare_Track_ID__c; 
            if(Schema.SObjectType.Enrollment_Track_History__c.fields.Source_App__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Source_App__c.isUpdateable()) oTrackSerHis.Source_App__c = GLOBAL_CONSTANT_HUM.TRACKSERVICE_SOURCE_APP_HUM; 
            if(Schema.SObjectType.Enrollment_Track_History__c.fields.Recipient_App__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Recipient_App__c.isUpdateable()) oTrackSerHis.Recipient_App__c = GLOBAL_CONSTANT_HUM.TRACKSERVICE_RECEIPT_APP_HUM; 
            if(Schema.SObjectType.Enrollment_Track_History__c.fields.Member_ID__c.isCreateable() && Schema.SObjectType.Enrollment_Track_History__c.fields.Member_ID__c.isUpdateable()) oTrackSerHis.Member_ID__c = oMemID.ID; 
		}
		catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'getHistoryRecord');
        }
		return oTrackSerHis;
	}
	
	/**
	* Method for BAM tracking of TRR Process Cases and Templates
	*
	* @param    None	
	* @return   Void
	*/
	public void callTrackServiceForTRRProcess()
	{		
		try
		{
			Case oCaseRec;
			Member_ID__c oMemID;
			MET_Milestone__c oMstone;
			set<String> subTypesSet;
			map<Id, Member_ID__c> mapCaseMemebrSSN;
			if(mapCaseEnrollHistory!=null && !mapCaseEnrollHistory.isEmpty())
			{
				map<Id, Case> mapCases = new map<Id, Case>([SELECT ID, CaseNumber, Type, SubType__c, Policy_Member__c, Policy_Member__r.Name, Case.Account.FirstName, Case.Account.LastName, Medicare_Track_ID__c, CreatedDate, Status, Election_Type_Code__c,Follow_up_Due_Date__c, RecordType.Name, AccountID, Classification_Type__c, Guided_Process_Flag__c FROM Case WHERE ID IN : mapCaseEnrollHistory.keySet()]);
			
				mapCaseMemebrSSN = getCaseMemebrSSNMap(mapCases);
				subTypesSet = getMileStoneTSSubTypes();
				for(String sCaseID : mapCaseEnrollHistory.keySet())
				{
					oCaseRec = mapCases.get(sCaseID);

					oMemID = (mapCaseMemebrSSN.get(oCaseRec.Policy_Member__c) <> null) ? mapCaseMemebrSSN.get(oCaseRec.Policy_Member__c) : new Member_ID__c(Name = '');

					map<string, set<Enrollment_Track_History__c>> mapTrackHistBySrcID = new map<string, set<Enrollment_Track_History__c>>();
					mapTrackHistBySrcID = mapCaseEnrollHistory.get(sCaseID);				
					for(String sSourceID : mapTrackHistBySrcID.keySet())
					{
						set<Enrollment_Track_History__c> setTrackHistRecs = mapTrackHistBySrcID.get(sSourceID);						
						map<id, Enrollment_Track_History__c> mapEnrollHist = new map<id, Enrollment_Track_History__c>();
						for(Enrollment_Track_History__c TrackHistRec : setTrackHistRecs)
						{	
							mapEnrollHist.put(TrackHistRec.ID, TrackHistRec);
						}						
						for(Enrollment_Track_History__c TrackHistRec : setTrackHistRecs)
						{
							oMstone = new MET_Milestone__c(Name = TrackHistRec.Milestone_Id__c);
							/* Call the Track Service for the current Enrollment Track History record. 
							After the callout, if success, the status would have been updated success or if failure, the status would have been updated to Failure.
							As in TRR there is no Milestone chaining, after the current callout irrespective of the result, chain the enqueue for remaining Enrollment Track History records.
							*/
							writeMstoneToTrackService(oCaseRec, oMstone, oMemID, subTypesSet, TrackHistRec,new Set<String>(), new set<string>()); 								
							mapEnrollHist.remove(TrackHistRec.ID);								
							if (mapEnrollHist != null && !mapEnrollHist.isEmpty())
							{ 
								set<Enrollment_Track_History__c> newSet = new set<Enrollment_Track_History__c>();
								for(String sId : mapEnrollHist.keySet())
								{
									newSet.add(mapEnrollHist.get(sId));
									
								}
								mapTrackHistBySrcID.put(sSourceID, newSet);																		
							}								
							else
							{
								mapTrackHistBySrcID.remove(sSourceID);
							}	
							
							if(mapTrackHistBySrcID!=null && !mapTrackHistBySrcID.isEmpty())
							{
								mapCaseEnrollHistory.put(sCaseID, mapTrackHistBySrcID);
							}
							else
							{
								mapCaseEnrollHistory.remove(sCaseID);	
							}
							
							if (!Test.isRunningTest() && !mapCaseEnrollHistory.isEmpty()) 
							{									
								system.enqueueJob(new TrackService_Q_HUM(mapCaseEnrollHistory));									
							}													
						    break;							
						}
						break;
					}
					break;
				}				
			}			
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'callTrackServiceForTRRProcess');
		}	
	}
	
		
	/**
	* Method for Bulk processing of BAM tracking of DEAA Cases and also start the ZAPI service callout for autoclosure.
	*
	* @param    None	
	* @return   Void
	*/
	public void processDEAACasesBulk()
	{
		list<string> lstCaseIDs = new list<string>();	//list of CaseIds to used for DEAA ZAPI service callout.
		list<Enrollment_Track_History__c> lstTrackHistory = new list<Enrollment_Track_History__c>();	//list of Track History for insert at the end of track service callouts.
		list<Case> lstCases = new list<Case>();	//list of Cases for Update of the Track ID on the case.
		TrackServiceWriteTrackResponse_DTO_HUM oRes;
		TrackServiceWriteTrackRequest_DTO_HUM oWriteTrackRequest;
		map<Id, Case> mapCases = new map<Id, Case>();
		map<Id, Member_ID__c> mapCaseMemberSSN = new map<Id, Member_ID__c>();		  						      
		integer iMaxCalloutLimit;
		string sTask = '';
		string sAction = '';
		string sSource = '';
		string sLOBCode = GLOBAL_CONSTANT_HUM.TRACKSERVICE_DEFAULT_LOBCODE_HUM;
		set<string> subTypesSet;
		string sCurrentCaseID;
		set<ID> setRemainingCaseIDs = new set<ID>();
		try
		{		
			if(setCaseIDs!=null && !setCaseIDs.isEmpty())
			{
				iMaxCalloutLimit = Limits.getLimitCallouts() - GLOBAL_CONSTANT_HUM.CONSTANT_10_HUM; //For defensive programming, we are keeping a buffer of 10 below the max callouts allowed.
				mapCases.putAll([SELECT ID, CaseNumber, Type, SubType__c, Policy_Member__c, Policy_Member__r.Name, Case.Account.FirstName, Case.Account.LastName, Medicare_Track_ID__c, CreatedDate, Status, Election_Type_Code__c,Follow_up_Due_Date__c, RecordType.Name, AccountID, Classification_Type__c, Guided_Process_Flag__c FROM Case WHERE ID IN : setCaseIDs]);
				if(mapCases!=null && !mapCases.isEmpty())
				{
					mapCaseMemberSSN = getCaseMemebrSSNMap(mapCases);
					subTypesSet = getMileStoneTSSubTypes();
					for(String sCaseID : setCaseIDs)
					{
						sCurrentCaseID = '';					
						if(mapCases.containsKey(sCaseID))
						{
							sCurrentCaseID = sCaseID;
							oWriteTrackRequest = new TrackServiceWriteTrackRequest_DTO_HUM();
							oRes = new TrackServiceWriteTrackResponse_DTO_HUM();
							Case oCaseRec = mapCases.get(sCaseId);
							Member_Id__c oMemID = (oCaseRec.Policy_Member__c != null && mapCaseMemberSSN.containsKey(oCaseRec.Policy_Member__c)) ? mapCaseMemberSSN.get(oCaseRec.Policy_Member__c) : new Member_ID__c(Name = '');
							
							Enrollment_Track_History__c oTrackHistory = getHistoryRecord(oCaseRec, new MET_Milestone__c(Name= GLOBAL_CONSTANT_HUM.DEAA_AUTOCLOSURE_MILESTONE), oMemID);
							
							string sMemberSSN = oMemID.Name;
							string sSourceId = (oCaseRec.Medicare_Track_ID__c != null) ? oCaseRec.Medicare_Track_ID__c : '' ;
							string sMilestoneTS = getMileStoneTSinEST(oCaseRec, subTypesSet);
							string sCaseNumber = oCaseRec.CaseNumber;
							string sType = String.isNotBlank(oCaseRec.Type) ? oCaseRec.Type : '';
							string sSubType = String.isNotBlank(oCaseRec.SubType__c) ? oCaseRec.SubType__c : '';
							String sFirstName = '';
							string sLastName = '';
							if (oCaseRec.Account != null) {
								sFirstName = (oCaseRec.Account.FirstName != null) ? oCaseRec.Account.FirstName : '';
								sLastName = (oCaseRec.Account.LastName != null) ? oCaseRec.Account.LastName : '';           
							}
							string sStatusCode = oCaseRec.Status.trim().left(7);
							string sMemberID = (oCaseRec.Policy_Member__c != null) ? oCaseRec.Policy_Member__r.Name : '';
							string sETCType = String.isNotBlank(oCaseRec.Election_Type_Code__c) ? oCaseRec.Election_Type_Code__c : '';
							string sFollowUpDueDate = (oCaseRec.Follow_up_Due_Date__c != Null && oCaseRec.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE)) ? String.valueOf(oCaseRec.Follow_up_Due_Date__c) : ''  ;
							
							oWriteTrackRequest = TrackService_S_HUM.createWriteTrackRequest(sLOBCode, sMemberSSN , sSourceID, GLOBAL_CONSTANT_HUM.DEAA_AUTOCLOSURE_MILESTONE, sMilestoneTS, sCaseNumber, sType, sSubType, sFirstName, sLastName, sStatusCode, sMemberID, sTask, sAction, sSource, sETCType, sFollowUpDueDate);
							if(oWriteTrackRequest!=null)
							{
								oRes = TrackService_S_HUM.callTrackServiceWriteCaseMethod(oWriteTrackRequest);
								string sRecTrackID = TrackService_S_HUM.parseWriteTrackResponse(oRes);								
								if (String.IsNotBlank(sRecTrackID))
								{
                                   if(Schema.SObjectType.Enrollment_Track_History__c.fields.Recipient_Track_Id__c.isCreateable()) oTrackHistory.Recipient_Track_Id__c = sRecTrackID; 
                                   if(Schema.SObjectType.Enrollment_Track_History__c.fields.Status__c.isCreateable()) oTrackHistory.Status__c = GLOBAL_CONSTANT_HUM.TRACKSERVICE_SUCCESS_RESP_HUM; 
                                   if(Schema.SObjectType.case.fields.Medicare_Track_ID__c.isUpdateable()) oCaseRec.Medicare_Track_ID__c = sRecTrackID; 
									lstCases.add(oCaseRec);									 																
								}
								else
								{
                                    if(Schema.SObjectType.Enrollment_Track_History__c.fields.Status__c.isCreateable()) oTrackHistory.Status__c = GLOBAL_CONSTANT_HUM.TRACKSERVICE_ERROR_RESP_HUM; 
								}								
							}
							mapCases.remove(sCaseId);
							lstCaseIDs.add(sCaseId);	//add the CaseId to the list to enqueue DEAA ZAPI callout
							lstTrackHistory.add(oTrackHistory);																												
						}
						if(Limits.getCallouts() >= iMaxCalloutLimit || Limits.getDMLStatements() > GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM)
						{
							sCurrentCaseID = '';
							break;
						}  
					}					
					if(mapCases!=null && !mapCases.isEmpty())	setRemainingCaseIDs.addAll(mapCases.keySet());
					processFinishCallouts(setRemainingCaseIDs, lstCaseIDs, lstCases, lstTrackHistory);	
				}	
			}
		}
		catch(Exception ex)
		{
			/* Start of logic to handle finish callout logic for any unhandled exceptions */			
			if(String.isNotBlank(sCurrentCaseID))
			{
				if(mapCases!=null && !mapCases.isEmpty())	mapCases.remove(sCurrentCaseID);	//remove the case from the set of Case IDs to be enqueued for track service chaining.
				lstCaseIDs.add(sCurrentCaseID);		//add the caseID to the list for calling the DEAA ZAPI callouts.
			}
			if(mapCases!=null && !mapCases.isEmpty())	setRemainingCaseIDs = mapCases.keySet();
			processFinishCallouts(setRemainingCaseIDs, lstCaseIDs, lstCases, lstTrackHistory);			
			/* End of logic to handle finish callout logic for any unhandled exceptions */
			
			//Log the exception to error log
			HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'processDEAACasesBulk');
		}
	}
	
	/**
	* Method for Bulk processing of BAM tracking of DEAA Cases and also start the ZAPI service callout for autoclosure.
	*
	* @param    setCaseIDs - set of CaseIds of unprocessed cases for further enqueue for Track Service.
	* @param    lstCaseIDs - list of processed case Ids for enqueue for DEAA ZAPI Service callout. 
	* @param    lstCasesForUpdate - Cases for update with the recipient track ID received from the BAM Service.
	* @param    lstTrackHistory - list of Enrollment track history for insert.	
	* @return   Void
	*/
	@TestVisible
	private void processFinishCallouts(set<ID> setCaseIDs, list<string> lstCaseIDs, list<Case> lstCasesForUpdate, list<Enrollment_Track_History__c> lstTrackHistory)
	{
		try
		{
			//create the enrollment track history entries for the cases processed for BAM Tracking.
			if(lstTrackHistory!=null && !lstTrackHistory.isEmpty())	Database.insert(lstTrackHistory);			
			
			//Update the cases with the recipient track ID received from the BAM Service callout response.
			if(lstCasesForUpdate!=null && !lstCasesForUpdate.isEmpty())	Database.update(lstCasesForUpdate);
			
			//enqueue the remaining unprocessed cases for Track service processing.
			if(!Test.isRunningTest() && setCaseIDs!=null && !setCaseIDs.isEmpty())	system.enqueueJob(new TrackService_Q_HUM(setCaseIDs));
			
			//call the future method to enqueue the processed cases for DEAA ZAPI Service callout.
			if(lstCaseIDs!=null && !lstCaseIDs.isEmpty())	DEAA_Process_S_HUM.enqueueDEAACases(lstCaseIDs);
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'TrackService_H_HUM', 'processFinishCallouts');	
		}
	}
}