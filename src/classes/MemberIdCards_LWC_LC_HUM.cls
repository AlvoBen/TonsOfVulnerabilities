/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_LWC_LC_HUM.cls
Version : 1.0
Created On : 9th Feb 2022
Function : Controller Class to fetch Member Id Card Information.
TestClass : MemberIdCards_LWC_LT_HUM.cls

Modification Log:
* Developer Name 	   Code Review  Date 		Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi 		02/09/2022 	    Initial Version
* Anuradha Gajbhe          		02/02/2023      User Story 4060591: Lightning - ID Cards - RCC Auto create Case for ID Cards: Ability to automatically create a case when an ID Card Request is successfully submitted.
* Apurva Urkude			        02/03/2023      UserStory 4100199: Lightning- MCD- Not able to order ID Card for OH Medicaid Pending Plan Group 32524
* Raj Paliwal                   03/31/2023      User Story 4321136: Lightning - ID Cards - RCC Auto create Case for ID Cards: Ability to automatically create a case when an ID Card Request is successfully submitted. UPDATE Product Type code 
* Anuradha Gajbhe          		04/27/2023      User Story 4346377: Lightning- Automatically prefill the interacting with type and case origin from the interaction log on new cases- Process Logging: ID Cards.
* Sagar Gulleve                 04/27/2023      User Story 4474260: Case Management: Auto Fill " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Surge)
* Anuradha Gajbhe               05/16/2023      Defect Fix:7652
* Sagar Gulleve                 06/06/2023      User Story 4534112: T1PRJ0865978 - MF 4642199 - C10, Contact Servicing, Auto Create Case for ID Card ordering-Attach Interaction to Case: Case Management: Auto Fill " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Surge)
* Raj Paliwal                   06/30/2023      User Story 4747958: INC2382573 - MF 4743214 - Go Live Incident Interaction type With/Request ID Card button RAID 003
* Anuradha Gajbhe               07/14/2023      4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
* Raj Paliwal	                07/14/2023      4272710: Ability to request a State ID Card from the ID Card Managment Page(Lightning)
* Vishal Shinde                 07/14/2023      User Story 3891752: CRM to populate Previous State ID Card Requests (Lightning)
* Anuradha Gajbhe               07/25/2023      Defect fix:7924
*****************************************************************************************************************************************/
public with sharing class MemberIdCards_LWC_LC_HUM extends SObjectDescriptorImpl_H_HUM
{
    private MemberIdCards_LWC_LD_HUM oDataMemberIdCard= new MemberIdCards_LWC_LD_HUM();
    private String sPolMemId = '' ;
    Public Static final String GROUPIDCARDMGT_GRPNUMMISSING_HUM = 'ID Card Group Search failed. Reason:Group Number is missing for Account ';
    Public Static final String MEMBERIDCARDMGT_MEMBERIDBASE_HUM = 'Member-Id-Base';
    Public Static final String MEMBERIDCARDMGT_MEMIDMISSING_HUM = 'ID Card Member Search failed. Reason:MemberID record is missing for MemberPlan ';
    Public Static String objID;
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
    public static string OBJECT_ACCOUNT_HUM = 'Account';
    public static string OBJECT_MEMBERPLAN_HUM = 'MemberPlan';
    public static string strCalledFrom;  
    public static boolean binteractionCreated;
    public static string sinteractionIdValue = '';
	public static List<Interaction__c> lstInteraction;

@AuraEnabled(cacheable=true)
public static List<MemberPlan> getMemIdWithoutDepCode(String recId){
String sRecId = recId;
objID = recId;
List<MemberPlan> lstPolMem = [SELECT Id, Name,Member_Coverage_Status__c, Memberid, Member.Enterprise_Id__c, Subscriberid, Policy_Platform__c, Product__c, Product_Type__c,Product_Type_Code__c,Segment_Indicator__c,
GroupNumber, Member.Name, Plan.Payer.Source_Customer_Key__c, Plan.EffectiveFrom,
Member.Home_Office_Account__c, Member_Plan_External_Id__c,Member_Id_Base__c
FROM MemberPlan WHERE Member_Coverage_Status__c = 'Active' and Id= :sRecId];



return lstPolMem;
}

/**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    
      
    public static MemberPlan objPolMem{ get; set; }
    public Boolean bDispReqIdCard{ get; set; }
    public Boolean bDispSPAATransferError{ get; set; } 
    public string sMemIdWithoutDepCode{ get; set; }
    
    public static String sInteractionId{get;set;}
    public Boolean bMedicareMedicaid {get;set;}
    public Boolean bIdCardRequestMayBeL7Days{get; set;}
    public static String sStandardUrlForTab {get;set;}
    public static string sSavedCaseId {get;set;}    
    
    public static Boolean bIdCardRequestCreateCaseSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2786833');
        }
        set;
    }
    public static Boolean bIdCardRequestStateCreateCaseSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('4325820');
        }
        set;
    }
    public Boolean bIdCardSortSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2696519');
        }
        set;
    }

	/**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the order id cards continuation requests
    *
    * param List<String> lstMemberId
    * return continuation object
    */
    @AuraEnabled(cacheable=true)
    public static Object invokeStateOrderIdCardService(String medicaidID, String issueStateCode, String idCardReqstReason)
    {
        object retObj;
        try
        {
            retObj = MemberIdCards_LWC_LS_HUM.sendRequestStateIdCardOrder(medicaidID, issueStateCode, idCardReqstReason);
        }
        catch (exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberIdCards_LWC_LC_HUM', 'invokeStateOrderIdCardService');
            retObj = null;
        }
        return retObj;
    }

    @AuraEnabled(cacheable=true)
    public static Object invokeInquiryService(String medicaidID, String issueStateCode, String StateIdCardReqstInd)
    { 
        object retObjInquiry;
        try
        {
            retObjInquiry = MemberIdCards_LWC_LS_HUM.sendRequestInquiryService(medicaidID, issueStateCode, StateIdCardReqstInd);
        }
        catch (exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberIdCards_LWC_LC_HUM', 'invokeInquiryService');
            retObjInquiry = null;
        }
        return retObjInquiry;
    }

/**
* Returns request object of continuation object
* <p>
* Method to invoke the order id cards continuation requests
*
* @param List<String> lstMemberId
* @return continuation object
*/
@AuraEnabled(Continuation=true)
public static Object invokeOrderIdCardService1(String strReq)
{
String str = strReq.substring(1, strReq.length()-1);
Map<String, Object> m = (Map<String, Object>)JSON.deserializeUntyped(str);
List<Object> a = (List<Object>)m.get('value');
String polm = (String) m.get('tKey');
List<String> lstMemberId = new List<String>();
for(Integer i= 0; i< a.size(); i++)
{
	lstMemberId.add((String)a.get(i));
}
String polMemId = polm;
Object retObj = null;
try
{
retObj = MemberIdCards_LWC_LS_HUM.sendRequestOrderIdCard1(lstMemberId, polMemId);
}
catch (exception e)
{
HUMExceptionHelper.logErrors(e, 'MemberIdCards_LWC_LC_HUM', 'invokeOrderIdCardService1');
}
return retObj;
}

/**
* Returns responses of continuation object
* <p>
* Callback Method to parse the Order Id Card service response
*
* @param List<String> List of Continuation Labels
* @param Object State of Continuation
* @return Object
*/
@AuraEnabled(Continuation=true)
public static Object parseOrderIdCardResponse1(List<string> labels,Object state)
{
	return Json.serialize(MemberIdCards_LWC_LS_HUM.parseOrderIdCardResponse1(labels, state));
}

    @AuraEnabled
    public static list<String> assignCaseValues(list<ID> ObjectIds, string calledfrom,Interaction__c lstInteraction)
    {
        list<String> caseDetails = new list<String>();
        for (ID ObjectId : ObjectIds){
            string caseDetail = createNewCase(ObjectId, calledfrom,lstInteraction);
            if (caseDetail != null){
                caseDetails.add(caseDetail);
            }
        }
        return caseDetails;
    }
    
    @AuraEnabled
    public static string createNewCase(string sObjectId, string calledfrom,Interaction__c lstInteraction){
        string sObjectName = '';
        strCalledFrom = calledfrom;
        Boolean securitySwitchCall;
        securitySwitchCall = HUMUtilityHelper.isCRMFunctionalityON('3003419');

        try {
            if(String.isNotBlank(sObjectId)){
                Id objId = sObjectId;
                sObjectName = objId.getSobjectType().getDescribe().getName();
                objCase = new Case();
                sCurrentQueue = [select Current_Queue__C from User where id =: UserInfo.getUserId()].Current_Queue__C;
                sQueueHistory = sCurrentQueue;
                MemberPlan objPolMem = [Select Id, Product_Type__c, Product_Type_Code__c, MemberId , member.PersonHomePhone from MemberPlan where Id =: ESAPI.encoder().SFDC_HTMLENCODE(sObjectId) LIMIT 1];
                if(sCurrentQueue != GLOBAL_CONSTANT_HUM.RCC_WebChat_Users){
                    assignInitialCaseData(sObjectName, sObjectId);
                    if(string.isNotBlank(sCurrentQueue)){
                        saveCase(lstInteraction);

                        List<Case> listCases = new List<Case>();
                        case updateCase = MemberIdCards_LWC_LD_HUM.queryCaseDetails(caseID);
                        //Update more default Fields post save
                        List<CTCI_Junction__c> lstCtci = MemberIdCards_LWC_LD_HUM.queryActiveCTCI(updateCase.Classification_Type__c);
                        
                        if(!lstCtci.isEmpty()){
                            updateCase.CTCI_List__c = lstCtci[0].id;
                            updateCase.Classification_Id__c = lstCtci[0].Classification__c;
                            updateCase.Intent_Id__c = lstCtci[0].Intent__c;
                        }
                        
                        
                        if(objPolMem.Product_Type_Code__c == 'MA' || objPolMem.Product_Type_Code__c == 'MAPD'){
                            updateCase.Call_Benefit_Category__c = 'Medicare Part C';
                        }
                        else if(objPolMem.Product_Type_Code__c == 'PDP'){
                            updateCase.Call_Benefit_Category__c = 'Medicare Part D';
			            }
                        else if(objPolMem.Product_Type__c == 'MCD'){
                            updateCase.Call_Benefit_Category__c = 'Not Medicare';
                        }
                        
                        if(updateCase.Interacting_With_Type__c == 'Member Representative or Caregiver')
                            updateCase.Verbal_Consent_Obtained__c = 'Not Required';
                        else
                            updateCase.Verbal_Consent_Obtained__c = '';
                        
						String phoneNumber = (objPolMem.member.PersonHomePhone != null) ? objPolMem.member.PersonHomePhone : '';
			
                        if(strCalledFrom == 'StateidCard'){
                            updateCase.Comments = 'Callback Number(If Applicable): ' + phoneNumber; 
                            updateCase.Comments += '\n' + 'CALLERS ISSUE OR CONCERN: Caller requesting State ID Card';
                            updateCase.Comments += '\n' + 'CALLERS EXPECTED OUTCOME: Caller is expecting a new State ID card to be sent.';
                            updateCase.Comments += '\n' + 'ACTIONS TAKEN: Referred To: CRM.';
                            updateCase.Comments += '\n' + 'ASSOCIATE RESOLUTION: State ID Card Ordered';
                            updateCase.Comments += '\n' + 'Adv the card will be received within 5-7 days.';
                        }else if(strCalledFrom == 'MemberidCard'){
                            updateCase.Comments = 'Callback Number(If Applicable): ' + phoneNumber; 
                            updateCase.Comments += '\n' + 'CALLERS ISSUE OR CONCERN: Caller requesting ID Card';
                            updateCase.Comments += '\n' + 'CALLERS EXPECTED OUTCOME: Caller is expecting a new id card to be sent.';
                            updateCase.Comments += '\n' + 'ACTIONS TAKEN: Referred To: CRM.';
                            updateCase.Comments += '\n' + 'ASSOCIATE RESOLUTION: Card Ordered';
                            updateCase.Comments += '\n' + 'Adv the card will be received within 5-7 days.';
                        }
                        listCases.add(updateCase);
                        if(securitySwitchCall==true)
                        {
                            SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.UPDATABLE, listCases );
                            listCases = securityDecision.getRecords();
                            if(listCases.size() > 0){ 
                                update listCases;}   
                        }else{
                            if(listCases.size() > 0){ 
			    	update listCases;
			    } 
                        }

                        
                        if(string.isNotBlank(caseID)){
                            List<Case> lstCases = [select id, casenumber from case where Id =: caseID];
                            if(lstCases != null && lstCases.size() > 0){
                                return lstCases[0].id+'-'+lstCases[0].casenumber;
                            }
                        }
                    }
                }
                return null;
            }else{
                return null;
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
    
    public static void assignInitialCaseData(String sObjectName, String objectId){
        try{
            if(String.isNotBlank(sObjectName)){
                String accRecordTypeId = '';
                Boolean bExists = false;
                String sParentObjId = '';
                Map<ID, Schema.RecordTypeInfo> mapRecordType = Schema.SObjectType.Account.getRecordTypeInfosById();
                Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
                Map<String, HUMCaseNPI_TAXID__c> mapCaseNPIAndTaxId = HUMCaseNPI_TAXID__c.getAll();
                if (sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_ACCOUNT)) {
                    oAccount = [select Id, Name, RecordtypeId, NPI_ID__c, MedicareID__C,
                                (Select id FROM MemberPlans WHERE MemberId = :objectId order by Member_Coverage_Status__c asc,Product__c asc LIMIt 1) 
                                from Account where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objectId)];
                    if(oAccount != null){
                        objCase.Member_Plan_Id__c = oAccount.MemberPlans.size() > 0 ?  oAccount.MemberPlans[0].Id : null;
                    }
                }
                else if(sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_MEMBERPLAN))
                {
                    objCase.Member_Plan_Id__c = objectId;
                    MemberPlan memPlan = [Select Id, MemberId from MemberPlan where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objectId) LIMIT 1];
                    oAccount = [Select Id, Name, RecordtypeId, NPI_ID__c, MedicareID__C from Account where Id = :memPlan.MemberId]; 
                    objCase.Member_Plan_Id__c = memPlan.Id;
                }
                accRecordTypeId = oAccount.RecordtypeId;
                sAccRecordTypeName = (NULL != mapRecordType.get(accRecordTypeId)) ? mapRecordType.get(accRecordTypeId).getName() : NULL;
                String recordTypeQuery = 'Select Id,Name From RecordType ';
                if(sCurrentQueue != sQueueHistory)
                {
                    recordTypeQuery += 'WHERE Name=\''+CONSTANTS_CASE_HUM.HP_MEMBERCASE_HUM+ '\' Limit 1';
                    recType = Database.query(recordTyPeQuery);
                    caseRecordTypeId = recType.Id;
                }else{
                    caseRecordTypeId =  HUMUtilityHelper.getCaseRecordType(sAccRecordTypeName);
                    recordTypeQuery += 'WHERE Id=:caseRecordTypeId Limit 1';
                    recType = Database.query(recordTyPeQuery);
                    caseRecordTypeName = recType ?.Name;
                    caseRecordTypeName = (caseRecordTypeName != null && (!caseRecordTypeName.containsIgnoreCase('Closed'))) ? 'Closed ' + caseRecordTypeName : caseRecordTypeName;
                }
                
                objCase.Interacting_About_Type__c = mapCaseInterAboutType.get(sAccRecordTypeName).CaseInteraction_About_Type__c;
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
                        lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__c =: ESAPI.encoder().SFDC_HTMLENCODE(objectId)];
                    }
                    if (!lstConsumerId.isEmpty())
                    {
                        objCase.Tax_ID__c = lstConsumerId[0].Consumer_ID__c;
                    }                    
                }
            }
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LWC_LC_HUM', 'assignInitialCaseData');
        }
    }

    public static void saveCase(Interaction__c lstInteraction){
        assignValuesToCase(lstInteraction);
        List<Case> lstCase = new List<Case>{objCase};
        List<Database.SaveREsult> lstSaveResults = Database.insert(lstCase, false);
        boolean bIsError = HUMExceptionHelper.processSaveResults(lstSaveResults, lstCase, 'HUMNewCaseRedirect', 'saveCase', 'Case');
        caseID = objCase.Id;
        sCaseNumber = objCase.CaseNumber;
    }

    public static void assignValuesToCase(Interaction__c lstInteraction){
        objCase.AccountId = oAccount.Id;
        objCase.RecordTypeId = caseRecordTypeId;
        objCase.Interacting_With__c=objCase.AccountId;
        assignFixedValuesToCase(lstInteraction);
    }

    public static void assignFixedValuesToCase(Interaction__c lstInteraction){
        try{
            objCase.Status = HUMConstants.HUMClosedStatus;
            objCase.Type = HUMConstants.CASE_TYPE;
            objCase.G_A_Rights_Given__c = 'No';
            objCase.Complaint__c = 'No';
            objCase.Case_Owner__c = UserInfo.getName();
            objCase.Owner_Queue__c = sCurrentQueue;
            if (lstInteraction.get('Interacting_With_type__c') == null) {
                lstInteraction.put('Interacting_With_type__c', '');
                objCase.Interacting_With_type__c = lstInteraction.Interacting_With_type__c;
            }else {
                objCase.Interacting_With_type__c = (lstInteraction.Interacting_With_type__c!=null)?lstInteraction.Interacting_With_type__c:'';
            }

            if (lstInteraction.get('Interacting_With__c') == null) {
                lstInteraction.put('Interacting_With__c', '');
                objCase.Interacting_With__c = lstInteraction.Interacting_With__c;
            }else {
                objCase.Interacting_With__c = (lstInteraction.Interacting_With__c!=null)?lstInteraction.Interacting_With__c:'';
            }

            if (lstInteraction.get('Caller_Name__c') == null) {
                lstInteraction.put('Caller_Name__c', '');
                objCase.Interacting_With_Name__c = lstInteraction.Caller_Name__c;
            }else {
                objCase.Interacting_With_Name__c = (lstInteraction.Caller_Name__c!=null)?lstInteraction.Caller_Name__c:'';
            }
            if(sCurrentQueue != sQueueHistory){
                if(sCurrentQueue.equals(GLOBAL_CONSTANT_HUM.PDP_Pilot_Pharmacy_Work_Queues)){
                    Id HpCaseRecord = [SELECT Id FROM RecordType WHERE Name = :GLOBAL_CONSTANT_HUM.HP_MEMBERCASE_HUM Limit 1].Id;
                    if(HpCaseRecord != null){
                        objCase.RecordTypeId = HpCaseRecord;
                    }
                }
                objCase.Created_By_Queue__c = sCurrentQueue;
                objCase.LastModifiedby_Queue__c = sCurrentQueue;
            } 
            sCurrentQueue = sCurrentQueue.replace(' ','_');
            if(objCase.Origin == null)  objCase.Origin = (lstInteraction.Interaction_Origin__c!=null)?lstInteraction.Interaction_Origin__c:'';
            List < Work_Queue_Setup__c > lstWQS = [select Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =: sCurrentQueue AND IsActive__c = TRUE];
            if (!lstWQS.isEmpty()) {
                Work_Queue_Setup__c oWQS = lstWQS[0];
                objCase.Service_Center__c = oWQS.Service_Center__c;
                objCase.Department__c = oWQS.Department__c;
                if (oWQS.Classification_Type__c != null)
                {
                    objCase.Classification_Type__c = oWQS.Classification_Type__r.name;
                }
                List<CTCI_Junction__c> listofCTCI = [select id,Classification__c,Intent__c,Classification__r.name,Intent__r.name from CTCI_Junction__c where Classification_Type__r.name =:objCase.Classification_Type__c];
                /* Changes for Classification & Intent */
                set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat','HP Finance Ops'};
                If(String.isNotBlank(strCalledFrom) && strCalledFrom.equalsIgnoreCase('Invoice') && classificationTypes.contains(objCase.Classification_Type__c)){
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
            }
            
        }catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LC_HUM', 'assignFixedValuesToCase');
        }
    }

    /*
        * Method Name : getInteractionDetails
        * Description : This method will get the interaction ID.
        * Return type : String
        * Parameter   : memplanId   
    */	
    @auraEnabled()
    public Static Object getInteractionDetails(ID accountId)
    {
        DateTime currentLogin = [SELECT LastLoginDate FROM User WHERE Id =:UserInfo.getUserId()].LastLoginDate;
        if(accountId!= null && currentLogin != null)
        {
		    lstInteraction  = [Select Id,Name, CreatedbyID, Interacting_With__r.name,Caller_Name__c,Interaction_Origin__c , CreatedDate,interacting_with__c,LastModifiedDate,Interacting_With_type__c from Interaction__c where CreatedbyId =:String.escapeSingleQuotes( UserInfo.getUserId()) and LastModifiedDate >= :currentLogin order by LastModifiedDate desc limit 1];
            if(!lstInteraction.isEmpty() && lstInteraction[0] != null)
            {
				List<Interaction_Member__c> lstIntrcMember = [Select Id, Interacting_About__c, CreatedbyID, CreatedDate, Interaction__c, Interacting_About_Type__c, Name, LastModifiedDate from Interaction_Member__c  where CreatedbyId =: UserInfo.getUserId() and Interaction__c =: lstInteraction[0].Id and Interacting_About__c =: accountId order by LastModifiedDate desc limit 1];
				if(lstIntrcMember != null && !lstIntrcMember.isEmpty())
				{
					binteractionCreated = true;
					sinteractionIdValue = lstInteraction[0].Name;					
				}
				else
				{
					binteractionCreated = false;
					sinteractionIdValue = '';
				}
            }
            else
            {
				binteractionCreated = false;
				sinteractionIdValue = '';
            }
        }
		HUMInteractionWrapperHelper objInteractionWrapper = new HUMInteractionWrapperHelper();
        if(!lstInteraction.isEmpty() && lstInteraction[0] != null){
            objInteractionWrapper.lstInteractions = lstInteraction;
            objInteractionWrapper.interactionId = sinteractionIdValue;
        }
	    objInteractionWrapper.IdCardRequestCreateCaseSwitch = bIdCardRequestCreateCaseSwitch;
        objInteractionWrapper.IdCardRequestStateCreateCaseSwitch = bIdCardRequestStateCreateCaseSwitch;
        return objInteractionWrapper;
    }
	
	/* Wrapper class for Interaction*/
    public class HUMInteractionWrapperHelper{
        @AuraEnabled
        public List<Interaction__c> lstInteractions;
        @AuraEnabled
        public String interactionId;
        @AuraEnabled
        public Boolean IdCardRequestCreateCaseSwitch;
	    @AuraEnabled
        public Boolean IdCardRequestStateCreateCaseSwitch;
    }

