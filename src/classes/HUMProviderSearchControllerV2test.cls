/*****************************************************************************************
Apex class Name    : HUMProviderSearchControllerV2Test
Version            : 1.0
Function           : Test class for HUMProviderSearchControllerV2
Created Date       : 5 Aug 2014

Modification Log:
*   Developer                        Code Review    Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Ranjeeth  Nagishetty              25261         08/05/2014          Original Version
*  Pradeepkumar Dani                               01/22/2016          Increased test coverage after latency changes
                                                                        Added Method: testProviderSearchQueryBuildTest
*  Gargey Sharma                     47056         11/01/2016          Modified testproviderserviceClassIntegration() method as per REQ - 270080(EPSS-SOAP to REST conversion)                                                                      
*  Shruthi Karanth                   84636         04/14/2017          Softphone Optimization to skip DPaaS callout when coming through softphone routing                                                                      
*  Santhi Mandava                    262341        07/27/2018          Added platform encryption changes
*  Santhi Mandava                    355559        05/10/2019          Removed "ENCRYPTIONTYPE" custom setting creation
* Daniel Laratta								   02/24/2021		   User Story 1875439: T1PRJ0001827 - MF 1 - AHT - Ability to select "Interacting With and About" button on the Provider Search tab (RF)
******************************************************************************************/
@isTest
private  class HUMProviderSearchControllerV2test
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
        insert oacc;
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
          
        SOFTPHONE_PRPID_HUM__c oPRPId7 = new SOFTPHONE_PRPID_HUM__c( Name = 'NpiId' , Value__c = 'Test');
        insert oPRPId7;
        system.assertEquals(oPRPId7.id!=null,true);
         
        SOFTPHONE_PRPID_HUM__c oPRPId8 = new SOFTPHONE_PRPID_HUM__c( Name = 'NpiId2' , Value__c = 'Test');
        insert oPRPId8;
        system.assertEquals(oPRPId8.id!=null,true); 
         
        HUM_Part_D_State_Mapping__c PartDObj = new HUM_Part_D_State_Mapping__c();
        PartDObj.Name = 'Test Name';
        PartDObj.State__c = 'Test State';
        PartDObj.State_Code__c = 'DE';
        PartDObj.Priority__c = 2;
        Insert PartDObj; 
        system.assert(PartDObj.id!=null);
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
        account oacc=[select id ,name  from account where name='duck' limit 1];
        HUMProviderSearchControllerV2  oHumproviderasync = new HUMProviderSearchControllerV2();
        Test.setCurrentPage(Page.HUMCustomSearchV2);
        //Test.setMock(WebServiceMock.class, new DpaaSWebServiceMockImpl());
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
        oHumproviderasync.mapEncryptedResult = mapEncryptedResult;
        
        system.runAs(oUser )   {
         Test.starttest();
         HUMProviderSearchWrapperHelper opwrapp = new  HUMProviderSearchWrapperHelper ();
         HUMProviderSearchControllerV2 cont = new HUMProviderSearchControllerV2();
         cont.changeDateStringFormat('1999-01-01');
            HUMProviderSearchControllerV2.getSpeciality(); 
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            opwrapp.staxId='123456789';
            opwrapp.sState= 'None';
            opwrapp.sSpeciality= 'None';   
            opwrapp.sfirstname='';
            opwrapp.slastname='';
            opwrapp.sfacilityname ='';
            opwrapp.sNpi = '';
            opwrapp.sPostalCode='';
            Map<String, String> mapRequestDto = new Map<String, String>();
            mapRequestDto.put('sLastName', opwrapp.sLastname);
            mapRequestDto.put('sTaxID', opwrapp.sTaxID);
            mapRequestDto.put('sNPI', opwrapp.sNPI);
            mapRequestDto.put('sFirstName', opwrapp.sFirstName);
            mapRequestDto.put('sFacilityName', opwrapp.sFacilityName);
            mapRequestDto.put('sPostalCode', opwrapp.sPostalCode);
            
            List<SelectOption> lstprostateval=oHumproviderasync.lstProviderStatevalue;
            oHumproviderasync.searchUnknownAccounts();
            Continuation oConti = oHumproviderasync.search();
            //Continuation contisearchl = 
            oHumproviderasync.searchSFDC();
            
            id iAccntId=oacc.id;
            oHumproviderasync.buildUnknownAccountMap(iAccntId);
            oHumproviderasync.bSearchflag =false;
            HUMUtilityHelper.getstateValue();
            
         Test.stoptest();    
        }
    }
    
    /**
    * Method to test validation rules
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testproviderSearchValidationProviders()
    {
        Test.starttest();
        
        HUMProviderSearchControllerV2  oPrvdSrch = new HUMProviderSearchControllerV2 ();
        System.assertNotEquals(oPrvdSrch , NULL);
        oPrvdSrch.oSearchWrapper.sfirstname='';
        System.AssertEquals( oPrvdSrch.oSearchWrapper.sfirstname, '');
        oPrvdSrch.oSearchWrapper.slastname= '';
        System.AssertEquals( oPrvdSrch.oSearchWrapper.slastname, '');
        oPrvdSrch.oSearchWrapper.sstate= 'Alaska';
        System.AssertEquals( oPrvdSrch.oSearchWrapper.sstate, 'Alaska');
        oPrvdSrch.oSearchWrapper.sfacilityname= '';
        System.AssertEquals( oPrvdSrch.oSearchWrapper.sfacilityname, '');
        oPrvdSrch.oSearchWrapper.sspeciality= 'none';
        System.AssertEquals( oPrvdSrch.oSearchWrapper.sspeciality, 'none');
        
        oPrvdSrch.search();
        system.assert(true,oPrvdSrch.search()!=NULL);
        Test.stoptest();  
    }
    
    /**
    * Method to test validations
    * 
    * @param    None
    * @return   Void 
    */
     static testMethod void testproviderSearchFirstnameValidations()
    {
        Test.starttest();
    
        HUMProviderSearchControllerV2  oPrvdSrch = new HUMProviderSearchControllerV2  ();
        HUMProviderSearchWrapperHelper opwrapp = new  HUMProviderSearchWrapperHelper ();
        opwrapp.sfirstname='testfirst';
        opwrapp.slastname= '';
        oPrvdSrch.search();
        opwrapp.sfirstname= 'testfirst';
        opwrapp.slastname= '';
        opwrapp.sSpeciality='ALU';
        oPrvdSrch.search();
        System.assertNotEquals(oPrvdSrch , NULL);
       
        Test.stoptest();
    }     

    /**
    * Method to test validations for state
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testproviderSearchstateValidations()
    {
        HUMProviderSearchControllerV2 oHumProvider= new HUMProviderSearchControllerV2 ();
      
        Test.startTest();
        oHumProvider.oSearchWrapper.sfirstname='nanifirstname';
        oHumProvider.oSearchWrapper.slastname= 'testlastname';
        oHumProvider.oSearchWrapper.sstate= 'Arizona';
        oHumProvider.search();
        oHumProvider.searchInSFDC();
        System.assertNotEquals(oHumProvider , NULL);
        Test.Stoptest();
    }
    
    /**
    * Method to test validations of speciality
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testproviderSpecialityValidations()
    {
        HUMProviderSearchControllerV2 oHumProvider= new HUMProviderSearchControllerV2 ();
        Account oacc = HUMTestDataHelperTest.getAccount();
        oacc.Name ='Test Account Cases';
        oacc.Provider_Classification__c='I';
        oacc.Primary_Specialty__c='Allergery';
        insert oacc;
        system.assert(oacc.id!=null);
        Test.startTest();
        List<SelectOption> lstprostateval=oHumProvider.lstProviderStatevalue;
        oHumProvider.oSearchWrapper.sfirstname= 'nanifirstname';
        oHumProvider.oSearchWrapper.slastname= '';
        oHumProvider.oSearchWrapper.sspeciality= 'none';
        System.assertNotEquals(oHumProvider , NULL);
        Test.Stoptest();
    }
    
    /**
    * Method to test validations of postal code
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testproviderSearchpostalcodeValidations()
    {
        HUMProviderSearchControllerV2  oHumProvider= new HUMProviderSearchControllerV2 ();
        
        Test.startTest();
        List<SelectOption> lstprostaval=oHumProvider.lstProviderStatevalue;
        oHumProvider.oSearchWrapper.sfirstname='nanifirstname';
        System.AssertEquals(oHumProvider.oSearchWrapper.sfirstname, 'nanifirstname');
        oHumProvider.oSearchWrapper.slastname= 'test';
        System.AssertEquals(oHumProvider.oSearchWrapper.slastname, 'test');
        oHumProvider.oSearchWrapper.spostalcode= '12345';
        System.AssertEquals(oHumProvider.oSearchWrapper.spostalcode, '12345');
        oHumProvider.oSearchWrapper.sfacilityName= '';
        System.AssertEquals(oHumProvider.oSearchWrapper.sfacilityName, '');
        oHumProvider.search();
        oHumProvider.searchInSFDC();
        System.assertNotEquals(oHumProvider , NULL);
        Test.Stoptest();
    }

    /**
    * Method to test search records internally
    * 
    * @param    None
    * @return   Void 
    */
     static testMethod void TestProviderInternalClassInternal() 
     {
        HUMProviderSearchControllerV2  oProvidrtInternal= new HUMProviderSearchControllerV2  ();
        Test.startTest();
        oProvidrtInternal.oSearchWrapper.sTaxID='123456789';
        oProvidrtInternal.oSearchWrapper.sFacilityName= 'TestGroup004';
        oProvidrtInternal.oSearchWrapper.sfirstName= 'testname';
        oProvidrtInternal.oSearchWrapper.sLastName= 'firstname';
        oProvidrtInternal.oSearchWrapper.sNPI= '0123456789';
        oProvidrtInternal.oSearchWrapper.sPostalcode= '12345';
        oProvidrtInternal.oSearchWrapper.sspeciality= 'HeallthNurition';
        oProvidrtInternal.oSearchWrapper.sstate= 'Alabama';
        oProvidrtInternal.oSearchWrapper.sBirthdate= '1997-05-05';
        System.assert(true,oProvidrtInternal.lstSObjects.size() > 0);
        oProvidrtInternal.search();
        System.assert(true,oProvidrtInternal.search()!= NULL);
        oProvidrtInternal.searchInSFDC();
        System.assert(true,oProvidrtInternal.searchInSFDC()!= NULL);
        oProvidrtInternal.searchUnknownAccounts();
        System.assert(true,oProvidrtInternal.searchUnknownAccounts()!= NULL);
        System.assertNotEquals(oProvidrtInternal , NULL);
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
        HUMProviderSearchControllerV2  oHumProvider= new HUMProviderSearchControllerV2  ();
        HUMProviderSearchWrapperHelper owrp = new HUMProviderSearchWrapperHelper();
        
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
        oHumProvider.search();
        oHumProvider.searchInSfdc();
        HUMUtilityHelper.validateCombinations(owrp ); 
        System.assertNotEquals(oHumProvider , NULL); 
        Test.stopTest();    
    }
    
    /**
    * Method to test scenario for facility name
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testproviderFacilitysValidations()
    {
        account oacc=[select id ,name  from account where name='Test Account' limit 1];
        HUMProviderSearchControllerV2  oHumProvider= new HUMProviderSearchControllerV2  ();
        oHumProvider.oSearchWrapper.sTaxID = '';
        System.AssertEquals(oHumProvider.oSearchWrapper.sTaxID, '');
        oHumProvider.oSearchWrapper.sNPI = '';
        System.AssertEquals(oHumProvider.oSearchWrapper.sNPI , '');
        oHumProvider.oSearchWrapper.sfacilityname= 'Test Account';
        System.AssertEquals(oHumProvider.oSearchWrapper.sfacilityname, 'Test Account');
        oHumProvider.oSearchWrapper.sfirstname= '';
        System.AssertEquals(oHumProvider.oSearchWrapper.sfirstname, '');
        oHumProvider.oSearchWrapper.slastname= '';
        System.AssertEquals(oHumProvider.oSearchWrapper.slastname, '');
        oHumProvider.searchSFDC();
        oHumProvider.searchInSFDC();
        
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
            oProvIntegration.oSearchWrapper.sFirstName= 'FirstProv';
            oProvIntegration.oSearchWrapper.sspeciality = 'Allergy / Immunology';
            oProvIntegration.oSearchWrapper.sstate = 'None';
            oProvIntegration.oSearchWrapper.sLastName= 'LastProv';
            oProvIntegration.oSearchWrapper.sPostalCode = '';
            Test.startTest();
            Continuation con = (Continuation)oProvIntegration.search();
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
            Test.setContinuationResponse(oProvIntegration.sEPSSReq,resEPSS);
            Test.invokeContinuationMethod(oProvIntegration,con);
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
            oProvIntegration.oSearchWrapper.sFirstName= 'FirstProv';
            oProvIntegration.oSearchWrapper.sspeciality = 'Neurology';
            oProvIntegration.oSearchWrapper.sstate = 'None';
            oProvIntegration.oSearchWrapper.sLastName= 'LastProv';
            oProvIntegration.oSearchWrapper.sPostalCode = '';
            Test.startTest();
            Continuation con = (Continuation)oProvIntegration.search();
            HttpResponse resEPSS = new Httpresponse();
            String EPSRes = '';
            resEPSS.setBody(EPSRes);
            resEPSS.setStatusCode(400);
            system.assert(resEPSS.getBody() == EPSRes);
            Test.setContinuationResponse(oProvIntegration.sEPSSReq,resEPSS);
            Test.invokeContinuationMethod(oProvIntegration,con);
            Test.stopTest();
        }
    } 
    
    /**
    * Method to test QueryBuild
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testProviderSearchQueryBuildTest()
    {
        HUMProviderSearchControllerV2 oHumProvider= new HUMProviderSearchControllerV2 ();
        Test.startTest();
        oHumProvider.oSearchWrapper.sTaxId ='555555555';
        oHumProvider.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c' =>  'Consumer_ID__c='+'\''+'555555555'+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+'TEST'+'\'','Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+'TEST'+'\''};
        oHumProvider.searchInSFDC();
        System.assert(true,oHumProvider.oSearchWrapper.sTaxId <> NULL);
        
        oHumProvider.mapEncryptedResult.put('BillingPostalCode', 'BillingPostalCode =' +'\''+'54321'+'\'');
        oHumProvider.oSearchWrapper.sPostalCode = '54321';
        oHumProvider.oSearchWrapper.sFirstName = 'TEST';
        oHumProvider.oSearchWrapper.sLastName = 'TEST';
        oHumProvider.searchInSFDC();
        System.assert(true,oHumProvider.oSearchWrapper.sPostalCode <> NULL);
        
        oHumProvider.oSearchWrapper.sFirstName = 'TEST';
        oHumProvider.oSearchWrapper.sLastName = 'TEST';
        oHumProvider.searchInSFDC();
        System.assert(true,oHumProvider.oSearchWrapper.sFirstName <> NULL);
        
        oHumProvider.mapEncryptedResult.put('Name', 'Name =' +'\''+'TEST TEST'+'\''); 
        oHumProvider.oSearchWrapper.sFirstName = '';
        oHumProvider.oSearchWrapper.sLastName = '';      
        oHumProvider.oSearchWrapper.sFacilityName = 'TEST';
        oHumProvider.searchInSFDC();
        System.assert(true,oHumProvider.oSearchWrapper.sFacilityName <> NULL);
        
        System.assert(true,oHumProvider.lstSObjects.size() > 0);
        
        oHumProvider.mapConsumerTaxIds= new Map<Integer,String>();
        oHumProvider.oSearchWrapper.sAgencyName = 'TEST TEST';
        oHumProvider.oSearchWrapper.sLastName =  'TEST';
        oHumProvider.oSearchWrapper.sState = 'Alabama';     
        oHumProvider.oSearchWrapper.sTaxId = '555555555';
        oHumProvider.bIsProviderError = true;
        oHumProvider.search();
        oHumProvider.processResponse();  
        System.assert(true,oHumProvider.bIsProviderError);          
        
        oHumProvider.oSearchWrapper.sLastName =  '';
        oHumProvider.oSearchWrapper.sState = 'None';           
        oHumProvider.oSearchWrapper.sNPI= '1245351000';
        oHumProvider.oSearchWrapper.sTaxId = '';
        oHumProvider.bIsProviderError = true;
        oHumProvider.searchSFDC();
        oHumProvider.processResponseSFDC();
        oHumProvider.searchInsfdc(); 
        System.assert(true,oHumProvider.bIsProviderError);
        
           
        oHumProvider.Reset();                 
        System.assert(true,oHumProvider.lstSObjects.size() == 0);
        
        oHumProvider.getAccountProviderList(NULL);
        System.assert(true,oHumProvider.bIsProviderError);
        
        Test.Stoptest();
    }
 
 /**
    * Method to test QueryBuild from Softphone
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testProviderSearchQueryBuildSoftphoneTest()
    {
        HUMProviderSearchControllerV2 oHumProvider= new HUMProviderSearchControllerV2 ();
        Map<String,String> mapSoftPhone = new Map<String,String>{'Test' => 'Test'};
        Test.startTest();
            oHumProvider.oSearchWrapper.mapEncryptedValues = mapSoftPhone;
            oHumProvider.oSearchWrapper.sTaxId ='555555555';
            oHumProvider.mapEncryptedResult = new Map<String,String>{'Consumer_ID__c' =>  'Consumer_ID__c='+'\''+'555555555'+'\'','Individual_First_Name__c'=> 'Individual_First_Name__c =' +'\''+'TEST'+'\'','Individual_Last_Name__c'=> 'Individual_Last_Name__c =' +'\''+'TEST'+'\''};
            oHumProvider.searchInSFDC();
            System.assert(true,oHumProvider.oSearchWrapper.sTaxId <> NULL);
            
            oHumProvider.mapEncryptedResult.put('Name', 'Name =' +'\''+'TEST TEST'+'\''); 
            oHumProvider.oSearchWrapper.sFirstName = '';
            oHumProvider.oSearchWrapper.sLastName = '';      
            oHumProvider.oSearchWrapper.sFacilityName = 'TEST';
            oHumProvider.searchInSFDC();
            System.assert(true,oHumProvider.oSearchWrapper.sFacilityName <> NULL);
            
            System.assert(true,oHumProvider.lstSObjects.size() > 0);
        
        Test.Stoptest();
    }
    /**
    * Method to test cover
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testfieldCipherMapping()
    {
         User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
       system.runAs(oUser )   {
         Test.starttest();
         HUMProviderSearchWrapperHelper opwrapp = new  HUMProviderSearchWrapperHelper ();
         HUMProviderSearchControllerV2 cont = new HUMProviderSearchControllerV2();
         cont.changeDateStringFormat('1999-01-01');
            HUMProviderSearchControllerV2.getSpeciality(); 
            Test.setCurrentPage(Page.HUMCustomSearchPage);
            opwrapp.staxId='123456789';
            opwrapp.sState= 'None';
            opwrapp.sSpeciality= 'None';   
            opwrapp.sfirstname='';
            opwrapp.slastname='';
            opwrapp.sfacilityname ='';
            opwrapp.sNpi = '';
            opwrapp.sPostalCode='';
            Map<String, String> mapRequestDto = new Map<String, String>();
            mapRequestDto.put('sLastName', opwrapp.sLastname);
            mapRequestDto.put('sTaxID', opwrapp.sTaxID);
            mapRequestDto.put('sNPI', opwrapp.sNPI);
            mapRequestDto.put('sFirstName', opwrapp.sFirstName);
            mapRequestDto.put('sFacilityName', opwrapp.sFacilityName);
            mapRequestDto.put('sPostalCode', opwrapp.sPostalCode);
        cont.fieldCipherMapping(opwrapp);
           cont.getMapEncryptedResult();
           cont.searchInSFDCFromSoftPhone();
           cont.bepsstimedout = true;
           cont.processResponseSFDC();
           cont.bepsstimedout = null;
           cont.processResponseSFDC();
           cont.npiId = '123456';
           cont.bIsSearchPage = true;
           cont.ConsumerIds = '123,123,123';
           cont.insertProviderAccount();
           cont.isSoftPhoneSearch = true;
           cont.searchInSFDC();
    }
    }
    /**
    * Method to test populateMapForState
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testpopulateMapForState()
    {

         Test.starttest();  
           HUMProviderSearchControllerV2 cont = new HUMProviderSearchControllerV2();
           HUM_Part_D_State_Mapping__c PartD = new HUM_Part_D_State_Mapping__c();
           PartD = [SELECT State_Code__c, State__c, Id,Name FROM HUM_Part_D_State_Mapping__c where State_Code__c='DE' limit 1];
            cont.populateMapForState(); 
        Test.stopTest();
           
           
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
           HUMProviderSearchControllerV2 cont = new HUMProviderSearchControllerV2();
           list<ConsumerID__c> cons = new list<ConsumerID__c>();
           cons = [SELECT Id, Consumer_ID__c, ETL_Record_Deleted__c, ID_Type__c, Account__c FROM ConsumerID__c where Consumer_ID__c='555555555' limit 1];
           cont.mapAconsumerAccount = new Map<string,List<ConsumerID__c>>(); 
           cont.prepareConsumerIDMap(cons);         
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
        HUMProviderSearchControllerV2 cont = new HUMProviderSearchControllerV2();
        list<ConsumerID__c> cons = new list<ConsumerID__c>();
        List<Account> lstProvider = new list<Account>();
        List<Account> lstProviderTemp = new list<Account>();
        lstProvider = [select Individual_First_Name__c,Individual_Last_Name__c,Name from Account limit 10];
        cont.oSearchWrapper.sFirstName = 'Test';
        cont.oSearchWrapper.sLastName = 'Test1';
        cont.oSearchWrapper.sFacilityName = 'Test Facility';
        cont.FilterSOSLResults(lstProvider,lstProviderTemp);        
        cont.oSearchWrapper.sFirstName = '';
        cont.oSearchWrapper.sLastName = 'Test1';
        cont.oSearchWrapper.sFacilityName = '';
        cont.FilterSOSLResults(lstProvider,lstProviderTemp);
        cont.oSearchWrapper.sFirstName = '';
        cont.oSearchWrapper.sLastName = '';
        cont.oSearchWrapper.sFacilityName = 'Test Facility';
        cont.FilterSOSLResults(lstProvider,lstProviderTemp);
        cons = [SELECT Id, Consumer_ID__c, ETL_Record_Deleted__c, ID_Type__c, Account__c FROM ConsumerID__c where Consumer_ID__c='555555555' limit 1];
        cont.mapAconsumerAccount = new Map<string,List<ConsumerID__c>>(); 
        cont.mapAconsumerAccount.put(cons[0].Account__c, new list<COnsumerId__c>());
        cont.prepareConsumerIDMap(cons);   
        cont.oSearchWrapper.sFirstName = '';
        cont.oSearchWrapper.sLastName = '';
        cont.oSearchWrapper.sFacilityName = '';
        cont.FilterSOSLResults(lstProvider,lstProviderTemp);
        
    }
 }