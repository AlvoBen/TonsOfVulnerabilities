/*******************************************************************************************************************************
Apex Class Name : AccountDetailTranscripts_LD_HUM
Version         : 1.0
Created On      : 02/09/2021
Function        : This Class serves as data class to AccountDetailTranscripts_LC_HUM. 
Test Class      : AccountDetailInteractions_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Joel George                                           02/09/2021                initial version
* Kajal Namdev                                          05/05/2021                Add query limit in SOQL
* Ashish Kumar/Ritik									09/13/2021				  Added login for Date time formatting
* Deepak khandelwal                                     08/18/2023                US_4905791 --T1PRJ0865978 - MF26212 - Consumer/Chat Transcripts Record page and Case Detail page is not displayed
* Deepak khandelwal                                     09/27/2023                 US_5050647 -- T1PRJ0865978 - MF26212 - Consumer/Chat Transcripts Record page View all option
*********************************************************************************************************************************/
public with sharing class AccountDetailTranscripts_LD_HUM
{
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    public static List<TranscriptModal> getTranscripts(string sAccId)
    {  
        List<TranscriptModal> lstTransModal= new List<TranscriptModal>();
        if(string.isNotBlank(sAccId))
        {          
        for(LiveChatTranscript trans : [SELECT Id, Name, CaseId, AccountId, StartTime, EndTime, OwnerId,Case.CaseNumber, Owner.Name, Status FROM LiveChatTranscript
                       WHERE AccountId=:sAccId order by createdDate Desc LIMIT: limitValue])
                    {
                        lstTransModal.add(new TranscriptModal(trans, String.valueOf(HealthCloudUtility_LH_HUM.dateTimeFormating(trans.StartTime)),String.valueOf(HealthCloudUtility_LH_HUM.dateTimeFormating(trans.EndTime))));
                    }
        }
        return lstTransModal;
    }

    public with sharing Class TranscriptModal{
        
        @AuraEnabled public String StartTime;
        @AuraEnabled public String EndTime;
        @AuraEnabled public String Status; 
        @AuraEnabled public String CaseNumber;
        @AuraEnabled public String OwnerName;
        @AuraEnabled public String Name;
		@AuraEnabled public Id CaseId;
        @AuraEnabled public Id ChatId;

        public TranscriptModal(LiveChatTranscript trans, String StartTime, String EndTime)
        {
            this.StartTime= StartTime;
            this.EndTime=EndTime;
            this.Status=trans.Status;
            this.CaseId=trans.CaseId; 
            this.ChatId=trans.ID; 
            this.Name= trans.Name +'#&;'+trans.Id;
            this.CaseNumber= String.isNotBlank(trans.Case?.CaseNumber) ? trans.Case.CaseNumber  : ''  +'#&;'+trans.CaseId;
            this.OwnerName= trans.Owner.Name;
        }
    }
}