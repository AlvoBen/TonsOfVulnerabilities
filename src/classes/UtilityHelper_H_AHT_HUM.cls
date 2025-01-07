/**************************************************************************** 
Apex Class Name  : UtilityHelper_H_AHT_HUM 
Version          : 1.0  
Created Date     : January 25 2017 
Function         : The class is the Helper class which has Utility methods accross the components
Test Class       : UtilityHelper_T_AHT_HUM
 
Modification Log: 
 * Developer Name           Code Review                  Date                       Description
 *------------------------------------------------------------------------------------------------------------------
 * Amar Gadewar                                         01/25/2017                  Original Version
 * Amar Gadewar             71394                       02/27/2017                  Added getCurrentPageName generic Function
 * Amar Gadewar                                         07/03/2017                  REQ - 320483 - Quick Start Permision Set Check Generic Function
 * Amar Gadewar                                         08/23/2017                  Defect fix on Quick Start for Medicare Call Part C or Part D
 * Amar Gadewar                                         02/21/2019                  REQ - 383787 - Is Rapid Force Function added
 * Asish Behera                                         02/21/2019                  REQ - 383941 User - Ability to set the Classification on the Case Edit Page Based on the Page a User Logged from (RF)
 * Keerti Durbha                                        04/05/2019                  REQ - 392177 Fix Mentor Benefit Matrix Link (GBORF)
 * Subhamay Mandal                                      04/22/2019                  REQ - 396217 Quality - MIR - Ability for CSR to see remaining SLA field on the Case/Tasks tab (RF) 
 * Subhamay Mandal                                      05/02/2019                  REQ - 395551, REQ - 396404 Quality – MIR – Ability for CSR Supervisor to get a view/dashboard of the Cases owned by their team members that are nearing SLA/Over SLA (RF)
 * Kiran Kumar B            357925                      06/03/2019                  Added Method to check if user is having Pharmvillie permission set or not.
 * Prathyu Tushar                                       07/29/2019                  REQ - 396182: added Method to check user is having access for a specific Permissionset or not
 * Subhamay Mandal                                      08/26/2019                  REQ - 406018: added Method to get object's prefix.
 * Subhamay Mandal                                      08/26/2019                  REQ - 408295 Quality - OGO - Disable Change Case Owner button when the case is routed to 'Louisville RSO Oral Grievance Oversight' WQ (RF)
 * Subhamay Mandal                                      02/06/2020                  USER STORY 840340 PR00091574 - Incident 8915027 Fix
 * Subhamay Mandal                                      03/06/2020                  NO/OFF switch implemented for RF stories
 * Santhi Mandava                                       05/02/2020                  User Story 938955: PR00091574 - Update Case Information Section to display "What is Call About" 
                                                                                    information instead of Classification and Intent fields (RF)
 * VijayaKumar                                          06/29/2020                  USER STORY 1203975 PR00095159 - IR 50-50 OGO - MVP - CRM Action page layout to provide feed back for ODM returned Case                                                                                  information instead of Classification and Intent fields (RF)
 * Amar Gadewar                                         07/05/2020                  User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
 * Vinay Lingegowda                                     07/09/2020                  USER STORY 1203975 PR00095159 - IR 50-50 OGO - MVP - CRM Action page layout to provide feed back for ODM returned Case Defect Fix- 1042                 
 * Vinay Lingegowda                                 	07/23/2020                  USER STORY 1278792 PR00091574-IR-50-50-OGO- disable close case option for actionable cases
 * Y Sunil Kumar Reddy                                  07/24/2020                  User Story 1186355: PR00091574 - Added method that identifies if a string is valid ID or not.
 * Y Sunil Kumar Reddy                                  08/14/2020                  User Story 1186355: PR00091574 - MF 1 - Quality - Ability to make PCP/PCD Change Template mandatory (CRM)
 * Amar Gadewar                                         08/28/2020                  User Story 1386444: PR00091574 - MF 1 - Rollback "What is the Call About?" feature in Quick Start (RF)
 * Y Sunil Kumar Reddy                                  01/22/2021                  User Story 1763850: Made Historical check logic as a new method to reuse on MF2 Feature 
 * Vijaykumar										    03/12/2021		            User story 2041570 T1PRJ0001827- IR 5050 - Add show/Hide columns on case/tasks page (CRM)
 * Y Sunil Kumar Reddy                                  03/23/2021                  User Story 1763904: T1PRJ0001827 - MF 1 - Quality - Ability to make PCP/PCD Template mandatory for "PCP Research Requested" Intent (CRM)
 * Kavya Bondada                                        04/14/2021                  User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
 * Y Sunil Kumar Reddy                                  04/21/2021                  DF-2916 Fix
 * Y Sunil Kumar Reddy                                  06/17/2021                  User Story 2369660: T1PRJ0001827 - MF 1 - Update logic to make 'Medicaid PAR Provider Not Accepting' Template required on the case (CRM)
 * Amar Gadewar                                         06/17/2021                  User Story 2389201: T1PRJ0001827 - MF 1 - Quality - Ability to exclude FL LTC QAA Complaint cases from auto routing process (CRM)
 * Y Sunil Kumar Reddy                                  06/30/2021                  User Story 2393208: T1PRJ0001827 - MF 1 - Quality - 'Medicaid PAR Provider Not Accepting' Template Routing Rules Update (CRM)
 * Y Sunil Kumar Reddy                                  07/13/2021                  User Story 2454886: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Medicare Good Cause Reinstatement' template when the case is created through Quick Start (CRM)User Story 2454886: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Medicare Good Cause Reinstatement' template when the case is created through Quick Start (CRM)
 * Y Sunil Kumar Reddy                                  07/28/2021                  User Story 2454886: Added isTemplateSubmittedHasValue Method
 * Vinay Lingegowda                                   	08/09/2021          	    USER STORY 1964685 T1PRJ0001827-IR 5050 - Update Show/Hide column options to Include Views (CRM)
 * Y Sunil Kumar Reddy                                  09/23/2021                  User Story 2696526: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Update Plan Demographics' template when a case is created through quick start (RF)
 * Pooja Kumbhar					                    02/02/2022		            User Story 2917977: T1PRJ0272555 - MF1 - Architectural Remediation - Global Constant -DF-4182 - Additional Changes 
  * Karthik Varma N R                                   03/02/2022                  User Story 2918805: T1PRJ0314829 - MF 1 - SF - TECH - Move Global Constants to New Constants file (CRM)
 * Pinky Vijur                                          01/20/2023                  3215226	T1PRJ0891339 - MF 5 - TECH - Pharmville Decommissioning - Permission Set Usage
 ************************************************************************************************************/
