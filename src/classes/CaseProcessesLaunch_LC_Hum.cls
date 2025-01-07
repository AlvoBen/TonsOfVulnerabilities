/*******************************************************************************************************************************
Apex Class Name : CaseProcessesLaunch_LC_Hum
Version         : 1.0
Created On      : 2/24/2022
Function        : This class is used for displaying OI Process Template
Test Class      : CaseProcessesLaunch_LT_Hum

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Isha Gupta                                          12/06/2021                 Original Version
*    2.0           Santhi Mandava                                      07/28/2022                 Implemented configurable templates
*    3.0           Santhi Mandava                                      09/01/2022                 US3279633 - QAA template changes
*    4.0           Bhumika Dodiya                                      09/02/2022                 US3751656 - Case Resolution - Action Component - Recommended Process Template Restriction based on Status of Case
* 	 5.0	       Muthukumar                                          09/14/2022                 DF-6166 Fix
*    6.0           Santhi Mandava                                      11/01/2022                 US:3813238 Displaying Update plan demographics template issue fix
*    7.0           Prasuna Pattabhi                                    12/21/2022                 US:4020210 QAA Template
*    8.0           Prasuna Pattabhi                                    12/23/2022                 build fix
*    8.0           Prasuna Pattabhi                                    12/27/2022                 Camel case issue fix
*    7.0           Nirmal Garg                                          12/29/2022                  DF-6855 fix
* 	 10.0			Prasuna Pattabhi             		02/01/2023                      US-4178418 : TRR Process Template on Case Details Page
* 	 11.0			Prasuna Pattabhi             		02/07/2023                      US-4178418 : TRR Process Template on Case Details Page Delete Fix
* 	 12.0			Prasuna Pattabhi             		02/07/2023                      Remove Template Submission glich fix for TRR
*    13.0         Aishwarya Pawar                   02/02/2023                   REQ 4211868 - Ability to view multiple processes in an invoice request
* 	 14.0			Prasuna Pattabhi             		02/08/2023                      Remove Template Submission glich fix for TRR
* 	 15.0			Prasuna Pattabhi             		02/14/2023                      TRR Business Feedback
*    16.0           Prasuna Pattabhi                  06/14/2023                 US 4467570 : Launching and opening Newborn Notification Template (baby bot) for Medicaid (Lightning)
*    17.0           Pooja Kumbhar	                  06/29/2023                     US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
*	 18.0			Pinky Vijur                       03/01/2024                 User Story 5203789: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch button /link and existing flow
***********************************************************************************************************************/
public  with sharing class CaseProcessesLaunch_LC_Hum {

    @testVisible public static List<String> CaseActions = new List<String>{'QAA Complaint'};

    /*
    * Method Name : getTemplateSubmissionId
    * Description : this method is used to get template submission id for any case
    * Return type : String
    * Parameter   : objId
    */
    @AuraEnabled
    public static String getTemplateSubmissionId(String caseId) {
        String tsid;
        List<Template_Submission_Owner__c> tso = [SELECT Object_Owner_ID__c , id From Template_Submission_Owner__c where Object_Owner_ID__c = :caseID];
        if(tso.size() > 0 ){
            tsid = [SELECT id From Template_Submission__c where Submission_Owner__c = :tso[0].id].id;
        }
        return tsid;
    }


    @AuraEnabled
    public static List<TemplateSetting__mdt> getTemplateDetails(){
        return [SELECT Id, MasterLabel, Template_Name__c,Template_Label__c, IsVisible__c, Description__c,Params__c,Template_Type__c, Order__c FROM TemplateSetting__mdt WHERE IsVisible__c = true  order by Order__c];
      }

    /*
    * Method Name : removeTemplateSubmission
    * Description : this method is used to remove template records if data is not entered else to return history data if data is present.
    * Return type : object
    * Parameter   : objId
    */
    @AuraEnabled
    public static WrapperResponse removeTemplateSubmission(String caseId,Boolean isCancel) {
        WrapperResponse responseWrapper = new WrapperResponse();
        String newbornSubmissionId = isNewbornProcessAttached(caseId);
        list<CaseProcessTemp_DTO_HUM> lstCaseArt = new  list<CaseProcessTemp_DTO_HUM>();
        try{
            Boolean isValidDataPresent = false;
            List<Template_Submission_Owner__c> lstSubOwnerToDelete = new List<Template_Submission_Owner__c>();
            List<Template_Submission__c> lstSubsToDelete = new List<Template_Submission__c>();
            Map<Id,Template_Submission__c> mapSubmissions = new Map<Id,Template_Submission__c>();
            Map<Id,Template_Submission_Owner__c> mapSubOwners = new Map<Id, Template_Submission_Owner__c>([SELECT Id, Object_Owner_ID__c, (Select Id,Template__r.Description__c from Template_Submissions__r), Object_Owner_Type__c FROM Template_Submission_Owner__c WHERE Object_Owner_ID__c =: caseId AND Object_Owner_Type__c ='Case']);
            String trrDescription = 'Transaction Reply Report (TRR)';
			if(mapSubOwners != null && !mapSubOwners.isEmpty()){
                for(Template_Submission_Owner__c objOwner : mapSubOwners.values()){
                    if(objOwner.Template_Submissions__r != null && !objOwner.Template_Submissions__r.isEmpty()){
                        for(Template_Submission__c objSub : objOwner.Template_Submissions__r){
                            if(objSub.Template__r.Description__c != trrDescription){
							if(isCancel){
                                if(newbornSubmissionId!=null && newbornSubmissionId==objSub.Id){
                                    mapSubmissions.Put(objSub.Id,objSub);
                                    lstCaseArt = getWrapperClass(caseId);
                                }else{
                                    lstSubOwnerToDelete.Add(objOwner);
                                    lstSubsToDelete.Add(objSub);
                                }
                            }else{
                                mapSubmissions.Put(objSub.Id,objSub);
                            }
							}
                        }
                    }else{
                        //If any template submission owner doesnt have template submission then that owner record should be deleted
                        lstSubOwnerToDelete.Add(objOwner);
                    }
                }
            }
            //Deleating template data if user selects cancel button on template creation UI
            if(isCancel){
                delete lstSubsToDelete;
                delete lstSubOwnerToDelete;
            }else{
                if(mapSubmissions != Null && !mapSubmissions.isEmpty()){
                    List<Template_Submission__c> lstFinalSubmissions = [Select Id ,Submission_Owner__c, (Select Id, Value__c from Template_Submission_Data__r) FROM Template_Submission__c Where  Id IN : mapSubmissions.keySet()];
                    If(lstFinalSubmissions != Null && !lstFinalSubmissions.isEmpty()){
                        For(Template_Submission__c ts:lstFinalSubmissions){
                            Boolean toDelete = true;
                            If(ts.Template_Submission_Data__r != Null && !ts.Template_Submission_Data__r.isEmpty()){
                                //Validating template data
                                for(Template_Submission_Data__c tsd : ts.Template_Submission_Data__r){
                                    if(String.isNotBlank(tsd.Value__c)){
                                        toDelete = false;
                                        isValidDataPresent = true;
                                        break;
                                    }
                                }
                                if(toDelete){
                                    //If template submission data is not valid then template submission data records,template submission and submission owner should be deleted
                                    lstSubsToDelete.Add(ts);
                                    lstSubOwnerToDelete.Add(new Template_Submission_Owner__c(Id=ts.Submission_Owner__c));
                                }
                            }else{
                                //If any template submission doesnt have template submission data records then that submission and corresponding owner records should be deleted
                                lstSubsToDelete.Add(ts);
                                lstSubOwnerToDelete.Add(new Template_Submission_Owner__c(Id=ts.Submission_Owner__c));
                            }
                        }
                    }
                }
                if(lstSubsToDelete != Null && !lstSubsToDelete.isEmpty()) delete lstSubsToDelete;
                if(lstSubOwnerToDelete != Null && !lstSubOwnerToDelete.isEmpty()) delete lstSubOwnerToDelete;
                if(isValidDataPresent) lstCaseArt = getWrapperClass(caseId);
                else lstCaseArt = getWrapperClass(caseId); // if we don't get data from template objects then we need to retrieve data from case action objects. Ex :QAA
            }

            if(lstCaseArt == Null || (lstCaseArt != Null && lstCaseArt.size() == 0 )){
                list<Case_Action_Association__c> lstCaseAccAssn = [SELECT Id,CreatedBy.Name, Case__c,CreatedDate,LastModifiedDate,LastModifiedBy.Name, Case_Action__c,Case_Action__r.Name, Action__c FROM Case_Action_Association__c Where Case__c =: caseId];
                CaseProcessTemp_DTO_HUM OcaseArt;
                for(Case_Action_Association__c objRes : lstCaseAccAssn){
                    OcaseArt = new CaseProcessTemp_DTO_HUM();
                    OcaseArt.LastModifiedBy = objRes.LastModifiedBy.Name;
                    String createdDate = objRes.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                    OcaseArt.CreatedDate = createdDate;
                    String lastModifiedDate = objRes.LastModifiedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                    OcaseArt.LastModifiedDate = lastModifiedDate;
                    OcaseArt.ProcessType = objRes.Case_Action__r.Name;
                    OcaseArt.CreatedBy = objRes.CreatedBy.Name;
                    OcaseArt.CaseTemplateId = objRes.Id;
                    lstCaseArt.add(OcaseArt);
                }
            }

            Case csRec = [SELECT Id,Origin,Member_Plan_ID__c,Member_Plan_ID__r.Name ,Account.Id,Account.Name,Account.Birthdate__c, RecordType.DeveloperName FROM Case where id = :caseId];
            Template__c templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Centerwell Pharmacy Web Issues' Limit 1];
            String profileName = [SELECT Id, Name FROM Profile WHERE Id=:userinfo.getProfileId()].Name;
            String recordtypeName = csRec.RecordType.DeveloperName;
            responseWrapper.CaseProcessTemp_DTO_HUMList = lstCaseArt;
            responseWrapper.CaseRecordType = recordtypeName;
            responseWrapper.ProfileName = profileName;
            responseWrapper.MemberPlanId = csRec.Member_Plan_ID__c;
            responseWrapper.MemberId = csRec.Member_Plan_ID__r.Name;
            responseWrapper.MemberName = csRec.Account.Name;
            responseWrapper.MemberBirthday = csRec.Account.Birthdate__c;
            responseWrapper.TempValue = templateNumber.Name;
            responseWrapper.bSubtabCloseSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('SubtabClose').IsON__c;
            return responseWrapper;
        }
        catch(Exception e){
            responseWrapper.CaseProcessTemp_DTO_HUMList = null;
            responseWrapper.Error = e.getMessage()+ ' at line no. '+e.getLineNumber();
            return responseWrapper;
        }
    }

    /*
* Method Name : getWrapperClass
* Description : this method is used to find the details related to executed OI Process Template
* Return type : object
* Parameter   : objId
*/
    @AuraEnabled
    public static list<CaseProcessTemp_DTO_HUM> getWrapperClass(Id sCaseId){
        String trrDescription = 'Transaction Reply Report (TRR)';
        list<CaseProcessTemp_DTO_HUM> lstCaseTemplates = new list<CaseProcessTemp_DTO_HUM>();
        list<Template_Submission_Owner__c> lstSubOwner = [SELECT (SELECT Name, Template__r.Description__c, LastModifiedDate, LastModifiedBy.Name, LastModifiedById,
                                                                  CreatedDate, CreatedBy.Name, CreatedById,Template__r.Unique_Template_Name__c FROM template_submissions__r)
                                                          FROM template_submission_owner__c WHERE Object_Owner_ID__c =: sCaseId];
        if(lstSubOwner!=null && !lstSubOwner.isEmpty())
        {
            for(Template_Submission_Owner__c oSubOwner : lstSubOwner)
            {
                if(oSubOwner.template_submissions__r!=null && !oSubOwner.template_submissions__r.isEmpty())
                {
                    for(Template_Submission__c oTempSub : oSubOwner.template_submissions__r)
                    {
                        if(oTempSub.Template__r.Description__c != trrDescription){
                            CaseProcessTemp_DTO_HUM OcaseArt = new CaseProcessTemp_DTO_HUM();
                            OcaseArt.LastModifiedBy = oTempSub.LastModifiedBy.Name;
                            String createdDate = oTempSub.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                            OcaseArt.CreatedDate = createdDate;
                            String lastModifiedDate = oTempSub.LastModifiedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                            OcaseArt.LastModifiedDate = lastModifiedDate;
                            OcaseArt.ProcessType = oTempSub.Template__r.Description__c;
                            OcaseArt.CreatedBy = oTempSub.CreatedBy.Name;
                            OcaseArt.CaseTemplateId = oTempSub.Id;
                            OcaseArt.CaseTemplateName = oTempSub.Name;
                            OcaseArt.TemplateSubmissionOwnerId = oSubOwner.Id;

                            lstCaseTemplates.add(OcaseArt);
                        }
                    }
                }
            }
        }
        return lstCaseTemplates;
    }

    /*
* Method Name : showOIMEdicareForm
* Description : this method is used to check whether a case is eligible to have OI Form link
* Return type : Boolean
* Parameter   : objId
*/
    @AuraEnabled
    public static Boolean showOIMEdicareForm(Id sCaseId){
        Boolean showOI = false;
        String username =  UserInfo.getUserName();
        String profileName = [SELECT Id, Name FROM Profile WHERE Id=:userinfo.getProfileId()].Name;
        Id CRMS_1210_Commercial_Demographic_Update_PSId = [SELECT id FROM PermissionSet where Name = 'CRMS_1210_Commercial_Demographic_Update'].id;
        Id CRMS_1200_MedicareMedicaid_Demographic_Update_PSId = [SELECT id FROM PermissionSet where Name = 'CRMS_1200_MedicareMedicaid_Demographic_Update'].id;
        List<PermissionSetAssignment> psas = [ select Id from PermissionSetAssignment where PermissionSetId IN (:CRMS_1210_Commercial_Demographic_Update_PSId,:CRMS_1200_MedicareMedicaid_Demographic_Update_PSId) and AssigneeId IN (select Id from User where UserName = :username ) ];
        Case csRec = [SELECT Id,Origin, RecordType.DeveloperName,Member_Plan_Id__r.Plan.Product__r.Product_Type__c,Case_Owner_Queue__r.Name FROM Case where id = :sCaseId];
        String recordtypeName = csRec.RecordType.DeveloperName;
        if(new List<String>{'Agent_Broker_Case','Group_Case','Provider_Case','HP_Agent_Broker_Case','HP_Group_Case','HP_Provider_Case'}.contains(recordtypeName)){
            showOI=true;
        } else if(new List<String>{'Member_Case','HP_Member_Case'}.contains(recordtypeName)) {
            if(csRec.Member_Plan_Id__r.Plan.Product__r.Product_Type__c != 'PDP' && psas.size() > 0) {
                if(new List<String>{'Customer Care Specialist','Customer Care Supervisor'}.contains(profileName)){
                    showOI=true;
                } else if (profileName == 'Humana Pharmacy Specialist'){
                    if(csRec.Origin != 'Web Chat' && !(new List<String>{'Humana Pharmacy Web Chat' , 'HP Clinical Services Web Chat'}.contains(csRec.Case_Owner_Queue__r.Name))) {
                        showOI=true;
                    } else if (!new List<String>{'Provider Payment Integrity', 'Louisville Other Insurance Investigation', 'LV Medicare Member Communications', 'Louisville RSO Oral Grievance Oversight'}.contains(csRec.Case_Owner_Queue__r.Name)){
                        showOI=true;
                    }  else if (!recordtypeName.contains('HP')) {
                        showOI=true;
                    }
                }
            }
        }
        return showOI;
    }

    //this class is used for storing the history of executed process template
    public with sharing class CaseProcessTemp_DTO_HUM
    {
        @AuraEnabled
        public String ProcessType = '';
        @AuraEnabled
        public String CreatedDate = '';
        @AuraEnabled
        public String CreatedBy  = '';
        @AuraEnabled
        public String LastModifiedDate = '';
        @AuraEnabled
        public String LastModifiedBy = '';
        @AuraEnabled
        public String CaseTemplateId = '';
        @AuraEnabled
        public String TemplateSubmissionId = '';
        @AuraEnabled
        public String TemplateSubmissionOwnerId = '';
        @AuraEnabled
        public String CaseTemplateName = '';
    }

    public with sharing class WrapperResponse
    {
        @AuraEnabled
        public List<CaseProcessTemp_DTO_HUM> CaseProcessTemp_DTO_HUMList;
        @AuraEnabled
        public string CaseRecordType = '';
        @AuraEnabled
        public string Error = '';
        @AuraEnabled
        public string ProfileName = '';
        @AuraEnabled
        public string MemberPlanId = '';
        @AuraEnabled
        public Boolean bSubtabCloseSwitch = true;
        @AuraEnabled
        public List<Template_Submission_Data__c> lstTemplateSubmittionData;
        @AuraEnabled
        public List<Case_Action_Response__c> lstCaseActionResponse;
        @AuraEnabled
		public string MemberName = '';
        @AuraEnabled
        public string MemberBirthday = '';
        @AuraEnabled
        public string TempValue = '';
        @AuraEnabled
        public string MemberId = '';
    }

    public with sharing class TemplateVisiblilityWrap{

        @AuraEnabled
        public Boolean bRedeterminationPartD = false;
        @AuraEnabled
        public Boolean bGoodCauseReinstatement = false;
    }

    @auraEnabled
    Public Static WrapperResponse getdatafromTemplateSubmission(String sTemplateName,Id templateId){
        System.debug('getting inside to fetch');
        System.debug('tempalte name'+sTemplateName);
        System.debug('template Id'+templateId);
        WrapperResponse responseWrapper = new WrapperResponse();
        if(CaseActions.contains(sTemplateName))
        {
            responseWrapper.lstCaseActionResponse =[SELECT Id, Case_Question__r.Question_Label__c, Process_Question_Response__c FROM Case_Action_Response__c where Action__c =: templateId];
        }
        else {
            System.debug('entering else part 1');
            responseWrapper.lstTemplateSubmittionData =[Select id,Name,Name__c,Template_Field__c,Template_Submission__c,Value__c from Template_Submission_Data__c where Template_Submission__c=:templateId];
			System.debug('entering else part 2'+responseWrapper.lstTemplateSubmittionData);
        }
        return responseWrapper;
    }

    /*
    * Method Name : getCaseStatus
    * Description : this method is used to get case status from caseID
    * Return type : String
    * Parameter   : caseId
    */
    @AuraEnabled
    public static String getCaseStatus(String sCaseId) {
        String caseStatus;
        List<Case> caseDetail = [SELECT Status FROM Case where id = :sCaseId];
        if(caseDetail.size() > 0 ){
            caseStatus = caseDetail[0].Status;
        }
        return caseStatus;
    }
    /*
    * Method Name : isProcessAttached
    * Description : this method Is used To check process attached Or Not
    * Return type : Boolean
    * Parameter   : caseId ,template Name
    */
    @AuraEnabled
    public static boolean isProcessAttached(String Id,String uniqueTempName){
        boolean hasProcess = true;
        Map<String,String> templateData = new Map<String,String>();
        templateData.put('Medicare/Medicaid Expedited Appeal','95C90830-3133');
        templateData.put('Medicare Part D Redetermination','BC929CD6-E06A');
        templateData.put('Medicare Good Cause Reinstatement Form','E0A13562-89EE-48B8-8EBA-E485C6583FF1');
        templateData.put('Medicaid PAR Provider Not Accepting','deeef952-9f63-49ce-97d0-c7eb3204b519');
        templateData.put('Medicare Creditable Coverage Attestation','ATVF007');
        templateData.put('Medicaid Newborn Notification','Medicaid_Newborn_Notification');
        templateData.put('Medicare and Medicaid Other Insurance Form',CaseAutoRouteConstant_C_HUM.MEDICAIRE_OI_TEMPLATE);
        If (uniqueTempName =='QAA Complaint'){
            List < Case_Action_Association__c > lstCaseActionAssociation = [SELECT Id, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = 'QAA' and Case__c = :Id];
            hasProcess =  (!lstCaseActionAssociation.isEmpty())? true : false ;
        }else if(templateData.get(uniqueTempName)!=null){
            List<Template_Submission__c> lstTemplatesData = [SELECT Id, Template__r.Unique_Template_Name__c,(SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__r) FROM Template_Submission__c where Submission_Owner__r.Object_Owner_ID__c = :Id];
            hasProcess = false;
            if(lstTemplatesData != null && !lstTemplatesData.isEmpty()){
                for(Template_Submission__c ts : lstTemplatesData){
                    if (ts.Template__r.Unique_Template_Name__c == templateData.get(uniqueTempName)){
                        hasProcess = true;
                        break;
                    }
                }
            }else{
                hasProcess = false;
            }
        }
        return hasProcess;
    }
	/*
    * Method Name : hasTRRProcess
    * Description : This method is used to get the no of TRR processes assocaited with the case and the data
    * Return type : Map
    * Parameter   : caseId, showAll,noOfRecords
    */

    @AuraEnabled
    public static Map<String,String> hasTRRProcess(String sCaseId,boolean showAll,Integer noOfRecords) {

        Map<String,String> result = new Map<String,String>();
        boolean hasTRR = false;
        Integer totalNoOfProcesses = 0;
        boolean showViewAll = false;
		String Origin = null;
        List<Case> caseDetail = [SELECT Origin,caseNumber FROM Case where id = :sCaseId];
        if(!caseDetail.isEmpty()){
            hasTRR = caseDetail[0].Origin=='CRMTRR'?true:false;
			Origin = caseDetail[0].Origin;
            result.put('caseNumber',caseDetail[0].caseNumber);
        }
        result.put('hasTRR',''+hasTRR);
        if(hasTRR){
            list<CaseProcess_LC_DTO_HUM>  templateData = getTRRData(sCaseId,Origin);
            result.put('totalNoOfProcesses',''+templateData.size());
            showViewAll = false;
            if(templateData.size()>0){
                if(showAll){
                    result.put('data',JSON.serialize(templateData));
                    showViewAll = false;
                }else{
                    if(templateData.size()>noOfRecords){

                        list<CaseProcess_LC_DTO_HUM> templateRecords = new List<CaseProcess_LC_DTO_HUM>();
                        Integer count = 0;
                        while(count < noOfRecords){
                            templateRecords.add(templateData.get(count));
                            count++;
                        }
                        templateRecords[noOfRecords-1].isLast = true;
                        result.put('data',JSON.serialize(templateRecords));
                        showViewAll = true;
                    }else{
                        templateData[templateData.size()-1].isLast = true;
                        result.put('data',JSON.serialize(templateData));
                        showViewAll = false;
                    }
                }
            }
        }
        result.put('showViewAll',''+showViewAll);
        return result;
    }

    /*
    * Method Name : getTRRData
    * Description : this method is used to get case TRR Process Data from caseID
    * Return type : object
    * Parameter   : caseId
    */
    public static list<CaseProcess_LC_DTO_HUM> getTRRData(Id sCaseId,String Origin){
        //HUMValidSubmissionHelper.deleteInValidSubmission(sCaseId);
        ID currentUser = UserInfo.getUserId();
        User uUser = [SELECT Id, Current_Queue__c FROM User WHERE Id = :currentUser];
        list<CaseProcess_LC_DTO_HUM>  lstCaseArt = new list<CaseProcess_LC_DTO_HUM>();
        if(string.isNotBlank(Origin) && (Origin.equals('CRMTRR'))){
            list<Template_Submission_Data__c> lstTempData = [SELECT Id, Name, Template_Field__c, Template_Submission__c,
            Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c, Template_Submission__r.Submission_Owner__r.Object_Owner_Type__c, Name__c,
            Value__c, Template_Submission__r.CreatedDate, Template_Submission__r.CreatedBy.Name, Template_Submission__r.CreatedById,
            Template_Submission__r.Template__r.Description__c, Template_Submission__r.Name FROM Template_Submission_Data__c WHERE
            Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :sCaseId AND
            Template_Submission__r.Template__r.Unique_Template_Name__c ='TRR_Template'
            AND Name__c IN ('Reply Code','Effective Date','Processing Date Stamp')];

            if(lstTempData != null && !lstTempData.isEmpty()){
                Map<String, list<Template_Submission_Data__c>> mapTempDataBySubmission = new Map<String, list<Template_Submission_Data__c>>();
                for(Template_Submission_Data__c tempData : lstTempData){
                    if(mapTempDataBySubmission!=null && !mapTempDataBySubmission.isEmpty() &&  mapTempDataBySubmission.containsKey(tempData.Template_Submission__c)){
                        mapTempDataBySubmission.get(tempData.Template_Submission__c).add(tempData);
                    }else {
                        mapTempDataBySubmission.put(tempData.Template_Submission__c, new List<Template_Submission_Data__c>{tempData});
                    }
                }
                if(!mapTempDataBySubmission.isEmpty()){
                    for(string submission : mapTempDataBySubmission.keySet()){
                        CaseProcess_LC_DTO_HUM oCaseArt = new CaseProcess_LC_DTO_HUM();
                        List<Template_Submission_Data__c> lstTempSubData = mapTempDataBySubmission.get(submission);
                        if(lstTempSubData !=null && !lstTempSubData.isEmpty()){
                            oCaseArt.CreatedDate = String.ValueOf(lstTempSubData[0].Template_Submission__r.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat));
                            oCaseArt.ProcessType = 'TRR';
                            oCaseArt.CreatedBy = lstTempSubData[0].Template_Submission__r.CreatedBy.Name;
                            oCaseArt.ProcessNumber = lstTempSubData[0].Template_Submission__r.Name;
                            string processedDate = '';
                            string processedTime = '';
                            for(Template_Submission_Data__c tempData : lstTempSubData){
                                if(tempData.Name__c.equals(CaseDetailConstants_HUM.TRR_FIELD_REPLYCD)){
                                    oCaseArt.ReplyCode = tempData.Value__c;
                                }else if(tempData.Name__c.equals(CaseDetailConstants_HUM.TRR_FIELD_EFFDATE)){
                                    if(string.isNotBlank(tempData.Value__c) && tempData.Value__c.length() == 10){
                                        oCaseArt.EffectiveDate = tempData.Value__c.substring(5,7) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(8,10) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(0,4);
                                    }else {
                                        oCaseArt.EffectiveDate = tempData.Value__c;
                                    }
                                }else if(tempData.Name__c.equals(CaseDetailConstants_HUM.TRR_FIELD_PROCDATSTMP)){
                                    if(string.isNotBlank(tempData.Value__c) && tempData.Value__c.length() == 10){
                                        oCaseArt.ProcessedDate = tempData.Value__c.substring(5,7) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(8,10) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(0,4);
                                    }else {
                                        oCaseArt.ProcessedDate = tempData.Value__c;
                                    }
                                }
                            }
                            lstCaseArt.add(oCaseArt);
                        }
                    }
                }
            }
        }
        return lstCaseArt;
    }

    /*
    * Method Name : getTRRDataForProcessNumber
    * Description : this method is used to get process details using processNumber
    * Return type : Map
    * Parameter   : processNumber
    */

    @auraEnabled
    Public Static Map<string, string> getTRRDataForProcessNumber(String processNumber){

        final list<string> TRR_TEMPLATE_FIELDS = new List<string>{'Processing Date Stamp', 'Processing Time Stamp', 'Medicare Claim Number', 'Date of Birth', 'First Name', 'Last Name',
            'UI Initiated Change Flag', 'UI User Organization Destination','Variable', 'Reply Code', 'Effective Date', 'End Date',
            'Disenrollment Reason Code', 'Contract', 'PBP', 'Segment ID', 'Trans Code', 'Election Type', 'SEP Reason Code','Source ID', 'Enroll Source',
            'Cumulative Number of Uncovered Months', 'Submitted Number of Uncovered Months', 'Creditable Coverage Flag', 'Preferred Language', 'Accessible Format','Letter Track ID', 'Enr Accept Track ID' , 'Reply Type Track ID'};
        Map<string, string> MapTRRTemplateData = new Map<string, string>();

        List<Template_Submission_Data__c> lstTemplateData = [SELECT Id, Name, Template_Field__c, Template_Submission__c,  Name__c, Value__c,Template_Field__r.Data_Type__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Name = :processNumber AND Template_Submission__r.Template__r.Unique_Template_Name__c = 'TRR_Template'];
	    List<Template_Submission_Data__c> finalLstTemplateData = new List<Template_Submission_Data__c>();

            if(lstTemplateData != null && !lstTemplateData.isEmpty()){
                for(Template_Submission_Data__c tempData : lstTemplateData){
    		        if(tempData.Template_Field__r.Data_Type__c.equals('DateTime')){
    			        string dateTimeValue = tempData.Value__c;
    			        if(string.isNotBlank(tempData.Value__c) && (tempData.Value__c.length() > 15)){
    				        dateTimeValue = tempData.Value__c.substring(5,7) + '/' + tempData.Value__c.substring(8,10) + '/' + tempData.Value__c.substring(0,4) + ' ' + tempData.Value__c.substring(11,13) + ':' + tempData.Value__c.substring(14,16) + ':' + tempData.Value__c.substring(17,19);
    			        }else if(string.isNotBlank(tempData.Value__c)){
    				        dateTimeValue = tempData.Value__c.substring(0,2) + ':' + tempData.Value__c.substring(3,5) + ':' + tempData.Value__c.substring(6,tempData.Value__c.length());
    			        }
    			        MapTRRTemplateData.put(tempData.Name__c, dateTimeValue);
    		        }else if(tempData.Template_Field__r.Data_Type__c.equals('Date')){
    			        string dateValue = tempData.Value__c;
    			        if(string.isNotBlank(tempData.Value__c) && tempData.Value__c.length() == 10){
    				        dateValue = tempData.Value__c.substring(5,7) + '/' + tempData.Value__c.substring(8,10) + '/' + tempData.Value__c.substring(0,4);
    			        }
    			        MapTRRTemplateData.put(tempData.Name__c, dateValue);
    		        }else{
                        MapTRRTemplateData.put(tempData.Name__c, tempData.Value__c);
                    }
    	        }                
            }
			list<Template_Field__c> lstTemplateFields = [SELECT Name FROM Template_Field__c where Template__r.Unique_Template_Name__c = 'TRR_Template'];
		        if(lstTemplateFields != null && !lstTemplateFields.isEmpty()){
			        for(Template_Field__c tempField : lstTemplateFields){
				        if((!MapTRRTemplateData.containsKey(tempField.Name)) ||
				        (MapTRRTemplateData.containsKey(tempField.Name) && string.isBlank(MapTRRTemplateData.get(tempField.Name)))){
					        MapTRRTemplateData.put(tempField.Name, '');
				        }
			        }
		        }
        return MapTRRTemplateData;
    }
     /*
    * Method Name : isNewbornProcessAttached
    * Description : this method Is used To check newborn process attached or Not
    * Return type : Boolean
    * Parameter   : caseId ,template Name
    */
    @AuraEnabled
    public static String isNewbornProcessAttached(String Id){
        String hasNewbornProcess = null;
        List<Template_Submission__c> lstTemplatesData = [SELECT Id, Template__r.Unique_Template_Name__c,(SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__r) FROM Template_Submission__c where Submission_Owner__r.Object_Owner_ID__c = :Id and Template__r.Unique_Template_Name__c='Medicaid_Newborn_Notification'];
        if(lstTemplatesData != null && !lstTemplatesData.isEmpty()){
            List<Template_Submission_data__c> newbornDataFields = [SELECT Name__c, Value__c FROM Template_Submission_Data__c where Template_Submission__c=: lstTemplatesData[0].Id];
            for(Template_Submission_data__c newbornDataField : newbornDataFields){
                if(newbornDataField.Value__c !='' && newbornDataField.Value__c !=null){
                    hasNewbornProcess = lstTemplatesData[0].Id;
                    break;
                }
            }
        }
        return hasNewbornProcess;
    }

    /*
    * Method Name : getNewbornTemplateDetails
    * Description : this method Is used To check newborn process attached or Not
    * Return type : TemplateSetting__mdt
    * Parameter   : template Name
    */
    @AuraEnabled
    public static TemplateSetting__mdt getNewbornTemplateDetails(){
        return [SELECT Id, MasterLabel, Template_Name__c,Template_Label__c, IsVisible__c, Description__c,Params__c,Template_Type__c, Order__c FROM TemplateSetting__mdt WHERE IsVisible__c = true and Template_Name__c='Medicaid_Newborn_Notification' Limit 1];
      }
}