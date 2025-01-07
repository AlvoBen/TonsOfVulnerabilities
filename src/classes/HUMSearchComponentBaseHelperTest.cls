/*******************************************************************************************************************************
Apex Class Name : HUMSearchComponentBaseHelperTest
Version         : 1.0
Created On      : July 6
Function        : Test class for HUMSearchComponentBaseHelper

Modification Log: 
 * Developer Name              Code Review            Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 *  Chaitanya Vegendla            17172              05/21/2014                  Original  
 *  Chaitanya Kumar               17862              08/22/2014                  Updated upsertAccountFromExternalSystem with mock webservice
 *  Chaitanya Kumar               17862              09/04/2014                  Created upsertConsumerTaxIDsProvider and upsertConsumerTaxIDsAgency 
 *  Chaitanya Kumar               17862              09/11/2014                  Updated upsertConsumerTaxIDsProvider,upsertConsumerTaxIDsProvider based on changed defination of calling method
 *  Pranali Choudhari                                10/30/2014                  Added a method for testing cbis account upsert
 *  Pradeep Veeravali             21519              03/12/2015                  added method upsertAccountFromExtSysWithSidAgentEmailIdValidation for Agent's Email validation.  
 *  Pradeep Veeravali             21640              03/17/2015                  Removed CBIS related test methods for insertAccountwithSID  
 * Santhi Mandava                                    03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage.
 ****************************************************************************************************************************/
@isTest
private class HUMSearchComponentBaseHelperTest extends HUMSearchComponentBaseHelper
{    
    /*
     * Method Name : storeClassReferences
     * Description : Store the search Class references in 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void storeClassReferences() 
    {  
        //Load Constansts
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

        Test.startTest();

        componentBase.searchController = new HUMSearchController();
        componentBase.key = 'ComponentBase' ; 

        Test.stopTest();

        //Assert the functionality in Search Controller
        System.assert( componentBase.searchController.searchControllerMap.containsKey('ComponentBase'));     
    }

    /*
     * Method Name : populateAccountMap 
     * Description : Store the Map which is responsbile to show results on the page . 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void populateAccountMap() 
    {  
        HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

        Test.startTest();

        componentBase.ipageSize = 3 ;
        componentbase.mapRecordData = new Map<Integer,Account> {0 => HUMTestDataHelperTest.getAccount() , 1 => HUMTestDataHelperTest.getAccount() , 2=> HUMTestDataHelperTest.getAccount()};

        Test.stopTest();

        //Check the current pagesize
        System.assertEquals(componentBase.mapRecordset.size(),3);     
        //Check the Size of Json Map
        System.assert(componentBase.mapRecordSetJson.size()==3);              
    }

    /*
     * Method Name : nextPagination 
     * Description : Pagination method to test next result set in the list 
     * Return type : void
     * Paramater   : void
     */
    static testMethod void nextPagination() 
    {   
        HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();
        boolean bHasNext=false;

        Test.startTest();

        componentBase.ipageSize = 1 ;
        componentbase.mapRecordData = new Map<Integer,Account> {0 => HUMTestDataHelperTest.getAccount() , 1 => HUMTestDataHelperTest.getAccount() , 2=> HUMTestDataHelperTest.getAccount()};
        bHasNext=componentBase.getHasNext();
        componentbase.next();

        Test.stopTest();

        //Assert the next result set behavior
        System.assert(bHasNext);       
        //Assert the size
        System.assertEquals(componentbase.icounter,1);            
    }

    /*
     * Method Name : previousPagination 
     * Description : Pagination method to test previous result set in the List
     * Return type : void
     * Paramater   : void
     */
    static testMethod void previousPagination() 
    {  
        HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();
        boolean bHasPrevious=false;

        Test.startTest();

        componentBase.ipageSize = 1 ;
        componentbase.mapRecordData = new Map<Integer,Account> {1 => HUMTestDataHelperTest.getAccount() , 2 => HUMTestDataHelperTest.getAccount() , 3=> HUMTestDataHelperTest.getAccount()};
        componentbase.next();
        bHasPrevious=componentbase.getHasPrevious();
        componentbase.previous();

        Test.stopTest();

        //Assert the previous result set behavior
        System.assert(bHasPrevious);      
        //Assert the previous result set
        System.assertEquals(componentbase.iCounter,0);       
    }

    /*
     * Method Name : firstPagination 
     * Description : Pagination method to test first result set in the List
     * Return type : void
     * Paramater   : void
     */
    static testMethod void firstPagination() 
    {  
        HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

        Test.startTest();

        componentBase.ipageSize = 1 ;
        componentbase.mapRecordData = new Map<Integer,Account> {1 => HUMTestDataHelperTest.getAccount() , 2 => HUMTestDataHelperTest.getAccount() , 3=> HUMTestDataHelperTest.getAccount()};
        componentbase.next();
        componentbase.first();

        Test.stopTest();

        //Assert the first result set
        System.assertEquals(componentbase.iCounter,0);       
    }


