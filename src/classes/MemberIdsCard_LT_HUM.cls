/*******************************************************************************************************************************
Apex Class Name : MemberIdsCard_LT_HUM
Version         : 1.0
Created On      : 06/25/2021
Function        : This class istest class of MemberIdsCard_LC_HUM 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        06/25/2020               Original Version
*    1.1           Ritik Agarwal                                        06/28/2020               Added a test method for test the negative scenario
*******************************************************************************************************************************************************************/
@isTest
public with sharing class MemberIdsCard_LT_HUM {
    /*
* Method Name : prepareTestData
* Description : Create common test Data
* Return type : void
* Parameter   : NA
*/
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            RecordType oMemRecType = [SELECT Id,Name FROM RecordType WHERE Name = 'Member' Limit 1];
            Test.startTest();
            Account oacc1 = new Account();
            oacc1.firstName='firstName';
            oacc1.LastName='lastName';
            oacc1.MiddleName='middleName';
            oacc1.suffix='111111';
            oacc1.Deceased_Date__c='';
            oacc1.recordtypeid = oMemRecType.Id;
            oacc1.Enterprise_ID__c='1030693161';
            oacc1.PersonEmail='';
            oacc1.PersonMailingCity='';
            oacc1.PersonMailingState='';
            oacc1.PersonMailingStateCode='';
            oacc1.PersonMailingStreet='';
            oacc1.PersonMailingPostalCode='';
            oacc1.PersonHomePhone='';
            oacc1.Home_Office_Account__c=true;
            oacc1.ETL_Record_Deleted__c=false;
            oacc1.Individual_First_Name__c='getWrapperLast';
            oacc1.Individual_Last_Name__c='getWrapper';
            oacc1.SSN__C = '987654321'; 
            insert oacc1;
            
            MemberPlan omem = new MemberPlan();
            omem.Name='MemberPlan2';
            omem.MemberId=oacc1.id;
            omem.CBIS_ALT_ID__C= '123456789';  
            omem.VITALITY_ENTITY_ID__C= 'Y';
            insert omem;
            
            Account oacc = new Account();
            oacc.firstName='firstName';
            oacc.LastName='lastName';
            oacc.MiddleName='middleName';
            oacc.recordtypeid = oMemRecType.Id;
            insert oacc; 
            
            MemberPlan omem1 = new MemberPlan();
            omem1.Name='MemberPlan';
            omem1.MemberId=oacc.id;
            omem1.CBIS_ALT_ID__C= '';  
            insert omem1;
            Test.stopTest();
        }
    }
    
     /*
* Method Name : TestfetchMemberIdCardData
* Description : method for test the fetchMemberIdCardData 
* Return type : void
* Parameter   : NA
*/
   static testmethod void TestfetchMemberIdCardData() 
    {
      MemberPlan omem = [Select id,VITALITY_ENTITY_ID__C,CBIS_ALT_ID__C,Name from Memberplan where Name='MemberPlan2'];  
      Object memplan =  MeMberIdsCard_LC_HUM.fetchMemberIdCardData(omem.id, 'MemberPlan');
      List<MemberIdsCard_LS_HUM.MemberIdCardData> memCard = (List<MemberIdsCard_LS_HUM.MemberIdCardData>) memplan;  
      System.assertEquals(omem!=null,true);  
      System.assertEquals(omem.VITALITY_ENTITY_ID__C,'Y');
      System.assertEquals(omem.CBIS_ALT_ID__C,'123456789');  
      System.assertEquals(omem.Name,'MemberPlan2'); 
      
      System.assertEquals(memCard[0]!=null,true);  
      System.assertEquals(memCard[0].label!=null,true);
      System.assertEquals(memCard[0].value!=null,true); 
    }
    
    /*
* Method Name : TestNegativefetchMemberIdCardData
* Description : method for test the fetchMemberIdCardData negative scenario
* Return type : void
* Parameter   : NA
*/
   static testmethod void TestNegativefetchMemberIdCardData() 
    {
      MemberPlan omem = [Select id from Memberplan where Name='MemberPlan'];  
      Object memplan =  MeMberIdsCard_LC_HUM.fetchMemberIdCardData(omem.id, 'MemberPlan');
      System.assertEquals(omem!=null,true);  
      
      System.assertEquals(memplan==null,true);  
     
    }
}