/***************************************************************************************************************************************************************************
Apex Class Name  : BME_RequirementEventsTriggerTest
Version          : 1.0
Created Date     : Aug 19 2014
Function         : Test Class for BME_RequirementEventsTrigger
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name        Code Review                Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma                                   08/19/2014                   Original Version
****************************************************************************************************************************************************************************/

@isTest
private class BME_RequirementEventsTriggerTest
{
	Static Major_Feature__c feature;
	Static Sprint__c sprint;
	Static Project__c proj;
	
	/*
    * Method name : BME_RequirementEventsTrigger_Insert
    * Description : This method is used to test after insert event forBME_RequirementEventsTrigger.
    * Return Type : Void
    * Parameter   : NA 
    */
    static testMethod void BME_RequirementEventsTrigger_Insert() 
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {	
        	createRecords();
        	Test.StartTest();
        	List<Requirement__c> requirementList = new List<Requirement__c>();
        	for( Integer i = 0 ; i < 200 ; i++)
        	{
	        	Requirement__c req = New Requirement__c(Major_Feature__c = feature.Id, Assigned_Sprint__c = sprint.id,
                                     Project__c = proj.Id, Story_Points__c = 1, EAPM_Application__c = 'CRM Service');
                requirementList.add(req);
        	}
        	if(requirementList != null  && requirementList.size() >0 ){insert requirementList;}
        	Sprint__c spr = [Select Id ,Total_Story_Points__c from Sprint__c where Id = :sprint.Id];
        	Test.StopTest();
        	System.assert(spr.Total_Story_Points__c == 200);
        }
    }
    /*
    * Method name : BME_RequirementEventsTrigger_Delete
    * Description : This method is used to test after delete event forBME_RequirementEventsTrigger.
    * Return Type : Void
    * Parameter   : NA 
    */
    static testMethod void BME_RequirementEventsTrigger_Delete() 
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {	
        	createRecords();
        	
        	List<Requirement__c> requirementList = new List<Requirement__c>();
        	for( Integer i = 0 ; i < 200 ; i++)
        	{
	        	Requirement__c req = New Requirement__c(Major_Feature__c = feature.Id, Assigned_Sprint__c = sprint.id,
                                     Project__c = proj.Id, Story_Points__c = 1, EAPM_Application__c = 'CRM Service');
                requirementList.add(req);
        	}
        	if(requirementList != null  && requirementList.size() >0 ){insert requirementList;}
        	Test.StartTest();
        	List<Requirement__c> requirementDeleteList = new List<Requirement__c>();
        	for( Integer i = 0 ; i < 100 ; i++)
        	{
	        	requirementDeleteList.add(requirementList[i]);
        	}
        	if(requirementDeleteList != null  && requirementDeleteList.size() >0 ){delete requirementDeleteList;}
        	Sprint__c spr = [Select Id ,Total_Story_Points__c from Sprint__c where Id = :sprint.Id];
        	Test.StopTest();
        	System.assert(spr.Total_Story_Points__c == 100);
        }
    }
    /*
    * Method name : BME_RequirementEventsTrigger_Update
    * Description : This method is used to test after update event forBME_RequirementEventsTrigger.
    * Return Type : Void
    * Parameter   : NA 
    */
    static testMethod void BME_RequirementEventsTrigger_Update() 
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {	
        	createRecords();
        	
        	List<Requirement__c> requirementList = new List<Requirement__c>();
        	for( Integer i = 0 ; i < 200 ; i++)
        	{
	        	Requirement__c req = New Requirement__c(Major_Feature__c = feature.Id, Assigned_Sprint__c = sprint.id,
                                     Project__c = proj.Id, Story_Points__c = 1, EAPM_Application__c = 'CRM Service');
                requirementList.add(req);
        	}
        	if(requirementList != null  && requirementList.size() >0 ){insert requirementList;}
        	Test.StartTest();
        	List<Requirement__c> requirementUpdateList = new List<Requirement__c>();
        	for(Requirement__c req : requirementList)
        	{
        		req.Story_Points__c = 2;
	        	requirementUpdateList.add(req);
        	}
        	if(requirementUpdateList != null  && requirementUpdateList.size() >0 ){update requirementUpdateList;}
        	Sprint__c spr = [Select Id ,Total_Story_Points__c from Sprint__c where Id = :sprint.Id];
        	Test.StopTest();
        	System.assert(spr.Total_Story_Points__c == 400);
        }
    }
    /*
    * Method name : createRecords
    * Description : This method is used to create test data
    * Return Type : Void
    * Parameter   : NA 
    */
    static void createRecords()
    {
    	Release__c rel = new Release__c(Name = 'Test');
        	insert rel;
        	
        	Team__c team = new Team__c(Name = 'Test');
        	insert team;
        	
        	sprint = new Sprint__c ( Name = 'Test' , Sprint_End_Date__c = system.Today()+10 , 
        						Sprint_Start_Date__c  = system.Today() , Assigned_Team__c = Team.Id,
        						Release__c = rel.Id);
        	insert sprint;
        	proj = new Project__c( Name = 'Test');
        	insert proj;
        	feature = new Major_Feature__c(Name='Test',Release__c = Proj.Id , 
        							   Release1__c = rel.Id ,  Major_Feature_Estimate_EMs__c = 1  );
        	insert feature;
    }
}