    /*
     * Method Name : lastPagination 
     * Description : Pagination method to test last result set in the List
     * Return type : void
     * Paramater   : void
     */
    static testMethod void lastPagination() 
    {  
        HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

        Test.startTest();

        componentBase.ipageSize = 1 ;
        componentbase.mapRecordData = new Map<Integer,Account> {1 => HUMTestDataHelperTest.getAccount() , 2 => HUMTestDataHelperTest.getAccount() , 3=> HUMTestDataHelperTest.getAccount()};
        componentbase.last();

        Test.stopTest();

        //Assert the last result set
        System.assertEquals(componentbase.iCounter,3);
        System.assert(!componentbase.getHasNext());   
    }

    /*
     * Method Name : upsertAccountFromExternalSystem 
     * Description : Upsert Account based on the external  ID
     * Return type : void
     * Paramater   : void
     */
    static testMethod void upsertAccountFromExternalSystem() 
    {   
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;

        System.runas(u) 
        {    
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1235';
            String sJsonAccount = JSON.serialize(acc);
            HUMCipherCloudSettings__c testCCS = new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME'); 
            testCCS.Value__c = '12345';                    
            insert testCCS;

            HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

            Test.startTest();

            componentbase.insertDataFromExternalSystem(sJsonAccount);

            Test.stopTest();

            //Assert a account record is created
            List<Account> lstAccounts = [Select id from Account limit 1];
            System.assertEquals(lstAccounts.size(),1);   

        }    
    }
    
    /*
     * Method Name : upsertAccountFromExternalSystemAgentEmailIdValidation 
     * Description : Upsert Account based on the external  ID
     * Return type : void
     * Paramater   : void
     */
    static testMethod void upsertAccountFromExternalSystemAgentEmailIdValidation() 
    {   
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;

        System.runas(u) 
        {   
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Work_Email__c='a@gmail.com';
            acc.Account_External_Id__c='1236';
            acc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.Label.HUMAgentBrokerRecordTypeName).getRecordTypeId();                        
            String sJsonAccount = JSON.serialize(acc);
            HUMCipherCloudSettings__c testCCS = new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME');            
            testCCS.Value__c = '12345';                    
            insert testCCS;

            HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

            Test.startTest();

            componentbase.insertDataFromExternalSystem(sJsonAccount);

            Test.stopTest();

            //Assert a account record is created
            List<Account> lstAccounts = [Select id from Account limit 1];
            System.assertEquals(lstAccounts.size(),1);   

        }    
    }
           
    /*
     * Method Name : upsertAccountException 
     * Description : Upsert Account based on the external  ID ,Give wrong data for exception
     * Return type : void
     * Paramater   : void
     */
    static testMethod void upsertAccountException() 
    {   

        HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

        Test.startTest();

        componentbase.insertDataFromExternalSystem(null);
        componentbase.getHasNext();

        Test.stopTest();

        //Assert a account record is created
        List<Account> lstAccounts = [Select id from Account limit 1];
        System.assertEquals(lstAccounts.size(),0);      
    }   

    /*
     * Method Name : upsertConsumerTaxIDsProvider 
     * Description : Upsert Consumer Ids based on the external  ID and List of Tax Ids
     * Return type : void
     * Paramater   : void
     */
    static testMethod void upsertConsumerTaxIDsProvider() 
    {   
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        HUMTestDataHelperTest.getHUMConstantsData();
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        

        System.runas(u) 
        {    
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1231';
            acc.RecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert acc;

            HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

            Test.startTest();

            componentbase.upsertConsumerTaxIds(acc.id, new Map<String,String>{'23432'=>'2342'});

            Test.stopTest();

            //Assert a account record is created
            List<ConsumerId__c> lstConsumerIds = [Select id from ConsumerId__c limit 1];
            System.assertEquals(lstConsumerIds.size(),1);    
        }   
    }

    /*
     * Method Name : upsertConsumerTaxIDsAgency 
     * Description : Upsert Consumer Ids based on the external  ID and List of Tax Ids
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void upsertConsumerTaxIDsAgency() 
    {   
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        
        List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
        lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
        insert lstApplicationName;
        
        HUMTestDataHelperTest.getHUMConstantsData();

        System.runas(u) 
        {    
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1231';
            acc.RecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
            insert acc;

            HUMSearchComponentBaseHelper componentBase = new HUMSearchComponentBaseHelper();

            Test.startTest();

            componentbase.upsertConsumerTaxIds(acc.id, new Map<String,String>{'23432'=>'2342'});

            Test.stopTest();

            //Assert a ConsumerId record is created
            List<ConsumerId__c> lstConsumerIds = [Select id from ConsumerId__c limit 1];
            System.assertEquals(lstConsumerIds.size(),1);    
        }   
    }
}