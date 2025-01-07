/**************************************************************************************************************************************
Apex Class Name :  HUMMemSearchEnrollmentControllerV2Test
Version: 2.0
Created On      : 08/12/2015
Function        : Test class for HUMMemberSearchEnrollmentControllerV2, HUMEESLegacyMemberSearchHelper

 * Modification Log
 * Developer Name            Code Review                 Date                       Description
 *---------------------------------------------------------------------------------------------------------------------------------------
 *  Shreya Choodamani                                    08/12/2015                 Original Version
 *  Ranjeeth Nagishetty          26266                   09/10/2015                 updated test class
 *  Shreya Choodamani            29217                   01/12/2016                 Test Class Check in
 *  Raja Sekahr Reddy M V                                10/12/2017                 Changed code for improving code coverage.
 *  Shivam Anand                140629                   10/18/2017                 Changed code for improving code coverage. 
 *  Sai Ayyagari                                         03/07/2018                 REQ - 351086 Can not use the search enrollment tab in CRM to pull up the platform data  
 *  Seema Kaulgi                                         03/15/2019                 REQ - 379048  SF - Ability for all CRM users to SEARCH for member specific Transaction Reply Reports (TRR) in CRM
 *  Bhumika S                                            10/14/2022                 User Story 3883795: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs-CBIS
 *  Nilesh Gadkar					                     10/21/2022	        	    User Story 3883798: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs_CIMTV
 *  Nilesh Gadkar					                     02/28/2023		            User Story 4259019: INC2174123 search enrollment screen just released 1/27 is not showing all of the products of a members 
 ****************************************************************************************************************************************/

