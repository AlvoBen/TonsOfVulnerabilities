/*****************************************************************************************************************************************************************************
Apex Class Name : HUMAttachmentDetailOverrideExtension
Version         : 1.0
Created On      : 03/24/2015
Function        : It is the main controller class for overriding Notes and Attachment section in Attachment detail page.
Test Class      : HUMAttachmentDetailOverrideExtensionTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
********************************************************************************************************************************************************************************
* Kritika P Popat           21906                    03/24/2015                  Original version
********************************************************************************************************************************************************************************/
public with sharing class HUMAttachmentDetailOverrideExtension
{
    /*
    * Method Name   :    HUMAttachmentDetailOverrideExtension
    * Description   :    Constructor for this class
    * Return Type   :    NA
    * Parameters    :    Object of Attachment__c custom object
    */
    public HUMAttachmentDetailOverrideExtension(ApexPages.StandardController attachmentStnd)
    {
        try 
        {   
            oAttachment = new Attachment__c();
            lstAttachmentData = new List<HUMAttachDtlOverrideExtensionHelper>();
            oTask = new Task();
            
            if(attachmentStnd != null) 
            {    
                if(!Test.isRunningTest())    attachmentStnd.addFields(new List<String>{'Related_To_Task__c','Name','Related_To__c','Related_To_Case__c'});
                this.oAttachment = (Attachment__c)attachmentStnd.getRecord();
                sTabTitle = oAttachment.Name;
            }
            
            if(!String.isEmpty(oAttachment.Related_To_Task__c))    oTask = [Select Task_Number__c,Status,Id from Task where ID=:oAttachment.related_To_Task__c];
            
            for(Note objNote : [SELECT Id, Parent.Id, Parent.Type, Title, Owner.Name, OwnerId, LastModifiedDate FROM Note WHERE ParentID =: oAttachment.Id])
            {
               lstAttachmentData.add(new HUMAttachDtlOverrideExtensionHelper(objNote, NULL));
            }
            for(Attachment objAttachment : [SELECT Id, ParentId, Name, Owner.Name, OwnerId, LastModifiedDate FROM Attachment WHERE  ParentID =: oAttachment.Id])
            {
               lstAttachmentData.add(new HUMAttachDtlOverrideExtensionHelper(NULL, objAttachment));
            }
            sortList();
        }
        catch (Exception e)
        {            
            HUMExceptionHelper.logErrors(e, 'HUMAttachmentDetailOverrideExtension', 'HUMAttachmentDetailOverrideExtension'); 
        }
    }
    
    
    public Attachment__c oAttachment {get;set;}
    public Task oTask {get;set;}
    public String sTabTitle {get;set;}
    public List<HUMAttachDtlOverrideExtensionHelper> lstAttachmentData {get; set;}
    
    /*
    * Method Name   :    onPageLoad
    * Description   :    Called from VF page whenver page is loaded
    * Return Type   :    PageReference 
    * Parameters    :    NA
    */
    public PageReference onPageLoad()
    {
        if((oAttachment.Related_To__c == (HUMConstants__c.getInstance('HUMCase').StringValue__c)) || 
              ((oAttachment.Related_To__c == (HUMConstants__c.getInstance('HUMTask').StringValue__c))
               && oTask != NULL && (oTask.Status != (HUMConstants__c.getInstance('HUMClosed').StringValue__c))))
        {
            return new PageReference(HUMConstants__c.getInstance('HUMSlash').StringValue__c + oAttachment.Id + HUMConstants__c.getInstance('HUMURL').StringValue__c);
        }
        return NULL;
    }
    
    /*
    * Method Name   :    sortList
    * Description   :    Used for displaying resulted in sorted order
    * Return Type   :    Void 
    * Parameters    :    NA
    */
    public void sortList()
    {
        lstAttachmentData.sort();
    }
}