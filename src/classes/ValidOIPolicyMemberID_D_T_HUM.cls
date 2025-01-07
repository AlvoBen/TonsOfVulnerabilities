/**************************************************************************************************************************************************
    @Apex class Name    :  ValidOIPolicyMemberID_D_T_HUM
    @Function           :  This class serves as test class for ValidOIPolicyMemberID_D_HUM.
    @Version            :  1.0
    @reference          :  ValidOIPolicyMemberID_D_HUM

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Keerti Durbha                             06/05/2020                   Original Version 
* Raja Sekhar Reddy M V                     08/20/2020	                PR00095673_US_1276005_DualstatusIndicator
* Ajay Chakradhar                           10/05/2020                   US1467120 Test Coverage
* Ajay Chakradhar                           02/15/2021	                US 1707499 - PR00093155 - MF 21 - Release 03/19/21
************************************************************************************************************************************************* */
@istest
public class ValidOIPolicyMemberID_D_T_HUM {
    
    /*
     * Method Name : initializeTestData
     * Description : Test method for initializing test data
     * Return type : void
     * Paramater   : NA
     */ 
    @testSetup static void initializeTestData()
    {  
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert oUser;
        System.assertEquals(oUser.Current_Queue__c, 'Market HO Quality Operations Compliance');
        System.runAs(oUser)
        {
            HUMTestDataHelperTest.getHUMConstantsData();
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
            insert oPolicy;
            system.assert(oPolicy.Id != null);
            
            Policy_Plan_Details__c oPlan = new Policy_Plan_Details__c();
            oPlan.Policy__c = oPolicy.Id;
            oPlan.Product__c = oProd.Id;
            oPlan.Name = 'Test Policy Plan';
            oPlan.Coverage_Plan_Effective_Date__c='01/01/2020';
            oPlan.Coverage_Plan_End_Date__c='12/31/2020';
            oPlan.Policy_Plan_External_ID__c = '00|ODS|2800004665517|45|2020-01-01';
            insert oPlan;
            
            Policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
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
                     
            HUM_WebService_Callout__c objAddressStandardize = New HUM_WebService_Callout__c();
            objAddressStandardize.Name = 'MBEPlusService';
            objAddressStandardize.Active__c = True;
            objAddressStandardize.Enable_Service_Tester__c = True;
            objAddressStandardize.Certificate_Name__c = 'test';
            objAddressStandardize.Content_Type__c = 'application/json';
            objAddressStandardize.Request_Type__c = 'POST';
            objAddressStandardize.Timeout__c = '12000';
            objAddressStandardize.End_Point_URL__c = 'https://test-test.com';
            insert objAddressStandardize;
        }
    }
    
