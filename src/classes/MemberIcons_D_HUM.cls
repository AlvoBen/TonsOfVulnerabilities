/*****************************************************************************************
Apex class Name    : MemberIcons_D_HUM
Version            : 1.0
Function           : Data layer for MemberIcons_C_HUM
Created Date       : 08/28/2019
Test Class         : MemberIcons_CT_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                      08/28/2019         Original Version
 *   Santhi Mandava                                      09/17/2019         Removed unused parameter in "getPolicyMemberDeatails" method
 *   Amar Gadewar                                        01/28/2020         USER STORY 729689 PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
 *   Amit Agarwal                                        06/25/2020         User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *   Amar Gadewar                                        07/15/2020         DF-1086: Repeat Caller Icon is not shown on Member Search Page & Member Account page 
 *   Amit Agarwal                                        07/15/2020         DF-1077 Bug 1294710: Count of open cases is not displayed on Open Cases (count of open cases) attribute when the count is in 4 digits
 *   Amit Agarwal                                        08/19/2020         User Story 1368823: PR00091574 - MF 1 - Iconology - H1036 Icon logic update (CRM)
 *   Atia Uzma                                           09/11/2020         User Story 1449938: PR00091574 - MF 1 - Iconology - HIDE Icon (CRM) 
 *   Raja Sekhar Reddy M V                               11/13/2020         User Story 1660597: PR00096346 - MF 1 - DEV - New Member Indicator
 *   Samantha Tennyson                                   12/08/2021         Removing unused existing logic for Dual Eligible Icon
 *   Y Sunil Kumar Reddy                                 02/12/2021         User Story 1874757: Checkmarx Scan Fix 
 *   Atia Uzma                                           09/21/2021         User Story 2696506: T1PRJ0001827 - MF 1 - Iconology - ID Card Request Icon (RF)
 *   Kiran Kumar B		 							 	 07/05/2022			User Story 3253058: T1PRJ0325139 - MF 1 - Iconology - IL MMP Icon (CRM)
 ****************************************************************************************************************************/

