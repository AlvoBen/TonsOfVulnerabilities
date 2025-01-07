/***************************************************************************************************
Apex Class Name  : HUMCaseTemplateHelperTest
Version          : 1.0
Created Date     : August 20 2014
Function         : Test Class for HUMCaseTemplateHelper 
**************************************************************************************************
Modification Log:

* Developer Name        Code Review                Date                        Description
*--------------------------------------------------------------------------------------------------
* Pradeep Raju          17429                     08/20/2014                     Original Version
* Vishal Verma          18313                     10/06/2014                     Updated Code review numbers and modification log
***************************************************************************************************/

@isTest
private class HUMCaseTemplateHelperTest
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
            HUMCaseTemplateHelper hum = new HUMCaseTemplateHelper();
            hum.sDatatype = null;
            system.assert(hum.sDatatype == null);
        Test.StopTest();
    }
}