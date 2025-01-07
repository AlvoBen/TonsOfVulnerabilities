/*****************************************************************************************
Apex class Name    : Logging_LC_HUM
Version            : 1.0
Function           : This is the controller class for Lightning Logging Functionality
Created Date       : 02/16/2022 
Test Class         : Logging_LT_HUM

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Abhishek Mangutkar                                  02/16/2022           Original Version
*  Nirmal Garg                                         02/16/2022           Adding logic for case creation
* Abhishek Mangutkar           	  					   03/01/2023           US 4286520 Remove logic for assign member plan id for logging cases
* Ceasar Sabarre                                          03/02/2023        US#4268275 Added the getAttachmentLogs method
* Ceasar Sabarre                                          03/02/2023        US#4315363 Added the getCaseComments method
* Abhishek Mangutkar                                   03/13/20223          US - 4365921 - Automatically prefill the interacting with type and case origin from the interaction log on new cases
* Swapnali Sonawane                                    04/12/2023           US - 4475808_4475814_4475820 - Automatically prefill " Interacting with" & "Interacting With Name" from the interaction log on new cases
* Sagar Gulleve                                        04/17/2023           US - 4474260 - Case Management: Auto Fill " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Surge)
* Jonathan Dickinson                                   06/14/2023           User Story 4705843: T1PRJ0891339 2023 Arch Remediation-SF-Tech-Filter cases having template attached from existing case history logging for process logging
* Jonathan Dickinson                                   07/16/2023           User Story 4812209: T1PRJ0891415 Platform Management- SF - TECH - Critical- Lightning – Logging
* Jonathan Dickinson                                   08/02/2023           SOQL Injection Security Issue Fix
* Isaac Chung                                          12/12/2023           US 5107579 Mail Order Management; Pharmacy- Guided Flow- Inactivate Rx- Implementation (Lightning)
*********************************************************************************************************************************/
public with sharing class Logging_LC_HUM
{
    @testVisible private Id accId = NULL;
    @testVisible private String sObjName = '';
    public Static String sCurrentQueue = '';
    public Static String sQueueHistory = '';
    public Static Case objCase = null;
    public Static String sAccRecordTypeName = '';
    public Static String caseRecordTypeId = '';
    public Static String caseRecordTypeName = null;
    public Static Account oAccount = NULL;
    public static string caseID = '';
    public static string sCaseNumber = '';
    public Static RecordType recType = null;
    public static String sType = '';
    public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    public static String sLimitValue = String.valueOf(system.label.iPaginator);
    public static string OBJECT_ACCOUNT_HUM = 'Account';
    public static string OBJECT_MEMBERPLAN_HUM = 'MemberPlan';
    public static string strCalledFrom;  
	public static string interactionId;	
    
    @AuraEnabled
    public static String createLog(String logValues, Id caseId, string keyvalue, string desvalue, string type, string subtype)
    {
        try
        {
            Id oAttchId = Null;
             Attachment_log__c attachLog = new Attachment_Log__c (
                    Case__c = caseId,
                    Attachment_Key__c = keyvalue,
                    Attachment_Description__c = desvalue,
                    Attachment_Type__c = type,
                    Attachment_Sub_type__c = subtype,
                    Source_System__c = LOGGING_CONSTANT_HUM.STRING_CRM_LIGHTNING,
                    Logging_URL__c = '',
                    Standard_Value__c = '',
                    User_Selected_Value__c = String.isNotBlank(logValues) ? logValues.length() > 131000 ? logValues.subString(0, 131000) : logValues: '',
                    User_Selected_Value_EXT__c = String.isNotBlank(logValues) ? logValues.length() > 131000 ? logValues.subString(131000, logValues.length()) : '': '',
                    Created_By__c =  LoggingDataLogic_D_HUM.getUser(UserInfo.getUserId()).Network_User_Id__c,
                    CreatedByQueue__c = LoggingDataLogic_D_HUM.getUserCurrentQueue()
                );
            oAttchId = LoggingDataLogic_D_HUM.insertAttachementLogRecord(attachLog);
            return oAttchId;
        }
        catch(Exception e)
        {            
            return null;
        }
    }
	
    /*
    * Method Name : getRecordData
    * Description : This method is used to retrived logged data
    * Return type : void
    * Parameter   : NA
    */
    @AuraEnabled
    public static List<Attachment_Log__c> getAttachmentRecord(string attachId)
    {        
        List<Attachment_Log__c> lstAttachmentLogs = null;
        if(String.isNotBlank(attachId)){
            lstAttachmentLogs = [Select Id,Attachment_Type__c, Attachment_Sub_type__c, User_Selected_Value__c, User_Selected_Value_EXT__c From Attachment_Log__c Where Id =: attachId WITH USER_MODE];
        }
        return lstAttachmentLogs;
    }

    /*
        * Method Name : getAttachmentLogs
        * Description : This method is used to retrived logged data of a given MemberId
        * Return type : List<Attachment_Log__c>
        * Parameter   : String sMemberId
        */
        @AuraEnabled
        public static List<Attachment_Log__c> getAttachmentLogs(string sMemberId)
		{        
			List<Attachment_Log__c> lstAttachmentLogs;
			if(string.IsNotBlank(sMemberId))
			{ 
                lstAttachmentLogs = [Select Id,Attachment_Type__c, Case__c, Case__r.CaseNumber, 
                Attachment_Sub_type__c, User_Selected_Value__c, User_Selected_Value_EXT__c 
                From Attachment_Log__c Where case__r.accountId =: sMemberId AND case__r.status != 'Cancelled' AND createdDate >= LAST_N_MONTHS:18 WITH USER_MODE];
			}
			return lstAttachmentLogs;
		}

    /*
        * Method Name : getCaseComments
        * Description : This method is used to retrived case comments
        * Return type : List<CaseComment>
        * Parameter   : String sMemberId
        */
        @AuraEnabled
        public static List<CaseComment> getCaseComments(string sMemberId)
        {        
            List<CaseComment> lstCaseComment = [SELECT Id, CommentBody, ParentId, Parent.Id, Parent.CaseNumber 
            FROM CaseComment 
            WHERE Parent.Policy_Member__r.Member__c = :sMemberId And Parent.Status != 'Cancelled' AND createdDate >= LAST_N_MONTHS:18 
            WITH USER_MODE
            ORDER BY Parent.LastModifiedDate DESC];

            return lstCaseComment;         
        }

    /*
    * Method Name : checkPermissionandQueue
    * Description : This method is used to assign queue value after checking the permissionset and queue assigned.
    * Return type : Void
    * Paramater   : N/A
    */  
    public static void checkPermissionandQueue()
    {
        List<Group> lstgrp = [Select Id from Group where type = :GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM and Name = :GLOBAL_CONSTANT_HUM.PDP_Pilot_PGQ WITH USER_MODE];
        List<GroupMember> lstgrpmem = [Select UserOrGroupId From GroupMember where GroupId =:lstgrp[0].Id and UserOrGroupId IN (select Id from User where Id =: UserInfo.getUserId()) WITH USER_MODE];
        List<PermissionSetAssignment> psas = [ select Id from PermissionSetAssignment where PermissionSetId IN ( select Id from PermissionSet where Name = 'CRMS_205_CCSPDPPharmacyPilot') and AssigneeId IN (select Id from User where Id =: UserInfo.getUserId())];
        
        if(lstgrpmem.size() > 0 && psas.size() > 0)
        {
           sCurrentQueue = GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues;
        }    
    }

    @AuraEnabled
    public static string createNewCase(string sObjectId, string calledfrom, string newInteractionId){
        string sObjectName = '';
        strCalledFrom = calledfrom;
		interactionId = newInteractionId;
        try {
            
            if(String.isNotBlank(sObjectId)){
                Id objId = sObjectId;
                sObjectName = objId.getSobjectType().getDescribe().getName();
                objCase = new Case();
                sCurrentQueue = [select Current_Queue__C from User where id =: UserInfo.getUserId() WITH USER_MODE].Current_Queue__C;
                sQueueHistory = sCurrentQueue;
                checkPermissionandQueue();
                assignInitialCaseData(sObjectName, sObjectId);
                if(string.isNotBlank(sCurrentQueue)){
                    return createCaseAndRetrieveId();
                }
                return null;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    
    public static string createCaseAndRetrieveId() {
        saveCase();
        if(string.isNotBlank(caseID)){
            List<Case> lstCases = [select id, casenumber from case where Id =: caseID WITH USER_MODE];
            return lstCases != null && lstCases.size() > 0 ? lstCases[0].id+'-'+lstCases[0].casenumber : null;
        }
        return null;
    }

    
    public static void saveCase(){
        assignValuesToCase();
        List<Case> lstCase = new List<Case>{objCase};
        List<Database.SaveREsult> lstSaveResults = Database.insert(lstCase, false, AccessLevel.USER_MODE);
        boolean bIsError = HUMExceptionHelper.processSaveResults(lstSaveResults, lstCase, 'HUMNewCaseRedirect', 'saveCase', 'Case');
        caseID = objCase.Id;
        sCaseNumber = objCase.CaseNumber;
    }

    public static void assignValuesToCase(){
        objCase.AccountId = oAccount.Id;
        objCase.RecordTypeId = caseRecordTypeId;
        assignFixedValuesToCase();
    }

    public static void assignFixedValuesToCase(){
        try{
            objCase.Status = HUMConstants.CASE_STATUS;
            objCase.Type = HUMConstants.CASE_TYPE;
            objCase.Case_Owner__c = UserInfo.getName();
            objCase.Owner_Queue__c = sCurrentQueue;
            Interaction__c objInteraction = getInteractionDetails(interactionId);
            if(objInteraction != null){
                objCase.Origin = objInteraction.Interaction_Origin__c;
                objCase.Interacting_With_type__c =objInteraction.Interacting_With_type__c;
                objCase.Interacting_With__c = objInteraction.Interacting_With__c;
                objCase.Interacting_With_Name__c = objInteraction.Caller_Name__c;
            }
            if(sCurrentQueue != sQueueHistory){
                assignRecTypeIdToCaseForPdpPilotQueue();
                objCase.Created_By_Queue__c = sCurrentQueue;
                objCase.LastModifiedby_Queue__c = sCurrentQueue;
            } 
            sCurrentQueue = sCurrentQueue.replace(' ','_');
            if(objCase.Origin == null)  objCase.Origin = HUMConstants.CASE_ORIGIN;

            List < Work_Queue_Setup__c > lstWQS = [select Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =: sCurrentQueue AND IsActive__c = TRUE];
            if (!lstWQS.isEmpty()) {
                 Work_Queue_Setup__c oWQS = lstWQS[0];
                assignWqsAndCtciValuesToCase(oWQS);
            }
        }catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'Logginc_LC_HUM', 'assignFixedValuesToCase');
        }
    }

    public static void assignRecTypeIdToCaseForPdpPilotQueue() {
        if(sCurrentQueue.equals(GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues)){
            Id HpCaseRecord = [SELECT Id FROM RecordType WHERE Name = :GLOBAL_CONSTANT_HUM.HP_MEMBERCASE_HUM WITH USER_MODE Limit 1].Id;
            if(HpCaseRecord != null){
                objCase.RecordTypeId = HpCaseRecord;
            }
        }
    }

    public static void assignWqsAndCtciValuesToCase(Work_Queue_Setup__c oWQS) {

        objCase.Service_Center__c = oWQS.Service_Center__c;
        objCase.Department__c = oWQS.Department__c;
        if (oWQS.Classification_Type__c != null) {
                objCase.Classification_Type__c = oWQS.Classification_Type__r.name;
        }
        List<CTCI_Junction__c> listofCTCI = [select id,Classification__c,Intent__c,Classification__r.name,Intent__r.name from CTCI_Junction__c where Classification_Type__r.name =:objCase.Classification_Type__c WITH USER_MODE];
        /* Changes for Classification & Intent for Invoice Request Logging */
        set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat','HP Finance Ops'};
        If(String.isNotBlank(strCalledFrom) && strCalledFrom.equalsIgnoreCase('Invoice') && classificationTypes.contains(objCase.Classification_Type__c)){
            assignCtciForInvoicesAndHpTypeUsers(listofCTCI);
        }
        If(String.isNotBlank(strCalledFrom) && strCalledFrom.equalsIgnoreCase('inactivateRx')){
            assignValuesForInactivateRx(listofCTCI);
        }
        set<String> webIssueClassificationTypes = new set<string>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat'};
        If(String.isNotBlank(strCalledFrom) && strCalledFrom.equalsIgnoreCase('pharmacywebissuesguidedflow')&&webIssueClassificationTypes.contains(objCase.Classification_Type__c) ){
            assignValuesForWebIssuesFlow(listofCTCI);
        }

    }
     public static void assignValuesForWebIssuesFlow(List<CTCI_Junction__c> listofCTCI){
        for(CTCI_Junction__c ctci : listofCTCI){
            if(ctci.Classification__r.name == 'Website (CT & TM)' && ctci.Intent__r.name == 'Web Education/Navigation'){
                objCase.CTCI_List__c = ctci.id;
                objCase.Classification_Id__c = ctci.Classification__c;
                objCase.Intent_Id__c = ctci.Intent__c;
            }
        }
    }
    public static void assignValuesForInactivateRx(List<CTCI_Junction__c> listofCTCI) {
        for(CTCI_Junction__c ctci : listofCTCI){
            if(ctci.Classification__r.name == 'Clinical Services Outreach/Transfer (CT & TM)' && ctci.Intent__r.name == 'Inactivate Rx'){
                objCase.CTCI_List__c = ctci.id;
                objCase.Classification_Id__c = ctci.Classification__c;
                objCase.Intent_Id__c = ctci.Intent__c;
            }
        }
        List < Work_Queue_Setup__c > lstWQS = [select Name, Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =:GLOBAL_CONSTANT_HUM.CLINICAL_SERVICES_QUEUE AND IsActive__c = TRUE];
        objCase.Service_Center__c = lstWQS[0].Service_Center__c;
        objCase.Department__c = lstWQS[0].Department__c;
        objCase.Owner_Queue__c =  lstWQS[0].Name;
    }

    @AuraEnabled
    public static Boolean assignValuesForInactivateRx(String caseId) {
        try {
            if (String.isNotBlank(caseId)) {
                List<Case> lstCases = [select Id, CTCI_List__c, Classification_Id__c, Intent_Id__c, Service_Center__c, Department__c, Owner_Queue__c from case where Id =: caseId WITH USER_MODE];
                Case lstupdate = lstCases[0];
                List<CTCI_Junction__c> listofCTCIHPS = [select id,Classification__c,Intent__c,Classification__r.name,Intent__r.name, Classification_Type__r.name from CTCI_Junction__c where Classification_Type__r.name =:GLOBAL_CONSTANT_HUM.CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_CALLS WITH USER_MODE];
        
                for(CTCI_Junction__c ctci : listofCTCIHPS){
                    if(ctci.Classification__r.name == 'Clinical Services Outreach/Transfer (CT & TM)' && ctci.Intent__r.name == 'Inactivate Rx'){
                        lstupdate.CTCI_List__c = ctci.id;
                        lstupdate.Classification_Type__c = ctci.Classification_Type__r.name;
                        lstupdate.Classification_Id__c = ctci.Classification__c;
                        lstupdate.Intent_Id__c = ctci.Intent__c;
                    }
                }
                List < Work_Queue_Setup__c > lstWQS = [select Name, Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =:GLOBAL_CONSTANT_HUM.CLINICAL_SERVICES_QUEUE AND IsActive__c = TRUE];
                if (lstWQS.size() > 0) {
                    lstupdate.Service_Center__c = lstWQS[0].Service_Center__c;
                    lstupdate.Department__c = lstWQS[0].Department__c;
                    lstupdate.Owner_Queue__c =  lstWQS[0].Name;
                }
                update lstupdate;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    public static void assignCtciForInvoicesAndHpTypeUsers(List<CTCI_Junction__c> listofCTCI) {
        for(CTCI_Junction__c ctci : listofCTCI){
            if(objCase.Classification_Type__c == 'HP Finance Ops' && ctci.Classification__r.name == 'Billing (Finance Ops)' && ctci.Intent__r.name == 'Invoice Requests'){
                objCase.CTCI_List__c = ctci.id;
                objCase.Classification_Id__c = ctci.Classification__c;
                objCase.Intent_Id__c = ctci.Intent__c;
            }else if(ctci.Classification__r.name == 'Billing Issue (CT & TM)' && ctci.Intent__r.name == 'Invoice Requests'){
                objCase.CTCI_List__c = ctci.id;
                objCase.Classification_Id__c = ctci.Classification__c;
                objCase.Intent_Id__c = ctci.Intent__c;
            }
        }
    }

    public static void assignInitialCaseData(String sObjectName, String objectId){
        try{
            if(String.isNotBlank(sObjectName)){
                getMember(sObjectName, objectId);
                getRecordType();

                Interaction__c objInteraction = getInteractionDetails(interactionId);
                if(objInteraction != null){
                    objCase.Origin = objInteraction.Interaction_Origin__c;
                    objCase.Interacting_With_type__c =objInteraction.Interacting_With_type__c;
                    objCase.Interacting_With__c = objInteraction.Interacting_With__c;
                    objCase.Interacting_With_Name__c = objInteraction.Caller_Name__c;
                }

                Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
                objCase.Interacting_About_Type__c = mapCaseInterAboutType.get(sAccRecordTypeName).CaseInteraction_About_Type__c;
                assignNpiAndTaxIds(sObjectName, objectId);
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'Logging_LC_Hum', 'assignInitialCaseData');
            throw new AuraHandledException(e.getMessage());
        }
    }

    public static void getMember(String sObjectName, String objectId){
        if (sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_ACCOUNT)) {
            oAccount = [select Id, Name, RecordtypeId, NPI_ID__c, MedicareID__C,
            (Select id FROM MemberPlans WHERE MemberId = :objectId order by Member_Coverage_Status__c asc,Product__c asc LIMIt 1) 
            from Account where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objectId) WITH USER_MODE];
        }
        else if(sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_MEMBERPLAN))
        {
            objCase.Member_Plan_Id__c = objectId;
            MemberPlan memPlan = [Select Id, MemberId from MemberPlan where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objectId) WITH USER_MODE LIMIT 1];
            oAccount = [Select Id, Name, RecordtypeId, NPI_ID__c, MedicareID__C from Account where Id = :memPlan.MemberId WITH USER_MODE];
            objCase.Member_Plan_Id__c = memPlan.Id;
        }
    }

    public static void getRecordType() {
        Map<ID, Schema.RecordTypeInfo> mapRecordType = Schema.SObjectType.Account.getRecordTypeInfosById();
        String accRecordTypeId = oAccount.RecordtypeId;
        sAccRecordTypeName = (NULL != mapRecordType.get(accRecordTypeId)) ? mapRecordType.get(accRecordTypeId).getName() : NULL;
        String recordTypeQuery = 'Select Id,Name From RecordType ';
        if(sCurrentQueue != sQueueHistory) {
            recordTypeQuery += 'WHERE Name=\''+CONSTANTS_CASE_HUM.HP_MEMBERCASE_HUM+ '\' Limit 1';
            recType = Database.query(recordTyPeQuery, AccessLevel.USER_MODE);
            caseRecordTypeId = recType.Id;
        } else {
            caseRecordTypeId =  HUMUtilityHelper.getCaseRecordType(sAccRecordTypeName);
            recordTypeQuery += 'WHERE Id=:caseRecordTypeId Limit 1';
            recType = Database.query(recordTyPeQuery, AccessLevel.USER_MODE);
            caseRecordTypeName = recType ?.Name;
        }
    }

    public static void assignNpiAndTaxIds(String sObjectName, String objectId) {
        Map<String, HUMCaseNPI_TAXID__c> mapCaseNPIAndTaxId = HUMCaseNPI_TAXID__c.getAll();
        Boolean bExists = false;
        for (String sCaseNPITax: mapCaseNPIAndTaxId.keySet()) {
            if (!String.isBlank(sCaseNPITax) && sCaseNPITax.equalsIgnoreCase(sAccRecordTypeName)) {
                bExists = true;
                break;
            }
        }
        
        if (bExists) {
            objCase.NPI_ID__c = oAccount.NPI_ID__c;
            List < ConsumerID__c > lstConsumerId = NULL;
            if (sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_ACCOUNT)) {
                lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__c =: ESAPI.encoder().SFDC_HTMLENCODE(objectId) WITH USER_MODE];
            }
            if (lstConsumerId?.size() > 0)
            {
                objCase.Tax_ID__c = lstConsumerId[0].Consumer_ID__c;
            }                    
        }
    }

    @AuraEnabled
    public static string getCaseHistory(string objId, string startdate, string enddate, boolean filterCasesHavingTemplate){
        DateTime dt1 = dateformat(startdate);
        DateTime dt2 = dateformat(enddate);
        dt2 = dt2.addDays(1);
        String caseStatus = 'Cancelled';
        Map<String, Object> caseBinds = new Map<String, Object>{'objId' => objId, 'startDate' => dt1, 'endDate' => dt2, 'caseStatus' => caseStatus, 'limitValue' => limitValue};

        try {
            String sQuery = initializeQuery(objId,startdate,enddate);
            
            if(String.isNotBlank(sQuery))
            {
                List<Case> result;

                result = Database.queryWithBinds(sQuery, caseBinds, AccessLevel.USER_MODE);
                
                if(filterCasesHavingTemplate) {
                    List<Case> filteredResult = getCasesWithoutTemplateAttached(result);
                    return getcasewrapper(filteredResult);
                } else {
                    return getcasewrapper(result);
                }
            }
            return sQuery;
			
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    
    public static date dateformat(string dt1){
        if(String.isNotBlank(dt1) && dt1.contains('/')){
            integer year = Integer.valueOf(dt1.split('/')[2]);
            integer month = Integer.valueOf(dt1.split('/')[0]);
            integer day = Integer.valueOf(dt1.split('/')[1]);
            return date.newInstance(year, month, day);
        }
        else{
            return null;
        }
    }

    public static List<Case> getCasesWithoutTemplateAttached(List<Case> casesToFilter) {
        Set<Id> caseIds = new Set<Id>();
        for(Case c: casesToFilter) {
               caseIds.add(c.Id);
        }
        List<Template_Submission_Owner__c> tempSubOwners;
        tempSubOwners = [SELECT Object_Owner_ID__c FROM Template_Submission_Owner__c WHERE Object_Owner_ID__c IN: caseIds WITH USER_MODE];
        
        List<String> ownerIds = new List<String>();
        for(Template_Submission_Owner__c t: tempSubOwners) {
            ownerIds.add(t.Object_Owner_ID__c);
        }

            
        List<Case> filteredResult = new List<Case>();
        for(Case c: casesToFilter) {
            if(!ownerIds.contains(c.Id)) {
                filteredResult.add(c);
            }
        }

        return filteredResult;
    }

    /*
    * Method Name : initializeQuery
    * Description : create the query for Case
    * Return type : String
    * Parameter   : NA
    */
    public static String initializeQuery(ID objId, string startdate, string enddate)
    {
        
        String sObjName = objId.getSObjectType().getDescribe().getName(); 
        
        String sQueryInit='Select Id,CaseNumber,DCN__c,DCN_Type__c,Origin,Type,Owner_Queue__c, LastModifiedby_Queue__c, Created_By_Queue__c,'
            +'CTCI_List__r.Classification__r.Name,CreatedBy.Name,CTCI_List__r.Intent__r.Name,'+
            +'Product__c,Member_Plan_Id__r.Product__c,Interacting_With__r.Name,Interacting_With_Type__c,Interacting_With_Name__c,'+
            +'Interacting_About__c,Interacting_About_Type__c,Status,Priority,Follow_up_Due_Date__c,CreatedDate,ClosedDate,HO_Dental_Case__c,HO_Medical_Case__c,'
            +'General_Case__c,LastModifiedBy.Name, Complaint__c from Case';
            if(sObjName == OBJECT_ACCOUNT_HUM){
                sQueryInit = sQueryInit + ' Where AccountId = :objId';
            }
            else if(sObjName == OBJECT_MEMBERPLAN_HUM){
                sQueryInit = sQueryInit + ' Where Member_Plan_Id__c = :objId';
            }
            if(String.isBlank(startdate)|| string.isBlank(enddate)){
                sQueryInit = sQueryInit + ' and createddate = TODAY';
            }else{
                sQueryInit = sQueryInit + ' and createddate >= :startDate and createddate <= :endDate';
            }
        sQueryInit = sQueryInit + ' and status != :caseStatus order by CreatedDate DESC LIMIT :limitValue'; 
        return sQueryInit;
    }

    /*
     * Method Name : getcasewrapper
     * Description : wrapper conversion for Case Obj
     * Return type : String
     * Parameter   : NA
     */
    private static String getcasewrapper(List<Case> lstCaseDetails)
    {      
        List<CaseHistory_DTO_HUM> lstCaseWrap = New List<CaseHistory_DTO_HUM>();
        Map<Id,String> dcnUrlMap = DCNRedirection_LC_HUM.generateRedirectToDCNURL(lstCaseDetails);
        if(lstCaseDetails !=NULL )
        {   
            for(case oCase: lstCaseDetails)
            {
                CaseHistory_DTO_HUM oCaseWrap = new CaseHistory_DTO_HUM();
                oCaseWrap.Id = oCase.Id;
                oCaseWrap.sCaseIdVal = oCase.Id;
                oCaseWrap.sCaseNum = oCase.CaseNumber;
                oCaseWrap.sDCN = (String.IsNotBlank(oCase.DCN__c)) ? 'Yes' : 'No';
                oCaseWrap.sdcnURL = dcnUrlMap.get(oCase.Id);
                oCaseWrap.sCreatedDate = (oCase.CreatedDate!=null) ? oCase.CreatedDate.format('MM/dd/yyyy') :'';
                oCaseWrap.sClosedDate = (oCase.ClosedDate!=null) ? oCase.ClosedDate.format('MM/dd/yyyy') :'';
                oCaseWrap.sClassification = oCase.CTCI_List__r.Classification__r.Name;
                oCaseWrap.sIntent = oCase.CTCI_List__r.Intent__r.Name;
                oCaseWrap.sProduct = oCase.Member_Plan_Id__r.Product__c;
                oCaseWrap.sStatus = oCase.Status;
                oCaseWrap.sOrigin = oCase.Origin;
                oCaseWrap.sType = oCase.Type;
                oCaseWrap.sInteractingWith = oCase.Interacting_With_Name__c;
                oCaseWrap.sInteractingWithType = oCase.Interacting_With_Type__c;
                oCaseWrap.sInteractingAbout = oCase.Interacting_About__c;
                oCaseWrap.sInteractingAboutType = oCase.Interacting_About_Type__c;
                oCaseWrap.sPriority = oCase.Priority;
                oCaseWrap.sCreatedBy = oCase.CreatedBy.Name;
                oCaseWrap.sCreatedByQueue = oCase.Created_By_Queue__c;
                oCaseWrap.sOwnerQueue = oCase.Owner_Queue__c;
                oCaseWrap.sLastModifiedByQueue = oCase.LastModifiedby_Queue__c;
                oCaseWrap.bDentalCase = oCase.HO_Dental_Case__c;
                oCaseWrap.bMedicalCase = oCase.HO_Medical_Case__c;
                oCaseWrap.bGeneralCase = oCase.General_Case__c;
                oCaseWrap.sLastModifiedBy = oCase.LastModifiedBy.Name;
                oCaseWrap.sComplaint = oCase.Complaint__c;
                oCaseWrap.sFollowUpDate = (oCase.Follow_up_Due_Date__c !=null) ? oCase.Follow_up_Due_Date__c.format() : '';
                  
                lstCaseWrap.add(oCaseWrap);
            }               
            return JSON.serialize(lstCaseWrap);
        }
        else
        {
            return null;
        }
    }
    
    @AuraEnabled
    public static List<case> fetchCases(string accountId){
        List<Case> caseList = [SELECT Id, CaseNumber, Origin, Classification_Id__c, Intent_Id__c, Product__c, 
        Interacting_With__c, Interacting_With_Type__c, Interacting_About__c, Status, Priority 
        FROM Case where AccountId =:accountId WITH USER_MODE];        
            
        return caseList;
    }
    
    @AuraEnabled(cacheable=true)
    public static string getCaseLoggedData(String caseid) {
        try {
            if(String.isNotBlank(caseid)){
                return JSON.serialize(getLogDetails(caseid));
            }
            return null;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    
    public static List<CaseLoggedInfo_DTO_HUM> getLogDetails(String sCaseId) {
        list<CaseLoggedInfo_DTO_HUM> loggedInfoList = new list<CaseLoggedInfo_DTO_HUM>();
        list<Attachment_Log__c> attachmentInfoList = [SELECT Id, Attachment_Sub_type__c, Created_BY__c,
            CreatedBy.Name, CreatedDate, CreatedBy.Current_Queue__c,CreatedByQueue__c
            FROM Attachment_Log__c
            WHERE IsDeleted__c = false AND Case__c = :sCaseId AND Source_System__c =: LOGGING_CONSTANT_HUM.STRING_CRM_LIGHTNING AND Attachment_Type__c !=:GLOBAL_CONSTANT_HUM.CLAIMS_MANUAL_ENTRY WITH USER_MODE ORDER BY Id DESC];
        
        for (Attachment_Log__c ai : attachmentInfoList) {
            CaseLoggedInfo_DTO_HUM caseInfoDto = new CaseLoggedInfo_DTO_HUM();
              
            caseInfoDto.Id = ai.id;
            caseInfoDto.subType = ai.Attachment_Sub_type__c;
            caseInfoDto.createdDate = ai.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
            caseInfoDto.createdBy = ai.CreatedBy.Name;
            caseInfoDto.createdByQueue = ai.CreatedByQueue__c;
            loggedInfoList.add(caseInfoDto);
        }
            return loggedInfoList;
    }

    class CaseHistory_DTO_HUM
    {
        public String Id  = '';
        public String sCaseIdVal  = '';
        public String sCaseNum  = '';
        public String sDCN = '';
        public String sdcnURL = '';
        public String sCreatedDate  = '';
        public String sClosedDate  = '';
        public String sClassification  = '';
        public string sIntent  = '';
        public String sProduct  = '';
        public String sStatus  = '';
        public String sOrigin  = '';
        public string sType  = '';
        public String sInteractingWith  = '';
        public String sInteractingWithName  = '';
        public String sInteractingWithType  = '';
        public String sInteractingAbout  = '';
        public String sInteractingAboutType  = '';
        public String sPriority  = '';
        public String sCreatedBy  = '';
        public String sCreatedByQueue  = '';
        public String sOwnerQueue  = '';
        public String sLastModifiedByQueue  = '';
        public String sLastModifiedBy  = '';
        public String sFollowUpDate  = '';
        public String sComplaint = '';
        public boolean bDentalCase = false;
        public boolean bMedicalcase = false;
        public boolean bGeneralCase = false;
    }
    
    /*
     * Method Name : updateWhoId
     * Description : Attach processes to the case and return true if the update was successful
     * Return type : Boolean
     * Parameter   : NA
     */
    @AuraEnabled
    public static Boolean updateWhoId(list<string> processIds, string caseid){
        try {
            if(String.isNotBlank(caseid)){
                list<Template_Submission__C> OwnertoUpdate =  [Select ID, Submission_Owner__c, Submission_Owner__r.Object_Owner_ID__c  from Template_Submission__C where ID IN : processIds WITH USER_MODE];
                if(OwnertoUpdate != null && OwnertoUpdate.size() > 0){
                    Set<id> tempSubmissionIds = new Set<Id>();
                    for(Template_Submission__C temp : OwnertoUpdate){
                        tempSubmissionIds.add(temp.Submission_Owner__c);
                    }
                    list<Template_submission_Owner__c> lstUpdate =[select  ID ,Object_Owner_ID__c from Template_submission_Owner__c where id In :tempSubmissionIds WITH USER_MODE];
                    return attachProcessesAndReturnOutcome(OwnertoUpdate, lstUpdate, caseid);
                }
            } else {
               return false;
            }
            return false;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    public static Boolean attachProcessesAndReturnOutcome(list<Template_Submission__C> OwnertoUpdate, list<Template_submission_Owner__c> lstUpdate, string caseid) {
        if(OwnertoUpdate != NULL && !OwnertoUpdate.isEmpty() && lstUpdate != NULL && !lstUpdate.isEmpty()) { 
            for(Template_submission_Owner__c temp1 : lstUpdate) {
                temp1.Object_Owner_ID__c  = caseid;
            }
            if(Schema.SObjectType.Template_submission_Owner__c.fields.Object_Owner_ID__c.isUpdateable()){       
                update as user lstUpdate;
                return true;         
            }  
        }
        return false;
    }
	
	@AuraEnabled
    public static Interaction__c getInteractionDetails(string interactionId){
        if(string.isNotBlank(interactionId) &&  interactionId.length() == 18 && interactionId.startswith(Schema.SObjectType.Interaction__c.keyprefix)){      
            return [SELECT Id, Interaction_Origin__c, Interacting_With_type__c,Caller_Name__c,Interacting_With__c,Interacting_With__r.Name  FROM Interaction__c WHERE Id =: interactionId WITH USER_MODE limit 1];
        }
        else{
            return null;
        }                 
    }
}