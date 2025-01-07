/*****************************************************************************************
Apex class Name    : HUMTaskListViewController
Version            : 1.0
Function           : Controller for Task List View page to display list of Case/Task
Created On         : June 9 2014
Test Class         : HUMTaskListViewControllerTest

    Modification Log
*   Developer                   Code Review         Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   SuryaKumari Medicherla      17093               05/09/2014          Original Version
*   Mrudula Jaddu               17093               06/21/2014          Added View by Functionality
*   Manish Kumar                17093               06/22/2014          Added TAT sorting and select option
*   Manish Kumar                17521               08/21/2014          Created a new Property sCaseOrTaskNumber for accept functionality and Modularized createand execute function into 2 functions
*   Manish Kumar                17521               08/28/2014          Modified acording to code review : 17521
*   Manish Kumar                18221               09/28/2014          Modified sorting functionality
*   Manish Kumar                18221               09/29/2014          Modified acording to code review : 18221
*   Manish Kumar                18221               09/29/2014          Added CC review# in log. Defect#155745
*   Manish Kumar                18331               10/03/2014          Code fix for defect#156552
*   Mrudula Jaddu               18811               10/28/2014          Modified interacting about with Account.Name as per defect 158714
*   Manish Kumar                18933               11/03/2014          Code fix for defect#159932,#159931
*   Mrudula Jaddu               19058               11/06/2014          Modified Assign and accept all functions for my task report
*   Manish Kumar                19218               11/12/2014          Code fix for defect#160822, #160818
*   Manuel Johnson              19533               11/25/2014          Code fix for defect#160818
*   SuryaKumari Medicherla      19911               12/11/2014          Code modified to replace custom settings with custom object (REQ-72809)
*   Manish Kumar                20383               13/01/2015          Added logic for REQ#72784
*   Cody Sanders                21555               19/02/2015          Code fix for Command Center Issue 64. Other moved to be default queueview.
*   Manish Kumar                21555               13/03/2015          Added logic to fix Defect#172474.0001. Multiple user should not be able to claim same Case/Task. Code added/modified from line#191-193 & 207-209
*   Manish Kumar                21713               21/03/2015          Added logic to fix CA Ticket#4952530. View state is exceeding 135KB limit size. Code added/modified from line#43, 44 & 47
*   Manish Kumar                22086               02/04/2015          Code fix as part of Case/Task optimization. Custom sortning removed and OOB sorting functionality implemented. Removed few more local variables.
*   Cody Sanders                29406               02/03/2016          Changed references to Constants Custom Object to Constants Class.
*   Payal yadav                 28800               02/10/2016          Worked on CA ticket 5369152 REQ - 237155
*   Apoorv Jain                 33402               02/24/2014          Code fix for defect#216978 , #216993
*   Prachi Bhasin               33402               07/18/2016          Changes made as part of REQ-270349
*   Pradeepkumar Dani           42551               10/05/2016          REQ-00669, REQ-244446, REQ-244447, REQ-244449 Changes
*   Vamsi Kondragunta           43634               10/07/2016					Changes As part of req :283728
*   Vamsi Kondragunta           44289               10/20/2016					Changes As part of Defect Fix #245787.0001
*   Sharan Shanmugam			47072			    11/07/2016			Added logic in cloneCase method to pick only active queues.
******************************************************************************************/
public with sharing class HUMTaskListViewController
{
    private boolean bSortingFlag = false;
    private integer iCounter = 0;
    private integer iList_size = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_CASETASKPAGESIZE_HUM;
    private integer iTotalSize = 0;
    private integer iMax_limit = 0;
    private integer iPageFirstRecord = 0;
    @testVisible private Transient map<Id, Case> mapCase = new map<Id, Case>();
    @testVisible private Transient map<Id, Account> mapAccount = new map<Id, Account>();
    @testVisible private map<String,List<String>> mapGroupQueue = new map<String, List<String>>();
    @testVisible private List<String> lstCatTempQueues = new List<String>();
    @testVisible private Transient List<HUMCaseTaskHelper> lstcaseTaskListHelper = new List<HUMCaseTaskHelper>();
    @testVisible private map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new map<String, Work_Queue_Setup__c>([Select Name,OwnerId, id,Public_Group_Name__c, Work_Queue_Name__c from Work_Queue_Setup__c where IsActive__c = TRUE limit :GLOBAL_CONSTANT_HUM.TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM]);
    @testVisible private Id sAttachmntId = NULL;
    @testVisible private string sCurrentQueue = '';
    @testVisible private List<Case> setDefaulterCaseIDs = new List<Case>();
    @testVisible private boolean bPullWorkItem = false;
    @testVisible private map<string, boolean> isReadMap = new map<string, boolean>();
    @testVisible private Case oDMLCase = new Case();
    @testVisible private Task oDMLTask = new Task();    

    private static final string sHUMQueue             = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM;
    private static final Integer iHUMCaseTaskPageSize = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_CASETASKPAGESIZE_HUM;
    private static final string sHUMRegular           = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_REGULAR_HUM;
    private static final string sInProgress           = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM;
    private static final string sPending              = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDING_HUM;
    private static final string sPendingTask          = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGTASK_HUM;
    private static final string sPendingResponse      = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGRESPONSE_HUM;
    private static final string sAllOtherView         = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALLOTHERVIEW_HUM;
    private static final integer iCaseTaskQueryLimit  = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_CASETASKQUERYLIMIT_HUM;

    /**
    * Test method used for instantiating local variables.
    * <p>    
    * @return     NA
    * @param      NA
    */
    public HUMTaskListViewController()
    {
        lstLogErrors = new List<Error_Log__c>();
        sSortBy = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYAGE_HUM;
        iResCounter = 0;
        bPagenationFlag  = false;
        bDisableApexMessage = false;
        sCaseOrTaskId = '';
        sCaseOrTaskNumber = '';
        bIsError = false;
        sCaseTaskIds = '';
        sSelectedView = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM;
        sSelectedAssign = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM;
        sSelPullWorkQueue = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM;    
        sSelPullWOrkView = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM; 
        sSelPullWorkFilterBy = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM;
        sCurrentQueue = [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c;
    }

    public integer iResCounter {get; set;}
    public string sSortBy {get;set;}
    public String sViewCategorydd1 {get;set;}
    public String sViewCategorydd2 {get;set;}
    public boolean bPagenationFlag {get; set;}
    public string sSelectedView {get; set;}
    public string sSelectedAssign {get; set;}
    public string sCaseOrTaskId {get; set;}
    public string sCaseOrTaskNumber {get; set;}
    public boolean bIsError {get; set;}
    public boolean bShowData {get; set;}
    public boolean bDisableApexMessage {get;set;}
    public string sSelPullWorkQueue {get; set;} 
    public string sSelPullWorkView {get; set;}  
    public string sSelPullWorkFilterBy {get; set;}
    public string sCaseTaskIds{get; private set;}
    public List<Error_Log__c> lstLogErrors {get; set;}

    /**
    * Method is used to return default case list
    * <p>
    * @return   List<Case>
    * @param    NA
    */
    public List<Case> getDefaulterCaseIDs()
    {
        return setDefaulterCaseIDs;
    }

    /**
    * Method is used to initialize workQueueSelector, GroupQueue, Category DDs
    * <p>
    * @return   void
    * @param    NA
    */
    public void initializeWorkQueue()
    {
        try
        {
            List<String> lstMygroups = GetQueueList();
            map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new map<String, Work_Queue_Setup__c>([Select Public_Group_Name__c, Work_Queue_Name__c from Work_Queue_Setup__c where IsActive__c = TRUE limit :GLOBAL_CONSTANT_HUM.TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM]);
            List<String> lstQueues = NULL;

            for (String sWorkQueue : mapWorkQueueSetup.keySet())
            {
                Work_Queue_Setup__c oWorkQueue = mapWorkQueueSetup.get(sWorkQueue);
                lstQueues = mapGroupQueue.get(oWorkQueue.Public_Group_Name__c);

                if(NULL == lstQueues)   lstQueues = new List<String>();

                lstQueues.add(oWorkQueue.Work_Queue_Name__c);                
                mapGroupQueue.put(oWorkQueue.Public_Group_Name__c, lstQueues);
            }

            initializeGroupQueue(lstMygroups, lstQueues);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMTaskListViewController','initializeWorkQueue');
        }
    }    
    /**
    * Method used to initialize GroupQueue and Category DDs
    * <p>
    * @return : void
    * @param   : List<String>, List<String>
    */
    @testVisible private void initializeGroupQueue(List<String> lstMygroups, List<String> lstQueues)
    {
        for(String sMine : lstMygroups)
        {
            lstQueues = mapGroupQueue.get(sMine);

            if(NULL != lstQueues)
            {
                for(String sQueue : lstQueues)
                {
                    lstCatTempQueues.add(sQueue.replace('_', ' '));
                }
            }
        }
        if(!lstCatTempQueues.isEmpty())
        {             
            lstCatTempQueues.sort(); 
            if(sSelectedAssign.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM))
            {
                sViewCategorydd1 = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM;
            }
            else if(sSelectedAssign.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM))
            {
                sViewCategorydd1 = lstCatTempQueues[0];
            }
            else
            {
                sViewCategorydd1 = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM;   
            }
            sViewCategorydd2 = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM;   
            bShowData = false;   
        }
        else
        {
            bShowData = true;
            throw new HUMCustomException(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ERRORQUEUENOACCESS_HUM);                  
        }
    }

    /**
    * Method to execute Database query for Case and Task
    * <p>
    * @return      Void
    * @param       sTaskQuery           Task query string
    * @param       sCaseQuery           Case query string
    * @param       lstQueues            lis of queues for case/Task query
    * @param       sDropDown1           user queue for case/Task query
    * @param       sUserName            user name for case/Task query
    * @param       sDropDown2           user queue view for case/Task query
    * @param       sQueueOrViewNamedd2  user queue view for case/Task query
    */
    @TestVisible private void executeDBQuery(String sTaskQuery, String sCaseQuery, List<String> lstQueues, String sDropDown1, String sUserName, String sDropDown2, String sQueueOrViewNamedd2)
    {
        string sInteractngWith = '';
        String sInteractngWithId = '';
        String sInteractingAbout = '';
        String sWhatId = '';
        Date dtFollowUpDate = NULL;

        List<Task> lstTask = new List<Task>();
        List<Case> lstCase = new List<Case>();
        List<ID> lstWhatID = new List<ID>();
        List<ID> lstIWID = new List<ID>();
        Case objCase = NULL;
        Account objAcc = NULL;
        mapCase = new map<Id, Case>();
        mapAccount = new map<Id, Account>();

        if(sSelectedView.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM) || sSelectedView.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM))
        {
            for(Task oTask : Database.query(sTaskQuery))
            {
                lstTask.add(oTask);
                lstWhatID.add(oTask.WhatId);
            }
            mapCase = new map<Id, Case>([select Id, Interacting_With__c, Interacting_With__r.Name, Account.Name, Follow_Up_Due_Date__c from Case WHERE ID IN : lstWhatID]);
        }
        if(sSelectedView.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM) || sSelectedView.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM))
        {
            for(Case oCase : Database.query(sCaseQuery))
            {
                lstCase.add(oCase);
                lstIWID.add(oCase.Interacting_With__c);
                mapCase.put(oCase.Id, oCase);
            }
            mapAccount = new map<Id, Account>([select Id, Name from Account WHERE ID IN : lstIWID]);
        }

        for(Task oTask : lstTask)
        {
            sWhatId = oTask.WhatId;
            objCase = mapCase.get(sWhatId);

            if(!string.isBlank(sWhatId) && sWhatId.startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SFDCCASEID_HUM))
            {
                if(NULL != objCase)
                {
                    sInteractngWith = objCase.Interacting_With__r.Name;
                    sInteractngWithId = objCase.Interacting_With__c;
                    sInteractingAbout = objCase.Account.Name;
                    dtFollowUpDate = objCase.Follow_Up_Due_Date__c;
                }
                else
                {
                    sInteractngWith = sInteractngWithId = sInteractingAbout = NULL;
                    dtFollowUpDate = NULL;
                }
            }

            if(!isReadMap.isEmpty() && isReadMap.containsKey(string.valueof(oTask.Id)))
            {
                lstcaseTaskListHelper.add(new HUMCaseTaskHelper(oTask, sInteractngWith, sInteractngWithId, sInteractingAbout, dtFollowUpDate, false, sSortBy, isReadMap.get(string.valueof(oTask.Id)), true));
            }
            else
            {
                lstcaseTaskListHelper.add(new HUMCaseTaskHelper(oTask, sInteractngWith, sInteractngWithId, sInteractingAbout, dtFollowUpDate, false, sSortBy, false, false));
            }
        }
        for(Case oCase : lstCase)
        {
            objAcc = mapAccount.get(oCase.Interacting_With__c);
            sInteractngWith = (NULL != objAcc ) ? objAcc.Name : '';
            if(!isReadMap.isEmpty() && isReadMap.containsKey(string.valueof(oCase.Id)))
            {
                lstcaseTaskListHelper.add(new HUMCaseTaskHelper(oCase, sInteractngWith, false, sSortBy, isReadMap.get(string.valueof(oCase.Id)), true));
            }
            else
            {
                lstcaseTaskListHelper.add(new HUMCaseTaskHelper(oCase, sInteractngWith, false, sSortBy, false, false));
            }            
        }
    }
    /**
    * Method to create dynamic query
    * <p>
    * @return     Void
    * @param      viewType      string type of view
    */
    @TestVisible private void CreateAndExecute(String viewType)
    {
        String sUserName;
        String sCategory;
        String sDropDown1;
        String sDropDown2;
        Integer iCntTasks = 0;
        Integer iCntCases = 0; 
        List<String> lstQueues = new List<String>();
        List<Group> lstGroups = [SELECT Name FROM Group WHERE Type =: sHUMQueue];
        map<string, sObject> mapSObject = new map<string, sObject>();

        for(Group oGroup : lstGroups)   lstQueues.add(oGroup.Name);
        string sTaskQuery = 'SELECT Subject, Classification__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, Ownerid, Owner_Queue__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task where (Status =\'' + sInProgress + '\'OR Status =\''+ sPending + '\') ';
        string sCaseQuery = 'SELECT CaseNumber, Case_Owner__c, Priority, Status, Case_Age__c, Case_Number__c, Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c, Re_Open_Case_Age__c, Days_Since_Last_Modified__c, Due_Date__c, Ownerid, Classification_Id__r.name, Intent_Id__r.name, Follow_Up_Due_Date__c, CreatedDate FROM Case where (Status =\'' + sPendingTask + '\'OR Status =\'' + sInProgress + '\'OR Status =\'' + sPendingResponse +'\') ';
        sUserName = UserInfo.getName();
        string sUserProfile = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;

        if(sSelectedAssign.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM) && sUserProfile == System.Label.HUMAgencyCCSupervisor)
        {
            sTaskQuery += ' AND (Task_Owner__c in :lstQueues OR Task_Owner__c =:sUserName)';
            sCaseQuery += ' AND (Case_Owner__c in :lstQueues OR Case_Owner__c =:sUserName)';             
        }
        else if(sSelectedAssign.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM))
        {
            sTaskQuery += ' AND Task_Owner__c in :lstQueues';
            sCaseQuery += ' AND Case_Owner__c in :lstQueues';             
        }
        else 
        {
            sTaskQuery += ' AND Task_Owner__c =:sUserName';
            sCaseQuery += ' AND Case_Owner__c =:sUserName';
        }

        sDropDown1 = String.escapeSingleQuotes(sViewCategorydd1.trim());
        sDropDown2 = String.escapeSingleQuotes(sViewCategorydd2.trim());

        if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEALL_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1';
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM))
        {            
            sTaskQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
            sCaseQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
        }

        sTaskQuery += ' Limit ' + iCaseTaskQueryLimit;
        sCaseQuery += ' Limit ' + iCaseTaskQueryLimit;  

        executeDBQuery(sTaskQuery, sCaseQuery, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
    }
    /**
    * Method to get the List of Tasks and Cases which we are going to display on View Page. 
    * <p>
    * @return    List<HUMCaseTaskHelper>
    * @param     NA
    */
    public List<HUMCaseTaskHelper> getCaseTask()
    {  
        try
        {
            bPagenationFlag = false;
            bDisableApexMessage = false;
            ApexPages.getMessages().clear();

            lstcaseTaskListHelper = new List<HUMCaseTaskHelper>();
            iResCounter = 0;
            List<String> lstQueueIds = new  List<String>{};
                setDefaulterCaseIDs = new List<Case>();

            if(sViewCategorydd1.equals(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM))
            {
                CreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNEDALL_HUM);
            }
            else
            {
                if(sViewCategorydd2.equals(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM))
                {
                    CreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEALL_HUM);
                }
                else if(sViewCategorydd2.equals(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OTHER_HUM))
                {
                    CreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM);
                }
                else
                {
                    CreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM);
                }
            }

            if(bPullWorkItem)
            {
                pullWorkAction();
            }

            lstcaseTaskListHelper.sort();            
            bSortingFlag = false;            
            iResCounter = (NULL != lstcaseTaskListHelper) ? lstcaseTaskListHelper.size() : 0;  
            if(iResCounter >= iCaseTaskQueryLimit){             
                bDisableApexMessage = true;
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, System.Label.CASELIMIT_MESSAGE_HUM));
            }

            if(NULL != lstcaseTaskListHelper && !lstcaseTaskListHelper.isEmpty())
            {
                iList_size = iHUMCaseTaskPageSize;
                iTotalSize = lstcaseTaskListHelper.size();
                iList_size = (iList_size > iTotalSize ) ? iTotalSize : ilist_size;
                if(iList_size == iTotalSize) iList_size = iTotalSize;
                bPagenationFlag = true;
            }

            if(NULL != lstcaseTaskListHelper)
            {
                return getPagination(lstcaseTaskListHelper);
            }
            else
            {
                return lstcaseTaskListHelper;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMTaskListViewController','getCaseTask');
        }       
        return lstcaseTaskListHelper;
    }
    /**
    * Method to get the queues based on the Public Groups in which the logged in user is part of. 
    * <p>
    * Return Type   :   list<SelectOption>
    * Parameters    :   NA
    */
    public list<SelectOption> getCategoriesdd1()
    {
        List<String>  lstCatTemp = new List<String>();
        Set<String> setCatTemp = new Set<String>();
        list<SelectOption> lstcat = new list<SelectOption>();         
        list<String> lstMygroups = GetQueueList();
        List<String> lstGroupNames = new List<String>();

        try
        {
            for (String sWorkQueue : mapWorkQueueSetup.keySet())
            {
                for(String oAllGrps : lstMygroups) 
                {
                    if(mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c <> null && (mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c).trim().equalsIgnoreCase(oAllGrps))
                        lstGroupNames.add(mapWorkQueueSetup.get(sWorkQueue).Work_Queue_Name__c);
                }
            }

            if(!lstGroupNames.isEmpty())
                for(String sQueue :lstGroupNames)
            {
                lstCatTemp.add(sQueue.replace('_', ' '));
            }

            if(!lstCatTemp.isEmpty())
            {
                setCatTemp.addAll(lstCatTemp);
                lstCatTemp = HUMUtilityHelper.doSort(setCatTemp);
                lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM));
                for(string sCategoryName : lstCatTemp)
                {
                    lstcat.add(new SelectOption(sCategoryName, sCategoryName));
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMTaskListViewController','getCategoriesdd1');
        }
        return lstcat;
    }
    /**
    * Method to get the queueviews based on the Public Groups in which the logged in user is part of and 
        depending on the first dropdown option selected
    * <p>
    * @return     list<SelectOption>
    * @param      NA
    */
    public list<SelectOption> getCategoriesdd2()
    {
        list<SelectOption> lstcat = new List<SelectOption>();
        lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM));
        lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OTHER_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OTHER_HUM)); 
        String sClause = sViewCategorydd1.trim();

        try
        {
            List<Queue_View__c> lstQueues = [SELECT Queue_View_Name__c FROM Queue_View__c where Queue_Name__c =:sClause AND Group_Name__c IN : GetQueueList() AND IsActive__c = TRUE];

            if(!lstQueues.isEmpty())
            {
                for(Queue_View__c oViews : lstQueues)    
                {                        
                    lstcat.add(new SelectOption(oViews.Queue_View_Name__c.trim(), oViews.Queue_View_Name__c.trim()));
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMTaskListViewController','getCategoriesdd2');
        }
        lstcat.sort();        
        return lstcat;
    }
    /**
    * Method used to populate SelectedAssign Dropdown
    * <p>
    * @return   List<SelectOption>
    * @param    NA
    */
    public List<SelectOption> getSelectedAssign()
    {
        List<SelectOption> lstSelectedAssign = new List<SelectOption>();
        lstSelectedAssign.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM));
        lstSelectedAssign.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM));
        lstSelectedAssign.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM));
        return lstSelectedAssign;
    }    
    /**
    * Method to populate SelectedView Dropdown
    * <p>
    * @return : List<SelectOption>
    * @param  : NA
    */
    public List<SelectOption> getSelectedView()
    {
        List<SelectOption> lstSelectedView = new List<SelectOption>();
        lstSelectedView.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM));
        lstSelectedView.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_CASES_HUM));
        lstSelectedView.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_TASKS_HUM));
        return lstSelectedView;
    }    
    /**
    * Method to populate category Dropdown on Initial Refresh
    * <p>
    * @return   void
    * @param    NA
    */
    public void populateUnassigndd2Initially()
    {
        if(sViewCategorydd1.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM) && sSelectedAssign.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM) && !lstCatTempQueues.isEmpty()) sViewCategorydd1 = lstCatTempQueues[0];
    }    
    /**
    * Method to populate category Dropdown initial value
    * <p>
    * @return   :   void
    * @param    :   NA
    */
    public void populateUnassigndd2()
    {
        sViewCategorydd2 = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM;
    }    
    /**
    * Method to get Public Groups in which the logged in user is part of.
    * <p> 
    * @return  :  List<string>
    * @param    :  NA
    */
    @testVisible private List<string> getQueueList()
    {
        try
        {
            List<GroupMember> lstGrpMem = [select GroupId from GroupMember where UserOrGroupId =: Userinfo.getUserId()];
            List<Id> lstGroupIds = new List<Id>();            
            for(GroupMember oGrpMem : lstGrpMem) lstGroupIds.add(oGrpMem.GroupId);            
            List<Group> lstGroups = [Select Id, Name from Group where type =: sHUMRegular AND Id IN : lstGroupIds];
            List<String> lstMyGroups =  new List<String>();            
            for(Group oGrpName : lstGroups) lstMyGroups.add(oGrpName.Name);
            return lstMyGroups;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMTaskListViewController','getQueueList');
        }
        return NULL;
    }    
    /**
    * Method to get Pagination for Cases & Tasks data
    * <p>
    * @return   lstcaseTaskListPaginationHelper required        page data
    * @param    lstcaseTaskListHelper                           pagination data
    */
    public List<HUMCaseTaskHelper> getPagination(List<HUMCaseTaskHelper> lstcaseTaskListHelper)
    {
        List <HUMCaseTaskHelper>  lstcaseTaskListPaginationHelper = new List <HUMCaseTaskHelper>();
        iMax_limit = ((iCounter + ilist_size) > iTotalSize) ? iTotalSize : (iCounter + ilist_size);
            iCounter = (iCounter > iMax_limit) ? 0 : iCounter;

        try
        { 
            if(NULL == lstcaseTaskListHelper)
            {
                lstcaseTaskListPaginationHelper = new List<HUMCaseTaskHelper>();
            } 
            else if(!lstcaseTaskListHelper.isEmpty())
            {
                lstcaseTaskListPaginationHelper.clear();
            }

            if(lstcaseTaskListHelper.isEmpty())    return NULL;

            for(integer i = iCounter ; i < iMax_limit ; i++)
            {
                lstcaseTaskListPaginationHelper.add(lstcaseTaskListHelper.get(i));
            }
            iPageFirstRecord = iCounter;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMTaskListViewController','getPagination');
        }       
        return lstcaseTaskListPaginationHelper;
    }    
    /**  
    * Method used to display the previous page record(s)  
    * <p>  
    * @return      void    
    * @param       NA
    */
    public void getPrevious()
    {
        iCounter -= ilist_size;
        iPageFirstRecord = iCounter;
    }    
    /**  
    * Method used to display the next page record(s)
    * <p>    
    * @return       void    
    * @param        NA
    */
    public void getNext()
    {
        iCounter += ilist_size;
        iPageFirstRecord = iCounter;
    }    
    /**  
    * Method to disable the previous buttons    
    * <p>
    * @return       boolean
    * @param        NA
    */
    public Boolean getDisablePrevious()
    {
        return (iCounter > 0) ? false : true;
    }    
    /**  
    * Method to disable the next buttons 
    * <p>   
    * @return       boolean
    * @param        NA
    */
    public Boolean getDisableNext()
    {
        return (iCounter + iList_size < iTotalSize) ? false : true;
    }    
    /**  
    * Method used to display the total size of record(s)
    * <p>    
    * @return       Integer
    * @param        NA
    */
    public Integer getTotal_size()
    {
        return iTotalSize;
    }    
    /**  
    * Method used to display the first page record(s) 
    * <p>   
    * @return       Integer
    * @param        NA
    */
    public Integer getPageFirstRecord()
    {
        return iPageFirstRecord + 1;
    }    
    /**
    * Method used to display the last page record(s)  
    * <p>      
    * @return       Integer
    * @param        NA
    */
    public Integer getPageLastRecord()
    {
        return getDisableNext() ? iTotalSize : (iPageFirstRecord + iList_size);
    }    
    /**  
    * Method used to disable Change Owner and Accept all button
    * <p>
    * @return   :    Boolean
    * @param   :    NA
    */
    public Boolean getDisableChangeOwner()
    {
        return !lstcaseTaskListHelper.isEmpty() ? false : true;
    }
    /**
     * Method to update read/unread action
     * <p>
     * @return     Void
     * @param      nil
     */
    public void updateWrapper()
    {
        string caseTaskId = Apexpages.currentPage().getParameters().get('CaseTaskId');
        if(!string.isBlank(caseTaskId)) isReadMap.put(caseTaskId , true);
    }    
    /**
     * Method to invoke pull work logic
     * <p>
     * @return     Void
     * @param      nil
     */
    public void pullWorkItem()
    {
        this.bPullWorkItem = true;
    }
    /**
     * Method to get pull work queue drop down options
     * <p>
     * @return     lstOptions
     * @param      nil
     */
    public list<SelectOption> getPullWorkQueueOptions()
    {
        list<SelectOption> lstOptions = new list<SelectOption>();
        for(SelectOption v : getCategoriesdd1())
        {
            if(v.getValue() == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM)
            {
                lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM));
            }
            else
            {
                lstOptions.add(new SelectOption(v.getLabel() ,v.getValue()));
            }
        }
        return lstOptions;
    }    
    /**
     * Method to get pull work view drop down options
     * <p>
     * @return     lstOptions
     * @param      nil
     */
    public list<SelectOption> getPullWorkViewOptions()
    {
        list<SelectOption> lstOptions =  new list<SelectOption>();
        for(SelectOption v : getCategoriesdd2())
        {
            if(v.getValue() == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM)
            {
                lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM));
            }
            else
            {
                lstOptions.add(new SelectOption(v.getLabel() ,v.getValue()));
            }
        }
        return lstOptions;
    }    
    /**
     * Method used to populate filter by Dropdown
     * <p>
     * @return   lstSortBy
     * @param    NA
     */
    public List<SelectOption> getPullWorkFilterByOptions()
    {
        List<SelectOption> lstSortBy = new List<SelectOption>();
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM)); 
        lstSortBy.sort(); 
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM));
        return lstSortBy;
    }
    /**
     * Method to execute pull work action logic
     * <p>
     * @return     void
     * @param      nil
     */
    @TestVisible private void pullWorkAction()
    {
        bPullWorkItem = false;

        if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM || sSelPullWorkView == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM || sSelPullWorkQueue == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SELECT_HUM)
        {
            bDisableApexMessage = true;
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PULLWORKVALIDATION_HUM));
        }else
        {
            if(sSelPullWorkView.equals(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OTHER_HUM))
            {
                if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM)
                {
                    pullWorkCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM);
                }
                else if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM)
                {
                    pullPriorityCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM);
                }       
            }
            else
            {
                if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM)
                {
                    pullWorkCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM);
                }
                else if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM)
                {
                    pullPriorityCreateAndExecute(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM);
                }                
            }
        }
    }    
    /**
    * Method to gemerate dynamic query for pull work SOQL
    * <p>
    * @return     void
    * @param      viewType      string type of view
    */
    @TestVisible private void pullWorkCreateAndExecute(String viewType)
    {
        String sUserName;
        String sCategory;
        String sDropDown1;
        String sDropDown2;
        Integer iCntTasks = 0;
        Integer iCntCases = 0; 
        List<String> lstQueues = new List<String>();
        List<Group> lstGroups = [SELECT Name FROM Group WHERE Type =: sHUMQueue];
        map<string, sObject> mapSObject = new map<string, sObject>();

        for(Group oGroup : lstGroups)
        {
            lstQueues.add(oGroup.Name);
        }

        string sTaskQuery = 'SELECT Id,Subject, Classification__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, Ownerid, Owner_Queue__c, QueueOrUserId__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task where (Status =\'' + sInProgress + '\' OR Status =\''+ sPending + '\')  AND Task_Owner__c in :lstQueues';
        string sCaseQuery = 'SELECT Id,CaseNumber, Case_Owner__c, Priority, Status, Case_Age__c, Case_Number__c, Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c, Re_Open_Case_Age__c, Days_Since_Last_Modified__c, Due_Date__c, Ownerid, Classification_Id__r.name, Intent_Id__r.name, Follow_Up_Due_Date__c, CreatedDate FROM Case where (Status =\'' + sPendingTask + '\' OR Status =\'' + sInProgress + '\' OR Status =\'' + sPendingResponse +'\') AND Case_Owner__c in :lstQueues';
        sUserName = UserInfo.getName();        

        sDropDown1 = String.escapeSingleQuotes(sSelPullWorkQueue.trim());
        sDropDown2 = String.escapeSingleQuotes(sSelPullWorkView.trim());

        if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM))
        {            
            sTaskQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
            sCaseQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';  
        }        

        if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM)
        {
            sTaskQuery += ' ORDER BY ActivityDate DESC, CreatedDate DESC NULLS LAST';
            sCaseQuery += ' ORDER BY Due_Date__c DESC, CreatedDate DESC NULLS LAST';  
            sSortBy = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUEDATE_HUM;
        }
        else if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM)
        {
            sTaskQuery += ' ORDER BY Age_Since_Created__c DESC, CreatedDate DESC NULLS LAST';
            sCaseQuery += ' ORDER BY Case_Age__c DESC, Reopened__c DESC, CreatedDate DESC NULLS LAST'; 
            sSortBy = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM;
        }

        sTaskQuery += ' Limit ' + '1';
        sCaseQuery += ' Limit ' + '1';

        Integer recsize=0;
        recsize = pullWorkExecuteDBQuery(sTaskQuery, sCaseQuery, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
        if(recsize  ==0 )
         {
            bDisableApexMessage = true;
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PULLWORKNOITEMS_HUM));
        } 
    }    
    /**
    * Method to execute Database query for Case and Task
    * <p>
    * @return      void
    * @param       sTaskQuery           Task query string
    * @param       sCaseQuery           Case query string
    * @param       lstQueues            lis of queues for case/Task query
    * @param       sDropDown1           user queue for case/Task query
    * @param       sUserName            user name for case/Task query
    * @param       sDropDown2           user queue view for case/Task query
    * @param       sQueueOrViewNamedd2  user queue view for case/Task query
    */
    @TestVisible private integer pullWorkExecuteDBQuery(String sTaskQuery, String sCaseQuery, List<String> lstQueues, String sDropDown1, String sUserName, String sDropDown2, String sQueueOrViewNamedd2)
    {
        string sInteractngWith = '';
        String sInteractngWithId = '';
        String sInteractingAbout = '';
        String sWhatId = '';
        Date dtFollowUpDate = NULL;

        List<Task> lstTask = new List<Task>();  
        List<Case> lstCase = new List<Case>();
        List<ID> lstWhatID = new List<ID>();
        List<ID> lstIWID = new List<ID>();
        Case objCase = NULL;
        Account objAcc = NULL;
        mapCase = new map<Id, Case>();
        mapAccount = new map<Id, Account>();

        if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM )
        {
            for(Task oTask : Database.query(sTaskQuery))
            {
                lstTask.add(oTask);
                lstWhatID.add(oTask.WhatId);
            }
            if(!lstWhatID.isEmpty())mapCase = new map<Id, Case>([select Id, Interacting_With__c, Interacting_With__r.Name, Account.Name, Follow_Up_Due_Date__c from Case WHERE ID IN : lstWhatID]);
        }        

        if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM || sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM)
        {
            for(Case oCase : Database.query(sCaseQuery))
            {
                lstCase.add(oCase);
                lstIWID.add(oCase.Interacting_With__c);
                mapCase.put(oCase.Id, oCase);
            }
            if(!lstIWID.isEmpty())mapAccount = new map<Id, Account>([select Id, Name from Account WHERE ID IN : lstIWID]);
        }
        list<HUMCaseTaskHelper> lstPullItems = new list<HUMCaseTaskHelper>();  
        if(!lstTask.isEmpty())
        {
            for(Task oTask : lstTask)
            {
                sWhatId = oTask.WhatId;
                objCase = mapCase.get(sWhatId);

                 if(oTask.QueueOrUserId__c <> null && !oTask.QueueOrUserId__c.startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM))
                {
                    Date dtDueDate = oTask.ActivityDate;                        
                    oTask.OwnerId = UserInfo.getUserId();
                    oTask.Task_Owner__c = UserInfo.getName();
                    oTask.QueueOrUserId__c = UserInfo.getUserId();                
                    oTask.select__c = false;
                    oTask.LastModifiedby_Queue__c = sCurrentQueue;
                    oTask.ActivityDate = dtDueDate;
                    oDMLTask = oTask;
                    isReadMap.put(string.valueof(oTask.Id), false);
                }               

                if(!string.isBlank(sWhatId) && sWhatId.startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SFDCCASEID_HUM))
                {
                    if(NULL != objCase)
                    {
                        sInteractngWith = objCase.Interacting_With__r.Name;
                        sInteractngWithId = objCase.Interacting_With__c;
                        sInteractingAbout = objCase.Account.Name;
                        dtFollowUpDate = objCase.Follow_Up_Due_Date__c;
                    }
                    else
                    {
                        sInteractngWith = sInteractngWithId = sInteractingAbout = NULL;
                        dtFollowUpDate = NULL;
                    }
                }

                lstPullItems.add(new HUMCaseTaskHelper(oTask, sInteractngWith, sInteractngWithId, sInteractingAbout, dtFollowUpDate, false, sSortBy, false, true));
            }
        }        

        if(lstPullItems.isEmpty() && !lstCase.isEmpty())
        {
            for(Case oCase : lstCase)
            {
                if(!String.valueOf(oCase.OwnerId).startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM))
                {
                    oCase.OwnerId = UserInfo.getUserId();
                    oCase.Case_Owner__c = UserInfo.getName();
                    oCase.LastModifiedby_Queue__c = oCase.Owner_Queue__c;
                    oDMLCase = oCase;
                    isReadMap.put(string.valueof(oCase.Id), false);

                    objAcc = mapAccount.get(oCase.Interacting_With__c);
                    sInteractngWith = (NULL != objAcc ) ? objAcc.Name : '';

                    lstPullItems.add(new HUMCaseTaskHelper(oCase, sInteractngWith, false, sSortBy, false, true));
                }
            }
        }
        if(!lstPullItems.isEmpty())
        {
            lstPullItems.sort();
            lstcaseTaskListHelper.add(lstPullItems.get(0)); 
        }
        return lstPullItems.size();

    }
    /**
    * Method to update the owner to logged in user
    * <p>
    * @return   void
    * @param    NA
    */
    public void pullWorkDML()
    {
        if(oDMLCase.Id <> null) 
        {
            Database.update(oDMLCase, false);
            oDMLCase = new Case();
        }
        if(oDMLTask.Id <> null)
        {
            Database.update(oDMLTask, false);
            oDMLTask = new Task();
        } 
    }
    /**
     * Method to execute filterByPriority filterby
     * <p>
     * @return     void
     * @param      string
     */

    private void filterByPriority(String viewType)
    {
        String sDropDown1;
        String sDropDown2;
        String sUserName;
        List<String> lstQueues = new List<String>();
        List<Group> lstGroups = [SELECT Name FROM Group WHERE Type =: sHUMQueue];

        for(Group oGroup : lstGroups)
        {
            lstQueues.add(oGroup.Name);
        }
        sUserName = UserInfo.getName(); 
        string sTaskQuery = 'SELECT Id,Subject, Classification__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, Ownerid, Owner_Queue__c, QueueOrUserId__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task where (Status =\'' + sInProgress + '\' OR Status =\''+ sPending + '\')  AND Task_Owner__c in :lstQueues';
        string sCaseQuery = 'SELECT Id,CaseNumber, Case_Owner__c, Priority, Status, Case_Age__c, Case_Number__c, Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c, Re_Open_Case_Age__c, Days_Since_Last_Modified__c, Due_Date__c, Ownerid, Classification_Id__r.name, Intent_Id__r.name, Follow_Up_Due_Date__c, CreatedDate FROM Case where (Status =\'' + sPendingTask + '\' OR Status =\'' + sInProgress + '\' OR Status =\'' + sPendingResponse +'\') AND Case_Owner__c in :lstQueues';

        sDropDown1 = String.escapeSingleQuotes(sSelPullWorkQueue.trim());
        sDropDown2 = String.escapeSingleQuotes(sSelPullWorkView.trim());

        if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM))
        { 
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';            
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEALL_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1';           
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM))
        {            
            sTaskQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
            sCaseQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';            
        }
        Integer recordsize=0;
        Integer taskSize=0;
        String sCaseQuery1;
        String sTaskQuery1;
        String sCaseQuery2;
        String sTaskQuery2;
        String sCaseQuery3;
        String sTaskQuery3;

        sTaskQuery1 = sTaskQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC Limit 1';
        sCaseQuery1 = sCaseQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC Limit 1';
        recordsize = pullWorkExecuteDBQuery(sTaskQuery1, sCaseQuery1, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);    
        if(recordsize==0){
            sTaskQuery2 = sTaskQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC Limit 1';
            sCaseQuery2 = sCaseQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC Limit 1';
            recordsize = pullWorkExecuteDBQuery(sTaskQuery2, sCaseQuery2, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
            if(recordsize==0 ){
                sTaskQuery3 = sTaskQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC Limit 1';
                sCaseQuery3 = sCaseQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC Limit 1';
                recordsize = pullWorkExecuteDBQuery(sTaskQuery3, sCaseQuery3, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
                if(recordsize==0){
                    bDisableApexMessage = true;
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, 'There are no work items left with the selected options. Please select new options and click the Pull Work button.'));
                }
            }
        }


    }
    /**
     * Method to execute filterByPriorityCasesOnly filterby
     * <p>
     * @return     void
     * @param      string
     */

    private void filterByPriorityCasesOnly(String viewType)
    {
        String sDropDown1;
        String sDropDown2;
        String sUserName;
        List<String> lstQueues = new List<String>();
        List<Group> lstGroups = [SELECT Name FROM Group WHERE Type =: sHUMQueue];

        for(Group oGroup : lstGroups)
        {
            lstQueues.add(oGroup.Name);
        }
        sUserName = UserInfo.getName();
        string sTaskQuery = 'SELECT Id,Subject, Classification__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, Ownerid, Owner_Queue__c, QueueOrUserId__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task where (Status =\'' + sInProgress + '\' OR Status =\''+ sPending + '\')  AND Task_Owner__c in :lstQueues';
        string sCaseQuery = 'SELECT Id,CaseNumber, Case_Owner__c, Priority, Status, Case_Age__c, Case_Number__c, Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c, Re_Open_Case_Age__c, Days_Since_Last_Modified__c, Due_Date__c, Ownerid, Classification_Id__r.name, Intent_Id__r.name, Follow_Up_Due_Date__c, CreatedDate FROM Case where (Status =\'' + sPendingTask + '\' OR Status =\'' + sInProgress + '\' OR Status =\'' + sPendingResponse +'\') AND Case_Owner__c in :lstQueues';

        sDropDown1 = String.escapeSingleQuotes(sSelPullWorkQueue.trim());
        sDropDown2 = String.escapeSingleQuotes(sSelPullWorkView.trim());

        if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';             
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEALL_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1';           
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM))
        {            
            sTaskQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
            sCaseQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';            
        }
               Integer recordsize=0;
               String sTaskQuery1;
               String sTaskQuery2;
               String sTaskQuery3;

        String sCaseQuery1 = sCaseQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC Limit 1';
        recordsize = pullWorkExecuteDBQuery(sTaskQuery1, sCaseQuery1, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);    
        if(recordsize==0){
           String sCaseQuery2 = sCaseQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC Limit 1';
            recordsize = pullWorkExecuteDBQuery(sTaskQuery1, sCaseQuery2, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
            if(recordsize==0){
                String sCaseQuery3 = sCaseQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC Limit 1';
                recordsize = pullWorkExecuteDBQuery(sTaskQuery1, sCaseQuery3, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
                if(recordsize==0){
                    bDisableApexMessage = true;
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, 'There are no work items left with the selected options. Please select new options and click the Pull Work button.'));
                }
            }
        }
    }
    /**
     * Method to execute filterByPriorityTasksOnly filterby
     * <p>
     * @return     void
     * @param      string
     */
    private void filterByPriorityTasksOnly(String viewType)
    {
        String sDropDown1;
        String sDropDown2;
        String sUserName;
        List<String> lstQueues = new List<String>();
        List<Group> lstGroups = [SELECT Name FROM Group WHERE Type =: sHUMQueue];

        for(Group oGroup : lstGroups)
        {
            lstQueues.add(oGroup.Name);
        }
        sUserName = UserInfo.getName();
        string sTaskQuery = 'SELECT Id,Subject, Classification__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, Ownerid, Owner_Queue__c, QueueOrUserId__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task where (Status =\'' + sInProgress + '\' OR Status =\''+ sPending + '\')  AND Task_Owner__c in :lstQueues';
        string sCaseQuery = 'SELECT Id,CaseNumber, Case_Owner__c, Priority, Status, Case_Age__c, Case_Number__c, Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c, Re_Open_Case_Age__c, Days_Since_Last_Modified__c, Due_Date__c, Ownerid, Classification_Id__r.name, Intent_Id__r.name, Follow_Up_Due_Date__c, CreatedDate FROM Case where (Status =\'' + sPendingTask + '\' OR Status =\'' + sInProgress + '\' OR Status =\'' + sPendingResponse +'\') AND Case_Owner__c in :lstQueues';

        sDropDown1 = String.escapeSingleQuotes(sSelPullWorkQueue.trim());
        sDropDown2 = String.escapeSingleQuotes(sSelPullWorkView.trim());

        if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';             
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEALL_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1';           
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM))
        {            
            sTaskQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
            sCaseQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';            
        }
           Integer recordsize=0;
           String sCaseQuery1;
           String sCaseQuery2;
           String sCaseQuery3;

         String sTaskQuery1 = sTaskQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC Limit 1';
         recordsize = pullWorkExecuteDBQuery(sTaskQuery1, Null, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);    
        if(recordsize==0){
            String sTaskQuery2 = sTaskQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC Limit 1';
            recordsize = pullWorkExecuteDBQuery(sTaskQuery2, Null, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
            if(recordsize==0){
                String sTaskQuery3 = sTaskQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC Limit 1';
                recordsize = pullWorkExecuteDBQuery(sTaskQuery3, Null, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
                if(recordsize==0){
                    bDisableApexMessage = true;
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, 'There are no work items left with the selected options. Please select new options and click the Pull Work button.'));
                }
            }
        }

    }
    /**
     * Method to execute filterByPriorityCriticalHighOnly filterby
     * <p>
     * @return     void
     * @param      string
     */

    private void filterByPriorityCriticalHighOnly(String viewType)
    {
        String sDropDown1;
        String sDropDown2;
        String sUserName;
        List<String> lstQueues = new List<String>();
        List<Group> lstGroups = [SELECT Name FROM Group WHERE Type =: sHUMQueue];

        for(Group oGroup : lstGroups)
        {
            lstQueues.add(oGroup.Name);
        }
        sUserName = UserInfo.getName();
        string sTaskQuery = 'SELECT Id,Subject, Classification__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, Ownerid, Owner_Queue__c, QueueOrUserId__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task where (Status =\'' + sInProgress + '\' OR Status =\''+ sPending + '\')  AND Task_Owner__c in :lstQueues';
        string sCaseQuery = 'SELECT Id,CaseNumber, Case_Owner__c, Priority, Status, Case_Age__c, Case_Number__c, Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c, Re_Open_Case_Age__c, Days_Since_Last_Modified__c, Due_Date__c, Ownerid, Classification_Id__r.name, Intent_Id__r.name, Follow_Up_Due_Date__c, CreatedDate FROM Case where (Status =\'' + sPendingTask + '\' OR Status =\'' + sInProgress + '\' OR Status =\'' + sPendingResponse +'\') AND Case_Owner__c in :lstQueues';

        sDropDown1 = String.escapeSingleQuotes(sSelPullWorkQueue.trim());
        sDropDown2 = String.escapeSingleQuotes(sSelPullWorkView.trim());

        if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';             
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEALL_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1';           
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM))
        {            
            sTaskQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
            sCaseQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';            
        }   
        Integer recordsize=0;
        String sTaskQuery1 = sTaskQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC Limit 1';
        String sCaseQuery1 = sCaseQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC Limit 1';
        recordsize = pullWorkExecuteDBQuery(sTaskQuery1, sCaseQuery1, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);    
        if(recordsize==0){
            String sTaskQuery2 = sTaskQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC Limit 1';
            String sCaseQuery2 = sCaseQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC Limit 1';
            recordsize = pullWorkExecuteDBQuery(sTaskQuery2, sCaseQuery2, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
            if(recordsize==0){
                    bDisableApexMessage = true;
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, 'There are no work items left with the selected options. Please select new options and click the Pull Work button.'));
                }
        }
    }
    /**
     * Method to execute filterByPriorityNormalOnly filterby
     * <p>
     * @return     void
     * @param      string
     */

    private void filterByPriorityNormalOnly(String viewType)
    {
        String sDropDown1;
        String sDropDown2;
        String sUserName;
        List<String> lstQueues = new List<String>();
        List<Group> lstGroups = [SELECT Name FROM Group WHERE Type =: sHUMQueue];

        for(Group oGroup : lstGroups)
        {
            lstQueues.add(oGroup.Name);
        }
        sUserName = UserInfo.getName();
        string sTaskQuery = 'SELECT Id,Subject, Classification__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, Ownerid, Owner_Queue__c, QueueOrUserId__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task where (Status =\'' + sInProgress + '\' OR Status =\''+ sPending + '\') AND Task_Owner__c in :lstQueues';
        string sCaseQuery = 'SELECT Id,CaseNumber, Case_Owner__c, Priority, Status, Case_Age__c, Case_Number__c, Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c, Re_Open_Case_Age__c, Days_Since_Last_Modified__c, Due_Date__c, Ownerid, Classification_Id__r.name, Intent_Id__r.name, Follow_Up_Due_Date__c, CreatedDate FROM Case where (Status =\'' + sPendingTask + '\' OR Status =\'' + sInProgress + '\' OR Status =\'' + sPendingResponse +'\') AND Case_Owner__c in :lstQueues';

        sDropDown1 = String.escapeSingleQuotes(sSelPullWorkQueue.trim());
        sDropDown2 = String.escapeSingleQuotes(sSelPullWorkView.trim());

        if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_VIEWOTHER_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1 AND Work_Queue_View_Name__c =: sAllOtherView';             
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEALL_HUM))
        {
            sTaskQuery += ' AND Owner_Queue__c =: sDropDown1';
            sCaseQuery += ' AND Owner_Queue__c =: sDropDown1';           
        }
        else if(viewType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUEVIEW_HUM))
        {            
            sTaskQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';
            sCaseQuery += ' AND Owner_Queue__c =:sDropDown1 And Work_Queue_View_Name__c =: sDropDown2';            
        }

        Integer recordsize=0;
                String sTaskQuery1 = sTaskQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC Limit 1';
                String sCaseQuery1 = sCaseQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC Limit 1';
                recordsize = pullWorkExecuteDBQuery(sTaskQuery1, sCaseQuery1, lstQueues, sDropDown1, sUserName, sDropDown2, sAllOtherView);
                if(recordsize==0){
                    bDisableApexMessage = true;
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, 'There are no work items left with the selected options. Please select new options and click the Pull Work button.'));
                }

    }
    /**
    * Test method to create and execute dynamic query
    * <p>
    * @return     Void
    * @param      string type of view
    */
    @TestVisible private void pullPriorityCreateAndExecute(String viewType)
    {

        if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM)
         {
            filterByPriority(viewType);
         }   
        else if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM)
         {
             filterByPriorityTasksOnly(viewType);
         }
        else if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM)
         {
             filterByPriorityCasesOnly(viewType);
         }
         else if(sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM)
         {
             filterByPriorityCriticalHighOnly(viewType);
         }
         else if (sSelPullWorkFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM)
         {
             filterByPriorityNormalOnly(viewType);
         }


    }
}