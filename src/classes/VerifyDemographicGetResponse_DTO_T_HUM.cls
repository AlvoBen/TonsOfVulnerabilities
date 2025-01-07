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
private class VerifyDemographicGetResponse_DTO_T_HUM {

   /*
    * Method Name : testVerifyDemographicGetResponse
    * Description : test the response for GetVerifyDemographics
    * Return type : void
    * Paramater   : NA
    */ 
    static testMethod void testVerifyDemographicGetResponse(){
        Test.startTest();       
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){    
            VerifyDemographicGetResponse_DTO_HUM objResponse = new VerifyDemographicGetResponse_DTO_HUM();
            objResponse = VerifyDemographicGetResponse_DTO_HUM.parse('{"ResponseHeader":{"RequestId": "GETreq1","SessionId": "GETses1","ResponseDuration": "12.155436","CoreApiHttpStatusCode": "200 OK"},"getverificationstatusResponse":{"demographics":{"lastVerifiedDate": "2020-05-16","source": "CRM","verifiedBy": "POLIMERA12345"}}}');
        	system.assertNotEquals(objResponse,Null);
        }
        Test.stopTest();
    }
}