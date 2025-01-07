/*
 * This class is data layer of AuthReferral functionality, this class wil perform all SOQL and DML operations.
 * Apex class Name    : AuthReferral_Query_LD_HUM  
 * Version            : 1.0
 * Function           : This class applies the security filters on results fetched from the Service callout.
 * Created Date       : 12/07/2022
 * Test Class         : AuthReferral_Query_LT_HUM
 * Author            :  
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Rajesh Narode                29042             14/07/2022                 Original Version 
 * Vishal Shinde                3811365           19/09/2022                  HUMException Helper Remediation 
 ******************************************************************************************************************************************************************/

public with sharing class AuthReferral_Query_LD_HUM
{ 
   //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs = new List<Error_Log__c>();
    
   /*
    * Method Name: checkHOAccountMember
    * Description: This method will query current instance of Account to get Home_Office_Account field
    * Parameter: recId the Id which holds data for current Account instance
    * Return Type: Boolean
    */
    public Boolean checkHOAccountMember(Id recId)
    {
        if(recId != Null)
        {        
            return [Select id, Home_Office_Account__c From Account Where Id =: recId].Home_Office_Account__c;
        }
        else
        {
            return false;
        }
    }
    
   /*
    * Method Name: getAccountGroupRecords
    * Description: This method will query Account object for given Group Ids
    * Parameter: setGroupIds the Set<String> which holds unique group ids from service
    * Return Type: List<Account>
    */
    public List<Account> getAccountGroupRecords(Set<String> setGroupIds)
    {
        if(!setGroupIds.isEmpty() && setGroupIds!=null)
        {   
            return [Select Id, Home_Office_Account__c, Group_Number__c From Account Where Group_Number__c IN : setGroupIds];
        }
        else
        {
            return Null;
        }
    }
    
   /*
    * Method Name: getUserRecordAccess
    * Description: This method will query UserRecordAccess object for given Ids
    * Parameter: setIds the Set<Id> which holds read access permission for given record Ids
    * Return Type: List<UserRecordAccess> is a list of UserRecordAccess Object.
    */
    public List<UserRecordAccess> getUserRecordAccess(Set<Id> setIds)
    {
        if(!setIds.isEmpty() && setIds!=null)
        {
            return [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : setIds];
        }
        else
        {
            return Null;
        }
    }
    
   /*
    * Method Name: getRelatedPolicies
    * Description: This method will query MemberPlan object for given Id
    * Parameter: recId the Id of Account record
    * Return Type: List<MemberPlan>
    */
    public List<MemberPlan> getRelatedPolicies(Id recId)
    {
        if(recId != Null)
        {
            return [Select Id, Policy__c From MemberPlan Where MemberId =: recId];
        }
        else
        {
            return Null;
        }
        
    }
   /*
    * Method Name: getMemberAccount
    * Description: This method will query Member account fields required for service as input
    * Parameter: sRecId the String which holds Record id of the current Policy Member
    * Return Type: Member Account
    */
    public static Account getMemberAccount(String sRecId)
    {
        if(string.isNotBlank(sRecId))
        {
           Account objAcct = [Select Id,Enterprise_ID__c,Birthdate__c,ShippingAddress,PersonMailingAddress,Name from Account where Id In (Select MemberId from MemberPlan where Id =: sRecId)]; 
            return objAcct;
       	    
        }
        else
        {
            return Null;
        }
    }
    
      /*
     * Method Name   :  getMemberName
     * Description   :  This method is return membername
     * Return Type   :  List<MemberPlan>
     * Parameters    :  ID sRecId
     */
    public List<MemberPlan> getMemberName(ID sRecId)
    {   
        List<MemberPlan> lstpolmem = null;
        try
        { 
            lstpolmem = new List<MemberPlan>();
            lstpolmem = [SELECT Id, Policy__r.Client_Number__c, Member.FirstName, Relationship_Code__c, Member.name FROM MemberPlan WHERE ID=:sRecId];
        }
        catch (Exception e)
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
			listErrorLogs.addAll(HUMExceptionHelper.logErrors(e,'AuthReferral_Query_LD_HUM ', 'getMemberName'));
        }
        return lstpolmem;
    }   
    
    public static String getEnterpriseId(String sObjId)
    {
        String sEnterpriseId = '';
        MemberPlan objPolMember = [Select Member.Enterprise_ID__c from MemberPlan where Id =:sObjId] ;    
        if(objPolMember != Null)  
        {
            sEnterpriseId = objPolMember.Member.Enterprise_ID__c;
        }
        return sEnterpriseId ;
    } 
    /*
     * Method Name   :  getAccountId
     * Description   :  This method is return Account id for a member
     * Return Type   : String
     * Parameters    :  ID sRecId
     */
    public static String getAccountId(String sObjId)
    {
        String sAccountId = '';
        MemberPlan objPolMember = [Select Member.Id from MemberPlan where Id =:sObjId] ;    
        if(objPolMember.Member.Id != Null)  
        {
            sAccountId = objPolMember.Member.Id;
        }
        return sAccountId ;
    } 
   
}