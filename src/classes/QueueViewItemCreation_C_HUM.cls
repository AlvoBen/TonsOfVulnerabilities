/*****************************************************************************************************************************************************************************************************************************************************
Apex Class Name  : QueueViewItemCreation_C_HUM
Version          : 1.0
Created Date     : June 01 2016
Function         : Class to create the Queue View Items based on the Queue View & Queue View Setup.
Test Class       : QueueViewItemCreation_T_HUM
 ******************************************************************************************************************************************************************************************************************************************************

Modification Log:

 * Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Sharan Shanmugam            29395                06/01/2016                   Original Version
 * Shivam Anand                                     08/18/2018                   REQ - 366120 editible  PGV Name field
 * Suresh Sunkari              339349               02/22/2019			         REQ - 377482 Report on Business Segments within the platform - ALOB
 * Manikanta                   		                07/11/2019                   REQ - 399177 - Added logic to resolve the queue view item issue (collection size exceeds maximum size of 1,000)
 * Vinay Lingegowda                                 04/30/2021			         T1PRJ0001827- IR 5050 - Update Queue View - Details, New and Edit page - Add SLA fields (CRM)
 * Bhumika Dodiya                                   07/22/2021                   T1PRJ0001827 - IR 5050 - Tech Debt - Clean up switch for past releases(CRM) -  clean up of User Story 2227969
 *****************************************************************************************************************************************************************************************************************************************************/

