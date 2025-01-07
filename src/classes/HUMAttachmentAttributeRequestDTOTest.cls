/************************************************************************************************************************
Apex Class Name  : HUMAttachmentAttributeRequestDTOTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMAttachmentAttributeRequestDTO
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Palak Papneja             18313                   09/30/2014                   Original Version
* Palak Papneja             18313                   10/06/2014                   Made changes according to Humana standards
*************************************************************************************************************************/
@isTest
private class HUMAttachmentAttributeRequestDTOTest
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
         HUMAttachmentAttributeRequestDTO hum =new HUMAttachmentAttributeRequestDTO();
         system.assertEquals(hum.AttachmentName!=null, true);
         Test.stopTest();
      }
  }
}