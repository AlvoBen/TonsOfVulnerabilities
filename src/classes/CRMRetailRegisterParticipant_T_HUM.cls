/******************************************************************************************************************
Apex class Name    : CRMRetailRegisterParticipant_T_HUM
Version            : 1.0 
Function           : This class is used as test class for registering participants Rest Service
Created On         : June 27 ,2021
Test Class         : 
 
Modification Log: 
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Ekta Bist                                       06/27/2021          Original Version 2389054 
*   2.0            Ekta Bist                                       09/02/2021          Test Coverage fix
------------------------------------------------------------------------------------------------------------------- */
@isTest
private class CRMRetailRegisterParticipant_T_HUM {  
	/**
    * This method is used to insert the data only once and called automicticaly in all methods 
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
            List<RecordType> listRecordType = [SELECT Name, SobjectType,IsPersonType FROM RecordType WHERE SobjectType='Account' AND (Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR OR Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER) ORDER BY Name ASC];
            System.assertEquals(listRecordType.size(),2);
            
            Product2 prod = new Product2(Name = 'MED',Family = 'Hardware');
            insert prod;
            System.assertEquals(prod.Name, 'MED');
            
            Id pricebookId = Test.getStandardPricebookId();
            
            PricebookEntry standardPrice = new PricebookEntry(Pricebook2Id = pricebookId, Product2Id = prod.Id, UnitPrice = 10000, IsActive = true);
            insert standardPrice;
            System.assertEquals(standardPrice.Product2Id, prod.Id);
            
            List<Account> listAccount = new List<Account>();
            Account objMemberAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Luke','Lee','99801','llee1@test.com','Juneau','Alaska',false);    
            objMemberAcc.ETL_Record_Deleted__c = false;
            listAccount.add(objMemberAcc);
            
            Account objVisitorAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[0].Id,'09/13/1990','Adam','Lee','99801','adam@test.com','Juneau','Alaska',false);    
            objVisitorAcc.ETL_Record_Deleted__c = false;
            objVisitorAcc.GCM_Visitor_Barcode__c = 'VNC';
            listAccount.add(objVisitorAcc);
            
            Account objAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Rachel','Lee','99801','llee1@test.com','Juneau','Alaska',false);    
            objAcc.ETL_Record_Deleted__c = false;
            listAccount.add(objAcc);
            
            insert listAccount;
            System.assertEquals(listAccount.size(),3);
            
            Policy__c objPolicy = CRMRetail_TestDataFactory_H_HUM.createPolicy(listAccount[0].Id,'123');
            objPolicy.Product__c = prod.Id;
            update objPolicy;
            System.assertEquals(objPolicy.Policy_Group_Number__c,'123');
            
            Policy_Member__c objPolicyMember = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(listAccount[0].Id, objPolicy.Id, 'H12345743723', '12/12/2020', '03/04/2022', false);
            System.assertEquals(objPolicyMember.Name, 'H12345743723');
            
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu');
            insert objIntType;
            System.assertEquals(objIntType.Name,'Consumer Edu');
            
            List<Storefront_I_Reason__c> lstReason = new List<Storefront_I_Reason__c>();
            
            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('consumer test',null);
            objIntReason.Interaction_Type__c = objIntType.Id;
            lstReason.add(objIntReason);
            
            Storefront_I_Reason__c objIntReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In',null);
            objIntReason1.Interaction_Type__c = objIntType.Id;
            lstReason.add(objIntReason1);
            
            Storefront_I_Reason__c objIntReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('consumer test1',null);
            objIntReason2.Interaction_Type__c = objIntType.Id;
            lstReason.add(objIntReason2);
            
            Storefront_I_Reason__c objIntReason3 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In (1st Time)',null);
            objIntReason3.Interaction_Type__c = objIntType.Id;
            lstReason.add(objIntReason3);
            
            insert lstReason;
            System.assertEquals(lstReason.size(),4);
            
            Storefront_Location__c location = CRMRetail_TestDataFactory_H_HUM.createLocation('Virtual HNC','Street no. 1','Texas','12345','83684','california',23);
            insert location;
            System.assertEquals(location.Name,'Virtual HNC');
            
            DateTime nowDateTime = System.Now();
            
            List<Event> listEvent = new List<Event>();
            DateTime startDateTime = nowDateTime;
            DateTime endDateTime = nowDateTime.addMinutes(30);
            Date activityDate = nowDateTime.dateGMT();
            Event objEvent = CRMRetail_TestDataFactory_H_HUM.createEvent(lstReason[0].Id, location.Id, startDateTime, endDateTime, activityDate, false, '', 0);
            objEvent.IsAllDayEvent = True;
            listEvent.add(objEvent);
            
            Event objEvent1 = CRMRetail_TestDataFactory_H_HUM.createEvent(lstReason[0].Id, location.Id, startDateTime, endDateTime, activityDate, false, '', 0);
            objEvent1.IsAllDayEvent = False;
            listEvent.add(objEvent1);
            
            insert listEvent;
            System.assertEquals(listEvent.size(),2); 
            
            List<Storefront_Interaction__c> lstInteractions = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c objInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[1].Id, false, 'Member', activityDate);
            objInteraction.Visitor_ID__c = '123';
            objInteraction.Category__c = '';
            lstInteractions.add(objInteraction);
            
            Storefront_Interaction__c objInteraction1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[1].Id, false, 'Member', activityDate);
            objInteraction1.Visitor_ID__c = 'H123457437';
            objInteraction1.Category__c = 'Virtual';
            objInteraction1.Interaction_Date__c =  System.Today();
            lstInteractions.add(objInteraction1);
            
            Storefront_Interaction__c objInteract = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[3].Id, false, 'Member', activityDate);
            objInteract.Visitor_ID__c = 'H123457437';
            objInteract.Category__c = 'Scheduled Onsite';
            objInteract.Interaction_Date__c =  System.Today();
            objInteract.Storefront_Event_Starttime__c = System.Today();
            lstInteractions.add(objInteract);
            
            Storefront_Interaction__c ob = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[2].Id, location.Id, lstReason[0].Id, false, 'Member', activityDate);
            ob.Visitor_ID__c = 'other';
            ob.Category__c = 'Virtual';
            ob.Interaction_Date__c =  System.Today();
            lstInteractions.add(ob);
            
            Storefront_Interaction__c obj = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[2].Id, location.Id, lstReason[3].Id, false, 'Member', activityDate);
            obj.Visitor_ID__c = 'other';
            obj.Category__c = 'Virtual';
            objInteract.Interaction_Date__c =  System.Today();
            lstInteractions.add(obj);
            
            Storefront_Interaction__c objInteraction2 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[0].Id, false, 'Member', activityDate);
            objInteraction2.Visitor_ID__c = 'H123457437';
            objInteraction2.Category__c = 'Scheduled Onsite';
            objInteraction2.Storefront_Event_Starttime__c = System.Today();
            lstInteractions.add(objInteraction2);

			Storefront_Interaction__c objInteraction3 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[2].Id, false, 'Member', activityDate);
            objInteraction3.Visitor_ID__c = 'H123457437';
            objInteraction3.Category__c = 'Scheduled Onsite';
            lstInteractions.add(objInteraction3);  
            
            insert lstInteractions;
            System.assertEquals(lstInteractions.size(),7);
            
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new  CRMFunctionality_ONOFF_Switch__c(Name = '1243084', IsON__c = True);
            insert csCRMOnOffSwitch;
            System.assertEquals(csCRMOnOffSwitch.Name,'1243084');
        }
    }    
    
    /*
    * Method Name   :   testCreateFirstCheckInEventInt
    * Description   :   This method is to create a 1st time checkin interaction along with event interaction.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAllDayCreateFirstCheckInEventInt()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

           List<Account> lstAccount = [Select id, FirstName from Account where FirstName = 'Adam' Limit 1 ];
           System.assertEquals(lstAccount.size(), 1);
            
           List<Event> lstEvent = [Select id from Event where IsAllDayEvent = true LIMIT 1 ];
           System.assertEquals(lstEvent.size(), 1);
    
           List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN]; 
           listOfReasons[0].Name = 'Test';
           update listOfReasons[0];
           System.assertEquals(listOfReasons[0].Name,'Test');
            
           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.sfEventId = lstEvent[0].Id;  
           request.accountId = lstAccount[0].Id;
           request.category = 'Onsite';
           
           RestRequest req = new RestRequest();
           String JSONMsg = System.JSON.serialize(request);
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants';  
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           Test.StartTest(); 
           CRMRetailRegisterParticipant_I_HUM.registerParticipants();
           RestResponse response = RestContext.response; 
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
           System.assertEquals(res1.isSuccess, false); 
           Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testAllDayCreateFirstCheckInEvent
    * Description   :   This method is to create a 1st time checkin interaction along with event interaction.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAllDayCreateFirstCheckInEvent()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

           List<Account> lstAccount = [Select id, FirstName from Account where FirstName = 'Adam' Limit 1 ];
           System.assertEquals(lstAccount.size(), 1);
            
           List<Event> lstEvent = [Select id from Event where IsAllDayEvent = false LIMIT 1 ];
           System.assertEquals(lstEvent.size(), 1);
    
           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.sfEventId = lstEvent[0].Id;  
           request.accountId = lstAccount[0].Id;
           request.category = 'Onsite';
           
           RestRequest req = new RestRequest();
           String JSONMsg = System.JSON.serialize(request);
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants';  
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           Test.StartTest(); 
           CRMRetailRegisterParticipant_I_HUM.registerParticipants();
           RestResponse response = RestContext.response; 
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
           System.assertEquals(res1.isSuccess, false); 
           Test.StopTest();
        }
    }
    /*
    * Method Name   :   testAllDayCreateCheckInEventInt
    * Description   :   This method is to create a check-in interaction along with event interaction.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAllDayCreateCheckInEventInt()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
           List<Account> lstAccount = [Select id, FirstName from Account where FirstName = 'Luke' Limit 1 ];
           System.assertEquals(lstAccount.size(), 1);
            
           List<Event> lstEvent = [Select id,startDateTime from Event where IsAllDayEvent = false LIMIT 1 ];
           System.assertEquals(lstEvent.size(), 1);
    
           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.sfEventId = lstEvent[0].Id;  
           request.accountId = lstAccount[0].Id;
           request.category = 'Onsite';
           
           RestRequest req = new RestRequest();
           String JSONMsg = System.JSON.serialize(request);
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants'; 
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           Test.StartTest(); 
           CRMRetailRegisterParticipant_I_HUM.registerParticipants();
           RestResponse response = RestContext.response; 
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
           res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
           System.assertEquals(true, res1.isSuccess);  
       	   Test.StopTest();
        }
    }
    /*
    * Method Name   :   testAllDayCreateCheckIn
    * Description   :   This method is to create a check-in interaction along with event interaction.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAllDayCreateCheckIn()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
           List<Account> lstAccount = [Select id, FirstName from Account where FirstName = 'Rachel' Limit 1 ];
           System.assertEquals(lstAccount.size(), 1);
            
           List<Event> lstEvent = [Select id,startDateTime from Event where IsAllDayEvent = true LIMIT 1 ];
           System.assertEquals(lstEvent.size(), 1);
    
           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.sfEventId = lstEvent[0].Id;  
           request.accountId = lstAccount[0].Id;
           request.category = 'Onsite';
           
           RestRequest req = new RestRequest();
           String JSONMsg = System.JSON.serialize(request);
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants'; 
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           Test.StartTest(); 
           CRMRetailRegisterParticipant_I_HUM.registerParticipants();
           RestResponse response = RestContext.response; 
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
           res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
           System.assertEquals(res1.isSuccess, true); 
       	   Test.StopTest();
        }
    }
    /*
    * Method Name   :   testAllDayCreateCheckInEventInt
    * Description   :   This method is to create a check-in interaction along with event interaction.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAllDayCreateCheck()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
           List<Account> lstAccount = [Select id, FirstName from Account where FirstName = 'Luke' Limit 1 ];
           System.assertEquals(lstAccount.size(), 1);
            
           List<Event> lstEvent = [Select id,startDateTime from Event where IsAllDayEvent = true LIMIT 1 ];
           System.assertEquals(lstEvent.size() , 1);  

           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.sfEventId = lstEvent[0].Id;  
           request.accountId = lstAccount[0].Id;
           request.category = 'Onsite';
           
           RestRequest req = new RestRequest();
           String JSONMsg = System.JSON.serialize(request);
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants'; 
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           Test.StartTest(); 
           CRMRetailRegisterParticipant_I_HUM.registerParticipants();
           RestResponse response = RestContext.response; 
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
           res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
           System.assertEquals(res1.isSuccess, true); 
       	   Test.StopTest();
        }
    }
    /*
    * Method Name   :   testAllDayCreateCheckInEventInt
    * Description   :   This method is to create a check-in interaction along with event interaction.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAllDayCreateCheckInEve()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
           List<Account> lstAccount = [Select id, FirstName from Account where FirstName = 'Luke' Limit 1 ];
           System.assertEquals(lstAccount.size(), 1);
            
           List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN]; 
           listOfReasons[0].Name = 'Test';
           update listOfReasons[0];
           System.assertEquals(listOfReasons[0].Name, 'Test');
            
           List<Event> lstEvent = [Select id,startDateTime from Event where IsAllDayEvent = true LIMIT 1 ];
           System.assertEquals(lstEvent.size() , 1); 
    
           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.sfEventId = lstEvent[0].Id;  
           request.accountId = lstAccount[0].Id;
           request.category = 'Onsite';
           
           RestRequest req = new RestRequest();
           String JSONMsg = System.JSON.serialize(request);
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants'; 
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           Test.StartTest(); 
           CRMRetailRegisterParticipant_I_HUM.registerParticipants();
           RestResponse response = RestContext.response; 
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
           res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
           System.assertEquals(res1.isSuccess, false); 
       	   Test.StopTest();
        }
    }
    /*
    * Method Name   :   testGetVisitorIdOfVisitor
    * Description   :   This method is to get the visitor id of a visitor account.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testGetVisitorIdOfVisitor()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, GCM_Visitor_Barcode__c,RecordType.Name from Account where FirstName = 'Adam' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            String visitor = CRMRetailVNC_Helper_HUM.getVisitorID(lstAccount[0]);
            System.assertEquals(visitor, 'VNC'); 
        }
    } 
/*
    * Method Name   :   testGetInteractionData
    * Description   :   This method is to catch the exception of getInteractionData.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testGetInteractionData()
    {       
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            Storefront_Interaction__c objInteraction = CRMRetailRegisterParticipant_H_HUM.getInteractionData(null,null,'',null,'');
            System.assertEquals(objInteraction, null);
        }
    } 

    /*
    * Method Name   :   testGetVisitorIdMemberWithNoPolicy
    * Description   :   This method is to get the visitor id of a member account with no policy.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testGetVisitorIdMemberWithNoPolicy()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

            List<Account> lstAccount = [Select id, GCM_Visitor_Barcode__c, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'Rachel' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            String visitor = CRMRetailVNC_Helper_HUM.getVisitorID(lstAccount[0]);
            System.assertEquals(visitor, 'other'); 
        }
    }
    
    /*
    * Method Name   :   testBlankCategory
    * Description   :   This method is to get error when category sent in the request is Blank.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testBlankCategory()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            List<Account> lstAccount = [Select id from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            List<Event> lstEvent = [Select id from Event LIMIT 1 ];
            System.assertEquals(lstEvent.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.sfEventId = lstEvent[0].Id;  
            request.accountId = lstAccount[0].Id;
            request.category = 'VirtualOnsite';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailRegisterParticipant_I_HUM.registerParticipants();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    } 
    
    /*
    * Method Name   :   testInvalidAccount
    * Description   :   This method is to get error when account id sent in the request is invalid.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testInvalidAccount()
    {   
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

            List<Event> lstEvent = [Select id from Event LIMIT 1 ];
            System.assertEquals(lstEvent.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.sfEventId = lstEvent[0].Id;  
            request.accountId = '00Uc00000091ABCFGH';      
            request.category = 'Onsite';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailRegisterParticipant_I_HUM.registerParticipants();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false);
            Test.StopTest();
        }
    } 
    
    /*
    * Method Name   :   testInvalidEvent
    * Description   :   This method is to get error when event id sent in the request is invalid.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testInvalidEvent()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

            List<Account> lstAccount = [Select id from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.sfEventId = '00Uc00000091ABCFGH';  
            request.accountId = lstAccount[0].Id;
            request.category = 'Virtual';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailRegisterParticipant_I_HUM.registerParticipants();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testErrorService
    * Description   :   This method is to get error in case of any exception.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testErrorService()
    {            
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegParticipantRequest_DTO_HUM(); 
            
            RestRequest req = new RestRequest();    
            RestResponse res = new RestResponse();
            String JSONMsg = '';
            req.requestURI = '/CRMRetailServices_V1.0/RegisterParticipants'; 
            req.httpMethod = 'POST';
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailRegisterParticipant_I_HUM.registerParticipants();
            RestResponse response = RestContext.response; 
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM();    
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegParticipantResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest(); 
        }
    }
    
    /**
	* testGetError Method.
	* <p>
	* This method is used to get the exact error for the exception
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
    
    @isTest static void testGetError()
    {   
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {         
            String error = 'Insert failed. First exception on row 0; first error: FIELD_CUSTOM_VALIDATION_EXCEPTION, This event already exists in the Calendar.: []';
            error = CRMRetailVNC_Helper_HUM.getError(error); 
            System.assertNotEquals(error, null);
        }
    } 
    
     /**
	* testReturnInteractionId Method.
	* <p>
	* This method is used to get the exact Id for the interaction
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
    
    @isTest static void testReturnInteractionId()
    {   
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {         
            Id intId;
            List<Storefront_Interaction__c> listOfInteractions = new List<Storefront_Interaction__c>();
            intId = CRMRetailVNC_Helper_HUM.returnInteractionId(listOfInteractions); 
            System.assertEquals(intId, null);
        }
    }
}