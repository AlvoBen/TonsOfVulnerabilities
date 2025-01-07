/***************************************************************************************************************************************************************************
Apex Class Name  : StatusReportControllerTest
Version          : 1.0
Created Date     : Aug 18 2014
Function         : Test Class for StatusReportController
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma                             08/18/2014                   Original Version
****************************************************************************************************************************************************************************/

@isTest
private Class StatusReportControllerTest
{
    public static testMethod void StatusReportController(){
        User u= HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {   
            Budget_Status_Mapping__c budgetStatusMapping = new Budget_Status_Mapping__c(
                                                            Name = 'test' ,Image_URL__c = 'test.test' );
            insert budgetStatusMapping;
            
            Project_Status_Mapping__c projectStatusMapping = new Project_Status_Mapping__c (
                                                             Name = 'test' ,Image_URL__c = 'test.test' ); 
            insert projectStatusMapping ;
                                                             
            Resource_Scope_Status_Mapping__c resourcescope = new Resource_Scope_Status_Mapping__c(
                                                             Name = 'test' ,Image_URL__c = 'test.test' ); 
            insert resourcescope ;     
                                                         
            Milestone_Status_Mapping__c milestoneStatusMapping = new Milestone_Status_Mapping__c(
                                                                 Name = 'test',Status__c = 'test');
            insert milestoneStatusMapping ;  
            
            Project__c proj = new Project__c(Name = 'Test');
            insert proj;
                                                               
            Status_Reports__c stReport = new Status_Reports__c (Name = 'stReport',Budget_Status__c = 'test' , Release__c=proj.Id);
            insert stReport ;
            
            Milestone__c mile = new Milestone__c(Release__c=proj.Id ,Target_Completion_Date__c = system.today() + 10 );
            insert mile;
            
            Deliverables__c deliverables = new Deliverables__c(Release__c = proj.Id,Name='test' , Planned_Completion_Date__c = system.today() + 10 );
            insert deliverables ;
            List<String> strList = new List<String>();
            strList.add('All'); 
            Apexpages.StandardController sc = new Apexpages.StandardController(stReport );            
            Test.StartTest();
            StatusReportController statusReprtcntrl = new StatusReportController(sc);
            Pagereference pagerefStep1 = statusReprtcntrl.step1();
            system.assertEquals(pagerefStep1.getURL(), '/apex/statusreportpage1'); 
            Pagereference pagerefStep2 = statusReprtcntrl.step2();
            system.assertEquals(pagerefStep2.getURL(), '/apex/statusreportpage2');
            Pagereference pagerefStep3 = statusReprtcntrl.step3();
            system.assertEquals(pagerefStep3.getURL(), '/apex/statusreportpage3');
            Pagereference pagerefStep4 = statusReprtcntrl.step4();
            system.assertEquals(pagerefStep4.getURL(), '/apex/statusreportpage4');  
            Pagereference pagerefGenerateword = statusReprtcntrl.generateWord(); 
            system.assertEquals(pagerefGenerateword.getURL(), '/apex/generatestatusreportinword'); 
            Pagereference pagerefGeneratepdf =statusReprtcntrl.generatePDF();   
            system.assertEquals(pagerefGeneratepdf.getURL(), '/apex/generatestatusreportinpdf');           
            List<SelectOption> statusVal = statusReprtcntrl.getStatusValues();
            system.assert(statusVal.size() >0 );
            List<SelectOption> proximityVal = statusReprtcntrl.getPriorityValues();
            system.assert(proximityVal.size() >0 );
            List<SelectOption> complexityVal = statusReprtcntrl.getComplexityValues();
            system.assert(complexityVal.size() >0 );
            List<SelectOption> delStatusVal = statusReprtcntrl.getDelStatusValues();  
            system.assert(delStatusVal.size() >0 );
            List<SelectOption> milestatusVal = statusReprtcntrl.getMileStatusValues(); 
            system.assert(milestatusVal.size() >0 );
            statusReprtcntrl.reqIssueStatus = strList;
            statusReprtcntrl.reqIssueComplexity = strList;
            statusReprtcntrl.reqIssuePriority = strList;
            statusReprtcntrl.reqRiskStatus = strList;
            statusReprtcntrl.reqRiskComplexity = strList;
            statusReprtcntrl.reqRiskPriority = strList;
            Pagereference pageRefpopulatePitems = statusReprtcntrl.populatePItems ();
            system.assertEquals(pageRefpopulatePitems,null);
            statusReprtcntrl.reqMilestoneStatus= strList;            
            Pagereference pagerefFetch = statusReprtcntrl.fetchDelNMileStones (); 
            system.assertEquals(pagerefFetch,null);
            statusReprtcntrl.actionFunction ();
            boolean dispDash1 = statusReprtcntrl.getDispDashboard();
            system.assertequals(dispDash1 , false);
            statusReprtcntrl.dashboardURL = 'Test';
            boolean dispDash2 = statusReprtcntrl.getDispDashboard();
            system.assertequals(dispDash2 , true);
            String redStr = statusReprtcntrl.getImageName('redi');
            system.assertequals(redStr, 'ImageRed');
            String yellowStr = statusReprtcntrl.getImageName('yellowi');
            system.assertequals(yellowStr, 'ImageYellow');
            String greenStr = statusReprtcntrl.getImageName('greeni'); 
            system.assertequals(greenStr, 'ImageGreen');
                           
            Test.StopTest();
        }
    }
}