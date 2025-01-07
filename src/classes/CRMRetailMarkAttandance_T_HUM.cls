/******************************************************************************************************************
Apex class Name    : CRMRetailMarkAttandance_T_HUM
Version            : 1.0 
Function           : This class is used as test class for mark attendance Rest Service
Created On         : June 27 ,2021
Test Class         : 

Modification Log: 
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Ekta Bist                                       06/27/2021          Original Version 2389191 
*   2.0            Ekta Bist                                       08/23/2021          Validation Rule Fix for Terra
*   3.0            Ekta Bist                                       09/03/2021          Test Coverage Fix
*   4.0            Ekta Bist                                       03/09/2022          Test Coverage Fix
------------------------------------------------------------------------------------------------------------------- */
@isTest
private class CRMRetailMarkAttandance_T_HUM { 
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
        
        List<permissionset> lstPermissionSet = new List<permissionset>();
        lstPermissionSet = [Select id, name from permissionset where name = 'CRMRetail_300_BusinessAdministrator' LIMIT 1];
        System.assertEquals(lstPermissionSet.size(), 1);
        
        PermissionSetAssignment assignment = new PermissionSetAssignment(AssigneeId = oUser.Id, PermissionSetId = lstPermissionSet[0].Id);
   		insert assignment;
        System.assertNotEquals(assignment, null);
        
        System.runAs(oUser) 
        {                        
            List<RecordType> listRecordType = [SELECT Name, SobjectType,IsPersonType FROM RecordType WHERE SobjectType =: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT AND (Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR OR Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER) ORDER BY Name ASC];
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
            
            Account objAc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','John','Lee','99801','llee1@test.com','Juneau','Alaska',false);    
            objAc.ETL_Record_Deleted__c = false;
            listAccount.add(objAc);
            
            insert listAccount;
            System.assertEquals(listAccount.size(),4);
            
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
            
            Storefront_I_Reason__c objIntReason3 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN,null);
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
            
            insert listEvent;            
            System.assertEquals(listEvent.size(),1);
            
