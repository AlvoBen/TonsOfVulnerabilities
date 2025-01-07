/**************************************************************************** 
Apex Class Name  : HUMCIMedicareMemberServiceTest
Version          : 1.0  
Created Date     : December 05 2014 
Description      : This class is used as test class for HUMCIMedicareMemberService
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri     19768                   12/05/2014                   Original Version
* Pradeep Veeravali         20004                   12/19/2014                   added more methods to improve code coverage
* Pradeep Veeravali         20628                   01/29/2015                   added a method to cover Error code
* Pradeep Veeravali         20878                   02/11/2015                   Modified to remove EOE as parameter in callRestCIMedicareMemberService() signature
* Shruthi Karanth			34224					07/29/2016					 Changed to json                   
***************************************************************************************************/
@isTest
private Class HUMCIMedicareMemberServiceTest 
{        
    /*
    * This is a test method to setup common test data for all test method
    * @param None
    * @return Void
    */
    @testSetup 
    static void testSetup()
    {   
    	insert new HUMConstants__c(Name='HUMLISTSIZEZERO', IntegerValue__c = 0);
        insert new HUM_Webservice_Callout__c(Name='HumCIMedicareMemberService', Certificate_Name__c='Humana_GuidanceCare_R1', Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/CIMedicareMember', Timeout__c='12000',Request_Type__c='POST', Content_Type__c='application/xml');     
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assert(oUser.LastName == 'test User1');
    }
    
    /*
    * This is a test method to test positive scenarios
    * @param None
    * @return Void
    */
    static testMethod void testHUMCIMedicareMemberService_positive()
    {                 
        HumCIMedicareMemberService CIMedicareMember = new HumCIMedicareMemberService();               
               
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];       
        system.runAs(oUser) 
        {
           test.startTest();
            Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());
            HUMCIMedicareMemberResponseHelper response = CIMedicareMember.callRestCIMedicareMemberService('500426508A','','135691','001','10/09/2014','Y','Y','Y','Y','Y','Y');                        
            system.assert(response.sSNPIndicator == 'Y');
           test.stopTest();
        }
       
    }
    
    /*
    * This is a test method to test negative scenarios
    * @param None
    * @return Void
    */
    static testMethod void testHUMCIMedicareMemberService_negative()
    {                 
        HumCIMedicareMemberService CIMedicareMember = new HumCIMedicareMemberService();               
               
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];       
        system.runAs(oUser) 
        {
           test.startTest();
            Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());
            HUMCIMedicareMemberResponseHelper response = CIMedicareMember.callRestCIMedicareMemberService('500426508E','','135691','001','10/09/2014','N','N','N','N','N','N');                        
            system.assert(response.Errors.size() > 0);
           test.stopTest();
        }
       
    }
}