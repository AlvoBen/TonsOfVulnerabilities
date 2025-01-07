/***************************************************************************************************************************************************************** 
Apex Class Name : EnrollCBISearch_LT_HUM
Version : 1.0 
Created On : 11/17/2020
Function : Test Class to cover EnrollCBISearch_LC_HUM

* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Rahul Krishan                                       11/17/2020                   Original Version
*  Kajal Namdev                                        05/07/2021                  Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
*  Visweswararao Jyavarapu                             04/07/2023                  User Story 4404841: T1PRJ0865978 - MF24206 - Add SSN field in UI and enhance the search capability for CBIS in enrollment search
**********************************************************************************************************************************************************************/

@isTest
public class EnrollCBISearch_LT_HUM
{    
    @testSetup static void setUpTestData()
    {
        User user1 = HUMTestDataHelperTest.createTestUser('User1','System Administrator'); 
        insert user1;
        User user2 = HUMTestDataHelperTest.createTestUser('User2','System Administrator'); 
        insert user2;
        Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
        Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
        Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_CONSTANT_LH_HUM.HumMemberRecordType);
        HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='HUMCbisSearchEnrollment_V2',Timeout__c='120000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Humana_GuidanceCare_SWAT1', 
                                                                                  Content_Type__c = 'application/json',End_Point_URL__c = 'https://int-crmservicews.humana.com/EnrollmentService_V2.0/GetCBISPendsAndTerms');
        HUM_Webservice_Callout__c mbeServiceRecord1 =new HUM_Webservice_Callout__c(Name='HUMCbisSearchEnrollment',Timeout__c='10000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Humana_GuidanceCare_SWAT1', 
                                                                                  Content_Type__c = 'text/xml',End_Point_URL__c = 'https://int-crmservicews.humana.com/EnrollmentService/GetCBISPendsAndTerms');
        insert mbeServiceRecord;
         insert mbeServiceRecord1;
        system.assert(mbeServiceRecord !=NULL);
        HUMConstants__c setting = new HUMConstants__c();
        setting.Name = 'CBIS_EXTID';
        setting.StringValue__c = 'UKM|00|CB|';
        insert setting;
    }   
    
    
    static testMethod void EnrollCBISearch()
    {
        User testUser1 = [Select Id, LastName from User where LastName='test User1'];
        EnrollCBISearch_LC_HUM.CBISearchModal cbis = new EnrollCBISearch_LC_HUM.CBISearchModal();
        System.runAs(testUser1)
        {
            Test.startTest();
            Continuation continueObj = (Continuation)EnrollCBISearch_LC_HUM.searchCBISCode('{"fName":"WILLIE","lName":"WAITERS","DOB":"04/19/1948","sState":"FL"}');
            Test.stopTest();
            HttpResponse httpRes = new Httpresponse();
            String resBodyString = '<?xml version="1.0"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body><GetPendHistoryTermsDetailsResponse xmlns="http://salesservices.humana.com/SpecialtyBenefits/CRMServices/GetPendHistoryAndTermsDetailsService"><GetPendHistoryTermsDetailsResult xmlns:a="http://schemas.datacontract.org/2004/07/Humana.SpecialtyBenefits.CRMService.Common" xmlns:i="http://www.w3.org/2001/XMLSchema-instance"><a:ResponseItems><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>Saint Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>253789304</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>10197011</a:MemberId><a:MemberSSN>253789304</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>7273233732</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>03</a:BenId><a:CoverageEndDate>10/1/2007</a:CoverageEndDate><a:CoverageStartDate>1/1/2005</a:CoverageStartDate><a:GroupId>26176</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>Saint Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>253789304</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>513832</a:MemberId><a:MemberSSN>253789304</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>8133233732</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>03</a:BenId><a:CoverageEndDate>1/1/2005</a:CoverageEndDate><a:CoverageStartDate>1/1/1996</a:CoverageStartDate><a:GroupId>6453</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CD5238</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CD5238</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CP0031</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>St Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>603113597</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>21022682</a:MemberId><a:MemberSSN>603113597</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>60</a:BenId><a:CoverageEndDate>4/1/2013</a:CoverageEndDate><a:CoverageStartDate>8/1/2012</a:CoverageStartDate><a:GroupId>ARRNTL</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem></a:ResponseItems><a:Status><a:Error><a:ErrorCode>0</a:ErrorCode><a:Message/></a:Error><a:IsSuccesful>true</a:IsSuccesful></a:Status></GetPendHistoryTermsDetailsResult></GetPendHistoryTermsDetailsResponse></s:Body></s:Envelope>';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            try
            {
                EnrollCBISearch_LC_HUM.CBISCodeProcessResponse(EnrollCBISearch_LC_HUM.contstate);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            }
        }     
    }   
    
  	static testMethod void EnrollCBISearchnewSSN()
    {
        User testUser1 = [Select Id, LastName from User where LastName='test User1'];
        EnrollCBISearch_LC_HUM.CBISearchModal cbis = new EnrollCBISearch_LC_HUM.CBISearchModal();
        System.runAs(testUser1)
        {
            Test.startTest();
            Continuation continueObj = (Continuation)EnrollCBISearch_LC_HUM.seachCBISCodeSpeciality('{"sSSN":"234234233","fName":"","lName":"","DOB":"","sState":""}');
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponseCBISSpecialty';
            objCon.state='test';
            Test.stopTest();
            HttpResponse httpRes = new Httpresponse();
            String resBodyString = '<?xml version="1.0"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body><GetPendHistoryTermsDetailsResponse xmlns="http://salesservices.humana.com/SpecialtyBenefits/CRMServices/GetPendHistoryAndTermsDetailsService"><GetPendHistoryTermsDetailsResult xmlns:a="http://schemas.datacontract.org/2004/07/Humana.SpecialtyBenefits.CRMService.Common" xmlns:i="http://www.w3.org/2001/XMLSchema-instance"><a:ResponseItems><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>Saint Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>253789304</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>10197011</a:MemberId><a:MemberSSN>253789304</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>7273233732</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>03</a:BenId><a:CoverageEndDate>10/1/2007</a:CoverageEndDate><a:CoverageStartDate>1/1/2005</a:CoverageStartDate><a:GroupId>26176</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>Saint Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>253789304</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>513832</a:MemberId><a:MemberSSN>253789304</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>8133233732</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>03</a:BenId><a:CoverageEndDate>1/1/2005</a:CoverageEndDate><a:CoverageStartDate>1/1/1996</a:CoverageStartDate><a:GroupId>6453</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CD5238</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CD5238</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CP0031</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>St Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>603113597</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>21022682</a:MemberId><a:MemberSSN>603113597</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>60</a:BenId><a:CoverageEndDate>4/1/2013</a:CoverageEndDate><a:CoverageStartDate>8/1/2012</a:CoverageStartDate><a:GroupId>ARRNTL</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem></a:ResponseItems><a:Status><a:Error><a:ErrorCode>0</a:ErrorCode><a:Message/></a:Error><a:IsSuccesful>true</a:IsSuccesful></a:Status></GetPendHistoryTermsDetailsResult></GetPendHistoryTermsDetailsResponse></s:Body></s:Envelope>';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'continuation-1';
             List<String> labels = new List<String>{reqType};
            //Test.setContinuationResponse(reqType,httpRes);
            Test.setContinuationResponse(reqType, httpRes);

            try
            {
                EnrollCBISearch_LC_HUM.processResponseCBISSpecialty(labels,httpRes);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            }
        }     
    }
    
    static testMethod void EnrollCBISearchfalse()
    {
        Test.startTest();
        Continuation continueObj = (Continuation)EnrollCBISearch_LC_HUM.searchCBISCode('{"SSN":"1234567890","fName":"","lName":"","sState":""}');
        Test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        list<string> labels=new list<string>();
        labels.add('continuation-1');
       // Test.setContinuationResponse('Hello', httpRes);
        try
        {
            EnrollCBISearch_LC_HUM.processResponseCBISSpecialty(labels,EnrollCBISearch_LC_HUM.contstate);
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }
    } 
    
    static testMethod void EnrollCBISearchfalse1()
    {
        User testUser2 = [Select Id, LastName from User where LastName='test User2'];
        System.runAs(testUser2)
        {
            Test.startTest();
            Continuation continueObj = (Continuation)EnrollCBISearch_LC_HUM.searchCBISCode('{"fName":"WILLIE","lName":"WAITERS","DOB":"04/19/1948","sState":"FL"}');
            Test.stopTest();
            HttpResponse httpRes = new Httpresponse();
            String resBodyString ='';
            httpRes.setBody(resBodyString);
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            try
            { 
                EnrollCBISearch_LC_HUM.CBISCodeProcessResponse(EnrollCBISearch_LC_HUM.contstate);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            } 
        }     
    } 
    
    static testMethod void EnrollCBISearchfalse2()
    {
        User testUser2 = [Select Id, LastName from User where LastName='test User2'];
        System.runAs(testUser2)
        {
            Test.startTest();
            Continuation continueObj = (Continuation)EnrollCBISearch_LC_HUM.searchCBISCode('{"fName":"WILLIE","lName":"WAITERS","DOB":"04/19/1948","sState":"FL"}');
            Test.stopTest();
            HttpResponse httpRes = new Httpresponse();           
            String resBodyString = '<?xml version="1.0"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body><GetPendHistoryTermsDetailsResponse xmlns="http://salesservices.humana.com/SpecialtyBenefits/CRMServices/GetPendHistoryAndTermsDetailsService"><GetPendHistoryTermsDetailsResult xmlns:a="http://schemas.datacontract.org/2004/07/Humana.SpecialtyBenefits.CRMService.Common" xmlns:i="http://www.w3.org/2001/XMLSchema-instance"><a:ResponseItems></a:ResponseItems><a:Status><a:Error><a:ErrorCode>0</a:ErrorCode><a:Message/></a:Error><a:IsSuccesful>true</a:IsSuccesful></a:Status></GetPendHistoryTermsDetailsResult></GetPendHistoryTermsDetailsResponse></s:Body></s:Envelope>';
            httpRes.setBody(resBodyString);
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            
            try
            {       
                EnrollCBISearch_LC_HUM.CBISCodeProcessResponse(EnrollCBISearch_LC_HUM.contstate);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            } 
        }     
    }
     
    //bv
     static testMethod void EnrollCBISearch1()
    {
        User testUser1 = [Select Id, LastName from User where LastName='test User1'];
        EnrollCBISearch_LC_HUM.CBISearchModal cbis = new EnrollCBISearch_LC_HUM.CBISearchModal();
        System.runAs(testUser1)
        {
            Test.startTest();
            Continuation continueObj = (Continuation)EnrollCBISearch_LC_HUM.seachCBISCodeSpeciality('{"sSSN":"","fName":"WILLIE","lName":"WAITERS","DOB":"04/19/1948","sState":"FL"}');            Test.stopTest();
            HttpResponse httpRes = new Httpresponse();
            String resBodyString = '<?xml version="1.0"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body><GetPendHistoryTermsDetailsResponse xmlns="http://salesservices.humana.com/SpecialtyBenefits/CRMServices/GetPendHistoryAndTermsDetailsService"><GetPendHistoryTermsDetailsResult xmlns:a="http://schemas.datacontract.org/2004/07/Humana.SpecialtyBenefits.CRMService.Common" xmlns:i="http://www.w3.org/2001/XMLSchema-instance"><a:ResponseItems><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>Saint Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>253789304</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>10197011</a:MemberId><a:MemberSSN>253789304</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>7273233732</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>03</a:BenId><a:CoverageEndDate>10/1/2007</a:CoverageEndDate><a:CoverageStartDate>1/1/2005</a:CoverageStartDate><a:GroupId>26176</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>Saint Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>253789304</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>513832</a:MemberId><a:MemberSSN>253789304</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>8133233732</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>03</a:BenId><a:CoverageEndDate>1/1/2005</a:CoverageEndDate><a:CoverageStartDate>1/1/1996</a:CoverageStartDate><a:GroupId>6453</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CD5238</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CD5238</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1/><a:Address2/><a:City/><a:State/><a:Zip/><a:ZipPlus/></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>0</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>0</a:MemberId><a:MemberSSN>0</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform/><a:Policy><a:BenId/><a:CoverageEndDate>1/1/0001</a:CoverageEndDate><a:CoverageStartDate>1/1/0001</a:CoverageStartDate><a:GroupId>CP0031</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType/></a:ResponseItem><a:ResponseItem><a:Address><a:Address1>2220 9th Ave N</a:Address1><a:Address2/><a:City>St Petersburg</a:City><a:State>FL</a:State><a:Zip>33713</a:Zip><a:ZipPlus>6831</a:ZipPlus></a:Address><a:DOB>4/19/1948</a:DOB><a:DependentSSN>603113597</a:DependentSSN><a:FirstName>WILLIE</a:FirstName><a:LastName>WAITERS</a:LastName><a:MemberId>21022682</a:MemberId><a:MemberSSN>603113597</a:MemberSSN><a:MiddleInit>J</a:MiddleInit><a:NameSuff/><a:PhoneNumber>0</a:PhoneNumber><a:Platform>CB</a:Platform><a:Policy><a:BenId>60</a:BenId><a:CoverageEndDate>4/1/2013</a:CoverageEndDate><a:CoverageStartDate>8/1/2012</a:CoverageStartDate><a:GroupId>ARRNTL</a:GroupId><a:PendDate>1/1/0001</a:PendDate><a:PendError/><a:PendKey>0</a:PendKey></a:Policy><a:RelationType/><a:SexType>F</a:SexType></a:ResponseItem></a:ResponseItems><a:Status><a:Error><a:ErrorCode>0</a:ErrorCode><a:Message/></a:Error><a:IsSuccesful>true</a:IsSuccesful></a:Status></GetPendHistoryTermsDetailsResult></GetPendHistoryTermsDetailsResponse></s:Body></s:Envelope>';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            HttpResponse httpresponse = new HttpResponse();           
            String abcd = '{"GetPendHistoryTermsDetailsResponse": {"ResponseItem": [{"Address": {"Address1": "3763 MAY STREET", "Address2":"", "City": "Joshua", "State": "TX", "Zip":"76058", "ZipPlus":"4820"},"DOB": "2/24/1988","DependentSSN": "0","FirstName": "LIENTJE","LastName": "BITONTI", "MemberId":"0", "MemberSSN":"477179580", "MiddleInit":"","NameSuff":"","PhoneNumber":"8170058444", "Platform":"CB", "Policy":{"BenId":"03","CoverageEndDate":"1/1/0001","CoverageStartDate":"2/1/2008","GroupId":"","PendDate":"1/1/0001","PendError":"","PendKey":"0"},"RelationType":"01","SexType":"M"},{"Address": {"Address1": "3763 June STREET", "Address2":"", "City": "Garlic", "State": "TX", "Zip":"76055", "ZipPlus":"4821"},"DOB": "2/24/1984","DependentSSN": "0","FirstName": "LIENTJE","LastName": "BITONTI", "MemberId":"0", "MemberSSN":"477179588", "MiddleInit":"J","NameSuff":"Mrs.","PhoneNumber":"8170058555", "Platform":"CB", "Policy":{"BenId":"04","CoverageEndDate":"31/1/2022","CoverageStartDate":"5/1/2010","GroupId":"","PendDate":"1/1/2002","PendError":"","PendKey":"0"},"RelationType":"02","SexType":"F"}]}}';
            httpresponse.setBody(abcd);
            httpresponse.setStatusCode(500);
            EnrollCBISearch_LC_HUM.checkResponseValidity(httpresponse);
            
            boolean isResponseValid = EnrollCBISearch_LC_HUM.checkResponseValidity(httpRes);
            try
            {       
                EnrollCBISearch_LC_HUM.CBISCodeProcessResponse(EnrollCBISearch_LC_HUM.contstate);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            } 
        }     
    }
    
    static testMethod void EnrollCBISearchnewSSN_1(){
         User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
         system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
          
      system.runAs (oCCSUser) 
      { 
          Test.StartTest();
           Continuation con = (Continuation) EnrollCBISearch_LC_HUM.seachCBISCodeSpeciality('{"sSSN":"234234233","fName":"","lName":"","DOB":"","sState":""}');
          Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponseCBISSpecialty';
           objCon.state='data';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"GetPendHistoryTermsDetailsResponse":{ "ResponseItems":{ "ResponseItem":[ { "Address":{ "Address1":"1696 MARTHA STREET", "Address2":"", "City":"Joshua", "State":"TX", "Zip":"76058", "ZipPlus":"" }, "DOB":"2/2/1959", "DependentSSN":"69261856", "FirstName":"REINKE", "LastName":"GUIDREY", "MemberId":"893246979", "MemberSSN":"69261856", "MiddleInit":"D", "NameSuff":"", "PhoneNumber":"8172296000", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"2/1/2008", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"", "SexType":"M" } ] }, "Status":{ "Error":{ "ErrorCode":"0", "Message":"" }, "IsSuccesful":"true" } } }';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(EnrollCBISearch_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{EnrollCBISearch_LC_HUM.continuationLabel};
           EnrollCBISearch_LC_HUM.processResponseCBISSpecialty(labels, EnrollCBISearch_LC_HUM.continuationLabel);
        Test.stopTest();

     }
    }
    
    static testMethod void EnrollCBISearchnewSSN_2(){
         User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
         system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
          
      system.runAs (oCCSUser) 
      { 
          Test.StartTest();
           Continuation con = (Continuation) EnrollCBISearch_LC_HUM.seachCBISCodeSpeciality('{"sSSN":"","fName":"REINKE","lName":"GUIDREY","DOB":"02/02/1959","sState":"Texas"}');
           Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponseCBISSpecialty';
           objCon.state='data';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"GetPendHistoryTermsDetailsResponse":{ "ResponseItems":{ "ResponseItem":[ { "Address":{ "Address1":"1696 MARTHA STREET", "Address2":"", "City":"Joshua", "State":"TX", "Zip":"76058", "ZipPlus":"" }, "DOB":"2/2/1959", "DependentSSN":"69261856", "FirstName":"REINKE", "LastName":"GUIDREY", "MemberId":"893246979", "MemberSSN":"69261856", "MiddleInit":"D", "NameSuff":"", "PhoneNumber":"8172296000", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"2/1/2008", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"", "SexType":"M" } ] }, "Status":{ "Error":{ "ErrorCode":"0", "Message":"" }, "IsSuccesful":"true" } } }';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(EnrollCBISearch_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{EnrollCBISearch_LC_HUM.continuationLabel};
           EnrollCBISearch_LC_HUM.processResponseCBISSpecialty(labels, EnrollCBISearch_LC_HUM.continuationLabel);
        Test.stopTest();
           

    }
    }
    
    
    static testMethod void EnrollCBISearchnewSSN_3_error(){
         User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
         system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
          
      system.runAs (oCCSUser) 
      { 
          Test.StartTest();
           Continuation con = (Continuation) EnrollCBISearch_LC_HUM.seachCBISCodeSpeciality('{"sSSN":"","fName":"REINKE","lName":"GUIDREY","DOB":"02/02/1959","sState":"Texas"}');
           Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponseCBISSpecialty';
           objCon.state='data';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"GetPendHistoryTermsDetailsResponse":{ "ResponseItems":{ "ResponseItem":[ { "Address":{ "Address1":"1696 MARTHA STREET", "Address2":"", "City":"Joshua", "State":"TX", "Zip":"76058", "ZipPlus":"" }, "DOB":"2/2/1959", "DependentSSN":"69261856", "FirstName":"REINKE", "LastName":"GUIDREY", "MemberId":"893246979", "MemberSSN":"69261856", "MiddleInit":"D", "NameSuff":"", "PhoneNumber":"8172296000", "Platform":"CB", "Policy":{ "BenId":"03", "CoverageEndDate":"1/1/0001", "CoverageStartDate":"2/1/2008", "GroupId":"", "PendDate":"1/1/0001", "PendError":"", "PendKey":"0" }, "RelationType":"", "SexType":"M" } ] }, "Status":{ "Error":{ "ErrorCode":"0", "Message":"" }, "IsSuccesful":"true" } } }';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(EnrollCBISearch_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{EnrollCBISearch_LC_HUM.continuationLabel};
           EnrollCBISearch_LC_HUM.processResponseCBISSpecialty(labels, EnrollCBISearch_LC_HUM.continuationState);
        Test.stopTest();
           

    }
    }
}