/*******************************************************************************************************************************
Apex Class Name : PolicyMemberDTO_LC_HUM.cls
Version         : 1.0
Created On      : 11th Febraury 2022
Function        : Wrapper Class for data displayed in Order ID card table for Member.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi Patni                       02/11/2022                  Original Version 
* Anuradha Gajbhe                                      05/09/2022                  User Story 3205329: ID cards adding Product Type Code to the order cart within the left panel of the Ordering ID Card Screen.
* Anuradha Gajbhe                                      07/14/2023                  4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
* Raj Paliwal	                                       07/14/2023                  4272710: Ability to request a State ID Card from the ID Card Managment Page(Lightning)
*****************************************************************************************************************************************/

public with sharing class PolicyMemberDTO_LC_HUM
{
   

    public PolicyMemberDTO_LC_HUM(MemberPlan policyMember) {
        this.sName = policyMember.Member.Name;
        this.sMemid = policyMember.Name;
        this.sMemidWithoutDepCo = this.sMemid.substring(0, this.sMemid.length() - 2);
        this.sSubscriber = policyMember.Subscriberid;
        this.sAddress = getAddress(policyMember);
        this.sBirthDate = policyMember.Member.Birthdate__c;
        this.sRelationship = policyMember.RelationshipToSubscriber;
        this.sPolciyMemberId = policyMember.Id;
        this.sSegmentInd = policyMember.Segment_Indicator__c;
        this.sProductType = policyMember.Product_Type__c;
        this.sSelectedPol = '';
        this.bChecked = false;
        this.bstateIdcardfnd = false;
        this.medicaidid = '';
        //this.bShowRow = false;
    }
        @AuraEnabled
        public String sName;
        @AuraEnabled
        public String sMemid;
        @AuraEnabled
        public String sSubscriber;
        @AuraEnabled
        public String sAddress;
        @AuraEnabled
        public String sBirthDate;
        @AuraEnabled
        public String sRelationship;
        @AuraEnabled
        public String sSelectedPol;
        @AuraEnabled
        public String sPolciyMemberId;
        @AuraEnabled
        public String sSegmentInd;
        @AuraEnabled
        public String sProductType;
        @AuraEnabled
        public Boolean bChecked;
        @AuraEnabled
        public String sMemidWithoutDepCo;
        @AuraEnabled
        public Boolean bstateIdcardfnd;
        @AuraEnabled
        public String medicaidid;
        /*@AuraEnabled
        public Boolean bShowRow;*/

        @AuraEnabled
        public static string getAddress(MemberPlan policyMem){
            String memberAddressStr = '';
            memberAddressStr+=policyMem.Member.PersonMailingStreet != null ? policyMem.Member.PersonMailingStreet+'  ' : '';
            memberAddressStr+=policyMem.Member.PersonMailingCity != null ? policyMem.Member.PersonMailingCity+', ' : '';
            memberAddressStr+=policyMem.Member.PersonMailingStateCode != null ? policyMem.Member.PersonMailingStateCode+'  ' : '';
            memberAddressStr+=policyMem.Member.PersonMailingPostalCode != null ? policyMem.Member.PersonMailingPostalCode : '';
            return memberAddressStr;
        }
}