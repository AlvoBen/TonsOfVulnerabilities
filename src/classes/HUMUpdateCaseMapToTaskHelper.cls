/*******************************************************************************************************************************
Apex Class Name : HUMUpdateCaseMapToTaskHelper
Version         : 1.0
Created On      : 6/21/2014
Function        : Used For trigger Functionality. Populates the case data on related task Records whenever a case is updated
Test Class      : HumUpdateCaseMapToTaskHelperTest  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* mrudula jaddu                17093                     6/21/2014                  Original Version
* mrudula jaddu                17878                     9/12/2014                  Modified fields as part of defect 154260 
* Santhi Mandava               21968                     23/03/2015                 As per req-88053, we have created four new fields on case/task objects.Included those fields in'populateData' method.
* Prachi Bhasin                21968                     07/18/2016                 Changes made as part of REQ- 270349
* Harshith Mandya                                        02/20/2016                 Implmented REQ - 300084
* Pallavi R                    81595                     04/03/2017                 Implemented REQ 306356 - As per new ctci model
* Subhamay Mandal										 05/19/2019					REQ - 399325 Quality - MIR - Milestone display on case transfer to the user NOT having MIR Permission Set (RF)
* Subhamay Mandal										 05/31/2019					REQ - 399325 Update on logic
* Subhamay Mandal										 06/03/2019					REQ - 399325 Code optimization 
* Amar Gadewar				   358242				     06/06/2019					REQ - 399325 - Warm Case Transfer Scenarios for MIR - Quality - MIR - Milestone display on case transfer to the user NOT having MIR Permission Set (RF)
* Moshitha Gunasekaran                                   01/30/2020                 US - 725017 -Ability for CRM to automatically update existing DEAA cases to a 'Closed' status in CRM and pass this information to CI-CMS.
* Mohammed Noor										     08/06/2021				  	US2498249 - Add new Group Number field for QVI.
******************************************************************************************************************************/


