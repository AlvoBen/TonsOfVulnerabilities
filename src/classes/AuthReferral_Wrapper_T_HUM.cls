/**
 * <p>
 * Test class for AuthRefferal_Wrapper_DTO_HUM
 * </p>
 *Apex Class Name  : AuthReferral_Wrapper_T_HUM
 *Version          : 1.0
 *Created Date     : 12/08/2015
 *Function         : Test class for AuthReferral_Wrapper_T_HUM
 *
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                 29042              12/09/2015                 Original Version
 * Sruthi Adivikolanu           29646              01/27/2016                 Renamed AuthRefferal_Wrapper_H_HUM to AuthRefferal_Wrapper_DTO_HUM
 * Santhi Mandava                                  03/26/2016                 Fixed code coverage issue.
 **********************************************************************************************************/
@isTest
private class AuthReferral_Wrapper_T_HUM
{
    /*
     * Method Name : dataSetup
     * Description : This method is used to setup data to use in rest of class
     * Return type : NA
     * Parameter   : NA
     */
    @testSetup static void dataSetup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
    }
    /*
     * Method Name : createRecords
     * Description : This method is used for creating records for wrapper class
     * Return type : NA
     * Parameter   : NA
     */
    testMethod static void createRecords()
    {
        User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('test User1',oUser.LastName);
        
        System.runAs(oUser)
        {   
            Test.startTest();
            AuthRefferal_Wrapper_DTO_HUM obwrapper = new AuthRefferal_Wrapper_DTO_HUM();
             obwrapper.sAuthorizationOrReferralNumber ='1234';
             System.assertEquals('1234',obwrapper.sAuthorizationOrReferralNumber);
             obwrapper.sAuthorizationType = 'ALL';
             System.assertEquals('ALL',obwrapper.sAuthorizationType);
             obwrapper.sOverallStatus  = 'SS'; 
             System.assertEquals('SS',obwrapper.sOverallStatus);            
             obwrapper.sAdmFirstDay  = '22';
             System.assertEquals('22',obwrapper.sAdmFirstDay);
             obwrapper.sDischargeLastDay = '22';
             System.assertEquals('22',obwrapper.sDischargeLastDay);
             obwrapper.sServiceType = 'test';
             System.assertEquals('test',obwrapper.sServiceType);
             obwrapper.sTreatingProvider ='ets';
             System.assertEquals('ets',obwrapper.sTreatingProvider);
             obwrapper.sFacility = 'facility';
             System.assertEquals('facility',obwrapper.sFacility);
             obwrapper.sRequestingrovider = 'tetsrqstprv';
             System.assertEquals('tetsrqstprv',obwrapper.sRequestingrovider);
             obwrapper.sGroupId = '12345';
             System.assertEquals('12345',obwrapper.sGroupId);
             obwrapper.bIsAccessible  =  true;
             System.assertEquals(true, obwrapper.bIsAccessible);
             obwrapper.Totalcount = 1245;
             System.assertEquals(1245, obwrapper.Totalcount);
             obwrapper.recId = '123456789';
             System.assertEquals('123456789', obwrapper.recId);

            Test.stopTest();
        }
    }
}