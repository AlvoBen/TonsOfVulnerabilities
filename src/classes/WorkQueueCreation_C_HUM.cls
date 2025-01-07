/***************************************************************************************************************************************************
Apex Class Name  : WorkQueueCreation_C_HUM
Version          : 1.0
Created Date     : 03/16/2016
Function         : Controller to create Work Queue & its related objects
Test Class       : HUMWorkQueueCreationTest
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Harshith                     30706              03/16/2016                    Original Version
* Sharan Shanmugam                                10/20/2016                    Defect fix #245745.0001 
* Suresh Sunkari		                          09/20/2018                    Modified for Req- 374839   
* Subhamay Mandal              74579              04/29/2021                    User Story 2230059: T1PRJ0001827- IR 5050 - Add SLA fields in New - Work Queue Setup (CRM)
* Prudhvi Pamarthi                                05/05/2021                    US#2212581 Checkmarx error fix
* Bhumika Dodiya								  07/22/2021					T1PRJ0001827 - IR 5050 - Tech Debt - Clean up switch for past releases(CRM) - clean up of US2230059
* Tummala Vijaya Lakshmi						  08/27/2021				    USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App	
* Prasuna Pattabhi      						  05/06/2022				    USER STORY 3044995  -T1PRJ0272555 - INC1518553 - CRM Business Configuration Tool - hitting SF Limit - Unable to Edit Provider Payment Integrity Queue
****************************************************************************************************************************************************/