@istest 
Private Class HUMMemSearchEnrollmentControllerV2Test
{
     /**
    * <p>
    * Test Setup Method 
    * @ Param     ApexPages.StandardController Standardcontroller reference
    * @ Return     NA
    */
     
     @testSetup 
    static void testSetup()
    {
        Endpoint_Url_HUM__c ssoLink= new Endpoint_Url_HUM__c(Name='SSO_LOGINURL' ,Remote_Url__c= 'https://ssolink.com');
        insert ssoLink;
		Endpoint_Url_HUM__c FastApp= new Endpoint_Url_HUM__c(Name='FAST_APP_URL' ,Remote_Url__c= 'https://ssolink.com');
        insert FastApp;
        Endpoint_Url_HUM__c DCNSpidarLink= new Endpoint_Url_HUM__c(Name='DCNSpider2URL' ,Remote_Url__c= 'https://ssolink.com');
        insert DCNSpidarLink;
	}
	
	
    /*
     * Method Name : testHUMFieldValidation
     * Description : This method Is used To test validation scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testHUMFieldValidation()
    {
        
        Test.setCurrentPage(Page.HUMCustomSearchPage);
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        //Set Current Page

        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched'); 
         
        system.runAs(oUser) 
        {
            Test.startTest();
            
            oSearchEnrl.mapMemInfo = null;
            oSearchEnrl.sDisplayResult = '';
            oSearchEnrl.bCBISFlag = true;
            oSearchEnrl.sExchangeId = '';
            oSearchEnrl.sSsNID = '';
            oSearchEnrl.bIsWarningExist = true;
                        
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.oSearchWrapper.sSSN = '262839197';
            oSearchEnrl.oSearchWrapper.sStartDate = '';
            oSearchEnrl.oSearchWrapper.sEndDate ='';
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()-90).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()-100).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);
            
            oSearchEnrl.bCBISFlag = false;
            oSearchEnrl.sServices = 'H1 Marketplace Search';
            oSearchEnrl.oSearchWrapper.sSSN = '262839197';
            oSearchEnrl.oSearchWrapper.sStartDate = '';
            oSearchEnrl.oSearchWrapper.sEndDate ='';
            oSearchEnrl.UnknownMemSearch();
            
            Test.stopTest();
        }
    }


    /*
     * Method Name : testHUMFieldValidationAppSearch
     * Description : This method Is used To test validation scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testHUMFieldValidationAppSearch()
    {
        
        Test.setCurrentPage(Page.HUMCustomSearchPage);
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        //Set Current Page

        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched'); 
         
        system.runAs(oUser) 
        {
            Test.startTest();
            oSearchEnrl.sServices = 'Application Search';
            oSearchEnrl.Service();
            
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : testHUMFieldValidationTRP
     * Description : This method Is used To test validation scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testHUMFieldValidationTRP()
    {
        
        Test.setCurrentPage(Page.HUMCustomSearchPage);
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        //Set Current Page

        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched'); 
         
        system.runAs(oUser) 
        {
            Test.startTest();
            
            oSearchEnrl.sServices = 'Transaction Reply Report (TRR)';
            oSearchEnrl.Service();
            
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : testHUMFieldValidationNoValue
     * Description : This method Is used To test validation scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testHUMFieldValidationNoValue()
    {
        
        Test.setCurrentPage(Page.HUMCustomSearchPage);
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        //Set Current Page

        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched'); 
         
        system.runAs(oUser) 
        {
            Test.startTest();
            
            oSearchEnrl.sServices = '';
            oSearchEnrl.Service();
            
            Test.stopTest();
        }
    }

    /*
     * Method Name : testHUMFieldValidationTRR
     * Description : This method Is used To test validation scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testHUMFieldValidationTRR()
    {
        
        Test.setCurrentPage(Page.HUMCustomSearchPage);
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        //Set Current Page

        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched'); 
         
        system.runAs(oUser) 
        {
            Test.startTest();
			oSearchEnrl.oSearchWrapper.sFirstName='Lin$%#ot';
            oSearchEnrl.oSearchWrapper.sLastName='Litt%$#e';
            oSearchEnrl.oSearchWrapper.sBirthdate='02/24%$/1987';
            oSearchEnrl.oSearchWrapper.sState='TX';
			oSearchEnrl.validationForTRR();
            Test.stopTest();
        }
    }

    /*
     * Method Name : testHUMEESServiceWOSSN
     * Description : This method Is used To test EES Legacy integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod Static void testHUMEESServiceWSSN()
    {
        HUM_CBIS_Enabled__c oCbis = New HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = False;
        insert oCbis;
        system.assertNotEquals(oCbis,null,'​Value not matched');
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        HUMEESLegacyMemberSearchHelper oEesLegacy = New HUMEESLegacyMemberSearchHelper();
        oSearchEnrl.sServices = System.Label.HUMEESService;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.oSearchWrapper.sSSN = '111585060';
        oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
        oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
        insert New HUMEESLegacySearchPlatforms__c(Platform_value__c='LM',Name='Platform1');
        insert New HUM_Webservice_Callout__c( Name = 'EESLegacySearchService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        String sResp = '<Return SessionID="5561184479" Version="2.23.0.0" Duration="1328" TimeStamp="05/15/2014 09:05:35:570" Environment="QA">';
        sResp += '<PlatformMembers><PlatformMember><FirstName>JOSEPH</FirstName> <LastName>ADAMOWICZ</LastName><PID>0338730300212608</PID>'; 
        sResp += '<Family><PlatformMember><FirstName>JOSEPH</FirstName> <SSN>111585060</SSN><MemberContracts>';
        sResp += '<MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate>';
        sResp += '<Group><GroupID>553121</GroupID><GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages>';
        sResp += '<PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><SrcPlatformCode>EM</SrcPlatformCode>';
        sResp += '<CategoryDesciption>MEDICAL</CategoryDesciption><MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate>'; 
        sResp += '<MemberCoverageEndDate>NONE</MemberCoverageEndDate> </PlatformMemberCoverage><PlatformMemberCoverage>';
        sResp += '<LineOfBusiness>IPB</LineOfBusiness><Category>DEN</Category><SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>DENTALAL</CategoryDesciption>';
        sResp += '<CoverageTier>FAM</CoverageTier><MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate></PlatformMemberCoverage>';
        sResp += '</Coverages></MemberContract></MemberContracts></PlatformMember></Family></PlatformMember></PlatformMembers></Return>';
        
        system.runAs(oUser) 
        {
           test.startTest();
          
           Dom.XMLNode rootNode;
           HUMEESLegacyMemberSearchHelper.xmlInputStringHierarchyParser(rootNode);
          
           Dom.Document xmlDoc = New Dom.Document();            
           xmlDoc.load(sResp);    
           Dom.XMLNode mpValue = xmlDoc.getRootElement();
           Map<String,map<String,String>> mapResultant = New Map<String,map<String,String>>(); 
           HUMEESLegacyMemberSearchHelper.collectHierarchy(mpValue,mapResultant);
            
           oSearchEnrl.searchEESService();
           oSearchEnrl.EESServiceProcessResponse();
           oEesLegacy.parseEEESResponse(sResp,oSearchEnrl.oSearchWrapper.sSSN);
           
           Map<string,String> oMemb = New Map<string,String>();
           oEesLegacy.mapMember = oMemb;
           oEesLegacy.mapMemInfo.isEmpty();
           oSearchEnrl.reset();
           test.stopTest();
        }
    }
    /*
     * Method Name : testHUMEESServiceDemographic
     * Description : This method Is used To test EES Legacy integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod Static void testHUMEESServiceDemographic()
    {
        HUM_CBIS_Enabled__c oCbis = New HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = False;
        insert oCbis;
        system.assertNotEquals(oCbis,null,'​Value not matched');
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        HUMEESLegacyMemberSearchHelper oEesLegacy = New HUMEESLegacyMemberSearchHelper();
        oSearchEnrl.sServices = System.Label.HUMEESService;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.oSearchWrapper.sSSN = '';
        oSearchEnrl.oSearchWrapper.sFirstName = 'John';
        oSearchEnrl.oSearchWrapper.sLastName = 'Corner';
        oSearchEnrl.oSearchWrapper.sBirthdate = '01/01/1971';
        oSearchEnrl.oSearchWrapper.sState = 'TX';
        insert New HUMEESLegacySearchPlatforms__c(Platform_value__c='LM',Name='Platform1');
        insert New HUM_Webservice_Callout__c( Name = 'EESLegacySearchService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
        insert New HUM_Webservice_Callout__c( Name = 'EESEnhancedService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000',Request_Type__c ='POST',Content_Type__c='application/json');           
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        String response;
        String sResp = '<Return SessionID="5561184479" Version="2.23.0.0" Duration="1328" TimeStamp="05/15/2014 09:05:35:570" Environment="QA">';
        sResp += '<PlatformMembers><PlatformMember><FirstName>JOSEPH</FirstName><MiddleInitial>U</MiddleInitial><LastName>ADAMOWICZ</LastName>';
        sResp += '<DateOfBirth>07/18/1962</DateOfBirth><Gender>M</Gender><SSN>947225537</SSN><Age>60</Age><Address><Line1>1234 GREEN Park</Line1>';
        sResp += '<Line2/><Line3/><City>OOSTBURG</City><County>10</County><State>WI</State></Address><PID>0338730300212608</PID>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><Prefix>MR</Prefix><Suffix/><MiddleName>U</MiddleName><MaritalStatus>M</MaritalStatus>';
        sResp += '<Smoker>N</Smoker><Language>E</Language><DeceasedDate>NONE</DeceasedDate><Family><PlatformMember><FirstName>JOSEPH</FirstName>';
        sResp += '<SSN>947225537</SSN><Address><Line1>1234 GREEN Park</Line1><City>OOSTBURG</City><County>10</County><State>WI</State>';
        sResp += '</Address><MemberContracts><MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate>';
        sResp += '<EndDate>NONE</EndDate><Group><GroupID>553121</GroupID><GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages>';
        sResp += '<PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category><SrcPlatformCode>EM</SrcPlatformCode>';
        sResp += '<CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier><MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate>';
        sResp += '<MemberCoverageEndDate>NONE</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate>';
        sResp += '<MemberEnrollmentEndDate>NONE</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode>';
        sResp += '<TermedReason/><Student>N</Student><Disabled>N</Disabled><MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode>';
        sResp += '<SubscriberIDDependentCode>04</SubscriberIDDependentCode> </PlatformMemberCoverage></Coverages></MemberContract><MemberContract>';
        sResp += '<ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate><Group><GroupID>553121</GroupID>';
        sResp += '<GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier>';
        sResp += '<MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate><MemberCoverageEndDate>12/31/2013</MemberCoverageEndDate>';
        sResp += '<MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate><MemberEnrollmentEndDate>12/31/2022</MemberEnrollmentEndDate><PrePostTax/>';
        sResp += '<CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode><TermedReason/><Student>N</Student><Disabled>N</Disabled>';
        sResp += '<MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode><SubscriberIDDependentCode>04</SubscriberIDDependentCode>'; 
        sResp += '</PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses><EMailAddress><Address>NOFLTBXZCD5@Tdmtest.com</Address>';
        sResp += '<Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber><Number>9201123456</Number><Extension>0</Extension>';
        sResp += '<Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember><PlatformMember><FirstName>JOSEPH1</FirstName>';
        sResp += '<SSN>947225111</SSN><Address><Line1>1234 GREEN Park</Line1><City>OOSTBURG</City><County>10</County><State>WI</State></Address><MemberContracts>';
        sResp += '<MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate><Group><GroupID>553121</GroupID>';
        sResp += '<GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages><PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><Category>MED</Category>';
        sResp += '<SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>MEDICAL</CategoryDesciption><CoverageTier>ESP</CoverageTier><MemberCoverageEffectiveDate>';
        sResp += '02/01/2013</MemberCoverageEffectiveDate><MemberCoverageEndDate>NONE</MemberCoverageEndDate><MemberEnrollmentEffectiveDate>05/01/2022</MemberEnrollmentEffectiveDate>';
        sResp += '<MemberEnrollmentEndDate>NONE</MemberEnrollmentEndDate><PrePostTax/><CoveragePeriod>Current</CoveragePeriod><RelationCode>SPO</RelationCode>';
        sResp += '<TermedReason/><Student>N</Student><Disabled>N</Disabled><MemberIDBase>503360037</MemberIDBase><MemberIDDependentCode>04</MemberIDDependentCode>';
        sResp += '<SubscriberIDDependentCode>04</SubscriberIDDependentCode> </PlatformMemberCoverage></Coverages></MemberContract></MemberContracts><EMailAddresses>';
        sResp += '<EMailAddress><Address>NOFLTBXZCD5@Tdmtest.com</Address><Type>HOME</Type><Primary>Y</Primary></EMailAddress></EMailAddresses><PhoneNumbers>';
        sResp += '<PhoneNumber><Number>9201123456</Number><Extension>0</Extension><Type>HOME</Type><Primary>Y</Primary></PhoneNumber></PhoneNumbers></PlatformMember>';
        sResp += '</Family><EMailAddresses><EMailAddress></EMailAddress></EMailAddresses><PhoneNumbers><PhoneNumber></PhoneNumber></PhoneNumbers></PlatformMember></PlatformMembers></Return>';

        system.runAs(oUser) 
        {
           test.startTest();
          
           oSearchEnrl.switchCIMTVSSN=true;
           oSearchEnrl.searchEESService();
           oSearchEnrl.searchEESService_SSN(system.label.HUMEESLegacyMemberSearchHelper_FirstName);
           oSearchEnrl.searchEESService_SSN('');
           oSearchEnrl.EESServiceProcessResponseNew();
           oSearchEnrl.EESServiceProcessSSNResponse();
           oSearchEnrl.EESServiceProcessResponse_Other();
           response = '{ "GetPlatformMembersResponse":{ "responseHeader":{ "status":"Success", "responseDuration":25, "message":{ "code":"PMSE0000", "description":"Successful" }, "environment":"INT", "context":{ "transactionID":"4e61f05e-59ac-4820-ba4d-9ee274d50311", "applicationKey":"638d3a31-732a-467b-aa57-1f7312b4ccb2", "applicationName":"CRM", "machine":"LOUWEBWTL64S02" } }, "platformMembers":[ { "middleName":"", "gender":"M", "age":"51", "pid":"0382721714431111", "ssn":"", "address":{ "line1":"4800 DUCK CREEK ROAD", "line2":"", "line3":"", "city":"ADKINS", "county":"029", "state":"TX", "zip":"78101", "zipPlus":"9741" }, "platform":"EM", "firstName":"JOHN", "lastName":"CORNER", "dob":"2/15/1971 12:00:00 AM" }, null ] } }';
           oSearchEnrl.ProcessResponse_OtherDemographic(response);
           response = '{ "Fault":{ "responseHeader":{ "status":"Success", "responseDuration":25, "message":{ "code":"PMSE0000", "description":"Successful" }, "environment":"INT", "context":{ "transactionID":"4e61f05e-59ac-4820-ba4d-9ee274d50311", "applicationKey":"638d3a31-732a-467b-aa57-1f7312b4ccb2", "applicationName":"CRM", "machine":"LOUWEBWTL64S02" } }, "platformMembers":[ { "middleName":"", "gender":"M", "age":"51", "pid":"0382721714431111", "ssn":"", "address":{ "line1":"4800 DUCK CREEK ROAD", "line2":"", "line3":"", "city":"ADKINS", "county":"029", "state":"TX", "zip":"78101", "zipPlus":"9741" }, "platform":"EM", "firstName":"JOHN", "lastName":"CORNER", "dob":"2/15/1971 12:00:00 AM" }, null ] } }';
           oSearchEnrl.ProcessResponse_OtherDemographic(response);
           

           oEesLegacy.parseEEESResponseNew(sResp,oSearchEnrl.oSearchWrapper.sSSN);
           oEesLegacy.parseEEESXMLResponse(sResp, oSearchEnrl.oSearchWrapper.sSSN);
           oSearchEnrl.reset();
           test.stopTest();
        }
    }

    /*
     * Method Name : testHUMUnknownAccount
     * Description : This method Is used To test unknown account scenario
     * Return type : NA
     * Paramater   : NA
     */        
    testMethod Static void testHUMUnknownAccount()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        // CCS profile user don't have access to HUMMemberSearchEnrollmentControllerV2 class. so runas CCS user leads QueryException
        
            Account oAccount = New Account();
            oAccount.Name = 'TestingAccount';
            insert oAccount; 
            system.assertNotEquals(oAccount,null,'​Value not matched');
            test.startTest();    
            List<SelectOption> lstGroupSelect = oSearchEnrl.ProviderStatevalue;
            List<Error_Log__c> lstErrorLogs = oSearchEnrl.lstLogErrors;
            oSearchEnrl.buildUnknownAccountMap(oAccount.Id); 

            system.assert(oSearchEnrl.lstLegacyService[0].iAccntId == oAccount.Id); 

            test.stopTest();
    }
    
    /*
     * Method Name : testHUMEESServiceWOSSN
     * Description : This method Is used To test validation scenario without passing SSN
     * Return type : NA
     * Paramater   : NA
     */        
    testMethod Static void testHUMEESServiceWOSSN()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {   
            HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
            oSearchEnrl.setSServices(System.Label.HUMEESService);
            oSearchEnrl.oSearchWrapper.sSSN = '';
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);
        }
    }

    /*
     * Method Name : testHUMH1ServiceWSSN
     * Description : This method Is used To test H1 Exchange integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod Static void testHUMH1ServiceWSSN()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        //Test.setMock(WebServiceMock.class, New HUMH1ExchangeServiceTest());
        insert New HUM_Webservice_Callout__c( Name = 'H1ExchangeService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        
        insert New HUM_Webservice_Callout__c( Name = 'OHBPService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {
             HUMConstants__c oHUMObj1 = New HUMConstants__c(Name ='Zero', StringValue__c = '0');
            insert oHUMObj1;
            System.assertNotEquals(oHUMObj1.Id, NULL);
            HUMConstants__c oHUMObj2 = New HUMConstants__c(Name ='Eleven', StringValue__c = '11');
            insert oHUMObj2;
            System.assertNotEquals(oHUMObj2.Id, NULL);
            
            HUMConstants__c oHUMObj3 = New HUMConstants__c(Name ='Nineteen', StringValue__c = '19');
            insert oHUMObj3;
            System.assertNotEquals(oHUMObj3.Id, NULL);
            HUMConstants__c oHUMObj6 = New HUMConstants__c(Name ='Thirteen', StringValue__c = '13');
            insert oHUMObj6;
            System.assertNotEquals(oHUMObj6.Id, NULL);
            
            HUMConstants__c oHUMObj5 = New HUMConstants__c(Name ='AM', StringValue__c = 'AM');
            insert oHUMObj5;
            System.assertNotEquals(oHUMObj5.Id, NULL);
            HUMConstants__c oHUMObj4 = New HUMConstants__c(Name ='PM', StringValue__c = 'PM');
            insert oHUMObj4;
            System.assertNotEquals(oHUMObj4.Id, NULL); 
            HUMConstants__c oHUMObj7 = New HUMConstants__c(Name ='Ten', StringValue__c = '10');
            insert oHUMObj7;
            System.assertNotEquals(oHUMObj7.Id, NULL);
            
            oSearchEnrl.setSServices(System.Label.HUMH1Service);
            oSearchEnrl.oSearchWrapper.sSSN = '123456789';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            test.startTest();
            
            oSearchEnrl.searchH1Service();
            
            HUMH1SearchResponseHelper.EnrollmentDetailResponse respElement = New HUMH1SearchResponseHelper.EnrollmentDetailResponse();
            HUMH1SearchResponseHelper.EnrollmentDetailList RespInfo = New HUMH1SearchResponseHelper.EnrollmentDetailList();
            List<HUMH1SearchResponseHelper.EnrollmentDetail> lstResp = New List<HUMH1SearchResponseHelper.EnrollmentDetail>();
            HUMH1SearchResponseHelper.EnrollmentDetail EnrApp = New HUMH1SearchResponseHelper.EnrollmentDetail();
               
           EnrApp.firstName = 'KEREL';
           EnrApp.lastName = 'NEILSEN';
           EnrApp.strDob = '1957-08-23';               
           EnrApp.groupId = '';
           EnrApp.covEffDt = '2014-01-01';
           EnrApp.covTrmDt = '9999-12-31';               
           EnrApp.applnId = '';
           EnrApp.contId = '';
           EnrApp.othSt = '';
           EnrApp.othZip = '';
           EnrApp.exchMbrID = '0000012333';
           EnrApp.strSsn = '123456789';               
           EnrApp.genderCd = 'M';
           EnrApp.othAddr1 = '';
           EnrApp.cntyName = '121';
           EnrApp.othCity = '';
           EnrApp.mailAddr1 = '2870 Peachtree Rd';
           EnrApp.mailCity = 'Atlanta';
           EnrApp.mailSt = 'GA';
           EnrApp.mailZip = '30305';
           EnrApp.homePhone = '';
           EnrApp.workPhone = '';
           EnrApp.pymtOptInd = '';
           EnrApp.csrEffDate = '9999-12-31';
           EnrApp.csrLevel = '0';
           EnrApp.csrEndDate = '9999-12-31';
           EnrApp.aptcAmount = '0.00';
           EnrApp.aptcEffDate = '2014-01-01';
           EnrApp.aptcEndDate = '9999-12-31';
           EnrApp.strBsn = '';
           EnrApp.mktRcptDate = '9999-12-31';
           EnrApp.signDATE = '9999-12-31';
           EnrApp.salesRepName = '';
           EnrApp.salesType = '';
           EnrApp.fileDt = '2013-10-02';
           EnrApp.procDt = '2013-10-02';
           EnrApp.creationTs = '2013-10-02-19.14.36.000008';
           EnrApp.barCodeBase = '';
           EnrApp.oecConfId = '';
           EnrApp.applnChnl = 'EDI';
                              
           lstResp.add(EnrApp);
           RespInfo.EnrollmentDetail = lstResp;
           respElement.EnrollmentDetailList = RespInfo;
           oSearchEnrl.oRespMesageH1 = respElement;
           oSearchEnrl.H1ServiceProcessResponse();
           oSearchEnrl.callH1Service();
           oSearchEnrl.H1PageExchangeSearch();
           test.stopTest();
        }
    }

    /*
     * Method Name : testHUMOHBPWSSN
     * Description : This method Is used To test OHBP integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod Static void testHUMOHBPWSSN()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();        
        insert New HUM_Webservice_Callout__c( Name = 'H1ExchangeService' , Certificate_Name__c = 'DummyCertificate', Active__c = false, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        insert New HUM_Webservice_Callout__c( Name = 'OHBPService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        system.runAs(oUser) 
        {
            oSearchEnrl.setSServices(System.Label.HUMH1Service);            
            oSearchEnrl.oSearchWrapper.sSSN = '327548591';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            test.startTest();
                     
            oSearchEnrl.oSearchWrapper.sSSN = '123456789';
            oSearchEnrl.searchOHBPService();
            oSearchEnrl.oREsponseOHBP = '<Return><Members><Member><FirstName>Sillily</FirstName><MiddleInitial></MiddleInitial><LastName>Callous</LastName><DateOfBirth>1982-09-01T00:00:00</DateOfBirth>'+
                                        '<Gender></Gender><SSN>778640461</SSN><Age></Age><TenantId>7</TenantId><Address><Line1>4248 CallousOne</Line1><Line2></Line2><City>MARBLE FALLS</City><County></County>'+
                                        '<State>TX</State><Zip>78654</Zip><ZipPlus></ZipPlus></Address><Properties><Property></Property></Properties><MemberGenKey></MemberGenKey><EmailAddress></EmailAddress>'+
                                        '<WKEmailAddress></WKEmailAddress><RetiredIndicator></RetiredIndicator><HomePhone></HomePhone><WorkPhone></WorkPhone><WorkPhoneExtension></WorkPhoneExtension>'+
                                        '<HumanaEmployeeIndicator></HumanaEmployeeIndicator><MemberBlockCode></MemberBlockCode><RetiredInd></RetiredInd><HumanaInc></HumanaInc><GenderDescription></GenderDescription>'+
                                        '<Family><Member><FirstName></FirstName><MiddleInitial></MiddleInitial><LastName></LastName><DateOfBirth></DateOfBirth><Gender></Gender><SSN></SSN><Age></Age>'+
                                        '<Address><Line1></Line1><Line2></Line2><City></City><County></County><State></State><Zip></Zip><ZipPlus></ZipPlus></Address><Properties><Property></Property></Properties><MemberGenKey></MemberGenKey>'+
                                        '<EmailAddress></EmailAddress><WKEmailAddress></WKEmailAddress><RetiredIndicator></RetiredIndicator><HomePhone></HomePhone><WorkPhone></WorkPhone><WorkPhoneExtension></WorkPhoneExtension>'+
                                        '<HumanaEmployeeIndicator></HumanaEmployeeIndicator><MemberBlockCode></MemberBlockCode><RetiredInd></RetiredInd><HumanaInc></HumanaInc><GenderDescription></GenderDescription><Coverages></Coverages></Member></Family>'+
                                        '<SourceInputs><MemberID></MemberID></SourceInputs><ResponseStatus>SUCCESS</ResponseStatus><PlanSelected><OHBPResponsePlans><PlanID>32673TX064000501</PlanID><PlanName>Humana Gold 2500/Austin HMOx</PlanName>'+
                                        '<ProposedEffectiveDateEM>2014-12-01T00:00:00</ProposedEffectiveDateEM></OHBPResponsePlans></PlanSelected><TransactionID>TX00778640461</TransactionID></Member></Members></Return>';
            oSearchEnrl.callOHBPService();
            oSearchEnrl.OHBPServiceProcessResponse();
            
            oSearchEnrl.lstLegacyService = New list<HUMMemberSearchEnrollmentHelper>();
            oSearchEnrl.OHBPServiceProcessResponse();
            

            test.stopTest();
        }
    }

    /*
     * Method Name : testHUMTrackEnrollmentServiceWSSN
     * Description : This method Is used To test track enrollment integration scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod Static void testHUMTrackEnrollmentServiceWSSN()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        insert New HUM_Webservice_Callout__c( Name = 'TrackEnrollmentService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        enrollmentTrackResponse.ResponseComplexType respComplex = New enrollmentTrackResponse.ResponseComplexType();
        enrollmentTrackResponse.TrackList TrackListResponse = New enrollmentTrackResponse.TrackList();
        List<enrollmentTrackResponse.Track> lstTrack = New List<enrollmentTrackResponse.Track>();
        enrollmentTrackResponse.Track respElement = New enrollmentTrackResponse.Track();
        respElement.Source_Track_Id = ' T1234';
        respElement.Recipient_Track_Id = ' Res T1234';
        respElement.Source_System_Cd = 'Source 1234';
        respElement.Source_Id_Type_Cd = ' Source Type 1234';
        respElement.Recipient_Id_Type_Cd  = 'Res Id 1234';
        respElement.Track_Date= null;
        respElement.Track_Time = null;
        respElement.Status_Cd = ' Test';
        respElement.Trans_Rec_Type_Cd  = 'Test';
        respElement.Last_Name = ' Patchy';
        respElement.First_Name = ' Panda';
        respElement.Birth_Date = null;
        respElement.Zip_Cd = ' 30324';
        respElement.Group_Id = ' T1234';
        respElement.Category_Id = ' T1234';
        respElement.Line_Of_Coverage = ' T1234';
        respElement.Coverage_Type_Cd  = ' T1234';
        respElement.Pend_Key = ' T1234';
        lstTrack.add(respElement);
        TrackListResponse.Track = lstTrack;
        respComplex.TrackList = TrackListResponse;
        
        system.runAs(oUser) 
        {
            oSearchEnrl.setSServices(System.Label.HUMTrackEnrollmentService);
            oSearchEnrl.oSearchWrapper.sSSN = '123456789';
            test.startTest();
            oSearchEnrl.searchTrackEnrollmentSearvice();
            oSearchEnrl.TrackEnrollmentSearviceProcessResponse();
            oSearchEnrl.processTrackResponseResult(respComplex);
            test.stopTest();
        }     
    }

    /*
     * Method Name : testHUMAEServiceWSSN
     * Description : This method Is used To test AE History integration scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testHUMAEServiceWSSN()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        insert New HUM_Webservice_Callout__c( Name = 'AEHistoryService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        
        HUMAEServicesHistoryResponse.ResponseComplexType respElement = New HUMAEServicesHistoryResponse.ResponseComplexType();
        HUMAEServicesHistoryResponse.PendHistoryList pendHistoryList = New HUMAEServicesHistoryResponse.PendHistoryList();
        HUMAEServicesHistoryResponse.PendHistory pendHistory = New HUMAEServicesHistoryResponse.PendHistory();
        pendHistory.SSN = '12345678';
        pendHistory.Pend_Key='121';
        pendHistory.First_Name = 'fName';
        pendHistory.Last_Name = 'lName';
        pendHistory.Birth_Date = Date.parse('11/30/1986');
        List<HUMAEServicesHistoryResponse.PendHistory> lstPendHistory = New List<HUMAEServicesHistoryResponse.PendHistory>();
        lstPendHistory.add(pendHistory);
        pendHistoryList.PendHistory = lstPendHistory;
        respElement.PendHistoryList = pendHistoryList;
        
        system.runAs(oUser) 
        {
            oSearchEnrl.oSearchWrapper.sSSN = '12345678';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.sServices = System.Label.HUMAEService;
            test.startTest();
            oSearchEnrl.searchAEService();
            oSearchEnrl.AEServiceProcessResponse();
            oSearchEnrl.processAEResponseResult(respElement);
            
            test.stopTest();
        }
    }

    /*
     * Method Name : testIntegrationExceptionScenarios
     * Description : This method Is used To test integration exception scenario
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testIntegrationExceptionScenarios()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');

        system.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();             
            test.startTest();   
            oSearchEnrl.sServices = System.Label.HUMEESService;    
            oSearchEnrl.UnknownMemSearch();
            
            system.assert(ApexPages.hasMessages()==true);
            // setup values for each unknown search as values get refresh for each search                
            oSearchEnrl.sServices = System.Label.HUMH1Service;    
            oSearchEnrl.oSearchWrapper.sSSN = '262839197';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch(); 

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.sServices = System.Label.HUMTrackEnrollmentService;    
            oSearchEnrl.oSearchWrapper.sSSN = '362839197';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
            oSearchEnrl.UnknownMemSearch(); 

            system.assert(ApexPages.hasMessages()==true);

            oSearchEnrl.sServices = System.Label.HUMAEService;  
            oSearchEnrl.oSearchWrapper.sSSN = '462839197';
            oSearchEnrl.oSearchWrapper.sStartDate = (Datetime.now()- 90).format('MM/dd/yyyy');
            oSearchEnrl.oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');   
            oSearchEnrl.UnknownMemSearch();

            system.assert(ApexPages.hasMessages()==true);

            test.stopTest();
        }
    }

/*
     * Method Name : testHUMCBISCode
     * Description : This method Is used To test CBIS Service
     * Return type : NA
     * Paramater : NA
     */ 
    testMethod Static void testHUMCBISSpecialtyCode()
    {       
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;  
        system.assertNotEquals(oUser,null,'​Value not matched');
                    
        HUM_CBIS_Enabled__c oCbis = New HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = True;
        insert oCbis;
        system.assertNotEquals(ocbis,null,'​Value not matched');
        insert New HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment_V2' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'TestingAccount';
        insert oAccount; 
        system.assertNotEquals(oAccount,null,'​Value not matched'); 
        
        Test.setMock(WebServiceMock.class, New HUMIntCBISServiceResponseTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        oSearchEnrl.sServices = System.Label.HUMcbisCode;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.Service();
        oSearchEnrl.bIsCbisSelected = True;
        
        System.runAs(oUser)
        {
            Test.startTest();
 
            oSearchEnrl.oSearchWrapper.sFirstName='Linot';
            oSearchEnrl.oSearchWrapper.sLastName='Litte';
            oSearchEnrl.oSearchWrapper.sBirthdate='02/24/1987';
            oSearchEnrl.oSearchWrapper.sState='TX';
            HUMSearchEnrollmentHelper searchEnrol = new HUMSearchEnrollmentHelper();
            searchEnrol.sFirstName='Linot';
            searchEnrol.sLastName='Litte';
            searchEnrol.sBirthdate='02/24/1987';
            searchEnrol.sState='TX';
            HttpResponse httpresponse = new HttpResponse();           
            String abcd = '{"GetPendHistoryTermsDetailsResponse":{"ResponseItems": {"ResponseItem": [{"Address": {"Address1": "3763 MAY STREET", "Address2":"", "City": "Joshua", "State": "TX", "Zip":"76058", "ZipPlus":"4820"},"DOB": "2/24/1988","DependentSSN": "0","FirstName": "LIENTJE","LastName": "BITONTI", "MemberId":"0", "MemberSSN":"477179580", "MiddleInit":"","NameSuff":"","PhoneNumber":"8170058444", "Platform":"CB", "Policy":{"BenId":"03","CoverageEndDate":"1/1/0001","CoverageStartDate":"2/1/2008","GroupId":"","PendDate":"1/1/0001","PendError":"","PendKey":"0"},"RelationType":"01","SexType":"M"},{"Address": {"Address1": "3763 June STREET", "Address2":"", "City": "Garlic", "State": "TX", "Zip":"76055", "ZipPlus":"4821"},"DOB": "2/24/1984","DependentSSN": "0","FirstName": "LIENTJE","LastName": "BITONTI", "MemberId":"0", "MemberSSN":"477179588", "MiddleInit":"J","NameSuff":"Mrs.","PhoneNumber":"8170058555", "Platform":"CB", "Policy":{"BenId":"04","CoverageEndDate":"31/1/2022","CoverageStartDate":"5/1/2010","GroupId":"","PendDate":"1/1/2002","PendError":"","PendKey":"0"},"RelationType":"02","SexType":"F"}]}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            HUMCBISSearchResponse_H_HUM oResponse = new HUMCBISSearchResponse_H_HUM();
            HUMCBISSearchResponse_H_HUM.GetPendHistoryTermsDetailsResponse getPendHistoryTermsDetailsResponse = new HUMCBISSearchResponse_H_HUM.GetPendHistoryTermsDetailsResponse();
            HUMCBISSearchResponse_H_HUM.ResponseItems responseItems = new HUMCBISSearchResponse_H_HUM.ResponseItems();
			HUMCBISSearchResponse_H_HUM.ResponseItem respItem = new HUMCBISSearchResponse_H_HUM.ResponseItem();
            //HUMCBISSearchResponse_H_HUM.OrganizationType organizationType = new HUMCBISSearchResponse_H_HUM.OrganizationType();
            List<HUMCBISSearchResponse_H_HUM.ResponseItem> responseItemList = new List<HUMCBISSearchResponse_H_HUM.ResponseItem>();
            HUMCBISSearchResponse_H_HUM.Address add = New HUMCBISSearchResponse_H_HUM.Address();
            add.Address1 = 'Address1';
            add.Address2 = 'Address2';
            add.City = 'City';
            add.State = 'State';
            add.Zip = '123';
            add.ZipPlus = '000';
           
            HUMCBISSearchResponse_H_HUM.Policy pol = New HUMCBISSearchResponse_H_HUM.Policy();
            pol.BenId = '123';
            pol.CoverageEndDate = String.valueof(System.today()+1);
            pol.CoverageStartDate = String.valueof(System.today() - 1);
            pol.GroupId = '12';
            pol.PendError = '';
           
            respItem.Address = add;
            respItem.DOB = '01/01/2000';
            respItem.DependentSSN = '000';
            respItem.FirstName = 'fName';
            respItem.LastName = 'lName';
            respItem.MemberSSN  = '123';
            respItem.MiddleInit = 'init' ;
            respItem.NameSuff = 'Mr';
            respItem.PhoneNumber = '9999999999';
            respItem.Platform = 'p';
            respItem.Policy = pol;
            respItem.RelationType = 'rType';
            respItem.SexType = 'sType';
            responseItemList.add(respItem);
            responseItems.ResponseItem = responseItemList;
            getPendHistoryTermsDetailsResponse.ResponseItems = responseItems;
            oResponse.GetPendHistoryTermsDetailsResponse = getPendHistoryTermsDetailsResponse;
            //oResponse.ResponseItems = responseItems; 
            oSearchEnrl.seachCBISCodeSpeciality();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponseCBISSpecialty'; 
            oSearchEnrl.processResponseCBISSpecialty();
            oSearchEnrl.setCBISMemberSpecialtyDetails(oResponse);
            HUMMemberSearchEnrollmentControllerV2.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }
    
    
    
    /*
     * Method Name : testHUMCBISCode
     * Description : This method Is used To test CBIS Service
     * Return type : NA
     * Paramater : NA
     */ 
    testMethod Static void testHUMCBISSpecialtyCodeException()
    {       
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;  
        system.assertNotEquals(oUser,null,'​Value not matched');
                    
        HUM_CBIS_Enabled__c oCbis = New HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = True;
        insert oCbis;
        system.assertNotEquals(ocbis,null,'​Value not matched');
        insert New HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment_V2' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'TestingAccount';
        insert oAccount; 
        system.assertNotEquals(oAccount,null,'​Value not matched'); 
        
        Test.setMock(WebServiceMock.class, New HUMIntCBISServiceResponseTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        oSearchEnrl.sServices = System.Label.HUMcbisCode;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.Service();
        oSearchEnrl.bIsCbisSelected = True;
        
        System.runAs(oUser)
        {
            Test.startTest();
 
            oSearchEnrl.oSearchWrapper.sFirstName='Linot';
            oSearchEnrl.oSearchWrapper.sLastName='Litte';
            oSearchEnrl.oSearchWrapper.sBirthdate='02/24/1987';
            oSearchEnrl.oSearchWrapper.sState='TX';
            HUMSearchEnrollmentHelper searchEnrol = new HUMSearchEnrollmentHelper();
            searchEnrol.sFirstName='Linot';
            searchEnrol.sLastName='Litte';
            searchEnrol.sBirthdate='02/24/1987';
            searchEnrol.sState='TX';
            HttpResponse httpresponse = new HttpResponse();           
            String abcd = '{"GetPendHistoryTermsDetailsResponse":{"ResponseItems": {"ResponseItem": [{"Address": {"Address1": "3763 MAY STREET", "Address2":"", "City": "Joshua", "State": "TX", "Zip":"76058", "ZipPlus":"4820"},"DOB": "2/24/1988","DependentSSN": "0","FirstName": "LIENTJE","LastName": "BITONTI", "MemberId":"0", "MemberSSN":"477179580", "MiddleInit":"","NameSuff":"","PhoneNumber":"8170058444", "Platform":"CB", "Policy":{"BenId":"03","CoverageEndDate":"1/1/0001","CoverageStartDate":"2/1/2008","GroupId":"","PendDate":"1/1/0001","PendError":"","PendKey":"0"},"RelationType":"01","SexType":"M"},{"Address": {"Address1": "3763 June STREET", "Address2":"", "City": "Garlic", "State": "TX", "Zip":"76055", "ZipPlus":"4821"},"DOB": "2/24/1984","DependentSSN": "0","FirstName": "LIENTJE","LastName": "BITONTI", "MemberId":"0", "MemberSSN":"477179588", "MiddleInit":"J","NameSuff":"Mrs.","PhoneNumber":"8170058555", "Platform":"CB", "Policy":{"BenId":"04","CoverageEndDate":"31/1/2022","CoverageStartDate":"5/1/2010","GroupId":"","PendDate":"1/1/2002","PendError":"","PendKey":"0"},"RelationType":"02","SexType":"F"}]}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(500);
            HUMMemberSearchEnrollmentControllerV2.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }
     /*
     * Method Name : testHUMCBISSpecialtyCodeSSN
     * Description : This method Is used To test CBIS Service
     * Return type : NA
     * Paramater : NA
     */ 
    testMethod Static void testHUMCBISSpecialtyCodeSSN()
    {       
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;  
        system.assertNotEquals(oUser,null,'​Value not matched');
                    
        HUM_CBIS_Enabled__c oCbis = New HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = True;
        insert oCbis;
        system.assertNotEquals(ocbis,null,'​Value not matched');
        insert New HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment_V2' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'TestingAccount';
        insert oAccount; 
        system.assertNotEquals(oAccount,null,'​Value not matched'); 
        
        Test.setMock(WebServiceMock.class, New HUMIntCBISServiceResponseTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        oSearchEnrl.sServices = System.Label.HUMcbisCode;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.Service();
        oSearchEnrl.bIsCbisSelected = True;
        
        System.runAs(oUser)
        {
            Test.startTest();
 
            oSearchEnrl.oSearchWrapper.sFirstName='';
            oSearchEnrl.oSearchWrapper.sLastName='';
            oSearchEnrl.oSearchWrapper.sBirthdate='';
            oSearchEnrl.oSearchWrapper.sState='';
            oSearchEnrl.oSearchWrapper.sSSN='110554467';
            HUMSearchEnrollmentHelper searchEnrol = new HUMSearchEnrollmentHelper();
            searchEnrol.sFirstName='';
            searchEnrol.sLastName='';
            searchEnrol.sBirthdate='';
            searchEnrol.sState='';
            searchEnrol.sSSN ='110554467';
            HttpResponse httpresponse = new HttpResponse();                      
            string abcd ='{ "GetPendHistoryTermsDetailsResponse":{ "ResponseItems":{ "ResponseItem":[ { "Address":{ "Address1":"4155 GARFIELD ROAD", "Address2":"", "City":"Provo", "State":"UT", "Zip":"84606", "ZipPlus":"2019" }, "DOB":"10/15/1964", "DependentSSN":"110554467", "FirstName":"MACORLAN", "LastName":"RUSINSKI", "MemberId":"19034294", "MemberSSN":"110554467", "MiddleInit":"F", "NameSuff":"", "PhoneNumber":"8017870818", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"11/1/2010", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"", "SexType":"M" }, { "Address":{ "Address1":"4155 GARFIELD ROAD", "Address2":"", "City":"Provo", "State":"UT", "Zip":"84606", "ZipPlus":"2019" }, "DOB":"5/14/1993", "DependentSSN":"646167123", "FirstName":"A", "LastName":"RUSINSKI", "MemberId":"0", "MemberSSN":"110554467", "MiddleInit":"L", "NameSuff":"", "PhoneNumber":"8017870818", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"11/1/2010", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"01", "SexType":"M" }, { "Address":{ "Address1":"4155 GARFIELD ROAD", "Address2":"", "City":"Provo", "State":"UT", "Zip":"84606", "ZipPlus":"2019" }, "DOB":"8/11/1999", "DependentSSN":"647482487", "FirstName":"JERYIA", "LastName":"RUSINSKI", "MemberId":"0", "MemberSSN":"110554467", "MiddleInit":"", "NameSuff":"", "PhoneNumber":"8017870818", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"11/1/2010", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"01", "SexType":"M" }, { "Address":{ "Address1":"4155 GARFIELD ROAD", "Address2":"", "City":"Provo", "State":"UT", "Zip":"84606", "ZipPlus":"2019" }, "DOB":"4/23/1991", "DependentSSN":"646260298", "FirstName":"LUCILLES", "LastName":"RUSINSKI", "MemberId":"0", "MemberSSN":"110554467", "MiddleInit":"", "NameSuff":"", "PhoneNumber":"8017870818", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"11/1/2010", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"01", "SexType":"F" }, { "Address":{ "Address1":"4155 GARFIELD ROAD", "Address2":"", "City":"Provo", "State":"UT", "Zip":"84606", "ZipPlus":"2019" }, "DOB":"9/10/1997", "DependentSSN":"646385803", "FirstName":"MACORLAN", "LastName":"RUSINSKI", "MemberId":"0", "MemberSSN":"110554467", "MiddleInit":"", "NameSuff":"", "PhoneNumber":"8017870818", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"11/1/2010", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"01", "SexType":"M" }, { "Address":{ "Address1":"4155 GARFIELD ROAD", "Address2":"", "City":"Provo", "State":"UT", "Zip":"84606", "ZipPlus":"2019" }, "DOB":"3/11/1966", "DependentSSN":"528531863", "FirstName":"MAHLEGNALYK", "LastName":"RUSINSKI", "MemberId":"0", "MemberSSN":"110554467", "MiddleInit":"", "NameSuff":"", "PhoneNumber":"8017870818", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"11/1/2010", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"03", "SexType":"F" } ] }, "Status":{ "Error":{ "ErrorCode":"0", "Message":"" }, "IsSuccesful":"true" } } }';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            HUMCBISSearchResponse_H_HUM oResponse = new HUMCBISSearchResponse_H_HUM();
            HUMCBISSearchResponse_H_HUM.GetPendHistoryTermsDetailsResponse getPendHistoryTermsDetailsResponse = new HUMCBISSearchResponse_H_HUM.GetPendHistoryTermsDetailsResponse();
            HUMCBISSearchResponse_H_HUM.ResponseItems responseItems = new HUMCBISSearchResponse_H_HUM.ResponseItems();
			HUMCBISSearchResponse_H_HUM.ResponseItem respItem = new HUMCBISSearchResponse_H_HUM.ResponseItem();
            //HUMCBISSearchResponse_H_HUM.OrganizationType organizationType = new HUMCBISSearchResponse_H_HUM.OrganizationType();
            List<HUMCBISSearchResponse_H_HUM.ResponseItem> responseItemList = new List<HUMCBISSearchResponse_H_HUM.ResponseItem>();
            HUMCBISSearchResponse_H_HUM.Address add = New HUMCBISSearchResponse_H_HUM.Address();
            add.Address1 = 'Address1';
            add.Address2 = 'Address2';
            add.City = 'City';
            add.State = 'State';
            add.Zip = '123';
            add.ZipPlus = '000';
           
            HUMCBISSearchResponse_H_HUM.Policy pol = New HUMCBISSearchResponse_H_HUM.Policy();
           pol.BenId = '123';
           pol.CoverageEndDate = String.valueof(System.today()+1);
           pol.CoverageStartDate = String.valueof(System.today() - 1);
           pol.GroupId = '12';
           pol.PendError = '';
            respItem.Address = add;
            respItem.DOB = '01/01/2000';
            respItem.DependentSSN = '000';
            respItem.FirstName = 'fName';
            respItem.LastName = 'lName';
            respItem.MemberSSN  = '123';
            respItem.MiddleInit = 'init' ;
            respItem.NameSuff = 'Mr';
            respItem.PhoneNumber = '9999999999';
            respItem.Platform = 'p';
            respItem.Policy = pol;
            respItem.RelationType = 'rType';
            respItem.SexType = 'sType';
            responseItemList.add(respItem);
            responseItems.ResponseItem = responseItemList;
            getPendHistoryTermsDetailsResponse.ResponseItems = responseItems;
            oResponse.GetPendHistoryTermsDetailsResponse = getPendHistoryTermsDetailsResponse;
            //oResponse.ResponseItems = responseItems; 
            oSearchEnrl.seachCBISCodeSpeciality();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponseCBISSpecialty'; 
            oSearchEnrl.processResponseCBISSpecialty();
            oSearchEnrl.CBISCodeProcessResponse();
            oSearchEnrl.setCBISMemberSpecialtyDetails(oResponse);
            HUMMemberSearchEnrollmentControllerV2.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : testHUMCBISCode
     * Description : This method Is used To test CBIS Service
     * Return type : NA
     * Paramater : NA
     */ 
    testMethod Static void testHUMCBISCode()
    {       
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;  
        system.assertNotEquals(oUser,null,'​Value not matched');
                    
        HUM_CBIS_Enabled__c oCbis = New HUM_CBIS_Enabled__c();
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = True;
        insert oCbis;
        system.assertNotEquals(ocbis,null,'​Value not matched');
        
        insert New HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'TestingAccount';
        insert oAccount; 
        system.assertNotEquals(oAccount,null,'​Value not matched'); 
        
        Test.setMock(WebServiceMock.class, New HUMIntCBISServiceResponseTest());
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();            
        oSearchEnrl.sServices = System.Label.HUMcbisCode;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.Service();
        oSearchEnrl.bIsCbisSelected = True;
        system.runAs(oUser) 
        {
            test.startTest();            
            
            oSearchEnrl.oSearchWrapper.sSSN = '';
            oSearchEnrl.oSearchWrapper.sBirthdate = '07/27/1980';
            oSearchEnrl.oSearchWrapper.sFirstName = 'test';
            oSearchEnrl.oSearchWrapper.sLastName = 'name';
            oSearchEnrl.oSearchWrapper.sState = 'FL'; 
            oSearchEnrl.UnknownMemSearch();                 
            oSearchEnrl.buildUnknownAccountMap(oAccount.Id); 
            oSearchEnrl.CBISCodeProcessResponse();
            test.stopTest();
        }
    }

    /*
     * Method Name : testHUMCBISCodeWrongInput
     * Description : This method Is used To test CBIS Service With wrong inputs
     * Return type : NA
     * Paramater : NA
     */ 
    testMethod Static void testHUMCBISCodeWrongInput()
    {
        HUM_CBIS_Enabled__c oCbis = New HUM_CBIS_Enabled__c();
        insert New HUM_Webservice_Callout__c( Name = 'HUMCbisSearchEnrollment' , Certificate_Name__c = 'DummyCertificate', Active__c = false, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        oCbis.Name = 'HUM_CBIS_Flag';
        oCbis.CBIS_Flag__c = True;
        insert oCbis;
        system.assertNotEquals(oCbis,null,'​Value not matched');
        
       
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
        oSearchEnrl.sServices = System.Label.HUMCBISCode;
        oSearchEnrl.setSServices(oSearchEnrl.getSServices());
        oSearchEnrl.getItems();
        oSearchEnrl.getThis();
        oSearchEnrl.Service();
        oSearchEnrl.oSearchWrapper.sSSN = '';
        oSearchEnrl.oSearchWrapper.sFirstName='';
        oSearchEnrl.bIsCbisSelected = true;
        Account oAccount = HUMTestDataHelperTest.getAccount();
        oAccount.Name = 'TestingAccountCBIS';
        insert oAccount;
        system.assertNotEquals(oAccount,null,'​Value not matched');
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser; 
        system.assertNotEquals(oUser,null,'​Value not matched');
                   
        system.runAs(oUser) 
        {
            oSearchEnrl.sServices = System.Label.HUMcbisCode;
            oSearchEnrl.oSearchWrapper.sFirstName='fname';
            oSearchEnrl.oSearchWrapper.sLastName='lname';
            oSearchEnrl.oSearchWrapper.sBirthdate='01/01/2000';
            oSearchEnrl.oSearchWrapper.sState='Georgia';
            test.startTest();
                     
            
        HUMCBISSearchServiceHelper.Address add = New HUMCBISSearchServiceHelper.Address();
        add.Address1 = 'Address1';
        add.Address2 = 'Address2';
        add.City = 'City';
        add.State = 'State';
        add.Zip = '123';
        add.ZipPlus = '000';
       
        HUMCBISSearchServiceHelper.Policy pol = New HUMCBISSearchServiceHelper.Policy();
        pol.BenId = '123';
        pol.CoverageEndDate = String.valueof(System.today()+1);
        pol.CoverageStartDate = String.valueof(System.today() - 1);
        pol.GroupId = '12';
        pol.PendError = '';
       
        List<HUMCBISSearchServiceHelper.ResponseItem> lstRespItem = New list<HUMCBISSearchServiceHelper.ResponseItem>();
        HUMCBISSearchServiceHelper.ResponseItem respItem = new HUMCBISSearchServiceHelper.ResponseItem();
        respItem.Address = add;
        respItem.DOB = '01/01/2000';
        respItem.DependentSSN = '000';
        respItem.FirstName = 'fName';
        respItem.LastName = 'lName';
        respItem.MemberSSN  = '123';
        respItem.MiddleInit = 'init' ;
        respItem.NameSuff = 'Mr';
        respItem.PhoneNumber = '9999999999';
        respItem.Platform = 'p';
        respItem.Policy = pol;
        respItem.RelationType = 'rType';
        respItem.SexType = 'sType';
        lstRespItem.add(respItem);
        
        HUMCBISSearchServiceHelper.ArrayOfResponseItem respItems = new HUMCBISSearchServiceHelper.ArrayOfResponseItem();
        respItems.ResponseItem = lstRespItem;
        
        HUMSearchEnrollmentHelper searchEnrol = new HUMSearchEnrollmentHelper();
        searchEnrol.sMemberId = '123456';
        searchEnrol.sSuffix = '00';
        searchEnrol.sGroupNumber = '123456';
        searchEnrol.sPhone = '9428252545';
        searchEnrol.sPostalCode = '500008';
        searchEnrol.sPersonEmail = 'test@test.com';
        searchEnrol.sGroupName = 'test';
        searchEnrol.sAgentId = '123456';
        searchEnrol.sAgentType = 'Den';
        searchEnrol.sAgencyName = 'test';
        searchEnrol.sTaxID  = '123456';
        searchEnrol.sNPI = '123456';
        searchEnrol.sSpeciality = 'Den';
        searchEnrol.sProviderName = 'test';
        searchEnrol.sFacilityName= 'test';
        searchEnrol.sAddress= 'test';
        searchEnrol.sOrgName = 'test';
        searchEnrol.sExtID ='1236';
        searchEnrol.sCity = 'test';
        searchEnrol.sGender = 'M';
        searchEnrol.sClassification = 'te';
        searchEnrol.sAddresstype  = 'g';
        searchEnrol.sTaxmonycode ='ter';
        searchEnrol.sDBA = 'test';
        searchEnrol.bIsUserHaveReadAccess = true;
        searchEnrol.sLocMemFound = 'test';
        searchEnrol.sMemName = 'test';
        searchEnrol.sPlatform ='LV';
        searchEnrol.sGroupId = '985558565';
        searchEnrol.sProduct = 'MED';
        searchEnrol.sLOB ='LOB';
        searchEnrol.sEffectiveDate = '01/02/2017';
        searchEnrol.sPend = 'test';
        searchEnrol.sPendKey = 'tester';
        searchEnrol.sDateTimePend = 'pend';
        searchEnrol.sDegree= '123123';
        searchEnrol.sEnterpriseID='1236';
        searchEnrol.sPhoneExtn = '01';
        searchEnrol.sServiceaddress = 'test';
        searchEnrol.sServiceState = 'State';
        searchEnrol.sServicezip = 'Zip';
        searchEnrol.sServiceCity ='City';
        
        HUMCBISSearchServiceHelper.Status status = new HUMCBISSearchServiceHelper.Status() ;
        status.Error = null;
        status.IsSuccesful = true;
        
        HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO responseDTO = new HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO();
        responseDTO.Status = status;
        responseDTO.ResponseItems = respItems;
            
            
            oSearchEnrl.oResponse = responseDTO;
            oSearchEnrl.seachCBISCode();
            oSearchEnrl.CBISCodeProcessResponse();
            oSearchEnrl.callCBISService();
            
            oSearchEnrl.oSearchWrapper.sFirstName='';
            oSearchEnrl.seachCBISCode();
            
            oSearchEnrl.buildUnknownAccountMap(oAccount.Id); 
            system.assert(ApexPages.hasMessages()==true);
            test.stopTest();
        }
    }
    
   /*
     * Method Name : testHUMListLogErrors
     * Description : This method is used to test List lstLogErrors getters setters
     * Return type : NA
     * Paramater : NA
     */ 
   
    testMethod static void testHUMListLogErrors()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched'); 
         
        system.runAs(oUser) 
        {
            Test.startTest();
            HUMEESLegacyMemberSearchHelper oLegacy = new HUMEESLegacyMemberSearchHelper();
            System.assert(oLegacy.lstLogErrors.size() == 0);
            
            List<Error_Log__c> lstLog = new List<Error_Log__c>();
            Error_Log__c error = new Error_Log__c(Status__c = 'EESLegacySearchServicee');
            lstLog.add(error);
            
            oLegacy.lstLogErrors = lstLog;
            System.assert(oLegacy.lstLogErrors.size() > 0);
           
            Test.stopTest();
        }
     }
     
        /*
     * Method Name : testHUMSubmit
     * Description : This method is used to test submit scenario
     * Return type : NA
     * Paramater   : NA
     */    
    testMethod static void testHUMSubmit()
    {
        HUMEESLegacyMemberSearchHelper oEesLegacy = new HUMEESLegacyMemberSearchHelper();
        insert new HUMEESLegacySearchPlatforms__c(Platform_value__c='LM',Name='Platform1');
        insert new HUM_Webservice_Callout__c( Name = 'EESLegacySearchService' , Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        system.runAs(oUser) 
        {
          test.startTest();
          
          Test.setMock(WebServiceMock.class, new HUMIntEESLegacySearchServiceTest());
          String sSSN = '111585060';
          oEesLegacy.submit(sSSN);
          test.stopTest();      
      }
  }
      
      
      /*
     * Method Name : testHUMSubmitException
     * Description : This method is used to test submit exception scenario
     * Return type : NA
     * Paramater   : NA
     */    

     testMethod static void testHUMSubmitException()
      {
       HUMEESLegacyMemberSearchHelper oEesLegacy = new HUMEESLegacyMemberSearchHelper();
       insert new HUMEESLegacySearchPlatforms__c(Platform_value__c='LM',Name='Platform1');
      insert new HUM_Webservice_Callout__c( Name = 'EESLegacySearchService' , Certificate_Name__c = '', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        system.runAs(oUser) 
         {
         test.startTest();
         String sSSN = '';
         oEesLegacy.submit(sSSN);
         test.stoptest();
         }
      }
      
       
      /*
     * Method Name : testHUMSubmitAsyncException
     * Description : This method is used to test submit exception scenario
     * Return type : NA
     * Paramater   : NA
     */  
     testMethod static void testHUMSubmitAsyncException()
      {
       HUMEESLegacyMemberSearchHelper oEesLegacy = new HUMEESLegacyMemberSearchHelper();
       insert new HUMEESLegacySearchPlatforms__c(Platform_value__c='LM',Name='Platform1');
      insert new HUM_Webservice_Callout__c( Name = 'EESLegacySearchService' , Certificate_Name__c = '', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'​Value not matched');
        system.runAs(oUser) 
         {
         test.startTest();
         Continuation oCon ; 
         String sSSN ='';
         oEesLegacy.submitAsync(oCon,sSSN);
         test.stoptest();
         }
      }
    
    
    /*
     * Method Name : testAMsearchExceptionScenarios
     * Description : This method Is used To test Error Messages in AM Search
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testAMsearchExceptionScenarios()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched');

        system.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();             
            test.startTest();   
            oSearchEnrl.sServices = HUMConstants.APPLICATION_SEARCH;    
            oSearchEnrl.AMSearchService();
            
            system.assert(ApexPages.hasMessages()==true);
            
            // setup values for each AM search as values get refresh for each search                
            oSearchEnrl.sServices = HUMConstants.APPLICATION_SEARCH;    
            oSearchEnrl.oSearchWrapper.sMedicareId = '123';
            oSearchEnrl.oSearchWrapper.sFirstName = 'ABCD';
            oSearchEnrl.oSearchWrapper.sLastName = '';
            oSearchEnrl.oSearchWrapper.sBirthdate = null;
            oSearchEnrl.oSearchWrapper.sApplicationId = '123';
            oSearchEnrl.oSearchWrapper.sOECConfirmationId = 'ABCD';
            oSearchEnrl.oSearchWrapper.sBarCode = 'test';
            oSearchEnrl.oSearchWrapper.sMedicaidId = '12';
			oSearchEnrl.AMSearchService();
			oSearchEnrl.oSearchWrapper.sFirstName = '!@##';
            oSearchEnrl.oSearchWrapper.sLastName = 'abcd';
            oSearchEnrl.oSearchWrapper.sBirthdate = '22/10/2020';
            oSearchEnrl.oSearchWrapper.sEffectiveDateFrom = '10/12/2018';
    		oSearchEnrl.oSearchWrapper.sEffectiveDateTo = '';
    		oSearchEnrl.oSearchWrapper.sReceivedDateFrom = '10/12/2018';
    		oSearchEnrl.oSearchWrapper.sReceivedDateTo = '05/12/2018';
    		oSearchEnrl.AMSearchService();
    		oSearchEnrl.oSearchWrapper.sFirstName = 'abcd';
            oSearchEnrl.oSearchWrapper.sLastName = '!@##';
            oSearchEnrl.oSearchWrapper.sBirthdate = '22/10/2020';
            oSearchEnrl.oSearchWrapper.sEffectiveDateFrom = '10/12/2018';
    		oSearchEnrl.oSearchWrapper.sEffectiveDateTo = '05/12/2018';
    		oSearchEnrl.oSearchWrapper.sReceivedDateFrom = '10/12/2018';
    		oSearchEnrl.oSearchWrapper.sReceivedDateTo = '';
    		oSearchEnrl.AMSearchService();
            
            system.assert(ApexPages.hasMessages()==true);

            test.stopTest();
        }
    }
    
    /*
     * Method Name : testAMsearchScenarios
     * Description : This method Is used To test in AM Search scenarios
     * Return type : NA
     * Paramater   : NA
     */  
     testMethod static void testAMsearchScenarios()
      {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched');

        system.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2(); 
            insert new HUM_Webservice_Callout__c( Name = 'Application_Search' ,Request_Type__c = 'POST',Content_Type__c = 'text/json' ,Certificate_Name__c = '', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');            
            test.startTest();   
            
            ApplicationSearch_Response_DTO_HUM responseDTOClass = new ApplicationSearch_Response_DTO_HUM();
            responseDTOClass.enrollmentResponse = new ApplicationSearch_Response_DTO_HUM.EnrollmentResponseWrapper();
            ApplicationSearch_Response_DTO_HUM.EnrollmentResponseWrapper eWrapper = new ApplicationSearch_Response_DTO_HUM.EnrollmentResponseWrapper();
            List<ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper> eDetailList = new List<ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper>();
            ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetail = new ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper();
            
            
            // setup values for each AM search as values get refresh for each search                
            oSearchEnrl.sServices = HUMConstants.APPLICATION_SEARCH;  
            oSearchEnrl.reset();
            oSearchEnrl.oSearchWrapper.sMedicareId = '1234567890';
            Continuation oCon = (Continuation)oSearchEnrl.AMSearchService();
            
            //setup response data
            HttpResponse response = new HttpResponse();
            response.setStatusCode(GLOBAL_CONSTANT_HUM.HTTP_OK_HUM);
            response.setBody('{"EnrollmentResponse": {"EnrollmentDetail": [{"SysAssnNumber": "010862414"}]}}');
            
            Test.setContinuationResponse(oSearchEnrl.amSearchContinuationId ,response);
            
            Object result = Test.invokeContinuationMethod(oSearchEnrl,oCon);
            
            
        
//Code Coverage for Detail Page
        ApplicationSearchDetail_C_HUM eDetailCon = new ApplicationSearchDetail_C_HUM();
            eDetailCon.enrollmentDetails = '{"sMedicareId":"123456789"}';
            eDetailCon.displayDetails();
            test.stopTest();
        }
      }
      
      /*
     * Method Name : testTRRsearchotherScenarios
     * Description : This method Is used To test Error Messages in AM Search
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testTRRsearchotherScenarios()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched');

        system.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();   
            insert new HUM_Webservice_Callout__c( Name = 'TRR_Search' ,Request_Type__c = 'POST',Content_Type__c = 'text/json' ,Certificate_Name__c = '', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');                      
            test.startTest();   
           
            
            // setup values for each TRR search as values get refresh for each search                
            oSearchEnrl.sServices = HUMConstants.HUMTRRCode; 
            oSearchEnrl.oSearchWrapper.sMedicareId = 'MED-1234567';
            oSearchEnrl.oSearchWrapper.sFirstName = 'SK';
            oSearchEnrl.oSearchWrapper.sLastName = 'KJ';
            oSearchEnrl.oSearchWrapper.sBirthdate = '05/03/2000';
            oSearchEnrl.oSearchWrapper.sHumanaReceivedDateFrom= '05/03/2019';
            oSearchEnrl.oSearchWrapper.sHumanaReceivedDateTo = '05/03/2019';
            oSearchEnrl.reset();
            oSearchEnrl.TRRSearchService();
            
            

            test.stopTest();
        }
    }
      
      /*
     * Method Name : testTRRsearchScenarios
     * Description : This method Is used To test in AM Search scenarios
     * Return type : NA
     * Paramater   : NA
     */  
     testMethod static void testTRRsearchScenarios()
      {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched');
       // Test.setMock(WebServiceMock.class, new TRRServiceRestResponse_DTO_HUM_Test());

        system.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMMemberSearchEnrollmentControllerV2 oSearchEnrl = New HUMMemberSearchEnrollmentControllerV2();
            TRRDetail_C_HUM odetailsTRR = new TRRDetail_C_HUM(); 
            insert new HUM_Webservice_Callout__c( Name = 'TRR_Search' ,Request_Type__c = 'POST',Content_Type__c = 'text/json' ,Certificate_Name__c = '', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');            
            test.startTest();   
            
            TRRServiceRestResponse_DTO_HUM objResponse = new TRRServiceRestResponse_DTO_HUM ();
            TRRServiceRestResponse_DTO_HUM.TRRResponse TRRResponse = new TRRServiceRestResponse_DTO_HUM.TRRResponse();
            TRRServiceRestResponse_DTO_HUM.TRRDetails TRRDetails = new TRRServiceRestResponse_DTO_HUM.TRRDetails();
            TRRDetails = TRRResponse.TRRDetails ;
            TRRServiceRestResponse_DTO_HUM.TRRDetail tDetail = new TRRServiceRestResponse_DTO_HUM.TRRDetail(); 
            List<TRRServiceRestResponse_DTO_HUM.TRRDetail> lstTRRDetail = new List<TRRServiceRestResponse_DTO_HUM.TRRDetail>();                      
            
            //Code Coverage for Detail Page
            odetailsTRR.TRRenrollmentDetails = '{"sMedicareId":"123456789"}';
            odetailsTRR.displayDetails();
            
            // setup values for each TRR search as values get refresh for each search                
            oSearchEnrl.sServices = HUMConstants.HUMTRRCode ;  
            oSearchEnrl.reset();
            oSearchEnrl.oSearchWrapper.sMedicareId = '1234567890';
            Continuation oCon = (Continuation)oSearchEnrl.TRRSearchService();
                        
            //setup response data
            HttpResponse response = new HttpResponse();
            response.setStatusCode(GLOBAL_CONSTANT_HUM.HTTP_OK_HUM);            
            response.setBody('{"TRRResponse": {"TRRDetails": {"TRRDetail": [{"MedicareId": "123456789"}]}}}');
            Test.setContinuationResponse(oSearchEnrl.TRRSearchContinuationId ,response);
           
            Object result = Test.invokeContinuationMethod(oSearchEnrl,oCon);
            
            system.assert(ApexPages.hasMessages()==false);
            
            
      
            test.stopTest();
            
            
        }
      }
      
      

 }