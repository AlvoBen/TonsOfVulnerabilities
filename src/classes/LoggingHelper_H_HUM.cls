/*******************************************************************************************************************************************************
Apex Class Name  : LoggingHelper_H_HUM
Version          : 1.0
Created Date     : 02/25/2016
Function         : This is helper class to handle common functionalities.
Test Class       : LoggingHelper_T_HUM
************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------
* Santhi Mandava                                     02/25/2016                 Original Version
*************************************************************************************************/

public with sharing class LoggingHelper_H_HUM
{
    /*
    * Method Name : getAccountId
    * Description : This method is used to get account id based on the given object id and object name.
    * Return type : String 
    * Parameter   : Id,String 
    */
    public static String getAccountId(Id objID,String sObjName)
    {
        String accId;
        If(sObjName == 'Account')
        {
            accId =  objID;
        }
        else if(sObjName == 'Policy__c')
        {
            accid = LoggingDataLogic_D_HUM.getAccountIdFromPolicy(objID);
        }
        else if(sObjName == 'Policy_Member__c')
        {
            accId = LoggingDataLogic_D_HUM.getAccountIdFromPolicyMember(objID) ;
        }
        return accId;
    }
}