public with sharing class WorkQueueCreation_C_HUM 
{
    private WorkQueueCreation_S_HUM sLayer = new WorkQueueCreation_S_HUM();
    public Set<String> setWQnames = new Set<String>();
    public Set<String> setServiceCenterDepartments = new Set<String>();
    public Set<String> setDepartments = new Set<String>();
    public List<Work_Queue_Junction__c> lstWqJunction = new List<Work_Queue_Junction__c>();
    public Work_Queue_Setup__c wqs = new Work_Queue_Setup__c();
    public Group queueGroupRecord  = new Group();
    public List<QueueSobject> queueRecordList = new List<QueueSobject>();
    public List <Queue_View__c> uniqueQV = new List <Queue_View__c>();
    public PageReference pg = NULL;
    public Work_Queue_Priority__c  wqPriority  = new Work_Queue_Priority__c();
    public Boolean bEditBoolean = false;
    public Boolean bErrorQueueView = false;
    public Boolean bErrorQueueViewDisplay = false;
    public Boolean bqvAdd = false;
    public set<String>  groupDeveloperPublicGroupPGV = new set<String>();
    public set<String> uniqueTopic = new set<String>();
    public Boolean bErrorBoolean = false;
    public Boolean bErrorBooleanPriority = false;
    public Boolean bpriority = false;
    public List<Work_Queue_Setup__c> lstWorkQueue = new List<Work_Queue_Setup__c>();
    public List<Work_Queue_Setup__c> lstunique = new List<Work_Queue_Setup__c>();
    public List<Work_Queue_Setup__c>  lstuniqueDept = new List<Work_Queue_Setup__c>();
    public transient List<Work_Queue_Setup__c> lstwqsetup = new List<Work_Queue_Setup__c>();
    public boolean isEdit = false;
    public boolean bIsEdit = false;
    public boolean bFlagSCANDD = false;
    public boolean bFlagD = false;
    public boolean bFlagWQ = false;
    public boolean bEditMessageFlag = false;
    public Set<String> setUniqueViews = new Set<String>();
    public Set<String> setUniquePGVs = new set<String>();
    public String sCurrentRecordPriority ;    
     
    
    
    /**
       * invokes service layer for constructing the variables.
       * <p>
       * Constructor Method to for the class , Initialize variables
   */
    public WorkQueueCreation_C_HUM(ApexPages.StandardController controller)
    {
        workQueueSetup = (Work_Queue_Setup__c)controller.getRecord();
        initialize();
        sLayer.construct(this);
        selectedSC = workQueueSetup.Service_Center__c;
        selectedDept = workQueueSetup.Department__c;
        oldServiceCenter = workQueueSetup.Service_Center__c;
        oldDepartment  = workQueueSetup.Department__c;
        if(ApexPages.currentPage().getParameters().get('id') != Null){
            WQID = ApexPages.currentPage().getParameters().get('id');
            newPGVName();
            if(Schema.sObjectType.Work_Queue_Setup__c.fields.Classification_Type__c.isUpdateable())
            workQueueSetup.Classification_Type__c = Null ;
            if(Schema.sObjectType.Work_Queue_Setup__c.fields.Queue_Manager__c.isUpdateable())
            workQueueSetup.Queue_Manager__c = Null ;
            if(Schema.sObjectType.Work_Queue_Setup__c.fields.Queue_Description__c.isUpdateable())
            workQueueSetup.Queue_Description__c ='';
            workQueueSetup.SLA_In_Days__c = 0 ;
            workQueueSetup.SLA_On_Case_Transfer__c = false;
        }
        
    }  
    
    public Work_Queue_Setup__c workQueueSetup { get; set; }
    public Work_Queue_Topic__c workQueueTopic { get; set; }
    public Group queuePublicGroup { get; set; }
    public List<Work_Queue_Topic__c> lstTopicName { get; set; }
    public Queue_View__c queueView { get; set; }
    public Queue_View_Item__c queueViewItem { get; set; }
    public String selectedSC { get; set; }
    public String sQVType { get; set;}
    public List<SelectOption> lstWQSC { get; set; }
    public List<SelectOption> lstWQI { get; set; }
    public List<Queue_View_Item__c> lstQVI { get; set; }
    public List<String> topicNames { get; set; }
    public List<SelectOption> lstWQDept { get; set; }
    public String removedTopic { get; set; }
    public Map<String,Id> qvNameToManager { get; set; }
    public Map<String,String> qvNameToManagerName { get; set; }
    public List<SelectOption> lstSecurityGroups { get; set;}
    public String securityGroupSelected { get; set; }
    public Boolean enableGeneralIndicator { get; set; }
    public Boolean enableHOMedicalIndicator { Get; Set; }
    Public Boolean enableHODentalIndicator { Get; Set;}
    Public Boolean enableContractProtectedDataIndicator { Get; Set; }
    Public String selectedDept { get; set; }
    public String newServiceCenterValue { get; set; }
    public String newDepartmentValue { get; set; }
    public Boolean displaySCInput { get; set; }
    public Boolean displayDeptInput { get; set; }
    public String wqPriorityValue { get; set; }
    public Boolean queueViewTable { get; set; }
    public Group publicGroupRecord{ get; set; }
    public Boolean workQueueCreationError { get; set; }
    public Boolean publicGroupCreationError {get; set;}
    public Boolean enableTopicsSection {get; set;}
    //To store Work Queue Priority
    public Work_Queue_Priority__c wqFP{get;set;}
     //To show wqCriteria Values
    public Work_Queue_Priority__c wqCriteriaValues {get; set;}
     //For storing Priority Numbers
    public List<string> priorityIntegers{get;set;}
    public Case cs {get; set;}
    public String sPGVName { get; set; }
    public Map<String,String> mapQueueViewNameToPGVName { get; set; }
    public boolean bDetailViewFlag { get; set; }
    public boolean bEditQueueViewFlag { get; set; }   
    Public string strNewGroupName{get;set;}
    public String oldServiceCenter { get; set; }
    public String oldDepartment { get; set; }
    public  List<Queue_View__C> qvlist { get; set;}
    public Id  WQID {get; set;}
  
     /**
    * <p>
    * This method is used for initializations.
    *
    * @param    null
    * @return   Void
    */ 
    private void initialize()
    {
        workQueueTopic = new Work_Queue_Topic__c();
        queuePublicGroup = new Group();
        lstTopicName = new List<Work_Queue_Topic__c>();
        queueView = new Queue_View__c();
        queueViewItem = new Queue_View_Item__c();
        selectedSC='';
        sQVType = '';
        lstWQSC = new List<SelectOption>();
        lstWQI = new List<SelectOption>();
        lstQVI = new List<Queue_View_Item__c>();
        topicNames = new List<String>();
        mapQueueViewNameToPGVName = new Map<String,String>();
        lstWQDept = new List<SelectOption>();
        removedTopic = '';
        qvNameToManager = new Map<String,String>();
        qvNameToManagerName = new Map<String,String>();
        lstSecurityGroups = new List<SelectOption>();
        setUniquePGVs = new Set<String>();
        securityGroupSelected = '';
        enableGeneralIndicator = False;
        enableHOMedicalIndicator = False;
        enableContractProtectedDataIndicator = False;
        selectedDept ='';
        sPGVName = '';
        newServiceCenterValue = '';
        sCurrentRecordPriority ='';
        newDepartmentValue = '';
        displaySCInput = false;
        displayDeptInput = false;
        wqPriorityValue = '';
        queueViewTable = false;
        publicGroupRecord  = new Group();
        workQueueCreationError = false;
        publicGroupCreationError = false;
        enableTopicsSection = false;
        wqFP = new Work_Queue_Priority__c();
        wqCriteriaValues = new Work_Queue_Priority__c();        
        cs= new Case();
        priorityIntegers = new List<String>();
        bDetailViewFlag= true;
        bEditQueueViewFlag= false;
        oldServiceCenter = '';
        oldDepartment  = '';
    }
    
    /**
    * <p>
    * This method used to get the security group
    *
    * @param    NULL
    * @return   Void
    */ 
    public void enableIndicators()
    {
        sLayer.enableIndicators(this);
    }
    
    /**
    * <p>
    * This method used to delete Group records.
    *
    * @param    NULL
    * @return   Void
    */ 
    public void deletePreviouslyCreatedGroupObjects()
    {
        sLayer.deletePreviouslyCreatedGroupObjects(this);        
    }
    
    /**
    * <p>
    * This method updates the Work Queue Name based on the service center and department values.
    *
    * @param    NULL
    * @return   Void
    */
    public void updateWorkQueueName()
    {
        sLayer.updateWorkQueueName(this);    
    }
    
    /**
    * <p>
    * This method updates the service center and department values.
    *
    * @param    NULL
    * @return   Void
    */
    public void updateServiceCenterAndDepartment()
    {
        sLayer.updateServiceCenterAndDepartment(this);    
    }
    
    /**
    * <p>
    * This method used to insert groups
    *
    * @param    NULL
    * @return   Void
    */
    public void createWorkQueueGroupPGQ()
    {
        sLayer.createWorkQueueGroupPGQ(this);   
    }
    
    
    /**
    * <p>
    * This method used to update groups
    *
    * @param    NULL
    * @return   Void
    */
    public void updateWorkQueueGroupPGQ()
    {
        bEditMessageFlag = false;
        
        if(selectedSC == null || selectedSC ==' '){
                bEditMessageFlag = true;
        }
        if(selectedSC == Label.WORKQUEUE_APP_ADDNEW_HUM  && (newServiceCenterValue == null || newServiceCenterValue == '')){
                bEditMessageFlag = true;
        }
        if(selectedDept == null || selectedDept == ' '){
                bEditMessageFlag = true;
        }
        if(selectedDept == Label.WORKQUEUE_APP_ADDNEW_HUM  && ( newDepartmentValue == null || newDepartmentValue == '')){
                bEditMessageFlag = true;
        }
        if(workQueueSetup.Name == null || workQueueSetup.Name == ''){
                bEditMessageFlag = true;
        }
        if(workQueueSetup.Public_Group_Name__c == null || workQueueSetup.Public_Group_Name__c == ''){
                bEditMessageFlag = true;
        }
        if(workQueueSetup.Classification_Type__c == null ){
                bEditMessageFlag = true;
        }
        if(securityGroupSelected == 'none'){
            bEditMessageFlag = true;
        }
        
        if(bEditMessageFlag != true){
            sLayer.updateWorkQueueGroupPGQ(this);
            if(bEditQueueViewFlag= true){
                updatepgv();
            }          
        }else {
             bEditMessageFlag = true;
        
        }
    }
    
        
    /**
    * <p>
    * This method used to insert records other than group.
    *
    * @param    NULL
    * @return   Void
    */
    public PageReference createWorkQueue()
    {
        bEditMessageFlag = false;
        if(workQueueSetup.SLA_In_Days__c > 400){
            ApexPages.addmessage(New ApexPages.message(ApexPages.severity.Error, Label.SLAErrorMsg));
            return null;
        }
        if(selectedSC == null || selectedSC ==' '){
                bEditMessageFlag = true;
        }
        if(selectedSC == Label.WORKQUEUE_APP_ADDNEW_HUM  && (newServiceCenterValue == null || newServiceCenterValue == '')){
                bEditMessageFlag = true;
        }
        if(selectedDept == null || selectedDept == ' '){
                bEditMessageFlag = true;
        }
        if(selectedDept == Label.WORKQUEUE_APP_ADDNEW_HUM  && ( newDepartmentValue == null || newDepartmentValue == '')){
                bEditMessageFlag = true;
        }
        if(workQueueSetup.Name == null || workQueueSetup.Name == ''){
                bEditMessageFlag = true;
        }
        if(workQueueSetup.Public_Group_Name__c == null || workQueueSetup.Public_Group_Name__c == ''){
                bEditMessageFlag = true;
        }
        if(workQueueSetup.Classification_Type__c == null ){
                bEditMessageFlag = true;
        }
        if(securityGroupSelected == 'none'){
            bEditMessageFlag = true;
        }
        
        if(bEditMessageFlag != true){
                return sLayer.createWorkQueue(this);
        }else {
             ApexPages.Message myMsg = New ApexPages.Message(ApexPages.Severity.ERROR,system.label.MANDATORYFIELDS_WORKQUEUE_HUM);
             ApexPages.addMessage(myMsg);
            return null;
        }
            
    }
    
    /**
    * <p>
    * This method used to add the topics.
    *
    * @param    NULL
    * @return   Void
    */    
    public void addTopic()
    {
        sLayer.addTopic(this); 
    }
    
    /**
    * <p>
    * This method used to remove the topics.
    *
    * @param    NULL
    * @return   Void
    */  
    public void removeTopicFromList()
    {
        sLayer.removeTopicFromList(this);
    }
    
    /**
    * <p>
    * This method used to add the Queue Views.
    *
    * @param    NULL
    * @return   Void
    */
    public void addQueueView()
    {
        sLayer.addQueueView(this);
    }
    
    /**
    * <p>
    * This method used to update the Queue Views.
    *
    * @param    NULL
    * @return   Void
    */
    public void updateQueueView()
    {
        sLayer.updateQueueView(this);
    }
    
        /**
    * <p>
    * This method used to Edit the Queue Views section.
    *
    * @param    NULL
    * @return   Void
    */
    
    public PageReference queViewEditFlag()
    {
        if((selectedSC == null || selectedSC ==' ') && (selectedDept == null || selectedDept == ' ')){
            ApexPages.Message myMsg = New ApexPages.Message(ApexPages.Severity.Info,system.label.PGVCHANGE_WORKQUEUE_HUM);
            ApexPages.addMessage(myMsg);
            return null;            
        }
        else if(selectedSC == oldServiceCenter && selectedDept == oldDepartment){
            ApexPages.Message myMsg = New ApexPages.Message(ApexPages.Severity.Info,system.label.PGVCHANGE_WORKQUEUE_HUM);
            ApexPages.addMessage(myMsg);
            return null;
        }
        else{
                         
            bDetailViewFlag= false;
            bEditQueueViewFlag= true;    
            return null;  
        }
        
    }
    
    /* Used to get new PGV name*/
    public void newPGVName()
    { 
        Work_Queue_Setup__c wqrss=[select Id,name,(SELECT Id, Name,  Group_Name__c, Public_Group_View_Id__c FROM Queue_Views__r) from Work_Queue_Setup__c where Id =: ApexPages.currentPage().getParameters().get('id')];   
         qvlist = wqrss.Queue_Views__r;      
    }
    
        /**
    * <p>
    * This method used to update the PGV's.
    *
    * @param    NULL
    * @return   Void
    */
    public void updatepgv()
    {    
        update qvlist;     
        updategrp();
        updatequeueviewserviceanddepartment();
    } 
    
        /**
    * <p>
    * This method used to update the PGV's in Group object.
    *
    * @param    NULL
    * @return   Void
    */
    private void updategrp()
    {      
        List<Group> lstGroup =new List<Group>();      
        set<Id> lst = new set<Id>(); // Get Public group ID     
        Queue_View__c oQV= new Queue_View__c();
        Map<id,Queue_View__c> mapQV = new Map<id,Queue_View__c>([Select id,name,Public_Group_View_Id__c,Group_Name__c from Queue_View__c where Work_Queue_Name__r.id =: ApexPages.currentPage().getParameters().get('id')]);
        /*Adding Public groups id to Map*/
        for( ID sID: mapQV.keyset() )
        {
            oQV = mapQV.get(sId); 
            lst.add(oQV.Public_Group_View_Id__c);
        }
        List<Group> lstGrp = [Select id,name,developername,type,lastmodifieddate from group where id IN: lst];      
        for(Queue_View__c q :qvlist)
        {
            for(Group g:lstGrp)
            {
                if(g.id==q.Public_Group_View_Id__c)
                {
                    if(Schema.sObjectType.Group.fields.name.isUpdateable())
                    g.name = q.Group_Name__c;
                    if(Schema.sObjectType.Group.fields.developername.isUpdateable())
                    g.developername = q.Group_Name__c.replace(' ','_');
                    lstGroup.add(g);
                }
            }        
        }
        update lstGroup;      
    }
    
        /**
    * <p>
    * This method used to update the service center and department in Queue views.
    *
    * @param    NULL
    * @return   Void
    */
    private void updatequeueviewserviceanddepartment()
    {
         List<Queue_View__c> listQView =new List<Queue_View__c>();
         List<Queue_View__c> listQV = new List<Queue_View__c>([Select id,name,Queue_Name__c,Service_Center__c, Department__c from Queue_View__c where Work_Queue_Name__r.id =: ApexPages.currentPage().getParameters().get('id')]);
         for(Queue_View__c q :listQV)
         {         
             if(Schema.sObjectType.Queue_View__c.fields.Service_Center__c.isUpdateable())
             q.Service_Center__c = selectedSC == Label.WORKQUEUE_APP_ADDNEW_HUM ? newServiceCenterValue : selectedSC;
             if(Schema.sObjectType.Queue_View__c.fields.Department__c.isUpdateable())
             q.Department__c = selectedDept == Label.WORKQUEUE_APP_ADDNEW_HUM ? newDepartmentValue : selectedDept;
             if(Schema.sObjectType.Queue_View__c.fields.Queue_Name__c.isUpdateable())
             q.Queue_Name__c = workQueueSetup.Name;             
             listQView.add(q);
                      
         }
         update listQView;
    }
}