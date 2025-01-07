/************************************************************************************************************************
Apex class Name    : Hum_CaseTasks_LWC
Version            : 1.0
Function           : Apex Class to display list of Cases/Tasks on Case/Task Page in LWC
Created On         : December 29 2021
Test Class         : Hum_CaseTasks_T_LWC

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala/
	Shailesh Bagade/Gowthami Thota                  12/29/2021          Original Version(Feature 2963843 - Cases/Tasks Tab)
***************************************************************************************************************************/

public with sharing class Hum_CaseTasks_LWC {

    // List of   Private Variables.
    private static   final String caseQuery ='SELECT CaseNumber,Open_Work_Task_Count__c ,Total_Work_Task_Count__c, Work_Queue_View_Name__c, Case_Owner__c, Priority, Status, Open_ExtensionTask_Count__c, Total_ExtensionTask_Count__c,'
                                                        +'Case_Age__c, Case_Number__c, CTCI_List__r.Intent__r.Name,CTCI_List__r.Intent__c,Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c,' 
                                                        +'Re_Open_Case_Age__c, CTCI_List__r.Classification__r.Name,CTCI_List__c , CTCI_List__r.Classification__c,Days_Since_Last_Modified__c, Due_Date__c, Ownerid, '
                                                        +'Follow_Up_Due_Date__c, CreatedDate, Origin, Policy_Member__c,RecordTypeId,Policy_Member__r.Policy__c,Classification_Type__c,AccountId,Complaint__c,Interacting_About_Type__c, '
                                                        +'Call_Benefit_Category__c,Product__c,Created_By_Queue__c,Guided_Process_Flag__c,AutoRoute_Status__c,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Interacting_With_Name__c,G_A_Rights_Given__c,Contract_Protected_Case__c FROM Case where (Status IN : lstValidCaseStatus) ';
    
    private static   final String caseQueryRF ='SELECT CaseNumber,Open_Work_Task_Count__c ,Total_Work_Task_Count__c, Work_Queue_View_Name__c, Case_Owner__c, Priority, Status, Open_ExtensionTask_Count__c, Total_ExtensionTask_Count__c,'
                                                        +'Case_Age__c, Case_Number__c, CTCI_List__r.Intent__r.Name,CTCI_List__r.Intent__c,Account.Name, Subject, Interacting_With__c, Interacting_With__r.Name, Owner_Queue__c,' 
                                                        +'Re_Open_Case_Age__c, CTCI_List__r.Classification__r.Name,CTCI_List__c , CTCI_List__r.Classification__c,Days_Since_Last_Modified__c, Due_Date__c, Ownerid, '
                                                        +'(SELECT id, iscompleted, isviolated, TimeRemainingInMins, TimeSinceTargetInMins FROM CaseMilestones where iscompleted = false order by id limit 1),'
                                                        +'Follow_Up_Due_Date__c, CreatedDate, Origin, Policy_Member__c,RecordTypeId,Policy_Member__r.Policy__c,Classification_Type__c,AccountId,Complaint__c,Interacting_About_Type__c, '
                                                        +'Call_Benefit_Category__c,Product__c,Created_By_Queue__c,Guided_Process_Flag__c,AutoRoute_Status__c,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Interacting_With_Name__c,G_A_Rights_Given__c,Contract_Protected_Case__c FROM Case where (Status IN : lstValidCaseStatus) ';
                                                        
    private static   final String taskQuery = 'SELECT QueueOrUserId__c ,Date_Time_Reopned__c , Subject,Open_Work_Task_Count__c,Total_Work_Task_Count__c, Classification__c,'
                                                       +'Work_Queue_View_Name__c,Re_Open_Task_Age__c, Intent__c, Task_Owner__c, Whoid, Whatid, ActivityDate, Task_Number__c, Status, '
                                                       +'Ownerid, Owner_Queue__c, Priority, Select__c, Age_Since_Created__c, Last_Modified_Age__c, CreatedDate FROM Task ' 
                                                       +'where (Status IN : lstValidTaskStatus) ';

    private static   final list<String> lstValidTaskStatus = new list<String>{HumConstants_LWC.TASKLISTVIEW_INPROGRESS_HUM,HumConstants_LWC.TASKLISTVIEW_PENDING_HUM};
    private static   final list<String> lstValidCaseStatus = new list<String>{HumConstants_LWC.TASKLISTVIEW_INPROGRESS_HUM,HumConstants_LWC.TASKLISTVIEW_PENDINGTASK_HUM,
                                                                                      HumConstants_LWC.TASKLISTVIEW_PENDINGRESPONSE_HUM,HumConstants_LWC.TASKLISTVIEW_PENDINGTRANSFER_HUM};
    private static  map<String,list<String>> mapGroupQueue = new map<String, list<String>>();
    private static list<String> lstCatTempQueues = new list<String>();
    
    public static map<String, Work_Queue_Setup__c> mapWorkQueueSetup = new map<String, Work_Queue_Setup__c>([Select Name,OwnerId, id,Public_Group_Name__c, Work_Queue_Name__c 
                                                            from Work_Queue_Setup__c where IsActive__c = TRUE limit :HumConstants_LWC.TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM]);
    private static HUMSearchLimit__c objcasequeryLimit;
    private static  HUMSearchLimit__c objTaskqueryLimit;
    private static List<String>  lstCatTemp = new List<String>();
    public static boolean isPDPPermissionSetUser {get; set;}
    public static boolean isRCCPermissionSetUsr {get; set;}
    public static boolean bMyTeamInvChk {get; set;}
    public static List<SelectOption> lstMyteamReportee {get; set;} 
    public static boolean bswitch_US2578074 {get; set;}
    public static String iUptoRange = '5';
    public static String iStartRange = '6';
    public static String iEndRange = '10';
    public static String iGtrRange = '10';
    public static map<String, String> caseAndTaskMap;
    public static String userCurrentProfile;
    
    /**
    * Constructor of the Class
    * <p>    
    * @return     NA
    * @param      NA
    */
    public static void getHum_CaseTasks_LWC()
    {       
        bswitch_US2578074=HUMUtilityHelper_LWC.isCRMFunctionalityON('2578074');
        Hum_CaseTasks_LWC.bPharmacySpecialistUser = false;
        Hum_CaseTasks_LWC.userCurrentProfile = Hum_CaseTasks_LWC.getCurrentUserProfileName();
        Hum_CaseTasks_LWC.bPharmacySupervisorUser = false;
        Hum_CaseTasks_LWC.sSelectedAssign = HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM;
        Hum_CaseTasks_LWC.sSelPullWorkFilterBy = HumConstants_LWC.TASKLISTVIEW_DUESPACEDATE_HUM;
        Hum_CaseTasks_LWC.objcasequeryLimit = new HUMSearchLimit__c();
        objTaskqueryLimit = New HUMSearchLimit__c();
        sPharmacyUser = HUMUtilityHelper_LWC.isPharmacyUser();
        if(sPharmacyUser == HumConstants_LWC.PHARMACYSPECIALIST_USER_HUM)
        {
            bPharmacySpecialistUser = true;
        }
        else if(sPharmacyUser == HumConstants_LWC.PHARMACYSUPERVISOR_USER_HUM)
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
        statusOptions.add(new SelectOption(HumConstants_LWC.HUM_CLOSED,HumConstants_LWC.HUM_CLOSED));
        statusOptions.add(new SelectOption(HumConstants_LWC.HUM_CANCELLED,HumConstants_LWC.HUM_CANCELLED));
 		bEnableCloseCaseButton = HUMUtilityHelper_LWC.isCRMFunctionalityON('800050'); 
        bEnableTransferButton = HUMUtilityHelper_LWC.isCRMFunctionalityON('1045266');
        reRenderQueueSelectionPage = false;
        isPDPPermissionSetUser = HUMUtilityHelper_LWC.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot');
        Filterval = '';
        lstSecurityView = new List<String>();
        isRCCPermissionSetUsr = HUMUtilityHelper_LWC.isUserHavingMedicareCustomerServiceAccess();
        bMyTeamInvChk = false;
    }
    public static List<String> lstSecurityView {get;set;}
    public static boolean reRenderQueueSelectionPage {get; set;} 
    public static string userCurrentQueue{ get; set; }
    public static string caseComment{ get; set; } 
    public static List<SelectOption> statusOptions {get;set;}
    public static String statusValue {get;set;}
    public static String Filterval{get;set;}
    public static Boolean bEnableCloseCaseButton {get;set;}
    public static List<String> lstSelPullWorkQueue {get;set;}
    public static List<String> lstSelPullWorkView{get;set;}
    public static List<String> lstViewCategorydd1 {get;set;}
    public static List<String> lstViewCategorydd2 {get;set;}
    public static List<String> lstSelectedView {get;set;}
    public static List<String> lstWorkItems {get;set;}
    public static String sViewCategorydd1 {get;set;}
    public static String sViewCategorydd2 {get;set;}
    public static string sSelectedAssign {get; set;}
    public static boolean bShowData {get; set;}
    public static string sSelPullWorkFilterBy {Get; Set;}
    Public static String sobjectId {Get; Set;}
    Public Static string sPharmacyUser{Get;Set;}
    public static boolean bPharmacySpecialistUser {get; set;}
    public static boolean bPharmacySupervisorUser {get; set;}
    public static String accountTabId {get;set;}
    public static String accountName {get;set;}
    public static string PolicyId {get; set;}
    public static String policyName {get;set;}
    public static boolean bPolicyAccount {get; set;}
    public Static List<String> transferFailCases;
    public static Boolean bEnableTransferButton {get;set;}
    
    
    
    /*
     * Method Name : getCurrentUserProfileName
     * Description : retrieves the current user profile name 
     * Return type : String
     * Paramater   : None
     */
    @AuraEnabled(cacheable = true)
    public static String getCurrentUserProfileName()
    {       
        Id profileId=userinfo.getProfileId();
        String profileName=[Select Id,Name from Profile where Id=:profileId].Name;
        return profileName;
    }
    
   

    /*
     * Method Name : getCurrentUserGroups
     * Description : retrieves the current user groups
     * Return type : List<String>
     * Paramater   : None
     */
    public static List<String> getCurrentUserGroups()
    {     
        List<Group> lstGroups = new List<Group>();
		List<String> userGroups = new List<String>();
        List<String> userGroupspgq = new List<String>();
		//Id userId = '0050G00000CQdTtQAL';
        Id userId = UserInfo.getUserId();
	
			for(Group gp: [Select name from Group where Id IN (Select GroupId from GroupMember where UserorGroupId=:userId)])
            {
				userGroups.add(gp.Name);
			}
        	for(String grp : userGroups){
           		 if(grp.right(3) == 'PGQ'){
               		 userGroupspgq.add(grp);               
            	}
            
        	}
       
        return userGroupspgq;
    }
     /*
     * Method Name : getWorkQueueSetupRecords
     * Description : retrieves the current user groups pgq mapping work queue records
     * Return type : List<String>
     * Paramater   : None
     */
    @AuraEnabled(cacheable=true)
    public static List<String> getWorkQueueSetupRecords()
    {   
       List<String> userGroupspgq = new List<String>();
       List<String> workQueueSetuprecs = new List<String>(); 
       userGroupspgq=Hum_CaseTasks_LWC.getCurrentUserGroups();
        
       map<String, Work_Queue_Setup__c> mapWorkQueueSetup1 = new map<String, Work_Queue_Setup__c>([Select Name,id,Public_Group_Name__c, Work_Queue_Name__c 
                                                            from Work_Queue_Setup__c where IsActive__c = TRUE and Public_Group_Name__c =:userGroupspgq limit :HumConstants_LWC.TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM]);
       
       for (String sWorkQueue : mapWorkQueueSetup1.keySet())
       {               
               workQueueSetuprecs.add(mapWorkQueueSetup1.get(sWorkQueue).Name) ;
	   }
        
        return workQueueSetuprecs;
            
    }

 /*
     * Method Name : getQueueViewRecords
     * Description : retrieves the queue view records corresponding to the work queue records
     * Return type : List<String>
     * Paramater   : None
     */
    public static List<String> getQueueViewRecords()
    {   
        List<String> workQueueSetuprecs = new List<String>();
        List<String> workQueueViewrecs = new List<String>(); 
        workQueueSetuprecs=Hum_CaseTasks_LWC.getCategoriesdd1();
        
        for(Queue_View__c queueView : [Select Name,id,Queue_View_Name__c,Queue_Name__c, Work_Queue_Name__c 
                                              from Queue_View__c where IsActive__c = TRUE and Queue_Name__c =:workQueueSetuprecs limit :HumConstants_LWC.TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM] ){
            workQueueViewrecs.add(queueView.Queue_View_Name__c+ ' (' +queueView.Queue_Name__c +' )');
        }
        
        
        return workQueueViewrecs;
    }
    
    /*
     * Method Name : getQueueViewRecords
     * Description : retrieves the queue view records corresponding to the work queue records
     * Return type : List<String>
     * Paramater   : None
     */
    public static List<String> getWorkQueueSelectedQueueView(List<String> workqueueSelected)
    {
        List<String> QueueViewrecs = new List<String>(); 
        
        for(Queue_View__c queueView : [Select Name,id,Queue_View_Name__c,Queue_Name__c, Work_Queue_Name__c 
                                              from Queue_View__c where IsActive__c = TRUE and Queue_Name__c =:workqueueSelected limit :HumConstants_LWC.TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM] ){
            QueueViewrecs.add(queueView.Queue_View_Name__c+ ' (' +queueView.Queue_Name__c +' )');
        }
        
       
        return QueueViewrecs;
    }
    
    /**
    * Method used to populate SelectedAssign Dropdown
    * <p>
    * @return list<String>
    * @param NA
    */

    public static list<String> getSelectedAssign()
    {
        list<String> lstSelectedAssign = new list<String>();
        lstSelectedAssign.add(HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM);
        lstSelectedAssign.add(HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM);
        
        return lstSelectedAssign;
    }
    
    /**
    * Method to populate SelectedView Dropdown
    * <p>
    * @return : list<String>
    * @param : NA
    */
    public static list<String> getSecurityView()
    {
        list<String> lstSecurityView = new list<String>();
        lstSecurityView.add(HumConstants_LWC.General);
        lstSecurityView.add(HumConstants_LWC.WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM);
        lstSecurityView.add(HumConstants_LWC.HOAllOther);
        if(bswitch_US2578074)
        lstSecurityView.add(HumConstants_LWC.ContractProtectedData);
       
        return lstSecurityView;
    }
    
    
     /**
    * Method to get Public Groups in which the logged in user is part of.
    * <p> 
    * @return  :  list<string>
    * @param    :  NA
    */
    public static list<string> getQueueList()
    {
        list<String> lstMyGroups =  new list<String>();  
        list<GroupMember> lstGrpMem = [select Group.Name from GroupMember where UserOrGroupId =: Userinfo.getUserId() And Group.Type =: HumConstants_LWC.TASKLISTVIEW_REGULAR_HUM];
        for(GroupMember oGrpMem : lstGrpMem)
        {
            lstMyGroups.add(oGrpMem.Group.Name); 
        } 
        
        return lstMyGroups;
    } 
    
    
    /**
    * Method is used to initialize workQueueSelector, GroupQueue, Category DDs
    * <p>
    * @return   void
    * @param    NA
    */
    @AuraEnabled
    public static void initializeWorkQueue()
    {
            list<String> lstMygroups = Hum_CaseTasks_LWC.GetQueueList();
            
            list<String> lstQueues = NULL;

            for (String sWorkQueue : mapWorkQueueSetup.keySet())
            {
                Work_Queue_Setup__c oWorkQueue = mapWorkQueueSetup.get(sWorkQueue);
                lstQueues = mapGroupQueue.get(oWorkQueue.Public_Group_Name__c);
                if(NULL == lstQueues)   lstQueues = new list<String>();
                lstQueues.add(oWorkQueue.Work_Queue_Name__c);                
                mapGroupQueue.put(oWorkQueue.Public_Group_Name__c, lstQueues);
            }

            Hum_CaseTasks_LWC.initializeGroupQueue(lstMygroups, lstQueues);
       
    } 
    
    
    /**
    * Method used to initialize GroupQueue and Category DDs
    * <p>
    * @return : void
    * @param   : list<String>, list<String>
    */
    private static void initializeGroupQueue(list<String> lstMygroups, list<String> lstQueues)
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
            
            if(sSelectedAssign.equalsIgnoreCase(HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM))
            {
                sViewCategorydd1 = HumConstants_LWC.TASKLISTVIEW_ALL_HUM;
            }
            else if(sSelectedAssign.equalsIgnoreCase(HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM))
            {
                sViewCategorydd1 = lstCatTempQueues[0];
            }
            else
            {
                sViewCategorydd1 = HumConstants_LWC.TASKLISTVIEW_ALL_HUM;   
            }
            sViewCategorydd2 = HumConstants_LWC.TASKLISTVIEW_ALL_HUM;   
            bShowData = false;   
        }
        else
        {
            bShowData = true;
            throw new HUMCustomException(HumConstants_LWC.TASKLISTVIEW_ERRORQUEUENOACCESS_HUM);                  
        }
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
            //temporary change  removed -1
            //lstSelectedViews.add(sView.mid(0, sView.lastIndexOf('(')));
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
            sQuery += '( Owner_Queue__c =\'' + String.escapeSingleQuotes(strwQ) + '\' AND Work_Queue_View_Name__c NOT IN (\''+ String.escapeSingleQuotes(strQV) +'\'))';
            count++;
        }
        return sQuery;
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
            sCaseQuery +=' ( Owner_Queue__c = \'' + String.escapeSingleQuotes(sQueueValue) + '\'  ';
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
                        sCaseQuery += 'Work_Queue_View_Name__c IN (\'' + String.escapeSingleQuotes(sViewVal) + '\'))' ;
                        
                    }
                    else
                    {
                        if(iInnerloop == 1)
                        {
                            sCaseQuery +=  'Work_Queue_View_Name__c IN (\''+ String.escapeSingleQuotes(sViewVal) +'\'' ;
                        }
                        if(iViewSize  == iInnerloop )
                        {
                            sCaseQuery +=  ',\''+ String.escapeSingleQuotes(sViewVal) +'\'))';
                        }
                        if(iInnerloop !=1 && iViewSize != iInnerloop )
                        {
                            sCaseQuery +=  ',\''+ String.escapeSingleQuotes(sViewVal) +'\'';
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
            sTaskQuery +=' ( Owner_Queue__c = \'' + String.escapeSingleQuotes(sQueueValue) + '\' ';
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
                    sTaskQuery += 'Work_Queue_View_Name__c IN (\'' + String.escapeSingleQuotes(sViewVal) + '\'))' ;
                    
                }
                else
                {
                    if(iInnerloop == 1)
                    {
                        sTaskQuery +=  'Work_Queue_View_Name__c IN (\''+ String.escapeSingleQuotes(sViewVal) +'\'' ;
                    }
                    if(iViewSize  == iInnerloop )
                    {
                        sTaskQuery +=  ',\''+ String.escapeSingleQuotes(sViewVal) +'\'))';
                    }
                    if(iInnerloop !=1 && iViewSize != iInnerloop )
                    {
                        sTaskQuery +=  ',\''+ String.escapeSingleQuotes(sViewVal) +'\'';
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
    * Method is called on load of page to populate Case Task Wrapper records.
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of fetched wrapper records 
    * @param    NA
    */
    @AuraEnabled
    public static list<CaseTaskView_DTO_HUM_LWC> fetchCaseTask(Boolean bPerformFilter, List<string> lstQueueData, List<String> lstQueueValue, List<String> lstViewVlue, List<String> lstFilterValue, string sAssignWorkValue, string sSecFilter, List<string> lstItemAgeValue, List<String> lstTeamMembers) 
    {   
        
       
        
        String filterValue;
        Boolean bMyTeamInvChk = (lstTeamMembers != null && !lstTeamMembers.isEmpty()) ? true : false;
        
        if(lstFilterValue.contains(HumConstants_LWC.TASKLISTVIEW_ALL_HUM) || (lstFilterValue.contains(HumConstants_LWC.TASKLISTVIEW_OBJECTCASE_HUM) && lstFilterValue.contains(HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM)))
            filterValue = HumConstants_LWC.TASKLISTVIEW_ALL_HUM;
        else if(lstFilterValue.contains(HumConstants_LWC.TASKLISTVIEW_OBJECTCASE_HUM) && !(lstFilterValue.contains(HumConstants_LWC.TASKLISTVIEW_ALL_HUM) || lstFilterValue.contains(HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM)))
            filterValue = HumConstants_LWC.TASKLISTVIEW_OBJECTCASE_HUM;
        else
            filterValue = HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM;
        list<CaseTaskView_DTO_HUM_LWC> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM_LWC>();
        Map<String,List<String>> mapQueueWithViews = new Map<String,List<String>>();
        try
        {
            objcasequeryLimit = HUMSearchLimit__c.getInstance('CASEQUERYLIMIT');
            objTaskqueryLimit= HUMSearchLimit__c.getInstance('TASKQUERYLIMIT'); 
            sPharmacyUser =  HUMUtilityHelper_LWC.isPharmacyUser();  
            list<String> lstQueues = new list<String>();
            Id iCurrentUserId = UserInfo.getUserId();
            string sTaskQuery = TaskQuery;
            string sCaseQuery = caseQuery;
            
            
            if(HUMUtilityHelper_LWC.isMIRPilotUser()) {
                sCaseQuery = caseQueryRF;
                
            }
            
            String sUserName = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
             
            if((sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM) && (String.isNOTBlank(sAssignWorkValue) && sAssignWorkValue == HumConstants_LWC.TASKLISTVIEW_ALL_HUM))  
            {// control should go into this if
                sTaskQuery +=' AND (Task_Owner__c in :lstQueueData OR QueueOrUserId__c =:iCurrentUserId)';
                sCaseQuery += ' AND (Case_Owner__c in :lstQueueData OR OwnerId =:iCurrentUserId)'; 
                
                
            }
            
            else if((sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM) && (String.isNOTBlank(sAssignWorkValue) && sAssignWorkValue == HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM))
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
                // control should do inside here
                boolean bswitch_US2578074=HUMUtilityHelper_LWC.isCRMFunctionalityON('2578074');
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
                    if(filterValue == HumConstants_LWC.TASKLISTVIEW_ALL_HUM){
                        sCaseQuery += ' Limit '+ Integer.valueof(objTeamCaseLimit.Search_Limit__c);
                        sTaskQuery += ' Limit '+ Integer.valueof(objTeamTaskLimit.Search_Limit__c);  
                       
                    }else if (filterValue == HumConstants_LWC.CASE_CONSTANT_HUM){
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
                if(filterValue == HumConstants_LWC.TASKLISTVIEW_ALL_HUM)
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
                else if(filterValue == HumConstants_LWC.CASE_CONSTANT_HUM)
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
            HUMExceptionHelper.logErrors(ex, 'Hum_CaseTasks_LWC', 'fetchCaseTask');
        }          
        return lstCaseTaskDTOWrapper;
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
       HUMExceptionHelper.logErrors(ex,'Hum_CaseTasks_LWC','prepareCaseReceivedData');             
    }

   return mapCaseToReceiveddate;
} 
    
    
    /**
    * Method is used to assign values to CaseTask wrapper which will be shown on UI. The method is also used to assign the values when Pull Work Button is clicked.
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of processed wrapper records 
    * @param    list<Case> : List of Case that needs to be added to wrapper.
    * @param    list<Task> : List of task that needs to be added to wrapper.
    * @param    Boolean    : Boolean to identify if Pull Work is clicked to fetch the unassigned work
    */
    private static list<CaseTaskView_DTO_HUM_LWC> populateCaseTaskWrapper (list<Task> lstTaskResults, list<Case> lstCaseResults , boolean isPulled)
    {
        list<CaseTaskView_DTO_HUM_LWC> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM_LWC>();
        string sInteractngWith ='';
        String sInteractngWithId = '';
        String sInteractingAbout = '';
        //changing user value
        
        String sCurrentQueue =  [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c;
        decimal decCaseAge = 0; 
        list<ID> lstWhatID = new list<ID>();
        map<Id, Case> mapCase = new map<Id, Case>(); // Map to hold the Cases associated with Tasks. We need this map to populate Interacting details for Task Wrapper Records 
        String sUserName = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
        String sPharmacyUser =  HUMUtilityHelper_LWC.isPharmacyUser();
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
        set<string> setCaseIdscc = new set<string>();
        set<string> setTaskIdscc = new set<string>();
        for(Case oCase : lstCaseResults){
            setCaseIds.add(oCase.Id);
            setCaseIdscc.add(oCase.Id);
        }
        for(Task oTask : lstTaskResults){
            if(!string.isBlank(oTask.WhatId) && String.valueof(oTask.WhatId).startsWith(HumConstants_LWC.TASKLISTVIEW_SFDCCASEID_HUM))
                setCaseIds.add(oTask.WhatId);
            	setTaskIdscc.add(oTask.id);
        }
        
        map<Id,string> mapCaseToReceiveddate = prepareCaseReceivedData(setCaseIds);
        //Case Received Date logic ends
      
        //case comments logic for lwc -start
        //case comments
        map<Id,string> mapCaseTocomments = new map<id,string>();
        List<Case> oCaseRecComm = [Select Id, (Select Id, CommentBody,LastModifiedDate From CaseComments order by LastModifiedDate DESC Limit 1 )From Case Where id IN : setCaseIdscc];
        if(!oCaseRecComm.isEmpty()){
            for(case casecomm :oCaseRecComm){
                if(!casecomm.CaseComments.isEmpty() && String.isNotBlank(casecomm.CaseComments.get(0).CommentBody) ) {
                    String scasecom = 'Last Case comment created on ' + casecomm.CaseComments.get(0).LastModifiedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a') + '  ' + casecomm.CaseComments.get(0).CommentBody;
                         mapCaseTocomments.put(casecomm.id,scasecom);     
                } else {
                    mapCaseTocomments.put(casecomm.id,'No comment found');
                }			
            }
        }
        
        
        // task comments
        map<Id,string> mapTaskTocomments = new map<id,string>();
        list<Id> taskWithComments = new list<Id>();
        set<Id> taskWithoutComments = new set<Id>();
            
        List<Task_Field_History__c> lstFieldUpdates = [SELECT Id,SObjFieldValue__c,Created_At__c,SObjID__c  FROM Task_Field_History__c  WHERE SObjID__c  IN :setTaskIdscc AND SObjFieldName__c = :HumConstants_LWC.CASEMGMT_DESCRIPTION_HUM ORDER BY Created_At__c  desc limit 20 ];
        
       
        if(!lstFieldUpdates.isEmpty()){
            for(Task_Field_History__c taskcomm :lstFieldUpdates){
                
                if(!mapTaskTocomments.containsKey(taskcomm.SObjID__c)){
                    String ctaskcom = 'Last Task comment created on ' + taskcomm.Created_At__c.format('MM/dd/YYYY\' \'hh:mm\' \'a') +'  '+taskcomm.SObjFieldValue__c;
                    mapTaskTocomments.put(taskcomm.SObjID__c,ctaskcom);
                    taskWithComments.add(taskcomm.SObjID__c);
                }              
            }
        }        
        
        //case comments logic for lwc - end

      
        for(Case oCase : lstCaseResults) // Loop over the Cases to prepare the wrapper
        {
            if(isPulled && !String.valueOf(oCase.OwnerId).startsWith(HumConstants_LWC.TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM)) oCase = assignCaseValues(oCase);// If Pull work
           
            String sOpenWorkTasks = HumConstants.HUMWorkTask + ' '+ (oCase.Open_Work_Task_Count__c != null ? String.valueof(oCase.Open_Work_Task_Count__c) : String.valueof(0)) +' ( of '+ (oCase.Total_Work_Task_Count__c != null ? 
                                     String.valueof(oCase.Total_Work_Task_Count__c) : String.valueof(0)) +' )';
            String sOpenExtTasks = HumConstants.HUMExtensionLabel + ' ' + (oCase.Open_ExtensionTask_Count__c != null ? String.valueof(oCase.Open_ExtensionTask_Count__c) : String.valueof(0)) +' ( of '+ (oCase.Total_ExtensionTask_Count__c != null ? 
                                    String.valueof(oCase.Total_ExtensionTask_Count__c) : String.valueof(0)) +' )';
            
            String sTimeSpentOnCase = (null != oCase.CaseMilestones && !oCase.CaseMilestones.isEmpty()) ? HUMUtilityHelper_LWC.formatMinutes(oCase.CaseMilestones.get(0)) : ''; //MIR sla value.
            String receiveddate = (mapCaseToReceiveddate.containskey(oCase.Id))? mapCaseToReceiveddate.get(oCase.Id): '';
            Map<string, string> caseData = new Map<string,string>();
            bTransfer = false;
            //wrapper for HP and CC Supervisor Profiles
            //if(sUserName == System.Label.HUMAgencyCCSupervisor || sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM)
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
                //Comment - Bhumika
    			String caseComment = mapCaseTocomments.get(oCase.Id);

                lstCaseTaskDTOWrapper.add(new CaseTaskView_DTO_HUM_LWC(HumConstants_LWC.TASKLISTVIEW_OBJECTCASE_HUM , oCase.Id , oCase.CaseNumber + '--' + caseComment , 
                sOpenWorkTasks + '_' +sOpenExtTasks , oCase.Status , oCase.Priority , oCase.Interacting_With__c , oCase.Interacting_With__r.Name ,
                oCase.Account.Name ,(oCase.CTCI_List__r.Classification__c!= NULL ? oCase.CTCI_List__r.Classification__r.Name : ''), (oCase.CTCI_List__r.Intent__c != NULL ? oCase.CTCI_List__r.Intent__r.Name : ''), 
                oCase.Owner_Queue__c ,oCase.Work_Queue_View_Name__c , (oCase.Follow_Up_Due_Date__c != NULL ?String.valueof(oCase.Follow_Up_Due_Date__c.Month()) + '/'+ 
                String.valueof(oCase.Follow_Up_Due_Date__c.Day()) +'/'+String.valueof(oCase.Follow_Up_Due_Date__c.Year()) : '') ,oCase.Case_Age__c , oCase.Case_Age__c, 
                oCase.Re_Open_Case_Age__c,receiveddate, oCase.CreatedDate.format('MM/dd/yyyy hh:mma',UserInfo.getTimeZone().toString()) , oCase.Follow_Up_Due_Date__c <system.today() ,
                (oCase.OwnerId == UserInfo.getUserId()? HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM : HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM) ,
                String.valueof(oCase.Id +'_' + oCase.CreatedDate.getTime()), sTimeSpentOnCase,bTransfer,oCase.Policy_Member__r.Policy__c,
                oCase.RecordTypeId,oCase.AccountId,oCase.Guided_Process_Flag__c,caseData,oCase.General_Case__c,oCase.HO_Dental_Case__c,oCase.HO_Medical_Case__c));
            }            
        }
        
        for(Task oTask : lstTaskResults) // Loop over the Tasks to prepare the wrapper
        {
            if(isPulled && oTask.QueueOrUserId__c <> null && !oTask.QueueOrUserId__c.startsWith(HumConstants_LWC.TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM)) oTask = assignTaskValues(oTask ,sCurrentQueue);
            if(!string.isBlank(oTask.WhatId) && String.valueof(oTask.WhatId).startsWith(HumConstants_LWC.TASKLISTVIEW_SFDCCASEID_HUM) && mapCase.containskey(oTask.WhatId))
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

            //Comment - Bhumika            
		    String taskComment = mapTaskTocomments.containsKey(oTask.Id) ? mapTaskTocomments.get(oTask.Id) : 'No Comment' ;	

            lstCaseTaskDTOWrapper.add(new CaseTaskView_DTO_HUM_LWC(HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM , oTask.Id , oTask.Task_Number__c + '--' +  taskComment ,
            sTaskCount + '_'+'' ,oTask.Status , oTask.Priority , sInteractngWithId , sInteractngWith ,sInteractingAbout ,
            (String.isNotBlank(oTask.Classification__c) ? oTask.Classification__c : '') ,(String.isNotBlank(oTask.Intent__c)  ? oTask.Intent__c : ''), oTask.Owner_Queue__c , 
            oTask.Work_Queue_View_Name__c , (oTask.ActivityDate != NULL ? String.valueof(oTask.ActivityDate.Month()) + '/'+ String.valueof(oTask.ActivityDate.Day()) +'/'+
            String.valueof(oTask.ActivityDate.Year()) : ''),oTask.Age_Since_Created__c , decCaseAge, (oTask.Date_Time_Reopned__c != NULL ?  oTask.Re_Open_Task_Age__c : 0),receiveddate,
            oTAsk.CreatedDate.format('MM/dd/yyyy hh:mma',UserInfo.getTimeZone().toString()) , oTask.ActivityDate < system.today() ,(oTask.QueueOrUserId__c == UserInfo.getUserId()? 
            HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM : HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM), String.valueof(oTask.Id +'_' + oTAsk.CreatedDate.getTime()),'',false,'','',(String)mapCase.get(oTask.WhatId).Account.Id,false,caseData,false,false,false));
            
            
        }
        return lstCaseTaskDTOWrapper;
    }

    @AuraEnabled
    public static list<CaseTaskView_DTO_HUM_LWC> updateOwnerOfAssignedRecords(List<string> lstUnassignedRecords,list<string> queueData, List<string> queueValue, List<String> allQueueViewList, List<string> viewVlue,List<string> filterValue,List<string> assignWorkValue, List<string> secFilter,  List<string> lstItemAgeValue) 
    {
        
        
        
    //    try
    //     {   
            String sCurrentQueue =  [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c;
            list<Task> taskList = new list<Task>();
            list<Case> caseList = new list<Case>();
            for(String s : lstUnassignedRecords)
            {
                if(string.valueOf(id.valueOf(s).getSObjectType()).equalsIgnoreCase(HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM))
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
            
        //}
        // catch(Exception ex)
        // {
        //     HUMExceptionHelper.logErrors(ex,'Hum_CaseTasks_LWC', 'updateOwnerOfAssignedRecords');
        // }
        return applyFilter( true, queueData, queueValue, allQueueViewList, viewVlue,filterValue,assignWorkValue,secFilter, lstItemAgeValue, null);
    
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
    * Method is called on parse the fetched records. If we have more than 1 record (When fetching both Case and Task) then we have to Pull Task before Case.
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of one pulled wrapper records 
    * @param    list<CaseTaskView_DTO_HUM_LWC> : List of all the pulled wrapper records
    */
    private static list<CaseTaskView_DTO_HUM_LWC>  parsePulledItems( list<CaseTaskView_DTO_HUM_LWC>  lstCaseTaskDTOWrapper)
    {
        if(!lstCaseTaskDTOWrapper.isEmpty() && lstCaseTaskDTOWrapper.size() > 1)
        {
            if(lstCaseTaskDTOWrapper.get(0).sFilterType  == HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM)
            {
                lstCaseTaskDTOWrapper.remove(1);
            }
            else if(lstCaseTaskDTOWrapper.get(1).sFilterType  == HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM)
            {
                lstCaseTaskDTOWrapper.remove(0);    
            }
        }
        return lstCaseTaskDTOWrapper;
    }
    

 /**
    * Method is used to pull work on basis of Due Date .  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM_LWC> filterByDueDate(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter ,list<String> lstQueues)
    {
        list<CaseTaskView_DTO_HUM_LWC> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM_LWC>();
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
                    else if(convertStringtoDate(lstCaseTaskDTOWrapper.get(0).dDueDate) < convertStringtoDate(lstCaseTaskDTOWrapper.get(1).dDueDate))
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
        list<String> strList = sDate.split('/');
        if(!strList.isEmpty() && strList.size() == 3)
        {
            return Date.valueof(strList[2]+'-'+strList[0]+'-'+strList[1]);
        }
        return null;
    }
    
    /**
    * Method Is called from UI when Pull work Button Is clicked. 
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : One pulled Item which mathces the input criteria 
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    */
    @AuraEnabled 
    public static list<CaseTaskView_DTO_HUM_LWC> pullWorkItems(List<String> lstQueue, List<String> lstView, String sFilterBy,List<String> sFilterTypeCheck )
    { 
        
        
        list<CaseTaskView_DTO_HUM_LWC> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM_LWC>();
        Map<String,List<String>> mapQueueWithViews = new Map<String,List<String>>();
        try
        {   
            list<String> lstQueues = new list<String>();
            for(Group oGroup : [SELECT Name FROM Group WHERE Type =: HumConstants_LWC.TASKLISTVIEW_QUEUE_HUM]) 
            {   
                lstQueues.add(oGroup.Name);
            }
            //lstQueues.add('Shelby Padilla');
            string sTaskQuery = TaskQuery +' AND Task_Owner__c in :lstQueues '; // Sample Task Query
            string sCaseQuery = caseQuery + ' AND Case_Owner__c in :lstQueues '; // Sample Case Query
            
            if(HUMUtilityHelper_LWC.isMIRPilotUser()) {
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
            if(sFilterBy == HumConstants_LWC.TASKLISTVIEW_DUESPACEDATE_HUM) lstCaseTaskDTOWrapper = filterByDueDate(sTaskQuery , sCaseQuery , lstQueue , lstView ,sFilterBy , lstQueues);
            
            if(sFilterBy == HumConstants_LWC.TASKLISTVIEW_AGETASKSONLY_HUM || sFilterBy == HumConstants_LWC.TASKLISTVIEW_AGE_HUM 
            || sFilterBy == HumConstants_LWC.TASKLISTVIEW_AGECASESONLY_HUM)
            {
                lstCaseTaskDTOWrapper = filterByAge(sTaskQuery , sCaseQuery , lstQueue , lstView , sFilterBy , lstQueues);
               
            }
            
            if(sFilterBy == HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM || sFilterBy == HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM 
                || sFilterBy == HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM 
                || sFilterBy ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM || sFilterBy ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYNORMALONLY_HUM)
            {
                lstCaseTaskDTOWrapper = filterByPriority(sTaskQuery , sCaseQuery , lstQueue , lstView , sFilterBy , lstQueues);
                
            }
            
            if(sFilterBy == HumConstants_LWC.TASKLISTVIEW_AGETASKSONCASE_HUM) 
            {
                lstCaseTaskDTOWrapper = filterByTaskByOldestCase(sTaskQuery , sCaseQuery , lstQueue , lstView , sFilterBy , lstQueues);
                
            }
    
            if(!lstCaseTaskDTOWrapper.isEmpty()) // If an item is pulled then update the owner and send it on UI
            {
                
                lstCaseTaskDTOWrapper = updateCaseTaskOwner(lstCaseTaskDTOWrapper[0] , lstQueue , lstView , sFilterBy,sFilterTypeCheck);
                
                if(!lstCaseTaskDTOWrapper.isEmpty())return new list<CaseTaskView_DTO_HUM_LWC>{lstCaseTaskDTOWrapper[0]};
            } 
            
               
        }
        catch(Exception ex)
        {
               HUMExceptionHelper.logErrors(ex, 'Hum_CaseTasks_LWC', 'pullWorkItems');
        }
        
        
        return lstCaseTaskDTOWrapper;        
    }
    
    /**
    * Method is used to pull work on basis of Age  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM_LWC> filterByAge(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter, list<String> lstQueues)
    {
        list<CaseTaskView_DTO_HUM_LWC> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM_LWC>();
        sTaskQuery +=' order by Age_Since_Created__c DESC NULLS LAST , Createddate ASC NULLS LAST LIMIT 1'; // Pull 1 Task 
        sCaseQuery += ' order by Case_Age__c DESC NULLS LAST ,  Reopened__c DESC NULLS LAST  , Createddate ASC NULLS LAST LIMIT 1'; // Pull 1 Case
        

        if(sFilter == HumConstants_LWC.TASKLISTVIEW_AGE_HUM) //Pull both Case and Task.
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
                else if((lstCaseTaskDTOWrapper.get(0)).decCaseAge < (lstCaseTaskDTOWrapper.get(1)).decCaseAge)
                {
                    lstCaseTaskDTOWrapper.remove(0);
                }
            }
        }

        if(sFilter == HumConstants_LWC.TASKLISTVIEW_AGETASKSONLY_HUM ) lstCaseTaskDTOWrapper = populateCaseTaskWrapper(Database.Query(sTaskQuery) ,  new list<Case>{}  , true);
        if(sFilter == HumConstants_LWC.TASKLISTVIEW_AGECASESONLY_HUM) lstCaseTaskDTOWrapper = populateCaseTaskWrapper(new list<Task>{} ,  Database.Query(sCaseQuery)  , true);
                
        return lstCaseTaskDTOWrapper;
    }
    
    /**
    * Method is called to parse the Wrapper on basis of Reopen Item Age
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of one record  on the basis of reopen item age
    * @param    list<CaseTaskView_DTO_HUM_LWC> : List of all the pulled wrapper records
    */
    private static list<CaseTaskView_DTO_HUM_LWC>  parseReopenItems( list<CaseTaskView_DTO_HUM_LWC>  lstCaseTaskDTOWrapper)
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
    * Method is used to pull work on basis of Priority . Priority can be set as Critical High and Normal  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM_LWC> filterByPriority(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter, list<String> lstQueues)
    {
        list<CaseTaskView_DTO_HUM_LWC> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM_LWC>();
        String sTsQuery =''; // Temp Query String which will change on the basis of Priority Seleceted from UI
        String sCsQuery = '';  // Temp Query String which will change on the basis of Priority Seleceted from UI
        boolean bCritical = false; // Boolean to identify if we pulled Critical record
        boolean bHigh = false; // Boolean to identify if we pulled High record
        list<Task> lstTask = new list<Task>(); 
        list<Case> lstCase = new list<Case>();
        String strNormal ='Normal';
        String strCritical ='Critical';
        String strHigh ='High';
        
        
        // Filter for Critical Cases
        if(sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM || sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM 
        || sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM || sFilter ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM ) 
        {
            sTsQuery = sTaskQuery + ' AND Priority=\''+ String.escapeSingleQuotes(strCritical) +'\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Critical Task
            sCsQuery = sCaseQuery + ' AND Priority=\''+ String.escapeSingleQuotes(strCritical) +'\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Critical Case
            
            if(sFilter != HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM ) lstTask = Database.Query(sTsQuery);
            if(sFilter != HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM) lstCase = Database.Query(sCsQuery);
            bCritical = ((sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM  || sFilter ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM)? 
                        (lstTask.size() > 0 || lstCase.size() > 0 ) : sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM ? 
                        lstCase.size() > 0 : sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM ? lstTask.size() > 0 : false);
        }
        
        // Filter for High Cases
        if(!bCritical && (sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM || sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM || sFilter ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM || sFilter ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM) ) 
        {
            sTsQuery = sTaskQuery + ' AND Priority=\''+ String.escapeSingleQuotes(strHigh) +'\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 High Task
            sCsQuery = sCaseQuery + ' AND Priority=\''+ String.escapeSingleQuotes(strHigh) +'\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 High Case
            if(sFilter != HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM ) lstTask = Database.Query(sTsQuery);
            if(sFilter != HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM) lstCase = Database.Query(sCsQuery);
            bHigh = ((sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM  || sFilter ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM)? 
                    (lstTask.size() > 0 || lstCase.size() > 0 ) : sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM ? 
                    lstCase.size() > 0 : sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM ? lstTask.size() > 0 : false); 
        }
        
        // Filter for Normal Cases
        if(( !bCritical && !bHigh && (sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM || sFilter == HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM || sFilter ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM)) ||  sFilter ==  HumConstants_LWC.TASKLISTVIEW_PRIORITYNORMALONLY_HUM ) 
        {
            sTsQuery = sTaskQuery + ' AND Priority=\''+ String.escapeSingleQuotes(strNormal) +'\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Normal Task
            sCsQuery = sCaseQuery + ' AND Priority=\''+ String.escapeSingleQuotes(strNormal) +'\' ORDER BY CreatedDate ASC NULLS LAST Limit 1'; // Pull 1 Normal Case
            if(sFilter != HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM ) lstTask = Database.Query(sTsQuery);
            if(sFilter != HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM) lstCase = Database.Query(sCsQuery);
        }
        lstCaseTaskDTOWrapper = populateCaseTaskWrapper(lstTask ,  lstCase  , true);
        lstCaseTaskDTOWrapper= parsePulledItems(lstCaseTaskDTOWrapper);
        return lstCaseTaskDTOWrapper;
    }
       
  /**
    * Method is used to pull Task on the oldest Case.  
    * <p>
    * @return   list<CaseTaskView_DTO_HUM_LWC> : List of one pulled wrapper records 
    * @param    String : static Task Query
    * @param    String : static Case Query
    * @param    String : Selected Queue to Pull
    * @param    String : Selected View to Pull
    * @param    String : Selected Filter to Pull
    * @param    list<String> : List of available queues
    */
    private static list<CaseTaskView_DTO_HUM_LWC> filterByTaskByOldestCase(String sTaskQuery , String sCaseQuery , List<String> lstQueue , List<String> lstView ,String sFilter ,list<String> lstQueues)
    {
        objcasequeryLimit = HUMSearchLimit__c.getInstance('CASEQUERYLIMIT');
        list<CaseTaskView_DTO_HUM_LWC> lstCaseTaskDTOWrapper = new list<CaseTaskView_DTO_HUM_LWC>();
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
    * Perform the DML on the pulled Workitem (Case/Task) to associate it to the logged in User
    * <p>
    * @return   NA 
    * @param    CaseTaskView_DTO_HUM_LWC : The record which was pulled 
    */
    public static List<CaseTaskView_DTO_HUM_LWC> updateCaseTaskOwner(CaseTaskView_DTO_HUM_LWC pulledCaseTask , List<String> lstQueue, List<String> lstView, String sFilterBy,List<String> sFilterTypeCheck)
    {
        try
        {
            if(pulledCaseTask.sFilterType == HumConstants_LWC.TASKLISTVIEW_OBJECTTASK_HUM && ( sFilterTypeCheck[0] =='Tasks' || sFilterTypeCheck[0] =='All' ) ) // Update Task
            {
                Task objTask = [Select QueueOrUserId__c from Task where Id = :pulledCaseTask.sCaseTaskId limit 1 for update];
                if (!String.isBlank(objTask.QueueOrUserId__c) && objTask.QueueOrUserId__c.startswith(HumConstants_LWC.CASEMGMT_GROUPID_INITIAL_HUM))
                {
                    objTask = assignTaskValues(objTask ,  [select Current_Queue__c from User where Id =: UserInfo.getUserId()].Current_Queue__c);           
                    Database.update(objTask,true);
                    return new List<CaseTaskView_DTO_HUM_LWC> {pulledCaseTask};
                }
                else
                {
                    if(Limits.getQueries() <Limits.getLimitQueries())
                        Return pullWorkItems(lstQueue, lstView, sFilterBy, sFilterTypeCheck);
                    else
                        return new List<CaseTaskView_DTO_HUM_LWC> {};
                }
            }
            else If (sFilterTypeCheck[0] =='Cases' || sFilterTypeCheck[0] =='All' ) // Update Case
            {
                Case objCase = [Select OwnerId ,Owner_Queue__c, lastmodifieddate from Case where Id = :pulledCaseTask.sCaseTaskId limit 1 for update];
                
                if (!String.isBlank(objCase.OwnerId) && String.valueof(objCase.OwnerId).startswith(HumConstants_LWC.CASEMGMT_GROUPID_INITIAL_HUM))
                {
                   
                    objCase = assignCaseValues(objCase);
                    Database.update(objCase,true); 
                    
                    return new List<CaseTaskView_DTO_HUM_LWC> {pulledCaseTask};   
                     
                }
                else
                {
                   
                    if(Limits.getQueries() < Limits.getLimitQueries()) 
                                            return pullWorkItems(lstQueue, lstView, sFilterBy, sFilterTypeCheck);
                    else
                        return new List<CaseTaskView_DTO_HUM_LWC> {};

                }
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Hum_CaseTasks_LWC','updateCaseTaskOwner');
        }
        return new List<CaseTaskView_DTO_HUM_LWC> {};
    }
     /*
     * Method Name : getCurrentUserCurrentQueue
     * Description : retrieves the current user current queue 
     * Return type : String
     * Paramater   : None
     */
    public static String getCurrentUserCurrentQueue()
    {       
        String currentQueue = [Select Current_Queue__c From User Where Id =:UserInfo.getUserId()][0].Current_Queue__c; 
        if(currentQueue != null){
        return currentQueue;
        }
        return null;
    }
    public static List<String> sortedViewData()
    {
        try{
        List<String> tmplist = new List<String>();
        
        list<Queue_View__c> lstQueues = [SELECT Queue_View_Name__c, Queue_Name__c FROM Queue_View__c where Queue_Name__c in: lstViewCategorydd1 AND Group_Name__c IN : Hum_CaseTasks_LWC.GetQueueList() AND IsActive__c = TRUE AND Queue_View_Name__c !=: HumConstants_LWC.HUM_ALLOTHERVIEW order by Queue_Name__c,Queue_View_Name__c asc];
        
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
            lsttmpSort.add(HumConstants_LWC.TASKLISTVIEW_OTHER_HUM +' ('+ sQueue +')');
            for(String sViewAndQueue : tmplist)
            {
                
                if(sViewAndQueue.containsIgnoreCase(sQueue))    
                    lsttmpSort.add(sViewAndQueue);
                lsttmpSort.sort();
                
            }
             lstSort.addAll(lsttmpSort); 
                  
                
        }
        
        return lstSort; 
    }
    catch(Exception e) {
        HUMExceptionHelper.logErrors(e,'Hum_CaseTasks_LWC','sortedViewData');
        return null;
    }
     
   }
    
    public static list<String> getCategoriesdd2()
    {
        list<String> lstcat = new list<String>();
        
        if(userCurrentProfile == HumConstants_LWC.Customer_Care_Supervisor || 
        (userCurrentProfile == HumConstants_LWC.PHARMACYSPECIALIST_PROFILE_HUM && sPharmacyUser == HumConstants_LWC.PHARMACYSUPERVISOR_USER_HUM)
        ) {
            lstcat.add(System.Label.CaseTaskAllOtherQV);
        }
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
    /**
    * Method to populate Item Age Dropdown
    * <p>
    * @return : list<String>
    * @param : NA
    */
    public static list<String> getItemAge()
    {
        list<String> lstItemAge = new list<String>();
        lstItemAge.add('Greater Than 10 days');
        lstItemAge.add('6 to 10 days');
        lstItemAge.add('Upto 5 days');
       
        return lstItemAge;
    }
    
    /**
    * Method to populate FilterBy Dropdown
    * <p>
    * @return : list<String>
    * @param : NA
    */
    public static list<String> getSelectedView()
    {
        list<String> lstSelectedView = new list<String>();
        lstSelectedView.add(HumConstants_LWC.TASKLISTVIEW_CASES_HUM);
        lstSelectedView.add(HumConstants_LWC.TASKLISTVIEW_TASKS_HUM);
        return lstSelectedView;
    }
    
    
    /**
     * Method used to populate Sort by Dropdown
     * <p>
     * @return   lstSortBy
     * @param    NA
     */
    public static list<String> getPullWorkFilterByOptions()
    {
        list<String> lstSortBy = new list<String>();
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_DUESPACEDATE_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_AGE_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_AGECASESONLY_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_AGETASKSONLY_HUM); 
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_AGETASKSONCASE_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_PRIORITYONLY_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_PRIORITYCASEONLY_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_PRIORITYTSKONLY_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM);
        lstSortBy.add(HumConstants_LWC.TASKLISTVIEW_PRIORITYNORMALONLY_HUM);
        return lstSortBy;
    }
    @AuraEnabled(cacheable = true)
    public static String getCurrentUserName()
    { 
        return UserInfo.getName();
    }
  /**
    * Method to get the queues based on the Public Groups in which the logged in user is part of. 
    * <p>
    * Return Type   :   list<SelectOption>
    * Parameters    :   NA
    */
    @AuraEnabled(cacheable = true)
    public static filterWrapper getInitialLoad()
    {   
        Hum_CaseTasks_LWC.getHum_CaseTasks_LWC();
        Hum_CaseTasks_LWC.initializeWorkQueue();
        
        filterWrapper filWrp = new filterWrapper();
        filWrp.currentProfile = Hum_CaseTasks_LWC.getCurrentUserProfileName();
        filWrp.workQueueList = Hum_CaseTasks_LWC.getCategoriesdd1(); 
        filWrp.currentQueue =  Hum_CaseTasks_LWC.getCurrentUserCurrentQueue();
        filWrp.queueViewList = Hum_CaseTasks_LWC.getCategoriesdd2();
        filWrp.myTeamMembers = Hum_CaseTasks_LWC.getMyTeamUsers();
        filWrp.selectedView = Hum_CaseTasks_LWC.getSelectedView();
        filWrp.pullWorkFilterByOptions =  Hum_CaseTasks_LWC.getPullWorkFilterByOptions();
        filWrp.currentUserName = Hum_CaseTasks_LWC.getCurrentUserName();
        filWrp.bswitch_JS2578074 = bswitch_US2578074;
        filWrp.sPharmacyUser = sPharmacyUser;
       
        if((Hum_CaseTasks_LWC.getCurrentUserProfileName() == 'Customer Care Supervisor') || (Hum_CaseTasks_LWC.getCurrentUserProfileName() == 'Humana Pharmacy Specialist' && HUMUtilityHelper_LWC.isPharmacyUser() == HumConstants_LWC.PHARMACYSUPERVISOR_USER_HUM)){
            filWrp.SelectedAssign = Hum_CaseTasks_LWC.getSelectedAssign();
            filWrp.securityView = Hum_CaseTasks_LWC.getSecurityView();
            filWrp.itemAge = Hum_CaseTasks_LWC.getItemAge(); 
        }
        
        List<String> initselectedView = new List<String>{'All'};
        initselectedView.addAll(filWrp.selectedView);

        List<String> selectedQ =  new List<String>();
        for(String strQ : filWrp.queueViewList){
            
            if(!strQ.contains(System.Label.CaseTaskAllOtherQV) && strQ.contains('('+filWrp.currentQueue+')')){
                selectedQ.add(strQ);
            }
            
        }
        
        if(userCurrentProfile == HumConstants_LWC.Customer_Care_Supervisor || 
        (userCurrentProfile == HumConstants_LWC.PHARMACYSPECIALIST_PROFILE_HUM && sPharmacyUser == HumConstants_LWC.PHARMACYSUPERVISOR_USER_HUM))
        {
            selectedQ.add('All (Al)');
            //load ctad logic on page load
           
            selectedQ.add(Hum_CaseTasks_LWC.getCTADQuery(new List<String>{filWrp.currentQueue}, selectedQ));
        }
        else{
            selectedQ.add('All (All)');
        }
        
         
        filWrp.initialFetchCaseTasks =  Hum_CaseTasks_LWC.fetchCaseTask(true, filWrp.workQueueList, new List<String>{filWrp.currentQueue}, selectedQ, initselectedView,
                                        'All','All', null, null );
        return filWrp;
        

    }
    /**
    * Method to get the String of CTAD based on queue and view
    * <p>
    * Return Type   :   String
    * Parameters    :   lstQueueValue1,lstAllView1
    */
    public static String getCTADQuery(List<String> lstQueueValue1, List<String> lstAllView1) {

        List<String> lstQueueValue = lstQueueValue1;
        List<String> lstAllView = lstAllView1;
        String keyStr = '';
        integer temp;
        String lstTemp = '';
        String strWQ = '';
        String sQuery ='';
        List<String> lstQV = new List<String>();
        map<String, List<String>> keyObj = new map<String, List<String>>();
        
        for(integer i=0;i<lstAllView.size();i++){
            
            temp = lstAllView[i].lastIndexOf(' (');
            if(temp != -1){
                lstTemp = lstAllView[i].substring(0, temp);
                strWQ = lstAllView[i].substring(temp+2, lstAllView[i].length() - 1);
                if(keyStr == ''){
                    lstQV.add(lstTemp);
                    keyStr = strWQ;
                }
                else if(keyStr == strWQ) {
                    lstQV.add(lstTemp);
                }
                else if(keyStr != strWQ) {

                    keyObj.put(keyStr, lstQV);
                    keyStr = strWQ;
                    lstQV =  new List<String>();
                    lstQV.add(lstTemp);
                }
            }
        }

        if(lstQV.size() > 0) {
	    keyObj.put(keyStr, lstQV);
           
            lstQV = null;
            keyStr = null;
        }
        if(keyObj !=null) {
            sQuery ='CTAD-'+JSON.serialize(keyObj);
        }
       
        return sQuery;
    }
    @AuraEnabled
    public static List<CaseTaskView_DTO_HUM_LWC> applyFilter( Boolean bPerformFilter, List<String> allWorkQueueList, List<String> lstWorkQ, 
                                            List<String> allQueueViewList, List<String> lstView,List<String> lstFilterBy,List<String> lstWorkItems,
                                            List<String> lstSecurityGrp, List<String> lstItemAge, List<String> lstTeamMembers)
											
    { 
       
	    List<CaseTaskView_DTO_HUM_LWC> lstApplyFilterWrp = new List<CaseTaskView_DTO_HUM_LWC>();
        //prepare actual data for methods
        prepareParamDataWrap dataWrap =  getStructuredDateForParam(allWorkQueueList, lstWorkQ, allQueueViewList, lstView, lstFilterBy,lstWorkItems,
        lstSecurityGrp,lstItemAge,lstTeamMembers);
        
       
        //call fetch method
        
        lstApplyFilterWrp = Hum_CaseTasks_LWC.fetchCaseTask(bPerformFilter,dataWrap.allWorkQueueList ,dataWrap.lstWorkQ, dataWrap.lstView, dataWrap.lstFilterByName, dataWrap.WorkItemsName,  dataWrap.lstSecurityGrpName, dataWrap.lstItemAgeName, dataWrap.lstTeamMemberName) ;
        
        return lstApplyFilterWrp;
    }

    @AuraEnabled(cacheable = true)
    public static list<String> getCategoriesdd1()
    {
        Set<String> setCatTemp = new Set<String>();
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
                lstCatTemp = HUMUtilityHelper_LWC.doSort(setCatTemp);
            }
           
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e,'Hum_CaseTasks_LWC','getCategoriesdd1');
        }
        return lstCatTemp;
    } 

    public static List<String> returnSelectedItemAge(List<String> lstOfSelectedItemAge) {
         String scaseItemAgeQuery ='';
         List<String> lstItemAgeQuery = new List<String>();
         String sTaskItemAgeQuery ='';

        if(lstOfSelectedItemAge!=null && lstOfSelectedItemAge.size() > 0 && lstOfSelectedItemAge.size() != 3){
            for(Integer i=0; i<lstOfSelectedItemAge.size(); i++) {
                if(lstOfSelectedItemAge[i]=='Greater Than 10 days'){
                    scaseItemAgeQuery +='Case_Age__c >'+iGtrRange;
                    sTaskItemAgeQuery +='Task_Age__c >'+iGtrRange;
                } else if(lstOfSelectedItemAge[i]=='6 to 10 days'){
                    scaseItemAgeQuery+=(scaseItemAgeQuery=='') ? ('( Case_Age__c >='+iStartRange+' AND Case_Age__c <='+iEndRange+' )' ) : (' OR ( Case_Age__c >='+iStartRange+' AND Case_Age__c <='+iEndRange+' )' );
                    sTaskItemAgeQuery+=(sTaskItemAgeQuery=='') ? ('( Task_Age__c >='+iStartRange+ ' AND Task_Age__c <='+iEndRange+' )' ) : (' OR ( Task_Age__c >='+iStartRange+' AND Task_Age__c <='+iEndRange+' )' );
                } else if(lstOfSelectedItemAge[i]=='Upto 5 days'){
                    scaseItemAgeQuery+=(scaseItemAgeQuery=='') ? ( 'Case_Age__c <='+iUptoRange ) : ( ' OR Case_Age__c <='+iUptoRange );
                    sTaskItemAgeQuery+=(sTaskItemAgeQuery=='') ? ( 'Task_Age__c <='+iUptoRange ) : ( ' OR Task_Age__c <='+iUptoRange );
                }  
            }
           lstItemAgeQuery.add(' AND ('+scaseItemAgeQuery +')'); 
           lstItemAgeQuery.add(' AND ('+sTaskItemAgeQuery +')');
            }
            
            return lstItemAgeQuery;

    }
 
    
public static String changeInSecurity(List<String> secItemslst) {
       
         String secItems ='';
		 boolean bswitch_US2578074=HUMUtilityHelper_LWC.isCRMFunctionalityON('2578074');
         if(secItemslst!=null && secItemslst.size() > 0 ){ 
                if(bswitch_US2578074){
                if(secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other') && !secItemslst.contains('Contract Protected Data'))
                secItems = 'GeneralDental';
                if(secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other' )&& !secItemslst.contains('Contract Protected Data'))
                secItems = 'GeneralMedical';
                if(!secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other') && !secItemslst.contains('Contract Protected Data'))
                secItems = 'DentalMedical';
                if(secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other') && !secItemslst.contains('Contract Protected Data'))
                secItems = 'General';
                if(!secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other') && !secItemslst.contains('Contract Protected Data'))
                secItems = 'Dental';
                if(!secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other') && !secItemslst.contains('Contract Protected Data'))
                secItems = 'Medical';
                if(secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other') && !secItemslst.contains('Contract Protected Data'))
                secItems = 'GeneralDentalMedical';
                if(secItemslst.contains('General') && secItemslst.contains('Home Office Dental')&& secItemslst.contains('Home Office All Other') &&secItemslst.contains('Contract Protected Data'))
                secItems = 'All';
                if(!secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other')
				&& secItemslst.contains('Contract Protected Data'))
                secItems = 'Contract Protected Data';
                if(secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other' ) && secItemslst.contains('Contract Protected Data'))
                secItems = 'ContractGeneral';
                if(!secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other') && secItemslst.contains('Contract Protected Data'))
                secItems = 'ContractDental';
                if(!secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other') && secItemslst.contains('Contract Protected Data'))
                secItems = 'ContractMedical';
                if(secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other') && secItemslst.contains('Contract Protected Data'))
                secItems = 'ContractGeneralDental';
                if(secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other') && secItemslst.contains('Contract Protected Data'))
                secItems = 'ContractGeneralMedical';
                if(!secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other') && secItemslst.contains('Contract Protected Data'))
                secItems = 'ContractDentalMedical';
                }else{
                if(secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other'))
                secItems = 'GeneralDental';
                if(secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other'))
                secItems = 'GeneralMedical';
                if(!secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other'))
                secItems = 'DentalMedical';
                if(secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other'))
                secItems = 'General';
                if(!secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && !secItemslst.contains('Home Office All Other'))
                secItems = 'Dental';
                if(!secItemslst.contains('General') && !secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other'))
                secItems = 'Medical';
                if(secItemslst.contains('General') && secItemslst.contains('Home Office Dental') && secItemslst.contains('Home Office All Other'))
                secItems = 'All';
                }
			
		}
	
	return secItems;
}
	
    public static String returnSelectedWorkItem(List<String> lstOfSelectedWorkItem) {
        String filterValue ='';
        
        if(lstOfSelectedWorkItem.contains(HumConstants_LWC.TASKLISTVIEW_ALL_HUM))
        filterValue=HumConstants_LWC.TASKLISTVIEW_ALL_HUM;
        else if(lstOfSelectedWorkItem.contains(HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM) && !lstOfSelectedWorkItem.contains(HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM))
        filterValue=HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM;
        else if(lstOfSelectedWorkItem.contains(HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM) && !lstOfSelectedWorkItem.contains(HumConstants_LWC.TASKLISTVIEW_ASSIGNED_HUM))
        filterValue=HumConstants_LWC.TASKLISTVIEW_UNASSIGNED_HUM;
        
        return filterValue;
        
        }
    
  
    /*
     * Method Name : checkUserQueue
     * Description : check current queue of user
     * Return type : Boolean
     * Parameter   : NA
    */
    @AuraEnabled
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
    @AuraEnabled
    public static List<string> checkQAA(List<string> lstCaseDTO)
    {
        List<Case_Action_Association__c> lstCaseAction = [SELECT Id, Case__c,Case__r.CaseNUmber, Case_Action__r.name FROM Case_Action_Association__c where Case_Action__r.Name = :HumConstants_LWC.CASEACTIONLAUNCHCONTROLLER_Intent_HUM and Case__c in :lstCaseDTO];
        set<string> casesSet = new set<string>();
        if(lstCaseAction != Null && !lstCaseAction.isEmpty())
        {
            for(Case_Action_Association__c caseAction: lstCaseAction)
            {
                for(String s: lstCaseDTO)
                {
                    if(caseAction.Case__c == s )
                    {
                        casesSet.add(caseAction.Case__c+'+'+caseAction.Case__r.CaseNUmber);
                        break;
                    }
                }
            }
        }
        return new List<String>(casesSet);
    }
    
    @AuraEnabled
    public static list<CaseTaskView_DTO_HUM_LWC> closeCase(List<String> lstOfUnassignedRecords,List<String> allWorkQueueList, List<String> lstWorkQ, 
                                                         List<String> lstView,List<String> lstFilterBy,List<String> lstWorkItems,List<String> lstSecurityGrp, 
														 string scasecom, string scasestatus, String mapCaseDTO1,
														 List<String> lstItemAge,List<String> lstTeamMembers)
    {
        

        Map<string, CaseTaskView_DTO_HUM_LWC> mapCaseDTO = (Map<string, CaseTaskView_DTO_HUM_LWC>) json.deserialize(mapCaseDTO1, Map<String, CaseTaskView_DTO_HUM_LWC>.class);
        
        
        
        List<CaseTaskView_DTO_HUM_LWC> lstApplyFilterWrp = new List<CaseTaskView_DTO_HUM_LWC>();
        //prepare actual data for methods
        prepareParamDataWrap dataWrap = getStructuredDateForParam(allWorkQueueList, lstWorkQ,new list<string>(), 
                                        lstView, lstFilterBy,lstWorkItems,lstSecurityGrp, lstItemAge,lstTeamMembers); 

                                                        
        
        list<CaseTaskView_DTO_HUM_LWC> lstupdateStatusOfAssignedRecords = updateStatusOfAssignedRecords(lstOfUnassignedRecords, dataWrap.allWorkQueueList, dataWrap.lstWorkQ, dataWrap.lstView, dataWrap.lstFilterByName, 
																										dataWrap.WorkItemsName, dataWrap.lstSecurityGrpName, scasecom, scasestatus, mapCaseDTO, dataWrap.lstItemAgeName, dataWrap.lstTeamMemberName);
                                                                                              
        	return lstupdateStatusOfAssignedRecords;        
    }
    
    /**
     * Method used to update Status
     * <p>
     * @return   list<CaseTaskView_DTO_HUM_LWC> : List of Wrapper Records returned after accepting the WorkItems
     * @param    map<string,boolean> : Map of records needs to be Pulled
     */
    
    public static list<CaseTaskView_DTO_HUM_LWC> updateStatusOfAssignedRecords(list<string> lstOfUnassignedRecords,list<string> queueData, List<string> queueValue,List<string> viewVlue,List<string> filterValue,string assignWorkValue, string secFilter, string scasecom,string scasestatus, map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO, List<string> lstItemAgeValue,List<String> lstTeamMembers)
    {
         
       //try
        //{   
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
            Set<String> wqueues = new Set<String>{HumConstants_LWC.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,HumConstants_LWC.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,HumConstants_LWC.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,HumConstants_LWC.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
            Set<String> chatQueues = new Set<String>(HumConstants_LWC.WEB_CHAT_WQUEUES);
            Set<String> processQueues = new Set<String>{HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER,HumConstants_LWC.WORK_QUEUE_LV_Screening,HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research,HumConstants_LWC.GOODCAUSEQUEUE};
            Set<String> processRecTypes = new Set<String>{HumConstants_LWC.CHANGECASEOWNER_MEMBERCASE_HUM, HumConstants_LWC.MEDICARE_CASE, HumConstants_LWC.HP_UNKNOWNCASE_HUM, HumConstants_LWC.CHANGECASEOWNER_UNKNOWNCASE_HUM};
            List<Task> lstOpenTasks1 = new List<Task>();
            lstOpenTasks1 = [Select Whatid from Task where Whatid in :lstOfUnassignedRecords AND Type in (: HumConstants_LWC.CASEEDIT_WORK_TASK_HUM, :HumConstants_LWC.HUMExtensionTask) and Status != : HumConstants_LWC.CASEEDIT_CLOSED_HUM order by createddate asc]; 
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
            for(string str : lstOfUnassignedRecords)
            {
                CaseTaskView_DTO_HUM_LWC oCaseDTO =  mapCaseDTO.get(str);
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
            for(string str : lstOfUnassignedRecords)
            {
                CaseTaskView_DTO_HUM_LWC oCaseDTO =  mapCaseDTO.get(str);
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
            List<Case_Action_Association__c> lstCaseAction = [SELECT Id, Case__c, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = :HumConstants_LWC.CASEACTIONLAUNCHCONTROLLER_Intent_HUM and Case__c in :lstOfUnassignedRecords];
            Map<id,list<CaseCommentType_Junction__c>> caseCCTJunc = new Map<id,list<CaseCommentType_Junction__c>>();
            map<Id, CaseComment> mapMediCC = new map<Id, CaseComment>([SELECT Id, CommentBody, ParentId, createddate FROM CaseComment WHERE ParentId in :lstOfUnassignedRecords]);
            caseCCTJunc = CaseTaskView_H_HUM_LWC.getCommentsMap(lstOfUnassignedRecords,mapMediCC);
            List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c,Template_Submission__r.Template__r.Unique_Template_Name__c,Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  in (:HumConstants_LWC.MedExpTEMPLATEUNIQUEID,:HumConstants_LWC.PartDTEMPLATEUNIQUEID, :HumConstants_LWC.ATTESTATION_TEMPLATE_NAME, :HumConstants_LWC.GOODCAUSEUNIQUEID, :HumConstants_LWC.PCPTEMPLATEUNIQUEID) and Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c in :lstOfUnassignedRecords];
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and Group.Name in (:HumConstants_LWC.GOODCAUSEGROUPNAME, :HumConstants_LWC.PCPGROUPNAME, :HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ, :HumConstants_LWC.Group_LV_Correspondence_Screening_PGVE, :HumConstants_LWC.Group_LV_Correspondence_Screening_PGVP)];
            Set<String> grpLst = new Set<String>();
            if(lstGrpMem != null && !lstGrpMem.isEmpty())
            {
                for(GroupMember g : lstGrpMem)
                {
                   grpLst.add(g.Group.Name);
                }
            }
            bIsPharmacyUser = HUMUtilityHelper_LWC.isPharmacyUser() == HumConstants_LWC.NONPHARMACY_USER_HUM ? false : true;
            bIsPDPPilotUser = HUMUtilityHelper_LWC.isPDPPilotUser();
            MedicareCallsCaseComments_H_HUM_LWC oMedCalls = new MedicareCallsCaseComments_H_HUM_LWC();
            List<String> lstComplaints = new List<String>{HumConstants_LWC.MEDICAID,HumConstants_LWC.MEDICAREPART_C, HumConstants_LWC.MEDICAREPART_D};
            Set<String> setPharRecType = new Set<String>(HumConstants_LWC.Pharmacy_Record_Type_Name);   
            List<PermissionSetAssignment> lstPermission = [SELECT PermissionSetId,PermissionSet.Name FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name in (:HumConstants_LWC.RSO_HP_PermissionSet_Name, : HumConstants_LWC.MEDICARECALLS_PERMISSIONSET_HUM)];
            List<PermissionSetAssignment> lstRSOHPPermissionSet = new List<PermissionSetAssignment>();
            list<PermissionSetAssignment> lstPMAssign = new List<PermissionSetAssignment>();
            if(lstPermission != Null && !lstPermission.isEmpty())
            {
                for(PermissionSetAssignment lstP : lstPermission)
                {
                if(lstP.PermissionSet.Name==HumConstants_LWC.RSO_HP_PermissionSet_Name)
                lstRSOHPPermissionSet.add(lstP);
                if(lstP.PermissionSet.Name==HumConstants_LWC.MEDICARECALLS_PERMISSIONSET_HUM)
                lstPMAssign.add(lstP);
                }
            }
            for(String s : lstOfUnassignedRecords)
            {
                    Policy__c  policy = new Policy__c();
                    bvalidCase = true;
                    bCheckMedicareCustomerServiceAccess = false;
                    bCheckMedicareCCPermission = false;
                    isCallBenefitCategoryView = false;
                    bMedicareCalls = false;
                    Case oCase = new Case(id = s);
                    CaseTaskView_DTO_HUM_LWC oCaseDTO =  mapCaseDTO.get(s);
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
                            if(bvalidCase && String.isNotBlank(oCaseDTO.sClassification)  && oCaseDTO.sClassification == HumConstants_LWC.GOODCAUSECLASSIFICATION
                                && String.isNotBlank(oCaseDTO.sIntent) && oCaseDTO.sIntent == HumConstants_LWC.GOODCAUSEINTENT  
                                && String.isNotBlank(oCaseDTO.sClassificationType) && HumConstants_LWC.GoodCauseAutoRouteClassificationTypes.contains(oCaseDTO.sClassificationType)
                                && String.isNotBlank(oCaseDTO.sPolicyMember) && String.isNotBlank(p.Product_type_code__c) 
                                && HumConstants_LWC.PRODUCT_TYPE_PDP == p.Product_type_code__c )
                            {       
                                List <Template_Submission_Data__c> flowLst = new List<Template_Submission_Data__c>();
                                if(flowValue != Null && !flowValue.isEmpty())
                                {
                                    for(Template_Submission_Data__c t: flowValue)
                                    {
                                        if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.GOODCAUSEUNIQUEID)
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
                        if(!caseRecordTypeName.containsIgnoreCase(HumConstants_LWC.HumHPRecType))
                        {
                          if(isPharmacyProfile && ((chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == HumConstants_LWC.WEB_CHAT_ORIGIN) || oCaseDTO.sProduct == HumConstants_LWC.MEDICARE_POLICY))
                          {
                            if(wqueues.contains(oCaseDTO.sCaseTaskQueue) || wqueues.contains(oCaseDTO.sCreatedQueue) || (chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == HumConstants_LWC.WEB_CHAT_ORIGIN))
                            {
                              CaseReadonly = false;
                            }
                          }
                        }                    
                        if(caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_MEMBERCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.MEDICARE_CASE))
                        {
                            if(!((!isPharmacyProfile) || (isPharmacyProfile && !CaseReadOnly)))
                                bvalidCase = false;
                        }
                    }
                    
                    //QAA process check
                    Boolean bIsQAANeeded = (bvalidCase && String.isNotBlank(oCaseDTO.sClassification) && oCaseDTO.sClassification == HumConstants_LWC.CASEACTIONLAUNCHCONTROLLER_Classification_HUM && String.isNotBlank(oCaseDTO.sIntent) && oCaseDTO.sIntent == HumConstants_LWC.CASEACTIONLAUNCHCONTROLLER_Intent_HUM && lstComplaints.contains(oCaseDTO.sComplaint)); 
                    if(bIsQAANeeded)
                    {                
                        if(!CaseTaskView_H_HUM_LWC.checkProcess(oCase.id,lstCaseAction))
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
                    bCheckMedicareCustomerServiceAccess = CaseTaskView_H_HUM_LWC.validateMedicareCustomerServiceAccess(oCaseDTO.sOrigin,lstPMAssign);
            if (oCaseDTO.sPolicy != null && polMap != null  && polMap.containsKey(s))
                        policy = polMap.get(s);
                        
                    if(bCheckMedicareCustomerServiceAccess)
                    {
                        if (oCaseDTO.sPolicy != null)
                        {
                    
                            Map<String, Set<String>> medicareLOBProductType = HumConstants_LWC.MEDICARE_LOB_PRODUCTTYPE_LIST();
                            if (policy.Product__r.ProductCode != null && policy.Product__r.ProductCode != '' && policy.Product__r.ProductCode == HumConstants_LWC.MEDICARE_POLICY && medicareLOBProductType.containsKey(policy.Major_Lob__c))
                            {
                                Set<String> TempProductType;
                                TempProductType = medicareLOBProductType.get(policy.Major_Lob__c);
                                if (policy.Product__r.Product_Type__c != null && policy.Product__r.Product_Type__c != '' && TempProductType.contains(policy.Product__r.Product_Type__c))
                                {
                                    isCallBenefitCategoryView = true;
                                }
                                else if (policy.Product__r.Product_Type__c == null || policy.Product__r.Product_Type__c == '')
                                {
                                    isCallBenefitCategoryView = CaseTaskView_H_HUM_LWC.checkProductSegmentType(policy);
                                }
                            }
                         }
                            
                        else if ((string.IsBlank(oCaseDTO.sPolicyMember) || oCaseDTO.sPolicyMember == null) && (oCaseDTO.sInteractingAboutType == HumConstants_LWC.Member))
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
                    if (caseRecordTypeName != HumConstants_LWC.MEDICARE_CASE && caseRecordTypeName != HumConstants_LWC.CLOSED_MEDICARE_CASE)
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
                        if (lstCCTJunction != null && !lstCCTJunction.isEmpty() && String.isBlank(scasecom) && scasestatus==HumConstants_LWC.HUM_CLOSED)
                        {
                            sMedicareResInit = CaseTaskView_H_HUM_LWC.getMedCom(lstCCTJunction, mapMediCC);
                            if (String.isBlank(sMedicareResInit)){
                                bvalidCase = false;
                             }
                             else if(!String.isBlank(sMedicareResInit))
                             {
                             if(sMedicareResInit.length() > 2000 || sMedicareResInit.length() < 10)
                             bvalidCase = false;
                             }
                        }
                        else if(String.isBlank(scasecom) && scasestatus==HumConstants_LWC.HUM_CLOSED)
                        {
                            bvalidCase = false;
                        }   
                        if(String.isNotBlank(scasecom) && (scasecom.length() > 2000 || scasecom.length() < 10) && scasestatus==HumConstants_LWC.HUM_CLOSED)
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
                            bCloseDisableEditPage = CaseTaskView_H_HUM_LWC.checkEditPageClose(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,grpLst,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s);
                            bCloseDisableDetailPage = CaseTaskView_H_HUM_LWC.checkDetailPageClose(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,grpLst,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s,caseRecordTypeName);   
                            if(bCloseDisableDetailPage) bvalidCase = false;
                        }
                        if(bvalidCase && oCaseDTO.sCaseTaskQueue == HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER && oCaseDTO.bGuidedFlag == true && oCaseDTO.sStatus == HumConstants_LWC.CASE_STATUS_PENDING_RESPONSE)
                            disableCaseOwner = CaseTaskView_H_HUM_LWC.disableChangeOwnerCreditableCoverage(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,oCaseDTO.sStatus,s,true);
                        if(bvalidCase && oCaseDTO.sCaseTaskQueue == HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research  && !grpLst.contains(HumConstants_LWC.PCPGROUPNAME))
                            disableCaseAfterPCP = CaseTaskView_H_HUM_LWC.isPCPProcessAttached(grpLst,flowValue,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s,true);
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
                CaseTaskView_H_HUM_LWC.addCaseComment(scasecom, mapCaseComment, lstIds);
            }
        //}
        //catch(Exception ex)
        //{
           // HUMExceptionHelper.logErrors(ex,'Hum_CaseTasks_LWC', 'updateStatusOfAssignedRecords');
        //}
        
        return fetchCaseTask(true, queueData, queueValue, viewVlue, filterValue, assignWorkValue, secFilter,  lstItemAgeValue, lstTeamMembers); 
    }
    
    /*
    * This method Account and Policy details
    * @param   NA
    * @return  NA
    */
    @AuraEnabled
    public static void getPolicyAccountDetails(){
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
            HUMExceptionHelper.logErrors(ex,'Hum_CaseTasks_LWC', 'getPolicyAccountDetails');
        }
    }
    /*
    * This method to get the team members/reportees for current supervisior.
    * @param  NA
    * @Return Map<Id,string>
    */
    @AuraEnabled
    public static map<Id,string> getMyTeamUsers() {
     map<Id,string>  lstMyteamReportee = new map<Id,string>();
            List<User> lstMyTeamMber = [SELECT Id, Name FROM User WHERE Supervisor_Email_Address__c =:UserInfo.getUserEmail() AND Current_Queue__c  != '' AND IsActive =true ORDER BY Name limit 150];
            if(!lstMyTeamMber.isEmpty()){ 
                for(User usr : lstMyTeamMber) {
                    lstMyteamReportee.put(usr.id,usr.Name);
                }
            }
       return  lstMyteamReportee;
    }
    
    
    
    
    //call this when clicked on closecase
    @AuraEnabled
    public static list<CaseTaskView_DTO_HUM_LWC> changeCaseOwner(List<String> allWorkQueueList, List<String> lstWorkQ, 
                                                        List<String> allQueueViewList, List<String> lstView,List<String> lstFilterBy,
                                                        List<String> lstWorkItems,List<String> lstSecurityGrp, List<String> lstItemAge,
                                                        List<String> lstOfUnassignedRecords, String mapCaseDTO1,
                                                        string scasecom,string sServiceCenter,string sDepartment,string sTopic,string sOwnerName, string sOwnerId,string sTransferReason, String JsonMap,List<String> lstTeamMembers) {
        
        
        Map<string, CaseTaskView_DTO_HUM_LWC> mapCaseDTO = (Map<string, CaseTaskView_DTO_HUM_LWC>) json.deserialize(mapCaseDTO1, Map<String, CaseTaskView_DTO_HUM_LWC>.class);                                                 
        List<CaseTaskView_DTO_HUM_LWC> lstApplyFilterWrp = new List<CaseTaskView_DTO_HUM_LWC>();
        //prepare actual data for methods
        prepareParamDataWrap dataWrap = getStructuredDateForParam( allWorkQueueList, lstWorkQ, allQueueViewList, 
                                        lstView, lstFilterBy,lstWorkItems,lstSecurityGrp, lstItemAge,lstTeamMembers); 

       
        
        list<CaseTaskView_DTO_HUM_LWC> lsttransferOfAssignedRecords = transferOfAssignedRecords(lstOfUnassignedRecords, dataWrap.allWorkQueueList, dataWrap.lstWorkQ, dataWrap.lstView, dataWrap.lstFilterByName, dataWrap.WorkItemsName, dataWrap.lstSecurityGrpName, scasecom, sServiceCenter, sDepartment,
                                                                                                         sTopic, sOwnerName, sOwnerId, JsonMap, mapCaseDTO, sTransferReason, dataWrap.lstItemAgeName, dataWrap.lstTeamMemberName);
                                                                                                
       
        return lsttransferOfAssignedRecords;
        
    }
    
	 /**
     * Method used to update Status
     * <p>
     * @return   list<CaseTaskView_DTO_HUM_LWC> : List of Wrapper Records returned after accepting the WorkItems
     * @param    map<string,boolean> : Map of records needs to be Pulled
     */
    
    public static list<CaseTaskView_DTO_HUM_LWC> transferOfAssignedRecords(list<string> lstOfUnassignedRecords,list<string> queueData, List<string> queueValue,List<string> viewVlue,List<string> filterValue,string assignWorkValue, string secFilter, string scasecom,string sServiceCenter,string sDepartment,string sTopic,string sOwnerName, string sOwnerId, string jsonoWQS, map<string,CaseTaskView_DTO_HUM_LWC> mapCaseDTO, string sTransferReason, List<String> lstItemAgeValue, List<String> lstTeamMembers)
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
            String sGrpName = sGrpQueueName+HumConstants_LWC.HYPHEN_HUM+sTopic;
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
            List<String> sCenters = new List<String>{HumConstants_LWC.SERVICE_CENTER_HUMANA_PHARMACY,HumConstants_LWC.SERVICE_CENTER_RX};
            Set<String> wqueues = new Set<String>{HumConstants_LWC.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,HumConstants_LWC.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,HumConstants_LWC.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,HumConstants_LWC.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
            Set<String> chatQueues = new Set<String>(HumConstants_LWC.WEB_CHAT_WQUEUES);
            Set<String> processQueues = new Set<String>{HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER,HumConstants_LWC.WORK_QUEUE_LV_Screening,HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research,HumConstants_LWC.GOODCAUSEQUEUE};
            List<Case_Action_Association__c> lstCaseAction = [SELECT Id, Case__c, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = :HumConstants_LWC.CASEACTIONLAUNCHCONTROLLER_Intent_HUM and Case__c in :lstOfUnassignedRecords];
            Map<id,list<CaseCommentType_Junction__c>> caseCCTJunc = new Map<id,list<CaseCommentType_Junction__c>>();
            map<Id, CaseComment> mapMediCC = new map<Id, CaseComment>([SELECT Id, CommentBody, ParentId, createddate FROM CaseComment WHERE ParentId in :lstOfUnassignedRecords]);
            caseCCTJunc = CaseTaskView_H_HUM_LWC.getCommentsMap(lstOfUnassignedRecords,mapMediCC);
            List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c,Template_Submission__r.Template__r.Unique_Template_Name__c,Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  in (:HumConstants_LWC.MedExpTEMPLATEUNIQUEID,:HumConstants_LWC.PartDTEMPLATEUNIQUEID, :HumConstants_LWC.ATTESTATION_TEMPLATE_NAME, :HumConstants_LWC.GOODCAUSEUNIQUEID, :HumConstants_LWC.PCPTEMPLATEUNIQUEID) and Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c in :lstOfUnassignedRecords];
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and Group.Name in (:HumConstants_LWC.GOODCAUSEGROUPNAME, :HumConstants_LWC.PCPGROUPNAME, :HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ, :HumConstants_LWC.Group_LV_Correspondence_Screening_PGVE, :HumConstants_LWC.Group_LV_Correspondence_Screening_PGVP)];
            Set<String> grpLst = new Set<String>();
            if(lstGrpMem != null && !lstGrpMem.isEmpty())
            {
                for(GroupMember g : lstGrpMem)
                {
                   grpLst.add(g.Group.Name);
                }
            }
            bIsPharmacyUser = HUMUtilityHelper_LWC.isPharmacyUser() == HumConstants_LWC.NONPHARMACY_USER_HUM ? false : true;
            bIsPDPPilotUser = HUMUtilityHelper_LWC.isPDPPilotUser();
            MedicareCallsCaseComments_H_HUM_LWC oMedCalls = new MedicareCallsCaseComments_H_HUM_LWC();
            Set<String> setPharRecType = new Set<String>(HumConstants_LWC.Pharmacy_Record_Type_Name); 
            List<PermissionSetAssignment> lstPermission = [SELECT PermissionSetId,PermissionSet.Name FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name in (:HumConstants_LWC.RSO_HP_PermissionSet_Name, : HumConstants_LWC.MEDICARECALLS_PERMISSIONSET_HUM)];
            List<PermissionSetAssignment> lstRSOHPPermissionSet = new List<PermissionSetAssignment>();
            list<PermissionSetAssignment> lstPMAssign = new List<PermissionSetAssignment>();
            if(lstPermission != Null && !lstPermission.isEmpty())
            {
                for(PermissionSetAssignment lstP : lstPermission)
                {
                if(lstP.PermissionSet.Name==HumConstants_LWC.RSO_HP_PermissionSet_Name)
                lstRSOHPPermissionSet.add(lstP);
                if(lstP.PermissionSet.Name==HumConstants_LWC.MEDICARECALLS_PERMISSIONSET_HUM)
                lstPMAssign.add(lstP);
                }
            }
            Set<id> polId = new Set<id>();
            list<Policy__c> pol = new list<Policy__c>();
            Map<id,Policy__c> polMap = new Map<id,Policy__c>();
            Map<id,id> polCaseMap = new Map<id,id>();
            for(string str : lstOfUnassignedRecords)
            {
                CaseTaskView_DTO_HUM_LWC oCaseDTO =  mapCaseDTO.get(str);
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
            for(String s : lstOfUnassignedRecords)
            {
                Case oCase = new Case(id = s);
                bvalidCase = true;
                CaseTaskView_DTO_HUM_LWC oCaseDTO =  mapCaseDTO.get(s);
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
                    if(!caseRecordTypeName.containsIgnoreCase(HumConstants_LWC.HumHPRecType))
                    {
                      if(isPharmacyProfile && ((chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == HumConstants_LWC.WEB_CHAT_ORIGIN) || oCaseDTO.sProduct == HumConstants_LWC.MEDICARE_POLICY))
                      {
                        if(wqueues.contains(oCaseDTO.sCaseTaskQueue) || wqueues.contains(oCaseDTO.sCreatedQueue) || (chatQueues.contains(oCaseDTO.sCaseTaskQueue) && oCaseDTO.sOrigin == HumConstants_LWC.WEB_CHAT_ORIGIN))
                        {
                          CaseReadonly = false;
                        }
                      }
                    }                    
                    if(caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_MEMBERCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.MEDICARE_CASE))
                    {
                        if(!((!isPharmacyProfile) || (isPharmacyProfile && !CaseReadOnly)))
                            bvalidCase = false;
                    }
                }
                //G&A rights and Complaint check
                bGAndASectionRender = false;
                bGAndASectionRender = CaseTaskView_H_HUM_LWC.checkRecordType(caseRecordTypeName, oCaseDTO.sInteractingAboutType); 
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
                    bCheckMedicareCustomerServiceAccess = CaseTaskView_H_HUM_LWC.validateMedicareCustomerServiceAccess(oCaseDTO.sOrigin,lstPMAssign);
                    if (oCaseDTO.sPolicy != null && polMap.containsKey(s))
                        policy = polMap.get(s);
                    if (caseRecordTypeName != HumConstants_LWC.MEDICARE_CASE && caseRecordTypeName != HumConstants_LWC.CLOSED_MEDICARE_CASE)
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
                            sMedicareResInit = CaseTaskView_H_HUM_LWC.getMedCom(lstCCTJunction, mapMediCC);
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
                    else if(bvalidCase && processQueues.contains(oCaseDTO.sCaseTaskQueue) && (caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_MEMBERCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.MEDICARE_CASE) || caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_UNKNOWNCASE_HUM)))
                    {
                        bCloseDisableDetailPage = CaseTaskView_H_HUM_LWC.checkDetailPageClose(flowValue,oCaseDTO.sOwner,oCaseDTO.bGuidedFlag,grpLst,oCaseDTO.sCaseTaskQueue,oCaseDTO.sAutoRouteStatus,oCaseDTO.sStatus,s,caseRecordTypeName);   
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
                    if(String.isNotBlank(sTopic) && !sTopic.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_SELECTTOPIC_HUM))
                    {
                        oCase.topic__c = sTopic;
                    }
                    else if(String.isBlank(sTopic) || sTopic.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_SELECTTOPIC_HUM))
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
                    CaseTaskView_H_HUM_LWC.addCaseComment(scasecom, mapCaseComment, lstIds);
                }
           }
           sCases.addAll(lstIds);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Hum_CaseTasks_LWC', 'transferOfAssignedRecords');
        }
        //Get IDs of not transferred cases
        if(sCases != null && !sCases.isEmpty())
        {
           for(String s : lstOfUnassignedRecords)
           {
               if(!sCases.Contains(s))
               transferFailCases.add(s);
           }
        }
       else
           transferFailCases.addall(lstOfUnassignedRecords);
        return fetchCaseTask(true, queueData, queueValue, viewVlue, filterValue, assignWorkValue, secFilter, lstItemAgeValue, lstTeamMembers);
    }  
    //for method manipulating data when applyfilter, acceptcase, close case ,and transfer is calling  
    public static prepareParamDataWrap getStructuredDateForParam(List<String> allWorkQueueList, List<String> lstWorkQ, 
                                        List<String> allQueueViewList, List<String> lstView,List<String> lstFilterBy,List<String> lstWorkItems,
                                        List<String> lstSecurityGrp, List<String> lstItemAge, List<String> lstTeamMembers) {

        
        //we dont want select All should be there => 'All' when selected all from lwc
        if(lstWorkQ.contains('All')) {
            lstWorkQ.remove(0);
        }
        caseAndTaskMap = new map<String, String>{ 'Cases' => 'Case', 'Tasks' => 'Task'};
        list<String> lstFilterByName = new list<String>();

        for(String ct : lstFilterBy){
             if(caseAndTaskMap.containsKey(ct)){
                lstFilterByName.add(caseAndTaskMap.get(ct));
            }
            else {
                lstFilterByName.add(ct);
            }
        }
        List<String> lstItemAgeName = new List<String>();
        //if all is there , pass null, for specialist, method not applicable, so lwc will send null, then dont call this method 
        //if contains All pass null value further otherwise pass to below function
        if(!lstItemAge.contains('All') && lstItemAge !=null) {
            lstItemAgeName = Hum_CaseTasks_LWC.returnSelectedItemAge(lstItemAge);
        }
        else{
            lstItemAgeName = null;
        }
        String WorkItemsName = Hum_CaseTasks_LWC.returnSelectedWorkItem(lstWorkItems);
        String lstSecurityGrpName;
        //for specialist , this filter is not applicable, so lwc will send All, in this case dont call method
        if(!lstSecurityGrp.contains('All')) {
            lstSecurityGrpName = Hum_CaseTasks_LWC.changeInSecurity(lstSecurityGrp);
        }
        else {
            lstSecurityGrpName = 'All';
        }
        
        String orderChangeForCTAD = '';
        if(lstView.contains('All')) {
            lstView.remove(0);
            if(lstView.contains(System.Label.CaseTaskAllOtherQV) && !allQueueViewList.isEmpty() ){
                lstView.add(Hum_CaseTasks_LWC.getCTADQuery(lstWorkQ, allQueueViewList));
            }
            
            if(userCurrentProfile == HumConstants_LWC.Customer_Care_Supervisor || 
            (userCurrentProfile == HumConstants_LWC.PHARMACYSPECIALIST_PROFILE_HUM && sPharmacyUser == HumConstants_LWC.PHARMACYSUPERVISOR_USER_HUM)
            ){
                lstView.add('All (Al)');
            }
            else{
                lstView.add('All (All)');
            }
            //CTAD is present on second last, as per logic, it should be last element written in apex fechcase menthod
            orderChangeForCTAD = lstView.remove(lstView.size() -2);
            lstView.add(orderChangeForCTAD);
        }
        else{
            if(lstView.contains(System.Label.CaseTaskAllOtherQV)&& !allQueueViewList.isEmpty()){
                lstView.add(Hum_CaseTasks_LWC.getCTADQuery(lstWorkQ, allQueueViewList));
            }
        }
        prepareParamDataWrap paramData = new prepareParamDataWrap();
        paramData.lstWorkQ = lstWorkQ;
        paramData.allWorkQueueList = allWorkQueueList;
        paramData.lstView = lstView;
        paramData.lstFilterByName = lstFilterByName;
        paramData.WorkItemsName = WorkItemsName;
        paramData.lstSecurityGrpName = lstSecurityGrpName;
        paramData.lstItemAgeName = lstItemAgeName;
        paramData.lstTeamMemberName = lstTeamMembers;                                    
        paramData.allQueueViewList = allQueueViewList;

        return paramData;
        
    }
    //for  manipulating data when applyfilter, acceptcase, close case ,and transfer is calling  
    public class prepareParamDataWrap {
        @AuraEnabled
        public List<String> allWorkQueueList;
        @AuraEnabled
        public List<String> lstWorkQ;
        @AuraEnabled
        public List<String> allQueueViewList;
        @AuraEnabled
        public List<String> lstView;
        @AuraEnabled
        public List<String> lstFilterByName;
        @AuraEnabled
        public String WorkItemsName;
        @AuraEnabled
        public String lstSecurityGrpName;
        @AuraEnabled
        public List<String> lstItemAgeName;
        @AuraEnabled
        public List<String> lstTeamMemberName;
    }

    //for holding intial load parameters
    public class filterWrapper {
        @AuraEnabled
        public List<String> workQueueList;
        @AuraEnabled
        public List<String> queueViewList;
        @AuraEnabled
        public List<String> pullWorkFilterByOptions;
        @AuraEnabled
        public List<String> SelectedAssign;
        @AuraEnabled
        public List<String> securityView;  
        @AuraEnabled
        public List<String> selectedView;
        @AuraEnabled
        public List<String> itemAge;
        @AuraEnabled
        public map<Id,String> myTeamMembers;
        @AuraEnabled
        public String currentQueue;
        @AuraEnabled
        public list<CaseTaskView_DTO_HUM_LWC> initialFetchCaseTasks;
		@AuraEnabled
        public String currentProfile; 
        @AuraEnabled
        public String currentUserName;
        @AuraEnabled
        public Boolean bswitch_JS2578074;
        @AuraEnabled
        public String sPharmacyUser;
    }

}