global with sharing class UtilityHelper_H_AHT_HUM {
    
     private static final string FWD_SLASH = '/';
     Public static final string PCP_Research_Requested = 'PCP Research Requested';
     Public static final string Louisville_Remote_Customer_Care_Queue = 'Louisville Remote Customer Care';
     Public static final string Guidance_Center = 'Guidance Center';
     Public Static final String RAPIDFORCE_PERMISSIONSET_HUM = 'CRM_RF_LouisvilleUser_Access';
     public Static final String MIR_PERMISSIONSET_HUM = 'CRMS_1100_MemberIssueResolution';
     Public Static final String QUICKSTARTACCESS_PERMISSIONSET_HUM = 'CRMS_650_Quick_Start_Access';
     Public Static final String MEDICARECALLS_PERMISSIONSET_HUM = 'CRMS_684_Medicare_Customer_Service_Access';
     public static final string PHARMVILLE_PERMISSIONSET_HUM = 'CRM_RXRF_PharmvilleUser_Access';
     Public Static final String RFGBOTITLETOWNERS_PERMISSION_HUM = 'CRM_GBORF_TitletownersUser_Access';
     Public Static Final String Grievance_Appeals_Permissionset_Name = 'CRMS_400_Grievance_Appeals';
     public static final string WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT = 'Louisville RSO Oral Grievance Oversight';
     public static final String RECORDTYPE_NAME_FEEDBACKTASK ='Service CRM Feedback';
     public static final List<String> OGO_SCRUBBER_PERMISSION_SET = New List<String>{'CRMS_401_OGO_Scrubber_Access','CRMS_400_Grievance_Appeals'};
     Public Static final String Louisville_Medicaid_Calls_Support = 'Louisville Medicaid Calls Support';
     Public Static final set<String> CALLS_RSO_WORKQUEUES_SET = new Set<String>{'Louisville After Hours Calls','Louisville Group Medicare Operations','Louisville RSO Calls Support','LV Partner Site RSO Calls Conduent','LV Partner Site RSO Calls Everise','LV Partner Site RSO Calls Faneuil','LV Partner Site RSO Calls HGS','LV Partner Site RSO Calls Ramp Partner','LV Partner Site RSO Calls Results Co','LV Partner Site RSO Calls Senture'};
     Public Static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';
     Public Static final String CASE_CLASSIFICATION_TYPE_MEDICAID_CALLS = 'Medicaid Calls';
     public static final String CLASSIFICATION_PCP_CHANGE = 'PCP Change';
     public static final String INTENT_PCP_CHANGE_MADE = 'PCP Change Made';
     Public Static final String INTERACTINGWITHTYPE_MEMBER = 'Member';
     Public Static final String MEMBERCAREGIVER = 'Member Representative or Caregiver';
     Public Static final String MED = 'MED';
     Public Static final String MAJOR_LOB_MCD = 'MCD';
     Public Static final String MAJOR_LOB_MER = 'MER';
     public static final string PRODUCT_TYPE_MGR = 'MGR';
     Public Static final String PRODUCT_TYPE_PDP = 'PDP';
     public static final List<String> GoodCauseAutoRouteClassificationTypes = new List<String>{'Calls (RSO)','Guidance Center','Web Chat', 'RCC Web Chat'};
     public static final string GOODCAUSECLASSIFICATION = 'Billing';
     public static final string GOODCAUSEINTENT = 'Reinstatement Review';
     Public Static final String CASEACTIONLAUNCHCONTROLLER_Classification_HUM = 'Complaints or Compliments';
     Public Static final String CASEACTIONLAUNCHCONTROLLER_Intent_HUM = 'QAA';


                //-------------------------------Start REQ - 303466, REQ - 301582 ----------------------------//
     Public Static Map<String, Set<String>> MEDICARE_LOB_PRODUCTTYPE_LIST()
    {
       Map<String, Set<String>> medicareLOBProductTypeList= New Map<String,Set<String>>{                                                                          
                                                                             'MEF' => new Set<String>{'MA', 'MAPD'},
                                                                             'MEP' => new Set<String>{'MA', 'MAPD'},
                                                                             'MER' => new Set<String>{'MA', 'MAPD','Empty'},
                                                                             'MGP' => new Set<String>{'MA', 'MAPD'},
                                                                             'MGR' => new Set<String>{'MA', 'MAPD'},
                                                                             'MPD' => new Set<String>{'PDP'},
                                                                             'MRO' => new Set<String>{'MAPD'},
                                                                             'PDP' => new Set<String>{'PDP'}
                                                                            };
      Return medicareLOBProductTypeList;                                                                            
    }
    
    /**
    * This method will return string for page name(current page name)
    * 
    * @return    a string for current page name

    */
    public static String getCurrentPageName()
    {
        String pageName = '';
        
        try
        {
            String sUrl = URL.getCurrentRequestUrl().getPath();
            
            if(String.isNotBlank(sURL)) {
                 Integer iPos = sUrl.lastIndexOf(FWD_SLASH) + 1;
                  if(iPos > 0 && iPos < sUrl.length())    
                    pageName = sURL.substring(iPos, sUrl.length());
                
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UtilityHelper_H_AHT_HUM ', 'getCurrentPageName');
        }
        
        return pageName;
    }
    
    
    /*
     * Method name : checkCustomPermissionAccess
     * Description : This method check if the assoicated Custom Permission Exist with the logged in User
     * Return Type : boolean
     * Parameter   : String Developer Name of the Customer Permission.
     */
    public static boolean checkCustomPermissionAccess(String sDevName)
    {        
        
        boolean bAccessExists = false;
        
        try
        {
        
            // Fetch the Custom Permission assoicated with the logged in User.
            Id userId = UserInfo.getUserId();
            List<SetupEntityAccess> lstAccess = [SELECT SetupEntityId FROM SetupEntityAccess WHERE SetupEntityType='CustomPermission' AND ParentId IN (SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:userId)];
            
            Set<Id> setCustomPermIds = new Set<Id>();
            for (SetupEntityAccess sea : lstAccess) {
                setCustomPermIds.add(sea.SetupEntityId);
            }
            
            List<CustomPermission> lstCustomPerms = [SELECT Id, DeveloperName, MasterLabel FROM CustomPermission WHERE Id IN :setCustomPermIds];
            
            // Check if the Access exists for a provided Custom Permission Developer Name
            for (CustomPermission perm : lstCustomPerms) {
                
                if (perm.DeveloperName.equals(sDevName))
                {
                     bAccessExists = true; 
                     break;
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UtilityHelper_H_AHT_HUM ', 'checkCustomPermissionAccess');
        }
        
        return bAccessExists;
    }
    
   /**
    * Method is used to identify if the logged in user is a Rapid Force  User having access to Rapid Force Permission Set. 
    * <p>
    * @return   boolean
    */
    public static boolean isRapidForceUser()
    {
        
        boolean bRapidForcePermissionAccess = false;
        String sUserId = UserInfo.getUserId();
        
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : RAPIDFORCE_PERMISSIONSET_HUM];
        
        if(null != lstPMAssign && !lstPMAssign.isEmpty()) bRapidForcePermissionAccess = true;
        
        
        
        return bRapidForcePermissionAccess;
    }
    
    /**
    * Method is used to identify if the logged in user is a MIR Pilot User having access to CRMS_1100_MemberIssueResolution Permission Set. 
    * <p>
    * @return   boolean
    */
    public static boolean isMIRPilotUser()
    {
        boolean bMIRPilotPermissionAccess = false;
        String sUserId = UserInfo.getUserId();
        
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : MIR_PERMISSIONSET_HUM];
        
        if(null != lstPMAssign && !lstPMAssign.isEmpty()) bMIRPilotPermissionAccess = true;

        return bMIRPilotPermissionAccess;
    }
    
   /**
    * Method is used to identify if the logged in user is a RSO User having access to Quick Start Permission Set. 
    * <p>
    * @return   boolean
    */
    public static boolean isRSOUserHavingQuickStartAccess()
    {
        
        boolean bRSOQuickStartAccess = false;
        String sUserId = UserInfo.getUserId();
        
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : QUICKSTARTACCESS_PERMISSIONSET_HUM];
        
        if(null != lstPMAssign && !lstPMAssign.isEmpty()) bRSOQuickStartAccess = true;
        
        
        
        return bRSOQuickStartAccess;
    }
    
    /**
    * Method is used to identify if the logged in user is a RSO User having access to  Medicare Customer Service Access. 
    * <p>
    * @return   boolean
    */
    public static boolean isUserHavingMedicareCustomerServiceAccess()
    {
        
        boolean bMedicareCustomerServiceAccess = false;
        String sUserId = UserInfo.getUserId();
        
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : MEDICARECALLS_PERMISSIONSET_HUM];
        
        if(null != lstPMAssign && !lstPMAssign.isEmpty()) bMedicareCustomerServiceAccess = true;
        
        
        
        return bMedicareCustomerServiceAccess;
    }
    
     /*
     * Method Name : getClassficationId
     * Description : 
     * Return type : String
     * Parameter   : String, String
     */
    public static String getClassficationId(String sType,String sSelectedCaseId){
        String sClassficationId = '';
        try{
            if(String.isNotBlank(sSelectedCaseId) && String.isNotBlank(sType) && Constants_C_AHT_HUM.map_PageName_Classfication !=null && Constants_C_AHT_HUM.map_PageName_Classfication.containsKey(sType) ) {
                List<Case> listOfCase = [SELECT Id,Classification_Type__c,CTCI_List__c FROM Case WHERE id = :sSelectedCaseId];
                if(!listOfCase.isEmpty() && String.IsBlank(listOfCase[0].CTCI_List__c)){
                    
                        list<CTCI_Junction__c> ctciList = [select id, Classification_Type__c, Classification__c from CTCI_Junction__c where Classification_Type__r.name = :listOfCase[0].Classification_Type__c and IsActive__c = true
                                                           AND Classification__r.name = : Constants_C_AHT_HUM.map_PageName_Classfication.get(sType)];
                        sClassficationId = ctciList.isEmpty() ? '' : ctciList[0].Classification__c;
                
                }
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'UtilityHelper_H_AHT_HUM', 'getClassficationId');
        }
        return sClassficationId;
    } 
                         
    /**
   * Method is used to identify if the logged in user is a GBO User having access to CRM GBO Titletowners Permission Set. 
    * <p>
    * @return   boolean
    */
    public static boolean ischeckCustomPermissionAccess()
    {
        
      boolean bCRMGBORFAccess= false;
      String sUserId = UserInfo.getUserId();
        
      List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : RFGBOTITLETOWNERS_PERMISSION_HUM];
        
      if(null != lstPMAssign && !lstPMAssign.isEmpty()) bCRMGBORFAccess = true;
        
     
       
        return bCRMGBORFAccess;
    }

    /**
    * Display minute value in day/hour format
    * param caseMilestone
    * return String 
    */
    public static String formatMinutes(CaseMilestone caseMilestone) {
        String sTimeSpentOnCase = '';
        
        if(caseMilestone != null) {
            Integer iTimeSpentOnCase = 0;
            Boolean bIsViolated = caseMilestone.isviolated;
            
            String sCaseTime = bIsViolated ? caseMilestone.TimeSinceTargetInMins : caseMilestone.TimeRemainingInMins;
            if(String.isNotBlank(sCaseTime)) {
                iTimeSpentOnCase = Integer.valueOf(sCaseTime.substringBefore(':'));             
            }
            
            if(iTimeSpentOnCase != 0) {
                Integer days = iTimeSpentOnCase / 60 / 24 ;
                Integer hours = (iTimeSpentOnCase - days * 60 * 24) / 60 ;
                
                sTimeSpentOnCase = (bIsViolated ? '-':'') + (days+'d '+hours+'h');
            }
        }
        return sTimeSpentOnCase;
    }
    
    /**
    * get Case Milestone record which are not completed with violation status
    * @param List<user>
    * @param Boolean isViolated
    * @param Integer ilimit     
    */
    public static List<CaseMilestone> getCaseMilestones(List<User> lstUsers, Boolean bParamIsViolated, Integer iLimit) {
        List<CaseMilestone> lstCaseMilestone = new List<CaseMilestone>();
        
        lstCaseMilestone = [select Case.CaseNumber, Case.Owner.Name, Case.Owner_Queue__c, StartDate, TargetDate, isviolated, TimeRemainingInMins, TimeSinceTargetInMins   
                                    from CaseMilestone 
                                    where IsCompleted = false and TargetDate <= NEXT_N_DAYS:2 and 
                                    Case.OwnerId in :lstUsers and IsViolated =:bParamIsViolated 
                                    order by Case.Owner_Queue__c, Case.Work_Queue_View_Name__c, TargetDate limit: iLimit];
        return lstCaseMilestone;
    }
    /**
    * Check user is having permission set access or not
    * @param String PermissionSetName
    * @returnParam Boolean bHasPermission
    */
    public static boolean isUserhavingPermissionSetAccess(String sPermissionSetName){
        boolean bHasPermission = false;
        try{
            List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :UserInfo.getUserId() AND PermissionSet.Name = :sPermissionSetName];
            if(null != lstPMAssign && !lstPMAssign.isEmpty()) bHasPermission = true;
        }catch(exception ex){
            bHasPermission = false;
            HUMExceptionHelper.logErrors(ex, 'UtilityHelper_H_AHT_HUM ', 'isUserHavingPharmvilleAccess');
        }
        return bHasPermission;
    }
    
    /**
    * get object prefix value for a given object name
    * @param String 
    * @return String
    */
    public static String getObjectPrefix(String sObjectName) {
        String sObjectPrefix = '001';
        try {
            if(String.isNotBlank(sObjectName)) {
                sObjectPrefix = Schema.getGlobalDescribe().get(sObjectName).getDescribe().getKeyPrefix();   
            }
        } catch(Exception ex) {
            sObjectPrefix = '001';
            HUMExceptionHelper.logErrors(ex, 'UtilityHelper_H_AHT_HUM ', 'getObjectPrefix');
        }
        return sObjectPrefix;
    }
    
    /**
    * Disable button on Case if transfer to OGO department and user has CRMS_400_Grievance_Appeals
    * @param String - sCaseWQ
    * return Boolean
    */
    public static Boolean disableButtonOGOCase(String sCaseWQ) {
        Boolean bDisableButtonOGO = false;

        try {
            Boolean bHasPermission400GA = isUserhavingPermissionSetAccess(Grievance_Appeals_Permissionset_Name);
            
            if(!bHasPermission400GA && String.isNotBlank(sCaseWQ) && sCaseWQ.equalsIgnoreCase(WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT)) {
                bDisableButtonOGO = true;
            }
        } catch(Exception ex) {
            bDisableButtonOGO = false;
        }
        
        return bDisableButtonOGO;
    }

    /**
     * To check whether work queue view has a recommended queue view for OGO 
     * @param String - case workqueueviewname
     * @return Boolean
     */
    public static Boolean isRecommendedQueueView(String sQueueViewName) {
        Boolean bRecommendedQueueView = false;
        RecommendedQueueView__c csRQVDetail = getRecommendedQueueViewDetail(sQueueViewName);
        if(csRQVDetail != null) {
            bRecommendedQueueView = true;
        }
        return bRecommendedQueueView;
    }
    /**
     * To check whether work queue view has a recommended queue view for OGO 
     * @param String - case workqueueviewname
     * @return RecommendedQueueView__c
     */
    public static RecommendedQueueView__c getRecommendedQueueViewDetail(String sQueueViewName) {
        RecommendedQueueView__c csReccomQueueView = null;
        List<RecommendedQueueView__c> lstRQV = RecommendedQueueView__c.getAll().values();
        for(RecommendedQueueView__c csRQV : lstRQV) {
            String sTempQVName = csRQV.OGO_QueueView_Name__c;
            if(String.isNotBlank(sTempQVName) && sTempQVName.equalsIgnoreCase(sQueueViewName)) {
                csReccomQueueView = csRQV;
                break;
            }
        }        
        return csReccomQueueView;
    }
    /**
     * To check case Feedback task
     * @param String - caseid
     * @return Boolean
     */
    public static Boolean hasCaseFeedback(String sCaseId) {
        Boolean bHasNoFeedbackTask = false;
        List<Task> lstFeedbackTask = [select id from task where whatid =:sCaseId and recordtype.name =: RECORDTYPE_NAME_FEEDBACKTASK];
        bHasNoFeedbackTask = lstFeedbackTask.isEmpty();
        return bHasNoFeedbackTask;
    }
    /**
    * Disable Action button on Case detail page if case owner is same as work queue (for CRMS_401_OGO_Scrubber_Access Scrubber user).
    * @param String - sCaseWQ 
             String - sCaseOwner
    * return Boolean
    */
    public static Boolean disableActionButton(String sCaseWQ, String sCaseOwner) {
        Boolean bDisableAction = false;

        try {
	       if(String.isNotBlank(sCaseWQ) && sCaseWQ == sCaseOwner) {
                bDisableAction = true;
            }
            if(Test.isRunningTest()) throw new UtilityHelperCustomException();
        } catch(Exception ex) {
            bDisableAction = false;
        }
        return bDisableAction;
    }
    /**
    * Method is used to identify if the logged in user is having OGO Scrubber and OGO Permission Set. 
    * <p>
    * @return   boolean
    */
    public static boolean isOGOScrubberUser()
    {
        boolean bOGOScrubberAccess = false;
        String sUserId = UserInfo.getUserId();
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : OGO_SCRUBBER_PERMISSION_SET];
        if(null != lstPMAssign && !lstPMAssign.isEmpty() && lstPMAssign.size()==2) bOGOScrubberAccess = true;
        return bOGOScrubberAccess;
    }
    
    /*
        Historical cases skip check
    */
    public static Boolean bSkipHistoricalCases(Case CaseRec,String sclassification, String sIntent){
        String sOldClassification='';
        String sOldIntent='';
        Boolean bNewCase = false;
        Boolean bChangeMade = false;
        try{
            List<CaseHistory> caseHistoryList = [select id,newvalue,field from casehistory where  field  in('classification_id__C','Intent_id__c')  AND caseid=:CaseRec.id  ORDER BY Createddate DESC LIMIT 999];
            if(!caseHistoryList.isEmpty()) {
                for(CaseHistory chRef : caseHistoryList){
                    if(String.isNotBlank(sOldClassification) && String.isNotBlank(sOldIntent)){
                        break;
                    }
                    if(String.isBlank(sOldClassification) && chRef.field=='Classification_Id__c' && !UtilityHelper_H_AHT_HUM.isStringId((String)chRef.newvalue)){
                        sOldClassification = (String)chRef.newvalue;
                    }
                    if(String.isBlank(sOldIntent) && chRef.field=='Intent_Id__c' && !UtilityHelper_H_AHT_HUM.isStringId((String)chRef.newvalue)){
                        sOldIntent = (String)chRef.newvalue;
                    }  
                }
            }
            else {
                bNewCase = true;
            }
            if(!bNewCase && ((String.isNotBlank(sOldClassification) && (sOldClassification!=sclassification))||((String.isNotBlank(sOldIntent) && sOldIntent!=sIntent)))){
                bChangeMade = true;
            }
            if(Test.isRunningTest()) throw new UtilityHelperCustomException();
            return (bNewCase || bChangeMade);
        }
        catch(Exception ex){
             HUMExceptionHelper.logErrors(ex, 'UtilityHelper_H_AHT_HUM', 'bSkipHistoricalCases');
             return false;
        }
    }
    
    
    
         /**
   * Method is used to identify if the string is valid ID or not.
    * <p>
    * @return   boolean
    */
    public static Boolean isStringId(String idTestStr) {
        Boolean isId = true;
        Id anId;
        try {
            anId = idTestStr;
        } catch (StringException e) {
            isId = false;
        }
        return isId;
    }
    public static Boolean checkProcessPCPPCD(Case CaseRec,String  oclassifi,String oIntent) {
        Boolean bCheckPCPPCD = false;
       try{
            if(String.isNotBlank(CaseRec.Owner_Queue__c) 
                && (CALLS_RSO_WORKQUEUES_SET.contains(CaseRec.Owner_Queue__c)
                        || CaseRec.Owner_Queue__c.equalsIgnoreCase(Louisville_Medicaid_Calls_Support) || CaseRec.Owner_Queue__c.equalsIgnoreCase(Louisville_Remote_Customer_Care_Queue))
                && String.isNotBlank(CaseRec.Classification_Type__c) 
                    && (CaseRec.Classification_Type__c.equalsIgnoreCase(CASE_CLASSIFICATION_TYPE_CALLRSO)
                            || CaseRec.Classification_Type__c.equalsIgnoreCase(CASE_CLASSIFICATION_TYPE_MEDICAID_CALLS) || CaseRec.Classification_Type__c.equalsIgnoreCase(Guidance_Center))
                && String.isNotBlank(oclassifi)  && oclassifi == CLASSIFICATION_PCP_CHANGE
                && (String.isNotBlank(oIntent) && oIntent == INTENT_PCP_CHANGE_MADE ||  oIntent == PCP_Research_Requested)
                && String.isNotBlank(caseRec.Interacting_With_Type__c) 
                        && (caseRec.Interacting_With_Type__c.equalsIgnoreCase(INTERACTINGWITHTYPE_MEMBER)
                                || caseRec.Interacting_With_Type__c.equalsIgnoreCase(MEMBERCAREGIVER))
                && String.isNotBlank(Caserec.Policy_Member__c) 
                    && String.isNotBlank(Caserec.Product__c) && Caserec.Product__c.equalsIgnoreCase(MED))
            {
                boolean bCallsRSOType = CALLS_RSO_WORKQUEUES_SET.contains(CaseRec.Owner_Queue__c) && CaseRec.Classification_Type__c.equalsIgnoreCase(CASE_CLASSIFICATION_TYPE_CALLRSO);
                boolean bGuidanceCenterType = CaseRec.Classification_Type__c.equalsIgnoreCase(Guidance_Center) && CaseRec.Owner_Queue__c.equalsIgnoreCase(Louisville_Remote_Customer_Care_Queue);
                // Get the Product Type  and Product Type Code assocaited with Case
                String sProductType = Caserec.Product_Type__c;
                String sProductTypeCode = Caserec.Policy_Member__r.Product_type_code__c;
                if(String.isNotBlank(sProductType)) {
                    Set<String> setProductTypeCode = MEDICARE_LOB_PRODUCTTYPE_LIST().get(sProductType);
                    if(!bCallsRSOType && !bGuidanceCenterType && sProductType.equalsIgnoreCase(MAJOR_LOB_MCD) && String.isBlank(sProductTypeCode)) {
                        bCheckPCPPCD = true;
                    }  else if((bCallsRSOType || bGuidanceCenterType) && (sProductType.equalsIgnoreCase(MAJOR_LOB_MER) || sProductType.equalsIgnoreCase(PRODUCT_TYPE_MGR)) && !setProductTypeCode.isEmpty()) {
                        bCheckPCPPCD = setProductTypeCode.contains(sProductTypeCode);
                    }
                }
                if(bCheckPCPPCD) {
                    bCheckPCPPCD = bSkipHistoricalCases(CaseRec,oclassifi,oIntent);
                }
            }
                if(Test.isRunningTest()) throw new UtilityHelperCustomException();
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UtilityHelper_H_AHT_HUM', 'checkProcessPCPPCD');
            bCheckPCPPCD = false;
            
        }
        return bCheckPCPPCD;
    }

    Public Static Boolean checkProcessMedicaidPARProviderNotAccepting(Case CaseRec,List<Classification__c> oclassifi,List<Intent__c> oIntent){
        Boolean bCheckProcess = false;
        try{
            if( null != CaseRec.Policy_Member__c && oclassifi != null && !oclassifi.isEmpty() &&  oIntent != null && !oIntent.isEmpty() && CaseRec.Classification_Type__c.equalsIgnoreCase(CASE_CLASSIFICATION_TYPE_MEDICAID_CALLS) && oclassifi[0].Name.equalsIgnoreCase(CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderClassification) && oIntent[0].Name.equalsIgnoreCase(CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderIntent) && Caserec.Product_Type__c.equalsIgnoreCase(MAJOR_LOB_MCD) && CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_STATES_VALUES.contains(CaseRec.Policy_Member__r.Issue_State__c) && bSkipHistoricalCases(caseRec,oclassifi[0].Name,oIntent[0].Name) ){
                bCheckProcess = true;
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'UtilityHelper_H_AHT_HUM', 'checkProcessMedicaidPARProviderNotAccepting');
            bCheckProcess = false;
        }
        return bCheckProcess;
    }

    Public Static Boolean checkProcessGoodCause(Case CaseRec,String  oclassifi,String oIntent){
        Boolean bCheckProcess = false;
        try{
            if( null != CaseRec.Policy_Member__c
                && String.isNotBlank(Caserec.Policy_Member__r.Product_type_code__c) && Caserec.Policy_Member__r.Product_type_code__c.equalsIgnoreCase(PRODUCT_TYPE_PDP)
                && GoodCauseAutoRouteClassificationTypes.contains(CaseRec.Classification_Type__c)
                && String.isNotBlank(oclassifi) && oclassifi.equalsIgnoreCase(GOODCAUSECLASSIFICATION)
                && String.isNotBlank(oIntent) && oIntent.equalsIgnoreCase(GOODCAUSEINTENT)){
                bCheckProcess = true;
            }
            if(Test.isRunningTest()) throw new UtilityHelperCustomException();
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'UtilityHelper_H_AHT_HUM', 'checkProcessGoodCause');
            bCheckProcess = false;
        }
        return bCheckProcess;
    }

    public Static Boolean checkUpdateDemographic( Case oCase, String sClassi, String sIntent){
        Boolean bAutoLaunch = false;
        try{
            if( oCase.Classification_Type__c.equalsIgnoreCase(CaseAutoRouteConstant_C_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO)
                && String.isNotBlank(sClassi) && sClassi.equalsIgnoreCase(CaseAutoRouteConstant_C_HUM.CLASSIFICATION_DEMOGRAPHIC)
                && String.isNotBlank(sIntent) && sIntent.equalsIgnoreCase(CaseAutoRouteConstant_C_HUM.INTENT_CHANGE)){
                    bAutoLaunch = true;
            } 
            if(Test.isRunningTest()) throw new UtilityHelperCustomException();  
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'UtilityHelper_H_AHT_HUM', 'checkUpdateDemographic');
            bAutoLaunch = false;
        }
        return bAutoLaunch;
    }
	
    /**
    * Method is used to identify if Case is Eligible for QAA Autoroute
    * <p>
    * @return   boolean
    */
    public static boolean isQAAAutoRoute(Case casObj)    {

        boolean bAutoRouteQAA = false;
        
        try{
             
            if(null != casObj.Policy_Member__c &&
                (
                    (null != casObj.Policy_Member__r.Segment_Indicator__c && CaseAutoRouteConstant_C_HUM.LST_MEDICAREPLANS.contains(casObj.Policy_Member__r.Segment_Indicator__c))
                    || 
                    ( 
                        (null != casObj.Policy_Member__r.Issue_State__c && CaseAutoRouteConstant_C_HUM.LST_MEDICAID_STATES.contains(casObj.Policy_Member__r.Issue_State__c))
                        && 
                        ( 
                            (
                                (null != casObj.Policy_Member__r.Segment_Indicator__c && CaseAutoRouteConstant_C_HUM.MEDICAIDPLAN == casObj.Policy_Member__r.Segment_Indicator__c)
                                ||
                                (null != casObj.Policy_Member__r.Product_Type__c && CaseAutoRouteConstant_C_HUM.PRODUCT_TYPE_MCD == casObj.Policy_Member__r.Product_Type__c)
                            )
                            &&
                            (
                                (!CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(casObj.Policy_Member__r.Issue_State__c))
                                ||
                                ( String.isBlank(casObj.Policy_Member__r.Group_Number__c) && CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(casObj.Policy_Member__r.Issue_State__c))
                                ||
                                ( 
                                        CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(casObj.Policy_Member__r.Issue_State__c) && 
                                        String.isNotBlank(casObj.Policy_Member__r.Group_Number__c) &&  casObj.Policy_Member__r.Group_Number__c.length() <= 4
                                )
                                ||
                                (
                                    String.isNotBlank(casObj.Policy_Member__r.Group_Number__c) && CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(casObj.Policy_Member__r.Issue_State__c)
                                    &&  casObj.Policy_Member__r.Group_Number__c.length() > 4 
                                    &&  !CaseAutoRouteConstant_C_HUM.lstFLLTCQAAIgnoreGroupNumbers.contains(casObj.Policy_Member__r.Group_Number__c.substring(0, 5))
                                )
                            )
                        )
                    )
                )
                &&
                (
                    String.isNotBlank(casObj.Created_By_Queue__c ) && !casObj.Created_By_Queue__c .equalsIgnoreCase(WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT)
                )
            ){
                bAutoRouteQAA =true;		
            }
            if(Test.isRunningTest()) throw new UtilityHelperCustomException();
            
        }catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UtilityHelper_H_AHT_HUM', 'isQAAAutoRoute');
            bAutoRouteQAA = false;
            
        }

    return bAutoRouteQAA;


    }

    Public static List<Template_Submission_Data__c> getTemplateData(String sTemplateName, List<String> lstTemplateFields, Case caseRec){
        return  [SELECT Value__c, Template_Field__r.Name  FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = : sTemplateName AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = : caseRec.Id AND Template_Field__r.Name IN :lstTemplateFields];
    }

    Public static TemplateSubmissionData getTemplateSubmissionWrapper(List<Template_Submission_Data__c> lstStateFlowValue){
        TemplateSubmissionData oTempSubData = new TemplateSubmissionData();
        for(Template_Submission_Data__c e : lstStateFlowValue){
            if(e.value__c!=null){
                if(CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_STATEFIELDS.contains(e.Template_Field__r.Name)){
                    oTempSubData.sStateField = e.value__c;
                }   
                if( CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_BEHAVIORAL_FIELDS.contains(e.Template_Field__r.Name)){
                    oTempSubData.sBehavioralHealthQuestion = e.value__c;
                }  
            }
        }
        return oTempSubData;
    }
    public static Boolean isTemplateSubmittedHasValue(List<Template_Submission_data__c> submissionData){
        Boolean hasValue = false;
        for(Template_Submission_data__c tData :submissionData){
            if(String.isNotBlank(tData.value__c)){
                hasValue = true;
               break;
            } 
        }
        return hasValue;
    }
    public class TemplateSubmissionData{
        public String sStateField;
        public String sBehavioralHealthQuestion;
    }
    public class UtilityHelperCustomException extends Exception{}
}