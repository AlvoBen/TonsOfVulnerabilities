/************************************************************************************************
Apex Class Name  : HUMAttachmentTriggerHelper
Version          : 1.0
Created Date     : May 24 2014
Function         : Holds the logic for trigger events on Standard Attachment Object
Test Class       : HUMAttachmentTriggerHelperTest
 **************************************************************************************************
Modification Log:
 * Developer Name              Code Review                  Date                       Description
 *------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar V          17248                        05/24/2014                   Original Version
 * Vishal Verma               21654                        03/09/2015                   REQ - 158399 :Added onAfterInsert method to initiate the webservice to Ehub 
                                                                                        and update the URL on Attachment__c
 * Vishal Verma               23084                        05/12/2015                   Added DPass Decryption for body in sendRequestToEhub method (line 174-182) before sending it to EHub.
 * Vishal Verma               27102                        10/13/2015                   Defect : 202441.0002  Added timeout for ehub call. 
 * ChiranjeeviRao Ravuri      30921                        4/07/2016                    REQ - 262439:Replace Attachment_URL__c field with Attachment_URL_Text__c field.   
                                                                                        Removed field 'Attachment_URL_Text__c' can not be filtered in query call and added additional if condition at line 109,to explicitly check for null values.                                                
 * Prathyu Tushar                                          04/06/2018                   As per Checkmarx REQ - 359262, checking user permissions before deleting the records in sendRequestToEhub() method.
 * Srivinas Damera                                         10/27/2018                   REQ - 377478 - eHub DSS 2.0 - Deployment - for AddDcoumentservice
 * Santhi Mandava                                          12/26/2018                   Removed dpaas call 
 * Keerti Durbha                                           01/15/2020                   782508 PR00086652 (Multi Browser Support) - SF - p9 Medium Defects - CRUD scan fixes
 ***************************************************************************************************/

