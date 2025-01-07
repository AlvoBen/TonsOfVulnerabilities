/*****************************************************************************************
Apex class Name    : HUMTaskEditPageExtension
Version            : 1.0
Function           : Extension class for HUMTaskEditPage
Created On         : 09 May 2014
Test Class         : HUMTaskEditPageExtensionTest

Modification Log:
*   Developer                   Code Review     Date                         Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Avishek Roy                    17093             09/05/2014          Original Version -Extension for Task Edit page
*   SuryaKumari Medicherla         17093             17/06/2014          Added a Method to Redirect to Queueselection when user Trying to edit a Task
*   Mrudula Jaddu                  17093             01/07/2014          Changed the Service center and department functionality based on security
*   Mrudula Jaddu                  17267             08/12/2014          Modified savetask method for upsert of task
*   Mrudula Jaddu                  17930             09/10/2014          Added record type to task for edit of task navigation
*   Manish Kumar                   18081             09/23/2014          Code added for users not found functionality 
*   Mrudula Jaddu                  18121             09/23/2014          Code added to display users name when user clicks on edit button
*   SuryaKumari Medicherla         18590             10/16/2014          Code added to update OwnerOrQueue Field of Task. Defect:151415
*   SuryaKumari Medicherla         18900             10/31/2014           Code added to show error for non existing Queues Defect: 159481
*   Mrudula Jaddu                  19058             11/06/2014          Modified code for my task report
*   Mrudula Jaddu                  19256             11/14/2014          Modified code for Queue not existing error subsequent save
*   SuryaKumari Medicherla         19911             12/10/2014          Code modified to replace custom settings with custom object (REQ-72809)
*   SuryaKumari Medicherla         20161             01/01/2015          Code modified to remove duplicate topic values
*   SuryaKumari Medicherla         20518             01/26/2015          Code modified to fix defect:169292
*   SuryaKumari Medicherla         20855             02/11/2015          Code modified to fix defect:169682.0001 
*   Asha Deepika Koyyana           20967             02/17/2015          Code modified to fix defect 172574 , 172576.
*   SuryaKumari Medicherla         21223             02/26/2015          Code modified to fix Issue number 146 and business priority 0(not able to get users part of queues even after selecting service center and department)
*   Asha Deepika Koyyana           21642             13/03/2015          As per requirement 72838, updated the saveTask method  
*   SuryaKumari Medicherla         21614             18/03/2015          Alphabetical Order issue fix(Issue Number 41)
*   Kritika P Popat                21906             23/03/2015          Modified for REQ 82518(Lines Modified 63,92-109,650-658)
*   SuryaKumari Medicherla         22085             30/03/2015          Modified SaveTask Method for REQ-72835
*   SuryaKumari Medicherla                           10/04/2015          Code Modified for Defect : 179369.0001
*   SuryaKumari Medicherla                           10/04/2015          populateQueueName method added for Defect : 179399.0001  
*   Shikhar Mehta                                    08/23/2016          REQ - 95982    
*   Shikhar Mehta                                    08/30/2016          Defect# 239772   
*   Shikhar Mehta                                    09/09/2016          Defect# 240080   
*   Shikhar Mehta                                    09/13/2016          Rolled back the changes to show the inactive users in the user picklist                                
*   Kritika Popat                  40081             09/20/2016          Defect 241751
*   Kritika Popat                  38094             09/21/2016          CA Ticket - 5865603- Specialist is able to close a task not owned by them
*   Avinash Choubey                                  09/15/2016          code added to pass parenttaskID in URL,  REQ 252450 
*   vamsi kondragunta              40981             09/23/2016          Increased code coverage as the part of REQ 95982
*   Sharan Shanmugam                                 09/26/2016          Removed None from Topic dropdown as part of REQ - 02442
*   Harika Devi Kalidindi          42104             10/04/2016          REQ - 283105 : showing only Active users names on case/task transfer in a drop-down
*   Shikhar Mehta                                    10/26/2016          REQ - 290098 MF3 RollBack
*   Sharan Shanmugam                                 11/07/2016          Added logic to pick only active queues.
*   Avinash Choubey                46943             11/08/2016          Fix for Defect - 247601
*   Harshith Mandya                                  02/21/2017          REQ - 301510 : CA Ticket - Fixing the OWner Queue, Service Center and Department Mismatch
*   Harshith Mandya                                  03/30/2017          Fix for Incident - 6693237
*   Gargey Sharma                  81181             04/01/2017          REQ - 306359 : Updates as per CTCI model changes
*   Shreya Choodamani              90487             04/28/2017          REQ#: 311397 Updated the code to mimic the behavior of CCSpecilist for HP Specialist
*   Prudhvi Pamarthi               154304            01/19/2018          REQ - 328929 Fix for HUMExceptionHelper: DML
*   Pallavi Ravishankar            268909            31/07/2018          REQ 361926 - Work Task Type'Extension Request' added to the existing Task Type   
*   Anurag Shah                                      24/08/2018          Fix for Defect#379982- The “Extension Request” Task has a dependent Task of Type ”Work Task”.
*   Amar Gadewar                                     04/18/2019          REQ - 396424 - Quality - RCC - Medicare ID field on Task Edit and Task Detail page (RF)    
*   Santhosh Ganji                                   19/22/2019          REQ - 379035 : MF 8-- Change Queue Owner page: limit the insurance WQV selections for HP profile   
*   Santhi Mandava                                   04/15/2019          Replaced "Member_Id_Value__c" with Name
*   Amar Gadewar                  358979             06/12/2019          REQ - 399947 - Quality - RCC - Medicare ID field on Task Edit And Task Detail page (CRM)
*   Vinay Lingegowda                                 07/11/2019          REQ - 403790 AHT - All Users - Add Assign to Me button on the Task Edit page (RF)
*   Samantha Tennyson                                02/05/2021          User Story 1875448: PR00091574 - MF 1 - Quality - Ability to capture additional information related to Notification Task (RF)
*   Mohammed Noor                                    03/03/2021          USER STORY 1730149 - Remove Topic on Task Transfer section in CRM when deactivated from Work Queue
*   Mohammed Noor                                    03/18/2021          USER STORY 1730149 - Add Functionality On/Off switch
*   Pooja Kumbhar                                    04/08/2021          User story 2163293: T1PRJ0002736 / PR00094232 - 2021 MF6 - Case Management Checkmarx Security violations - Task Edit 
*   Samantha Tennyson                                06/01/2021          User Story 2325471: T1PRJ0001827 - MF 1 - AHT - Ability to prefill queue details on Task Edit page (CRM)
*   Samantha Tennyson                                06/17/2021          User Story 2369230: T1PRJ0001827 - MF 1 - Quality - Ability to capture additional information related to Notification Task (CRM)
*   Samantha Tennyson                                06/22/2021          User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM)
*	Vamshi Krishna Pemberthi						 08/13/2021			 User Story 2548465: T1PRJ0002736 / PR00094232 - 2021 MF6 - Case Mgt Checkmarx sf-main/src/classes/HUMTaskEditPageExtension.cls
*   Anil Parvathaneni				                 10/18/2022			 User Story 3176163 : Create New Task Type and fields for Market Credentialing
********************************************************************************************************************************/

