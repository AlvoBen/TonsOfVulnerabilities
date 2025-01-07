/************************************************************************************************************************
Apex Class Name  : HUMAttachmentItemAttributeHelperTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMAttachmentItemAttributeHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Palak Papneja               18313                 09/30/2014                Original Version
*************************************************************************************************************************/
@isTest
private class HUMAttachmentItemAttributeHelperTest
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
         HUMAttachmentItemAttributeHelper hum =new HUMAttachmentItemAttributeHelper();
         system.assertEquals(hum.AttachmentLog!=null,true);
         Test.stopTest();
      }
  }
}