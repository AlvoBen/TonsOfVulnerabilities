/*****************************************************************************************
Apex class Name    : MemberIcons_LD_HUM
Version            : 1.0
Function           : Data layer for MemberIcons_LC_HUM
Created Date       : 03/12/2021
Test Class         : MemberIcons_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ankima Srivastava										 03/12/2021				  initial version
*Ashish Kumar                                            04/23/2021               Added changes related to Icons on Policy Detail page 
*Kajal Namdev                                            05/13/2021               Removed reference of policy object
*Ankima Srivastava										 08/11/2021				  Legacy Delete Story Icon Update
*Kajal Namdev                                        	 08/18/2021          	  Removed limit from single record query
*Kajal Namdev                                            08/20/2021          	   Added limitOne in query
*Kajal Namdev                                            09/29/2021          	   Added limitOne in query
*Hima Bindu Ramayanam									 07/16/2023				  User Story 4653961: T1PRJ0865978 - MF26459 - DF7647/Consumer/Interactions Today and Int.in Last 14 Days fields on the Person Account page are reflecting all interactions created, not just the interactions that display on the Person Account page
****************************************************************************************************************************/

public with sharing class MemberIcons_LD_HUM
{
      Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
      public static final Integer iLimitOne = GLOBAL_CONSTANT_LH_HUM.iLimitONe;
     /*
     * This method is used to fetch the Birthdate and Deceased Date of the Account
     * @param String - Record Id 
     * return Account
	*/
     public static Account getBirthDescDate(string sRecordId){
        Account accRec;
        List<MemberPlan> memberPlanRec = new List<MemberPlan>();
        memberPlanRec = [Select id from MemberPlan where ETL_Record_Deleted__c = false AND MemberId=:sRecordId];
        if(memberPlanRec.size()>0){
             for(Account acc : [Select Id, Birthdate__c, Deceased_Date__c FROM Account where id=:sRecordId LIMIT: iLimitOne]){
                 accRec = acc;
             }
        }else{
             for(Account acc : [Select Id, Birthdate__c, Deceased_Date__c FROM Account where id=:sRecordId and ETL_Record_Deleted__c=false LIMIT: iLimitOne]){
                 accRec = acc;
             }
        }
        return accRec;
        
    }
    /*
    * This method is used to retrieve member plan based on account
    * @param  String - Account Id
    * @return Boolean 
    */
    public static List<MemberPlan> getPolicyMemberRecords(String sAccountId)
    {
        return [SELECT id, Product__c, Name, Product_Type_Code__c, Member_Coverage_Status__c , Product_Type__c, Plan.Product__r.Product_Type__c,
        Dual_Status_Indicator__c, Plan.Contract_Number__c, Plan.Issue_State__c, Policy_Platform__c,
        Plan.Medicare_Segment_ID__c, Plan.PBP_Code__c, EffectiveFrom, EffectiveTo 
        FROM MemberPlan 
        where MemberId=:sAccountId  AND Member_Coverage_Status__c  != null AND ETL_Record_Deleted__c = false LIMIT: limitValue ];

    }
        /*
    * This method is used to retrieve member plan based on account
    * @param  String - MemberPlan Id
    * @return Boolean 
    */
    public static List<MemberPlan> getMemberPlanRecord(String sMemberPlanId)
    {
        List<string> coverageStatus = GLOBAL_CONSTANT_LH_HUM.coverageStatus.Split(',');
        return [SELECT id, Product__c, Product_Type_Code__c, Member_Coverage_Status__c , Product_Type__c, Plan.Product__r.Product_Type__c,
        Dual_Status_Indicator__c, Plan.Contract_Number__c, Plan.Issue_State__c,Policy_Platform__c,Name,Issue_State__c,
        Plan.Medicare_Segment_ID__c, Plan.PBP_Code__c, EffectiveFrom, EffectiveTo 
        FROM MemberPlan where id=:sMemberPlanId  AND Member_Coverage_Status__c  IN:coverageStatus AND ETL_Record_Deleted__c = false LIMIT: iLimitOne];
    }
    
   /*
    * This method is used to retrieve policy member based on Id
    * @param  String - Account Id
    * @return Boolean 
    */
    public static List<PermissionSetAssignment> getUserPermissionSets(List<String> lstPermissionSetNames)
    {
        String sUserId = UserInfo.getUserId();
        return [SELECT Id, PermissionSetId, PermissionSet.Name, AssigneeId FROM PermissionSetAssignment Where AssigneeId =:sUserId AND PermissionSet.Name IN : lstPermissionSetNames LIMIT: limitValue ];                                                       
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
        List<Account> lstAccounts = new List<Account>();
        List<MemberPlan> memberPlanRec = new List<MemberPlan>();
        memberPlanRec = [Select id from MemberPlan where ETL_Record_Deleted__c = false AND MemberId=:sAccountId];
        if(memberPlanRec.size()>0){ 
        	lstAccounts = [SELECT Id,(Select Id,CreatedDate FROM Interaction_Members__r Where Interaction__r.id <> : sInteractionId AND Interaction__r.Interacting_With_type__c IN :lstInteractionWithType AND CreatedDate>=: dt14DaysDateBackDate limit 999 ),(Select Id from Cases Where Origin IN : lstInteractionOrigin AND Status <> 'Closed' AND Status <> 'Cancelled' limit 999) from account where id=:sAccountId LIMIT: iLimitOne]; 
         }
        return lstAccounts;
    }
         /*
    * This method is used check if the Member Plan is not Legacy Delete and retrieve list of Accounts member based on different parameters
    * @param  String - Account Id
              String InteractionId
              List<String? Interaction with type
              List<String> Interaction Origin
              DateTime  14 days back date
    * @return List<Account> 
    */
     public Static List<Account> InteractionPlanMember(String memberId,String sInteractionId,List<String> lstInteractionWithType,List<String> lstInteractionOrigin,DateTime dt14DaysDateBackDate,String sAccountId)
    {
        List<Account> lstAccounts = new List<Account>();
        List<MemberPlan> memberPlanRec = new List<MemberPlan>();
        memberPlanRec = [Select id from MemberPlan where ETL_Record_Deleted__c = false AND Id=:memberId LIMIT: iLimitOne];
        if(memberPlanRec.size()>0){ 
           
        	lstAccounts = [SELECT Id,(Select Id,CreatedDate FROM Interaction_Members__r Where Interaction__r.id <> : sInteractionId AND CreatedDate>=: dt14DaysDateBackDate limit 999 ),(Select Id from Cases Where Origin IN : lstInteractionOrigin AND Status <> 'Closed' AND Status <> 'Cancelled' limit 999) from account where id=:sAccountId LIMIT: iLimitOne]; 
        }
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
        List<Account> lstAccounts = new List<Account>();
        List<MemberPlan> memberPlanRec = new List<MemberPlan>();
        memberPlanRec = [Select id from MemberPlan where ETL_Record_Deleted__c = false AND MemberId=:sAccountId];
        if(memberPlanRec.size()>0){
        	 lstAccounts = [SELECT Id,(Select Id,CreatedDate FROM Interaction_Members__r Where Interaction__r.id <> : sInteractionId AND Interaction__r.Interacting_With_type__c IN :lstInteractionWithType AND Interaction__r.Interaction_Origin__c IN : lstInteractionOrigin AND CreatedDate>=: dt14DaysDateBackDate limit 999 ) from account where id=:sAccountId LIMIT: iLimitOne]; 
        }else{
        	 lstAccounts = [SELECT Id,(Select Id,CreatedDate FROM Interaction_Members__r Where Interaction__r.id <> : sInteractionId AND Interaction__r.Interacting_With_type__c IN :lstInteractionWithType AND Interaction__r.Interaction_Origin__c IN : lstInteractionOrigin AND CreatedDate>=: dt14DaysDateBackDate limit 999 ) from account where id=:sAccountId and ETL_Record_Deleted__c=false LIMIT: iLimitOne];    
        }
            return lstAccounts;
    }
	public static List<Case> getCaseforIDCardIcon(String sPolMemId)
    { 
		         return [SELECT Id,CaseNumber from Case  where Member_Plan_Id__r.id =:sPolMemId 
                 AND CTCI_List__r.Classification__r.Name IN :Constants_C_AHT_HUM.Classification_IDCard
                 AND CTCI_List__r.Intent__r.Name  =: Constants_C_AHT_HUM.ICACR_INTENT
                 AND Member_Plan_Id__r.ETL_Record_Deleted__c = false
                 AND Member_Plan_Id__r.Member_Coverage_Status__c IN ('Active','Future')
                 AND CreatedDate = LAST_N_DAYS:7 limit 1] ; 
    }

}