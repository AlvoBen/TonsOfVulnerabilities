/*******************************************************************************************************************************
Apex Class Name :  QueueViewActivateDeactivate_S_HUM
Version: 1.0
Created On      : 06/13/2016 
Function        : Service layer for QueueViewActivateDeactivate_VF_HUM

Modification Log:  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya                                   06/13/2016                   Original Version
* Harshith Mandya 			  50014					11/30/2106				  	 Implemented REQ - 290990 -  removed all lookup related fields from
																				 Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
* Jasmeen Shangari                                  02/22/2021                   CheckMarx fix -update class with sharing																				 
********************************************************************************************************************************/
public with sharing class QueueViewActivateDeactivate_S_HUM
{
    
    Private QueueViewActivateDeactivate_D_HUM dLayer = New QueueViewActivateDeactivate_D_HUM();
    
    /**
    * <p>
    * Method to set all the values required 
    *
    * @param     cls       QueueViewActivateDeactivate_C_HUM class instance
    * @return    Queue_View__c                                          
    */ 
    Public Queue_View__c getQueueView(QueueViewActivateDeactivate_C_HUM cls) 
    {
        Return dLayer.getQueueView(cls.sWVID);
    }
    
    /**
    * Returns PageRefernce url after Activation/De-activation of work queue
    * <p>
    * Method to Activate/De-Activate Work Queue
    *
    * @param     cls                WorkQueueActivateDeactivate_C_HUM class instance
    * @return    PageReference
    */ 
    Public PageReference activateDeactivate(QueueViewActivateDeactivate_C_HUM cls) 
    {
        List<sObject> lstsObjects = New List<sObject>();
        Try
        {
            Boolean bValidAction = True;
            bValidAction = checkAssociatedCasesTasks(cls);
            If (!bValidAction)
            {
                ApexPages.Message myMsg = New ApexPages.Message(ApexPages.Severity.ERROR,system.label.QUEUEOPENCASEERROR_WORKQUEUE_HUM);
                ApexPages.addMessage(myMsg);
                Return null;
            }
            Else
            {
                lstsObjects.add(cls.queueview);
                dLayer.updateRecords(lstsObjects);
                Return (New PageReference('/'+cls.sWVID));
            }
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.logErrors(ex, 'QueueViewActivateDeactivate_S_HUM', 'activateDeactivate');
            Return null;
        }
    }
    
    
    /**
    * Returns Boolean to identify whether there are existing cases associated to the work queue 
    * <p>
    * Method to validate whether there are existing cases associated to the work queue And update the work queue status
    *
    * @param     cls                WorkQueueActivateDeactivate_C_HUM class instance
    * @return    Boolean
    */ 
    Private Boolean checkAssociatedCasesTasks(QueueViewActivateDeactivate_C_HUM cls)
    {
        Boolean bValidAction = True; 
        List<Case> lstCase = New List<Case>();
        List<Task> lstTask = New List<Task>();
                
        If (cls.queueView.IsActive__c)
        {       	
        	cls.queueView.isActive__c = false;
            lstCase = dLayer.getOpenCases(cls.queueView.Name);
            If (lstCase.isEmpty())
            {
            	lstTask = dLayer.getOpenTasks(cls.queueView.Name);
            	If (!lstTask.isEmpty())	 bValidAction = False;
            }
            Else
            { 
                bValidAction = false;
            }
        }
        Else
        {
            cls.queueView.IsActive__c = true;
        }
        Return bValidAction;
    }
}