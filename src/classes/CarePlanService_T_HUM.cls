/***************************************************************************************************************************************************************************
    Apex Class Name  : CarePlanService_T_HUM
    Version          : 1.0
    Created Date     : Jun 04 2021
    Function         : This Rest Webservice is used to for all the careplan related services.
    ****************************************************************************************************************************************************************************

    Modification Log:

    * Developer Name                  Date                        Description
    *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    * Moshitha Gunasekaran            06/04/2021                  Original Version
    * Moshitha Gunasekaran            08/04/2021                  Made changes to add SubType
    * Prasuna Pattabhi                11/05/2021                  Code Coverage Improvements
****************************************************************************************************************************************************************************/
@isTest
private class CarePlanService_T_HUM {

    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('CarePlanUser1','System Administrator');
       insert oUser;
       User oUser1 = HUMTestDataHelperTest.createTestUser('CarePlanUser2','ETL API Access');
       insert oUser1;
       system.assertEquals(oUser1.lastname=='test CarePlanUser2',true);
       User oUser2 = HUMTestDataHelperTest.createTestUser('CoachUser','Humana Wellness Coach');
       oUser2.FirstName = 'firsttest CoachUser';
       insert oUser2;
       System.runAs(oUser) 
       {
           Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
           insert acc;

           Account acc1 = new Account( Enterprise_Id__c = '223400104', firstname = 'testCarePlan1' ,lastname = 'methodCarePlan1' , Birthdate__c= '01/01/1980',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'KY', PersonHomePhone = '(502) 123-0010', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
           insert acc1;
           Id caseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get('CarePlan').getRecordTypeId();
           Case caseObj1 = HUMTestDataHelperTest.getCase();
            caseObj1.Owner_Queue__c = 'Wellness Humana Wellness Solutions';
            caseObj1.Work_Queue_View_Name__c='Other';
            caseObj1.accountId = acc.Id;
            caseObj1.Interacting_With__c = acc.Id;
            caseObj1.Interacting_With_Name__c = 'Test Name';
            caseobj1.Interacting_With_Type__c = 'Member';
            caseobj1.RecordTypeId = caseRecordType;
            caseobj1.Status = 'In Progress';
            caseobj1.SubType__c = 'Tobacco Support';
            insert caseObj1 ;

            Case caseObj2 = HUMTestDataHelperTest.getCase();
            caseObj2.Owner_Queue__c = 'Wellness Humana Wellness Solutions';
            caseObj2.Work_Queue_View_Name__c='Other';
            caseObj2.accountId = acc.Id;
            caseObj2.Interacting_With__c = acc.Id;
            caseObj2.Interacting_With_Name__c = 'Test Name';
            caseObj2.Interacting_With_Type__c = 'Member';
            caseObj2.RecordTypeId = caseRecordType;
            caseObj2.Type = 'Life Coaching';
            caseObj2.Status = 'Closed';
            caseobj1.SubType__c = 'Life Coach Support';
            insert caseObj2 ;

            HealthCloudGA__CarePlanGoal__c oCarePlanGoal = new HealthCloudGA__CarePlanGoal__c(Name = 'Blood Pressure Check', HealthCloudGA__Progress__c = 10, HealthCloudGA__Category__c = 'Blood Pressure', HealthCloudGA__Description__c = 'Checking blood pressure if maintained', HealthCloudGA__Priority__c = 'High', HealthCloudGA__SortOrder__c = 1, HealthCloudGA__CarePlan__c = caseObj1.Id);
        	insert oCarePlanGoal;
        	
        	HealthCloudGA__CarePlanGoal__c oCarePlanGoal1 = new HealthCloudGA__CarePlanGoal__c(Name = 'Weight Management Check', HealthCloudGA__Progress__c = 20, HealthCloudGA__Category__c = 'Weight Management', HealthCloudGA__Description__c = 'Checking if weight is maintained', HealthCloudGA__Priority__c = 'Normal', HealthCloudGA__SortOrder__c = 2, HealthCloudGA__CarePlan__c = caseObj1.Id);
            insert oCarePlanGoal1;
            
            HealthCloudGA__CarePlanGoal__c oCarePlanGoal2 = new HealthCloudGA__CarePlanGoal__c(Name = 'Blood Pressure Check', HealthCloudGA__Progress__c = 10, HealthCloudGA__Category__c = 'Blood Pressure', HealthCloudGA__Description__c = 'Checking blood pressure if maintained', HealthCloudGA__Priority__c = 'High', HealthCloudGA__SortOrder__c = 1, HealthCloudGA__CarePlan__c = caseObj2.Id);
        	insert oCarePlanGoal2;
             
            Id taskRecordType = Schema.SObjectType.Task.getRecordTypeInfosByName().get('Member Action Item').getRecordTypeId();
            list<Task> lstTasks = new list<Task>();
            Task oMemberTask1 = new Task (RecordTypeId = taskRecordType, WhatId = caseObj1.Id, Subject = 'Goal Check', ActivityDate = system.today(), Status = 'In Progress', Priority = 'High', Description = 'Checking Blood Pressure', HealthCloudGA__CarePlanGoal__c = oCarePlanGoal.Id);
            lstTasks.add(oMemberTask1);
            Task oMemberTask2 = new Task (RecordTypeId = taskRecordType, WhatId = caseObj1.Id, Subject = 'Goal Check', ActivityDate = system.today(), Status = 'In Progress', Priority = 'Low', Description = 'Checking Weight', HealthCloudGA__CarePlanGoal__c = oCarePlanGoal1.Id);
            lstTasks.add(oMemberTask2);
            Task oMemberTask3 = new Task (RecordTypeId = taskRecordType, WhatId = caseObj1.Id, Subject = 'Coach Task', ActivityDate = system.today(), Status = 'In Progress', Priority = 'Low', Description = 'Checking if mesured properly');
            lstTasks.add(oMemberTask3);
            Task oMemberTask4 = new Task (RecordTypeId = taskRecordType, WhatId = caseObj1.Id, Subject = 'Coach Task', ActivityDate = system.today(), Status = 'In Progress', Priority = 'Low', Description = 'Checking Weight');
            lstTasks.add(oMemberTask4);
            Task oMemberTask5 = new Task (RecordTypeId = taskRecordType, WhatId = caseObj2.Id, Subject = 'Goal Check', ActivityDate = system.today(), Status = 'In Progress', Priority = 'High', Description = 'Checking Blood Pressure', HealthCloudGA__CarePlanGoal__c = oCarePlanGoal2.Id);
            lstTasks.add(oMemberTask5);
            insert lstTasks;
       }
    }

