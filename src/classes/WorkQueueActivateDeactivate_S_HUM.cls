/*******************************************************************************************************************************
Apex Class Name :  WorkQueueActivateDeactivate_S_HUM
Version: 1.0
Created On      : 03/02/2016
Function        : Service Class for WorkQueueActivateDeactivate_C_HUM

Modification Log:  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam             30706                03/02/2016                  Original Version
* Harshith Mandya              50014                11/30/2106                  Implemented REQ - 290990 -  removed all lookup related fields from
                                                                                Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
*   Suresh Sunkari            272809                08/16/2018                  REQ - 366108 Implemented it.  Regarding Transfer Open cases and Tasks                                                                                
********************************************************************************************************************************/

Public Class WorkQueueActivateDeactivate_S_HUM
{
    Private WorkQueueActivateDeactivate_D_HUM dLayer = New WorkQueueActivateDeactivate_D_HUM();
    
    /**
    * Returns Work Queue setup record And related queue views.
    * <p>
    * Method to return Work Queue Setup record And related cases/tasks
    *
    * @param     cls                WorkQueueActivateDeactivate_C_HUM class instance
    * @return    Work_Queue_Setup__c                                            
    */ 
    Public Work_Queue_Setup__c getWQViews(WorkQueueActivateDeactivate_C_HUM cls) 
    {
        Try
        {
            Return (dLayer.getWQViews(cls.sWQSId));
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueActivateDeactivate_S_HUM', 'init');
            HUMExceptionHelper.bShowErrorOnPage = false;
            Return null;
        }
    }
    
    
    
    
    /**
    * Returns String label activate/deactivate depending on the current state
    * <p>
    * Method to return Activate/De-Activate label to be shown on the page.
    *
    * @param     cls                WorkQueueActivateDeactivate_C_HUM class instance
    * @return   String  
    */ 
    Public String activeDeactive(WorkQueueActivateDeactivate_C_HUM cls) 
    {
        Try
        {
            cls.sActivateDeactivate = (cls.workQueues.IsActive__c == false ? system.label.ACTIVATE_WORKQUEUE_HUM :  system.label.DEACTIVATE_WORKQUEUE_HUM);
            Return cls.sActivateDeactivate;
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueActivateDeactivate_S_HUM', 'activeDeactive');
            HUMExceptionHelper.bShowErrorOnPage = false;
            Return null;
        }
    }
    
    /**
    * Returns PageRefernce url after Activation/De-activation of work queue
    * <p>
    * Method to Activate/De-Activate Work Queue
    *
    * @param     cls                WorkQueueActivateDeactivate_C_HUM class instance
    * @return    PageReference
    */ 
    Public PageReference updateActiveDeactive(WorkQueueActivateDeactivate_C_HUM cls) 
    {
        Try
        {
            List<SObject> lstsObjects = New List<sObject>();
            Boolean bValidAction = True;
            List<Queue_View__c> lstQueueView = New List<Queue_View__c>();
            List<User> lstUsers = New List<User>();
            
            If (!checkAssociatedCasesTasks(cls))
            {
                ApexPages.Message myMsg = New ApexPages.Message(ApexPages.Severity.info,system.label.QUEUEOPENCASEINFO_WORKQUEUE_HUM);
                ApexPages.addMessage(myMsg);
                Return null;
            }
            Else
            {
                lstsObjects.add(cls.workQueues);
                lstQueueView = updateQueueViews(cls);
                If (!lstQueueView.isEmpty())
                {
                     lstsObjects.addAll((List<sObject>)lstQueueView);
                }
                lstUsers = flushCurrentQueueforUsers(cls);
                If (!lstUsers.isEmpty())
                {
                    lstsObjects.addAll((List<sObject>)lstUsers);
                }
                dLayer.updateRecords(lstsObjects);
                Return (New PageReference('/'+cls.sWQSId));
            }
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.logErrors(ex, 'WorkQueueActivateDeactivate_S_HUM', 'updateActiveDeactive');
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
    Private Boolean checkAssociatedCasesTasks(WorkQueueActivateDeactivate_C_HUM cls)
    {
        Boolean bValidAction = True; 
        List<Case> lstCase = New List<Case>();
        List<Task> lstTask = New List<Task>();
                
        If (cls.workQueues.isActive__c)
        {           
            cls.workQueues.isActive__c = false;
            lstCase = dLayer.getOpenCases(cls.workQueues.Name);
            If (lstCase.isEmpty())
            {
                lstTask = dLayer.getOpenTasks(cls.workQueues.Name);
                If (!lstTask.isEmpty())  bValidAction = False;
            }
            Else
            { 
                bValidAction = false;
            }
        }
        Else
        {
            cls.workQueues.isActive__c = true;
        }
        Return bValidAction;
    }
    
     /**
    * Returns List<queue_View__> Activates/De-activates the queue views related to work queue
    * <p>
    * Method to Activates/De-activates the queue views related to work queue
    *
    * @param     cls                WorkQueueActivateDeactivate_C_HUM class instance
    * @return    List<Queue_View__c>
    */ 
    Private List<Queue_View__c> updateQueueViews(WorkQueueActivateDeactivate_C_HUM cls)
    {
        List<Queue_View__c> lstQueueViews = New List<Queue_View__c>();
        Boolean bActive = (cls.workQueues.isActive__c);
        For (Queue_View__c queue : cls.workQueues.Queue_Views__r)
        {
            queue.IsActive__c = bActive; 
            lstQueueViews.add(queue);
        }
        Return lstQueueViews;
    }
    
      /**
    * Returns List<User> flushes the current work queue for user record if current work queue Is deactivated
    * <p>
    * Method to flush current queue details on User record
    *
    * @param     cls                WorkQueueActivateDeactivate_C_HUM class instance
    * @return    List<User>
    */ 
    Private List<User> flushCurrentQueueforUsers(WorkQueueActivateDeactivate_C_HUM cls)
    {
        List<User> lstUsers = New List<User>();
        If (cls.workQueues.isActive__c == False)
        {
            lstUsers = dLayer.getCurrentQueue_SOQL(cls.workQueues.Name);
            For (User oUser : lstUsers)
            {
                oUser.Current_Queue__c ='';
            }
        }
        Return lstUsers;
    }
    
}