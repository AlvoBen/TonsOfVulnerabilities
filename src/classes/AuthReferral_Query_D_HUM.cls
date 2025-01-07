/*
 * This class is data layer of AuthReferral functionality, this class wil perform all SOQL and DML operations.
 * Apex class Name    : AuthReferral_Query_D_HUM  
 * Version            : 1.0
 * Function           : This class applies the security filters on results fetched from the Service callout.
 * Created Date       : 12/09/2015
 * Test Class         : AuthReferral_Query_T_HUM
 * Author            : Manish Kumar 
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                 29042             12/09/2015                 Original Version
 * Sruthi Adivikolanu           29042             12/18/2015                 Included getMemberAccount method to get Account details based on Policy member Id. 
 * Sruthi Adivikolanu           29646             01/27/2016                 Modified as per coding standards.
 * Santhi Mandava               30647             03/26/2016                 Moved queries from controller to this class.
 * Prasanthi Kandula            30854             04/06/2016                 Removed queries related to interaction and modified getInteractions() method
 * Praveen Kumar Parimi         28468             05/26/2016                 Removed getInteractions() method since Req#196131 is cancelled.
 * Harika Devi Kalidindi        36595             08/24/2016                 Defect Fix 238145.0001 -Added getMemberName method 
 * Santhi Mandava               40488             09/20/2016                 Added logic to Retrieve enterprise id from account object.
 * Harika Devi Kalidindi        103812            06/21/2017                 REQ - 320375 -Changed class from Without sharing to with sharing
 * Joel George                                    08/08/2017                 Added function to return account id for policy member
 * Sai Ayyagari                184451              02/21/2018                 REQ - 350853 Fix for HUMExceptionHelper, store the list of exceptions capture
 ******************************************************************************************************************************************************************/

public with sharing class AuthReferral_Query_D_HUM
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
    * Description: This method will query Policy_Member__c object for given Id
    * Parameter: recId the Id of Account record
    * Return Type: List<Policy_Member__c>
    */
    public List<Policy_Member__c> getRelatedPolicies(Id recId)
    {
        if(recId != Null)
        {
            return [Select Id, Policy__c From Policy_Member__c Where Member__c =: recId];
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
    public Account getMemberAccount(String sRecId)
    {
        if(string.isNotBlank(sRecId))
        {
            Account objAcct = [Select Id,Enterprise_ID__c,Birthdate__c from Account where Id In (Select Member__c from Policy_member__c where Id =: sRecId)];
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
     * Return Type   :  List<POLICY_Member__C>
     * Parameters    :  ID sRecId
     */
    public List<POLICY_Member__C> getMemberName(ID sRecId)
    {   
        List<POLICY_Member__C> lstpolmem = null;
        try
        { 
            lstpolmem = new List<POLICY_Member__C>();
            lstpolmem = [SELECT Id, Policy__r.Client_Number__c, Member__r.FirstName, Relationship_Code__c, Member__r.name FROM POLICY_Member__C WHERE ID=:sRecId];
        }
        catch (Exception e)
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(e,'AuthReferral_Query_D_HUM ', 'getMemberName');
        }
        return lstpolmem;
    }   
    
    public static String getEnterpriseId(String sObjId)
    {
        String sEnterpriseId = '';
        Policy_Member__c objPolMember = [Select Member__r.Enterprise_ID__c from Policy_Member__c where Id =:sObjId] ;    
        if(objPolMember != Null)  
        {
            sEnterpriseId = objPolMember.Member__r.Enterprise_ID__c;
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
        Policy_Member__c objPolMember = [Select Member__r.Id from Policy_Member__c where Id =:sObjId] ;    
        if(objPolMember.Member__r.Id != Null)  
        {
            sAccountId = objPolMember.Member__r.Id;
        }
        return sAccountId ;
    } 
   
}