Public with sharing Class MemberIcons_D_HUM
{
    /*
    * This method is used to retrieve policy members based on account
    * @param  String - Account Id
    * @return Boolean 
    */
    public static List<Policy_Member__c> getPolicyMemberRecords(String sAccountId)
    {
       return [Select id, Name, Product__c, Policy_Platform__c, Product_Type_Code__c, Effective_Date__c, End_Date__c, Status__c, Product_Type__c, Policy__r.Product__r.Product_Type__c, Policy__r.Contract_Number__c, Policy__r.Issue_State__c, Policy__r.Medicare_Segment_ID__c, Policy__r.PBP_Code__c FROM Policy_Member__c 
                                                        WHERE Member__c = :sAccountId  AND Status__c != null AND ETL_Record_Deleted__c = false ORDER BY Status__c];                                                                 
    }
    
    /*
    * This method is used to retrieve policy member based on Id
    * @param  String - Account Id
    * @return Boolean 
    */
    public static List<Policy_Member__c> getPolicyMemberDetails(String sPolMemId)
    {
        return [Select id, Name, Status__c, Product__c, Policy_Platform__c, Product_Type__c, Policy__r.Product__r.Product_Type__c, Product_Type_Code__c, Effective_Date__c, End_Date__c, Policy__r.Contract_Number__c, Policy__r.Issue_State__c, Policy__r.Medicare_Segment_ID__c, Policy__r.PBP_Code__c FROM Policy_Member__c WHERE Id = :sPolMemId AND Status__c != null AND ETL_Record_Deleted__c = false];                                                                 
    }
    
    /*
    * This method is used to retrieve Termed policy member based on Id
    * @param  String - Account Id
    * @return Boolean 
    */
    
    public static List<Policy_Member__c> getTermedPolicyMemberDetails(String sPolMemId)
    {	
         return [Select id,Product__c,Policy_Platform__c,Product_Type__c,Product_Type_Code__c,Effective_Date__c,End_Date__c,Policy__r.Contract_Number__c,Policy__r.Issue_State__c,Policy__r.Medicare_Segment_ID__c,Policy__r.PBP_Code__c, Status__c FROM Policy_Member__c WHERE Id = :sPolMemId AND Status__c = 'Termed'];  
    }
    
    /*
    * This method is used to retrieve policy member based on Id
    * @param  String - Account Id
    * @return Boolean 
    */
    public static List<PermissionSetAssignment> getUserPermissionSets(List<String> lstPermissionSetNames)
    {
        String sUserId = UserInfo.getUserId();
        return [SELECT Id, PermissionSetId, PermissionSet.Name, AssigneeId FROM PermissionSetAssignment Where AssigneeId =:sUserId AND PermissionSet.Name IN : lstPermissionSetNames ];                                                       
    }
    
    public Static List<Document> getDocuments()
    {
        List<Folder> folderId = [SELECT Id FROM Folder WHERE Name = 'MemberIcons'];
        List<Document> lstDocs = [SELECT Id, Name FROM Document WHERE FolderId =:folderId[0].Id];
        
        return lstDocs;
    }
    /*
    * This method is used to retrieve list of Accounts member based on different parameters
    * @param  String - Account Id
              String InteractionId
              List<String? Interaction with type
              List<String> Interaction Origin
              DateTime  14 days back date
    * @return List<Account> 
    */
    public Static List<Account> getAccounts(String sInteractionId,List<String> lstInteractionWithType,List<String> lstInteractionOrigin,DateTime dt14DaysDateBackDate,String sAccountId)
    {
       List<Account> lstAccounts = [SELECT Id,(Select Id,CreatedDate FROM Interaction_Members__r Where Interaction__r.id <> : sInteractionId AND Interaction__r.Interacting_With_type__c IN :lstInteractionWithType AND Interaction__r.Interaction_Origin__c IN : lstInteractionOrigin AND CreatedDate>=: dt14DaysDateBackDate limit 999 ),(Select Id from Cases Where Origin IN : lstInteractionOrigin AND Status <> 'Closed' AND Status <> 'Cancelled' limit 999) from account where id=:sAccountId]; 
        return lstAccounts;
    }
    /*
    * This method is used to retrieve list of Accounts member based on different parameters
    * @param  String - Account Id
              String InteractionId
              List<String? Interaction with type
              List<String> Interaction Origin
              DateTime  14 days back date
    * @return List<Account> 
    */
    public Static List<Account> getAccountsforRCIcon(String sInteractionId,List<String> lstInteractionWithType,List<String> lstInteractionOrigin,DateTime dt14DaysDateBackDate,String sAccountId)
    {
        List<Account> lstAccounts = [SELECT Id,(Select Id,CreatedDate FROM Interaction_Members__r Where Interaction__r.id <> : sInteractionId AND Interaction__r.Interacting_With_type__c IN :lstInteractionWithType AND Interaction__r.Interaction_Origin__c IN : lstInteractionOrigin AND CreatedDate>=: dt14DaysDateBackDate limit 999 ) from account where id=:sAccountId]; 
        return lstAccounts;
    }
    
    /*
	* This method is used to retrieve list of Case
 	* @param String - Policy Member Id
	* @return List of Case
	*/
    public static List<Case> getCaseforIDCardIcon(String sPolMemId)
    {
        return [SELECT Id,CaseNumber from Case 
                where Policy_Member__c =:sPolMemId 
                AND CTCI_List__r.Classification__r.Name IN :Constants_C_AHT_HUM.Classification_IDCard 
                AND CTCI_List__r.Intent__r.Name  =: Constants_C_AHT_HUM.ICACR_INTENT 
                AND Policy_Member__r.ETL_Record_Deleted__c = false
                AND Policy_Member__r.Status__c IN ('Active','Future')
                AND CreatedDate = LAST_N_DAYS:7  limit 1] ;   
    }

}