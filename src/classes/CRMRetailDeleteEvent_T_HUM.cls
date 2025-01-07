/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailDeleteEvent_T_HUM 
* Version          : 1.0
* Created Date     : 24th June, 2021
* Function         : This class is used as a test class for Delete Event rest service
* Test Class       : 
***********************************************************************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                                          06/24/2021            Original Version 2361242
**********************************************************************************************************************************/

@isTest
public class CRMRetailDeleteEvent_T_HUM {
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
            list<Event> lstEvent = new list<Event>();
            list<Storefront_I_Reason__c> lstIntReason = new list<Storefront_I_Reason__c>();
            list<Storefront_I_Type__c> lstIntType = new list<Storefront_I_Type__c>();
            list<Storefront_Location__C> lstLocation = new List<Storefront_Location__C>();
            
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Education');
            lstIntType.add(objIntType);
            insert lstIntType;
            System.assertEquals(lstIntType.size(), 1);
            
            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Consumer Education',null);
            objIntReason.Interaction_Type__c = lstIntType[0].Id;
            lstIntReason.add(objIntReason);
            insert lstIntReason;
            System.assertEquals(lstIntReason.size(), 1);
            
            Storefront_Location__C objLocation = CRMRetail_TestDataFactory_H_HUM.createLocation('Atlanta','Street no. 2','Texas','67890','12345','LA',25);
            lstLocation.add(objLocation);
            insert lstLocation;
            System.assertEquals(lstLocation.size(), 1);
            
            Event objEvt = CRMRetail_TestDataFactory_H_HUM.createEvent(lstIntReason[0].id,lstLocation[0].id,Datetime.now().addHours(1), Datetime.now().addHours(2),null,false,'',0);
            lstEvent.add(objEvt);
            insert lstEvent;
            System.assertEquals(lstEvent.size(), 1);
        }
    }
    
    /*
    * Method Name   :   testEventExists
    * Description   :   Creates an instance of Event object and returns it
    * Return Type   :   Event
    */
    
    @isTest static void testEventExists()
	{
		User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            list<Event> lstEvent = [SELECT Id FROM Event Where isRecurrence = false];
            System.assertEquals(lstEvent.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.sfEventId = lstEvent[0].Id;
           
            RestRequest req = new RestRequest();
        
            String JSONMsg = System.JSON.serialize(request);
	
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/DeleteEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
      
            CRMRetailDeleteEvent_I_HUM.deleteEvent();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM deleteEvt = new CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM(); 
            deleteEvt = (CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM.class);
            system.assertEquals(deleteEvt.isSuccess, true); 
            
            Test.StopTest();
        }
	}
    
    /*
    * Method Name   :   testEventDontMatch
    * Description   :   Send incorrect Event Id which does not exists in database
    * Return Type   :   Event
    */
    
    @isTest static void testEventDontMatch()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailDeleteEventRequest_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.sfEventId = '00Uc00000091ABCDEF';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/DeleteEvent';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailDeleteEvent_I_HUM.deleteEvent();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM deleteEvt = new CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM(); 
            deleteEvt = (CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM.class);
            system.assertEquals(deleteEvt.isSuccess, false); 
            
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testDeleteEventException
    * Description   :   Cover the exception part 
    * Return Type   :   Event
    */
    
    @isTest static void testDeleteEventException()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            RestRequest req = new RestRequest();
            
            String JSONMsg = '';
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/DeleteEvent';  
            req.httpMethod = 'POST';
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailDeleteEvent_I_HUM.deleteEvent();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM deleteEvt = new CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM(); 
            deleteEvt = (CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailDeleteEventResponse_DTO_HUM.class);
            system.assertEquals(deleteEvt.isSuccess, false); 
            
            Test.StopTest();
        }
    }
}