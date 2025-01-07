/*******************************************************************************************************************************
Apex Class Name : CaseDetails_LC_Hum
Version         : 1.0
Created On      : 12/06/2021
Function        : This class is used for create and show case details on edit case page
Test Class      : CaseDetails_LT_Hum

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                    12/06/2021               Original Version
*    1.1           Ritik Agarwal                                    12/16/2021               As part of this US:(1464414) it will only work for(301,302 PS)
*    1.2           Ritik Agarwal                                     01/13/2022               apply condition to create case from memberplan page and link it to that memberplan
*    1.3           Ritik Agarwal                                     02/04/2022               apply condition to populate memberplan from HP tab while creating case
*    1.4           Ritik Agarwal                                     02/10/2022               add profile parameter in wrapper to get current user prfofile
*    1.5           Kajal Namdev                                      03/29/2022               Add changes related to log cod visibility US_3192496
*    1.6           M k Manoj/Tummala Vijaya Lakshmi                  06/28/2022               US-3278156 ,US-3327799 For unknown accounts case creation And case edit functionality
*    1.7           Gowhami Thota                                     06/29/2022               US-3087406, US-3087423 Case Management - Case Closure - All HP And Non HP Case Record Types
*    1.8           Nirmal Garg                                       09/26/2022               US3756469-PCP Reassignment
*    1.9            Prasuna Pattabhi                                 09/30/2022               US 3730127 - Edit And Closed Edit Medicare Case
*    2.0          Gowthami/Vijaya                                   09/30/2022               DF-6271 Fix
*    3.0          Manohar                                           10/12/2022               Code changes for record type changes for RCC users with 206 permission set
*    3.1            Prasuna Pattabhi                                 10/27/2022               US 3934433 - Medicare Calls Case Comments
*    3.3          Prasuna Pattabhi                                 11/07/2022              US 3934433 - return stmt fix
*    3.4          Prasuna Pattabhi                                 12/05/2022              US_4016910,US_4020206,US_4020208,US_4020207
*    3.5          Jasmeen Shangari								   12/05/2022		         US-4016899, Creditable coverage Process changes to launch Case Edit to pre-populate specific values
*    3.6          Prasuna Pattabhi                                 12/07/2022              US_4016910,US_4020206,US_4020208,US_4020207
*    3.7          Prasuna Pattabhi                                 12/07/2022              US_4016910,US_4020206,US_4020208,US_4020207
*    3.8          Prasuna Pattabhi                                 12/08/2022              US_4016910,US_4020206,US_4020208,US_4020207
*    3.8          Prasuna Pattabhi                                 12/21/2022              US_4020210 QAA Changes
*    3.9          Gowthami Thota                                   02/01/2023              US-4025730 - T1PRJ0170850 - Lightning-Case management- Automatic Case record type switch logic implementation
*    3.9          Swapnali Sonawane                                01/31/2023              US 3897001: T1PRJ0170850 - MF 21052 - Lightning: - Case comments sent to ePost- Pharmacy- Lego
*    4.0          Prasuna Pattabhi                                 2/17/2023              Classification and Interent Soritng Fix
*    4.1          Prasuna Pattabhi                                 2/27/2023              US 4286868 Automatically prefill the Interacting With Type and Case Origin
*    4.2          Prasuna Pattabhi                                 3/31/2023              US 4429882 Case Edit Page to retain Quick Start info
*    4.3          Prasuna Pattabhi                                 4/05/2023              Creditable Coverage Defect Fix
*    4.4          Prasuna Pattabhi                                 4/05/2023              DF7484 Fix
*	 4.5		  Gowthami Thota								   4/5/2023				  US 4461952 Auto Fill "Interacting With" & "Interacting With Name"
*    4.6          Prasuna Pattabhi                                 4/12/2023              DF7525 Fix
*    4.6          Pooja Kumbhar                                    4/13/2023              US 4425976 Case Mgt: Disable Buttons for Auto-Routing templates on Case Edit Page
*    4.7          Akshay Gulve                                     4/17/2023              US: 4475037 - Case Management: Auto Fill "Case Origin" "Interacting with type" " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Jaguars) 
*    4.8          Pooja Kumbhar                                    4/27/2023              US 4425976 - DF - 7582 fix
*	 4.9		  Abhishek Mangutkar							   4/28/2023			  US4551539 - PCP Disable buttons
*    5.0          Prasuna Pattabhi                                 06/14/2023             US 4467570 and 4255214 : Launching and opening Newborn Notification Template (baby bot) for Medicaid (Lightning)
*    5.1          Pooja Kumbhar	                                   06/29/2023             US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
*    5.2          Gowthami Thota					               07/11/2023		      US4625841: Case Management (Provider) - Case Edit Page- Ability to edit Related Account section when case origin is Correspondence
*    5.3         Prasuna Pattabhi                                 07/13/2023             US 4467570 and 4255214 : Enable Buttons for Babybot on case edit page
*    5.4         Prasuna Pattabhi                                 07/27/2023             US 4876326 Restrict the User to Close and Transfer the Case without attaching Babybot for Medicaid from Case Details page
*    5.5         Prasuna Pattabhi                                  08/24/23                 US 4412371 Market Credentialing Task Fields
*    5.6         Vani shrivastava                               09/15/2023              US 4918290: T1PRJ0865978 - C06- Case Management- Close Case button should have correct toast message verbiage    
*    5.6         Prasuna Pattabhi                                  10/04/23                 US 4412371 Market Credentialing Task Fields Switch Changes
*	 5.7    	 Jasmeen Shangari								   10/5/2023			  DF-8188 on Click of save And trasfer button And close case button, toast message Is Not seen rather case Is getting closed on case edit page And User without 400 PS also able to edit the case
*    5.7         Prasuna Pattabhi                                  10/05/23                 US 4828071 Disable Case Editing for Legacy Delete Members and Policies
*    5.4         Santhi Mandava                                   10/12/2023             US 4884468: Member Search lookup in Related Accounts Section  
*    5.5         Santhi Mandava                                   02/28/2024             User Story 5243585: T1PRJ1132745 - MF28328 - C06; Case Management - Classic & Lightning - Oklahoma MCD Update Medicaid PAR Provider Not Accepting Routing rules (MCD OK MBRCALLS 1.1)
*    5.6         Santhi Mandava                                   02/28/2024             User Story 5231359: T1PRJ1132745 - MF 28326 - C06; Case Management - Oklahoma MCD Lighting - Auto Routing Feature for State of Issue (MCD OK G&A 1.1)
*/

