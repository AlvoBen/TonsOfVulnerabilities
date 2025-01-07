/**
   @Author      : Prasad Jandhyala
   @name        : CCPGCPAttachmentDTO_C_HUM
   @CreateDate  : 10/30/2017
   @Description : This DTO is used in CCPGCPDataTableExtension_C_HUM
   @Version     : 1.0
   @reference   : CCPGCPDataTableExtension_C_HUM
   
   Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Prasad Jandhyala         149219                      10/30/2017                     Original version
 * Sumit Pharande                                       27/12/2017                     Defect Fix -334135.0001
 * Sumit Pharande                                       28/12/2017                     Rollback Changes  
 * Joel George	                                        11/14/2018                     REQ 378818 Changes  
 * Vijaya Koriprolu                                     11/18/2019                 REQ-383958 added functionality for adding claim numbers on case edit page
********************************************************************************************************************************/
public with sharing class CCPGCPAttachmentDTO_C_HUM 
{
    public String attachmentType {get; set;}
    public String attachmentId{get; set;}
    public String loggedDateTime {get; set;}
    public String loggedBy {get; set;}
    public String recId {get; set;}
    public String sourceSys {get; set;}
    public List<AttachmentLogItemDTO> attachmentLogItemDTOList {get; set;}
    
    public CCPGCPAttachmentDTO_C_HUM(Attachment_Log__c log,List<Attachment_Log_Item__c> logItemList) 
	{
        this.attachmentType = log.Attachment_Type__c;
        this.attachmentId = log.Name;
        this.loggedDateTime = log.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
        this.loggedBy = log.Created_By__c;
        this.recId = log.Id;
        this.sourceSys = log.Source_System__c;
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