            List<Storefront_Interaction__c> lstInteractions = new List<Storefront_Interaction__c>();            
            Storefront_Interaction__c objInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[1].Id, false, 'Member', activityDate);
            objInteraction.Visitor_ID__c = '123';
            objInteraction.Category__c = '';
            lstInteractions.add(objInteraction);
            
            Storefront_Interaction__c objInteraction1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[1].Id, false, 'Member', activityDate);
            objInteraction1.Visitor_ID__c = 'H123457437';
            objInteraction1.Category__c = '';
            lstInteractions.add(objInteraction1); 
            
            Storefront_Interaction__c objInteraction2 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[0].Id, false, 'Member', activityDate);
            objInteraction2.Visitor_ID__c = 'H123457437';
            objInteraction2.Category__c = 'Scheduled Onsite';
            objInteraction2.Storefront_Event_Starttime__c = nowDateTime;
            objInteraction2.Interaction_Date__c = nowDateTime;
            lstInteractions.add(objInteraction2);   
            
            Storefront_Interaction__c objInteraction02 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[1].Id, location.Id, lstReason[0].Id, false, 'Member', activityDate);
            objInteraction02.Visitor_ID__c = 'H123457437';
            objInteraction02.Category__c = 'Scheduled Virtual';
            objInteraction02.Storefront_Event_Starttime__c = nowDateTime;
            objInteraction02.Interaction_Date__c = datetime.newInstance(2021, 12, 12);
            lstInteractions.add(objInteraction02); 
            
            Storefront_Interaction__c obb = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[1].Id, location.Id, lstReason[1].Id, false, 'Member', activityDate);
            obb.Visitor_ID__c = 'H123457437';
            obb.Category__c = '';
            obb.Interaction_Date__c = System.today();
            lstInteractions.add(obb); 

			Storefront_Interaction__c objInteraction3 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[0].Id, location.Id, lstReason[2].Id, false, 'Member', activityDate);
            objInteraction3.Visitor_ID__c = 'H123457437';
            objInteraction3.Category__c = 'Recorded';
            objInteraction3.Interaction_Date__c = nowDateTime;

            lstInteractions.add(objInteraction3);
            
            Storefront_Interaction__c objInteraction4 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[2].Id, location.Id, lstReason[0].Id, false, 'Member', activityDate);
            objInteraction4.Visitor_ID__c = 'H123457437';
            objInteraction4.Category__c = 'Scheduled Virtual';
            objInteraction4.Interaction_Date__c = nowDateTime;
            lstInteractions.add(objInteraction4);
            
            Storefront_Interaction__c objInteraction5 = CRMRetail_TestDataFactory_H_HUM.createInteraction(listAccount[2].Id, location.Id, lstReason[3].Id, false, 'Member', activityDate);
            objInteraction5.Visitor_ID__c = 'H123457437';
            objInteraction5.Category__c = 'Virtual';
            objInteraction5.Interaction_Date__c = nowDateTime;
            lstInteractions.add(objInteraction5);
            
            insert lstInteractions;
            System.assertEquals(lstInteractions.size(),8);
            
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new  CRMFunctionality_ONOFF_Switch__c(Name = '1243084', IsON__c = True);
            insert csCRMOnOffSwitch;
            System.assertEquals(csCRMOnOffSwitch.Name,'1243084');            
        }
    }
    
    /*
    * Method Name   :   testMarkAttendance
    * Description   :   This method is to create check in along with attended interaction in case of non video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testMarkAttendance()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__r.Id,Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Account__r.Firstname = 'Adam' AND Category__c = 'Scheduled Virtual' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            lstInteraction[0].Category__c = 'Scheduled Onsite';
            update lstInteraction[0];
            System.assertEquals(lstInteraction[0].Category__c, 'Scheduled Onsite');
                        
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }  
    
     
    /*
    * Method Name   :   testMarkAttendance
    * Description   :   This method is to create check in along with attended interaction in case of non video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testMarkAttendnce()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__r.Id,Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Account__r.Firstname = 'Adam' AND Category__c = 'Scheduled Virtual' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            List<Account> obAccount = [Select id, name from account where name = 'Adam'];
            System.assertNotEquals(obAccount,null);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }      
    
    /*
    * Method Name   :   testAttendance
    * Description   :   This method is to create check in along with attended interaction for an Onsite event in case of non video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAttendance()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Account__r.Firstname = 'Rachel'  AND Category__c = 'Scheduled Virtual' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testReasonNotFound
    * Description   :   This method is for when reason not found. 
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testReasonNotFound()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Account__r.Firstname = 'Adam'  AND Category__c = 'Scheduled Virtual' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN]; 
            if(listOfReasons != null && !listOfReasons.isEmpty())
            {
                listOfReasons[0].Name = 'Test';
            }
            update listOfReasons[0];
            
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }   
    
    /*
    * Method Name   :   testReasonNot
    * Description   :   This method is for when reason not found. 
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testReasonNot()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Account__r.Firstname = 'Adam'  AND Category__c = 'Scheduled Virtual' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN]; 
            if(listOfReasons != null && !listOfReasons.isEmpty())
            {
                listOfReasons[0].Name = 'Test';
            }
            update listOfReasons[0];
            
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }  
    
    /*
    * Method Name   :   testMarkAttendanceOnsite
    * Description   :   This method is to create check in along with attended interaction for an Onsite event in case of non video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testMarkAttendanceOnsite()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Account__r.Firstname = 'Adam'  AND Category__c = 'Scheduled Virtual' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            lstInteraction[0].Category__c = 'Scheduled Onsite';
            update lstInteraction[0];
            System.assertEquals(lstInteraction[0].Category__c,'Scheduled Onsite');
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testMarkAttendanceDuplication
    * Description   :   This method is for duplicate check in case of non video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testMarkAttendanceDuplication()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {  
               
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Interaction_Date__c,Location__c,Storefront_Event_Starttime__c, Account__r.Id,Location__r.Id, Reason__c, Reason__r.Name, Reason__r.Id,isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c  from Storefront_Interaction__c WHERE Category__c = 'Scheduled Onsite' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1); 
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    } 
       
    /*
    * Method Name   :   testCreateCheckInEventInt
    * Description   :   This method is to create check in along with attended interaction in case of video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testCreateCheckInEventInt()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2'); 
        
        system.runAs(oUser)
        {   
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Category__c = 'Recorded' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            lstInteraction[0].Interaction_Date__c = System.Now().DateGMT();
            update lstInteraction[0];                        
            
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);            
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c where name = 'consumer test1' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertnotEquals(null,res1);             
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testCheckInEventInt
    * Description   :   This method is to create check in along with attended interaction in case of video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testCheckInEventInt()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2'); 
        
        system.runAs(oUser)
        {   
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Category__c = 'Recorded' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            lstInteraction[0].Interaction_Date__c = System.Today();
            update lstInteraction[0];
            
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'John' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c where name = 'consumer test1' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }
    
     /*
    * Method Name   :   testCreateCheckInEventDup
    * Description   :   This method is for duplicate interaction in case of video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testCreateCheckInEventDup()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');        
        
        system.runAs(oUser)
        {            
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
                        
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c where name = 'consumer test1' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1);  

            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);            
            System.assertnotEquals(null,res1); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testCreate1stEventInt
    * Description   :   This method is to create first time check in along with attended interaction in case of video event.  
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testCreate1stEventInt()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {            
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Adam' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id from Storefront_I_Reason__c where name = 'consumer test1' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testNoAccountFound
    * Description   :   This method is to get error when accountId in the interaction is null in case of non video event
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testNoAccountFound()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Category__c = 'Scheduled Onsite' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            lstInteraction[0].Account__c = null;
            update lstInteraction[0];
            System.assertEquals(lstInteraction[0].Account__c, null);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }    
     
    /*
    * Method Name   :   testCheckInReasonError
    * Description   :   This method is to get error when Visitor Checking Reason is not found in case of video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
	@isTest static void testCheckInReasonError()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Category__c = 'Recorded' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            Id reasonID;
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c]; 
            for(Storefront_I_Reason__c ob : listOfReasons)
            {
                if(ob.name == 'consumer test1')
                {
                    reasonID = ob.Id;
                }
                if(ob.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN)
                {
                    ob.Name = 'Test';
                }
                update ob;
            }
            System.assertEquals(listOfReasons.size(), 4);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = reasonID;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testReasonNotFoundNonVideo
    * Description   :   This method is to get error when Visitor Checking First Time Reason is not found in case of video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testReasonNotFoundNonVideo()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Adam' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name from Storefront_I_Reason__c];
            System.assertEquals(listOfReasons.size() ,4);
            Id reasonID;
            for(Storefront_I_Reason__c ob : listOfReasons)
            {
                if(ob.name == 'consumer test1')
                {
                    reasonID = ob.Id;
                }
                if(ob.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN)
                {
                    ob.Name = 'Test';
                }
                update ob;
            }
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = reasonID;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }

	/*
    * Method Name   :   testReasonIdNonVideoError
    * Description   :   This method is to get error when Visitor Checking Reason is not found in case of non video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testReasonIdNonVideoError()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Category__c = 'Scheduled Onsite' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id,name  from Storefront_I_Reason__c]; 
            System.assertEquals(listOfReasons.size(), 4);
            
            Id reasonId;
            for(Storefront_I_Reason__c ob : listOfReasons)
            {
                if(ob.Name == 'consumer test')
                {
                    reasonId = ob.Id;
                }
                if(ob.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN)
                {
                    ob.Name = 'Test';
                }
                update ob;
            }
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    } 
    
    /*
    * Method Name   :   testNoInteractionFound
    * Description   :   This method is to get error when no interaction is found for the interaction id sent in the request in case of non video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */    
    @isTest static void testNoInteractionFound()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');       
        
        system.runAs(oUser)
        {
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c WHERE Category__c = 'Scheduled Onsite' LIMIT 4];
            System.assertEquals(lstInteraction.size(), 1);
            
            lstInteraction[0].Account__c = null;
            update lstInteraction[0];
            System.assertEquals(lstInteraction[0].Account__c,null);
        
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = '00Uc00000091ABCFGH';
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }    
    
    /*
    * Method Name   :   testLocationError
    * Description   :   This method is to get error when for the source system there is not location maintained in the system.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testLocationError()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Adam' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            List<Storefront_I_Reason__c> listOfReasons = [Select id from Storefront_I_Reason__c where name = 'consumer test1' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC1';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testIncorrectReasonId
    * Description   :   This method is to get error when reason Id is incorrect.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testIncorrectReasonId()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = '00Uc00000091ABCDEF';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testReasonIdError
    * Description   :   This method is to get error when reason Id is blank in case when event is a video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testReasonIdError()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id,RecordType.Name from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = lstAccount[0].Id;
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testAccountIdError
    * Description   :   This method is to get error when account Id is blank in case when event is a video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testAccountIdError()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Storefront_I_Reason__c> listOfReasons = [Select id from Storefront_I_Reason__c where name = 'consumer test1' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1); 
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = '';
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testInteractionIdError
    * Description   :   This method is to get error when interaction Id is blank in case when event is not a video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testInteractionIdError()
    {  
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = '';
            request.accountId = '';
            request.intReasonId = '';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testIncorrectAccountId
    * Description   :   This method is to get error in case of incorrect accountID.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testIncorrectAccountId()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Storefront_I_Reason__c> listOfReasons = [Select id from Storefront_I_Reason__c where name = 'consumer test1' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = true;  
            request.interactionId = '';
            request.accountId = '00Uc00000091ABCFGH';
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testServiceError
    * Description   :   This method is to get error in case of any exception.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */    
	@isTest static void testServiceError()
    {
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            RestRequest req = new RestRequest();        
            String JSONMsg = '';        
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';        
            RestContext.request = req;
            RestContext.response= res;        
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();		
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM unregisterPart = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            unregisterPart = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            system.assertEquals(unregisterPart.isSuccess, false);         
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testGenerateError
    * Description   :   This method is to get exception in case of non video event.   
    * Return Type   :   N/A
    * Parameters    :   N/A
    */
    @isTest static void testGenerateError()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            List<Storefront_Interaction__c> lstInteraction = [Select id, Category__c, Account__c, Location__c, Reason__c, Reason__r.Name, isAllDayEvent__c, CRM_Retail_Interaction_Visitor_Type__c,Interaction_Date__c  from Storefront_Interaction__c where Category__c = '' limit 1];
            System.assertEquals(lstInteraction.size(), 1);            
               
            List<Storefront_I_Reason__c> listOfReasons = [Select id from Storefront_I_Reason__c where name = 'consumer test' Limit 1]; 
            System.assertEquals(listOfReasons.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailMarkAttandanceReq_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.isVideoEvent = false;  
            request.interactionId = lstInteraction[0].Id;
            request.accountId = lstAccount[0].Id;
            request.intReasonId = listOfReasons[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/MarkAttendance';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.StartTest(); 
            CRMRetailMarkAttandance_I_HUM.markAttandance();
            RestResponse response = RestContext.response;  
            CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailMarkAttandanceResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            Test.StopTest();
        }
    }
}