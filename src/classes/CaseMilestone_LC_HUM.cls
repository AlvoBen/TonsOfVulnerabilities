/*******************************************************************************************************************************
Apex Class Name : CaseMilestone_LC_HUM
Version         : 1.0
Created On      : 12/05/2022
Function        : This class is used to write Milestone to BAM while creating or updating case
Test Class      : CaseMilestone_LT_HUM

Modification Log:
* Version          Developer Name                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                12/05/2022               Original Version for US 4035121
*    1.1           Jasmeen Shangari 			   12/22/2022				US-4035121 Milestone writing for BAM tracking for MET & SubType change
*    1.2           Jasmeen Shangari                02/03/2023               US 4178419-Added Changes for TRR Milestone
*/
public with sharing class CaseMilestone_LC_HUM {


    /*
    * Method to get the newly created TaskID from Upsert Operation of CreateTasks Method and pass this ID to write Milestones to Track Service
    *
    * @param  listUpsertResults(task upsert results), taskList(list of task records), caseRecord(Case that is altered), sPrevSubtype (Previous subtype on case)
    * @return na
    */
    public void writeMstoneToTrackService(List<Database.UpsertResult> listUpsertResults, List<Task> taskList, case caseRecord, string sPrevSubtype)
    {
        ID idTask = null;
        list<Id> resLst = new list<Id>();
        if (listUpsertResults != null && listUpsertResults.size() > 0)
        {
            //Iterate through all the Upsert Results records and find out the newly created Task record.
            for (Integer count = 0; count < listUpsertResults.size(); count++)
            {
                if (listUpsertResults[count].isSuccess() && listUpsertResults[count].isCreated())
                {
                    resLst.add(listUpsertResults[count].getID());
                }
            }
        }
        if (resLst != null && !resLst.isEmpty())        
        {            
            //Identify if it is a TRR Case
            if(caseRecord.Origin.equals(CaseEditConstants_HUM.TRR_ORIGIN) && caseRecord.Type.equals(CaseEditConstants_HUM.TRR_CASE_TYPE) && caseRecord.subtype__c.equals(CaseEditConstants_HUM.TRR_CASE_SUBTYPE))            
            {
                evaluateTasksForTRRTracking(resLst, taskList, caseRecord);
            }
            // Call the TrackService_H_HUM class to get the matching Milestone and send to Track Service. THis comes into picture for MET Task only without SUbType change
            else if(caseRecord.subtype__c.equals(sPrevSubtype))
            {               
                System.enqueueJob(new TrackService_Q_HUM(resLst, caseRecord.Id));
            }        
            else
            {                               
                /* Create Milestones for Both Subtype change and TAS entries */
                caseSubtypeAndTAS_MStone(resLst,caseRecord,sPrevSubtype);
            }
        }
        else  //No new MET Tasks Added.
        {                                    
            if(String.isNotBlank(caseRecord.subtype__c) && String.isNotBlank(sPrevSubtype) && !sPrevSubtype.equals(caseRecord.subtype__c))
            {
                /* Create Milestones for the Case Subtype Change*/
                caseSubtypeOnlyChangeMStone(caseRecord,sPrevSubtype);                                 
            }            
        }
    }
    
    /*
    * This method Is to create the Milestone Entry for only the Case Subtype changes 
    * @param  CaseRecord  & PreviousSubType
    * @return void
    */    
    public void caseSubtypeOnlyChangeMStone(case caseRecord, string sPrevSubtype)
    {
        set<MET_Milestone__c> prevSubtypeMS = new set<MET_Milestone__c>();   //Should be set as empty to verify the historical Enrollment History for a 898 already written
        set<MET_Milestone__c> caseMS = new set<MET_Milestone__c>();
        map<Id, set<MET_Milestone__c>> mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();
        caseRecord.SubType__c= caseRecord.SubType__c==null?'':caseRecord.SubType__c;

        /* Get Milestones for the Case Subtype Change*/             
        caseMS =  CaseMilestoneTrackService_C_HUM.getUICaseMilestone(caseRecord, sPrevSubtype, prevSubtypeMS);
        
        if(!caseMS.isEmpty())
        {                    
            mapCaseMilestones.put(caseRecord.Id, caseMS);            
            System.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));
        }
    }
    
    /*
    * This method Is to create the Milestone Entry for Case Subtype changes along with T/A/S entries
    * @param  resLst(List of newly created MET task), caseRecord(Case that is altered), sPrevSubtype (Previous subtype on case)
    * @return void
    */
    @TestVisible
    private void caseSubtypeAndTAS_MStone(list<Id> resLst,case caseRecord, string sPrevSubtype )
    {
        list<MET_Milestone__c> lstTaskMS = [SELECT Id, Name, Case_Type__c, Case_Subtype__c, Is_Active__c, Is_Milestone__c, Launch_EMME__c, Action__c, Task__c, Source_Name__c,
                                                MET_Milestone__c.Task__r.Name, MET_Milestone__c.Action__r.Name, MET_Milestone__c.Source_Name__r.Name 
                                            FROM MET_Milestone__c WHERe Is_Active__c = true AND Is_Milestone__c = true];
        
        list<Task> lstTasks = [SELECT Id, WhoId, WhatId, RecordTypeId, MET_Track__c, MET_Task__r.Case_Subtype__c, MET_Source__c, MET_Action__c, MET_Task__c FROM Task where Id IN :resLst];                           
                        
        map<Id, set<MET_Milestone__c>> mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();
        
        set<MET_Milestone__c> caseMS = new set<MET_Milestone__c>();
        set<MET_Milestone__c> prevSubtypeMS = new set<MET_Milestone__c>();
        set<MET_Milestone__c> newSubtypeMS = new set<MET_Milestone__c>();
        
        map<String, MET_Milestone__c> mapTaskMS = new map<String, MET_Milestone__c>();
        
        //Master data for MET task to write what milestone for TAS combination
        for(MET_Milestone__c ms : lstTaskMS)
        {
            String Key = '';
            if(String.isNotBlank(ms.Name) && String.isNotBlank(ms.Case_Type__c) && String.isNotBlank(ms.Case_Subtype__c) && ms.Task__c != null && ms.Action__c != null && ms.Source_Name__c != null)
            {
                key = ms.Case_Type__c + ms.Case_Subtype__c + ms.Task__c + ms.Action__c + ms.Source_Name__c;
                mapTaskMS.put(key, ms);
            }                   
        }
        
        //Actual MET task created
        for(Task tRec : lstTasks)
        {      
            //System writes MET task milestone around previous subtype
            //Compare previous SubType with MET Task Case SubType mentioned in master data
            if(sPrevSubtype.equals(tRec.MET_Task__r.Case_Subtype__c))
            {                     
                String sPrevKey = caseRecord.Type + sPrevSubtype + tRec.MET_Task__c + tRec.MET_Action__c + tRec.MET_Source__c;
                if(mapTaskMS.containskey(sPrevKey))
                {
                    prevSubtypeMS.add(mapTaskMS.get(sPrevKey));
                }                       
            }
            else //System writes MET task milestone around new subtype
            {                     
                String snewKey = caseRecord.Type + caseRecord.Subtype__c + tRec.MET_Task__c + tRec.MET_Action__c + tRec.MET_Source__c;
                if(mapTaskMS.containskey(snewKey))
                {
                    newSubtypeMS.add(mapTaskMS.get(snewKey));
                }
            }                   
        }
        
        /* Get Milestones for the Case Subtype Change*/             
        caseMS =  CaseMilestoneTrackService_C_HUM.getUICaseMilestone(caseRecord, sPrevSubtype, prevSubtypeMS);
                                        
        set<MET_Milestone__c> finalMSList = new set<MET_Milestone__c>();
                        
        /* Add Milestones for the Tasks Created for Previous Subtypes*/
        if(!prevSubtypeMS.isEmpty())
        {                   
            finalMSList.addAll(prevSubtypeMS);
        }
        
        /* Add the Subtype change Case Milestones*/
        if(!caseMS.isEmpty())
        {
            finalMSList.addAll(caseMS);
        }
                        
        /* Add Milestones for the Tasks Created for New Subtypes*/              
        if(!newSubtypeMS.isEmpty())
        {
            finalMSList.addAll(newSubtypeMS);
        }
        if(!finalMSList.isEmpty())
        {                
            mapCaseMilestones.put(caseRecord.Id, finalMSList);
            System.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));
        }            
    }

    /*
    * This method Is to evaluate the TRR T/A/S for BAM tracking
    * @param  resLst(list of succesfully inserted TaskIDs),taskList(list of all the Tasks added) & CaseRecord
    * @return void
    */
    @TestVisible
    private void evaluateTasksForTRRTracking(list<ID> resLst, list<task> taskList, case caseRecord)
    {        
        set<ID> setTaskID = new set<ID>();
        map<ID, task> mapTasks = new map<ID, task>();
        map<ID, MET_Milestone__c> mapTaskMS = new map<ID, MET_Milestone__c>();
        set<ID> setMETTaskIDs = new set<ID>();
        set<ID> setMETActionIDs = new set<ID>();
        set<ID> setMETSourceIDs = new set<ID>();        
        map<string, MET_Milestone__c> mapMilestones = new map<string, MET_Milestone__c>();
                            
        if(resLst!=null && !resLst.isEmpty() && taskList!=null && !taskList.isEmpty())
        {
            setTaskID.addAll(resLst);
            for(task taskRec : taskList)
            {
                // If task is newly created task
                if(setTaskID.contains(taskRec.ID) && taskRec.MET_Task__c!=null && taskRec.MET_Action__c!=null && taskRec.MET_Source__c!=null)
                {
                    mapTasks.put(taskRec.ID, taskRec);
                    setMETTaskIDs.add(taskRec.MET_Task__c);
                    setMETActionIDs.add(taskRec.MET_Action__c);
                    setMETSourceIDs.add(taskRec.MET_Source__c);
                }
            }         
            //Process for newly created tasks only
            if(mapTasks!=null && !mapTasks.isEmpty() && setMETTaskIDs!=null && !setMETTaskIDs.isEmpty() 
                && setMETActionIDs!=null && !setMETActionIDs.isEmpty() && setMETSourceIDs!=null && !setMETSourceIDs.isEmpty())
            {
                list<MET_Milestone__c> 
                // Master milestone data against case CaseType & SubType
                lstMilestones = [SELECT ID,Name,Case_Subtype__c, Task__c, Action__c, Source_Name__c,Task__r.Name, Action__r.Name, Source_Name__r.Name FROM MET_Milestone__c 
                                                    WHERE Case_Type__c = :caseRecord.Type and Case_Subtype__c =:caseRecord.SubType__c and Task__c IN :setMETTaskIDs and Source_Name__c IN :setMETSourceIDs and Action__c IN :setMETActionIDs and Is_Active__c = true and Is_Milestone__c = true];
                if(lstMilestones!=null && !lstMilestones.isEmpty())
                {
                    //Create map for master milestone data
                    for(MET_Milestone__c milestone : lstMilestones)
                    {
                        mapMilestones.put(String.valueOf(milestone.Task__c)+String.valueOf(milestone.Action__c)+String.valueOf(milestone.Source_Name__c), milestone);
                    }                      
                    //Fetch milestone details for Task TAS combination and store against TaskID
                    for(task taskRec : mapTasks.values())
                    {
                        string sKey = String.valueOf(taskRec.MET_Task__c) + String.valueOf(taskRec.MET_Action__c) + String.valueOf(taskRec.MET_Source__c);
                        
                        if(mapMilestones.containsKey(sKey))
                        {
                            mapTaskMS.put(taskRec.ID, mapMilestones.get(sKey));
                        }
                    }
                    //Call method for all task where milestone needs to be written
                    if(mapTaskMS!=null && !mapTaskMS.isEmpty())
                    {
                        callTrackServiceForTRR(mapTaskMS,caseRecord);
                    }                
                }                                                                   
            }
        }   
    }
    
    /*
    * This method is to call the BAM Track service 
    * @param  mapTaskMS(map of TaskID and its corresponding MET Milestone) & CaseRecord
    * @return void
    */
    @TestVisible
    private void callTrackServiceForTRR(map<ID, MET_Milestone__c> mapTaskMS, case caseRecord)
    {
        string sUISeriesSrcID;
        string sRejSrcID;
        list<Enrollment_Track_History__c> lstHistForInsert = new list<Enrollment_Track_History__c>();
        map<string, map<string, set<Enrollment_Track_History__c>>> mapCaseEnrollHistory = new map<string, map<string, set<Enrollment_Track_History__c>>>();
        map<string, list<Enrollment_Track_History__c>> mapTrackHistory = new map<string, list<Enrollment_Track_History__c>>();
        if(mapTaskMS!=null && !mapTaskMS.isEmpty())
        {        
            // Pull enrollmentTrackHistory for case
            list<Enrollment_Track_History__c> lstTrackHistory = [SELECT Id, Name, Case__c, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, createddate 
                                                                    FROM Enrollment_Track_History__c WHERE Case__c = :caseRecord.ID AND Milestone_MET_Id__c = null AND Source_Track_Id__c!=null];            
            //Proceed with milestone writing if milestone already exist on the case
            if(lstTrackHistory!=null && !lstTrackHistory.isEmpty())
            {
                set<string> setSTOPMS = new set<string>{CaseEditConstants_HUM.TRR_REJ_STOP_MS, CaseEditConstants_HUM.TRR_UI_SERIES_STOP_MS};
                // Create map of trackHistory with SourceTrackID as key
                for(Enrollment_Track_History__c trackHistory : lstTrackHistory)
                {
                    if(mapTrackHistory.containsKey(trackHistory.Source_Track_Id__c))
                    {
                        mapTrackHistory.get(trackHistory.Source_Track_Id__c).add(trackHistory);
                    }
                    else
                    {
                        mapTrackHistory.put(trackHistory.Source_Track_Id__c, new list<Enrollment_Track_History__c>{trackHistory});
                    }
                }            

                //Loop map against SourceTrackID key, validate if latest milestone against SourceTrackID is not STOP milestone for UI Series or Rejection and set SourceTrackID for UI series or Rejection Task
                for(string sSourceID : mapTrackHistory.keySet())
                {
                    list<Enrollment_Track_History__c> lstEnrollHist = mapTrackHistory.get(sSourceID);
                    if(lstEnrollHist!=null && !lstEnrollHist.isEmpty())
                    {
                        if(!setSTOPMS.contains(lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c))
                        {
                            if(lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c.startsWith('18'))  sUISeriesSrcID = lstEnrollHist[lstEnrollHist.size()-1].Source_Track_Id__c;
                            else if(lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c.startsWith('19')) sRejSrcID = lstEnrollHist[lstEnrollHist.size()-1].Source_Track_Id__c; 
                        }
                    }
                }
                
                // Proceed with milestone writing only if UI Series or Rejection related milestone are already written.
                // Logic is done to figure out  SourceTrackID against what entry should be used because 2 parallel flows are possible - UI series or rejection
                Enrollment_Track_History__c TrackHisRec;
                if(String.isNotBlank(sUISeriesSrcID) || String.isNotBlank(sRejSrcID))
                {
                    for(MET_Milestone__c milestone : mapTaskMS.values())
                    {                                
                        if(milestone.Name.startsWith(CaseEditConstants_HUM.TRR_REJ_MS_PATTERN) && String.isNotBlank(sRejSrcID))
                        {
                            lstHistForInsert.add(getNewTrackHistory(caseRecord.ID, milestone.Name, sRejSrcID, milestone.ID));
                        }
                        else if(milestone.Name.startsWith(CaseEditConstants_HUM.TRR_UI700_MS_PATTERN) && String.isNotBlank(sUISeriesSrcID))
                        {
                            lstHistForInsert.add(getNewTrackHistory(caseRecord.ID, milestone.Name, sUISeriesSrcID, milestone.ID));                            
                        }
                        else if(milestone.Name.equals(CaseEditConstants_HUM.CRM_STOP_MILESTONE))
                        {
                            if(String.isNotBlank(sRejSrcID))
                                lstHistForInsert.add(getNewTrackHistory(caseRecord.ID, milestone.Name, sRejSrcID, milestone.ID));
                            else if(String.isNotBlank(sUISeriesSrcID))
                                lstHistForInsert.add(getNewTrackHistory(caseRecord.ID, milestone.Name, sUISeriesSrcID, milestone.ID));
                        }
                    }
                    if(lstHistForInsert!=null && !lstHistForInsert.isEmpty())
                    {
                        insert lstHistForInsert;                        
                        map<string, set<Enrollment_Track_History__c>> mapHistBySrcID = new map<string, set<Enrollment_Track_History__c>>();                         
                        for(Enrollment_Track_History__c histRec : lstHistForInsert)
                        {
                            if(mapHistBySrcID.containsKey(histRec.Source_Track_Id__c))
                            {
                                mapHistBySrcID.get(histRec.Source_Track_Id__c).add(histRec);    
                            }
                            else
                            {
                                mapHistBySrcID.put(histRec.Source_Track_Id__c, new set<Enrollment_Track_History__c>{histRec});
                            }
                        }                       
                        if(mapHistBySrcID!=null && !mapHistBySrcID.isEmpty())
                        {
                            mapCaseEnrollHistory.put(caseRecord.ID, mapHistBySrcID);   
                            system.enqueueJob(new TrackService_Q_HUM(mapCaseEnrollHistory));
                        }
                    }    
                }                                                
            }                                       
        }
    }
    
    /*
    * This method is to create the new Enrollment track History record instance 
    * @param  sCaseID(Case ID), sMstoneName(milestone to track), sSrcID(source track ID to track against) and sMET_ID(MET_Milestone ID for tracking)
    * @return Enrollment_Track_History__c instance
    */
    @TestVisible
    private Enrollment_Track_History__c getNewTrackHistory(string sCaseID, string sMstoneName, string sSrcID, string sMET_ID)
    {
        Enrollment_Track_History__c TrackHisRec = new Enrollment_Track_History__c();        
            if(schema.SobjectType.Enrollment_Track_History__c.fields.Milestone_Id__c.isCreateable())
        TrackHisRec.Milestone_Id__c = sMstoneName;
            if(schema.SobjectType.Enrollment_Track_History__c.fields.Source_App__c.isCreateable())
        TrackHisRec.Source_App__c = 'CRM';
            if(schema.SobjectType.Enrollment_Track_History__c.fields.Recipient_App__c.isCreateable())
        TrackHisRec.Recipient_App__c = 'CRM';
            if(schema.SobjectType.Enrollment_Track_History__c.fields.Status__c.isCreateable())
        TrackHisRec.Status__c = 'Pending';
            if(schema.SobjectType.Enrollment_Track_History__c.fields.Case__c.isCreateable())
        TrackHisRec.Case__c = sCaseID;
            if(schema.SobjectType.Enrollment_Track_History__c.fields.Source_Track_Id__c.isCreateable())
        TrackHisRec.Source_Track_Id__c = sSrcID;
            if(schema.SobjectType.Enrollment_Track_History__c.fields.Milestone_MET_Id__c.isCreateable())
        TrackHisRec.Milestone_MET_Id__c = sMET_ID;
        return TrackHisRec; 
    }	
        
}