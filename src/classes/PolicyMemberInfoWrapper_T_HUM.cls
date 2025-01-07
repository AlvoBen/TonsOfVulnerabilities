/**************************************************************************** 
Apex Class Name :  PolicyMemberInfoWrapper_T_HUM
Version: 1.0  
Created Date     : 28th September, 2017
Function         : Test class for PolicyMemberInfoWrapper_DTO_HUM
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shivam Anand             140629             09/28/2017                  Original Version
***************************************************************************************************/
@isTest
Private Class PolicyMemberInfoWrapper_T_HUM
{
  /*
   * Method Name : prepareTestData
   * Description : Used to create wrapper class data
   * Return type : void
   * Paramater   : NA
   */
   @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    }
  /*
   * Method Name : runPositiveData
   * Description : Used to run scenerios of test data
   * Return type : void
   * Paramater   : NA
   */ 
   Static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
         { 
           Set<ID> setRemainingPolicyMember =New Set<ID>();
           List<String> lstLabels = New List<String> ();
           Policy_Member__c objCurrentPolicyMember = New Policy_Member__c();
           List<Policy_Member__c> lstPolicyMember = New List<Policy_Member__c>();
           List<VobBenefitsCurrent_DTO_HUM> listOfDTOData = New List<VobBenefitsCurrent_DTO_HUM>();
           String accumsType ='';
           Integer startCounter = 0;
           Date dtOfService = null;
           PolicyMemberInfoWrapper_DTO_HUM oPolicyMember = New PolicyMemberInfoWrapper_DTO_HUM(objCurrentPolicyMember, lstPolicyMember, setRemainingPolicyMember, lstLabels, listOfDTOData, accumsType, startCounter, dtOfService);
         }     
    }
}