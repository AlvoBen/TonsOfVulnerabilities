/*******************************************************************************************************************************
Apex Class Name : ArchivedCaseHistoryResponse_DTO_HUM.cls
Version         : 1.0
Created On      : 7/12/2022
Function        : Class contains Response Nodes for Archived Case History.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
*  Ankima/Isha		                                   07/18/2022                  Original version
 ********************************************************************************************************************************/
public with sharing class ArchivedCaseHistoryResponse_DTO_HUM {
    
    public class CaseSearchResponse {
		@AuraEnabled public Header Header;
		@AuraEnabled public List<CaseResults> CaseResults;
        @AuraEnabled public Boolean sError=false;
        @AuraEnabled public String sErrorMsg='';
	}
	
	public class CaseResults {
		@AuraEnabled public String ssfid;
		@AuraEnabled public String sAccountId;
		@AuraEnabled public String sCaseNumber;
		@AuraEnabled public String sType;
		@AuraEnabled public String sOrigin;
		@AuraEnabled public String sPriority;
		@AuraEnabled public String sCreatedDate;
		@AuraEnabled public String sClosedDate;
		@AuraEnabled public String sFollow_up_Due_Date;
		@AuraEnabled public String sStatus;
		@AuraEnabled public String sCTCI_List;
		@AuraEnabled public String sClassification;
		@AuraEnabled public String sIntent;
		@AuraEnabled public String sDCN;
		@AuraEnabled public String sComplaint;
		@AuraEnabled public String sInteracting_With_Type;
		@AuraEnabled public String sInteracting_With;
		@AuraEnabled public String sInteracting_About;
		@AuraEnabled public String sOwner_Queue;
		@AuraEnabled public String sCreated_By_Queue;
		@AuraEnabled public String sLastModifiedby_Queue;
		
	}

	@AuraEnabled public CaseSearchResponse CaseSearchResponse;

	public class Header {
		@AuraEnabled public String sTotalRows;
		@AuraEnabled public String sOldestCaseDate;
	}
}