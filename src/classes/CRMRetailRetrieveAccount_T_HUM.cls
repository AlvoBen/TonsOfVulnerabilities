/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailRetrieveAccount_T_HUM 
* Version          : 1.0
* Created Date     : 1st June, 2021
* Function         : This class is used as a test class for retrieve account rest service
* Test Class       : 
***********************************************************************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                                      06/01/2021            Original Version 2322478
* Ananya									  						09/02/2021		      Test Coverage Fix
* Ekta									  						    01/07/2022		      Remove Case Sensitivity
**********************************************************************************************************************************/

@isTest
public class CRMRetailRetrieveAccount_T_HUM {
    
    /**
    * This method is used to insert the data only once and called automatically in all methods 
    *
    * @param  None      
    * @return  Void
    */

	@testSetup static void setup()
    {
        
        List<User> lstUserToInsert = new List<User>();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','ETL API Access');
        lstUserToInsert.add(oUser);
        User oUserNotAccess = HUMTestDataHelperTest.createTestUser('UserRetail1','CRMRetail User');
        lstUserToInsert.add(oUserNotAccess);

        insert lstUserToInsert;
        System.assertEquals(lstUserToInsert.size(), 2);
        
        PermissionSet ps = [SELECT Id, Name FROM PermissionSet WHERE Name = 'CRMRetail_300_BusinessAdministrator'];
        System.assertEquals(ps.Name, 'CRMRetail_300_BusinessAdministrator');
        
		PermissionSetAssignment objPermSet = new PermissionSetAssignment(AssigneeId = lstUserToInsert[1].id, PermissionSetId = ps.Id);
        insert objPermSet;
        System.assertEquals(objPermSet.AssigneeId, lstUserToInsert[1].id);

        Id recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
        Id recordTypeMemId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId();
            
		System.runAs(oUserNotAccess) 
        {
            List<Account> lstOfAccounts = new List<Account>();

            Account objMem = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1985','Jonas','Smith','12209','a@test.com','','arizona',false);
            lstOfAccounts.add(objMem);
            
            Account objMemb = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1985','Jonas','Smith','12209','a@test.com','','arizona',false);
            lstOfAccounts.add(objMemb);
            
            Account objMembAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1985','Jonas','Well','12209','a@test.com','','arizona',false);
            lstOfAccounts.add(objMembAcc);
            insert lstOfAccounts;
            
            System.assertEquals(lstOfAccounts.size(),3);
        }
        
        System.runAs(oUser) 
        {
            
            List<Account> lstVisitorAcc = new List<Account>();
            
            
			//Member Account
            Account objMemberAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1985','Smith','Woxer','12209','a@test.com','','arizona',false);
            objMemberAcc.Gender__c = 'Female';
            objMemberAcc.PersonMailingStreet = 'Street 9';
            objMemberAcc.PersonOtherPhone = '9999998888';
            objMemberAcc.PersonHomePhone = '88888888888';
            objMemberAcc.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objMemberAcc);
			
			//Vistor Account For Multiple Cases
			Account objVisitorAc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1985','Roger','Walter','12209','','','',false);
            lstVisitorAcc.add(objVisitorAc);
            
			Account objVisitorAccount = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1985','ALEX','Walter','12209','a@test.com','','',false);
            lstVisitorAcc.add(objVisitorAccount);
            
			Account objVisitorAccount1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1985','Roger','Walter','88807','','','',false);
            objVisitorAccount1.PersonMailingStreet = 'Street 9';
            lstVisitorAcc.add(objVisitorAccount1);
            
			Account objVisitorAccount2 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1985','Roger','Walter','88806','','','',false);
            objVisitorAccount2.CRMRetailNotificationOptOut__c = 'Veteran';
			lstVisitorAcc.add(objVisitorAccount2);
            
            Account objAccBlankEmail = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'02/02/1991','Atest','Mem','','','','',false);
            objAccBlankEmail.Gender__c = 'Female';
            objAccBlankEmail.PersonMailingStreet = 'Street 9';
            objAccBlankEmail.PersonOtherPhone = '9999998888';
            objAccBlankEmail.PersonHomePhone = '88888888888';
            objAccBlankEmail.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objAccBlankEmail);
            
            Account objVisitorAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'02/02/1991','Atest','Mem','','','','',false);
            lstVisitorAcc.add(objVisitorAcc);
            
            //Account Without Member Id
            Account objMemberAccn = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1985','ALEX','Walter','12209','a@test.com','','arizona',false);
            objMemberAccn.Gender__c = 'Female';
            objMemberAccn.PersonMailingStreet = 'Street 9';
            objMemberAccn.PersonOtherPhone = '9999998888';
            objMemberAccn.PersonHomePhone = '88888888888';
            objMemberAccn.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objMemberAccn);
            
            Account objMemberAccnn = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1986','Ross','Walter','12209','a@test.com','','arizona',false);
            objMemberAccnn.Gender__c = 'Female';
            objMemberAccnn.PersonMailingStreet = 'Street 9';
            objMemberAccnn.PersonOtherPhone = '9999998888';
            objMemberAccnn.PersonHomePhone = '88888888888';
            objMemberAccnn.CRMRetailNotificationOptOut__c = 'Veteran';
            objMemberAccnn.PersonMailingCity = 'New York';
            lstVisitorAcc.add(objMemberAccnn);
            
            Account objVisitorAccEmail = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1986','Bradley','Walter','','a@test.com','','',false);
            objVisitorAccEmail.Gender__c = 'Male';
            objVisitorAccEmail.PersonMailingStreet = 'Street 12';
            objVisitorAccEmail.PersonOtherPhone = '9999998888';
            objVisitorAccEmail.PersonHomePhone = '88888888888';
            objVisitorAccEmail.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objVisitorAccEmail);
            
			Account objVisitorAccc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1986','Bradley','Walter','','a@test.com','','',false);
            lstVisitorAcc.add(objVisitorAccc);
            
            Account objVisitorWithoutEmail = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1986','Bradley','Walter','','','','',false);
            lstVisitorAcc.add(objVisitorWithoutEmail);
            
            Account objAccSingleZip = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1986','Shawn','Mendes','12210','a@test.com','','',false);
            objAccSingleZip.Gender__c = 'Male';
            objAccSingleZip.PersonMailingStreet = 'Street 12';
            objAccSingleZip.PersonOtherPhone = '9999998888';
            objAccSingleZip.PersonHomePhone = '88888888888';
            objAccSingleZip.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objAccSingleZip);
            
			Account objVistZip = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1986','Shawn','Mendes','12210','a@test.com','','',false);
            lstVisitorAcc.add(objVistZip);
            
            Account objVisitorWithoutZip = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1986','Shawn','Mendes','','a@test.com','','',false);
            lstVisitorAcc.add(objVisitorWithoutZip);
            
			Account objMemberAccnn1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1987','Bradley','Cooper','12209','ac@test.com','','arizona',false);
            objMemberAccnn1.Gender__c = 'Female';
            objMemberAccnn1.PersonMailingStreet = 'Street 9';
            objMemberAccnn1.PersonOtherPhone = '9999998888';
            objMemberAccnn1.PersonHomePhone = '88888888888';
            objMemberAccnn1.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objMemberAccnn1);
            
			Account objVisitorAcccnn = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1987','Bradley','Cooper','12209','ac@test.com','','',false);
            lstVisitorAcc.add(objVisitorAcccnn);
            
			Account objVisitorAcccn = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1987','Bradley','Cooper','12209','ac@test.com','','',false);
            lstVisitorAcc.add(objVisitorAcccn);
            
			Account objMemberAct = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1987','Jen','Cooper','12209','ac@test.com','','arizona',false);
            objMemberAct.Gender__c = 'Female';
            objMemberAct.PersonMailingStreet = 'Street 9';
            objMemberAct.PersonOtherPhone = '9999998888';
            objMemberAct.PersonHomePhone = '88888888888';
            objMemberAct.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objMemberAct);
            
			Account objVisitorAct = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1987','Jen','Cooper','12209','ac@test.com','','',false);
            lstVisitorAcc.add(objVisitorAct);
            
			Account objVisitorActt = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1987','Jen','Cooper','12209','ac@test.com','','',false);
            lstVisitorAcc.add(objVisitorActt);
            
            Account objAccWithInt = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1986','Camilla','Mendes','12210','a@test.com','','',false);
            objAccWithInt.Gender__c = 'Male';
            objAccWithInt.PersonMailingStreet = 'Street 12';
            objAccWithInt.PersonOtherPhone = '9999998888';
            objAccWithInt.PersonHomePhone = '88888888888';
            objAccWithInt.CRMRetailNotificationOptOut__c = 'Veteran';
            lstVisitorAcc.add(objAccWithInt);
            
			Account objVistInt = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1986','Camilla','Mendes','12210','a@test.com','','',false);
            lstVisitorAcc.add(objVistInt);
            
            Account objVisitorWithoutInt = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1986','Camilla','Mendes','12210','a@test.com','','',false);
            lstVisitorAcc.add(objVisitorWithoutInt);
            
            Account objVisitorAccMemEmail = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeMemId,'04/06/1986','Jenice','Looper','','j@test.com','','',false);
            lstVisitorAcc.add(objVisitorAccMemEmail);
            
            Account objVisitorAccDiffEmail = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1986','Jenice','Looper','','jl@test.com','','',false);
            lstVisitorAcc.add(objVisitorAccDiffEmail);
			
			insert lstVisitorAcc;
            System.assertEquals(lstVisitorAcc.size(),26);
            
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Social');
            insert objIntType;
           	System.assertEquals(objIntType.Name,'Social');
            
            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Event-Poverty',null);
            objIntReason.Interaction_Type__c = objIntReason.Id;
            insert objIntReason;
            System.assertEquals(objIntReason.Name,'Event-Poverty' );
            
            Storefront_Location__C objlocation = CRMRetail_TestDataFactory_H_HUM.createLocation('Virginia','Street no. 1','Texas','12345','83684','california',23);
            insert objlocation;
            System.assertEquals(objlocation.Name, 'Virginia');
         	
            Storefront_Interaction__c objInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstVisitorAcc[22].Id, objlocation.Id, objIntReason.Id, false, '', Date.Today());
			insert objInteraction;
            System.assertEquals(objInteraction.isAllDayEvent__c, false);
            
            Policy__c objPolicy = CRMRetail_TestDataFactory_H_HUM.createPolicy(lstVisitorAcc[0].Id,'');
            
            Policy_Member__c objPolicyMem = CRMRetail_TestDataFactory_H_HUM.createPolicyMember(lstVisitorAcc[0].Id,objPolicy.Id,'policyMem123','','',false);
           
            Member_ID__c objMemId = CRMRetail_TestDataFactory_H_HUM.createMemberId(objPolicyMem.Id,'H23565','',objPolicy.Id,'','');

            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new  CRMFunctionality_ONOFF_Switch__c(Name = '2824434', IsON__c = True);
            insert csCRMOnOffSwitch;
            System.assertEquals(csCRMOnOffSwitch.Name,'2824434');
        }
    }   
    
    /**
	* testRetailRetrieveAccountVNC Method.
	* <p>
	* This method is used to test the response for account record type as  'CRMRetail Vistior'.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
    
    @isTest static void testRetailRetrieveAccountVNC()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {

            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR Limit 1 ];
            System.assert(lstAccount.size() == 1);
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = lstAccount[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest();
        }
        
    }
    
    /**
	* testRetailRetrieveAccountMem Method.
	* <p>
	* This method is used to test the response for account record type as  'Member'.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/
    @isTest static void testRetailRetrieveAccountMem()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {

            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingCity, PersonMailingState, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER AND lastName = 'Woxer' Limit 1 ];
            System.assert(lstAccount.size() == 1); 
            
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = lstAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = lstAccount[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true); 
            
            Test.StopTest();
        }
    }

    /**
	* testSwitchVNCCreate Method.
	* <p>
	* This method is used to test update the retail member account if multiple account found on the basis of filters.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/        
    
    @isTest static void testSwitchVNCCreate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<CRMFunctionality_ONOFF_Switch__c> lstSwitch = [Select Name, IsON__c from CRMFunctionality_ONOFF_Switch__c where name = '2824434' LIMIT 1];
            System.assertEquals(lstSwitch.size(),1);
            
            lstSwitch[0].IsON__c = false;
            update lstSwitch[0];
            System.assertEquals(lstSwitch[0].IsON__c,false);
            
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Atest'];
            System.assert(lstAccount.size() == 2); 
                       
            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }
            
            Id [] fixedSearchResults= new Id[2];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest();
        }
    }
    
    /**
	* testRetailRetrieveAccountCatch Method.
	* <p>
	* This method is used to test the exception part in CRMRetailRetrieveAccount_H_HUM.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/	
    
    @isTest static void testRetailRetrieveAccountCatch()
    {        
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {

            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = '001K000001em1M5ICI';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, false); 
            
            Test.StopTest();
        }
    }
    
    /**
	* testRetailRetrieveAccountFault Method.
	* <p>
	* This method is used to test the exception part in CRMRetailRetrieveAccount_I_HUM.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/	
    
    @isTest static void testRetailRetrieveAccountFault()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {

            RestRequest req = new RestRequest();    
            RestResponse res = new RestResponse();
            String JSONMsg = '';
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAcc';  
            req.httpMethod = 'POST';
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, false); 
            
            Test.StopTest();
        }
    }
    
    /**
	* testRetailVNCAsMemUpdate Method.
	* <p>
	* This method is used to test update the retail member account if multiple account found on the basis of filters.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/        
    
    @isTest static void testRetailVNCAsMemUpdate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'ALEX' ];
            System.assert(lstAccount.size() == 2); 
            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }
            
            Id [] fixedSearchResults= new Id[2];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest();
        }
    }
	
	/**
	* testRetailVNCCreate Method.
	* <p>
	* This method is used to test the creation of the Account with record Type as CRM Retail Visitor.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/        
    
    @isTest static void testRetailVNCCreate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, RecordType.Name from Account where RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER AND FirstName = 'Ross' ];
            System.assert(lstAccount.size() == 1);
                       
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = lstAccount[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg); 
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true); 
            
            Test.StopTest(); 
        }
    }
	
    /**
	* testVNCCreate Method.
	* <p>
	* This method is used to test update the retail member account if multiple account found on the basis of filters.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/        
    
    @isTest static void testVNCCreate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, RecordType.Name from Account where FirstName = 'Atest']; 
            System.assert(lstAccount.size() == 2);                                    
            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }
            Id [] fixedSearchResults= new Id[2];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest();
        }
    }
    
    /**
	* testRetailVNCMultipleZip Method.
	* <p>
	* This method is used to test the multiple zip scenario.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/  
	
	@isTest static void testRetailVNCMultipleZip()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'Jen' AND LastName ='Cooper'];
            System.assert(lstAccount.size() == 3); 

            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }            
            
            Id [] fixedSearchResults= new Id[3];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            fixedSearchResults[2] = lstAccount[2].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
           
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true); 
            
            Test.StopTest(); 
        }
    }
    
	/**
	* testRetailVNCSingleEmail Method.
	* <p>
	* This method is used to test the single email scenario.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
	
	@isTest static void testRetailVNCSingleEmail()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'Bradley'AND LastName ='Walter'];
            System.assert(lstAccount.size() == 3); 

            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }            
            
            Id [] fixedSearchResults= new Id[3];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            fixedSearchResults[2] = lstAccount[2].Id;
            Test.setFixedSearchResults(fixedSearchResults);
        
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest(); 
        }
    }
    
    /**
	* testRetailVNCSingleRecord Method.
	* <p>
	* This method is used to test the single record where email are not equal scenario.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
	
	@isTest static void testRetailVNCSingleRecord()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'Jenice' AND LastName ='Looper'];
            System.assert(lstAccount.size() == 2); 

            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }            
            
            Id [] fixedSearchResults= new Id[2];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            Test.setFixedSearchResults(fixedSearchResults);
        	
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  //Request URL
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest(); 
        }
    }
    
	/**
	* testRetailVNCMultipleEmail Method.
	* <p>
	* This method is used to test the multiple email scenario.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
	
	@isTest static void testRetailVNCMultipleEmail()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'Bradley'AND LastName ='Cooper'];
            
            System.assert(lstAccount.size() == 3); 
            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }            
            
            Id [] fixedSearchResults= new Id[3];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            fixedSearchResults[2] = lstAccount[2].Id;
            Test.setFixedSearchResults(fixedSearchResults);
           
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest(); 
        }
    }
    
    /**
	* testRetailVNCSingleZip Method.
	* <p>
	* This method is used to test the multiple email scenario.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
	
	@isTest static void testRetailVNCSingleZip()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'Shawn'AND LastName ='Mendes'];       
            System.assert(lstAccount.size() == 3); 
            String accId = '';
            for(Account ob : lstAccount){
                if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                	accId = ob.Id;
            }            
            
            Id [] fixedSearchResults= new Id[3];
            fixedSearchResults[0] = lstAccount[0].Id;
            fixedSearchResults[1] = lstAccount[1].Id;
            fixedSearchResults[2] = lstAccount[2].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest(); 
        }
    }
    
    /**
	* testVNCAccessIssue Method.
	* <p>
	* This method is used to test access issue.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
	
	@isTest static void testVNCAccessIssue()
    {
        User oUser = [select Id,Name from User where name = 'test UserRetail1' limit 1]; 
        system.assertEquals(oUser.Name,'test UserRetail1');
        
        List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER AND FirstName = 'Jonas'AND LastName ='Smith' Limit 1 ];
        System.assertEquals(lstAccount.size(), 1);
        
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = lstAccount[0].Id;
        Test.setFixedSearchResults(fixedSearchResults);        
        String permSetId = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_300_BusinessAdministrator' LIMIT 1].Id;
        System.assertNotEquals(permSetId,Null);
        
        if(permSetId != null)
        {
			List<PermissionSetAssignment> lstPermissionSetAssignment = [SELECT Id from PermissionSetAssignment WHERE PermissionSetId = : permSetId AND AssigneeId = : oUser.Id];
            System.assertNotEquals(lstPermissionSetAssignment,Null);
            
            if(lstPermissionSetAssignment != null && !lstPermissionSetAssignment.isEmpty())
            {
                delete lstPermissionSetAssignment;   
            }  
        }
        
        system.runAs(oUser)
        {   
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = lstAccount[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, false);
            
            Test.StopTest(); 
        }
    }
    
    /**
	* testVNCAccessIssues Method.
	* <p>
	* This method is used to test access issue.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
    
    @isTest static void testVNCAccessIssues()
    {
        User oUser = [select Id,Name from User where name = 'test UserRetail1' limit 1]; 
        system.assertEquals(oUser.Name,'test UserRetail1');
        
        List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER AND FirstName = 'Jonas' AND LastName ='Well' Limit 1 ];
        System.assertEquals(lstAccount.size(), 1);
        
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = lstAccount[0].Id;
        Test.setFixedSearchResults(fixedSearchResults);        
        String permSetId = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_300_BusinessAdministrator' LIMIT 1].Id;
        System.assertNotEquals(permSetId,Null);
        
        if(permSetId != null)
        {
			List<PermissionSetAssignment> lstPermissionSetAssignment = [SELECT Id from PermissionSetAssignment WHERE PermissionSetId = : permSetId AND AssigneeId = : oUser.Id];
            System.assertNotEquals(lstPermissionSetAssignment,Null);
            
            if(lstPermissionSetAssignment != null && !lstPermissionSetAssignment.isEmpty())
            {
                delete lstPermissionSetAssignment;   
            } 
        }
        
        system.runAs(oUser)
        {   
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = lstAccount[0].Id;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, false);
            
            Test.StopTest(); 
        }
    }
    
    /**
	* testVNCAccountWithInteraction Method.
	* <p>
	* This method is used to test the multiple email scenario.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/ 
	
	@isTest static void testVNCAccountWithInteraction()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        List<Account> lstAccount = [Select id, FirstName, LastName, Birthdate__c, CRMRetailNotificationOptOut__c, PersonEmail, PersonHomePhone, PersonOtherPhone, PersonMailingStreet, PersonMailingState, Gender__c, PersonMailingCity, PersonMailingPostalCode, RecordType.Name from Account where FirstName = 'Camilla'AND LastName ='Mendes'];
        System.assert(lstAccount.size() == 3); 
        String accId = '';
        for(Account ob : lstAccount){
            if(ob.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                accId = ob.Id;
        }
        Id [] fixedSearchResults= new Id[3];
        fixedSearchResults[0] = lstAccount[0].Id;
        fixedSearchResults[1] = lstAccount[1].Id;
        fixedSearchResults[2] = lstAccount[2].Id;
        Test.setFixedSearchResults(fixedSearchResults);
        
        system.runAs(oUser)
        {   
            CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.accountId = accId;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/RetrieveAccount';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailRetrieveAccount_I_HUM.retrieveAccount();
            RestResponse response = RestContext.response;
            CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM retrieveAccount = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM();
            retrieveAccount = (CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM.class);
            System.assertEquals(retrieveAccount.isSuccess, true);
            
            Test.StopTest(); 
        }
    }

}