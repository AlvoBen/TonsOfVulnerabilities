/******************************************************************************************************************
Apex class Name    : CRMRetailUpdateEvent_T_HUM
Version            : 1.0
Function           : This class is used as test class for update Event Rest Service
Created On         : June 10 ,2021
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Sahil Verma                                    06/10/2021            Original Version 2361199
*   2.0			   Ananya									      09/02/2021		    Test Coverage Fix
------------------------------------------------------------------------------------------------------------------- */
@isTest
private class CRMRetailUpdateEvent_T_HUM 
{
    /**
	* This method is used to insert the data only once and called automatically in all methods 
	*
	* @param  None      
	* @return  Void
	*/
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User2','ETL API Access');
        insert oUser;
        System.assertNotEquals(oUser, null);
        
        System.runAs(oUser) 
        {
            list<Event> eventlist = new list<Event>();
            list<Storefront_I_Reason__c> intReasonList = new list<Storefront_I_Reason__c>();
            list<Storefront_I_Type__c> intTypeList = new list<Storefront_I_Type__c>();
            list<Storefront_Location__C> locationList = new list<Storefront_Location__C>();
            
            Storefront_I_Type__c intType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Social');
            intTypeList.add(intType);
            Storefront_I_Type__c intType2 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Technology');
            intTypeList.add(intType2);
            insert intTypeList;
            System.assertEquals(intTypeList.size(), 2);
            
            Storefront_I_Reason__c intReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Event-Poverty',null);
            intReason.Interaction_Type__c = intTypeList[0].Id;
            intReasonList.add(intReason);
            Storefront_I_Reason__c intReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Event-Science',null);
            intReason2.Interaction_Type__c = intTypeList[1].Id;
            intReasonList.add(intReason2);
            insert intReasonList;
            System.assertEquals(intReasonList.size(), 2);
            
            Storefront_Location__C location = CRMRetail_TestDataFactory_H_HUM.createLocation('Virginia','Street no. 1','Texas','12345','83684','california',23);
            locationList.add(location);
            Storefront_Location__C location2 = CRMRetail_TestDataFactory_H_HUM.createLocation('Atlanta','Street no. 2','Delhi','67890','12345','LA',25);
            locationList.add(location2);
            insert locationList;
            System.assertEquals(locationList.size(), 2);
            
            Event eventWithoutInteraction = CRMRetail_TestDataFactory_H_HUM.createEvent(intReasonList[0].id,locationList[0].id,Datetime.now(), Datetime.now().addHours(1),null,false,'',0);
            eventWithoutInteraction.Facilitator__c = 'Steve';
            eventWithoutInteraction.Guest_Speaker__c = 'Smith';
            eventWithoutInteraction.Performance__c = 'Very Good';
            eventWithoutInteraction.Feedback__c = 'Could be better';
            eventlist.add(eventWithoutInteraction);
            insert eventlist;
            System.assertEquals(eventlist.size(), 1);
        }
    }
    
    @isTest static void testEventExist()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

            list<Storefront_I_Reason__c> lstReason = [Select Id, Name FROM Storefront_I_Reason__c Where Name = 'Event-Science' LIMIT 1];
            System.assertEquals(lstReason.size(), 1);
            list<Storefront_Location__C> listLocation = [Select Id, Name from Storefront_Location__C Where Name ='Atlanta' LIMIT 1];
            System.assertEquals(listLocation.size(), 1);
            list<Event> lstEvent = [SELECT Id, Facilitator__c, Guest_Speaker__c, Performance__c, Feedback__c, StartDateTime, EndDateTime, WhatId, Reason__c FROM Event Where isRecurrence = false];
            System.assertEquals(lstEvent.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.sfEventId = lstEvent[0].Id;
            request.intReasonId = lstReason[0].id;
            request.relatedToLocation = listLocation[0].Name;
            request.startDateTime = String.valueOf(Datetime.now().addDays(2));
            request.endDateTime = String.valueOf(Datetime.now().addDays(2).addHours(1));
            request.facilitator = 'Luke';
            request.guestSpeaker = 'Luke';
            request.performance = 'outstanding';
            request.feedback = 'Excellent';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UpdateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateEvent_I_HUM.updateEvent();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM.class);
            
            system.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();
        }
    }
	
    @isTest static void testIncorrectSourceSystem()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            list<Storefront_I_Reason__c> lstReason = [Select Id, Name FROM Storefront_I_Reason__c Where Name = 'Event-Science' LIMIT 1];
            System.assertEquals(lstReason.size(), 1);
            
            list<Event> lstEvent = [SELECT Id, Facilitator__c, Guest_Speaker__c, Performance__c, Feedback__c, StartDateTime, EndDateTime, WhatId, Reason__c FROM Event Where isRecurrence = false];
            System.assertEquals(lstEvent.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM();
            request.sourceSystem = 'Tulsa';
            request.sfEventId = lstEvent[0].Id;
            request.intReasonId = lstReason[0].id;
            request.relatedToLocation = '';
            request.startDateTime = String.valueOf(Datetime.now().addDays(2));
            request.endDateTime = String.valueOf(Datetime.now().addDays(2).addHours(1));
            request.facilitator = 'Luke';
            request.guestSpeaker = 'Luke';
            request.performance = 'outstanding';
            request.feedback = 'Excellent';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UpdateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateEvent_I_HUM.updateEvent();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM.class);
            
            system.assertEquals(res1.isSuccess, false); 
            
            Test.StopTest();
        }
    }
    
    @isTest static void testCustomMetadata()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

            list<Event> lstEvent = [SELECT Id, Facilitator__c, Guest_Speaker__c, Performance__c, Feedback__c, StartDateTime, EndDateTime, WhatId, Reason__c FROM Event Where isRecurrence = false];
            System.assertEquals(lstEvent.size(), 1);
            list<Storefront_I_Reason__c> lstReason = [Select Id, Name FROM Storefront_I_Reason__c Where Name = 'Event-Science' LIMIT 1];
            System.assertEquals(lstReason.size(), 1);
            List<CRMRetail_DefLoc__mdt> mdt = [SELECT Location_Name__c FROM CRMRetail_DefLoc__mdt WHERE DeveloperName = 'VNC'];
            system.assertEquals(mdt.size(), 1);
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.sfEventId = lstEvent[0].id;
            request.intReasonId = lstReason[0].id;
            request.relatedToLocation = '';
            request.startDateTime = '';
            request.endDateTime = '';
            request.facilitator = '';
            request.guestSpeaker = '';
            request.performance = '';
            request.feedback = '';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UpdateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateEvent_I_HUM.updateEvent();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM.class);
            system.assertEquals(res1.isSuccess, false); 
            
            Test.StopTest();
        }
    }
	
    @isTest static void testEventNotExist()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.sfEventId = '00Uc00000091ABCDEF';
            request.intReasonId = '';
            request.relatedToLocation = '';
            request.startDateTime = '';
            request.endDateTime = '';
            request.facilitator = '';
            request.guestSpeaker = '';
            request.performance = '';
            request.feedback = '';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UpdateEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateEvent_I_HUM.updateEvent();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM.class);
            system.assertEquals(res1.isSuccess, false); 
            
            Test.StopTest();
        }
    }
	
    @isTest static void testException()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            RestRequest req = new RestRequest();    
            RestResponse res = new RestResponse();
            String JSONMsg = '';
            req.requestURI = 'CRMRetailServices_V1.0/updateEvent';  
            req.httpMethod = 'POST';
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailUpdateEvent_I_HUM.updateEvent();
            RestResponse response = RestContext.response;
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM updateEvent = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM();
            updateEvent = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateEventResponse_DTO_HUM.class);
            System.assertEquals(updateEvent.isSuccess, false);
            
            Test.StopTest();
        }
    }    
}