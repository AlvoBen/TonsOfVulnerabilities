/*******************************************************************************************************************************
Apex Class Name : ArchivedCaseDetail_LD_HUM
Version         : 1.0
Created On      : 07/18/2022   
Function        : This class is used to get data from archived case detail service.
Test Class      : ArchivedCaseDetail_LT_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ashish Kumar/Kajal Namdev		                   07/18/2022                  Original version
**********************************************************************************************************************************************************/
public with sharing class ArchivedCaseDetail_LD_HUM {

    /*
    * Method Name : fetchAccountData
    * Return type : List<Account>
    * Parameter   : String
    */
    public static List<Account> fetchAccountData(String accId)
    {  
            return [Select id, Name from Account where Id =: accId];
    }
    
   	/*
    * Method Name : fetchMemberPlanData
    * Return type : List<MemberPlan>
    * Parameter   : String
    */
    public static List<MemberPlan> fetchMemberPlanData(String memberPlanId)
    {

        return [Select id, Name ,Member_Plan_External_Id__C from MemberPlan where Id =: memberPlanId];

    }
    
      	/*
    * Method Name : fetchPolicyMemberData
    * Return type : List<Policy_Member__c
    * Parameter   : String
    */
    public static List<Policy_Member__c> fetchPolicyMemberData(String memberPlanExternalId)
    {
       	 return [Select id, Name from Policy_Member__c where Policy_Member_External_Id__c =: memberPlanExternalId];
    }
    /*
    * Method Name : getPolicyMember
    * Return type : List<Policy_Member__c
    * Parameter   : String
    */
    public static List<Policy_Member__c> getPolicyMember(String policyMemberId)
    {
            return [Select id, Name from Policy_Member__c where Id =: policyMemberId];
    }
    /*
    * Method Name : fetchClassificationData
    * Return type : List<Classification__c>
    * Parameter   : String
    */
    public static List<Classification__c> fetchClassificationData(String classificationId)
    {
            return [Select id, Name from Classification__c where Id =: classificationId ];
    }
    
    /*
    * Method Name : fetchIntentData
    * Return type : List<Intent__c>
    * Parameter   : String
    */
    public static List<Intent__c> fetchIntentData(String intentId)
    {
        return [Select id, Name from Intent__c where Id =: intentId];    
    }
    
    /*
    * Method Name : fetchRecordType
    * Return type : RecordType
    * Parameter   : String
    */
    public static RecordType fetchRecordType(String recTypeId)
    {
         return [Select id, Name from RecordType where Id=:recTypeId];
    }
    
    /*
    * Method Name : fetchUserDetails
    * Return type :	List<User> 
    * Parameter   : Set<Id>
    */
    public static List<User> fetchUserDetails(Set<Id> userIds)
    {
        if(userIds.size()>0)
        {
            return [SELECT Id, Name FROM User where Id IN : userIds];
        }
        return null;
    }
    
      /*
    * Method Name : fetchUser
    * Return type :	List<User> 
    * Parameter   : String 
    */
    public static List<User> fetchUser(String userId)
    {
 
            return [SELECT Id, Name FROM User where Id=:userId];
      
    }

              /*
    * Method Name : fetchContact
    * Return type :	List<Contact> 
    * Parameter   : String 
    */
    public static List<Contact> fetchContact(String contactId)
    {
 
            return [SELECT Id, Name FROM Contact where Id=:contactId];
      
    }

}