/************************************************************************************************************************
Apex Class Name  : HUMAVFTemplateResponseHelperTest
Version          : 1.0
Created Date     : May 8 2015
Function         : Test class for HUMAVFTemplateResponseHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam          23024                    05/08/2014                   Original Version
*************************************************************************************************************************/
@isTest
private class HUMAVFTemplateResponseHelperTest
{        
   /*    
    * Method Name : testHUMAVFTemplateRequestHelper    
    * Description : Test method for the response helper  
    * Return type : void    
    * Paramater   : NA    
    */    
    static testMethod void testHUMAVFTemplateResponseHelper()    
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.id!=null,true);  
        system.runAs(u) 
        {
            Test.startTest();
            HUMAVFTemplateResponseHelper res = new HUMAVFTemplateResponseHelper();
            res.sErrorCode = '0000';
            res.sErrorDescription = 'Success';
            system.assertEquals(res.sErrorCode!=null,true); 
            Test.stopTest();
        }
    }
}