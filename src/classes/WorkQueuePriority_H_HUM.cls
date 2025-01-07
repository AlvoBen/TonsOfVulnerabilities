/****************************************************************************
Apex Class Name  : WorkQueuePriority_H_HUM
Version          : 1.0 
Created Date     : August 12 2016
Function         : This class is invoked from Work Queue Priority trigger. This validates the value of priority being entered on the work queue
TestClass Name   : HUMWorkQueuePriorityTriggerHelperTest
Modification Log :
 *  Modification ID     Developer              Code Review           Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *          1.0         Amit Sinha                                  08/12/2016           Original Version                                                                                     
 **********************************************************************************************************************************/
public with sharing class WorkQueuePriority_H_HUM 
{
    
    public static Boolean bValidInput = true;
    
    /*
     * Method Name : validateInput
     * Description : Validates the input before saving
     * Return Type : void
     * Parameters  : List<Work_Queue_Priority__c>
     */
    public static void validateInput(List<Work_Queue_Priority__c> lstWqp) 
    { 
        try
        {
            Set<Integer> setUniquePriority = new set<Integer>();
            
            for(Work_Queue_Priority__c wqp : lstWqp)
            {
                setUniquePriority.add(Integer.valueof(wqp.Priority_1__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_2__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_3__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_4__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_5__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_6__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_7__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_8__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_9__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_10__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_11__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_12__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_13__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_14__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_15__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_16__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_17__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_18__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_19__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_20__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_21__c));
                setUniquePriority.add(Integer.valueof(wqp.Priority_22__c));
                            
                //To check if the priority values are unique and between 1 and 22 
                if(setUniquePriority.size() == 22)
                {
                    for(Integer i : setUniquePriority)
                    {
                        if(i < 1 || i > 22)
                        {
                            bValidInput = false;
                            wqp.addError(system.label.WQPRIORITY_WORKQUEUE_HUM);
                        }
                    }
                }
                else
                {
                    bValidInput = false;
                    wqp.addError(system.label.WQPRIORITYUNIQUE_WORKQUEUE_HUM);  
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HUMWorkQueuePriorityTriggerHelper','validateInput');
        }
    }
    
    /*
     * Method Name : updateWorkQueueSetup
     * Description : Updates Batch Update Required field on Work Queue Setup when Work Queue Priority is updated.
     * Return Type : void
     * Parameters  : List<Work_Queue_Priority__c>
     */
    public static void updateWorkQueueSetup(List<Work_Queue_Priority__c> lstWqp)
    {
        set<ID> setID = new set<ID>();
        List<Work_Queue_Setup__c> lstWQS = new List<Work_Queue_Setup__c>();
        try
        {
            for(Work_Queue_Priority__c oWQP : lstWqp)
            {
                setID.add(oWQP.Work_Queue_Name__c);         
            }
            
            if(!setID.isEmpty())
            {
                lstWQS = [Select id, Name, Batch_Update_Required__c from Work_Queue_Setup__c where id =: setID];
            }
            
            for(Work_Queue_Setup__c oWQS : lstWQS)
            {
                oWQS.Batch_Update_Required__c = true;   
            }
            update lstWQS;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HUMWorkQueuePriorityTriggerHelper','updateWorkQueueSetup');
        }
    }
}