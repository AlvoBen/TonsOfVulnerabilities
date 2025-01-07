/*******************************************************************************************************************************
Apex Class Name : CaseTriggerHandler_HUM 
Version           : 1.0
Created Date     : March 07 2016
Function          : This serves as Handler for Case Trigger.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------

* Avinash Choubey           30464                        03/04/2016                 Initial Version for REQ- 252640
* Harshith Mandya                                        02/20/2016                 Implmented REQ - 300084 
* PradeepKumar Dani         83713                        02/12/2016                 Implmented REQ - 308332(CaseTriggerHandler_HUM)
* Pallavi R                                              05/19/2017                 Changed code  - fix for CRM Ticket : 6794554
* Manoj Chaurasia           167654                       12/15/2017                 Implmented REQ - 350299(OmniCaseAssignmentRuleEvaluation_H_HUM)
* Manoj Chaurasia           167654                       12/22/2017                 Implmented REQ - 350420 Avoid trigger logic for OmniCaseAssignmentRuleEvaluation when case Is updated from OmniEligibleCaseAssignment batch job.
* Manoj Chaurasia           172758                       01/22/2018                 Changed code  - fix for Omni case assignment rule evaluation should be executed only for Unassigned Cases (Onwer Queue == Case Owner)
* Mohammed Noor             208595                       04/03/2018                 REQ - 359092 - Fix for Defect# 351815.0001. Allow only Unassigned cases for Omni Rule Evaluation.
* Moshitha Gunasekaran      224503                       05/02/2018                 REQ - 350862 - Fix to process Omni Routing rules for asynchrnous call from ETL API Acess profile
* Syed Mubarak                                           05/21/2018                 REQ - 361868 - Update the case transferred date on change of WQ and WQV
* Bharath Rajappan                                       05/31/2018                 REQ - 359179 - AutoRoute the Creditable Coverage case to RSO enrollment 
* Bharath Rajappan                                       06/25/2018                 Fix to update only cc case for subtype change
* Vandana Chaudhari                                      07/20/2018                 REQ - 370601 CA Ticket 7646745 Fix: Web Chat Auto Create Case
* Ajay Chakradhar                                        03/22/2019                 REQ - 378712 - Case Transfer to Medhok
* Ajay Chakradhar                                        04/09/2019                 REQ - 389747 - Lock case with Case Status of External Transfer in Progress 
* Subhamay Mandal                                        05/19/2019                 REQ - 399325 Quality - MIR - Milestone display on case transfer to the user NOT having MIR Permission Set (RF)
* Amar Gadewar              358242                       06/06/2019                 REQ - 399325 - Warm Case Transfer Scenarios for MIR - Quality - MIR - Milestone display on case transfer to the user NOT having MIR Permission Set (RF)
* Mohammed Noor                                          09/05/2019                 REQ - 408201 - Omni Case Assignment Rule Evaluation Optimization.
* Shailesh Mali                                          11/15/2019                 REQ - 409603 Process Builder logic Migration to Apex Code.
* Mohammed Noor                                          12/07/2019                 US826586 - Case Workflow rules logic migration to Apex code.
* Mohammed Noor                                          05/07/2020                 USER STORY 1035056 - Stop CRM to CPP Inquiry creation.
* Subhamay Mandal                                        08/24/2020                 User Story 1293633: PR00091574- IR 5050-OGO - Recommendation actions on re-opened cases (reject )-(CRM)
* Vinay Lingegowda                                       10/10/2020                 USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)
* Santhi Mandava                                         09/30/2020                 Changed 'populateviewname' execution from sync to async for Humana APi user
* Mohammed Noor                                          04/30/2021                 USER STORY 2163321 - Checkmarx - Remove references of HUMClosedCaseCRMToCCPInquiries.
* Pooja Kumbhar                                          08/26/2021                 US2513665 - DF3418_Regression Defect previous work queue view not updating
* Pooja Kumbhar                                          07/29/2022                 US3521594 - Case Visibility - CRM Lightning in CRM Classic and CRM Classic in CRM Lightning
* Ajay Chakradhar                                        08/30/2022                 US3576296 : T1PRJ0393258- Complaint Routing - iZone 903- CRM Complaint Field Routing Changes (Classic)
* Pooja Kumbhar											 12/28/2022				    User story 4083329: T1PRJ0170850 - Lightning- UI Trigger Change to Support ETL
* Pooja Kumbhar											 1/11/2023				    User story 4083329: Change in Flag settings
* Moshitha Gunasekaran                                   01/20/2023                 US3788451 - T1PRJ0337737 - 2022 - SF- additional Access for Mulesoft user for Case and Interaction details
* Pooja Kumbhar						 1/30/2023		    User story 4083329: T1PRJ0170850 - Lightning- UI Trigger Change to Support ETL added Enhanced changes
* Pooja Kumbhar											 2/2/2023				    User story 4083329: Removed initialization and updated variable name
* Ajay Chakradhar                                        04/12/2023                 US4157293 : T1PRJ0891339 - INC2084180- SDUP Cases Not Routing to SDUP Views in CRM
* Ajay Chakradhar                                        08/31/2023                 US4957069  T1PRJ0891742 - AHTR - SF Security Remediation - Case Lgt
*******************************************************************************************************************************/
public with sharing class  CaseTriggerHandler_HUM
{
    /**
    * checkCasetoUpdate
    * <p>
    * This method is used to check the that is Open Work task count is changed or not
    *
    * @param  Map<id, Case> mapOldCases
    * @param  List<case>    lstNewCases
    * @return List<case>
    **/
    private static List<case> checkCasetoUpdate(List<Case> lstNewCases, Map<id, Case> mapOldCases)
    {
        List<Case> lstCasesToUpdate = new List<case>();
        try
        {
            for(Case oNewCase : lstNewCases)
            {
                if(oNewCase.Open_Work_Task_Count__c == mapOldCases.get(oNewCase.Id).Open_Work_Task_Count__c)
                {
                    lstCasesToUpdate.add(oNewCase);
                }
            }
            return lstCasesToUpdate;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseTriggerHandler_HUM', 'checkCasetoUpdate');
        }  
        return null;
    }
    
    /**
    * ProcessCaseBeforeUpdate
    * <p>
    * This method is used to do a profile check and then call the Helper class method to Update Owner Queue and Populate Queue Views
    *
    * @param  Map<id, Case> mapOldCases
    * @param  List<case>    lstNewCases
    * @return Void
    **/ 
    public static void ProcessCaseBeforeUpdate(List<Case> lstNewCases, Map<id, Case> mapOldCases )
    {
        Set<String> SetAllowedProfileNames = new Set<String>();
        Set<ID> SetAllowedProfileIds = new Set<ID>();
        
        String sUsername = UserInfo.getUserName();
        ETL_User_Check__c OneshotETLUser;
        if(sUsername.length()<=38) OneshotETLUser = ETL_User_Check__c.getValues(sUsername);
                        
        List<Group> lstRSOEnrollmentGroup = [SELECT Id FROM Group WHERE Type = :GLOBAL_CONSTANT_HUM.GROUP_QUEUE_HUM AND NAME = :GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_QUEUENAME];
        try{    
              //Start - REQ - 308332
            set<Id> setIds = new set<Id>();
            map<Id, CTCI_Junction__c> mapCTCIRec = new map<Id, CTCI_Junction__c>(); 
            for(Case caseObj : lstNewCases)
            {
                if(caseObj.CTCI_List__c <> null && mapOldCases.get(caseObj.Id).CTCI_List__c <> caseObj.CTCI_List__c)
                {
                    setIds.add(caseObj.CTCI_List__c);
                }

                //Creditable Coverage Auto Route for attached process
                if(caseObj.SubType__c == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE && mapOldCases.get(caseObj.Id).SubType__c != GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE && caseObj.Guided_Process_Flag__c == true && caseObj.status == GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE && lstRSOEnrollmentGroup.size()>0)
                {
                    caseObj.Owner_Queue__c = GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_QUEUENAME;
                    caseObj.Classification_Type__c = GLOBAL_CONSTANT_HUM.CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM;
                    caseObj.Case_Owner__c = GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_QUEUENAME;
                    caseObj.Topic__c = GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE;
                    caseObj.Service_Center__c = GLOBAL_CONSTANT_HUM.SERVICE_CENTER_LOUISVILLE;
                    caseObj.Department__c = GLOBAL_CONSTANT_HUM.DEPARTMENT_RETAIL_RSO_ENROLLMENT;
                    caseObj.ownerId = lstRSOEnrollmentGroup[0].Id;
                    
                }
                
            }
            Profile oProfile = [Select Name From Profile Where ID =: UserInfo.getProfileId() limit 1];
            Boolean hasUserMuleAccess = HUMUtilityHelper.hasUserMulesoftAccess();
            mapCTCIRec = new map<Id, CTCI_Junction__c>([SELECT Id, Classification_Type__c, Classification__c,Intent__c FROM CTCI_Junction__c WHERE Id=:setIds]);
            for(Case caseObj : lstNewCases)
            {
                if(HUMUtilityHelper.isCRMFunctionalityON('2513665'))
               {
               if((oProfile.Name == GLOBAL_CONSTANT_HUM.ETL_API_ACCESS || hasUserMuleAccess) && mapOldCases.get(caseObj.Id).Work_Queue_View_Name__c != null && mapOldCases.get(caseObj.Id).Work_Queue_View_Name__c != caseObj.Work_Queue_View_Name__c)
                {
                    caseObj.Previous_Work_Queue_View__c =  mapOldCases.get(caseObj.Id).Work_Queue_View_Name__c;
                }
                }
                if(caseObj.CTCI_List__c <> null && mapOldCases.get(caseObj.Id).CTCI_List__c <> caseObj.CTCI_List__c)
                {
                    caseObj.Classification_Id__c = mapCTCIRec.get(caseObj.CTCI_List__c).Classification__c;
                    caseObj.Intent_Id__c = mapCTCIRec.get(caseObj.CTCI_List__c).Intent__c;
                }
                
                //REQ - 361868 Transferred date update
                if(caseObj.Owner_Queue__c != mapOldCases.get(caseObj.Id).Owner_Queue__c)
                {
                    caseObj.Transferred_Date__c = System.Now();
                }
            }
            //End - REQ - 308332
            
                for(HUMCaseUpdateAllowedProfies__c objHumCaseUpdtProf : HUMCaseUpdateAllowedProfies__c.getAll().values())
                {
                    SetAllowedProfileNames.add(objHumCaseUpdtProf.name);
                }
                for(Profile objProfile : [select Name ,ID from Profile where Name in :SetAllowedProfileNames])
                {
                    SetAllowedProfileIds.add(objProfile.ID);
                }
                
                if(NULL != SetAllowedProfileIds && SetAllowedProfileIds.size() > 0 && SetAllowedProfileIds.contains(UserInfo.GetProfileID()) )
                {
                    List<Case> lstCasesToUpdate = checkCasetoUpdate(lstNewCases,mapOldCases);
                    if( lstCasesToUpdate!= NULL && lstCasesToUpdate.size() > 0)
                    {   
                        HUMUpdateCaseHelper ohandler = new HUMUpdateCaseHelper();                    
                        if(oProfile.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ETL_API_ACCESS) || hasUserMuleAccess) {
                            ohandler.updateCaseQueuesByAPIUser(lstNewCases, mapOldCases);
                        } else {
                        ohandler.updateCaseQueues(lstNewCases, mapOldCases);
                        }
                        String sTriggerNewList = JSON.serialize(lstNewCases);
                        
                        //If the logged in user profile is 'ETL API Access' and logged in user id is matching with id present in 'CaseProcessBuilderSettings_HUM__c' custom setting
                        //Then populateviename method will be executed in async mode this is to fix apex CPU time limit issue for ETL batch jobs
                        String sUserId = userinfo.getuserid();
                        CaseProcessBuilderSettings_HUM__c objSetting = CaseProcessBuilderSettings_HUM__c.getOrgDefaults();
                        // Running populate View Name Method in Async mode for all users in ETL_User_Check__c custom setting
                        if(oProfile.Name == GLOBAL_CONSTANT_HUM.ETL_API_ACCESS && (objSetting.ETL_UserId__c == sUserId || OneshotETLUser!= null))
                        {
                            HUMUpdateCaseHelper.populateViewNameAsync(sTriggerNewList , true, null,oProfile.Name);
                        }
                        else
                        {   
                            HUMUpdateCaseHelper.populateViewName(lstNewCases, true, null,oProfile.Name);
                        }
                        //REQ - 378712 - Case Transfer to Medhok
                        if(Label.Case_Transfer_Service_Flag == '1')
                        {
                            //To fetch all metadata records in Case_Transfer_Queue_and_View__mdt
                            List<Case_Transfer_Queue_and_View__mdt> QueueViewData = new List<Case_Transfer_Queue_and_View__mdt>(); 
                            if(HUMUtilityHelper.isCRMFunctionalityON('4957069')){
                                QueueViewData = Case_Transfer_Queue_and_View__mdt.getAll().values();    
                            }
                            for(Case caseObj : lstNewCases)
                            {
                                if(caseObj.Status == GLOBAL_CONSTANT_HUM.CASESTATUS_HUM) 
                                {
                                    if(!HUMUtilityHelper.isCRMFunctionalityON('4957069')){
                                        QueueViewData = [select id,MasterLabel,Case_View__c,Case_Work_Queue__c from Case_Transfer_Queue_and_View__mdt];    
                                    }
                                    Map<ID, Schema.RecordTypeInfo> rtMap = Schema.SObjectType.Case.getRecordTypeInfosById();
                  for (Case_Transfer_Queue_and_View__mdt qvData : QueueViewData)
                                    {
                                        if(qvData.Case_Work_Queue__c == caseObj.Owner_Queue__c && 
                                           qvData.Case_View__c == caseObj.Work_Queue_View_Name__c)
                                        {  
                                            String currentRecordType = rtMap.get(caseObj.RecordTypeId).getName();
                                            if(!currentRecordType.containsIgnoreCase('Closed'))
                                            {   
                                                caseObj.Status = Label.External_Transfer_in_Progress;
                                                String closedRecordType = 'Closed ' + rtMap.get(caseObj.RecordTypeId).getName();
                                                Id recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(closedRecordType).getRecordTypeId();
                                                caseObj.Recordtypeid = String.valueOf(recordTypeId);
                                            }
                                            if(currentRecordType.containsIgnoreCase('Closed') && caseObj.Status == GLOBAL_CONSTANT_HUM.CASESTATUS_HUM)
                                            {
                                                String updatedRecordType = currentRecordType.removeStart('Closed ');
                                                Id recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(updatedRecordType).getRecordTypeId();
                                                caseObj.Recordtypeid = String.valueOf(recordTypeId);
                                            }    
                                        }
                                    }
                                }   
                            }
                        }
                        //REQ - 378712 - Case Transfer to Medhok
                        
            if(!oProfile.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.SYSTEM_ADMINISTRATOR_PROFILE_HUM) &&
                          !oProfile.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.DEPLOYMENT_PROFILE_HUM))
                        {
                            list<Case> lstOmniCasesToEvaluate = new list<Case>();
                            map<Id, Group> mapcasgrp = new map<Id, Group>();
                            set<Id> setOwnerIds = new set<Id>();
                            for(Case caseObj : lstNewCases)
                            {
                                if(caseObj.OwnerId != null)
                                {
                                    setOwnerIds.add(caseObj.OwnerId);
                                }
                            }
                            //select all the Omni Queues
                            mapcasgrp = new map<Id, Group>([SELECT Id, QueueRoutingConfigId FROM Group WHERE Id=:setOwnerIds AND QueueRoutingConfigId!=null]);                          
                            if(mapcasgrp!=null && !mapcasgrp.isEmpty())
                            {
                                for(Case caseObj : lstNewCases)
                                {
                                    //Skip the cases already in Omni Queue and select remaining cases from Re-evaluation
                                    if(caseObj.OwnerId != null && !(mapcasgrp.containsKey(caseObj.OwnerId)))                                    
                                    {
                                        lstOmniCasesToEvaluate.add(caseObj);
                                    }
                                }
                            }
                            else    //Select all the cases not in Omni Queue
                            {
                                lstOmniCasesToEvaluate.addAll(lstNewCases); 
                            }                           
                            if(lstOmniCasesToEvaluate!=null && !lstOmniCasesToEvaluate.isEmpty())
                            {
                                processOmniCaseAssignmentRuleEvaluation(lstOmniCasesToEvaluate);                                
                            }
                        }                        
                    }
                }
                
                HUMUpdateCaseHelper ohandler1 = new HUMUpdateCaseHelper();                    
                    ohandler1.caseGeneralHOGeneralSecurity(lstNewCases,mapOldCases);
                    ohandler1.creditableCoverage(lstNewCases);                        
                    ohandler1.humCloseCaseUpdateOwner(lstNewCases, mapOldCases,oProfile.Name);
                
                HUMUpdateCaseMapToTaskHelper.updateCaseMilestoneNonMIRUser(lstNewCases, mapOldCases);
                
                /*This method invocation is specifically added at the end of BeforeCaseUpdate,
                * so that all the case fields updates in trigger are complete before the method invocation.
                * This method invocation is for recordtype, reopen flag and reopen date for Case Closure and reopen scenario
                * that are moved from Workflow rules to Apex Trigger.
                */              
                ohandler1.caseCloseAndReopenUpdates(lstNewCases, mapOldCases);
                  
            // Added ETL Visibility Code skip switch to skip case visibility code when switch is set True for users in ETL_User_Check__c custom settings
            CRMFunctionality_ONOFF_Switch__c ETLRollbackflag = CRMFunctionality_ONOFF_Switch__c.getValues('ETL Visibility Code skip');
            if(OneshotETLUser != null)
            {
                        if(ETLRollbackflag.IsON__c == false)
                        {
                        MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCases, mapOldCases);
                        }
            }else{
              MemberPlanAndPolicyMemberLookupBeforeUpdate(lstNewCases, mapOldCases);
            }
            //US3576296
            if(HUMUtilityHelper.isCRMFunctionalityON('3576296'))
            {
                for(Case caseObj : lstNewCases)
                { 
                    if(caseObj.Status == Label.External_Transfer_in_Progress && caseObj.Case_Complaint_Routed__c == True)                                    
                    {	
                        // CaseTransferService TO MHK
                        CaseTransferService_S_HUM.CaseTransferProcess(caseObj.Id);
                        caseObj.Work_Queue_View_Name__c = 'Medicare Verbal Grievance and Appeal';
                        Map<ID, Schema.RecordTypeInfo> rtMap = Schema.SObjectType.Case.getRecordTypeInfosById();
                        String closedRecordType = 'Closed ' + rtMap.get(caseObj.RecordTypeId).getName();
                        Id recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(closedRecordType).getRecordTypeId();
                        caseObj.Recordtypeid = String.valueOf(recordTypeId);
                        caseObj.Status = 'Closed';
                    }
                }
            }
            //US3576296
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseTriggerHandler_HUM', 'ProcessCaseBeforeUpdate');
        }
    }
    
    /**
    * ProcessCaseAfetrUpdate
    * <p>
    * This method is used to do a Open work task count e check and then call the Helper class method 
    *
    * @param  Map<id, Case> mapOldCases
    * @param  List<case>    lstNewCases
    * @return Void
    **/
    public static void ProcessCaseAfterUpdate (List<Case> lstNewCases,   Map<id, Case> mapOldCases , Map<id, Case> mapNewCases)
    {
        try
        {
            List<Case> lstCasesToUpdate = checkCasetoUpdate(lstNewCases,mapOldCases);
            if( lstCasesToUpdate!= NULL && lstCasesToUpdate.size() > 0)
            {
                HUMUpdateCaseMapToTaskHelper objhandler = new HUMUpdateCaseMapToTaskHelper();
                objhandler.populateData(lstCasesToUpdate,mapOldCases,mapNewCases);
            }
            callMilestoneTrackService( lstNewCases, mapOldCases );                  
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseTriggerHandler_HUM', 'ProcessCaseAfetrUpdate');
        }
    }
    
    /**
    * processCaseBeforeInsert
    * <p>
    * This method is used to call the Helper class method to populate Views
    *
    * @param  List<case>    lstNewCases
    * @return Void
    **/ 
    public static void processCaseBeforeInsert (List<Case> lstNewCases,String sProfileName)
    {
        HUMUpdateCaseHelper ohandler = new HUMUpdateCaseHelper();
        try
       {
            //If the logged in user profile is 'ETL API Access' and logged in user id is matching with id present in 'CaseProcessBuilderSettings_HUM__c' custom setting
            //Then populateviename method will be executed in async mode this is to fix apex CPU time limit issue for ETL batch jobs
            String sUserId = userinfo.getuserid();

            //To set Contract_Protected_Case__c field value
            if(HUMUtilityHelper.isCRMFunctionalityON('4157293')){
                setCPDSecurity(lstNewCases);
            }
            

            CaseProcessBuilderSettings_HUM__c objSetting = CaseProcessBuilderSettings_HUM__c.getOrgDefaults();
            String sUsername = UserInfo.getUserName();
            ETL_User_Check__c OneshotETLUser;
            if(sUsername.length()<=38) OneshotETLUser = ETL_User_Check__c.getValues(sUsername);
            // Running populate View Name Method in Async mode for all users in ETL_User_Check__c custom setting
            if(!(sProfileName == GLOBAL_CONSTANT_HUM.ETL_API_ACCESS && (objSetting.ETL_UserId__c == sUserId || OneshotETLUser != null)))
            {
                HUMUpdateCaseHelper.populateViewName(lstNewCases, true, null,sProfileName);
            }

            processOmniCaseAssignmentRuleEvaluation(lstNewCases); 
            reassignWebChatOwner(lstNewCases);
	    ohandler.creditableCoverage(lstNewCases);
	    ohandler.caseCloseAndReopenUpdates(lstNewCases, null); 
            // Added ETL Visibility Code skip switch to skip case visibility code when switch is set True for users in ETL_User_Check__c custom settings
            CRMFunctionality_ONOFF_Switch__c ETLRollbackflag = CRMFunctionality_ONOFF_Switch__c.getValues('ETL Visibility Code skip');
            if(OneshotETLUser != null)
            {
                        if(ETLRollbackflag.IsON__c == false)
                        {
                        MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCases);
                        }
            }else{
              MemberPlanAndPolicyMemberLookupBeforeInsert(lstNewCases);
            } 
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseTriggerHandler_HUM', 'processCaseBeforeInsert');
        }
    }
    
    /**
    * reassignWebChatOwner
    * <p>
    * This method is used to reassign values of case owner and owner queue.
    *
    * @param  List<case>    lstNewCaseList
    * @return Void
    **/ 
    public static void reassignWebChatOwner(List<Case> lstNewCaseList)
    {    
            try
            {
                List<Case> lstNewCaseListUpdate = new List<Case>();
                List<User> currentUser = [Select id, name, Current_Queue__c from User where ID=:userinfo.getuserID()];
                List<Work_Queue_Setup__c> lstWQS = new List<Work_Queue_Setup__c>();
                if(!currentUser.isEmpty()){
                    String sCurrentQueue = currentUser[0].Current_Queue__c.replace(' ','_');                    
                    lstWQS = [select Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =: sCurrentQueue AND IsActive__c = TRUE];
                }
                for(Case oCase : lstNewCaseList)
                {
                    if(oCase.Origin == HUMConstants.HUMWebChatText && (oCase.Case_Owner__c == null || oCase.Case_Owner__c == '') ){                       
                        if(!currentUser.isEmpty()){
                            oCase.Case_Owner__c = currentUser[0].name;
                            oCase.Owner_Queue__c = currentUser[0].Current_Queue__c;                          
                        }
                      }
                    if(oCase.Origin == HUMConstants.HUMWebChatText && !lstWQS.isEmpty()) 
          {
            Work_Queue_Setup__c oWQS = lstWQS[0];
            oCase.Service_Center__c = oWQS.Service_Center__c; 
            oCase.Department__c =  oWQS.Department__c;  
            if(oWQS.Classification_Type__c!=null)         
            oCase.Classification_Type__c = oWQS.Classification_Type__r.name; 
          }             
                }             
            }
            Catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e,'CaseTriggerHandler_HUM','reassignWebChatOwner');
            }        
    }
    /**
    * processOmniCaseAssignmentRuleEvaluation
    * <p>
    * This method is used to call the Helper class method to evaluate Omni Case Assignment Rule
    *
    * @param  List<case>    lstNewCases 
    * @return Void
    **/   
    public static void processOmniCaseAssignmentRuleEvaluation(List<Case> lstCases)
    {
        List<Case> lstOmniCase = new List<Case>();
        Map<String,Set<String>> mapWorkQueue = HUMUpdateCaseHelper.getOmniEligibleList();
        for(Case oCase: lstCases){
            oCase.Omni_Eligible__c = False;
            if(mapWorkQueue.containsKey(oCase.Owner_Queue__c)){
                Set<String> setWorkQView = mapWorkQueue.get(oCase.Owner_Queue__c);
                if(setWorkQView.contains(oCase.Work_Queue_View_Name__c))
                {
                    oCase.Omni_Eligible__c = True;
                }
            }
           if(oCase.Omni_Eligible__c && !oCase.Status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) && !oCase.Status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) && oCase.Owner_Queue__c.equalsIgnoreCase(oCase.Case_Owner__c))
           {                
                lstOmniCase.add(oCase);                             
           }
        }
        if(lstOmniCase!=null && !lstOmniCase.isEmpty())
        {
            OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstOmniCase, false);
        }
    }
   
    /**
    * processCaseAfterInsert
    * <p>
    * This method is used to call the Helper class method to populate interaction log data
    *
    * @param  List<case>    lstNewCases
    * @return Void
    **/   
    public static void processCaseAfterInsert (List<Case> lstNewCases,String sProfileName )
    {
        try
        {
            String sTriggerNewList = JSON.serialize(lstNewCases);
            
            //If the logged in user profile is 'ETL API Access' and logged in user id is matching with id present in 'CaseProcessBuilderSettings_HUM__c' custom setting
            //Then populateviename method will be executed in async mode this is to fix apex CPU time limit issue for ETL batch jobs
            // In any async method, to save the values we need to do 'Update' DML operation. this is not possible in before insert thats why 'populateViewNameAsync' moved to after insert.
            String sUserId = userinfo.getuserid();
            CaseProcessBuilderSettings_HUM__c objSetting = CaseProcessBuilderSettings_HUM__c.getOrgDefaults();
            String sUsername = UserInfo.getUserName();
            ETL_User_Check__c OneshotETLUser;
            if(sUsername.length()<=38) OneshotETLUser = ETL_User_Check__c.getValues(sUsername);
         
            // Running populate View Name method in Async mode for all users in ETL_User_Check__c custom setting
            if(sProfileName == GLOBAL_CONSTANT_HUM.ETL_API_ACCESS && (objSetting.ETL_UserId__c == sUserId || OneshotETLUser != null) )
            {
                HUMUpdateCaseHelper.populateViewNameAsync(sTriggerNewList , true, null,sProfileName );
            }
            
            HUMCaseTriggerHelper.onAfterInsert(lstNewCases, true, true);
            callMilestoneTrackService(lstNewCases,null);                    
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseTriggerHandler_HUM', 'processCaseAfterInsert');
        }
    }
    
    /**
    * This method is used to call  Track Service 
    * @MethodName callMilestoneTrackService()
    * @param  List<case> lstCases, Map<id, Case> mapOldCases
    * @return void
    * Added check for Interacting_with__c values, to handle the trackId generation for unknown case creation asynchronously
    **/
    public static void callMilestoneTrackService(List<Case> lstCases, Map<id, Case> mapOldCases )
    {
        try{
            string recordTypeName;
            CaseMilestoneTrackService_C_HUM.CaseMilestoneParams caseMilParam;
            List<CaseMilestoneTrackService_C_HUM.CaseMilestoneParams> caseMilParamList = new List<CaseMilestoneTrackService_C_HUM.CaseMilestoneParams>();        
            Map<ID, Schema.RecordTypeInfo> rtMap = Schema.SObjectType.Case.getRecordTypeInfosById();        
            String profileName = HumUtilityHelper.getCurrentUserProfileName();      
            if(profileName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ETL_API_ACCESS) || HUMUtilityHelper.hasUserMulesoftAccess()){
                for(Case caseObj : lstCases){
                    recordTypeName = rtMap.get(caseObj.RecordTypeId).getName();
                    caseMilParam = new CaseMilestoneTrackService_C_HUM.CaseMilestoneParams();
                    if((String.isNotBlank(recordTypeName) && recordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE))
                       && (caseObj.Origin != GLOBAL_CONSTANT_HUM.TRR_ORIGIN && caseObj.Type != GLOBAL_CONSTANT_HUM.TRR_CASE_TYPE ))
                    {
                        if(Trigger.isInsert && mapOldCases == null){
                           if(String.isBlank(caseObj.Interacting_with__c)){
                              continue;
                            }
                            else{
                                caseMilParam.CaseId = caseObj.id;
                                caseMilParam.previousCaseSubType = null;
                                caseMilParam.isNewCase = true;
                                caseMilParamList.add(caseMilParam);
                            }
                        }
                        else if(Trigger.isUpdate && mapOldCases != null && mapOldCases.containsKey(caseObj.Id) ){
                            if(caseObj.Subtype__c != mapOldCases.get(caseObj.Id).Subtype__c){
                                caseMilParam.previousCaseSubType = mapOldCases.get(caseObj.Id).subtype__c;
                                caseMilParam.CaseId = caseObj.id;
                                caseMilParam.isNewCase = false;
                                caseMilParamList.add(caseMilParam);
                            }
                            else if(String.isNotBlank(caseObj.Interacting_with__c) && String.isBlank(mapOldCases.get(caseObj.Id).Interacting_with__c)){
                                caseMilParam.CaseId = caseObj.id;
                                caseMilParam.previousCaseSubType = null;
                                caseMilParam.isNewCase = true;
                                caseMilParamList.add(caseMilParam); 
                            }
                        }                       
                    }
                }
            }   
                if(caseMilParamList != null && !caseMilParamList.isEmpty())
                    CaseMilestoneTrackService_C_HUM.invokeTrackService(caseMilParamList);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseTriggerHandler_HUM', 'callMilestoneTrackService');
        }
    }
    
    /**
    * This method is called from ProcessCaseBeforeInsert to populate the Memberplan and Policy Member id with case.
    * @MethodName InsertMemberPlanAndPolicyMemberFromLookup()
    * @param  List<Case> lstNewCases
    * @return void
    **/
    public static void MemberPlanAndPolicyMemberLookupBeforeInsert(List<Case> lstNewCases){
        Set<Id> policyMemberIds = new Set<Id>();
        Set<Id> memberPlanIds = new Set<Id>();
        Map<Id,Id> caseMemberPlan =new Map<Id,Id>();
        Map<Id,Id> casePolicyMember =new Map<Id,Id>();        
    
        for(case caseObj: lstNewCases){
            if(!String.isBlank(caseObj.Policy_Member__c) && String.isBlank(caseObj.Member_Plan_Id__c)) {// if creating case not having policy member attached but having member plan 
                policyMemberIds.add(caseObj.policy_Member__c); // get policy member on case          
            }else if(String.isBlank(caseObj.Policy_Member__c) && !String.isBlank(caseObj.Member_Plan_Id__c)){ // if creating case not having member plan attached but having policy member 
                memberPlanIds.add(caseObj.Member_Plan_Id__c);// get member plans
            }
        }        
        if(policyMemberIds.size()>0){
            List<Policy_Member__c> lstPolicyMember = [SELECT Id,Member_Plan__c from Policy_Member__c where id in : policyMemberIds]; // query member plan lookup field values from policy members id on cases 
            if(!lstPolicyMember.isEmpty()){
                for(Policy_Member__c policyMember : lstPolicyMember){
                    caseMemberPlan.put(policyMember.Id,policyMember.Member_Plan__c);
                }
            }
        }
        if(memberPlanIds.size()>0){
            List<MemberPlan> lstMemberPlan = [SELECT Id,Policy_Member__c from MemberPlan where id in : memberPlanIds]; // query policy Member lookup field values from MemberPlans ids on cases 
            if(!lstMemberPlan.isEmpty()){
                for(MemberPlan memberPlan : lstMemberPlan){
                    casePolicyMember.put(memberPlan.Id,memberPlan.Policy_Member__c);
                }
            }
        }
        for(Case caseObj : lstNewCases ){
            if(caseMemberPlan.containsKey(caseObj.Policy_Member__c)){ 
                caseObj.Member_Plan_Id__c = caseMemberPlan.get(caseObj.Policy_Member__c); // updating case with respective member plan
            }else if(casePolicyMember.containsKey(caseObj.Member_Plan_Id__c)){
                caseObj.Policy_Member__c = casePolicyMember.get(caseObj.Member_Plan_Id__c); // updating case with respective policy member
            }
        }
    }

    /**
    * This method is called from ProcessCaseBeforeUpdate to update the Memberplan and Policy Member id with case.
    * @MethodName UpdateMemberPlanAndPolicyMemberFromLookup()
    * @param  List<Case> lstNewCases , Map<id, Case> mapOldCases
    * @return void
    **/
    
    public static void MemberPlanAndPolicyMemberLookupBeforeUpdate(List<Case> lstNewCases, Map<id, Case> oldCasesMap){
        Map<Id,Id> policyMemberIds = new Map<Id,Id>();
        Map<Id,Id> memberPlanIds = new Map<Id,Id>();
        Map<Id,Id> caseMemberPlan =new Map<Id,Id>();
        Map<Id,Id> casePolicyMember =new Map<Id,Id>();
        List<Case> matchingCases = new  List<Case>();
        Case oldCaseData = new Case();              
    
        for(case caseObj: lstNewCases){
            oldCaseData = oldCasesMap.get(caseObj.id);
            if(oldCaseData.Policy_Member__c != caseObj.Policy_Member__c){ // if policy on updating case gets changed
                if(!String.isBlank(caseObj.policy_Member__c)){
                    policyMemberIds.put(caseObj.id,caseObj.policy_Member__c); //if policy Member is not blank get policy member of that case
                    matchingCases.add(caseObj);
                }else{
                    caseObj.Member_Plan_Id__c = caseObj.policy_Member__c; //if policy member blank makes member plan blank as well
                }
            }else if(oldCaseData.Member_Plan_Id__c != caseObj.Member_Plan_Id__c){ // if Member plan on updating case gets changed
                if(!String.isBlank(caseObj.Member_Plan_Id__c)){
                    memberPlanIds.put(caseObj.id,caseObj.Member_Plan_Id__c);// if Member plan is not blank get member plans of that case
                    matchingCases.add(caseObj);
                }else{
                    caseObj.policy_Member__c = caseObj.Member_Plan_Id__c; // if member plan is blank makes policy member blank as well
                }
            }
        }
        
        if(policyMemberIds.size()>0){
            List<Policy_Member__c> lstPolicyMember = [SELECT Id,Member_Plan__c from Policy_Member__c where id in : policyMemberIds.values()]; // query member plan from policy member ids on cases
            if(!lstPolicyMember.isEmpty()){
                for(Policy_Member__c policyMember : lstPolicyMember){
                    caseMemberPlan.put(policyMember.Id,policyMember.Member_Plan__c);
                }                
            }
        }
        if(memberPlanIds.size()>0){
            List<MemberPlan> lstMemberPlan = [SELECT Id,Policy_Member__c from MemberPlan where id in : memberPlanIds.values()]; // query policy member from member plans ids on cases
            if(!lstMemberPlan.isEmpty()){
                for(MemberPlan memberPlan : lstMemberPlan){
                    casePolicyMember.put(memberPlan.Id,memberPlan.Policy_Member__c);
                }
            }
        }
        for(Case caseObj : matchingCases){
            if(policyMemberIds.containsKey(caseObj.id)){
                caseObj.Member_Plan_Id__c = caseMemberPlan.get(caseObj.Policy_Member__c); //update case with respective member plan
            }else if(memberPlanIds.containsKey(caseObj.id)){
                caseObj.Policy_Member__c = casePolicyMember.get(caseObj.Member_Plan_Id__c); //update case with respective policy member
            }
        }
    }
    
    /**
    * setCPDSecurity
    * This method is used to call the Helper class method to update Contract_Protected_Case__c 
	* for Case before setting ViewName
	* @param  List<case> lstNewCases 
    * @return Void
    **/
    public static void setCPDSecurity(List<Case> lstNewCases){
        Set<Id> policyMemberIds = new Set<Id>();
        Set<Id> accountIds = new Set<Id>();
        Map<Id,String> caseDetails =new Map<Id,String>();
        
        for(case caseObj: lstNewCases){
            if(!String.isBlank(caseObj.Policy_Member__c)) {
                policyMemberIds.add(caseObj.policy_Member__c);           
            }
            if(!String.isBlank(caseObj.AccountId)){
                accountIds.add(caseObj.AccountId);
            }
        }
        if(policyMemberIds.size()>0 && accountIds.size()>0){
            List<Policy_Member__c> lstPolicyMember = [SELECT Id, Name, Member__c, Member__r.Contract_Protected_Data__c ,Policy__c,Policy__r.Group_Name__r.Contract_Protected_Data__c from Policy_Member__c where id in : policyMemberIds AND Member__c in : accountIds];
            if(!lstPolicyMember.isEmpty()){
                for(Policy_Member__c policyMember : lstPolicyMember){
                    caseDetails.put(policyMember.Id,(String)policyMember.Policy__r.Group_Name__r.Contract_Protected_Data__c);
                }
            }
        }
        if(policyMemberIds.size()==0 && accountIds.size()>0){
            List<Account> lstAccount = [SELECT Id, Name, Contract_Protected_Data__c FROM Account where id in : accountIds];
            if(!lstAccount.isEmpty()){
                for(Account acc : lstAccount){
                    caseDetails.put(acc.Id,(String)acc.Contract_Protected_Data__c);
                }
            }
        }
        for(Case caseObj : lstNewCases ){
            if(caseDetails.containsKey(caseObj.Policy_Member__c)){
                caseObj.Contract_Protected_Case__c  = caseDetails.get(caseObj.Policy_Member__c);
            }else if(caseDetails.containsKey(caseObj.AccountId)){
                caseObj.Contract_Protected_Case__c = caseDetails.get(caseObj.AccountId);
            }
        }
    }
    
    /**
    * This method is called from case processbuilders if the logged in user is Humana API.
    * @MethodName doNothing()
    * @param  NA
    * @return void
    **/
    @InvocableMethod
    public static void doNothing()
    {}

}