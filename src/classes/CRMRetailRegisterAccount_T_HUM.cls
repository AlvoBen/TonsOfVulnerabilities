/******************************************************************************************************************
Apex class Name    : CRMRetailRegisterAccount_T_HUM
Version            : 1.0
Function           : This class is used as test class for account registration Rest Service
Created On         : June 02 ,2021
Test Class         : 

Modification Log: 
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Ekta Bist                                       06/01/2021          Original Version 2322470
*   2.0            Ekta Bist                                       09/02/2021          Test Coverage fix
*   3.0            Ekta Bist                                       09/14/2021          Security Fixes
*   4.0            Ekta Bist									   01/07/2022		   Remove Case Sensitivity
*   5.0            Ekta Bist                                       01/20/2022          Secondary Confirmation
------------------------------------------------------------------------------------------------------------------- */
@isTest
private class CRMRetailRegisterAccount_T_HUM {	
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {  
        List<User> lstUserToInsert = new List<User>();
        User oUser = HUMTestDataHelperTest.createTestUser('User2','ETL API Access');
        lstUserToInsert.add(oUser);        
        User oUserNotAccess = HUMTestDataHelperTest.createTestUser('UserRetail1','CRMRetail User');
        lstUserToInsert.add(oUserNotAccess);

        insert lstUserToInsert;
        System.assertEquals(lstUserToInsert.size(), 2);
        
        System.runAs(oUser)  
        {                        
            List<RecordType> listRecordType = [SELECT Name, SobjectType,IsPersonType FROM RecordType WHERE (Name = 'CRMRetail Visitor' OR Name = 'Member') AND SobjectType='Account' AND IsPersonType = True ORDER BY Name ASC];
            System.assertEquals(listRecordType.size(),2);
            
            List<Account> listAccount = new List<Account>();
            
            Account objMemberAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Luke','Lee','99801','llee1@test.com','Juneau','Alaska',false);    
            listAccount.add(objMemberAcc);
            
            Account objMemberAcc1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Luke','Lee','99801','llee2@test.com','Juneau','Alaska',false);     
            listAccount.add(objMemberAcc1);
            
            Account objAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1990','Jonas','Doe','99801','llee1@test.com','Juneau','Alaska',false);    
            listAccount.add(objAcc);
            
            Account objMemberAc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[0].Id,'04/06/1985','Smith','Woxer','99801','swoxer@test.com','Juneau','Alaska',false);    
            listAccount.add(objMemberAc);
            
            Account obMemberAc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[0].Id,'04/06/1992','John','Well','99801','john@test.com','Juneau','Alaska',false);    
            listAccount.add(obMemberAc);
                        
