/**********************************************************************************************************************************************************************
Apex Class Name :  HUMUpdateCaseHelper
Version: 1.0
Created On      : 6/17/2014
Function        : Used For trigger functionality. Populates the queueview name whenever a case Is inserted Or updated based on queueview And filters.
Test Class      : HUMUpdateCaseHelperTest

        Modification Log:  
* Developer Name              Code Review                Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu                17093                   6/17/2014                  Original Version
* Mrudula Jaddu                17878                   9/12/2014                  Modified fields as part of defect 154260 
* Mrudula Jaddu                17996                   9/18/2014                  Modified Queue assignment to other view as part of defect 154980 
* Mrudula Jaddu                20092                   12/26/2014                 Modified code for REQ-95896
* Santhi Mandava               20517                   01/23/2015                 Fixed defect #164139
* Santhi Mandava               21968                   03/16/2015                 As per REQ-02635 removed future call out related logic And updated code to include 
                                                                                  newly created MD relationship fields And refactored existing code.
* SuryaKumari Medicherla       22110                   04/06/2015                 updateCaseQueues Method modified to fix production issue 247.                                                                                   
* Pradeep Raju                 22530                   04/22/2015                 Updated the code at line# 118 to make use of List for CA Ticket# 5034437 fix
* Manuel Johnson                                       10/15/2015                 REQ - 215096: Added logic to update Previous Owner Queue field when Owner Queue field Is modified
* Avinash Choubey              34551                   08/04/2016                 defect 235634 fix 
* Harshith Mandya              47072                   11/07/2016                 Modified code to pickup only active queue
* Avinash Choubey              46567                   11/07/2016                 defect 247444 fix 
* Sharan Shanmugam             47072                   11/09/2016                 Modified code to pickup only active queue
* Sharan Shanmugam             47343                   11/10/2016                 Modified code to fix the routing
* Harshith Mandya                                      11/23/2016                 Defect Fix # 249892.0001 - Updating limit o query to fetch all the QVIs related to View
* Harshith Mandya                                      11/30/2106                 Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                  Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
* Harshith Mandya              50905                   12/07/2016                 Defect Fix #252024.0001 
* Harshith Mandya                                      12/27/2016                 REQ - 296640 - Removing query from getmatchedViews And moving it to HUMUpdateCaseTaskHelper
* Harshith Mandya                                      02/20/2016                 Implmented REQ - 300084
* Shiva Pasumarty              85638                   04/19/2017                 Auto route queue view populate
* Pallavi R                    78362                   03/22/2017                 REQ 304973 - Case Data model change w.r.t CTCI object
* Shruthi Karanth              91794                   05/10/2017                 Skip code in updateCaseQueues when medicare enrollment cases are created through ETL
* Pallavi R                    94646                   05/18/2017                 Changed code  - fix for CRM Ticket : 6794554
* Mohammed Noor                94461                   05/18/2017                 REQ - 296939 Remove constants from the logic used to skip code in updateCaseQueues when  
*                                                                                 medicare enrollment cases are created through ETL.
* Shruthi Karanth              94461                   05/24/2017                 REQ - 296939 update the logic used to skip code for ETL user in updateCaseQueues method  
* Pallavi R                    98201                   05/31/2017                 Implemented REQ 318433 - changed classification and intent id references to CTCI references
* Pradeepkumar Dani           102268                   06/16/2017                 Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Manoj Chaurasia             167654                   01/04/2018                 REQ - 349585: Update Omni Eligible flag
* Manoj Chaurasia             178236                   02/02/2018                 Fix for Defect# 341375
* Mohammed Noor                                        02/06/2018                 Fix for Defect # 341762.
* Syed Mubarak                                         05/21/2018                 REQ - 361868 - Update the case transferred date on change of WQ and WQV
* Archana Das                 244025                   06/18/2018                 REQ- 362041 - Add Metadata for Omni Eligible list
* Ajay Chakradhar                                      04/16/2019                 REQ -392832 - Transfer of cases that are auto routed to the work queue and view
* Subhamay Mandal                                      04/23/2019                 REQ - 396287 Quality - MIR - Case transfer within same work queue but different queue view (RF)
* Amar Gadewar               358242                    06/06/2019                 REQ - 399325 - Warm Case Transfer Scenarios for MIR - Quality - MIR - Milestone display on case transfer to the user NOT having MIR Permission Set (RF)
* Subhamay Mandal                                      07/02/2019                 REQ - 403802 Incident 8384434 Fix
* Shailesh Mali                                        08/08/2019                 REQ - 405958 WorkQueueView Optimization
* Moshitha Gunasekaran                                 11/12/2019                 REQ - 409539 Made changes to have proper execution of if condition in updateCaseQueues method for Closed cases in asynch process 
* Shailesh Mali                                        11/15/2019                 REQ - 409603 Process Builder logic Migration to Apex Code.
* Shailesh Mali                                        12/05/2019                 REQ - 409603 , Defect 450927.0001 fixed  .
* Mohammed Noor                                        12/06/2019                 US826586 - Case Workflow rules logic migration to Apex code.
* Sai Kumar Boga                                       07/08/2020                 REQ - 1197867 MF 1- Development- FR1.1 Update case service to allow case owner to be updated.
* Subhamay Mandal                                      08/24/2020                 User Story 1293633: PR00091574- IR 5050-OGO - Recommendation actions on re-opened cases (reject )-(CRM)
* Subhamay Mandal                                      08/28/2020                 User Story 1293633: update logic for humCloseCaseUpdateOwner() method
* Vinay Lingegowda                                     10/10/2020                 USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)**
* Santhi Mandava                                       09/30/2020                 Changed 'populateviewname' execution from sync to async for Humana APi user
* Sai Kumar Boga                                       11/09/2020                 INC0716609 - Pull Work issue for reopened Vantage cases
* Vinay Lingegowda                                     11/13/2020                 USER STORY 1628353 PR00091574 - IR 5050 - OGO - Add a new View "Clarabridge Recommendations" for Re-opened cases (CRM)
* Gourab Tewary                                        01/15/2021                 USER STORY 1750379 PR00091574 - IR 5050 - Update/Fix previous tracking fields on case transfers within same departments (RF)
* Subhamay Mandal                                      02/12/2021                 USER STORY 1989939 Defect fix. DF-2404. User Story 1745061
* Mohammed Noor                                        04/30/2021                 USER STORY 2163321 - Checkmarx - Remove references of HUMClosedCaseCRMToCCPInquiries.
* Mithra Bharadwaj                                                                US#2089579 Sonar Qube Fix
* Mohammed Noor										   07/01/2021				  USER STORY 2273201 - Case Management Checkmarx Fixes.
* Moshitha Gunasekaran                                 07/23/2021                 US#2027311 T1PRJ0001848 - (CSI1955) - Platform - Asynch Chat - Async Case and Interaction in CRM
* Mohammed Noor										   08/06/2021				  US2498249 - Add new Group Number field for QVI.
* Pooja Kumbhar					                       01/18/2023                 DF-6990 Defect fix
* Ajay Chakradhar                                      01/24/2023                 US-3782199 - T1PRJ0286242 - MF CRM Phase 5 - Tech - SF - Incident #INC1901501 Update to ETL Fix for CPD Mixed Members
* Pooja Kumbhar                                        01/30/2023                 User story 4083329: T1PRJ0170850 - Lightning- UI Trigger Change to Support ETL added Enhanced changes
* Pooja Kumbhar										    2/2/2023				  User story 4083329: Removed initialization and updated variable name 
* Ajay Chakradhar                                      08/31/2023                 US4957069  T1PRJ0891742 - AHTR - SF Security Remediation - Case Lgt
***********************************************************************************************************************************************************************/
 
