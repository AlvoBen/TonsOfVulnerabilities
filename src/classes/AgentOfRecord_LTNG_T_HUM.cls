/****************************************************************************************
Apex Class Name :  AgentOfRecord_LTNG_T_HUM
Version: 1.0 
Created Date     : 06/27/2019
Function         : Test class to AgentOfRecord_LTNG_C_HUM Apex class
Modification Log : 
*    Developer               Code Review            Date                 Description
* ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Chetan S                                     11/12/2019            Original Version.
*  Pradeep Dani                                 06/05/2020            Code Coverage related changes.
 * Mohamed Thameem                              22/06/2023            US4736604 - T1PRJ0154546 / SF / MF9 Storefront - AOR UI Component Upgrade[Tech]
****************************************************************************************************************************************************************************/
@isTest
private class AgentOfRecord_LTNG_T_HUM{
   /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {  
           RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '987654321';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.PersonBirthdate = Date.newInstance(1984, 01, 01);
            oAcc.Birthdate__c = '01/01/1984';
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
            oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1017246970';
            oAcc.Home_Office_Account__c = false;
            oAcc.PersonMailingState = 'New York';
            oAcc.PersonMailingPostalCode = '566058';
            oAcc.PersonHomePhone = '';
            oAcc.Waiver_Date__c = Date.newInstance(2007, 01, 10);//system.today().addMonths(-36);
            insert oAcc;
            System.AssertNotEquals(oAcc, NULL);
            
            HUM_Webservice_Callout__c crmretailServiceConfigObj = new HUM_Webservice_Callout__c();
            crmretailServiceConfigObj.Name = 'AORDetailsFetchService';
            crmretailServiceConfigObj.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            crmretailServiceConfigObj.Active__c = true;
            crmretailServiceConfigObj.End_Point_URL__c ='https://dev-crmservicews.humana.com/ProducerCompensationRelationshipsREST';
            crmretailServiceConfigObj.Service_Name__c = '/producerBookOfBusinessRequest';
            crmretailServiceConfigObj.Timeout__c = '12000';
            crmretailServiceConfigObj.Request_Type__c = 'GET';
            crmretailServiceConfigObj.Content_Type__c = 'application/json'; 
            insert crmretailServiceConfigObj;
            system.assert(HUM_Webservice_Callout__c.getValues('AORDetailsFetchService').Certificate_Name__c == 'Humana_GuidanceCare_neptune');
        }
    }
    
     /*
    * Method Name : prepareTestDataForSearchCriteria
    * Description : Test method to cover  
    * Return Type : NA
    * Parameters : NA
    */
    static testMethod void prepareTestDataForSearchCriteria()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        system.runAs(oCCSUser)
        {
            List<Id> accids = new List<Id>();
            Account oAcc = [select Id, Name,FirstName, LastName, PersonBirthdate, Birthdate__c, PersonMailingState, PersonMailingPostalCode, 
                            recordtypeid,RecordType.Name,Enterprise_ID__c,PersonHomePhone from Account where LastName = 'Superman' LIMIT 1];
            system.assertEquals(oAcc.LastName,'Superman');
            accids.add(oAcc.Id);
            
            try
            {
                Test.startTest();
                Test.setMock(HttpCalloutMock.class, new AORMockHttpResponse_LTNG_T_HUM());    
                Map<String, Object> res = AgentOfRecord_LTNG_C_HUM.retrieveAORDetails(oAcc.Id,oAcc.Enterprise_ID__c);
                System.assertEquals(((Map<string, string>)res.get('Data')).get('State'), 'SUCCESS');
                
                //Success Scenario with empty nodes
                Map<String, Object> successRes = AgentOfRecord_LTNG_C_HUM.retrieveAORDetails(oAcc.Id, 'SuccessResponse');
                System.assertEquals(((Map<string, string>)successRes.get('Data')).get('State'), 'SUCCESS');
                
                //Negative Scenario by receving fault code
                Map<String, Object> resFault = AgentOfRecord_LTNG_C_HUM.retrieveAORDetails(oAcc.Id, 'faultcode');
                System.assertEquals(((Map<string, string>)resFault.get('Data')).get('State'), 'ERROR');
                
                //Negative Scenario by sending wrong enterprise id
                Map<String, Object> resError = AgentOfRecord_LTNG_C_HUM.retrieveAORDetails(oAcc.Id, 'Enterprise_ID__test');
                System.assertEquals(((Map<string, string>)resError.get('Data')).get('State'), 'ERROR');
                
                Map<String, Object> resError1 = AgentOfRecord_LTNG_C_HUM.retrieveAORDetails(oAcc.Id, 'faultcodeXXX');
                System.assertEquals(((Map<string, string>)resError1.get('Data')).get('State'), 'ERROR');

                Test.stopTest();
            }
            Catch(Exception e)
            {
                HUMExceptionHelper.logErrors(e, 'AgentOfRecord_LTNG_T_HUM', 'TestclasswithMockData');
            }
        }
    }     

    /*
    * Method Name : testgetAccountRecord
    * Description : Test method to cover getAccountRecord
    */
    @isTest static void testgetAccountRecord()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        assert.areEqual('user1user1@humcrmstest.com',oCCSUser.UserName);
        
        system.runAs(oCCSUser)
        {
            Account oAcc = [select id,enterprise_id__c,LastName,recordtype.id,recordtype.name from Account where LastName = 'Superman' limit 1];
            assert.areEqual('Superman',oAcc.LastName);
            
            List<Account> accList = AgentOfRecord_LTNG_C_HUM.getAccountRecord(oAcc.Id);
            assert.areEqual(oAcc.enterprise_id__c,accList[0].enterprise_id__c);
            assert.areEqual(oAcc.recordtype.name,accList[0].recordtype.name);
        }
    }
}