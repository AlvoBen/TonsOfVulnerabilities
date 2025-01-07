/*******************************************************************************************************************************
Apex Class Name : H1MarketEnrollSearch_LC_HUM
Version         : 1.0
Created On      : 04/01/2021
Function        : This class is used for cover H1MarketPlaceEnrollSearch_LC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.1           Ritik Agarwal                                        04/01/2021              original version
*    1.2           Santhi Mandava                                       11/16/2022              US3482496_TestClass issue fix
*******************************************************************************************************************************************************************/
@isTest
public class H1MarketEnrollSearch_LT_HUM {
	@testSetup static void setUpTestData(){
        HUM_Webservice_Callout__c cs = new HUM_Webservice_Callout__c();
        cs.Name = 'H1ExchangeService';
        cs.Certificate_Name__c = 'H1ExchangeService';
        cs.Active__c = true;
        cs.End_Point_URL__c ='https://int-crmservicews.humana.com/EPSServiceREST_V1.0/EnterpriseProviderSearch';
        cs.Timeout__c = '12000';
        cs.Request_Type__c = 'POST';
        cs.Content_Type__c ='application/json'; 
        insert cs;
        HUM_Webservice_Callout__c cs2 = new HUM_Webservice_Callout__c();
        cs2.Name = 'OHBPService';
        cs2.Certificate_Name__c = 'H1ExchangeService';
        cs2.Active__c = true;
        cs2.End_Point_URL__c ='https://int-crmservicews.humana.com/EPSServiceREST_V1.0/EnterpriseProviderSearch';
        cs2.Timeout__c = '12000';
        cs2.Request_Type__c = 'POST';
        cs2.Content_Type__c ='application/json'; 
        insert cs2;

        List<HUMConstants__c> lstConstantsNew = new List<HUMConstants__c>();
        lstConstantsNew.Add(new HUMConstants__c(Name='ENROLLMENT_H1',StringValue__c='UKM|00|H1|'));
        insert lstConstantsNew;
        system.assert(lstConstantsNew !=NULL);
    }
    
    static testMethod void OHBPSearch(){
        test.startTest();
        Continuation con = (Continuation)H1MarketEnrollSearch_LC_HUM.searchOHBPService('{"sSSN":"274965160","sEndDate":"11/23/2020","sEffectiveDate":"03/23/1929"}');
		test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
       String resBodyString = '<Return><Members><Member><FirstName>Sillily</FirstName><MiddleInitial></MiddleInitial><LastName>Callous</LastName><DateOfBirth>1982-09-01T00:00:00</DateOfBirth>'+
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
        httpRes.setBody(resBodyString);
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        H1MarketEnrollSearch_LC_HUM.OHBPServiceProcessResponse(H1MarketEnrollSearch_LC_HUM.contstate);
    }
    
    static testMethod void OHBPProcessing(){
         String resBodyString = '<Return><Members><Member><FirstName>Sillily</FirstName><MiddleInitial></MiddleInitial><LastName>Callous</LastName><DateOfBirth>1982-09-01T00:00:00</DateOfBirth>'+
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
       H1MarketEnrollSearch_LC_HUM.OHBPProcessResponse(resBodyString);
    }
    
     static testMethod void H1MarketEnrollSearch(){
        test.startTest();
        Continuation con = (Continuation)H1MarketEnrollSearch_LC_HUM.searchH1Service('{"sSSN":"274965160","sEndDate":"11/23/2020","sEffectiveDate":"03/23/1929"}');
		test.stopTest();
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        H1MarketEnrollSearch_LC_HUM.H1ServiceProcessResponse(H1MarketEnrollSearch_LC_HUM.contstate);
    }
    
    static testMethod void H1MarketResponseParse(){
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
           H1MarketEnrollSearch_LC_HUM.ProcessResponse(respElement,'123456789');
    }

}