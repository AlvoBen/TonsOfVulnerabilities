/*******************************************************************************************************************************
Apex Class Name : CaseCommentsDataTable_LD_HUM
Version         : 1.0
Created On      : 12/07/2021 
Function        : The Data layer class for CaseCommentsDataTable_LC_HUM class
Test Class      : CaseCommentsDataTable_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                        12/07/2021                 Original Version
*    2.0           Vinay Lingegowda                                    05/25/2022                 Added query for caseComment
*    3.0           Ritik Agarawal                                      04/04/2022               Add log code visibility changes US_3192496
*    3.0           Shailesh B/ Bhumika D                               30/06/2022               Add logic for delete task comment
*    4.0           Prasuna Pattabhi                                     08/24/23                 US 4412371 Market Credentialing Task Fields
*    5.0           Prasuna Pattabhi                                      10/04/23                 US 4412371 Market Credentialing Task Fields Switch Changes
*******************************************************************************************************************************************************************/

public with sharing class CaseCommentsDataTable_LD_HUM {
    public static string OBJECT_CASE_HUM = 'Case';
    public static string OBJECT_TASK_HUM = 'Task';
    /**
* Method to get Case record
*  @name fetchCaseRecord
*  @param String
*  @return Case
*/
    public static Case fetchCaseRecord(String sCaseID)
    {
        return [select id,subject,description,CaseNumber,Status,AccountId,Classification_Type__c,Classification_Id__c,Intent_Id__c,RecordType.name,Member_Plan_Id__c,
                Member_Plan_Id__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin,CTCI_List__r.Classification__r.name,CTCI_List__r.Intent__r.name,
                CTCI_List__r.Service_Model_Type__c,Account.Enterprise_ID__c,Account.ETL_Record_Deleted__c,Member_Plan_Id__r.ETL_Record_Deleted__c
                from case where id=:sCaseID LIMIT 1];
    }
    
    /**
* Method to get Case record
*  @name fetchUserRecord
*  @param NA
*  @return User
*/
    public static User fetchUserRecord()
    {
        return [Select id,Network_User_Id__c from User where ID=:userinfo.getuserID() LIMIT 1];
    }
    
    
    /*
* Method Name : getCaseCommentDetails
* Description : generate the list of records for case and related comments.
* Return type : NA
* Parameter   : Case Record Id
*/
    public static List<CaseComment> getCaseCommentDetails(String caseId){
        Id sCaseId = Id.valueOf(caseId);
        List<CaseComment> caseCommentObj = [Select Id,CreatedDate, CommentBody,CreatedBy.Name,LastModifiedBy.Name, LastModifiedDate FROM CaseComment where ParentId=: caseId  ORDER BY LastModifiedDate DESC];
        return caseCommentObj;
    }
    
    /*
* Method Name : getCurrentUserProfileName
* Description : get current user profile name.
* Return type : String
* Parameter   : NA
*/  
@AuraEnabled 
public static String getCurrentUserProfileName()
{
    Id profileId=userinfo.getProfileId();
    return [Select Id,Name from Profile where Id=:profileId].Name;
}
/*
* Method Name : getCaseRecordStatus
* Description : get the status of the case.
* Return type : String
* Parameter   : String caseId
*/  
@AuraEnabled 
public static String getCaseRecordStatus(Id objID)
{
   String sRecordStatus;
    if(objID != null && !String.isEmpty(objID)){
        String sObjName = objID.getSObjectType().getDescribe().getName(); 
        
        if(sObjName == OBJECT_CASE_HUM){
            sRecordStatus = [Select Id,Status from case where Id=:objID].Status;
        }
        else if(sObjName == OBJECT_TASK_HUM){
            sRecordStatus = [Select Id,Status from task where Id=:objID].Status;
        }
    }
    return sRecordStatus;
}
    /**
*  To delete the record from DB
*  @name deleterecord
*  @param String,String
*  @return void
*  @throws NA
*/
    
public static Boolean deleteRecord(string deleteID, String objectName) 
{   Boolean bIsDeleted = false;
    try{
        if(string.isNotBlank(deleteID) && (string.isNotBlank(objectName) && objectName == 'CaseComment')){
            CaseComment oComments = [SELECT id FROM CaseComment WHERE Id=:deleteID WITH SECURITY_ENFORCED];
            if(CaseComment.sobjecttype.getdescribe().isdeletable() && oComments != null){
                delete oComments;
                bIsDeleted = true;
            } 
        }  
        else if(string.isNotBlank(deleteID) && (string.isNotBlank(objectName) && objectName == 'Task_Field_History__c')){
            Task_Field_History__c oTFHistory = [SELECT id FROM Task_Field_History__c WHERE Id=:deleteID WITH SECURITY_ENFORCED];
            if(Task_Field_History__c.sobjecttype.getdescribe().isdeletable() && oTFHistory != null){
                delete oTFHistory;
                bIsDeleted = true;
            } 
        }    
    }catch(Exception e){
        HUMExceptionHelper.logErrors(e,'CaseCommentsDataTable_LD_HUM','deleteRecord'); 
    }
    return bIsDeleted;
}
    
    
    public static Boolean updateRecord(string updateId, String sCommentBody) 
    {      
        Boolean bIsUpdated = false;
        try{
            if(string.isNotBlank(updateId)){
                CaseComment oComments = [SELECT id, CommentBody FROM CaseComment WHERE Id=:updateId WITH SECURITY_ENFORCED];

                if(oComments != null && Schema.getGlobalDescribe().get('CaseComment').getDescribe().isUpdateable()){
                    oComments.CommentBody = sCommentBody;
                    Database.SaveResult sr =Database.update(oComments);
                    bIsUpdated= sr.isSuccess();
                }
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e,'CaseCommentsDataTable_LD_HUM','updateRecord'); 
        }
        return bIsUpdated; 
    }


    public static List<PermissionSetAssignment> fetchUserPermission(String permission1, String permission2){
        return [SELECT PermissionSetId,PermissionSet.Name  FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND (PermissionSet.Name =:permission1 OR PermissionSet.Name =:permission2)];
    }    

    /*
        * Method Name : credentialingClosable
        * Description : Check if the task can be closed or not
        * Return type : boolean
        * Parameter   : String caseId
    */  
    @AuraEnabled 
    public static boolean credentialingClosable(Id Id){
        Id caseId;
        boolean isClosable = false;
        if(HUMUtilityHelper.isCRMFunctionalityON('4412371')){
        List<Task> lstTask = [select WhatId,Credentialing_Task__c,Task_Completion_Date__c from Task where Id=:Id];
        if(!lstTask.isEmpty()){
            caseId = lstTask[0].WhatId;
            List<Case> lstCase = [select id,Owner_Queue__c,Classification_Id__r.name , Intent_Id__r.name from Case Where Id=:caseId];
            if(!lstCase.isEmpty()){
                if(lstCase[0].Owner_Queue__c == CaseEditConstants_HUM.MCD_OWNER_Q && 
                lstCase[0].Classification_Id__r.name == CaseEditConstants_HUM.MCD_Classification && 
                lstCase[0].Intent_Id__r.name == CaseEditConstants_HUM.MCD_Intent &&                  
                (String.isBlank(lstTask[0].Credentialing_Task__c) ||lstTask[0].Task_Completion_Date__c == null)){
                    isClosable = true;
                }
            }
        }   
        }   
        return isClosable;
    }
}