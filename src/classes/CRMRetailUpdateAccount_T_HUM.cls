/******************************************************************************************************************
Apex class Name    : CRMRetailUpdateAccount_T_HUM
Version            : 1.0
Function           : This class is used as test class for update account Rest Service
Created On         : June 01 ,2021
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Ekta Bist                                       06/01/2021          Original Version 2322492
*   2.0            Akshay                                          09/03/2021          Test Coverage Fix
------------------------------------------------------------------------------------------------------------------- */

@isTest
private class CRMRetailUpdateAccount_T_HUM 
{	
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
      
        System.runAs(oUser) 
        {
                       
            //Account
            List<RecordType> listRecordType = [SELECT Name, SobjectType,IsPersonType FROM RecordType WHERE (Name = 'CRMRetail Visitor' OR Name = 'Member') AND SobjectType='Account' AND IsPersonType = True ORDER BY Name ASC];
			System.assertEquals(listRecordType.size(), 2);
           
            List<Account> listAccount = new List<Account>();
            
            Account objVisitorAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[1].Id,'02/02/1991','John','Doe','99801','johndoe@test.com','Juneau','Alaska',false);    
            listAccount.add(objVisitorAcc);            
            
            Account objMemberAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(listRecordType[0].Id,'04/06/1985','Smith','Woxer','99801','swoxer@test.com','Juneau','Alaska',false);
            listAccount.add(objMemberAcc);
            
            insert listAccount; 
            system.assertEquals(listAccount.size(), 2);            
        }
    }
    
    @isTest static void testUpdateVisitorAccountService()
    {        
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name = 'CRMRetail Visitor' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.accountId = lstAccount[0].Id ;   
            request.firstName = 'Test';
            request.lastName = '123';
            request.dateOfBirth = '09/09/1998';
            request.emailId = 'a@a.com';
            request.zipCode = '12133';
            request.city = 'CAP';
            request.state = lstAccount[0].PersonMailingState;
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UpdateAccount';
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateAccount_I_HUM.updateAccount();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM.class);
            system.assertEquals(res1.isSuccess, true); 
            
            Test.StopTest();
        }
    }  
    
     @isTest static void testUpdateAccountService()
     {        
         User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
         system.assertEquals(oUser.Name,'test User2');
         
         system.runAs(oUser)
         {
             
             List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name = 'CRMRetail Visitor' Limit 1 ];
             System.assertEquals(lstAccount.size(),1);
             
             CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM(); 
             request.sourceSystem = 'VNC';
             request.accountId = 'false';   
             request.firstName = lstAccount[0].FirstName; 
             request.lastName = lstAccount[0].LastName;
             request.dateOfBirth = lstAccount[0].Birthdate__c;
             request.emailId = lstAccount[0].PersonEmail; 
             request.zipCode = lstAccount[0].PersonMailingPostalCode;
             request.city = lstAccount[0].PersonMailingCity;
             request.state = lstAccount[0].PersonMailingState;
             
             RestRequest req = new RestRequest();
             
             String JSONMsg = System.JSON.serialize(request);
             
             RestResponse res = new RestResponse();
             req.requestURI = '/CRMRetailServices_V1.0/UpdateAccount';
             req.httpMethod = 'POST';
             req.requestBody = Blob.valueof(JSONMsg);
             RestContext.request = req;
             RestContext.response= res;
             
             Test.StartTest();               
             
             CRMRetailUpdateAccount_I_HUM.updateAccount();
             
             RestResponse response = RestContext.response;           
             
             CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM(); 
             res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM.class);
             system.assertEquals(res1.isSuccess,false); 
             
             Test.StopTest();
         }
    } 
    
     @isTest static void testUpdateMemberAccountService()
    { 
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        { 
           
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name != 'CRMRetail Visitor' Limit 1 ];
            System.assertEquals(lstAccount.size(),1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.accountId = lstAccount[0].Id ;   
            request.firstName =lstAccount[0].FirstName;
            request.lastName =lstAccount[0].LastName;
            request.dateOfBirth = lstAccount[0].Birthdate__c;
            request.emailId = lstAccount[0].PersonEmail;
            request.zipCode = lstAccount[0].PersonMailingPostalCode;
            request.city = lstAccount[0].PersonMailingCity;
            request.state = lstAccount[0].PersonMailingState;
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UpdateAccount';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateAccount_I_HUM.updateAccount();
            
            RestResponse response = RestContext.response;           
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM.class);
            system.assertEquals(res1.isSuccess, false); 
            
            Test.StopTest();
        }
    } 
    
    
     @isTest static void testUpdateMemberAccountService1()
    {          
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {
            
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name != 'CRMRetail Visitor' Limit 1 ];
            System.assertEquals(lstAccount.size(), 1); 
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM(); 
            
            RestRequest req = new RestRequest();    
            RestResponse res = new RestResponse();
            
            String JSONMsg = '';
            
            req.requestURI = '/CRMRetailServices_V1.0/UpdateAccount'; 
            req.httpMethod = 'POST';
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateAccount_I_HUM.updateAccount();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM();    
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM.class);
            system.assertEquals(res1.isSuccess, false); 
            
            Test.StopTest();
        }
    }  
    
     @isTest static void invalidMemberIdTest()
    {          
        User oUser = [select Id,Name from User where name = 'test User2' limit 1]; 
        system.assertEquals(oUser.Name,'test User2');
        
        system.runAs(oUser)
        {   
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUpdateAccountRequest_DTO_HUM(); 
            request.sourceSystem = 'VNC';
            request.accountId = '001c00000091ABCDEF' ;   
            request.firstName = 'Test';
            request.lastName = '123';
            request.dateOfBirth = '09/09/1998';
            request.emailId = 'a@a.com';
            request.zipCode = '12133';
            request.city = 'CAP';
            request.state = '';
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UpdateAccount';
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUpdateAccount_I_HUM.updateAccount();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM res1 = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM(); 
            res1 = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUpdateAccountResponse_DTO_HUM.class);
            system.assertEquals(res1.isSuccess, false); 
            
            Test.StopTest();
        }
    } 
}