/*******************************************************************************************************************************
Apex Class Name : QueueViewItem_H_HUM
Version         : 1.0
Created On      : 06/14/2016 
Function        : Helper class for QueueViewItem_G_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya          	  		                06/14/2016                   Original Version
* Rajesh Keswani          	  		                10/07/2016                   REQ - 333701; Set/Group field, Edit Expression UI & Deleting Queue View Items - Expression impacts.
********************************************************************************************************************************/
public with sharing class QueueViewItem_H_HUM 
{
	/**
    * <p>
    * Update Criteria Updated field on Queue View.
    *
    * @param     lstQVI  	List of queue view item records
    * @return    Void
    */
    public static void updateQueueView(List<Queue_View_Item__c> lstQVI, boolean isDelete) 
    {
        try
        {
            Set<Id> setQueueViewIds = new Set<Id>();
            List<Queue_View__c> lstQV = new List<Queue_View__c>();
            
            for(Queue_View_Item__c queueViewItem : lstQVI)
            {
                setQueueViewIds.add(queueViewItem.Queue_View__c);
            }
            
            if(!setQueueViewIds.isEmpty())
            {
            	lstQV = QueueViewItem_D_HUM.getQVInfo(setQueueViewIds);
            }
            if(isDelete) QueueViewItem_D_HUM.updateQVExpression(lstQV,lstQVI);
            for (Queue_View__c queueView : lstQV)
            {
                queueView.Criteria_Updated__c = true;
            }
            
            QueueViewItem_D_HUM.updateQV(lstQV);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'QueueViewItem_H_HUM','updateQueueView');
        }
              
    }   
}