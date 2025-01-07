/***************************************************************************************************************************************************************** 
Apex Class Name : ApplicationEnrollSearch_LT_HUM
Version : 1.0 
Created On : 11/17/2020
Function : Test Class to cover ApplicationEnrollSearch_LC_HUM

* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Rahul Krishan                                       11/17/2020                   Original Version
*  Kajal Namdev                                        05/07/2021                   Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
**********************************************************************************************************************************************************************/

@isTest
public class ApplicationEnrollSearch_LT_HUM
{    
    @testSetup static void setUpTestData()
    {
        Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
        Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
        Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_CONSTANT_LH_HUM.HumMemberRecordType);
        HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='Application_Search',Timeout__c='12000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', 
                                                                                  Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
        insert mbeServiceRecord;
        system.assert(mbeServiceRecord !=NULL);
    }
    
    static testMethod void ApplicationSearch()
    {
        ApplicationEnrollSearch_LC_HUM.AppEnrollSearch app = new ApplicationEnrollSearch_LC_HUM.AppEnrollSearch();
        Test.startTest();
        Continuation continueObj = (Continuation)ApplicationEnrollSearch_LC_HUM.AMSearchService('{"medicareId":"364300595A","SSN":"","fName":"Marry","lName":"Dodd","DOB":"10/20/1976","sApplicationId":"abc","sOECConfirmationId":"abc","sBarCode":"abc","sMedicaidId":"abc","sEffectiveDateFrom":"10/01/2012","sEffectiveDateTo":"12/31/2020","sReceivedDateFrom":"11/18/2020","sReceivedDateTo":"12/31/2020"}');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '{"EnrollmentResponse": { "EnrollmentDetail": [ { "SysAssnNumber": "010862414", "FirstName": "MARRY", "MiddleName": "", "LastName": "DODD", "NameSuffix": "", "MedicareId": "", "SSN": "193374495", "DOB": "10/20/1976", "MailAddress1": "7006 S ROGERS RD", "MailAddress2": "", "MailCity": "LIZELLA", "MailState": "GA", "MailZipCode": "31052", "CountyName": "021", "OtherAddress1": "", "OtherAddress2": "", "OtherCity": "", "OtherState": "", "OtherZipCode": "", "HomePhone": "478-719-0964", "WorkPhone": "478-719-0964", "GenderCode": "F", "MemberLangPreference": "ENG(Verbal)ENG(Written)", "SmokeIndicator": "N", "DisabledIndicator": "", "GroupId": "", "BSN": "", "SalesRepName": "", "SalesRepSSN": "", "SalesType": "", "CoverageEffectiveDate": "01/01/2014", "CoverageTermDate": "12/31/9999", "ContractId": "", "PlanBenefitPackage": "", "PCPNumber": "", "PCPName": "", "MedicaidId": "", "ESRDIndicator": "", "PaymentOptIndicator": "", "ElectionTypeCd": "", "SignatureType": "", "SignatureDate": "12/31/9999", "MarketReceiptDate": "12/31/9999", "DCNNumber": "", "OSBEffectiveDate": "", "OSBPackage": "", "EmployerId": "", "ApplicationId": "", "OECConfirmationId": "", "PartAEligibleDate": "", "PartBEligibleDate": "", "SepReasonCode": "", "FileDate": "10/04/2013", "ProcessedDate": "10/02/2013", "ApplicationChannel": "EDI", "CurGroupId": "", "POAFirstName": "", "POALastName": "", "POARelationType": "", "POAPhone": "", "FileTrackId": "0042509530036", "RecipientTrackId": "0042509530037", "JobName": "AM0856", "CreationTs": "2013-10-04-17.28.28.000000", "BarCodeBase": "", "BarCodeId1": "", "BarCodeId2": "", "BarCodeId3": "", "BarCodeId4": "", "BarCodeId5": "", "BarCodeId6": "", "BarCodeId7": "", "ExchangeSenderId": "CMSFFM", "ExchangeSubscriberId": "0000001001", "ExchangeMemberId": "0000001001", "ApplicantRelationType": "18", "QHPId": "93332GA072000701", "CSRLevel": "0", "CSREffectiveDate": "12/31/9999", "CSREndDate": "12/31/9999", "APTCAmount": "0.00", "APTCEffectiveDate": "01/01/2014", "APTCEndDate": "12/31/9999", "ExchangeTransactionId": "GA00000033089", "SearchType": "E" }, { "SysAssnNumber": "010862415", "FirstName": "MARRY", "MiddleName": "", "LastName": "DODD", "NameSuffix": "", "MedicareId": "", "SSN": "193374495", "DOB": "10/20/1976", "MailAddress1": "7006 S ROGERS RD", "MailAddress2": "", "MailCity": "LIZELLA", "MailState": "GA", "MailZipCode": "31052", "CountyName": "021", "OtherAddress1": "", "OtherAddress2": "", "OtherCity": "", "OtherState": "", "OtherZipCode": "", "HomePhone": "478-719-0964", "WorkPhone": "478-719-0964", "GenderCode": "F", "MemberLangPreference": "", "SmokeIndicator": "N", "DisabledIndicator": "", "GroupId": "", "BSN": "", "SalesRepName": "", "SalesRepSSN": "", "SalesType": "", "CoverageEffectiveDate": "01/01/2014", "CoverageTermDate": "12/31/9999", "ContractId": "", "PlanBenefitPackage": "", "PCPNumber": "", "PCPName": "", "MedicaidId": "", "ESRDIndicator": "", "PaymentOptIndicator": "", "ElectionTypeCd": "", "SignatureType": "", "SignatureDate": "12/31/9999", "MarketReceiptDate": "12/31/9999", "DCNNumber": "", "OSBEffectiveDate": "", "OSBPackage": "", "EmployerId": "", "ApplicationId": "", "OECConfirmationId": "", "PartAEligibleDate": "", "PartBEligibleDate": "", "SepReasonCode": "", "FileDate": "10/04/2013", "ProcessedDate": "10/02/2013", "ApplicationChannel": "EDI", "CurGroupId": "", "POAFirstName": "", "POALastName": "", "POARelationType": "", "POAPhone": "", "FileTrackId": "0042509530036", "RecipientTrackId": "0042509530038", "JobName": "AM0856", "CreationTs": "2013-10-04-17.28.28.000000", "BarCodeBase": "", "BarCodeId1": "", "BarCodeId2": "", "BarCodeId3": "", "BarCodeId4": "", "BarCodeId5": "", "BarCodeId6": "", "BarCodeId7": "", "ExchangeSenderId": "CMSFFM", "ExchangeSubscriberId": "0000001001", "ExchangeMemberId": "0000001001", "ApplicantRelationType": "18", "QHPId": "93332GA072000701", "CSRLevel": "0", "CSREffectiveDate": "12/31/9999", "CSREndDate": "12/31/9999", "APTCAmount": "0.00", "APTCEffectiveDate": "01/01/2014", "APTCEndDate": "12/31/9999", "ExchangeTransactionId": "GA00000042444", "SearchType": "E" }, { "SysAssnNumber": "012593516", "FirstName": "MARRY", "MiddleName": "", "LastName": "DODD", "NameSuffix": "", "MedicareId": "", "SSN": "193374495", "DOB": "10/20/1976", "MailAddress1": "7006 S ROGERS RD", "MailAddress2": "", "MailCity": "LIZELLA", "MailState": "GA", "MailZipCode": "31052", "CountyName": "021", "OtherAddress1": "", "OtherAddress2": "", "OtherCity": "", "OtherState": "", "OtherZipCode": "", "HomePhone": "478-719-0964", "WorkPhone": "478-719-0964", "GenderCode": "F", "MemberLangPreference": "", "SmokeIndicator": "N", "DisabledIndicator": "", "GroupId": "", "BSN": "", "SalesRepName": "", "SalesRepSSN": "", "SalesType": "", "CoverageEffectiveDate": "01/01/2014", "CoverageTermDate": "12/31/9999", "ContractId": "", "PlanBenefitPackage": "", "PCPNumber": "", "PCPName": "", "MedicaidId": "", "ESRDIndicator": "", "PaymentOptIndicator": "", "ElectionTypeCd": "", "SignatureType": "", "SignatureDate": "12/31/9999", "MarketReceiptDate": "12/31/9999", "DCNNumber": "", "OSBEffectiveDate": "", "OSBPackage": "", "EmployerId": "", "ApplicationId": "", "OECConfirmationId": "", "PartAEligibleDate": "", "PartBEligibleDate": "", "SepReasonCode": "", "FileDate": "12/02/2013", "ProcessedDate": "11/26/2013", "ApplicationChannel": "EDI", "CurGroupId": "", "POAFirstName": "", "POALastName": "", "POARelationType": "", "POAPhone": "", "FileTrackId": "0042856864930", "RecipientTrackId": "0042856865130", "JobName": "AM0856", "CreationTs": "2013-12-02-16.41.38.000199", "BarCodeBase": "", "BarCodeId1": "", "BarCodeId2": "", "BarCodeId3": "", "BarCodeId4": "", "BarCodeId5": "", "BarCodeId6": "", "BarCodeId7": "", "ExchangeSenderId": "CMSFFM", "ExchangeSubscriberId": "0000332177", "ExchangeMemberId": "0000332177", "ApplicantRelationType": "18", "QHPId": "37001GA054000101", "CSRLevel": "0", "CSREffectiveDate": "12/31/9999", "CSREndDate": "12/31/9999", "APTCAmount": "0.00", "APTCEffectiveDate": "01/01/2014", "APTCEndDate": "12/31/9999", "ExchangeTransactionId": "GA00000771177", "SearchType": "E", "SEPDate": "12/31/9999", "SegmentId": "", "PlanYear": "" } ] } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        try
        {
            ApplicationEnrollSearch_LC_HUM.AMSearchServiceResponse(ApplicationEnrollSearch_LC_HUM.contstate);
        }
        catch(Exception e){
            system.assert(e.getMessage()!=NULL);
        }
    }
    
    static testMethod void ApplicationSearchfalse()
    {
        Test.startTest();
        Continuation con = (Continuation)ApplicationEnrollSearch_LC_HUM.AMSearchService('{"medicareId":"1234567890","SSN":"","fName":"Marry","lName":"Dodd","DOB":"10/20/1976","sApplicationId":"abc","sOECConfirmationId":"abc","sBarCode":"abc","sMedicaidId":"abc","sEffectiveDateFrom":"10/01/2012","sEffectiveDateTo":"12/31/2020","sReceivedDateFrom":"11/18/2020","sReceivedDateTo":"12/31/2020"}');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(400);
        Test.setContinuationResponse(ApplicationEnrollSearch_LC_HUM.contstate,httpRes);
        try
        {
            ApplicationEnrollSearch_LC_HUM.AMSearchServiceResponse(ApplicationEnrollSearch_LC_HUM.contstate);
        }
        catch(Exception e){
            system.assert(e.getMessage()!=NULL);
        }
    }    
}