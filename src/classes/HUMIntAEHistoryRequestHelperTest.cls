/************************************************************************************************************************
Apex Class Name  : HUMIntAEHistoryRequestHelperTest
Version          : 1.0
Created Date     : Aug 09 2014
Function         : Test class for HUMIntAEHistoryRequestHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth               17165                08/09/2014                 Original Version
*************************************************************************************************************************/
@isTest
private class HUMIntAEHistoryRequestHelperTest
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
         HUMIntAEHistoryRequestHelper hum= new HUMIntAEHistoryRequestHelper();
         HUMIntAEHistoryRequestHelper.Request_element req= new HUMIntAEHistoryRequestHelper.Request_element();
         system.assertEquals(req.SSN!=null,false);
         Test.stopTest();
         
                  
       }
     }
}