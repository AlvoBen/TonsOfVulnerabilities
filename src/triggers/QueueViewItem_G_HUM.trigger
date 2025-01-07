/*******************************************************************************************************************************
Apex Class Name : QueueViewItem_G_HUM
Version         : 1.0
Created On      : 06/14/2016 
Function        : Trigger execution sequence logic for updating the queue view

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya                                   06/14/2016                  Original Version
* Rajesh Keswani          	 163966                 12/22/2017                  REQ - 335304; Set/Group field, Edit Expression UI & Deleting Queue View Items - Expression impacts.
********************************************************************************************************************************/

trigger QueueViewItem_G_HUM on Queue_View_Item__c (after delete, after insert, after update) 
{
    if(Trigger.isAfter)
    {
        if(Trigger.isInsert)
        {
            QueueViewItem_H_HUM.updateQueueView(trigger.new, false);
        }
        else if(Trigger.isUpdate)
        {
            QueueViewItem_H_HUM.updateQueueView(trigger.new, false);
        }
        else if(Trigger.isDelete)
        {
            QueueViewItem_H_HUM.updateQueueView(trigger.old, true); 
        }
    }
}