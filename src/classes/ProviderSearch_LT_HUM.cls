/*******************************************************************************************************************************
Apex Class Name : ProviderSearch_LT_HUM
Version         : 1.0
Created On      : 11//2020
Function        : Test class for ProviderSearch_LC_HUM 
Modification Log: 
Developer Name           Code Review                      Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar											  11/06/2020				  Refactored the code
* Ankima Srivastava											  03/03/2021				 Added fields to the query
* Saikumatr Boga                                         06/14/2021                Added few lines to cover Unknown Provider class
* Kajal Namdev                                           09/07/2021                  Test coverage fix      
* SravanKumar Ch                                         10/11/2023                     US 4885646:Provider Search in Related Accounts Section           
*********************************************************************************************************************************************************************************************/

@isTest
public class ProviderSearch_LT_HUM
 {
    
    /**
* Method to setup test data
* 
* @param    None
* @return   Void 
*/
    @testSetup static void setup() 
    {                   
        //Load all Constants data        
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser,null,'?Value not matched');
        insert new HumProviderSpecialtyMapping__c(Name='Allergy / Immunology',EpssSpec_Code__c='ALG');
        RecordType PersonAccountRecordType = [Select Id from RecordType where Name = 'Provider' and sObjectType='Account'];
		RecordType oAgentBrokerRecType = [SELECT Id FROM RecordType WHERE Name = 'Agent/Broker' Limit 1];
        system.assert(PersonAccountRecordType !=null);
        list<account> lstaccount = new list<account>();
        Account oacc =  HUMTestDataHelperTest.getAccount();
        oacc.Group_Number__c = '987654321';
        oacc.recordtypeid = PersonAccountRecordType.id;
        oacc.name= 'duck';
        oacc.NPI_ID__c='0123456789';
        oacc.Individual_First_Name__c='testname';
        oacc.Individual_Last_Name__c='firstname';
        oacc.Provider_Classification__c='I';
        oacc.billingstate='Arizona';
        oacc.billingpostalcode='12345';
        oacc.Tenant_Id__c='00';  
        oacc.General_Account__c=true;  
        oacc.Work_Email__c = 'test@tt.com';
        insert oacc;
        
        Account oacc11 =  HUMTestDataHelperTest.getAccount();
        oacc11.Group_Number__c = '987654321';
        oacc11.recordtypeid = PersonAccountRecordType.id;
        oacc11.name= 'duck11';
        oacc11.NPI_ID__c='0123456789';
        oacc11.Individual_First_Name__c='testname';
        oacc11.Individual_Last_Name__c='firstname';
        oacc11.Provider_Classification__c='I';
        oacc11.billingstate='Arizona';
        oacc11.billingpostalcode='12345';
        oacc11.Tenant_Id__c='00';  
        oacc11.General_Account__c=true;  
        
        insert oacc11;
        
        Account oaccObj =  HUMTestDataHelperTest.getAccount();
        oaccObj.Group_Number__c = '987654321';
        oaccObj.recordtypeid = oAgentBrokerRecType.id;
        oaccObj.name= 'Test Account';
        oaccObj.NPI_ID__c='0123456789';
        oaccObj.Individual_First_Name__c='testname';
        oaccObj.Individual_Last_Name__c='firstname';
        oaccObj.Provider_Classification__c='I';
        oaccObj.billingstate='Arizona';
        oaccObj.Work_Email__c = 'test@tt.c';
        oaccObj.billingpostalcode='12345';
        
        insert oaccObj;
        
        Account oacc1 = new Account();
        oacc1.name= 'test';
        oacc1.Enterprise_Id__c='27003616';
        oacc1.Account_External_ID__c='PRV|00|270036161';
        oacc1.DBA__c='SFDC Search';
        oacc1.Provider_Classification__c ='G';
        oacc1.Individual_First_Name__c='getWrapperLast';
        oacc1.Individual_Last_Name__c='getWrapper';
        oacc1.Degree__c='';
        oacc1.Source_Platform_Code__c='CAS';
        oacc1.Birthdate__c ='11/08/1997';
        oacc1.Gender__c='M';
        oacc1.BillingStreet='London	';
        oacc1.BillingPostalCode='12345';
        oacc1.BillingStatecode='GA';
        oacc1.BillingCity='London';            
        oacc1.phone='11111';
        oacc1.Phone_Ext__c='123345';
        oacc1.NPI_ID__c='9916143841';            
        oacc1.Description='Test';
        oacc1.Taxonomy_Code__c='207RG0100X';			
        oacc1.ShippingStatecode='GA';
        oacc1.shippingPostalCode='';
        oacc1.ShippingCity='London';
        oacc1.ShippingStreet='1234 london';
        insert oacc1;
        
        system.assert(oacc.id!=null);
        Account onewacc = HUMTestDataHelperTest.getAccount();
        onewacc.Name = 'Test Account';
        onewacc.Provider_Classification__c='F';
        insert onewacc ;
        
        system.assert(onewacc.id!=null);
        
        HUM_Dpass_Callout__c oHumDpass = new HUM_Dpass_Callout__c ();
        oHumDpass.name='Dpass provider';
        oHumDpass.Active__c=true;
        oHumDpass.Timeout__c =120;
        insert oHumDpass;
        system.assert(oHumDpass.id != null);
        
        HUM_Webservice_Callout__c EPSServiceDetails = new HUM_Webservice_Callout__c();
        EPSServiceDetails.Name = 'EPSServiceREST_V1';
        EPSServiceDetails.Certificate_Name__c = 'Humana_GuidanceCare_Mercury';
        EPSServiceDetails.Active__c = true;
        EPSServiceDetails.End_Point_URL__c ='https://int-crmservicews.humana.com/EPSServiceREST_V1.0/EnterpriseProviderSearch';
        EPSServiceDetails.Timeout__c = '12000';
        EPSServiceDetails.Request_Type__c = 'POST';
        EPSServiceDetails.Content_Type__c ='application/json'; 
        insert EPSServiceDetails; 
        system.assert(EPSServiceDetails.id!=null);
        
        //Test Data Created for Method testProviderSearchQueryBuildTest
        Account acc_queryBuildTestCase = HUMTestDataHelperTest.getAccount();
        acc_queryBuildTestCase.RecordType = PersonAccountRecordType;
        acc_queryBuildTestCase.Individual_First_Name__c = 'TEST';
        acc_queryBuildTestCase.Individual_Last_Name__c = 'TEST';
        acc_queryBuildTestCase.Name = 'TEST' + ' ' + 'TEST'; 
        acc_queryBuildTestCase.NPI_ID__c='1245351000';
        acc_queryBuildTestCase.BillingState = 'Alabama';
        acc_queryBuildTestCase.BillingPostalCode = '54321';
        acc_queryBuildTestCase.Agent_Id__c = '111111111';
        acc_queryBuildTestCase.Provider_Classification__c='F';            
        insert acc_queryBuildTestCase;
        
        ConsumerID__c cons_queryBuildTestCase = new ConsumerID__c();
        cons_queryBuildTestCase.Consumer_ID__c = '555555555';
        cons_queryBuildTestCase.ID_Type__c = Label.HumServiceTaxIDtag;
        cons_queryBuildTestCase.ETL_Record_Deleted__c = false;
        cons_queryBuildTestCase.Account__c = acc_queryBuildTestCase.id;            
        insert cons_queryBuildTestCase;
        
        ConsumerID__c cons_queryBuildTestCase1 = new ConsumerID__c();
        cons_queryBuildTestCase1.Consumer_ID__c = '666666666';
        cons_queryBuildTestCase1.ID_Type__c = Label.HumServiceTaxIDtag;
        cons_queryBuildTestCase1.ETL_Record_Deleted__c = false;
        cons_queryBuildTestCase1.Account__c = acc_queryBuildTestCase.id;            
        insert cons_queryBuildTestCase1;
        
        ConsumerID__c cons_queryBuildTestCase2 = new ConsumerID__c();
        cons_queryBuildTestCase2.Consumer_ID__c = '777777777';
        cons_queryBuildTestCase2.ID_Type__c = Label.HumServiceTaxIDtag;
        cons_queryBuildTestCase2.ETL_Record_Deleted__c = false;
        cons_queryBuildTestCase2.Account__c = oacc1.id;            
        insert cons_queryBuildTestCase2;
        
        SOFTPHONE_PRPID_HUM__c oPRPId1 = new SOFTPHONE_PRPID_HUM__c( Name = 'IFirstName' , Value__c = 'Test');
        insert oPRPId1;
        system.assertEquals(oPRPId1.id!=null,true);
        
        SOFTPHONE_PRPID_HUM__c oPRPId2 = new SOFTPHONE_PRPID_HUM__c( Name = 'IFirstName2' , Value__c = 'Test');
        insert oPRPId2;
        system.assertEquals(oPRPId2.id!=null,true);
        
        SOFTPHONE_PRPID_HUM__c oPRPId3 = new SOFTPHONE_PRPID_HUM__c( Name = 'ILastName' , Value__c = 'Test');
        insert oPRPId3;
        system.assertEquals(oPRPId3.id!=null,true);
        
        SOFTPHONE_PRPID_HUM__c oPRPId4 = new SOFTPHONE_PRPID_HUM__c( Name = 'ILastName2' , Value__c = 'Test');
        insert oPRPId4;
        system.assertEquals(oPRPId4.id!=null,true);
        
        SOFTPHONE_PRPID_HUM__c oPRPId5 = new SOFTPHONE_PRPID_HUM__c( Name = 'TaxId' , Value__c = 'Test');
        insert oPRPId5;
        system.assertEquals(oPRPId5.id!=null,true);
        
        SOFTPHONE_PRPID_HUM__c oPRPId6 = new SOFTPHONE_PRPID_HUM__c( Name = 'TaxId2' , Value__c = 'Test');
        insert oPRPId6;
        system.assertEquals(oPRPId6.id!=null,true);
        
        HUM_Part_D_State_Mapping__c PartDObj = new HUM_Part_D_State_Mapping__c();
        PartDObj.Name = 'Test Name';
        PartDObj.State__c = 'Test State';
        PartDObj.State_Code__c = 'DE';
        PartDObj.Priority__c = 2;
        Insert PartDObj; 
        system.assert(PartDObj.id!=null);

        HUMConstants__c objCons = new HUMConstants__c (); //DK start
        objCons.Name='TENANTID_GEN';
        objCons.StringValue__c  ='00';
        insert objCons;
        system.assert(objCons.id != null);  //DK END
    }
    
    /**
* Method to test search and creation of account and custom settings
* 
* @param    None
* @return   Void 
*/
    static testMethod void prepareTestDataForSearchCriteria()
    {	
        
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        account oacc=[select id ,recordtypeid,name ,NPI_ID__c,Individual_First_Name__c,Individual_Last_Name__c,Provider_Classification__c,billingstate,billingpostalcode,Work_Email__c from account where name='duck' limit 1];
        account oacc11=[select id ,recordtypeid,name ,NPI_ID__c,Individual_First_Name__c,Individual_Last_Name__c,Provider_Classification__c,billingstate,billingpostalcode from account where name='duck11' limit 1];
        
        List<Account> oaccNew=[select id,Name,Enterprise_Id__c,Account_External_ID__c,DBA__c,Provider_Classification__c ,Individual_First_Name__c,Individual_Last_Name__c,Degree__c,
                               Source_Platform_Code__c,Birthdate__c ,Gender__c,BillingStreet,BillingPostalCode,BillingStatecode,BillingCity,           phone,
                               Phone_Ext__c,NPI_ID__c,Description,Taxonomy_Code__c,ShippingStatecode,shippingPostalCode,ShippingCity,ShippingStreet,RecordType.Name  from account where phone='11111'];
        
        List<ConsumerID__c> listConsumerId=[Select Consumer_ID__c, ID_Type__c,ETL_Record_Deleted__c,Account__c from ConsumerID__c where Consumer_ID__c='666666666' OR Consumer_ID__c='555555555' OR Consumer_ID__c='777777777' ];
        String consumerString='';
        String externalID='123456789';
		ProviderSearch_LD_HUM.mapAconsumerAccount= New map<String,list<ConsumerID__c>>();
        UnknownProviderSearch_LD_HUM.mapAconsumerAccount= New map<String,list<ConsumerID__c>>();
        ProviderSearch_LD_HUM.mapAconsumerAccount.put(oaccNew[0].id,listConsumerId);
        UnknownProviderSearch_LD_HUM.mapAconsumerAccount.put(oaccNew[0].id,listConsumerId);
        
        
        For (ConsumerID__c consumerId:listConsumerId )
        {
            consumerString=consumerString+consumerId.id;
        }
        
        
        ProviderSearch_LC_HUM  oHumproviderasync = new ProviderSearch_LC_HUM();
        Map<String, String> mapEncryptedResult = new Map<String, String>
        {
            System.Label.HUMConsumerTaxID => '('+System.Label.HUMConsumerTaxID+' = \'123456789\') ',
                'Individual_First_Name__c' => '(Individual_First_Name__c Like \'testname%\') ',
                'Individual_Last_Name__c' => '(Individual_Last_Name__c LIKE \'firstname%\') ',
                'PersonMailingPostalCode' => '(PersonMailingPostalCode = \'12345\') ',
                'NPI_ID__c' => '(NPI_ID__c = \'0123456789\')',
                'Group_Number__c' => '(Group_Number__c = \'987654321\') ',
                'Name' => '( Name Like \'12345%\') '
                };
                    
                    system.runAs(oUser )   {
                        Test.starttest();
                        ProviderSearch_LD_HUM.getProviderWrapper(oaccNew);
                        UnknownProviderSearch_LD_HUM.getProviderWrapper(oaccNew);
                        HUMProviderSearchWrapperHelper opwrapp = new  HUMProviderSearchWrapperHelper ();
                        opwrapp.sTaxId='123456789';
                        opwrapp.sState= 'None';
                        opwrapp.sSpeciality= 'None';   
                        opwrapp.sFirstName='';
                        opwrapp.sLastName='';
                        opwrapp.sFacilityName ='';
                        opwrapp.sNPI = '';
                        opwrapp.sPostalCode=''; 
                        ProviderSearch_LC_HUM cont = new ProviderSearch_LC_HUM();
                        ProviderSearch_LC_HUM.providerformData opwrapp1= new ProviderSearch_LC_HUM.providerformData();
                        
                        opwrapp1.sTaxId='123456789';
                        opwrapp1.sState= 'None';
                        opwrapp1.sSpeciality= 'None';   
                        opwrapp1.sFirstName='';
                        opwrapp1.sLastName='';
                        opwrapp1.sFacilityName ='';
                        opwrapp1.sNPI = '';
                        opwrapp1.sPostalCode='';   
                        ProviderSearch_LC_HUM.getSpeciality(); 
                        ProviderSearch_LC_HUM.getAccountCaseList(oacc.id);
                        ProviderSearch_LC_HUM.getInteractionList(oacc.id);
                        ProviderSearch_LC_HUM.insertProviderAccount(consumerString, JSON.serialize(oacc) , '123456789');
                        //ProviderSearch_LC_HUM.retrieveAccountForProiderLookup(consumerString, JSON.serialize(oacc) , '123456789');
                        ProviderSearch_LD_HUM.insertDataFromExternalSystem(oaccNew[0]);

                        Map<String, String> mapRequestDto = new Map<String, String>();
                        mapRequestDto.put('sLastName', opwrapp.sLastname);
                        mapRequestDto.put('sTaxID', opwrapp.sTaxID);
                        mapRequestDto.put('sNPI', opwrapp.sNPI);
                        mapRequestDto.put('sFirstName', opwrapp.sFirstName);
                        mapRequestDto.put('sFacilityName', opwrapp.sFacilityName);
                        mapRequestDto.put('sPostalCode', opwrapp.sPostalCode);
                        
                        Continuation oConti = ProviderSearch_LC_HUM.search(opwrapp1);
                        ProviderSearch_LC_HUM.searchUnknown(opwrapp1);
                        ProviderSearch_LD_HUM.requestListAccountUsingSFDC(opwrapp);
                        ProviderSearch_LC_HUM.createUnknownProviderSearch(opwrapp1);
                        UnknownProviderSearch_LD_HUM.requestListAccountUsingSFDC(opwrapp);

                        id iAccntId =oacc.id;
                        SearchUtilty_H_HUM.getStateValues();
                        Test.stoptest();    
                    }
    }
   
    /**
* Method to test search records internally
* 
* @param    None
* @return   Void 
*/
    static testMethod void TestProviderInternalClassInternal() 
    {
        ProviderSearch_LC_HUM  oProvidrtInternal= new ProviderSearch_LC_HUM  ();
        HUMProviderSearchWrapperHelper opwrapp1= new HUMProviderSearchWrapperHelper();
        
        opwrapp1.staxId='123456789';
        opwrapp1.sState= 'Alabama';
        opwrapp1.sSpeciality= 'HeallthNurition';   
        opwrapp1.sfirstname='testname';
        opwrapp1.slastname='firstname';
        opwrapp1.sfacilityname ='TestGroup004';
        opwrapp1.sNpi = '0123456789';
        opwrapp1.sPostalCode='12345';
        
        Test.startTest();
        
        ProviderSearch_LD_HUM.requestListAccountUsingSFDC(opwrapp1);
        opwrapp1.sfacilityname ='';
        ProviderSearch_LD_HUM.requestListAccountUsingSFDC(opwrapp1);
        Test.stopTest();
    }
    
    /**
* Method to test search validations
* 
* @param    None
* @return   Void 
*/
    static testMethod void testproviderSearchValidations()
    {
        ProviderSearch_LC_HUM  oHumProvider= new ProviderSearch_LC_HUM();
        ProviderSearch_LC_HUM.providerformData owrp= new ProviderSearch_LC_HUM.providerformData();
        
        
        Test.startTest();
        owrp.sNPI='';
        System.AssertEquals(owrp .sNPI, '');
        owrp.sTaxID= '';
        System.AssertEquals(owrp.sTaxID, '');
        owrp.sfirstname= '';
        System.AssertEquals(owrp.sfirstname, '');
        owrp.slastname= '';
        System.AssertEquals(owrp.slastname, '');
        owrp.sstate= 'None';
        System.AssertEquals(owrp.sstate, 'None');
        owrp.sfacilityname= '';
        System.AssertEquals(owrp.sfacilityname, '');
        owrp.sspeciality= 'None';
        System.AssertEquals(owrp.sspeciality, 'None');
        owrp.sPostalCode = '';
        System.AssertEquals(owrp.sPostalCode, '');
        ProviderSearch_LC_HUM.search(owrp);

        Test.stopTest();    
    }
    

    /**
* Method to test search results from EPS service
* 
* @param    None
* @return   Void 
*/
    static testMethod void testproviderserviceClassIntegration() 
    {  
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        { 
            HUMProviderSearchControllerV2 oProvIntegration= new HUMProviderSearchControllerV2();
            ProviderSearch_LC_HUM oProvIntegration1= new ProviderSearch_LC_HUM();
            
            ProviderSearch_LC_HUM.providerformData owrp= new ProviderSearch_LC_HUM.providerformData();
            
            owrp.sFirstName= 'FirstProv';
            owrp.sspeciality = 'Allergy / Immunology';
            owrp.sstate = 'None';
            owrp.sLastName= 'LastProv';
            owrp.sPostalCode = '';
            Test.startTest();
            Continuation con = (Continuation)ProviderSearch_LC_HUM.search(owrp);
            HttpResponse resEPSS = new Httpresponse();
            String EPSRes = '{ "SearchProviderResponse":{ "EnterpriseProviders":[ { "EnterpriseID":"7982118", "UniqueID":{ "Source":"PIM" }, "ProviderName":'+
                '{ "Last":"Norton", "First":"Artis", "Degree":"CRNA" }, "DbaName":"Norton, Artis A CRNA", "Classification":"Individual", "Demographics":'+
                '{ "Addresses":[ { "AddressType":"Billing", "Line1":"DEPT 52519", "Line2":"PO BOX 950123", "City":"Louisville", "State":"KY", '+
                '"Zip":"402950123", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"6066725243", "Extension":"" }, { "PhoneType":"Phone", '+
                '"PhoneNumber":"2035737137", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2035746428", "Extension":"" } ] }, '+
                '{ "AddressType":"Service", "Line1":"3605 Northgate Ct", "Line2":"Ste 101", "City":"New Albany", "State":"IN", "Zip":"471506400", '+
                '"Phones":[ { "PhoneType":"Phone", "PhoneNumber":"8129444263", "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 23354", '+
                '"Line2":"", "City":"LEXINGTON", "State":"KY", "Zip":"405233354", "Phones":[ ] }, { "AddressType":"Service", "Line1":"225 Abraham Flexner Way",'+
                ' "Line2":"Ste 700", "City":"Louisville", "State":"KY", "Zip":"402021846", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"5025614263",'+
                ' "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 950286", "Line2":"", "City":"LOUISVILLE", "State":"KY", "Zip":"402950286",'+
                ' "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2706866281", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"5025626810", '+
                '"Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2708526600", "Extension":"" } ] } ], "DateOfBirth":"1956-02-23", "Gender":"Female" },'+
                ' "Identifiers":[ { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"610677948" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ '+
                '{ "ID":"611230160" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"273645560" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { '+
                '"ID":"611029626" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ '+
                '{ "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":'+
                '[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"273645560" }, { "ID":"611230160" } ] },'+
                ' { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"371431151" }, { "ID":"611029626" }, { "ID":"611230160" } ] }, '+
                '{ "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, '+
                '{ "IdentifierType":"NpiID", "Identifiers":[ { "ID":"1841284874" } ] } ], "Specialties":[ { "Description":"Nurse Anesthetist [PIMS]" } ], '+
                '"SourceProviders":[ { "Source":"CAS", "ProviderID":"000001225KD" }, { "Source":"CAS", "ProviderID":"000025864AD" }, { "Source":"CAS", '+
                '"ProviderID":"000192008ED" }, { "Source":"CAS", "ProviderID":"000206339TD" }, { "Source":"CAS", "ProviderID":"000211314OD" }, { "Source":"CAS", '+
                '"ProviderID":"000217327 D" }, { "Source":"CAS", "ProviderID":"001110565ZD" }, { "Source":"CAS", "ProviderID":"371431151CD" }, { "Source":"MTV",'+
                ' "ProviderID":"10654338098" }, { "Source":"MTV", "ProviderID":"187710093124" }, { "Source":"NPI", "ProviderID":"1841284874" }, { "Source":"PIM",'+
                ' "ProviderID":"2045182" }, { "Source":"PIM", "ProviderID":"737548" } ], "Taxonomy":[ { "Code":"367500000X" }, { "Code":"163W00000X" }, '+
                '{ "Code":"207L00000X" } ] } ] } }';
            resEPSS.setBody(EPSRes);
            resEPSS.setStatusCode(200);
            system.assert(resEPSS.getBody() == EPSRes);
            Test.setContinuationResponse(ProviderSearch_LC_HUM.sRequestLabelPID,resEPSS); 
            List<String> lstLabels = new List<String>{'label1','label2'};
            Object state;
            ProviderSearch_LC_HUM.processResponse(lstLabels,state);
            //below lines added to cover if condition of requestProcessResponse function
            List<string> labelList = new List<string>();
            labelList.add(ProviderSearch_LC_HUM.sRequestLabelPID);
            ProviderSearch_LC_HUM.processResponse(labelList,state);            
            
            Test.stopTest();
        }
    } 
    
    /**
* Method to test negative scenario for EPSS service call
* 
* @param    None
* @return   Void 
*/
    static testMethod void testproviderserviceClassIntegrationNeg() 
    {  
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        { 
            HUMProviderSearchControllerV2 oProvIntegration= new HUMProviderSearchControllerV2();
            ProviderSearch_LC_HUM oProvIntegration1= new ProviderSearch_LC_HUM();
            
            ProviderSearch_LC_HUM.providerformData owrp= new ProviderSearch_LC_HUM.providerformData();
            
            owrp.sFirstName= 'FirstProv';
            owrp.sspeciality = 'Allergy / Immunology';
            owrp.sstate = 'None';
            owrp.sLastName= 'LastProv';
            owrp.sPostalCode = '';
            Test.startTest();
            Continuation con = (Continuation)ProviderSearch_LC_HUM.search(owrp);
            HttpResponse resEPSS = new Httpresponse();
            String EPSRes = '';
            resEPSS.setBody(EPSRes);
            resEPSS.setStatusCode(400);
            system.assert(resEPSS.getBody() == EPSRes);
            Test.setContinuationResponse(ProviderSearch_LC_HUM.sRequestLabelPID,resEPSS);
            Test.stopTest();
            
        }
    } 
    

    
    /**
* Method to test prepareConsumerIDMap
* 
* @param    List<ConsumerID__c>
* @return   Void 
*/
    static testMethod void testprepareConsumerIDMap()
    {
        Test.starttest();    
        ProviderSearch_LD_HUM cont = new ProviderSearch_LD_HUM();
        list<ConsumerID__c> cons = new list<ConsumerID__c>();
        cons = [SELECT Id, Consumer_ID__c, ETL_Record_Deleted__c, ID_Type__c, Account__c FROM ConsumerID__c where Consumer_ID__c='555555555' OR Consumer_ID__c='666666666' limit 2];
        ProviderSearch_LD_HUM.mapAconsumerAccount = New Map<string,List<ConsumerID__c>>(); 
        ProviderSearch_LD_HUM.prepareConsumerIDMap(cons);
        UnknownProviderSearch_LD_HUM.mapAconsumerAccount = New Map<string,List<ConsumerID__c>>();
        UnknownProviderSearch_LD_HUM.prepareConsumerIDMap(cons);
        Test.stoptest();
    }
    /**
* This method will be used to  test filterSOSLResults
* 
* @param List<Account> SOSL results
* @param List<Account> Filtered results
* @return   Void
*/
    static testmethod void FilterTest()
    {
        ProviderSearch_LC_HUM cont = new ProviderSearch_LC_HUM();
        list<ConsumerID__c> cons = new list<ConsumerID__c>();
        List<Account> lstProvider = new list<Account>();
        List<Account> lstProviderTemp = new list<Account>();
        lstProvider = [select Individual_First_Name__c,Individual_Last_Name__c,Name from Account limit 10];
        HUMProviderSearchWrapperHelper opwrapp = new  HUMProviderSearchWrapperHelper ();
 
        opwrapp.sfirstname='Test';
        opwrapp.slastname='Test1';
        opwrapp.sfacilityname ='Test Facility';

        ProviderSearch_LD_HUM.FilterSOSLResults(lstProvider, lstProviderTemp, opwrapp);
        opwrapp.sfirstname='';
        opwrapp.slastname='Test1';
        opwrapp.sfacilityname ='';

        ProviderSearch_LD_HUM.FilterSOSLResults(lstProvider, lstProviderTemp, opwrapp); 
        opwrapp.sfirstname='';
        opwrapp.slastname='';
        opwrapp.sfacilityname ='Test Facility';

        ProviderSearch_LD_HUM.FilterSOSLResults(lstProvider, lstProviderTemp, opwrapp);
        
        Account acc = new Account();
        acc.Name = 'test';
        acc.Account_External_Id__C = '12345678123456';
		ProviderSearch_LD_HUM.insertDataFromExternalSystem(acc);
        map<String,String> mapstr = new map<String,String>();
        mapstr.put('123454321', '123454321');
        ProviderSearch_LD_HUM.upsertConsumerTaxIds(acc.Id,mapstr);
        HUMProviderSearchWrapperHelper opwrapp1 = new  HUMProviderSearchWrapperHelper ();
        opwrapp1.staxID='123456789';
        opwrapp1.sState= 'None';
        opwrapp1.sSpeciality= 'None';   
        opwrapp1.sfirstname='';
        opwrapp1.slastname='';
        opwrapp1.sfacilityname ='';
        opwrapp1.sNpi = '';
        opwrapp1.sPostalCode='';
        ConsumerID__c cons_queryBuildTestCase1 = new ConsumerID__c();
        cons_queryBuildTestCase1.Consumer_ID__c = '123456789';
        cons_queryBuildTestCase1.ID_Type__c = 'TaxID';
        cons_queryBuildTestCase1.ETL_Record_Deleted__c = false;
        cons_queryBuildTestCase1.Account__c = acc.id;            
        insert cons_queryBuildTestCase1;
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = cons_queryBuildTestCase1.Id;
        Test.setFixedSearchResults(fixedSearchResults);
        ProviderSearch_LD_HUM.requestListAccountUsingSFDC(opwrapp1);
        
        Account accObj=[Select id,RecordTypeId,Work_Email__c from Account where Name =: 'Test Account' Limit 1];
        ProviderSearch_LD_HUM.getEmailForAgentAccount(accObj);
        HUMProviderSearchWrapperHelper opwrappNew = new  HUMProviderSearchWrapperHelper ();
        opwrappNew.sfirstname='';
        opwrappNew.slastname='';
        opwrappNew.sfacilityname ='';
        ProviderSearch_LD_HUM.requestListAccountUsingSFDC(opwrappNew);
        lstProvider.add(accObj);
        ProviderSearch_LD_HUM.FilterSOSLResults(lstProvider, lstProviderTemp, opwrappNew);
        ProviderSearch_LD_HUM.insertDataFromExternalSystem(null);
    
    }
}