/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailCreateEvent_T_HUM 
* Version          : 1.0
* Created Date     : 7th June, 2021
* Function         : This class is used as a test class for create event rest service
* Test Class       : 
***********************************************************************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Isha Gupta                                                         06/7/2021            Original Version 2361161
* Akshay Pai                                                         08/31/2021           Test coverrage fix
**********************************************************************************************************************************/
@isTest
public class CRMRetailCreateEvent_T_HUM {
    
     /**
    * This method is used to insert the data only once and called automatically in all methods 
    *
    * @param  None      
    * @return  Void
    */
    
    @testSetup static void setup()
    {
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','ETL API Access');
        insert oUser;
        System.assertNotEquals(oUser, null);
        
        System.runAs(oUser) 
        {
            List<Storefront_I_Type__c> lstIntType = new List<Storefront_I_Type__c>();
            List<Storefront_I_Reason__c> lstIntReason = new List<Storefront_I_Reason__c>();
            List<Storefront_Location__c> lstIntLoc = new List<Storefront_Location__c>();
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu');
            lstIntType.add(objIntType);
            
            Storefront_I_Type__c objIntTypeWithoutReason = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu1');
            lstIntType.add(objIntTypeWithoutReason);
            
            insert lstIntType;
            System.assertEquals(lstIntType.size(),2);
            
            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('consumer test',null);
            objIntReason.Interaction_Type__c = lstIntType[0].Id;
            lstIntReason.add(objIntReason);
            insert lstIntReason;
            System.assertEquals(lstIntReason.size(),1);
            
            Storefront_Location__c objLocation = CRMRetail_TestDataFactory_H_HUM.createLocation();
            lstIntLoc.add(objLocation);
            insert lstIntLoc;
            System.assertEquals(lstIntLoc.size(),1);
        }
    }
    
     /**
    * This method is used to test the creation of events based on repeats for monthly basis.
    *
    * @param  None      
    * @return  Void
    */
    
