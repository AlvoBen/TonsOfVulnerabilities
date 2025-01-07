/**************************************************************************** 
Apex Class Name :  PolicyMemberInfoWrapper_T_HUM_V2
Version: 1.0  
Created Date     : 28th September, 2017
Function         : Test class for PolicyMemberInfoWrapper_DTO_HUM
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shivam Anand             140629             09/28/2017                  Original Version
* Shailesh Mali                               01/20/2020                  Version - 2, REQ - 791947
***************************************************************************************************/
@isTest
Private Class PolicyMemberInfoWrapper_T_HUM_V2
{
  /*
   * Method Name : prepareTestData
   * Description : Used to create wrapper class data
   * Return type : void
   * Paramater   : NA
   */
   @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('prepareTestData','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test prepareTestData');
    }
  /*
   * Method Name : runPositiveData
   * Description : Used to run scenerios of test data
   * Return type : void
   * Paramater   : NA
   */ 
   Static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id, UserName,LastName from User where LastName = 'test prepareTestData' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.LastName,'test prepareTestData');
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
           Member_Id__c memberIdObj = new Member_Id__c();
           PolicyMemberInfoWrapper_DTO_HUM_V2 oPolicyMember = New PolicyMemberInfoWrapper_DTO_HUM_V2(memberIdObj, objCurrentPolicyMember, lstPolicyMember, setRemainingPolicyMember, lstLabels, listOfDTOData, accumsType, startCounter, dtOfService);
         }     
    }

}