public with sharing class HUMUpdateCaseMapToTaskHelper
{ 
   /*
    * This Is the Main Method Used to update Task data with its parent Case data
    * <p>
    * @ Param     :    List<Case>
    * @ Param     :    Map<id, Case>
    * @ Param     :    Map<id, Case>
    * @ Return    :    Void
    */     
    public void populateData(List<Case> lstCaseRecords,Map<id, Case> mapOldCases,Map<id, Case> mapNewCases)
    {        
       Set<Id>  SetCaseIds =  new Set<Id>();
       Set<Id>  setDEAACaseIds =  new Set<Id>();
       Map<Id,Case> mapIdCase = new Map<Id,Case>([Select Id , CTCI_List__r.Classification__r.name , CTCI_List__r.Intent__r.name from Case where Id in :lstCaseRecords]);        
       List<Task> lstTasks = new List<Task>();   
       
       try
       {
           string profileName = HumUtilityHelper.getCurrentUserProfileName();
           for(Case oCaseRec : lstCaseRecords)
           {
                if(oCaseRec.ctci_list__c != mapOldCases.get(oCaseRec.Id).ctci_list__c||oCaseRec.Funding_Type__c != mapOldCases.get(oCaseRec.Id).Funding_Type__c ||
                  oCaseRec.Interacting_With_Type__c != mapOldCases.get(oCaseRec.Id).Interacting_With_Type__c ||  oCaseRec.Plan_Issue_State__c != mapOldCases.get(oCaseRec.Id).Plan_Issue_State__c ||
                  oCaseRec.Plan_Segment_Size__c != mapOldCases.get(oCaseRec.Id).Plan_Segment_Size__c || oCaseRec.Policy_Platform__c != mapOldCases.get(oCaseRec.Id).Policy_Platform__c ||
                  oCaseRec.Product_Type__c != mapOldCases.get(oCaseRec.Id).Product_Type__c || 
                  oCaseRec.State_Mem_Resid_Add__c != mapOldCases.get(oCaseRec.Id).State_Mem_Resid_Add__c || oCaseRec.Type != mapOldCases.get(oCaseRec.Id).Type ||
                  oCaseRec.Subtype__c != mapOldCases.get(oCaseRec.Id).Subtype__c || oCaseRec.Employer_Group_Name__c != mapOldCases.get(oCaseRec.Id).Employer_Group_Name__c ||
                  oCaseRec.Behavioral_Health__c != mapOldCases.get(oCaseRec.Id).Behavioral_Health__c ||
                  oCaseRec.Complaint_Type__c != mapOldCases.get(oCaseRec.Id).Complaint_Type__c || oCaseRec.Provider_Shipping_State_Code__c != mapOldCases.get(oCaseRec.Id).Provider_Shipping_State_Code__c ||
                  oCaseRec.Group_Billing_State_Code__c != mapOldCases.get(oCaseRec.Id).Group_Billing_State_Code__c || oCaseRec.Policy_Product_Code__c != mapOldCases.get(oCaseRec.Id).Policy_Product_Code__c 
				  || oCaseRec.Group_Number__c != mapOldCases.get(oCaseRec.Id).Group_Number__c)
                        SetCaseIds.add(oCaseRec.id);
                  
                  //When status is updated to Closed in DEAA Process batch job then those cases will be collected in below condition and enqueued to call Track Service
                  if(string.isNotBlank(oCaseRec.Origin) && oCaseRec.Origin.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.DEAA_Origin) && (oCaseRec.Case_Owner__c.equals(oCaseRec.Owner_Queue__c)) && oCaseRec.status != mapOldCases.get(oCaseRec.Id).status
	  		      && !mapOldCases.get(oCaseRec.Id).status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) && (oCaseRec.status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM)) &&
                  !profileName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ETL_API_ACCESS))
	  		      {
	  		    		setDEAACaseIds.add(oCaseRec.id);
	  		      }
           }

           if(setDEAACaseIds != null && !setDEAACaseIds.isEmpty() && setDEAACaseIds.size() >0)
           {
           		//Call and enqueue Track service
           		system.enqueueJob(new TrackService_Q_HUM(setDEAACaseIds));
           }

           lstTasks  = [SELECT id, whatId, Classification__c,Funding_Type__c,Plan_Issue_State__c,Plan_Segment_Size__c,Policy_Platform__c,Group_Billing_State_Code__c,Policy_Product_Code__c,
                        Product_Type__c,Intent__c,State_Mem_Resid_Add__c,Type__c,Subtype__c,Employer_Group_Name__c,Group_Number__c,Behavioral_Health__c,Complaint_Type__c,Provider_Shipping_State_Code__c FROM task 
                        WHERE WhatId in :SetCaseIds Limit :(limits.getLimitQueryRows() - limits.getQueryRows())];   
           
           if(lstTasks != NULL && !lstTasks.isEmpty())            
             CaseToTaskData(lstTasks,lstCaseRecords,mapIdCase,mapNewCases);
       }
       catch(Exception e)
       {
           HUMExceptionHelper.logErrors( e,'HumUpdateCaseMapToTaskHelper','populateData');
       }   
    }
    
    /**
    * This is the Method Used to update Task data with its parent Case data
    *<p>
    * @param  lstCaseRecords   List<Case> 
    * @param  lstRelatedTasks  List<Task>
    * @param  mapIdCase        Map<Id,Case>    
    * @return void   
    */       
    public void CaseToTaskData(List<Task> lstRelatedTasks,List<case> lstCaseRecords,Map<Id,Case> mapIdCase,Map<id, Case> mapNewCases)
    {            
        for(Task oTaskRec : lstRelatedTasks)
        {
            oTaskRec.Classification__c  = mapIdCase.containsKey(oTaskRec.whatId) ? mapIdCase.get(oTaskRec.whatId).CTCI_List__r.Classification__r.name :'';
            oTaskRec.Funding_Type__c = mapNewCases.get(oTaskRec.whatId).Funding_Type__c;
            oTaskRec.Interacting_With_Type__c  = mapNewCases.get(oTaskRec.whatId).Interacting_With_Type__c;
            oTaskRec.Plan_Issue_State__c  = mapNewCases.get(oTaskRec.whatId).Plan_Issue_State__c;
            oTaskRec.Plan_Segment_Size__c  = mapNewCases.get(oTaskRec.whatId).Plan_Segment_Size__c;
            oTaskRec.Policy_Platform__c  =  mapNewCases.get(oTaskRec.whatId).Policy_Platform__c;                    
            // modified field from product to product type as part of defect 154260
            oTaskRec.Product_Type__c  = mapNewCases.get(oTaskRec.whatId).Product_Type__c;
            oTaskRec.Intent__c  = mapIdCase.containsKey(oTaskRec.whatId) ? mapIdCase.get(oTaskRec.whatId).CTCI_List__r.Intent__r.name : '';
            oTaskRec.State_Mem_Resid_Add__c  = mapNewCases.get(oTaskRec.whatId).State_Mem_Resid_Add__c;
            oTaskRec.Type__c  = mapNewCases.get(oTaskRec.whatId).Type;
            oTaskRec.Subtype__c  = mapNewCases.get(oTaskRec.whatId).Subtype__c;
            oTaskRec.Employer_Group_Name__c  = mapNewCases.get(oTaskRec.whatId).Employer_Group_Name__c;
			oTaskRec.Group_Number__c = mapNewCases.get(oTaskRec.whatId).Group_Number__c;
            oTaskRec.Behavioral_Health__c = mapNewCases.get(oTaskRec.whatId).Behavioral_Health__c;
            oTaskRec.Complaint_Type__c = mapNewCases.get(oTaskRec.whatId).Complaint_Type__c;
            oTaskRec.Provider_Shipping_State_Code__c = mapNewCases.get(oTaskRec.whatId).Provider_Shipping_State_Code__c;
            oTaskRec.Group_Billing_State_Code__c = mapNewCases.get(oTaskRec.whatId).Group_Billing_State_Code__c;
            oTaskRec.Policy_Product_Code__c = mapNewCases.get(oTaskRec.whatId).Policy_Product_Code__c;  
        }
        HUMDueDateHelper.bSkipFlag = true;
        update lstRelatedTasks;
        HUMDueDateHelper.bSkipFlag = false;         
     } 
	
	/**
     * Complete Case Milestone if transfered case owner does not have MIR permission set
     * @param lstCaseRecords List<Case> 
     * @param mapOldCases Map<id, Case> 
     * @return void
     */
     public static void updateCaseMilestoneNonMIRUser(List<Case> lstCaseRecords, Map<id, Case> mapOldCases) {
     	Set<Id> setCaseId = new Set<Id>();
     	
     	List<Id> lstOwnerID = new List<Id>();
     	Map<id, Case> mapNewCasesTemp = new Map<Id, Case>();
     	Map<String, String> mapCaseOwner = new Map<String, String>();
     	for(Case oCaseRec : lstCaseRecords) {
     		String sCurrentOwnerID = oCaseRec.OwnerId;
        	if(oCaseRec.IsEntitlementApplicable__c && sCurrentOwnerID.startswith('005') && oCaseRec.OwnerId != mapOldCases.get(oCaseRec.Id).OwnerId) {
            	mapCaseOwner.put(oCaseRec.Id, oCaseRec.OwnerId);
            	lstOwnerID.add(oCaseRec.OwnerId);
            	mapNewCasesTemp.put(oCaseRec.Id, oCaseRec);
            }
     	}
     	// get list of user having MIR permission 
     	if(!lstOwnerID.isEmpty()) {
	     	List<String> lstMIRUser = new List<String>();
	     	for(PermissionSetAssignment psa : [SELECT AssigneeId FROM PermissionSetAssignment WHERE AssigneeId= :lstOwnerID AND PermissionSet.Name = : GLOBAL_CONSTANT_HUM.MIR_PERMISSIONSET_HUM]) {
	     		lstMIRUser.add(psa.AssigneeId);
	     	}
	     	
	     	if(!(lstOwnerID.size() == lstMIRUser.size())) {
		 		for(String sCaseId : mapCaseOwner.keySet()) {
		     		String sOwnerId = mapCaseOwner.get(sCaseId);
		     		if(!lstMIRUser.contains(sOwnerId)) {
		     			mapNewCasesTemp.get(sCaseId).IsEntitlementApplicable__c = false;
		     			setCaseId.add(sCaseId);
		     		}
		     	}
	     	}
     	}
     	if(!setCaseId.isEmpty()) {
     		completeCurrentMilestone(setCaseId);
     	}
     }
     
     /**
     * Update current milestone if transferred to User having no MIR permission set
     * @param Set<Id>: Case Id 
     * @return void
     */
     private static void completeCurrentMilestone(Set<Id> setCaseId) {
     	List<CaseMilestone> lstCaseMilestone = [select Id, completionDate from CaseMilestone where caseId in : setCaseId and completionDate = null];
     	for(CaseMilestone cm : lstCaseMilestone) {
     		cm.completionDate = System.today();
     	}
     	
     	if(!lstCaseMilestone.isEmpty()) {
     		update lstCaseMilestone;
     	}
     }
 }