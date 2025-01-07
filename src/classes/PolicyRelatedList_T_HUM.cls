/******************************************************************************************************************
Apex class Name    : PolicyRelatedList_T_HUM
Version            : 1.0
Function           : Test Class for PolicyRelatedList_C_HUM
Created On         : August 13 2018

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0          Gaffur Gagguturu                                                     Original Version 
******************************************************************************************************************/
@isTest
private class PolicyRelatedList_T_HUM
{
      /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
   @testSetup  static void setUpTestData()
   {
     User oCCSUser = HUMTestDataHelperTest.createTestUser('Userprll','Customer Care Specialist');
        insert oCCSUser;
        System.assert(oCCSUser.LastName == 'test Userprll');
        
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.OwnerId=oCCSUser.id;
        acc.Name = 'Test Account Cases';
        acc.Enterprise_ID__c = '45678';
        acc.Group_Number__c = '12345678';
        acc.Source_Customer_Key__c ='234234324234232';
        insert acc;
        System.assertEquals(acc.id!=null,true);
        
        Product2 prod = new Product2();
        prod.Name='VIS';
        prod.Sold_Product_Key_Value__c ='prodKey1234';
        insert prod;
        System.assertNotEquals(prod.Id,null,'?Value not matched');
        
        Policy__c policy=HUMTestDataHelperTest.getPolicy();
        list<Policy__c> lstpolicy=new list<Policy__c> ();
        policy.Name = 'TestPolicy';
        policy.Group_Name__c=acc.id;
        policy.Exchange_Indicator__c = true;
        policy.Product__c =prod.id ;
        policy.Source_Cust_Cov_Key__c='2234233432423423422323';
        lstpolicy.add(policy);
        insert lstpolicy;
        System.assertEquals(policy.id!=null,true);
        
       } 
     
  /**
    * This method is used to test Policy related list Methods
    *
    * @param     NULL
    * @return    void 
    */
  private static testMethod void testMethods()
  {
      
     
        User objuser=[select id from user where lastName='test Userprll'];
        Account oAccnt = [select id,Name from Account where Name = 'Test Account Cases' limit 1];
        Product2 objprod=[select id,name,Sold_Product_Key_Value__c  from Product2 where name='VIS'];
        Policy__c objpolicy =[select id,name,Group_Name__c,Exchange_Indicator__c ,Product__c,Source_Cust_Cov_Key__c from  Policy__c  where name='TestPolicy'];
        System.runAs(objuser)
        {
          Test.StartTest();
          PolicyRelatedList_C_HUM oPlcyReltedList = new PolicyRelatedList_C_HUM ();
          oPlcyReltedList.sAccId = oAccnt.id;    
          System.assertNotEquals(null, oPlcyReltedList.setCon);
          oPlcyReltedList.getPolicy();
          oPlcyReltedList.refresh();  
          Test.StopTest();
        } 
  }   
  }