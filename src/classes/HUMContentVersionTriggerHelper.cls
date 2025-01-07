/***********************************************************************************************************************
   @Author      : Vijaya Lakshmi Tummala
   @name        : HUMContentVersionTriggerHelper
   @CreateDate  : Oct/17/2022
   @Description : This class serves as Helper for the trigger HUMContentVersionTrigger
   @Version     : 1.0
************************************************************************************************************************
Modification Log:
 * Developer Name            	Code Review   Date           Description
 *------------------------------------------------------------------------------------------------------------
 * Vijaya Lakshmi Tummala                 	 10/17/2022     Original Version-Case Attachment -Feature-3033033
 * 															UserStory -3775271 Creating Case Attachments in Lightning
 * Gowthami Thota                            08/31/2023     User Story 4943505: T1PRJ0891742 - AHTR - SF Security Remediation - Case Lgt - HUMContentVersionTrigger		
************************************************************************************************************************/
public with sharing class HUMContentVersionTriggerHelper {     
    /*
     * Method Name : onAfterInsert
     * Description : Method to create attachemnt__c custom object record once ConentVersion record is inserted from lightning
     * Return type : void
     * Paramater   : List<ContentVersion> 
     */
    public static void onAfterInsert(List<ContentVersion> lstNewContentVersions)
    {
        // Attachment__c record is inserted based on content version record
        if(lstNewContentVersions != null  && !lstNewContentVersions.isEmpty())
		{
			List<Attachment__c> newAtt = new List<Attachment__c>();
            Map<id, Attachment__c> mapContentVersionAttach = new  Map<id, Attachment__c>();
            List<ContentVersion> contentVersionUpdate = new List<ContentVersion>();
            List<String> attachmentIDs = new  List<String>();
            
			
			for(ContentVersion conver : lstNewContentVersions)
			{
				Attachment__c attachment = new Attachment__c();
				
                attachment.RecordtypeID  = HUMUtilityHelper.getRecordTypeID(Attachment__c.sObjectType ,'Case');
                attachment.Name=conver.Title;
                attachment.Related_To_Case__c 	= conver.FirstPublishLocationId; 
                attachment.Name=conver.Title;
                attachment.Document_Source__c = CaseDetailConstants_HUM.LWC;
				
                newAtt.add(attachment);			
                mapContentVersionAttach.put(conver.id,attachment);  			
                
			}
            
            if(!newAtt.isEmpty()){
                Database.SaveResult[] srList = Database.insert(newAtt, false);
				// Iterate through each returned result
                for (Database.SaveResult sr : srList) {
                    if (sr.isSuccess()) {
                        // Operation was successful, so get the ID of the record that was processed
                        
                        
                    }
                    else {
                        // Operation failed, so get all errors                
							for(Database.Error err : sr.getErrors())
							{
								
								
								
							}
					}
                }
            }
            
            if(!mapContentVersionAttach.isEmpty()){
                 for(ContentVersion ContentVersionrecs : [select Id,Attachment__c from ContentVersion where id in : mapContentVersionAttach.keySet()]){
						if(mapContentVersionAttach.containsKey(ContentVersionrecs.Id))
						{	
							ContentVersionrecs.Attachment__c=mapContentVersionAttach.get(ContentVersionrecs.id).id;
                            attachmentIDs.add(mapContentVersionAttach.get(ContentVersionrecs.id).id);
                            contentVersionUpdate.add(ContentVersionrecs);
							
						}
					}
            }
            
            if(!contentVersionUpdate.isEmpty()){
                Database.update(contentVersionUpdate);
            }
            
            if(!attachmentIDs.isEmpty()){
                for(Attachment__c attObj : [Select Id, Related_To_Case__c from Attachment__c where Id =:attachmentIDs]){
                    String CaseId=attObj.Related_To_Case__c;
                    createAttachmentMstone(attachmentIDs,CaseId);
                }                
            }
            
 
			if(contentVersionUpdate != null) // Process only when there is one record in the list as a future call is initiated form this method which will call a webservice
			{
				id Contentversionid = contentVersionUpdate[0].id;	
                String attachmentStr=contentVersionUpdate[0].Attachment__c;			
                if(string.isNotBlank(attachmentStr)){
				String sUser = [Select Network_User_Id__c from User where id = : UserInfo.getUserId()].Network_User_Id__c;
				for(ContentVersion cv : [select Id,Attachment__c,ContentSize,Title,VersionData,FileExtension from ContentVersion where id =: Contentversionid])
				{					
					if(cv.Attachment__c  != null)
					{
                         if(cv.ContentSize<=CONSTANTS_CASE_HUM.EHUB_Limit){
						sendRequestToEhub(cv.Id,cv.Attachment__c  ,EncodingUtil.base64Encode(cv.VersionData),cv.Title+'.'+cv.FileExtension ,sUser );
                         }
						
					}
                }
				}
			}            
        } 
		
    }
    
    
    /*
     * Method Name : sendRequestToEhub
     * Description : Method to invoke the webservice request . This method is a future method as we are invoking a webservice from trigger
     * Return type : Void
     * Paramater   : String, String , String , String , String 
     */
    @TestVisible
    @future(callout = true) 
    private static void sendRequestToEhub(String contentVersionId,String scustomAttachmentId , String VersionData , String sTitle , String sUser)
    {        
        
        try
        {
            HUM_Webservice_Callout__c attachementServiceConfigObj = HUM_Webservice_Callout__c.getValues(CaseDetailConstants_HUM.MRCALLOUT_SERVICE_NAME);
            
            //Ignored calling callout utlity which help to increase heap size also help avoid creating additional objects and bytes creation 
            HttpRequest req = new HttpRequest();
            req.setMethod(attachementServiceConfigObj.Request_Type__c);  
            req.setHeader(Label.HUMServiceTesterController_content_type, attachementServiceConfigObj.Content_Type__c);            
            req.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
            
            //Creating DTO is adding additional heapsize, hence ignored the DTO creation for this add document request.
            req.setBody('{"AddDocument":{"AddDocumentRequest":{"SysCode":"'+CaseDetailConstants_HUM.MRSYSCODE+'","FileName":"'+sTitle+'","DocId":"'+CaseDetailConstants_HUM.MRDOCID+'","File":"'+VersionData +'"}}}');
            req.setTimeout(Integer.valueOf(attachementServiceConfigObj.Timeout__c));           
            req.setClientCertificateName(attachementServiceConfigObj.Certificate_Name__c); 
            req.setEndpoint(attachementServiceConfigObj.End_Point_URL__c);  
            
            Http http = new Http();           
            HttpResponse response  = http.send(req);
                        
            EhubAttachmentResponse_DTO_HUM documentDTO= (EhubAttachmentResponse_DTO_HUM)JSON.deserialize(response.getbody(),EhubAttachmentResponse_DTO_HUM.class);  
                String sDocID = documentDTO.AddDocument.AddDocumentResponse.AddDocumentResultDTO.DocId;
                String sVersionId = documentDTO.AddDocument.AddDocumentResponse.AddDocumentResultDTO.Version;
                                
                List<Attachment__c > lstatt = new list<Attachment__c>();
                for(Attachment__c attObj : [Select Id, Version_Id__c,DocId__c,LastModifiedById from Attachment__c where Id =  :scustomAttachmentId])
                {
                    attObj.Version_Id__c = sVersionId;
                    attObj.DocId__c = sDocID;  
                    lstatt.add(attObj);  
                    
                }
				     if(lstatt!=null) {               
                    Database.SaveResult[] Result=Database.update(lstatt,false);
                     }
                    // deletion should be from content document where content version id's are available.
            if(contentVersionId!=null && ContentDocument.sobjecttype.getdescribe().isdeletable())
            {
                Set<ID>	ContentDocids = new Set<ID>(); 
                 for(ContentVersion ContentVerRec : [select Id,ContentDocumentId,Attachment__c,Attachment__r.DocId__c,Attachment__r.Version_Id__c,Attachment__r.Attachment_URL_Text__c from ContentVersion where id =: contentVersionId])
                 {
					 
                      if(ContentVerRec.Attachment__r.DocId__c!=null && ContentVerRec.Attachment__r.Version_Id__c!=null)
					  { 
                        if(HUMUtilityHelper.isCRMFunctionalityON('4943505')){
                            ContentDocids.add(ContentVerRec.ContentDocumentId);
                        }else{
                            Database.delete([Select Id from ContentDocument where Id= :ContentVerRec.ContentDocumentId],false);
                        }
                      } 
                 }
                 if(HUMUtilityHelper.isCRMFunctionalityON('4943505')){
                    Database.delete([Select Id from ContentDocument where Id IN:ContentDocids],false);
                }
            }
            
        
		} catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'HUMContentVersionTriggerHelper','sendRequestToEhub');             
        }      
     }
   
    /*
    * This is the method which loads data for attachments added for Incomplete DE SNP cases.
    * @param     List of Attachment IDs and Case ID.
    * @return    Boolean.
    */
	    
    private static boolean createAttachmentMstone(List<String> attachmentIDs, string sCaseID)
    {    	
    	try
    	{
            List<Case> parentCase = [Select id, subtype__c, recordtype.name from Case where ID =:sCaseID];
	    	
	    	if(parentCase!=null && !parentCase.isEmpty()&& parentCase[0].subtype__c == CaseDetailConstants_HUM.CASE_SUBTYPE_INCOMPLETE_DE_SNP && parentCase[0].recordtype.name == CaseDetailConstants_HUM.MEDICARE_CASE)
	    	{	    		    	
		    	if(attachmentIDs!=null && !attachmentIDs.isEmpty())
		    	{
		    		CaseAttachmentTrackService_C_HUM.invokeTrackServiceForAttachments(attachmentIDs);
		    	}
	    	}
    		return true;
    	}
    	catch(exception ex)
    	{
    		return false;
    	}
    }


}