/***************************************************************************************************************************************************************************
Apex Class Name  : ReleaseChartsControllerTest
Version          : 1.0
Created Date     : Aug 18 2014
Function         : Test Class for ReleaseChartsController
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma                             08/18/2014                   Original Version
****************************************************************************************************************************************************************************/

@isTest
private Class ReleaseChartsControllerTest
{
    /*
    * Method name : ReleaseChartsController
    * Description : This method is used to test ReleaseChartsController.
    * Return Type : Void
    * Parameter   : NA 
    */
    public static testMethod void ReleaseChartsController()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
	        Project__c proj = new Project__c( Name = 'proj');
	        insert proj;        
	        System.assert(proj.id != null);
	        Deliverables__c deliverables = new Deliverables__c(Release__c = proj.Id,Name='test' , Planned_Completion_Date__c = system.today() + 10 );
	        insert deliverables ;
	        Test.StartTest();
	        ApexPages.StandardController sc = new ApexPages.StandardController(proj);
	        ReleaseChartsController releaseChartsCntrl = new ReleaseChartsController(sc);
	        system.assertEquals(releaseChartsCntrl.display,true);
	        Test.StopTest();    
        }

    }
}