public with sharing class HUMTaskEditPageExtension
{
    @testVisible private Task oTask = new Task();
    @testVisible private String sWhatId = '';
    private string sTaskNumber = '';
    private String sSelectedQueue = '';
    @testvisible   private String sTaskOwnerValue = ''; 
    private String sTaskStatus = '';  
    private Case oRelCase = new Case();
    @testVisible private Set<string> setServiceCenter = new Set<string>(); 
    private Set<string> setDepartment = new Set<string>();
    private Integer iLimitValue = 1000;
    @testVisible private Map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new Map<String, Work_Queue_Setup__c>([Select Name,id, Classification_Type__c, Classification_Type__r.Name, CokY__c, Department__c, DST__c, General__c, HO_Dental__c, HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c from Work_Queue_Setup__c where IsActive__c = TRUE limit :iLimitValue]); 
    @testVisible   private Boolean bGroupexists = false;
    private Work_Queue_Setup__c oWorkQueSetup = NULL;
    private String sOldTaskNumber = '';
    private String sOldOwnerQueue = '';
    @testVisible   private Boolean bShowError = false;  
    private String sParentTaskID = '';
    public Task_Field_History__c oTaskFH = new Task_Field_History__c ();
    @testVisible private Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueueSetup;
    public String sExtRequest = HumConstants.HUMExtensionTask; 
    public Boolean bShowTaskEndDate{get;set;}
    public boolean bCredentialingswitch{get;set;}
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
    public String sMedicareID {get; set;}
    /*
     *<p>    
     * Constructor Method for the class, Initializes Task Records and other variables
     */
     
    public HUMTaskEditPageExtension(ApexPages.StandardController oController)
    {
        try
        {   
            if(!Test.isRunningTest()) {
                oController.addFields(new List<String>{'Call_Back_Start__c','Call_Back_End__c','Callback_Requested__c'});   
            }
            oTask = (Task)oController.getRecord();
            bCredentialingswitch = HUMUtilityHelper.isCRMFunctionalityON('3176163');
            mapServCenDeptWorkQueueSetup = new Map<String,Work_Queue_Setup__c>();
            bDisablefield = false;
            bDisableStatus = false;
            disableFields();
            bVisible = true;
            setSelectedQueue();
            setTaskFieldsData();
            bShowPanel = false;
            bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM ? false : true;  
            DateTime NOW = datetime.now();
            Datetime dt = Datetime.newInstance(NOW.year(), NOW.month(), NOW.day(), 8, 00, 0);
            lstTimeInput = new List<String>();
            for (Integer i = 0;  i < 23; i++) {
                lstTimeInput.add(dt.format('hh:mm a'));
                dt = dt.addMinutes(30);
            }  
            If(String.IsNotBlank(oTask.WhatId)){
                
                List<Case> lstCase = [select id,Owner_Queue__c, 
                                  Classification_Id__r.name , Intent_Id__r.name
                                 from Case
                                 Where Id=:oTask.WhatId];
                
                IF(!lstCase.isEMpty()){
                        bShowTaskEndDate = (lstCase[0].Classification_Id__r.name == 'Provider Requests' && 
                        lstCase[0].Intent_Id__r.name == 'LA Medicaid Cred Workflow' &&
                        lstCase[0].Owner_Queue__c == 'Market National Network Ops')? true:false;
                        
                }
                /*
                String sTaskStatus = [select Status from Task where Id =: oTask.Id].Status;
                if(sTaskStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM))
                {
                    bDisableStatus = true;
                } 
                 */
            }             
        } 
        catch(Exception e) 
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(e, 'HUMTaskEditPageExtension', 'HUMTaskEditPageExtension');
        }
    }
    
    public string sUserName {get; set;}
    public boolean bShowPanel {get; set;}
    public String sServiceValue {get; set;}
    public String sDepartValue{get; set;}
    public String sTopicValue{get; set;}    
    public string sSectionHeader {get; set;}
    public string sSectionHeaderForTitle {get; set;}
    public string sUserID {get; set;}
    public String sQueueName{get; set;}
    public boolean bShowTopic {get; set;}
    public Boolean bVisible {get;set;}
    public string sComments {get;set;}
    public Boolean bDisablefield {get;set;}
    public Boolean bDisableStatus {get;set;}
    public Case oCaseNum {get;set;}
    public Boolean bIsError{get;set;}
    public String sPublicGroupName{get;set;}
    public string sTaskTypeParam{get;set;}
    Public String sParentId {get;set;}   
    public Boolean bIsPharmacyUser {get;set;}
    public List<String> sCenters = new List<String>{GLOBAL_CONSTANT_HUM.SERVICE_CENTER_HUMANA_PHARMACY,GLOBAL_CONSTANT_HUM.SERVICE_CENTER_RX};
    public List<String> wqueues = new List<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public List<String> wTopics = new List<String>{GLOBAL_CONSTANT_HUM.TOPIC_SPECIAL_INVESTIGATIONS,GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE,GLOBAL_CONSTANT_HUM.NONE_HUM};
    public List<String> lstTimeInput { get; set; } 
    public String sStartCallTime{ get; set; } 
    public String sEndCallTime{ get; set; } 

 /**    
   * <p>    
   * This Method is used to disable fields depending on status and profile.    
   *    
   * @param    null    
   * @return   Void    
   */      
        public void disableFields()
    {
        if(NULL != oTask && (!String.isEmpty(oTask.id)))
        {
            sTaskStatus = [select Status from Task where Id =: oTask.Id].Status;
            if(sTaskStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM))
            {
                bDisablefield = true;
                if(checkUserProfile())
                {
                    bDisableStatus = true;    
                }
            }
        }
    }
     

 /**    
   * <p>    
   * This Method is used to set task Data based on current queue. 
   *    
   * @param    null    
   * @return   Void    
   */   
 
    @testvisible private void setSelectedQueue()
    {
        sSelectedQueue = [select Current_Queue__c from User where Id= :Userinfo.getUserId()].Current_Queue__c ;
        if(string.isBlank(sSelectedQueue)) return;
        sServiceValue = '';
        sDepartValue = '';
        sTopicValue = '';
        sUserName = ''; 
        sUserID = '';
    }
    
   /**    
     * <p>    
     * This Method is used to set data for a task in two ways, one for task edit and another for new task.
     *    
     * @param    null    
     * @return   Void    
     */ 
     
    @testvisible private void setTaskFieldsData()
    {
        Task objTask = null;  
        String sKeyPrefix = '';    
        if(NULL != oTask && NULL != oTask.id)    
        {
            objTask = [select Service_Center__c,QueueOrUserId__c, Department__c, RecordTypeId, Owner_Queue__C, Topic__C,OwnerId, Task_Number__c,Task_Owner__C, WhatId, Call_Back_Start__c,Call_Back_End__c,Callback_Requested__c from Task where Id =: String.escapeSingleQuotes(oTask.Id)];
       
            if(objTask.QueueOrUserId__c!=null) sKeyPrefix = objTask.QueueOrUserId__c.subString(0,3);
        if(objTask.QueueOrUserId__c != Userinfo.getUserId() && User.sObjectType.getDescribe().getKeyPrefix().equals(sKeyPrefix))
        {
            if(checkUserProfile())
            {
                bVisible = true;
                bShowError = true;
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.TASKEDIT_TRANSFERORCLOSE_HUM));    
            }
        }
        }
        sWhatId = (objTask != NULL? objTask.WhatId: ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_WHAT_ID));
        sParentTaskID = (objTask == NULL? ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_PARENT_TASKID):'');
        sSectionHeader = (objTask != NULL? GLOBAL_CONSTANT_HUM.CASEMGMT_TASK_EDIT_PAGE_HUM : GLOBAL_CONSTANT_HUM.CASEMGMT_NEW_TASK_HUM);
        sSectionHeader = (bDisableStatus? GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM : sSectionHeader);
        sSectionHeaderForTitle = (objTask != NULL? objTask.Task_Number__c: GLOBAL_CONSTANT_HUM.CASEMGMT_NEW_TASK_HUM);
        sTaskNumber = (objTask != NULL? objTask.Task_Number__c: '');
        sServiceValue  = (objTask != NULL? objTask.Service_Center__c: '');
        sDepartValue = (objTask != NULL? objTask.Department__c: '');
        sTopicValue = (objTask != NULL? objTask.Topic__c: '');
        fetchCaseDetails(sWhatId);     
        sTaskOwnerValue = (objTask != NULL? objTask.Owner_Queue__C:'');
        if(!String.isBlank(sTaskOwnerValue))    sQueueName =  sTaskOwnerValue.replace(' ','_');
        bShowTopic = (objTask != NULL? false: true);
        if(Schema.SObjectType.Task.fields.RecordTypeId.isCreateable() || Schema.SObjectType.Task.fields.RecordTypeId.isUpdateable()) oTask.RecordTypeId = (objTask != NULL? objTask.RecordTypeId: ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.CASEMGMT_RECORDTYPE)); 
        if(Schema.SObjectType.Task.fields.WhatId.isCreateable() || Schema.SObjectType.Task.fields.WhatId.isUpdateable()) oTask.WhatId = sWhatId; 
        if(objTask != NULL)
        {
            if(objTask.Task_Owner__C != objTask.Owner_Queue__C)
            {
                 sUserName = objTask.Task_Owner__C;
                 sOldTaskNumber = objTask.Task_Owner__C;
                 sOldOwnerQueue = objTask.Owner_Queue__C;
            }
            sUserID = objTask.OwnerId;
        }       
        // REQ 361926 - To check if existing Tasks have parentId or not
        if(NULL != oTask && NULL != oTask.id)    
        {
        List<HUM_Task_Heirarchy__c> lstparentTaskID = [select HUM_Parent_TaskID__c  FROM HUM_Task_Heirarchy__c where (HUM_Dependent_TaskID__c 
                                                       =: String.escapeSingleQuotes(oTask.id) AND  HUM_Parent_TaskID__c != null) OR HUM_Parent_TaskID__c =: String.escapeSingleQuotes(oTask.id) limit 1];  
        
           
          if( lstparentTaskID != null && lstparentTaskID.size() > 0)
          {
             sParentId =lstparentTaskID[0].HUM_Parent_TaskID__c;              
          }
          }
    if(null != oTask.Call_Back_Start__c) {
        Datetime startDateTime = Datetime.newInstance(Date.today(), oTask.Call_Back_Start__c);
        sStartCallTime = startDateTime.format('hh:mm a');
    }

    if(null != oTask.Call_Back_End__c) {
        Datetime endDateTime = Datetime.newInstance(Date.today(), oTask.Call_Back_End__c);
        sEndCallTime = endDateTime.format('hh:mm a');  
    }      
    }
    
   /**    
     * <p>    
     * This method is used to check logged user profile
     *    
     * @param    null    
     * @return   Boolean    
     */      
    public Boolean checkUserProfile()
    {
        Id userProfileId = userinfo.getProfileId();
        String sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();                                      
        Id CCSProfileId = [Select id from Profile where Name =: GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_CCSPECIALISTPROFILE_HUM].id;
        If ((userProfileId == CCSProfileId) || (sPharmacyUser == system.label.PharmacyUserSpecialistYes_HUM))
        {
            return true;
        }
        return false;
    }
  

   /**    
     * <p>    
     * This Method is used to set page parameters for 'NextPage' based on user workqueue value and redirects to
       next page when user trying to Edit a Task.
     * @param    null    
     * @return   PageReference    
     */      
    public PageReference reDirectPage()
    {
        try
        {
            Id sTaskId = ApexPages.CurrentPage().getParameters().get(GLOBAL_CONSTANT_HUM.ID_HUM);
            if(!string.isBlank(sTaskId))    sTaskNumber = [select Task_Number__c From Task Where Id = :sTaskId].Task_Number__c ;
            String sNextPage = URL.getSalesforceBaseUrl().toExternalForm() + '/' + sTaskId;
            
            if(String.isBlank(sSelectedQueue))
            {
                PageReference pageRef = new PageReference(GLOBAL_CONSTANT_HUM.CASECLONE_QUEUE_SEL_PAGE_HUM);
                pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASECLONE_NEXT_PAGE_HUM, GLOBAL_CONSTANT_HUM.CASEMGMT_TASKEDITPAGE_HUM);
                pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.ID_HUM, sTaskId);
                pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASECLONE_OBJECT_TYPE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM);
                pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEMGMT_WHATID, sWhatId);
                pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEMGMT_TASK_NUMBER_HUM, sTaskNumber);
                
                if(!String.isBlank(sTaskId))    pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEMGMT_ERROR, GLOBAL_CONSTANT_HUM.CASEMGMT_YESFOREDIT_HUM);
                return pageRef;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMTaskEditPageExtension', 'reDirectPage');
        }
        return null;
    }
    
   /**    
     * <p>    
     * This is used to render panel in page
     * @param    null    
     * @return   Void    
     */     
    public void doRender()
    {
        try
        {   
            bShowPanel = true;
             
            if(String.isBlank(sTaskOwnerValue)) return;
            
            String sQueueValue = oTask.Owner_Queue__c;    
            sQueuevalue = sQueueValue.replace(' ', '_');
            Work_Queue_Setup__c oWQS = [select Service_Center__c, Department__c from Work_Queue_Setup__c where Work_Queue_Name__c =: sQueueValue AND IsActive__c = TRUE]; 
            if(oWQS != NULL)
            {
                sServiceValue  = oWQS.Service_Center__c; 
                sDepartValue  =  oWQS.Department__c;    
            } 
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMTaskEditPageExtension', 'doRender');
        }        
    } 
    
   /**    
     * <p>    
     * This method is used to get all the service centers
     * @param    null    
     * @return   list<Selectoption>
     */    
      
    public list<Selectoption> getLstServiceCenter()
    {   
        if(NULL != sWhatId)   oRelCase = [Select Recordtype.Name,Tenant_Id__c, CoKY_Case__c, HO_Dental_Case__c, DST_Case__c, HO_Medical_Case__c, General_Case__c,Contract_Protected_Case__c from case where id =: sWhatId];
        setServiceCenter = new Set<String>();
        List<SelectOption> lstOptions = new List<SelectOption>();
        List<String> lstServiceValues = new List<String>();
        try
        {
            for(String sWorkQueue : mapWorkQueueSetup.keySet()) 
            {
                if(GetCaseIndicatorStatus(sWorkQueue)) 
                {
                  if(!bIsPharmacyUser)
                  {    
                    setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c);
                    mapServCenDeptWorkQueueSetup.put(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c +'#' + mapWorkQueueSetup.get(sWorkQueue).Department__c,mapWorkQueueSetup.get(sWorkQueue));
                  }
                  else
                  {  
                    if(wqueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || sCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c))
                    {
                      setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c);
                      mapServCenDeptWorkQueueSetup.put(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c +'#' + mapWorkQueueSetup.get(sWorkQueue).Department__c,mapWorkQueueSetup.get(sWorkQueue));
                    }
                  }
                }
            }
            lstServiceValues = HUMUtilityHelper.doSort(setServiceCenter);
            for(string sSCName : lstServiceValues)   
            { 
                lstOptions.add(new SelectOption(sSCName, sSCName));
            }
            if(lstOptions.size() != 0)
            {
                lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));
            }
            else
            {
                lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));    
            }
            return lstOptions;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMTaskEditPageExtension', 'getLstServiceCenter');
        }
        return null;
    }
    

   /**    
     * <p>    
     * This method is used to check the case selector indicator
     * @param    String    
     * @return   Boolean
     */
     
    public boolean getCaseIndicatorStatus(String sQueueName)
    {
        return (mapWorkQueueSetup.get(sQueueName).General__c && oRelCase.General_Case__c ||
                mapWorkQueueSetup.get(sQueueName).General__c && oRelCase.Contract_Protected_Case__c == '1' ||
                mapWorkQueueSetup.get(sQueueName).DST__c && oRelCase.DST_Case__c ||
                mapWorkQueueSetup.get(sQueueName).HO_Dental__c && oRelCase.HO_Dental_Case__c ||   
                mapWorkQueueSetup.get(sQueueName).HO_Medical__c && oRelCase.HO_Medical_Case__c ||
                mapWorkQueueSetup.get(sQueueName).CokY__c &&  oRelCase.CoKY_Case__c) &&
                NULL != mapWorkQueueSetup.get(sQueueName).TenantID__c && NULL != oRelCase.Tenant_Id__c &&
                mapWorkQueueSetup.get(sQueueName).TenantID__c.equals(oRelCase.Tenant_Id__c);        
    }
     

   /**    
     * <p>    
     * This method is used to get all the Departments based on service center selected
     * @param    null    
     * @return   list<Selectoption>     
     */
    
    public list<Selectoption> getLstDepartment()
    {
        try
        {
            setDepartment = new Set<String>();
            List<SelectOption> lstOptions = new List<SelectOption>();
            List<String> lstDepValues = new List<String>();
            for (String serviceCenter : setServiceCenter)
            {
                if(serviceCenter.equalsIgnoreCase(sServiceValue))
                {
                    for(string sWQS : mapWorkQueueSetup.keySet())
                    {
                        if(mapWorkQueueSetup.get(sWQS).Service_Center__c.equalsIgnoreCase(sServiceValue) && !string.isBlank(mapWorkQueueSetup.get(sWQS).Department__c))
                                if(GetCaseIndicatorStatus(sWQS))
                                {
                                  if(!bIsPharmacyUser)
                                  {     
                                     setDepartment.add(mapWorkQueueSetup.get(sWQS).Department__c);
                                  }
                                  else
                                  {
                                    if(wqueues.contains(mapWorkQueueSetup.get(sWQS).Name) || sCenters.contains(mapWorkQueueSetup.get(sWQS).Service_Center__c))
                                    { 
                                       setDepartment.add(mapWorkQueueSetup.get(sWQS).Department__c);
                                    }
                                  }
                                }
                    }
                    break;
                }
            }
            lstDepValues = HUMUtilityHelper.doSort(setDepartment);
            // Create the Select lstOptions.
            for(String sName : lstDepValues)
            {
                lstOptions.add(new SelectOption(sName, sName));
            }
            if(lstOptions.size() == 0)
            {
                lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));
            }
            else
            {
                lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));    
            }
            return lstOptions;
        }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMTaskEditPageExtension', 'getLstDepartment');
        }     
        return null;   
    }
    

   /**    
     * <p>    
     * This method is used to get all the topics based on Department selected
     * @param    null    
     * @return   list<Selectoption>     
     */    

    public list<Selectoption> getLstTopics()
    {
        try
        {
            Set<String> setTopic = new Set<String>();
            List<SelectOption> lstOptions = new List<SelectOption>();
            List<String> lstTopicValues = new List<String>();
            lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));
            String sWorkQueueSetUpName;
            if((NULL == sServiceValue && NULL == sDepartValue) || (!sServiceValue.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) 
                && sDepartValue.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM)))    return lstOptions;
            lstOptions.remove(0);
            for(string sWQS : mapWorkQueueSetup.keySet())
            {
                if(mapWorkQueueSetup.get(sWQS).Service_Center__c.equalsIgnoreCase(sServiceValue) && 
                   mapWorkQueueSetup.get(sWQS).Department__c.equalsIgnoreCase(sDepartValue))
                {
                    sWorkQueueSetUpName = sWQS;
                    break;
                }
            }
            
            list<Work_Queue_Junction__c> lstWorkQueueJunction = new list<Work_Queue_Junction__c>();
            
            if(HUMUtilityHelper.isCRMFunctionalityON('1730149'))
            {
                lstWorkQueueJunction = [Select Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__r.Name from Work_Queue_Junction__c where Work_Queue_Setup_Name__c =: sWorkQueueSetUpName
                                                                AND Work_Queue_Topic_Name__r.IsActive__c = true];
            }
            else
            {                                                   
                lstWorkQueueJunction = [Select Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__r.Name from Work_Queue_Junction__c where Work_Queue_Setup_Name__c =: sWorkQueueSetUpName];
            }
            
            if(lstWorkQueueJunction!=null && !lstWorkQueueJunction.isEmpty())
            {
                for(Work_Queue_Junction__c oWQJ : lstWorkQueueJunction)
                {
                     if(!bIsPharmacyUser) 
                     {  
                       setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                     }
                     else
                     {
                        if(sCenters.contains(sServiceValue))
                        {
                           setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                        }
                        else
                        {
                           if(wTopics.contains(oWQJ.Work_Queue_Topic_Name__r.Name)) 
                           {
                             setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                           }
                        }
                     }                       
                }
                lstTopicValues = HUMUtilityHelper.doSort(setTopic);
                for(String sName : lstTopicValues)
                {    
                    lstOptions.add(new SelectOption(sName, sName));
                }
            } 
            
            if(!setTopic.isEmpty())
            {
                lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));
                bShowTopic = false;
            }
            else
            {
                lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));
                bShowTopic = true;
            }  
            return lstOptions;
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'HUMTaskEditPageExtension', 'getLstTopics');             
        }
        return null;
    }

   /**    
     * <p>    
     * This method is used to set sDepartValue to null .     
     * @param    null    
     * @return   Void     
     */

    public void populateDepartmentName()
    {
        sDepartValue = '';      
    }
    

   /**    
     * <p>    
     * This Method is used to assign values to task record.     
     * @param    null    
     * @return   Void     
     */

    @testvisible private void assignValuesToTask()
    {
        String sGrpName;
        bGroupexists = false; 
        if(mapServCenDeptWorkQueueSetup.containsKey(sServiceValue + '#' + sDepartValue))
        {
            oWorkQueSetup = mapServCenDeptWorkQueueSetup.get(sServiceValue + '#' + sDepartValue);
        }
        if(sTaskOwnerValue != NULL)
        {
            SGrpName = sTaskOwnerValue;
            sGrpName = sGrpName.replace('_',' ');
        }
        
        Group oGrp;
        
        if(!string.isBlank(oTask.Task_Owner__c) && !sServiceValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && string.isBlank(sUserName))
        {   
            try
            {
                List<Group> lstGrp;
                if(!string.isBlank(sGrpName))    lstGrp = [Select Id, Name from Group Where Name =: sGrpName LIMIT 1]; 
        if(Test.isRunningTest()) lstGrp = [Select Id, Name from Group LIMIT 1];
                if(lstGrp.size() != 0) 
                {
                   oGrp = lstGrp[0];
                }
                else
                {
                    bGroupexists = true;
                }
            }
            catch(Exception ex)
            {
                 HUMExceptionHelper.logErrors(ex, 'HUMTaskEditPageExtension', 'assignValuesToTask');
            }
            
            if (NULL != oGrp)
            {
                if(Schema.SObjectType.Task.fields.Owner_Queue__c.isCreateable() || Schema.SObjectType.Task.fields.Owner_Queue__c.isUpdateable()) oTask.Owner_Queue__c = oGrp.Name; 
                if(Schema.SObjectType.Task.fields.Task_Owner__c.isCreateable() || Schema.SObjectType.Task.fields.Task_Owner__c.isUpdateable()) oTask.Task_Owner__c = oGrp.Name; 
                if(Schema.SObjectType.Task.fields.QueueOrUserId__c.isCreateable() || Schema.SObjectType.Task.fields.QueueOrUserId__c.isUpdateable()) oTask.QueueOrUserId__c = oGrp.Id; 
                if(Schema.SObjectType.Task.fields.OwnerID.isCreateable() || Schema.SObjectType.Task.fields.OwnerID.isUpdateable()) oTask.OwnerID = Userinfo.getUserId(); 
                if(Schema.SObjectType.Task.fields.Service_Center__c.isCreateable() || Schema.SObjectType.Task.fields.Service_Center__c.isUpdateable()) oTask.Service_Center__c = sServiceValue; 
                if(Schema.SObjectType.Task.fields.Department__c.isCreateable() || Schema.SObjectType.Task.fields.Department__c.isUpdateable()) oTask.Department__c = sDepartValue; 
                if(!String.isBlank(sTopicValue) && !sTopicValue.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
                {
                    if(Schema.SObjectType.Task.fields.Topic__c.isCreateable() || Schema.SObjectType.Task.fields.Topic__c.isUpdateable())oTask.Topic__c = sTopicValue; 
                }
                else if(String.isBlank(sTopicValue) || sTopicValue.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
                {
                    if(Schema.SObjectType.Task.fields.Topic__c.isCreateable() || Schema.SObjectType.Task.fields.Topic__c.isUpdateable())oTask.Topic__c =  '';   
                }
            }
            else
            {
                 ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.Label.TASKEDIT_QUEUENOTEXISTS_HUM)); 
            }
        }
        else if(!string.isBlank(oTask.Task_Owner__c) && !sServiceValue.containsIgnoreCase(System.label.TASKEDIT_NONE_HUM))
        {
            if(Schema.SObjectType.Task.fields.Owner_Queue__c.isCreateable() || Schema.SObjectType.Task.fields.Owner_Queue__c.isUpdateable())oTask.Owner_Queue__c = sGrpName; 
            if(Schema.SObjectType.Task.fields.Task_Owner__c.isCreateable() || Schema.SObjectType.Task.fields.Task_Owner__c.isUpdateable())oTask.Task_Owner__c = sUserName; 
            if(Schema.SObjectType.Task.fields.Service_Center__c.isCreateable() || Schema.SObjectType.Task.fields.Service_Center__c.isUpdateable())oTask.Service_Center__c = sServiceValue;  
            if(Schema.SObjectType.Task.fields.Department__c.isCreateable() || Schema.SObjectType.Task.fields.Department__c.isUpdateable())oTask.Department__c = sDepartValue; 
            if(!String.isBlank(sTopicValue) && !sTopicValue.equalsIgnoreCase(System.label.TASKEDIT_SELECTTOPIC_HUM))
            {
                if(Schema.SObjectType.Task.fields.Topic__c.isCreateable() || Schema.SObjectType.Task.fields.Topic__c.isUpdateable())oTask.Topic__c = sTopicValue; 
            }
            else if(String.isBlank(sTopicValue) || sTopicValue.equalsIgnoreCase(System.label.TASKEDIT_SELECTTOPIC_HUM))
            {
                if(Schema.SObjectType.Task.fields.Topic__c.isCreateable() || Schema.SObjectType.Task.fields.Topic__c.isUpdateable())oTask.Topic__c =  '';   
            }
            if(sUserID != NULL && !String.isBlank(sUserID) )
            {
                if(Schema.SObjectType.Task.fields.OwnerID.isCreateable() || Schema.SObjectType.Task.fields.OwnerID.isUpdateable())oTask.OwnerID = sUserID; 
                if(Schema.SObjectType.Task.fields.QueueOrUserId__c.isCreateable() || Schema.SObjectType.Task.fields.QueueOrUserId__c.isUpdateable())oTask.QueueOrUserId__c = sUserID;  
            }
        }
    } 
    

   /**    
     * <p>    
     * This method is used to populate and set the owner field with either queue or user value     
     * @param    null    
     * @return   Void     
     */

    public void populateGroupName()
    {
        if(!String.isBlank(sUserName))    if(Schema.SObjectType.Task.fields.Task_Owner__c.isCreateable() || Schema.SObjectType.Task.fields.Task_Owner__c.isUpdateable()) oTask.Task_Owner__c = sUserName; 
    }
    
	/**    
     * <p>    
     * This method is used to check all the validation conditions are met before closing the Task
     * @param    null    
     * @return   Void     
     */
	public void validateCloseTask()
    {
        
        if(null == oTask.Task_Completion_Date__c || null == oTask.Credentialing_Task__c && bShowTaskEndDate){
            ApexPages.Message msg = new Apexpages.Message
            (ApexPages.Severity.Error,'You must select Credentialing Task and Completion date before closing the Task');
    		Apexpages.addMessage(msg);
            bIsError = true;
            return;
        }else{
            saveTask();
        }
                
    }
	
   /**    
     * <p>    
     * This method is used to check all the validation conditions are met and save the task with proper data
     * @param    null    
     * @return   Void     
     */

    public Void saveTask()
    {
        
		if((null == oTask.Task_Completion_Date__c || null == oTask.Credentialing_Task__c) && oTask.Status == 'Closed'  && bShowTaskEndDate){ 
            validateCloseTask();
            if(bIsError){
                return;
            }        
        }
		bIsError = false;
        Boolean UserNameMatched = true;
        try
        {
            assignValuesToTask();
			if(String.isNotBlank(sStartCallTime)) {
                if(Schema.SObjectType.Task.fields.Call_Back_Start__c.isCreateable() || Schema.SObjectType.Task.fields.Call_Back_Start__c.isUpdateable()) oTask.Call_Back_Start__c = parseTime(sStartCallTime); 
                if(null == oTask.Call_Back_Start__c ) {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.INVALIDFORMAT_STARTTIME_TASKEDIT_TASK_HUM));  
                    bIsError = true;
                    return;
                }
            }
			
            if(String.isNotBlank(sEndCallTime)) {
                if(Schema.SObjectType.Task.fields.Call_Back_End__c.isCreateable() || Schema.SObjectType.Task.fields.Call_Back_End__c.isUpdateable()) oTask.Call_Back_End__c = parseTime(sEndCallTime); 
                if(null == oTask.Call_Back_End__c ) {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.INVALIDFORMAT_ENDTIME_TASKEDIT_TASK_HUM));  
                    bIsError = true;
                    return;
                }
            }
                
            if(otask.Type.equalsIgnoreCase('Notification Task') && otask.Callback_Requested__c.equalsIgnoreCase('Yes')){ 
                if(String.isBlank(sStartCallTime) || String.isBlank(sEndCallTime)) {
                  ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.blankStartEndTime_TaskEdit_Task_HUM));  
                   bIsError = true;
                   return;     
                 }        
            }
                
            else if(otask.Type.equalsIgnoreCase('Notification Task') && otask.Callback_Requested__c.equalsIgnoreCase('No')){ 
                if(String.isBlank(sStartCallTime) || String.isBlank(sEndCallTime)) {
                    if(Schema.SObjectType.Task.fields.Call_Back_Start__c.isCreateable() || Schema.SObjectType.Task.fields.Call_Back_Start__c.isUpdateable()) oTask.Call_Back_Start__c = null; 
                    if(Schema.SObjectType.Task.fields.Call_Back_End__c.isCreateable() || Schema.SObjectType.Task.fields.Call_Back_End__c.isUpdateable()) oTask.Call_Back_End__c = null; 
                }
            }

            else if(!otask.Type.equalsIgnoreCase('Notification Task')){
                if(oTask.Callback_Requested__c != null || oTask.Time_Zone__c != null || oTask.Call_Back_Start__c != null || oTask.Call_Back_End__c != null){
                   if(Schema.SObjectType.Task.fields.Callback_Requested__c.isCreateable() || Schema.SObjectType.Task.fields.Callback_Requested__c.isUpdateable()) oTask.Callback_Requested__c = null; 
                   if(Schema.SObjectType.Task.fields.Time_Zone__c.isCreateable() || Schema.SObjectType.Task.fields.Time_Zone__c.isUpdateable()) oTask.Time_Zone__c = null; 
                   if(Schema.SObjectType.Task.fields.Call_Back_Start__c.isCreateable() || Schema.SObjectType.Task.fields.Call_Back_Start__c.isUpdateable()) oTask.Call_Back_Start__c = null; 
                   if(Schema.SObjectType.Task.fields.Call_Back_End__c.isCreateable() || Schema.SObjectType.Task.fields.Call_Back_End__c.isUpdateable()) oTask.Call_Back_End__c = null; 

                }
            }
        
          if(!otask.Type.equalsIgnoreCase(sExtRequest))
           {    
            if (string.isBlank(oTask.Task_Owner__c) && String.isBlank(sServiceValue))
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.TASKEDIT_CASEROUTING_HUM));  
                bIsError = true;
                return;
            }
            if(null != sUserID  && !String.isBlank(sUserID) && null != sUserName && !String.isBlank(sUserName) &&  [select id,name from user where id= :sUserID limit 1].name != sUserName) 
            {
                UserNameMatched = false;
            }
            if((String.isBlank(sUserID) && (!String.isBlank(sUserName))) || !UserNameMatched)
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, System.label.TASKEDIT_USERNOTFOUND_HUM));
                bIsError = true;
                return;
            }
            if (sServiceValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && !String.isBlank(sServiceValue))
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.Label.TASKEDIT_CASETASKSC_HUM));
                bIsError = true;
                return;    
            }
            if (!sServiceValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && sDepartValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) )
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.Label.TASKEDIT_CASETASKDEPT_HUM));
                bIsError = true;
                return;    
            }
            else if (!sServiceValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && !sDepartValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) && !String.isBlank(sDepartValue))
            {        
                    if(!bShowTopic && !String.isBlank(sTopicValue) && sTopicValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.Label.TASKEDIT_CASETASKTOPIC_HUM));
                        bIsError = true;
                        return;
                    }
                    if(!bShowTopic && !String.isBlank(sTopicValue) && !sTopicValue.containsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM) && string.isBlank(oTask.Task_Owner__c))
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.TASKEDIT_CASEROUTING_HUM));  
                        bIsError = true;
                        return;
                    }
                    if(bShowTopic && string.isBlank(oTask.Task_Owner__c))
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.TASKEDIT_CASEROUTING_HUM));  
                        bIsError = true;
                        return;
                    }
                    }
            }
            if (bShowError && ( (!string.isBlank(oTask.Task_Owner__c) && !sOldTaskNumber.equals(oTask.Task_Owner__c)) || ((oTask.Status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM)) &&  (!bDisablefield)) ))
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.TASKEDIT_TRANSFERORCLOSE_HUM));  
                bIsError = true;
                return;
            }
            if(sOldTaskNumber.equals(oTask.Task_Owner__c) && !sOldOwnerQueue.equals(oTask.Owner_Queue__C))
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, System.label.TASKEDIT_TRANSFERERROR_HUM));  
                bIsError = true;
                return;    
            }           
            if(oTask != null && !bGroupexists)
            {
                sComments = oTask.Description; 
                if(Schema.SObjectType.Task.fields.Description.isCreateable() || Schema.SObjectType.Task.fields.Description.isUpdateable()) oTask.Description = ''; 
                SObjectType objectTypeTask = oTask.getSObjectType();
                Database.upsertResult upsertResult = Database.upsert(oTask,false);
                
                bIsError = HUMExceptionHelper.processUpsertResults(new List<Database.upsertResult>{upsertResult},
                new List<SObject>{oTask}, 'HUMTaskEditPageExtension','saveTask','Task');
                
                if(!String.isBlank(sComments))
                {       
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjID__c.isUpdateable()) oTaskFH.SObjID__c = oTask.Id;  
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldName__c.isUpdateable()) oTaskFH.SObjFieldName__c = GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM; 
                    if(Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.SObjFieldValue__c.isUpdateable()) oTaskFH.SObjFieldValue__c= sComments;    
                    if(Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isCreateable() || Schema.SObjectType.Task_Field_History__c.fields.Created_At__c.isUpdateable()) oTaskFH.Created_At__c = System.now();  
                   
                    Database.upsertResult upsertResult1 =Database.upsert(oTaskFH,false);
                     
                }
            }
            
            if(Apexpages.hasMessages())    bIsError = true;
            if(!bIsError && sParentTaskID!= null && sParentTaskID != ''  && sParentTaskID!= 'null')
            {
                if(Schema.sObjectType.HUM_Task_Heirarchy__c.fields.HUM_Parent_TaskID__c.isCreateable() && Schema.sObjectType.HUM_Task_Heirarchy__c.fields.Hum_CaseID__c.isCreateable())
                {
                System.debug('edit task page test parent id' + sParentTaskID + '    Task id'+  oTask.id+ ' caseid ' + oTask.whatID );
                insert new HUM_Task_Heirarchy__c( HUM_Parent_TaskID__c = sParentTaskID, HUM_Dependent_TaskID__c =(string) oTask.id ,Hum_CaseID__c =oTask.whatID );
                }
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMTaskEditPageExtension', 'saveTask');            
        }
    }   
      

   /**    
     * <p>    
     * This method is used to redirect to detail page if we click on cancel button
     * @param    null    
     * @return   PageReference     
     */

     public PageReference cancel()
     {
        try
        {   
            if(NULL != oTask.Task_Owner__c )
            {
                return new ApexPages.StandardController(oTask).view();
            }
            else
            {
                Case oCase = [Select Id From Case Where Id =: sWhatId];   
                return new ApexPages.StandardController(oCase).view();          
            }
        }
        catch(Exception ex)
        {
            return new ApexPages.StandardController(oTask).view();
        }
        return new ApexPages.StandardController(oTask).view();
    }
    
    
     /**    
     * <p>    
     * This method is used to populate and set the owner field with either queue or user value.
     * @param    null    
     * @return   Void     
     */     

    public void populateQueueName()
    {
        try
        {
            sTopicValue = '';
            if(!String.isBlank(sDepartValue) && !sDepartValue.equals(GLOBAL_CONSTANT_HUM.NONE_HUM) && mapServCenDeptWorkQueueSetup.containsKey(sServiceValue + '#' + sDepartValue))
            {
                oWorkQueSetup = mapServCenDeptWorkQueueSetup.get(sServiceValue + '#' + sDepartValue);
                sTaskOwnerValue = oWorkQueSetup.Work_Queue_Name__c;               
                if(!String.isBlank(sTaskOwnerValue))
                {
                    sQueueName = sTaskOwnerValue;
                    oTask.Task_Owner__c = sTaskOwnerValue.replace('_',' ');               
                    sUserName = '';
                }
            }
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'HUMTaskEditPageExtension', 'populateQueueName');
        }        
    }
   
    /**    
     * <p>    
     * This Method is used to fetch Case  ,Case owner Details on page Load. 
     * @param    String   
     * @return   Void    
     */ 
     public void fetchCaseDetails(String sWhatID)
     {
       List<case> oCaseList = [SELECT Id, CaseNumber, OwnerId, Case_Owner__c, Service_Center__c, Department__c, Topic__c, Owner_Queue__c,Policy_Member__c, 
                               Work_Queue_View_Name__c FROM Case where Id =: sWhatId LIMIT 1];
       if(oCaseList!=null && !(oCaseList.isEmpty())) {
        
            oCaseNum = oCaseList[0];
            
            if(null != oCaseList[0].Policy_Member__c) {
                List<Member_ID__c> listMemberIds = [select id,Name from Member_ID__c where Policy_Member__c =:oCaseList[0].Policy_Member__c and Type__c =: HUMConstants.HUMMedicare limit 1];
                if(!listMemberIds.isempty()) {
                    sMedicareID = listMemberIds[0].Name;
                }
            }
       }            
     }
    
    
    /**    
     * <p>    
     * This Method is used to populate Task transfer Section values when it is disabled. 
     * @param    null   
     * @return   Void    
     */  
    public void populateTaskTransferForExtTask()
     {
       String sTaskType = Apexpages.currentPage().getParameters().get('taskType');
       if(sTaskType.EqualsIgnoreCase(sExtRequest))
        {  
          if(oCaseNum!=null)
           {                  
            sServiceValue = oCaseNum.Service_Center__c;
            sDepartValue = oCaseNum.Department__c;
            populateQueueName(); 
            sTopicValue = oCaseNum.Topic__c;
            if(oCaseNum.Case_Owner__c!=oCaseNum.Owner_Queue__c)
              {
                sUserName = oCaseNum.Case_Owner__c;    
                oTask.Task_Owner__c = sUserName; 
                if(!String.isBlank(sUserName)) sUserID = oCaseNum.OwnerId;                         
              }    
            }
         }        
      }    

    /**
    * <p>
    * This Method is used to populate and assign Service and Department values when "Prefill Queue" button is clicked by agent. 
    * @param    null   
    * @return   Void    
    */
    public void prefillQueue() {
        try{

            User oUser = [select Current_Queue__c from User where Id= :Userinfo.getUserId()] ;
            String sQueueValue = oUser.Current_Queue__c;
            sQueuevalue = sQueueValue.replace(' ', '_');
            
            List<Work_Queue_Setup__c> lstWQS = [select id, Service_Center__c, Department__c, (select Work_Queue_Topic_Name__r.Name from Work_Queue_Juntions__r where Work_Queue_Topic_Name__r.IsActive__c = true) from Work_Queue_Setup__c where Work_Queue_Name__c =: sQueueValue AND IsActive__c = TRUE];
            
            for(Work_Queue_Setup__c oWQS : lstWQS){
                this.sServiceValue = oWQS.Service_Center__c;
                this.sDepartValue = oWQS.Department__c;
                this.sUserName = null;
                this.sUserID = null;
                populateQueueName();
                List<Work_Queue_Junction__c> lstofWQJ = oWQS.Work_Queue_Juntions__r;
                
                if(null != lstofWQJ && lstofWQJ.size()==1)
                    this.sTopicValue = lstofWQJ[0].Work_Queue_Topic_Name__r.Name; 
                else
                    this.sTopicValue = null;                  
            } 
            
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'HUMTaskEditPageExtension', 'prefillQueue');
        }
    }  
        
    /**
    * 
    *  Validate the Time Entered is correct
    * @param    String   
    * @return   Time    
    */
    public Time parseTime(String sTime) {
        

        Time calTime;
        try {
            Date todayDate = Date.today();
            String dayString = todayDate.format();
           
            Datetime dt = DateTime.parse (dayString + ' ' + sTime);
            calTime = dt.time();

        } catch (Exception e) {

            calTime = null;
        }
        return calTime;
    }
}