Public with sharing Class HUMUpdateCaseHelper
{
     
    Private List<Case> lstCasesUpdate = New List<Case>();
    Private static boolean bNoView = True;
    Private static List<Queue_View__c> lstMatchedQueuesViews = New List<Queue_View__c>();  
    Private static boolean bOpenCasePresent = False;
    Private static Set<String> setQueues = New Set<String>();
    private static boolean bSwitch_2027311 = false;
    
    /**
    *  getCTCImap
    * <p>
    * This method Is used for initializing Map of Intent And Classification values
    *
    * @param  List<case> lstMyCases
    * @param  Map <id, CTCI_Junction__c > mapCTCIobj
    * @return  void
    * @see
    */  
    private static void getCTCImap(List<case> lstMyCases, Map < id, CTCI_Junction__c > mapCTCIobj)
    {  
     set<ID> setcaseCTCID = new set<id>();   
        for(Case oCaseRec : lstMyCases)
        {            
            if(oCaseRec.CTCI_List__c != null) setcaseCTCID.add(ocaseRec.CTCI_List__c );   
            if(oCaseRec.Status !=  GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM &&  oCaseRec.Status != GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM)
            {
                bOpenCasePresent = true;
                setQueues.add(oCaseRec.Owner_Queue__c);
            }
        } 
        
        list<CTCI_Junction__c> lstobjctci = [select id,name ,classification__c,classification__r.name,Intent__c,intent__r.name from  CTCI_Junction__c  where id in : setcaseCTCID];
          if(!lstobjctci.isEmpty())
           {
             for( CTCI_Junction__c CT: lstobjctci)
              {
                mapCTCIobj.put(CT.id,CT);
              }
           }               
    }
    
    /**
    * Returns void
    * <p>
    * This is the main method used for getting the work queue view of case record- based on the service center
      and department along with checking the queue views and filter conditions in view lists
    *
    * @param  List<case> lstMyCases
    * @param  Boolean isTrigger
    * @param  Set<String> setQueueNames
    * @return  void
    * @see
    */ 
    public static void populateViewName(List<case> lstMyCases,Boolean isTrigger,Set<String> setQueueNames,String sProfileName)
    { 
        Map < id, CTCI_Junction__c > mapCTCIobj = new Map < Id, CTCI_Junction__c > ();             
        getCTCImap(lstMyCases, mapCTCIobj);
        List<Case> lstCasesForAsyncUpdate = new List<Case>();
        List<Case> lstOmniCases = new List<Case>();
        List<Case> lstNonOmniCases = new List<Case>();
        Map<Id,Case> mapAsyncCases = new Map<Id,Case>();
        String sCaseOwner;
        Case objCase;
        String sUserId = userinfo.getuserid();
        CaseProcessBuilderSettings_HUM__c objSetting = CaseProcessBuilderSettings_HUM__c.getOrgDefaults();
        String sUsername = UserInfo.getUserName();
        ETL_User_Check__c OneshotETLUser;
        if(sUsername.length()<=38) OneshotETLUser = ETL_User_Check__c.getValues(sUsername);     

        if(!setQueues.isEmpty())
        {
            setQueueNames = new Set<String>();
            setQueueNames.addAll(setQueues);
        }
            
        if(bOpenCasePresent)
        {
            HUMUpdateCaseTaskHelper.initializeListAndMaps(false,setQueueNames);
            try
            { 
                //Get all Omni eligible list from Metadata
                Map<String,Set<String>> mapWorkQueue = getOmniEligibleList();
                for(Case oCaseRec : lstMyCases)
                {   
                    objCase = new Case(Id=oCaseRec.Id);
                    String prevWorkQueueViewName = oCaseRec.Work_Queue_View_Name__c;
                    sCaseOwner = oCaseRec.Case_Owner__c;
                     oCaseRec.Omni_Eligible__c = false;
                     CTCI_Junction__c oCtci  = new CTCI_Junction__c();
                    if(oCaseRec.CTCI_List__c != null) oCtci = mapCTCIobj.get(oCaseRec.ctci_list__c) ;             
                    If(oCaseRec.Status !=  GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM &&  oCaseRec.Status != GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM )
                    {
                       if(oCaseRec.Topic__c!=GLOBAL_CONSTANT_HUM.VantageReopenedCases)
                       {
                        oCaseRec.Work_Queue_View_Name__c = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALLOTHERVIEW_HUM;
                       }
                       
                        lstMatchedQueuesViews = getMatchedViews(oCaseRec,oCtci);
                        if(lstMatchedQueuesViews.size() > 1)
                        {
                            Map<String, String> mapOfPriority = HUMUpdateCaseTaskHelper.mapOfQueuePriority.get(oCaseRec.Owner_Queue__c);
                            oCaseRec.Work_Queue_View_Name__c = HUMUpdateCaseTaskHelper.getMatcheViewBasedOnPriority(lstMatchedQueuesViews,mapOfPriority ,1 );
                        }
                        else if(lstMatchedQueuesViews.size() == 1)
                        {
                            oCaseRec.Work_Queue_View_Name__c = lstMatchedQueuesViews[0].Queue_View_Name__c;
                        }
                        else if(bNoView)
                        {
                            oCaseRec.Work_Queue_View_Name__c ='';
                        } 
                        bNoView = true;
                        lstMatchedQueuesViews.clear(); 
                        if(prevWorkQueueViewName != oCaseRec.Work_Queue_View_Name__c)
                        {
                            oCaseRec.Transferred_Date__c= System.Now();
                            oCaseRec.Previous_Work_Queue_View__c = prevWorkQueueViewName;
                        }
                    }
                    //REQ - 349585
                    //Get the list of Omni Eligible Queues from metadata instead of global contants
                    if(mapWorkQueue.containsKey(oCaseRec.Owner_Queue__c)){
                        Set<String> setWorkQView = mapWorkQueue.get(oCaseRec.Owner_Queue__c);
                        if(setWorkQView.contains(oCaseRec.Work_Queue_View_Name__c))
                        {
                            oCaseRec.Omni_Eligible__c = True;
                        }
                    }

                    //In case of Humana API user, we are not updating all cases and all fields. Only modified cases and modified fields will be considered for update
                    if(sProfileName == GLOBAL_CONSTANT_HUM.ETL_API_ACCESS  && (objSetting.ETL_UserId__c == sUserId || OneshotETLUser != null))
                    {
                        if(prevWorkQueueViewName != oCaseRec.Work_Queue_View_Name__c)
                        {
                            if(Schema.SObjectType.Case.fields.Work_Queue_View_Name__c.isUpdateable())	objCase.Work_Queue_View_Name__c = oCaseRec.Work_Queue_View_Name__c;
                            if(Schema.SObjectType.Case.fields.Transferred_Date__c.isUpdateable())	objCase.Transferred_Date__c = oCaseRec.Transferred_Date__c;
                            if(Schema.SObjectType.Case.fields.Previous_Work_Queue_View__c.isUpdateable())	objCase.Previous_Work_Queue_View__c = oCaseRec.Previous_Work_Queue_View__c;
                            if(oCaseRec.Omni_Eligible__c == False) 
                            {
                                if(mapWorkQueue.containsKey(oCaseRec.Owner_Queue__c))
                                {
                                    Set<String> setWorkQView = mapWorkQueue.get(oCaseRec.Owner_Queue__c);
                                    if(setWorkQView.contains(prevWorkQueueViewName))
                                    {  
                                        lstNonOmniCases.Add(oCaseRec);
                                    }
                                }
                            }
                            else if(oCaseRec.Omni_Eligible__c == True)
                            {
                                lstOmniCases.Add(oCaseRec);
                            }
                            if(Schema.SObjectType.Case.fields.Omni_Eligible__c.isUpdateable())	objCase.Omni_Eligible__c = oCaseRec.Omni_Eligible__c;
                        }
                        if(sCaseOwner != oCaseRec.Case_Owner__c && Schema.SObjectType.Case.fields.Case_Owner__c.isUpdateable()) objCase.Case_Owner__c = oCaseRec.Case_Owner__c;
                        if(prevWorkQueueViewName != oCaseRec.Work_Queue_View_Name__c || sCaseOwner != oCaseRec.Case_Owner__c) 
                        {
                            mapAsyncCases.Put(objCase.Id,objCase);
                        }
                    }
                    //END REQ - 349585
                }
                if(!isTrigger)
                {
                    Database.UpsertResult[] lstResults = Database.upsert(lstMyCases,false);
                    HUMExceptionHelper.isBatchApex = true;
                    HUMExceptionHelper.processUpsertResults(lstResults,lstMyCases,'WorkQueueViewNameBatch','','Case');
                }
                
                //If the logged in user's profile is 'ETL API Access' and logged in user id is matching with id present in 'CaseProcessBuilderSettings_HUM__c' custom setting
                //Then populateviename method will be executed in async mode this is to fix apex CPU time limit issue for ETL batch jobs. In case of async execution we need to update records explicitly to save changes
                if(sProfileName == GLOBAL_CONSTANT_HUM.ETL_API_ACCESS  && (objSetting.ETL_UserId__c == sUserId || OneshotETLUser != null))
                    
                {
                     TriggerAvoidRecursion_H_HUM.bFutureLogicRan = true;
                     Map<String,Group> mapGroupIds = new Map<String,Group>();
                     for(Group groupObj : [select Id,Name from Group where Name IN : setQueueNames AND Type = : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM])
                     {
                        mapGroupIds.put(groupObj.Name,groupObj);
                     }
                     //Before updating cases in async mode, we are executing omni rules again for omni eligible cases
                     List<Case> lstOmniEligibleCases = executeOmniQueueReEvaluationLogic(lstOmniCases);
                     List<Case> lstOmniProcessedCases = executeOmniEligibleCriteriaAsync(lstOmniEligibleCases);
                     For(Case obj : lstOmniProcessedCases)
                     {
                        Case objTempCase = mapAsyncCases.get(obj.Id);
                        if(objTempCase == Null)
                        {
                            Case objNew = new Case(Id=obj.Id,OwnerId = obj.OwnerId);
                            mapAsyncCases.put(objNew.Id,objNew);
                        }
                        else
                        {
                            if(Schema.SObjectType.Case.fields.OwnerId.isUpdateable())	objTempCase.OwnerId = obj.OwnerId;
                            mapAsyncCases.put(objTempCase.Id,objTempCase);
                        }
                     }
                     For(Case obj : lstNonOmniCases)
                     {
                        Case objTempCase = mapAsyncCases.get(obj.Id);
                        String sOwnerId = '';
                        if(mapGroupIds!= null && mapGroupIds.containsKey(obj.Owner_Queue__c)){
                            sOwnerId = mapGroupIds.get(obj.Owner_Queue__c).Id;                                                                
                        }
                        if(objTempCase == Null)
                        {

                            Case objNew = new Case(Id=obj.Id,OwnerId = sOwnerId);
                            mapAsyncCases.put(objNew.Id,objNew);
                        }
                        else
                        {
                            objTempCase.OwnerId = sOwnerId;
                            mapAsyncCases.put(objTempCase.Id,objTempCase);
                        }
                     }
                     if(mapAsyncCases != Null && !mapAsyncCases.isEmpty())
                     {
                        List<Case> lstTobeUpdated = mapAsyncCases.values();
                        update lstTobeUpdated;
                     }
                }
             }
             catch (Exception e)
             {
                HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'populateViewName');
             }
         }
    }
    
    /**
    * Returns void
    * <p>
    * This is the main method used for executing invokable method from Process Builder to update View Names
    *
    * @param  List<case> lstCases    
    * @return  void
    * @see
    */ 
    @InvocableMethod
    public static void populateViewNamePB(List<case>lstCases)
    {
       set<id> caseid = new set<id>();
       for(case cas : lstCases)
       {
            caseid.add(cas.id);     
       }
        List<case> lstMyCases = [Select Id, IsDeleted, CaseNumber, ContactId, AccountId, AssetId, ParentId, 
       SuppliedName, SuppliedEmail, SuppliedPhone, SuppliedCompany, Type, RecordTypeId, Status, Reason, Origin, Subject, Priority, Description, 
       IsClosed, ClosedDate, IsEscalated, OwnerId, IsClosedOnCreate, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, SystemModstamp, Case_Age__c, Case_Number__c, 
       Case_Owner__c, Case_TAT__c, CoKY_Case__c, Complaint_Reason__c, Complaint__c, Created_By_Queue__c, DST_Case__c, Days_Since_Last_Modified__c, 
       Department__c, Due_Date__c, Employer_Group_Name__c, Group_Number__c, Follow_Up_Needed__c, Exchange_Type__c, Follow_up_Due_Date__c, Funding_Type__c, G_A_Reason__c,
       G_A_Rights_Given__c, General_Case__c, HO_Dental_Case__c, HO_Medical_Case__c, Interacting_About_Type__c, Interacting_About__c, Interacting_With_Name__c, 
       Interacting_With_Type__c, Interacting_With__c, Interaction_ID__c, Language_Preference__c, LastModifiedby_Queue__c, Line_of_Business__c, Owner_Queue__c, Owner__c, Plan_Issue_State__c,
       Plan_Segment_Size__c, Policy_Member__c, Policy_Platform__c, Previous_Case_Owner_Role__c, Product_Type__c, Product__c, Exchange__c, Re_Open_Case_Age__c, Re_Open_Case_Date__c, Reopened__c,
       Service_Center__c, State_Mem_Resid_Add__c, Subtype__c, Task_TAT__c, Tenant_Id__c, Texas_Complaint_Letter_Requested__c, Topic__c, Work_Queue_View_Name__c, Apply_to_Complaint__c,
       Classification_Type__c, Email_Sent__c, NPI_ID__c, Recommended_Process__c, TX_Complaint_Related_Message__c, Tax_ID__c, Case_Referred_To__c, Referred_to_Another_Channel__c, 
       Selling_Market_Number__c, Behavioral_Health__c, OpenEnrollmentType__c, OpenEnrollment__c, Open_Work_Task_Count__c, Policy_Product_Code__c, Provider_Shipping_State_Code__c, 
       Group_Number_for_Policy__c, Policy__c, Previous_Owner_Queue__c, Classification_Id__c, Intent_Id__c, 
       Complaint_Type__c, Total_Work_Task_Count__c,  Autoroute_Status__c,Transferred_Date__c,CTCI_List__c,CTCI_List__r.Intent__r.Name,CTCI_List__r.Classification__r.Name FROM Case where id IN:caseid];
       HUMUpdateCaseHelper ohandler = new HUMUpdateCaseHelper();       
       populateViewName(lstMyCases,false,null,'');
       ohandler.caseTransferToMedhok(lstMyCases);
    }
    
    /**
    * Returns List<Queue_View__c>
    * <p>
    * This method is used to get the matched views based on case details.
    *
    * @param  Case oCaseRec
    * @param  Map<Id, String> mapIntentAndCL
    * @return  List<Queue_View__c>
    * @see
    */
    private static List<Queue_View__c> getMatchedViews(Case oCaseRec, CTCI_Junction__c oCtci)
    {
        Map<Id,List<Queue_View_Item__c>> mapViewItem = HUMUpdateCaseTaskHelper.mapViewItems.clone();
        List<Queue_View__c> lstMatchedViews = new List<Queue_View__c>();

        if(String.isNotBlank(oCaseRec.Owner_Queue__c) && String.isNotBlank(oCaseRec.Service_Center__c) && String.isNotBlank(oCaseRec.Department__c))
        {
            List<Queue_View__c> lstTemp = HUMUpdateCaseTaskHelper.mapViews.get(oCaseRec.Owner_Queue__c);
            If( lstTemp != Null)
            {
                for(Queue_View__c oQueueView : lstTemp )
                {
                    if (oCaseRec.Owner_Queue__c.equals(oQueueView.Work_Queue_Name__r.Name) && 
                         oCaseRec.Service_Center__c.equals(oQueueView.Work_Queue_Name__r.Service_Center__c) &&
                         oCaseRec.Department__c.equals(oQueueView.Work_Queue_Name__r.Department__c)) 
                    {
                        bNoView = false;
                        if(mapViewItem.containsKey(oQueueView.Id) && !mapViewItem.get(oQueueView.Id).isEmpty())
                        {
                            if(HUMUpdateCaseTaskHelper.checkFilterConditions(oQueueView ,oCaseRec,mapViewItem.get(oQueueView.Id), oCtci)) 
                            {
                                lstMatchedViews.Add(oQueueView);
                            }
                        }
                    }
                }
            }
            else
            {
                 bNoView = false;
            }
        }
        return lstMatchedViews;
    }
    
    /**
    * This method used for updating case fields.
    * Process Builder "HUMCloseCaseCRMToCCPInquiries" logic implemented to below method.
    * Updating the Case field values, calling the populateViewName() method
    * @method  creditableCoverage()
    * @param   List<case> lstCases
    * @return  void 
    **/ 
    public void creditableCoverage(List<case> lstCases){
        try{
            List<Case> creditableCoverageCaseList = new List<Case>();           
            Group[] ownerIdGroup = [SELECT Id FROM Group WHERE Name =: GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT AND Type =: GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM LIMIT 1];         
            for(Case oCaseRec : lstCases){
                if(String.isNotBlank(oCaseRec.Subtype__c) && String.isNotBlank(oCaseRec.Owner_Queue__c) && String.isNotBlank(oCaseRec.Topic__c) && String.isNotBlank(oCaseRec.Complaint__c) && String.isNotBlank(oCaseRec.Status) && String.isNotBlank(oCaseRec.Classification_Type__c)
                   && oCaseRec.Subtype__c == GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE && (oCaseRec.Owner_Queue__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_QUEUENAME) && (oCaseRec.Status).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM)
                   && (oCaseRec.Topic__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGECASESUBTYPE) && ( (oCaseRec.Complaint__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICAREPART_D) || (oCaseRec.Complaint__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICAREPART_C) || (oCaseRec.Complaint__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEDICAID))
                   && (oCaseRec.Classification_Type__c).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.HUMCCPEnrollmentRSO))
                {
                    oCaseRec.Autoroute_Status__c = GLOBAL_CONSTANT_HUM.ROUTED_PENDING_ACCEPT;
                    oCaseRec.Case_Owner__c = GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT;                        
                    oCaseRec.Department__c = GLOBAL_CONSTANT_HUM.RSO_ORAL_GRIEVANCE_OVERSIGHT_TEAM;
                    oCaseRec.Owner_Queue__c =  GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT; 
                    oCaseRec.Service_Center__c = GLOBAL_CONSTANT_HUM.SERVICE_CENTER_LOUISVILLE;
                    oCaseRec.Status = GLOBAL_CONSTANT_HUM.CASESTATUS_HUM;
                    oCaseRec.Topic__c = GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE;
                    
                    if (ownerIdGroup != null && !ownerIdGroup.isEmpty()) 
                        oCaseRec.OwnerId = ownerIdGroup[0].Id;    
                    
                    creditableCoverageCaseList.add(oCaseRec);   
                }               
            }        
            if(creditableCoverageCaseList !=null && !creditableCoverageCaseList.isEmpty()){
                populateViewName(creditableCoverageCaseList,true,null,'');             
            }       
        }
        catch (Exception e)
        {
             HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'creditableCoverage');
        }
    }
        
    /**
    * This method used for updating case fields.
    * Process builder "HUMCloseCaseUpdateOwner" logic implemented to below method.
    * Updating the CaseOwner,OwnerId and OwnerQueue for Customer_Care_Supervisor, HPSupervisor and PharmacySpecialist users 
    * @method  humCloseCaseUpdateOwner()
    * @param   List<Case> lstCaseNewValues, Map<Id, Case> oldMap, string profileName
    * @return  void 
    **/ 
    public void humCloseCaseUpdateOwner(List<Case> lstCaseNewValues, Map<Id, Case> oldMap, string profileName){
        try{
            Id userId = UserInfo.getUserId();        
            boolean isHPSupervisor = false;
            List<User> userInformation ;            
            isHPSupervisor = UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess(GLOBAL_CONSTANT_HUM.CRMS_300_HP_Supervisor_Custom);
                
            if(profileName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Customer_Care_Supervisor) || (profileName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.PHARMACYSPECIALIST_PROFILE_HUM) && isHPSupervisor) && oldMap != null){
                if(userId != null)
                    userInformation = [Select Id,FirstName,LastName,Current_Queue__c From User where Id =:userId limit 1];
                
                for(Case caseRecord: lstCaseNewValues){
                    String sOldTopic = oldMap.get(caseRecord.Id).Topic__c;
                    Boolean bIsOGOClose = false;
                    if(String.isNotBlank(sOldTopic)) {
                        bIsOGOClose = (sOldTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE_INTERNAL) || sOldTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TOPIC_CLARABRIDGE_ORAL_GRIEVANCE_INTERNAL));
                    }
                    if(caseRecord.Status !=null && (caseRecord.Status).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) && oldMap.containsKey(caseRecord.Id) && caseRecord.Status != oldMap.get(caseRecord.Id).Status && !bIsOGOClose)
                    {
                        if(userInformation != null && !userInformation.isEmpty())
                        {
                            caseRecord.Case_Owner__c = userInformation[0].FirstName + ' ' + userInformation[0].LastName;
                            caseRecord.OwnerId = userInformation[0].Id;
                            caseRecord.Owner_Queue__c = userInformation[0].Current_Queue__c;
                        }                       
                    }   
                } 
            }
        }
        catch (Exception e)
        {
             HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'humCloseCaseUpdateOwner');
        }
    }
    
    /**
    * This method used for updating case fields.
    * Process Builder "Case General and HO Security Flag Updates" logic implemented in below method.
    * Updating the General, HO_Medical, HO_Dental values on Case object
    * @method  caseGeneralHOGeneralSecurity()
    * @param   List<Case> lstCaseNewValues, Map<Id, Case> oldMap
    * @return  void 
    **/ 
    public void caseGeneralHOGeneralSecurity(List<Case> lstCaseNewValues, Map<Id, Case> oldMap){
        try{
            Set<Id> policyMemberId = new Set<Id>();
            Set<Id> acctId = new Set<Id>();        
            List<Policy_Member__c> policyMemberList = new List<Policy_Member__c>();
            List<Account> accList = new List<Account>();        
            Map<Id,Policy_Member__c> casePolicyMemberMap = new Map<Id,Policy_Member__c>();
            Map<Id,Account> caseAccountMap;
            
            for(Case caseRecord: lstCaseNewValues){
                if(caseRecord.AccountId !=null)
                    acctId.add(caseRecord.AccountId);
                if(caseRecord.Policy_Member__c !=null)
                    policyMemberId.add(caseRecord.Policy_Member__c);
            }
            
            if(policyMemberId != null && !policyMemberId.isEmpty())
                policyMemberList = [Select Id, Policy__r.Product__r.ProductCode,Policy__r.Group_Name__r.Contract_Protected_Data__c, Policy__r.Group_Name__r.General_Account__c, Policy__r.Group_Name__r.Home_Office_Account__c From Policy_Member__c where Id IN : PolicyMemberId];
            
            if(policyMemberList !=null && !policyMemberList.isEmpty()){
                for(Policy_Member__c policyMember :policyMemberList ){
                    if(policyMember.Policy__c !=null)
                        casePolicyMemberMap.put(policyMember.id, policyMember);  
                } 
            }
            
            if(acctId !=null && !acctId.isEmpty())
                caseAccountMap = new Map<Id,Account>([Select Id, General_Account__c,Contract_Protected_Data__c,Home_Office_Account__c From Account where Id IN : acctId ]);
            
            string productCodeMedical = GLOBAL_CONSTANT_HUM.productCodeMedicalGlobal;
            string productCodeDental = GLOBAL_CONSTANT_HUM.productCodeDentalGlobal;
            Map<ID, Schema.RecordTypeInfo> rtMap = Schema.SObjectType.Case.getRecordTypeInfosById();   
            
            for(Case caseRecord : lstCaseNewValues){
                string newAccountId, newPolicyMem, oldAccountId, oldPolicyMem ;
                boolean generalAccount;
                boolean homeAccount;
                string cpdAccount;
                string productCode;
                string recordTypeName;
                string interactingAboutType; 
                string oldRecordType;
                
                if(caseRecord.AccountId !=null)
                    newAccountId = caseRecord.AccountId;
                if(caseRecord.Policy_Member__c !=null)
                    newPolicyMem = caseRecord.Policy_Member__c;
                
                if(oldMap !=null && oldMap.containsKey(caseRecord.id)){
                    oldAccountId = oldMap.get(caseRecord.Id).AccountId;    
                    oldPolicyMem = oldMap.get(caseRecord.Id).Policy_Member__c;
                    oldRecordType = oldMap.get(caseRecord.Id).RecordTypeId;
                }
                
                if(caseRecord.RecordTypeId != null)
                    recordTypeName = rtMap.get(caseRecord.RecordTypeId).getName(); 
                
                if(String.isNotBlank(caseRecord.Interacting_About_Type__c))
                    interactingAboutType = caseRecord.Interacting_About_Type__c;
                //Handles when case get updated without policymember
                if((newPolicyMem != oldPolicyMem || newAccountId != oldAccountId || caseRecord.RecordTypeId != oldRecordType) && string.isBlank(caseRecord.Policy_Member__c))
                {
                    if(String.isNotBlank(caseRecord.AccountId))
                    {
                        if(recordTypeName == GLOBAL_CONSTANT_HUM.MEDICARE_CASE || recordTypeName == GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE)
                        {
                            if(interactingAboutType == GLOBAL_CONSTANT_HUM.MEMEBER_INT_ABT_TYPE || interactingAboutType == GLOBAL_CONSTANT_HUM.CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER)
                            {
                                caseRecord.General_Case__c = True;
                                caseRecord.HO_Dental_Case__c =  caseAccountMap.get(caseRecord.AccountId).Home_Office_Account__c;
                                caseRecord.HO_Medical_Case__c =  caseAccountMap.get(caseRecord.AccountId).Home_Office_Account__c;           
                                caseRecord.Contract_Protected_Case__c = caseAccountMap.get(caseRecord.AccountId).Contract_Protected_Data__c;
                            }
                        }                   
                        else
                        {
                            caseRecord.General_Case__c =  caseAccountMap.get(caseRecord.AccountId).General_Account__c;
                            caseRecord.HO_Dental_Case__c =  caseAccountMap.get(caseRecord.AccountId).Home_Office_Account__c;
                            caseRecord.HO_Medical_Case__c =  caseAccountMap.get(caseRecord.AccountId).Home_Office_Account__c;
                            caseRecord.Contract_Protected_Case__c = caseAccountMap.get(caseRecord.AccountId).Contract_Protected_Data__c;
                        }
                    }
                }  //Handles when case get updated with Account & policymember
                else if(String.isNotBlank(caseRecord.Policy_Member__c) && newPolicyMem != oldPolicyMem)
                {
                    if(casePolicyMemberMap !=null && casePolicyMemberMap.containsKey(caseRecord.Policy_Member__c)){
                        generalAccount = casePolicyMemberMap.get(caseRecord.Policy_Member__c).Policy__r.Group_Name__r.General_Account__c;
                        homeAccount = casePolicyMemberMap.get(caseRecord.Policy_Member__c).Policy__r.Group_Name__r.Home_Office_Account__c;
                        productCode = casePolicyMemberMap.get(caseRecord.Policy_Member__c).Policy__r.Product__r.ProductCode;
                        cpdAccount = casePolicyMemberMap.get(caseRecord.Policy_Member__c).Policy__r.Group_Name__r.Contract_Protected_Data__c;
                    }
                    if(generalAccount)
                    {
                        caseRecord.General_Case__c = True;
                        caseRecord.HO_Dental_Case__c = False;
                        caseRecord.HO_Medical_Case__c = False;
                        caseRecord.Contract_Protected_Case__c = '';  
                    }
                    else if(homeAccount)
                    {
                        caseRecord.General_Case__c = False;
                        caseRecord.HO_Dental_Case__c = False;
                        caseRecord.HO_Medical_Case__c = False;
                        caseRecord.Contract_Protected_Case__c = '';  
                        
                        if(String.isNotBlank(productCode) && productCodeMedical.Contains(productCode))
                            caseRecord.HO_Medical_Case__c = True;
                        
                        if(String.isNotBlank(productCode) && productCodeDental.Contains(productCode))
                            caseRecord.HO_Dental_Case__c = True;
                    } // sets to '1' on case Contract_Protected_Case__c field
                    else if(cpdAccount == '1' && HUMUtilityHelper.isCRMFunctionalityON('3782199'))
                    {
                    	caseRecord.Contract_Protected_Case__c = '1';
                        caseRecord.General_Case__c = False;
                        caseRecord.HO_Dental_Case__c = False;
                        caseRecord.HO_Medical_Case__c = False;
					}
                }
            }
        }
        catch (Exception e)
        {
             HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'caseGeneralHOGeneralSecurity');
        }
    }
    
    /**
    * Returns void
    * <p>
    * This method is used to update the owner queue and case owner fields of cases when reopening a case
    *
    * @param  List<Case> lstCaseNewValues
    * @param   Map<Id, Case> oldMap
    * @return  void
    * @see
    */
     public void updateCaseQueues(List<Case> lstCaseNewValues, Map<Id, Case> oldMap) 
     {
        User currentUser = [select current_queue__c,Profile.Name from User where id = :Userinfo.getUserId() LIMIT 1]; 
        String sCurrentQueue = currentUser.current_queue__c;
        String sUserProfileName = currentUser.Profile.Name;
        List<Work_Queue_Setup__c> oWQS = [select Service_Center__c,Department__c,Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Name = :sCurrentQueue AND IsActive__c = TRUE LIMIT 1];
        String sUserName = Userinfo.getName();
        String sOldStatus ='';
        String sNewStatus = '';
        MENROLLMENT_CHECK_HUM__c sBypassQueue = MENROLLMENT_CHECK_HUM__c.getInstance(sUserProfileName);
        Set<String> sBypassRecords = new Set<String>();
        Boolean bMIRPilotUser = UtilityHelper_H_AHT_HUM.isMIRPilotUser();
        if(sBypassQueue != NULL)
        {
            sBypassRecords.addAll(sBypassQueue.RecordTypes__c.split(':'));
        } 
        try
        {
             for(Case objNewCase : lstCaseNewValues) 
             {                 
                 if(!sBypassRecords.contains(Schema.sObjectType.Case.getRecordTypeInfosById().get(objNewCase.RecordTypeId).getName()))
                 {
                     Case objOldCase = oldMap.get(objNewCase.Id);
                     sOldStatus = objOldCase.status;
                     sNewStatus = objNewCase.status;
                     String sOldCaseOwnerId = objOldCase.OwnerId;
                     String sNewCaseOwnerId = objNewCase.OwnerId;   
                     Boolean bIsBetweenUser = (sOldCaseOwnerId.startsWithIgnoreCase(GLOBAL_CONSTANT_HUM.CASEMGMT_USERID_INITIAL_HUM) && sNewCaseOwnerId.startsWithIgnoreCase(GLOBAL_CONSTANT_HUM.CASEMGMT_USERID_INITIAL_HUM));
                     if((oldMap.get(objNewCase.Id).Department__c != objNewCase.Department__c) || (oldMap.get(objNewCase.Id).Topic__c != objNewCase.Topic__c)|| (bIsBetweenUser && objOldCase.OwnerId != objNewCase.OwnerId))
                     {
                         objNewCase.Previous_Case_Owner_Role__c = sUserProfileName; 
                         objNewCase.Previous_Owner_Queue__c = oldMap.get(objNewCase.Id).Owner_Queue__c;
                     }                
                     if((sOldStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) && !sNewStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM)) || (sOldStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) && !sNewStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM)))
                     {
                         if(objNewCase.Topic__c!=GLOBAL_CONSTANT_HUM.VantageReopenedCases)
                         {
                            objNewCase.Owner_Queue__c = sCurrentQueue;
                            objNewCase.Case_Owner__c = sUserName;
                           }
                         objNewCase.Case_Owner__c = sUserName;   
                         objNewCase.OwnerId = Userinfo.getUserId();
                         if(!oWQS.isEmpty() && objNewCase.Topic__c!=GLOBAL_CONSTANT_HUM.VantageReopenedCases)
                         {
                             objNewCase.Service_Center__c = oWQS[0].Service_Center__c;
                             objNewCase.Department__c = oWQS[0].Department__c;
                             if(oWQS[0].Classification_Type__c!=null)
                             objNewCase.Classification_Type__c = oWQS[0].Classification_Type__r.name;
                         }
                     }
                     else
                     {                           
                         CaseQueuesUpdation(sOldStatus,sNewStatus,objNewCase,oWQS,sCurrentQueue,sUserName,objOldCase);
                     }
                     
                     
                     if(bMIRPilotUser) {
                        
                        String sPreviousOwnerQueue = objOldCase.Owner_Queue__c;
                        String sNewOwnerQueue = objNewCase.Owner_Queue__c;
                        
                        if(String.isNotBlank(sPreviousOwnerQueue) && String.isNotBlank(sNewOwnerQueue) && sPreviousOwnerQueue != sNewOwnerQueue) {
                            objNewCase.Previous_Owner_Queue__c = sPreviousOwnerQueue;
                            
                        }
                        
                     } 
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'updateCaseQueues');
        }            
     }
    
    /**
    * CaseQueuesUpdation
    * <p>
    * This method is used to update the owner queue and case owner fields of cases when reopening a case
    * @param  sOldStatus            string
    * @param  sNewStatus            string
    * @param  objNewCase            Case
    * @param  oWQS                  List<Work_Queue_Setup__c>
    * @param  sCurrentQueue         string
    * @param  sUserName             string
    * @param  objOldCase            Case
    * @return void   
    */
     public void CaseQueuesUpdation(String sOldStatus,String sNewStatus,Case objNewCase,List<Work_Queue_Setup__c> oWQS,string sCurrentQueue,String sUserName,Case objOldCase) 
     {
 
                 if((objOldCase.Case_Owner__c == objOldCase.Owner_Queue__c) && objNewCase.Case_Owner__c == sUserName && objNewCase.Owner_Queue__c == objOldCase.Owner_Queue__c)
                 {
                     objNewCase.LastModifiedby_Queue__c = objOldCase.Owner_Queue__c;
                 }
                 else if((objOldCase.Case_Owner__c == objOldCase.Owner_Queue__c) && objNewCase.Case_Owner__c == sUserName && objNewCase.Owner_Queue__c != objOldCase.Owner_Queue__c)
                 {
                     objNewCase.LastModifiedby_Queue__c = objNewCase.Owner_Queue__c;
                 }
                 else if (sUserName != objOldCase.Case_Owner__c && (objNewCase.Case_Owner__c != sUserName))
                 {                
                      objNewCase.LastModifiedby_Queue__c = sCurrentQueue ;   
                 }                 
                 else if(sUserName == objOldCase.Case_Owner__c && !String.isEmpty(objOldCase.Case_Owner__c)) 
                 {
                      objNewCase.LastModifiedby_Queue__c = objOldCase.Owner_Queue__c;
                 }
      }
      
        /**
    *  getOmniEligibleList
    * <p>
    * This method Is used for initializing Map of Work Queue and Set of View which are Omni eligible
    * @return  Map<String,Set<String>>
    * @see
    */  
    public static Map<String,Set<String>> getOmniEligibleList()
    {  
        Map<String,Set<String>> mapQueueView = new Map<String,Set<String>>();
        
        List<Omni_Eligible_Queue_Views__mdt> lstOmniEligible = [Select QueueViewName__c, WorkQueueName__c from Omni_Eligible_Queue_Views__mdt];
        for(Omni_Eligible_Queue_Views__mdt omniQV : lstOmniEligible)
        {
            if(String.IsNotBlank(omniQV.WorkQueueName__c) && String.IsNotBlank(omniQV.QueueViewName__c))
            {
                if(mapQueueView !=null && mapQueueView.containsKey(omniQV.WorkQueueName__c.trim()))
                {
                    Set<String> sView = mapQueueView.get(omniQV.WorkQueueName__c.trim());
                    sView.add(omniQV.QueueViewName__c.trim());
                    mapQueueView.put(omniQV.WorkQueueName__c.trim(),sView); 
                }
                else
                {
                    Set<String> sView = new Set<String>();
                    sView.add(omniQV.QueueViewName__c.trim());
                    mapQueueView.put(omniQV.WorkQueueName__c.trim(),sView); 
                }
            }
        }
                          
        return mapQueueView;              
    }
    /**
    * This method used for updating case status and record type for case process for expedited.
    * @method  caseTransferToMedhok
    * @param   List<case> lstMyCases
    * @return  void
    * REQ - 392832 - Transfer of cases that are auto routed to the work queue and view
    */
    public void caseTransferToMedhok(List<case> lstMyCases)
    {
        if(Label.Case_Transfer_Service_Flag == '1')
        { 
            try
            {
                //To fetch all metadata records in Case_Transfer_Queue_and_View__mdt
                List<Case_Transfer_Queue_and_View__mdt> QueueViewData = new List<Case_Transfer_Queue_and_View__mdt>();
                if(HUMUtilityHelper.isCRMFunctionalityON('4957069')){
                    QueueViewData = Case_Transfer_Queue_and_View__mdt.getAll().values();    
                }else{
                    QueueViewData = [select id,MasterLabel,Case_View__c,Case_Work_Queue__c from Case_Transfer_Queue_and_View__mdt];    
                }
                list<case> lstCase = new list<Case>();
                for(Case caseObj : lstMyCases)
                {
                    if(caseObj.Status == GLOBAL_CONSTANT_HUM.CASESTATUS_HUM) 
                    {   
                      
                        Map<ID, Schema.RecordTypeInfo> rtMap = Schema.SObjectType.Case.getRecordTypeInfosById();
                        for (Case_Transfer_Queue_and_View__mdt qvData : QueueViewData)
                        {
                            if(qvData.Case_Work_Queue__c == caseObj.Owner_Queue__c && 
                               qvData.Case_View__c == caseObj.Work_Queue_View_Name__c)
                            {   
                                String currentRecordType = rtMap.get(caseObj.RecordTypeId).getName();
                                if(!currentRecordType.containsIgnoreCase('Closed'))
                                {   
                                     caseObj.Status = Label.External_Transfer_in_Progress;
                                     String closedRecordType = 'Closed ' + rtMap.get(caseObj.RecordTypeId).getName();
                                     Id recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(closedRecordType).getRecordTypeId();
                                     caseObj.Recordtypeid = String.valueOf(recordTypeId);
                                     lstCase.add(caseObj);
                                }
                            }
                        }
                    }   
                }
                if(lstCase!=NULL && !lstCase.isEmpty()){
                	list<Database.upsertResult> upsertResult = Database.upsert(lstCase, true);
                }
            }
            catch (Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'caseTransferToMedhok');
            }
        }
    }
    
    /**
    * caseCloseAndReopenUpdates
    * <p>
    * This method was created to optimize the Case Workflow rules and move them into apex code.
    * The WorkFlow rules related to recordtype, reopen flag and reopen date for Case Closure and reopen scenario are moved into this apex method.
    * @param  List<Case> lstNewCases
    * @param   Map<Id, Case> mapOldCases
    * @return  void
    */    
    public void caseCloseAndReopenUpdates(list<Case> lstNewCases, Map<Id, Case> mapOldCases)
    {       
        Schema.DescribeSObjectResult dSResult = Schema.SObjectType.Case;
        map<Id,Schema.RecordTypeInfo> rtMapById = dSResult.getRecordTypeInfosById();    //map of Case Recordtype details by recordTypeId
        map<String,Schema.RecordTypeInfo> rtMapByName = dSResult.getRecordTypeInfosByName();    //map of Case Recordtype details by recordTypeName
            
        if(Trigger.isInsert)
        {
            if(lstNewCases!=null && !lstNewCases.isEmpty())
            {                                  
                for(Case caseObj : lstNewCases)
                {                                   
                    string sCurrentRecTypeName = rtMapById.get(caseObj.recordTypeId).Name;                  
                    //Check if the Case is Closed
                    if(String.isNotBlank(caseObj.status) && (caseObj.status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) || caseObj.status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM)))                 
                    {                                   
                        Id newRecordTypeID = getClosedCaseRecordType(rtMapByName, sCurrentRecTypeName);
                        if(newRecordTypeID!=null)   caseObj.recordTypeId = newRecordTypeID;                                                                     
                    }
                }
            }
        }
        else if(Trigger.isUpdate)
        {
            if(lstNewCases!=null && !lstNewCases.isEmpty()&& mapOldCases!=null && !mapOldCases.isEmpty())
            {                                  
                for(Case caseObj : lstNewCases)
                {                                   
                    if(String.isNotBlank(caseObj.status) && String.isNotBlank(mapOldCases.get(caseObj.Id).status))                 
                    {                                   
                        string sCurrentRecTypeName = rtMapById.get(caseObj.recordTypeId).Name;
                        
                        //Check if the Case is Closed                        
                        if(caseObj.status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) || caseObj.status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM))
                        {                       
                            Id newRecordTypeID = getClosedCaseRecordType(rtMapByName, sCurrentRecTypeName);
                            if(newRecordTypeID!=null)   caseObj.recordTypeId = newRecordTypeID;                                             
                        }
                        //Check if the Case is Reopened
                        else if(!caseObj.status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) && !caseObj.status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) && (mapOldCases.get(caseObj.Id).status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) || mapOldCases.get(caseObj.Id).status.equals(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM)))
                        {
                            caseObj.Reopened__c = true; //set the Reopened flag as true
                            caseObj.Re_Open_Case_Date__c = Date.today();
                            Id newRecordTypeID = getReopenedCaseRecordType(rtMapByName, sCurrentRecTypeName);
                            if(newRecordTypeID!=null)   caseObj.recordTypeId = newRecordTypeID;                                                 
                        }
                    }
                }
            }
        }
    }
    
    /**
    * getClosedCaseRecordType
    * <p>
    * This method is to identify the new recordtype for Case Close scenario. 
    * @param  rtMapByName (map of recordtypes by Name) and sCurrentRecTypeName (current recordtype name)
    * @returntype Id (new recordTypeId)
    */
    private Id getClosedCaseRecordType(map<String,Schema.RecordTypeInfo> rtMapByName, String sCurrentRecTypeName)
    {
        id newRecordTypeID = null;
        if(String.isNotBlank(sCurrentRecTypeName) && rtMapByName!=null && !rtMapByName.isEmpty())
        {
            if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM))      //Member Case -> Closed Member Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.MEDICARE_CASE))      //Medicare Case -> Closed Medicare Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNCASE_HUM))    //Unknown Case -> Closed Unknown Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HUM_CLOSEDUNKNOWNCASE).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HP_MEMBERCASE_HUM))          //HP Member Case -> Closed HP Member Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CLOSEDHP_MEMBERCASE_HUM).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HP_UNKNOWNCASE_HUM))         //HP Unknown Case -> Closed HP Unknown Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CLOSED_HP_UNKNOWNCASE_HUM).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_PROVIDERCASE_HUM))   //Provider Case -> Closed Provider Case 
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HUM_CLOSEDPROVIDERCASE).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_GROUPCASE_HUM))          //Group Case -> Closed Group Case   
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HUM_CLOSEDGROUPCASE).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_AGENTBROKERCASE_HUM))    //Agent/Broker Case -> Closed Agent/Broker Case 
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HUM_CLOSEDAGENTBROKERCASE).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HP_PROVIDERCASE_HUM))                //HP Provider Case -> Closed HP Provider Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CLOSED_HP_PROVIDERCASE_HUM).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HP_GROUPCASE_HUM))           //HP Group Case -> Closed HP Group Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CLOSED_HP_GROUPCASE_HUM).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HP_AGENTBROKERCASE_HUM))     // HP Agent/Broker Case -> Closed HP Agent/Broker Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CLOSED_HP_AGENTBROKERCASE_HUM).recordTypeId;
        }
        return newRecordTypeID;     
    }
    
    /**
    * getReopenedCaseRecordType
    * <p>
    * This method is to identify the new recordtype for Case Reopen scenario. 
    * @param  rtMapByName (map of recordtypes by Name) and sCurrentRecTypeName (current recordtype name)
    * @returntype Id (new recordTypeId)
    */
    private Id getReopenedCaseRecordType(map<String,Schema.RecordTypeInfo> rtMapByName, String sCurrentRecTypeName)
    {
        id newRecordTypeID = null;
        if(String.isNotBlank(sCurrentRecTypeName) && rtMapByName!=null && !rtMapByName.isEmpty())
        {
            if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE))        //Closed Member Case -> Member Case 
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM).recordTypeId;                           
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE))   //Closed Medicare Case -> Medicare Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).recordTypeId;                            
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HUM_CLOSEDUNKNOWNCASE))  //Closed Unknown Case -> Unknown Case   
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_UNKNOWNCASE_HUM).recordTypeId;                          
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CLOSEDHP_MEMBERCASE_HUM))    //Closed HP Member Case -> HP Member Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HP_MEMBERCASE_HUM).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CLOSED_HP_UNKNOWNCASE_HUM))  //Closed HP Unknown Case -> HP Unknown Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HP_UNKNOWNCASE_HUM).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HUM_CLOSEDPROVIDERCASE))     //Closed Provider Case -> Provider Case         
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_PROVIDERCASE_HUM).recordTypeId;                         
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HUM_CLOSEDGROUPCASE))        //Closed Group Case -> Group Case   
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_GROUPCASE_HUM).recordTypeId;                            
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.HUM_CLOSEDAGENTBROKERCASE))      //Closed Agent/Broker Case -> Agent/Broker Case 
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_AGENTBROKERCASE_HUM).recordTypeId;                          
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CLOSED_HP_PROVIDERCASE_HUM))     //Closed HP Provider Case -> HP Provider Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HP_PROVIDERCASE_HUM).recordTypeId;                      
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CLOSED_HP_GROUPCASE_HUM))        //Closed HP Group Case -> HP Group Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HP_GROUPCASE_HUM).recordTypeId;
            else if(sCurrentRecTypeName.equals(GLOBAL_CONSTANT_HUM.CLOSED_HP_AGENTBROKERCASE_HUM))  //Closed HP Agent/Broker Case -> HP Agent/Broker Case
                newRecordTypeID =   rtMapByName.get(GLOBAL_CONSTANT_HUM.HP_AGENTBROKERCASE_HUM).recordTypeId;
        }
        return newRecordTypeID;     
    }
    /**
     * This method is used to update the owner queue and case owner fields of cases when reopening a case by API user. 
     * Expected value from web service - Owner Queue, Topic, Status(In Progress)
     * @param List<Case>
     * @param Map<Id, Case>
     * @return void
     */
    public void updateCaseQueuesByAPIUser(List<Case> lstCaseNewValues, Map<Id, Case> oldMap) {
        User currentUser = [select id, current_queue__c,Profile.Name from User where id = :Userinfo.getUserId() LIMIT 1];
        String sUserProfileName = currentUser.Profile.Name;
        MENROLLMENT_CHECK_HUM__c sBypassQueue = MENROLLMENT_CHECK_HUM__c.getInstance(sUserProfileName);
        Set<String> setBypassRecords = new Set<String>();
        if(sBypassQueue != NULL) {
            setBypassRecords.addAll(sBypassQueue.RecordTypes__c.split(':'));
        } 
        try {
            Map<String, HUMUpdateCaseHelper.OwnerQueueDetail> mapOQD = getOwnerQueueDetails(lstCaseNewValues);
            for(Case objNewCase : lstCaseNewValues) {
                HUMUpdateCaseHelper.OwnerQueueDetail oOQD = mapOQD.get(objNewCase.Owner_Queue__c);
                Work_Queue_Setup__c oWorkQueueSetup = oOQD.oWQS;
                if(!setBypassRecords.contains(Schema.sObjectType.Case.getRecordTypeInfosById().get(objNewCase.RecordTypeId).getName())) {
                    Case objOldCase = oldMap.get(objNewCase.Id);
                    String sOldStatus = objOldCase.status;
                    String sNewStatus = objNewCase.status;
                    if((sOldStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) && !sNewStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM)) 
                            || (sOldStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) && !sNewStatus.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM))) 
                    {
                        //Change Case Owner to Queue When the Case is Reopened and Case Owner is not updated by service
                        if(objNewCase.Case_Owner__c.equalsIgnoreCase(objOldCase.Case_Owner__c))
                        {
                            objNewCase.OwnerId = (oOQD != null && String.isNotBlank(oOQD.sGroupId15)) ? oOQD.sGroupId15 : Userinfo.getUserId();
                        
                            if(String.isNotBlank(objNewCase.Owner_Queue__c)) {
                                objNewCase.Case_Owner__c = objNewCase.Owner_Queue__c;
                            }
                        }
                        if(oWorkQueueSetup != null) {
                            objNewCase.Department__c = oWorkQueueSetup.Department__c;
                            objNewCase.Service_Center__c = oWorkQueueSetup.Service_Center__c;    
                            if(String.isNotBlank(oWorkQueueSetup.Classification_Type__c)) {
                                objNewCase.Classification_Type__c = oWorkQueueSetup.Classification_Type__r.name;
                            }
                        }                        
                        // track old case value
                        objNewCase.LastModifiedby_Queue__c = currentUser.current_queue__c;
                        objNewCase.Previous_Owner_Queue__c = objOldCase.Owner_Queue__c;
                        objNewCase.Previous_Work_Queue_View__c = objOldCase.Work_Queue_View_Name__c;
                        objNewCase.Previous_Case_Owner_Role__c = sUserProfileName; 
                        String sOldOwnerId = objOldCase.OwnerId;
                        if(sOldOwnerId.left(3).equals('005')) {
                            objNewCase.Transferred_From__c = objOldCase.OwnerId;
                        }
                    } else {
                        bSwitch_2027311 =  HUMUtilityHelper.isCRMFunctionalityON('2027311');
                        if(bSwitch_2027311)
                        {
                            //Change Case Owner to Queue When the Case is in Progress or changed to closed status, Queue is updated in service and Case Owner is not updated by service
                            if(objNewCase.Case_Owner__c.equalsIgnoreCase(objOldCase.Case_Owner__c) && 
                                String.isNotBlank(objNewCase.Owner_Queue__c) && !objNewCase.Owner_Queue__c.equalsIgnoreCase(objOldCase.Owner_Queue__c))
                            {
                                objNewCase.OwnerId = (oOQD != null && String.isNotBlank(oOQD.sGroupId15)) ? oOQD.sGroupId15 : Userinfo.getUserId();
                                objNewCase.Case_Owner__c = objNewCase.Owner_Queue__c;
                            }
                            if(oWorkQueueSetup != null) {
                                objNewCase.Department__c = oWorkQueueSetup.Department__c;
                                objNewCase.Service_Center__c = oWorkQueueSetup.Service_Center__c;    
                                if(String.isNotBlank(oWorkQueueSetup.Classification_Type__c)) {
                                    objNewCase.Classification_Type__c = oWorkQueueSetup.Classification_Type__r.name;
                                }
                            }
                        }
                        CaseQueuesUpdation(sOldStatus,sNewStatus,objNewCase,new List<Work_Queue_Setup__c>{oWorkQueueSetup},currentUser.current_queue__c,Userinfo.getName(),objOldCase);
                    }
                }
            }
        } catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseHelper', 'updateCaseQueuesByAPIUser');
        }
    }
    /**
     * Get Work Queue Setup details for list of cases
     * @param List<Case>
     * @return Map<String, Work_Queue_Setup__c>
     */
    private Map<String, HUMUpdateCaseHelper.OwnerQueueDetail> getOwnerQueueDetails(List<Case> lstCaseNewValues) {
        Map<String, HUMUpdateCaseHelper.OwnerQueueDetail> mapOQD = new Map<String, HUMUpdateCaseHelper.OwnerQueueDetail>();
        Map<String, Group> mapGroup = new Map<String, Group>();
        Set<String> setQwnerQueue = new Set<String>();
        for(Case oCase : lstCaseNewValues) {
            if(String.isNotBlank(oCase.Owner_Queue__c)) {
                setQwnerQueue.add(oCase.Owner_Queue__c);
            }
        }
        for(Group oGroup : [select id, Name from Group where Name in: setQwnerQueue and Type = 'Queue']) {
            mapGroup.put(oGroup.Name, oGroup);
        }
        for(Work_Queue_Setup__c oWQS : [SELECT Name, Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.Name 
        from Work_Queue_Setup__c where Name in : setQwnerQueue AND IsActive__c = TRUE]) {
            mapOQD.put(oWQS.Name, new HUMUpdateCaseHelper.OwnerQueueDetail(oWQS, mapGroup.get(oWQS.Name)));
        }
        return mapOQD;
    }
    /**
     * Wrapper class to store group id and work queue setup detail
     */
    public class OwnerQueueDetail {
        public Work_Queue_Setup__c oWQS{get; set;}
        public String sGroupId15{get; set;}
        public OwnerQueueDetail(Work_Queue_Setup__c oWQS, Group oGroup) {
            this.oWQS = oWQS;            
            this.sGroupId15 = '';
            if(oGroup != null) {
                String sIdValue = oGroup.Id;
                this.sGroupId15 = sIdValue.left(15);
            }
        }
    }
    
    
    //If the logged in user's profile is 'ETL API Access' and logged in user id is matching with id present in 'CaseProcessBuilderSettings_HUM__c' custom setting
    //Then populateviename method will be executed in async mode this is to fix apex CPU time limit issue for ETL batch jobs
    @future
    public static void populateViewNameAsync(String sCaseData,Boolean isTrigger,Set<String> setQueueNames,String sProfileName)
    {
        List<Case> lstNewCases = (List<Case>) JSON.deserialize(sCaseData,List<Case>.class);
        populateViewName(lstNewCases ,isTrigger,setQueueNames,sProfileName);
    }

    /**
     * This method is used to skip the cases already in Omni Queue and select remaining cases from Re-evaluation
     * @param List<Case>
     * @return List<Case>
     */
    @TestVisible
    private static List<Case> executeOmniQueueReEvaluationLogic(List<Case> lstCases)
    {
        list<Case> lstOmniCasesToEvaluate = new list<Case>();
        map<Id, Group> mapcasgrp = new map<Id, Group>();
        set<Id> setOwnerIds = new set<Id>();
        for(Case caseObj : lstCases)
        {
            if(caseObj.OwnerId != null)
            {
                setOwnerIds.add(caseObj.OwnerId);
            }
        }
        //select all the Omni Queues
        mapcasgrp = new map<Id, Group>([SELECT Id, QueueRoutingConfigId FROM Group WHERE Id=:setOwnerIds AND QueueRoutingConfigId!=null]);                          
        if(mapcasgrp != null && !mapcasgrp.isEmpty())
        {
            for(Case caseObj : lstCases)
            {
                //Skip the cases already in Omni Queue and select remaining cases from Re-evaluation
                if(caseObj.OwnerId != null && !(mapcasgrp.containsKey(caseObj.OwnerId)))                                    
                {
                    lstOmniCasesToEvaluate.add(caseObj);
                }
            }
        }
        else    //Select all the cases not in Omni Queue
        {
            lstOmniCasesToEvaluate.addAll(lstCases); 
        } 
        return lstOmniCasesToEvaluate;
    }

     /**
     * This method is used to call the Helper class method to evaluate Omni Case Assignment Rule in async mode
     * @param List<Case>
     * @return List<Case>
     */
    @TestVisible
    private static List<Case> executeOmniEligibleCriteriaAsync(List<Case> lstCases)
    {                
        List<Case> lstOmniCase = new List<Case>();
        if(lstCases != null && !lstCases.isEmpty())
        {
            for(Case oCase: lstCases)
            {
                if(oCase.Omni_Eligible__c && !oCase.Status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM) && !oCase.Status.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) && oCase.Owner_Queue__c.equalsIgnoreCase(oCase.Case_Owner__c))
                {                
                        lstOmniCase.add(oCase);                             
                }
            }
            if(lstOmniCase != null && !lstOmniCase.isEmpty())
            {
                OmniCaseAssignmentRuleEvaluation_H_HUM.EvaluateOmniCaseAssignmentRule(lstOmniCase, false);
            }                              
        }
        return lstOmniCase;
    }
}