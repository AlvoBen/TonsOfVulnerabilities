/**************************************************************************** 
Apex Class Name  : HUMCIMedicareMemberResponseHelperTest
Version          : 1.0  
Created Date     : December 05 2014 
Description      : This class is used as test class for HUMCIMedicareMemberResponseHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri       19768                 12/05/2014                     Original Version
* Pradeep Veeravali           20628                 01/28/2015                     added code to test method to cover newly added error class  
***************************************************************************************************/
@isTest
private Class HUMCIMedicareMemberResponseHelperTest
{

    /*
     * Method Name : testHUMCIMedicareMemberResponseHelper
     * Description : Test method for HUMCIMedicareMemberResponseHelper
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testHUMCIMedicareMemberResponseHelper()
    {
        
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assert(u.Id != null);
        system.runAs(u)
        {
          Test.StartTest();
            HUMCIMedicareMemberResponseHelper CIMedicareMemberRes = new HUMCIMedicareMemberResponseHelper();
            List<HUMCIMedicareMemberResponseHelper.Error> lstError = new List<HUMCIMedicareMemberResponseHelper.Error>();
            HUMCIMedicareMemberResponseHelper.Error error = new HUMCIMedicareMemberResponseHelper.Error();
            lstError.add(error);
            CIMedicareMemberRes.Errors = lstError; 
            CIMedicareMemberRes.sLISIndicator  = 'Y';
            system.assert(CIMedicareMemberRes.sLISIndicator != null);
          Test.StopTest();
        }
    }  
}