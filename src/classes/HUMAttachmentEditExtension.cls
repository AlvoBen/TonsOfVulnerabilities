/****************************************************************************************************************************************************************************************
Apex Class Name  : HUMAttachmentEditExtension
Version          : 1.0
Created Date     : May 24 2014
Function         : Holds the logic to create/Edit Attachment(custom) record 
Test Class       : HUMAttachmentEditExtensionTest
 **********************************************************************************************************************************************************************************
Modification Log:
 * Developer Name            Code Review                    Date                       Description
 *------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar           17248                         05/24/2014                   Original Version
 * Shruthi Karanth           18229                         09/29/2014                   Defect -155404 Included logic to get Task Number and show it on page instead of Task ID
 * Tanushree Hazari          21906                         03/24/2015                   Modified line 53-60 and added method onCancel() at line 90-104 as part of REQ-82518
   Pradeepkuamr Dani																	Added code w.r.t CAse Attachment funtionality
 * Vamshi Krishna Pemberthi								   08/13/2021					User Story 2548458: T1PRJ0002736 / PR00094232 - 2021 MF6 - Case Mgt Checkmarx sf-main/src/classes/HUMAttachmentEditExtension.cls
 *********************************************************************************************************************************************************************************/

public with sharing class HUMAttachmentEditExtension
{
    /*
     * Method Name   :    HUMAttachmentEditExtension
     * Description   :    Constructor for the class 
     * Return Type   :    void
     * Parameters    :    ApexPages.StandardController - This is related to Attachment__c
     */
    public HUMAttachmentEditExtension(ApexPages.StandardController attachmentStr) 
    {
        try 
        {   
            bIsVisible = false;
            //Initialize variables before usage
            attachment = new Attachment__c();
            
            //Add fields to refer later while assigning values to variables
            if(attachmentStr != null) 
            {    
                //Salesforce bug as Test methods fail while using addFields. Present work around is to by pass statement in case of test class
                if(!Test.isRunningTest()) {attachmentStr.addFields (new List<String>{'Related_To_Case__c','Related_To_Task__c','Name','Related_To__c'});}
                attachment = (Attachment__c)attachmentStr.getRecord();
            }
            
            //Check if this is new Attachment 
            if( !(attachment != null && attachment.id != null) ) 
            {                  
                //Populate initial Values . In case of exception , a generic exception message will be displayed
                if(Schema.SObjectType.Attachment__c.fields.Related_To_Case__c.isCreateable() || Schema.SObjectType.Attachment__c.fields.Related_To_Case__c.isUpdateable()) attachment.Related_To_Case__c = ApexPages.CurrentPage().getParameters().containsKey('caseID') ? ApexPages.CurrentPage().getParameters().get('caseID') : null;                
                if(Schema.SObjectType.Attachment__c.fields.Related_To_Task__c.isCreateable() || Schema.SObjectType.Attachment__c.fields.Related_To_Task__c.isUpdateable()) attachment.Related_To_Task__c = ApexPages.CurrentPage().getParameters().containsKey('taskID') ? (ID)ApexPages.CurrentPage().getParameters().get('taskID') : null;
                
                if(attachment.Related_To_Task__c != null )
                {
                    Task oTask = [Select Task_Number__c,Status from Task where ID=:attachment.Related_To_Task__c];
                    if(oTask != null) sTaskNumber = oTask.Task_Number__c;
                }
            }
            if(attachment != null && (!String.isEmpty(attachment.id)))
            {
                if((!String.isEmpty(attachment.Related_To_Task__c)) && attachment.Related_To__c==HUMConstants__c.getInstance('HUMTask').StringValue__c)
                {
                    Task oTask = [Select Status from Task where ID=:attachment.related_To_Task__c];
                    if(oTask.Status.equals(HUMConstants__c.getInstance('HUMClosed').StringValue__c)) bIsVisible=true;
                }
            }
        }
        Catch(Exception ex) 
        {
            /*Possible exception - Wrong Type can be assiged to sTaskID / sCaseID -
            Call Exception logger class to handle the exception to show generic message */
            
            //Dont insert any logs in the constructor . A seperate custom component in the Page will handle this part.
            HUMExceptionHelper.bRestrictLog = true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex,'HUMAttachmentEditExtension','HUMAttachmentEditExtension');
        }
    }
    
    
    //Property to hold Custom Attachment Record 
    public Attachment__c attachment {get;set;}
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs == null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
    //Store the Task Name
    public string sTaskNumber {get;set;}
    public boolean bIsVisible{get;set;}
    
    /*
     * Method Name   :    onCancel
     * Description   :    Redirects to Attachment detail page on clicking on cancel button in VF page.
     * Return Type   :    PageReference
     * Parameters    :    NA
     */
    public PageReference onCancel()
    {
        if(attachment != null && (!String.isEmpty(attachment.id)))
        {
            PageReference pgRef = new ApexPages.StandardController(attachment).view();
            return pgRef;
        }    
    return NULL;
    }
    
    /*
     * Method Name   :    saveAttachment
     * Description   :    Populate all the required values based on the Context and Save the Attachment record -Only for single record
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void saveAttachment() 
    {
        try 
        {    
            //Check if this is new attachment record
            if(!(attachment != null && (!String.isEmpty(attachment.id)))) 
            {                    
                //Check the context of Attachment 
                //Task Attachment
                if(!(String.isBlank(attachment.Related_To_Task__c)) && attachment.Related_To_Case__c == null) 
                {    
                    Id taskRecordTypeID = HUMUtilityHelper.getRecordTypeID(Attachment__c.sObjectType ,'Task');
                
                    //Query for Task and Get the Case ID to assign to attachment
                    Task taskRecord = [Select Id, WhatID from Task where ID=:attachment.Related_To_task__c];
                    attachment.Related_To_Case__c = taskRecord.WhatID;
                    if(taskRecordTypeID != null) 
					{
                        if(Schema.SObjectType.Attachment__c.fields.recordTypeID.isCreateable() || Schema.SObjectType.Attachment__c.fields.recordTypeID.isUpdateable()) attachment.recordTypeID = taskRecordTypeID;
                    }
                }
                //Case Attachment
                else 
                {                            
                    Id caseRecordTypeID = HUMUtilityHelper.getRecordTypeID(Attachment__c.sObjectType ,'Case');
                    if(caseRecordTypeID != null) {attachment.recordtypeID = caseRecordTypeID;}                    
                }               
            }       
                        
            //Upsert the custom attachment record  
            Database.upsertResult upsertResult = Database.upsert(attachment,false);
            //Process the save result for any exceptions
            boolean isError = HUMExceptionHelper.processUpsertResults(new List<Database.upsertResult>{upsertResult},new List<SObject>{attachment}, 'HUMAttachmentService','saveAttachment','Attachment__c');
        }
        catch(Exception e) 
        {    
            /*Possible Exceptions 
            DML Exception - will be handled by DatabaseUpsertResult
            Any other exception - Exception logger will show a generic error message 
             */
            HUMExceptionHelper.logErrors(e,'HUMAttachmentEditExtension','saveAttachment');           
        }  
    }
}