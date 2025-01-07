/********************************************************************************************************************************************************
Apex Class Name  : CRMRetail_SearchResults_DTO_HUM
Version          : 1.0 
Created Date     : MAR 18 2022
Function         : Wrapper class to hold the storefront visitor's details
Modification Log :       
* Developer                         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------
* Vinoth L                      	3/18/2022            User Story 2792978 -T1PRJ0154546 / SF / MF9 Storefront - Search/Add Visitors: Inactive Accounts
* Vivek Sharma                     	4/06/2022            User Story 3248244: T1PRJ0154546 / SF / MF9 Storefront - Display Visitor ID
* Vinoth L                      	10/11/2022           User Story 3759560: T1PRJ0154546 / SF / MF4 Storefront: Add Account Information on Tasks
* Vinoth L                      	10/18/2022           User Story 5225414: T1PRJ0154546 / SF / MF9 Storefront - Show Future Policy Members in Search Results
*******************************************************************************************************************************************************/
public class CRMRetail_SearchResults_DTO_HUM {
	@AuraEnabled public Boolean inactiveCheck = false;    
    @AuraEnabled public String Id;
    @AuraEnabled public String FirstName;
    @AuraEnabled public String LastName;
    @AuraEnabled public String Name;
    @AuraEnabled public String PersonHomePhone;
    @AuraEnabled public String PersonMailingState;
    @AuraEnabled public String PersonMailingPostalCode;
    @AuraEnabled public String Birthdate;
    @AuraEnabled public Date PersonBirthdate;
    @AuraEnabled public String RecordTypeId;
    @AuraEnabled public Date Waiver_Date;
    @AuraEnabled public String CRMRetailNotificationOptOut;
    @AuraEnabled public String Veteran_Status;
    @AuraEnabled public String RecordTypeName;
    @AuraEnabled public Boolean hasVisitorRecord;
    @AuraEnabled public String inactiveMemberVisitorAccount; 
    @AuraEnabled public String inactiveMember;
    @AuraEnabled public String Gender;
    @AuraEnabled public DateTime recentInteractionDate; 
    @AuraEnabled public String visitorId;
    @AuraEnabled public String deceasedDate = '';
    @AuraEnabled public String membershipStatus = '';
    @AuraEnabled public Object PersonMailingAddress;
    @AuraEnabled public String PersonEmail;
    @AuraEnabled public String errorLog;
    @AuraEnabled public List<Event> eventlist;
    @AuraEnabled public Boolean isFutureMember;
}