/*******************************************************************************************************************************
Apex Class Name : CaseActionLaunch_LC_Hum.cls
Version         : 1.0
Created On      : 01/18/2022
Function        : Class contains methods for getting case action processes.
TestClass       : CaseActionLaunch_LT_Hum

Modification Log:
* Developer Name            Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg				01/18/2022				  Initial Version - US2876685 - Adding for Case Action Processes
* Nirmal Garg				02/17/2022					Adding invocable method for defect 4463
*****************************************************************************************************************************************/
public with sharing class CaseActionLaunch_LC_Hum {
    private static string sClassification ='';
    private static string sIntent = '';    
    private static Boolean bShowLinks = false;
    private static Boolean bMemberCase = false;
    private static Boolean bOpenCase = false;
    private static Boolean bIsCaseOwner = false;
    private static string sProcessName = '';
    private static Boolean bShowWebIssuesRecommended = false;
    public static ProcessDetails objProcessDetails = new ProcessDetails();
    @testVisible private static Case caseRec = New Case();
    public static List<String>  sRecommendedProcesslist;
    public static List<String>  sRecommendedLinklist;
    public static List<Integer> processlinkiter;
    public static  String sRecommendedLink='';
    public static final String CASEACTIONLAUNCHCONTROLLER_HP_GROUPCASETYPE_HUM = 'HP_Group_Case';
    public static final String CASEACTIONLAUNCHCONTROLLER_HP_PROVIDERCASETYPE_HUM = 'HP_Provider_Case';
    public static final String CASEACTIONLAUNCHCONTROLLER_HP_AGENTBROKERCASETYPE_HUM = 'HP_Agent_Broker_Case';
    public static final String CASEACTIONLAUNCHCONTROLLER_HP_MEMBERCASETYPE_HUM = 'HP_Member_Case';
    public static final String MEMBER = 'Member';
    public static final String CASEEDIT_CLOSED_HUM = 'Closed'; 
    public static final String CASEEDIT_CANCELLED_HUM = 'Cancelled'; 
    public static final String CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_CALLS = 'Humana Pharmacy Calls'; 
    public static final string HUMANAPHARMACYWEBCHATWQ = 'Humana Pharmacy Web Chat'; 
    public static final String CASE_CLASSIFICATION_PLAN_WEBMOBILE_CTTM = 'Website (CT & TM)';
    public static final String CASE_INTENT_WEB_EDUCATIONNAVIGATION= 'Web Education/Navigation';
    public static final String PHARMACYSUPERVISOR_PERMISSION_HUM = 'CRMS_300_Humana_Pharmacy_Supervisor';
    public static final string Customer_Care_Supervisor = 'Customer Care Supervisor';
    public static final String PHARMACY_WEBISSUES_RECOMMENDED = 'Humana Pharmacy Web Issues';
    public static final String CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC_HUM = 'Medicare Address Verification Form';
    public static final string HP_MEMBERCASE_HUM = 'HP Member Case';
    public static final String CASEACTIONLAUNCHCONTROLLER_GROUPCASETYPE_HUM = 'Group_Case';
    public static final String CASEACTIONLAUNCHCONTROLLER_PROVIDERCASETYPE_HUM = 'Provider_Case';
    public static final String CASEACTIONLAUNCHCONTROLLER_AGENTBROKERCASETYPE_HUM = 'Agent_Broker_Case';
    public static string MemberId ='';
    public static String MemberName='';
    public static String MemberDOB = '';
    public static String SubmissionID ='';
    public static String TN ='';
    public static string SubmissionData ='';
    public static Boolean TicketRequired=false;
    public static Boolean hasProcess=false;
    public static Map<String,String> mapProcessData = new Map<String,String>();
    

    @AuraEnabled(cacheable=true)
    public static string getProcesses(string caseid){
       
        String sProcess ='';
        try {
            if(String.isNotBlank(caseid)){
                Case objCase = [Select status, Interacting_About_Type__c, Interacting_With_Type__c, RecordType.Name, RecordType.developerName, Product__c,Policy_Member__r.Policy__r.Product__r.ProductCode, Policy_Member__r.Product__c, Policy_Member__r.Policy__r.Product__r.Major_LOB__c,
                            Policy_Member__r.Policy__r.Product__r.Product_Type__c, product_type__c, Policy_Member__c, OwnerID, Account.RecordType.Name,Classification_Type__c,Owner_Queue__c,Created_By_Queue__c, CTCI_List__c
                             , CTCI_List__r.Classification__c, CTCI_List__r.Intent__c, CTCI_List__r.Classification__r.Name, Member_Plan_Id__c,
                             CTCI_List__r.Intent__r.Name From Case Where Id =: caseid];
                        string caseRecordType = objCase.RecordType.Name;              
                            If (caseRecordType == CASEACTIONLAUNCHCONTROLLER_HP_GROUPCASETYPE_HUM ||
                            caseRecordType == CASEACTIONLAUNCHCONTROLLER_HP_PROVIDERCASETYPE_HUM ||
                            caseRecordType == CASEACTIONLAUNCHCONTROLLER_HP_AGENTBROKERCASETYPE_HUM ||
                            caseRecordType == CASEACTIONLAUNCHCONTROLLER_GROUPCASETYPE_HUM ||
                            caseRecordType == CASEACTIONLAUNCHCONTROLLER_PROVIDERCASETYPE_HUM ||
                            caseRecordType == CASEACTIONLAUNCHCONTROLLER_AGENTBROKERCASETYPE_HUM)
                         {
                             bShowLinks = True;
                             objProcessDetails.ShowLinks = true;
                             
                         }
                        
                         if((objCase.Interacting_About_Type__c != NULL && objCase.Interacting_With_Type__c != NULL) && ((objCase.Interacting_About_Type__c).equalsIgnoreCase(MEMBER)  && (objCase.Interacting_With_Type__c).equalsIgnoreCase(MEMBER)))
            {
                bMemberCase = true;
                objProcessDetails.MemberCase = true;
            }
            
            bOpenCase = objCase.status == CASEEDIT_CLOSED_HUM || objCase.status == CASEEDIT_CANCELLED_HUM ? false : true;   
            objProcessDetails.OpenCase = true;
			if(!(String.isBlank(objCase.OwnerID)) && (objCase.OwnerID == UserInfo.getUserId())){
                bIsCaseOwner= true;
                objProcessDetails.CaseOwner = true;
            }
            
            if(objCase.CTCI_List__c!=NULL && NULL != objCase.CTCI_List__r.Classification__c)  sClassification=objCase.CTCI_List__r.Classification__r.Name;
            objProcessDetails.Classification = sClassification;
                            
            if(objCase.CTCI_List__c!=NULL && NULL != objCase.CTCI_List__r.Intent__c) sIntent=objCase.CTCI_List__r.Intent__r.Name ;
            objProcessDetails.Intent = sIntent;
            if((caseRecordType == HP_MEMBERCASE_HUM) && ((String.isNotBlank(objCase.Policy_Member__c) && objCase.Policy_Member__c != null)
            || (String.isNotBlank(objCase.Member_Plan_Id__c) && objCase.Member_Plan_Id__c != null)))
            {      
                objProcessDetails.WebIssueAvailable = true;
                
                if(!(String.isBlank(objCase.Classification_Type__c)) && (objCase.Classification_Type__c.equals(CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_CALLS) || (objCase.Classification_Type__c.equals(HUMANAPHARMACYWEBCHATWQ))))
                {    
                    if(!(String.isBlank(sclassification) && String.isBlank(sintent)) && (sclassification == CASE_CLASSIFICATION_PLAN_WEBMOBILE_CTTM) && (sintent == CASE_INTENT_WEB_EDUCATIONNAVIGATION))
                    {
                        if(bIsCaseOwner || HUMUtilityHelper.getCurrentUserProfileName() == Customer_Care_Supervisor || UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(PHARMACYSUPERVISOR_PERMISSION_HUM)){    
                            objProcessDetails.WebIssueRecommended = true;
                            bShowWebIssuesRecommended = true;
                        }
                    }
                    
                }                
            }
            
            if(bOpenCase && (objCase.Policy_Member__c != null || objCase.Member_Plan_Id__c != null)){
                if(objCase.CTCI_List__r.classification__r.Name == CASE_CLASSIFICATION_PLAN_WEBMOBILE_CTTM && objCase.CTCI_List__r.Intent__r.Name == CASE_INTENT_WEB_EDUCATIONNAVIGATION)
                {
                    sProcessName = PHARMACY_WEBISSUES_RECOMMENDED;
                }
                if(!(String.isBlank(sProcessName)))
                sProcess = populateRecommendedProcess(sProcessName);
                objProcessDetails.RecommendedProcess = sProcess;
                if(caseRecordType.containsIgnoreCase('HP')){
                    objProcessDetails.CaseReadOnly = false;
                }
                objProcessDetails.lstRecommendedProcess = sRecommendedProcesslist;
            }
        }return JSON.serialize(objProcessDetails);
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(cacheable=true)
    public static string init(String caseid){
        try {
            List<Case> MemberPrefill = [SELECT Id, Policy_Member__r.Name, Policy_Member__r.member__r.Name, Policy_Member__r.member__r.Birthdate__c,
            Member_Plan_Id__c,Member_Plan_Id__r.Name, Member_Plan_Id__r.Member.Name, Member_Plan_Id__r.Member.Birthdate__c FROM case WHERE Id=:CaseId];
        if (MemberPrefill.size() > 0) {
            MemberId = MemberPrefill[0].Policy_Member__r.Name != null ? MemberPrefill[0].Policy_Member__r.Name : MemberPrefill[0].Member_Plan_Id__r.Name != null ? MemberPrefill[0].Member_Plan_Id__r.Name : '';
            mapProcessData.put('MemberId',MemberId);
            MemberName = MemberPrefill[0].Policy_Member__r.member__r.Name != null ? MemberPrefill[0].Policy_Member__r.member__r.Name : MemberPrefill[0].Member_Plan_Id__r.Member.Name != null ? MemberPrefill[0].Member_Plan_Id__r.Member.Name : '';
            mapProcessData.put('MemberName',MemberName);
            MemberDOB = MemberPrefill[0].Policy_Member__r.member__r.Birthdate__c != null ? MemberPrefill[0].Policy_Member__r.member__r.Birthdate__c : MemberPrefill[0].Member_Plan_Id__r.Member.Birthdate__c != null ? MemberPrefill[0].Member_Plan_Id__r.Member.Birthdate__c : '';
            mapProcessData.put('MemberDOB',MemberDOB);
        }

        List<Template_Submission__c> TempList = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:CaseId LIMIT 1];
        if(TempList != null && TempList.size() > 0){ 
            SubmissionID = TempList[0].Id;
            mapProcessData.put('SubmissionID',SubmissionID);
            List<Template_Submission_Data__c> Submission = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c =: SubmissionID];
            if(Submission != null && Submission.size() > 1 && (Submission[0].Value__c != '' && Submission[1].Value__c != ''))
            {
                if(Submission.size() > 0)
                {
                    SubmissionData = Submission[0].Value__c;
                    mapProcessData.put('SubmissionData',SubmissionData);
                }
                if(Submission.size() > 1)
                {
                    TicketRequired = Boolean.valueOf(Submission[1].Value__c);
                    mapProcessData.put('TicketRequired',String.valueOf(TicketRequired));
                }
                hasProcess = true;
                mapProcessData.put('hasProcess',String.valueOf(hasProcess));
            }
        }else{
            SubmissionID = '';
            mapProcessData.put('SubmissionID',SubmissionID);
            hasProcess = false;
            mapProcessData.put('hasProcess',String.valueOf(hasProcess));
        }
        
        List<Template__c> templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Humana Pharmacy Web Issues' Limit 1];
        if(templateNumber != null && templateNumber.size() > 0){
            TN = templateNumber[0].name;
            mapProcessData.put('TN',TN);
        }
            return JSON.serialize(mapProcessData);
            
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    

    /*
    *This method Is used to retrieve recommended process url based on classification And intent values
    *@param      String   sProcessType
    *@rertunr    String    
    */
    public static String populateRecommendedProcess(String sProcessType)
    {   
		Integer i = 0;
        String sRecommendedProcess = '';
        string sProcessAVF = CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC_HUM;
		sRecommendedProcesslist = new List<String>();
        sRecommendedLinklist = new List<String>();
        processlinkiter = new List<Integer>();
        If(!(String.isBlank(sclassification) || String.isBlank(sintent)))
        {
            List <HUMRecommended_Process__c> lstRecommendedProcess = [Select RecommendedProcess__c, RecommendedLink__c from HUMRecommended_Process__c 
                                                                where classification__c=:sClassification and intent__c=: sIntent and PolicyType__c=:sProcessType and RecommendedProcess__c!=: sProcessAVF];                                
            if(!lstRecommendedProcess.isEmpty())
            {
                sRecommendedProcess = lstRecommendedProcess[0].RecommendedProcess__c; 
                sRecommendedLink = lstRecommendedProcess[0].RecommendedLink__c;
             } 
			 /* Hard coded the values for process as having check on classification and policy type only not on Intent 
			and also need to disply the two links at a time so added list variables to display the links*/
			 
                 if(bShowWebIssuesRecommended){
                    sRecommendedProcesslist.add(PHARMACY_WEBISSUES_RECOMMENDED);
                    sRecommendedLinklist.add(PHARMACY_WEBISSUES_RECOMMENDED);    
                    processlinkiter.add(i);
                    i++;
                }
                                  
        }   
        return  sRecommendedProcess;  
    } 
    
    /*
    * Method Name   :    Check for existing data
    * Description   :    check for existing data in flow
    * Return Type   :    NA
    * Parameters    :    Case Record
    */
    @InvocableMethod(label='checkForExistingFlow' description='Check for existing flow for pharmacy web issues flow')
    public static List<string> checkForExistingFlow(List<String> sSelectedCaseId)
    { 
        List<string> lstExistingProcess = new List<string>();
        if(sSelectedCaseId.size() > 0 && !sSelectedCaseId.isEmpty())
        {
            List<Template__c> templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Humana Pharmacy Web Issues' Limit 1];
            if(templateNumber.size() > 0 && !templateNumber.isEmpty()){
                List<Template_Submission__c> TempList = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:sSelectedCaseId[0]
                                                         and template__c =:templateNumber[0].Id LIMIT 1];
                if(TempList.size() > 0 && !TempList.isEmpty()){
                    for(Template_Submission__c temp : TempList){
                        lstExistingProcess.add(temp.Id);
                    }
                }
            }
        }
           
        return lstExistingProcess;
    }

    public class ProcessDetails{
        public Boolean ShowLinks = false;
        public Boolean MemberCase = false;
        public Boolean OpenCase = false;
        public Boolean CaseOwner = false;
        public string Classification = '';
        public string Intent = '';
        public Boolean WebIssueAvailable = false;
        public Boolean WebIssueRecommended = false;
        public List<string> lstRecommendedProcess;
        public Boolean CaseReadOnly = true;
        public string RecommendedProcess = '';
    }
}