/*******************************************************************************************************************************
Apex Class Name : HUMCaseTaskBatchHelper
Version         : 1.0
Created On      : 08/01/2015
Function        : This is a helper class to case/task batch apex.
Test Class      : HUMCaseTaskBatchHelperTest

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava                                         08/01/2015                 Original Version
* Sharan Shanmugam			   47072				     07/07/2016					Modified code to pick only active queue and queue view
*******************************************************************************************************************************/

public with sharing class HUMCaseTaskBatchHelper
{   
     public static Database.UpsertResult[] dmlResults{get;set;}
     
     /*
     * Method Name   :   processDMLResults
     * Description   :   This method is used to process DML results
     * Return Type   :   NA
     * Parameters    :   NA 
     */
     public static void processDMLResults()
     {
        set<ID> failId = new set<ID>();
        for(Integer i = 0; i < dmlResults.size(); i++)
        {
            if(!(dmlResults[i].isSuccess()))
            {
                failId.add(dmlResults[i].ID);      
            }
        }           
     }
     
     /*
     * Method Name   :   getUpdatedQueueNames
     * Description   :   This method is used to find the updated queue names based on views/view items/queue priority.
     * Return Type   :   Set<String>
     * Parameters    :   NA 
     */
    public static Set<String> getUpdatedQueueNames()
    {
        Set<String> setQueueNames=new Set<String>();
        DateTime dtEndDate=DateTime.Now();
        Date dtPastDate = Date.today().AddDays(-Integer.valueOf(HUMConstants__c.getInstance('HUMDefaultBatchLimit').IntegerValue__c));
        DateTime dtStartDate = DateTime.newInstance(dtPastDate.year(),dtPastDate.month(),dtPastDate.day(),HUMConstants.ZERO,HUMConstants.ZERO,HUMConstants.ZERO);
        HUMQueueViewBatchSettings__c objBatchSetup = HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');
        If(objBatchSetup.BatchExecutionDate__c != null) 
        {
            dtStartDate =objBatchSetup.BatchExecutionDate__c;
        }
        
        for (Queue_View__c obj: [Select Work_Queue_Name__r.Name From Queue_View__c where LastModifiedDate > : dtStartDate AND LastModifiedDate<= : dtEndDate and Work_Queue_Name__r.IsActive__c = true and IsActive__c = true All ROWS])
        {
            setQueueNames.Add(obj.Work_Queue_Name__r.Name);
        }
       
        for(Queue_View_Item__c obj: [Select Queue_View__r.Work_Queue_Name__r.Name from Queue_View_Item__c where LastModifiedDate > : dtStartDate AND LastModifiedDate<= : dtEndDate and Queue_View__r.Work_Queue_Name__r.IsActive__c = true and Queue_View__r.IsActive__c = true])
        {
            setQueueNames.Add(obj.Queue_View__r.Work_Queue_Name__r.Name);
        }
        
        for(Work_Queue_Priority__c obj : [Select Work_Queue_Name__r.Name from Work_Queue_Priority__c where LastModifiedDate > : dtStartDate AND LastModifiedDate<= : dtEndDate and Work_Queue_Name__r.IsActive__c = true])
        {
            setQueueNames.Add(obj.Work_Queue_Name__r.Name);
        }

        for(Queue_View__c qv: [Select  Work_Queue_Name__r.Name from Queue_View__c where Criteria_Updated__c = true and Work_Queue_Name__r.IsActive__c = true and IsActive__c = true])
        {
            setQueueNames.add(qv.Work_Queue_Name__r.Name); 
        }
        
        For (Work_Queue_Setup__c obj : [Select id,Name from Work_Queue_Setup__c where IsActive__c = true and Batch_Update_Required__c = true])
        {
            setQueueNames.Add(obj.Name);
        }
        
        objBatchSetup.TriggerExecutionRequired__c=false;
        objBatchSetup.BatchExecutionDate__c =dtEndDate;
        update objBatchSetup;
        return setQueueNames;
    }
    
}