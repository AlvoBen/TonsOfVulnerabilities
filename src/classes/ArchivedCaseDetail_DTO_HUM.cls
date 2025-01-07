/*******************************************************************************************************************************
Apex Class Name : ArchivedCaseDetail_DTO_HUM
Version         : 1.0
Created On      : 07/18/2022   
Test Class      : ArchivedCaseDetail_LT_HUM

Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ashish Kumar/Kajal Namdev		                   07/18/2022                  Original version
**********************************************************************************************************************************************************/

public with sharing class ArchivedCaseDetail_DTO_HUM {
    
    @AuraEnabled public CaseCommentsResponse CaseCommentsResponse;
    @AuraEnabled public CaseHistoryResponse CaseHistoryResponse;
    @AuraEnabled public CaseDetailResponse CaseDetailResponse;
    @AuraEnabled public CaseTasksResponse CaseTasksResponse;
    @AuraEnabled public Boolean sError=false;
    @AuraEnabled public String sErrorMsg='';
    
    public class Header {
        @AuraEnabled public String sTotalRows;
    }
    
    public class CaseCommentsResponse {
        @AuraEnabled public Header Header;
        @AuraEnabled public List<CaseCommentsResponseData> CaseCommentsResponseData;
    }
    
    public class CaseDetailResponse {
        @AuraEnabled public String sId;
        @AuraEnabled public String sCaseNumber;
        @AuraEnabled public String sOrigin;
        @AuraEnabled public String sSubtype;
        @AuraEnabled public String sType;
        @AuraEnabled public String sOwner_Queue;
        @AuraEnabled public String sCase_Owner;
        @AuraEnabled public String sStatus;
        @AuraEnabled public String sRecordTypeId;
        @AuraEnabled public String sCreatedDate;
        @AuraEnabled public String sInteracting_With_Type;
        @AuraEnabled public String sInteracting_About_Type;
        @AuraEnabled public String sPolicy_Member;
        @AuraEnabled public String sAccountId;
        @AuraEnabled public String sInteracting_With;
        @AuraEnabled public String sClassification_Id;
        @AuraEnabled public String sMember_Plan_Id;
        @AuraEnabled public String sIntent_Id;
        @AuraEnabled public String sPriority;
        @AuraEnabled public String sDCN;
        @AuraEnabled public String sDoc_Type;
        @AuraEnabled public String sClassification_Type;
        @AuraEnabled public String sTopic;
        //
        @AuraEnabled public String sCase_Age;
        @AuraEnabled public String sInteracting_With_Name;
        
        @AuraEnabled public String sG_A_Reason;
        @AuraEnabled public String sG_A_Rights_Given;
        @AuraEnabled public String sComplaint;
        @AuraEnabled public String sComplaint_Reason;
        @AuraEnabled public String sComplaint_Type;
        @AuraEnabled public String sLanguage_Preference;
        
        @AuraEnabled public String sOpenEnrollment ;
        @AuraEnabled public String sOpenEnrollmentType;
        @AuraEnabled public String sAccount_MedicareID;
        @AuraEnabled public String sFollow_up_Due_Date;
        @AuraEnabled public String sCase_Referred_To;
        @AuraEnabled public String sResponse_Status;
        @AuraEnabled public String sRe_Open_Case_Age;
        @AuraEnabled public String sRe_Open_Case_Date;
        @AuraEnabled public String sCreatedById;
        //added by kajal
        @AuraEnabled public String sCreatedByName;
        @AuraEnabled public String sLastModifiedByName;
        
        @AuraEnabled public String sCreated_By_Queue;
        @AuraEnabled public String sLastModifiedby_Queue;
        @AuraEnabled public String sWork_Queue_View_Name;
        @AuraEnabled public String sDCN_Type;
        @AuraEnabled public String sDCN_URL;
        @AuraEnabled public String sOral_Grievance_Category;
        @AuraEnabled public String sOral_Grievance_Sub_Category;
        
        //Below are the new fields added by ashish- Need to be added on UI
        @AuraEnabled public String sOGO_Resolution_Date;
        @AuraEnabled public String sOGO_Resolution_Type;
        
        @AuraEnabled public String sCall_Benefit_Category;
        @AuraEnabled public String sLastModifiedById;
        @AuraEnabled public String sSuppliedEmail;
        @AuraEnabled public String sSubject;
        @AuraEnabled public String sDays_Since_Last_Modified;
        @AuraEnabled public String sDescription;
        @AuraEnabled public String sGroup_Number_for_Policy;
        @AuraEnabled public String sTax_ID;
        @AuraEnabled public String sNPI_ID;
        @AuraEnabled public String sComments;
        @AuraEnabled public String sClosedDate;
        @AuraEnabled public String sContactId;
        @AuraEnabled public String sContactName;
        
        //Variables for internal user lightning
        @AuraEnabled public String sAccountName;
        @AuraEnabled public String sMemberPlanName;
        @AuraEnabled public String sInteractingWithName;
        @AuraEnabled public String sClassificationName;
        @AuraEnabled public String sIntentName;
        @AuraEnabled public String sRecordTypeName;
        
        //Variables for internal user classic
        @AuraEnabled public String sPolicyMemberName;
        @AuraEnabled public String sDCN_Link;
        
        
    }
    
    public class CaseCommentsResponseData {
        @AuraEnabled public String sId;
        @AuraEnabled public String sParentId;
        @AuraEnabled public String sCreatedDate;
        @AuraEnabled public String sCommentBody;
        @AuraEnabled public String sCreatedById;
        @AuraEnabled public String sCreatedByName;
        @AuraEnabled public String sLastModifiedById;
        @AuraEnabled public String sLastModifiedDate;
        @AuraEnabled public String sLastModifiedByName;
    }
    
    public class CaseTasksResponseData {
        @AuraEnabled public String sId;
        @AuraEnabled public String sTask_Number;
        @AuraEnabled public String sActivityDate;
        @AuraEnabled public String sType;
        @AuraEnabled public String sStatus;
        @AuraEnabled public String sPriority;
        @AuraEnabled public String sTask_Age;
        @AuraEnabled public String sCreated_DateTime;
        @AuraEnabled public String sDate_Time_Closed;
        @AuraEnabled public String sTask_Owner;
        @AuraEnabled public String sOwner_Queue;
        @AuraEnabled public String sRecordTypeId;
    }
    
    public class CaseTasksResponse {
        @AuraEnabled public Header Header;
        @AuraEnabled public List<CaseTasksResponseData> CaseTasksResponseData;
    }
    
    public class CaseHistoryResponse {
        @AuraEnabled public Header Header;
        @AuraEnabled public List<CaseHistoryResponseData> CaseHistoryResponseData;
        @AuraEnabled public Map<Id, String> mapOfCreatedByIdName;
    }
    
    public class CaseHistoryResponseData {
        @AuraEnabled public String sId;
        @AuraEnabled public String sCaseId;
        @AuraEnabled public String sCreatedById;
        @AuraEnabled public String sCreatedDate;
        @AuraEnabled public String sField;
        @AuraEnabled public String sNewValue;
        @AuraEnabled public String sOldValue;
        @AuraEnabled public String sCreatedByName;         
    }
    
}