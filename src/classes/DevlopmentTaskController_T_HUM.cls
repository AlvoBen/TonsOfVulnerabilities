/************************************************************************************************************************************************
Apex Class Name  : DevelopmentTaskController_T_HUM
Version          : 1.0
Created Date     : 2018
Function         : Test Class for DevelopmentTaskController_C_HUM.
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Santhosh Ganji                                       11/01/2018                   Original Version
*************************************************************************************************************************************************/
@isTest
private class DevlopmentTaskController_T_HUM{

  Static Major_Feature__c feature;
  Static Project__c proj;
  
  @testSetup static void setUpTestData() 
  {
        User u= HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert u;
        System.assert(u.id != null);
        system.assertEquals(u.lastname=='test User1',true);
        system.runas(u) 
        {
            //Create Test Records
            Release__c rel = new Release__c(Name = 'Test');
            insert rel;
            
            Team__c team = new Team__c(Name = 'Test');
            insert team;
            
            Sprint__c sprint = new Sprint__c ( Name = 'Test' , Sprint_End_Date__c = system.Today()+9 , 
                                Sprint_Start_Date__c  = system.Today()-1 , Assigned_Team__c = Team.Id,
                                Release__c = rel.Id);
            insert sprint;
            
            proj = new Project__c( Name = 'Test');
            insert proj;
            
            feature = new Major_Feature__c(Name='Test',Release__c = Proj.Id , 
                                       Release1__c = rel.Id ,  Major_Feature_Estimate_EMs__c = 1  );
            insert feature;
            
            Requirement__c requirement = New Requirement__c(Major_Feature__c = feature.Id, Assigned_Sprint__c = sprint.id,
                                     Project__c = proj.Id, Story_Points__c = 1, EAPM_Application__c = 'CRM Service');
            insert requirement;
                                     
            Story_Tasks__c storytasks = new Story_Tasks__c ( Task__c= 'Test' , Estimate_Hours__c = 8, Logged_Hours__c = 8, Requirement__c= requirement.id);
            insert storytasks; 
            
            Story_Tasks__History TaskHistory = new Story_Tasks__History ( Parentid = storytasks.id, Field ='Logged_Hours__c');
            //insert TaskHistory;
            
            Sprint__c sprint1 = new Sprint__c ( Name = 'Test1' , Sprint_End_Date__c = system.Today()+5 , 
                                Sprint_Start_Date__c  = system.Today()-1 , Assigned_Team__c = Team.Id,
                                Release__c = rel.Id);
            insert sprint1;
            
            Requirement__c requirement1 = New Requirement__c(Major_Feature__c = feature.Id, Assigned_Sprint__c = sprint1.id,
                                     Project__c = proj.Id, Story_Points__c = 2, EAPM_Application__c = 'CRM Service');
            insert requirement1;
            
            List<Story_Tasks__c> storytask= new List<Story_Tasks__c>();
            for( Integer i = 0 ; i < 2 ; i++)
            {
              Story_Tasks__c storytasks1 = new Story_Tasks__c ( Task__c= 'Test1' , Estimate_Hours__c = 16, Logged_Hours__c = 8, Requirement__c= requirement1.id);
              storytask.add(storytasks1);
            }                         
            if(storytask != null  && storytask.size() >0 ){insert storytask;}
            
            List<Story_Tasks__History> TaskHistory1= new List<Story_Tasks__History>();
            for(Story_Tasks__c tasks: storytask){
               Story_Tasks__History taskhist = new Story_Tasks__History ( Parentid = tasks.id, Field ='Logged_Hours__c');
               TaskHistory1.add(taskhist);
            }
               //insert TaskHistory1;
            
            System.assert(sprint.Name == 'Test');
            System.assert(sprint1.Name == 'Test1');
               
        }
   }
    
    //test 2 weeks Sprint
    static testmethod void testOverAll()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test User1',true);
       
       System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference DevelopmentTaskHistoryTrackingPage = Page.DevelopmentTaskHistoryTracking_VF_HUM;
            Test.setCurrentPage(DevelopmentTaskHistoryTrackingPage); 
            Sprint__c sprint=[Select Name , Sprint_End_Date__c , Sprint_Start_Date__c from Sprint__C WHERE Name = 'Test' LIMIT 1];
            ApexPages.currentPage().getParameters().put('Id',sprint.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),sprint.Id);
            
            DevlopmentTaskController_C_HUM DevlopmentTaskController =  new DevlopmentTaskController_C_HUM(new ApexPages.StandardController(sprint));
            DevlopmentTaskController.getListWrapper();
            Test.StopTest();
         }
     }
     
     //Test 1 week sprint
     static testmethod void testOver()
    {
       User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test User1',true);
       
       System.runAs (oUser) 
        {
            Test.StartTest();
            PageReference DevelopmentTaskHistoryTrackingPage = Page.DevelopmentTaskHistoryTracking_VF_HUM;
            Test.setCurrentPage(DevelopmentTaskHistoryTrackingPage); 
            Sprint__c sprint1=[Select Name , Sprint_End_Date__c , Sprint_Start_Date__c from Sprint__C WHERE Name = 'Test1' LIMIT 1];
            ApexPages.currentPage().getParameters().put('Id',sprint1.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),sprint1.Id);
            
            DevlopmentTaskController_C_HUM DevlopmentTaskController =  new DevlopmentTaskController_C_HUM(new ApexPages.StandardController(sprint1));
            DevlopmentTaskController.getListWrapper();
            Test.StopTest();
         }
     }   
    
}