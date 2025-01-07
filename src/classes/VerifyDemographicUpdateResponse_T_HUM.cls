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
private class VerifyDemographicUpdateResponse_T_HUM {

    /*
    * Method Name : testVerifyDemographicUpdateResponse
    * Description : test the Response for UpdateVerifyDemographics
    * Return type : void
    * Paramater   : NA
    */  
    static testMethod void testVerifyDemographicUpdateResponse(){
        Test.startTest();       
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){  
            VerifyDemographicUpdateResponse_DTO_HUM objRequest = new VerifyDemographicUpdateResponse_DTO_HUM();
            objRequest = VerifyDemographicUpdateResponse_DTO_HUM.parse('{"ResponseHeader":{"RequestId": "PUTreq12","SessionId": "PUTses12","ResponseDuration": "177.884201","CoreApiHttpStatusCode": "200 OK"},"putverificationstatusResponse": {"Status": "success"}}');
        	system.assertNotEquals(objRequest,Null);
        }
        Test.stopTest();
    }
}