/*****************************************************************************************
Apex class Name    : CaseHistoryComponent_LD_HUM
Version            : 1.0
Function           : Gets Case History Records as Wrapper to LWC caller   
Created Date       : 03/03/2021
Test Class         : CaseHistoryComponent_LT_HUM

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
Ritik Agarwal                                       03/03/2021          Original version
*  Kajal Namdev                                     04/01/2021          Added Limit in all the SOQL 
* Ashish Kumar/Ritik								09/13/2021				  Added login for Date time formatting
* Nirmal Garg											09/24/2021			Added logic for pharmacy case history
*Kajal Namdev                                            09/29/2021          	   Added limitOne in query
* Gowthami Thota									10/05/2021			Changed logic to pull Product value from MemberPlan and 
                                                                                modified to re-use on Member Plan case history
* Ankima                                            02/17/2022                  added method related to case comment timeline
*********************************************************************************************************************************/
public with sharing class CaseHistoryComponent_LD_HUM {
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    Public static String sLimitValue = String.valueOf(system.label.iPaginator);
    public static string OBJECT_ACCOUNT_HUM = 'Account';
    public static string OBJECT_MEMBERPLAN_HUM = 'MemberPlan';
    
    /*
	* Method Name : initializeQuery
	* Description : create the query for Case
	* Return type : String
	* Parameter   : NA
	*/
    public static String initializeQuery(ID objID)
    {
        String sObjName = objID.getSObjectType().getDescribe().getName();      
        String sQueryInit='Select Id,CaseNumber,DCN__c,DCN_URL__c,DCN_Type__c,Origin,Type,Owner_Queue__c, LastModifiedby_Queue__c, Created_By_Queue__c,'
            +'CTCI_List__r.Classification__r.Name,CreatedBy.Name,CTCI_List__r.Intent__r.Name,'+
            +'Product__c,Member_Plan_Id__r.Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
            +'Interacting_About__c,Interacting_About_Type__c,Status,Priority,Follow_up_Due_Date__c,CreatedDate,ClosedDate,HO_Dental_Case__c,HO_Medical_Case__c,'
            +'General_Case__c,LastModifiedBy.Name, Complaint__c from Case';
            if(sObjName == OBJECT_ACCOUNT_HUM){
                sQueryInit = sQueryInit + ' Where AccountId =\''+String.escapeSingleQuotes(objID)+'\'';
            }
            else if(sObjName == OBJECT_MEMBERPLAN_HUM){
                sQueryInit = sQueryInit + ' Where Member_Plan_Id__c =\''+String.escapeSingleQuotes(objID)+'\'';
            }
        sQueryInit = sQueryInit + ' order by CreatedDate DESC LIMIT '+ sLimitValue; 
        return sQueryInit;
    }
	
	/*
     * Method Name : initializePharmacyQuery
     * Description : create the query for Case
     * Return type : String
     * Parameter   : NA
     */
    public static String initializePharmacyQuery(Integer days)
    {      
        String sQueryInit='Select Id,CaseNumber,DCN__c,Origin,Type,Owner_Queue__c, LastModifiedby_Queue__c, Created_By_Queue__c,'
        +'CTCI_List__r.Classification__r.Name,CreatedBy.Name,CTCI_List__r.Intent__r.Name,'+
        +'Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
        +'Interacting_About__c,Status,Priority,Follow_up_Due_Date__c,CreatedDate,ClosedDate,HO_Dental_Case__c,HO_Medical_Case__c,'
        +'General_Case__c,LastModifiedBy.Name, Complaint__c ,(Select Id, CommentBody, LastModifiedDate FROM CaseComments ORDER BY LastModifiedDate DESC LIMIT 1 ) from Case'
		+' Where AccountId =:accountId';
        if(days > 0){
            sQueryInit += ' and createddate >= LAST_N_DAYS:'+days;
        }
        sQueryInit +=' order by CreatedDate DESC LIMIT '+ sLimitValue;
        return sQueryInit;
    }
    
    /*
	* Method Name : getCaseCommentDetails
	* Description : generate the string for Query and fetch the case comment records.
	* Return type : NA
	* Parameter   : Case Record Id
	*/
    
    public static CaseCommentModal getCaseCommentDetails(String caseId){
        Id sCaseId = Id.valueOf(caseId);
        
        CaseComment oCaseComment;
        List<Case> lstCase =  [SELECT id, (Select Id, CommentBody, LastModifiedDate FROM CaseComments ORDER BY LastModifiedDate DESC LIMIT 1 ) 
                               FROM Case WHERE id=:sCaseId LIMIT 1];
        
        if(!lstCase.isEmpty() && lstCase.get(0) != null && lstCase.get(0).CaseComments!=null && !lstCase.get(0).CaseComments.isEmpty())
        {
            oCaseComment = lstCase.get(0).CaseComments.get(0);
            return new CaseCommentModal(oCaseComment.CommentBody, String.valueOf(HealthCloudUtility_LH_HUM.dateTimeFormating(oCaseComment.LastModifiedDate)));
        }
        return null;
    }
    
    
    public with sharing Class CaseCommentModal{
        
        @AuraEnabled public String LastModifiedDate;
        @AuraEnabled public String CommentBody;
        
        public CaseCommentModal(String CommentBody, String LastModifiedDate)
        {
            this.LastModifiedDate=LastModifiedDate;
            this.CommentBody=CommentBody;
        }
    }
    /*
	* Method Name : getCaseDetails
	* Description : generate the list of records for case and related comments.
	* Return type : NA
	* Parameter   : Case Record Id
	*/
    
    public static List<Case> getCaseDetails(String caseId){
        Id sCaseId = Id.valueOf(caseId);
        List<Case> caseObj = [Select id, CTCI_List__r.Classification__r.Name,CreatedBy.Name,CTCI_List__r.Intent__r.Name,
                              (Select Id,CreatedDate, CommentBody,CreatedBy.Name,LastModifiedBy.Name, LastModifiedDate FROM CaseComments ORDER BY CreatedDate DESC ) from Case where id=:sCaseId];
        return caseObj;
    }
}