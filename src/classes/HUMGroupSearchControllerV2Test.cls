/**************************************************************************************************************************************
Apex Class Name : HUMGroupSearchControllerV2Test
Version         : 2.0
Created On      : 07/31/2015
Function        : Test class for HUMGroupSearchControllerV2

* Modification Log:
*   Version Number        Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
*    2.1                  Shreya Choodamani              25261               07/31/2015                 Implementing Async Webservice Callout
*    2.2                  Vishal Verma                   26498               09/18/2015                 Added @testSetup for creating test data.
*    2.3                  Vishal Verma                   26498               09/23/2015                 Fixed Review Comments
*    2.3                  Vishal Verma                   26498               09/23/2015                 Removed commented code
*    2.4                  Shruthi Karanth                84636               04/14/2017                 Softphone Optimization to skip DPaaS callout when coming through softphone routing
*    2.5                  Sunil Bukya                    260674              07/23/2018                 REQ - 362070 - Implemented Group Name platform encryption changes.
*    2.6                  Sunil Bukya                                        18/12/2018                 Removed cipherCloud encryption call
*    2.7                  Deepankar Binjola                                  04/03/2019                 REQ - 386854 The info in the Provider and unkonwn Provider pop up are not cleared
*    2.8                  Santhi Mandava                 355559              05/10/2019                 Removed "ENCRYPTIONTYPE" custom setting creation
*    2.9                  Ajay Chakradhar                                    08/14/2020                 REQ - 1185083 SoftPhoneSearch
*    3.0                  Santhi Mandava                                     03/07/2024                 User Story 5303730: T1PRJ0891742 - C01; Consumer Management - Classic VIT Remediation Search pages
****************************************************************************************************************************************/

