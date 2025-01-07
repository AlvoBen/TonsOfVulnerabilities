/*******************************************************************************************************************************
Apex Class Name : HUMAttachmentNewButtonOverrideExtension
Version         : 1.0
Created On      : 06/13/2014
Function        : This class is used to redirect to Queue Selection page, if user doesn't select any workqueue and tries
                  to create attachments for Case /Tasks
Test Class      : HUMAttachNewButtonOverrideExtensionTest
          
Modification Log: 
* Developer Name                       Code Review                       Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
    SuryaKumari Medicherla              17093                          06/13/2014                 Original Version
    SuryaKumari Medicherla              17093                          06/17/2014                 Case Edit Functionality Added
    SuryaKumari Medicherla              17717                          09/04/2014                 Replaced Custom labels with custom settings to fix Defect :153752  
    SuryaKumari Medicherla              17717                          09/08/2014                 Refactored code as per cc review comments
    SuryaKumari Medicherla                                             09/19/2014                 Removed SQL for custom settings with Map.
    Pradeepkuamr Dani                                                                             Replaced labels with constants & added code w.r.t CAse Attachment funtionality
    Mithra Bharadwaj                                                                              US#2089563 Sonar Qube Changes
****************************************************************************************************************************/


public with sharing class HUMAttachmentNewButtonOverrideExtension
{
    private String sNextPage = '';
    private String sCaseSalesForceId = '';
    private String sTaskSalesforceId = '';
    private Map<String, HUMSalesforceFieldIds__c> mapSalesforceFieldIds = HUMSalesforceFieldIds__c.getAll();
    Public Static final String CASEMGMT_OWNERID_HUM =  'ownerId';
    Public Static final String CASEMGMT_TASKID_HUM =  'taskId';
    Public Static final String CASEMGMT_CASEATTACHMENT_ATTCURRENTSUBTABIDPARAM =  'attCurrentSubTabIdParam';
    Public Static final String CASEMGMT_CASEID_HUM =  'caseId';
    Public Static final String CASEMGMT_CASEATTACHMENT_HUMSMALLID = 'id';
    Public Static final String CASEMGMT_CASEATTACHMENT_HUMATTACHMENTEDITPAGE = 'HUMAttachmentEdit';
    Public Static final String CASEMGMT_CASEATTACHMENT_HUMQUEUEYESFORATTACHMENT = 'YesForAttachement';
    Public Static final String CASEMGMT_CASEATTACHMENT_HUMOBJECTNUMBER = 'objectNumber';
    Public Static final String CASEMGMT_CASEATTACHMENT_HUMOBJECTID = 'objectId';
    Public Static final String CF_VALUE ='CF';
    Public Static final String LKID_VALUE = '_lkid';
    Public Static final String CASEMGMT_TASK_NUMBER_HUM = 'Task_Number';
    Public Static final String CASEID_CONSTANT_HUM = 'CaseId';
    Public Static final String CASEMGMT_APEXIN_HUM = '/apex/';
    Public Static final String TASKNEWBUTTON_QUEUE_SELECTION_PAGE_HUM = 'HUMQueueSelection';
    Public Static final String CASECLONE_NEXT_PAGE_HUM = 'nextPage';
    Public Static final String CASECLONE_OBJECT_TYPE_HUM = 'objectType';
    Public Static final String CASE_CONSTANT_HUM = 'Case';
    Public Static final String CASEMGMT_ERROR = 'Error';
    Public Static final String ID_HUM = 'id';
    Public Static final String OBJECTNAME_TASK_HUM = 'Task';
    
    
    /*
     * Method Name : HUMAttachmentNewButtonOverrideExtension
     * Description : Constructor for the class, initialise variables , read page parameter values
     * Return type : N/A
     * Paramater   : StandardController 
     */
    public HUMAttachmentNewButtonOverrideExtension(ApexPages.StandardController controller) 
    {
    }
    
    public String sCaseId { get; set; }
    public String sTaskId { get; set; }
    public String sCaseTabId ;
    
    /*
     * Method Name : reDirectPage
     * Description : This method is used to call methods to set page parameters for nextPage based on user workqueue value and redirects to
                     next page when user clicks on New Attachment on Cases/Tasks Related List.
     * Return type : PageReference 
     * Paramater   : N/A
     */
    public PageReference reDirectPage() 
    { 
        try 
        {
            String sCurrentUserQueue = [select Id, Current_Queue__c from User where id =: Userinfo.getUserId()].Current_Queue__c;
            //To Fix Defect 153752, replaced Custom labels with Custom settings
            sCaseSalesForceId = mapSalesforceFieldIds.get(CASEMGMT_CASEATTACHMENT_HUMSMALLID).RelatedTo_Case_in_Attachment__c; 
            sCaseId = ApexPages.Currentpage().getParameters().get(CASEID_CONSTANT_HUM);
            sTaskId = ApexPages.Currentpage().getParameters().get(CASEMGMT_TASKID_HUM);
            sCaseTabId = ApexPages.Currentpage().getParameters().get(CASEMGMT_CASEATTACHMENT_ATTCURRENTSUBTABIDPARAM);
            
            if(!String.isEmpty(sCaseId))    return assignValuesForCase(sCurrentUserQueue);   
            if(!String.isEmpty(sTaskId))    return assignValuesForTask(sCurrentUserQueue);
            
            return null;
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMAttachmentNewButtonOverrideExtension', 'reDirectPage');
            return null;
        }
    }
    
    /*
     * Method Name : assignValuesForCase
     * Description : This method is used to set page parameters for nextPage based on user workqueue value and redirects to
                     next page when user clicks on New Attachment on Cases Related List.
     * Return type : PageReference 
     * Paramater   : String 
     */
    public PageReference assignValuesForCase(String sCurrentUserQueue)
    {
        try
        {
            if(String.isEmpty(sCurrentUserQueue))
            {
                sCaseId = String.escapeSingleQuotes(sCaseId);
                sCaseTabId = String.escapeSingleQuotes(sCaseTabId);
                sNextPage = CASEMGMT_APEXIN_HUM + CASEMGMT_CASEATTACHMENT_HUMATTACHMENTEDITPAGE + '?' + CASEID_CONSTANT_HUM + '=' +string.escapeSingleQuotes(sCaseId);
                String sCaseName = [select CaseNumber from Case where Id= :sCaseId].CaseNumber;
                PageReference opageRef = new PageReference(CASEMGMT_APEXIN_HUM + TASKNEWBUTTON_QUEUE_SELECTION_PAGE_HUM);                
                opageRef.getParameters().put(CASECLONE_NEXT_PAGE_HUM, sNextPage);
                opageRef.getParameters().put(CASECLONE_OBJECT_TYPE_HUM, CASE_CONSTANT_HUM);             
                opageRef.getParameters().put(CASEMGMT_ERROR, CASEMGMT_CASEATTACHMENT_HUMQUEUEYESFORATTACHMENT);
                opageRef.getParameters().put(CASEMGMT_CASEATTACHMENT_HUMOBJECTNUMBER, sCaseName );  
                opageRef.getParameters().put(CASEMGMT_CASEATTACHMENT_HUMOBJECTID, string.escapeSingleQuotes(sCaseId) );  
                
                //Adding Case tab id received from HUMAttachmentNewButtonOverride.page
                opageRef.getParameters().put(CASEMGMT_CASEATTACHMENT_ATTCURRENTSUBTABIDPARAM, string.escapeSingleQuotes(sCaseTabId));
                                                                                                                                                                                                                                                                                            
                return opageRef;
            }
            else
            {
                PageReference opageRef = new PageReference(CASEMGMT_APEXIN_HUM + CASEMGMT_CASEATTACHMENT_HUMATTACHMENTEDITPAGE + '?' + CASEID_CONSTANT_HUM + '=' + string.escapeSingleQuotes(sCaseId));
                
                //Adding Case tab id received from HUMAttachmentNewButtonOverride.page
                opageRef.getParameters().put(CASEMGMT_CASEATTACHMENT_ATTCURRENTSUBTABIDPARAM, string.escapeSingleQuotes(sCaseTabId));
                
                return opageRef;
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMAttachmentNewButtonOverrideExtension', 'assignValuesForCase');
            return null;
        }
    }
    
    /*
     * Method Name : assignValuesForTask
     * Description : This method is used to set page parameters for nextPage based on user workqueue value and redirects to
                     next page when user clicks on New Attachment on Tasks Related List.
     * Return type : PageReference 
     * Paramater   : String
     */
    public PageReference assignValuesForTask(String sCurrentUserQueue)
    {
        try
        {
            String sTaskName;
            if(String.isEmpty(sCurrentUserQueue))
            {
                sNextPage = CASEMGMT_APEXIN_HUM + CASEMGMT_CASEATTACHMENT_HUMATTACHMENTEDITPAGE+'?' + 'taskID' + '=' + string.escapeSingleQuotes(sTaskId);
            
                sTaskName = [select Task_Number__c from Task where Id =: sTaskId].Task_Number__c;   
                sTaskSalesforceId = mapSalesforceFieldIds.get(ID_HUM).RelatedTo_Task_in_Attachment__c;                     
                PageReference opageRef = new PageReference(CASEMGMT_APEXIN_HUM + TASKNEWBUTTON_QUEUE_SELECTION_PAGE_HUM);                
                opageRef.getParameters().put(CASECLONE_NEXT_PAGE_HUM, sNextPage);
                opageRef.getParameters().put(CASECLONE_OBJECT_TYPE_HUM, OBJECTNAME_TASK_HUM);             
                opageRef.getParameters().put(CASEMGMT_ERROR, CASEMGMT_CASEATTACHMENT_HUMQUEUEYESFORATTACHMENT);
                opageRef.getParameters().put(CASEMGMT_TASK_NUMBER_HUM,sTaskName);  
                opageRef.getParameters().put(CASEMGMT_CASEATTACHMENT_HUMOBJECTID, string.escapeSingleQuotes(sTaskId));  
                opageRef.getParameters().put(CF_VALUE + sTaskSalesforceId + LKID_VALUE, sTaskName);  
                return opageRef;
            }
            else
            {
                PageReference opageRef = new PageReference(CASEMGMT_APEXIN_HUM + CASEMGMT_CASEATTACHMENT_HUMATTACHMENTEDITPAGE+ '?' + CASEMGMT_TASKID_HUM + '=' +string.escapesinglequotes(sTaskId) );
                opageRef.getParameters().put(CF_VALUE + sTaskSalesforceId  + LKID_VALUE, sTaskName); 
                return opageRef;
            }
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMAttachmentNewButtonOverrideExtension', 'assignValuesForTask');
            return null;
        }
    }
}