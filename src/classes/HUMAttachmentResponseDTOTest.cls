/************************************************************************************************************************
Apex Class Name  : HUMAttachmentResponseDTOTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMAttachmentResponseDTO
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Palak Papneja             18313                    09/30/2014                   Original Version
* Vishal Verma              18313                    10/06/2014                   Added assert statements.
*************************************************************************************************************************/
@isTest
private class HUMAttachmentResponseDTOTest
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
         HUMAttachmentResponseDTO hum = new HUMAttachmentResponseDTO();
         hum.CaseId = null;
         system.assert(hum.CaseId == null);
         Test.stopTest();
      }
  }
}