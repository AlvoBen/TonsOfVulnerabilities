/************************************************************************************************************************
Apex Class Name  : HUMServiceTesterControllerTest
Version          : 1.0
Created Date     : February 12 2015
Function         : Test class for HUMServiceTesterController
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam           21651                  02/12/2015                   Original Version
* Vishal Verma               21651                  03/20/2015                   Added code to insert custom settings record.
* ChiranjeeviRao Ravuri      22545                  04/22/2015                   Changed CIMedicareMemberService sSelectedMethod parameter value from GET to POST
* Gargey Sharma              22901                  05/05/2015                   REQ - 178697 : Added Request Type and Content Type fields to custom setting
* Gargey Sharma              23473                  05/27/2015                   REQ - 186152 : Added code to implement Virtual Switch in Service Tester 
* Vishal Verma               23473                  06/02/2015                   Fixing Code Review Comments for Review # 23473 
*************************************************************************************************************************/
@isTest
private class HUMServiceTesterControllerTest
{  
    /*
     * Method Name : createCustomSetting 
     * Description : Method is used to create custom setting data ifor Test class
     * Return type : void
     * Return type : NA
     */
    @testSetup 
    static void createCustomSetting() 
    {
        List<HUM_Webservice_Callout__c> lstWebserviceCallout = new List<HUM_Webservice_Callout__c>();
        lstWebserviceCallout.add(new HUM_Webservice_Callout__c(Name='HumCIMedicareMemberService',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/CIMedicareMember', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C'));
        lstWebserviceCallout.add( new HUM_Webservice_Callout__c(Name='GroupSearchService',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/GroupService.svc', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C'));
        lstWebserviceCallout.add( new HUM_Webservice_Callout__c(Name='VirtualSwitch_Get',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/VirtualConfigDetails/get', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C'));
        lstWebserviceCallout.add( new HUM_Webservice_Callout__c(Name='VirtualSwitch_Update',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/VirtualConfigDetails/update', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C'));        
        
        insert lstWebserviceCallout;    
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
    }
    /*
     * Method Name : testHUMServiceTesterController
     * Description : Method to cover positive scenarios for HUMServiceTesterController
     * Return type : void
     * Paramater   : None 
     */
    static testMethod void testHUMServiceTesterController() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runAs(u) 
        {
            Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());
            HUMServiceTesterController service = new HUMServiceTesterController();
            service.sService = 'HumCIMedicareMemberService';
            service.getServices();
            service.getMethodsAvailable();
            service.getRequestType();
            service.getContentType();
            service.fetchServiceDetails(); 
            Test.startTest();
                service.executeRequest();
            Test.stopTest();
            system.assert(service.lstXmlResponse.isEmpty() == false);
        }
    }
    
    /*
     * Method Name : testVirtualSwitchGet
     * Description : Method to cover positive scenarios for HUMServiceTesterController
     * Return type : void
     * Paramater   : None 
     */
    static testMethod void testVirtualSwitchGet() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runAs(u) 
        {
            Test.setMock(HttpCalloutMock.class, new HUMVirtualSwitchGetMockHttpResponseTest());
            HUMServiceTesterController service = new HUMServiceTesterController();
            service.sService = 'GroupSearchService';
            service.sMethodsAvailable = 'GetGBEInfo';
            Test.startTest();
                service.getURL();
            Test.stopTest();
            system.assert(service.sVirtualURL != null);
        }
    }
    
    /*
     * Method Name : testVirtualSwitchSet
     * Description : Method to cover positive scenarios for HUMServiceTesterController
     * Return type : void
     * Paramater   : None 
     */
    static testMethod void testVirtualSwitchSet() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runAs(u) 
        {
            Test.setMock(HttpCalloutMock.class, new HUMVirtualSwitchGetMockHttpResponseTest());
            HUMServiceTesterController service = new HUMServiceTesterController();
            service.sService = 'GroupSearchService';
            service.sMethodsAvailable = 'GetGBEInfo';
            Test.startTest();
                service.setURL();
            Test.stopTest();
            boolean bMsg = false;
            for(Apexpages.Message msg: ApexPages.getMessages())
            {
                if (msg.getDetail().contains(Label.HUMVSwitchUpdateSuccess)) bMsg = true; // Check if proper messge is displayed on Page
            }
            system.assert(bMsg);
        }
    }
    
    /*
     * Method Name : testHUMServiceTesterController_NoInputs
     * Description : Method to cover HUMServiceTesterController when no inputs are passed
     * Return type : void
     * Paramater   : None 
     */
    static testMethod void testHUMServiceTesterController_NoInputs() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runAs(u)
        {
            Test.startTest(); 
                HUMServiceTesterController service= new HUMServiceTesterController();
                service.executeRequest();
                service.getURL();
                service.setURL();
            Test.stopTest();
            system.assert(service.lstXmlResponse.isEmpty() == true);
        } 
    }
    
    /*
     * Method Name : testHUMServiceTesterController_Negative
     * Description : Method to cover Negative scenarios for HUMServiceTesterController
     * Return type : void
     * Paramater   : None 
     */ 
    static testMethod void testHUMServiceTesterController_Negative() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runAs(u) 
        {
            HUMServiceTesterController service = new HUMServiceTesterController();
            service.sService = 'HumCIMedicareMemberService';
            service.getServices();
            service.getRequestType();
            service.getContentType();  
            service.fetchServiceDetails(); 
            Test.startTest();
                service.executeRequest();
            Test.stopTest();
            system.assert(service.lstXmlResponse.isEmpty() == true);
        }
    }
}