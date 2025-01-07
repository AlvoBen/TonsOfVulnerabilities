/***************************************************************************************************************************************************
Apex Class Name  : WorkQueueCreation_D_HUM
Version          : 1.0
Created Date     : 03/16/2016
Function         : Controller to create Work Queue & its related objects
Test Class       : HUMWorkQueueCreationTest
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya              30706              03/16/2016                    Original Version
* Pradeepkumar Dani            74579              03/09/2017                    Modified for the Classification_Type__c reference
* Gargey Sharma                81589              04/04/2017                    REQ - 306370 : Updates as per CTCI changes
* Vijaya Koriprolu             268583             08/13/2018                    Implemented REQ - 366118 - ability to deactivate a topic name even when there are open cases associated with the topic.                                                                                                                                                                    
* Suresh Sunkari		                          09/20/2018                    Modified for Req- 374839
* Keerti Durbha                                   01/15/2020                    782503 PR00086652 (Multi Browser Support) - SF - P9 Medium Defects - CRUD Security fixes
* Atia Uzma                                       03/01/2021                    User Story 2040620: T1PRJ0001827 -MF 1 - Quality - Display guidance on Case Transfer page on selection of Service Center And Department (CRM)
* Subhamay Mandal              74579              04/29/2021                    User Story 2230059: T1PRJ0001827-IR 5050 - Add SLA fields in New - Work Queue Setup (CRM)
 *Tummala Vijaya Lakshmi						  08/27/2021					USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App
****************************************************************************************************************************************************/