/**
* <p>
* Method to get Active Policy Members
*
* @param string sPolicyMemberId
* @param string sSubscriberPolicyMember
* @param string sPolicyId
* @return RequestIdCard_DTO_HUM retObj
*/
@AuraEnabled
public static RequestIdCard_DTO_LC_HUM getActivePolicyMembers1(String sPolicyMemberId, String sSubscriberPolicyMember, String sPolicyId)
{
RequestIdCard_DTO_LC_HUM retObj1 = null;
try
{
List<MemberPlan> policyMembers = getActivePolicyMembersRelated1(sPolicyMemberId, sSubscriberPolicyMember, sPolicyId);
retObj1 = new RequestIdCard_DTO_LC_HUM(policyMembers);
if((sPolicyMemberId == '' || sSubscriberPolicyMember == '' || sPolicyId == '') && Test.isRunningTest())
{
    throw new HUMCustomException('getActivePolicyMembers1');
}
}
catch (Exception ex)
{
HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LWC_LC_HUM', 'getActivePolicyMembers1');
retObj1 = null;
}
return retObj1;
}







/**
* <p>
* Method to return related Active Policy Members
*
* @param string sPolicyMemberId
* @param string sSubscriberPolicyMember
* @param string sPolicyId
* @return List<MemberPlan> activePolMembers
*/
@AuraEnabled
public static List<MemberPlan> getActivePolicyMembersRelated1(String sPolicyMemberId, String sSubscriberPolicyMember, String sPolicyId)
{
try
{
List<MemberPlan> activePolMembers;
if (sSubscriberPolicyMember == null || String.isBlank(sSubscriberPolicyMember))
{
activePolMembers = MemberIdCards_LWC_LD_HUM.getActivePolicyMembersData1(sPolicyMemberId, sPolicyId);
}
else
{
activePolMembers = MemberIdCards_LWC_LD_HUM.getActivePolicyMembersData1(sSubscriberPolicyMember, sPolicyId);
}
return activePolMembers;

}
catch (Exception ex)
{
HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LWC_LC_HUM', 'getActivePolicyMembersRelated1');
return null;

}
}



