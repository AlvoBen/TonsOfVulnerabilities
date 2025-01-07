/*****************************************************************************************************************************************************************************
    @Apex Class Name  : VerifyDemographicGetRequest_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : July 30th, 2020
    @Function         : Test class for VerifyDemographicGetRequest_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  07/30/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class VerifyDemographicUpdateRequest_DTO_T_HUM {
	
	/*
    * Method Name : testVerifyDemographicUpdateRequest
    * Description : test the Request for UpdateVerifyDemographics
    * Return type : void
    * Paramater   : NA
    */  
    static testMethod void testVerifyDemographicUpdateRequest(){
        Test.startTest();       
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){  
            VerifyDemographicUpdateRequest_DTO_HUM objRequest = new VerifyDemographicUpdateRequest_DTO_HUM();
            objRequest = VerifyDemographicUpdateRequest_DTO_HUM.parse('{"RequestHeader": {"RequestID": "PUTreq12","SessionID": "PUTses12"},"putverificationstatusRequest": {"PersonId": ["1000060697"],"demographics": {"lastVerifiedDate": "2020-05-16","source": "CRM","verifiedBy": "POLIMERA1234"}}}');
        	system.assertNotEquals(objRequest,Null);
        }
        Test.stopTest();
    }
}