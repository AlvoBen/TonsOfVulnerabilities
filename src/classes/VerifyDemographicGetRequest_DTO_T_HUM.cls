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
private class VerifyDemographicGetRequest_DTO_T_HUM {
    
   /*
    * Method Name : testVerifyDemographicGetRequest
    * Description : test the Request for GetVerifyDemographics
    * Return type : void
    * Paramater   : NA
    */  
    static testMethod void testVerifyDemographicGetRequest(){
        Test.startTest();       
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){  
            VerifyDemographicGetRequest_DTO_HUM objRequest = new VerifyDemographicGetRequest_DTO_HUM();
            objRequest = VerifyDemographicGetRequest_DTO_HUM.parse('{"RequestHeader": {"RequestID": "GETreq1","SessionID": "GETses1"},"getverificationstatusRequest": {"PersonId": "1000065119"}}');
        	system.assertNotEquals(objRequest,Null);
        }
        Test.stopTest();
    }
}