public with sharing class CaseDetails_LC_Hum {

    public Static boolean bQueueUser = false;  
    public Static String sCurrentQueue = '';
    public Static String sQueueHistory = '';
    public Static Case objCase = null;
    public Static String caseRecordTypeName = null;
    public Static String caseRecordTypeId = '';
    public Static String sAccRecordTypeName = '';
    public Static Account oAccount = NULL;
    public Static Map<Id,List<classificationIntentModel.optionModel>> classificationToIntent;
    public Static Map<String,Map<String,String>> mpOfclassificationIntentToCTCIId;
    public Static RecordType recType = null;
    public Static case editCaseData = null;
    public Static InputParams inpParams;
    public Static User currentUser;
    public Static Map<Id, CTCI_Junction__c> mapOfCTCIIdToCTCI;
    public Static CTCI_Junction__c ctciObj;
    public Static final List<String> lstWQNames = new List<String>{'Louisville RSO Calls Support', 'LV Partner Site RSO Calls C3', 'LV Partner Site RSO Calls Results Co', 'LV Partner Site RSO Calls Xerox', 'LV Partner Site RSOCalls Teleperformance','Group Service Operations Calls'}; 
    public Static final List<String> processFlowNames = New List<String>{'Medicare Part D Redetermination','Medicare/Medicaid Expedited Appeal','Medicare Good Cause Reinstatement Form','Medicaid PAR Provider Not Accepting','QAA Complaint','Medicare Creditable Coverage Attestation','Medicaid Newborn Notification','Medicare and Medicaid Other Insurance Form'};
    public Static final List<String> GoodCauseAutoRouteClassificationTypes = New List<String>{'Calls (RSO)','Guidance Center','Web Chat', 'RCC Web Chat'};
	public Static final List<String> LST_MEDICAIDPAR_FIELDS = New List<String> {'Provider State','Group State','Is this a behavioral health group','Is this a behavioral health provider'};
    Public Static final String WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT = 'Louisville RSO Oral Grievance Oversight';
    /*
* Method Name : populateCaseData
* Description : method used to create a new case
* Return type : object
* Parameter   : objId ,isPharmacyLogging
*               objId - objectId that will be passed from LWC from under which case needs to be ceated.
*/
    @AuraEnabled
    public Static Object populateCaseData(String objectID, String params) {
        Id objID = objectID;
        inpParams = (InputParams) JSON.deserialize(params, InputParams.class);
        return fetchCaseData(objID);
    }

/*
* Method Name : populateAccountRecordTypeName
* Description : method used to get the account recordtype name of a New case
* Return type : String
* Parameter   : objId
*               objId - objectId that will be passed from LWC from under which case needs to be ceated.
*/
@AuraEnabled
Public Static String populateAccountRecordTypeName(String objectID) {
    Id objID = objectID;
    String sAccRecTypeName =' ';
    If(objectID.startsWith('500')){
        Case oCase=[select id, AccountId, Account.RecordType.name from Case where id=: objID limit 1];
        objID = oCase.AccountId;
        sAccRecTypeName = oCase.Account.RecordType.name;
    }else if(objectID.startsWith('001')){
        Account acc =[select id, RecordType.name from account where id=: objID limit 1];
        sAccRecTypeName = acc.Recordtype.name;
    }
    return sAccRecTypeName;

}

    /*
* Method Name : fetchCaseData
* Description : this method is resuable method that will work for other LOBs as well from where we need to save case
* Return type : object
* Parameter   : objId
*/
    public Static Object fetchCaseData(Id objID) {
        try{
            objCase = new Case();
            currentUser = [select Current_Queue__C, Profile.Name from User where id =: UserInfo.getUserId()];
            sCurrentQueue = currentUser.Current_Queue__C;
            sQueueHistory = sCurrentQueue;
            String SObjectName = objID.getSobjectType().getDescribe().getName();
            //this if needed when we are on Edit case and then we have caseId onload of LWC page and using that caseId we will fetch its accountID
            if(SObjectName == 'Case'){
                //all edit case related operation needs to handle in caseEdit_LC_HUM apex
                editCaseData = caseEdit_LC_HUM.fetchCaseData(objID, objCase);
                editCaseData.Autoroute_Status__c = string.isNotBlank(editCaseData.Autoroute_Status__c) ? editCaseData.Autoroute_Status__c : '';
                objID = editCaseData.AccountId;
                caseRecordTypeName = editCaseData.Recordtype.Name;
                funcOnPageLoad(CONSTANTS_CASE_HUM.OBJECT_ACCOUNT, String.valueOf(objID));
            }else{
                //else is needed when user is on new case page
                funcOnPageLoad(SObjectName, String.valueOf(objID));
            }

            if (!String.isBlank(sCurrentQueue)) {
                return assignValuesToCase();
            }
            throw Lib_Security_LH_HUm.createException('Current User is not assigned to any Queue',null);
        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'caseDetails_LC_HUM', 'fetchCaseData');
            if (String.isBlank(sCurrentQueue)) {
                throw Lib_Security_LH_HUm.createException('Current User is not assigned to any Queue',null);
            }
            throw Lib_Security_LH_HUm.createException('UNKNOWN'+ex.getStackTraceString(),ex);
        }
    }

    /*
* Method Name : funcOnPageLoad
* Description : This method is used to assign recordtype id for case if it is created from account
* Return type : void
* Paramater   : ojectName,objID
*/
    public static void funcOnPageLoad(String sObjectName, String objID) {
        try{
            String accRecordTypeId = '';
            if (sObjectName != NULL) {
                Boolean bExists = false;
                Map <ID, Schema.RecordTypeInfo > mapRecordType = Schema.SObjectType.Account.getRecordTypeInfosById();
                Map <String, HUMCaseInteractionAboutType__c > mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
                Map <String, HUMCaseNPI_TAXID__c > mapCaseNPIAndTaxId = HUMCaseNPI_TAXID__c.getAll();
                 if (sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_ACCOUNT)) {
                    // if required for when user is creating case from HP tab then MED product if any then link it to case otherwise left blank
                    if(inpParams != NULL && String.isNotBlank(inpParams.callerPageName) && inpParams.callerPageName == 'Humana_Pharmacy_Tab'){
                        oAccount = [select Id, Name, RecordtypeId, NPI_ID__c, MedicareID__C,
                                    (Select id FROM MemberPlans WHERE MemberId = :objID and Product__c='MED' order by Member_Coverage_Status__c asc LIMIt 1)
                                    from Account where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objID)];
                        objCase.Member_Plan_Id__c = oAccount.MemberPlans.size() > 0 ?  oAccount.MemberPlans[0].Id : null;
                    }
                    // else case is created from other than HP tab
                    else{
                        oAccount = [select Id, Name, RecordtypeId, NPI_ID__c, MedicareID__C,
                                    (Select id FROM MemberPlans WHERE MemberId = :objID order by Member_Coverage_Status__c asc,Product__c asc LIMIt 1)
                                    from Account where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objID)];
                   }
                }
                else if(sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_MEMBERPLAN))
                {
                    objCase.Member_Plan_Id__c = objID;
                    MemberPlan memPlan = [Select Id, MemberId from MemberPlan where Id =: ESAPI.encoder().SFDC_HTMLENCODE(objID) LIMIT 1];
                    oAccount = [Select Id, Name, RecordtypeId, NPI_ID__c, MedicareID__C from Account where Id = :memPlan.MemberId];
                }

                accRecordTypeId = oAccount.RecordtypeId;
                sAccRecordTypeName = (NULL != mapRecordType.get(accRecordTypeId)) ? mapRecordType.get(accRecordTypeId).getName() : NULL;

                if(editCaseData == null){
                    String recordTyPeQuery = 'Select Id,Name From RecordType ';
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
                    }
                    objCase.RecordTypeId = caseRecordTypeId;

                    for (String sCaseNPITax: mapCaseNPIAndTaxId.keySet()) {
                        if (!String.isBlank(sCaseNPITax) && sCaseNPITax.equalsIgnoreCase(sAccRecordTypeName)) {
                            bExists = true;
                            break;
                        }
                    }

                    if (bExists) {
                        objCase.NPI_ID__c = oAccount.NPI_ID__c;
                        List <ConsumerID__c> lstConsumerId = NULL;
                        if (sObjectName.equalsIgnoreCase(CONSTANTS_CASE_HUM.OBJECT_ACCOUNT)) {
                            lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__c =: ESAPI.encoder().SFDC_HTMLENCODE(objID)];
                        }
                        if (!lstConsumerId.isEmpty())
                        {
                            objCase.Tax_ID__c = lstConsumerId[0].Consumer_ID__c;
                        }
                    }
                }
                objCase.Interacting_About_Type__c = mapCaseInterAboutType.get(sAccRecordTypeName).CaseInteraction_About_Type__c;

            }
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CaseDetails_LC_Hum', 'funcOnPageLoad');
        }
    }


    /*
* Method Name : assignValuesToCase
* Description : This method is used to assign values to case fields before creating a case.
* Return type : Object
*/
    @testVisible private static Object assignValuesToCase() {
        objCase.AccountId = oAccount.Id;
        return assignFixedValuesToCase();
    }

    /*
* Method Name : assignFixedValuesToCase
* Description : This method is used to assign the fixed values to case fields before creating a case.
* Return type : Void
* Paramater   : N/A
*/
    private static object assignFixedValuesToCase() {
        try {
		    if(String.isBlank(objCase.Case_Owner__c))
            objCase.Case_Owner__c = UserInfo.getName();
            // this IF to prevent this block of code to run in edit case
            if(editCaseData == null){
                objCase.Interacting_With__c=oAccount.Id;
                objCase.Owner_Queue__c = sCurrentQueue;

				if(inpParams.interactionId != null){
                    List<Interaction__c> caseInteraction = [select Interacting_With_type__c, Interaction_Origin__c,Interacting_With__c, Caller_Name__c from Interaction__c where Id =: inpParams.interactionId];
                    if(!caseInteraction.isEmpty()){
                        objCase.Origin = caseInteraction[0].Interaction_Origin__c;
                        objCase.Interacting_With_Type__c = caseInteraction[0].Interacting_With_type__c;
                        objCase.Interacting_With__c = caseInteraction[0].Interacting_With__c;
                        objCase.Interacting_With_Name__c = caseInteraction[0].Caller_Name__c;
                    }
                }
				
                /* REQ 1445907 only populate this field for 205 PDP pilot user*/
                if (sCurrentQueue != sQueueHistory) {
                    if (sCurrentQueue.equals(CONSTANTS_CASE_HUM.PDP_Pilot_Pharmacy_Work_Queues)) {
                        caseRecordTypeName = CONSTANTS_CASE_HUM.HP_MEMBERCASE_HUM;
                        objCase.RecordTypeId = caseRecordTypeId;
                    }
                    objCase.Created_By_Queue__c = sCurrentQueue;
                    objCase.LastModifiedby_Queue__c = sCurrentQueue;
                }

                sCurrentQueue = sCurrentQueue.replace(' ', '_');

                if (objCase.Origin == null) {
                    objCase.Origin = CONSTANTS_CASE_HUM.CASE_ORIGIN;
                }


                List <Work_Queue_Setup__c> lstWQS = [select Service_Center__c,Work_Queue_Name__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =: sCurrentQueue AND IsActive__c = TRUE];
                if (!lstWQS.isEmpty()) {
                    Work_Queue_Setup__c oWQS = lstWQS[0];
                    objCase.Service_Center__c = oWQS.Service_Center__c;
                    objCase.Department__c = oWQS.Department__c;
                    if (oWQS.Classification_Type__c != null)
                        objCase.Classification_Type__c = oWQS.Classification_Type__r.name;
                }
            }
            return Modal();
        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'CaseDetails_LC_Hum', 'assignFixedValuesToCase');
            return ex.getStackTraceString();
        }
    }

    public Static Object Modal() {
        classificationIntentModel objCTCI = new classificationIntentModel();
        objCTCI.classificationToIntentValues = (List<classificationIntentModel.optionModel>) fetchClassification( objCase.Classification_Type__c);
        objCTCI.classificationToIntent = classificationToIntent;
        objCTCI.mpOfclassificationIntentToCTCIId = mpOfclassificationIntentToCTCIId;
        objCTCI.intentName = editcaseData?.Intent_Id__c; //only has a chance of value when user is on edit case
        objCTCI.classificationName = editcaseData?.Classification_Id__c;//only has a chance of value when user is on edit case
        objCTCI.mapOfCTCIIdToCTCI = mapOfCTCIIdToCTCI;
        objCTCI.ctciObj = ctciObj;
        objCase.OwnerId = editcaseData?.OwnerId;
        List<classificationIntentModel.optionModel> optionBox = (List<classificationIntentModel.optionModel>) objCTCI.collectionToOption(PharmacyCaseComment_LC_HUM.getCaseCommentOptions(objCase.Classification_Type__c));
        CaseCommentModal objComment = new CaseCommentModal(optionBox, '', '');
        List<classificationIntentModel.optionModel> callBenefitCategoryValues = new List<classificationIntentModel.optionModel>();
        Map<String,boolean> userAccess = checkPermisionSetAccess();
        boolean bMedicareCalls = checkMedicareCalls(objCase.Member_Plan_Id__c,objCase.Origin);
        boolean bMediCCUpdate = false;
        if(bMedicareCalls == true && editcaseData!=null){
            bMediCCUpdate  = getCCMedicareCalls(editcaseData.Id);
        }
        boolean showCallBenifitCategoryView = callBenifitCategoryView(objCase.Member_Plan_Id__c,objCase.Interacting_About_Type__c,objCase.Origin);
        if(showCallBenifitCategoryView==true){
            callBenefitCategoryValues = getCallBenefitCategoryValues(objCase.Member_Plan_Id__c);
        }else{
            callBenefitCategoryValues.add(new classificationIntentModel.optionModel('--None--' , '--None--'));
        }
        String selectedMedicarePartcPartD = editcaseData?.Call_Benefit_Category__c!=null?editcaseData?.Call_Benefit_Category__c:'--None--';
        boolean bHasMCDPolicy = isMCDPolicy(objCase.Member_Plan_Id__c);
        String memberPlanName = getMemberPlanName(objCase.Member_Plan_Id__c);
        additionalInfo addinfo = new additionalInfo(callBenefitCategoryValues,userAccess,showCallBenifitCategoryView,selectedMedicarePartcPartD,objCase.Interacting_About_Type__c,editcaseData?.DCN_Formula__c,bMedicareCalls,bMediCCUpdate,bHasMCDPolicy, memberPlanName);
        addinfo.interactingWithAccName = (editcaseData?.Interacting_With__c != null) ? editcaseData?.Interacting_With__r?.Name : '';
        addinfo.interactingAboveAccName = (editcaseData?.AccountId != null) ? editcaseData?.Account?.Name : '';
        boolean isCredentialing = false;
        if(editcaseData!=null && HUMUtilityHelper.isCRMFunctionalityON('4412371')){
            isCredentialing = checkIsCredentialing(editcaseData.Id);
        }
        PrefillWithReadOnly objPrefill = new PrefillWithReadOnly(oAccount.MedicareID__C, caseRecordTypeName, oAccount.Name, objCase.Classification__c, objCase.CTCI_List__c, objCase.Classification_Type__c,objCase.Priority, editCaseData?.CaseNumber, currentUser, objCase.Interacting_With_Type__c,objCase.Member_Plan_Id__c,objCase.Interacting_With__c,objCase.Interacting_With_Name__c,objCase.NPI_ID__C,objCase.Tax_ID__c,objCase.G_A_Rights_Given__c,objCase.Complaint__c,objCase.Origin,objCase.Type,objCase.Response_Status__c,objCase.Subtype__c,editCaseData?.AutoRoute_Status__c,editCaseData!=null?editCaseData.Intent_Id__r.name:'',isCredentialing);

        processResultDetails processResultDetails;
        Map<String,String> flowDataResult = new Map<String,String>();
        if(inpParams.flowName!='' && editcaseData!=null && 
            processFlowNames.contains(inpParams.flowName)){            
            if(inpParams.flowName=='Medicare Part D Redetermination'){
              flowDataResult = updateCaseAfterPart();                        
            }else if(inpParams.flowName=='Medicare/Medicaid Expedited Appeal'){
              flowDataResult = updateCaseAfterExp();
            }else if(inpParams.flowName=='Medicare Good Cause Reinstatement Form'){
                flowDataResult = updateCaseAfterGoodCause();
            }else if(inpParams.flowName=='Medicaid PAR Provider Not Accepting'){
                flowDataResult = updateCaseMedicaidPARProvider();
            }else if(inpParams.flowName=='Medicare Creditable Coverage Attestation'){ 
                flowDataResult = updateCaseCreditableCoverage();
            }else if(inpParams.flowName=='QAA Complaint'){
                flowDataResult = updateCaseQAA();
            }else if(inpParams.flowName == 'PCP Update/Change' || inpParams.flowName == 'PCP/PCD Questions'){
                flowDataResult = updateCaseAfterPCP();
            }else if(inpParams.flowName=='Medicaid Newborn Notification'){  
                flowDataResult = updateNewbornNotification(editcaseData.Id);  
            }
            else if(inpParams.flowName == 'Medicare and Medicaid Other Insurance Form')
            {
                flowDataResult = updateMedicareOITemplate();
            }                       
            processResultDetails =  setFlowDetails(flowDataResult); 
            if(processResultDetails.sClassification!=null && processResultDetails.sClassification!=''){
                objCTCI.classificationName = processResultDetails.sClassification;
                objCase.Classification_Id__c = processResultDetails.sClassification;
            }
            if(processResultDetails.sIntent!=null && processResultDetails.sIntent!=''){
                    objCTCI.intentName = processResultDetails.sIntent;
                objCase.Intent_Id__c = processResultDetails.sIntent;
                }
                if(processResultDetails.status!=null && processResultDetails.status!=''){
                    objCase.status = processResultDetails.status;
                }
                if(processResultDetails.topic!=null){
                    objCase.topic__c = processResultDetails.topic;
                }
				objCase.Interacting_With_Name__c = editcaseData?.Interacting_With_Name__c;
        }else if(HUMUtilityHelper.isCRMFunctionalityON('5231359') ){
            addinfo.sAttachedTemplateName = '';
            if(editcaseData != Null && editcaseData.Id !=null){
                List<Case_Action_Association__c> lstCaseActAssociation = [SELECT Id, Name FROM Case_Action_Association__c where Case_Action__r.Name = 'QAA' and Case__c = :editcaseData.Id];
                addinfo.sAttachedTemplateName =  (lstCaseActAssociation != Null && !lstCaseActAssociation.IsEmpty())? 'QAA' : '' ;
            }
        }
        
        CaseModal objCaseModal = new CaseModal();
        objCaseModal.objCase = objCase;
        objCaseModal.prefillValues = objPrefill;
        objCaseModal.caseCommentData = objComment;
        objCaseModal.ctciModel = objCTCI;
        objCaseModal.additionalInfo = addinfo;
        objCaseModal.processResultDetails = processResultDetails;
        return objCaseModal;
    }

    public static Object fetchClassification(String classificationType){
        List<classificationIntentModel.optionModel> classficationTypeToClassification =  new List<classificationIntentModel.optionModel>();
        classificationToIntent = new Map<Id,List<classificationIntentModel.optionModel>>();

        //this map is just used for add unique classication value in classficationTypeToClassification
        Map<String,String> mpOfClassifcationIntent = new Map<String,String>();
        //it will  use to store the ctci id once classification and intent is selected so that it will use in save case
        mpOfclassificationIntentToCTCIId = new Map<String,Map<String,String>>();
        //This map is used to get service madel and Humana HP log code value once classification and intent is selected. we will fetch CTCI id and using CTCI id will get CTCI object.
        mapOfCTCIIdToCTCI = new Map<Id, CTCI_Junction__c>();
        String sClassificationType = editCaseData != null ? editCaseData.Classification_Type__c : objCase.Classification_Type__c;

        for(CTCI_Junction__c objCTC :[select id,classification__r.Name,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c, classification__c, intent__r.name , isActive__C from CTCI_Junction__c
                                      where classification_type__r.Name =:sClassificationType AND isActive__c=true order by classification__r.Name,intent__r.name])
        {
            if(!mpOfClassifcationIntent.containsKey(objCTC?.classification__c)){
                mpOfClassifcationIntent.put(objCTC.classification__c,objCTC.classification__r.Name);

                classificationToIntent.put(objCTC.classification__c,new List<classificationIntentModel.optionModel>{new classificationIntentModel.optionModel(objCTC.intent__r.name,objCTC.intent__c)});
                mpOfclassificationIntentToCTCIId.put(objCTC.classification__c,new Map<String,String>());
                classficationTypeToClassification.add(new classificationIntentModel.optionModel(objCTC.classification__r.Name , objCTC.classification__c));
            }else{
                classificationToIntent.get(objCTC.classification__c).add(new classificationIntentModel.optionModel(objCTC.intent__r.name,objCTC.intent__c));
            }

            if(mpOfclassificationIntentToCTCIId.containsKey(objCTC?.classification__c)){
                mpOfclassificationIntentToCTCIId.get(objCTC.classification__c).put(objCTC.intent__c,objCTC.Id);
            }
        }

        List<PermissionSetAssignment> lstRSOHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:CaseEditConstants_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
        string caseClassificationType = '';
        ctciObj = new CTCI_Junction__c();
        // if logged in user has 206 permission set then the default value of classification will be Call(RSO) and HP log,service model type will be evaluated based on call(RSO)
        if(lstRSOHPPermissionSet != null && !lstRSOHPPermissionSet.isEmpty()){
            caseClassificationType = CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO;

        }else{
            caseClassificationType = editCaseData != null ? editCaseData.Classification_Type__c : objCase.Classification_Type__c;
        }
       //below if will run in the edit case and query CTCI based on the selected classificatio, intent, Classification type
        if(editCaseData != null){
            objCase.Classification_Type__c = editCaseData.Classification_Type__c;
            mapOfCTCIIdToCTCI.clear();
            List<CTCI_Junction__c> objCTC = [SELECT Id,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c FROM CTCI_Junction__c WHERE Classification__c = :editCaseData.Classification_Id__c AND Intent__c =:editCaseData.Intent_Id__c AND (Humana_Pharmacy_Log_Code__c !=null OR Service_Model_Type__c !=null) AND Classification_Type__r.name =:caseClassificationType];
            if(!objCTC.isEmpty()){
            	ctciObj = objCTC[0];
            }
        }
        for(CTCI_Junction__c objCTC :[select id,classification__r.Name,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c, classification__c,intent__c, intent__r.name , isActive__C from CTCI_Junction__c
                                          where classification_type__r.Name =:caseClassificationType AND isActive__c=true]){
                mapOfCTCIIdToCTCI.put(objCTC.Id,objCTC);
        }
        return classficationTypeToClassification;
    }



    // classes model starts from here to replicated data on UI
    public with Sharing class CaseModal {
        @AuraEnabled
        public Case objCase;
        @AuraEnabled
        public CaseCommentModal caseCommentData;
        @AuraEnabled
        public classificationIntentModel ctciModel;
        @AuraEnabled
        public PrefillWithReadOnly prefillValues;
        @AuraEnabled
        public additionalInfo additionalInfo;
        @AuraEnabled
        public processResultDetails processResultDetails;
    }


    public with Sharing class CaseCommentModal {
        @AuraEnabled
        public Object pharmacyLogCode;
        @AuraEnabled
        public String parentId;
        @AuraEnabled
        public String objCaseComment;

        public CaseCommentModal() {}

        public CaseCommentModal(Object pharmacyLogCode, String parentId, String objCaseComment) {
            this.pharmacyLogCode = pharmacyLogCode;
            this.parentId = parentId;
            this.objCaseComment = objCaseComment;
        }
    }

    //this class is used for show the data on case edit/create page in prefill state with readonly mode
    public with Sharing class PrefillWithReadOnly {
        @AuraEnabled
        public String medicareId;
        @AuraEnabled
        public String caseRecordTypeName;
        @AuraEnabled
        public String accountName;
        @AuraEnabled
        public String classification_Id;
        @AuraEnabled
        public String intent_Id;
        @AuraEnabled
        public String classificationType;
        @AuraEnabled
        public String Priority;
        @AuraEnabled
        public String caseNumber;
        @AuraEnabled
        public String profileName;
        @AuraEnabled
        public String Interacting_With_Type;
        @AuraEnabled
        public String Member_Plan_Id;
        @AuraEnabled
        public String Interacting_With;
        @AuraEnabled
        public String Interacting_With_Name;
        @AuraEnabled
        public String NPI_ID;
        @AuraEnabled
        public String Tax_ID;
        @AuraEnabled
        public String G_A_Rights_Given;
        @AuraEnabled
        public String Complaint;
        @AuraEnabled
        public String caseOrigin;
        @AuraEnabled
        public String caseType;
        @AuraEnabled
        public String responseStatus;
        @AuraEnabled
        public String Subtype;
        @AuraEnabled
        public string AutoRouteStatus;
        @AuraEnabled
        public String intent_Name;
        @AuraEnabled
        public boolean isCredentialing;

        public PrefillWithReadOnly(String medicareId, String caseRecordTypeName, String accountName, String classification_Id, String intent_Id, String classificationType,String Priority, String caseNumber, User currentUsr, String Interacting_With_Type,String Member_Plan_Id,String Interacting_With,String Interacting_With_Name,String NPI_ID,String Tax_ID,String G_A_Rights_Given,String Complaint,String caseOrigin,String caseType,String responseStatus,String Subtype,String autorouting,String intent_Name,boolean isCredentialing){
            this.medicareId = String.isNotBlank(medicareId) ? medicareId :'';
            this.caseRecordTypeName = caseRecordTypeName;
            this.accountName = accountName;
            this.classification_Id = classification_Id;
            this.intent_Id = intent_Id;
            this.classificationType = classificationType;
            this.Priority = Priority;
            this.caseNumber= caseNumber;
            this.profileName = currentUsr.Profile.Name;
            this.Interacting_With_Type=Interacting_With_Type;
            this.Interacting_With_Name=Interacting_With_Name;
            this.Member_Plan_Id=Member_Plan_Id;
            this.Interacting_With=Interacting_With;
            this.NPI_ID=NPI_ID;
            this.Tax_ID=Tax_ID;
            this.G_A_Rights_Given=G_A_Rights_Given;
            this.Complaint=Complaint;
            this.caseOrigin=caseOrigin;
            this.caseType=caseType;
            this.responseStatus = responseStatus;
            this.Subtype=Subtype;
            this.AutoRouteStatus = autorouting;
            this.intent_Name = intent_Name;
            this.isCredentialing = isCredentialing;
        }

    }

    public with Sharing class InputParams{
        public String callerPageName;
        public String flowName;
		public Id interactionId;
        public InputParams(){}
    }
