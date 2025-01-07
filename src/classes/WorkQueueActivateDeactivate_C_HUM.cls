/*******************************************************************************************************************************
Apex Class Name :  WorkQueueActivateDeactivate_C_HUM
Version: 1.0
Created On      : 03/02/2016
Function        : Controller for WorkQueueActivateDeactivate_VF_HUM

Modification Log:  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya             30706                  03/02/2016                   Original Version
* Harshith Mandya             50014                  11/30/2106                   Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                  Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
* Prudhvi Pamarthi            154304               01/19/2018                     REQ - 328929 Fix for HUMExceptionHelper: DML
*   Suresh Sunkari            272809                      08/16/2018            REQ - 366108 Implemented it.  Regarding Transfer Open cases and Tasks 
* Prudhvi Pamarthi                                   05/05/2021                     US#2212581 Checkmarx fix
********************************************************************************************************************************/

Public with sharing Class WorkQueueActivateDeactivate_C_HUM
{

    
    Private WorkQueueActivateDeactivate_S_HUM sLayer = New WorkQueueActivateDeactivate_S_HUM();  
    Public String sActivateDeactivate =''; 
  
    /**
       * invokes service layer for constructing the variables.
       * <p>
       * Constructor Method to for the class , Initialize variables
   */
    Public WorkQueueActivateDeactivate_C_HUM()
    {
        sWQSId = NULL;  
        workQueues = New Work_Queue_Setup__c(); 
        init();    
        DependentPickListController();
    } 
    
    
    
    Public Work_Queue_Setup__c workQueues {Get; Set;}  
    Public ID sWQSId {Get;Set;}
    public boolean bEditQueViewFlag { get; set; }
    public boolean bEditQueViewFlag2 { get; set; }
    public boolean bEmptyWQTPerrorFlag { get; set; }
    Public Map<String,List<SelectOption>> mapQueuesWithTopics = new Map<String,List<SelectOption>>();
    Public List<POCWrapper> lstItems{get;set;}
    Public String sQueue{get;set;}
    
     //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
     /**
    * Returns String label activate/deactivate depending on the current state
    * <p>
    * Method to return Activate/De-Activate label to be shown on the page.
    *
    * @param    NULL
    * @return   String
    */ 
    Public String getActiveDeactive() 
    {
        Return sLayer.activeDeactive(this);
    }
    
     /**
    * Returns PageReference url when Work Queue Is Activated/De-activated
    * <p>
    * Method to Activate/De-Activate Work Queue.
    *
    * @param    NULL
    * @return   PageReference
    */ 
    Public PageReference activateDeactivate() 
    {    
        queViewEditFlag();
        Return sLayer.updateActiveDeactive(this);
    }
     
    
    /**
    * Method to Initialize variables related to the functionality.
    *
    * @param     NULL
    * @return    Void                                           
    */ 
    Private Void init() 
    {
        Try
        {
            bEditQueViewFlag = true;
            bEditQueViewFlag2 = false;
            bEmptyWQTPerrorFlag = false;
            
            
            
            sWQSId = (ID)ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.ID_HUM);
            If (sWQSId!= null)
            {
                workQueues = sLayer.getWQViews(this);
            }  
        }
        Catch(Exception ex)
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex,'WorkQueueActivateDeactivate_C_HUM', 'init');
            HUMExceptionHelper.bShowErrorOnPage = false;
        }
    }
     
     /**
    * Returns PageReference error message or new page when complete deactivation is clicked
    * <p>
    * Method to Activate/De-Activate Work Queue.
    *
    * @param    NULL/New Page
    * @return   PageReference
    */ 
       
    public PageReference OnFinishPage()
    {
        SaveData();
        
        if(bEmptyWQTPerrorFlag != true)
        {
            HUMTriggerSwitch__c oHTS = HUMTriggerSwitch__c.getValues('Work Queue Setup');
    		oHTS.Exeute_Trigger__c = false;
    		update oHTS;
            
            if(Schema.sObjectType.Work_Queue_Setup__c.fields.isActive__c.isUpdateable())
            workQueues.isActive__c = false;
    		
    	    update workQueues;
    	    oHTS.Exeute_Trigger__c = true;
    		update oHTS;
            
            PageReference prRef = new PageReference('/apex/WorkQueueDeactivatecompletion_VF_HUM');
            prRef.getParameters().put('id',sWQSId); 
            prRef.setRedirect(true);
            return prRef;
        }
        
        else
        {
    
            ApexPages.Message myMsg = New ApexPages.Message(ApexPages.Severity.ERROR,system.label.QUEUEUNSELECTERROR_WORKQUEUE_HUM);
             ApexPages.addMessage(myMsg);
             Return null;
        }
        
        
        
     
   } 
    /**
    * Returns PageReference to add message to complete deactivate page
    * <p>
    * Method to show message
    *
    * @param    NULL
    * @return   PageReference
    */ 
 
    public PageReference CheckValidation(){     
                If (!bEditQueViewFlag2)
                {
                    ApexPages.Message myMsg = New ApexPages.Message(ApexPages.Severity.info,system.label.BATCH_JOB_REASSIGNMENT_WORKQUEUE_HUM);
                    ApexPages.addMessage(myMsg);
                    
                }
                Return null;
            }
     
    
    
     public void queViewEditFlag()
    {
       bEditQueViewFlag = false;
        bEditQueViewFlag2 = true;
    }

     /**
    * Returns NUll 
    * <p>
    * Method togenerate picklist to generate topic and queue 
    *
    * @param    NULL
    * @return   null
    */ 
 
     
    public void DependentPickListController()
    {
        
        
       
        Map<ID, Work_Queue_setup__c> mapQueue = new Map<ID, Work_Queue_setup__c>([SELECT ID, Name FROM Work_Queue_setup__c where isActive__c = true ORDER BY Name]);
        Map<ID, Work_Queue_Topic__c> mapTopic = new Map<ID, Work_Queue_Topic__c>([SELECT Id, Name FROM Work_Queue_topic__c where isActive__c = true ORDER BY Name]);
        Map<ID, Work_Queue_Junction__c> mapjn = new Map<ID, Work_Queue_Junction__c>([SELECT ID, Work_Queue_Topic_Name__c, Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__r.Name from Work_Queue_Junction__c order by Work_Queue_Topic_Name__r.Name]);
        List<Work_Queue_Topic__c> listNAacheck  = new List<Work_Queue_Topic__c>([SELECT Id, Name FROM Work_Queue_topic__c where Name = 'NA' limit 1]);
        Id naID = listNAacheck[0].Id ;   
        for(ID queue: mapQueue.keyset())
        {
            
            
            List<ID>  Work_Queue_Topic_list = new List<ID>();
            List<SelectOption> topics = new List<SelectOption>();
            for(Work_Queue_Junction__c temp : mapjn.values()  )
            {
                if(temp.Work_Queue_Setup_Name__c == queue){
                    
                    Work_Queue_Topic_list.add(temp.Work_Queue_Topic_Name__c);
                }
                
            
            }
            
            
            topics.add(new SelectOption('','Select a Topic'));
            for(ID oWQS : Work_Queue_Topic_list  )
            {
                if(mapTopic.containsKey(oWQS)){
                    
                    topics.add(new SelectOption(oWQS,mapTopic.get(oWQS).Name));
                }
            }
            
            topics.add(new SelectOption(naID,'NA'));
            
            
            mapQueuesWithTopics.put(queue,topics);
            
            
        }
        
        
        lstItems = new List<POCWrapper>();
        
        
        
        POCWrapper obj = new POCWrapper(mapQueuesWithTopics);
        List<Queue_View__c>  Queue_View_list = new List<Queue_View__c>();
        Queue_View_list  = [SELECT Name,Queue_View_Name__c, Work_Queue_Name__c, Id FROM Queue_View__c where Work_Queue_Name__c =: sWQSId];
        
        List<Queue_View__c> listOthercheck  = new List<Queue_View__c>([SELECT Id, Name FROM Queue_View__c where Work_Queue_Name__c =: sWQSId And Name = 'Other' limit 1]);
        
        if(listOthercheck.isEmpty())
        {
            Queue_View__c qvOther = new Queue_View__c(Name = 'Other');
            Queue_View_list.add(qvOther);
        }
        
        
        
        for(Queue_View__c qvtemp : Queue_View_list )
        {
                obj = new POCWrapper(mapQueuesWithTopics);
                obj.sViewName = qvtemp.Name;
                obj.lstQueueNames.Add(new SelectOption('','Select a work queue'));
            for(ID queue: mapQueue.keyset())
            {
                
                
                
                obj.lstQueueNames.Add(new SelectOption(queue,mapQueue.get(queue).Name));

            }
            
            lstItems.Add(obj);
           
        }
        
       
        
    }
    
     /**
    * Returns NUll 
    * <p>
    * Method to save the selected  picklist values 
    *
    * @param    NULL
    * @return   null
    */ 
 
    
    public void SaveData()
    {
        List<WQS_Case_Transfer_History__c> lstobj = new List<WQS_Case_Transfer_History__c>();
        Integer count = 0;
        For(POCWrapper obj :lstItems)
        {
           WQS_Case_Transfer_History__c wqs = new WQS_Case_Transfer_History__c();
        	
            
           
            
            wqs.From_Queue_View__c = obj.sViewName;
            wqs.ParentId__c = sWQSId;
            if(obj.sSelectedQName == '' || obj.sSelectedQName == null ||  obj.sSelectedTopic == '' || obj.sSelectedTopic == null)
            {
            	 count = count + 1;
            }
            
            wqs.To_Work_Queue__c = obj.sSelectedQName;
            wqs.To_Work_Queue_Topic__c = obj.sSelectedTopic;
            
            wqs.Transfer_Status__c = 'In Progress';
            
            
            lstobj.add(wqs);
            
        }
        if(count == 0)
        {
            bEmptyWQTPerrorFlag = false;
            insert lstobj;
        }
        else{bEmptyWQTPerrorFlag = true;}
        
        
    }
    
    /**
    * 
    * <p>
    * wrapper class to generate and get the picklist values
    *
    
    */ 
    
     
    public class POCWrapper
    {
        Public String sViewName{get;set;}
        Public String sSelectedQName{get;set;}
        Public String sSelectedTopic{get;set;}
        Public List<SelectOption> lstQueueNames{get;set;}
        List<SelectOption> topicNames = new List<SelectOption>();
        Map<String,List<SelectOption>> mapQueuesTopics;
        
        public POCWrapper(Map<String,List<SelectOption>> mapdetails)
        {
            mapQueuesTopics = mapdetails;
            lstQueueNames = new List<SelectOption>();
        }
        
        Public List<SelectOption> getlstTopics()
        {
            
            
            
            If(String.IsBlank(sSelectedQName)) 
            {
                
               topicNames.Add(new SelectOption('','Select a Topic'));
               
            }
            else
            {
                
               
               If(mapQueuesTopics.containsKey(sSelectedQName)) topicNames = mapQueuesTopics.get(sSelectedQName);
            }
            return topicNames ;
        }
    }
       
       
    
}