            Account obMemberAc1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[0].Id,'04/06/1992','John','Well','99801','john@test.com','Juneau1','Alaska',false);   
            obMemberAc1.GCM_Visitor_Barcode__c = 'VNC';
            listAccount.add(obMemberAc1);
            
            Account objAccount = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'09/13/1997','Adam','Adam','99801','adam@test.com','Juneau','Alaska',false);     
            listAccount.add(objAccount);
            
            insert listAccount; 

            System.assertEquals(listAccount.size(),7); 
            
            
            Policy__c objPolicy = CRMRetail_TestDataFactory_H_HUM.createPolicy(listAccount[0].Id,'123');
            System.assertEquals(objPolicy.Policy_Group_Number__c,'123');
            
            Policy__c objPolicy1 = CRMRetail_TestDataFactory_H_HUM.createPolicy(listAccount[2].Id,'1232');
            System.assertEquals(objPolicy1.Policy_Group_Number__c,'1232');
            
            Policy__c objPolicy2 = CRMRetail_TestDataFactory_H_HUM.createPolicy(listAccount[0].Id,'123');
            System.assertEquals(objPolicy2.Policy_Group_Number__c,'123');
            
            Policy_Member__c objPolicyMember = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(listAccount[0].Id, objPolicy.Id, '12345', '03/04/2020', '03/04/2022', false);
            System.assertEquals(objPolicyMember.Name, '12345');
            
            Policy_Member__c objPolicyMem = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(listAccount[2].Id, objPolicy1.Id, '12345', '03/04/2020', '03/04/2022', false);
            System.assertEquals(objPolicyMember.Name, '12345');
            
            Policy_Member__c objPolicyMember1 = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(listAccount[0].Id, objPolicy1.Id, '12345', '03/04/2020', '03/04/2022', false);
            System.assertEquals(objPolicyMember1.Name, '12345');
            
            Member_ID__c objMem = CRMRetail_TestDataFactory_H_HUM.createMemberId(objPolicyMem.Id, '12345' ,'', objPolicy1.Id, '', '');
            System.assertEquals(objMem.Name, '12345');
            
            Member_ID__c objMember = CRMRetail_TestDataFactory_H_HUM.createMemberId(objPolicyMember.Id, '12345' ,'', objPolicy.Id, '', '');
            System.assertEquals(objMember.Name, '12345');
            
            Member_ID__c objMember1 = CRMRetail_TestDataFactory_H_HUM.createMemberId(objPolicyMember.Id, '12345' ,'', objPolicy.Id, '', '');
            System.assertEquals(objMember1.Name, '12345'); 

            List<CRMFunctionality_ONOFF_Switch__c> lstOfswitches = new List<CRMFunctionality_ONOFF_Switch__c>();
			CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch1 = new  CRMFunctionality_ONOFF_Switch__c(Name = '2824434', IsON__c = True);
            lstOfswitches.add(csCRMOnOffSwitch1);            
            
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch2 = new  CRMFunctionality_ONOFF_Switch__c(Name = '2691993', IsON__c = True);
            lstOfswitches.add(csCRMOnOffSwitch2);     
            insert lstOfswitches;
            System.assertEquals(lstOfswitches.get(0).Name,'2824434');
            System.assertEquals(lstOfswitches.get(1).Name,'2691993');          
        }
    } 
    
    @isTest static void testSingleMemberAccService()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Adam' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234598';  
            request.firstName = 'Adam';
            request.lastName = 'Adam';
            request.dateOfBirth = '09/13/1997';
            request.emailId = 'abc@example.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest(); 
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();
        }
    }

    @isTest static void testSwitchOff()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            List<CRMFunctionality_ONOFF_Switch__c> lstSwitch = [Select Name, IsON__c from CRMFunctionality_ONOFF_Switch__c where name = '2824434' LIMIT 1];
            System.assertEquals(lstSwitch.size(),1);
            
            lstSwitch[0].IsON__c = false;
            update lstSwitch[0];
            System.assertEquals(lstSwitch[0].IsON__c,false);
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Adam' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234598';  
            request.firstName = 'Adam';
            request.lastName = 'Adam';
            request.dateOfBirth = '09/13/1997';
            request.emailId = 'abc@example.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest(); 
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest();
        }
    }
    
   @isTest static void testSingleMemberAccountService()
    {        
       User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
       system.assertEquals(oUser.Name,'test User2');
       
       system.runAs(oUser)
       {

           List<Member_ID__c> lstMember = [Select Id from Member_ID__c];
           System.assertEquals(lstMember.size(), 3);
            
           Id [] fixedSearchResults= new Id[3];
           fixedSearchResults[0] = lstMember[0].Id;
           fixedSearchResults[1] = lstMember[1].Id;
           fixedSearchResults[2] = lstMember[2].Id;
           Test.setFixedSearchResults(fixedSearchResults);

           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.memberId = '12345';  
           request.firstName = 'Luke';
           request.lastName = 'Lee';
           request.dateOfBirth = '09/13/1990';
           request.emailId = 'llee1@test.com';
           request.zipCode = '99801';
           request.city = 'CAP';
           request.state = 'Alaska';
           request.externalId = '76HR753IK96438YRU';
           
           RestRequest req = new RestRequest();
           
           String JSONMsg = System.JSON.serialize(request);
           
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           
           Test.StartTest();   
                      
           CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
           
           RestResponse response = RestContext.response;           
           
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
           res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
           System.assertEquals(res1.isSuccess, true);                                  
           
           Test.StopTest();
       }
    }
    @isTest Static void testNegativeSingle(){
       User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
       system.assertEquals(oUser.Name,'test User2');
       
       system.runAs(oUser)
       {
           CRMFunctionality_ONOFF_Switch__c Switch_2691993 = [SELECT Id, Name, IsOn__c FROM CRMFunctionality_ONOFF_Switch__c WHERE Name = '2691993'];
           Switch_2691993.IsOn__c = False;
           update Switch_2691993;                      
           
           List<Member_ID__c> lstMember = [Select Id from Member_ID__c];
           System.assertEquals(lstMember.size(), 3);
            
           Id [] fixedSearchResults= new Id[3];
           fixedSearchResults[0] = lstMember[0].Id;
           fixedSearchResults[1] = lstMember[1].Id;
           fixedSearchResults[2] = lstMember[2].Id;
           Test.setFixedSearchResults(fixedSearchResults);

           CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
           request.sourceSystem = 'VNC';
           request.memberId = '12345';  
           request.firstName = 'Luke';
           request.lastName = 'Lee';
           request.dateOfBirth = '09/13/1990';
           request.emailId = 'llee1@test.com';
           request.zipCode = '99801';
           request.city = 'CAP';
           request.state = 'Alaska';
           request.externalId = '76HR753IK96438YRU';
           
           RestRequest req = new RestRequest();
           
           String JSONMsg = System.JSON.serialize(request);
           
           RestResponse res = new RestResponse();
           req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
           req.httpMethod = 'POST';
           req.requestBody = Blob.valueof(JSONMsg);
           RestContext.request = req;
           RestContext.response= res;
           
           Test.StartTest();   
           
           
           CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
           
           RestResponse response = RestContext.response;           
           
           CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
           res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
           System.assertEquals(res1.isSuccess, true);

           Test.StopTest();   
    	}	
    }
    
    @isTest static void testSingleMemberAccountService1()
    {   
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Jonas' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234589';  
            request.firstName = 'Jonas';
            request.lastName = 'Doe';
            request.dateOfBirth = '09/13/1990';
            request.emailId = 'abc@example.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true);
                       
            CRMRetailRegisterAccount_H_HUM ob = new CRMRetailRegisterAccount_H_HUM(request);
            ob.createAccount();
            System.assertNotEquals(ob, null);
            
            Test.StopTest();

            
        }
    }
    
     @isTest static void testSingleMemberAccountServ1()
    {   
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {   
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Jonas' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234589';  
            request.firstName = 'Jonas';
            request.lastName = 'Doe';
            request.dateOfBirth = '09/13/1990';
            request.emailId = 'llee1@test.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount';
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();
           
        }
    }
    
    @isTest static void testBlankEmailCheck()
    {   
        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {  
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Jonas' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234589';  
            request.firstName = 'Jonas';
            request.lastName = 'Doe';
            request.dateOfBirth = '09/13/1990';
            request.emailId = '';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();

        }
    }

    
    @isTest static void testAllNonMemberAccService()
    {      
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'John' Limit 2 ];
            System.assertEquals(lstAccount.size(), 2);
            
            lstAccount[0].PersonEmail = 'john1@test.com';
            update lstAccount[0];
            
            Id [] fixedSearchResults= new Id[2];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234578';  
            request.firstName = 'John';
            request.lastName = 'Well';
            request.dateOfBirth = '04/06/1992';
            request.emailId = 'john@test.com';
            request.zipCode = '99801';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount';
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true);

            Test.StopTest();
        }
    } 
    
     @isTest static void testAllNonMemberAccountService()
    {      
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {  
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'John' Limit 2 ];
            System.assertEquals(lstAccount.size(), 2);
            
            Id [] fixedSearchResults= new Id[2];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
                        
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234578';  
            request.firstName = 'John';
            request.lastName = 'Well';
            request.dateOfBirth = '04/06/1992';
            request.emailId = 'john@test.com';
            request.zipCode = '99801';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount';
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();
        }
    } 

    @isTest static void testSingleVisitorAccountService()
    {   
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {  
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Smith' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234567';  
            request.firstName = 'Smith';
            request.lastName = 'Woxer';
            request.dateOfBirth = '04/06/1985';
            request.emailId = 'swoxer@test.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();  
        }
    } 
    
     @isTest static void testSingleVisitorBlankEmail()
    {   
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Smith' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234567';  
            request.firstName = 'Smith';
            request.lastName = 'Woxer';
            request.dateOfBirth = '04/06/1985';
            request.emailId = '';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();  
        }
    } 
    
    @isTest static void testCreateAccountService()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {  
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Smith' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234567';  
            request.firstName = 'SMith';
            request.lastName = 'Woxer';
            request.dateOfBirth = '04/06/1985';
            request.emailId = 'abc@example.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 

            Test.StopTest(); 
        }
    }  
    
    @isTest static void testErrorService()
    {            
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {

            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            
            RestRequest req = new RestRequest();    
            RestResponse res = new RestResponse();
            
            String JSONMsg = '';
            
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount';  
            req.httpMethod = 'POST';
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM();    
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            
            Test.StopTest(); 
        }
    }    
        
    @isTest static void testAccessError()
    {            
        User oUser = [select Id,Name from User where name = 'test UserRetail1' limit 1]; 
        system.assertEquals(oUser.Name,'test UserRetail1');
        
        system.runAs(oUser)
        {
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234567';  
            request.firstName = 'SMith';
            request.lastName = 'Woxer';
            request.dateOfBirth = '04/06/1985';
            request.emailId = 'abc@example.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, false); 
            
        }
    }

    @isTest static void testAllMemberAccountService()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234578';  
            request.firstName = 'Luke';
            request.lastName = 'Lee';
            request.dateOfBirth = '09/13/1990';
            request.emailId = 'llee1@test.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest(); 
        }
    } 

    @isTest static void testAllMemberNotMatchingEmail()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {  
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234578';  
            request.firstName = 'Luke';
            request.lastName = 'Lee';
            request.dateOfBirth = '09/13/1990';
            request.emailId = 'llee2@test.com';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest(); 
        }
    } 
    
    @isTest static void testAllMemberBlankEmail()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
            
            List<Account> lstAccount = [Select Id from Account where FirstName = 'Luke' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.memberId = '1234578';  
            request.firstName = 'Luke';
            request.lastName = 'Lee';
            request.dateOfBirth = '09/13/1990';
            request.emailId = '';
            request.zipCode = '10001';
            request.city = 'CAP';
            request.state = 'Alaska';
            request.externalId = '76HR753IK96438YRU';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RegisterAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailRegisterAccount_I_HUM.registerNewParticipants();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM.class);
            System.assertEquals(res1.isSuccess, true); 
            Test.StopTest(); 
        }
    } 
    
}