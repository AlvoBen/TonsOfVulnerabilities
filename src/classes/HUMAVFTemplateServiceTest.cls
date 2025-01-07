/************************************************************************************************************************
Apex Class Name  : HUMAVFTemplateServiceTest
Version          : 1.0
Created Date     : May 8 2015
Function         : Test class for HUMAVFTemplateService
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam         23024                       05/08/2014                   Original Version
* Sharan Shanmugam         23115                       05/15/2014                   Moved custom setting to constant file.
* Shruthi Karanth		   34224					   07/29/2016					Changed to json
*************************************************************************************************************************/
@isTest
private class HUMAVFTemplateServiceTest
{        
   /*
    * This is a test method to setup common test data for all test method
    * @param None
    * @return Void
    */
    @testSetup 
    static void testSetup()
    {   
    	insert new HUM_Webservice_Callout__c(Name='AVFTemplate',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TemplateService_V1.0/SaveAVFTemplate', Timeout__c='12000', Request_Type__c='GET', Content_Type__c='application/xml');     
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assert(oUser.LastName == 'test User1');
    }
    
   /*
    * This is a test method to check positive scenario
    * @param None
    * @return Void
    */    
    static testMethod void testHUMAVFTemplateServicePositive()    
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];
        
        system.runAs(oUser) 
        {
            HUMAVFTemplateService avfTemp  = new HUMAVFTemplateService();
            HUMAVFTemplateRequestHelper avfTempReqHelp = new HUMAVFTemplateRequestHelper();
        	avfTempReqHelp.CaseNumber = '1234';
            avfTempReqHelp.EditSequenceNumber = '1234';
            avfTempReqHelp.HumanaAssociateName = '1234';
            avfTempReqHelp.PersonFirstName = '1234';
            avfTempReqHelp.PersonLastName = '1234';
            avfTempReqHelp.LastSaveDate = '1234';
            avfTempReqHelp.MemberUMID = '1234';
            avfTempReqHelp.MedicareId = '1234';
            avfTempReqHelp.EnterprisePersonID = '1234';
            avfTempReqHelp.CMSMAPlanNumber = '1234';
            avfTempReqHelp.CMSPDPPlanNumber = '1234';
            avfTempReqHelp.PersonSpeakingWith = '1234';
            avfTempReqHelp.RelationToMember = '1234';
            avfTempReqHelp.PermanentResidentialAddress = '1234';
            avfTempReqHelp.PermanentResidentialCityName = '1234';
            avfTempReqHelp.PermanentResidentialCountyName = '1234';
            avfTempReqHelp.PermanentResidentialStateCode = '1234';
            avfTempReqHelp.PermanentResidentialZipCode = '1234';
            avfTempReqHelp.PermanentResidentialZipPlusCode = '1234';
            avfTempReqHelp.PermanentResidentialPhoneNumber = '1234';
            avfTempReqHelp.PermanentResidentialStartDate = '1234';
            avfTempReqHelp.MailingAddress = '1234';
            avfTempReqHelp.MailingCityName = '1234';
            avfTempReqHelp.MailingStateCode = '1234';
            avfTempReqHelp.MailingZipCode = '1234';
            avfTempReqHelp.MailingZipPlusCode = '1234';
            avfTempReqHelp.TemporaryAddress = '1234';
            avfTempReqHelp.TemporaryCityName = '1234';
            avfTempReqHelp.TemporaryCountyName = '1234';
            avfTempReqHelp.TemporaryStateCode = '1234';
            avfTempReqHelp.TemporaryZipCode = '1234';
            avfTempReqHelp.TemporaryZipPlusCode = '1234';
            avfTempReqHelp.TemporaryOSAStartDate = '1234';
            avfTempReqHelp.TemporaryOSAEndDate = '1234';
            avfTempReqHelp.E2ETrackID = '1234';
            avfTempReqHelp.E2EActivityID = '1234';
            avfTempReqHelp.E2EMilestoneID = '1234';
            System.assert(avfTempReqHelp.E2EMilestoneID != null);
            
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new HUMAVFTemplateMockHttpResponseTest());
            HUMAVFTemplateResponseHelper oResHelper = new HUMAVFTemplateResponseHelper();
            oResHelper = HUMAVFTemplateService.getAVFTemplate(avfTempReqHelp);
            system.assert(oResHelper.sErrorCode == '00000');
            Test.stopTest();       
        }
    }
    
    /*
    * This is a test method to check negative scenario
    * @param None
    * @return Void
    */    
    static testMethod void testHUMAVFTemplateServiceNegative()    
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {
            HUMAVFTemplateService avfTemp  = new HUMAVFTemplateService();
            HUMAVFTemplateRequestHelper avfTempReqHelp = new HUMAVFTemplateRequestHelper();
            
            Test.startTest();
            HUMAVFTemplateResponseHelper oResHelper = new HUMAVFTemplateResponseHelper();
            oResHelper = HUMAVFTemplateService.getAVFTemplate(avfTempReqHelp);
            system.assert(oResHelper == null);
            Test.stopTest();        
        }
    }
}