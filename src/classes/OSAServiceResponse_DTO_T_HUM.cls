/*****************************************************************************************************************************************************************************
    @Apex Class Name  : OSAServiceResponse_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 11th, 2020
    @Function         : Test class for OSAServiceResponse_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  11/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class OSAServiceResponse_DTO_T_HUM
{
    /*
    * This method verifies response structure for OSA Service
    * @param        None
    * @return       None
    */
    static testMethod void testResponseOSAService(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.runAs(oCCSUser)
        { 
            Test.StartTest();
            
            OSAServiceResponse_DTO_HUM objOSAServiceResponse = new OSAServiceResponse_DTO_HUM();
            List<OSAServiceResponse_DTO_HUM.isOSAResponse> lstIsOSAResponse = new List<OSAServiceResponse_DTO_HUM.isOSAResponse>();
            OSAServiceResponse_DTO_HUM.IsOSAResponse objIsOSAResponse = new OSAServiceResponse_DTO_HUM.IsOSAResponse();
            objIsOSAResponse.PlanID = 'S5884-151-000-2020';
            objIsOSAResponse.OSA = 'true';
            lstIsOSAResponse.add(objIsOSAResponse);
            objOSAServiceResponse = OSAServiceResponse_DTO_HUM.parse('{ "isOSAResponse":[ { "PlanID":"S5884-151-000-2020", "OSA":"true" } ] }');
            System.assertNotEquals(null, objOSAServiceResponse);
            
            Test.stopTest();
        }
    }
}