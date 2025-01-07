/*******************************************************************************************************************************
Apex Class Name : HUMAttachmentRelatedListHelper
Version         : 1.0
Created On      : 05/21/2014
Function        : Show Custom Attachment records related to Task on the Detail page of the Task. 
Test Class      : HUMAttachmentRelatedListHelperTest
Modification Log: 
 * Developer Name            Code Review                   Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar          17248                       05/21/2014                 Original Version
 * Surya Kumari M           17248                       07/28/2014                 Added to logic to know the logged user has work queue or not
 * Tanushree Hazari         21906                       03/20/2015                 Created method disableAttachments() as part of REQ-82518
 * ChiranjeeviRao Ravuri    30921                       4/07/2016                  REQ - 262439:Replace Attachment_URL__c field with Attachment_URL_Text__c field. 
 * Mithra Bharadwaj         345536                      03/22/2019                 REQ-  383827 Added code to download the attachments from task
 ****************************************************************************************************************************/

public with sharing class HUMAttachmentRelatedListHelper 
{   
    private Task oTask = new Task();
    private String sTaskStatus = '';
         
    /*
     * Method Name : HUMAttachmentRelatedListHelper
     * Description : Constructor for the class
     * Return Type : void
     * Parameters  : NA
     */
    public HUMAttachmentRelatedListHelper() 
    {
     try 
     {
          
         sCurrentQueue = '';
         sCurrentQueue = [select current_Queue__c from User Where Id =: userinfo.getUserId()].current_Queue__c;                                                     
         bHideAttachments = false;
         disableAttachments();
         getUpdatedAttachments();
        
     }
     catch(Exception ex) 
     {       
            /*Possible Exception - Query Exception 
            Call the Exception Logger Class to show generic message in case of Query Exception as user cannot take any corrective action */
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex,'HUMAttachmentRelatedListHelper','HUMAttachmentRelatedListHelper');
     }           
    }

    //Store the Parent ID of the Custom Attachments in current Context
    public String sParentID {get;set;}
    //Store the Parent Type of the Custom Attachments in current Context - Example Case,Task etc
    public String sParentType {get;set;}   
    //Standard controller to store the Attachment Records
   public ApexPages.standardSetController lstAttachmentStdController {get;set;}
    public String sCurrentQueue {get; set;}
    public boolean bHideAttachments {get; set;}

    /*
     * Method Name : lstAttachments
     * Description : Extract the Custom Attachment records from the Standard Controller  
     * Return Type : List<Attachment__c> 
     * Parameters  : NA
     */    
    public List<Attachment__c> lstAttachments 
    {
        get 
        {
            if(lstAttachmentStdController!=null) {return (List<Attachment__c>)(lstAttachmentStdController.getRecords());}
            return new List<Attachment__c>();
        }
    } 

    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
            /*
     * Method Name   :    getUpdatedAttachments
     * Description   :    Query for the Attachment records depending on the Parent ID
     * Return Type   :    void
     * Parameters    :    NA
     */
     
     private void getUpdatedAttachments() 
     {
        if(String.isBLANK(sParentID))
        {
                if( ApexPages.currentPage().getParameters().containsKey('id')) {sParentID=ApexPages.currentPage().getParameters().get('id');}
        }
        if(!String.ISBLANK(sParentID))
        {
                lstAttachmentStdController= new ApexPages.StandardSetController([Select 
                                                                                 id ,Name, LastModifiedDate, CreatedBy.Name ,CreatedDate,Attachment_URL_Text__c ,
                                                                                 Related_To__c, Created_By_Queue__c, Related_To_Case__c  ,Related_To_Task__c 
                                                                                 from Attachment__c 
                                                                                 where Related_To_Task__c=:(ID)sParentID 
                                                                                 order by LastmodifiedDate]) ;
                                                                                 
            }
     }

    /*
     * Method Name   :    queryAttachments
     * Description   :    Query for the Attachment records to refetch updated IDs
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void queryAttachments()  
    {
            try 
            {    
                getUpdatedAttachments();
            }
            catch(Exception e)
            {
                listErrorLogs = HUMExceptionHelper.logErrors(e,'HUMAttachmentRelatedHelper','queryAttachments');
            }
    } 
     /*
     * Method Name   :    getTaskAttachmentURL
     * Description   :    Create link to download files from Task
     * Return Type   :    void
     * Parameters    :    NA
     */
    @RemoteAction
    public static string getTaskAttachmentURL(String sAttachmentId)
    {
        string sURL = CaseDetailConstants_HUM.sEmpty;
        list<Attachment__c> lstAttachments = [SELECT Id, Attachment_URL_Text__c,DocId__c,Version_Id__c,(SELECT Id FROM Attachments LIMIT 1) FROM Attachment__c WHERE Id = :sAttachmentId];
        if (lstAttachments != null && !lstAttachments.isEmpty())
        {
            Attachment__c att = lstAttachments.get(0);
            if (string.isNotBlank(att.Attachment_URL_Text__c))
            {
                sURL = att.Attachment_URL_Text__c;
            }
              else if(string.isBlank(att.Attachment_URL_Text__c) && string.isNotBlank(att.DocId__c))
            {
                String sfdcBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
                sURL = sfdcBaseURL + GLOBAL_CONSTANT_HUM.EHUB_URL_DOCID+lstAttachments[0].DocId__c +GLOBAL_CONSTANT_HUM.EHUB_URL_VID+lstAttachments[0].Version_Id__c;
            }
            else
            {
                sURL = CaseDetailConstants_HUM.sAttURL + (!att.Attachments.isEmpty() ? att.Attachments.get(0).Id : CaseDetailConstants_HUM.sEmpty);
            }
        }
        return sURL;
    }

    
    /*
     * Method Name   :    disableAttachments
     * Description   :    This method is called to disable attachments whenever task status is closed.
     * Return Type   :    void
     * Parameters    :    NA
     */
     public void disableAttachments()
     {   
         oTask.ID = ApexPages.currentPage().getParameters().get('Id');
         if(!String.isBlank(oTask.ID))
         {
            sTaskStatus = [Select Status from Task where ID =: oTask.ID].Status;
            if(sTaskStatus.equals(HUMConstants__c.getInstance('HUMClosed').StringValue__c))    bHideAttachments = true;
            else    bHideAttachments = false;
         }
     }  
}