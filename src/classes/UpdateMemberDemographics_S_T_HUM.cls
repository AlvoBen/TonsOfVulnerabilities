/*****************************************************************************************************************************************************************************
    @Apex Class Name  : UpdateMemberDemographics_S_T_HUM
    @Version          : 1.0
    @Created Date     : June 26th, 2020
    @Function         : Test class for UpdateMemberDemographics_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  26/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class UpdateMemberDemographics_S_T_HUM {
    
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
            CRMFunctionality_ONOFF_Switch__c objOnOff = new CRMFunctionality_ONOFF_Switch__c();
            objOnOff.Name = '1569992';
            objOnOff.IsON__c = true;
            objOnOff.Release_Name__c = '10/23/2020';
            objOnOff.Scrum_Team__c = 'Thunder';
            objOnOff.Story_Title__c = 'PR00093155 - SF - Moving effective Date logic to Server side from Browser side.';
            insert objOnOff;
            HUM_Webservice_Callout__c objEligiblityRequestD = new HUM_Webservice_Callout__c();
            objEligiblityRequestD.Name = 'MMPUpdateMemberService';
            objEligiblityRequestD.Active__c = True;
            objEligiblityRequestD.Enable_Service_Tester__c = True;
            objEligiblityRequestD.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objEligiblityRequestD.Content_Type__c = 'application/json';
            objEligiblityRequestD.Request_Type__c = 'GET';
            objEligiblityRequestD.Timeout__c = '120000';
            objEligiblityRequestD.End_Point_URL__c = 'https://dev-crmservicews.humana.com/ManageMemberProcessServiceREST';
            objEligiblityRequestD.Service_Name__c = '/UpdateMember';
            insert objEligiblityRequestD;
            system.assertequals('MMPUpdateMemberService',objEligiblityRequestD.Name);
            system.assertequals('Humana_GuidanceCare_neptune', objEligiblityRequestD.Certificate_Name__c);
        }
    }
    
    /*
    * Method to test DemographicUpdate
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessUpdateMemberDemographics()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caserec=[select id,subject,description,Accountid from case where subject='Test Subject' limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            String sUpdateDemographic = '{"UpdateMemberRequest":{"timestamp":"2020-10-07T11:21:02.000937","requestid":"b288835b-4109-d064-2f8b-d2892ba3a046","consumer":"CRM","members":[{"lastname":"EDNILAO","middleinitial":"E","firstname":"RICHILEE","ssn":"436293539","ownindicator":"Y","demotype":"Z","biographics":{"changeindicator":"Y","effectivedate":"2020-10-07","enddate":"9999-12-31","lastname":"EDNILAO","middleinitial":"","middlename":"EZX","firstname":"RICHILEE","dateofbirth":"1947-10-09","personid":"","gender":"F","nameprefix":"","namesuffix":""},"criticalbiographics":{"ssn":{"changeindicator":"Y","newssn":"436293536"}},"demographics":null,"membercriterion":[{"updaterequesttype":["BIOGRAPHICS","SSN"],"membersourcepersonid":"1002677163","subscribersourcepersonid":"1002677163","platform":"METAVANCE","relationship":"EMPLOYEE","groupid":""}]}]}}';
            HttpRequest request = new HttpRequest();
            request = UpdateMemberDemographics_S_HUM.constructRequest(sUpdateDemographic);
            system.assertNotEquals(null, request);
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
            String sUpdateDemographic = null;
          	HttpRequest request = new HttpRequest();
            request = UpdateMemberDemographics_S_HUM.constructRequest(sUpdateDemographic);
            System.assertEquals(null, request);
            Test.StopTest();
        }
    }

}