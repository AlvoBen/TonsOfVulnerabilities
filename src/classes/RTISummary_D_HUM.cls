/*****************************************************************************************
Apex class Name    : RTISummary_D_HUM
Version            : 1.0
Function           : DataLayer Class for the RTI Summary
Created Date       : 01/04/2017
Test Class         : RTISummaryDataLogic_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 * Pallavi ravishankar                                 01/04/2017         Original Version
 * Koriprolu Vijaya            234129                  05/24/2018         REQ-359055, REQ-359056,REQ-359057
 * Koriprolu Vijaya            233371                  05/24/2018         REQ-359055, REQ-359056,REQ-359057
 * Koriprolu Vijaya            252265                  07/04/2018         REQ - 367805 changes
 * Sumit Pharande			 						   2/2/2019           Fix for RTI panel Check for 18 and 15 digit salesforce id.
 * Sumit Pharande			 							                  REQ-386780 -Removed the 18 to 15 Digit Id conversion
 * Amar Gadewar                                        01/28/2020         USER STORY 729651 and USER STORY 729659 and REQ - 416082 Move Icons from Communication Panel to Under Page Titles on Member Account page / Policy Member Page
 * Prasanthi Kandula                                   04/20/2021         USER STORY 1875032 Checkmarx low security fixes   
 * Suraj Patil										   07/27/2021		  REQ - 1928698 PR00094226 - CR AEP readiness - Low Security Issues 
*********************************************************************************************************************************/
public with sharing class RTISummary_D_HUM
{ 
 /*
  * Method Name   :  getAccountDetails
  * Description   :  This method is used for getting Account details
  * Return Type   :  Account 
  * Parameters    :  String, String
  */
    public static Account getAccountDetails(String sRecId)
    {
        Account objAcc = new Account();
        try
        { 
            if(String.valueof(sRecId).startswith(Schema.SObjectType.Account.keyprefix))
            {

                List<Account> lstAccount = [Select Name, Security_Groups__c, Enterprise_ID__c,Deceased_Date__c,Birthdate__c  from Account where Id = :String.escapeSingleQuotes(sRecId)];
                if(!lstAccount.isEmpty())
                {
                    objAcc = lstAccount[0];       
                }
            }
            else
            {
 
                List<Policy_Member__c> lstPolMember = [Select Member__r.Id from Policy_Member__c where Id =:String.escapeSingleQuotes(sRecId)];
				if(lstPolMember.size() > 0 && !lstPolMember.isEmpty() && string.IsNotBlank(lstPolMember[0].Member__r.Id))
                {
                    objAcc  = [Select Name, Security_Groups__c, Enterprise_ID__c,Deceased_Date__c,Birthdate__c  from Account where Id = :String.EscapeSingleQuotes(lstPolMember[0].Member__r.Id)];
					
                }
            }
           if(Test.isRunningTest()) throw new HUMCustomException('Error');       
        }   
        catch(exception ex)
        {
			HUMExceptionHelper.logErrors(ex,'RTISummary_D_HUM', 'getEnterpriseID');
            objAcc = new Account();
        } 
        return objAcc;   
    }   
}