/************************************************************************************************************************
Apex class Name    : CaseTaskView_C_HUM
Version            : 1.0
Function           : Controller for Task List View page to display list of Case/Task
Created On         : October 10 2016
Test Class         : CaseTaskView_T_HUM

        Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Rajesh Keswani                44250                10/10/2016          Original Version
*   Rajesh Keswani                45290                10/28/2016          Defect fix 246256.0001
*   Rajesh Keswani                45583                11/01/2016          Defect fix 246731.0001
*   Vishal Verma                  46117                11/03/2016          Defect fix 246907.0001 - Updated the Case And Task Status
*   Sharan Shanmugam              46862                11/08/2016          Defect - 247767.0001 : Modified code to pick only active queues.
*   Vishal Verma                  46862                11/08/2016          Defect - 247459.0001 : Added NULL Check before addign CLassification and Intent to wrapper
*   Sharan Shanmugam              47105                11/09/2016          Defect - 247766.0001 : Modified code to not pick other QV.
*   Vishal Verma                                       11/17/2016          Defect - 249179.0001 : Added Else condition to pick Case Task when filtering with Age
*   Vamsi Kondragunta             49317                11/28/2016          Defect - 250188.0001 : Added Pending-transfer as value for status 
*   Rajesh Keswani                49947                12/06/2016          Defect fix to show date time in non military format
*   Harshith ML                   48234                12/07/2016          REQ - 288718 : CA Ticket 4996394: Update CaseTask Query to User ID
*   Vishal Verma                  57799                18/01/2017          REQ - 296754 : CA Ticket -6473729 : Added for Update query before updating the owner of Pulled Work Item
*   Rajesh Keswani                60675                02/06/2016          REQ - 300076 : CA Ticket -6533672 / 6554074 - Cases-Tasks tab not Refreshing appropriately.
*   Ranjeeth Nagishetty                                04/03/2017          REQ - 306332 : SF - TECH - Validate and Update CaseTaskView_VF_HUM
*   Amar Gadewar                                       03/21/2017          REQ - 304842 Ability to have a hover over on the Cases/Tasks tab on each Case And Task ID to show the last Comment made on the work item
*   Ranjeeth Nagishetty           89086                05/02/2017          Defect 276492.0001 Limit values updated in custom settings
*   Shreya Choodamani             90487                05/05/2017          REQ#: 311397 Updated the code to mimic the behavior of CCSupervisor for HP Supervisor
*   Sharan Shanmugam                                   06/13/2017          Defect fix 294841.0001 : Added booleans for HP specialist and supervisor
*   Prasanthi Kandula             199807               03/15/2018          REQ - 95977 Multiple Queue, View selections on Case/Task Page
*   Prasanthi Kandula             221277               04/26/2018          REQ - 360037  View all assigned inventory from multi picklist search
*   Pallavi R                     268909               08/01/2018          REQ 361926 - Work Task Type'Extension Request' added to the 'Work Task Count' column
*   Subhamay Mandal                                    04/24/2019          REQ - 396217 Quality - MIR - Ability for CSR to see remaining SLA field on the Case/Tasks tab (RF)
*   Muralidhar Kollu                                   10/16/2019          REQ - 410410 - Fix of ticket 8438259 the cases/tasks page isn't working correctly- the Supervisor only
*   Sumanth Nardela                                    03/12/2020          User Story - 731476 MF 9 Case & Tasks Tab - Image Received Date Column
*   Jasmeen Shangari                                   04/29/2020          Log error in Catch block to resolve Checkmarx error
*   Prudhvi Pamarthi                                   06/12/2020      US#800050 Close or Cancel two or more cases at once with case comments within CRM Service
*   AshokKumar Nutalapati                              07/09/2020          REQ - 1098063 Open Policy Member and Humana Pharmacy Tabs When Accessing Case from Cases/Tasks Page (RxRF)
*   AshokKumar Nutalapati                              07/20/2020          REQ - 1189747 Open Policy Member and Humana Pharmacy Tabs When Accessing Case from Cases/Tasks Page (CRMS)
*   Prudhvi Pamarthi                                   07/23/2020          US#1045266 Transfer two or more cases at once with case comments within CRM Service
*   Vinay Lingegowda                                   01/12/2021          USER STORY 1733817 PR00091574- IR 5050- Add New View option selection for case/tasks page - Specialist View (CRM)
*   Vinay Lingegowda                                   01/12/2021          USER STORY 1733853 PR00091574 - IR5050 - Add New View option to case/task page - supervisor view (Assigned) (CRM) 
*   Vinay Lingegowda                                   01/12/2021          USER STORY 1733859 PR00091574 - IR5050 - Add New View option to case/task page - supervisor view ( unassigned cases) (CRM)  
*   Vinay Lingegowda                                   01/22/2021          USER STORY 1733859 Defect Fix. 
*   Mithra Bharadwaj                                   02/09/2021          US#1522825 SonarQube Changes
*   Vinay Lingegowda                                   02/12/2021          USER STORY 1748357 PR00091574 - IR 5050 - Add a Transfer Reason in Case/Tasks page - Supervisor Bulk Transfer (CRM)
*   Vinay Lingegowda                                   02/18/2021          USER STORY 1748357 Defect fix DF-2437.
*   Vijaykumar                                         02/22/2021          User story 1956905 T1PRJ0001827- IR 5050 - Case/tasks page: Supervisor: Add new filter by Age option( CRM)
*   Vijay kumar                                        03/12/2021          US-2041570 T1PRJ0001827- IR 5050 - Add show/Hide columns on case/tasks page (CRM)
*   Vijaykumar                                         03/22/2021          User story 2041570 T1PRJ0001827- IR 5050 - Add show/Hide columns on case/tasks page (CRM)
*   Vinay Lingegowda                                   03/29/2021          USER STORY 2107574 T1PRJ0001827 - IR 5050 - Tech Debt - Clean up switch for past releases(CRM)
*   Vinay Lingegowda                                   04/28/2021          USER STORY 1970327 T1PRJ0001827- IR 5050 -  Ability for Supervisors to view Team's Inventory (CRM)
*   Vinay Lingegowda                                   05/17/2021          USER STORY 2033042 T1PRJ0001827 - IR 5050 - Refresh results on Pull Work ("All other Queue Views") (CRM)
*   Mithra Bharadwaj				                   06/22/2021	       US#2089563 Sonar Qube Changes
*   Mohammed Noor                              		   07/01/2021          US2273201 - Case Management Checkmarx Security violations
*   Gourab Tewary                                      07/23/2021          User Story 2503627: T1PRJ0001827 - IR 5050 - Tech Debt - Clean up switch for past releases(CRM)
*   Vinay Lingegowda                                   08/09/2021          USER STORY 1964685 T1PRJ0001827-IR 5050 - Update Show/Hide column options to Include Views (CRM)
*   Tummala Vijaya Lakshmi							   08/27/2021 		   USER STORY 2578074 - T1PRJ0002980/PR00095219 - MF 5 - Supervisor Case Management --added conditions for contract protected data mapping from CASETASKVIEW_SR_HUM js file	
*   Tummala Vijaya Lakshmi							   09/30/2021 		   USER STORY 2578074 - T1PRJ0002980/PR00095219 - MF 5 - Supervisor Case Management --added conditions for contract protected data mapping from CASETASKVIEW_SR_HUM js file	-CPD switch
***************************************************************************************************************************/
public with sharing class CaseTaskView_C_HUM
{
    // List of Transient Private Variables.
    private static transient final String caseQuery ='SELECT CaseNumber,Open_Work_Task_Count__c ,Total_Work_Task_Count__c, Work_Queue_View_Name__c, Case_Owner__c, Priority, Status, Open_ExtensionTask_Count__c, Total_ExtensionTask_Count__c,'
                                                        +'Case_Age__c, Case_Number__c, CTCI_List__r.Intent__r.Name,CTCI_List__r.Intent__c,Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c,' 
                                                        +'Re_Open_Case_Age__c, CTCI_List__r.Classification__r.Name,CTCI_List__c , CTCI_List__r.Classification__c,Days_Since_Last_Modified__c, Due_Date__c, Ownerid, '
                                                        +'Follow_Up_Due_Date__c, CreatedDate, Origin, Policy_Member__c,RecordTypeId,Policy_Member__r.Policy__c,Classification_Type__c,AccountId,Complaint__c,Interacting_About_Type__c, '
                                                        +'Call_Benefit_Category__c,Product__c,Created_By_Queue__c,Guided_Process_Flag__c,AutoRoute_Status__c,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Interacting_With_Name__c,G_A_Rights_Given__c,Contract_Protected_Case__c FROM Case where (Status IN : lstValidCaseStatus) ';
    
    private static transient final String caseQueryRF ='SELECT CaseNumber,Open_Work_Task_Count__c ,Total_Work_Task_Count__c, Work_Queue_View_Name__c, Case_Owner__c, Priority, Status, Open_ExtensionTask_Count__c, Total_ExtensionTask_Count__c,'
                                                        +'Case_Age__c, Case_Number__c, CTCI_List__r.Intent__r.Name,CTCI_List__r.Intent__c,Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c,' 
                                                        +'Re_Open_Case_Age__c, CTCI_List__r.Classification__r.Name,CTCI_List__c , CTCI_List__r.Classification__c,Days_Since_Last_Modified__c, Due_Date__c, Ownerid, '
                                                        +'(SELECT id, iscompleted, isviolated, TimeRemainingInMins, TimeSinceTargetInMins FROM CaseMilestones where iscompleted = false order by id limit 1),'
                                                        +'Follow_Up_Due_Date__c, CreatedDate, Origin, Policy_Member__c,RecordTypeId,Policy_Member__r.Policy__c,Classification_Type__c,AccountId,Complaint__c,Interacting_About_Type__c, '
                                                        +'Call_Benefit_Category__c,Product__c,Created_By_Queue__c,Guided_Process_Flag__c,AutoRoute_Status__c,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Interacting_With_Name__c,G_A_Rights_Given__c,Contract_Protected_Case__c FROM Case where (Status IN : lstValidCaseStatus) ';
                                                        
    private static transient final String taskQuery = 'SELECT QueueOrUserId__c ,Date_Time_Reopned__c , Subject,Open_Work_Task_Count__c,Total_Work_Task_Count__c, Classification__c,'
                                                       +'Work_Queue_View_Name__c,Re_Open_Task_Age__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, '
                                                       +'Ownerid, Owner_Queue__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task ' 
                                                       +'where (Status IN : lstValidTaskStatus) ';

    private static transient final list<String> lstValidTaskStatus = new list<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDING_HUM};
    private static transient final list<String> lstValidCaseStatus = new list<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGTASK_HUM,
                                                                                      GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGRESPONSE_HUM,GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGTRANSFER_HUM};
    private transient map<String,list<String>> mapGroupQueue = new map<String, list<String>>();
    private transient list<String> lstCatTempQueues = new list<String>();
    
    public map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new map<String, Work_Queue_Setup__c>([Select Name,OwnerId, id,Public_Group_Name__c, Work_Queue_Name__c 
                                                            from Work_Queue_Setup__c where IsActive__c = TRUE limit :GLOBAL_CONSTANT_HUM.TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM]);
    private static HUMSearchLimit__c objcasequeryLimit;
    private static  HUMSearchLimit__c objTaskqueryLimit;
    List<String>  lstCatTemp = new List<String>();
    public boolean isPDPPermissionSetUser {get; set;}
    public List<String> lstItemAge {get;set;}
    public boolean isRCCPermissionSetUsr {get; set;}
    public boolean bMyTeamInvChk {get; set;}
    public List<SelectOption> lstMyteamReportee {get; set;} 
    public boolean bswitch_US2578074 {get; set;}
    /**
    * Constructor of the Class
    * <p>    
    * @return     NA
    * @param      NA
    */
    public CaseTaskView_C_HUM()
    {
        bswitch_US2578074=HUMUtilityHelper.isCRMFunctionalityON('2578074');
        bPharmacySpecialistUser = false;
        bPharmacySupervisorUser = false;
        sSelectedAssign = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM;
        sSelPullWorkFilterBy = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM;
        objcasequeryLimit = new HUMSearchLimit__c();
        objTaskqueryLimit = New HUMSearchLimit__c();
        sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
        if(sPharmacyUser == GLOBAL_CONSTANT_HUM.PHARMACYSPECIALIST_USER_HUM)
        {
            bPharmacySpecialistUser = true;
        }
        else if(sPharmacyUser == GLOBAL_CONSTANT_HUM.PHARMACYSUPERVISOR_USER_HUM)
        {
            bPharmacySupervisorUser = true;
        }
        lstViewCategorydd1 = new List<String>();
        lstViewCategorydd2 = new List<String>();
        lstSelPullWorkQueue = new List<String>();
        lstSelectedView = new List<String>();
        lstSelPullWorkView = new List<String>();
        lstWorkItems = new List<String>();
        statusOptions = new List<SelectOption>();
        statusOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.HUM_CLOSED,GLOBAL_CONSTANT_HUM.HUM_CLOSED));
        statusOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.HUM_CANCELLED,GLOBAL_CONSTANT_HUM.HUM_CANCELLED));
        bEnableCloseCaseButton = HUMUtilityHelper.isCRMFunctionalityON('800050'); 
        bEnableTransferButton = HUMUtilityHelper.isCRMFunctionalityON('1045266');
        reRenderQueueSelectionPage = false;
        isPDPPermissionSetUser = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot');
        Filterval = '';
        lstSecurityView = new List<String>();
        lstItemAge = new List<String>();
        isRCCPermissionSetUsr = UtilityHelper_H_AHT_HUM.isUserHavingMedicareCustomerServiceAccess();
        bMyTeamInvChk = false;
    }
    public List<String> lstSecurityView {get;set;}
    public boolean reRenderQueueSelectionPage {get; set;} 
    public string userCurrentQueue{ get; set; }
    public string caseComment{ get; set; } 
    public List<SelectOption> statusOptions {get;set;}
    public String statusValue {get;set;}
    public String Filterval{get;set;}
    public Boolean bEnableCloseCaseButton {get;set;}
    public List<String> lstSelPullWorkQueue {get;set;}
    public List<String> lstSelPullWorkView{get;set;}
    public List<String> lstViewCategorydd1 {get;set;}
    public List<String> lstViewCategorydd2 {get;set;}
    public List<String> lstSelectedView {get;set;}
    public List<String> lstWorkItems {get;set;}
    public String sViewCategorydd1 {get;set;}
    public String sViewCategorydd2 {get;set;}
    public string sSelectedAssign {get; set;}
    public boolean bShowData {get; set;}
    public string sSelPullWorkFilterBy {Get; Set;}
    Public String sobjectId {Get; Set;}
    Public Static string sPharmacyUser{Get;Set;}
    public boolean bPharmacySpecialistUser {get; set;}
    public boolean bPharmacySupervisorUser {get; set;}
    public String accountTabId {get;set;}
    public String accountName {get;set;}
    public string PolicyId {get; set;}
    public String policyName {get;set;}
    public boolean bPolicyAccount {get; set;}
    public Static List<String> transferFailCases;
    public Boolean bEnableTransferButton {get;set;}
    
    /**
    * Method to populate SelectedView Dropdown
    * <p>
    * @return : list<SelectOption>
    * @param  : NA
    */
    public list<SelectOption> getSecurityView()
    {
        list<SelectOption> lstSecurityView = new list<SelectOption>();
        lstSecurityView.add(new SelectOption(GLOBAL_CONSTANT_HUM.General,GLOBAL_CONSTANT_HUM.General));
        lstSecurityView.add(new SelectOption(GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM,GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM));
        lstSecurityView.add(new SelectOption(GLOBAL_CONSTANT_HUM.HOAllOther,GLOBAL_CONSTANT_HUM.HOAllOther));
        if(bswitch_US2578074)
        lstSecurityView.add(new SelectOption(GLOBAL_CONSTANT_HUM.ContractProtectedData,GLOBAL_CONSTANT_HUM.ContractProtectedData));
        return lstSecurityView;
    }
    /**
    * Method Is called from UI when Pull work Button Is clicked. 
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : One pulled Item which mathces the input criteria 
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    */
    @RemoteAction 
    public static list<CaseTaskView_DTO_HUM> pullWorkItems(List<String> lstQueue, List<String> lstView, String sFilterBy)
    {
        list<CaseTaskView_DTO_HUM> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM>();
        Map<String,List<String>> mapQueueWithViews = new Map<String,List<String>>();
        try
        {   
            list<String> lstQueues = new list<String>();
            for(Group oGroup : [SELECT Name FROM Group WHERE Type =: GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM]) 
            {   
                lstQueues.add(oGroup.Name);
            }
            string sTaskQuery = TaskQuery +' AND Task_Owner__c in :lstQueues '; // Sample Task Query
            string sCaseQuery = caseQuery + ' AND Case_Owner__c in :lstQueues '; // Sample Case Query
            
            if(UtilityHelper_H_AHT_HUM.isMIRPilotUser()) {
                sCaseQuery = caseQueryRF + ' AND Case_Owner__c in :lstQueues '; // Sample Case Query
            }
           
            mapQueueWithViews = prepareMapOfQueueWithViews(lstView,mapQueueWithViews);
            Integer iMapSize = mapQueueWithViews.size();
            Integer iVal = 1;
            sCaseQuery += ' AND (';
            sTaskQuery += ' AND (';
            sCaseQuery = prepareCaseQuery(mapQueueWithViews,iMapSize, iVal, sCaseQuery,false);
            iMapSize = mapQueueWithViews.size();
            iVal = 1;
            sTaskQuery = prepareTaskQuery(mapQueueWithViews,iMapSize, iVal, sTaskQuery, false);  
            sCaseQuery += ') '; 
            sTaskQuery += ') '; 
            if(sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM) lstCaseTaskDTOWrapper = filterByDueDate(sTaskQuery , sCaseQuery , lstQueue , lstView ,sFilterBy , lstQueues);
           
            if(sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM || sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM 
            || sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM)
            {
                lstCaseTaskDTOWrapper = filterByAge(sTaskQuery , sCaseQuery , lstQueue , lstView , sFilterBy , lstQueues);
            }
            
            if(sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM 
                || sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM 
                || sFilterBy ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM || sFilterBy ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM)
            {
                lstCaseTaskDTOWrapper = filterByPriority(sTaskQuery , sCaseQuery , lstQueue , lstView , sFilterBy , lstQueues);
            }
            
            if(sFilterBy == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONCASE_HUM) 
            {
                lstCaseTaskDTOWrapper = filterByTaskByOldestCase(sTaskQuery , sCaseQuery , lstQueue , lstView , sFilterBy , lstQueues);
            }
    
            if(!lstCaseTaskDTOWrapper.isEmpty()) // If an item is pulled then update the owner and send it on UI
            {
                lstCaseTaskDTOWrapper = updateCaseTaskOwner(lstCaseTaskDTOWrapper[0] , lstQueue , lstView , sFilterBy);
                if(!lstCaseTaskDTOWrapper.isEmpty())return new list<CaseTaskView_DTO_HUM>{lstCaseTaskDTOWrapper[0]};
            } 
        }
        catch(Exception ex)
        {
                HUMExceptionHelper.logErrors(ex, 'CaseTaskView_C_HUM', 'pullWorkItems');
        }
        return lstCaseTaskDTOWrapper;        
    }
    
    /**
    * Prepare map with key as Queue and Value as selected views from page 
    * <p>
    * @return   Map<String,List<String>> 
    * @param    List<String> lstView, Map<String,List<String>> mapQueueWithViews
    */
    public static Map<String,List<String>> prepareMapOfQueueWithViews(List<String> lstView, Map<String,List<String>> mapQueueWithViews)
    {
        for (String sView : lstView)
        {
            List<String> lstSelectedViews = new List<String>();
            lstSelectedViews.add(sView.mid(0, sView.lastIndexOf('(')-1));
            if(mapQueueWithViews.containsKey(sView.mid(sView.lastIndexOf('(')+1,sView.length()-(sView.lastIndexOf('(')+1)-1 )))
            {
                List<String> lstAllViews = mapQueueWithViews.get(sView.mid(sView.lastIndexOf('(')+1,sView.length()-(sView.lastIndexOf('(')+1)-1 ));
                lstAllViews.addAll(lstSelectedViews);
                mapQueueWithViews.put(sView.mid(sView.lastIndexOf('(')+1,sView.length()-(sView.lastIndexOf('(')+1)-1 ) ,lstAllViews);
            }
            else
            {
                mapQueueWithViews.put(sView.mid(sView.lastIndexOf('(')+1,sView.length()-(sView.lastIndexOf('(')+1)-1 ),lstSelectedViews);
            } 
        }
        return mapQueueWithViews;
    }
    
    /**
    * Perform the DML on the pulled Workitem (Case/Task) to associate it to the logged in User
    * <p>
    * @return   NA 
    * @param    CaseTaskView_DTO_HUM : The record which was pulled 
    */
    public static List<CaseTaskView_DTO_HUM> updateCaseTaskOwner(CaseTaskView_DTO_HUM pulledCaseTask , List<String> lstQueue, List<String> lstView, String sFilterBy)
    {
        try
        {
            if(pulledCaseTask.sFilterType == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM) // Update Task
            {
                Task objTask = [Select QueueOrUserId__c from Task where Id = :pulledCaseTask.sCaseTaskId limit 1 for update];
                if (!String.isBlank(objTask.QueueOrUserId__c) && objTask.QueueOrUserId__c.startswith(GLOBAL_CONSTANT_HUM.CASEMGMT_GROUPID_INITIAL_HUM))
                {
                    objTask = assignTaskValues(objTask ,  [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c);           
                    Database.update(objTask,true);
                    return new List<CaseTaskView_DTO_HUM> {pulledCaseTask};
                }
                else
                {
                    return pullWorkItems(lstQueue , lstView, sFilterBy);
                }
            }
            else // Update Case
            {
                Case objCase = [Select OwnerId , lastmodifieddate from Case where Id = :pulledCaseTask.sCaseTaskId limit 1 for update];
                if (!String.isBlank(objCase.OwnerId) && String.valueof(objCase.OwnerId).startswith(GLOBAL_CONSTANT_HUM.CASEMGMT_GROUPID_INITIAL_HUM))
                {
                    objCase = assignCaseValues(objCase);
                    Database.update(objCase,true); 
                    return new List<CaseTaskView_DTO_HUM> {pulledCaseTask};     
                }
                else
                {
                    return pullWorkItems(lstQueue , lstView, sFilterBy);
                }
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseTaskView_C_HUM','updateCaseTaskOwner');
        }
        return new List<CaseTaskView_DTO_HUM> {};
    }
    
      /**
    * Method is used to pull Task on the oldest Case.  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM> filterByTaskByOldestCase(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter ,list<String> lstQueues)
    {
        objcasequeryLimit = HUMSearchLimit__c.getInstance('CASEQUERYLIMIT');
        list<CaseTaskView_DTO_HUM> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM>();
        map<Id,Task> mapWhatIdTask = new map<Id,Task>(); // Map to hold the Case Id and the oldest Task  associated to it.
        sTaskQuery +=' order by Createddate ASC NULLS LAST LIMIT ' +Integer.valueof(objcasequeryLimit.Search_Limit__c); // Pull the oldest Case
        for(Task t : Database.query(sTaskQuery))
        {
            if(!mapWhatIdTask.containskey(t.WhatId)) mapWhatIdTask.put(t.WhatId , t);   
        }
        if(!mapWhatIdTask.keyset().isEmpty())
        {
            // Pull the oldest case (only 1) and then from the Map get the Task associated to it. Map.containskey is not needed as the we are only querying the Cases which are laready in Map
            Task taskinstance = mapWhatIdTask.get([Select Id from Case where ID IN :mapWhatIdTask.keyset() order by Createddate ASC LIMIT 1].Id);
            lstCaseTaskDTOWrapper = populateCaseTaskWrapper(new list<Task> {taskinstance} ,  new list<Case>{}  , true);
        }
        return lstCaseTaskDTOWrapper;
    }
    
    /**
    * Method is used to pull work on basis of Due Date .  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM> filterByDueDate(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter ,list<String> lstQueues)
    {
        list<CaseTaskView_DTO_HUM> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM>();
        sTaskQuery +=' order by ActivityDate ASC NULLS LAST , Createddate ASC NULLS LAST LIMIT 1 '; // Pull 1 Task
        sCaseQuery += ' order by Follow_Up_Due_Date__c ASC NULLS LAST, Createddate ASC NULLS LAST LIMIT 1 '; // Pull 1 Case
        lstCaseTaskDTOWrapper = populateCaseTaskWrapper(Database.Query(sTaskQuery) ,  Database.Query(sCaseQuery)  , true);
        if(lstCaseTaskDTOWrapper.size() >1) // Pull the one which has higher due date
        {
            if(String.isBlank(lstCaseTaskDTOWrapper.get(0).dDueDate) && String.isBlank(lstCaseTaskDTOWrapper.get(1).dDueDate))
            {
                lstCaseTaskDTOWrapper= parsePulledItems(lstCaseTaskDTOWrapper); 
            }
            else if(String.isBlank(lstCaseTaskDTOWrapper.get(0).dDueDate) && !String.isBlank(lstCaseTaskDTOWrapper.get(1).dDueDate))
            {
                lstCaseTaskDTOWrapper.remove(0);
            }
            else if(!String.isBlank(lstCaseTaskDTOWrapper.get(0).dDueDate) && String.isBlank(lstCaseTaskDTOWrapper.get(1).dDueDate))
            {
                lstCaseTaskDTOWrapper.remove(1);
            }
            else if(!String.isBlank(lstCaseTaskDTOWrapper.get(0).dDueDate) && !String.isBlank(lstCaseTaskDTOWrapper.get(1).dDueDate))
            {
                if((lstCaseTaskDTOWrapper.get(0)).dDueDate == (lstCaseTaskDTOWrapper.get(1)).dDueDate ) 
                {
                    lstCaseTaskDTOWrapper= parsePulledItems(lstCaseTaskDTOWrapper); 
                }
                else
                {
                    if(convertStringtoDate(lstCaseTaskDTOWrapper.get(0).dDueDate) > convertStringtoDate(lstCaseTaskDTOWrapper.get(1).dDueDate))
                    {
                        lstCaseTaskDTOWrapper.remove(0);
                    }
                    else if(convertStringtoDate(lstCaseTaskDTOWrapper.get(0).dDueDate) <convertStringtoDate(lstCaseTaskDTOWrapper.get(1).dDueDate))
                    {
                        lstCaseTaskDTOWrapper.remove(1);
                    }
                }               
            }
        }
        return lstCaseTaskDTOWrapper;
    }
    
      /**
    * Method is used to pull work on basis of Priority . Priority can be set as Critical High and Normal  
    * <p>
    * @return   Date : Date value of the input Sting 
    * @param    String : String value of Date
    */
    @TestVisible
    private static date convertStringtoDate(String sDate)
    {
        List<String> strList = sDate.split('/');
        if(!strList.isEmpty() && strList.size() == 3)
        {
            return Date.valueof(strList[2]+'-'+strList[0]+'-'+strList[1]);
        }
        return null;
    }
    
     /**
    * Method is used to pull work on basis of Priority . Priority can be set as Critical High and Normal  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM> filterByPriority(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter, list<String> lstQueues)
    {
        list<CaseTaskView_DTO_HUM> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM>();
        String sTsQuery =''; // Temp Query String which will change on the basis of Priority Seleceted from UI
        String sCsQuery = '';  // Temp Query String which will change on the basis of Priority Seleceted from UI
        boolean bCritical = false; // Boolean to identify if we pulled Critical record
        boolean bHigh = false; // Boolean to identify if we pulled High record
        list<Task> lstTask = new list<Task>(); 
        list<Case> lstCase = new list<Case>();
        
        // Filter for Critical Cases
        if(sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM 
        || sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM || sFilter ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM ) 
        {
            sTsQuery = sTaskQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Critical Task
            sCsQuery = sCaseQuery + ' AND Priority=\'Critical\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Critical Case
            if(sFilter != GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM ) lstTask = Database.Query(sTsQuery);
            if(sFilter != GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM) lstCase = Database.Query(sCsQuery);
            bCritical = ((sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM  || sFilter ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM)? 
                        (lstTask.size() > 0 || lstCase.size() > 0 ) : sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM ? 
                        lstCase.size() > 0 : sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM ? lstTask.size() > 0 : false);
        }
        
        // Filter for High Cases
        if(!bCritical && (sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM || sFilter ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM || sFilter ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM) ) 
        {
            sTsQuery = sTaskQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 High Task
            sCsQuery = sCaseQuery + ' AND Priority=\'High\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 High Case
            if(sFilter != GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM ) lstTask = Database.Query(sTsQuery);
            if(sFilter != GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM) lstCase = Database.Query(sCsQuery);
            bHigh = ((sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM  || sFilter ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM)? 
                    (lstTask.size() > 0 || lstCase.size() > 0 ) : sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM ? 
                    lstCase.size() > 0 : sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM ? lstTask.size() > 0 : false); 
        }
        
        // Filter for Normal Cases
        if(( !bCritical && !bHigh && (sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM || sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM || sFilter ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM)) ||  sFilter ==  GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM ) 
        {
            sTsQuery = sTaskQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Normal Task
            sCsQuery = sCaseQuery + ' AND Priority=\'Normal\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Normal Case
            if(sFilter != GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM ) lstTask = Database.Query(sTsQuery);
            if(sFilter != GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM) lstCase = Database.Query(sCsQuery);
        }
        lstCaseTaskDTOWrapper = populateCaseTaskWrapper(lstTask ,  lstCase  , true);
        lstCaseTaskDTOWrapper= parsePulledItems(lstCaseTaskDTOWrapper);
        return lstCaseTaskDTOWrapper;
    }
    
    /**
    * Method is used to pull work on basis of Age  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM> filterByAge(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter, list<String> lstQueues)
    {
        list<CaseTaskView_DTO_HUM> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM>();
        sTaskQuery +=' order by Age_Since_Created__c DESC NULLS LAST , Createddate ASC NULLS LAST LIMIT 1'; // Pull 1 Task 
        sCaseQuery += ' order by Case_Age__c DESC NULLS LAST ,  Reopened__c DESC NULLS LAST  , Createddate ASC NULLS LAST LIMIT 1'; // Pull 1 Case
        if(sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM) //Pull both Case and Task.
        {
            lstCaseTaskDTOWrapper = populateCaseTaskWrapper(Database.Query(sTaskQuery) ,  Database.Query(sCaseQuery)  , true); 
            if(lstCaseTaskDTOWrapper.size() >1) // Pull the one record which has higher Age
            {
                if((lstCaseTaskDTOWrapper.get(0)).decCaseAge == (lstCaseTaskDTOWrapper.get(1)).decCaseAge ) 
                {
                    lstCaseTaskDTOWrapper = parseReopenItems(lstCaseTaskDTOWrapper);  
                }
                else if((lstCaseTaskDTOWrapper.get(0)).decCaseAge > (lstCaseTaskDTOWrapper.get(1)).decCaseAge)
                {
                    lstCaseTaskDTOWrapper.remove(1);
                }
                else if((lstCaseTaskDTOWrapper.get(0)).decCaseAge < (lstCaseTaskDTOWrapper.get(1)).decCaseAge )
                {
                    lstCaseTaskDTOWrapper.remove(0);
                }
            }
        }
        if(sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM ) lstCaseTaskDTOWrapper = populateCaseTaskWrapper(Database.Query(sTaskQuery) ,  new list<Case>{}  , true);
        if(sFilter == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM) lstCaseTaskDTOWrapper = populateCaseTaskWrapper(new list<Task>{} ,  Database.Query(sCaseQuery)  , true);
        
        return lstCaseTaskDTOWrapper;
    }
    
    
    /**
    * Method is called to parse the Wrapper on basis of Reopen Item Age
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of one record  on the basis of reopen item age
    * @param    list<CaseTaskView_DTO_HUM> : List of all the pulled wrapper records
    */
    private static list<CaseTaskView_DTO_HUM>  parseReopenItems( list<CaseTaskView_DTO_HUM>  lstCaseTaskDTOWrapper)
    {
        if(lstCaseTaskDTOWrapper.get(0).decReopenWorkItemAge > lstCaseTaskDTOWrapper.get(1).decReopenWorkItemAge)  
        {
            lstCaseTaskDTOWrapper.remove(1);
        }
        else if(lstCaseTaskDTOWrapper.get(0).decReopenWorkItemAge < lstCaseTaskDTOWrapper.get(1).decReopenWorkItemAge ) 
        {
            lstCaseTaskDTOWrapper.remove(0);
        }
        else if((lstCaseTaskDTOWrapper.get(0)).decReopenWorkItemAge == (lstCaseTaskDTOWrapper.get(1)).decReopenWorkItemAge ) 
        {
            lstCaseTaskDTOWrapper= parsePulledItems(lstCaseTaskDTOWrapper);
        }  
        return lstCaseTaskDTOWrapper;
    }
    
    
    /**
    * Method is called on parse the fetched records. If we have more than 1 record (When fetching both Case and Task) then we have to Pull Task before Case.
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of one pulled wrapper records 
    * @param    list<CaseTaskView_DTO_HUM> : List of all the pulled wrapper records
    */
    private static list<CaseTaskView_DTO_HUM>  parsePulledItems( list<CaseTaskView_DTO_HUM>  lstCaseTaskDTOWrapper)
    {
        if(!lstCaseTaskDTOWrapper.isEmpty() && lstCaseTaskDTOWrapper.size() > 1)
        {
            if(lstCaseTaskDTOWrapper.get(0).sFilterType  == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM)
            {
                lstCaseTaskDTOWrapper.remove(1);
            }
            else if(lstCaseTaskDTOWrapper.get(1).sFilterType  == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM)
            {
                lstCaseTaskDTOWrapper.remove(0);    
            }
        }
        return lstCaseTaskDTOWrapper;
    }
    
    
    /**
    * Method is called on load of page to populate Case Task Wrapper records.
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of fetched wrapper records 
    * @param    NA
    */
    @RemoteAction
    public static list<CaseTaskView_DTO_HUM> fetchCaseTask(Boolean bPerformFilter, List<string> lstQueueData, List<String> lstQueueValue, List<String> lstViewVlue, List<String> lstFilterValue, string sAssignWorkValue, string sSecFilter, List<string> lstItemAgeValue, List<String> lstTeamMembers) 
    {   
        String filterValue;
        Boolean bMyTeamInvChk = (lstTeamMembers != null && !lstTeamMembers.isEmpty()) ? true : false;
        if(lstFilterValue.contains(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM) || (lstFilterValue.contains(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM) && lstFilterValue.contains(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM)))
            filterValue = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM;
        else if(lstFilterValue.contains(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM) && !(lstFilterValue.contains(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM) || lstFilterValue.contains(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM)))
            filterValue = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM;
        else
            filterValue = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM;
        list<CaseTaskView_DTO_HUM> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM>();
        Map<String,List<String>> mapQueueWithViews = new Map<String,List<String>>();
        try
        {
            objcasequeryLimit = HUMSearchLimit__c.getInstance('CASEQUERYLIMIT');
            objTaskqueryLimit= HUMSearchLimit__c.getInstance('TASKQUERYLIMIT'); 
            sPharmacyUser =  Pharmacy_H_HUM.isPharmacyUser();  
            list<String> lstQueues = new list<String>();
            Id iCurrentUserId = UserInfo.getUserId();
            string sTaskQuery = TaskQuery;
            string sCaseQuery = caseQuery;
            
            if(UtilityHelper_H_AHT_HUM.isMIRPilotUser()) {
                sCaseQuery = caseQueryRF;
            }
            
            String sUserName = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
            if((sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM) && (String.isNOTBlank(sAssignWorkValue) && sAssignWorkValue == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM))  
            {
                sTaskQuery +=' AND (Task_Owner__c in :lstQueueData OR QueueOrUserId__c =:iCurrentUserId)';
                sCaseQuery += ' AND (Case_Owner__c in :lstQueueData OR OwnerId =:iCurrentUserId)';             
            }
            
            else if((sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM) && (String.isNOTBlank(sAssignWorkValue) && sAssignWorkValue == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM))
            {
                sTaskQuery += ' AND Task_Owner__c in :lstQueueData';
                sCaseQuery += ' AND Case_Owner__c in :lstQueueData';
            }
            else if(bMyTeamInvChk && (sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM)){
                sTaskQuery += ' AND Owner_Queue__c in :lstQueueValue AND QueueOrUserId__c in :lstTeamMembers';
                sCaseQuery += ' AND Owner_Queue__c in :lstQueueValue AND OwnerId in :lstTeamMembers';
                bPerformFilter = false;
            }
            else 
            {
                sTaskQuery += ' AND QueueOrUserId__c =:iCurrentUserId';
                sCaseQuery += ' AND OwnerId =:iCurrentUserId';
            }

            if(lstItemAgeValue!=null && !lstItemAgeValue.isEmpty()){
                sCaseQuery += lstItemAgeValue[0];
                sTaskQuery += lstItemAgeValue[1];
             }


            if((sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM) && String.isNOTBlank(sSecFilter))  
            {
                boolean bswitch_US2578074=HUMUtilityHelper.isCRMFunctionalityON('2578074');
                if(bswitch_US2578074){
                if(sSecFilter == 'General')
                sCaseQuery += ' AND (General_Case__c = true)'; 
                else if(sSecFilter == 'Dental')
                sCaseQuery += ' AND (HO_Dental_Case__c = true)'; 
                else if(sSecFilter == 'Medical')
                sCaseQuery += ' AND (HO_Medical_Case__c = true)';
                else if(sSecFilter == 'GeneralDental')
                sCaseQuery += ' AND (General_Case__c = true OR HO_Dental_Case__c = true)';
                else if(sSecFilter == 'GeneralMedical')
                sCaseQuery += ' AND (General_Case__c = true OR HO_Medical_Case__c = true)';
                else if(sSecFilter == 'DentalMedical')
                sCaseQuery += ' AND (HO_Medical_Case__c = true OR HO_Dental_Case__c = true)';
                else if(sSecFilter == 'Contract Protected Data')
                sCaseQuery += ' AND (Contract_Protected_Case__c = \'1\')'; 
                else if(sSecFilter == 'GeneralDentalMedical')
                sCaseQuery += ' AND (General_Case__c = true OR HO_Dental_Case__c = true OR HO_Medical_Case__c = true)'; 
                else if(sSecFilter == 'ContractGeneral')
                sCaseQuery += ' AND (General_Case__c = true OR Contract_Protected_Case__c = \'1\')'; 
                else if(sSecFilter == 'ContractDental')
                sCaseQuery += ' AND (HO_Dental_Case__c = true OR Contract_Protected_Case__c = \'1\')'; 
                else if(sSecFilter == 'ContractMedical')
                sCaseQuery += ' AND (HO_Medical_Case__c = true OR Contract_Protected_Case__c = \'1\')'; 
                else if(sSecFilter == 'ContractGeneralDental')
                sCaseQuery += ' AND (General_Case__c = true OR HO_Dental_Case__c = true OR Contract_Protected_Case__c = \'1\')'; 
                else if(sSecFilter == 'ContractGeneralMedical')
                sCaseQuery += ' AND (General_Case__c = true OR HO_Medical_Case__c = true OR  Contract_Protected_Case__c = \'1\')'; 
                else if(sSecFilter == 'ContractDentalMedical')
                sCaseQuery += ' AND (HO_Dental_Case__c = true OR HO_Medical_Case__c = true OR Contract_Protected_Case__c = \'1\')'; 
                } else{
                 
                if(sSecFilter == 'General')
                sCaseQuery += ' AND (General_Case__c = true OR Contract_Protected_Case__c = \'1\')';  
                else if(sSecFilter == 'Dental')
                sCaseQuery += ' AND (HO_Dental_Case__c = true)'; 
                else if(sSecFilter == 'Medical')
                sCaseQuery += ' AND (HO_Medical_Case__c = true)';
                else if(sSecFilter == 'GeneralDental')
                sCaseQuery += ' AND (General_Case__c = true OR HO_Dental_Case__c = true)';
                else if(sSecFilter == 'GeneralMedical')
                sCaseQuery += ' AND (General_Case__c = true OR HO_Medical_Case__c = true)';
                else if(sSecFilter == 'DentalMedical')
                sCaseQuery += ' AND (HO_Medical_Case__c = true OR HO_Dental_Case__c = true)';
                }
            } 
            if(!bPerformFilter)
            {
                if(bMyTeamInvChk && (sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM)){
                      HUMSearchLimit__c objTeamCaseLimit = HUMSearchLimit__c.getInstance('TEAMCASEQUERYLIMIT');
                      HUMSearchLimit__c objTeamTaskLimit = HUMSearchLimit__c.getInstance('TEAMTASKQUERYLIMIT');
                    if(filterValue == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM){
                        sCaseQuery += ' Limit '+ Integer.valueof(objTeamCaseLimit.Search_Limit__c);
                        sTaskQuery += ' Limit '+ Integer.valueof(objTeamTaskLimit.Search_Limit__c);  
                    }else if (filterValue == GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM){
                        sCaseQuery += ' Limit '+ Integer.valueof(objTeamCaseLimit.Search_Limit__c);
                        sTaskQuery = null;
                    }else{
                        sCaseQuery = null;
                        sTaskQuery += ' Limit '+ Integer.valueof(objTeamTaskLimit.Search_Limit__c);
                    }
                }else{
                 sTaskQuery += ' Limit ' +Integer.valueof(objTaskqueryLimit.Search_Limit__c); 
                 sCaseQuery += '  Limit ' +Integer.valueof(objcasequeryLimit.Search_Limit__c); 
                }
            }
            if(bPerformFilter)
            {
                Boolean bShowAllCasesTasks = false;
                Boolean bFilterOnlyByOwnerQueue = false;
                String  sQVValue ='';
                Boolean isAllQVSelect = false;
                Integer qSize = lstViewVlue.size();
                Map<String, Object> mapParsedData = new Map<String, Object>();
                if (qSize >= 1 && lstViewVlue[qSize - 1].contains('CTAD-')){
                    sQVValue=lstViewVlue[qSize - 1].replace('CTAD-','');
                    lstViewVlue.remove(qSize - 1);
                    isAllQVSelect = true;
                    mapParsedData = (Map<String, Object>) JSON.deserializeUntyped(sQVValue);
                }
                mapQueueWithViews = prepareMapOfQueueWithViews(lstViewVlue,mapQueueWithViews);
                Integer iMapSize = mapQueueWithViews.size();
                Integer iVal = 1;
                if(lstQueueValue.contains('All') && (mapQueueWithViews.containsKey('All') || ((sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM) && mapQueueWithViews.containsKey('Al')))) bShowAllCasesTasks = true;
                if((!lstQueueValue.contains('All')) && (mapQueueWithViews.containsKey('All') || ((sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM) && mapQueueWithViews.containsKey('Al')))) bFilterOnlyByOwnerQueue = true;
                //if(sAssignWorkValue == 'Unassigned') bFilterOnlyByOwnerQueue = false;
                if(filterValue == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM)
                { 
                    if(!bShowAllCasesTasks)
                    {  
                        sCaseQuery += ' AND (';
                        sTaskQuery += ' AND (';
                        sCaseQuery = prepareCaseQuery(mapQueueWithViews,iMapSize, iVal, sCaseQuery, bFilterOnlyByOwnerQueue);
                        iMapSize = mapQueueWithViews.size();
                        iVal = 1;
                        sTaskQuery = prepareTaskQuery(mapQueueWithViews,iMapSize, iVal, sTaskQuery, bFilterOnlyByOwnerQueue);  
                        if(isAllQVSelect){
                            sCaseQuery = prepareQueryforAllQViews(sCaseQuery, mapParsedData, iMapSize > 0);
                            sTaskQuery = prepareQueryforAllQViews(sTaskQuery, mapParsedData, iMapSize > 0);
                        } 
                        sCaseQuery += ')'; 
                        sTaskQuery += ')'; 
                    }
                    sCaseQuery += ' Limit ' +Integer.valueof(objcasequeryLimit.Search_Limit__c); 
                    sTaskQuery += ' Limit ' +Integer.valueof(objTaskqueryLimit.Search_Limit__c);
                }  
                else if(filterValue == GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM)
                {  
                    if(!bShowAllCasesTasks) {
                    sCaseQuery += ' AND (';
                    sCaseQuery = prepareCaseQuery(mapQueueWithViews,iMapSize, iVal, sCaseQuery, bFilterOnlyByOwnerQueue);
                    if(isAllQVSelect){
                        sCaseQuery = prepareQueryforAllQViews(sCaseQuery, mapParsedData, iMapSize > 0);
                    }
                    sCaseQuery += ')';
                    }
                    sCaseQuery += ' Limit ' +Integer.valueof(objcasequeryLimit.Search_Limit__c); 
                    sTaskQuery = null;
                }   
                else
                {
                    if(!bShowAllCasesTasks) {
                    sTaskQuery += ' AND (';
                    sTaskQuery = prepareTaskQuery(mapQueueWithViews,iMapSize, iVal, sTaskQuery, bFilterOnlyByOwnerQueue);
                    if(isAllQVSelect){
                        sTaskQuery = prepareQueryforAllQViews(sTaskQuery, mapParsedData, iMapSize > 0);
                    }
                    sTaskQuery += ')'; 
                    }
                    sCaseQuery = null; 
                    sTaskQuery += ' Limit ' +Integer.valueof(objTaskqueryLimit.Search_Limit__c); 
                } 
            }
            if(string.isNotBlank(sTaskQuery) && string.isNotBlank(sCaseQuery)) lstCaseTaskDTOWrapper = populateCaseTaskWrapper(Database.Query(sTaskQuery), Database.Query(sCaseQuery), false);
            else if(string.isNotBlank(sTaskQuery) && sCaseQuery == null) lstCaseTaskDTOWrapper = populateCaseTaskWrapper(Database.Query(sTaskQuery) , new List<Case> {}, false);
            else if(string.isNotBlank(sCaseQuery) && sTaskQuery == null) lstCaseTaskDTOWrapper = populateCaseTaskWrapper(new List<Task> {}, Database.Query(sCaseQuery), false);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseTaskView_C_HUM', 'fetchCaseTask');
        }          
        return lstCaseTaskDTOWrapper;
    }
    
    /**
    * Method is used to prepare query on Case object when Case is selected in FilterBy section.
    * <p>
    * @return   String    
    * @param    Map<String,List<String>> : Map with key as Queue and Value being list of Views selected on page
    * @param    Integer  : Integer with Map size
    * @param    Integer
      @param    String : Case query
    */
    public static String prepareCaseQuery(Map<String,List<String>> mapQueueWithViews,Integer iMapSize, Integer iVal, String sCaseQuery, Boolean bFilterByOwnerQueue)
    {
        Boolean flag = false;
        for(String oQueue : mapQueueWithViews.keyset())
        {
            String sQueueValue = oQueue;
            Integer iInnerloop = 1;
            sCaseQuery +=' ( Owner_Queue__c = \'' + sQueueValue + '\'  ';
            List<String> lstQueueRelatedViews = mapQueueWithViews.get(oQueue);
            Integer iViewSize = lstQueueRelatedViews.size();
            flag = false;
            if (bFilterByOwnerQueue == true) sCaseQuery += ')';
            for(String oViewName : lstQueueRelatedViews)
            {
                if(!bFilterByOwnerQueue)
                {
                    if(flag == false){
                        sCaseQuery += ' AND ';
                        flag = true;
                    }
                    String sViewVal = oViewName;
                    if(iViewSize == 1)
                    {
                        sCaseQuery += 'Work_Queue_View_Name__c IN (\'' + sViewVal + '\'))' ;
                        
                    }
                    else
                    {
                        if(iInnerloop == 1)
                        {
                            sCaseQuery +=  'Work_Queue_View_Name__c IN (\''+ sViewVal +'\'' ;
                        }
                        if(iViewSize  == iInnerloop )
                        {
                            sCaseQuery +=  ',\''+ sViewVal +'\'))';
                        }
                        if(iInnerloop !=1 && iViewSize != iInnerloop )
                        {
                            sCaseQuery +=  ',\''+ sViewVal +'\'';
                        }
                    }
                    iInnerloop++;   
                } 
            }
            if(iMapSize != iVal)
            {
                sCaseQuery += 'OR'; 
            }
            iVal ++;
        }
        return sCaseQuery;  
    }
    
    /**
    * Method is used to prepare query on Task object when Task is selected in FilterBy section.
    * <p>
    * @return   String    
    * @param    Map<String,List<String>> : Map with key as Queue and Value being list of Views selected on page
    * @param    Integer  : Integer with Map size
    * @param    Integer
      @param    String : Task query
    */
    public static String prepareTaskQuery(Map<String,List<String>> mapQueueWithViews,Integer iMapSize, Integer iVal, String sTaskQuery,Boolean bFilterByOwnerQueue)
    {
        Boolean flag = false;
        for(String oQueue : mapQueueWithViews.keyset())
        {
            String sQueueValue = oQueue;
            Integer iInnerloop = 1;
            sTaskQuery +=' ( Owner_Queue__c = \'' + sQueueValue + '\' ';
            List<String> lstQueueRelatedViews = mapQueueWithViews.get(oQueue);
            Integer iViewSize = lstQueueRelatedViews.size();
            flag = false;
            if (bFilterByOwnerQueue  == true) sTaskQuery += ')';
            for(String oViewName : lstQueueRelatedViews)
            {
                if(!bFilterByOwnerQueue)
                {
                    if(flag == false){
                        sTaskQuery += ' AND ';
                        flag = true;
                    }
                String sViewVal = oViewName;
                if(iViewSize == 1)
                {
                    sTaskQuery += 'Work_Queue_View_Name__c IN (\'' + sViewVal + '\'))' ;
                    
                }
                else
                {
                    if(iInnerloop == 1)
                    {
                        sTaskQuery +=  'Work_Queue_View_Name__c IN (\''+ sViewVal +'\'' ;
                    }
                    if(iViewSize  == iInnerloop )
                    {
                        sTaskQuery +=  ',\''+ sViewVal +'\'))';
                    }
                    if(iInnerloop !=1 && iViewSize != iInnerloop )
                    {
                        sTaskQuery +=  ',\''+ sViewVal +'\'';
                    }
                }
                iInnerloop++;    
                }
            }
            if(iMapSize != iVal)
            {
                sTaskQuery += 'OR'; 
            }
            iVal ++;
        } 
        return sTaskQuery;
    }

    /**
    * Method is used to prepare query on Case/Task object when All queue view is selected with other options.
    * <p>
    * @return   String  
    * @param    String : Case/Task query  
    * @param    Map<String,object> : Json parsed Data on queue view selection
    * @param    boolean: Queue view size is > 0 
    */
    private static string prepareQueryforAllQViews(String sQuery, Map<String, Object> mapParsedData, Boolean isOldSelection){
        integer count = 0;
        for(String strwQ : mapParsedData.keyset()){
            if(isOldSelection || (!isOldSelection && count >= 1 )){
                sQuery += 'OR ';  
            }
            List<Object> temp = (List<Object>)mapParsedData.get(strwQ);
            String strQV = String.join(temp,'\',\'');
            sQuery += '( Owner_Queue__c =\'' + strwQ + '\' AND Work_Queue_View_Name__c NOT IN (\''+ strQV +'\'))';
            count++;
        }
        return sQuery;
    }

    /**
    * Method is used to assign values to CaseTask wrapper which will be shown on UI. The method is also used to assign the values when Pull Work Button is clicked.
    * <p>
    * @return   list<CaseTaskView_DTO_HUM> : List of processed wrapper records 
    * @param    list<Case> : List of Case that needs to be added to wrapper.
    * @param    list<Task> : List of task that needs to be added to wrapper.
    * @param    Boolean    : Boolean to identify if Pull Work is clicked to fetch the unassigned work
    */
    private static list<CaseTaskView_DTO_HUM> populateCaseTaskWrapper (list<Task> lstTaskResults, list<Case> lstCaseResults , boolean isPulled)
    {
        list<CaseTaskView_DTO_HUM> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM>();
        string sInteractngWith ='';
        String sInteractngWithId = '';
        String sInteractingAbout = '';
        String sCurrentQueue =  [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c;
        decimal decCaseAge = 0; 
        list<ID> lstWhatID = new list<ID>();
        map<Id, Case> mapCase = new map<Id, Case>(); // Map to hold the Cases associated with Tasks. We need this map to populate Interacting details for Task Wrapper Records 
        String sUserName = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
        String sPharmacyUser =  Pharmacy_H_HUM.isPharmacyUser();
        Boolean bTransfer;
        Set<String> caseSet = new Set<String>();
        if(transferFailCases != Null && !transferFailCases.isEmpty())
        caseSet.addAll(transferFailCases);
        
        for(Task oTask : lstTaskResults) // Loop over the Tasks to get the Case Id for the Task
        {
            lstWhatID.add(oTask.WhatId);
        }
        
        if(!lstWhatID.isEmpty()) mapCase = new map<Id, Case>([select Id, Interacting_With__c, Interacting_With__r.Name, Account.Name,Case_Age__c  from Case WHERE ID IN : lstWhatID]);
        
         //Case Received Date logic starts
        set<string> setCaseIds = new set<string>();
        for(Case oCase : lstCaseResults){
            setCaseIds.add(oCase.Id);
        }
        for(Task oTask : lstTaskResults){
            if(!string.isBlank(oTask.WhatId) && String.valueof(oTask.WhatId).startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SFDCCASEID_HUM))
                setCaseIds.add(oTask.WhatId);
        }
        map<Id,string> mapCaseToReceiveddate = prepareCaseReceivedData(setCaseIds);
        //Case Received Date logic ends
        
        for(Case oCase : lstCaseResults) // Loop over the Cases to prepare the wrapper
        {
            if(isPulled && !String.valueOf(oCase.OwnerId).startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM)) oCase = assignCaseValues(oCase);// If Pull work
           
            String sOpenWorkTasks = HumConstants.HUMWorkTask + ' '+ (oCase.Open_Work_Task_Count__c != null ? String.valueof(oCase.Open_Work_Task_Count__c) : String.valueof(0)) +' ( of '+ (oCase.Total_Work_Task_Count__c != null ? 
                                     String.valueof(oCase.Total_Work_Task_Count__c) : String.valueof(0)) +' )';
            String sOpenExtTasks = HumConstants.HUMExtensionLabel + ' ' + (oCase.Open_ExtensionTask_Count__c != null ? String.valueof(oCase.Open_ExtensionTask_Count__c) : String.valueof(0)) +' ( of '+ (oCase.Total_ExtensionTask_Count__c != null ? 
                                    String.valueof(oCase.Total_ExtensionTask_Count__c) : String.valueof(0)) +' )';
            
            String sTimeSpentOnCase = (null != oCase.CaseMilestones && !oCase.CaseMilestones.isEmpty()) ? UtilityHelper_H_AHT_HUM.formatMinutes(oCase.CaseMilestones.get(0)) : ''; //MIR sla value.
            String receiveddate = (mapCaseToReceiveddate.containskey(oCase.Id))? mapCaseToReceiveddate.get(oCase.Id): '';
            Map<string, string> caseData = new Map<string,string>();
            bTransfer = false;
            //wrapper for HP and CC Supervisor Profiles
            if(sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM)
            {
            caseData.put('Complaint',oCase.Complaint__c);
            caseData.put('Product',oCase.Product__c);
            caseData.put('Complaint',oCase.Created_By_Queue__c);
            caseData.put('ClassificationType',oCase.Classification_Type__c);
            caseData.put('AutoRouteStatus',oCase.AutoRoute_Status__c);
            caseData.put('Owner',oCase.Case_Owner__c);
        caseData.put('Origin',oCase.Origin);
            caseData.put('InteractingAboutType',oCase.Interacting_About_Type__c);
            caseData.put('CallBenefitCategory',oCase.Call_Benefit_Category__c);
            caseData.put('InteractingWithName',oCase.Interacting_With_Name__c);
            caseData.put('GARightsGiven',oCase.G_A_Rights_Given__c);
            caseData.put('PolicyMember',oCase.Policy_Member__C);
        if(caseSet != null && !caseSet.isEmpty() && caseSet.Contains(oCase.Id))
            {
            bTransfer = true;
            }
            lstCaseTaskDTOWrapper.add(new CaseTaskView_DTO_HUM(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM , oCase.Id , oCase.CaseNumber , 
            sOpenWorkTasks + '_' +sOpenExtTasks , oCase.Status , oCase.Priority , oCase.Interacting_With__c , oCase.Interacting_With__r.Name ,
            oCase.Account.Name ,(oCase.CTCI_List__r.Classification__c!= NULL ? oCase.CTCI_List__r.Classification__r.Name : ''), (oCase.CTCI_List__r.Intent__c != NULL ? oCase.CTCI_List__r.Intent__r.Name : ''), 
            oCase.Owner_Queue__c ,oCase.Work_Queue_View_Name__c , (oCase.Follow_Up_Due_Date__c != NULL ?String.valueof(oCase.Follow_Up_Due_Date__c.Month()) + '/'+ 
            String.valueof(oCase.Follow_Up_Due_Date__c.Day()) +'/'+String.valueof(oCase.Follow_Up_Due_Date__c.Year()) : '') ,oCase.Case_Age__c , oCase.Case_Age__c, 
            oCase.Re_Open_Case_Age__c,receiveddate, oCase.CreatedDate.format('MM/dd/yyyy hh:mma',UserInfo.getTimeZone().toString()) , oCase.Follow_Up_Due_Date__c < system.today() ,
            (oCase.OwnerId == UserInfo.getUserId()? GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM) ,
            String.valueof(oCase.Id +'_' + oCase.CreatedDate.getTime()), sTimeSpentOnCase,bTransfer,oCase.Policy_Member__r.Policy__c,
            oCase.RecordTypeId,oCase.AccountId,oCase.Guided_Process_Flag__c,caseData,oCase.General_Case__c,oCase.HO_Dental_Case__c,oCase.HO_Medical_Case__c));
            }
            
            else
            {
            //wrapper for HP and CC Specialist Profiles
            lstCaseTaskDTOWrapper.add(new CaseTaskView_DTO_HUM(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM , oCase.Id , oCase.CaseNumber , 
            sOpenWorkTasks + '_' +sOpenExtTasks , oCase.Status , oCase.Priority , oCase.Interacting_With__c , oCase.Interacting_With__r.Name ,
            oCase.Account.Name ,(oCase.CTCI_List__r.Classification__c!= NULL ? oCase.CTCI_List__r.Classification__r.Name : ''), (oCase.CTCI_List__r.Intent__c != NULL ? oCase.CTCI_List__r.Intent__r.Name : ''), 
            oCase.Owner_Queue__c ,oCase.Work_Queue_View_Name__c , (oCase.Follow_Up_Due_Date__c != NULL ?String.valueof(oCase.Follow_Up_Due_Date__c.Month()) + '/'+ 
            String.valueof(oCase.Follow_Up_Due_Date__c.Day()) +'/'+String.valueof(oCase.Follow_Up_Due_Date__c.Year()) : '') ,oCase.Case_Age__c , oCase.Case_Age__c, 
            oCase.Re_Open_Case_Age__c,receiveddate, oCase.CreatedDate.format('MM/dd/yyyy hh:mma',UserInfo.getTimeZone().toString()) , oCase.Follow_Up_Due_Date__c < system.today() ,
            (oCase.OwnerId == UserInfo.getUserId()? GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM) ,
            String.valueof(oCase.Id +'_' + oCase.CreatedDate.getTime()), sTimeSpentOnCase,bTransfer,oCase.Policy_Member__r.Policy__c,
            oCase.RecordTypeId,oCase.AccountId,oCase.Guided_Process_Flag__c,caseData,oCase.General_Case__c,oCase.HO_Dental_Case__c,oCase.HO_Medical_Case__c));
            }
            
        }
        
        for(Task oTask : lstTaskResults) // Loop over the Tasks to prepare the wrapper
        {
            if(isPulled && oTask.QueueOrUserId__c <> null && !oTask.QueueOrUserId__c.startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM)) oTask = assignTaskValues(oTask ,sCurrentQueue);
            if(!string.isBlank(oTask.WhatId) && String.valueof(oTask.WhatId).startsWith(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_SFDCCASEID_HUM) && mapCase.containskey(oTask.WhatId))
            {
                sInteractngWith = mapCase.get(oTask.WhatId).Interacting_With__r.Name; // Assign Interacting  With from Case
                sInteractngWithId = mapCase.get(oTask.WhatId).Interacting_With__c; // Assign Interacting  With Id from Case
                sInteractingAbout = mapCase.get(oTask.WhatId).Account.Name; // Assign Interacting  About from Case
                decCaseAge = mapCase.get(oTask.WhatId).Case_Age__c; // Assign Case Age from Case
            }
            String sTaskCount  = HumConstants.HUMWorkTask + ' ' + (oTask.Open_Work_Task_Count__c != null ? String.valueof(oTask.Open_Work_Task_Count__c) : String.valueof(0)) +' ( of '+ (oTask.Total_Work_Task_Count__c != null ? 
            String.valueof(oTask.Total_Work_Task_Count__c) : String.valueof(0)) +' )';
            
            String receiveddate = (mapCaseToReceiveddate.containskey(oTask.WhatId))? mapCaseToReceiveddate.get(oTask.WhatId): '';
            Map<string, string> caseData = new Map<string,string>();
            caseData.put('Owner',oTask.Task_Owner__c);
            lstCaseTaskDTOWrapper.add(new CaseTaskView_DTO_HUM(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM , oTask.Id , oTask.Task_Number__c ,
            sTaskCount + '_'+'' ,oTask.Status , oTask.Priority , sInteractngWithId , sInteractngWith ,sInteractingAbout ,
            (String.isNotBlank(oTask.Classification__c) ? oTask.Classification__c : '') ,(String.isNotBlank(oTask.Intent__c)  ? oTask.Intent__c : ''), oTask.Owner_Queue__c , 
            oTask.Work_Queue_View_Name__c , (oTask.ActivityDate != NULL ? String.valueof(oTask.ActivityDate.Month()) + '/'+ String.valueof(oTask.ActivityDate.Day()) +'/'+
            String.valueof(oTask.ActivityDate.Year()) : ''),oTask.Age_Since_Created__c , decCaseAge, (oTask.Date_Time_Reopned__c != NULL ?  oTask.Re_Open_Task_Age__c : 0),receiveddate,
            oTAsk.CreatedDate.format('MM/dd/yyyy hh:mma',UserInfo.getTimeZone().toString()) , oTask.ActivityDate < system.today() ,(oTask.QueueOrUserId__c == UserInfo.getUserId()? 
            GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM), String.valueof(oTask.Id +'_' + oTAsk.CreatedDate.getTime()),'',false,'','','',false,caseData,false,false,false));
        }
        return lstCaseTaskDTOWrapper;
    }
    
    /**
    * Method is used to assign values to Task before pulling 
    * <p>
    * @return   Task : Instance of Task
    * @param    Task : Instance of Task
    * @param    String : Current Queue Name
    */
    private static Task assignTaskValues(Task oTask , String sCurrentQueue)
    {
        if(Schema.SObjectType.Task.fields.OwnerId.isUpdateable())	oTask.OwnerId = UserInfo.getUserId();
        if(Schema.SObjectType.Task.fields.Task_Owner__c.isUpdateable())	oTask.Task_Owner__c = UserInfo.getName();
        if(Schema.SObjectType.Task.fields.QueueOrUserId__c.isUpdateable())	oTask.QueueOrUserId__c = UserInfo.getUserId();                
        if(Schema.SObjectType.Task.fields.select__c.isUpdateable())	oTask.select__c = false;
        if(Schema.SObjectType.Task.fields.LastModifiedby_Queue__c.isUpdateable())	oTask.LastModifiedby_Queue__c = sCurrentQueue;  
        return oTask;
    }
    
    /**
    * Method is used to assign values to Case before pulling 
    * <p>
    * @return   Case : Instance of case
    * @param    Case : Instance of case
    */
    private static Case assignCaseValues(Case oCase)
    {
        if(Schema.SObjectType.Case.fields.OwnerId.isUpdateable())	oCase.OwnerId = UserInfo.getUserId();
        if(Schema.SObjectType.Case.fields.Case_Owner__c.isUpdateable())	oCase.Case_Owner__c = UserInfo.getName();
        if(Schema.SObjectType.Case.fields.LastModifiedby_Queue__c.isUpdateable())	oCase.LastModifiedby_Queue__c = oCase.Owner_Queue__c;   
        return oCase;
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
            list<String> lstMygroups = GetQueueList();
            
            list<String> lstQueues = NULL;

            for (String sWorkQueue : mapWorkQueueSetup.keySet())
            {
                Work_Queue_Setup__c oWorkQueue = mapWorkQueueSetup.get(sWorkQueue);
                lstQueues = mapGroupQueue.get(oWorkQueue.Public_Group_Name__c);
                if(NULL == lstQueues)   lstQueues = new list<String>();
                lstQueues.add(oWorkQueue.Work_Queue_Name__c);                
                mapGroupQueue.put(oWorkQueue.Public_Group_Name__c, lstQueues);
            }

            initializeGroupQueue(lstMygroups, lstQueues);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CaseTaskView_VF_HUM','initializeWorkQueue');
        }
    } 
    
    /**
    * Method used to initialize GroupQueue and Category DDs
    * <p>
    * @return : void
    * @param   : list<String>, list<String>
    */
    private void initializeGroupQueue(list<String> lstMygroups, list<String> lstQueues)
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
    * Method to get the queues based on the Public Groups in which the logged in user is part of. 
    * <p>
    * Return Type   :   list<SelectOption>
    * Parameters    :   NA
    */
    public list<SelectOption> getCategoriesdd1()
    {
        Set<String> setCatTemp = new Set<String>();
        list<SelectOption> lstcat = new list<SelectOption>();         
        list<String> lstMygroups = GetQueueList();
        list<String> lstGroupNames = new list<String>();
        lstViewCategorydd1 = new List<String>();
        try
        {
            if(mapWorkQueueSetup != NULL)
            {
                for (String sWorkQueue : mapWorkQueueSetup.keySet())
                {
                    for(String oAllGrps : lstMygroups) 
                    {
                        if(mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c <> null && (mapWorkQueueSetup.get(sWorkQueue).Public_Group_Name__c).trim().equalsIgnoreCase(oAllGrps))
                            lstGroupNames.add(mapWorkQueueSetup.get(sWorkQueue).Work_Queue_Name__c);
                    }
                }
            }

            if(!lstGroupNames.isEmpty())
            {
                for(String sQueue :lstGroupNames)
                {
                    lstCatTemp.add(sQueue.replace('_', ' '));
                }
            }
            
            if(!lstCatTemp.isEmpty())
            {
                setCatTemp.addAll(lstCatTemp);
                lstViewCategorydd1.addAll(setCatTemp);
                lstCatTemp = HUMUtilityHelper.doSort(setCatTemp);
                lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM, '(Select All)'));
                for(string sCategoryName : lstCatTemp)
                {
                    lstcat.add(new SelectOption(sCategoryName, sCategoryName));
                }
            }
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e,'CaseTaskView_VF_HUM','getCategoriesdd1');
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
        list<SelectOption> lstcat = new list<SelectOption>();
         String sUserName = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
         if(sUserName != System.Label.HUMAgencyCCSupervisor)  
         {
            lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM +' (All)' , '(Select All)'));
         }
         else
         {
            lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM +' (Al)' , '(Select All)'));
         }
         
        lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.BLANK_ALERTS_HUM, System.Label.CaseTaskAllOtherQV));
        try
        {
            lstcat.addAll(sortedViewData());    
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CaseTaskView_VF_HUM','getCategoriesdd2');
        }
        return lstcat;
    }

    
    private List<SelectOption> sortedViewData()
    {
        list<SelectOption> lstcat = new list<SelectOption>();
        List<String> tmplist = new List<String>();
        list<Queue_View__c> lstQueues = [SELECT Queue_View_Name__c, Queue_Name__c FROM Queue_View__c where Queue_Name__c in :lstViewCategorydd1 AND Group_Name__c IN : GetQueueList() AND IsActive__c = TRUE AND Queue_View_Name__c !=: GLOBAL_CONSTANT_HUM.HUM_ALLOTHERVIEW order by Queue_Name__c,Queue_View_Name__c asc];
        if(!lstQueues.isEmpty())
        {
            for(Queue_View__c oViews : lstQueues)    
            {   
                if(String.isNotBlank(oViews.Queue_View_Name__c) && String.isNotBlank(oViews.Queue_Name__c))
                tmplist.add(oViews.Queue_View_Name__c.trim() +' ('+oViews.Queue_Name__c.trim()+')');                     
            }
        }
        List<String>  lstSort = new List<String>();
        List<String> lsttmpSort;
        for(String sQueue : lstCatTemp)
        {
            lsttmpSort = new List<String>();
            lsttmpSort.add(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OTHER_HUM +' ('+ sQueue +')');
            for(String sViewAndQueue : tmplist)
            {
                
                
                if(sViewAndQueue.containsIgnoreCase(sQueue))    
                    lsttmpSort.add(sViewAndQueue);
                lsttmpSort.sort();
                
            }
             lstSort.addAll(lsttmpSort);       
                
        }
        for(String s : lstSort)
        {
            lstcat.add(new SelectOption(s , s));    
        }
        return lstcat;
    }
    
     /**
    * Method used to populate SelectedAssign Dropdown
    * <p>
    * @return   list<SelectOption>
    * @param    NA
    */
    public list<SelectOption> getSelectedAssign()
    {
        list<SelectOption> lstSelectedAssign = new list<SelectOption>();
        lstSelectedAssign.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM ,'(Select All)'));
        lstSelectedAssign.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ASSIGNED_HUM));
        lstSelectedAssign.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_UNASSIGNED_HUM));
        return lstSelectedAssign;
    } 
    
    /**
    * Method to populate SelectedView Dropdown
    * <p>
    * @return : list<SelectOption>
    * @param  : NA
    */
    public list<SelectOption> getSelectedView()
    {
        list<SelectOption> lstSelectedView = new list<SelectOption>();
        lstSelectedView.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM,'(Select All)'));
        lstSelectedView.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTCASE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_CASES_HUM));
        lstSelectedView.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_TASKS_HUM));
        return lstSelectedView;
    }
    /**
    * Method to populate Item Age Dropdown
    * <p>
    * @return : list<SelectOption>
    * @param  : NA
    */
    public list<SelectOption> getItemAge()
    {
        list<SelectOption> lstItemAge = new list<SelectOption>();
        lstItemAge.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM,'Select All'));
        lstItemAge.add(new SelectOption(GLOBAL_CONSTANT_HUM.IA_MORE_THAN_10,'Greater Than 10 days'));
        lstItemAge.add(new SelectOption(GLOBAL_CONSTANT_HUM.IA_6_TO_10,'6 to 10 days'));
        lstItemAge.add(new SelectOption(GLOBAL_CONSTANT_HUM.IA_UPTO_5,'Upto 5 days'));
        return lstItemAge;
    }
    
     /**
    * Method to get Public Groups in which the logged in user is part of.
    * <p> 
    * @return  :  list<string>
    * @param    :  NA
    */
    private list<string> getQueueList()
    {
        list<String> lstMyGroups =  new list<String>(); 
        list<GroupMember> lstGrpMem = [select Group.Name from GroupMember where UserOrGroupId =: Userinfo.getUserId() And Group.Type =: GLOBAL_CONSTANT_HUM.TASKLISTVIEW_REGULAR_HUM];
        for(GroupMember oGrpMem : lstGrpMem)
        {
            lstMyGroups.add(oGrpMem.Group.Name); 
        } 
        return lstMyGroups;
    } 
    
    /**
     * Method to get pull work queue drop down options
     * <p>
     * @return     lstOptions
     * @param      NA
     */
    public list<SelectOption> getPullWorkQueueOptions()
    {
        list<SelectOption> lstOptions = new list<SelectOption>();
        for(SelectOption v : getCategoriesdd1())
        {
            if(v.getValue() == GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM)
            {
                lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM,'(Select All)'));
            }
            else
            {
                lstOptions.add(new SelectOption(v.getLabel(), v.getValue()));
            }
        }
        return lstOptions;
    } 
    
    
    /**
    * Method to get the queueviews based on the Public Groups in which the logged in user is part of and 
        depending on the first dropdown option selected
    * <p>
    * @return     list<SelectOption>
    * @param      NA
    */
    public list<SelectOption> getPullWorkViewOptions()
    {
        list<SelectOption> lstcat = new list<SelectOption>();
        lstcat.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALL_HUM ,'(Select All)'));
        try
        {
            lstcat.addAll(sortedViewData());
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'CaseTaskView_VF_HUM','getCategoriesdd2');
        }
        //lstcat.sort();        
        return lstcat;
    }
   
   /**
     * Method used to populate filter by Dropdown
     * <p>
     * @return   lstSortBy
     * @param    NA
     */
    public list<SelectOption> getPullWorkFilterByOptions()
    {
        list<SelectOption> lstSortBy = new list<SelectOption>();
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_DUESPACEDATE_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGE_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGECASESONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONLY_HUM)); 
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONCASE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_AGETASKSONCASE_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYCASEONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYTSKONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM));
        lstSortBy.add(new SelectOption(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PRIORITYNORMALONLY_HUM));
        return lstSortBy;
    }
        
    /**
     * Method used to update Owner for Assigned Work
     * <p>
     * @return   list<CaseTaskView_DTO_HUM> : List of Wrapper Records returned after accepting the WorkItems
     * @param    map<string,boolean> : Map of records needs to be Pulled
     */
     
    @RemoteAction 
    public static list<CaseTaskView_DTO_HUM> updateOwnerOfAssignedRecords(map<string,boolean> mapOfUnassignedRecords,list<string> queueData, List<string> queueValue,List<string> viewVlue,List<string> filterValue,string assignWorkValue, string secFilter,  List<string> lstItemAgeValue) 
    {
       try
        {   
            String sCurrentQueue =  [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c;
            list<Task> taskList = new list<Task>();
            list<Case> caseList = new list<Case>();
            for(String s : mapOfUnassignedRecords.keyset())
            {
                if(string.valueOf(id.valueOf(s).getSObjectType()).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASKLISTVIEW_OBJECTTASK_HUM))
                {
                    Task oTask = new Task(id = s);
                    oTask = assignTaskValues(oTask,sCurrentQueue);
                    taskList.add(oTask);
                }
                else
                {
                    Case oCase = new Case(id = s);
                    oCase = assignCaseValues(oCase);
                    caseList.add(oCase);
                }
            }
            if(!taskList.isEmpty()) database.update(taskList,false);
            if(!caseList.isEmpty()) database.update(caseList,false);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseTaskView_C_HUM', 'updateOwnerOfAssignedRecords');
        }
        return fetchCaseTask(true, queueData, queueValue, viewVlue, filterValue, assignWorkValue, secFilter,  lstItemAgeValue, null);
    }
    
    /**
     * Method used to update Status
     * <p>
     * @return   list<CaseTaskView_DTO_HUM> : List of Wrapper Records returned after accepting the WorkItems
     * @param    map<string,boolean> : Map of records needs to be Pulled
     */
    @RemoteAction 
    public static list<CaseTaskView_DTO_HUM> updateStatusOfAssignedRecords(map<string,boolean> mapOfUnassignedRecords,list<string> queueData, List<string> queueValue,List<string> viewVlue,List<string> filterValue,string assignWorkValue, string secFilter, string scasecom,string scasestatus, map<string,CaseTaskView_DTO_HUM> mapCaseDTO,  List<string> lstItemAgeValue, List<String> lstTeamMembers)
    {
       try
        {   
            list<Case> caseList = new list<Case>();
            map<string,boolean> mapCaseComment = new map<string,boolean>();
            map<Id,string> policyCaseMap = new map<Id,string>();
            list<CaseComment> commentList = new list<CaseComment>();
            list<Database.SaveResult> updatedCaseList = new list<Database.SaveResult>();
            boolean bvalidCase;
            boolean isCallBenefitCategoryView;
            Boolean bCheckMedicareCCPermission;
            Boolean bMedicareCalls;
            Boolean bCheckMedicareCustomerServiceAccess;
            Boolean bIsPharmacyUser;
            String sMedicareResInit;
            Boolean bIsPDPPilotUser;
            Boolean isPharmacyProfile;
            Boolean CaseReadOnly;
            Profile currentProfile = new Profile();
            currentProfile = [Select name from profile where id = :UserInfo.getProfileId()];
            Set<String> wqueues = new Set<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
            Set<String> chatQueues = new Set<String>(GLOBAL_CONSTANT_HUM.WEB_CHAT_WQUEUES);
            Set<String> processQueues = new Set<String>{GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_OWNER,GLOBAL_CONSTANT_HUM.WORK_QUEUE_LV_Screening,GLOBAL_CONSTANT_HUM.WORK_QUEUE_PCP_Assignment_Research,GLOBAL_CONSTANT_HUM.GOODCAUSEQUEUE};
            Set<String> processRecTypes = new Set<String>{GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM, GLOBAL_CONSTANT_HUM.MEDICARE_CASE, GLOBAL_CONSTANT_HUM.HP_UNKNOWNCASE_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNCASE_HUM};
            List<Task> lstOpenTasks1 = new List<Task>();
            lstOpenTasks1 = [Select Whatid from Task where Whatid in :mapOfUnassignedRecords.keyset() AND Type in (: GLOBAL_CONSTANT_HUM.CASEEDIT_WORK_TASK_HUM, :HUMConstants.HUMExtensionTask) and Status != : GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM order by createddate asc]; 
            Set<Id> setTask = new Set<Id>();
            if(lstOpenTasks1 != Null && !lstOpenTasks1.isEmpty())
            for(Task sTask : lstOpenTasks1)
            {
                setTask.add(sTask.Whatid);
            }
            Set<id> polMemId = new Set<id>();
            list<Policy_Member__c> pMem = new list<Policy_Member__c>();
            Map<id,Policy_Member__c> polMemMap = new Map<id,Policy_Member__c>();
            Map<id,id> polMemCaseMap = new Map<id,id>();
            for(string str : mapOfUnassignedRecords.keyset())
            {
                CaseTaskView_DTO_HUM oCaseDTO =  mapCaseDTO.get(str);
                if(oCaseDTO.sPolicyMember!=null){
                polMemId.add(oCaseDTO.sPolicyMember);
                polMemCaseMap.put(str,oCaseDTO.sPolicyMember);
                }
            }
            if(polMemId != null && !polMemId.isEmpty())
            pMem = [SELECT Id, Member__c,Product_type_code__c FROM Policy_Member__c WHERE Id in :polMemId];
            if(pMem != null && !pMem.isEmpty()){
            for(id caseId : polMemCaseMap.keyset())
            {
               for(Policy_Member__c p : pMem)
                {
                if(polMemCaseMap.get(caseId) == p.id)
                polMemMap.put(caseId,p);
                }  
             }
            }
            Set<id> polId = new Set<id>();
            list<Policy__c> pol = new list<Policy__c>();
            Map<id,Policy__c> polMap = new Map<id,Policy__c>();
            Map<id,id> polCaseMap = new Map<id,id>();
            for(string str : mapOfUnassignedRecords.keyset())
            {
                CaseTaskView_DTO_HUM oCaseDTO =  mapCaseDTO.get(str);
                if(oCaseDTO.sPolicy!=null){
                polId.add(oCaseDTO.sPolicy);
                polCaseMap.put(str,oCaseDTO.sPolicy);
                }
            }
            if(polId != null && !polId.isEmpty())
            pol = [SELECT Major_Lob__c, Policy__c.Product__c, Policy__c.Product__r.Product_Type__c, Policy__c.Product__r.Segment_Type__c, Policy__c.Product__r.ProductCode from Policy__c WHERE id in :polId];
            if(pol != null && !pol.isEmpty())
            {
                for(id caseId : polCaseMap.keyset())
                {
                   for(Policy__c p : pol)
                    {
                    if(polCaseMap.get(caseId) == p.id)
                    polMap.put(caseId,p);
                    }  
                }
            }
            List<Case_Action_Association__c> lstCaseAction = [SELECT Id, Case__c, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = :GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_Intent_HUM and Case__c in :mapOfUnassignedRecords.keyset()];
            Map<id,list<CaseCommentType_Junction__c>> caseCCTJunc = new Map<id,list<CaseCommentType_Junction__c>>();
            map<Id, CaseComment> mapMediCC = new map<Id, CaseComment>([SELECT Id, CommentBody, ParentId, createddate FROM CaseComment WHERE ParentId in :mapOfUnassignedRecords.keyset()]);
            caseCCTJunc = CaseTaskView_H_HUM.getCommentsMap(mapOfUnassignedRecords.keyset(),mapMediCC);
            List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c,Template_Submission__r.Template__r.Unique_Template_Name__c,Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  in (:GLOBAL_CONSTANT_HUM.MedExpTEMPLATEUNIQUEID,:GLOBAL_CONSTANT_HUM.PartDTEMPLATEUNIQUEID, :GLOBAL_CONSTANT_HUM.ATTESTATION_TEMPLATE_NAME, :GLOBAL_CONSTANT_HUM.GOODCAUSEUNIQUEID, :GLOBAL_CONSTANT_HUM.PCPTEMPLATEUNIQUEID) and Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c in :mapOfUnassignedRecords.keyset()];
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and Group.Name in (:GLOBAL_CONSTANT_HUM.GOODCAUSEGROUPNAME, :GLOBAL_CONSTANT_HUM.PCPGROUPNAME, :GLOBAL_CONSTANT_HUM.Group_LV_Correspondence_Screening_PGQ, :GLOBAL_CONSTANT_HUM.Group_LV_Correspondence_Screening_PGVE, :GLOBAL_CONSTANT_HUM.Group_LV_Correspondence_Screening_PGVP)];
            Set<String> grpLst = new Set<String>();
            if(lstGrpMem != null && !lstGrpMem.isEmpty())
            {
                for(GroupMember g : lstGrpMem)
                {
                   grpLst.add(g.Group.Name);
                }
            }
            bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM ? false : true;
            bIsPDPPilotUser = Pharmacy_H_HUM.isPDPPilotUser();
            MedicareCallsCaseComments_H_HUM oMedCalls = new MedicareCallsCaseComments_H_HUM();
            List<String> lstComplaints = new List<String>{GLOBAL_CONSTANT_HUM.MEDICAID,GLOBAL_CONSTANT_HUM.MEDICAREPART_C, GLOBAL_CONSTANT_HUM.MEDICAREPART_D};
            Set<String> setPharRecType = new Set<String>(GLOBAL_CONSTANT_HUM.Pharmacy_Record_Type_Name);   
            List<PermissionSetAssignment> lstPermission = [SELECT PermissionSetId,PermissionSet.Name FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name in (:GLOBAL_CONSTANT_HUM.RSO_HP_PermissionSet_Name, : GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM)];
            List<PermissionSetAssignment> lstRSOHPPermissionSet = new List<PermissionSetAssignment>();
            list<PermissionSetAssignment> lstPMAssign = new List<PermissionSetAssignment>();
            if(lstPermission != Null && !lstPermission.isEmpty())
            {
                for(PermissionSetAssignment lstP : lstPermission)
                {
                if(lstP.PermissionSet.Name==GLOBAL_CONSTANT_HUM.RSO_HP_PermissionSet_Name)
                lstRSOHPPermissionSet.add(lstP);
                if(lstP.PermissionSet.Name==GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM)
                lstPMAssign.add(lstP);
                }
            }
            for(String s : mapOfUnassignedRecords.keyset())
            {
                    Policy__c  policy = new Policy__c();
                    bvalidCase = true;
                    bCheckMedicareCustomerServiceAccess = false;
                    bCheckMedicareCCPermission = false;
                    isCallBenefitCategoryView = false;
                    bMedicareCalls = false;
                    Case oCase = new Case(id = s);
                    CaseTaskView_DTO_HUM oCaseDTO =  mapCaseDTO.get(s);
                    string caseRecordTypeName = Schema.SObjectType.Case.getRecordTypeInfosById().get(oCaseDTO.sRecordTypeId).getName();
                    
            // Intent and Classification value check
                    if(String.isBlank(oCaseDTO.sIntent) || String.isBlank(oCaseDTO.sClassification))
                       bvalidCase = false;
                    
            //Policy Member check
                    if (bvalidCase && String.isNotBlank(oCaseDTO.sPolicyMember))
                    {
                        if(polMemMap.containsKey(s)){
                            Policy_Member__c p = polMemMap.get(s);
                            if(p!=Null)
                            {
                            if (p.Member__c != oCaseDTO.sAccount)
                                bvalidCase = false;
                                
                            //GoodCause process check
                            if(bvalidCase && String.isNotBlank(oCaseDTO.sClassification)  && oCaseDTO.sClassification == GLOBAL_CONSTANT_HUM.GOODCAUSECLASSIFICATION
                                && String.isNotBlank(oCaseDTO.sIntent) && oCaseDTO.sIntent == GLOBAL_CONSTANT_HUM.GOODCAUSEINTENT  
                                && String.isNotBlank(oCaseDTO.sClassificationType) && GLOBAL_CONSTANT_HUM.GoodCauseAutoRouteClassificationTypes.contains(oCaseDTO.sClassificationType)
                                && String.isNotBlank(oCaseDTO.sPolicyMember) && String.isNotBlank(p.Product_type_code__c) 
                                && GLOBAL_CONSTANT_HUM.PRODUCT_TYPE_PDP == p.Product_type_code__c )
                            {       
                                List <Template_Submission_Data__c> flowLst = new List<Template_Submission_Data__c>();
                                if(flowValue != Null && !flowValue.isEmpty())
                                {
                                    for(Template_Submission_Data__c t: flowValue)
                                    {
                                        if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == GLOBAL_CONSTANT_HUM.GOODCAUSEUNIQUEID)
                                        {
                                        flowLst.add(t);
                                        break;
                                        }
                                    }   
                                }
                                if(flowLst != Null && flowLst.isEmpty())
                                    bvalidCase = false;
                            }

                            } 
                        }   
                    }
                    
            //Edit permission check
                    isPharmacyProfile = false;
                    CaseReadonly = true;
                    if(currentProfile.Name == Label.PHARMACY_SPECIALIST_PROFILE_NAME)       isPharmacyProfile  = true;
                    if(bvalidCase)
                    {
                        if(!caseRecordTypeName.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType))
                        {
                          if(isPharmacyProfile && ((chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN) || oCaseDTO.sProduct == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY))
                          {
                            if(wqueues.contains(oCaseDTO.sCaseTaskQueue) || wqueues.contains(oCaseDTO.sCreatedQueue) || (chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
                            {
                              CaseReadonly = false;
                            }
                          }
                        }                    
                        if(caseRecordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE))
                        {
                            if(!((!isPharmacyProfile) || (isPharmacyProfile && !CaseReadOnly)))
                                bvalidCase = false;
                        }
                    }
                    
                    //QAA process check
                    Boolean bIsQAANeeded = (bvalidCase && String.isNotBlank(oCaseDTO.sClassification) && oCaseDTO.sClassification == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_Classification_HUM && String.isNotBlank(oCaseDTO.sIntent) && oCaseDTO.sIntent == GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_Intent_HUM && lstComplaints.contains(oCaseDTO.sComplaint)); 
                    if(bIsQAANeeded)
                    {                
                        if(!CaseTaskView_H_HUM.checkProcess(oCase.id,lstCaseAction))
                           bvalidCase = false;
                    }
                    
            //check for open tasks
                   if(bvalidCase && setTask != Null && !setTask.isEmpty())
                    {
                        if(setTask.contains(s))
                        {
                        bvalidCase = false; 
                        }
                     }
                    
            //Medicare Part C or Part D value check
                    bCheckMedicareCustomerServiceAccess = CaseTaskView_H_HUM.validateMedicareCustomerServiceAccess(oCaseDTO.sOrigin,lstPMAssign);
            if (oCaseDTO.sPolicy != null && polMap != null  && polMap.containsKey(s))
                        policy = polMap.get(s);
                        
                    if(bCheckMedicareCustomerServiceAccess)
                    {
                        if (oCaseDTO.sPolicy != null)
                        {
                    
                            Map<String, Set<String>> medicareLOBProductType = GLOBAL_CONSTANT_HUM.MEDICARE_LOB_PRODUCTTYPE_LIST();
                            if (policy.Product__r.ProductCode != null && policy.Product__r.ProductCode != '' && policy.Product__r.ProductCode == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY && medicareLOBProductType.containsKey(policy.Major_Lob__c))
                            {
                                Set<String> TempProductType;
                                TempProductType = medicareLOBProductType.get(policy.Major_Lob__c);
                                if (policy.Product__r.Product_Type__c != null && policy.Product__r.Product_Type__c != '' && TempProductType.contains(policy.Product__r.Product_Type__c))
                                {
                                    isCallBenefitCategoryView = true;
                                }
                                else if (policy.Product__r.Product_Type__c == null || policy.Product__r.Product_Type__c == '')
                                {
                                    isCallBenefitCategoryView = CaseTaskView_H_HUM.checkProductSegmentType(policy);
                                }
                            }
                         }
                            
                        else if ((string.IsBlank(oCaseDTO.sPolicyMember) || oCaseDTO.sPolicyMember == null) && (oCaseDTO.sInteractingAboutType == GLOBAL_CONSTANT_HUM.Member))
                        {
                            isCallBenefitCategoryView = true;
                        }
                    }
                    else
                    {
                        isCallBenefitCategoryView = false;                       
                    }
                    
                    Boolean bRSOHPFlag = false;
                    if((!bIsPharmacyUser) && (setPharRecType.contains(caseRecordTypeName)))
                    {
                        bIsPharmacyUser = bIsPDPPilotUser; 
                    }
                    if(lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty())
                    {
                        if((!bIsPharmacyUser) && (setPharRecType.contains(caseRecordTypeName)))
                        {
                            bIsPharmacyUser = true;
                            bRSOHPFlag = true;
                        }
                        else
                        {
                            bIsPharmacyUser = false;
                            bRSOHPFlag = false;
                        }
                    } 
                    if(isCallBenefitCategoryView && !bRSOHPFlag)
                    {
                       if(String.isBlank(oCaseDTO.sCallBenefitCategory))
                           bvalidCase = false; 
                    }
                    
                    //check if Medicare comment existing and length not greater than 2000
                    sMedicareResInit = '';
                    if (caseRecordTypeName != GLOBAL_CONSTANT_HUM.MEDICARE_CASE && caseRecordTypeName != GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE)
                    {
                        if (oCaseDTO.sPolicyMember != null && bCheckMedicareCustomerServiceAccess)
                        {
                            if (oCaseDTO.sPolicy != null)
                                bMedicareCalls = oMedCalls.checkMedicareCare(oCaseDTO.sOrigin, policy.Product__r.ProductCode, policy.Major_Lob__c, policy.Product__r.Product_Type__c, policy.Product__r.Segment_Type__c);
                            if (lstPMAssign != null && !lstPMAssign.isEmpty()) 
                                bCheckMedicareCCPermission = true;
                        }
                    }
                    if(bMedicareCalls && bCheckMedicareCCPermission && !bIsPharmacyUser)
                    {
                       mapCaseComment.put(s,true);
                       list<CaseCommentType_Junction__c> lstCCTJunction = caseCCTJunc.get(Id.valueOf(s));
                        if (lstCCTJunction != null && !lstCCTJunction.isEmpty() && String.isBlank(scasecom) && scasestatus==GLOBAL_CONSTANT_HUM.HUM_CLOSED)
                        {
                            sMedicareResInit = CaseTaskView_H_HUM.getMedCom(lstCCTJunction, mapMediCC);
                            if (String.isBlank(sMedicareResInit)){
                                bvalidCase = false;
                             }
                             else if(!String.isBlank(sMedicareResInit))
                             {
                             if(sMedicareResInit.length() > 2000 || sMedicareResInit.length() < 10)
                             bvalidCase = false;
                             }
                        }
                        else if(String.isBlank(scasecom) && scasestatus==GLOBAL_CONSTANT_HUM.HUM_CLOSED)
                        {
                            bvalidCase = false;
                        }   
                        if(String.isNotBlank(scasecom) && (scasecom.length() > 2000 || scasecom.length() < 10) && scasestatus==GLOBAL_CONSTANT_HUM.HUM_CLOSED)
                            bvalidCase = false;
                    }
                    else
                        mapCaseComment.put(s,false);
                    
                    //Edit && Close case button enable check on case detail page
                    if(bvalidCase)
                    {
                        Boolean bCloseDisableDetailPage;
                        Boolean bCloseDisableEditPage;
                        Boolean disableCaseOwner = false;
                        Boolean disableCaseAfterPCP = false;
                        if(oCaseDTO.sStatus == 'External Transfer')
                            bvalidCase = false;                    
                        else if(processQueues.contains(oCaseDTO.sCaseTaskQueue) && bvalidCase)
                        {
                            bCloseDisableEditPage = CaseTaskView_H_HUM.checkEditPageClose(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,grpLst,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s);
                            bCloseDisableDetailPage = CaseTaskView_H_HUM.checkDetailPageClose(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,grpLst,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s,caseRecordTypeName);   
                            if(bCloseDisableDetailPage) bvalidCase = false;
                        }
                        if(bvalidCase && oCaseDTO.sCaseTaskQueue == GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_OWNER && oCaseDTO.bGuidedFlag == true && oCaseDTO.sStatus == GLOBAL_CONSTANT_HUM.CASE_STATUS_PENDING_RESPONSE)
                            disableCaseOwner = CaseTaskView_H_HUM.disableChangeOwnerCreditableCoverage(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,oCaseDTO.sStatus,s,true);
                        if(bvalidCase && oCaseDTO.sCaseTaskQueue == GLOBAL_CONSTANT_HUM.WORK_QUEUE_PCP_Assignment_Research  && !grpLst.contains(GLOBAL_CONSTANT_HUM.PCPGROUPNAME))
                            disableCaseAfterPCP = CaseTaskView_H_HUM.isPCPProcessAttached(grpLst,flowValue,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s,true);
                        if(disableCaseOwner || disableCaseAfterPCP)
                            bvalidCase = false;
                    }
                        
                    if(bvalidCase)
                    {
                        oCase.status = scasestatus;
                        caseList.add(oCase);
                    }                                  
            }
            if(caseList != null && !caseList.isEmpty()) 
            {
                updatedCaseList = database.update(caseList,false);
            }
            List<string> lstIds = new List<string>();
            if(updatedCaseList != null && !updatedCaseList.isEmpty())
            {
                for(Database.SaveResult sr : updatedCaseList)
                {
                    if(sr.isSuccess())
                    lstIds.add(sr.getId());
                }
                if(lstIds != null && !lstIds.isEmpty() && String.isNotBlank(scasecom))
                CaseTaskView_H_HUM.addCaseComment(scasecom, mapCaseComment, lstIds);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseTaskView_C_HUM', 'updateStatusOfAssignedRecords');
        }
      
        return fetchCaseTask(true, queueData, queueValue, viewVlue, filterValue, assignWorkValue, secFilter,  lstItemAgeValue, lstTeamMembers); 
    }
    
    /*
     * Method Name : checkUserQueue
     * Description : Get the current queue of user
     * Return type : Boolean
     * Parameter   : NA
    */
    @RemoteAction 
    public static Boolean checkUserQueue()
    {
        String sCurrentQueue =  [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c;
        Boolean isQueueSelected;
        if(string.isBlank(sCurrentQueue ))
            isQueueSelected = false;
        else
            isQueueSelected = true;
        return isQueueSelected;
    }
    
     /*
     * Method Name : checkQAA
     * Description : Check if cases have qaa attached
     * Return type : Boolean
     * Parameter   : Map of records
    */
    @RemoteAction 
    public static List<string> checkQAA(map<string,Boolean> mapCaseDTO)
    {
        List<Case_Action_Association__c> lstCaseAction = [SELECT Id, Case__c,Case__r.CaseNUmber, Case_Action__r.name FROM Case_Action_Association__c where Case_Action__r.Name = :GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_Intent_HUM and Case__c in :mapCaseDTO.keyset()];
        List<string> lstCases = new List<string>();
        if(lstCaseAction != Null && !lstCaseAction.isEmpty())
        {
            for(Case_Action_Association__c caseAction: lstCaseAction)
            {
                for(String s: mapCaseDTO.keyset())
                {
                    if(caseAction.Case__c == s)
                    {
                        lstCases.add(caseAction.Case__c+'+'+caseAction.Case__r.CaseNUmber);
                        break;
                    }
                }
            }
        }
        return lstCases;
    }
    /**
     * Method used to update Status
     * <p>
     * @return   list<CaseTaskView_DTO_HUM> : List of Wrapper Records returned after accepting the WorkItems
     * @param    map<string,boolean> : Map of records needs to be Pulled
     */
    @RemoteAction 
    public static list<CaseTaskView_DTO_HUM> transferOfAssignedRecords(map<string,boolean> mapOfUnassignedRecords,list<string> queueData, List<string> queueValue,List<string> viewVlue,List<string> filterValue,string assignWorkValue, string secFilter, string scasecom,string sServiceCenter,string sDepartment,string sTopic,string sOwnerName, string sOwnerId, string jsonoWQS, map<string,CaseTaskView_DTO_HUM> mapCaseDTO, string sTransferReason, List<String> lstItemAgeValue, List<String> lstTeamMembers)
    {
       Set<String> sCases = new Set<String>();
       transferFailCases = new List<String>();
       try
        {
            List<string> lstIds = new List<string>();
            Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
            if(jsonoWQS != null)
            oWQS = (Work_Queue_setup__c)JSON.deserialize(jsonoWQS, Work_Queue_setup__c.Class);
            string sSelectedWorkQueue = oWQS.Work_Queue_Name__c;
            string sPublicGroupName= oWQS.Public_Group_Name__c;
            list<Case> caseList = new list<Case>();
            string sGrpQueueName = sSelectedWorkQueue.replace('_',' ');
            map<string,boolean> mapCaseComment = new map<string,boolean>();
            list<CaseComment> commentList = new list<CaseComment>();
            list<Database.SaveResult> updatedCaseList = new list<Database.SaveResult>();
            String sGrpName = sGrpQueueName+GLOBAL_CONSTANT_HUM.HYPHEN_HUM+sTopic;
            boolean bvalidCase;
            Boolean bCheckMedicareCCPermission;
            Boolean bMedicareCalls;
            Boolean bCheckMedicareCustomerServiceAccess;
            Boolean bIsPharmacyUser;
            Boolean bRSOHPFlag;
            Boolean bPhamacyQueue;
            String sMedicareResInit;
            Boolean bIsPDPPilotUser;
            Boolean isPharmacyProfile;
            Boolean CaseReadOnly;
            Boolean bGAndASectionRender;
            Profile currentProfile = new Profile();
            currentProfile = [Select name from profile where id = :UserInfo.getProfileId()];
            List<String> sCenters = new List<String>{GLOBAL_CONSTANT_HUM.SERVICE_CENTER_HUMANA_PHARMACY,GLOBAL_CONSTANT_HUM.SERVICE_CENTER_RX};
            Set<String> wqueues = new Set<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
            Set<String> chatQueues = new Set<String>(GLOBAL_CONSTANT_HUM.WEB_CHAT_WQUEUES);
            Set<String> processQueues = new Set<String>{GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_OWNER,GLOBAL_CONSTANT_HUM.WORK_QUEUE_LV_Screening,GLOBAL_CONSTANT_HUM.WORK_QUEUE_PCP_Assignment_Research,GLOBAL_CONSTANT_HUM.GOODCAUSEQUEUE};
            List<Case_Action_Association__c> lstCaseAction = [SELECT Id, Case__c, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = :GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCHCONTROLLER_Intent_HUM and Case__c in :mapOfUnassignedRecords.keyset()];
            Map<id,list<CaseCommentType_Junction__c>> caseCCTJunc = new Map<id,list<CaseCommentType_Junction__c>>();
            map<Id, CaseComment> mapMediCC = new map<Id, CaseComment>([SELECT Id, CommentBody, ParentId, createddate FROM CaseComment WHERE ParentId in :mapOfUnassignedRecords.keyset()]);
            caseCCTJunc = CaseTaskView_H_HUM.getCommentsMap(mapOfUnassignedRecords.keyset(),mapMediCC);
            List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c,Template_Submission__r.Template__r.Unique_Template_Name__c,Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  in (:GLOBAL_CONSTANT_HUM.MedExpTEMPLATEUNIQUEID,:GLOBAL_CONSTANT_HUM.PartDTEMPLATEUNIQUEID, :GLOBAL_CONSTANT_HUM.ATTESTATION_TEMPLATE_NAME, :GLOBAL_CONSTANT_HUM.GOODCAUSEUNIQUEID, :GLOBAL_CONSTANT_HUM.PCPTEMPLATEUNIQUEID) and Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c in :mapOfUnassignedRecords.keyset()];
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and Group.Name in (:GLOBAL_CONSTANT_HUM.GOODCAUSEGROUPNAME, :GLOBAL_CONSTANT_HUM.PCPGROUPNAME, :GLOBAL_CONSTANT_HUM.Group_LV_Correspondence_Screening_PGQ, :GLOBAL_CONSTANT_HUM.Group_LV_Correspondence_Screening_PGVE, :GLOBAL_CONSTANT_HUM.Group_LV_Correspondence_Screening_PGVP)];
            Set<String> grpLst = new Set<String>();
            if(lstGrpMem != null && !lstGrpMem.isEmpty())
            {
                for(GroupMember g : lstGrpMem)
                {
                   grpLst.add(g.Group.Name);
                }
            }
            bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM ? false : true;
            bIsPDPPilotUser = Pharmacy_H_HUM.isPDPPilotUser();
            MedicareCallsCaseComments_H_HUM oMedCalls = new MedicareCallsCaseComments_H_HUM();
            Set<String> setPharRecType = new Set<String>(GLOBAL_CONSTANT_HUM.Pharmacy_Record_Type_Name); 
            List<PermissionSetAssignment> lstPermission = [SELECT PermissionSetId,PermissionSet.Name FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name in (:GLOBAL_CONSTANT_HUM.RSO_HP_PermissionSet_Name, : GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM)];
            List<PermissionSetAssignment> lstRSOHPPermissionSet = new List<PermissionSetAssignment>();
            list<PermissionSetAssignment> lstPMAssign = new List<PermissionSetAssignment>();
            if(lstPermission != Null && !lstPermission.isEmpty())
            {
                for(PermissionSetAssignment lstP : lstPermission)
                {
                if(lstP.PermissionSet.Name==GLOBAL_CONSTANT_HUM.RSO_HP_PermissionSet_Name)
                lstRSOHPPermissionSet.add(lstP);
                if(lstP.PermissionSet.Name==GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM)
                lstPMAssign.add(lstP);
                }
            }
            Set<id> polId = new Set<id>();
            list<Policy__c> pol = new list<Policy__c>();
            Map<id,Policy__c> polMap = new Map<id,Policy__c>();
            Map<id,id> polCaseMap = new Map<id,id>();
            for(string str : mapOfUnassignedRecords.keyset())
            {
                CaseTaskView_DTO_HUM oCaseDTO =  mapCaseDTO.get(str);
                if(oCaseDTO.sPolicy!=null){
                polId.add(oCaseDTO.sPolicy);
                polCaseMap.put(str,oCaseDTO.sPolicy);
                }
            }
            if(polId != null && !polId.isEmpty())
            pol = [SELECT Major_Lob__c, Policy__c.Product__c, Policy__c.Product__r.Product_Type__c, Policy__c.Product__r.Segment_Type__c, Policy__c.Product__r.ProductCode from Policy__c WHERE id in :polId];
            if(pol != null && !pol.isEmpty()){
            for(id caseId : polCaseMap.keyset())
            {
               for(Policy__c p : pol)
                {
                if(polCaseMap.get(caseId) == p.id)
                polMap.put(caseId,p);
                }  
            }
            }
            for(String s : mapOfUnassignedRecords.keyset())
            {
                Case oCase = new Case(id = s);
                bvalidCase = true;
                CaseTaskView_DTO_HUM oCaseDTO =  mapCaseDTO.get(s);
                string caseRecordTypeName = Schema.SObjectType.Case.getRecordTypeInfosById().get(oCaseDTO.sRecordTypeId).getName();
                bCheckMedicareCustomerServiceAccess = false;
                bCheckMedicareCCPermission = false;
                bMedicareCalls = false;
                Policy__c  policy = new Policy__c();
                // Intent and Classification value check
                if(String.isBlank(oCaseDTO.sIntent) || String.isBlank(oCaseDTO.sClassification))
                   bvalidCase = false;
                //Interacting with check
                if (oCaseDTO.sInteractingWithId == NULL && String.isBlank(oCaseDTO.sInteractingWithName))
                {
                    bvalidCase = false;
                }
                //Transfer permission check 
                isPharmacyProfile = false;
                CaseReadonly = true;
                if(currentProfile.Name == Label.PHARMACY_SPECIALIST_PROFILE_NAME)       isPharmacyProfile  = true;
                if(bvalidCase)
                {
                    if(!caseRecordTypeName.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType))
                    {
                      if(isPharmacyProfile && ((chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN) || oCaseDTO.sProduct == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY))
                      {
                        if(wqueues.contains(oCaseDTO.sCaseTaskQueue) || wqueues.contains(oCaseDTO.sCreatedQueue) || (chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
                        {
                          CaseReadonly = false;
                        }
                      }
                    }                    
                    if(caseRecordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE))
                    {
                        if(!((!isPharmacyProfile) || (isPharmacyProfile && !CaseReadOnly)))
                            bvalidCase = false;
                    }
                }
                //G&A rights and Complaint check
                bGAndASectionRender = false;
                bGAndASectionRender = CaseTaskView_H_HUM.checkRecordType(caseRecordTypeName, oCaseDTO.sInteractingAboutType); 
                if(bGAndASectionRender == true && (oCaseDTO.sGARightsGiven == NULL || oCaseDTO.sComplaint == NULL))
                    bvalidCase = false;
                //Medicare comments check
                if(bvalidCase)
                {
                    sMedicareResInit = '';
                    bRSOHPFlag = false;
                    if((!bIsPharmacyUser) && (setPharRecType.contains(caseRecordTypeName)))
                    {
                        bIsPharmacyUser = bIsPDPPilotUser; 
                    }
                    if(lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty())
                    {
                        if((!bIsPharmacyUser) && (setPharRecType.contains(caseRecordTypeName)))
                        {
                            bIsPharmacyUser = true;
                            bRSOHPFlag = true;
                        }
                        else
                        {
                            bIsPharmacyUser = false;
                            bRSOHPFlag = false;
                        }
                    }
                    bCheckMedicareCustomerServiceAccess = CaseTaskView_H_HUM.validateMedicareCustomerServiceAccess(oCaseDTO.sOrigin,lstPMAssign);
                    if (oCaseDTO.sPolicy != null && polMap.containsKey(s))
                        policy = polMap.get(s);
                    if (caseRecordTypeName != GLOBAL_CONSTANT_HUM.MEDICARE_CASE && caseRecordTypeName != GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE)
                    {
                        if (oCaseDTO.sPolicyMember != null && bCheckMedicareCustomerServiceAccess)
                        {
                            if (oCaseDTO.sPolicy != null && policy != null)
                                bMedicareCalls = oMedCalls.checkMedicareCare(oCaseDTO.sOrigin, policy.Product__r.ProductCode, policy.Major_Lob__c, policy.Product__r.Product_Type__c, policy.Product__r.Segment_Type__c);
                            if (lstPMAssign != null && !lstPMAssign.isEmpty()) 
                                bCheckMedicareCCPermission = true;
                        }
                    }
                    if(bMedicareCalls && bCheckMedicareCCPermission && !bIsPharmacyUser)
                    {
                       mapCaseComment.put(s,true);
                       list<CaseCommentType_Junction__c> lstCCTJunction = caseCCTJunc.get(Id.valueOf(s));
                        if (lstCCTJunction != null && !lstCCTJunction.isEmpty() && String.isBlank(scasecom))
                        {
                            sMedicareResInit = CaseTaskView_H_HUM.getMedCom(lstCCTJunction, mapMediCC);
                            if (String.isBlank(sMedicareResInit)){
                                bvalidCase = false;
                             }
                             else if(!String.isBlank(sMedicareResInit))
                             {
                             if(sMedicareResInit.length() > 2000 || sMedicareResInit.length() < 10)
                             bvalidCase = false;
                             }
                        }
                        else if(String.isBlank(scasecom))
                        {
                            bvalidCase = false;
                        }   
                        if(String.isNotBlank(scasecom) && (scasecom.length() > 2000 || scasecom.length() < 10))
                            bvalidCase = false;
                    }
                    else
                        mapCaseComment.put(s,false); 
                }
                //Check for pharmacy queues
                bPhamacyQueue = false;
                if(bvalidCase && lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty() && setPharRecType.contains(caseRecordTypeName))
                {
                    if(!(sCenters.contains(sServiceCenter) || wqueues.contains(sGrpQueueName)))
                    {
                        bvalidCase = false;    
                    }
                }
                //Change Case Owner button enable check on case detail page
                if(bvalidCase)
                {
                    Boolean bCloseDisableDetailPage;
                    if(oCaseDTO.sStatus == 'External Transfer')
                        bvalidCase = false;                    
                    else if(bvalidCase && processQueues.contains(oCaseDTO.sCaseTaskQueue) && (caseRecordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICARE_CASE) || caseRecordTypeName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNCASE_HUM)))
                    {
                        bCloseDisableDetailPage = CaseTaskView_H_HUM.checkDetailPageClose(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,grpLst,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s,caseRecordTypeName);   
                        if(bCloseDisableDetailPage) bvalidCase = false;
                    }
                }
                if(bvalidCase)
                {
                    Group oGrp = NULL;  
                    oCase.Classification_Type__c =  oWQS.Classification_Type__r.Name; 
                    oCase.Owner_Queue__c = sGrpQueueName;
                    oCase.Service_Center__c = sServiceCenter;
                    oCase.Department__c = sDepartment;
                    oCase.Transfer_Reason__c = sTransferReason;
                    if(String.isNotBlank(sTopic) && !sTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
                    {
                        oCase.topic__c = sTopic;
                    }
                    else if(String.isBlank(sTopic) || sTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
                    {
                        oCase.topic__c = '';
                    }
                    if((String.isBlank(sOwnerName) || String.isBlank(sOwnerId)) && NULL != oCase)
                    {
                        if (NULL != oWQS.OwnerId)  
                        {
                             oCase.Case_Owner__c = sGrpQueueName;
                             oCase.OwnerId = oWQS.OwnerId;
                        }
                    }
                    else if((!String.isBlank(sOwnerName) && !String.isBlank(sOwnerId)) && NULL != oCase)
                    {   
                        oCase.OwnerId = sOwnerId;
                        oCase.Case_Owner__c = sOwnerName;
                    }
                    caseList.add(oCase);
                }
            }
            if(!caseList.isEmpty())
            {
             updatedCaseList = database.update(caseList,false);
            }
            if(updatedCaseList != null && !updatedCaseList.isEmpty())
            {
                for(Database.SaveResult sr : updatedCaseList)
                {
                    if(sr.isSuccess())
                    lstIds.add(sr.getId());
                }
                if(lstIds != null && !lstIds.isEmpty() && String.isNotBlank(scasecom))
                {
                    CaseTaskView_H_HUM.addCaseComment(scasecom, mapCaseComment, lstIds);
                }
           }
           sCases.addAll(lstIds);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseTaskView_C_HUM', 'transferOfAssignedRecords');
        }
        //Get IDs of not transferred cases
        if(sCases != null && !sCases.isEmpty())
        {
           for(String s : mapOfUnassignedRecords.keyset())
           {
               if(!sCases.Contains(s))
               transferFailCases.add(s);
           }
        }
       else
           transferFailCases.addall(mapOfUnassignedRecords.keyset());
        return fetchCaseTask(true, queueData, queueValue, viewVlue, filterValue, assignWorkValue, secFilter, lstItemAgeValue, lstTeamMembers);
    } 
    /*
     * Method Name : getComments
     * Description : Get the sObjectId for the case/Task on which mouse is hover
     * Return type : void
     * Parameter   : NA
    */      
    public void getComments()    
    {        
        sobjectId = ApexPages.CurrentPage().getParameters().get('sobjectIdValue');        
    }
    
    //Preparing Receiveddates related to the cases
   public static map<Id,string> prepareCaseReceivedData(set<string> setCaseIds){
        map<Id,string> mapCaseToReceiveddate = new map<Id,string>();
        try{
        if (setCaseIds != null){
        
        for(Attachment_Log__c objAttLog : [SELECT Id,Case__c,(select Name,Value__c from Attachment_Log_Items__r) from Attachment_Log__c where Case__c in : setCaseIds]){
            list<Attachment_Log_Item__c> lstATTLI = objAttLog.Attachment_Log_Items__r;
            if(lstATTLI!=null && !lstATTLI.isEmpty() && lstATTLI.size()>1){
                boolean isReceivedDate = false;
                Attachment_Log_Item__c objResult;
                for(Attachment_Log_Item__c objALI : lstATTLI){
                    if(objALI.Name == 'Received Date'){
                        isReceivedDate = true;
                        objResult = objALI;
                    }
                }            
                if(isReceivedDate && objResult!=null){
                    mapCaseToReceiveddate.put(objAttLog.Case__c,objResult.Value__c);
                }
            }
        }
        
      }
    }
    catch (exception ex){
       HUMExceptionHelper.logErrors(ex,'CaseTaskView_C_HUM','prepareCaseReceivedData');             
    }

   return mapCaseToReceiveddate;
}

    /*
    * This method Account and Policy details
    * @param   NA
    * @return  NA
    */
    public void getPolicyAccountDetails(){
        try{
            List<Case> lstCasePolicyAccount = [SELECT Id, Account.Name, Policy_Member__c, Product__c, Policy_Member__r.Name FROM Case where Id = :accountTabId];
            bPolicyAccount =  (!lstCasePolicyAccount.IsEmpty())? true : false ;
            if(bPolicyAccount) {
                Case objCase = lstCasePolicyAccount[0];
                if(objCase != null && objCase.Policy_Member__c != null){              
                    accountName = objCase.Account.Name;
                    policyId = objCase.Policy_Member__c;
                    policyName = objCase.Product__c + '-' + objCase.Policy_Member__r.Name;
                } else {
                    bPolicyAccount = false;
                }
            }
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CaseTaskView_C_HUM', 'getPolicyAccountDetails');
        }
    }
    /*
    * This method to get the team members/reportees for current supervisior.
    * @param  NA
    * @Return list<SelectOption>
    */
    public void getMyTeamUsers() {
        lstMyteamReportee = new List<SelectOption>();
        if(bMyTeamInvChk){
            List<User> lstMyTeamMber = [SELECT Id, Name FROM User WHERE Supervisor_Email_Address__c =:UserInfo.getUserEmail() AND Current_Queue__c  != '' AND IsActive =true ORDER BY Name limit 150];
            if(!lstMyTeamMber.isEmpty()){ 
                lstMyteamReportee.add(new SelectOption('Select All', 'Select All'));
                for(User usr : lstMyTeamMber) {
                    lstMyteamReportee.add(new SelectOption(usr.Id, usr.Name));
                }
            } 
        }
    }
}