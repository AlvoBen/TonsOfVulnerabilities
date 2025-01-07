/*******************************************************************************************************************************
Apex Class Name : ArchivedCaseHistory_LD_HUM
Version         : 1.0
Created On      : 07/22/2022   
Function        : This class is used to get data for archived cases.
Test Class      : ArchivedCaseHistory_LT_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ankima/Isha Gupta		                         07/22/2022                  Original version
**********************************************************************************************************************************************************/
public with sharing class ArchivedCaseHistory_LD_HUM {
    /*
    * Method Name : CTCIData
    * Return type : List<CTCI_Junction__c>
    * Parameter   : List<String>
    */
    @AuraEnabled
    public static List<CTCI_Junction__c> CTCIData(List<String> CTCIIds) {
        List<CTCI_Junction__c> CTCIObj = new List<CTCI_Junction__c>();
        CTCIObj = [Select id,Classification__r.Name,Intent__r.Name from CTCI_Junction__c where id=:CTCIIds];
        return CTCIObj;
    }
	/*
    * Method Name : AccData
    * Return type : List<Account>
    * Parameter   : List<String>
    */
    @AuraEnabled
    public static List<Account> AccData(List<String> AccountIds) {
        List<Account> AccObj = new List<Account>();
        AccObj = [Select id,Name from Account where id=:AccountIds];
        return AccObj;
    }
    /*
    * Method Name : accRelatedToPolicy
    * Return type : String
    * Parameter   : String
    */
    @AuraEnabled
    public static String accRelatedToPolicy(String recId) {
        String memId = [SELECT Id, Member__c FROM Policy_Member__c where id=:recId].Member__c;
        return memId;
    }
        /*
    * Method Name : accRelatedToMemberPlan
    * Return type : String
    * Parameter   : String
    */
    @AuraEnabled
    public static String accRelatedToMemberPlan(String recId) {
        String memId = [SELECT Id, MemberId FROM MemberPlan where id=:recId].MemberId;
        return memId;
    }
    /*
    * Method Name : groupList
    * Return type : List<String>
    * Parameter   : String
    */
    @AuraEnabled
    public static List<String> groupList(String userId) {
        List<String> groupNameList = new List<String>();
        for(GroupMember groupmem:[Select id,groupid,Group.DeveloperName from GroupMember where userOrGroupId=:userId]){
        	groupNameList.add(groupmem.groupid); 
        }
        return groupNameList;
    }
     /*
    * Method Name : fetchGroupId
    * Return type : List<String>
    * Parameter   : List<String>
    */
    @AuraEnabled
    public static List<String> fetchGroupId(List<String> mainGroupName) {
        List<String> mainGroupId = new List<String>();
        for(Group groupmem:[Select id from Group where DeveloperName =: mainGroupName]){
            mainGroupId.add(groupmem.id); 
        }
        return mainGroupId;
    }
    
       /*
    * Method Name : fetchContainerGroupId
    * Return type : List<GroupMember>
    * Parameter   : Set<String>
    */
    public static List<GroupMember> fetchContainerGroupId(Set<String> indirectGroup) {
        return [Select groupId from GroupMember where userorgroupid=:indirectGroup];
    }
     /*
    * Method Name : fetchGroupNames
    * Return type : List<String>
    * Parameter   : List<String>
    */
    @AuraEnabled
    public static List<String> fetchGroupNames(List<String> mainGroupIds) {
        List<String> mainGroupNames = new List<String>();
        for(Group groupmem:[Select DeveloperName from Group where id =: mainGroupIds]){
            mainGroupNames.add(groupmem.DeveloperName); 
        }
        return mainGroupNames;
    }
    /*
    * Method Name : allInternalUsers
    * Return type : Boolean
    * Parameter   : String
    */
    @AuraEnabled
    public static Boolean isInternalUser(String userId) {
        Boolean internalUser=false;
        User loginuser = [Select id,UserType,IsPortalEnabled from User where id=:userId and IsActive=true limit 1];
            if((loginuser.UserType=='Standard') && (loginuser.IsPortalEnabled==false)){
            	internalUser= true;    
            }  
        
        return internalUser;
    }
    /*
    * Method Name : caseSharingRules
    * Return type : List<Case_SharingRule__mdt>
    * Parameter   : NA
    */
    @AuraEnabled
    public static List<Case_SharingRule__mdt> caseSharingRules() {
        List<Case_SharingRule__mdt> sharingmdt = new List<Case_SharingRule__mdt>();
        sharingmdt= [Select id,Group_Name__c,Criteria__c from Case_SharingRule__mdt]; //Group_Name__c='Humana_General'
        return sharingmdt;
    }
    
    /*
    * Method Name : caseRecordtype
    * Return type : List<recordType>
    * Parameter   : NA
    */
    @AuraEnabled
    public static List<recordType> caseRecordtype() {
        List<recordType> recType = new List<recordType>();
        recType =  [Select id,DeveloperName  from recordType where sObjectType='Case'];
        return recType;
    }
    
    
    
    
}