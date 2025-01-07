/*******************************************************************************************************************************
Apex Class Name : HUMTaskNewButtonOverrideExtension 
Version         : 1.0
Created On      : 06/13/2014
Function        : This class is used to redirect to queue selection page, if user doesn't select any work queue and tries
                  to create a task/ edit a task
Test Class      : HUMTaskNewButtonOverrideExtensionTest
                  
Modification Log: 
* Developer Name                Code Review                             Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
    SuryaKumari Medicherla          17093                               06/13/2014                 Original Version
    SuryaKumari Medicherla          17093                               06/17/2014                 Case Edit Functionality Added
    Mrudula Jaddu                   17930                               09/11/2014                 Code added to differentiate the recordtypes Defect:153737 
    SuryaKumari Medicherla                                              09/18/2014                 Romved mutiple SQL queries for custom settings
    Avinash Choubey                 40794     							09/15/2016            	   code added to pass parenttaskID in URL,  REQ 252450
   *************************************************************************************************************************/
   
public with sharing class HUMTaskNewButtonOverrideExtension 
{
    private String sWhoId = '';
    private String sWhatId = '';
    private String sReturnURL = '';
    private String sParentTaskID = '';
    private Map<String, HUMTaskRecordTypes__c> mapTaskRecordTypes = HUMTaskRecordTypes__c.getAll();
    private DescribeSObjectResult oResult =  Schema.getGlobalDescribe().get(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM).getDescribe();
    private MAP<String,Schema.RecordTypeInfo> mapRecordTypeInfo = oResult.getRecordTypeInfosByName(); 
        
    /*
     * Method Name : HUMTaskNewButtonOverrideExtension 
     * Description : Constructor for the class, initialize variables , read page parameter Values
     * Return type : N/A
     * Paramater   : StandardController 
     */
    public HUMTaskNewButtonOverrideExtension(ApexPages.StandardController controller) 
    {
        sWhoId = ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_WHO_ID);
        sWhatId = ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_WHAT_ID);
        sReturnURL = ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_RETURL);
        sParentTaskID = ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_PARENT_TASKID);
    }
       
    /*
     * Method Name : reDirectPage
     * Description : This method is used to redirect to respective pages based on recordtype and to set nextPage based on user workqueue value and redirects to
                     next page when user trying to create a task from case detail Page.
     * Return type : PageReference 
     * Paramater   : N/A
     */
     public PageReference reDirectPage() 
     {
        try
        {
            User oCurrentUser = [select Id, ProfileId, Current_Queue__c from User where id = :Userinfo.getUserId()];
            Id csiProfileId = [SELECT Id,Name FROM Profile WHERE Name =: GLOBAL_CONSTANT_HUM.CASEMGMT_BME_PROFILE_HUM].id;
           
            String sRecordTypeIdCSI  = mapRecordTypeInfo.get(mapTaskRecordTypes.get(GLOBAL_CONSTANT_HUM.CASEMGMT_RECORDTYPE).CSIRecordTypeName__c).getRecordTypeId();
            String sRecordTypeIdCRM = mapRecordTypeInfo.get(mapTaskRecordTypes.get(GLOBAL_CONSTANT_HUM.CASEMGMT_RECORDTYPE).ServiceCRMRecTypeName__c).getRecordTypeId();
            String sRecordTypeIdCSI15Char = sRecordTypeIdCSI .substring(0, 15);   
            String sURLforStandard;
            
            if(oCurrentUser.ProfileId == csiProfileId)
            {
                sURLforStandard = '/' + Schema.getGlobalDescribe().get(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM).getDescribe().getKeyPrefix() + '/e?'+GLOBAL_CONSTANT_HUM.CASEMGMT_RETURL +'='+sReturnURL;
                if(!String.isEmpty(sWhoId))
                {
                	sURLforStandard +=  '&' + GLOBAL_CONSTANT_HUM.CASEMGMT_WHO_ID + '=' + sWhoId;
                }   
                if(!String.isEmpty(sWhatId))    sURLforStandard +=  '&' + GLOBAL_CONSTANT_HUM.CASEMGMT_WHAT_ID + '=' + sWhatId;
                {
              	  sURLforStandard += GLOBAL_CONSTANT_HUM.CASEMGMT_OVERRIDE_ONE;
                }
                return new PageReference(sURLforStandard);               
            }
            else
            {
                if(String.isEmpty(oCurrentUser.Current_Queue__c))
                {
                    PageReference oPageRef = new PageReference(GLOBAL_CONSTANT_HUM.CASECLONE_QUEUE_SEL_PAGE_HUM);
                    oPageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASECLONE_NEXT_PAGE_HUM, GLOBAL_CONSTANT_HUM.CASEMGMT_TASKEDITPAGE_HUM);
                    oPageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEMGMT_WHOID, sWhoId);
                    oPageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEMGMT_WHATID, sWhatId);
                    oPageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASECLONE_OBJECT_TYPE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM );
                    oPageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEMGMT_RECORDTYPE, sRecordTypeIdCRM);
                    return oPageRef;                                        
                }
                else 
                {
               	    return new PageReference(GLOBAL_CONSTANT_HUM.CASEMGMT_APEXIN_HUM + GLOBAL_CONSTANT_HUM.CASEMGMT_TASKEDITPAGE_HUM + '?&' + GLOBAL_CONSTANT_HUM.CASEMGMT_RECORDTYPE +'='+sRecordTypeIdCRM+'&' + GLOBAL_CONSTANT_HUM.CASEMGMT_WHAT_ID + '=' + sWhatId +'&'+GLOBAL_CONSTANT_HUM.CASEMGMT_PARENT_TASKID+'='+sParentTaskID+ GLOBAL_CONSTANT_HUM.CASEMGMT_OVERRIDE_ONE);
                } 
            }            
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMTaskNewButtonOverrideExtension','reDirectPage');
            return null;
        }             
    }
}