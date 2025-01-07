/************************************************************************************************************************
Apex Class Name  : HUMCaseUpdateRequestHelperTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMCaseUpdateRequestHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Palak Papneja             18313                   09/30/2014                   Original Version
*************************************************************************************************************************/
@isTest
private class HUMCaseUpdateRequestHelperTest
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
         HUMCaseUpdateRequestHelper hum= new HUMCaseUpdateRequestHelper();
         hum.sCaseNumber = null;
         system.assert(hum.sCaseNumber == null);
         Test.stopTest();
       }
     }
}