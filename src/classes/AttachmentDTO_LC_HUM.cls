/***********************************************************************************************************
Apex Class Name  : AttachmentDTO_LC_HUM
Version          : 1.0
Function         : This class is for holding wrapper variables of CaseRelatedInfo_LC _HUM
Test Class       : CaseRelatedInfo_LT_HUM
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gowthami Thota                       			10/10/2022                 Original Version
************************************************************************************************************/
public with sharing class AttachmentDTO_LC_HUM {
    @AuraEnabled
    public String attachmentType {get; set;}
    @AuraEnabled
    public String attachmentId{get; set;}
    @AuraEnabled
    public String loggedDateTime {get; set;}
    @AuraEnabled
    public String loggedBy {get; set;}
    @AuraEnabled
    public String recId {get; set;}
    @AuraEnabled
    public String sourceSys {get; set;}
    @AuraEnabled
    public String sCaseNumber {get; set;}
    @AuraEnabled
    public List<AttachmentLogItemDTO> attachmentLogItemDTOList {get; set;}
    
    public AttachmentDTO_LC_HUM(Attachment_Log__c log,List<Attachment_Log_Item__c> logItemList) 
	{
        this.attachmentType = log.Attachment_Type__c;
        this.attachmentId = log.Name;
        this.loggedDateTime = log.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
        this.loggedBy = log.Created_By__c;
        this.recId = log.Id;
        this.sourceSys = log.Source_System__c;
        this.sCaseNumber = log.Case__r.casenumber;
        this.attachmentLogItemDTOList = getAttachmentLogItemDTOList(logItemList);
    }
    
   public class AttachmentLogItemDTO 
    {
        public String Name {get; set;}
        public String Value {get; set;}
        public String isLinkRequiredWrap{get; set;}
        public AttachmentLogItemDTO(Attachment_Log_Item__c logItem) 
        {
            this.Name = logItem.Name;
            this.Value = logItem.Value__c;
            this.isLinkRequiredWrap = logItem.Attachment_Log__r.Attachment_Type__c;
        }
    }
    
    public List<AttachmentLogItemDTO> getAttachmentLogItemDTOList(List<Attachment_Log_Item__c> logItemList) 
	{
        List<AttachmentLogItemDTO> attachmentLogItemDTOList = new List<AttachmentLogItemDTO>();
        if(logItemList != null && !logItemList.isEmpty()) 
		{
            for(Attachment_Log_Item__c logItem : logItemList) 
			{
                attachmentLogItemDTOList.add(new AttachmentLogItemDTO(logItem));
            }
        }
        return attachmentLogItemDTOList;
    }

}