/****************************************************************************************
Apex Class Name  : HUMProcessUtilityHelperTest
Version          : 1.0 
Created Date     : AUGUST 14 2014
Function         : Test class to HUMProcessUtilityHelper Apex class
Modification Log :
 *  Developer                   Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 *  Sagar Tapadia                  17703            08/14/2014              Original Version  
 *  Vivek Batham                   17840            09/04/2014              Changes for creating cipher cloud custom setting
 *  Shruthi Karanth                17840            09/04/2014              Changes for creating cipher cloud custom setting
 *  Vivek Batham                   17987            09/04/2014              Changes for QMS Attachments
 *  Krishna Tadikonda              18953            10/21/2014              Changes for Case Comments QMS Code
 *  Vishal Verma                   19528            11/25/2014              Updated since Member Card external id has been made mandatory on Member Id object
 *  SuryaKumari Medicherla         19911            12/12/2014              Code modified to replace custom settings with custom object (REQ-72809)
 *  Krishna Tadikonda              19911            12/16/2014              Added assert statements for DML operations as part of review#19911.
 *  Vishal Verma                   19911            12/18/2014              Modified code as per Humana standards
 *  Pradeep Veeravali              20976            02/16/2015              Modified Mock service class for Person Service with new class written for new response   
 *  Pradeep Veeravali              21106            02/23/2015              Modified External Id with extact format of test data
 *  Vishal Verma                   29834            02/09/2016              Defect 210750.0001 : Added Interaction records in Test Setup.  
*   Vishal Verma                   30548            03/17/2016              Defect 216758.0001 : Passing the Comment entered from Change Owner Page to QMS
*   Shikhar Mehta                                   06/21/2016              REQ - 237000 : EIP fix , removed dpass calls for decryption.
*   ChiranjeeviRao Ravuri          30921            4/08/2016               REQ - 262439:Replace Attachment_URL__c field with Attachment_URL_Text__c field at line #126.
*   Suresh Gottipati                                06/18/2018              REQ - 366201 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Fiel
*   Santhi Mandava                                  04/15/2019              Replaced "Member_Id_Value__c" with Name
*   Mohammed Noor                                   08/04/2020              Changes done for US825176 for COB Template.
*   Mohammed Noor                                   08/14/2020              Increase code coverage for US825176 defect fixes.
* 	Mohammed Noor					  				09/09/2020				Changes done for User Story 1474107.
* 	Mohammed Noor                                   11/13/2020				Changes done for User Story 1654207.
*   Mohammed Noor									02/09/2021				Changes done for USER STORY 1700807.
* 	Mohammed Noor									03/15/2021			 	USER STORY 1700807 - changes to support On/off flag in the createMBEPlus Request method. 
 *******************************************************************************************/