    /*
    * Method to test for success other insurance Response
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessOIResponse()
    {
        User testUser = [SELECT Id, Current_Queue__c, LastName from User WHERE Lastname = 'test User1' LIMIT 1];
        system.assertEquals(testUser.LastName,'test User1');
        system.runAs(testUser)
        { 
            Account oAcc = [Select FirstName, LastName, RecordTypeId, OwnerId, Birthdate__c From Account 
                                                Where FirstName = 'Test QMS' AND LastName = 'Account Last'];
            Policy_Member__c oPolicyMember = [Select id, Member__c, Policy__c, Relationship__c, Policy_Member_External_ID__c 
                                        From Policy_Member__c Where Member__c =: oAcc.Id ];
            Test.StartTest();
            HttpResponse response = new HttpResponse();
        	String sSampleResponse = '{ "GetMemberResponse":{ "Header":{ "ResponseHeader":{ "Environment":"Int", "Version":"2.0", "Server":{ "Name":"louigwii06s01" }, "Metrics":{ "Duration":"53", "RequestTimeStamp":"2020-08-17T11:10:38.674Z", "ResponseTimeStamp":"2020-08-17T11:10:38.727Z" }, ' +
								'"ResponseMetaData":{ "Status":"Warning", "ClientSessionID":"GUID", "SessionID":"7e734e30-654e-483e-8088-587d95ff0a34", "DataAuthorizationConstraintList":{ "DataAuthorizationConstraint":[ { "ConstraintType":"Exclude", "Tenant":"1", "Source":"5" }, ' +
								'{ "ConstraintType":"Include", "Tenant":"1" }, { "ConstraintType":"Include", "Tenant":"1", "Source":"16" } ] } } } },' +
								'"Body":{ "Member":{ "DateOfBirth":"", "FirstName":"TestMemberFN", "Gender":"M", "LastName":"TestMemberLN", "MiddleInitial":"", "Ssn":"", "Contact":{ "ContactProfile":{ "EmailList":{ "PersonEmail":[  ] }, "PhoneList":{ "PersonPhone":[  ] }, "AddressList":{ "PersonAddress":[  ] } } }, ' +
								'"Suffix":"", "MasterId":"1234567891", "SoldProductList":{ "SoldProduct":[ { "InsuranceProduct":{ "AlternateDescription":"", "Description":"", "MajorLineOfBusinessCode":"", "MajorLineOfBusinessDescription":"", "ProductStartDate":"01/01/2020", "ProductEndDate":"12/31/2020", ' +
								'"ProductId":"23456789", "Source":"LV", "ProductLineCode":"", "ProductLineDescription":"", "SegmentType":"", "SoldProductDetailList":{ "SoldProductDetail":[ { "InsuranceCoverage":{ "EndDate":"12/31/2020", "StartDate":"01/01/2020", "CoveredRoleList":{ "CoveredRole":[ { "StartDate":"01/01/2020", ' +
								'"EndDate":"12/31/2020", "MemberMasterId":"1234567891", "RelationshipToSubscriber":"EmployeeSelf", "CoveredRolePlatformPointerList":{ "PlatformProxyKey":[ { "Application":"CI", "KeyType":"MemberIDDependentCode", "KeyValue":"00", "Source":"LV" } ] }, "Hipaa":"", "CoverageKey":"" }]}, ' +
								'"PolicyType":"", "CoverageType":"", "GroupPlatformPointerList":{ "PlatformProxyKey":[ { "Application":"CI", "KeyType":"GroupId", "KeyValue":"54321", "Source":"LV" }]}, "ContractId":"", "CoordinationOfBenefitList":{ "CoordinationOfBenefit":[ { "CarrierName":"CARLLYN", "EffectiveDate":"06/01/2002", ' +
								'"EndDate":"12/31/9999", "Primary":"1", "LastUpdatedDate":"2019-11-04", "LastVerifiedDate":"2019-11-04", "StatusCode":"M", "StatusDescription":"OVER 65,MEDICARE PART A&B" } ] }, "OperationalMajorLineofBusiness":"", "PolicySequenceNumber":"" } } ] }, ' +
								'"IsCarryOverCredit":"", "IsCustomerCarveOut":"", "IsCustomPlan":"", "IsFedQualified":"", "IsHdhp":"", "IsPca":"", "IsPcpRequired":"", "IsRxMedicalIntegratedDeductible":"", "IsRxSpeciality":"" } }] } }, ' +
								'"MessageList":{ "Message":[ { "Type":"Warning", "Code":"MBEW0003", "Source":"OSE Services", "Description":"No coverage found for one of the platforms." }] } } } }';
            response.setBody(sSampleResponse);
            response.setStatusCode(200);
            String sRecordId = oAcc.Id, sPolicyMemberId = oPolicyMember.Id;
            Continuation conti = (Continuation)ValidOIPolicyMemberID_D_HUM.getOtherInsuranceData(sRecordId, sPolicyMemberId);
            
            Test.setContinuationResponse(ValidOIPolicyMemberID_D_HUM.continuationLabel, response); 
	 		List<String> labels = new List<String>{ValidOIPolicyMemberID_D_HUM.continuationLabel}; 
	 		ValidOIPolicyMemberID_D_HUM.ProcessMBEOtherInsuranceResponse(labels, oPolicyMember); 
	 
            MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit oCOB = new MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit();
            oCOB.CarrierName = 'MEDICARE PART XX';
            oCOB.EffectiveDate='12/31/2018';
            oCOB.EndDate='01/01/2007';
            oCOB.LastUpdatedDate='null';
            oCOB.OILastVerifiedDate='08/31/2020';
            oCOB.Primary='0';
            
            list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBFromService = new list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit>();
            lstCOBFromService.add(oCOB);
            ValidOIPolicyMemberID_D_HUM.processCOBData(lstCOBFromService);
            
            MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oiCOB = new MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit();
            oiCOB.CarrierName = 'MEDICARE PART XX';
            oiCOB.EffectiveDate='12/31/2018';
            oiCOB.EndDate='01/01/2007';
            oiCOB.LastUpdatedDate='null';
            oiCOB.OILastVerifiedDate='08/31/2020';
            oiCOB.Primary='0';
            
            list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstOICOBFromService = new list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();
            lstOICOBFromService.add(oiCOB);
            ValidOIPolicyMemberID_D_HUM.processOICOBData(lstOICOBFromService);
            System.assertNotEquals(null, sRecordId);    
            Test.StopTest();
        }
    }
	                         
     Private Static Testmethod void getDualEligibleInfo()
    {
       User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Policy_Member__c Pmember=[Select id,Member__c from Policy_Member__c LIMIT 1];
        Account Acc=[Select id, Enterprise_Id__c from Account where  Enterprise_Id__c='1234567891' AND id=:pmember.Member__c Limit 1];
        
         Account objMemAccount = New  Account();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = '4000004536728';
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
        
        system.runas(testUser)
        {
            test.startTest();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            
            String response ='{"GetMemberResponse":{"Body":{"Member":{"DualStatusList":[{"EffectiveDate":"2020-03-01","EndDate":"2020-03-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-12-01","EndDate":"2019-12-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-06-01","EndDate":"2019-06-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-01-01","EndDate":"2020-01-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-07-01","EndDate":"2020-07-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-07-01","EndDate":"2019-07-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-04-01","EndDate":"2020-04-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-06-01","EndDate":"2020-06-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-11-01","EndDate":"2019-11-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-10-01","EndDate":"2019-10-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-08-01","EndDate":"2019-08-31","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2019-09-01","EndDate":"2019-09-30","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-02-01","EndDate":"2020-02-29","Value":"QI","Code":"06","Indicator":false},{"EffectiveDate":"2020-05-01","EndDate":"2020-05-31","Value":"QI","Code":"06","Indicator":false}]},"MessageList":{"Message":[{"Type":"Information","Code":"OSI001","Source":"OSEServices","Description":"MemberDetailsreturnedsuccessfully."}]}}}}';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse !=null);
            MBEPlusOIResponse_DTO_HUM objresp = new MBEPlusOIResponse_DTO_HUM();
            objresp = (MBEPlusOIResponse_DTO_HUM)System.JSON.deserialize(oResponse.getbody(),MBEPlusOIResponse_DTO_HUM.class);
            Boolean sDual = false;
            sDual = DualEligibleDetailsHelper_H_HUM.processDualResponse(objresp);
            List<ValidOIWrapper_DTO_HUM> lstVoi = new List<ValidOIWrapper_DTO_HUM>();
            PolicyMemberMBEResWrapper_DTO_HUM oPM = new PolicyMemberMBEResWrapper_DTO_HUM();
            oPm.isMemDualEligibleInLastTwelveMonth  = sDual;
            oPm.validOIWrapperList = lstVoi;
            Test.stopTest();
        }
    }

    /*
    * Method to test for failure Other Insurance Response
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureOIResponse()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Account oAcc = [Select FirstName, LastName From Account Where FirstName = 'Test QMS' AND LastName = 'Account Last'];
            Policy_Member__c oPol = [Select id, Member__c  From Policy_Member__c Where Member__c =: oAcc.Id ];
            
            Test.StartTest();
            HttpResponse response1 = new HttpResponse();
            response1.setbody('{"GetMemberResponse":{"Header":{"ResponseHeader":{"Environment":"Int","Version":"2.0","Server":{"Name":"si6s02"},"Metrics":{"Duration":"75","RequestTimeStamp":"2020-09-09T13:50:12.789Z","ResponseTimeStamp":"2020-09-09T13:50:12.864Z"},"ResponseMetaData":{"Status":"Warning","ClientSessionID":"GUID","SessionID":"df3f4e69-9972-4ffe-9111-6e2c8fcbb1b7","DataAuthorizationConstraintList":{"DataAuthorizationConstraint":[{"ConstraintType":"Exclude","Tenant":"1","Source":"5"},{"ConstraintType":"Include","Tenant":"1"},{"ConstraintType":"Include","Tenant":"1","Source":"16"}]}}}},"Body":{"Member":{"DateOfBirth":"10/18/1941","FirstName":"SHAORU","Gender":"F","LastName":"THEIM","MiddleInitial":"D","Ssn":"340531412","Contact":{"ContactProfile":{"EmailList":{"PersonEmail":[]},"PhoneList":{"PersonPhone":[]},"AddressList":{"PersonAddress":[]}}},"MasterId":"1005018817","SoldProductList":{"SoldProduct":[{"InsuranceProduct":{"AlternateDescription":"MER RISK","Description":"076","MajorLineOfBusinessCode":"MER","MajorLineOfBusinessDescription":"MEDICARE HMO","ProductStartDate":"01/01/2020","ProductEndDate":"12/31/2020","ProductId":"8832940101","Source":"LV","ProductLineCode":"MED","ProductLineDescription":"MEDICAL PRODUCTS","ProductTypeCode":"MAPD","SegmentType":"MCR","SoldProductDetailList":{"SoldProductDetail":[{"InsuranceCoverage":{"EndDate":"12/31/9999","StartDate":"01/01/2019","PolicyType":"Individual","CoverageType":"Single","ContractId":"756232103","OperationalMajorLineofBusiness":"MER","PolicySequenceNumber":"0","HcfaEnrollmentSourceCode":"P","CoordinationOfBenefitList":{"CoordinationOfBenefit":[{"CarrierName":"HUMANA GOLD PLUS (Duplicate)","EffectiveDate":"01/01/2019","EndDate":"08/30/2019","Primary":"0","OILastVerifiedDate":"2020-01-31","StatusCode":"INTM"}]}}}]},"IsCarryOverCredit":"false","IsCustomerCarveOut":"false","IsCustomPlan":"false","IsFedQualified":"false","IsHdhp":"false","IsPca":"false","IsPcpRequired":"true","IsRxMedicalIntegratedDeductible":"false","IsRxSpeciality":"false"}},{"InsuranceProduct":{"AlternateDescription":"XAVIER","Description":"Xavier","MajorLineOfBusinessCode":"PPO","MajorLineOfBusinessDescription":"PREFERRED","ProductStartDate":"01/01/2019","ProductEndDate":"12/31/9999","ProductId":"FIXUNV17","Source":"EM","ProductLineCode":"MED","ProductLineDescription":"MEDICAL","ProductTypeCode":"LPXX14","SoldProductDetailList":{"SoldProductDetail":[{"InsuranceCoverage":{"EndDate":"12/31/9999","StartDate":"01/01/2017","CoverageType":"Single","ContractId":"003128729","CoordinationOfBenefitList":{"CoordinationOfBenefit":[{"CarrierName":"HUMANA GOLD PLUS (MEDICARE)","EffectiveDate":"01/01/2019","EndDate":"12/31/9999","Primary":"0","OILastVerifiedDate":"2020-01-31","StatusCode":"INTM"}]},"OriginalEffectiveDate":"12/31/9999","OperationalMajorLineofBusiness":"PPO","PolicySequenceNumber":"0"}}]},"DeductibleTypeCode":"E","IsCarryOverCredit":"false","IsCustomerCarveOut":"false","IsCustomPlan":"false","IsFedQualified":"false","IsHdhp":"false","IsPca":"false","IsPcpRequired":"false","IsRxMedicalIntegratedDeductible":"false","IsRxSpeciality":"true"}},{"InsuranceProduct":{"AlternateDescription":"XAVIER","Description":"Xavier","MajorLineOfBusinessCode":"PPO","MajorLineOfBusinessDescription":"PREFERRED","ProductStartDate":"01/01/2019","ProductEndDate":"12/31/9999","ProductId":"FIXUNV17","Source":"EM","ProductLineCode":"MED","ProductLineDescription":"MEDICAL","ProductTypeCode":"LPXX14","SoldProductDetailList":{"SoldProductDetail":[{"InsuranceCoverage":{"EndDate":"12/31/9999","StartDate":"01/01/2017","CoverageType":"Single","ContractId":"003128729","CoordinationOfBenefitList":{"CoordinationOfBenefit":[{"CarrierName":"HUMANA GOLD PLUS (Duplicate Two)","EffectiveDate":"04/13/2020","EndDate":"10/31/2016","Primary":"1","OILastVerifiedDate":"2020-01-31","StatusCode":"INTM"}]},"OriginalEffectiveDate":"12/31/9999","OperationalMajorLineofBusiness":"PPO","PolicySequenceNumber":"0"}}]},"DeductibleTypeCode":"E","IsCarryOverCredit":"false","IsCustomerCarveOut":"false","IsCustomPlan":"false","IsFedQualified":"false","IsHdhp":"false","IsPca":"false","IsPcpRequired":"false","IsRxMedicalIntegratedDeductible":"false","IsRxSpeciality":"true"}}]}},"MessageList":{"Message":[{"Type":"Warning","Code":"MBEW0003","Source":"OSE Services","Description":"No"},{"Type":"Warning","Code":"MBEW0019","Source":"OSE","Description":"No Dual Status"}]}}}}');                  
			response1.setStatusCode(200);
            response1.setStatus('success');
            String sRecordId = oAcc.Id, sPolicyMemberId = oPol.Id, sProd = 'MED';
            Continuation conti = (Continuation)ValidOIPolicyMemberID_D_HUM.getOtherInsuranceData(sRecordId, sPolicyMemberId);
            Test.setContinuationResponse('continuation-1', response1);
            ValidOIPolicyMemberID_D_HUM.ProcessMBEOtherInsuranceResponse(new List<string>{(String.valueOf(response1))},oPol);
            MBEPlusOIResponse_DTO_HUM objresp = new MBEPlusOIResponse_DTO_HUM();
            objresp = (MBEPlusOIResponse_DTO_HUM)System.JSON.deserialize(response1.getbody(),MBEPlusOIResponse_DTO_HUM.class);
            //ValidOIPolicyMemberID_D_HUM.processOtherInsuranceResponse(objresp,sProd);
            System.assertNotEquals(null, sRecordId);    
            Test.StopTest();
        }
    }
}