public with sharing class HUMAttachmentTriggerHelper 
{
    /*
     * Method Name   :    onBeforeInsert
     * Description   :    Execute all on before Insert Trigger Events
     * Return Type   :    void
     * Parameters    :    List<Attachment>
     */
    public static void onBeforeInsert(List<Attachment> lstNewAttachments) 
    {
        validateDuplicateAttachments(lstNewAttachments);
    }

    /*
     * Method Name :  onBeforeUpdate
     * Description :  Execute all on before Update Trigger Events
     * Return Type :  void
     * Parameters  :  List<Attachment> , List<Attachment> , Map<Id,Attachment> ,MAp<ID,Attachment> 
     */
    public static void onBeforeUpdate(List<Attachment> lstNewAttachments, List<Attachment> lstOldAttachments , Map<Id,Attachment> mapNewAttachment,MAp<ID,Attachment> mapOldAttachment) 
    {
        restrictAttachmentUpdate(lstNewAttachments);
    }

    /*
     * Method Name :  onBeforeDelete
     * Description :  Execute all on before Delete Trigger Events
     * Return Type :  void
     * Parameters  :  List<Attachment>
     */
    public static Void onBeforeDelete(List<Attachment> lstOldAttachments) 
    {
        restirctAttachmentDeletion(lstOldAttachments);
    }

    /*
     * Method Name :  Validate Duplicate attachments on Custom Attachment object
     * Return Type :  void
     * Parameters  :  List<Attachment>
     */
    public static void validateDuplicateAttachments(List<Attachment> lstNewAttachments) 
    {        
        //Set to Store Parent IDs
        Set<ID> setParentIDs = new set<ID>();
        
        //Set to track any Duplicate attachments in the new list
        set<ID> setDupParentIds = new Set<ID>();
        
        for(Attachment attach:lstNewAttachments) 
        {          
            if(!setParentIDs.add(attach.parentID)) 
            {                
                setDupParentIds.add(attach.parentID);
            }         
        }
        
        //Fire a query to identify existing attachments for a parent ID
        Map<Id,Attachment__c> mapCustomAttachments = new Map<Id,Attachment__c>([Select Attachment_URL_Text__c,(select id from Attachments) from Attachment__c where Id in :setParentIDs]);
                    
        for(Attachment attachNew:lstNewAttachments) 
        {                     
                if((mapCustomAttachments.containsKey(attachNew.parentID) && (mapCustomAttachments.get(attachNew.parentID).Attachments.size()>0 || mapCustomAttachments.get(attachNew.parentID).Attachment_URL_Text__c!=null)) ||
                    (mapCustomAttachments.containsKey(attachNew.parentID) && setDupParentIds.contains(attachNew.parentID) && mapCustomAttachments.get(attachNew.parentID).Attachments.size()==0)) 
                    {
                       attachNew.addError(Label.HUMRestrictDuplicateAttachment);
                    }
        }
    }

    /*
     * Method Name : restirctAttachmentDeletion
     * Description : Restrict Attachment deletion - Standard delete of Attachment cannot be restricted through Profile settings
     * Return Type : void
     * Parameters  : List<Attachment>
     */
    public static void restirctAttachmentDeletion(List<Attachment> lstOldAttachments) 
    {        
        //Set to Store Parent IDs
        Set<ID> setParentIDs = new set<ID>();
                
        for(Attachment attach:lstOldAttachments)
        {
            setParentIDs.add(attach.parentID);      
        }
        
        Map<Id,Attachment__c> mapCustomAttachments = new Map<Id,Attachment__c>([Select Version_Id__c,DocId__c,Attachment_URL_Text__c from Attachment__c where Id in :setParentIDs]);
        
        for(Attachment attach:lstOldAttachments)
        {
           if(mapCustomAttachments.containsKey(attach.parentID) && mapCustomAttachments.get(attach.parentID).DocId__c == null && mapCustomAttachments.get(attach.parentID).Version_Id__c == null && mapCustomAttachments.get(attach.parentID).Attachment_URL_Text__c == null) {attach.addError(Label.HUMRestrictAttachmentDelete);}
        }       
    }

    /*
     * Method Name : restrictAttachmentUpdate
     * Description : Restrict Attachment Edit
     * Return Type : void
     * Parameters  : List<Attachment>
     */
    public static void restrictAttachmentUpdate(List<Attachment> lstNewAttachments) 
    { 
        //Set to Store Parent IDs
        Set<ID> setParentIDs = new set<ID>();
                
        for(Attachment attach:lstNewAttachments)
        {
            setParentIDs.add(attach.parentID);      
        }
        
        Map<Id,Attachment__c> mapCustomAttachments = new Map<Id,Attachment__c>([Select Attachment_URL_Text__c from Attachment__c where Id in :setParentIDs ]);
        
        for(Attachment attach:lstNewAttachments)
        {
            if(mapCustomAttachments.containsKey(attach.parentID)) {attach.addError(Label.HUMRestrictAttachmentUpdate);}
        }
    }
    
    /*
     * Method Name : onAfterInsert
     * Description : Method to initiate the webservice request when Attachment record is inserted
     * Return type : void
     * Paramater   : List<Attachment> 
     */
    public static void onAfterInsert(List<Attachment> lstNewAttachments)
    {
        if(lstNewAttachments != null && lstNewAttachments.size() == Integer.valueof(HUMConstants__c.getValues('HUMLISTSIZEONE').IntegerValue__c)) // Process only when there is one record in the list as a future call is initiated form this method which will call a webservice
        {
            String sUser = [Select Network_User_Id__c from User where id = : UserInfo.getUserId()].Network_User_Id__c;
            for(Attachment att : lstNewAttachments)
            {
                if(att.parentID != null && String.valueof(att.parentID).startswith(Schema.SObjectType.Attachment__c.keyprefix)) // Check if the attachment is created for Attachment__c object
                {
                    sendRequestToEhub(att.Id,att.parentId ,EncodingUtil.base64Encode(att.body), att.Name ,sUser );
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
    private static void sendRequestToEhub(String attachmentStdId,String sParentId , String sBody , String sName , String sUser)
    {        
        try
        {
            HUM_Webservice_Callout__c attachementServiceConfigObj = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.MRCALLOUT_SERVICE_NAME);
            
            //Ignored calling callout utlity which help to increase heap size also help avoid creating additional objects and bytes creation 
            HttpRequest req = new HttpRequest();
            req.setMethod(attachementServiceConfigObj.Request_Type__c);  
            req.setHeader(Label.HUMServiceTesterController_content_type, attachementServiceConfigObj.Content_Type__c);            
            req.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
            
            //Creating DTO is adding additional heapsize, hence ignored the DTO creation for this add document request.
            req.setBody('{"AddDocument":{"AddDocumentRequest":{"SysCode":"'+GLOBAL_CONSTANT_HUM.MRSYSCODE+'","FileName":"'+sName+'","DocId":"'+GLOBAL_CONSTANT_HUM.MRDOCID+'","File":"'+sBody +'"}}}');
            req.setTimeout(Integer.valueOf(attachementServiceConfigObj.Timeout__c));           
            req.setClientCertificateName(attachementServiceConfigObj.Certificate_Name__c); 
            req.setEndpoint(attachementServiceConfigObj.End_Point_URL__c);  
            Http http = new Http();
            HttpResponse response  = http.send(req);
           
            EhubAttachmentResponse_DTO_HUM documentDTO= (EhubAttachmentResponse_DTO_HUM)JSON.deserialize(response.getbody(),EhubAttachmentResponse_DTO_HUM.class);  
                String DocID = documentDTO.AddDocument.AddDocumentResponse.AddDocumentResultDTO.DocId;
                String VersionId = documentDTO.AddDocument.AddDocumentResponse.AddDocumentResultDTO.Version;
                
                List<Attachment__c > lstatt = new list<Attachment__c>();
                for(Attachment__c attObj : [Select Id, Version_Id__c,DocId__c from Attachment__c where Id =  :sParentId])
                {
                    attObj.Version_Id__c = VersionId ;
                    attObj.DocId__c = DocID ;  
                    lstatt.add(attObj);                 
                }
                
                 Database.SaveResult[] Result=Database.update(lstatt,false);
                     
            if(attachmentStdId!=NUll && Attachment.sobjecttype.getdescribe().isdeletable())
            { 
                
                Database.delete([Select Id from Attachment where Id= :attachmentStdId],false);
            }
            
        
       } catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'HUMAttachmentTriggerHelper','sendRequestToEhub');             
        }      
     }
}