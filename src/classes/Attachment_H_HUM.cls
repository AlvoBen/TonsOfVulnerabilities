/*******************************************************************************************************************************
Apex Class Name : Attachment_H_HUM
Version         : 1.0
Created On      : 11/18/2016 
Function        : Helper class for Attachment_G_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya                                   11/18/2016                   Original Version 
********************************************************************************************************************************/

public class Attachment_H_HUM {
    
    /**
    * <p>
    * Update Created By Queue field on Attachment
    *
    * @param     lstAttachments     List of Attachment records
    * @return    Void
    */
    public static void updateCreatedByQueue(List<Attachment__c> lstAttachments) 
    {
        try
        {
            // To store case Ids
            SET<Id> setCase = new Set<Id>();
            Map<String,Id> mapWQNametoId = new Map<String,Id>();
            User currentUser = [SELECT Id,current_Queue__c FROM User Where id =:UserInfo.getUserId() LIMIT 1];
            for(Attachment__c attachmentInstance : lstAttachments )
            {
                setCase.add(attachmentInstance.Related_To_Case__c);      
            }
            // Map to store case Id and case details
            Map<Id,Case> mapCase = new map<Id,Case>([SELECT Case_Owner__c,Owner__c,Owner_Queue__c,OwnerId FROM Case Where id In :setCase]);
            // Map to hold Work queue name and work queue id
            for(Work_Queue_Setup__c wqsInstance : [SELECT Id,Name FROM Work_Queue_Setup__c Where IsActive__c = TRUE])
            {
                mapWQNametoId.put(wqsInstance.Name,wqsInstance.Id);
            }
            // Iterating over the attachments and populating created by Queue
            for(Attachment__c attachmentInstance : lstAttachments )
            {
                String sCaseOwner = mapCase.get(attachmentInstance.Related_To_Case__c).Case_Owner__c;
                String sOwnerFIeld =  mapCase.get(attachmentInstance.Related_To_Case__c).Owner__c;
                if((sOwnerFIeld != NULL && !sOwnerFIeld.trim().equalsIgnoreCase(userInfo.getFirstName() + ' ' + userInfo.getLastName())))
                {
                    if(mapWQNametoId.containsKey(currentUser.current_queue__c))
                    {
                        attachmentInstance.Created_By_Queue__c = currentUser.current_queue__c;
                    }
                }
                if(sCaseOwner != NULL && sCaseOwner.trim().equalsIgnoreCase(userInfo.getFirstName() + ' ' + userInfo.getLastName()))
                {
                    if(mapCase.containsKey(attachmentInstance.Related_To_Case__c) && mapWQNametoId.containsKey(mapCase.get(attachmentInstance.Related_To_Case__c).Owner_Queue__c) 
                      && mapCase.containsKey(attachmentInstance.Related_To_Case__c))
                    {
                        attachmentInstance.Created_By_Queue__c = mapCase.get(attachmentInstance.Related_To_Case__c).Owner_Queue__c; 
                    } 
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Attachment_H_HUM','updateCreatedByQueue');
        }
            
    }
    
}