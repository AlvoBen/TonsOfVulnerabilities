/**************************************************************************************************************************************************
Apex class Name    :  MTVRemarksMember_LT_HUM
Version            :  1.0
Function           :  This is the test class for MTVRemarksMember_LC_HUM

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Kajal Namdev                                            06/07/2021               	initial version
************************************************************************************************************************************************* */
@isTest
public class MTVRemarksMember_LT_HUM {
 	        
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup 
    static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1',GLOBAL_CONSTANT_HUM.PHARMACYSPECIALIST_PROFILE_HUM);   
        insert objUser;

        system.runAs(objUser)
        {
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);

            PurchaserPlan oMedicalPolicy = new PurchaserPlan();
            oMedicalPolicy.Policy_Group_Number__c='11111';
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.PayerId = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';  
            oMedicalPolicy.Platform__c = 'EM';
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);

            PurchaserPlan oDentalPolicy = new PurchaserPlan();
            oDentalPolicy.Name ='Dummy Policy';
            oDentalPolicy.Policy_Group_Number__c='11111';
            oDentalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oDentalPolicy.PayerId = objMemAccount.Id;
            oDentalPolicy.Major_LOB__c ='EPO';  
            insert oDentalPolicy;

            Product2 objMedProduct = new Product2();
            objMedProduct.Name = 'MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            insert objMedProduct;
            system.assert(objMedProduct.Product_Type__c == 'Med');

            MemberPlan objMemberplan = new MemberPlan();
            objMemberplan.Name='MemberPlan1';
            objMemberplan.MemberId=objMemAccount.id;
            objMemberPlan.Member_Dependent_Code__c = '1234567890';
            objMemberPlan.Member_Id_Base__c = 'test23456';
            objMemberPlan.GroupNumber = '737803';
            objMemberPlan.PlanId= oMedicalPolicy.Id;
            insert objMemberplan; 
            system.assert(objMemberPlan.GroupNumber == '737803');
  
            HUM_Webservice_Callout__c owebService = new HUM_Webservice_Callout__c();
            owebService.Name = 'GetMTVRemarks';
            owebService.Active__c = true;
            owebService.Request_Type__c = 'POST';
            owebService.End_Point_URL__c = 'https://int-crmservicews.humana.com/MTVRemarksService_EE/GetMTVRemarks';
            owebService.Timeout__c = '12000';
            owebService.Content_Type__c = 'Application/Json';
            owebService.Certificate_Name__c = 'Humana_GuidanceCare';
            insert owebService;
            
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = new CRMFunctionality_ONOFF_Switch__c(Name = '2172712',IsON__c=true);
            insert csCRMOnOffSwitch;
        }

    }
    /*
    * Method Name : initiateMTVRequestTest
    * Functionality : initiateMTVRequestTest is to test call service for getting remarks from 'GetMTVRemarks' service.
    * Return type : NA.
    */ 
    @isTest 
    static void initiateMTVRequestTest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
		MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            object continueObj = (object)MTVRemarksMember_LC_HUM.initiateMTVRequest(memberPlanObj.Id);
            object continueObj3 = (object)MTVRemarksMember_LC_HUM.initiateMTVRequest('');
            CRMFunctionality_ONOFF_Switch__c csSwitchOnOf = CRMFunctionality_ONOFF_Switch__c.getValues('2172712');
            csSwitchOnOf.IsON__c = false;
            update csSwitchOnOf;
            object continueObj2 = (object)MTVRemarksMember_LC_HUM.initiateMTVRequest(memberPlanObj.Id);
            HttpResponse httpRes = new Httpresponse ();
            String resBodyString ='{ "MTVRemarksResponse":{ "ResponseStatus":{ "StatusCode":"0", "StatusDescription":"SUCCESS", "ErrorMessage":"" }, "RemarkCount":"4", "IdType":"Member", "IdentifierId":"000005027", "SubIdentifier":"01", "RemarkList":{ "Remarks":[ {"IdType":"Member","IdentifierId":"000005027","SubIdentifier":"", "RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2017-09-21", "RemarkText":"0051678525512" }, {"IdType":"Member","IdentifierId":"000005027","SubIdentifier":"","RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2018-12-11", "RemarkText":"0053811233293" }, {"IdType":"Member","IdentifierId":"000005027","SubIdentifier":"", "RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2018-09-16", "RemarkText":"0053343889311" }, {"IdType":"Member","IdentifierId":"000005027","SubIdentifier":"", "RemarkType":"TMID", "RemarkCategory":"", "CreatedDate":"2017-05-23", "RemarkText":"0051185913909" } ] } } }';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            List<string>labelGetmemberEmpty = New List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse(reqType,httpRes);
            MTVRemarksMember_LC_HUM.processMTVResponse(labelGetmember,continueObj);
            MTVRemarksMember_LC_HUM.processMTVResponse(labelGetmemberEmpty,continueObj);
            Test.stopTest();
            
        }
    }
    
}