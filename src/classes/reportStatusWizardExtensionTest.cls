/***************************************************************************************************************************************************************************
Apex Class Name  : reportStatusWizardExtensionTest
Version          : 1.0
Created Date     : Aug 18 2014
Function         : Test Class for reportStatusWizardExtension
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma                             08/18/2014                   Original Version
****************************************************************************************************************************************************************************/

@isTest
private Class reportStatusWizardExtensionTest
{
    /*
    * Method name : reportStatusWizardExtension
    * Description : This method is used to test reportStatusWizardExtension.
    * Return Type : Void
    * Parameter   : NA 
    */
    public static testMethod void reportStatusWizardExtension()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
	        Project__c proj = new Project__c( Name = 'proj');
	        insert proj;        
	        System.assert(proj.id != null);
	        Test.StartTest();
	        ApexPages.StandardController sc = new ApexPages.StandardController(proj);
	        reportStatusWizardExtension reportStatusWizard = new reportStatusWizardExtension(sc);
	        Pagereference pagerefNext = reportStatusWizard.next(); 
	        system.assertEquals(pagerefNext.getURL(), '/apex/reportstatus_issues_risks_openitems'); 
	        Test.StopTest();    
        }

    }
}