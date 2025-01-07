/**********************************************************************************************************************
Apex Class: DentalInformationRedirect_T_HUM
Description: Test class for DentalInformationRedirect_C_HUM
Created By: Naveen Kanth B
Created On:  April 21 2016

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer            Code Review         Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Naveenkanth B                            04/21/2016               REQ - 241731 : Original Version.
*Avinash Choubey                           05/06/2016               Defect 224770 fix
*Avinash Choubey       30512               06/17/2016               Defect 230126 fix changes 
**************************************************************************************************************************************************************/
@isTest
public class DentalInformationRedirect_T_HUM 
{
    
    /*        
     * Method Name : setUpTestData
     * Description : Test method to setup common test data for all test methods        
     * Return type : void        
     * Paramater   : NA        
     */ 
    @testSetup static void setUpTestData() 
	{
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname=='test User1',true);
        System.runAs(objUser) 
		{
            Account objAcc = HUMTestDataHelperTest.getAccount();
            objAcc.Name = 'testacc';
            insert objAcc;
            system.assertEquals('testacc', objAcc.Name);

            Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
            objpol1.Name = 'testPolicy';
            objpol1.Platform__c = 'LV';
            objpol1.Group_Name__c = objAcc.Id;
            objpol1.Major_LOB__c = 'MER';
            insert objpol1;
            system.assertEquals(objpol1.Platform__c, 'LV');

            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Effective_Date__c = Date.today().addDays(-1).format();
            objPolMem.End_Date__c = Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c = '00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol1.Id;
            objPolMem.Policy__r = objpol1;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            HUMToothHistoryAppURL__c toothHistory = new HUMToothHistoryAppURL__c();
            toothHistory.Name = 'CCPURL';
            toothHistory.CCPAppURL__c = 'https://test-ccp.humana.com/ccp2/ToothHistory.asp';
            insert toothHistory;
            System.assertNotEquals(NULL,toothHistory.Id);  
        }
	}
   
    /*        
     * Method Name : testDental
     * Description : Test method to validate the class DentalInformationRedirect_C_HUM       
     * Return type : void        
     * Paramater   : NA        
     */ 
    public static testMethod void testDental() 
	{
        User testUser = [Select Id, Name,lastname from User where LastName = 'test User1'];
        system.assertEquals(testUser.lastname=='test User1',true);
        Policy_Member__c testPolMem = [Select Id, Name from Policy_Member__c where Name = 'testpolmem11'];
      	system.assertEquals(testPolMem.name == 'testpolmem11',true);
        System.runAs(testUser) 
		{
            PageReference pageRef = page.DentalInformationRedirect_VF_HUM;
            Test.setCurrentPage(pageRef);
            Test.startTest();
            String pmId = ApexPages.currentpage().getParameters().put('id', testPolMem.id);
            DentalInformationRedirect_C_HUM DentalObj = new DentalInformationRedirect_C_HUM();
            Test.stopTest();
        }
    }
}