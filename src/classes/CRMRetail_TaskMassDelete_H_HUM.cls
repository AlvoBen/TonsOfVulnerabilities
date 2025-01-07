/************************************************************************************************************************************************************************************       
Class Name       : CRMRetail_TaskMassDelete_H_HUM 
Created Date     : 25 February 2022
Function         : Used in CRM Retail Task Mass Delete flow to get the error/success string based on the deletion of the ids passed through CRM Retail Task Mass Delete Flow.
**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Vivek Sharma                               02/25/2022                User Story 3118168: T1PRJ0154546 MF9 Storefront - Mass Delete Task Records & New List View
************************************************************************************************************************************************************************************/
public without sharing class CRMRetail_TaskMassDelete_H_HUM {
    /**
    * getTaskRecords
    * <p>
    * Return error/success string based on the deletion of the ids passed through CRM Retail Task Mass Delete Flow.
    * @param  List<List<Task>>  ids
    * @return List<FlowOutputs>
    **/ 
    @InvocableMethod (label = 'Get Selected Task Records' description = 'query on the selected ids and perform deletion logic.')
    public static List<FlowOutputs> processTaskRecords (List<List<String>> ids) {
        Boolean recordTypeFlag = True;
        Boolean ownershipFlag = True;
        Boolean statusFlag = True;
        Id currentUserId = UserInfo.getUserId();
        String returnString = '';
        List<FlowOutputs> wrapperList = new List<FlowOutputs>();
        FlowOutputs wrapper = new FlowOutputs();
        try
        {
            List<Id> taskIdLst = new List<Id>();
            for(List<String> taskId: ids){
                taskIdLst.addAll(taskId);
            }
            List<Task> taskList = [SELECT Id, CreatedById, RecordType.DeveloperName, Status FROM Task  WHERE Id IN: taskIdLst];
            if(!taskList.isEmpty()){
                for(Task tskObj: taskList){
                    if(recordTypeFlag && tskObj.RecordType.DeveloperName != GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKRECORDTYPENAME){
                        recordTypeFlag = False;
                        returnString = returnString + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALIDTASKRECORDTYPE + '\n';
                    }
                    if(ownershipFlag && tskObj.CreatedById != currentUserId){
                        ownershipFlag = False;
                        returnString = returnString + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALIDTASKOWNERSHIP + '\n';
                    }
                    if(statusFlag && tskObj.Status != GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKNSSTATUS){
                        statusFlag = False;
                        returnString = returnString + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INVALIDTASKSTATUS + '\n';
                    }
                }
                if(recordTypeFlag && ownershipFlag && statusFlag){
                    delete taskList;
                    returnString = String.valueOf(taskList.size()) + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKSUCCESSTEXT;
                    wrapper.isSuccess = True;
                }else{
                    wrapper.isSuccess = False;
                }
            }else{
                returnString = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKDMLERROR;
                wrapper.isSuccess = False;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKMASSDELETE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GETTASK);
            returnString = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKDMLERROR;
            wrapper.isSuccess = False;
        }
        wrapper.outputString = returnString;
        wrapperList.add(wrapper);
        return wrapperList;
    }
    
    // Wrapper class to return a success DML flag and output succ/Err text string
    public class FlowOutputs 
    {
        @InvocableVariable public Boolean isSuccess = false;
        @invocableVariable public String outputString = '';
    }
}