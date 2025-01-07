/************************************************************************************************************************
Apex Class Name  : HUMCaseUpdateLogAttachmentHelperTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMCaseUpdateLogAttachmentHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma              20294                   01/15/2015                   Original Version
*************************************************************************************************************************/
@isTest
private class HUMCaseUpdateLogAttachmentHelperTest
{        
    
    static testMethod void testCaseUpdate()    
    {        
       HUMTestDataHelperTest.getHUMConstantsData();                
       User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
       insert u;        
       system.assertEquals(u.id!=null,true);                
       System.runAs(u)        
       {            
         Test.startTest();
         HUMCaseUpdateLogAttachmentHelper hum= new HUMCaseUpdateLogAttachmentHelper();
         hum.sCaseNumber = null;
         system.assert(hum.sCaseNumber == null);
         Test.stopTest();
       }
     }
}