@isTest
global class HUMGroupSearchControllerV2Test
{    
    
    /*
    * Method Name : setupTestData
    * Description : Method to set up test data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void setupTestData()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        User u1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert u1;
        system.assertNotEquals(u, NULL, 'Value not matched');
        system.assertNotEquals(u1, NULL, 'Value not matched');

        system.runas(u)
        {
            HUMMember_search_state_mapping__c memState = new HUMMember_search_state_mapping__c( Name = 'Alabama' , State__c = 'Alabama',State_Code__c='AL');
            insert memState;
            system.assertEquals(memState.id!=null,true);
            HUM_Dpass_Callout__c dpass = new HUM_Dpass_Callout__c( Name = 'DPAAS GROUP' , Timeout__c = 120, Active__c = true);
            insert dpass;
            system.assertEquals(dpass.id!=null,true);
            HUMSearchLimit__c searchLimit = new HUMSearchLimit__c(Name='Member_SearchLimit',Search_Limit__c=10);
            insert searchLimit;
            system.assertEquals(searchLimit.id!=null,true);
            HUM_Webservice_Callout__c webServ =  new HUM_Webservice_Callout__c( Name = 'GroupSearchService' , Certificate_Name__c = 'TestCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
            insert webServ;
            system.assertEquals(webServ.id!=null,true);

            CRMFunctionality_ONOFF_Switch__c crmONOFF = new CRMFunctionality_ONOFF_Switch__c(Name='1185083',IsON__c=true);
            insert crmONOFF;
            system.assertEquals(crmONOFF.id!=null,true);

            HUMCipherCloudSettings__c ccData = new HUMCipherCloudSettings__c(Name='APPLICATION_NAME',Value__c = 'CRMS TESTING');
            insert ccData;
            system.assertEquals(ccData.id!=null,true);
            
            SOFTPHONE_PRPID_HUM__c oPRPId1 = new SOFTPHONE_PRPID_HUM__c( Name = 'GroupNumber' , Value__c = 'Test');
            insert oPRPId1;
            system.assertEquals(oPRPId1.id!=null,true);
            
            SOFTPHONE_PRPID_HUM__c oPRPId2 = new SOFTPHONE_PRPID_HUM__c( Name = 'GroupNumber2' , Value__c = 'Test');
            insert oPRPId2;
            system.assertEquals(oPRPId2.id!=null,true);
            
            //Load all Constants data
            HUMTestDataHelperTest.getHUMConstantsData();
            
            List<Account> lstacc = new List<Account>();
            RecordType PersonAccountRecordType = [Select Id from RecordType where Name = 'Group' and sObjectType='Account'];
            RecordType PersonUnknownAccountRecordType = [Select Id from RecordType where Name = 'Unknown Group' and sObjectType='Account'];
            for(integer i=1;i<11;i++)
            {
                Account acc = HUMTestDataHelperTest.getAccount();
                acc.RecordType = PersonAccountRecordType;
                acc.Name = 'TestGroup00'+i;
                acc.Group_Number__c = '00'+i;
                acc.BillingState = 'Alabama';
                acc.Home_Office_Account__c = true;
                lstacc.add(acc);
            }
            insert lstacc;
            system.assertEquals(lstacc.size() == 10,true);

            lstacc = new List<Account>();
            Account acc1 = new Account();
            acc1 = HUMTestDataHelperTest.getAccount();
            acc1.RecordTypeId = PersonUnknownAccountRecordType.Id;
            acc1.Name = 'Humana UM';
            acc1.Group_Number__c = '123456';
            acc1.BillingState = 'Kentucky';
            acc1.General_Account__c  = true;
            acc1.Tenant_Id__c   = '00';
            lstacc.add(acc1);
            acc1 = new Account();
            acc1 = HUMTestDataHelperTest.getAccount();
            acc1.RecordTypeId = PersonUnknownAccountRecordType.Id;
            acc1.Name = 'Humana UM New';
            acc1.Group_Number__c = '67890';
            acc1.BillingState = 'Kentucky';
            acc1.General_Account__c  = true;
            acc1.Tenant_Id__c   = '00';
            lstacc.add(acc1);
            acc1 = new Account();
            acc1 = HUMTestDataHelperTest.getAccount();
            acc1.RecordTypeId = PersonUnknownAccountRecordType.Id;
            acc1.Name = 'Humana UM Old';
            acc1.Group_Number__c = '654321';
            acc1.BillingState = 'Kentucky';
            acc1.General_Account__c  = true;
            acc1.Tenant_Id__c   = '00';
            lstacc.add(acc1);
            insert lstacc;
            system.assertEquals(lstacc.size(),3);
        }
    }
     
    /*
    * Method Name : testLimitHundred
    * Description : This method is used for testing the hundred limit
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testLimitHundred() 
    {    
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {            
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                grpController.responsedto = null;
                Account acc = [Select Id from Account limit 1];
                for(integer i = 0 ; i<200 ; i++)
                {
                    grpController.mapRecordData.put(i, acc);
                }
                grpController.processResponse();
                
            Test.stopTest();
            grpController.Reset();
            System.assert(true,grpController.lstSObjects.size() == 0);
        }
    }
    
     
    /*
    * Method Name : testGroupServiceInternal
    * Description : This method is used for group search within salesforce
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testGroupServiceInternal() 
    {    
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                
                grpController.oSearchWrapper.sGroupNumber = '004';
                grpController.oSearchWrapper.sGroupName = 'TestGroup004';
                grpController.oSearchWrapper.sState = 'AL';
                
                grpController.bIsUnknownGroupSearch = false;
                grpController.groupsearch();
                
                grpController.oSearchWrapper.sState = 'NEW';
                grpController.Reset();
            Test.stopTest();
            
            System.assert(true,grpController.lstSObjects.size() == 0);    
            
        }
    }  
    
    
    static testMethod void testGroupServiceInternalNegative() 
    {    
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                
                grpController.oSearchWrapper.sGroupNumber = '004';
                grpController.oSearchWrapper.sGroupName = 'TestGroup004';
                grpController.oSearchWrapper.sState = 'AL';
                
                grpController.bIsUnknownGroupSearch = false;
                grpController.groupsearch();
                
                grpController.oSearchWrapper.sState = 'NEW';
                grpController.Reset();
            Test.stopTest();
            
            System.assert(true,grpController.lstSObjects.size() == 0);    
            
        }
    }  
    
    
    
    
    
    /*
    * Method Name : testGroupServiceExternal
    * Description : This method is used for group search exteranl to salesforce
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testGroupServiceExternal() 
    {    
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            List<HUMGroupSearchServiceHelper.ResponseGroupSearch> lstResponseGroupSearch= new List<HUMGroupSearchServiceHelper.ResponseGroupSearch>();
            HUMGroupSearchServiceHelper.ResponseGroupSearch responseGroupSearch = new HUMGroupSearchServiceHelper.ResponseGroupSearch();
            responseGroupSearch.GroupName='test GroupName';
            lstResponseGroupSearch.add(responseGroupSearch);
              
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
            
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            
            Test.startTest();
                grpController.oSearchWrapper.sGroupNumber = 'CD066801';
                grpController.oSearchWrapper.sGroupName = 'mockGroup';
                grpController.oSearchWrapper.sState = 'AL';  
                grpController.bIsUnknownGroupSearch=null;
                grpController.unknownGroupSearch();
                grpController.responseDTO = lstResponseGroupSearch;
                grpController.processResponse();
            Test.stopTest();
            System.assert(true,grpController.lstSObjects.size() == 0);  
        }
    }
    
     /*
    * Method Name : testGroupServiceNoInput
    * Description : This method is used for group search exteranl to salesforce
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testGroupServiceNoInput() 
    {    
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                grpController.bIsUnknownGroupSearch = false;
                grpController.groupsearch();
            Test.stopTest();
            System.assert(true,grpController.lstSObjects.size() == 0);    
            
        }
    }


   /* Method Name : testGroupSearchValidationNegative
    * Description : This method is used for testing validations
    * Return type : NA
    * Paramater   : NA
   */
    static testMethod void testGroupSearchValidationNegative() 
    {
        //Deleting Custom Settings to test Exceptions
        HUM_Dpass_Callout__c dpassCS = HUM_Dpass_Callout__c.getValues('DPAAS GROUP');
        delete dpassCS;
        system.assert(HUM_Dpass_Callout__c.getAll().size() == 0);
        HUM_Webservice_Callout__c serviceCS = HUM_Webservice_Callout__c.getValues('GroupSearchService');
        delete serviceCS;
        system.assert(HUM_Webservice_Callout__c.getAll().size() == 0);
        HUMSearchLimit__c searchLimit =  HUMSearchLimit__c.getValues('Member_SearchLimit');
        delete searchLimit;
        system.assert(HUMSearchLimit__c.getAll().size() == 0);
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        system.runAs(u)
        {
            List<HUMGroupSearchServiceHelper.ResponseGroupSearch> lstResponseGroupSearch= new List<HUMGroupSearchServiceHelper.ResponseGroupSearch>();
            HUMGroupSearchServiceHelper.ResponseGroupSearch responseGroupSearch = new HUMGroupSearchServiceHelper.ResponseGroupSearch();
            responseGroupSearch.GroupName='test GroupName';
            lstResponseGroupSearch.add(responseGroupSearch); 
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
            
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            
            Test.startTest();
                grpController.oSearchWrapper.sGroupNumber = '!@#$%^';
                grpController.oSearchWrapper.sGroupName = 'mockGroup';
                grpController.oSearchWrapper.sState = 'ALui';  
                grpController.bIsUnknownGroupSearch=null;
                grpController.unknownGroupSearch();
                grpController.groupsearch();
                //grpController.invokeGroupService();
                grpController.buildUnknownAccountMap(null);
                grpController.mapRecordData= new Map<Integer,Account>();
                grpController.processResponse();
                grpController.callService();
            Test.stopTest();
            System.assert(true,grpController.lstSObjects.size() == 0);  
        }
    }
    
    /*
    * Method Name : testGroupServiceSoftPhone
    * Description : This method is used for group search from softphone
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testGroupServiceSoftPhone() 
    {    
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
            Map<String,String> mapSoftPhone = new Map<String,String>{'Test' => 'Test'};
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                
                grpController.oSearchWrapper.mapEncryptedValues = mapSoftPhone;
                grpController.oSearchWrapper.sGroupNumber = '004';
                grpController.oSearchWrapper.sGroupName = 'TestGroup004';
                grpController.oSearchWrapper.sState = 'AL';
                
                grpController.bIsUnknownGroupSearch = false;                
                grpController.callFromSoftPhone();
                grpController.groupsearch();

                grpController.oSearchWrapper.sGroupNumber = '';
                grpController.oSearchWrapper.sGroupName = 'Humana UM';
                grpController.oSearchWrapper.sState = 'KY';
                List<Account> lstAccs = [Select Id,Name from Account Where Name='Humana UM' Limit 1];
                Id [] fixedSearchResults= new Id[1];
                fixedSearchResults[0] = lstAccs[0].Id;
                Test.setFixedSearchResults(fixedSearchResults);
                grpController.queryUnknownGroupAccount();
                System.assertEquals(1,grpController.lstAccountList.size());    
                grpController.Reset();
            Test.stopTest();
        }
    }

    /*
    * Method Name : testUnknownGroupSearch
    * Description : This method is used for unknown group search
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testUnknownGroupSearch() 
    {  
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
            grpController.oSearchWrapper = new HUMGroupSearchWrapperHelper();
            grpController.oSearchWrapper.sGroupNumber = '123456';
            grpController.oSearchWrapper.sGroupName = '';
            grpController.oSearchWrapper.sState = 'KY';
            grpController.queryUnknownGroupAccount();
            System.assertEquals(0,grpController.lstAccountList.size());  
            Test.stopTest();
        }
    }

    static testMethod void testInsertGroupAccount() 
    {  
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                
                grpController.oSearchWrapper.sGroupNumber = '004';
                grpController.oSearchWrapper.sGroupName = 'TestGroup004';
                grpController.oSearchWrapper.sState = 'AL';
                grpController.bIsSearchPage = true;
                grpController.idGroup = 'testGroupId'; 
                grpController.mapOfGroup = 'TestGroup';
                grpController.InsertGroupAccount();
            Test.stopTest();
            System.assertEquals(0,grpController.lstSObjects.size());     
        }
    }
    
    /*
    * Method Name : testFieldCipherMapping
    * Description : This method is used for field mapping
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testFieldCipherMapping() 
    {  
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                
                grpController.oSearchWrapper.sGroupNumber = '004';
                grpController.oSearchWrapper.sGroupName = 'TestGroup004';
                grpController.oSearchWrapper.sState = 'AL';
                
                grpController.fieldCipherMapping( grpController.oSearchWrapper);
            Test.stopTest();
            System.assert(true,grpController.lstSObjects.size() == 0);     
        }
    }

    /*
    * Method Name : testGroupServiceSoftPhone
    * Description : This method is used for group search from softphone
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testGroupServiceSoftPhoneSearch() 
    {    
        User u = [Select Id , Name from User where Username='user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {
            HUMGroupSearchControllerV2 grpController = new HUMGroupSearchControllerV2();
            Map<String,String> mapSoftPhone = new Map<String,String>{'Test' => 'Test'};
                      
            Test.setCurrentPage(Page.HUMCustomSearchPage);
           
            Test.startTest();
                List<SelectOption> lstGroupSelect = grpController.GroupStatevalue;
                List<Error_Log__c> lstErrorLogs = grpController.lstLogErrors;
                
                grpController.oSearchWrapper.mapEncryptedValues = mapSoftPhone;
                grpController.oSearchWrapper.sGroupNumber = '004';
                grpController.oSearchWrapper.sGroupName = 'TestGroup004';
                grpController.oSearchWrapper.sState = 'AL';
                grpController.bIsUnknownGroupSearch = false;
                
            Test.stopTest();
            
            System.assert(true,grpController.lstSObjects.size() == 0);    
            
        }
    }
}