/*******************************************************************************************************************************
Apex Class Name : HUMTaskDeleteExtension
Version         : 1.0
Created On      : 09/27/2021 
Function        : This class is used to delete task or not delete based on recordtype of task.                  
Test Class      : HUMTaskEditButtonOverrideExtensionTest
                  
Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
  Akshay                                                         27/09/2021                 Original Verison User story : 2561838
*************************************************************************************************************************/
public with sharing class HUMTaskDeleteExtension {
	
    private Task objTask;
     
    /*
    * Method Name : HUMTaskDeleteExtension 
    * Description : Constructor for the class, Initialize variables , read page parameter Values
    * Return type : N/A
    * Paramater   : StandardController 
    */
    public HUMTaskDeleteExtension(ApexPages.StandardController controller) {
        this.objtask = (Task)controller.getRecord();
    }
    
    /*
     * Method Name : urlRedirect
     * Description : This method is used to redirect the task to a standard or custom detail page based on recordtype of task.
     * Return type : PageReference 
     * Paramater   : N/A
     */
    public PageReference urlRedirect() { 
        Id getRecordTypeId = objtask.RecordTypeId;
        String selectedRecordType = Schema.SObjectType.Task.getRecordTypeInfosById().get(getRecordTypeId).getName();
        Task objTaskOwnerId = [Select Id,OwnerId FROM task WHERE Id =: objTask.Id LIMIT 1];
        Id assignedToOwner = objTaskOwnerId.OwnerId;
        Id userId = userInfo.getUserId();
        try{
           	CRMRetail_Edit_Delete_Task_Bypass__c objEditDeleteBypass = CRMRetail_Edit_Delete_Task_Bypass__c.getValues(selectedRecordType);
            Boolean Switch_2561838 = HUMUtilityHelper.isCRMFunctionalityON('2561838');
            if(Switch_2561838 && objEditDeleteBypass!= null && userId == assignedToOwner)
            {            
                ApexPages.StandardController sc = new ApexPages.StandardController(objTask);
                PageReference currentPage = sc.delete();
                currentPage.getParameters().put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_NO_OVERRIDE,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CURRENT_PAGE_PARAM);
                currentPage.setRedirect(true);
                return currentPage;
            }else{
                return null;
            }
        } 
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'HUMTaskDeleteExtension', 'HUMTaskDeleteExtension');
            return null;
        }
        
    }
    
}