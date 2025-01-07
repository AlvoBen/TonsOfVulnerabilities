/*******************************************************************************************************************************
Apex Class Name : PolicyMemberDetail_T_HUM.cls
Version         : 1.0
Created On      : May 05,2017
Function        : Class contains test methods for PolicyMemberDetail_T_HUM class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey                                   05/05/2017                 Original Version
* Rajesh Keswani            118189                  02/08/2017                 REQ - 305662  PCU- RX Carved Out Information
* Vijay Gurusamy	        275634                  08/23/2018                 REQ - 367015 Test methods
* Anjali Kumar                                      08/28/2018                 REQ - 373406 Replace PBE1.2 with PBE1.3 in Policy Member Detail Page
* Shailesh Mali                                     02/25/2019                 REQ - 383577 EDI Changes
* Satyam Malviya                                    05/28/2019                 Rollback - 1650540
* Satyam Malviya                                    05/28/2019                 Rollback - 1650533
* Manikanta                 356372                  05/31/2019                 REQ - 379006 Ancillary service test methods
* Manikanta                                         06/07/2019                 REQ - 379006 Made code changes to align with the DP changes
* Yogesh Gupta                                      11/04/2019                 REQ-403709 Ancillary service for Medicaid Id
* Ajay Chakradhar                                   08/14/2020                 REQ-755539 
* Firoja Begam                                      10/28/2020                 US-1279281- PR00095366 - MF 1 - New fields in policy details section (member policy page)
* Sai Kumar Boga                                    11/10/2020                 REQ - 1567028  CALL_MF03_FR02: KY MMC 2021(Added per CR17)_CRM service must create a NATIVE AMERICAN indicator
* Ajay Chakradhar                                   05/13/2021                 REQ-2288131 - Improve Test Code Coverage
* Atia Uzma                                         05/27/2021                 User Story 2325450: T1PRJ0001827 - MF 1 - AHT - Ability to automatically launch Dual Eligible tab if the policy is Dual Eligible (CRM)
* Firoja Begam                                      07/14/2021                  User Story 1992199: Accolade 2021 - US 29.01 CRM to display the members effective and term date for the Accolade Program
* Vardhman Jain                                     07/29/2021                 User story 1801229 PR00094010 - MVP MF 6 - Policy Member page - Strides
********************************************************************************************************************************/
@isTest
private class PolicyMemberDetail_T_HUM {
/*
* Method Name : setUpTestData
* Description : This method creates test data for test class
* Return type : None
* Parameter   : None
*/
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1',GLOBAL_CONSTANT_HUM.PHARMACYSPECIALIST_PROFILE_HUM);   
        insert objUser;
        Pharmacy_Supervisor_HUM__c objPharSuper = new Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor' );
        insert objPharSuper;
        system.assertEquals(objPharSuper.name , 'CRMS_300_Humana_Pharmacy_Supervisor');
        
        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);

            //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name = 'MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            insert objMedProduct;
            system.assert(objMedProduct.Product_Type__c == 'Med');
            
            // Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';      
            oMedicalPolicy.Client_Number__c='15';
            oMedicalPolicy.Platform__c = 'EM';
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            
            Policy__c oDentalPolicy = HUMTestDataHelperTest.getPolicy();
            oDentalPolicy.Name = 'Dental Product';
            oDentalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oDentalPolicy.Group_Name__c = objMemAccount.Id;
            oDentalPolicy.Major_LOB__c ='EPO';          
            oDentalPolicy.Platform__c = 'LV';
            insert oDentalPolicy;

            Policy__c oAncillaryPolicy = HUMTestDataHelperTest.getPolicy();
            oAncillaryPolicy.Name = 'Ancillary Product';
            oAncillaryPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oAncillaryPolicy.Group_Name__c = objMemAccount.Id;
            oAncillaryPolicy.Major_LOB__c ='MCD';          
            insert oAncillaryPolicy;
            system.assertEquals(oAncillaryPolicy.Name == 'Ancillary Product', true);
            
             

            Product2 objAncillaryProduct = new Product2();
            objAncillaryProduct.Name = 'MCD';
            objAncillaryProduct.Product_Type__c = 'MCD';
            objAncillaryProduct.productCode = 'MCD';
            insert objAncillaryProduct;
            system.assert(objAncillaryProduct.Product_Type__c == 'MCD');
            
            
            // Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.name = '12591';
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assertEquals(objMedPolMem.Relationship__c == 'self',true);

            Policy_Member__c objAncillaryPolMem = HUMTestDataHelperTest.getPolicyMember();
            objAncillaryPolMem.Member__c = objMemAccount.Id;
            objAncillaryPolMem.name = '12592';
            objAncillaryPolMem.Policy__c = oAncillaryPolicy.Id;
            objAncillaryPolMem.Relationship__c = 'SUBSCRIBER';    
            objAncillaryPolMem.end_date__c = '12/31/2015'; 
            objAncillaryPolMem.Effective_Date__c='01/01/2015';
            objAncillaryPolMem.Policy_Member_External_ID__c = '01|ODS|123456789|89|89|02-01-2001';
            insert objAncillaryPolMem;
            system.assertEquals(objAncillaryPolMem.Relationship__c == 'SUBSCRIBER', true);

            Policy_Plan_Details__c planObj = new Policy_Plan_Details__c(Coverage_Plan_Effective_Date__c = '12/31/9999',Policy_Plan_External_ID__c ='test', Coverage_Plan_End_Date__c='12/31/9999',product__C = objMedProduct.id,policy__c=oMedicalPolicy.Id);
            insert planObj;
            Policy_Plan_Details__c planObjs = new Policy_Plan_Details__c(Coverage_Plan_Effective_Date__c = '12/31/9999',Policy_Plan_External_ID__c ='test1', Coverage_Plan_End_Date__c='12/31/9999',product__C = objMedProduct.id,policy__c=oMedicalPolicy.Id);
            insert planObjs;
            Policy_Plan_Details__c planObject = new Policy_Plan_Details__c(Coverage_Plan_Effective_Date__c = '12/31/9999',Policy_Plan_External_ID__c ='test2', Coverage_Plan_End_Date__c='12/31/9999',product__C = objMedProduct.id,policy__c=oDentalPolicy.Id);
            insert planObject;
        	HUM_Webservice_Callout__c PBEServiceDetails = New HUM_Webservice_Callout__c(Name = 'ProductBusinessEntity_V2',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/PBEService_V2/GetProduct' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        	insert PBEServiceDetails;

            HUM_Webservice_Callout__c oGetMemberAncillary = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GetMemberAncillary',
            End_Point_URL__c = 'https://testst-cmservicews.hum.com', Certificate_Name__c = 'testts', Timeout__c = '12000',
            Content_Type__c = 'application/json', Active__c = true);
            insert oGetMemberAncillary;
            system.assert(oGetMemberAncillary.id!=null);
            system.assert(oGetMemberAncillary.name == 'GetMemberAncillary');
            
            Member_ID__c objMemberID = new Member_ID__c();
            objMemberID.policy_member__c = objAncillaryPolMem .id;
            objMemberID.policy__c = oAncillaryPolicy.Id;
            objMemberID.Name = '12345';
            objMemberID.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID.Member_Dependent_Code__c  = '03'; 
            objMemberID.Type__c = 'Member-Id-Base';
            insert objMemberID;
            system.assertequals('12345',objMemberID.Name);
        Policy_Plan_Details__c policyplandetails1 = new Policy_Plan_Details__c();
        policyplandetails1.Policy_Plan_External_ID__c = '12345';
        policyplandetails1.Coverage_Plan_Effective_Date__c = '12/2/2014';
        policyplandetails1.Coverage_Plan_End_Date__c = '12/12/2014';
        policyplandetails1.Policy__c = objMedPolMem.Policy__c;
        policyplandetails1.Name = 'policyplan1';
        policyplandetails1.Platform__c = 'EM';
        insert policyplandetails1;
        system.assertEquals(policyplandetails1.id!=null,true);
        	HUM_Webservice_Callout__c oHUMGBESservice = new HUM_Webservice_Callout__c();
            oHUMGBESservice.name='GroupBusinessEntityService';
            oHUMGBESservice.Active__c=true;
            oHUMGBESservice.Certificate_Name__c='Certificate';
            oHUMGBESservice.End_Point_URL__c='https://int-crmservicews.humana.com/GBEServiceREST/GetGroupInfo';
            oHUMGBESservice.Timeout__c='12000';
            oHUMGBESservice.Request_Type__c = 'POST';
            insert oHUMGBESservice ;
            system.assert(oHUMGBESservice.id!=null);
            system.assert(oHUMGBESservice.name == 'GroupBusinessEntityService'); 
            HUM_WebService_Callout__c objGetMemberAncillary = New HUM_WebService_Callout__c();
            objGetMemberAncillary.Name = 'GetMemberAncillary_V2';
            objGetMemberAncillary.Active__c = True;
            objGetMemberAncillary.Enable_Service_Tester__c = True;
            objGetMemberAncillary.Certificate_Name__c = 'test';
            objGetMemberAncillary.Content_Type__c = 'application/json';
            objGetMemberAncillary.Request_Type__c = 'POST';
            objGetMemberAncillary.Timeout__c = '12000';
            objGetMemberAncillary.End_Point_URL__c = 'https://test-test.com';
            insert objGetMemberAncillary; 
            HUM_WebService_Callout__c objMBEPlusService = New HUM_WebService_Callout__c();
            objMBEPlusService.Name = 'MBEPlusService';
            objMBEPlusService.Active__c = True;
            objMBEPlusService.Enable_Service_Tester__c = True;
            objMBEPlusService.Certificate_Name__c = 'test';
            objMBEPlusService.Content_Type__c = 'application/json';
            objMBEPlusService.Request_Type__c = 'POST';
            objMBEPlusService.Timeout__c = '12000';
            objMBEPlusService.End_Point_URL__c = 'https://test-test.com';
            insert objMBEPlusService;    
        }
		User objUser2 = HUMTestDataHelperTest.createTestUser('User2', 'Customer Care Specialist');
		insert objUser2;


    }
    

    /*  
     * Method name : testPEOFieldsValue
     * Description : This Method is used to invoke GBE Service and to update PEO Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testpeoInfoValue()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            objPolExt.policyMember  = policyMemObj;
            objPolExt.GetPolicyPlan(policyMemObj);
            objPolExt.SetPolicyPlanValues();
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"871455","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"01/01/2016","EndDate":"","AsOfDate":"10/15/2020","BenfitSequenceNumber":"","VendorProgramOfferSubGroup":{},"GroupName":"KORE PRITAM","EffectiveDate":"12/01/2020","EffectiveStatus":"Future","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"5","NextRenewalDate":"12/01/2021","DentalOriginalEffectiveDate":"01/01/0001","VendorProgramOfferGroup":{},"AssociationList": {"Association": [{"AssociationType": "GPEO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/09/2021"},{"AssociationType": "PEO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/07/2022"},{"AssociationType": "GPCO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/19/2021"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			GBEServiceResponse_H_HUM.AssociationList associationDTO = new GBEServiceResponse_H_HUM.AssociationList();
            GBEServiceResponse_H_HUM.Association association = new GBEServiceResponse_H_HUM.Association();
            List<GBEServiceResponse_H_HUM.Association> associationList = new List<GBEServiceResponse_H_HUM.Association>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'peoGroup';
            groupInfo.Platform = 'EM';
            groupInfo.StartDate = '01/01/2016';
            association.AssociationType = 'PEO';
            association.AssociationName = 'PREFERRED EMPLOYER ORG.';
            association.AssociationEffectiveDate = '01/01/2021';
            association.AssociationEndDate = '12/31/9999';
            associationList.add(association);
            associationDTO.Association = associationList;
            groupInfo.associationList = associationDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.GetGroupInfoFromGBE();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponseVendor'; 
            objPolExt.processResponseVendor();
            objPolExt.peoInfoValue(oResponse);
            PolicyMemberDetail_CE_HUM.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }
    
    /*  
     * Method name : testPEOFieldsValue
     * Description : This Method is used to invoke GBE Service and to update PEO Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testpeoEmNoInfoValue()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            objPolExt.policyMember  = policyMemObj;
            objPolExt.GetPolicyPlan(policyMemObj);
            objPolExt.SetPolicyPlanValues();
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"871455","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"01/01/2016","EndDate":"","AsOfDate":"10/15/2020","BenfitSequenceNumber":"","VendorProgramOfferSubGroup":{},"GroupName":"KORE PRITAM","EffectiveDate":"12/01/2020","EffectiveStatus":"Future","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"5","NextRenewalDate":"12/01/2021","DentalOriginalEffectiveDate":"01/01/0001","VendorProgramOfferGroup":{},"AssociationList": {"Association": [{"AssociationType": "GPEO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/09/2021"},{"AssociationType": "GPCO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/19/2021"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			GBEServiceResponse_H_HUM.AssociationList associationDTO = new GBEServiceResponse_H_HUM.AssociationList();
            GBEServiceResponse_H_HUM.Association association = new GBEServiceResponse_H_HUM.Association();
            List<GBEServiceResponse_H_HUM.Association> associationList = new List<GBEServiceResponse_H_HUM.Association>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'peoGroup';
            groupInfo.Platform = 'EM';
            groupInfo.StartDate = '01/01/2016';
            association.AssociationType = 'GEO';
            association.AssociationName = 'PREFERRED EMPLOYER ORG.';
            association.AssociationEffectiveDate = '01/01/2021';
            association.AssociationEndDate = '12/31/9999';
            associationList.add(association);
            associationDTO.Association = associationList;
            groupInfo.associationList = associationDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.GetGroupInfoFromGBE();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponseVendor'; 
            objPolExt.processResponseVendor();
            objPolExt.peoInfoValue(oResponse);
            PolicyMemberDetail_CE_HUM.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }
    
    /*  
     * Method name : testPEOFieldsValue
     * Description : This Method is used to invoke GBE Service and to update PEO Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testpeoLVInfoValue()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            objPolExt.policyMember  = policyMemObj;
            objPolExt.GetPolicyPlan(policyMemObj);
            objPolExt.SetPolicyPlanValues();
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"871455","Platform":"LV","BenefitPackageId":"XXXXXXXX","StartDate":"01/01/2016","EndDate":"","AsOfDate":"10/15/2020","BenfitSequenceNumber":"","VendorProgramOfferSubGroup":{},"GroupName":"KORE PRITAM","EffectiveDate":"12/01/2020","EffectiveStatus":"Future","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"5","NextRenewalDate":"12/01/2021","DentalOriginalEffectiveDate":"01/01/0001","VendorProgramOfferGroup":{},"OrganizationTypeList": {"OrganizationType": [{"Code": "BC"},{"Code": "PE"},{"Code": "GC"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			GBEServiceResponse_H_HUM.OrganizationTypeList organizationTypeDTO = new GBEServiceResponse_H_HUM.OrganizationTypeList();
            GBEServiceResponse_H_HUM.OrganizationType organizationType = new GBEServiceResponse_H_HUM.OrganizationType();
            List<GBEServiceResponse_H_HUM.OrganizationType> organizationTypeList = new List<GBEServiceResponse_H_HUM.OrganizationType>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'peoGroup';
            groupInfo.Platform = 'LV';
            groupInfo.StartDate = '01/01/2016';
            OrganizationType.Code = 'PE';
            organizationTypeList.add(OrganizationType);
            organizationTypeDTO.OrganizationType = organizationTypeList;
            groupInfo.organizationTypeList = organizationTypeDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.GetGroupInfoFromGBE();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponseVendor'; 
            objPolExt.processResponseVendor();
            objPolExt.peoInfoValue(oResponse);
            PolicyMemberDetail_CE_HUM.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }
    
    /*  
     * Method name : testPEOFieldsValue
     * Description : This Method is used to invoke GBE Service and to update PEO Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testpeoLVNoInfoValue()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            objPolExt.policyMember  = policyMemObj;
            objPolExt.GetPolicyPlan(policyMemObj);
            objPolExt.SetPolicyPlanValues();
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"871455","Platform":"LV","BenefitPackageId":"XXXXXXXX","StartDate":"01/01/2016","EndDate":"","AsOfDate":"10/15/2020","BenfitSequenceNumber":"","VendorProgramOfferSubGroup":{},"GroupName":"KORE PRITAM","EffectiveDate":"12/01/2020","EffectiveStatus":"Future","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"5","NextRenewalDate":"12/01/2021","DentalOriginalEffectiveDate":"01/01/0001","VendorProgramOfferGroup":{},"OrganizationTypeList": {"OrganizationType": [{"Code": "BC"},{"Code": "GC"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			GBEServiceResponse_H_HUM.OrganizationTypeList organizationTypeDTO = new GBEServiceResponse_H_HUM.OrganizationTypeList();
            GBEServiceResponse_H_HUM.OrganizationType organizationType = new GBEServiceResponse_H_HUM.OrganizationType();
            List<GBEServiceResponse_H_HUM.OrganizationType> organizationTypeList = new List<GBEServiceResponse_H_HUM.OrganizationType>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'peoGroup';
            groupInfo.Platform = 'LV';
            groupInfo.StartDate = '01/01/2016';
            OrganizationType.Code = 'GC';
            organizationTypeList.add(OrganizationType);
            organizationTypeDTO.OrganizationType = organizationTypeList;
            groupInfo.organizationTypeList = organizationTypeDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.GetGroupInfoFromGBE();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponseVendor'; 
            objPolExt.processResponseVendor();
            objPolExt.peoInfoValue(oResponse);
            PolicyMemberDetail_CE_HUM.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }

    /*  
     * Method name : testVendorFieldsValue
     * Description : This Method is used to invoke GBE Service and to update Vendor Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testvendorInfoValue()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            objPolExt.policyMember  = policyMemObj;
            objPolExt.GetPolicyPlan(policyMemObj);
            objPolExt.SetPolicyPlanValues();
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"810314","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			//GBEServiceResponse_H_HUM.VendorProgramOfferListGroup vendorProgramGroupDTO = new GBEServiceResponse_H_HUM.VendorProgramOfferListGroup();
			GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = new GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup();
            GBEServiceResponse_H_HUM.VendorProgramOffer vendorOffer = new GBEServiceResponse_H_HUM.VendorProgramOffer();
            List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorList = new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'vENDORgROUP';
            groupInfo.Platform = 'EM';
            groupInfo.StartDate = '01/01/2016';
            vendorOffer.VendorIdentifier = 'ACCL';
            vendorOffer.VendorIdentifierDescription = 'Test';
            vendorOffer.VendorType = 'Test';
            vendorOffer.VendorBenefitCode = '1234';
            vendorOffer.VendorBenefitCodeDescription = 'Accolade';
            vendorOffer.ProgramOfferEffectiveDate = '2020-01-01';
            vendorOffer.ProgramOfferEndDate = '2020-12-12';
            vendorList.add(vendorOffer);
            vendorProgramSubGroupDTO.VendorProgramOffer = vendorList;
            groupInfo.VendorProgramOfferListSubGroup = vendorProgramSubGroupDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.GetGroupInfoFromGBE();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponseVendor'; 
            objPolExt.processResponseVendor();
            objPolExt.vendorInfoValue(oResponse);
            PolicyMemberDetail_CE_HUM.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    } 

    /*  
     * Method name : testActivevendorInfoValueOnDate
     * Description : This Method is used to invoke GBE Service and to update Vendor Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testActivevendorInfoValueOnDate()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"810314","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = new GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup();
            GBEServiceResponse_H_HUM.VendorProgramOffer vendorOffer = new GBEServiceResponse_H_HUM.VendorProgramOffer();
            List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorList = new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'VendorTestGroup';
            groupInfo.Platform = 'EM';
            groupInfo.StartDate = '01/01/2016';
            vendorOffer.VendorIdentifier = 'ACCL';
            vendorOffer.VendorIdentifierDescription = 'Test';
            vendorOffer.VendorType = 'Test';
            vendorOffer.VendorBenefitCode = '1234';
            vendorOffer.VendorBenefitCodeDescription = 'AccoladeTest';
            vendorOffer.ProgramOfferEffectiveDate = '01/01/2021';
            vendorOffer.ProgramOfferEndDate = '07/13/2021';
            vendorList.add(vendorOffer);
            vendorProgramSubGroupDTO.VendorProgramOffer = vendorList;
            groupInfo.VendorProgramOfferListSubGroup = vendorProgramSubGroupDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.setVendorValueOnDate(oResponse);
            objPolExt.setAccoladeProgram(groupInfo.VendorProgramOfferListSubGroup);
            Test.stopTest();
        }
    }
    
    /*  
     * Method name : testFuturevendorInfoValueOnDate
     * Description : This Method is used to invoke GBE Service and to update Vendor Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testFuturevendorInfoValueOnDate()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"810314","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = new GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup();
            GBEServiceResponse_H_HUM.VendorProgramOffer vendorOffer = new GBEServiceResponse_H_HUM.VendorProgramOffer();
            List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorList = new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'VendorTestGroup';
            groupInfo.Platform = 'EM';
            groupInfo.StartDate = '01/01/2016';
            vendorOffer.VendorIdentifier = 'ACCL';
            vendorOffer.VendorIdentifierDescription = 'Test';
            vendorOffer.VendorType = 'Test';
            vendorOffer.VendorBenefitCode = '1234';
            vendorOffer.VendorBenefitCodeDescription = 'AccoladeTest';
            vendorOffer.ProgramOfferEffectiveDate = '09/01/2021';
            vendorOffer.ProgramOfferEndDate = '10/13/2021';
            vendorList.add(vendorOffer);
            vendorProgramSubGroupDTO.VendorProgramOffer = vendorList;
            groupInfo.VendorProgramOfferListSubGroup = vendorProgramSubGroupDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.setVendorValueOnDate(oResponse);
            objPolExt.setAccoladeProgram(groupInfo.VendorProgramOfferListSubGroup);
            Test.stopTest();
        }
    }
    
    /*  
     * Method name : testTermedvendorInfoValueOnDate
     * Description : This Method is used to invoke GBE Service and to update Vendor Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testTermedvendorInfoValueOnDate()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"810314","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
			GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = new GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup();
            GBEServiceResponse_H_HUM.VendorProgramOffer vendorOffer = new GBEServiceResponse_H_HUM.VendorProgramOffer();
            List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorList = new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
            groupInfo.AsOfDate = '01/01/2016';
            groupInfo.EffectiveDate = '01/01/2015';
            groupInfo.Enddate = '01/01/2016';
            groupInfo.GroupName = 'VendorTestGroup';
            groupInfo.Platform = 'EM';
            groupInfo.StartDate = '01/01/2016';
            vendorOffer.VendorIdentifier = 'ACCL';
            vendorOffer.VendorIdentifierDescription = 'Test';
            vendorOffer.VendorType = 'Test';
            vendorOffer.VendorBenefitCode = '1234';
            vendorOffer.VendorBenefitCodeDescription = 'AccoladeTest';
            vendorOffer.ProgramOfferEffectiveDate = '01/01/2021';
            vendorOffer.ProgramOfferEndDate = '06/13/2021';
            vendorList.add(vendorOffer);
            vendorProgramSubGroupDTO.VendorProgramOffer = vendorList;
            groupInfo.VendorProgramOfferListSubGroup = vendorProgramSubGroupDTO;
            oResponse.GroupInfo = groupInfo; 
            objPolExt.setVendorValueOnDate(oResponse);
            objPolExt.setAccoladeProgram(groupInfo.VendorProgramOfferListSubGroup);
            Test.stopTest();
        }
    }

    /*
	* Method Name : testPolicyMemberExt
	* Description : This method is used to test the PolicyMember Ext
	* Return type : None
	* Parameter   : None
	*/
  static testMethod void testPolicyMemberExt()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM(scon);
            system.assertEquals(objPolExt.bIsPharmacyUser,true);
            //added extra
            PolicyMemberDetail_CE_HUM objPolExt1 = new PolicyMemberDetail_CE_HUM(scon);
            objPolExt1.bIsPharmacyUser=false;
            objPolExt1.createFloatingList();
            objPolExt1.bShowEligibilityInfoInHeader = True;
            Test.stopTest();
        }
    }

  /*
  * Method Name : testPolicyMemberExt
  * Description : This method is used to test the PolicyMember Ext as customer Care Specialist User
  * Return type : None
  * Parameter   : None
  */
  @istest
  static void testPolicyMemberExtCC()
  {
	  User testUser = [Select Id, Name from User where LastName = 'test User2'];
	  policy_member__c policyObj = [select id from policy_member__c where Relationship__c = 'self' limit 1];
	  System.runAs(testUser)
	  {
		  Test.startTest();
		  Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
		  pageRef();
		  ApexPages.currentPage().getParameters().put('id', policyObj.id);
		  ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
		  
		  Test.stopTest();
	  }
  }
    
	/*
    * Method Name : testPolicyPage
    * Description : This method is used to test the PBE service call
    * Return type : None
    * Parameter   : None
    */    
    
    static testMethod void testPolicyPage()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
		System.runAs(testUser)
        {
            Test.startTest();
            	Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
				pageRef();
	            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
	            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
	            ApexPages.currentPage().getParameters().put('refDate','01012013');
	            
	            PolicyMemberDetail_DTO_HUM dtoObj = new PolicyMemberDetail_DTO_HUM();
	            dtoObj.policyMemberID = policyObj.id;
	            dtoObj.policyID = policyObj.Policy__c;
	            dtoObj.status = 'Active';
	            dtoObj.endDate = policyObj.end_date__c;
	            dtoObj.soldProductKeyValue = 'test';
	            dtoObj.groupMemberNumber = '176366';
	            Continuation con = (Continuation)PolicyMemberDetail_CE_HUM.invokePBECall(dtoObj);
	            HttpResponse resPBE = new Httpresponse ();
	            String Pbe = '{"GetProductResponse":{ "ProductList":{ "Product":[ { "Categorization":{ "Category":{ "Name":"Insurance", "Code":"INS" }, "Line":{ "Name":"DENTAL", "Code":"DEN" }, "Family":{ "Name":"PPOD", "Code":"PPOD" } }, "Metadata":{ "BrandName":"Humana Inc.", "DesignType":"", "PlatformCode":"EM", "BusinessSegmentList":{ "BusinessSegment":[  ] }, "MarketSegmentList":{ "MarketSegment":[ "None" ] }, "LocationList":{ "Location":[ { "IsPartialCounty":"false", "County":{ "Code":"", "Name":"" }, "ZipList":{ "Zip":[  ] }, "Country":{ "Code":"", "Name":"" }, "State":{ "StateNumber":"", "Code":"KY", "Name":"" } } ] }, "EffectiveDates":{ "BeginEffectiveDate":"2017-07-01", "EndEffectiveDate":"" }, "QuoteDates":{ "QuoteBeginDate":"", "QuoteEndDate":"" } }, "Definition":{ "ProductMasterId":"0", "SourceSystemCode":"PD", "SourceSystemProductId":"000000003831", "Name":"MOCHA V PPO 100/80/60 IN 100/70/40 OUT PEB ORTHO", "Description":"DENTAL", "GenerationCode":"", "Version":"007", "IdentifierList":{ "ProductIdentifier":[ { "ProductId":"HU300001", "ProductIdType":"PackageProductId" }, { "ProductId":"HU300001", "ProductIdType":"BenefitPackageId" }, { "ProductId":"000000003831", "ProductIdType":"PdbPlanId" } ] }, "AlternateNameList":{ "AlternateName":[ { "Name":"MOCHA V PPO 100/80/60 IN 100/7", "NameType":"ProductBuildName" }, { "Name":"MOCHA V PPO 100/80/60 IN 100/70/40 OUT PEB ORTHO", "NameType":"MarketingName" } ] }, "Status":"None" }, "Detail":{ "PackageInfo":{ "PackageId":"HU300001", "Name":"MOCHA V PPO 100/80/60 IN 100/70/40 OUT PEB ORTHO", "AlternateName":"MOCHA V PPO", "Description":"", "AlternateReimburseId":"40590", "IsCarryOverCredit":"false", "IsRxSpeciality":"false", "IsPCA":"false", "IsWomensPreventive":"false", "EffectiveDates":{ "BeginEffectiveDate":"2017-07-01", "EndEffectiveDate":"" }, "AccumulationPeriod":"PlanYear", "CASLOBCode":"" }, "GeneralBenefits":{ "FinanceMarketNumber":"", "IsBenchmarkPlan":"false", "IsCarveOut":"false", "IsHDHP":"false", "IsMinimumEssentialCoverage":"false", "IsMinimumValueStandard":"false", "IsPcpRequired":"false", "OnOrOffExchange":"None", "OptoutCode":"", "PolicySeries":"", "ProductType":"MOCPPO", "FirstDollarAmount":"", "Deductible":{ "DeductibleBehaviorCode":"", "IsRxIncluded":"false", "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "DeductibleList":{ "Coverage":[  ] }, "TypeCode":"None" }, "OutOfPocket":{ "TypeCode":"", "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "OutOfPocketList":{ "Coverage":[  ] } }, "MaxOutOfPocket":{ "MaxOutOfPocketList":{ "Coverage":[  ] } }, "CoInsurance":{ "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "CoInsuranceList":{ "Coverage":[  ] } }, "SuperMoop":{ "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "SuperMoopList":{ "Coverage":[  ] } }, "Limitation":{ "LimitationList":{ "Coverage":[  ] } }, "CopayOfficeVisit":{ "CopayOfficeVisitList":{ "Coverage":[  ] } }, "PlanCommentList":{ "PlanComment":[  ] }, "BenefitIndicatorList":{ "BenefitIndicator":[  ] }, "Copay":{ "CopayList":{ "Coverage":[  ] } } }, "RiderList":{ "Rider":[  ] }, "SupplementalRiderList":{ "SupplementalRider":[ { "Id": "0","TypeCode": "RX","VendorId": "1234","IsCarveOut": "true","IsIntegratedBenefit": "true" } ] }, "BenefitCodesList":{ "BenefitCodes":[  ] }, "RxDetail":{ "PharmacyNetwork":"", "RxPlanId":"", "RxServiceList":{ "RxCoverage":[  ] } }, "ServiceList":{ "Service":[  ] }, "ServiceTypeList":{ "ServiceType":[  ] }, "ServiceCategoryList":{ "ServiceCategory":[  ] }, "PlaceOfServiceList":{ "PlaceOfService":[  ] }, "ParcodeList":{ "ParCode":[  ] }, "AdaCodeList":{ "AdaCode":[  ] }, "CauseCodeList":{ "CauseCode":[  ] } } } ] } } }';
            	resPBE.setBody(Pbe);
            	system.assert(resPBE.getBody() == Pbe);
	            Map<String, HTTPRequest> mapDetails = con.getRequests();
	            String reqPBE = '';
	            for(String s : mapDetails.keyset())
	            {
	                reqPBE = s;
	            } 
	            List<string>labelGetmember = New List<string>();
	            List<string>labelGetmemberEmpty = New List<string>();
	            labelGetmember.add(reqPBE );
	            Test.setContinuationResponse(reqPBE ,resPBE);
	            PolicyMemberDetail_CE_HUM.parsePBEResponse(labelGetmember,'176366');
	            PolicyMemberDetail_CE_HUM.parsePBEResponse(labelGetmemberEmpty,'176366');
            	
            Test.stopTest();
        }
    } 
    
    /*
    * Method Name : testPolicyMemberDetailPage
    * Description : This method is used to test the PBE service call
    * Return type : None
    * Parameter   : None
    */ 
    static testMethod void testPolicyMemberDetailPage()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
		policy__c polObj = [select id from policy__c where name = 'Dental Product'];
        System.runAs(testUser)
        {
            Test.startTest();
            	Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
				pageRef();
	            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
	            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
	            
	            PolicyMemberDetail_DTO_HUM dtoObj = new PolicyMemberDetail_DTO_HUM();
	            dtoObj.policyMemberID = policyObj.id;
	            dtoObj.policyID = polObj.id;
	            dtoObj.status = 'InActive';
	            dtoObj.platformCode = 'LV';
	            dtoObj.endDate = policyObj.end_date__c;
	            dtoObj.soldProductKeyValue = 'test';
	            dtoObj.groupMemberNumber = '176366';
	            Continuation con = (Continuation)PolicyMemberDetail_CE_HUM.invokePBECall(dtoObj);
	            HttpResponse resPBE = new Httpresponse ();
				String Pbe = '{"GetProductResponse":{ "ProductList":{ "Product":[ { "Categorization":{ "Category":{ "Name":"Insurance", "Code":"INS" }, "Line":{ "Name":"DENTAL", "Code":"DEN" }, "Family":{ "Name":"PPOD", "Code":"PPOD" } }, "Metadata":{ "BrandName":"Humana Inc.", "DesignType":"", "PlatformCode":"EM", "BusinessSegmentList":{ "BusinessSegment":[  ] }, "MarketSegmentList":{ "MarketSegment":[ "None" ] }, "LocationList":{ "Location":[ { "IsPartialCounty":"false", "County":{ "Code":"", "Name":"" }, "ZipList":{ "Zip":[  ] }, "Country":{ "Code":"", "Name":"" }, "State":{ "StateNumber":"", "Code":"KY", "Name":"" } } ] }, "EffectiveDates":{ "BeginEffectiveDate":"2017-07-01", "EndEffectiveDate":"" }, "QuoteDates":{ "QuoteBeginDate":"", "QuoteEndDate":"" } }, "Definition":{ "ProductMasterId":"0", "SourceSystemCode":"PD", "SourceSystemProductId":"000000003831", "Name":"MOCHA V PPO 100/80/60 IN 100/70/40 OUT PEB ORTHO", "Description":"DENTAL", "GenerationCode":"", "Version":"007", "IdentifierList":{ "ProductIdentifier":[ { "ProductId":"HU300001", "ProductIdType":"PackageProductId" }, { "ProductId":"HU300001", "ProductIdType":"BenefitPackageId" }, { "ProductId":"000000003831", "ProductIdType":"PdbPlanId" } ] }, "AlternateNameList":{ "AlternateName":[ { "Name":"MOCHA V PPO 100/80/60 IN 100/7", "NameType":"ProductBuildName" }, { "Name":"MOCHA V PPO 100/80/60 IN 100/70/40 OUT PEB ORTHO", "NameType":"MarketingName" } ] }, "Status":"None" }, "Detail":{ "PackageInfo":{ "PackageId":"HU300001", "Name":"MOCHA V PPO 100/80/60 IN 100/70/40 OUT PEB ORTHO", "AlternateName":"MOCHA V PPO", "Description":"", "AlternateReimburseId":"40590", "IsCarryOverCredit":"false", "IsRxSpeciality":"false", "IsPCA":"false", "IsWomensPreventive":"false", "EffectiveDates":{ "BeginEffectiveDate":"2017-07-01", "EndEffectiveDate":"" }, "AccumulationPeriod":"PlanYear", "CASLOBCode":"" }, "GeneralBenefits":{ "FinanceMarketNumber":"", "IsBenchmarkPlan":"false", "IsCarveOut":"false", "IsHDHP":"false", "IsMinimumEssentialCoverage":"false", "IsMinimumValueStandard":"false", "IsPcpRequired":"false", "OnOrOffExchange":"None", "OptoutCode":"", "PolicySeries":"", "ProductType":"MOCPPO", "FirstDollarAmount":"", "Deductible":{ "DeductibleBehaviorCode":"", "IsRxIncluded":"false", "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "DeductibleList":{ "Coverage":[  ] }, "TypeCode":"None" }, "OutOfPocket":{ "TypeCode":"", "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "OutOfPocketList":{ "Coverage":[  ] } }, "MaxOutOfPocket":{ "MaxOutOfPocketList":{ "Coverage":[  ] } }, "CoInsurance":{ "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "CoInsuranceList":{ "Coverage":[  ] } }, "SuperMoop":{ "Accumulates":{ "InNetwork":{ "PlanType":[ "None" ] }, "OutOfnetwork":{ "PlanType":[ "None" ] } }, "SuperMoopList":{ "Coverage":[  ] } }, "Limitation":{ "LimitationList":{ "Coverage":[  ] } }, "CopayOfficeVisit":{ "CopayOfficeVisitList":{ "Coverage":[  ] } }, "PlanCommentList":{ "PlanComment":[  ] }, "BenefitIndicatorList":{ "BenefitIndicator":[  ] }, "Copay":{ "CopayList":{ "Coverage":[  ] } } }, "RiderList":{ "Rider":[  ] }, "SupplementalRiderList":{ "SupplementalRider":[ { "Id": "0","TypeCode": "RX","VendorId": "1234","IsCarveOut": "true","IsIntegratedBenefit": "true" } ] }, "BenefitCodesList":{ "BenefitCodes":[  ] }, "RxDetail":{ "PharmacyNetwork":"", "RxPlanId":"", "RxServiceList":{ "RxCoverage":[  ] } }, "ServiceList":{ "Service":[  ] }, "ServiceTypeList":{ "ServiceType":[  ] }, "ServiceCategoryList":{ "ServiceCategory":[  ] }, "PlaceOfServiceList":{ "PlaceOfService":[  ] }, "ParcodeList":{ "ParCode":[  ] }, "AdaCodeList":{ "AdaCode":[  ] }, "CauseCodeList":{ "CauseCode":[  ] } } } ] } } }';
            	resPBE.setBody(Pbe);
            	system.assert(resPBE.getBody() == Pbe);
	            Map<String, HTTPRequest> mapDetails = con.getRequests();
	            String reqPBE = '';
	            for(String s : mapDetails.keyset())
	            {
	                reqPBE = s;
	            } 
	            List<string>labelGetmember = New List<string>();
	            List<string>labelGetmemberEmpty = New List<string>();
	            labelGetmember.add(reqPBE );
	            Test.setContinuationResponse(reqPBE ,resPBE);
	            PolicyMemberDetail_CE_HUM.parsePBEResponse(labelGetmember,'176366');
	            PolicyMemberDetail_CE_HUM.parsePBEResponse(labelGetmemberEmpty,'176366');
            	
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testPolicyFaultPage
    * Description : This method is used to test the PBE service call to cover error logging
    * Return type : None
    * Parameter   : None
    */ 
	static testMethod void testPolicyFaultPage()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
       
        System.runAs(testUser)
        {
            Test.startTest();
            	Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
				pageRef();
	            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
	            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
	            ApexPages.currentPage().getParameters().put('refDate','01012013');
	            
	            PolicyMemberDetail_DTO_HUM dtoObj = new PolicyMemberDetail_DTO_HUM();
	            dtoObj.policyMemberID = policyObj.id;
	            dtoObj.policyID = policyObj.Policy__c;
	            dtoObj.status = 'Active';
	            dtoObj.platformCode = 'EM';
	            dtoObj.refDateFromURL = '01012013';
	            dtoObj.endDate = policyObj.end_date__c;
	            dtoObj.soldProductKeyValue = 'test';
	            dtoObj.groupMemberNumber = '176366';
	            Continuation con = (Continuation)PolicyMemberDetail_CE_HUM.invokePBECall(dtoObj);
	            HttpResponse resPBE = new Httpresponse ();
	            string Pbe = '{ "Fault":{ "faultcode":"\n          Code-67.8.1", "faultstring":"\n          error_service_name :\"RightSourceService &#124;\n          error_service_method : GetAdjustmentsAndPayments &#124;\n          fault_message : ", "detail":{ "transactionInfo":{ "transaction-id":"207260733", "transaction-client":"127.0.0.1", "URL-in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetAdjustmentsAndPayments", "processor-name":"JSONServicesGateway", "processor-type":"Multiprotocol Gateway" }, "errorInfo":{ "error-code":"0x00d30003", "error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: OK OK\r\nConnection: close\r\n\r\n", "error-message":"Member not found", "error-protocol-reason-phrase":"", "error-protocol-response":"0", "error-subcode":"0x00d30003" } } } }';
	           
            	resPBE.setBody(Pbe);
            	system.assert(resPBE.getBody() == Pbe);
	            Map<String, HTTPRequest> mapDetails = con.getRequests();
	            String reqPBE = '';
	            for(String s : mapDetails.keyset())
	            {
	                reqPBE = s;
	            } 
	            List<string>labelGetmember = New List<string>();
	            labelGetmember.add(reqPBE );
	            Test.setContinuationResponse(reqPBE ,resPBE);
	            PolicyMemberDetail_CE_HUM.parsePBEResponse(labelGetmember,'176366');
            Test.stopTest();
        }
    } 
    
    /*
    * Method Name : testPolicyFaultPage
    * Description : This method is used to test negative scenario of PBE service call
    * Return type : None
    * Parameter   : None
    */ 
    static testMethod void testPolicyNegativeScenario()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
		HUM_Webservice_Callout__c obj = [select id from HUM_Webservice_Callout__c where name = 'ProductBusinessEntity_V2' limit 1];
        delete obj;
        System.runAs(testUser)
        {
            Test.startTest();
            	Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
				pageRef();
	            ApexPages.StandardController scon = new ApexPages.StandardController(policyObj);
	            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
	            ApexPages.currentPage().getParameters().put('refDate','01012013');
	            
	            PolicyMemberDetail_DTO_HUM dtoObj = new PolicyMemberDetail_DTO_HUM();
	            dtoObj.policyMemberID = policyObj.id;
	            dtoObj.policyID = policyObj.Policy__c;
	            dtoObj.status = 'Active';
	            dtoObj.endDate = policyObj.end_date__c;
	            dtoObj.soldProductKeyValue = 'test';
	            dtoObj.groupMemberNumber = '176366';
	            Continuation con = (Continuation)PolicyMemberDetail_CE_HUM.invokePBECall(dtoObj);
            Test.stopTest();
        }
    } 
    
       /**
     * REQ-383577 - Integration for EDI Group Test  
     * Returns responses of continuation object
     * Callback Method to parse the EDI service response 
     * @param label object and continuation state object
     * @return Object
     */  
    
   static testMethod void processEDIResponseTest()
    {
         User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
		HUM_Webservice_Callout__c obj = new HUM_Webservice_Callout__c();
		obj.Name = 'GetEDIData';
		obj.Active__c = true;
		obj.Request_Type__c = 'POST';
		obj.End_Point_URL__c = 'https://dev-crmservicews.humana.com/EDIGroupService/GetEDIGroupInfo';
		obj.Timeout__c = '12000';
		obj.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
		insert obj;

        String groupId ='5246131'; 
        HUMConstants__c EnableDualStatus1 = New HUMConstants__c();
        EnableDualStatus1.Name = 'Enable Dual Status Indicator';
        EnableDualStatus1.IntegerValue__c = 0;
        EnableDualStatus1.StringValue__c = 'False';
        insert EnableDualStatus1;
 		System.runAs(testUser)
        {
             Test.startTest();
				pageRef();
              ApexPages.StandardController oStdController = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            PolicyMemberDetail_CE_HUM ocontroller = New PolicyMemberDetail_CE_HUM(oStdController);       
             PolicyMemberDetail_DTO_HUM dtoObj = new PolicyMemberDetail_DTO_HUM();
	            dtoObj.policyMemberID = policyObj.id;
	            dtoObj.policyID = policyObj.Policy__c;
	            dtoObj.status = 'Active';
	            dtoObj.endDate = policyObj.end_date__c;
	            dtoObj.soldProductKeyValue = 'test';
	            dtoObj.groupMemberNumber = '176366';
             Continuation continueObj = (Continuation)PolicyMemberDetail_CE_HUM.getEDIDataCntrlMethod(dtoObj.groupMemberNumber);
           
           HttpResponse httpRes = new Httpresponse ();
          String resBodyString = '{"GetEDIGroupInfoResponse": {"Timestamp": "","ResponseType": "","ErrorCode": "","ErrorDescription":"","RequestGuid":"", "EdiGroupDetails": [{"ErrorCode": "","ErrorDescription":  "","GroupId": "","PCP_PCDFlag":"","Frequency":"","Day1":"","Day2":"","Day3":"","Day4":"","Platform": "MTV","EDIGroup": ""}]}}';
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);         
          string reqType = '';
            
          List<string>labelGetmember = new List<string>();
            List<string>labelGetmemberEmpty = New List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);
          PolicyMemberDetail_CE_HUM.processEDIResponse(labelGetmember,continueObj); 
            PolicyMemberDetail_CE_HUM.processEDIResponse(labelGetmemberEmpty,continueObj);
          Test.stopTest();
        }
     }
     
    /**
	* Test method for Response DTO class.
	
	*/
	static testMethod void testDTOResponse()
	{
		User testUser = [Select Id, Name, LastName from User where LastName = 'test User1'];
		system.assertEquals('test User1', testUser.LastName);
		System.runAs(testUser)
		{
			Test.startTest();

			HUMPolicyMember_EDIResponse_DTO.EdiGroupDetails pmd = new HUMPolicyMember_EDIResponse_DTO.EdiGroupDetails();
				pmd.ErrorCode='';
            	pmd.ErrorDescription='';
            	pmd.GroupId='';
            	pmd.EDIGroup='';
                pmd.Platform='';
                pmd.PCP_PCDFlag='';
                pmd.Frequency='';
            	pmd.Day1='';
            	pmd.Day2='';
            	pmd.Day3='';
            	pmd.Day4='';
                    
			List<HUMPolicyMember_EDIResponse_DTO.EdiGroupDetails> lstpmd = new List<HUMPolicyMember_EDIResponse_DTO.EdiGroupDetails>();
			lstpmd.add(pmd);

			HUMPolicyMember_EDIResponse_DTO.GetEDIGroupInfoResponse gsr = new HUMPolicyMember_EDIResponse_DTO.GetEDIGroupInfoResponse();
			gsr.Timestamp ='';
			gsr.ResponseType ='';
	        gsr.ErrorCode ='';
		    gsr.ErrorDescription ='';
		    gsr.RequestGuid ='';
			gsr.EdiGroupDetails = lstpmd;

			HUMPolicyMember_EDIResponse_DTO h = new HUMPolicyMember_EDIResponse_DTO();
			h.GetEDIGroupInfoResponse = gsr;

			Test.stopTest();
		}
	}
	
	static testMethod void testDTORequest(){
		User testUser = [Select Id, Name, LastName from User where LastName = 'test User1'];
		system.assertEquals('test User1', testUser.LastName);
		System.runAs(testUser)
		{
			Test.startTest();

			HUMPolicyMember_EDIRequest_DTO pmd1 = new HUMPolicyMember_EDIRequest_DTO();
			HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest gsr = new HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest();
			HUMPolicyMember_EDIRequest_DTO.GroupDetail pmd = new HUMPolicyMember_EDIRequest_DTO.GroupDetail();
				
                    
			List<HUMPolicyMember_EDIRequest_DTO.GroupDetail> lstpmd = new List<HUMPolicyMember_EDIRequest_DTO.GroupDetail>();
			lstpmd.add(pmd);

						
			gsr.GroupDetail = lstpmd;

			HUMPolicyMember_EDIRequest_DTO h = new HUMPolicyMember_EDIRequest_DTO();
			h.GetEDIGroupInfoRequest = gsr;

			Test.stopTest();}
   
		}

    /*
    * Method Name : testMemberAncillary
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testMemberAncillary()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj1 = [select id,Issue_State__c from policy_member__c where Name = '12592' limit 1];
         HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon1 = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            PolicyMemberDetail_CE_HUM objPolExt1 = new PolicyMemberDetail_CE_HUM(scon1);
            objPolExt1.initiateAncillaryRequest();
            objPolExt1.processAncillaryResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{"responseHeader":{"environment": "INT", "context":{"clientSessionId": "G0x7f22da06bc08-1D", "server": "LOUWEBWTL25S02", "transactionId": "0180de59-a07d-4162-9f32-1b06f4d66647"}, "metrics":{"RequestTimeStamp": "2019-06-07T09:11:52.6", "ResponseTimeStamp": "2019-06-07T09:11:52.7", "duration": 61}},"responseStatus":{"status": "SUCCESS", "message":{"code": "MASI0001", "details": "Member Ancillary details returned successfully."}},"AncillaryEntityList":[{ "memberIdentifier":{"MemberIdBase": "H78898283", "DependentCode": "00", "PlatformCode": "LV", "CoverageStartDate": null, "GroupId": "0Y3521", "DivisionId": "0Y3521", "ProductId": "7592490201" }, "EntityTypeList":[{ "Type": "Premium", "Value": "12.00", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "MedicallyFrail", "Value": "58", "StartDate": "2019-03-01", "EndDate": "9999-12-31" },{ "Type": "Rewards", "Value": "A", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "JuvenilleJustice", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "PenaltyReasonIndicator", "Value": "CP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "SuspensionReasonIndicator", "Value": "CS", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "HospiceIndicator", "Value": "HP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "FosterCareIndicator", "Value": "N", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "GuardianshipIndicator", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"}],"HeadofHousehold":{"FirstName": "RTNDIN", "LastName": "RTHNSAN", "PostalAddress":{ "AddressLine1": "10855 NW 45TH ST", "AddressLine2": "", "City": "CORAL SPRINGS", "State": "12", "Zip": "33065"}}, "LTSS":{ "OtherHealthIndicators": "", "SpecialCond": "SCE", "LtcProgramRecommendation": "", "ProdPlanId": "", "ProviderCode": "", "LevelOfCareDate": null, "LevelOfCareCode": "", "SpecialNeedsNote": "", "MedicaidAidCategoryCode": "MS", "MedicaidBeginDate": null, "MedicaidEndDate": null, "ACWM": "" }, "CostShareInformation": "181Y", "CommunityEngagementExemptCode": "E", "TypeOfAssistance": "ABDM", "DeductibleAmount": 12}]}}';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            objPolExt1.ancillaryResponseParse(oResponse);
            system.assert(objPolExt1.AncillaryInfo!=null,true);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testMemberAncillaryNegative
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods negative scenario
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testMemberAncillaryNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj2 = [select id from policy_member__c where Name = '12591' limit 1];
         HUMConstants__c EnableDualStatus3 = New HUMConstants__c();
        EnableDualStatus3.Name = 'Enable Dual Status Indicator';
        EnableDualStatus3.IntegerValue__c = 0;
        EnableDualStatus3.StringValue__c = 'False';
        insert EnableDualStatus3;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon2 = New ApexPages.StandardController(policyObj2);
            ApexPages.currentPage().getParameters().put('id',policyObj2.id);
            PolicyMemberDetail_CE_HUM objPolExt2 = new PolicyMemberDetail_CE_HUM(scon2);            
            objPolExt2.initiateAncillaryRequest();
            objPolExt2.processAncillaryResponse();            
            HttpResponse oResponse;            
            objPolExt2.ancillaryResponseParse(oResponse);
            system.assert(objPolExt2.AncillaryInfo!=null,true);
            Test.stopTest();
        }
    }
	 /*
    * Method Name : testMemberAncillaryKY
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
   static testMethod void testMemberAncillaryKY()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy__c policy = [select id from policy__c where name = 'Dental Product'];
        policy.Issue_State__c  = 'KY';
        update policy;
        policy_member__c policyObj1 = [select id, policy__c,Issue_State__c from policy_member__c where Name = '12592' limit 1];
        policyObj1.policy__c = policy.Id;
        update policyObj1;
        HUM_Webservice_Callout__c obj = new HUM_Webservice_Callout__c();
        obj.Name = 'GetMemberAncillary';
        obj.Active__c = true;
        obj.Request_Type__c = 'POST';
        obj.End_Point_URL__c = 'https://dev-crmservicews.humana.com/AncillaryServiceREST_V2/getMemberAncillary';
        obj.Timeout__c = '12000';
        obj.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
        insert obj;
        
        HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon1 = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            PolicyMemberDetail_CE_HUM objPolExt1 = new PolicyMemberDetail_CE_HUM(scon1);
            objPolExt1.EDIValue = 'Hg5852';
            objPolExt1.humanaValue = 'A58852';
            objPolExt1.groupUpdateFrequencyValue = 'N56933';
            objPolExt1.initiateAncillaryRequest();
            objPolExt1.processAncillaryResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String Response = '{"getMemberAncillaryResponse":{"responseHeader":{"environment":"INT","context":{"clientSessionId":"G0x7f49b241b408-1D","server":"LOUWEBWTL25S02","transactionId":"8a257fdb-b8c5-4ef0-a254-0f1e8b24a139"},"metrics":{"RequestTimeStamp":"2019-11-15T05:22:54.6","ResponseTimeStamp":"2019-11-15T05:22:54.6","duration":2.0}},"responseStatus":{"status":"SUCCESS","message":{"code":"MASI0001","details":"MemberAncillaryDetailsreturnedsuccessfully."}},"AncillaryEntityList":[{"LTSS":{"otherHealthIndicators":"","specialCond":"","ltcProgramRecommendation":"","prodPlanId":"","providerCode":"","levelOfCareDate":"","levelOfCareCode":"","specialNeedsNote":"","medicaidAidCategoryCode":"","medicaidBeginDate":"","medicaidEndDate":"","ACWM":""},"HeadOfHousehold":{"FirstName":"RACHEL","LastName":"DDROP","postalAddress":{"addressLine1":"112OSBOURNEWAY","addressLine2":"","city":"GEORGETOWN","zip":"40324","state":"21"}},"CostShareInformation":"191Y","CommunityEngagementExemptCode":"W","DeductibleAmount":21.0,"TypeOfAssistance":"ADLT","JuvenilleJustice":"N","FosterCareIndicator":"Y","GuardianshipIndicator":"N","HospiceIndicator":"N","MedicallyFrail":"A1","PenaltyReasonIndicator":"CP","Premium":"11.00","Rewards":"A","SuspensionReasonIndicator":"CS"}]}}';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            objPolExt1.ancillaryResponseParse(oResponse);
            Test.stopTest();
        }
    }
/*
    * Method Name : testMbeplus
    * Description : This method is used to test the initiateMbeRequest and processMbeResponse methods
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testMbeplus()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj1 = [select id,Issue_State__c from policy_member__c where Name = '12592' limit 1];
         HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon1 = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            PolicyMemberDetail_CE_HUM objPolExt1 = new PolicyMemberDetail_CE_HUM(scon1);
            objPolExt1.initiateMbeRequest();
            objPolExt1.processMbeResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"GetMemberResponse": {   "Header": {"ResponseHeader":    {      "Environment": "Dev",      "Version": "2.0",      "Server": {"Name": "SIMIGWDI05"},      "Metrics":       {         "Duration": "145",         "RequestTimeStamp": "2020-09-08T11:24:12.723Z",         "ResponseTimeStamp": "2020-09-08T11:24:12.868Z"      },      "ResponseMetaData":       {         "Status": "Warning",         "ClientSessionID": "GUID",         "SessionID": "c79aafb5-b615-4add-803a-63a92fddde66",         "DataAuthorizationConstraintList": {"DataAuthorizationConstraint":          [                        {               "ConstraintType": "Exclude",               "Tenant": "1",               "Source": "5"            },                        {               "ConstraintType": "Include",               "Tenant": "1"            },                        {               "ConstraintType": "Include",               "Tenant": "1",               "Source": "16"            }         ]}      }   }},   "Body":    {      "Member":       {         "DateOfBirth": "09/15/1940",         "FirstName": "LUSELA",         "Gender": "M",         "LastName": "BROWNELL",         "Ssn": "581177602",         "Contact": {"ContactProfile":          {            "EmailList": {"PersonEmail": [            {               "Address": "T0037004240917@HUMANATEST.COM",               "Type": "Primary"            }]},            "PhoneList": {"PersonPhone": [            {               "PhoneNumber": "6544390720",               "Type": "Home"            }]},            "AddressList": {"PersonAddress": [            {               "AddressLine1": "8561 SUMNER STREET",               "City": "CHICAGO",               "StateCode": "IL",               "County": "COOK",               "ZipCode": "60618",               "ZipPlus": "1166",               "Type": "Home",               "GeoLocation": {"Position":                {                  "Coordinates":                   {                     "Latitude": "84.047100",                     "Longitude": "-28.581800"                  },                  "AcquiredTime": ""               }},               "CountyCode": "031"            }]}         }},         "RaceList": "\n\nR4.01.002\nSAMOAN",         "EthnicityList": {"Ethnicity": [         {            "Code": "G",            "Description": "HISPANIC OR LATINO SPANISH"         }]},         "LanguageList": "\n\nENG\nEnglish\nSpoken\n\nENG\nEnglish\nWritten",         "MedicaidIdentifierList": {"MedicaidIdentifier": [         {            "MedicaidId": "114315096",            "StateCode": "IL"         }]},         "MedicareIdentifierList": {"MedicareIdentifier": [{"MedicareId": "8Q10R47EF24"}]},         "MasterId": "1008647672",         "LongTermCareEligibilityList": {"LongTermCareEligibility": [         {            "EffectiveDate": "02/01/2019",            "EndDate": "10/31/2019",            "PlanTypeCode": "DEMO",            "ProgramCategoryCode": "A0",            "StateCode": "IL"         }]},         "LowIncomeSubsidyLevelList": {"LowIncomeSubsidyLevel":          [                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2021",               "EndDate": "12/31/2021",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS3",               "EffectiveDate": "03/01/2019",               "EndDate": "12/31/2019",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2013",               "EndDate": "12/31/2013",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2019",               "EndDate": "02/28/2019",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2012",               "EndDate": "12/31/2012",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2015",               "EndDate": "12/31/2015",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2007",               "EndDate": "12/31/2007",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "06/01/2014",               "EndDate": "12/31/2014",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2018",               "EndDate": "12/31/2018",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2014",               "EndDate": "05/31/2014",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2010",               "EndDate": "12/31/2010",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2017",               "EndDate": "12/31/2017",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "04/01/2006",               "EndDate": "12/31/2006",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2009",               "EndDate": "12/31/2009",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS3",               "EffectiveDate": "01/01/2020",               "EndDate": "12/31/2020",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2011",               "EndDate": "12/31/2011",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2016",               "EndDate": "12/31/2016",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2008",               "EndDate": "12/31/2008",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2006",               "EndDate": "03/31/2006",               "Percent": "100"            }         ]},         "PlatformPointerList": {"PlatformProxyKey":          [                        {               "Application": "",               "KeyType": "PERSONID",               "KeyValue": "364195843",               "Source": "LV"            },                        {               "Application": "",               "KeyType": "PERSONID",               "KeyValue": "0631432423285108",               "Source": "EM"            },                        {               "Application": "",               "KeyType": "ODS_GENKEY",               "KeyValue": "0037004240917",               "Source": "ODS"            },                        {               "Application": "",               "KeyType": "NIEXTERNALSYSTEMENTITYID",               "KeyValue": "2532497316",               "Source": "HV"            }         ]},         "SoldProductList": {"SoldProduct":          [            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "7228910206",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "10/31/2019",                  "StartDate": "02/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459006000||7228910206|2019-01-01|false|||2019-02-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459006000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2021",               "ProductEndDate": "12/31/2021",               "ProductId": "2512690201",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||2512690201|2021-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "3010020106",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "10/31/2019",                  "StartDate": "02/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461006000||3010020106|2019-01-01|false|||2019-02-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461006000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "3010020102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||3010020102|2019-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2020",               "ProductEndDate": "12/31/2020",               "ProductId": "9064930102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||9064930102|2020-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "7228910202",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||7228910202|2019-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2017",               "ProductEndDate": "12/31/2017",               "ProductId": "0252770108",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "01/31/2019",                  "StartDate": "06/01/2014",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||0252770108|2017-01-01|false|||2014-06-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642824",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2020",               "ProductEndDate": "12/31/2020",               "ProductId": "8615660102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||8615660102|2020-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2021",               "ProductEndDate": "12/31/2021",               "ProductId": "2140440401",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||2140440401|2021-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2017",               "ProductEndDate": "12/31/2017",               "ProductId": "2372530201",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "01/31/2019",                  "StartDate": "06/01/2014",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||2372530201|2017-01-01|false|||2014-06-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643824",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }}         ]},         "IndicatorList": {"Indicator":          [                        {               "Name": "Retire",               "Value": "False"            },                        {               "Name": "HumanaEmployee",               "Value": "False"            },                        {               "Name": "Smoke",               "Value": "False"            },                        {               "Name": "Tenant",               "Value": "3"            },                        {               "Name": "MedicareEnrolled",               "Value": "True"            }         ]}      },      "MessageList": {"Message":       [                  {            "Type": "Warning",            "Code": "MBEW0002",            "Source": "OSE Services",            "Description": "Unable to retrieve membership details for one of the platform."         },                  {            "Type": "Warning",            "Code": "MBEW0003",            "Source": "OSE Services",            "Description": "No coverage found for one of the platforms." } ]}}}}';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            objPolExt1.mbeResponseparse(oResponse);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testMemberMbePlusKY
    * Description : This method is used to test the initiateMbeRequest and processMbeResponse methods
    * Return type : void
    * Parameter   : None
    */
   static testMethod void testMbePlusKY()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy__c policy = [select id from policy__c where name = 'Dental Product'];
        policy.Issue_State__c  = 'KY';
        update policy;
        policy_member__c policyObj1 = [select id,Name,policy__c,Issue_State__c,Member__r.Enterprise_Id__c from policy_member__c where Name = '12592' limit 1];
        policyObj1.policy__c = policy.Id;
        
        update policyObj1;
        HUM_Webservice_Callout__c obj = new HUM_Webservice_Callout__c();
        obj.Name = 'MBEPlusService';
        obj.Active__c = true;
        obj.Request_Type__c = 'POST';
        obj.End_Point_URL__c = 'https://dev-crmservicews.humana.com/MBEPLUSServiceREST/GetMember';
        obj.Timeout__c = '12000';
        obj.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
        insert obj;
        HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController sconm = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            PolicyMemberDetail_CE_HUM objPolExtm = new PolicyMemberDetail_CE_HUM(sconm);
            objPolExtm.EDIValue = 'Hg5852';
            objPolExtm.humanaValue = 'A58852';
            objPolExtm.groupUpdateFrequencyValue = 'N56933';
            Continuation con = (Continuation)objPolExtm.initiateMbeRequest();
            objPolExtm.processMbeResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String Response = '{"GetMemberResponse": {   "Header": {"ResponseHeader":    {      "Environment": "Dev",      "Version": "2.0",      "Server": {"Name": "SIMIGWDI05"},      "Metrics":       {         "Duration": "145",         "RequestTimeStamp": "2020-09-08T11:24:12.723Z",         "ResponseTimeStamp": "2020-09-08T11:24:12.868Z"      },      "ResponseMetaData":       {         "Status": "Warning",         "ClientSessionID": "GUID",         "SessionID": "c79aafb5-b615-4add-803a-63a92fddde66",         "DataAuthorizationConstraintList": {"DataAuthorizationConstraint":          [                        {               "ConstraintType": "Exclude",               "Tenant": "1",               "Source": "5"            },                        {               "ConstraintType": "Include",               "Tenant": "1"            },                        {               "ConstraintType": "Include",               "Tenant": "1",               "Source": "16"            }         ]}      }   }},   "Body":    {      "Member":       {         "DateOfBirth": "09/15/1940",         "FirstName": "LUSELA",         "Gender": "M",         "LastName": "BROWNELL",         "Ssn": "581177602",         "Contact": {"ContactProfile":          {            "EmailList": {"PersonEmail": [            {               "Address": "T0037004240917@HUMANATEST.COM",               "Type": "Primary"            }]},            "PhoneList": {"PersonPhone": [            {               "PhoneNumber": "6544390720",               "Type": "Home"            }]},            "AddressList": {"PersonAddress": [            {               "AddressLine1": "8561 SUMNER STREET",               "City": "CHICAGO",               "StateCode": "IL",               "County": "COOK",               "ZipCode": "60618",               "ZipPlus": "1166",               "Type": "Home",               "GeoLocation": {"Position":                {                  "Coordinates":                   {                     "Latitude": "84.047100",                     "Longitude": "-28.581800"                  },                  "AcquiredTime": ""               }},               "CountyCode": "031"            }]}         }},         "RaceList": "\n\nR4.01.002\nSAMOAN",         "EthnicityList": {"Ethnicity": [         {            "Code": "G",            "Description": "HISPANIC OR LATINO SPANISH"         }]},         "LanguageList": "\n\nENG\nEnglish\nSpoken\n\nENG\nEnglish\nWritten",         "MedicaidIdentifierList": {"MedicaidIdentifier": [         {            "MedicaidId": "114315096",            "StateCode": "IL"         }]},         "MedicareIdentifierList": {"MedicareIdentifier": [{"MedicareId": "8Q10R47EF24"}]},         "MasterId": "1008647672",         "LongTermCareEligibilityList": {"LongTermCareEligibility": [         {            "EffectiveDate": "02/01/2019",            "EndDate": "10/31/2019",            "PlanTypeCode": "DEMO",            "ProgramCategoryCode": "A0",            "StateCode": "IL"         }]},         "LowIncomeSubsidyLevelList": {"LowIncomeSubsidyLevel":          [                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2021",               "EndDate": "12/31/2021",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS3",               "EffectiveDate": "03/01/2019",               "EndDate": "12/31/2019",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2013",               "EndDate": "12/31/2013",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2019",               "EndDate": "02/28/2019",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2012",               "EndDate": "12/31/2012",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2015",               "EndDate": "12/31/2015",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2007",               "EndDate": "12/31/2007",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "06/01/2014",               "EndDate": "12/31/2014",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2018",               "EndDate": "12/31/2018",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2014",               "EndDate": "05/31/2014",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2010",               "EndDate": "12/31/2010",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2017",               "EndDate": "12/31/2017",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "04/01/2006",               "EndDate": "12/31/2006",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2009",               "EndDate": "12/31/2009",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS3",               "EffectiveDate": "01/01/2020",               "EndDate": "12/31/2020",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2011",               "EndDate": "12/31/2011",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2016",               "EndDate": "12/31/2016",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2008",               "EndDate": "12/31/2008",               "Percent": "100"            },                        {               "CopayLevelCode": "LIS2",               "EffectiveDate": "01/01/2006",               "EndDate": "03/31/2006",               "Percent": "100"            }         ]},         "PlatformPointerList": {"PlatformProxyKey":          [                        {               "Application": "",               "KeyType": "PERSONID",               "KeyValue": "364195843",               "Source": "LV"            },                        {               "Application": "",               "KeyType": "PERSONID",               "KeyValue": "0631432423285108",               "Source": "EM"            },                        {               "Application": "",               "KeyType": "ODS_GENKEY",               "KeyValue": "0037004240917",               "Source": "ODS"            },                        {               "Application": "",               "KeyType": "NIEXTERNALSYSTEMENTITYID",               "KeyValue": "2532497316",               "Source": "HV"            }         ]},         "SoldProductList": {"SoldProduct":          [            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "7228910206",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "10/31/2019",                  "StartDate": "02/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459006000||7228910206|2019-01-01|false|||2019-02-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459006000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2021",               "ProductEndDate": "12/31/2021",               "ProductId": "2512690201",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||2512690201|2021-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "3010020106",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "10/31/2019",                  "StartDate": "02/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461006000||3010020106|2019-01-01|false|||2019-02-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461006000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "02/01/2019",                     "EndDate": "10/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "3010020102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||3010020102|2019-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2020",               "ProductEndDate": "12/31/2020",               "ProductId": "9064930102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||9064930102|2020-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2019",               "ProductEndDate": "12/31/2019",               "ProductId": "7228910202",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||7228910202|2019-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MER RISK GATE CAP EXC",               "Description": "076 736",               "MajorLineOfBusinessCode": "MER",               "MajorLineOfBusinessDescription": "MEDICARE HMO",               "ProductStartDate": "01/01/2017",               "ProductEndDate": "12/31/2017",               "ProductId": "0252770108",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "ProductTypeCode": "MAPD",               "SegmentType": "MCR",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "01/31/2019",                  "StartDate": "06/01/2014",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nHN\nHMO - MEDICARE RISK",                     "CoverageKey": "1008647672|364195843|364195843|LV|258459|258459002000||0252770108|2017-01-01|false|||2014-06-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258459",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258459002000",                        "Source": "LV"                     }                  ]},                  "IndicatorList": {"Indicator": [                  {                     "Name": "HCFAStatCd",                     "Value": "02"                  }]},                  "ContractId": "539411174",                  "Network":                   {                     "NetworkId": "328379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33642824",                     "GrouperName": "AMG IRVING & WESTERN MMAI MER"                  }]},                  "OperationalMajorLineofBusiness": "MER",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "S"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2020",               "ProductEndDate": "12/31/2020",               "ProductId": "8615660102",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||8615660102|2020-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2021",               "ProductEndDate": "12/31/2021",               "ProductId": "2140440401",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "12/31/9999",                  "StartDate": "11/01/2019",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||2140440401|2021-01-01|false|||2019-11-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "11/01/2019",                     "EndDate": "12/31/9999",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643927",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }},            {"InsuranceProduct":             {               "AlternateDescription": "MEDICAID GATE CAP DIR",               "Description": "020 156",               "MajorLineOfBusinessCode": "MCD",               "MajorLineOfBusinessDescription": "MEDICAID",               "ProductStartDate": "01/01/2017",               "ProductEndDate": "12/31/2017",               "ProductId": "2372530201",               "Source": "LV",               "ProductLineCode": "MED",               "ProductLineDescription": "MEDICAL PRODUCTS",               "SegmentType": "MCD",               "SoldProductDetailList": {"SoldProductDetail": [{"InsuranceCoverage":                {                  "EndDate": "01/31/2019",                  "StartDate": "06/01/2014",                  "CoveredRoleList": {"CoveredRole": [                  {                     "StartDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "MemberMasterId": "1008647672",                     "RelationshipToSubscriber": "EmployeeSelf",                     "CoveredRolePlatformPointerList": {"PlatformProxyKey":                      [                                                {                           "Application": "CI",                           "KeyType": "MemberIDDependentCode",                           "KeyValue": "00",                           "Source": "LV"                        },                                                {                           "Application": "CI",                           "KeyType": "MemberIdBase",                           "KeyValue": "H40146656",                           "Source": "LV"                        },                                                {                           "Application": "",                           "KeyType": "PersonId",                           "KeyValue": "364195843",                           "Source": "LV"                        }                     ]},                     "Hipaa": "\nEMP\n18\nSELF\nMC\nMEDICAID",                     "CoverageKey": "1008647672|364195843|364195843|LV|258461|258461002000||2372530201|2017-01-01|false|||2014-06-01|H40146656|00|IL"                  }]},                  "PolicyType": "Individual",                  "CoverageType": "Single",                  "ReasonList": {"Reason": [                  {                     "Code": "713",                     "Description": "OPTION CHANGE (MER/MGR)",                     "Type": "None"                  }]},                  "GroupPlatformPointerList": {"PlatformProxyKey":                   [                                          {                        "Application": "CI",                        "KeyType": "GroupId",                        "KeyValue": "258461",                        "Source": "LV"                     },                                          {                        "Application": "CI",                        "KeyType": "DivisionId",                        "KeyValue": "258461002000",                        "Source": "LV"                     }                  ]},                  "ContractId": "729104254",                  "Network":                   {                     "NetworkId": "938379297983792979",                     "CASH1Network": "8379",                     "CASD1Network": "2979",                     "CASH2Network": "8379",                     "CASD2Network": "2979",                     "NetworkDirectoryList": {"NetworkDirectory": [                     {                        "DirectoryId": "853",                        "DisplayName": "Humana Gold Plus Integrated",                        "FormNumber": "H0336_ILHHNPVES",                        "Primary": "true"                     }]}                  },                  "CoordinationOfBenefitList": {"CoordinationOfBenefit": [                  {                     "CarrierName": "BRUK",                     "EffectiveDate": "06/01/2014",                     "EndDate": "12/31/9999",                     "LastUpdatedDate": "2014-07-14",                     "StatusCode": "D",                     "StatusDescription": "DUAL DEMONSTRATION PLAN"                  }]},                  "PrimaryCarePhysicianList": {"PrimaryCarePhysician": [                  {                     "EffectiveDate": "06/01/2014",                     "EndDate": "01/31/2019",                     "Name": "FABROS MUNEZ, DESIREE I MD",                     "SourceProviderKey": "000098226J",                     "SourceGrouperNumber": "33643824",                     "GrouperName": "AMG IRVING & WESTERN MMAI MCD"                  }]},                  "OperationalMajorLineofBusiness": "MCD",                  "PolicySequenceNumber": "0",                  "HcfaEnrollmentSourceCode": "B"               }}]},               "IsCarryOverCredit": "false",               "IsCustomerCarveOut": "false",               "IsCustomPlan": "false",               "IsFedQualified": "false",               "IsHdhp": "false",               "IsPca": "false",               "IsPcpRequired": "true",               "IsRxMedicalIntegratedDeductible": "false",               "IsRxSpeciality": "false"            }}         ]},         "IndicatorList": {"Indicator":          [                        {               "Name": "Retire",               "Value": "False"            },                        {               "Name": "HumanaEmployee",               "Value": "False"            },                        {               "Name": "Smoke",               "Value": "False"            },                        {               "Name": "Tenant",               "Value": "3"            },                        {               "Name": "MedicareEnrolled",               "Value": "True"            }         ]}      },      "MessageList": {"Message":       [                  {            "Type": "Warning",            "Code": "MBEW0002",            "Source": "OSE Services",            "Description": "Unable to retrieve membership details for one of the platform."         },                  {            "Type": "Warning",            "Code": "MBEW0003",            "Source": "OSE Services",            "Description": "No coverage found for one of the platforms." } ]}}}}';
            oResponse.setBody(Response);
            oResponse.setStatusCode(200);
            
            System.assert(oResponse!=null);
            objPolExtm.mbeResponseparse(oResponse);
            Test.stopTest();
        }
    }
   /*
    * Method Name : testMemberMbePlusKYNegative
    * Description : This method is used to test the initiateMbeRequest and processMbeResponse methods negative scenario
    * Return type : void
    * Parameter   : None
    */
    
     static testMethod void testMemberMbePlusKYNegative()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj2 = [select id from policy_member__c where Name = '12591' limit 1];
         HUMConstants__c EnableDualStatus3 = New HUMConstants__c();
        EnableDualStatus3.Name = 'Enable Dual Status Indicator';
        EnableDualStatus3.IntegerValue__c = 0;
        EnableDualStatus3.StringValue__c = 'False';
        insert EnableDualStatus3;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController sconn = New ApexPages.StandardController(policyObj2);
            ApexPages.currentPage().getParameters().put('id',policyObj2.id);
            PolicyMemberDetail_CE_HUM objPolExtn = new PolicyMemberDetail_CE_HUM(sconn);            
            objPolExtn.initiateMbeRequest();
            objPolExtn.processMbeResponse();            
            HttpResponse oResponse;            
            objPolExtn.mbeResponseparse(oResponse);
            system.assert(objPolExtn.MbeInfo!=null,true);
            Test.stopTest();
        }
    }
    /*
    * Method Name : testMemberAncillaryLA
    * Description : This method is used to test the initiateAncillaryRequest and processAncillaryResponse methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberAncillaryLA()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy__c policy = [select id from policy__c where name = 'Dental Product'];
        policy.Issue_State__c  = 'LA';
        update policy;
        policy_member__c policyObj1 = [select id, policy__c,Issue_State__c from policy_member__c where Name = '12592' limit 1];
        policyObj1.policy__c = policy.Id;
        update policyObj1;
        HUM_Webservice_Callout__c obj = new HUM_Webservice_Callout__c();
        obj.Name = 'GetMemberAncillary';
        obj.Active__c = true;
        obj.Request_Type__c = 'POST';
        obj.End_Point_URL__c = 'https://dev-crmservicews.humana.com/AncillaryServiceREST_V2/getMemberAncillary';
        obj.Timeout__c = '12000';
        obj.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
        insert obj;
        HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon1 = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            PolicyMemberDetail_CE_HUM objPolExt1 = new PolicyMemberDetail_CE_HUM(scon1);
            objPolExt1.initiateAncillaryRequest();
            objPolExt1.processAncillaryResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{ "AncillaryEntityList":[ { "MaintenanceReason":"MaintenanceReason", "HandicapIndicator":"Y", "CoverageEndDate":"2021-05-30", "ChishlmCase":{ "startDate":"2021-05-16", "endDate":"2021-06-16", "manager":"Louxi52di01" }, "CapitationCode":"CapitationCode", "ChoiceCode":"ChoiceCode", "HighRiskPregancyCode":"HighRiskPregancyCode", "BehavioralHealthCode":"BehavioralHealthCode", "ClosureCode":"ClosureCode", "RenewalDate":"05/16/2021 3:41:19 PM", "RenewalCode":"RenewalCode", "MultipleBirthIndicator":"MultipleBirthIndicator", "MedicalAssistanceCategoryCode":"MedicalAssistanceCategoryCode", "CoordinatedSystemOfCare":{ "admitDate":"2021-05-16", "dischargeDate":"2021-05-18" }, "NewBornMothersReferenceId":"NewBornMothersReferenceId", "CaseTypeCode":"CaseTypeCode", "DepartmentOfCorrection":{ "type":"type", "startDate":"2023-05-17", "endDate":"2021-05-16", "preReleaseDate":"2020-05-16", "locationIndicator":"Y" } } ] } }';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            objPolExt1.ancillaryResponseParse(oResponse);
            system.assert(objPolExt1.AncillaryInfo!=null,true);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testpolicymembervalidOI
    * Description : This method is used to test OI valid policy Member
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testpolicymembervalidOI()
     {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy__c policy = [select id from policy__c where name = 'Dental Product'];
        policy.Issue_State__c  = 'LA';
        update policy;
        policy_member__c policyObj1 = [select id, policy__c,Issue_State__c from policy_member__c where Name = '12592' limit 1];
        policyObj1.policy__c = policy.Id;
        update policyObj1;
        HUM_Webservice_Callout__c obj = new HUM_Webservice_Callout__c();
        obj.Name = 'MBEPlusService';
        obj.Active__c = true;
        obj.Request_Type__c = 'POST';
        obj.End_Point_URL__c = 'https://XXX-crmservicews.humana.com/MBEPLUSServiceREST/GetMember'; 
        obj.Timeout__c = '12000';
        obj.Certificate_Name__c = 'testt';
        insert obj;
        HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            String polId = policyObj1.Id, recId = policy.Id;
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon1 = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            List<string> labelGetmember = New List<string>();
            labelGetmember.add('MBE');
            PolicyMemberDetail_CE_HUM.ProcessMBEOtherInsuranceResponse(labelGetmember,'176366');
            PolicyMemberDetail_CE_HUM oPol = new PolicyMemberDetail_CE_HUM();
            oPol.sOtherInsuranceResult = 'Yes';
            oPol.otherInsuranceYesNO();
            PolicyMemberDetail_CE_HUM.getOIData(recId,polId);
            system.assert(polId!=null,true);
            Test.stopTest();
        }        
     }
    
    /*
    * Method Name : testnegative
    * Description : This method is used to test negative
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testnegative()
    {
       User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy_member__c policyMemObj = [select id,name, member__c, end_date__c, Effective_Date__c,  Policy__r.Product__r.ProductCode, Policy__r.Major_LOB__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12591' limit 1];
        HUMConstants__c EnableDualStatus = New HUMConstants__c();
        EnableDualStatus.Name = 'Enable Dual Status Indicator';
        EnableDualStatus.IntegerValue__c = 0;
        EnableDualStatus.StringValue__c = 'False';
        insert EnableDualStatus;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.currentPage().getParameters().put('id',policyMemObj.id); 
            ApexPages.StandardController scon = new ApexPages.StandardController(policyMemObj);
            PolicyMemberDetail_CE_HUM objPolExt = new PolicyMemberDetail_CE_HUM();
            objPolExt.bIsPharmacyUser= true;
            system.assertEquals(objPolExt.bIsPharmacyUser,true);
            objPolExt.policyMember  = policyMemObj;
            objPolExt.GetPolicyPlan(policyMemObj);
            objPolExt.SetPolicyPlanValues();
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"871455","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"01/01/2016","EndDate":"","AsOfDate":"10/15/2020","BenfitSequenceNumber":"","VendorProgramOfferSubGroup":{},"GroupName":"KORE PRITAM","EffectiveDate":"12/01/2020","EffectiveStatus":"Future","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"5","NextRenewalDate":"12/01/2021","DentalOriginalEffectiveDate":"01/01/0001","VendorProgramOfferGroup":{},"OrganizationTypeList": {"OrganizationType": [{"Code": "BC"},{"Code": "GC"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            PolicyMemberDetail_CE_HUM.checkResponseValidity(httpresponse);
            PolicyMemberDetail_CE_HUM.getOIData('','');
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testotherInsuranceYesNO
    * Description : This method is used to test else condition on otherInsuranceYesNO
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testotherInsuranceYesNO()
     {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Policy__c policy = [select id from policy__c where name = 'Dental Product'];
        policy.Issue_State__c  = 'LA';
        update policy;
        policy_member__c policyObj1 = [select id, policy__c,Issue_State__c from policy_member__c where Name = '12592' limit 1];
        policyObj1.policy__c = policy.Id;
        update policyObj1;
        HUM_Webservice_Callout__c obj = new HUM_Webservice_Callout__c();
        obj.Name = 'MBEPlusService';
        obj.Active__c = true;
        obj.Request_Type__c = 'POST';
        obj.End_Point_URL__c = 'https://XXX-crmservicews.humana.com/MBEPLUSServiceREST/GetMember'; 
        obj.Timeout__c = '12000';
        obj.Certificate_Name__c = 'testt';
        insert obj;
        HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        System.runAs(testUser)
        {
            Test.startTest();
            String polId = policyObj1.Id, recId = policy.Id;
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon1 = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            List<string> labelGetmember = New List<string>();
            labelGetmember.add('MBE');
            PolicyMemberDetail_CE_HUM.ProcessMBEOtherInsuranceResponse(labelGetmember,'176366');
            PolicyMemberDetail_CE_HUM oPol = new PolicyMemberDetail_CE_HUM();
            oPol.otherInsuranceYesNO();
            PolicyMemberDetail_CE_HUM.getOIData(recId,polId);
            system.assert(polId!=null,true);
            Test.stopTest();
        }        
     }
	 /*
    * Method Name : unifiedSearchDetails
    * Description : This method is used to cover the unified search button
    * Return type : void
    * Parameter   : None
    */
	     static testMethod void unifiedSearchDetails()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        policy_member__c policyObj1 = [select id,Issue_State__c from policy_member__c where Name = '12592' limit 1];
        HUMConstants__c EnableDualStatus2 = New HUMConstants__c();
        EnableDualStatus2.Name = 'Enable Dual Status Indicator';
        EnableDualStatus2.IntegerValue__c = 0;
        EnableDualStatus2.StringValue__c = 'False';
        insert EnableDualStatus2;
        CRMFunctionality_ONOFF_Switch__c obj = new CRMFunctionality_ONOFF_Switch__c();
        obj.Name = 'Unified Search Switch';
        obj.IsON__c = true;
        insert obj;
        System.runAs(testUser)
        {
            Test.startTest();
            Id permissionSetId = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_310_UnifiedDocumentSearch'].Id;
            PermissionSetAssignment psa = new PermissionSetAssignment(PermissionSetId = permissionSetId, AssigneeId = testUser.id);
            insert psa;
            Test.setCurrentPage(page.PolicyMemberDetail_VF_HUM);
			pageRef();
            ApexPages.StandardController scon1 = New ApexPages.StandardController(policyObj1);
            ApexPages.currentPage().getParameters().put('id',policyObj1.id);
            PolicyMemberDetail_CE_HUM objPolExt1 = new PolicyMemberDetail_CE_HUM(scon1);
            objPolExt1.initiateAncillaryRequest();
            objPolExt1.processAncillaryResponse();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            String response = '{"getMemberAncillaryResponse":{"responseHeader":{"environment": "INT", "context":{"clientSessionId": "G0x7f22da06bc08-1D", "server": "LOUWEBWTL25S02", "transactionId": "0180de59-a07d-4162-9f32-1b06f4d66647"}, "metrics":{"RequestTimeStamp": "2019-06-07T09:11:52.6", "ResponseTimeStamp": "2019-06-07T09:11:52.7", "duration": 61}},"responseStatus":{"status": "SUCCESS", "message":{"code": "MASI0001", "details": "Member Ancillary details returned successfully."}},"AncillaryEntityList":[{ "memberIdentifier":{"MemberIdBase": "H78898283", "DependentCode": "00", "PlatformCode": "LV", "CoverageStartDate": null, "GroupId": "0Y3521", "DivisionId": "0Y3521", "ProductId": "7592490201" }, "EntityTypeList":[{ "Type": "Premium", "Value": "12.00", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "MedicallyFrail", "Value": "58", "StartDate": "2019-03-01", "EndDate": "9999-12-31" },{ "Type": "Rewards", "Value": "A", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "JuvenilleJustice", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "PenaltyReasonIndicator", "Value": "CP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "SuspensionReasonIndicator", "Value": "CS", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "HospiceIndicator", "Value": "HP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "FosterCareIndicator", "Value": "N", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "GuardianshipIndicator", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"}],"HeadofHousehold":{"FirstName": "RTNDIN", "LastName": "RTHNSAN", "PostalAddress":{ "AddressLine1": "10855 NW 45TH ST", "AddressLine2": "", "City": "CORAL SPRINGS", "State": "12", "Zip": "33065"}}, "LTSS":{ "OtherHealthIndicators": "", "SpecialCond": "SCE", "LtcProgramRecommendation": "", "ProdPlanId": "", "ProviderCode": "", "LevelOfCareDate": null, "LevelOfCareCode": "", "SpecialNeedsNote": "", "MedicaidAidCategoryCode": "MS", "MedicaidBeginDate": null, "MedicaidEndDate": null, "ACWM": "" }, "CostShareInformation": "181Y", "CommunityEngagementExemptCode": "E", "TypeOfAssistance": "ABDM", "DeductibleAmount": 12}]}}';
            oResponse.setBody(response);
            oResponse.setStatusCode(200); 
            System.assert(oResponse!=null);
            objPolExt1.ancillaryResponseParse(oResponse);
            system.assert(objPolExt1.AncillaryInfo!=null,true);
            Test.stopTest();
        }
    }
	
	static void pageRef(){
        PageReference pageref = Page.PolicyMemberDetail_VF_HUM;    
             pageref.getHeaders().put('USER-AGENT','chrome');
             Test.setCurrentPage(pageref);
    }
}