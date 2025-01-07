@isTest
private class Constants_T_AHT_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
     	User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.lastname=='test User1',true);
    }
   
    /*
    * Method Name : testConstantsInstantiation
    * Description : Test method to instantiate constants
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testConstantsInstantiation()
    {
        User u = [select id, name from user where lastname='test User1' limit 1];
        System.runAs(u)
        {
            Test.startTest();
            Constants_C_AHT_HUM humCons = new Constants_C_AHT_HUM();
            Test.stopTest();
        }
    }
}