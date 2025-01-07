/*******************************************************************************************************************************
Apex Name 	: HUMTaskHeirarchy_G
Version         : 1.0
Created On      : 10/21/2016
Function        : This serves a trigger for the HUMTaskHeirarchy

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Vamsi Kondragunta				44606			       10/21/2016              Update the data for Req:72833
*******************************************************************************************************************************/

trigger HUMTaskHeirarchy_G on HUM_Task_Heirarchy__c (after insert,after update) {
  if(trigger.isAfter)
    {
        HUMTaskHeirarchy_H helper=new HUMTaskHeirarchy_H();
        if(trigger.isInsert)
        {
           helper.CountofOpenTaskForParent(trigger.new);
        }
        if(trigger.isUpdate)
        {
           helper.CountofOpenTaskForParent(trigger.new);
        }
    }
    
}