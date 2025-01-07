/****************************************************************************************
Apex Class Name  : ProviderDisputesRequest_T_HUM
Version          : 1.0 
Function         : Test class to ProviderDisputesRequest_H_HUM Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                10/15/2019              Original Version     
***********************************************************************************************************/
@isTest
private class ProviderDisputesRequest_T_HUM
{
        /*
    * Method Name : setup
    * Description : Create common test Data
    * Return type : void
    * Paramater   : NA
    */ 
    static testMethod void testSetup(){
        Test.startTest();       
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u; 
        system.assertEquals(u.Id != Null,true);
        system.assertEquals(u.Lastname == 'test User1',true);        
        System.runAs(u){    
            ProviderDisputesRequest_H_HUM docRequestObj = new ProviderDisputesRequest_H_HUM();
            ProviderDisputesRequest_H_HUM.GetProviderDisputesByCriteria proDisputesObj = new ProviderDisputesRequest_H_HUM.GetProviderDisputesByCriteria();
            ProviderDisputesRequest_H_HUM.Member memberObj = new ProviderDisputesRequest_H_HUM.Member();
            memberObj.externalId = 'Test External';
            proDisputesObj.fromDate = 'test';
            proDisputesObj.member = memberObj;
            proDisputesObj.thruDate = 'Test';
            docRequestObj = ProviderDisputesRequest_H_HUM.parse('{"getProviderDisputesByCriteria":{"thruDate":"2019-11-04T07:47:57","member":{"externalId":"1007711483"},"fromDate":"2018-05-05T08:47:57"}}');
        }
        Test.stopTest();
    }
}