Public with sharing class QueueViewItemCreation_C_HUM
{
    private QueueViewItemCreation_S_HUM sLayer = new QueueViewItemCreation_S_HUM(); 
    public Map<String,String> mapEncryptedValues = new Map<String,String>();
    public List<HUMQueueViewCriteriaWrapper> lstQVCWrapperInsert = new List<HUMQueueViewCriteriaWrapper>();
    public Map<Id, List<HUMQueueViewCriteriaWrapper>> mapWrapper = new Map<Id, List<HUMQueueViewCriteriaWrapper>>();
    public Integer counter = 0;
    public List<Group> lstGroups = new List<Group>();
    public Map<String,String> mapQV = new Map<String,String>();    
    public Boolean bMatchWQSLA{get; set;}
    public Boolean bEnableSLA{get; set;}
    public Boolean bCreateEditAccess{get; set;}
    
   /**
   * invokes service layer for constructing the variables.
   * <p>
   * Constructor Method to for the class , Initialize variables
   */
    public QueueViewItemCreation_C_HUM()
    {
        try
        {            
            this.bMatchWQSLA = false;
            this.bEnableSLA = false;
        	Id sID = (ID) ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.ID_HUM);
        	List<Queue_View__c> oQV =[SELECT Id, Name, Group_Name__c FROM Queue_View__c where id=:sID];
            if(!oQV.IsEMpty())
                publicGroupRecordName = oQV[0].Group_Name__c;
            bCreateEditAccess = ((Schema.sObjectType.Queue_View__c.fields.SLA_In_Days__c.isCreateable() || Schema.sObjectType.Queue_View__c.fields.SLA_In_Days__c.isUpdateable()) && 
                            (Schema.sObjectType.Queue_View__c.fields.SLA_On_Case_Transfer__c.isCreateable() || Schema.sObjectType.Queue_View__c.fields.SLA_On_Case_Transfer__c.isUpdateable())) ? true: false;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            listErrorLogs = HUMExceptionHelper.logErrors(e,'QueueViewItemCreation_C_HUM','QueueViewItemCreation_C_HUM');
        }        
    }
    
    // Work Queue ID
    public ID sWQSId {get;set;}
    //Queue View ID 
    public ID sQVID {get;set;}
    public ID sQVCreationID {get;set;}
    public List<SelectOption> lstWQI {get;set;}
    public String sQVType {get;set;}
    public Work_Queue_Setup__c workQueueSetup {get;set;}
    public Queue_View_Item__c queueViewItem {get;set;}
    public Queue_View__c queueView {get;set;}
    public List<HUMQueueViewCriteriaWrapper> lstQViewItemWrapper {get;set;}
    public Boolean bRenderQWI {get;set;}
    public Boolean bShowTable {get;set;}
    public Integer iCurrentCounter{get;set;}
    public boolean bShowQueueView {get;set;}
    public List<Queue_View_item__c> lstDeleteQueueViewItem {get;set;}
    public boolean bIsEdit{get;set;}
    public String publicGroupRecordName { get; set; }
    public List<Queue_View_Item__c> lstQWIRecords { get; set; }
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs { get; set; }
    public String sQueueViewItems{get;set;}
    
    /**
    * Initialized variables related to QueueViewItemCreation_C_HUM class
    * <p>
    * Method to set the values for QueueViewItemCreation_C_HUM.
    *
    @param  null
    */
    public void initialize()
    {
        sWQSId = NULL;
        sQVID = NULL;
        sQVCreationID = NULL;
        lstWQI = new List<SelectOption>();
        workQueueSetup = new  Work_Queue_Setup__c();
        queueViewItem = new Queue_View_Item__c();
        queueView = new Queue_View__c();
        lstQViewItemWrapper = new List<HUMQueueViewCriteriaWrapper>();
        bRenderQWI = false;
        bShowTable = false;
        iCurrentCounter = 0;
        bShowQueueView = false;
        lstDeleteQueueViewItem = new List<Queue_View_Item__c>();
        bIsEdit = false;
        lstQWIRecords = new List<Queue_View_Item__c>();
        listErrorLogs = new List<Error_Log__c>();
        sLayer.getWQQVIds(this);
        bEnableSLA = (queueView.Work_Queue_Name__r.SLA_In_Days__c != null && queueView.Work_Queue_Name__r.SLA_In_Days__c > 0);     
        sQueueViewItems = JSON.serialize(lstQViewItemWrapper);
        sQueueViewItems = '{"data":'+sQueueViewItems+'}';
        if(!bEnableSLA && bCreateEditAccess){
            fetchWorkQueueSLADetails();
        }
    }
    
     /**
    * Returns pageReference url when QVI is added
    * <p>
    * Method to add the Queue View Items to the wrapper list once user clicks on Add button.
    *
    * @param                               NULL
    * @return                              PageReference
    */ 
    public pageReference addQVI()
    {
        return sLayer.addQVI(this);
    }
    
     /**
    * Returns pageReference url when QVI is removed
    * <p>
    * Method to remove the Queue View Items from the wrapper list once user clicks on Remove link.
    *
    * @param                               NULL
    * @return                              PageReference
    */ 
    public pageReference removeQVI()
    {
        return sLayer.removeQVI(this);
    }
    
     /**
    * Returns pageReference url which redirects to respective detail page
    * <p>
    * Method to save the Queue View Items & redirect the user to the QWS detail page
    *
    * @param                               NULL
    * @return                              PageReference
    */ 
    public pageReference save()
    {
        if(queueView.Accountable_Line_of_Business__c == '' || queueView.Accountable_Line_of_Business__c == null)
        {
       		ApexPages.addmessage(New ApexPages.message(ApexPages.severity.Error, Label.Queueview_ALOB));
            return null;
        }else if(queueView.SLA_In_Days__c > 400){
            ApexPages.addmessage(New ApexPages.message(ApexPages.severity.Error, Label.SLAErrorMsg));
       	    return null;
        }
        else{
            return sLayer.save(this);
        }
    }
    
     /**
    * Returns pageReference url which redirects the page to WQ detail page
    * <p>
    * Method to redirect the user to the QWS detail page when on click of Cancel button
    *
    * @param                               NULL
    * @return                              PageReference
    */ 
    public pageReference cancel()
    {
        return sLayer.cancel(this);
    }
    
     /**
    * Returns pageReference to populate the PGV name based on QV name
    * <p>
    * Method to populate the PGV name.
    *
    * @param                               NULL
    * @return                              PageReference
    */ 
    public PageReference populatePGV()
    {
       return sLayer.populatePGV(this);   
    }
    
     /**
    * Method to update the PGV name.
    *
    * @param                               NULL
    * @return                              void
    */ 
    public void updatePGV()
    {
        sLayer.updatePGV(this);
    }
    
    /**
    * Method to fetch SLA fields for the selected work Queue.
    * @param null
    * @return void
    */ 
    public void fetchWorkQueueSLADetails() {
        this.bMatchWQSLA = false;
        this.bEnableSLA = false;
        this.queueView.SLA_In_Days__c = null;
        this.queueView.SLA_On_Case_Transfer__c = false;
        if(String.isNotBlank(this.queueView.Work_Queue_Name__c)){   
            list<Work_Queue_Setup__c> lstFetchWQSetup = [select id, SLA_In_Days__c, SLA_On_Case_Transfer__c from Work_Queue_Setup__c where id =: this.queueView.Work_Queue_Name__c and SLA_In_Days__c != null Limit 1];
            if(!lstFetchWQSetup.isEmpty() && lstFetchWQSetup.size() > 0){
                this.queueView.SLA_In_Days__c = lstFetchWQSetup[0].SLA_In_Days__c;
                this.queueView.SLA_On_Case_Transfer__c = lstFetchWQSetup[0].SLA_On_Case_Transfer__c;
                this.bMatchWQSLA = true;
                this.bEnableSLA = true;
            }
        }
    }
}