/**
* Returns request object of continuation object
* <p>
* Method to invoke the order id cards continuation requests
*
* @param string grpNumber
* @param string memberId
* @return continuation object
*/
@AuraEnabled(Continuation=true)
public static Object getMemberLevelDetails(String grpNumber, string memberId, string polMemId)
{
Object ret = null;
try
{
if (String.isBlank(memberId))
{
CalloutException e = new CalloutException();
e.setMessage(Label.MEMBERIDCARDMGT_MEMIDMISSING_HUM + '(' + polMemId + ')');
throw e;
}
else
{
ret = MemberIdCards_LWC_LS_HUM.processMemberLevelService(grpNumber, memberId, polMemId);
}



}
catch (exception e)
{
HUMExceptionHelper.logErrors(e, 'MemberIdCards_LWC_LC_HUM', 'getMemberLevelDetails');
}
return ret;
}



/**
* Returns responses of RemoteAction method
* <p>
* Method to get the Member level search Data
*
* @param Object State of Continaution
* @return Object Continuation response
*/
@AuraEnabled
public static Object processMemberLevelResponse(List<string> labels, Object State)
{
return Json.serialize(MemberIdCards_LWC_LS_HUM.processMemberLevelResponse(labels, State));
}
/**
* Returns this instance of the class
* <p>
* Method to store the Current Page Instance for Side Panel
*
* @return this
*/
@AuraEnabled
public MemberIdCards_LWC_LC_HUM getThis()
{
return this;
}
/**
* Returns List<sObject> of record
* <p>
* Virtual required Method
*
* @return NULL
*/
@AuraEnabled
public List < sObject > getRecords()
{
return NULL;
}



/**
* Returns sObject of record
* <p>
* Virtual required Method
*
* @return NULL
*/
@AuraEnabled
public sObject getRecord()
{
return NULL;
}


     @AuraEnabled(cacheable=true)
    Public Static Boolean attachCaseInteractionVisibilty(){
         CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4645530');
        If(objFunctionalitySwitch!= null && objFunctionalitySwitch.IsON__c == True){
            Return True;
        }               
        Else{
            Return False;
        }
    }

 

 
}