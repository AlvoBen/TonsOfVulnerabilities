/***************************************************************************************************
Apex Class Name  : HUMCaseActionQuestionHelperTest
Version          : 1.0
Created Date     : August 20 2014
Function         : Teat Class for HUMCaseActionQuestionHelper
**************************************************************************************************
Modification Log:

* Developer Name     Review Number          Date                        Description
*--------------------------------------------------------------------------------------------------
* Pradeep Raju        17875               08/20/2014                     Original Version
* Vishal Verma        18313               10/06/2014                     Added assert statements.
***************************************************************************************************/

@isTest
private class HUMCaseActionQuestionHelperTest
{
    
    /*
    * Method Name : testCaseCommentHelper
    * Description : Test method to test Case Comment Helper
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testCaseCommentHelper()
    {
    	Test.startTest();
	       HUMCaseActionQuestionHelper hum = new HUMCaseActionQuestionHelper();
	       hum.sValue = null;
	       system.assert(hum.sValue == null);
	    Test.StopTest();
    }
}