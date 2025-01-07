/***********************************************************************************************************
Apex Class Name  : VendorProgram_C_T_HUM
Version          : 1.0
Created Date     : Oct 13 2020
Function         : Test class for VendorProgram_C_HUM
 ************************************************************************************************************

Modification Log:

 * Developer Name           Review Number              Date                       Description
 *-----------------------------------------------------------------------------------------------------------
 * Firoja Begam                                       10/13/2020                   Original Version 
 ********************************************************************************************************************************************************************/

@isTest
private class VendorProgram_C_T_HUM
{
     /*
     * Method Name : setup
     * Description : This method is used to insert the data only once and called automicticaly in all methods
     * Return type : NA
     * Paramater   : NA
     */
    @testSetup
    static void testSetup() 
    {  
        //Load all Constants data
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'?Value not matched');
 
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.OwnerId=oUser.id;
        acc.Name = 'Test Account Cases';
        acc.Enterprise_ID__c = '45678';
        acc.Group_Number__c = '12345678';
        acc.Source_Customer_Key__c ='234234324234232';
        insert acc;
        system.assertEquals(acc.id!=null,true);

        //Medical Product
        Product2 objMedProduct = new Product2();
            objMedProduct.Name = 'MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            insert objMedProduct;
            system.assert(objMedProduct.Product_Type__c == 'Med');
        
        Policy__c policy=HUMTestDataHelperTest.getPolicy();
        policy.Name = 'TestPolicy';
        policy.Group_Name__c=acc.id;
        policy.Exchange_Indicator__c = true;
        policy.Source_Cust_Cov_Key__c='2234233432423423422323';
        policy.Platform__c = 'EM';
        policy.Product__c = objMedProduct.Id;
        insert policy;
        system.assertEquals(policy.id!=null,true);

        Policy_Plan_Details__c policyplandetails1 = new Policy_Plan_Details__c();
        policyplandetails1.Policy_Plan_External_ID__c = '12345';
        policyplandetails1.Coverage_Plan_Effective_Date__c = '12/2/2014';
        policyplandetails1.Coverage_Plan_End_Date__c = '12/12/2014';
        policyplandetails1.Policy__c = policy.id;
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
    }

    /*  
     * Method name : testVendorFieldsValue
     * Description : This Method is used to invoke GBE Service and to update Vendor Fields based on service output.
     * Return Type : Void 
     * Parameter   : NA
     */
    static testMethod void testvendorInfoValue()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'?Value not matched'); 

        System.runAs(oUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new GBEServiceMockResponse_T_HUM());
            
            Policy__c lstPolicy = [Select Id,Product_Type__c,Major_LOB__c,Product__r.Product_Line_Description__c, Platform__c, product__r.name from Policy__c where Name ='TestPolicy' limit 1];
            
            system.assertEquals(lstPolicy !=null,true);
            ApexPages.StandardController oStdController = new ApexPages.StandardController(lstPolicy);
            VendorProgram_C_HUM hum = new VendorProgram_C_HUM(oStdController);
            hum.isCRMSGBOPermissionSetUser = true;
            hum.ccpOnOff =true;
            hum.sObjectID = lstPolicy.Id;
            hum.oPolicy  = lstPolicy;
            hum.getValue();
            hum.SetPolicyValues();
            HttpResponse httpresponse = new HttpResponse();
            String abcd = '{"GroupInfo":{"GroupId":"810314","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(200);
            GBEServiceResponse_H_HUM oResponse = new GBEServiceResponse_H_HUM();
            GBEServiceResponse_H_HUM.GroupInfo groupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
            GBEServiceResponse_H_HUM.GroupInfo groupInfosub = new GBEServiceResponse_H_HUM.GroupInfo();
            /*GBEServiceResponse_H_HUM.VendorProgramOfferGroup vendorprogoffer = new GBEServiceResponse_H_HUM.VendorProgramOfferGroup();
            GBEServiceResponse_H_HUM.VendorProgramOfferSubGroup vendorprogoffersub = new GBEServiceResponse_H_HUM.VendorProgramOfferSubGroup();*/
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

            groupInfosub.AsOfDate = '01/01/2016';
            groupInfosub.EffectiveDate = '01/01/2015';
            groupInfosub.Enddate = '01/01/2016';
            groupInfosub.GroupName = 'vENDORgROUP';
            groupInfosub.Platform = 'EM';
            groupInfosub.StartDate = '01/01/2016';
            
           
           
            hum.getGroupGBEInformation();
            Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponse'; 
            hum.processResponse();
            hum.vendorInfoValue(oResponse);
            VendorProgram_C_HUM.checkResponseValidity(httpresponse);
            Test.stopTest();
        }
    }
}