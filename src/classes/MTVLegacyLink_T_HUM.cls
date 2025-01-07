/**************************************************************************** 
Apex Class Name  : MTVLegacyLink_T_HUM 
Version          : 1.0  
Created Date     : May 20, 2016
Function         : Test class for MTVLegacyLink_C_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma            	27989             05/20/2016                   Original Version
***************************************************************************************************/
@isTest
private Class MTVLegacyLink_T_HUM
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
        insert new HUM_Webservice_Callout__c( Name = System.Label.HUMLaunchGCP , Certificate_Name__c = 'TestCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        system.assertEquals(oCCSUser.LastName,'test User1');   
    }
    
     /*
     * Method Name : MTVLegacyLaunch
     * Description : This method tests MTVLegacyLink_C_HUM 
     * Return type : void
     * Parameter   : None
     */ 
    Static testMethod void MTVLegacyLaunch() 
    {
    	ApexPages.currentPage().getParameters().put('AppName','MTV');
    	ApexPages.currentPage().getParameters().put('ClmNbr','12345');
    	
        User oCCSUser = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        {
            Test.startTest();
            MTVLegacyLink_C_HUM mtvleg = new MTVLegacyLink_C_HUM();
            Test.stopTest();
            system.assertEquals(mtvleg.sAppName,'MTV');
            system.assertEquals(mtvleg.sClmNumber,'12345');   
      }
    }
}