public with Sharing class additionalInfo {     
        @AuraEnabled
        public List<classificationIntentModel.optionModel> medicarePartcPartD;
        @AuraEnabled
        public Map<String,boolean> userAccess;
        @AuraEnabled
        public boolean showCallBenifitCategoryView;
        @AuraEnabled
        public String selectedMedicarePartcPartD;
        @AuraEnabled
        public String interactingAbtType;
        @AuraEnabled
        public String dcnFormula;
        @AuraEnabled
        public boolean bMedicareCalls;
        @AuraEnabled
        public boolean bMediCCUpdate;
        @AuraEnabled
        public boolean bHasMCDPolicy;
    	@AuraEnabled
        public String memberPlanName;
        @AuraEnabled
        public String interactingWithAccName;
        @AuraEnabled
        public String interactingAboveAccName;
        @AuraEnabled
        public String sAttachedTemplateName;

        
        public additionalInfo() {}

        public additionalInfo(List<classificationIntentModel.optionModel> medicarePartcPartD, Map<String,boolean> userAccess,boolean showCallBenifitCategoryView,String selectedMedicarePartcPartD,String interactingAbtType,String dcnFormula,boolean bMedicareCalls,boolean bMediCCUpdate,boolean bHasMCDPolicy, String memberPlanName) {
            this.medicarePartcPartD = medicarePartcPartD;
            this.userAccess = userAccess;
            this.showCallBenifitCategoryView = showCallBenifitCategoryView;
            this.selectedMedicarePartcPartD = selectedMedicarePartcPartD;
            this.interactingAbtType = interactingAbtType;            
            if(dcnFormula!=null && dcnFormula!=''){
                Integer startPos = dcnFormula.indexOf('/apex/DCNRedirection_VF_HUM?id=');
                this.dcnFormula = (startPos != -1)?'/apex/DCNRedirection_VF_HUM?id='+dcnFormula.substring(startPos + 31, startPos + 46):'';
            }
            this.bMedicareCalls = bMedicareCalls;
            this.bMediCCUpdate = bMediCCUpdate;
            this.bHasMCDPolicy = bHasMCDPolicy;
            this.memberPlanName = memberPlanName;
        }
    }
	
