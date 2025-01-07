/************************************************************************************************************************
Apex Class Name  : HUMAttachmentItemHelperTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMAttachmentItemHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Palak Papneja              18313                  09/30/2014                   Original Version
* Palak Papneja              18313                  10/06/2014                   Added assert statement
*************************************************************************************************************************/
@isTest
private class HUMAttachmentItemHelperTest
{        
  static testMethod void testAlertsFromSfdc()        
  {               
      HUMTestDataHelperTest.getHUMConstantsData();                       
      User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');               
      insert u;               
      system.assertEquals(u.id != null,true);                       
      System.runAs(u)               
      {                     
         Test.startTest();
         HUMAttachmentItemHelper hum = new HUMAttachmentItemHelper();
         system.assertEquals(hum.AttachmentKey != null,true);     
         Test.stopTest();
      }
  }
}