/*******************************************************************************************************************************
Apex Class Name : PolicySearchIdCards_LC_HUM.cls
Version         : 1.0
Created On      : 02/09/2022
Function        : Class contains methods for getting case action processes.
TestClass       : PolicySearchIdCards_LT_HUM

Modification Log:
* Developer Name            Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi				02/09/2022				  Initial Version - 
*****************************************************************************************************************************************/
public class PolicySearchIdCards_LC_HUM{
        Public Static final String MEMBERIDCARDMGT_MEMBERIDBASE_HUM = 'Member-Id-Base';
        @AuraEnabled
        public static List<Policy_Member__c> getPoliciesMed(String recId){
        String sRecId = recId;
        List<Policy_Member__c> lstPolMem = [SELECT Id, Name,Status__c, Member__c, Member__r.Enterprise_Id__c, Subscriber__c, Policy_Platform__c, Product__c, Product_Type__c,Product_Type_Code__c,Segment_Indicator__c,
        Group_Number__c, Member__r.Name, Policy__r.id, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Coverage_Plan_Effective_Date__c,
        Member__r.Home_Office_Account__c, Alternate_Description__c, Policy_Member_External_ID__c,(SELECT Id, Name, Type__c FROM Member_Ids__r WHERE Type__c =:MEMBERIDCARDMGT_MEMBERIDBASE_HUM)
        FROM Policy_Member__c WHERE Status__c != 'Termed' and 
        Policy_Platform__c NOT IN ('CB','CS','FM','PR') and Product_Type__c NOT IN ('OHBD','POSD','OPPD','SUPPD','OCHD','SUPD') AND Product__c NOT IN ('LIF','STI','VIS') AND Policy__r.Group_Name__r.Source_Customer_Key__c !='684668' 
        AND ( Product__c !='DEN' OR (NOT (Alternate_Description__c LIKE '%PED DEN%'))) AND
         Member__c = :sRecId and Product__c = 'MED' order by  Status__c asc]; 
        return lstPolMem;
        } 
        
        @AuraEnabled
        public static List<Policy_Member__c> getPoliciesDen(String recId){
        String sRecId = recId;
        
        List<Policy_Member__c> lstPolMem = [SELECT Id, Name,Status__c, Member__c, Member__r.Enterprise_Id__c, Subscriber__c, Policy_Platform__c, Product__c, Product_Type__c,Product_Type_Code__c,Segment_Indicator__c,
        Group_Number__c, Member__r.Name, Policy__r.id, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Coverage_Plan_Effective_Date__c,
        Member__r.Home_Office_Account__c, Alternate_Description__c, Policy_Member_External_ID__c,(SELECT Id, Name, Type__c FROM Member_Ids__r WHERE Type__c =:MEMBERIDCARDMGT_MEMBERIDBASE_HUM)
        FROM Policy_Member__c WHERE Status__c != 'Termed' and 
        Policy_Platform__c NOT IN ('CB','CS','FM','PR') and Product_Type__c NOT IN ('OHBD','POSD','OPPD','SUPPD','OCHD','SUPD') AND Product__c NOT IN ('LIF','STI','VIS') AND Policy__r.Group_Name__r.Source_Customer_Key__c !='684668' 
        AND ( Product__c !='DEN' OR (NOT (Alternate_Description__c LIKE '%PED DEN%'))) AND
        Member__c = :sRecId and Product__c = 'DEN' order by  Status__c asc]; 
        
        return lstPolMem;


        } 

}