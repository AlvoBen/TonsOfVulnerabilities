/**********************************************************************************************************************************************
Apex Class Name  : PolicyProductTypeWrapper_DTO_T_HUM
Version          : 1.0
Created Date     : August 2020
Function         : Test class for PolicyProductTypeWrapper_DTO_T_HUM 
Modification Log :
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Sagar MN											08/06/2020				    USER STORY 1329433 EDW Reporting changes - Increased the test coverage
**************************************************************************************************************************************/
@isTest
public class PolicyProductTypeWrapper_DTO_T_HUM {

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
        policy.Benefit_Coverage__c='HYG1';
        policy.Platform__c='LV';
       // policy.Source_Cust_Cov_Key__c='60030501E000';
       	policy.Coverage_Plan_Effective_Date__c='08/01/2015';
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
     User objuser=[select id,lastName from user where lastName='test Userprll'];  
     System.assertEquals('test Userprll', objuser.lastName); 
      System.runAs(objuser)
        {
           Test.StartTest();
             Account oAccnt = [select id,Name from Account where Name = 'Test Account Cases' limit 1];
        Product2 objprod=[select id,name,Sold_Product_Key_Value__c  from Product2 where name='VIS'];
        Policy__c objpolicy =[select id,name,Group_Name__c,Exchange_Indicator__c ,Product__c,Source_Cust_Cov_Key__c,Platform__c,Benefit_Coverage__c,Coverage_Plan_Effective_Date__c from  Policy__c  where name='TestPolicy'];
     	 String prodtype='VSVPP'; 
            
            PolicyProductTypeWrapper_DTO_HUM opolicyDTO = new PolicyProductTypeWrapper_DTO_HUM(objpolicy,prodtype);
            Test.StopTest();
        }
      
  }
}