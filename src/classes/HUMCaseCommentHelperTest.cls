/***************************************************************************************************
Apex Class Name  : HUMCaseCommentHelperTest
Version          : 1.0
Created Date     : August 20 2014
Function         : Teat Class for HUMCaseCommentHelper
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Pradeep Raju         17395                    08/20/2014                     Original Version
* Vishal Verma         18313                    10/06/2014                     Added Assert Statements
***************************************************************************************************/

@isTest
private class HUMCaseCommentHelperTest
{
    
    /*
    * Method Name : testCaseCommentHelper
    * Description : Test method to test Case Comment Helper
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testCaseCommentHelper()
    {
    	Test.StartTest();
	        HUMCaseCommentHelper hum = new HUMCaseCommentHelper();
	        hum.CreatedDate = null;
	        system.assert(hum.CreatedDate == null);
        Test.StopTest();
    }
}