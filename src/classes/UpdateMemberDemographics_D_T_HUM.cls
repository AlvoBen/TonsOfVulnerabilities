/*****************************************************************************************************************************************************************************
    @Apex Class Name  : UpdateMemberDemographics_D_T_HUM
    @Version          : 1.0
    @Created Date     : June 26th, 2020
    @Function         : Test class for UpdateMemberDemographics_D_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  26/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@istest
public class UpdateMemberDemographics_D_T_HUM {
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
     @testSetup static void prepareTestData() 
     {  
        User oSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oSUser;
      	System.runAs(osUser) 
        {
           id RecordTypeID=Schema.SObjectType.case.getrecordtypeinfosByname().get('Medicare Case').getRecordtypeID();
         
           Account account = HUMTestDataHelperTest.getAccount();
	   account.Name = 'Test Account';
	   insert account;
         
	   Case cse = HUMTestDataHelperTest.getCase();
	   cse.subject = 'Test Subject';
	   cse.Description = 'Test Description';
	   cse.AccountId = account.id;
	   cse.RecordTypeId = RecordTypeID;
	   insert cse;
            HUM_Webservice_Callout__c objUpdateMemberDemographics = new HUM_Webservice_Callout__c();
            objUpdateMemberDemographics.Name = 'MMPUpdateMemberService';
            objUpdateMemberDemographics.Active__c = True;
            objUpdateMemberDemographics.Enable_Service_Tester__c = True;
            objUpdateMemberDemographics.Certificate_Name__c = 'Humana_GuidanceCare_Deimos';
            objUpdateMemberDemographics.Content_Type__c = 'application/json';
            objUpdateMemberDemographics.Request_Type__c = 'POST';
            objUpdateMemberDemographics.Timeout__c = '12000';
            objUpdateMemberDemographics.End_Point_URL__c = 'https://dev-crmservicews.humana.com/ManageMemberProcessServiceREST';
            objUpdateMemberDemographics.Service_Name__c = '/UpdateMember';
            insert objUpdateMemberDemographics;
        } 
    }
    
/*
    * Method to test success response for Eligiblity
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessEligiblity()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":{"source":"40221","groupid":"KY","IncludeValidationDetails":false,"IncludeInputOnError":false,"IncludeGeoCode":true,"IncludeEWS":false,"IncludeDPV":false,"City":"Louisville","AddressLine2":"Apt 2","AddressLine1":"9022 Shelbyville Road"}}';
          	Test.setMock(HttpCalloutMock.class, new EligiblitySuccessMockResponse_T_HUM());
            String result = UpdateMemberDemographics_D_HUM.retrieveUpdateResponse(sEligiblityRequestDHUM);
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }
    
     /*
    * Method to test failure response for Eligiblity
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureEligiblityRequest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest(); 
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":{"source":""}}';
          	Test.setMock(HttpCalloutMock.class, new EligiblityFailureMockResponse_T_HUM());
            String result = UpdateMemberDemographics_D_HUM.retrieveUpdateResponse(sEligiblityRequestDHUM);
            System.assertNotEquals(null, result);    
            Test.StopTest();
        }
    }
    
    /*
    * Method to test exception for EligiblityRequest
    * @param        None
    * @return       None
    */
    private static testmethod void testExceptionEligiblityRequest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest(); 
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":""}';
            String result = UpdateMemberDemographics_D_HUM.retrieveUpdateResponse(sEligiblityRequestDHUM);
            System.assertNotEquals(null, result); 
            Test.StopTest();
        }
    }

}