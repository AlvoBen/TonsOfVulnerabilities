/*****************************************************************************************************************************************************************************
    @Apex Class Name  : OSAServiceRequest_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 11th, 2020
    @Function         : Test class for OSAServiceRequest_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  11/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class OSAServiceRequest_DTO_T_HUM
{
    /*
    * This method verifies request structure for OSA Service
    * @param        None
    * @return       None
    */
    static testMethod void testRequestOSAService(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.runAs(oCCSUser)
        { 
            Test.StartTest();
            
            OSAServiceRequest_DTO_HUM objOSAServiceRequest = new OSAServiceRequest_DTO_HUM();
            OSAServiceRequest_DTO_HUM.IsOSARequest objIsOSARequest = new OSAServiceRequest_DTO_HUM.IsOSARequest();
            List<String> lstPlanID = new List<String>();
            lstPlanID.add('S5884-151-000-2020');
            objIsOSARequest.FipsCode = '21111';
            objIsOSARequest.PlanID = lstPlanID;
            objIsOSARequest.ZipCode = '40222';
            
            objOSAServiceRequest = OSAServiceRequest_DTO_HUM.parse('{"isOSARequest":{"ZipCode":"40222","PlanID":["S5884-151-000-2020"],"FipsCode":"21111"}}');
            System.assertNotEquals(null, objOSAServiceRequest);
            
            Test.stopTest();
        }
    }
}