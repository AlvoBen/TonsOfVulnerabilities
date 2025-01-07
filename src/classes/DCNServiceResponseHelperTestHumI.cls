/**************************************************************************** 
Apex Class Name  : DCNServiceResponseHelperTestHumI
Version          : 1.0  
Created Date     : September 25, 2015 
Description      : This class is used as test class for DCNServiceResponseHelperTestHumI
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Gargey Sharma              26705                 09/25/2015                    Original Version
***************************************************************************************************/
@isTest
private Class DCNServiceResponseHelperTestHumI
{

    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
     * Method Name : testDCNServiceResponseHelperTestHumI
     * Description : Test method for DCNServiceResponseHelperHumI
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testDCNServiceResponseHelperTestHumI()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Test.setMock(HttpCalloutMock.class, new DCNServiceMockTestHumI());               
        system.runAs(oUser)
        {
          Test.StartTest();
            DCNServiceResponseHelperHumI DCNResDetails = new DCNServiceResponseHelperHumI();
            DCNResDetails.Success = 'true';
            system.assert(DCNResDetails.Success!= null);
          Test.StopTest();
        }
    }  
}