/*******************************************************************************************************************************
Apex Class Name : AccountDetailTranscripts_LC_HUM
Version         : 1.0
Created On      : 02/09/2021
Function        : This Class serves as controller class 
Test Class      : AccountDetailInteractions_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Joel George                                           02/09/2021                initial version
* Ashish Kumar/Ritik									09/13/2021				  Refactored code for Date time formatting
*********************************************************************************************************************************/
public with sharing class AccountDetailTranscripts_LC_HUM
{
    @AuraEnabled
    public static Object getTranscripts(string sAccId)
    {  
        return AccountDetailTranscripts_LD_HUM.getTranscripts(sAccId);
    }
}