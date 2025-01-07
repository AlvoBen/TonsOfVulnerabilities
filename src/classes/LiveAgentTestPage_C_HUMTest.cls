/*****************************************************************************************************************************************************************
Apex Class Name : LiveAgentTestPage_T_HUM
Version         : 1.0
Created On      : 10/09/2015
Function        : This is test class for LiveAgentTestPage_C_HUM
                 
Modification Log: 
* Developer Name              Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Isaac Lehar/Manish Kumar                             10-09-2015                 Original version
* Gargey Sharma                                        07/29/2016                 REQ - 237067: Dynamically Generate Chat for Testing Home Page
* Suresh Gottipati                                     07/31/2018                 Fixed build issues.
* Lakshmi Madduri                               	   04/09/2019                 HP Live Agent changes
*******************************************************************************************************************************************************************/


@isTest
private class LiveAgentTestPage_C_HUMTest
{
    /*
    * Method Name : setUpTestData
    * Description : Test setup method to created test data
    * Return Type : void
    * Parameters  : NA
    */
    @testSetup
    static void setUpTestData()
    {
      
        insert new HUMConstants__c( Name = 'CRYPTOKEY', StringValue__c = '78dd574bfdec4ef38042df8091c299c6');
        insert new HUMConstants__c( Name = 'CRYPTOALGORITHM', StringValue__c = 'AES256');
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assert(oCCSUser.LastName == 'test User1');
            
        System.runAs(oCCSUser)
        {
            Test.startTest();
            Account oAccountRec = HUMTestDataHelperTest.getAccount();
            oAccountRec.FirstName = 'General';
            oAccountRec.LastName = 'Member';
            oAccountRec.Individual_First_Name__c = 'General';
            oAccountRec.Individual_Last_Name__c  = 'Member';
            oAccountRec.Enterprise_Id__c  = '123457890';
            oAccountRec.Birthdate__c = '10/10/2010';
            oAccountRec.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAccountRec;
            System.assert(oAccountRec.id!= null);
            System.assert(oAccountRec.Individual_First_Name__c == 'General'); 
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assertNotEquals(oProduct.id,null,'Value not matched');
            system.assert(oProduct.Name == 'Mediclaim');
                
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccountRec.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            system.assert(oPolicy.id != null);
            system.assert(oPolicy.Name == 'test123');
                
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccountRec.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            system.assert(oPolicyMem.id != null);
            system.assert(oPolicyMem.Name == '12345'); 
            Test.StopTest();
        }   
    }
    
    
    /*
    * Test method to cover LiveAgent Test Page
    * @param None
    * @return Void
    */
    static testMethod void prepareTestDataForLiveAgent()
    {
        User oCCSUser = [Select Id, Name From User Where LastName='test User1' Limit 1];
        
        System.runAs(oCCSUser)
        {
            Test.startTest();
            
            LiveAgentTestPage_C_HUM oLivegaent = new LiveAgentTestPage_C_HUM();
            
            ApexPages.currentPage().getParameters().put('EntId','1234567890');
            system.assert(oLivegaent.encryptedValue != null);
            
            PageReference oPg = Page.HUMLiveAgentSiteHomePage;
            oLivegaent.encryptedValue = '';
            oLivegaent.bIsError = false;
            PageReference oPgReset = oLivegaent.Reset();
            system.assert(String.isBlank(oLivegaent.sFname) == true);
            
            Test.stopTest();
        }
    }
    
    /*
    * Test method to cover all search Scenarios
    * @param None
    * @return Void
    */
    static testMethod void testSearchMethod()
    {
        User oCCSUser = [Select Id, Name From User Where LastName='test User1' Limit 1];
        
        System.runAs(oCCSUser)
        {
            Test.startTest();
            
            LiveAgentTestPage_C_HUM oLiveagent = new LiveAgentTestPage_C_HUM();
            oLiveagent.sFname = 'General';
            oLiveagent.sLname = 'Member';
            oLiveagent.sDob = '10/10/2010';
            oLiveagent.sEnterpriseId = '123457890';
            oLiveagent.sMemberId = '12345';
            oLiveagent.searchMember();
            system.assert(oLiveagent.lstSearcResults.size() == 0);
            
            LiveAgentTestPage_C_HUM oLiveagent1 = new LiveAgentTestPage_C_HUM();
            oLiveagent1.sFname = 'General';
            oLiveagent1.searchMember();
            system.assert(oLiveagent1.lstSearcResults.size() == 0);
            
            LiveAgentTestPage_C_HUM oLiveagent2 = new LiveAgentTestPage_C_HUM();
            oLiveagent2.sDob = '10/10/20';
            oLiveagent2.searchMember();
            system.assert(oLiveagent2.lstSearcResults.size() == 0);
            
            LiveAgentTestPage_C_HUM oLiveagent3 = new LiveAgentTestPage_C_HUM();
            oLiveagent3.sEnterpriseId = '123457890123456';
            oLiveagent3.searchMember();
            system.assert(oLiveagent3.lstSearcResults.size() == 0);
            
            LiveAgentTestPage_C_HUM oLiveagent4 = new LiveAgentTestPage_C_HUM();
            oLiveagent4.sMemberId = '12345';
            oLiveagent4.searchMember();
            system.assert(oLiveagent4.lstSearcResults.size() == 0);
            
            LiveAgentTestPage_C_HUM oLiveagent5 = new LiveAgentTestPage_C_HUM();
            oLiveagent5.sFname = 'Ge';
            oLiveagent5.sLname = 'er';
            oLiveagent5.searchMember();
            system.assert(oLiveagent5.lstSearcResults.size() == 0);
            
            LiveAgentTestPage_C_HUM oLiveagent6 = new LiveAgentTestPage_C_HUM();
            oLiveagent6.sLname = 'General';
            oLiveagent6.searchMember();
            system.assert(oLiveagent6.lstSearcResults.size() == 0);
            
            LiveAgentTestPage_C_HUM oLiveagent7 = new LiveAgentTestPage_C_HUM();
            oLiveagent7.sFname = 'General';
            oLiveagent7.sLname = 'Member';
            oLiveagent7.sMemberId = '1235';
            oLiveagent7.searchMember();
            system.assert(oLiveagent7.lstSearcResults.size() == 0);
            
            Test.stopTest();
        }
    }
    
    /*
    * Test method to cover redirection of User back to test page
    * @param None
    * @return Void
    */
    static testMethod void redirectBackToTestPage()
    {
        User oCCSUser = [Select Id, Name From User Where LastName='test User1' Limit 1];

        System.runAs(oCCSUser)
        {
            Test.startTest();
            LiveAgentTestPage_C_HUM oLivegaent = new LiveAgentTestPage_C_HUM();
            oLivegaent.encryptedValue = '';
            PageReference oPgRef = oLivegaent.liveAgentTestPage();
            PageReference oPg = Page.LiveAgentTestPage_VF_HUM;
            system.assert(oLivegaent.encryptedValue == '');
            Test.stopTest();
        }
    }
}