public with sharing class WorkQueueCreation_D_HUM 
{
    /**
    * Returns Work_Queue_Setup__c Work queue setup record along with work queue junction and work queue priroty record details.
    * <p>
    * This method is used to get the work queue setup, work queue junction and work queue priroty record details.
    *
    * @param    wqId                    Work queue Id
    * @return   Work_Queue_Setup__c     Work queue setup record and associated priority and junction records
    */ 
    public Work_Queue_Setup__c wqEdit_SOQL(ID wqId)
    {
        Return [Select Work_Queue_Guidance__c,Service_Center__c,Department__c, Name, Queue_Description__c,Public_Group_Name__c, Classification_Type__c, Queue_Manager__c, Security_Group__c, TenantID__c, Public_Group_Queue_Id__c, General__c, Contract_Protected_Data__c,
                       SLA_In_Days__c, SLA_On_Case_Transfer__c, 
                       HO_Dental__c, HO_Medical__c,(Select Criteria_1__c, Criteria_2__c, Criteria_3__c, Criteria_4__c, Criteria_5__c, Criteria_6__c, Criteria_7__c, Criteria_8__c, Criteria_9__c,
                       Criteria_10__c, Criteria_11__c, Criteria_12__c, Criteria_13__c, Criteria_14__c, Criteria_15__c, Criteria_16__c, Criteria_17__c, Criteria_18__c, Criteria_19__c, 
                       Criteria_20__c, Criteria_21__c, Criteria_22__c,Priority_1__c, Priority_2__c, Priority_3__c, Priority_4__c, Priority_5__c, Priority_6__c, Priority_7__c, Priority_8__c, 
                       Priority_9__c, Priority_10__c,Priority_11__c, Priority_12__c, Priority_13__c, Priority_14__c, Priority_15__c, Priority_16__c, Priority_17__c, Priority_18__c, 
                       Priority_19__c,Priority_20__c, Priority_21__c, Priority_22__c from Work_Queue_Priorities__r),
                       (SELECT Work_Queue_Topic_Name__r.Name, Work_Queue_Setup_Name__r.Name from Work_Queue_Juntions__r where Deactivate_Topic__c = true),(select id,Name,Group_Name__c from Queue_Views__r) from Work_Queue_Setup__c where Id =: wqId
                        limit :(limits.getLimitQueryRows() - limits.getQueryRows())];
    }
    
    /**
    * Returns Work_Queue_Priority__c work returns work queue priority record
    * <p>
    * This method is used to get the criteria values of the filter
    *
    * @param    NULL
    * @return   Work_Queue_Priority__c      Work Queue Priority Record
    */
    public Work_Queue_Priority__c getWorkQueueCriteriaValues()
    {
        return  [Select Criteria_1__c,Criteria_2__c,Criteria_3__c,Criteria_4__c,Criteria_5__c,Criteria_6__c,Criteria_7__c,Criteria_8__c,Criteria_9__c,Criteria_10__c,Criteria_11__c, Criteria_12__c,
        Criteria_13__c,Criteria_14__c,Criteria_15__c,Criteria_16__c,Criteria_17__c,Criteria_18__c,Criteria_19__c,Criteria_20__c,Criteria_21__c,Criteria_22__c from Work_Queue_Priority__c limit 1];
    }   
    
    /**
    * <p>
    * This method is used to delete records.
    *
    * @param    NULL
    * @return   Void
    */
    public void deleteRecords(List<sObject> lstSobject)
    {
        List<sObject> deletableList = new List<sObject>();
        for(sObject obj : lstSobject) {
            Schema.DescribeSObjectResult s = obj.getSObjectType().getDescribe();
            if(s.isAccessible() && s.isDeletable()) {
               deletableList.add(obj);
            }
        }
        if(deletableList.size() > 0) {
            delete deletableList;    
        }
    }
    
    /**
    * Returns List<Group> return list of public groups
    * <p>
    * This method returns all the public groups
    *
    * @param    NULL
    * @return   List<Group>     List of public groups
    */
    public List<Group> getGroup_SOQL()
    {
        return [Select developerName from Group where type = :GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM  limit :(limits.getLimitQueryRows() - limits.getQueryRows())];
    }
    
    
    /**
    * Returns User's record
    * <p>
    * This method returns user record details
    *
    * @param    sManagerId  Id of the manager
    * @return   User        
    */
    public User getUserName(string sManagerId)
    {
        return ([Select Name from User where ID =: sManagerId]);
    }
    
    /**
    * Returns Boolean verifies whether record inserted is successful or not
    * <p>
    * This method is used to create work queue related records.  
    *
    * @param    lstsObject  List of sObjects
    * @return   Boolean
    */
    public Boolean insertsObject(List<sObject> lstsObject)
    {
        Database.SaveResult[] lstSaveResult = Database.insert(lstsObject,false);
        HUMExceptionHelper.bShowErrorOnPage = false;
        Boolean berror = HUMExceptionHelper.processSaveResults(lstSaveResult, lstsObject,'WorkQueueCreation_D_HUM', 'insertsObject', lstsObject[0].getsobjectType().getDescribe().getName());
        return berror;
    }
    
    /**
    * Returns Boolean verifies whether record inserted is successful or not
    * <p>
    * This method is used to create work queue related records, which will be used in Static Context ( @future method)  
    *
    * @param    lstsObject  List of sObjects
    * @return   Boolean
    */
    public static Boolean insertStaticsObject(List<sObject> lstsObject)
    {
        Database.SaveResult[] lstSaveResult = Database.insert(lstsObject,false);
        HUMExceptionHelper.bShowErrorOnPage = false;
        Boolean berror = HUMExceptionHelper.processSaveResults(lstSaveResult, lstsObject,'WorkQueueCreation_D_HUM', 'insertStaticsObject', lstsObject[0].getsobjectType().getDescribe().getName());
        return berror;
    }
    
    /**
    * Returns Boolean verifies whether record upserted is successful or not
    * <p>
    * This method is used to upsert work queue record.  
    *
    * @param    wqRec       Work Queue Setup Record
    * @return   Boolean
    */
    public Boolean upsertWQObject(Work_Queue_Setup__c wqRec)
    {
        Database.UpsertResult[] lstUpsertResultWorkQueue = Database.upsert(new List<Work_Queue_Setup__c> {wqRec},false);
        HUMExceptionHelper.bShowErrorOnPage = false;
        Boolean bErrorWorkQueue = HUMExceptionHelper.processUpsertResults(lstUpsertResultWorkQueue, new List<Work_Queue_Setup__c> {wqRec},'WorkQueueCreation_D_HUM', 'upsertWQObject', 'Work_Queue_Setup__c');
        return bErrorWorkQueue;
    }
    
    /**
    * Returns Boolean verifies whether record upserted is successful or not
    * <p>
    * This method is used to upsert work queue priority record. 
    *
    * @param    wqPriorityRec       Work Queue Priority Record
    * @return   Boolean
    */
    public Boolean upsertWQPriorityObject(Work_Queue_Priority__c wqPriorityRec)
    {
        Database.UpsertResult[] lstUpsertResultPriority = Database.upsert(new List<Work_Queue_Priority__c> {wqPriorityRec},false);
        HUMExceptionHelper.bShowErrorOnPage = false;
        Boolean bErrorPriority = HUMExceptionHelper.processUpsertResults(lstUpsertResultPriority, new List<Work_Queue_Priority__c> {wqPriorityRec},'WorkQueueCreation_D_HUM', 'upsertWQPriorityObject', 'Work_Queue_Priority__c');
        return bErrorPriority;
    }
    
    /**
    * Returns List<work_queue_setup__c> list of work queueSetup records
    * <p>
    * This method is used to fetch work queue setup records. 
    *
    * @param    NULL
    * @return   List<work_queue_setup__c>
    */
    public List<work_queue_setup__c> getWorkQueueSetupRecords()
    {
       return [SELECT Name,Service_Center__c,Department__c,(SELECT Name,Group_Name__c,Public_Group_View_Id__c FROM Queue_Views__r),(SELECT Priority_1__c,Priority_2__c,Priority_3__c,Priority_4__c,Priority_5__c,Priority_6__c,Priority_7__c,Priority_8__c,Priority_9__c,Priority_10__c,Priority_11__c, Priority_12__c,
       Priority_13__c,Priority_14__c,Priority_15__c,Priority_16__c,Priority_17__c,Priority_18__c,Priority_19__c,Priority_20__c,Priority_21__c,Priority_22__c FROM Work_Queue_Priorities__r) from work_queue_setup__c limit :(limits.getLimitQueryRows() - limits.getQueryRows())];
    } 
    
}