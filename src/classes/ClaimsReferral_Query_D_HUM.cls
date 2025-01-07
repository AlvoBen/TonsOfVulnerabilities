/*
 * Apex class Name    : ClaimsReferral_Query_D_HUM  
 * Version            : 1.0
 * Function           : This class is data layer of ClaimsSummary functionality, this class wil perform all SOQL and DML operations.
 * Created Date       : March 03, 2016
 * Test Class         : ClaimsReferral_Query_T_HUM 

 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Kritika Popat              24466              03/03/2016                  Original Version
 * Naveenkanth B			  27208			 	 05/11/2016                  For the Defect 223672.0001.
 *******************************************************************************************************************************************************************/

public with sharing class ClaimsReferral_Query_D_HUM
{
    /*
    * Method Name: getPolicyMemberDetails
    * Description: This method will query Policy Member fields required for service as input
    * Parameter: sRecId (the String which holds Record id of the current Policy Member)
    * Return Type: PolicyMember
    */
    public Policy_Member__c getPolicyMemberDetails(String sRecId)
    {
        if(string.isNotBlank(sRecId))
        {
            Policy_Member__c objPM = [Select Id, Name, Subscriber__c,Product__c from Policy_member__c where Id =: sRecId];
            return objPM;
        }
        else
        {
            return Null;
        }
    }
    
    /*
    * Method Name: getAccountDetails
    * Description: This method will query Accoount fields required for service as input
    * Parameter: sRecId (the String which holds Record id of the current Policy Member)
    * Return Type: Account
    */
    public Account getAccountDetails(String sRecId)
    {
        if(string.isNotBlank(sRecId))
        {
            Account objAcc = [Select Id,Enterprise_ID__c from Account where Id In (Select Member__c from Policy_member__c where Id =: sRecId)];
            return objAcc;
        }
        else
        {
            return Null;
        }
    }
}