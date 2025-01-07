/***************************************************************************************************************************************************************************
Apex Class Name  : Release1_Charts_GanttControllerTest
Version          : 1.0
Created Date     : Aug 18 2014
Function         : Test class for Release1_Charts_GanttController
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma                                     08/18/2014                  Original Version
* Santhi Mandava        17534                      08/26/2014                  Fixed code review defect. 
****************************************************************************************************************************************************************************/

@isTest
private Class Release1_Charts_GanttControllerTest
{
    /*
    * Method name : Release1_Charts_GanttController
    * Description : This method is used to test Release1_Charts_GanttController.
    * Return Type : Void
    * Parameter   : NA 
    */
    private static testMethod void Release1_Charts_GanttController()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Deployment');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            Date dStartDate=date.today();
            Date dEndDate=date.today().addDays(1);
            
            Release__c objRelease=new Release__c(Name='Sample Release');
            insert  objRelease;  
            System.assert(objRelease.id != null);
            
            Project__c objproj = new Project__c( Name = 'Sample Project',Start_date__c=dStartDate,End_date__c=dEndDate,Complete__c=false);
            objproj.Release__c= objRelease.Id;           
            insert objproj; 
            System.assert(objproj.id != null);
            
            Project__c objsecproj = new Project__c( Name = 'Sample Project',Start_date__c=null,End_date__c=null,Complete__c=true);
            objsecproj.Release__c= objRelease.Id;           
            insert objsecproj;        
            System.assert(objsecproj.id != null);
                        
            Test.StartTest();

            Release1_Charts_GanttController  orelease = new Release1_Charts_GanttController();
            orelease.getProjectGanttJson();
            orelease.mytarget =objRelease.Id;
            orelease.mytarget =objproj.Id;
            orelease.mytarget =objsecproj.Id;
            System.assertNotEquals(orelease,NULL);    
            Test.StopTest();    
        }
    }
}