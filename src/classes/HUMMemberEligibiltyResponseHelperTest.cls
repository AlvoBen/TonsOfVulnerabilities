/************************************************************************************************************************
Apex Class Name  : HUMMemberEligibiltyResponseHelperTest
Version          : 1.0
Created Date     : October 6 2014
Function         : Test class for HUMMemberEligibiltyResponseHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Vishal Verma              18335                    10/06/2014                   Original Version
*************************************************************************************************************************/
@isTest
private class HUMMemberEligibiltyResponseHelperTest
{        
  static testMethod void testAlertsFromSfdc()        
  {               
      HUMTestDataHelperTest.getHUMConstantsData();                       
      User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');               
      insert u;               
      system.assertEquals(u.id!=null,true);                       
      System.runAs(u)               
      {                     
         Test.startTest();
         HUMMemberEligibiltyResponseHelper hum =new HUMMemberEligibiltyResponseHelper();
         hum.sErrorCode = null;
         system.assert(hum.sErrorCode == null);
         Test.stopTest();
      }
  }
}