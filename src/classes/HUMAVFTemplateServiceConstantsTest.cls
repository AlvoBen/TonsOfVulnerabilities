/************************************************************************************************************************
Apex Class Name  : HUMAVFTemplateServiceConstantsTest
Version          : 1.0
Created Date     : May 15 2015
Function         : Test class for HUMAVFTemplateServiceConstants
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam          23024                  05/15/2015                   Original Version
*************************************************************************************************************************/
@isTest
private class HUMAVFTemplateServiceConstantsTest
{        
   /*    
    * Method Name : testHUMAVFTemplateServiceConstants    
    * Description : Test method for the AVF template constants.  
    * Return type : void    
    * Paramater   : NA    
    */    
    static testMethod void testHUMAVFTemplateServiceConstants()    
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.Id != null);
        system.runAs(u) 
        {
        	Test.startTest();
            HUMAVFTemplateServiceConstants avfConstant = new HUMAVFTemplateServiceConstants();

			System.assert(HUMAVFTemplateServiceConstants.HUM_TEXT_E2EACTIVITYID != null);
			System.assert(HUMAVFTemplateServiceConstants.HUM_TEXT_E2EMILESTONEID != null);
			System.assert(HUMAVFTemplateServiceConstants.HUM_TEXT_E2ETRACKID != null);
			System.assert(HUMAVFTemplateServiceConstants.HUM_TEXT_CASENUMBER != null);
			System.assert(HUMAVFTemplateServiceConstants.HUM_TEXT_MEMBERUMID != null);
			Test.stopTest();
        }
    }
}