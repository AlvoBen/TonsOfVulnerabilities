/************************************************************************************************************************
Apex Class Name  : HUMAttachmentRequestDTOTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMAttachmentRequestDTO
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Palak Papneja            18313                    09/30/2014                   Original Version
*************************************************************************************************************************/
@isTest
private class HUMAttachmentRequestDTOTest
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
         HUMAttachmentRequestDTO hum = new HUMAttachmentRequestDTO();
         system.assertEquals(hum.AttachmentKey != null,true);
         Test.stopTest();
      }
  }
}