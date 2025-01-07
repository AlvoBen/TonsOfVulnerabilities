/***********************************************************************************************************************
   @Author      : Vijaya Lakshmi Tummala
   @name        : HUMContentDocumentTriggerHelper
   @CreateDate  : Oct/17/2022
   @Description : This class serves as Helper for the trigger HUMContentDocumentTrigger
   @Version     : 1.0
************************************************************************************************************************
Modification Log:
 * Developer Name            	Code Review   Date           Description
 *------------------------------------------------------------------------------------------------------------
 * Vijaya Lakshmi Tummala                 	 10/17/2022     Original Version-Case Attachment -Feature-3033033
 * 															UserStory -3775271 Creating Case Attachments in Lightning
 Gowthami Thota                              02/28/2023     US4313616: T1PRJ0170850 - INC2169863 - Lightning- Case Mgt- Attachments
                                                            FIX for Communities, CDO & Wellness Coaching		
************************************************************************************************************************/
public class HUMContentDocumentTriggerHelper {
	/*
     * Method Name : onBeforeDelete
     * Description : Method to perform deletion logic on content version
     * Return type : void
     * Paramater   : List<ContentVersion> 
     */
    public static void onBeforeDelete(List<ContentDocument> lstOldContentDocument)
    {
        Set<ID> setConVerIDs = new set<ID>();
		Set<ID> setConDocIDs = new set<ID>();
		Map<Id,Id> mapConentDocumentContentVersion = new  Map<Id,Id>();
        List<ContentVersion> lstOldContentVersions = new List<ContentVersion>();
        for(ContentDocument conDoc:lstOldContentDocument)
        {
			setConVerIDs.add(conDoc.LatestPublishedVersionId);			
        }
          
		for(ContentVersion ContentVerRec : [select Id,ContentDocumentId,Attachment__c,Attachment__r.DocId__c,Attachment__r.Version_Id__c,Attachment__r.Attachment_URL_Text__c from ContentVersion where id in : setConVerIDs])
        {
            lstOldContentVersions.add(ContentVerRec) ; 
        }
		
		for(ContentVersion ContentVerRec :lstOldContentVersions)
        {
            if(ContentVerRec.Attachment__r.DocId__c == null && ContentVerRec.Attachment__r.Version_Id__c == null && ContentVerRec.Attachment__r.Attachment_URL_Text__c == null )
                {
					mapConentDocumentContentVersion.put(ContentVerRec.ContentDocumentId,ContentVerRec.id);
                    setConDocIDs.add(ContentVerRec.ContentDocumentId);
                }
        }
		 
		if(setConDocIDs != null && !setConDocIDs.isEmpty())
        {
			for(ContentDocument ContentDocRec : lstOldContentDocument)
			{
				if(mapConentDocumentContentVersion.containsKey(ContentDocRec.id))
				{ContentDocRec.addError(Label.HumRestrictFileDeletion);}
			}
		}
        
         
    }
	/*
     * Method Name :  onBeforeUpdate
     * Description :  Execute all on before Update Trigger Events
     * Return Type :  void
     * Parameters  :  List<ContentDocument> , List<ContentDocument> , Map<Id,ContentDocument> ,MAp<ID,ContentDocument> 
     */
	public static void onBeforeUpdate(List<ContentDocument> lstOldContentVersions, List<ContentDocument> lstNewContentVersions , Map<Id,ContentDocument> mapNewContentVersion,MAp<ID,ContentDocument> mapOldContentVersion) 
    {
        Set<ID> setConVerIDs = new set<ID>();
		Set<ID> setAttachmentIDs = new set<ID>();
		Map<Id,Id> mapCustomDocuments = new  Map<Id,Id>();
        List<ContentVersion> lstContentVersions = new List<ContentVersion>();
		
        for(ContentDocument conDoc:lstOldContentVersions)
        {
            setConVerIDs.add(conDoc.LatestPublishedVersionId);
			mapCustomDocuments.put(conDoc.id,conDoc.LatestPublishedVersionId);
        }
		
        for(ContentVersion ContentVerRec : [select Id,ContentDocumentId,Attachment__c,Attachment__r.DocId__c,Attachment__r.Version_Id__c,Attachment__r.Attachment_URL_Text__c from ContentVersion where id in : setConVerIDs])
        {
           if(ContentVerRec.Attachment__c != null)
           setAttachmentIDs.add(ContentVerRec.Attachment__c);
        }
		  
		if(setAttachmentIDs != null && !setAttachmentIDs.isEmpty())
		{
			for(ContentDocument ContentDocRec : lstNewContentVersions)
			{
				if(mapCustomDocuments.containsKey(ContentDocRec.id)) 
                {ContentDocRec.addError(Label.HumRestrictFileUpdate);}
			}
		}
	}  
    
	/*
     * Method Name   :    onBeforeInsert
     * Description   :    Execute all on before Insert Trigger Events
     * Return Type   :    void
     * Parameters    :    List<ContentDocument>
     */
	public static void onBeforeInsert(List<ContentDocument> lstNewContentDocument) 
    {
        validateDuplicateAttachments(lstNewContentDocument);
    }
    
    /*
     * Method Name :  Validate Duplicate contentVersion on Custom ContentDocument object
     * Return Type :  void
     * Parameters  :  List<ContentDocument>
     */
    public static void validateDuplicateAttachments(List<ContentDocument> lstNewContentDocs) 
    {        
        //Set to Store Parent IDs
        Set<ID> setParentIDs = new set<ID>();
        
        //Set to track any Duplicate contentVersion in the new list
        set<ID> setDupParentIds = new Set<ID>();
		
        for(ContentDocument conDoc:lstNewContentDocs)
        {
            if(!setParentIDs.add(conDoc.LatestPublishedVersionId)) 
            {                
                setDupParentIds.add(conDoc.LatestPublishedVersionId);
            }    
        }
         //Fire a query to identify existing contentVersion for a parent ID
        Map<Id,ContentDocument> mapDupContentVersions = new Map<Id,ContentDocument>([Select Title,(select id from ContentVersions) from ContentDocument where Id in :setParentIDs]);
				
        for(ContentDocument attachNew:lstNewContentDocs) 
        {
			if((mapDupContentVersions.containsKey(attachNew.parentID) && (mapDupContentVersions.get(attachNew.parentID).ContentVersions.size()>0 || mapDupContentVersions.get(attachNew.parentID).Title!=null)) ||
                    (mapDupContentVersions.containsKey(attachNew.parentID) && setDupParentIds.contains(attachNew.parentID) && mapDupContentVersions.get(attachNew.parentID).ContentVersions.size()==0)) 
                    {
                       attachNew.addError(Label.HUMRestrictDuplicateAttachment);
                    }
        }
	}

}