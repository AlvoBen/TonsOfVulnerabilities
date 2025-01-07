/***********************************************************************************************************
Apex Class Name  : CaseRelatedInfo_LC_HUM
Version          : 1.0
Function         : This class is for displaying case related Information data in lightning
Test Class       : CaseRelatedInfo_LT_HUM
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gowthami Thota                       			10/10/2022                 Original Version
************************************************************************************************************/
public with sharing class CaseRelatedInfo_LC_HUM {
    
    /**
    * Function: method called to return lsit of Attachment records
    * @param  String
    * @return String
    */
    @AuraEnabled
    public static string getCaseRelatedInfo(String sCaseId) 
	{
        return JSON.serialize(getAttachmentsList(sCaseId));
    }
    
    /**
    * Function: This method fetches Attachments and Attachment log item records related to the Attachement Logs.
    * @param  String, Boolean, Boolean
    * @return List<String>
    */
    public static list<AttachmentDTO_LC_HUM> getAttachmentsList(String sCaseId)
    {
        String crmServiceSystem = CaseDetailConstants_HUM.CRMSERVICESYSTEMNAME_HUM;
        Integer humLimit = Integer.valueOf(Label.HUM_Limit);
        List<Id> attachmentLogList = new List<Id>();
        List<AttachmentDTO_LC_HUM> AttachmentDTOList = new List<AttachmentDTO_LC_HUM>();
         Map<Id,Attachment_Log__c> attachmentLogMap = new Map<Id,Attachment_Log__c> ([Select Id,Name,Attachment_Type__c,Created_By__c,CreatedDate,Source_System__c,Case__r.casenumber from Attachment_Log__c where Case__c = :sCaseId AND ((Source_System__c = :crmServiceSystem and Attachment_Type__c = :CaseDetailConstants_HUM.CLAIMS_MANUAL_ENTRY) OR (Source_System__c != :crmServiceSystem and Attachment_Type__c !=:CaseDetailConstants_HUM.CLAIMS_MANUAL_ENTRY))  ORDER BY Id DESC limit :humLimit]);
        Map<Id,List<Attachment_Log_Item__c>> attachmentLogItemMap = new Map<Id,List<Attachment_Log_Item__c>>();
        List<Attachment_Log_Item__c> attachLogList ;
        for(Attachment_Log_Item__c attachItemLogItem : [Select id,Name,Value__c,Attachment_Log__c,Attachment_Log__r.Attachment_Type__c from Attachment_Log_Item__c where Attachment_Log__c in :attachmentLogMap.keySet() and Name != :CaseDetailConstants_HUM.sMsgDefCode order by Custom_Order_Number__c])
        {
            attachLogList = attachmentLogItemMap.containsKey(attachItemLogItem.Attachment_Log__c) ? attachmentLogItemMap.get(attachItemLogItem.Attachment_Log__c) : new List<Attachment_Log_Item__c>();
            attachLogList.add(attachItemLogItem);
            attachmentLogItemMap.put(attachItemLogItem.Attachment_Log__c,attachLogList);
        }
        for(Attachment_Log__c attachmentLog :attachmentLogMap.values()) 
		{
            AttachmentDTO_LC_HUM  attDTO = new AttachmentDTO_LC_HUM (attachmentLog,attachmentLogItemMap.get(attachmentLog.Id));
            AttachmentDTOList.add(attDTO);
        }
        return AttachmentDTOList;
        
    }
    /**
    * Function: method fetches Attachment log item records related to the Attachement Log.
    * @param  String, Boolean, Boolean
    * @return List<String>
    */
    @AuraEnabled
    public static list<Attachment_Log_Item__c> getAttachmentLogItems(String sAttLogId){
        List<Attachment_Log_Item__c> attLogItem = [select id,name,Value__c,Attachment_Log__r.Name from Attachment_Log_Item__c WHERE Attachment_Log__r.Name = : sAttLogId];
        return attLogItem; 
    }
    /**
    * Function: method fetches case number 
    * @param  String, Boolean, Boolean
    * @return List<String>
    */
    @AuraEnabled(cacheable=true)
    public static String getCaseNumber(String recordId) {
            List<Case> lstCase = [Select id,CaseNumber From Case Where Id =:recordId limit 1];
            return lstCase[0].CaseNumber;
    }

}