    /**
    * This is a test method for Get Care Plans
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCarePlanService_GetCarePlans()
    {
    	User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CarePlanUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test CarePlanUser2',true);
        
        System.runAs(oUser) 
        {
            GetCarePlanRequestDTO getCarePlanRequest = new GetCarePlanRequestDTO();
        	getCarePlanRequest.CarePlanType = 'All';
        	getCarePlanRequest.PersonId = '223300104';
        	
        	
        	RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(getCarePlanRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CarePlanService/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CarePlanService.getCarePlans();
            

            GetCarePlanRequestDTO getCarePlanRequest1 = new GetCarePlanRequestDTO();
        	getCarePlanRequest1.CarePlanType = 'Life Coaching';
        	getCarePlanRequest1.PersonId = '223400104';
        	
        	
        	RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(getCarePlanRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/CarePlanService/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            
            CarePlanService.getCarePlans();
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Get Care Plans for PersonId error
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCarePlanService_GetCarePlansPersonIdError()
    {
    	User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CarePlanUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test CarePlanUser2',true);
        
        System.runAs(oUser) 
        {
            GetCarePlanRequestDTO getCarePlanRequest = new GetCarePlanRequestDTO();
        	getCarePlanRequest.CarePlanType = 'All';
        	getCarePlanRequest.PersonId = '22300104';
        	
        	
        	RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(getCarePlanRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CarePlanService/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CarePlanService.getCarePlans();

            GetCarePlanRequestDTO getCarePlanRequest1 = new GetCarePlanRequestDTO();
        	getCarePlanRequest1.CarePlanType = 'All';
        	getCarePlanRequest1.PersonId = '';
        	
        	
        	RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(getCarePlanRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/CarePlanService/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            
            CarePlanService.getCarePlans();
            Test.stopTest();
        }
    }

    /**
    * This is a test method for Get Care Plans for CarePlanType error
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCarePlanService_GetCarePlansCPTypeError()
    {
    	User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CarePlanUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test CarePlanUser2',true);
        
        System.runAs(oUser) 
        {
            GetCarePlanRequestDTO getCarePlanRequest = new GetCarePlanRequestDTO();
        	getCarePlanRequest.CarePlanType = '';
        	getCarePlanRequest.PersonId = '223300104';
        	
        	
        	RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(getCarePlanRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CarePlanService/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CarePlanService.getCarePlans();
            Test.stopTest();
            
        }
    }

    /**
    * This is a test method for Coaching Enrolled
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCoachingEnrolledService_IsCoachingEnrolled()
    {
    	User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CarePlanUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test CarePlanUser2',true);
        
        System.runAs(oUser) 
        {
            GetCarePlanRequestDTO coachingEnrollRequest = new GetCarePlanRequestDTO();
        	coachingEnrollRequest.PersonId = '223300104';
        	
        	
        	RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(coachingEnrollRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CoachingEnrolledService/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CoachingEnrolledService.IsCoachingEnrolled();
            Test.stopTest();
        }
    }

    /**
    * This is a test method for CoachingEnrolled for PersonId error
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCoachingEnrolledService_IsCoachingEnrolledError()
    {
    	User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test CarePlanUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test CarePlanUser2',true);
        
        System.runAs(oUser) 
        {
            GetCarePlanRequestDTO coachingEnrollRequest = new GetCarePlanRequestDTO();
        	coachingEnrollRequest.PersonId = '22300104';
        	
        	
        	RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(coachingEnrollRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CoachingEnrolledService/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CoachingEnrolledService.IsCoachingEnrolled();

            GetCarePlanRequestDTO coachingEnrollRequest1 = new GetCarePlanRequestDTO();
        	coachingEnrollRequest1.PersonId = '';
        	
        	
        	RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(coachingEnrollRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/CoachingEnrolledService/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            
            CoachingEnrolledService.IsCoachingEnrolled();
            Test.stopTest();
        }
    }

    /**
    * This Is a test method for Exception Scenarios
    *
    * @param  None      
    * @return  Void
    */

    Static testMethod void testCarePlanService_Exception()
    {
    	User oUser = [SELECT Id, lastname FROM User  WHERE Lastname='test CarePlanUser2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test CarePlanUser2',true);
        
        System.runAs(oUser) 
        {
            RestRequest req = New RestRequest();
            RestResponse res = New RestResponse();
            
            req.requestURI = '/CarePlanService/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof('null');            
           
            RestContext.request = req;
            RestContext.response= res;
			
            RestRequest req1 = New RestRequest();            
            RestResponse res1 = New RestResponse();
        
            req1.requestURI = '/CoachingEnrolledService/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof('null');
                        
            Test.startTest();
            CarePlanService.getCarePlans(); 
            RestContext.request = req1;
            RestContext.response= res1;
            CoachingEnrolledService.IsCoachingEnrolled();
            Test.stopTest();
        }
    }
}