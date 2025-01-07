/*******************************************************************************************************************************
Apex Class Name : CaseProcessEvaluation_LC_Hum
Version         : 1.0
Created On      : 2/13/2023
Function        : This class is used for displaying OI Process Template
Test Class      : CaseProcessEvaluation_LT_Hum

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Nirmal Garg                                          02/13/2023                 Original Version
***********************************************************************************************************************/

public with sharing class CaseProcessEvaluation_LC_Hum {
    
    @AuraEnabled(cacheable=true)
    public static List<CaseDetails_DTO_HUM> getCaseDetails(string caseId){
        try {
            List<CaseDetails_DTO_HUM> lstCaseDetails = new List<CaseDetails_DTO_HUM>();
            List<Case> lstCases = [Select status,AccountId, Interacting_About_Type__c, Interacting_With_Type__c,
            RecordType.developerName, Product__c,
            Policy_Member__r.Policy__r.Product__r.ProductCode, 
            Policy_Member__r.Product__c, Policy_Member__r.Policy__r.Product__r.Major_LOB__c,
            Policy_Member__r.Policy__r.Product__r.Product_Type__c, product_type__c, 
			Policy_Member__c, OwnerID, Account.RecordType.Name,Classification_Type__c,
			Owner_Queue__c,Created_By_Queue__c, CTCI_List__c
             ,CTCI_List__r.Classification__r.Name, Member_Plan_Id__c,
			 Member_Plan_Id__r.Product__c,
			 Member_Plan_Id__r.Plan.Product__r.Product_Type__c,
			 Member_Plan_Id__r.Plan.Product__r.Major_LOB__c,
             Member_Plan_Id__r.Plan.Product__r.ProductCode,
             Member_Plan_Id__r.Product_Type__c,
             CTCI_List__r.Intent__r.Name  From Case Where Id = :caseId];
            if(lstCases.size() > 0 && !lstCases.isEmpty()){
                for(Case objcase : lstCases){
                    CaseDetails_DTO_HUM objCaseDetail = new CaseDetails_DTO_HUM();
                	objCaseDetail.Status = objcase.status;
                    objCaseDetail.AccountName = objcase.AccountId;
                    objCaseDetail.InteractingAbout = objcase.Interacting_About_Type__c;
                    objCaseDetail.InteractingWith = objcase.Interacting_With_Type__c;
                    objCaseDetail.CaseRecordType = objcase.RecordType.developerName;
                    objCaseDetail.Product = objcase.Product__c;
                    objCaseDetail.PolicyMemberProductCode = objcase.Policy_Member__r.Policy__r.Product__r.ProductCode;
                    objCaseDetail.PolicyMemberProduct = objcase.Policy_Member__r.Product__c;
                    objCaseDetail.PolicyMemberMajorLOB = objcase.Policy_Member__r.Policy__r.Product__r.Major_LOB__c;
                    objCaseDetail.PolicyMemberProductType = objcase.Policy_Member__r.Policy__r.Product__r.Product_Type__c;
                    objCaseDetail.ProductType = objcase.product_type__c;
                    objCaseDetail.PolicyMemberId = objcase.Policy_Member__c;
                    objCaseDetail.OwnerId = objcase.OwnerID;
                    objCaseDetail.AccountRecordType = objcase.Account.RecordType.Name;
                    objCaseDetail.OwnerQueue = objcase.Owner_Queue__c;
                    objCaseDetail.ClassificationType = objcase.Classification_Type__c;
                    objCaseDetail.CreatedByQueue = objcase.Created_By_Queue__c;
                    objCaseDetail.CTCI = objcase.CTCI_List__c;
                    objCaseDetail.Classification = objcase.CTCI_List__r.Classification__r.Name;
                    objCaseDetail.MemberPlanId = objcase.Member_Plan_Id__c;
                    objCaseDetail.MemberPlanProduct = objcase.Member_Plan_Id__r.Product__c;
                    objCaseDetail.MemberPlanProductType = objcase.Member_Plan_Id__r.Plan.Product__r.Product_Type__c;
                    objCaseDetail.MemberPlanMajorLOB = objcase.Member_Plan_Id__r.Plan.Product__r.Major_LOB__c;
                    objCaseDetail.MemberPlanProductCode = objcase.Member_Plan_Id__r.Product_Type__c;
                    objCaseDetail.Intent = objcase.CTCI_List__r.Intent__r.Name;
                    lstCaseDetails.add(objCaseDetail);
                }
                
            }else{
                lstCaseDetails = null;
            }
             return lstCaseDetails;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled
    public static List<CaseProcessWrapper_DTO_HUM> getInitialDetails(string caseId, string memberPlanId){
        try {
            List<CaseProcessWrapper_DTO_HUM> lstCaseProcessWrapper = new List<CaseProcessWrapper_DTO_HUM>();
            CaseProcessWrapper_DTO_HUM objCaseProcessWrapper = new CaseProcessWrapper_DTO_HUM();
            objCaseProcessWrapper.profilename = [SELECT Id, Name FROM Profile WHERE Id=:userinfo.getProfileId()]?.Name;
            objCaseProcessWrapper.lstPermissionSet = getPermissionSets();
            objCaseProcessWrapper.lstCaseDetails = string.isNotBlank(caseId) && caseId.startsWith(Schema.SObjectType.Case.keyprefix)
                ? getCaseDetails(caseId) : null;
            objCaseProcessWrapper.lstTemplateData = getTemplateDetails();
            objCaseProcessWrapper.lstMemberData = getMemberPlanData(memberPlanId);
            lstCaseProcessWrapper.add(objCaseProcessWrapper);
            return lstCaseProcessWrapper;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(cacheable=true)
    public static List<MemberPlan> getMemberPlanData(string memberplanid){
        if(string.isNotBlank(memberplanid) && memberplanid.startsWith(Schema.SObjectType.MemberPlan.keyprefix)
           && memberplanid.length() == 18){
               return [SELECT Id, product__c, product_type__C from memberplan where id=:memberplanid limit 1];
           }else{
               return null;
           }
    }

    public with sharing class CaseProcessWrapper_DTO_HUM{
        @AuraEnabled
        public string profilename = '';
        @AuraEnabled
        public List<CaseDetails_DTO_HUM> lstCaseDetails;
        @AuraEnabled
        public List<PermissionSetAssignment> lstPermissionSet;
        @AuraEnabled
        public List<TemplateSetting__mdt> lstTemplateData;
        @AuraEnabled
        public List<MemberPlan> lstMemberData;
    }
    
    public with sharing class CaseDetails_DTO_HUM
    {
        @AuraEnabled
        public String Status = '';
        @AuraEnabled
        public String AccountName = '';
        @AuraEnabled
        public String InteractingAbout = '';
        @AuraEnabled
        public String InteractingWith  = '';
        @AuraEnabled
        public String CaseRecordType = '';
        @AuraEnabled
        public String Product = '';
        @AuraEnabled
        public String ProductCode = '';
        @AuraEnabled
        public String PolicyMemberProductCode = '';
        @AuraEnabled
        public String PolicyMemberMajorLOB = '';
        @AuraEnabled
        public String PolicyMemberProductType = '';
        @AuraEnabled
        public String PolicyMemberProduct = '';
        @AuraEnabled
        public String PolicyMemberId = '';
        @AuraEnabled
        public String AccountRecordType = '';
        @AuraEnabled
        public String ClassificationType = '';
        @AuraEnabled
        public String OwnerId = '';
        @AuraEnabled
        public String OwnerQueue = '';
        @AuraEnabled
        public String CreatedByQueue = '';
        @AuraEnabled
        public String CTCI = '';
        @AuraEnabled
        public String Classification = '';
        @AuraEnabled
        public String MemberPlanId = '';
        @AuraEnabled
        public String Intent = '';
        @AuraEnabled
        public String MemberPlanProductCode = '';
        @AuraEnabled
        public String MemberPlanMajorLOB = '';
        @AuraEnabled
        public String MemberPlanProductType = '';
        @AuraEnabled
        public String MemberPlanProduct = '';
        @AuraEnabled
        public String ProductType = '';
    }

    @AuraEnabled(cacheable=true)
    public static List<PermissionSetAssignment> getPermissionSets(){
        try {
            List<PermissionSetAssignment> lstPermissionSet = [SELECT Id, PermissionSetId, PermissionSet.Name, PermissionSet.ProfileId, PermissionSet.Profile.Name, AssigneeId, Assignee.Name FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId()];
            return lstPermissionSet;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(cacheable=true)
    public static List<TemplateSetting__mdt> getTemplateDetails(){
        try {
            List<TemplateSetting__mdt> listTemplates = [SELECT Id, MasterLabel, Template_Name__c,Template_Label__c, IsVisible__c, Description__c,Params__c,Template_Type__c, Order__c,TemplateCondition__c FROM TemplateSetting__mdt WHERE IsVisible__c = true  order by Order__c];
        	return listTemplates;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
}