    @isTest static void createEventMonthlyOnRepeat()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '21,31';
            request.numberOfRepeats = 3;
            request.repeatEndDate  ='';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();

        }        
    } 
    
    /**
    * This method is used to test the creation of events based on end date for monthly basis.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventMonthlyOnEndDate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
           
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '21,31';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-06-28';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();

        }        
    } 
    
    /**
    * This method is used to test the creation of events based on end date for weekly basis.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventWEEKLYOnEndDate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'WEEKLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = 'MON,THU';
            request.repeatMonthDates = '';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-06-28';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();
        }
        
    } 
    
      
    /**
    * This method is used to test the creation of events based on number of repeats for weekly basis.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventWeeklyOnRepeat()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
           
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'WEEKLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = 'MON,SUN';
            request.repeatMonthDates = '';
            request.numberOfRepeats = 3;
            request.repeatEndDate  ='';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();

        }        
    } 
    
      
    /**
    * This method is used to test the creation of events based on end date for daily basis.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventDailyOnEndDate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'DAILY';
            request.frequecyInterval = 2;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-06-28';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();
        }
        
    } 
    
     /**
    * This method is used to test the creation of events based on number of repeats for daily basis.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventDailyOnRepeat()
     {
         User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
         system.assertEquals(oUser.Name,'test User1');
         
         system.runAs(oUser)
         {
             
             List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
             System.assert(lstIntReason.size() == 1);
             
             CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
             request.sourceSystem = 'VNC';
             request.intReasonId = lstIntReason[0].id;
             request.relatedToLocation = 'Virginia'; 
             request.startDateTime = '2021-06-16 21:30:00';
             request.endDateTime = '2021-06-16 22:30:00';
             request.isAllDayEvent = false;
             request.facilitator = 'Dr. Lee';
             request.guestSpeaker = 'Dr. Wee';
             request.performance = 'Good';
             request.feedback = 'Great';
             request.isRepeat = true;
             request.frequency = 'DAILY';
             request.frequecyInterval = 1;
             request.repeatWeekDays = '';
             request.repeatMonthDates = '21,31';
             request.numberOfRepeats = 3;
             request.repeatEndDate  ='';
             
             RestRequest req = new RestRequest();
             String JSONMsg = System.JSON.serialize(request);
             
             RestResponse res = new RestResponse();
             req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
             req.httpMethod = 'POST';
             req.requestBody = Blob.valueof(JSONMsg);
             
             RestContext.request = req;
             RestContext.response= res;
             
             Test.startTest();
             
             CRMRetailCreateEvent_I_HUM.createNewEvent();
             RestResponse response = RestContext.response;
             CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
             createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
             System.assertEquals(createEvtRes.isSuccess, true);
             
             Test.StopTest();
         }
        
    } 
    
     /**
    * This method is used to test the validation of data.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventCheckValidDataCase1()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '21,31';
            request.numberOfRepeats = 100;
            request.repeatEndDate  ='2021-06-28';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();

        }        
    } 
    
    /**
    * This method is used to test the validation of data.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventCheckValidDataCase2()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'WEEKLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '21,31';
            request.numberOfRepeats = 200;
            request.repeatEndDate  ='2021-06-28';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();

        }        
    } 
    
    /**
    * This method is used to test creation of events monthly for different events.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventMonthlyOnEndDateCase1()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '21,31';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-08-28';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();

        }        
    } 
    
    /**
    * This method is used to test the creation of events for months having 30 days.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventMonthlyOnRepeatCase1()
     {
         User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
         system.assertEquals(oUser.Name,'test User1');
         
         system.runAs(oUser)
         {
            
             List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
             System.assert(lstIntReason.size() == 1);
             
             CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
             request.sourceSystem = 'VNC';
             request.intReasonId = lstIntReason[0].id;
             request.relatedToLocation = 'Virginia'; 
             request.startDateTime = '2021-06-16 21:30:00';
             request.endDateTime = '2021-06-16 22:30:00';
             request.isAllDayEvent = false;
             request.facilitator = 'Dr. Lee';
             request.guestSpeaker = 'Dr. Wee';
             request.performance = 'Good';
             request.feedback = 'Great';
             request.isRepeat = true;
             request.frequency = 'MONTHLY';
             request.frequecyInterval = null;
             request.repeatWeekDays = '';
             request.repeatMonthDates = '21,31';
             request.numberOfRepeats = 20;
             request.repeatEndDate  ='2021-09-28';
             
             RestRequest req = new RestRequest();
             String JSONMsg = System.JSON.serialize(request);
             
             RestResponse res = new RestResponse();
             req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
             req.httpMethod = 'POST';
             req.requestBody = Blob.valueof(JSONMsg);
             
             RestContext.request = req;
             RestContext.response= res;
             
             Test.startTest();
             
             CRMRetailCreateEvent_I_HUM.createNewEvent();
             RestResponse response = RestContext.response;
             CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
             createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
             System.assertEquals(createEvtRes.isSuccess, true);
             
             Test.StopTest();

         }        
    } 
    
      /**
    * This method is used to test the valid frequency entry.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventWithoutFrequency()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = false;
            request.frequency = '';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();

        }        
    } 
    
      /**
    * This method is used to test for invalid data.
    *
    * @param  None      
    * @return  Void
    */
    
    @isTest static void createEventWithInvalidData()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'DAILY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();

        }        
    } 
    
     /**
    * This method is used to test when no event is created.
    *
    * @param  None      
    * @return  Void
    */
    
    @isTest static void NoEventCreation()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '23';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-06-20';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();

        }
    }
    
     /**
    * This method is used to test for bad data entry.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void VerifyBadRequest()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '23';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-06-20';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();

        }        
    } 
    
     /**
    * This method is used to test for Exception condition.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void CheckException()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '23';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-06-20';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();

        }
    } 
    
     /**
    * This method is used to test when no location is found .
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void NoLocationFoundCheck()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginiaa'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '23';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2021-06-20';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();

        }
    }
    
     /**
    * This method is used to test the creation of events for startdate falling in december.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void createEventMonthlyOnEndDateRepeatForDec()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-12-16 21:30:00';
            request.endDateTime = '2021-12-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '25';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2022-01-12';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, true);
            
            Test.StopTest();

        }
    } 
    
     /**
    * This method is used to test the location extraction from metadata.
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void checkMetadataForLocation()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = ''; 
            request.startDateTime = '2021-12-16 21:30:00';
            request.endDateTime = '2021-12-16 22:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '25';
            request.numberOfRepeats = null;
            request.repeatEndDate  ='2022-01-12';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
            Test.StopTest();
        }
        
    } 
	
	 /**
    * This method is used to test all salesforce validations checks
    * @param  None      
    * @return  Void
    */
    
     @isTest static void allValidationsChecks()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {   
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.intReasonId = lstIntReason[0].id;
            request.relatedToLocation = 'Virginia'; 
            request.startDateTime = '2021-06-16 21:30:00';
            request.endDateTime = '2021-06-16 20:30:00';
            request.isAllDayEvent = false;
            request.facilitator = 'Dr. Lee';
            request.guestSpeaker = 'Dr. Wee';
            request.performance = 'Good';
            request.feedback = 'Great';
            request.isRepeat = true;
            request.frequency = 'MONTHLY';
            request.frequecyInterval = null;
            request.repeatWeekDays = '';
            request.repeatMonthDates = '21,31';
            request.numberOfRepeats = 3;
            request.repeatEndDate  ='';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateEvent'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateEvent_I_HUM.createNewEvent();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM createEvtRes = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM();
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
            
			//Frequency check
			request.endDateTime = '2021-06-16 22:30:00';
			request.frequency = 'YEARLY';
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//Frequency check
			request.endDateTime = '2021-06-16 22:30:00';
			request.frequency = 'YEARLY';
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//blank week days check
			request.frequency = 'WEEKLY';
			request.repeatWeekDays = '';
			
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//All week days check
			request.repeatWeekDays = 'SUN,MON,TUE,WED,THU,FRI,SAT';
			
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//invalid week days check
			request.repeatWeekDays = 'SON';
			
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//blank month days check
			request.frequency = 'MONTHLY';
			request.repeatMonthDates = '';
			
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//invalid month days check
			request.repeatMonthDates = '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32';
			
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//invalid month days check
			request.repeatMonthDates = '0';
			
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//invalid end dates checks
			request.repeatMonthDates = '10';
			request.numberOfRepeats = null;
            request.repeatEndDate  ='';
			
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);

			//max DAILY limits checks
			request.frequency = 'DAILY';
			request.numberOfRepeats = 0;
            
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//max WEEKLY limits checks
			request.frequency = 'WEEKLY';
			request.numberOfRepeats = 0;
            
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
			//max MONTHLY limits checks
			request.frequency = 'MONTHLY';
			request.numberOfRepeats = 0;
            
			JSONMsg = System.JSON.serialize(request);
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			
			CRMRetailCreateEvent_I_HUM.createNewEvent();
            response = RestContext.response;
            createEvtRes = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(),CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateEventResponse_DTO_HUM.class);
            System.assertEquals(createEvtRes.isSuccess, false);
			
            Test.StopTest();
		
        }
    } 
}