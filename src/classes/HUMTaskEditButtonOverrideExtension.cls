/*******************************************************************************************************************************
Apex Class Name : HUMTaskEditButtonOverrideExtension
Version         : 1.0
Created On      : 09/10/2014 
Function        : This class is used to redirect task to standard or custom edit pages based on recordtype of task.                  
Test Class      : HUMTaskEditButtonOverrideExtensionTest
                  
Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
Mrudula Jaddu                 17930                             09/10/2014                 Original Version
SuryaKumari Medicherla                                          09/18/2014                 Romved mutiple SQL queries for custom settings
Jasmeen Shangari                                                05/20/2021                 US#1800829 Redirect to standar page for Wellness Profiles  
Mithra Bharadwaj                                                                           US#2089563 Sonar Qube Changes
Akshay                                                          09/27/2021                 2561838
*************************************************************************************************************************/

public with sharing class HUMTaskEditButtonOverrideExtension 
{
    private String sReturnURL = '';
    private String sId= '';
    private Map<String, HUMTaskRecordTypes__c> mapTaskRecordTypes = HUMTaskRecordTypes__c.getAll();
    public boolean isStdEdit {get; set;}
    public String navURL {get; set;}
    
    
    /*
    * Method Name : HUMTaskEditButtonOverrideExtension 
    * Description : Constructor for the class, Initialize variables , read page parameter Values
    * Return type : N/A
    * Paramater   : StandardController 
    */
    public HUMTaskEditButtonOverrideExtension (ApexPages.StandardController controller) 
    {
        if( ApexPages.CurrentPage().getParameters().get(System.label.HUMretURL) != NULL)
        {
            sReturnURL = ApexPages.CurrentPage().getParameters().get(System.label.HUMretURL).escapeHTML4();
        }
        sId = ApexPages.CurrentPage().getParameters().get(System.label.HUMId).escapeHTML4();
        isStdEdit = false;
    }
       
    /*
    * Method Name : reDirectPage
    * Description :  This method is used to redirect the task to a standard or custom detail page based on recordtype of task.
    * Return type : PageReference 
    * Paramater   : N/A
    */
    public PageReference reDirectPage() 
    {
        try
        {
            DescribeSObjectResult oResult =  Schema.getGlobalDescribe().get(HUMConstants.OBJECT_TASK).getDescribe();
            MAP<String,Schema.RecordTypeInfo> mapRecordTypeInfo = oResult.getRecordTypeInfosByName();
            String sCSIRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).CSIRecordTypeName__c;
            String sServiceCRMRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).ServiceCRMRecTypeName__c;
            String sRecordTypeIdCSI  = mapRecordTypeInfo.get(sCSIRecName).getRecordTypeId();
            String sRecordTypeIdCRM = mapRecordTypeInfo.get(sServiceCRMRecName).getRecordTypeId();
            String sRecordTypeIdCSI15Char = sRecordTypeIdCSI.substring(0, 15);           
            String sSelectedRecordType = '';
            String sSelectedRecordTypeName = '';
            
            list<Task> lstTask = [SELECT RecordTypeId, RecordType.Name FROM Task WHERE Id =:sId limit 1];            
            if(lstTask!= null && !lstTask.isEmpty())
            {
                sSelectedRecordType = lstTask[0].RecordTypeId;
                sSelectedRecordTypeName = lstTask[0].RecordType.name;
            }
            CRMRetail_Edit_Delete_Task_Bypass__c objEditDeleteBypass = CRMRetail_Edit_Delete_Task_Bypass__c.getValues(sSelectedRecordTypeName);
            Boolean Switch_2561838 = HUMUtilityHelper.isCRMFunctionalityON('2561838');
            
            if(Switch_2561838 && objEditDeleteBypass!= null)
            {
                isStdEdit =  true; 
                navURL =  GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LIGHTNING_URL_TASK_ID+sId+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_BACKGROUND_URL+sId+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_END_URL;
            }
            else if (sSelectedRecordType.equals(sRecordTypeIdCSI) || sSelectedRecordTypeName.EqualsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sCoachWorkTask) || sSelectedRecordTypeName.EqualsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sCoachMemberTask))
            {
                return new PageReference('/' + sId + '/e?' + System.label.HUMretURL + '=' + sReturnURL + System.label.HUMEnt + System.label.HUMNoOverrideEqualsOne);               
            }                
            else
            {
                return new PageReference(System.label.HUMApexinURL + System.label.TaskEditPage + '?&' + System.label.HUMId + '=' + sId + System.label.HUMRecordtypeURL + sRecordTypeIdCSI15Char + System.label.HUMEnt + System.label.HUMNoOverrideEqualsOne);
            }  
            return null;
        }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMTaskEditButtonOverrideExtension','reDirectPage');
            return null;
        }             
    }
}