public with Sharing class processResultDetails {
        @AuraEnabled
        public String bHasCTCI;
        @AuraEnabled
        public String sClassification;
        @AuraEnabled
        public String sIntent;      
        @AuraEnabled
        public String sAutoRouteStatus;
        @AuraEnabled
        public String attachedTemplateName;
        @AuraEnabled
        public String status;
        @AuraEnabled
        public String topic;
        @AuraEnabled
        public String bTransferBtnDisabled;
        @AuraEnabled
        public String bDisableCaseStatus;
       
        @AuraEnabled
        public String bdisablecloseCancel;
        @AuraEnabled
        public String bDisableType;
        @AuraEnabled
        public String bDisableSubtype;   
        @AuraEnabled
        public String isCreditableCoverageATVF007Routed;   		
        @AuraEnabled
        public String isMedProcessAttachedValue;
        @AuraEnabled
        public String isGoodCauseMedProcessAttachedValue;
        @AuraEnabled
        public String bMedicareProcessPartD;
        @AuraEnabled
        public String bIsMedicaidPARProcessAttached;
        @AuraEnabled
        public String isOItemplateAttached;
        @AuraEnabled
        public String classificationtypes;
		@AuraEnabled
        public String ctype;
		@AuraEnabled
        public String subtype;
        @AuraEnabled
        public String caseowner;
        @AuraEnabled
        public String ownerqueue;
		@AuraEnabled
        public String workqueueview;
		@AuraEnabled
        public String rectype;

        public processResultDetails() {}

        public processResultDetails(String bHasCTCI, String sClassification, String sIntent,
        String sAutoRouteStatus, String attachedTemplateName,String status,String topic,        
        String bTransferBtnDisabled,String bDisableCaseStatus,string bdisablecloseCancel, String bDisableType, String bDisableSubtype, String isCreditableCoverageATVF007Routed, String isMedProcessAttachedValue,String isGoodCauseMedProcessAttachedValue,
        String bMedicareProcessPartD,String bIsMedicaidPARProcessAttached,String isOItemplateAttached,String classificationtypes,String ctype, String subtype,
        String rectype, String caseowner, String ownerqueue, String workqueueview){
            this.bHasCTCI = bHasCTCI;
            this.sClassification = sClassification;
            this.sIntent = sIntent;
            this.sAutoRouteStatus = sAutoRouteStatus;
            this.attachedTemplateName = attachedTemplateName;
            this.status = status;
            this.topic = topic;
            this.bTransferBtnDisabled = bTransferBtnDisabled;
            this.bDisableCaseStatus = bDisableCaseStatus;
          
            this.bdisablecloseCancel = bdisablecloseCancel;
            this.bDisableType = bDisableType;
            this.bDisableSubtype = bDisableSubtype;
			this.isCreditableCoverageATVF007Routed = isCreditableCoverageATVF007Routed;
            this.isMedProcessAttachedValue = isMedProcessAttachedValue;
            this.isGoodCauseMedProcessAttachedValue = isGoodCauseMedProcessAttachedValue;
            this.bMedicareProcessPartD = bMedicareProcessPartD;
            this.bIsMedicaidPARProcessAttached = bIsMedicaidPARProcessAttached;
            this.isOItemplateAttached =  isOItemplateAttached;
            this.classificationtypes = classificationtypes;
			this.ctype = ctype;
			this.subtype = subtype;
            this.rectype = rectype;
            this.caseowner = caseowner;
			this.ownerqueue = ownerqueue;
			this.workqueueview = workqueueview;
        }
    }
    /*
		* Method Name : checkPermisionSetAccess
		* Description : method used to check user permissions set access
		* Return type : Boolean
		* Parameters  : String - name of the permission set
    */
    @AuraEnabled
    public Static Map<String,boolean> checkPermisionSetAccess(){
        boolean hasAccess = false;
        Map<String,boolean> result  =  new Map<String,boolean>();                
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId,PermissionSet.Name FROM PermissionSetAssignment WHERE AssigneeId= :UserInfo.getUserId() AND 
        PermissionSet.Name IN ('CRMS_400_Grievance_Appeals','CRMS_630_MedicareElectionTracking_EnrollmentEdit','CRMS_206_CCSHumanaPharmacyAccess')];
        
        if(!lstPMAssign.isEmpty()){
            for(PermissionSetAssignment p :lstPMAssign){
                result.put(p.PermissionSet.Name,true);
            }
        }    
        Boolean hasCustomPermission = FeatureManagement.checkPermission('Oral_Grievance_Oversight_OGO'); 
        if(hasCustomPermission){
            result.put('Oral_Grievance_Oversight_OGO',true);
        }
        hasCustomPermission = FeatureManagement.checkPermission('RapidForceUser_AHT_HUM'); 
        if(hasCustomPermission){
            result.put('RapidForceUser_AHT_HUM',true);
        }
        hasCustomPermission = FeatureManagement.checkPermission('MedicareElectionTrackingEnrollmentUser_HUM'); 
        if(hasCustomPermission){
            result.put('MedicareElectionTrackingEnrollmentUser_HUM',true);
        }
        hasCustomPermission = FeatureManagement.checkPermission('CRMS_206_CCSHumanaPharmacyAccess'); 
        if(hasCustomPermission){
            result.put('CRMS_206_CCSHumanaPharmacyAccess',true);
        }
        return result;
    }

    /*
        * Method Name : callBenifitCategoryView
        * Description : This method will check the call benifit category view on create/edit case page
        * Return type : Boolean
        * Parameter   : memplanId,InteractingAbtType and sOrigin    
    */
    @AuraEnabled
    public Static boolean callBenifitCategoryView(String memplanId,String InteractingAbtType,String sOrigin) {	
	    
        boolean isCallBenefitCategoryView = false;   
        String UserId = Userinfo.getUserId();
        List<PermissionSetAssignment> lstPMAssign = [SELECT Id FROM PermissionSetAssignment WHERE PermissionSet.Name = :CaseEditConstants_HUM.MEDICARECALLS_PERMISSIONSET_HUM AND AssigneeId = : userId];
        if(!lstPMAssign.isEmpty() && sOrigin == CaseEditConstants_HUM.MEDICARECALLS_CASEORIGIN_HUM){
            if(string.isNotBlank(memplanId)){
                List<MemberPlan> lstMemPlan = [SELECT planId FROM MemberPlan WHERE Id = :ESAPI.encoder().SFDC_HTMLENCODE(memplanId) LIMIT 1];
                if(!lstMemPlan.isEmpty() && lstMemPlan[0].PlanId != null){
                    List<PurchaserPlan> pp = [SELECT Major_Lob__c,Product__c, Product__r.Product_Type__c, Product__r.Segment_Type__c, Product__r.ProductCode from PurchaserPlan WHERE id = :lstMemPlan[0].planId LIMIT 1];
                    Map<String, Set<String>> medicareLOBProductType = CaseEditConstants_HUM.MEDICARE_LOB_PRODUCTTYPE_LIST();
                    if(!pp.isEmpty() &&  string.isNotBlank(pp[0].Product__r.ProductCode) && pp[0].Product__r.ProductCode == CaseEditConstants_HUM.MEDICARE_POLICY && medicareLOBProductType.containsKey(pp[0].Major_Lob__c)){
                        Set<String> TempProductType = medicareLOBProductType.get(pp[0].Major_Lob__c);
                        if(string.isNotBlank(pp[0].Product__r.Product_Type__c) && TempProductType.contains(pp[0].Product__r.Product_Type__c)){
                            isCallBenefitCategoryView = true;
                        }else if(string.isBlank(pp[0].Product__r.Product_Type__c)){
                            isCallBenefitCategoryView = false;
                            if(pp[0].Product__r.Segment_Type__c != null && pp[0].Product__r.Segment_Type__c != ''){
                                if(pp[0].Major_Lob__c == CaseEditConstants_HUM.MAJOR_LOB_MCD  && pp[0].Product__r.Segment_Type__c == CaseEditConstants_HUM.SEGMENT_TYPE_MCD){
                                    isCallBenefitCategoryView = true;
                                }else if (pp[0].Major_Lob__c == CaseEditConstants_HUM.MAJOR_LOB_MER && pp[0].Product__r.Segment_Type__c == CaseEditConstants_HUM.SEGMENT_TYPE_MCR){
                                    isCallBenefitCategoryView = true;
                                }
                            }
                        }
                    }
                }else if(string.IsBlank(memplanId) && InteractingAbtType == CaseEditConstants_HUM.Member){
                    isCallBenefitCategoryView = true;
                }
            }else if(string.IsBlank(memplanId) && InteractingAbtType == CaseEditConstants_HUM.Member){
                isCallBenefitCategoryView = true;
            }
        }
	    return isCallBenefitCategoryView;	
    }
    
    /*
        * Method Name : checkMedicareCalls
        * Description : This method will check Medicare Calls For Medicare Case or not
        * Return type : Boolean
        * Parameter   : memplanId, sOrigin    
    */
    @AuraEnabled
    public Static boolean checkMedicareCalls(String memplanId,String sOrigin) {	
	    
        boolean bMedicareCallsForMedicareCase = false;   
        String UserId = Userinfo.getUserId();
        List<PermissionSetAssignment> lstPMAssign = [SELECT Id FROM PermissionSetAssignment WHERE PermissionSet.Name = :CaseEditConstants_HUM.MEDICARECALLS_PERMISSIONSET_HUM AND AssigneeId = : userId];
        if(!lstPMAssign.isEmpty() && sOrigin == CaseEditConstants_HUM.MEDICARECALLS_CASEORIGIN_HUM){
            if(string.isNotBlank(memplanId)){
                List<MemberPlan> lstMemPlan = [SELECT planId FROM MemberPlan WHERE Id = :ESAPI.encoder().SFDC_HTMLENCODE(memplanId) LIMIT 1];
                if(!lstMemPlan.isEmpty() && lstMemPlan[0].PlanId != null){
                    List<PurchaserPlan> pp = [SELECT Major_Lob__c,Product__c, Product__r.Product_Type__c, Product__r.Segment_Type__c, Product__r.ProductCode from PurchaserPlan WHERE id = :lstMemPlan[0].planId LIMIT 1];
                    Map<String, Set<String>> medicareLOBProductType = CaseEditConstants_HUM.MEDICARE_LOB_PRODUCTTYPE_LIST();
                    if(!pp.isEmpty()){
                        if(string.isNotBlank(pp[0].Product__r.ProductCode) && pp[0].Product__r.ProductCode == CaseEditConstants_HUM.MEDICARE_POLICY && medicareLOBProductType.containsKey(pp[0].Major_Lob__c)){                        
                            if(pp[0].Product__r.Product_Type__c == GLOBAL_CONSTANT_HUM.MEDICARECALLS_MAPD_HUM || pp[0].Product__r.Product_Type__c == GLOBAL_CONSTANT_HUM.MEDICARECALLS_PDP_HUM || pp[0].Product__r.Product_Type__c == GLOBAL_CONSTANT_HUM.MEDICARECALLS_MA_HUM){
                                bMedicareCallsForMedicareCase = true; 
                            }else if(string.isBlank(pp[0].Product__r.Product_Type__c) && pp[0].Product__r.Segment_Type__c == GLOBAL_CONSTANT_HUM.MEDICARECALLS_SEGMENTMCR_HUM){
                                bMedicareCallsForMedicareCase = true; 
                            }
                        }
                    }
                }
            }
        }
	    return bMedicareCallsForMedicareCase;	
    }
    
    /*
		* This method gets  Picklist Values for the Field call Benefit Category
		* @param  memplanId
		* @return List<String>
    */
	@AuraEnabled
    public Static List<classificationIntentModel.optionModel> getCallBenefitCategoryValues(String memplanId){
        
		String productType ='';
        List<classificationIntentModel.optionModel> callBenefitCategoryOptions = new List<classificationIntentModel.optionModel>();
        List<Schema.PicklistEntry> lstCallBenefitPicklist = Case.Call_Benefit_Category__c.getDescribe().getPicklistValues(); 
        callBenefitCategoryOptions.add(new classificationIntentModel.optionModel('--None--' , '--None--'));
        if(!String.isBlank(memplanId)){            
        
            List<MemberPlan> mp = [select Plan.Product__r.Product_Type__c from memberplan where id =:ESAPI.encoder().SFDC_HTMLENCODE(memplanId)];
            if(!mp.isEmpty()){
                productType = mp[0].Plan.Product__r.Product_Type__c;
			    if(productType == null || productType == ''){
                    callBenefitCategoryOptions.add(new classificationIntentModel.optionModel(lstCallBenefitPicklist[0].getValue(), lstCallBenefitPicklist[0].getLabel()));
                    callBenefitCategoryOptions.add(new classificationIntentModel.optionModel(lstCallBenefitPicklist[1].getValue(), lstCallBenefitPicklist[1].getLabel()));
                }else if(productType == CaseEditConstants_HUM.PRODUCT_TYPE_MAPD){
                    callBenefitCategoryOptions.add(new classificationIntentModel.optionModel(lstCallBenefitPicklist[0].getValue(), lstCallBenefitPicklist[0].getLabel()));
                    callBenefitCategoryOptions.add(new classificationIntentModel.optionModel(lstCallBenefitPicklist[1].getValue(), lstCallBenefitPicklist[1].getLabel()));
                }else if(productType == CaseEditConstants_HUM.PRODUCT_TYPE_MA){
                    callBenefitCategoryOptions.add(new classificationIntentModel.optionModel(lstCallBenefitPicklist[0].getValue(), lstCallBenefitPicklist[0].getLabel()));
                }else if(productType == CaseEditConstants_HUM.PRODUCT_TYPE_PDP){
                    callBenefitCategoryOptions.add(new classificationIntentModel.optionModel(lstCallBenefitPicklist[1].getValue(), lstCallBenefitPicklist[1].getLabel()));
                }
		    }else{
                for(Schema.PicklistEntry p : lstCallBenefitPicklist){
                    callBenefitCategoryOptions.add(New classificationIntentModel.optionModel(p.getValue(), p.getLabel()));
                }
            }
        }else{
            for(Schema.PicklistEntry p : lstCallBenefitPicklist){
                callBenefitCategoryOptions.add(New classificationIntentModel.optionModel(p.getValue(), p.getLabel()));
            }
        } 
        return callBenefitCategoryOptions;
    }

    /*
		* This method to validate the DCN entered is valid or not
		* @param  DCN
		* @return String
    */
	@AuraEnabled
    public Static String validateDCN(String DCN){
        String errmsg = '';
        boolean isSuccessResponse = HUMUtilityHelper.ehubValidationDCNService(DCN); 
        if(isSuccessResponse == false){
            errmsg = 'DCN :  The DCN provided is not valid. Please enter a valid DCN.';
        }        
        return errmsg;
    }

    /*
		* This method to validate the task data before closing or cancelling a case
		* @param  CaseId
		* @return Boolean
    */
	@AuraEnabled
    public Static boolean validateTasks(Id caseId){
        List<Task> lstOpenTasks = [
            Select id, Task_Number__c from Task where Whatid = :caseId 
            AND (Type = 'Work Task' or Type = 'Extension Request') and Status != : 'Closed' order by createddate asc];
        return !lstOpenTasks.isEmpty();
    }

    /**
     * Method Name: validateCase
     * Function: Used to validate the data entered to the case before saving
  */
    @AuraEnabled
    public Static String validateCase(String status,String origin,String memplanId,boolean validateDCN,String DCN,Id caseId, String type){
       
        String errorMsg = '';
        if(status == 'Closed' || status == 'Cancelled'){
            boolean hasOpenTasks =  validateTasks(caseId);
            if(hasOpenTasks){
                errorMsg += 'The open Work Task(s) listed below must be closed before closing or canceling the case.<br/>';
            }
        }else{ 
            if(validateDCN && DCN!='' && DCN!=null && (type=='Correspondence Inquiry' || type=='MHK Dispute Task')){
                errorMsg += validateDCN(DCN);
            }
        }

        return errorMsg;
    }

    /*
    * This method is used to check if Case is Record type change eligible based on classification and intent
    * @param   Classification Id, Intent Id, Recordtype, classficationType
    * @return   RtChangeObj
    */
    
    @AuraEnabled
    public static RtChangeObj getRecordTypeChange(string clasId, string intId, string caseRt, string clasType){
    Set<String> setPharRecType = new Set<String>(CaseEditConstants_HUM.Pharmacy_Record_Type_Name);
    Boolean bchangeRecordType;
    string sClassificationName;
    string sIntentName;
    string returnRT = '';
    RtChangeObj rtObj = new RtChangeObj();
    try
    {
        //check if user has PS to do the RT update 
        List<PermissionSetAssignment> RTChangePSCheck = new List<PermissionSetAssignment>();
        RTChangePSCheck = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name IN ('CRMS_206_CCSHumanaPharmacyAccess')];

        if(!RTChangePSCheck.isEmpty() && RTChangePSCheck.size() != null){
                bchangeRecordType = false;
            for (Classification__c classification : [SELECT Name FROM Classification__c where id = :clasId])
            {
                sClassificationName = classification.Name;
            }
            for (Intent__c intent : [SELECT Name FROM Intent__c where id = :intId])
            {
                sIntentName = intent.Name;
            }

            List<CTCI_Junction__c> objCTC = [SELECT Id,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c,isActive__c FROM CTCI_Junction__c WHERE Classification__c = :clasId AND Intent__c =:intId AND Classification_Type__r.name =:clasType AND isActive__c = true];
            
            if (objCTC!=null && !objCTC.isEmpty() ){
                rtObj.ctciObj = objCTC[0];
                if(objCTC[0].Service_Model_Type__c == 'Humana Pharmacy' && (!setPharRecType.contains(caseRt))){
                    returnRT = getCaseRecordType(CaseEditConstants_HUM.CASE_RECORD_TYPES,caseRt);
                }else if(objCTC[0].Service_Model_Type__c != 'Humana Pharmacy' && (setPharRecType.contains(caseRt))){
                    returnRT = getCaseRecordType(CaseEditConstants_HUM.HP_CASE_RECORD_TYPES,caseRt);
                }
                rtObj.returnRT = returnRT;
            }
            
        }                   
    }
    catch (Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'CaseDetails_LC_HUM', 'getRecordTypeChange');
    }
    return rtObj;
    }

    /*
    * This method to get CaseRecord Type
    * @param    mapCaseRecordTypes,caseRt (String)
    * @return   string
    */

    @testvisible
    private static string getCaseRecordType(Map<String,String> mapCaseRecordTypes, string caseRt)
    {
        String sCaseRecName = '';
        Id iCaseRecordType = null;
    string returnRT = '';
        if(mapCaseRecordTypes!=null && !mapCaseRecordTypes.isEmpty())
        {
            sCaseRecName = mapCaseRecordTypes.get(caseRt);
            if(String.isNotBlank(sCaseRecName)){
                sCaseRecName =  sCaseRecName.trim();
                iCaseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get(String.valueOf(sCaseRecName)).getRecordTypeId();
                returnRT = sCaseRecName + '=' + iCaseRecordType ;
            }
        }
        return returnRT;
    }

    public class RtChangeObj {
        @AuraEnabled
        Public CTCI_Junction__c ctciObj;
        @AuraEnabled
        public string returnRT;
    }/*
    * This method to get case comments in case of Medicare Calls
    * @param    Record Id
    * @return   boolean
    */
    @AuraEnabled
    public Static boolean getCCMedicareCalls(String caseId){
        boolean bMediCCUpdate = false;
        String sMedicareIssInit;
        String sMedicareResInit;

        Map<Id, CaseComment> mapMediCC = new Map<Id, CaseComment>([SELECT Id, CommentBody FROM CaseComment WHERE ParentId = :caseId]);
        list<CaseCommentType_Junction__c> lstCCTJunc = [
            SELECT Id, Name, CaseCommentType__r.Description__c, 
            CaseCommentType__r.Name, CaseComment__c FROM CaseCommentType_Junction__c 
            WHERE CaseComment__c IN : mapMediCC.keyset() order by createdDate ASC];
        if (lstCCTJunc != null && !lstCCTJunc.isEmpty()){
            for (CaseCommentType_Junction__c oCCTJ : lstCCTJunc){
                if(oCCTJ.CaseCommentType__r.Name == CaseEditConstants_HUM.MEDICARECALLS_ISSUE_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c))){
                    sMedicareIssInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
                }
                if(oCCTJ.CaseCommentType__r.Name == CaseEditConstants_HUM.MEDICARECALLS_RESO_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c))){
                    sMedicareResInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
                }
            }
            if(String.isNotBlank(sMedicareIssInit) && String.isNotBlank(sMedicareResInit) && sMedicareIssInit.length()>=10 && sMedicareResInit.length()>=10) {
                bMediCCUpdate = true;
            }else{
                bMediCCUpdate = false;
            } 
        }else{
            bMediCCUpdate = false;
        } 
        return bMediCCUpdate;
    }
    
            /*
    * This method Is to update classification and intents for Part D process
    * @param  NA
    * @return void
    */
    @AuraEnabled
    public static Map<String,String> updateCaseAfterPart(){		
        Map<String,String> result = new Map<String,String>();
        boolean bTransferBtnDisabled = true;        
        boolean bHasCTCIPart = false;
        String attachedTemplateName;
        String sAutoRouteStatus;
        case ofetchCurrentPriority=[select priority,topic__c,Work_Queue_View_Name__c from case where Id=: editcaseData.id];
        List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name,
         Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where 
         Classification__r.name='Rx' and Intent__r.Name='Part D Redetermination' and 
         Classification_Type__r.Name=:editcaseData.Classification_Type__c and IsActive__c=TRUE];
         if(lstCTCIs != Null && !lstCTCIs.isEmpty()){
            bHasCTCIPart = true;
            result.put('sClassification',lstCTCIs[0].Classification__c); 
            result.put('sIntent',lstCTCIs[0].Intent__c);
        }
        editcaseData.status = 'In Progress';
        if(ofetchCurrentPriority.topic__c != NULL){
            editcaseData.topic__c = ofetchCurrentPriority.topic__c;
        }
        result.put('sAutoRouteStatus','Evaluate Part D');
		result.put('attachedTemplateName','BC929CD6-E06A');
		result.put('status',editcaseData.status);
		result.put('topic',editcaseData.topic__c);      
        result.putAll(isMedExpProcessAttachedPartD(result,editcaseData));
        bTransferBtnDisabled = enableDisableSaveAndTransferButton(editcaseData,result.get('attachedTemplateName'),result.get('sAutoRouteStatus'));
		result.put('bTransferBtnDisabled',''+bTransferBtnDisabled);
        return result;   
    }

	/*
    * This method Is to disable fields and buttons for Part D process
    * @param  NA
    * @return void
    */   	
    public static Map<String,String> isMedExpProcessAttachedPartD(Map<String,String> result,Case caseRec){
        boolean bQueueUser = false;
        boolean bFlag = false;
        boolean bMedicareProcessPartD = false;
        boolean bHasCTCIExpRef = false;
        Map<string,string> MedExpProcessPartD = new Map<string,string>();
        if(caseRec.status != 'Closed'){
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where 
                UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and 
                Group.Name in ('LV GA and Correspondence Screening PGQ', 'LV GA and Correspondence Screening P PGV')];
            bQueueUser = (lstGrpMem.size() == 2)?false:true;
            if(result.get('sAutoRouteStatus') == 'Evaluate Part D' || (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'LV G and A and Correspondence Screening' )){
                bFlag = true;
            }
            if(bQueueUser && bFlag){
                List <Template_Submission_Data__c> flowValue = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c =  'BC929CD6-E06A' AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id];
                bMedicareProcessPartD = !flowValue.isEmpty()? true:false;
            }
        }
        if(bMedicareProcessPartD) {
            MedExpProcessPartD.put('bDisableCaseStatus','true');
           
            MedExpProcessPartD.put('bdisablecloseCancel','true');
            List <Case> caselst = [select id from case where Classification_Type__c ='Calls (RSO)' and id=:caseRec.id];
            bHasCTCIExpRef = !caselst.isEmpty() ? true : false;
        }else{
            MedExpProcessPartD.put('bDisableCaseStatus','false');
            MedExpProcessPartD.put('bdisablecloseCancel','false');
        }
        MedExpProcessPartD.put('bHasCTCI',''+bHasCTCIExpRef);
        MedExpProcessPartD.put('isPartProcessAttachedValue',''+bMedicareProcessPartD);
    	return MedExpProcessPartD; 
    }
    /*
    * This method Is to update classification and intents for medicare appeal process
    * @param  NA
    * @return void
    */ 
    public static Map<String,String> updateCaseAfterExp(){
        Map<String,String> result = new Map<String,String>();
        boolean bTransferBtnDisabled = true;
        boolean bHasCTCIExp = false;  
        case ofetchCurrentPriority=[select priority,topic__c,Work_Queue_View_Name__c from case where Id=: editcaseData.id];
        List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name,
         Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where 
         Classification__r.name='G&A' and Intent__r.Name='Expedited'  and
          Classification_Type__r.Name=:editcaseData.Classification_Type__c and IsActive__c=TRUE];
          if(lstCTCIs != Null && !lstCTCIs.isEmpty()){
                    bHasCTCIExp = true;
                    result.put('sClassification',lstCTCIs[0].Classification__c); 
                    result.put('sIntent',lstCTCIs[0].Intent__c); 
                }
            editcaseData.status = 'In Progress';

            if(ofetchCurrentPriority.topic__c != NULL){
                editcaseData.topic__c = ofetchCurrentPriority.topic__c;
            }
            result.put('sAutoRouteStatus','Evaluate Expedited Appeal');
		    result.put('attachedTemplateName','95C90830-3133');
		    result.put('status',editcaseData.status);
		    result.put('topic',editcaseData.topic__c);
        result.putAll(isMedExpProcessAttachedExp(result,editcaseData));
        bTransferBtnDisabled = enableDisableSaveAndTransferButton(editcaseData,result.get('attachedTemplateName'),result.get('sAutoRouteStatus'));
		result.put('bTransferBtnDisabled',''+bTransferBtnDisabled);				
		return result;
    }  
   
    /*
    * This method Is to disable fields and buttons for medicare appeal process
    * @param  NA
    * @return void
    */
    public static Map<String,String> isMedExpProcessAttachedExp(Map<String,String> result,Case caseRec){				
	    boolean bQueueUser = false;
        boolean bFlag = false;
        boolean bMedExpAppeal = false;
        boolean bHasCTCIExpRef = false;
        boolean isMedProcessAttachedValue = false;
        List <Template_Submission_Data__c> flowValue;
        Map<string,string> MedExpProcess= new Map<string,string>();
        if(caseRec.status != 'Closed'){
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where 
            UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and 
            Group.Name in ('LV GA and Correspondence Screening PGQ','LV GA and Correspondence Screening E PGV')];
            bQueueUser = (lstGrpMem.size() == 2)?false:true;
            if(result.get('sAutoRouteStatus') == 'Evaluate Expedited Appeal' || (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'LV G and A and Correspondence Screening' )){
                bFlag = true;
            }
            if(bQueueUser && bFlag){
                flowValue = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = '95C90830-3133' AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id];
                bMedExpAppeal = !flowValue.isEmpty()? true:false;
            }
        }
        if(bMedExpAppeal){
            MedExpProcess.put('bDisableCaseStatus','true');
           
            MedExpProcess.put('bdisablecloseCancel','true');
            List <Case> caselst =[select id from case where  Classification_Type__c =: CaseRec.Classification_Type__c and 
            Classification_Id__r.Name ='G&A' and Intent_Id__r.Name ='Expedited' and id=:caseRec.id]; bHasCTCIExpRef = !caselst.isEmpty() ? true : false;
        }else{
            MedExpProcess.put('bDisableCaseStatus','false');
          
            MedExpProcess.put('bdisablecloseCancel','false');
        }
        if(Label.Case_Transfer_Service_Flag == '1'){
            flowValue = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  = '95C90830-3133'  AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id];
            isMedProcessAttachedValue = !flowValue.isEmpty()? true:false;
        }
        MedExpProcess.put('bHasCTCI',''+bHasCTCIExpRef);
        MedExpProcess.put('isMedProcessAttachedValue',''+isMedProcessAttachedValue);
        return MedExpProcess; 
    } 
	
    /*
    * This method Is to update classification and intents for medicare appeal process
    * @param  NA
    * @return void
    */   
    public static Map<String,String> updateCaseAfterGoodCause(){ 
		Map<String,String> result = new Map<String,String>();
        String sAutoRouteStatus;
        String attachedTemplateName;
        boolean bTransferBtnDisabled = true;
        List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM 
            Template_Submission_Data__c WHERE 
            Template_Submission__r.Template__r.Unique_Template_Name__c  ='E0A13562-89EE-48B8-8EBA-E485C6583FF1'  
            AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:editcaseData.Id AND 
            Template_Field__r.Name ='GCRTermedPlanYesNo' LIMIT 1]; 
        if(!flowValue.isEmpty() &&  flowValue[0].Value__c == 'Yes'){    
            if(GoodCauseAutoRouteClassificationTypes.contains(editcaseData.Classification_Type__c) && editcaseData.AutoRoute_Status__c != 'Completed'){ 
                List<CTCI_Junction__c> lstCTTIRecords = [SELECT Id, Classification__c, Classification_Type__c,
                    Intent__c  FROM CTCI_Junction__c WHERE Classification__r.name='Billing' AND 
                    Intent__r.Name='Reinstatement Review' AND 
                    Classification_Type__r.Name=:GoodCauseAutoRouteClassificationTypes]; 
                if(lstCTTIRecords != Null && !lstCTTIRecords.isEmpty()){
                    result.put('sClassification',lstCTTIRecords[0].Classification__c); 
                    result.put('sIntent',lstCTTIRecords[0].Intent__c);
                }
                editcaseData.status = 'In Progress';
                List <Template_Submission_Data__c> flowMedicareValue = [SELECT Value__c, 
                    Template_Field__r.Name FROM Template_Submission_Data__c WHERE 
                    Template_Submission__r.Template__r.Unique_Template_Name__c ='E0A13562-89EE-48B8-8EBA-E485C6583FF1' 
                    AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:editcaseData.Id AND 
                    Template_Field__r.Name='GCRIsMedicationNeeded' LIMIT 1];
                if(flowMedicareValue != Null && !flowMedicareValue.isEmpty() &&  flowMedicareValue[0].Value__c == 'Yes'){
                    sAutoRouteStatus = 'Evaluate Good Cause';
                    editcaseData.topic__c = 'Evaluate Good Cause';
                }   
                else if(flowMedicareValue != Null && !flowMedicareValue.isEmpty() &&  flowMedicareValue[0].Value__c == 'No'){
                    sAutoRouteStatus = 'Termed in Error';
                    editcaseData.topic__c = 'Termed in Error (TIE)';
                }
                attachedTemplateName = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
            }
        }
        result.put('sAutoRouteStatus',sAutoRouteStatus);
		result.put('attachedTemplateName',attachedTemplateName);
		result.put('status',editcaseData.status);
		result.put('topic',editcaseData.topic__c);
        result.putAll(isMedExpProcessAttachedGoodCause(result,editcaseData));
        bTransferBtnDisabled = enableDisableSaveAndTransferButton(editcaseData,result.get('attachedTemplateName'),result.get('sAutoRouteStatus'));
		result.put('bTransferBtnDisabled',''+bTransferBtnDisabled); 			
		return result;
    } 
    
    /*
    * This method Is to disable fields and buttons for medicare Good Cause process
    * @param  NA
    * @return void
    */
    public static Map<String,String> isMedExpProcessAttachedGoodCause(Map<String,String> result,Case caseRec){        
        boolean bQueueUser = false;
        boolean bFlag = false;
        boolean bGoodCause = false;
        boolean isGoodCauseMedProcessAttachedValue = false;
        boolean bHasCTCIExpRef = false;
        Map<string,string> MedExpProcessAttachedGoodCause = new Map<string,string>();
        if(caseRec.status != 'Closed'){
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where 
                UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and 
                Group.Name ='Louisville Premium Payment Recovery PGQ'];
            bQueueUser = (lstGrpMem.size() == 1)?false:true;
            if((result.get('sAutoRouteStatus') == 'Evaluate Good Cause' || result.get('sAutoRouteStatus') == 'Termed in Error')|| 
            (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'Louisville Premium Payment Recovery')){
                bFlag = true;
            }
            if(bQueueUser && bFlag){
                List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM 
                Template_Submission_Data__c WHERE 
                Template_Submission__r.Template__r.Unique_Template_Name__c  ='E0A13562-89EE-48B8-8EBA-E485C6583FF1'
                AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id AND 
                Template_Field__r.Name='GCRTermedPlanYesNo' LIMIT 1];
                if(flowValue != Null && !flowValue.isEmpty()) {
                    MedExpProcessAttachedGoodCause.put('bDisableCaseStatus','true');
                  
                    MedExpProcessAttachedGoodCause.put('bdisablecloseCancel','true');
                    if(flowValue[0].Value__c == 'Yes'){
                        bHasCTCIExpRef = (GoodCauseAutoRouteClassificationTypes.contains(CaseRec.Classification_Type__c));               
                    }
                    isGoodCauseMedProcessAttachedValue  = true;
                }
            }
        }        
        MedExpProcessAttachedGoodCause.put('bHasCTCI',''+bHasCTCIExpRef);
        MedExpProcessAttachedGoodCause.put('isGoodCauseMedProcessAttachedValue',''+isGoodCauseMedProcessAttachedValue);
        return MedExpProcessAttachedGoodCause;
    }
      
    public static Map<String,String> updateCaseMedicaidPARProvider(){

		Map<String,String> result = new Map<String,String>();
        boolean bIsMedicaidPARProcessAttached = false;
        String attachedTemplateName;
        boolean bHasCTCIExpRef = false;
        String sAutoRouteStatus;
        boolean bTransferBtnDisabled = true;
        boolean bAutoRoutePARSwitch = HUMUtilityHelper.isCRMFunctionalityON('3627766');
        List <Template_Submission_Data__c> lstStateFlowValue  = UtilityHelper_H_AHT_HUM.getTemplateData(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID,CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_FIELDS,editcaseData);
        if(lstStateFlowValue != null && !lstStateFlowValue.isEmpty()){ 
                bIsMedicaidPARProcessAttached = false;
                for(Template_Submission_Data__c data : lstStateFlowValue){
                    if(data.value__c!=null){
                        bIsMedicaidPARProcessAttached = true;
                        attachedTemplateName= CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID;
                        break;
                    }
                }
                if(bIsMedicaidPARProcessAttached && editcaseData.AutoRoute_Status__c != 'Completed'){ 
                    List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, 
                    Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c,
                    Intent__c, IsActive__c FROM 
                    CTCI_Junction__c where Classification__r.name='Advanced Resolution (ART)' and 
                    Intent__r.Name='Par Provider Not Accepting' and 
                    Classification_Type__r.name = :editcaseData.Classification_Type__c  and IsActive__c=TRUE];
                    if(null!= lstCTCIs  && !lstCTCIs.isEmpty()){   
                        result.put('sClassification',lstCTCIs[0].Classification__c); 
                        result.put('sIntent',lstCTCIs[0].Intent__c);
                        bHasCTCIExpRef = true;
                        editcaseData.status = 'In Progress';                      
                        sAutoRouteStatus = 'Evaluate PAR';
                        UtilityHelper_H_AHT_HUM.TemplateSubmissionData oTempSubData = UtilityHelper_H_AHT_HUM.getTemplateSubmissionWrapper(lstStateFlowValue);
                        if(oTempSubData!=null){
                            if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_FL){
                                editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_FLTOPIC;
                            }else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_KY){ 
                                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                    editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health; 
                                }else {
                                    sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY;
                                }
                            }else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SC){
                                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                    editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
                                }else{
                                    editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_South_Carolina;
                                }
                            }else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_IL){
                                editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_ILTOPIC;
                            }else if(bAutoRoutePARSwitch && oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_IL_LTSS){
								sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL;
							}else if(bAutoRoutePARSwitch && oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OH){
								if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
									editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
								}else{
                                    editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OHTOPIC;
								}
                            }else if(bAutoRoutePARSwitch && oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_LA){
								if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
									editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
								}else{
                                    editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_LATOPIC;
								}
                            }else if(HUMUtilityHelper.isCRMFunctionalityON('5243585')){  // This is to set topic for OK state selection in PAR provider template - Story 5243585
                                if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OK){
                                    if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                        editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
                                    }else{
                                       editcaseData.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OKTOPIC;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        result.put('sAutoRouteStatus',sAutoRouteStatus);
	    result.put('attachedTemplateName',attachedTemplateName);
		result.put('status',editcaseData.status);
	    result.put('topic',editcaseData.topic__c);
        result.putAll(isMedicaidPARProviderAttached(result,editcaseData,bIsMedicaidPARProcessAttached,bHasCTCIExpRef));
        bTransferBtnDisabled = enableDisableSaveAndTransferButton(editcaseData,attachedTemplateName,sAutoRouteStatus);
		result.put('bTransferBtnDisabled',''+bTransferBtnDisabled);    
        return result;     
    }

    public static Map<String,String> isMedicaidPARProviderAttached(Map<String,String> result,Case caseRec,boolean bIsMedicaidPARProcessAttached,boolean bHasCTCIExpRef){
        String sGroupName='';
        boolean bFlag = false;
        boolean bQueueUser = false;
        String sAutoRouteStatus = result.get('sAutoRouteStatus');
        Map<string,string> MedicaidPARProvider = new Map<string,string>();
        List<Template_Submission_Data__c> lstStateFlowValue = UtilityHelper_H_AHT_HUM.getTemplateData(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID,CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_FIELDS,caseRec);
        UtilityHelper_H_AHT_HUM.TemplateSubmissionData oTempSubData = UtilityHelper_H_AHT_HUM.getTemplateSubmissionWrapper(lstStateFlowValue);
        if(oTempSubData != null){
            if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_KY){ 
                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                    sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP;
                }else{
                    sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_KY;
                } 
            }else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_IL_LTSS){
                sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_IL;
			}else{
                sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP;
            } 
        }
        if(caseRec.status != 'Closed'){
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId =: UserInfo.getUserId()   and group.type='Regular' and Group.Name =:  sGroupName];
            bQueueUser = true;
            if(!lstGrpMem.isEmpty()){
                bQueueUser = false;
            }
            if((sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER) || 
            (sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY) || 
            (sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL) ||
			(caseRec.AutoRoute_Status__c == 'Completed' &&
                (caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE || 
                caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_KY ||
				caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_IL)
            )){
            bFlag = true;
            }
            boolean bMedicaidPARProvider = false;
            if(bQueueUser && bFlag){
                bMedicaidPARProvider = bIsMedicaidPARProcessAttached ? true : false; 
            }
            if(bMedicaidPARProvider) {    
                MedicaidPARProvider.put('bDisableCaseStatus','true');
              
                MedicaidPARProvider.put('bdisablecloseCancel','true');
                if(bHasCTCIExpRef!=null && !bHasCTCIExpRef){ 
                    bHasCTCIExpRef = (([select id from case where  Classification_Type__c =: CaseRec.Classification_Type__c and Classification_Id__r.Name =: CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderClassification and Intent_Id__r.Name =: CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderIntent  and id=:caseRec.id]).size()>0 ) ? true : false; 
                }               
            }
        }
        MedicaidPARProvider.put('bHasCTCI',''+bHasCTCIExpRef);
        MedicaidPARProvider.put('bIsMedicaidPARProcessAttached',''+bIsMedicaidPARProcessAttached);
        return MedicaidPARProvider;
    }
    public static boolean enableDisableSaveAndTransferButton(Case caseRec,String attachedTemplateName, String sAutoRouteStatus){
        boolean bTransferBtnDisabled= false;
        User currentloggedInUser = HUMUtilityHelper.fetchUserDetails();
		if(String.IsNotBlank(attachedTemplateName)){
            if(attachedTemplateName == CONSTANTS_CASE_HUM.ATTESTATION_TEMPLATE_NAME){
                if(caseRec.Case_Owner__c  == CONSTANTS_CASE_HUM.CASE_RSO_ENROLLMENT_OWNER 
                && caseRec.Guided_Process_Flag__c == true && caseRec.status == CONSTANTS_CASE_HUM.CASE_STATUS_PENDING_RESPONSE){
                    if(currentloggedInUser.Current_Queue__c != caseRec.Owner_Queue__c){
                        bTransferBtnDisabled = true;
                    }
                }
            }else if(attachedTemplateName == '95C90830-3133' || attachedTemplateName == 'BC929CD6-E06A' 
                || attachedTemplateName == CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID || attachedTemplateName == CaseEditConstants_HUM.GOODCAUSEUNIQUEID
                    || attachedTemplateName == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID  || attachedTemplateName == CaseAutoRouteConstant_C_HUM.MEDICAIRE_OI_TEMPLATE){
                if(sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_MEDICALUPDATE|| sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMCDONLY || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMESPDP || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RX090DENIAL || sAutoRouteStatus== CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_OIPRESCRIPTION || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_NEWBORN || sAutoRouteStatus == 'Evaluate Expedited Appeal' || sAutoRouteStatus == 'Evaluate Part D' || sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_PCP 
                    || sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_GOODCAUSE || sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_GOODCAUSETIE
                        || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL){
                    bTransferBtnDisabled = true;
                }else if(currentloggedInUser.Current_Queue__c != caseRec.Owner_Queue__c){
                    bTransferBtnDisabled = true;
                }
            }
        }
		return bTransferBtnDisabled;
    }
    public static processResultDetails setFlowDetails(Map<String,String> result){
        processResultDetails prd = new processResultDetails(
        result.get('bHasCTCI'),
        result.get('sClassification'),
        result.get('sIntent'),
        result.get('sAutoRouteStatus'),
        result.get('attachedTemplateName'),
        result.get('status'),
        result.get('topic'), 
        result.get('bTransferBtnDisabled'),
        result.get('bDisableCaseStatus'),      
        result.get('bdisablecloseCancel'),
        result.get('bDisableType'), 
        result.get('bDisableSubtype'),  
		result.get('isCreditableCoverageATVF007Routed'),
        result.get('isMedProcessAttachedValue'),
        result.get('isGoodCauseMedProcessAttachedValue'),
        result.get('isPartProcessAttachedValue'),
        result.get('bIsMedicaidPARProcessAttached'),
        result.get('isOItemplateAttached'),
        result.get('classificationtypes'),
		result.get('ctype'),
		result.get('subtype'),
        result.get('rectype'),
        result.get('caseowner'),
		result.get('ownerqueue'),
		result.get('workqueueview'));
        return prd;
    }
	
	 /*
    * This method is to Disable certain fields if attached process is creditable coverage
    * @param  NA
    * @return Map<String,String> - consist of Key Value to store process specific values
    */
    @AuraEnabled
    public static Map<String,String> updateCaseCreditableCoverage()
    {
        String attachedTemplateName = '';
        Boolean bTransferBtnDisabled = false;
        Map<String,String> result = new Map<String,String>();
        if(objCase.status == CONSTANTS_CASE_HUM.CASE_STATUS_PENDING_RESPONSE)
        {
             Boolean bCreditableStatusReadonly = ([SELECT Value__c FROM Template_Submission_Data__c 
				WHERE Template_Submission__r.Template__r.Unique_Template_Name__c =: CONSTANTS_CASE_HUM.ATTESTATION_TEMPLATE_NAME AND 
				Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =: editcaseData.id].size()>0) ? true : false;    
                        
                if(bCreditableStatusReadonly)
                {
                    result.put('bDisableType','true');   
                    result.put('bDisableSubtype','true');   
                    result.put('bDisableCaseStatus','true');
                    result.put('bdisablecloseCancel','true');
                    result.put('isCreditableCoverageATVF007Routed','true');   
                    attachedTemplateName = 'ATVF007'; 
                    List<Case> lstCase = [SELECT Case_Owner__c,Owner_Queue__c,Work_Queue_View_Name__c, Recordtype.Name,Classification_Type__c, Classification_Id__c,Intent_Id__c,Type,Subtype__c,Topic__c,Status FROM Case WHERE Id = :editcaseData.id LIMIT 1];
                    if(!lstCase.isEmpty()){ 
                        result.put('classificationtypes',lstCase[0].Classification_Type__c); 
                        result.put('sClassification',lstCase[0].Classification_Id__c); 
                        result.put('sIntent',lstCase[0].Intent_Id__c); 
                        result.put('status',lstCase[0].Status); 
                        result.put('topic',lstCase[0].Topic__c); 
                        result.put('ctype',lstCase[0].Type); 
                        result.put('subtype',lstCase[0].Subtype__c);
                        result.put('caseowner',lstCase[0].Case_Owner__c);
                        result.put('ownerqueue',lstCase[0].Owner_Queue__c);
                        result.put('workqueueview',lstCase[0].Work_Queue_View_Name__c);
                        result.put('rectype',lstCase[0].Recordtype.Name); 
                    }          
                }
        }		
		bTransferBtnDisabled = enableDisableSaveAndTransferButton(editcaseData,attachedTemplateName,'');
		result.put('bTransferBtnDisabled',''+ bTransferBtnDisabled);
        
		return result;   
    }
    /*
    * This method is to update classification, intent and disable buttons (Close Case, Save and Transfer, Cancel) for QAA processed cases.
    * @param  N/A
    * @return Map
    */ 
    public static Map<String,String> updateCaseQAA() {
		Map<String,String> result = new Map<String,String>();
		String segmentIndicator = '';
		String issueState = '';
		String productType = '';
		String groupNumber = '';
		boolean bHasCTCIExpRef = false;
		String sAutoRouteStatus;
		
		List<MemberPlan> lstMemPlan = [SELECT Issue_State__c, Segment_Indicator__c, Product_Type__c, GroupNumber FROM MemberPlan WHERE Id = :editcaseData.Member_Plan_Id__c LIMIT 1];
        if(!lstMemPlan.isEmpty()){            
            segmentIndicator =lstMemPlan[0].Segment_Indicator__c;
            issueState = lstMemPlan[0].Issue_State__c;
            productType = lstMemPlan[0].Product_Type__c;                
			groupNumber = lstMemPlan[0].GroupNumber;
        }
		
		if(isQAAAutoRoute(editcaseData,segmentIndicator,issueState,productType,groupNumber)){
            if(!(editcaseData.AutoRoute_Status__c == 'Completed')) {
                List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseAutoRouteConstant_C_HUM.sQAACalssification and Intent__r.Name=: CaseAutoRouteConstant_C_HUM.sQAAIntent and Classification_Type__r.name = :editcaseData.Classification_Type__c  and IsActive__c=TRUE];
				if(null!= lstCTCIs  && !lstCTCIs.isEmpty()){
					result.put('sClassification',lstCTCIs[0].Classification__c); 
                    result.put('sIntent',lstCTCIs[0].Intent__c);
					result.put('bDisableCaseStatus','true');
                    editcaseData.status = 'In Progress'; 	
                    bHasCTCIExpRef = true; 
					
					if (null != segmentIndicator && CaseAutoRouteConstant_C_HUM.LST_MEDICAREPLANS.contains(segmentIndicator)) {                            
                        sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICARE;
                    }else if((null != issueState && CaseAutoRouteConstant_C_HUM.LST_MCDSTATES_KYIL.contains(issueState)) && ((null != segmentIndicator && CaseAutoRouteConstant_C_HUM.MEDICAIDPLAN == segmentIndicator) || (null != productType && CaseAutoRouteConstant_C_HUM.PRODUCT_TYPE_MCD == productType))) {
                        sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_KY_IL_MCD;
					}else if((null != issueState && CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL == issueState) && ((null != segmentIndicator && CaseAutoRouteConstant_C_HUM.MEDICAIDPLAN == segmentIndicator) || (null != productType && CaseAutoRouteConstant_C_HUM.PRODUCT_TYPE_MCD == productType))){                            
                        sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_FL_MCD;
                    }
                }
            }    
		}
        result.putAll(isQAAProcessAttached(editCaseData,sAutoRouteStatus));
		result.put('bHasCTCI',''+bHasCTCIExpRef);
		result.put('sAutoRouteStatus',sAutoRouteStatus);
		result.put('status',editcaseData.status);
		result.put('attachedTemplateName','QAA Complaint');
		
		return result;
    }
	
	 /**
    * Method is used to identify if Case is Eligible for QAA Autoroute
    * @param  Case,Memberplan segment indicator,Memberplan Issuestate, Memberplan product type and Memberplan group number 
    * @return   boolean
    */
    public static boolean isQAAAutoRoute(Case casObj,String segmentIndicator,String issueState,String productType,String groupNumber){

        boolean bAutoRouteQAA = false; 
        if(null != casObj.Member_Plan_Id__c){
            if(segmentIndicator !='' && CaseAutoRouteConstant_C_HUM.LST_MEDICAREPLANS.contains(segmentIndicator)){
                bAutoRouteQAA =true;
            }else if(issueState != '' && CaseAutoRouteConstant_C_HUM.LST_MEDICAID_STATES.contains(issueState)){
                if(
                    ((segmentIndicator != '' && CaseAutoRouteConstant_C_HUM.MEDICAIDPLAN == segmentIndicator) || 
                    (productType != '' && CaseAutoRouteConstant_C_HUM.PRODUCT_TYPE_MCD == productType)) &&
                    (!CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(issueState) ||
                    (String.isBlank(groupNumber) && CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(issueState)) ||
                    (CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(issueState) && String.isNotBlank(groupNumber) &&  groupNumber.length() <= 4) ||
                    ( String.isNotBlank(groupNumber) && CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL.equalsIgnoreCase(issueState) &&  groupNumber.length() > 4  &&  !CaseAutoRouteConstant_C_HUM.lstFLLTCQAAIgnoreGroupNumbers.contains(groupNumber.substring(0, 5)))
                    )
                ){
                    bAutoRouteQAA =true;
                }
            }
        }       
        return bAutoRouteQAA;
    } 
 	
    @AuraEnabled(continuation=true)
    public static Continuation sendRequestLogNotes(String sComment, String sCode, String sCaseId, String sEntId, String sNetworkId, String sAccId)
    { 
        Continuation con1 = PharmacyCaseComment_C_HUM.sendRequestLogNotes(sComment,sEntId, sNetworkId,sAccId,sCode,sCaseId);
        return con1 ;
    }
	
	/**
	* Returns responses of continuation object
	* Callback Method to parse the Get log Note service response
	*
	* @param  List<String> List of Continuation Labels
	* @param  Object State of Continuation
	* @return Object
	*/ 
    @AuraEnabled
    public static object parseLogNoteResponse(List <String> labels, object State)
    {	       
	   return PharmacyCaseComment_C_HUM.parseLogNoteResponse(labels,State);
    }
	
    /**
    * Method is used to identify the flowname and return the required object to auto route
    * @param  Id,flowName
    * @return   Object
    */
    @AuraEnabled
    public static Object getProcessDetails(String Id,String flowName){
        editcaseData = [select Id,status,topic__c,AutoRoute_Status__c,Owner_Queue__c,Case_Owner__c,Guided_Process_Flag__c,Classification_Type__c,Member_Plan_Id__c	from case where Id=: Id];
        objCase = editcaseData;
        Map<String,String> flowDataResult = new Map<String,String>();
        if(processFlowNames.contains(flowName)){            
            if(flowName=='Medicare Part D Redetermination'){
              flowDataResult = updateCaseAfterPart();                        
            }else if(flowName=='Medicare/Medicaid Expedited Appeal'){
              flowDataResult = updateCaseAfterExp();
            }else if(flowName=='Medicare Good Cause Reinstatement Form'){
                flowDataResult = updateCaseAfterGoodCause();
            }else if(flowName=='Medicaid PAR Provider Not Accepting'){
                flowDataResult = updateCaseMedicaidPARProvider();
            }else if(flowName=='Medicare Creditable Coverage Attestation'){ 
                flowDataResult = updateCaseCreditableCoverage();
            }else if(flowName=='QAA Complaint'){
                flowDataResult = updateCaseQAA();
            }else if(flowName == 'PCP Update/Change' || flowName == 'PCP/PCD Questions'){
                flowDataResult = updateCaseAfterPCP();
            }else if(flowName=='Medicaid Newborn Notification'){ 
                flowDataResult = updateNewbornNotification(editcaseData.Id);  
            }else if(flowName == 'Medicare and Medicaid Other Insurance Form')
            {
                flowDataResult = updateMedicareOITemplate();
            }              
        }
        processResultDetails processResultDetails =  setFlowDetails(flowDataResult);
        return processResultDetails;   
    }

    /*
    * This method is to update classification, intent and disable buttons (Close Case, Save and Transfer, Cancel) for Medicare and Medicaid OI processed cases.
    * @param  N/A
    * @return Map
    */ 

    public static Map<string,string> updateMedicareOITemplate()
    {
        Map<String,String> result = new Map<String,String>();
        boolean bTransferBtnDisabled = true;
        boolean bIsMedicareOIProcessAttached = false;
        string attachedTemplateName = '';
        string sAutoRouteStatus;
        List<CTCI_Junction__c> lstCTCIs = new List<CTCI_Junction__c>();
       
        
		if(editcaseData.Classification_Type__c !=null)
		{
			lstCTCIs = [SELECT Id, Name, Classification__r.name, 
						Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c,
						Intent__c, IsActive__c FROM 
						CTCI_Junction__c where Classification__r.name='COB' and Classification_Type__r.name  =: editcaseData.Classification_Type__c and IsActive__c=TRUE ];
		}
        Map<String, Set<String>> MedLOB = HumConstants_LWC.MEDICARE_LOB_PRODUCTTYPE_LIST();
		List <Template_Submission_Data__c> lstStateFlowValue  = UtilityHelper_H_AHT_HUM.getTemplateData(CaseAutoRouteConstant_C_HUM.MEDICAIRE_OI_TEMPLATE,CaseAutoRouteConstant_C_HUM.MEDICAIRE_OI_FIELD,editcaseData);
        if(lstStateFlowValue != null && !lstStateFlowValue.isEmpty() && editcaseData.Member_Plan_Id__c != null ){ 
            if(lstStateFlowValue[0].value__c!=null && CaseAutoRouteConstant_C_HUM.LST_MEDICAIRE_OI_INTENT_VALUES.contains(lstStateFlowValue[0].value__c)){
                if(lstCTCIs!= null && !lstCTCIs.isEmpty() )
                {         
                    List<MemberPlan> Pplan = [SELECT Product_Type__c,Product_Type_Code__c from MemberPlan where Id =:editcaseData.Member_Plan_Id__c limit 1];
                    		
                 if(!Pplan.isEmpty() && string.isNotBlank(Pplan[0].Product_Type__c)){	
				 if(lstStateFlowValue[0].value__c == 'Medical Update/Dispute')
                    {
                        if((Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MCD && Pplan[0].Product_Type_Code__c == null) || (Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MES && Pplan[0].Product_Type_Code__c == null) )
                        {
                            sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_MEDICALUPDATE;
                        
                        }
                            else if(MedLOB.containsKey(Pplan[0].Product_Type__c)){
                                if((MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_MAPD) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_MAPD) || (MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_MA) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_MA))
                                {
                                    sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_MEDICALUPDATE;
                                }
                            }
                    }
                    if( lstStateFlowValue[0].value__c == 'RX Update/Dispute') 
                    {
                        if((Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MCD && Pplan[0].Product_Type_Code__c == null))
                        {
                            sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMCDONLY;
                            
                        }else if(Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MES && Pplan[0].Product_Type_Code__c == null)
                        {
                              sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMESPDP;
                            
                        }else if(MedLOB.containsKey(Pplan[0].Product_Type__c))
                        {
                            if((MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_MAPD) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_MAPD) || (MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_PDP) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_PDP))
                            {
                                sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMESPDP;
                            }
                        }
                    } 
                    if(lstStateFlowValue[0].value__c== 'RX 090 Denial')
                    {
                        if((Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MCD && Pplan[0].Product_Type_Code__c == null)||(Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MES && Pplan[0].Product_Type_Code__c == null))
                        {
                           sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RX090DENIAL;
                        }else if(MedLOB.containsKey(Pplan[0].Product_Type__c))
                        {
                            if((MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_MAPD) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_MAPD)  || (MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_PDP) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_PDP) )
                            {
                                sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RX090DENIAL;
                            }
                        }
                    }
                    if(lstStateFlowValue[0].value__c== 'OI Prescription Drug Letter')
                    {
                        if(Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MES && Pplan[0].Product_Type_Code__c == null)
                        {
                            sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_OIPRESCRIPTION;
                        }else if(MedLOB.containsKey(Pplan[0].Product_Type__c))
                        {
                            if((MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_MAPD) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_MAPD)  || (MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_PDP) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_PDP)  || (MedLOB.get(Pplan[0].Product_Type__c).contains(HumConstants_LWC.PRODUCT_TYPE_MA) && Pplan[0].Product_Type_Code__c == HumConstants_LWC.PRODUCT_TYPE_MA))
                            {
                               sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_OIPRESCRIPTION;
                            }
                        }
                    }
                    if(lstStateFlowValue[0].value__c== 'Newborn' && (Pplan[0].Product_Type__c == HumConstants_LWC.MAJOR_LOB_MCD && Pplan[0].Product_Type_Code__c == null))
                    {
                        sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_NEWBORN;
                    }
                
                }
                 
                if(sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_MEDICALUPDATE|| sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMCDONLY || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMESPDP ||sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RX090DENIAL || sAutoRouteStatus== CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_OIPRESCRIPTION || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_NEWBORN)
                { 
                 	 for( integer i=0;i<lstCTCIs.size();i++)
						{
							if(lstCTCIs[i].intent__r.name == lstStateFlowValue[0].value__c)
							{
                                bIsMedicareOIProcessAttached = true;
                                attachedTemplateName= CaseAutoRouteConstant_C_HUM.MEDICAIRE_OI_TEMPLATE;
                                result.put('sAutoRouteStatus',sAutoRouteStatus);
                                result.put('sClassificationName',lstCTCIs[i].Classification__r.name);
                                result.put('sIntentName',lstCTCIs[i].Intent__r.Name);
								result.put('sClassification',lstCTCIs[i].Classification__c); 
								result.put('sIntent',lstCTCIs[i].Intent__c);
                                result.put('bHasCTCI', 'true');
							}
						}
                }
            }
        }
			
	}	
        result.put('attachedTemplateName',attachedTemplateName);
        result.put('isOItemplateAttached',''+bIsMedicareOIProcessAttached);   
        result.putAll(isMedicaidOITemplateAttached(result,editcaseData));
        bTransferBtnDisabled = enableDisableSaveAndTransferButton(editcaseData,result.get('attachedTemplateName'),result.get('sAutoRouteStatus'));
		result.put('bTransferBtnDisabled',''+bTransferBtnDisabled);			
		return result;
    }

    /*
    * This method Is to disable fields and buttons for Medicare/MCD Other Insurance Template 
    * @param  result, caseRec
    * @return Map<string,string>
    */

   public static Map<string,string> isMedicaidOITemplateAttached(Map<string,string> result, case CaseRec)
    {
        boolean bQueueUser = false;
        boolean bFlag = false;
        boolean bMedicareOI = false;
        Map<string,string> MedicareOI = new Map<string,string>();
        if(caseRec.status != 'Closed'){
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where 
                UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and 
                Group.Name ='Louisville Other Insurance PGQ' limit 1];
            bQueueUser = (lstGrpMem.size() > 0)?false:true;
            if(result.get('sAutoRouteStatus') == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_MEDICALUPDATE|| result.get('sAutoRouteStatus') == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMCDONLY || result.get('sAutoRouteStatus') == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMESPDP ||result.get('sAutoRouteStatus') == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RX090DENIAL || result.get('sAutoRouteStatus')== CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_OIPRESCRIPTION || result.get('sAutoRouteStatus') == CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_NEWBORN || (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'Louisville Other Insurance Investigation' )){
                bFlag = true;
            }
            if(bQueueUser && bFlag){
                List <Template_Submission_Data__c> flowValue = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c =  '9FD171F0-D1F0' AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id];
                bMedicareOI = !flowValue.isEmpty()? true:false;
            }
        }
        if(bMedicareOI) {
            MedicareOI.put('bdisablecloseCancel','true');
           }else{
            MedicareOI.put('bdisablecloseCancel','false');
        }
    	return MedicareOI; 
        }

    /*
     * This method is used to check if any case template attached or not
     * @param  case record id - caseId
     * @return Map<string,string>
     */
    @AuraEnabled
    public static Map<String,String> CheckCaseTemplates(id caseId)
    {
        Boolean isTransferButtonDisable = false;
        Map<String,String> result = new Map<String,String>();
        List<Template_Submission__c> lstTemplateSubmissions = New List<Template_Submission__c>();
        lstTemplateSubmissions = [SELECT Template__r.Unique_Template_Name__c,Template__r.Description__c FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:caseId];
      
        case caseRec = [select id,Member_Plan_Id__c,Classification_Type__c,Classification_Id__r.Name,Intent_Id__r.Name,AutoRoute_Status__c,status,Case_Owner__c,Guided_Process_Flag__c,Owner_Queue__c from case where id=:caseId];
        result.put('sAutoRouteStatus',caseRec.AutoRoute_Status__c);

        for(Template_Submission__c ObjTemp : lstTemplateSubmissions)
        {
            switch on ObjTemp.Template__r.Unique_Template_Name__c  
            {
                when '95C90830-3133' {
                    
                    result = isMedExpProcessAttachedExp(result, caseRec);
                    isTransferButtonDisable = enableDisableSaveAndTransferButton(caseRec, '95C90830-3133', caseRec.AutoRoute_Status__c);
                    result.put('bTransferBtnDisabled',''+isTransferButtonDisable);
                }
                when 'BC929CD6-E06A' {
                   
                    result = isMedExpProcessAttachedPartD(result, caseRec);
                    isTransferButtonDisable = enableDisableSaveAndTransferButton(caseRec, 'BC929CD6-E06A', caseRec.AutoRoute_Status__c);
                    result.put('bTransferBtnDisabled',''+isTransferButtonDisable);
                }
                when 'E0A13562-89EE-48B8-8EBA-E485C6583FF1' {
                    
                    result = isMedExpProcessAttachedGoodCause(result, caseRec);
                    isTransferButtonDisable = enableDisableSaveAndTransferButton(caseRec, 'E0A13562-89EE-48B8-8EBA-E485C6583FF1', caseRec.AutoRoute_Status__c);
                    result.put('bTransferBtnDisabled',''+isTransferButtonDisable);
                }
                when 'ATVF007' {
                    
                    result = isCreditableCoverage(caseRec);
                    isTransferButtonDisable = enableDisableSaveAndTransferButton(caseRec, 'ATVF007', caseRec.AutoRoute_Status__c);
                    result.put('bTransferBtnDisabled',''+isTransferButtonDisable);
                }
                when 'deeef952-9f63-49ce-97d0-c7eb3204b519' {
                    result = isMedicaidPARProviderAttached(result,caseRec,true,false);
                    isTransferButtonDisable = enableDisableSaveAndTransferButton(caseRec, 'deeef952-9f63-49ce-97d0-c7eb3204b519', caseRec.AutoRoute_Status__c);
                    result.put('bTransferBtnDisabled',''+isTransferButtonDisable);
                }
				when 'd884d160-77c0-4d1b-95aa-edf8c81f3a59' {
                    result = isPCPProcessAttached(result,caseRec);
                    isTransferButtonDisable = enableDisableSaveAndTransferButton(caseRec, 'd884d160-77c0-4d1b-95aa-edf8c81f3a59', caseRec.AutoRoute_Status__c);
                    result.put('bTransferBtnDisabled',''+isTransferButtonDisable); 
                }when '9FD171F0-D1F0' {
                    result = isMedicaidOITemplateAttached(result,caseRec);
                    isTransferButtonDisable = enableDisableSaveAndTransferButton(caseRec, CaseAutoRouteConstant_C_HUM.MEDICAIRE_OI_TEMPLATE, caseRec.AutoRoute_Status__c);
                    result.put('bTransferBtnDisabled',''+isTransferButtonDisable); 
                }
                
            }
        }
        List<Case_Action_Association__c> lstCaseActionAssociation = [SELECT Id, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = 'QAA' and Case__c = :caseRec.id];
        Boolean bSuccess =  (!lstCaseActionAssociation.IsEmpty())? true : false ;
        if(bSuccess)
        {
            result = isQAAProcessAttached(caseRec,caseRec.AutoRoute_Status__c);
        }else{
            if(HUMUtilityHelper.isCRMFunctionalityON('5231359')){
                if(caseRec.Status != 'Closed' && caseRec.Classification_Id__r.Name == 'Complaints or Compliments' && caseRec.Intent_Id__r.Name == 'QAA'){
                    result.put('disableClose','true'); 
                }
            }
        }
        return result;
    }
	
	public static Map<string,string> isPCPProcessAttached(Map<string,string>result,Case caseRec)
    { 
        List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and Group.Name =: CaseEditConstants_HUM.PCPGROUPNAME];
        Boolean bQueueUser = lstGrpMem != null && lstGrpMem.size() == 1 ? false : true;
        boolean bFlag = result != null && result.get('sAutoRouteStatus') != null && result.get('sAutoRouteStatus') == 'Completed' && caseRec.Owner_Queue__c == 'PCP Assignment Research' ? true : false;
        List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =: CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id AND Template_Field__r.Name =:CaseEditConstants_HUM.SERVICEFUNDCHECK LIMIT 1];
        if(flowValue != Null && !flowValue.isEmpty() && flowValue[0].Value__c == 'Yes'){
            boolean bPCPPCD = bQueueUser && caseRec.status != 'Closed' && bFlag ? true : false; 
            if(bPCPPCD) {
                result.put('bdisablecloseCancel','true');
                result.put('bDisableCaseStatus','true');
                result.put('bDisableHPClosedCaseBtn','true');
                result.put('bDisableCancelCaseBtn','true');
                result.put('bDisableCloseCaseBtn','true');
            }
        }
        return result;
    }
	
	public static Map<String,String> updateCaseAfterPCP()
    {   
        Map<String,String> result = new Map<String,String>();
        boolean bTransferBtnDisabled = true;
        List<Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =: CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:editcaseData.Id AND Template_Field__r.Name =:CaseEditConstants_HUM.SERVICEFUNDCHECK LIMIT 1];
        if(flowValue != null && !flowValue.isEmpty() && flowValue[0].Value__c == 'Yes'){    
            if(editcaseData.AutoRoute_Status__c != 'Completed')
            {   
                editcaseData.status = 'In Progress';
            }
        }    
        result.put('sAutoRouteStatus',CaseEditConstants_HUM.AUTOROUTING_PCP);
        result.put('attachedTemplateName',CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID);
        result.put('status',editcaseData.status);
        result.putAll(isPCPProcessAttached(result,editcaseData));
        bTransferBtnDisabled = enableDisableSaveAndTransferButton(editcaseData,result.get('attachedTemplateName'),result.get('sAutoRouteStatus'));
        result.put('bTransferBtnDisabled',''+bTransferBtnDisabled);				
        return result;
    }
	

    /*
    * This method Is to disable fields and buttons for Creditable coverage process
    * @param  case record - caseRec
    * @return Map<string,string>
    */
    public static Map<string,string> isCreditableCoverage(case caseRec)
    {
        Map<string,string> result = new Map<string,string>();
        if(caseRec.Case_Owner__c  == CaseDetailConstants_HUM.CASE_RSO_ENROLLMENT_OWNER && caseRec.Guided_Process_Flag__c == true && caseRec.status == CaseDetailConstants_HUM.CASE_STATUS_PENDING_RESPONSE) 
        {
            result.put('bdisablecloseCancel','true');
        }
        return result;
    }

    /*
    * This method Is to disable fields and buttons for QAA Proces
    * @param  case Records, Auto route status
    * @return Map<string,string>
    */
    public static Map<String,String> isQAAProcessAttached(case caseRec, string sAutoRouteStatus)
    {    
        String segmentIndicator = '';
		String issueState = '';
        String productType = '';
		String groupNumber = '';
        Map<String,String> result = new Map<String,String>();

        List<String> lstPublicGroups = new List<String>{CaseDetailConstants_HUM.Group_LV_Correspondence_Screening_PGQ, CaseDetailConstants_HUM.Group_LV_Correspondence_Screening_PGVE, CaseDetailConstants_HUM.Group_LV_Correspondence_Screening_PGVP,CaseDetailConstants_HUM.GOODCAUSEGROUPNAME,CaseDetailConstants_HUM.PCPGROUPNAME};
        lstPublicGroups.addAll(CaseAutoRouteConstant_C_HUM.QAAGroupNames);
        lstPublicGroups.add(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP);
        lstPublicGroups.add(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_KY);
        
        List<MemberPlan> lstMemPlan = [SELECT Issue_State__c, Segment_Indicator__c, Product_Type__c, GroupNumber FROM MemberPlan WHERE Id = :caseRec.Member_Plan_Id__c LIMIT 1];
        if(!lstMemPlan.isEmpty()){            
            segmentIndicator =lstMemPlan[0].Segment_Indicator__c;
            issueState = lstMemPlan[0].Issue_State__c;
            productType = lstMemPlan[0].Product_Type__c;                
			groupNumber = lstMemPlan[0].GroupNumber;
        }
        
        List<GroupMember> lstGrpMems = New List<GroupMember>();
        lstGrpMems = [Select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and Group.Name IN : lstPublicGroups];
        if(isQAAAutoRoute(caseRec,segmentIndicator,issueState,productType,groupNumber)){
              
            String pgqName ='';
            if(null != segmentIndicator && CaseAutoRouteConstant_C_HUM.LST_MEDICAREPLANS.contains(segmentIndicator)) {
                pgqName=CaseAutoRouteConstant_C_HUM.QAAGroupPlanValue.get('Medicare');
            }else{
                pgqName=CaseAutoRouteConstant_C_HUM.QAAGroupPlanValue.get(issueState);
            }
            boolean bQueueUser = true;
            if(lstGrpMems.size() > 0){
                bQueueUser = false;
            }
            boolean bFlag = false;
            if((sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_FL_MCD || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_KY_IL_MCD || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICARE  )|| (caseRec.AutoRoute_Status__c == 'Completed' && CaseAutoRouteConstant_C_HUM.QAAQueues.contains(caseRec.Owner_Queue__c))){
                bFlag = true;
            }
            Boolean bQAA = (bQueueUser &&  bFlag && caseRec.status != 'Closed')? true : false;
            if(bQAA){
				result.put('bTransferBtnDisabled','true');
                result.put('bdisablecloseCancel','true');
             
            }
        }else{
			result.put('bTransferBtnDisabled','false');
            result.put('bdisablecloseCancel','false');
          
        }

        return result;
    } 
    /*
        * This method Is to update classification and Intent fields for Medicaid Newborn Notification Proces
        * @param  CaseId, Classification Id, Intent Id
        * @return void
    */ 
    @AuraEnabled
    public static Map<String,String> updateNewbornNotification(String Id){
        Case caseRec = [select Id,Classification_Type__c from Case where Id=: Id];
        Map<String,String> result = new Map<String,String>();        
        List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Classification__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name='Enrollment' and Intent__r.Name='Newborn' and Classification_Type__r.name = :caseRec.Classification_Type__c  and IsActive__c=TRUE];
		if(null!= lstCTCIs  && !lstCTCIs.isEmpty()){
            result.put('sClassification',lstCTCIs[0].Classification__c); 
            result.put('sIntent',lstCTCIs[0].Intent__c);
            result.put('bdisablecloseCancel','false');
            result.put('bTransferBtnDisabled','false');
            result.put('bHasCTCI','true');
            result.put('CTCILIstId',lstCTCIs[0].Id);
            caseRec.Classification_Id__c = lstCTCIs[0].Classification__c;
            caseRec.Intent_Id__c = lstCTCIs[0].Intent__c;
            caseRec.CTCI_List__c = lstCTCIs[0].Id;
            update caseRec;
        }						       
	    return result;
    }
    /*
		* This method check policy type is MCD or not
		* @param  memplanId
		* @return boolean
    */
	@AuraEnabled
    public Static boolean isMCDPolicy(String memplanId){        
		if(!String.isBlank(memplanId)){  
            List<MemberPlan> mp = [select Product__c,Product_Type__c,Product_Type_Code__c from memberplan where id =:ESAPI.encoder().SFDC_HTMLENCODE(memplanId)];
            if(!mp.isEmpty() && mp[0].Product__c=='MED' && mp[0].Product_Type__c=='MCD' && String.isBlank(mp[0].Product_Type_Code__c)){
                return true;
            }
        }
        return false;
    }
    public Static string getMemberPlanName(String memplanId){
        string MemPlanName;
		if(!String.isBlank(memplanId)){  
            MemberPlan mp = [select Id,Name from memberplan where id =:memplanId limit 1];
            MemPlanName = mp.Name;
        }
        return MemPlanName;
    }

    /*
		* method : isNewbornReq
		* @param  CaseID
		* @return boolean
    */
    @AuraEnabled
    public Static boolean isNewbornReq(String Id){
        List<Case> caseRec = [select Id,Classification_Type__c,Classification_Id__r.name, Intent_Id__r.name,Member_Plan_Id__c from Case where Id=: Id Limit 1];        
		Boolean hasNewbornProcess = false;
        Boolean hasNewbornRequired = false;
        String templateName ='Medicaid_Newborn_Notification';
        if(!caseRec.isEmpty() && !String.isBlank(caseRec[0].Member_Plan_Id__c) && 
        caseRec[0].Classification_Id__r.name=='Enrollment' && caseRec[0].Intent_Id__r.name=='Newborn' &&
        caseRec[0].Classification_Type__c =='Medicaid Calls'){  
            List<MemberPlan> mp = [select Product__c,Product_Type__c,Product_Type_Code__c from memberplan where id =:caseRec[0].Member_Plan_Id__c];
            if(!mp.isEmpty() && mp[0].Product__c=='MED' && mp[0].Product_Type__c=='MCD' && String.isBlank(mp[0].Product_Type_Code__c)){
                hasNewbornRequired=true;
            }
        }
        if(hasNewbornRequired){
        List<Template_Submission__c> lstTemplatesData = [SELECT Id, Template__r.Unique_Template_Name__c,(SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__r) FROM Template_Submission__c where Submission_Owner__r.Object_Owner_ID__c = :Id and Template__r.Unique_Template_Name__c=:templateName];
                if(lstTemplatesData != null && !lstTemplatesData.isEmpty()){
                    List<Template_Submission_data__c> newbornDataFields = [SELECT Name__c, Value__c FROM Template_Submission_Data__c where Template_Submission__c=: lstTemplatesData[0].Id];
                    for(Template_Submission_data__c newbornDataField : newbornDataFields){
                        if(newbornDataField.Value__c !='' && newbornDataField.Value__c !=null){
                            hasNewbornProcess = true;
                            break;
                        }
                    }
                }
            }
        return hasNewbornRequired== true && hasNewbornProcess==false ? true:false;
    }

    /*
        * Method Name : checkIsCredentialing
        * Description : Check if the case eligble for MCD
        * Return type : boolean
        * Parameter   : ID caseId
    */ 
    public Static boolean checkIsCredentialing(Id caseId){
        List<Case> lstCase = [select id,Owner_Queue__c,Classification_Id__r.name ,Intent_Id__r.name from Case Where Id=:caseId];
        List<Task> lstTask = [select id from Task where whatId=:caseId];
            if(!lstCase.isEmpty() && !lstTask.isEmpty()){
                if(lstCase[0].Owner_Queue__c == CaseEditConstants_HUM.MCD_OWNER_Q && 
                lstCase[0].Classification_Id__r.name == CaseEditConstants_HUM.MCD_Classification && 
                lstCase[0].Intent_Id__r.name == CaseEditConstants_HUM.MCD_Intent){
                    return true;
                }
            }
        return false;
    }
     /*
        * Method Name : getCaseNumber
        * Description :get the casenumber for given case id
        * Return type : String
        * Parameter   : ID caseId
    */ 
    @AuraEnabled
    public static String getCaseNumber(string CaseId){
        string casenum;
        case Caserec =[select id , CaseNumber from case where id =:CaseId limit 1 ];
        casenum = Caserec.CaseNumber;
        return casenum;
        
    }

/*
		* method : isLegacyDeleteMemberOrPolicy
		* @param  CaseID
		* @return boolean
    */
    @AuraEnabled
    public Static boolean isLegacyDeleteMemberOrPolicy(Id Id){
        boolean isLegacyDelete = false;
        List<Case> lstCase = [SELECT Member_Plan_Id__r.ETL_Record_Deleted__c,Account.ETL_Record_Deleted__c FROM Case WHERE Id = :Id LIMIT 1];
        if(!lstCase.isEmpty()){
            if(lstCase[0].Member_Plan_Id__r.ETL_Record_Deleted__c == true || lstCase[0].Account.ETL_Record_Deleted__c == true){
                isLegacyDelete = true;
            }
        }
        return isLegacyDelete;
    }    
}