@isTest
private class HUMProcessUtilityHelperTest   
{   /*
     * Method Name : initializeTestData
     * Description : Test method for initializing test data
     * Return type : void
     * Paramater   : NA
     */ 
    @testSetup static void initializeTestData()
    {  
        
        Group oGrp=new Group(Name ='Market_HO_Quality_Operations_Compliance', Type = 'QUEUE');
        insert oGrp;
        system.assertNotEquals(oGrp,null,'Value not matched');
        QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType = 'Work_Queue_Setup__c');
        insert oQueueObject ;   
        system.assertNotEquals(oQueueObject,null,'Value not matched');
        //Create User
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert oUser;
        System.assertEquals(oUser.Current_Queue__c, 'Market HO Quality Operations Compliance');
        System.runAs(oUser)
        {
            
             HUMTestDataHelperTest.getHUMConstantsData();
            //Cipher Cloud Custom Setting Data
            HUMCipherCloudSettings__c oCCS = new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',Value__c = 'CRMS TESTING');
            insert oCCS;
            System.assertEquals(oCCS.Value__c, 'CRMS TESTING');
            //Hum Constants Custom Setting Data
           HUMConstants__c oHC = new HUMConstants__c(Name='HUMLISTSIZEZERO', IntegerValue__c = 0); 
            insert oHC;
            System.assertEquals(oHC.IntegerValue__c, 0);
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.FirstName = 'Test QMS';
            oAcc.LastName ='Account Last'; 
            oAcc.Mbr_Gen_Key__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            oAcc.OwnerId = oUser.Id;
            oAcc.Enterprise_ID__c = '1234567891';
            oAcc.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert oAcc;
            system.assert(oAcc.Id != null);
            
            
            //Group Account
	        Account oAcc1 = HUMTestDataHelperTest.getAccount(); 
	        oAcc1.Name ='TestGroupAccount';                
	        oAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
	        oAcc1.Enterprise_ID__c = '1234567896';
	        oAcc1.Source_Customer_Key__c = '54321';        
	        insert oAcc1;
	        
            Product2 oProd = New Product2();
            oProd.Name = 'test';
            oProd.Product_Type__c = 'Med';
            oProd.Sold_Product_Key_Value__c = '23456789';
            insert oProd;
            system.assert(oProd.Id != null);
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'Test Policy';
            oPolicy.Group_Name__c = oAcc1.Id;
            oPolicy.Product__c = oProd.Id;
            oPolicy.Platform__c = 'LV';
            oPolicy.Source_Cust_Cov_Key__c = '543210ABC';            
            insert oPolicy;
            system.assert(oPolicy.Id != null);
            
            Policy_Plan_Details__c oPlan = new Policy_Plan_Details__c();
            oPlan.Policy__c = oPolicy.Id;
            oPlan.Product__c = oProd.Id;
            oPlan.Name = 'Test Policy Plan';
            oPlan.Coverage_Plan_Effective_Date__c='01/01/2020';
            oPlan.Coverage_Plan_End_Date__c='12/31/2020';
            oPlan.Source_Cust_Cov_Key__c = '543210ABC';
            oPlan.Policy_Plan_External_ID__c = '00|ODS|2800004665517|45|2020-01-01';
            insert oPlan;
            
            Policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
            oPolMem.Name = '00311111100';
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = oPolicy.Id;
            oPolMem.Effective_Date__c = '01/01/2020';
	        oPolMem.End_Date__c = '12/31/2020';
            oPolMem.Relationship__c = 'self';                       
            oPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert oPolMem;
            system.assert(oPolMem.Id != null);
            
            Member_ID__c oMemId = New Member_ID__c();
            oMemId.Policy__c = oPolicy.Id;
            oMemId.Policy_Member__c = oPolMem.Id;
            oMemId.Name = '123456789012';
            oMemId.Member_Card_External_ID__c = '1234';
            insert oMemId;
            system.assert(oMemId.Id != null);
            
            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.AccountId = oAcc.Id;            
            oCase.Interacting_With__c = oAcc.Id; 
            oCase.Policy_Member__c = oPolMem.Id;
            oCase.OwnerId = oUser.Id;
            insert oCase;
            system.assert(oCase.Id != null);
            
            Interaction__c oInt = HUMTestDataHelperTest.getInteraction();
            oInt.Interacting_With__c = oAcc.Id;
            oInt.Interacting_With_Type__c = 'Member';
            insert oInt;
            system.assert(oInt.Id != null);
            
            Case_Interaction__c oCaseInt = New Case_Interaction__c();
            oCaseInt.Interaction__c = oInt.Id;
            oCaseInt.Case__c = oCase.Id;
            insert oCaseInt;
            system.assert(oCaseInt.Id != null);
             
            CaseComment oCCom = HUMTestDataHelperTest.getCaseComm();
            oCCom.ParentId = oCase.Id;
            oCCom.CommentBody ='Test Comment';
            insert oCCom;
            system.assert(oCCom.Id != null);
            
            Attachment__c oAtt = HUMTestDataHelperTest.getCustomAttachment();
            oAtt.Related_To_Case__c = oCase.Id;
            oAtt.Attachment_URL_Text__c = 'www.gogle.com';
            insert oAtt;
            system.assert(oAtt.Id != null);
                        
            Work_Queue_Setup__c wqs = New Work_Queue_Setup__c(Name ='Market_HO_Quality_Operations_Compliance');
            wqs.Public_Group_Name__c = 'PG MKT HO Quality Operations Compliance';
            wqs.Service_Center__c = 'Market';
            wqs.Department__c = 'Home Office Quality Operations Compliance';
            wqs.General__c = False;
            wqs.HO_Medical__c = true;
            wqs.TenantID__c = '00';            
            insert wqs;
            
            system.assertNotEquals(wqs,null,'Value not matched');
                      
            HUM_WebService_Callout__c hWeb = New HUM_WebService_Callout__c();
            hWeb.Name = System.label.HUMAddressVerification;
            hWeb.Certificate_Name__c = 'test';
            hWeb.End_Point_URL__c = 'test.com';
            hWeb.Timeout__c = '123';
            insert hWeb;
        }
    
    }
    /*
     * Method Name : generateQMSRequestTest
     * Description : Test method For Generating QMS Request And Not callout
     * Return type : NA
     * Paramater   : NA
     */ 
    Static testmethod void generateQMSRequestTest() 
    {   
        User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');
        
        System.runAs(oUser)
        {
            Account oAcc = [Select FirstName, LastName, RecordTypeId, OwnerId, Birthdate__c From Account 
                                                Where FirstName = 'Test QMS' AND LastName = 'Account Last'];
            Case oCase = [Select AccountId, Interacting_With__c, Policy_Member__c, OwnerId From Case Where AccountId =: oAcc.Id];
            Test.startTest();
            HUMIntQMSService.QualityReferralCRMRequestDataContract qmsReq = New HUMIntQMSService.QualityReferralCRMRequestDataContract();
            qmsReq = HUMProcessUtilityHelper.generateQMSServiceRequest(oCase.Id,oUser.Current_Queue__c, 'Test');
            String cNum = [Select CaseNumber from Case Where Id =: oCase.Id].CaseNumber;
            system.assert(qmsReq != NULL);
            Test.stoptest();
        }
        
    }

    /*
     * Method Name : generatePersonServiceRespTest
     * Description : Test method For Generating Person Service Request Response
     * Return type : NA
     * Paramater   : NA
     */ 
    Static testMethod void generatePersonServiceRespTest()
    {
        User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');
        
        System.runAs(oUser)
        {
            List<Policy_Member__c> lstPolicyMember = New List<Policy_Member__c>();
            
            Account oAcc = [Select FirstName, LastName, RecordTypeId, OwnerId, Birthdate__c From Account 
                                                Where FirstName = 'Test QMS' AND LastName = 'Account Last'];
             lstPolicyMember = [Select Member__c, Policy__c, Relationship__c, Policy_Member_External_ID__c 
                                        From Policy_Member__c Where Member__c =: oAcc.Id ];
            

            Test.setMock(WebServiceMock.class, New HUMVerifyFamilyCOBServiceMockTest());
            Test.startTest();
            Map< String, HUMPersonServiceResponseHelper > mapResponse = HUMProcessUtilityHelper.generatePersonServiceResponse(lstPolicyMember,'123456789', '12345');
            Test.stopTest();
            system.assert(mapResponse != null);
        }
    }
    
	/*
     * Method Name : builtHttpRequestTest
     * Description : Test method for verifying the builtHttpRequest method.
     * Return type : NA
     * Paramater   : NA
     */
	Static testMethod void builtHttpRequestTest()
    {        
    	User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');
        HUM_Webservice_Callout__c MBEServiceDetails = new HUM_Webservice_Callout__c();
        MBEServiceDetails.Name = 'MBEPlusService';
        MBEServiceDetails.End_Point_URL__c = 'https://test.humana.com';
        MBEServiceDetails.Certificate_Name__c = 'TestCertificate';
        MBEServiceDetails.Request_Type__c = 'POST';
        MBEServiceDetails.Timeout__c = '12000';
        MBEServiceDetails.Content_Type__c = 'application/json';
        insert MBEServiceDetails;
        System.runAs(oUser)
        {
			Test.startTest();
            HttpRequest testReq = HUMProcessUtilityHelper.builtHttpRequest();            	           				
            system.assertNotEquals(testReq,null);
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : createMBEPlusRequestTest
     * Description : Test method for verifying the createMBEPlusRequest method.
     * Return type : NA
     * Paramater   : NA
     */
    Static testMethod void createMBEPlusRequestTest()
    {
    	User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');        
        System.runAs(oUser)
        {
			Test.startTest();
            MBEPlusOIRequest_DTO_HUM testReq = HUMProcessUtilityHelper.createMBEPlusRequest('',true);            	           				
            system.assertEquals(testReq,null);
            
            testReq = HUMProcessUtilityHelper.createMBEPlusRequest('1234567890',false);            	           				
            system.assertNotEquals(testReq,null);
            Test.stopTest();
        }	
    }
    
    /*
     * Method Name : parseMBERespForCOBDetailsTest
     * Description : Test method for verifying the parseMBERespForCOBDetails method.
     * Return type : NA
     * Paramater   : NA
     */
    Static testMethod void parseMBERespForCOBDetailsTest()
    {
    	User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');
        List<Policy_Member__c> lstPolicyMember = New List<Policy_Member__c>();            
        Account oAcc = [Select FirstName, LastName, RecordTypeId, OwnerId, Birthdate__c From Account 
						Where FirstName = 'Test QMS' AND LastName = 'Account Last'];	
			
		lstPolicyMember = [Select Id, Relationship__c, Name, Member__c, Member__r.Name, Member__r.FirstName , Member__r.LastName , Member__r.Enterprise_Id__c, Policy_Member_External_Id__c, Effective_Date__c, End_Date__c, Policy__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy_Platform__c, Group_Number__c, Policy__r.Source_Cust_Cov_Key__c 
						   From Policy_Member__c where Member__c =: oAcc.Id];
		
		list<Policy_Plan_Details__c> lstPolicyPlans = new list<Policy_Plan_Details__c>();
        lstPolicyPlans = [SELECT Id, Name, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c, Policy__c, Product__c, Product__r.Sold_Product_Key_Value__c, Policy_Group_Number__c, Source_Cust_Cov_Key__c FROM Policy_Plan_Details__c];
		        
        MBEPlusOIResponse_DTO_HUM oMBEResponse = new MBEPlusOIResponse_DTO_HUM();
        String sSampleResponse = '{ "GetMemberResponse":{ "Header":{ "ResponseHeader":{ "Environment":"Int", "Version":"2.0", "Server":{ "Name":"louigwii06s01" }, "Metrics":{ "Duration":"53", "RequestTimeStamp":"2020-08-17T11:10:38.674Z", "ResponseTimeStamp":"2020-08-17T11:10:38.727Z" }, ' +
								'"ResponseMetaData":{ "Status":"Warning", "ClientSessionID":"GUID", "SessionID":"7e734e30-654e-483e-8088-587d95ff0a34", "DataAuthorizationConstraintList":{ "DataAuthorizationConstraint":[ { "ConstraintType":"Exclude", "Tenant":"1", "Source":"5" }, ' +
								'{ "ConstraintType":"Include", "Tenant":"1" }, { "ConstraintType":"Include", "Tenant":"1", "Source":"16" } ] } } } },' +
								'"Body":{ "Member":{ "DateOfBirth":"", "FirstName":"TestMemberFN", "Gender":"M", "LastName":"TestMemberLN", "MiddleInitial":"", "Ssn":"", "Contact":{ "ContactProfile":{ "EmailList":{ "PersonEmail":[  ] }, "PhoneList":{ "PersonPhone":[  ] }, "AddressList":{ "PersonAddress":[  ] } } }, ' +
								'"Suffix":"", "MasterId":"1234567891", "SoldProductList":{ "SoldProduct":[ { "InsuranceProduct":{ "AlternateDescription":"", "Description":"", "MajorLineOfBusinessCode":"", "MajorLineOfBusinessDescription":"", "ProductStartDate":"01/01/2020", "ProductEndDate":"12/31/2020", ' +
								'"ProductId":"23456789", "Source":"LV", "ProductLineCode":"", "ProductLineDescription":"", "SegmentType":"", "SoldProductDetailList":{ "SoldProductDetail":[ { "InsuranceCoverage":{ "EndDate":"12/31/2020", "StartDate":"01/01/2020", "CoveredRoleList":{ "CoveredRole":[ { "StartDate":"01/01/2020", ' +
								'"EndDate":"12/31/2020", "MemberMasterId":"1234567891", "RelationshipToSubscriber":"EmployeeSelf", "CoveredRolePlatformPointerList":{ "PlatformProxyKey":[ { "Application":"CI", "KeyType":"MemberIDDependentCode", "KeyValue":"00", "Source":"LV" },{"Application": "CI","KeyType": "MemberIdBase","KeyValue": "003111111","Source": "EM"} ] }, "Hipaa":"", "CoverageKey":"" }]}, ' +
								'"PolicyType":"", "CoverageType":"", "GroupPlatformPointerList":{ "PlatformProxyKey":[ { "Application":"CI", "KeyType":"GroupId", "KeyValue":"543210", "Source":"LV" }]}, "ContractId":"", "OICoordinationOfBenefitList":{ "OICoordinationOfBenefit":[ { "CarrierName":"CARLLYN", "EffectiveDate":"06/01/2002", ' +
								'"EndDate":"12/31/9999", "Primary":"1", "LastUpdatedDate":"2019-11-04", "LastVerifiedDate":"2019-11-04", "StatusCode":"M", "StatusDescription":"OVER 65,MEDICARE PART A&B" } ] }, "CoordinationOfBenefitList":{ "CoordinationOfBenefit":[ { "CarrierName":"CARLLYN", "EffectiveDate":"06/01/2002", "EndDate":"12/31/9999", '+
								'"Primary":"1", "LastUpdatedDate":"2019-11-04", "LastVerifiedDate":"2019-11-04", "StatusCode":"M", "StatusDescription":"OVER 65,MEDICARE PART A&B" } ] }, "OperationalMajorLineofBusiness":"", "PolicySequenceNumber":"" } } ] }, ' +
								'"IsCarryOverCredit":"", "IsCustomerCarveOut":"", "IsCustomPlan":"", "IsFedQualified":"", "IsHdhp":"", "IsPca":"", "IsPcpRequired":"", "IsRxMedicalIntegratedDeductible":"", "IsRxSpeciality":"" } }] } }, ' +
								'"MessageList":{ "Message":[ { "Type":"Warning", "Code":"MBEW0003", "Source":"OSE Services", "Description":"No coverage found for one of the platforms." }] } } } }';
		oMBEResponse = (MBEPlusOIResponse_DTO_HUM)System.JSON.deserialize(sSampleResponse, MBEPlusOIResponse_DTO_HUM.class);
														
        System.runAs(oUser)
        {
			Test.startTest();
			
            if(lstPolicyMember!=null && !lstPolicyMember.isEmpty() && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
            {
            	list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstTest = HUMProcessUtilityHelper.parseMBERespForCOBDetails(oMBEResponse, lstPolicyMember[0], lstPolicyPlans);
            	list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstTestOne = HUMProcessUtilityHelper.parseMBERespForOICOBDetails(oMBEResponse, lstPolicyMember[0], lstPolicyPlans);
            	system.assertNotEquals(lstTest,null);
            	system.assertEquals(lstTest.isEmpty(),false);
            	HUMProcessUtilityHelper.prepareEmptyCOBDetails(lstPolicyMember[0]);
            }             
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : prepareCOBDetailsDTOTest
     * Description : Test method for verifying the prepareCOBDetailsDTO method.
     * Return type : NA
     * Paramater   : NA
     */
    Static testMethod void prepareCOBDetailsDTOTest()
    {
    	User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');
        List<Policy_Member__c> lstPolicyMember = New List<Policy_Member__c>();            
        Account oAcc = [Select FirstName, LastName, RecordTypeId, OwnerId, Birthdate__c From Account 
						Where FirstName = 'Test QMS' AND LastName = 'Account Last'];	
		lstPolicyMember = [Select Id, Relationship__c, Name, Member__c, Member__r.Name, Member__r.FirstName , Member__r.LastName , Member__r.Enterprise_Id__c, Policy_Member_External_Id__c, Effective_Date__c, End_Date__c, 
                           Policy__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Coverage_Plan_Effective_Date__c ,Policy__r.Coverage_Plan_end_date__c, Policy__r.Product__r.Sold_Product_Key_Value__c
						   From Policy_Member__c Where Member__c =: oAcc.Id ];
		list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBDetails = new list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit>();
		MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit oCOBDetails = new MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit();
		oCOBDetails.CarrierName = 'TEST Carrier';
        oCOBDetails.EffectiveDate = '01/01/2020'; 
		oCOBDetails.EndDate = '12/31/2020';
        oCOBDetails.Primary = 'Y';
        oCOBDetails.OILastVerifiedDate = '2020-07-10'; 
        oCOBDetails.LastUpdatedDate = '06/30/2020';
        lstCOBDetails.add(oCOBDetails);
        
        list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstOICOBDetails = new list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();
		MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oOICOBDetails = new MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit();
		oOICOBDetails.CarrierName = 'TEST Carrier';
        oOICOBDetails.EffectiveDate = '01/01/2020'; 
		oOICOBDetails.EndDate = '12/31/2020';
        oOICOBDetails.Primary = 'Y';
        oOICOBDetails.OILastVerifiedDate = '2020-07-10'; 
        oOICOBDetails.LastUpdatedDate = '06/30/2020';
        lstOICOBDetails.add(oOICOBDetails);
        													
        System.runAs(oUser)
        {
			Test.startTest();			
            if(lstPolicyMember!=null && !lstPolicyMember.isEmpty())
            {
            	list<HUMPersonServiceResponseHelper> lstTest = HUMProcessUtilityHelper.prepareCOBDetailsDTO(lstPolicyMember[0], lstCOBDetails);
            	list<HUMPersonServiceResponseHelper> lstTestOne = HUMProcessUtilityHelper.prepareOICOBDetailsDTO(lstPolicyMember[0], lstOICOBDetails);
            	system.assertEquals(false,lstTest.isEmpty());
            }             
            Test.stopTest();
        }
    }  

	/*
     * Method Name : formatDateMethodTest
     * Description : Test method for verifying the formatDateMethodTest method.
     * Return type : NA
     * Paramater   : NA
     */
    Static testMethod void formatDateMethodTest()
    {
    	User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');        
        System.runAs(oUser)
        {
			Test.startTest();
            String sResult = HUMProcessUtilityHelper.formatDateMethod('2000-07-10');            	           				
            system.assertEquals(sResult, '07/10/2000');                    
            Test.stopTest();
        }	
    }    
    
    /*
     * Method Name : checkProductIDFromCRMTest
     * Description : Test method for verifying the checkProductIDFromCRM method.
     * Return type : NA
     * Paramater   : NA
     */
    Static testMethod void checkProductIDFromCRMTest()
    {
    	User oUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(oUser.LastName,'test User1');
        list<Policy_Plan_Details__c> lstPolicyPlans = new list<Policy_Plan_Details__c>();
        lstPolicyPlans = [SELECT Id, Name, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c, Policy__c, Product__c, Product__r.Sold_Product_Key_Value__c, Policy_Group_Number__c, Source_Cust_Cov_Key__c FROM Policy_Plan_Details__c];        
        System.runAs(oUser)
        {
			Test.startTest();
            Boolean bResult = HUMProcessUtilityHelper.checkProductIDFromCRM('23456789', lstPolicyPlans);
            system.assertEquals(bResult, true);                    
            Test.stopTest();
        }	
    }    
}