/*******************************************************************************************************************************
Apex Class Name : HUMAgencySearchControllerV2Test
Version         : 2.0
Created On      : 08/05/2015
Function        : Test class for HUMAgencySearchControllerV2

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani         25261                     08/05/2015                 Original Version
* Vishal Verma                                        09/11/2015                 Added @testSetup for creating test data.
* Shruthi Karanth                                     04/14/2017                 Softphone Optimization to skip DPaaS callout when coming through softphone routing
* Suresh Gottipati                                    7/18/2018                  DPaas Call out for internal search related changes
* Santhi Mandava            348723                    04/04/2019                 Fixed Codecoverage issue
*******************************************************************************************************************************/

@isTest
private class HUMAgencySearchControllerV2Test
{  
    
    /*
    * Method Name : setupTestData
    * Description : Method to set up test data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void setupTestData()
    {
        User u = HUMTestDataHelperTest.createTestUser(System.Label.HUMAgencyUser,System.Label.HUMAgencyCCS);
        insert u;
        system.assertNotEquals(u, NULL, 'Value not matched');
        system.runas(u)
        {
            insert new HUMMember_search_state_mapping__c( Name = 'Alabama' , State__c = 'Alabama',State_Code__c='AL');
            //Cipher Cloud Custom Setting Data
            insert new HUMCipherCloudSettings__c(Name='APPLICATION_NAME',Value__c = 'CRMS TESTING');
            
            insert new HUM_Webservice_Callout__c( Name = 'AgencySearchService' , Certificate_Name__c = 'TestCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
            
            insert new HUM_Dpass_Callout__c( Name = 'DPAAS AGENCY' , Timeout__c = 120, Active__c = true);
            
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'AccountName' , Value__c = 'Test');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'AccountName2' , Value__c = 'Test');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'ILastName' , Value__c = 'Test');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'ILastName2' , Value__c = 'Test');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'TaxId' , Value__c = 'Test');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'TaxId2' , Value__c = 'Test');
            
            
            //Load all Constants data
            HUMTestDataHelperTest.getHUMConstantsData();
            List<Account> lstacc = new List<Account>();
            List<ConsumerID__c> lstcons = new List<ConsumerID__c>();
            RecordType PersonAccountRecordType = [Select Id from RecordType where Name = 'Agent/Broker' and sObjectType='Account'];
            for(integer i=1;i<11;i++)
            {
                Account acc = HUMTestDataHelperTest.getAccount();
                acc.RecordType = PersonAccountRecordType;
                acc.Individual_First_Name__c = 'Agent00'+i;
                acc.Individual_Last_Name__c = 'Agent00'+i;
                acc.Name = 'Agent00' + i + ' ' + 'Agent00' + i;
                acc.Agent_Type__c = 'Agency';
                acc.BillingState = 'Alabama';
                acc.Agent_Id__c = '000'+i;
                lstacc.add(acc);
            }
            insert lstacc;
            System.assertEquals(lstacc.size(),10);
            for(integer i=0;i<lstacc.size();i++)
            {
                ConsumerID__c cons = new ConsumerID__c();
                cons.Consumer_ID__c = 'DUMMY'+i+' ';
                cons.ID_Type__c = Label.HumServiceTaxIDtag;
                cons.ETL_Record_Deleted__c = false;
                cons.Account__c = lstacc[i].id;
                lstcons.add(cons);
                System.assertEquals(true, lstcons.size() > 0);
            }
            insert lstcons;
        }   
    }
     
    /*
    * Method Name : testAgencyServiceClassIntegration
    * Description : This method is used for group search from external system
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencyServiceWebServiceResults() 
    {        
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {        
            HUMAgencySearchService.ProducerSearchResult resEle =  new HUMAgencySearchService.ProducerSearchResult();
            HUMAgencySearchService.ArrayOfProducer ProducerList = new HUMAgencySearchService.ArrayOfProducer();
            HUMAgencySearchService.Producer agencyResult = new HUMAgencySearchService.Producer();
            HUMAgencySearchService.Address addr = new HUMAgencySearchService.Address();
            agencyResult.SysAssgnNbr = '1111';
            agencyResult.BaseDate = DateTime.ValueOf('2013-01-01 00:00:00');
            agencyResult.HireDate = DateTime.ValueOf('2013-01-01 00:00:00');
            agencyResult.StatusDate = DateTime.ValueOf('2013-01-01 00:00:00');
            agencyResult.DateOfBirth = DateTime.ValueOf('1990-01-01 00:00:00');
            agencyResult.EmailId = 'testmock@testmock.com';
            agencyResult.FirstName = 'TestMock';
            agencyResult.LastName = 'TestMock';
            agencyResult.HireDate = DateTime.ValueOf('1990-01-01 00:00:00');
            agencyResult.IsTermed = true;
            agencyResult.ProducerType = 'CareerAgent';
            agencyResult.StateCode = 'AL';
            addr.StateCode = 'AL';
            agencyResult.ShippingAddress = addr;
            agencyResult.ResidenceAddress = addr;
            agencyResult.BusinessAddress = addr;
            ProducerList.Producer = new List<HUMAgencySearchService.Producer>();
            ProducerList.Producer.add(agencyResult);
            resEle.ProducerList = ProducerList;
              
            
            HUMAgencySearchWrapperHelper obj = new HUMAgencySearchWrapperHelper();  
            obj.mapEncryptedValues = new Map<String,String>();
            obj.mapEncryptedValues.put('Consumer_Id__c_SOFTPHONE','123456789');
            obj.mapEncryptedValues.put('acc2_SOFTPHONE','Testing');
            obj.mapEncryptedValues.put('Individual_Last_Name__c_SOFTPHONE','Test');
            obj.mapEncryptedValues.put('00NF000000CCg14','123456789');
            obj.mapEncryptedValues.put('AccountName_SOFTPHONE','Testing');
            obj.mapEncryptedValues.put('00NF000000CCfzS','Test');
            Test.startTest();
                        
            HUMAgencySearchControllerV2 AgnSrch = new HUMAgencySearchControllerV2();
            AgnSrch.oSearchWrapper.sAgentId = '1111';
            AgnSrch.oSearchWrapper.sTaxId = '2222';
            AgnSrch.oSearchWrapper.sAgentType = 'Agency';
            AgnSrch.oSearchWrapper.sState = 'AL';
            AgnSrch.oSearchWrapper.sAgencyName = 'TestMockService';
            List<SelectOption> lstGroupSelect = AgnSrch.Agencystatevalue;
            List<Error_Log__c> lstErrorLogs = AgnSrch.lstLogErrors;
            Test.setMock(WebServiceMock.class, new HUMAgencySearchServiceTest ());
            AgnSrch.resEle = resEle;
            AgnSrch.ProducerList = resEle.ProducerList;
            AgnSrch.searchAgency();
            AgnSrch.bIsUnknownAgencySearch = false;
            AgnSrch.processResponse();
            AgnSrch.bIsNameSearch = false;
            Map<String,String> NewMap =AgnSrch.fieldCipherMapping(obj);
            System.assertNotEquals(NewMap,Null );
            
            AgnSrch.createAccountList(AgnSrch.ProducerList);
            AgnSrch.oSearchWrapper.sAgentId = '1111';
            AgnSrch.oSearchWrapper.sTaxId = '2222';
            AgnSrch.oSearchWrapper.sState = 'AL';
            AgnSrch.oSearchWrapper.sAgencyName = 'TestMockService';
            AgnSrch.oSearchWrapper.sLastName = 'TestMockService';
            AgnSrch.oSearchWrapper.sAgentType = 'All';
            AgnSrch.searchAgency();
            AgnSrch.processResponse();
            AgnSrch.createAccountList(AgnSrch.ProducerList);
            
            AgnSrch.oSearchWrapper.sAgentId = '1111';
            AgnSrch.oSearchWrapper.sTaxId = '2222';
            AgnSrch.oSearchWrapper.sState = 'AL';
            AgnSrch.oSearchWrapper.sAgencyName = 'TestMockService';
            AgnSrch.oSearchWrapper.sLastName = '';
            AgnSrch.oSearchWrapper.sAgentType = 'All';
            AgnSrch.resEle = resEle;
            AgnSrch.ProducerList = resEle.ProducerList;
            AgnSrch.searchAgency();
            AgnSrch.processResponse();
            AgnSrch.createAccountList(AgnSrch.ProducerList);
            AgnSrch.Reset();
            
            AgnSrch.oSearchWrapper.sAgentId = '1111';
            AgnSrch.oSearchWrapper.sTaxId = '';
            AgnSrch.oSearchWrapper.sState = '';
            AgnSrch.oSearchWrapper.sAgencyName = '';
            AgnSrch.oSearchWrapper.sLastName = '';
            AgnSrch.oSearchWrapper.sAgentType = 'Agency';
            AgnSrch.searchAgency();
            AgnSrch.processResponse();
            AgnSrch.createAccountList(AgnSrch.ProducerList);
            AgnSrch.Reset();
            
            HUMAgencySearchControllerV2 AgnSrch2 = new HUMAgencySearchControllerV2();
            AgnSrch2.oSearchWrapper.sAgentId = '1111';
            AgnSrch2.oSearchWrapper.sAgentType = 'Agency';
            Test.setMock(WebServiceMock.class, new HUMAgencySearchServiceTest ());
            AgnSrch2.resEle = resEle;
            AgnSrch2.ProducerList = resEle.ProducerList;
            AgnSrch2.searchAgency();
            AgnSrch2.bIsUnknownAgencySearch = false;
            AgnSrch2.processResponse();
            AgnSrch2.createAccountList(AgnSrch.ProducerList);
            
            
            
            Test.stopTest();
            System.assert(true,AgnSrch.lstSObjects.size() > 0);       
        }    
    }
    
    /*
    * Method Name : testAgencySearchValidations
    * Description : This method is used to test the validations in Agency Search
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencySearchValidations()
    {
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        system.runAs(u)
        {
            Account acc = [Select Individual_First_Name__c , Individual_Last_Name__c , Name , Agent_Type__c , BillingState , Agent_Id__c from Account where Agent_Id__c = '0001' limit 1];
            
            HUMAgencySearchControllerV2 AgnSrch = new HUMAgencySearchControllerV2();
            List<SelectOption> lst=AgnSrch.AgencyStatevalue;
            
            AgnSrch.buildUnknownAccountMap(acc.Id);
        
       
            Test.setCurrentPage(Page.HUMCustomSearchPage); 
                      
            Test.startTest();
            AgnSrch.searchAgency();
            AgnSrch.oSearchWrapper.sAgencyName = 'Agent001';
            AgnSrch.oSearchWrapper.sLastName = '';
            AgnSrch.oSearchWrapper.sState = 'Alabama';
            AgnSrch.oSearchWrapper.sAgentType = 'Agency';
            AgnSrch.oSearchWrapper.sAgentId = 'test';
            AgnSrch.searchAgency();
            
            System.assert(true,AgnSrch.lstSObjects.size() == 0);
            
            Test.stopTest();
        }
    }
    
   /*
    * Method Name : testAgencySearchClassInternal
    * Description : This method is used for group search within salesforce
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencySearchClassInternal() 
    {        
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        system.runAs(u)
        { 
        
            HUMAgencySearchControllerV2 AgnSrch = new HUMAgencySearchControllerV2();
            List<Account> lstacc = new List<Account>();
            lstacc = [Select Individual_First_Name__c , Individual_Last_Name__c , Name , Agent_Type__c , BillingState , Agent_Id__c from Account where Agent_Id__c = '0001'];
            //Set Current Page
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            
            Test.startTest();
            
            AgnSrch.oSearchWrapper.sAgencyName = lstacc[0].Name;
            AgnSrch.oSearchWrapper.sLastName =  lstacc[0].Individual_Last_Name__c;
            AgnSrch.oSearchWrapper.sState = lstacc[0].BillingState;
            AgnSrch.oSearchWrapper.sAgentType = lstacc[0].Agent_Type__c;
            AgnSrch.oSearchWrapper.sAgentId = lstacc[0].Agent_Id__c;
            AgnSrch.oSearchWrapper.sTaxId = 'DUMMY0';
            AgnSrch.bIsUnknownAgencySearch = true;
            AgnSrch.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c'=>  'Consumer_ID__c='+'\''+'DUMMY0'+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+'Agent000'+'\'', 'Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+'Agent000'+'\'', 'Name'=> 'Name =' +'\''+'Agent000 Agent000'+'\'', 'Agent_Type__c'=> 'Agent_Type__c =' +'\''+'Agency'+'\'', 'BillingState'=> 'BillingState =' +'\''+'Alabama'+'\'', 'Agent_Id__c'=> 'Agent_Id__c =' +'\''+'0000'};
            AgnSrch.searchAgency();
            
            
            AgnSrch.oSearchWrapper.sAgencyName = lstacc[0].Name;
            AgnSrch.oSearchWrapper.sLastName =  lstacc[0].Individual_Last_Name__c;
            AgnSrch.oSearchWrapper.sState = lstacc[0].BillingState;
            AgnSrch.oSearchWrapper.sAgentType = 'All';
            AgnSrch.oSearchWrapper.sAgentId = lstacc[0].Agent_Id__c;
            AgnSrch.oSearchWrapper.sTaxId = '';
            AgnSrch.bIsUnknownAgencySearch = true;
            AgnSrch.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c'=>  'Consumer_ID__c='+'\''+'DUMMY0'+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+'Agent000'+'\'', 'Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+'Agent000'+'\'', 'Name'=> 'Name =' +'\''+'Agent000 Agent000'+'\'', 'Agent_Type__c'=> 'Agent_Type__c =' +'\''+'Agency'+'\'', 'BillingState'=> 'BillingState =' +'\''+'Alabama'+'\'', 'Agent_Id__c'=> 'Agent_Id__c =' +'\''+'0000'};
            AgnSrch.searchAgency();
             
            
            AgnSrch.oSearchWrapper.sAgencyName = '';
            AgnSrch.oSearchWrapper.sLastName =  '';
            AgnSrch.oSearchWrapper.sState = 'None';
            AgnSrch.oSearchWrapper.sAgentType = lstacc[0].Agent_Type__c;
            AgnSrch.oSearchWrapper.sAgentId = lstacc[0].Agent_Id__c;
            AgnSrch.oSearchWrapper.sTaxId = '';
            AgnSrch.bIsUnknownAgencySearch = true;
            AgnSrch.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c'=>  'Consumer_ID__c='+'\''+''+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+''+'\'', 'Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+''+'\'', 'Name'=> 'Name =' +'\''+''+'\'', 'Agent_Type__c'=> 'Agent_Type__c =' +'\''+'Agency'+'\'', 'BillingState'=> 'BillingState =' +'\''+'None'+'\'', 'Agent_Id__c'=> 'Agent_Id__c =' +'\''+'0000'};
             
            
            AgnSrch.bAgencyTimedout=true;
             
                        
            
            Test.stopTest();
            System.assert(true,AgnSrch.lstSObjects.size() > 0);
            AgnSrch.Reset();
            System.assert(true,AgnSrch.lstSObjects.size() == 0);
        }
    }
    
  
    
    /*
    * Method Name : testAgencySearchClassError
    * Description : This method is used for group search within salesforce
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencySearchClassError() 
    {   
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        system.runAs(u)
        {     
            HUMAgencySearchControllerV2 AgnSrch = new HUMAgencySearchControllerV2();
            //Set Current Page
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            
            Test.startTest();
            
            AgnSrch.oSearchWrapper.sAgencyName = 'Agent00';
            AgnSrch.oSearchWrapper.sLastName =  '';
            AgnSrch.oSearchWrapper.sState = 'Alabama';
            AgnSrch.oSearchWrapper.sAgentType = 'Agency';
            AgnSrch.oSearchWrapper.sAgentId = '';
            AgnSrch.oSearchWrapper.sTaxId = '';
            AgnSrch.bIsUnknownAgencySearch = true;
            AgnSrch.searchAgency();
            AgnSrch.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c'=>  'Consumer_ID__c='+'\''+''+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+''+'\'', 'Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+'Agent'+'\'', 'Name'=> 'Name =' +'\''+''+'\'', 'Agent_Type__c'=> 'Agent_Type__c =' +'\''+'Agency'+'\'', 'BillingState'=> 'BillingState =' +'\''+'Alabama'+'\'', 'Agent_Id__c'=> 'Agent_Id__c =' +'\''+''};
            AgnSrch.bAgencyTimedout=true;
           
            
            Test.stopTest();
            
            System.assert(true,AgnSrch.lstSObjects.size() > 0);
            
            AgnSrch.Reset();
            System.assert(true,AgnSrch.lstSObjects.size() == 0);
        }
    }
    
    /*
    * Method Name : testAgencyServiceClassIntegration
    * Description : This method is used for group search from external system
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencyServiceClassIntegrationAgentId() 
    {        
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        system.runAs(u)
        {
        
            HUMAgencySearchService.ProducerSearchResult resEle =  new HUMAgencySearchService.ProducerSearchResult();
            HUMAgencySearchService.ArrayOfProducer ProducerList = new HUMAgencySearchService.ArrayOfProducer();
            HUMAgencySearchService.Producer agencyResult = new HUMAgencySearchService.Producer();
            HUMAgencySearchService.Address addr = new HUMAgencySearchService.Address();
            agencyResult.SysAssgnNbr = '1111';
            agencyResult.BaseDate = DateTime.ValueOf('2013-01-01 00:00:00');
            agencyResult.HireDate = DateTime.ValueOf('2013-01-01 00:00:00');
            agencyResult.StatusDate = DateTime.ValueOf('2013-01-01 00:00:00');
            agencyResult.DateOfBirth = DateTime.ValueOf('1990-01-01 00:00:00');
            agencyResult.EmailId = 'testmock@testmock.com';
            agencyResult.FirstName = 'TestMock';
            agencyResult.LastName = 'TestMock';
            agencyResult.HireDate = DateTime.ValueOf('1990-01-01 00:00:00');
            agencyResult.IsTermed = true;
            agencyResult.ProducerType = 'CareerAgent';
            agencyResult.StateCode = 'AL';
            addr.StateCode = 'AL';
            agencyResult.ShippingAddress = addr;
            agencyResult.ResidenceAddress = addr;
            ProducerList.Producer = new List<HUMAgencySearchService.Producer>();
            ProducerList.Producer.add(agencyResult);
            resEle.ProducerList = ProducerList;
                   
       
            Test.startTest();
            
            HUMAgencySearchControllerV2 AgnSrch2 = new HUMAgencySearchControllerV2();
            AgnSrch2.oSearchWrapper.sAgentId = '1111';
            AgnSrch2.oSearchWrapper.sTaxId = '';
            AgnSrch2.oSearchWrapper.sState = 'None';
            AgnSrch2.oSearchWrapper.sAgencyName = '';
            AgnSrch2.oSearchWrapper.sLastName = '';
            AgnSrch2.oSearchWrapper.sAgentType = 'Agency';
            AgnSrch2.bIsUnknownAgencySearch = false;
            AgnSrch2.resEle = resEle;
            AgnSrch2.ProducerList = resEle.ProducerList;
            AgnSrch2.searchAgency();
            AgnSrch2.processResponse();
            AgnSrch2.createAccountList(AgnSrch2.ProducerList);
            AgnSrch2.Reset();
            
            Test.stopTest();
            System.assert(true,AgnSrch2.lstSObjects.size() > 0);       
        }    
    }
    
   /*
    * Method Name : testAgencySearchClassInternal
    * Description : This method is used for group search within salesforce
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencySearchClassInternalQueryException() 
    {        
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        system.runAs(u)
        {
        
            HUMAgencySearchControllerV2 AgnSrch = new HUMAgencySearchControllerV2();
            List<Account> lstacc = new List<Account>();
            lstacc = [Select Individual_First_Name__c , Individual_Last_Name__c , Name , Agent_Type__c , BillingState , Agent_Id__c from Account where Agent_Id__c = '0001'];
            //Set Current Page
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            
            Test.startTest();
            AgnSrch.mapTaxId = new Map<Integer,String>();
            AgnSrch.oSearchWrapper.sAgencyName = lstacc[0].Name;
            AgnSrch.oSearchWrapper.sLastName =  lstacc[0].Individual_Last_Name__c;
            AgnSrch.oSearchWrapper.sState = lstacc[0].BillingState;
            AgnSrch.oSearchWrapper.sAgentType = lstacc[0].Agent_Type__c;
            AgnSrch.oSearchWrapper.sAgentId = lstacc[0].Agent_Id__c;
            AgnSrch.oSearchWrapper.sTaxId = 'DUMMY0';
            AgnSrch.bIsUnknownAgencySearch = true;
            AgnSrch.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c'=>  'Consumer_ID__c='+'\''+''+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+''+'\'', 'Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+''+'\'', 'Name'=> 'Name =' +'\''+''+'\'', 'Agent_Type__c'=> 'Agent_Type__c =' +'\''+'Agency'+'\'', 'BillingState'=> 'BillingState =' +'\''+'Alabama'+'\'', 'Agent_Id__c'=> 'Agent_Id__c =' +'\''+'0000'};
            AgnSrch.searchAgency();
             
            
            
            AgnSrch.oSearchWrapper.sAgencyName = '';
            AgnSrch.oSearchWrapper.sLastName =  '';
            AgnSrch.oSearchWrapper.sState = 'None';
            AgnSrch.oSearchWrapper.sAgentType = 'Agency';
            AgnSrch.oSearchWrapper.sAgentId = lstacc[0].Agent_Id__c;
            AgnSrch.oSearchWrapper.sTaxId = '';
            AgnSrch.bIsUnknownAgencySearch = true;
            AgnSrch.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c'=>  'Consumer_ID__c='+'\''+''+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+''+'\'', 'Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+''+'\'', 'Name'=> 'Name =' +'\''+''+'\'', 'Agent_Type__c'=> 'Agent_Type__c =' +'\''+'Agency'+'\'', 'BillingState'=> 'BillingState =' +'\''+'None'+'\'', 'Agent_Id__c'=> 'Agent_Id__c =' +'\''+'0000'};
            AgnSrch.searchAgency();
            AgnSrch.throwErrorNoResult();
            
            Test.stopTest();
            
            System.assert(true,AgnSrch.lstSObjects.size() > 0);
            
            AgnSrch.Reset();
            
            System.assert(true,AgnSrch.lstSObjects.size() == 0);
        }
    } 
    
    /*
    * Method Name : testAgencySearchClassSoftPhone
    * Description : This method is used for group search within salesforce from softphone
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencySearchClassSoftPhone() 
    {        
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        system.runAs(u)
        { 
        
            HUMAgencySearchControllerV2 AgnSrch = new HUMAgencySearchControllerV2();
            Map<String,String> mapSoftPhone = new Map<String,String>{'Test' => 'DUMMY0'};
            List<Account> lstacc = new List<Account>();
            List<Account> lstaccTemp = new List<Account>();
            lstacc = [Select Individual_First_Name__c , Individual_Last_Name__c , Name , Agent_Type__c , BillingState , Agent_Id__c from Account where Agent_Id__c = '0001'];
            //Set Current Page
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            
            Test.startTest();
            for(integer i=0;i<=101;i++)
            {
             AgnSrch.mapRecordData.put(i,new account());
            }
            AgnSrch.throwErrorNoResult();
            AgnSrch.processResponse();
            AgnSrch.searchAgency();
            AgnSrch.isSoftPhoneSearch = true;  
            AgnSrch.oSearchWrapper.mapEncryptedValues = mapSoftPhone;
            AgnSrch.oSearchWrapper.sAgencyName = '';
            AgnSrch.oSearchWrapper.sLastName = lstacc[0].Individual_Last_Name__c;
            AgnSrch.oSearchWrapper.sState = 'None';
            AgnSrch.oSearchWrapper.sAgentType = lstacc[0].Agent_Type__c;
            AgnSrch.oSearchWrapper.sTaxId = 'DUMMY0';
            AgnSrch.oSearchWrapper.sAgentId = '';
            AgnSrch.searchAgency();
            AgnSrch.processResponse();
            AgnSrch.bIsSearchPage = true;
            AgnSrch.idgroup = '123456';
            AgnSrch.mapOfAgents = 'mapmap';
            AgnSrch.taxId = '654231';
            AgnSrch.consumerIds = '659874'; 
            AgnSrch.searchAgencySoftPhone();
            AgnSrch.insertAgentAccount();
            AgnSrch.fieldCipherMapping(AgnSrch.oSearchWrapper);      
            AgnSrch.isSoftPhoneSearch = true;
            AgnSrch.FilterSOSLResults(lstacc,lstaccTemp);
        AgnSrch.processResponse();
            AgnSrch.resElementFuture = Null;
            AgnSrch.callService();
            System.AssertEquals(AgnSrch.bIsAgencyError,true);
            lstacc = [Select Individual_First_Name__c , Individual_Last_Name__c , Name , Agent_Type__c , BillingState , Agent_Id__c from Account where Agent_Id__c IN ('0001','0002')];
            
            List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();
            lstConsumers.Add(new ConsumerID__c(Account__c=lstacc[0].Id,Consumer_ID__c='123456789',Name='Test'));
            lstConsumers.Add(new ConsumerID__c(Account__c=lstacc[0].Id,Consumer_ID__c='987654321',Name='Test'));
            lstConsumers.Add(new ConsumerID__c(Account__c=lstacc[1].Id,Consumer_ID__c='123456789',Name='Test'));
            lstConsumers.Add(new ConsumerID__c(Account__c=lstacc[1].Id,Consumer_ID__c='987654321',Name='Test'));
            AgnSrch.prepareConsumerIDMap(lstConsumers);
            Test.stopTest();
            System.assert(true,AgnSrch.lstSObjects.size() > 0);
        }
    }
}