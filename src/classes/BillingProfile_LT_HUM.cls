/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfile_LT_HUM
Version          : 1.0
Created Date     : 05/23/2022
Function         : This is controller class for LWC component BillingProfile.
This is used to display billing profiles with latest Data table standards.
Test Class For   : BillingProfile_LC_HUM, BillingService_LS_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sagar Gulleve                                      05/23/2022                 Original Version
* Anuradha Gajbhe                                    03/06/2023                 US#4302387 - Lightning-Phonebook- Secure Payment UI Controls-EBilling (Surge) Genesys impact
************************************************************************************************************************************************/
@isTest
public class BillingProfile_LT_HUM {
    /*
* Method Name : setup
* Description : This method is used to create test data for the class 
* Return type : void
* Parameter   : NA
*/
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {            
            Id grpRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.name = 'test';
            acc.Source_Platform_Code__c ='EM';
            acc.Source_Customer_Key__c = 'XY';
            acc.Recordtypeid = grpRecordTypeId ;
            insert acc;
            system.assertNotEquals(Null,acc.id);
            system.assertequals(grpRecordTypeId ,acc.Recordtypeid);
            
            HUM_Webservice_Callout__c svcHeaders = new HUM_Webservice_Callout__c();
            svcHeaders.Name = 'BillingProfiles';
            svcHeaders.Certificate_Name__c = 'Humana_GuidanceCare_Venus';
            svcHeaders.Timeout__c = '2000';
            svcHeaders.End_Point_URL__c = 'https://int-crmservicews.humana.com/BillingService_V1.0';
            insert svcHeaders;
            system.assertequals('BillingProfiles',svcHeaders.Name );
            
            List<BillingProfileStatus_HUM__c> lstPS = new List<BillingProfileStatus_HUM__c>();
            lstPS.Add(new BillingProfileStatus_HUM__c(Name='All'));
            lstPS.Add(new BillingProfileStatus_HUM__c(Name='Active'));
            lstPS.Add(new BillingProfileStatus_HUM__c(Name='Termed'));
            insert lstPS;
            system.assertequals(3,lstPS.size());
            
            List<BillingNonWebUrls_HUM__c> lstBurls = new List<BillingNonWebUrls_HUM__c>();
            lstBurls.Add(new BillingNonWebUrls_HUM__c(Name='PBSUrl',Url__c='Sample url'));
            lstBurls.Add(new BillingNonWebUrls_HUM__c(Name='OnlineBillingUrl',Url__c='Sample url'));
            insert lstBurls;
            system.assertequals(2,lstBurls.size());
            
            List<Endpoint_Url_HUM__c> lstssnurls = new List<Endpoint_Url_HUM__c>();
            lstssnurls .Add(new Endpoint_Url_HUM__c(Name='SSO_LOGINURL',Remote_Url__c='Sample url'));
            insert lstssnurls;
            system.assertequals(1,lstssnurls.size());
            
            
            Policy__c policy = new Policy__c();
            policy.Platform__c = 'LV';
            policy.Group_Name__c = acc.Id;
            policy.Policy_Group_Number__c='11111';
            insert policy ;
            system.assertequals('LV',policy.Platform__c );
            
            Policy_Member__c objPolMemeber = new Policy_Member__c();
            objPolMemeber.Policy__c = policy.Id;
            objPolMemeber.Source_Coverage_ID__c = '12345';
            insert objPolMemeber;
            system.assertequals('12345',objPolMemeber.Source_Coverage_ID__c );
            
            MemberPlan objMemberPlan = new MemberPlan();
            objMemberPlan.Name='12345';
            objMemberPlan.MemberId=acc.Id;
            objMemberPlan.Policy__c = policy.Id;
            objMemberPlan.Source_Coverage_ID__c = '12345';
            insert objMemberPlan;
            system.assertequals('12345',objMemberPlan.Source_Coverage_ID__c );
            
            HUM_Webservice_Callout__c svcHeaders2 = new HUM_Webservice_Callout__c();
            svcHeaders2.Name = 'BillingServiceV2';
            svcHeaders2.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
            svcHeaders2.Timeout__c = '2000';
            svcHeaders2.Request_Type__c = 'POST';
            svcHeaders2.Content_Type__c ='application/json';
            svcHeaders2.End_Point_URL__c = 'https://int-crmservicews.humana.com/BillingService_V2.0';
            svcHeaders2.Active__c = true;    
            insert svcHeaders2;
            system.assertequals('BillingServiceV2',svcHeaders2.Name );
        }
        
        //REQ-402887 Inserting applauncher in test class
        List<HUMConstants__c> listOfConstantToInsert = new List<HUMConstants__c>();
        listOfConstantToInsert.add(new HUMConstants__c(Name = 'APPLAUNCHER_ENVIRONMENT', StringValue__c = 'TEST', IntegerValue__c = 0));
        insert listOfConstantToInsert;
    }
    
    /*
* Method Name : testService
* Description : This method is used to test the main service output.
* Return type : void
* Parameter   : NA
*/
    static testMethod void testService()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid,Source_Platform_Code__c,Source_Customer_Key__c  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            
            BillingProfile_LC_HUM.sProfileStatus = 'All';
            BillingProfile_LC_HUM.sAkaName='abc';
            BillingProfile_LC_HUM.sRowNumber='123';
            BillingProfile_LC_HUM.sBillingProfileJson='xyz';
            BillingProfile_LC_HUM.sTermDate='20/07/2005';
            BillingProfile_LC_HUM.sObjName='Account';
            BillingProfile_LC_HUM.sGroupName='';                         
            MemberPlan objMemberPlan = [Select Id from MemberPlan limit 1]; 
            Continuation continueObj = (Continuation)BillingProfile_LC_HUM.getBillingProfiles(objMemberPlan.Id);
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
            String objres ='{"GetBillingAccountInfoResponse": {"GetBillingAccountInfoResponseList": {"GetBillingAccountInfoResponseDetail": [{ "ResponseStatus": { "ReturnCD": "00", "ReturnMessage": "SUCCESS" }, "SearchCriteria": { "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711", "ProfileSequenceNbr": "0" }, "BillingAccountList": {"BillAccount": [ { "BillingProfile": { "DisplayID": "0766071910252834", "BillingPartyKey": "441711", "DisplayIDPlatformCD": "EM", "PartyKey": "441711", "ProfileSequenceNbr": "1", "ProfileName": "ATLANTIC COAST FEDERAL C", "AccountKey": "441711", "AdminOrganizationID": "0", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "ProfileStatusCD": "A", "ComponentTypeCD": "GI", "ProductTypeCD": "DHM", "ProfileTypeCD": "SB", "BillMethodCD": "NB", "BillLevelCD": "D", "BillBlockCD": "SMDEN", "IndividualBillIndicator": "N", "CustomerAccountBalanceAmt": "16.22", "PaidThroughDate": "2018-05-31T00:00:00", "BillModeCD": "MNTH", "BillFrequencyCD": "MNTH", "AdminDivisionCD": "FL", "NextBillDate": "2018-06-25T00:00:00", "LastBillDate": "2018-06-30T00:00:00", "LastInvoiceDate": "2018-06-01T00:00:00", "OldestOpenInvoiceDate": "0001-01-01T00:00:00", "CommercialOrMedicareIndicator": "C", "GroupOrIndividualBillIndicator": "I", "SelfBillIndicator": "N", "ReconMethodCD": "M", "ReconMethodDate": "0001-01-01T00:00:00", "LagDayCount": "0", "FinancialCompanyCD": "69", "FinancialMarketCD": "0905", "BillingPlatformCD": "EM", "ProrationPremiumCD": "D", "RouteInternalIndicator": "N", "TemplatePartyKey": "912975835", "TemplateProfileNbr": "2" }, "AddressList": {"Address": [ { "Type": "PRIMARY", "Line1": "1811 FAIRFAX DRIVE", "City": "WAYCROSS", "State": "GA", "Zip": "31501", "ZipPlus": "6954" }]}, "BalanceList": {"Balance": [ { "Type": "Current Balance", "Amount": "16.22" }, { "Type": "Current SSA Balance", "Amount": "0.00" }, { "Type": "Current Non-SSA Balance", "Amount": "16.22" }, { "Type": "Past Due", "Amount": "0.00" }, { "Type": "SSA Past Due", "Amount": "0.00" }, { "Type": "Non-SSA Past Due", "Amount": "0.00" }, { "Type": "Adjusted Balance", "Amount": "0.00" }, { "Type": "Initial Premium", "Amount": "0.00" } ]}, "CollectionInfo": { "DelinquentDate": "2018-06-01T00:00:00", "TotalPastDueAmt": "0.00", "TotalNonSsaPastDueAmt": "0.00", "TotalSsaPastDueAmt": "0.00", "CollectionStatusCD": "UN", "NextCollectionActionDate": "0001-01-01T00:00:00", "ProtectUntilDate": "0001-01-01T00:00:00", "ProtectedDate": "0001-01-01T00:00:00", "MaoPercentage": "20.0000", "MaoAmt": "3.24", "ExpectedTermDate": "0001-01-01T00:00:00", "ExpectedTermActionDate": "0001-01-01T00:00:00" }, "RecurringPaymentMethodInfo": { "PaymentOption": "ACH", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "RecurringAmountTypeCD": "ADJD BAL", "NextRecurringDate": "2018-06-10T00:00:00", "EarliestEndDate": "2015-12-31T00:00:00", "PaymentAccount": { "PaymentAccountType": "ACH", "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountNbr": "123456789", "AccountType": "C", "TypeDescription": "ACH", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderAddress": {"Type": "BILLING"} } }, "DebitDay": "10", "DebitDayTypeCd": "POST", "CBSentDate": "0001-01-01T00:00:00", "CBIssuedFromDate": "0001-01-01T00:00:00", "CBIssuedToDate": "0001-01-01T00:00:00" }, "InvoiceList": {"InvoiceSummary": [ { "InvoiceNbr": "441717", "InvoiceDate": "2018-06-01T00:00:00", "CreatedDate": "2018-05-25T00:00:00", "DueDate": "2018-06-01T00:00:00", "Category": "LATEST", "InvoiceType": "INDCOM", "LevelCD": "S", "CycleBeginDate": "2018-06-01T00:00:00", "CycleEndDate": "2018-06-30T00:00:00", "CreatedByRequest": "false", "ExemptFromCollection": "false", "TotalDueAmt": "16.22", "TotalBalanceFwdAmt": "16.22", "TotalCharges": "16.22", "TotalMemberAdjustments": "0.00", "TotalPayments": "-16.22", "TotalOtherAdjustments": "0.00", "TotalWellnessIncentive": "0.00", "VBSuspPayAmt": "0.00", "VBOverPayAmt": "0.00", "VBPastDueAmt": "0.00", "VBCurrentDueAmt": "0.00", "VBOtherAdjustmentsAmt": "0.00", "VBDueAmt": "0.00", "CoreBalanceForwardAmt": "0.00", "CorePaymentsAppliedAmt": "0.00", "CorePastDueAmt": "0.00", "CoreChargesAmt": "0.00", "CoreMemberAppliedAmt": "0.00", "CoreOtherAdjustmentsAmt": "0.00", "CoreDueAmt": "0.00", "ServiceFeeAmt": "0.00", "CurrentUnpaidBalance": "16.22", "NextLateFeeDate": "2018-06-01T00:00:00", "LateFeeStatusCD": "N", "PaidByAutoDebit": "true" }]}, "TransactionHistoryList": {"Transaction": [ { "TransactionType": "P", "TransactionDate": "2018-05-14T00:00:00", "TransactionDesc": "PCACHWD", "ReceivedDate": "2018-05-25T00:00:00", "CheckNbr": "441744", "TransactionAmt": "16.22", "PaymentAccount": { "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderName": "NATTAWAT RODENBURG", "CardHolderAddress": {} } } }]}, "PremiumInfo": { "Premium": "16.22", "AdditionalSupplementalCharges": "0.00", "PenaltyAmt": "0.00", "SubsidyAmt": "0.00", "Fees": "0.00", "OtherAmt": "0.00", "DiscountAmt": "0.00", "NetPremium": "16.22" }, "PayorList": {"Payor": [ { "Type": "PRIMARY", "PersonID": "0766071910252834", "FirstName": "NATTAWAT", "LastName": "MIENE" }]} }]} }]}}}';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
            BillingProfile_LC_HUM.sRequest = jsonRequest;
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfile_LDTO_HUM objResponseDTO = new BillingProfile_LDTO_HUM();
            objResponseDTO=(BillingProfile_LDTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfile_LDTO_HUM.class);             
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            for(String s : mapDetails.keyset())
            {
                reqType = s;
                labelGetmember.add(s);
            } 
            
            Test.setContinuationResponse(reqType,resBilling);
            BillingService_LS_HUM sbilling = new BillingService_LS_HUM();
            Billing_Wrapper_LDTO_HUM dataTransfer=new Billing_Wrapper_LDTO_HUM(acc.Id, '', 'Account', 'LV', 'PEM1795');     
            
            BillingProfile_LC_HUM.parseBillingServiceResponse(labelGetmember,dataTransfer);
            
            oUser.Network_User_Id__c = '';
            update oUser;
            System.assertEquals('',oUser.Network_User_Id__c );
            
            BillingProfile_LC_HUM obj = new BillingProfile_LC_HUM();
            obj.getThis();       
            String surl = BillingProfile_LC_HUM.getHSS_ONECLICK_URL();
            System.assertEquals('Sample url',surl);
            BillingProfile_LC_HUM.getsPBSUrl();
            BillingProfile_LC_HUM.getsOnlineBillingUrl();
            BillingProfile_LC_HUM.getsEnvironment();
            BillingProfile_LC_HUM.getsPBSNew();
            BillingProfile_LC_HUM.getsPBSNewComments();
            BillingProfile_LC_HUM.getSObjName(null);
	    BillingProfile_LC_HUM.getBillingProfiles(acc.id);
	    BillingProfile_LC_HUM.getSecurePayFlagDetails(acc.id);
        }  
    }
    
    
    /*
* Method Name : testService
* Description : This method is used to test the main service output.
* Return type : void
* Parameter   : NA
*/
    static testMethod void NegativeTestService1()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid,Source_Platform_Code__c,Source_Customer_Key__c  from Account where name = 'test'];
            System.assertEquals('test',acc.name);            
            BillingProfile_LC_HUM.sProfileStatus = 'All';
            BillingProfile_LC_HUM.sAkaName='abc';
            BillingProfile_LC_HUM.sRowNumber='123';
            BillingProfile_LC_HUM.sBillingProfileJson='xyz';
            BillingProfile_LC_HUM.sTermDate='20/07/2005';
            BillingProfile_LC_HUM.sObjName='Account';
            BillingProfile_LC_HUM.sGroupName='';
            MemberPlan objMemberPlan = [Select Id from MemberPlan limit 1];
            Continuation continueObj = (Continuation)BillingProfile_LC_HUM.getBillingProfiles(objMemberPlan.Id);
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
            String objres ='{ 	"GetBillingAccountInfoResponse": { 		"GetBillingAccountInfoResponseList": { 			"GetBillingAccountInfoResponseDetail": [{ 				"ResponseStatus": { 					"ReturnCD": "00", 					"ReturnMessage": "SUCCESS" 				}, 				"SearchCriteria": { 					"PlatformCD": "EM", 					"IDType": "ACCOUNT", 					"SearchID": "000441711", 					"ProfileSequenceNbr": "0" 				}, 				"BillingAccountList": { 					"BillAccount": [{ 						"BillingProfile": { 							"DisplayID": "0766071910252834", 							"BillingPartyKey": "441711", 							"DisplayIDPlatformCD": "EM", 							"PartyKey": "441711", 							"ProfileSequenceNbr": "1", 							"ProfileName": "ATLANTIC COAST FEDERAL C", 							"AccountKey": "441711", 							"AdminOrganizationID": "0", 							"EffectiveDate": "2017-09-01T00:00:00", 							"EndDate": "9999-12-31T00:00:00", 							"ProfileStatusCD": "A", 							"ComponentTypeCD": "GI", 							"ProductTypeCD": "DHM", 							"ProfileTypeCD": "SB", 							"BillMethodCD": "NB", 							"BillLevelCD": "D", 							"BillBlockCD": "SMDEN", 							"IndividualBillIndicator": "N", 							"CustomerAccountBalanceAmt": "16.22", 							"PaidThroughDate": "0TestDate", 							"BillModeCD": "MNTH", 							"BillFrequencyCD": "MNTH", 							"AdminDivisionCD": "FL", 							"NextBillDate": "2018-06-25T00:00:00", 							"LastBillDate": "2018-06-30T00:00:00", 							"LastInvoiceDate": "2018-06-01T00:00:00", 							"OldestOpenInvoiceDate": "0001-01-01T00:00:00", 							"CommercialOrMedicareIndicator": "C", 							"GroupOrIndividualBillIndicator": "I", 							"SelfBillIndicator": "N", 							"ReconMethodCD": "M", 							"ReconMethodDate": "0001-01-01T00:00:00", 							"LagDayCount": "0", 							"FinancialCompanyCD": "69", 							"FinancialMarketCD": "0905", 							"BillingPlatformCD": "EM", 							"ProrationPremiumCD": "D", 							"RouteInternalIndicator": "N", 							"TemplatePartyKey": "912975835", 							"TemplateProfileNbr": "2" 						}, 						"AddressList": { 							"Address": [{ 								"Type": "PRIMARY", 								"Line1": "1811 FAIRFAX DRIVE", 								"City": "WAYCROSS", 								"State": "GA", 								"Zip": "31501", 								"ZipPlus": "6954" 							}] 						}, 						"BalanceList": { 							"Balance": [{ 								"Type": "Current Balance", 								"Amount": "16.22" 							}, { 								"Type": "Current SSA Balance", 								"Amount": "0.00" 							}, { 								"Type": "Current Non-SSA Balance", 								"Amount": "16.22" 							}, { 								"Type": "Past Due", 								"Amount": "0.00" 							}, { 								"Type": "SSA Past Due", 								"Amount": "0.00" 							}, { 								"Type": "Non-SSA Past Due", 								"Amount": "0.00" 							}, { 								"Type": "Adjusted Balance", 								"Amount": "0.00" 							}, { 								"Type": "Initial Premium", 								"Amount": "0.00" 							}] 						}, 						"CollectionInfo": { 							"DelinquentDate": "2018-06-01T00:00:00", 							"TotalPastDueAmt": "0.00", 							"TotalNonSsaPastDueAmt": "0.00", 							"TotalSsaPastDueAmt": "0.00", 							"CollectionStatusCD": "UN", 							"NextCollectionActionDate": "0001-01-01T00:00:00", 							"ProtectUntilDate": "0001-01-01T00:00:00", 							"ProtectedDate": "0001-01-01T00:00:00", 							"MaoPercentage": "20.0000", 							"MaoAmt": "3.24", 							"ExpectedTermDate": "0001-01-01T00:00:00", 							"ExpectedTermActionDate": "0001-01-01T00:00:00" 						}, 						"RecurringPaymentMethodInfo": { 							"PaymentOption": "ACH", 							"EffectiveDate": "2017-09-01T00:00:00", 							"EndDate": "9999-12-31T00:00:00", 							"RecurringAmountTypeCD": "ADJD BAL", 							"NextRecurringDate": "recDate", 							"EarliestEndDate": "2015-12-31T00:00:00", 							"PaymentAccount": { 								"PaymentAccountType": "ACH", 								"AccountSequenceNbr": "1", 								"BankAccount": { 									"BankName": "TEST BANK", 									"TransitNbr": "000000000", 									"AccountNbr": "123456789", 									"AccountType": "C", 									"TypeDescription": "ACH", 									"AccountHolderName": "NATTAWAT RODENBURG" 								}, 								"CreditCard": { 									"ExpiryDate": "0001-01-01T00:00:00", 									"CardHolderAddress": { 										"Type": "BILLING" 									} 								} 							}, 							"DebitDay": "10", 							"DebitDayTypeCd": "POST", 							"CBSentDate": "0001-01-01T00:00:00", 							"CBIssuedFromDate": "0001-01-01T00:00:00", 							"CBIssuedToDate": "0001-01-01T00:00:00" 						}, 						"InvoiceList": { 							"InvoiceSummary": [{ 								"InvoiceNbr": "441717", 								"InvoiceDate": "2018-06-01T00:00:00", 								"CreatedDate": "2018-05-25T00:00:00", 								"DueDate": "2018-06-01T00:00:00", 								"Category": "LATEST", 								"InvoiceType": "INDCOM", 								"LevelCD": "S", 								"CycleBeginDate": "2018-06-01T00:00:00", 								"CycleEndDate": "2018-06-30T00:00:00", 								"CreatedByRequest": "false", 								"ExemptFromCollection": "false", 								"TotalDueAmt": "16.22", 								"TotalBalanceFwdAmt": "16.22", 								"TotalCharges": "16.22", 								"TotalMemberAdjustments": "0.00", 								"TotalPayments": "-16.22", 								"TotalOtherAdjustments": "0.00", 								"TotalWellnessIncentive": "0.00", 								"VBSuspPayAmt": "0.00", 								"VBOverPayAmt": "0.00", 								"VBPastDueAmt": "0.00", 								"VBCurrentDueAmt": "0.00", 								"VBOtherAdjustmentsAmt": "0.00", 								"VBDueAmt": "0.00", 								"CoreBalanceForwardAmt": "0.00", 								"CorePaymentsAppliedAmt": "0.00", 								"CorePastDueAmt": "0.00", 								"CoreChargesAmt": "0.00", 								"CoreMemberAppliedAmt": "0.00", 								"CoreOtherAdjustmentsAmt": "0.00", 								"CoreDueAmt": "0.00", 								"ServiceFeeAmt": "0.00", 								"CurrentUnpaidBalance": "16.22", 								"NextLateFeeDate": "2018-06-01T00:00:00", 								"LateFeeStatusCD": "N", 								"PaidByAutoDebit": "true" 							}] 						}, 						"TransactionHistoryList": { 							"TransactionDetails": [{ 								"TransactionType": "P", 								"TransactionDate": "2018-05-14T00:00:00", 								"TransactionDesc": "PCACHWD", 								"ReceivedDate": "2018-05-25T00:00:00", 								"CheckNbr": "441744", 								"TransactionAmt": "16.22", 								"PaymentAccount": { 									"AccountSequenceNbr": "1", 									"BankAccount": { 										"BankName": "TEST BANK", 										"TransitNbr": "000000000", 										"AccountHolderName": "NATTAWAT RODENBURG" 									}, 									"CreditCard": { 										"ExpiryDate": "0001-01-01T00:00:00", 										"CardHolderName": "NATTAWAT RODENBURG", 										"CardHolderAddress": {} 									} 								} 							}] 						}, 						"PremiumInfo": { 							"Premium": "16.22", 							"AdditionalSupplementalCharges": "0.00", 							"PenaltyAmt": "0.00", 							"SubsidyAmt": "0.00", 							"Fees": "0.00", 							"OtherAmt": "0.00", 							"DiscountAmt": "0.00", 							"NetPremium": "16.22" 						}, 						"PayorList": { 							"Payor": [{ 								"Type": "PRIMARY", 								"PersonID": "0766071910252834", 								"FirstName": "NATTAWAT", 								"LastName": "MIENE" 							}] 						}  					}] 				} 			}] 		} 	} }';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
            BillingProfile_LC_HUM.sRequest = jsonRequest;
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfile_LDTO_HUM objResponseDTO = new BillingProfile_LDTO_HUM();
            objResponseDTO=(BillingProfile_LDTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfile_LDTO_HUM.class);             
            objResponseDTO.compareTo(objResponseDTO);
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            for(String s : mapDetails.keyset())
            {
                reqType = s;
                labelGetmember.add(s);
            } 
            
            Test.setContinuationResponse(reqType,resBilling);
            BillingService_LS_HUM sbilling = new BillingService_LS_HUM();
            Billing_Wrapper_LDTO_HUM dataTransfer=new Billing_Wrapper_LDTO_HUM(acc.Id, '', 'Account', 'LV', 'PEM1795');     
            BillingProfile_LC_HUM.parseBillingServiceResponse(labelGetmember,dataTransfer);
            
            oUser.Network_User_Id__c = '';
            update oUser;
            System.assertEquals('',oUser.Network_User_Id__c );
            
            BillingProfile_LC_HUM obj = new BillingProfile_LC_HUM();
            obj.getThis();       
            String surl = BillingProfile_LC_HUM.getHSS_ONECLICK_URL();
            System.assertEquals('Sample url',surl);
            BillingProfile_LC_HUM.getsPBSUrl();
            BillingProfile_LC_HUM.getsOnlineBillingUrl();
            BillingProfile_LC_HUM.getsEnvironment();
            BillingProfile_LC_HUM.getsPBSNew();
            BillingProfile_LC_HUM.getsPBSNewComments();
            BillingProfile_LC_HUM.getSObjName(null);
        }  
    }
    
    
    /*
* Method Name : NegativetestService
* Description : This method is used to test the main service output.
* Return type : void
* Parameter   : NA
*/
    static testMethod void NegativetestService()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid,Source_Platform_Code__c,Source_Customer_Key__c  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            BillingProfile_LC_HUM.sProfileStatus = 'All';
            BillingProfile_LC_HUM.sAkaName='abc';
            BillingProfile_LC_HUM.sRowNumber='123';
            BillingProfile_LC_HUM.sBillingProfileJson='xyz';
            BillingProfile_LC_HUM.sTermDate='20/07/2005';
            BillingProfile_LC_HUM.sObjName='Account';
            BillingProfile_LC_HUM.sGroupName='';
            MemberPlan objMemberPlan = [Select Id from MemberPlan limit 1];   
            
            Continuation continueObj = (Continuation)BillingProfile_LC_HUM.getBillingProfiles(objMemberPlan.Id);
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
            String objres ='';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
            BillingProfile_LC_HUM.sRequest = jsonRequest;
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfile_LDTO_HUM objResponseDTO = new BillingProfile_LDTO_HUM();
            if (objres != ''){
              objResponseDTO=(BillingProfile_LDTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfile_LDTO_HUM.class);             
            }    
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            for(String s : mapDetails.keyset())
            {
                reqType = s;
                labelGetmember.add(s);
            } 
            
            Test.setContinuationResponse(reqType,resBilling);
            BillingService_LS_HUM sbilling = new BillingService_LS_HUM();
            Billing_Wrapper_LDTO_HUM dataTransfer=new Billing_Wrapper_LDTO_HUM(acc.Id, '', 'Account', 'LV', 'PEM1795');     
            BillingProfile_LC_HUM.parseBillingServiceResponse(labelGetmember,dataTransfer);
            
            
        }  
    }
    /*
* Method Name : testPolicyMember
* Description : This method is used to test the main service output.
* Return type : void
* Parameter   : NA
*/
    static testMethod void testPolicyMember()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            MemberPlan objPolcyMem = [select id,Name, Subscriber.Name, subscriberId,Source_Coverage_ID__c ,Policy_Platform__c from MemberPlan where Source_Coverage_ID__c = '12345'];
            System.assertEquals('12345',objPolcyMem.Source_Coverage_ID__c );
            BillingService_S_HUM s = new BillingService_S_HUM();
            BillingProfile_LC_HUM.sProfileStatus = 'All';
            BillingProfile_LC_HUM.sAkaName='abc';
            BillingProfile_LC_HUM.sRowNumber='123';
            BillingProfile_LC_HUM.sBillingProfileJson='xyz';
            BillingProfile_LC_HUM.sTermDate='20/07/2005';
            BillingProfile_LC_HUM.sSubscriberIDBase='123456';
            BillingProfile_LC_HUM.sMemberIdBase='123456';
            BillingProfile_LC_HUM.sSectionHeader = 'Member Billing';
            BillingProfile_LC_HUM.sRecId = objPolcyMem.id;
            BillingProfile_LC_HUM.sObjName='MemberPlan';
            
            Continuation continueObj = (Continuation)BillingProfile_LC_HUM.getBillingProfiles(objPolcyMem.id);
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
            String objres ='{"GetBillingAccountInfoResponse": {"GetBillingAccountInfoResponseList": {"GetBillingAccountInfoResponseDetail": [{ "ResponseStatus": { "ReturnCD": "00", "ReturnMessage": "SUCCESS" }, "SearchCriteria": { "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711", "ProfileSequenceNbr": "0" }, "BillingAccountList": {"BillAccount": [ { "BillingProfile": { "DisplayID": "0766071910252834", "BillingPartyKey": "441711", "DisplayIDPlatformCD": "EM", "PartyKey": "441711", "ProfileSequenceNbr": "1", "ProfileName": "ATLANTIC COAST FEDERAL C", "AccountKey": "441711", "AdminOrganizationID": "0", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "ProfileStatusCD": "A", "ComponentTypeCD": "GI", "ProductTypeCD": "DHM", "ProfileTypeCD": "SB", "BillMethodCD": "NB", "BillLevelCD": "D", "BillBlockCD": "SMDEN", "IndividualBillIndicator": "N", "CustomerAccountBalanceAmt": "16.22", "PaidThroughDate": "2018-05-31T00:00:00", "BillModeCD": "MNTH", "BillFrequencyCD": "MNTH", "AdminDivisionCD": "FL", "NextBillDate": "2018-06-25T00:00:00", "LastBillDate": "2018-06-30T00:00:00", "LastInvoiceDate": "2018-06-01T00:00:00", "OldestOpenInvoiceDate": "0001-01-01T00:00:00", "CommercialOrMedicareIndicator": "C", "GroupOrIndividualBillIndicator": "I", "SelfBillIndicator": "N", "ReconMethodCD": "M", "ReconMethodDate": "0001-01-01T00:00:00", "LagDayCount": "0", "FinancialCompanyCD": "69", "FinancialMarketCD": "0905", "BillingPlatformCD": "EM", "ProrationPremiumCD": "D", "RouteInternalIndicator": "N", "TemplatePartyKey": "912975835", "TemplateProfileNbr": "2" }, "AddressList": {"Address": [ { "Type": "PRIMARY", "Line1": "1811 FAIRFAX DRIVE", "City": "WAYCROSS", "State": "GA", "Zip": "31501", "ZipPlus": "6954" }]}, "BalanceList": {"Balance": [ { "Type": "Current Balance", "Amount": "16.22" }, { "Type": "Current SSA Balance", "Amount": "0.00" }, { "Type": "Current Non-SSA Balance", "Amount": "16.22" }, { "Type": "Past Due", "Amount": "0.00" }, { "Type": "SSA Past Due", "Amount": "0.00" }, { "Type": "Non-SSA Past Due", "Amount": "0.00" }, { "Type": "Adjusted Balance", "Amount": "0.00" }, { "Type": "Initial Premium", "Amount": "0.00" } ]}, "CollectionInfo": { "DelinquentDate": "2018-06-01T00:00:00", "TotalPastDueAmt": "0.00", "TotalNonSsaPastDueAmt": "0.00", "TotalSsaPastDueAmt": "0.00", "CollectionStatusCD": "UN", "NextCollectionActionDate": "0001-01-01T00:00:00", "ProtectUntilDate": "0001-01-01T00:00:00", "ProtectedDate": "0001-01-01T00:00:00", "MaoPercentage": "20.0000", "MaoAmt": "3.24", "ExpectedTermDate": "0001-01-01T00:00:00", "ExpectedTermActionDate": "0001-01-01T00:00:00" }, "RecurringPaymentMethodInfo": { "PaymentOption": "ACH", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "RecurringAmountTypeCD": "ADJD BAL", "NextRecurringDate": "2018-06-10T00:00:00", "EarliestEndDate": "2015-12-31T00:00:00", "PaymentAccount": { "PaymentAccountType": "ACH", "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountNbr": "123456789", "AccountType": "C", "TypeDescription": "ACH", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderAddress": {"Type": "BILLING"} } }, "DebitDay": "10", "DebitDayTypeCd": "POST", "CBSentDate": "0001-01-01T00:00:00", "CBIssuedFromDate": "0001-01-01T00:00:00", "CBIssuedToDate": "0001-01-01T00:00:00" }, "InvoiceList": {"InvoiceSummary": [ { "InvoiceNbr": "441717", "InvoiceDate": "2018-06-01T00:00:00", "CreatedDate": "2018-05-25T00:00:00", "DueDate": "2018-06-01T00:00:00", "Category": "LATEST", "InvoiceType": "INDCOM", "LevelCD": "S", "CycleBeginDate": "2018-06-01T00:00:00", "CycleEndDate": "2018-06-30T00:00:00", "CreatedByRequest": "false", "ExemptFromCollection": "false", "TotalDueAmt": "16.22", "TotalBalanceFwdAmt": "16.22", "TotalCharges": "16.22", "TotalMemberAdjustments": "0.00", "TotalPayments": "-16.22", "TotalOtherAdjustments": "0.00", "TotalWellnessIncentive": "0.00", "VBSuspPayAmt": "0.00", "VBOverPayAmt": "0.00", "VBPastDueAmt": "0.00", "VBCurrentDueAmt": "0.00", "VBOtherAdjustmentsAmt": "0.00", "VBDueAmt": "0.00", "CoreBalanceForwardAmt": "0.00", "CorePaymentsAppliedAmt": "0.00", "CorePastDueAmt": "0.00", "CoreChargesAmt": "0.00", "CoreMemberAppliedAmt": "0.00", "CoreOtherAdjustmentsAmt": "0.00", "CoreDueAmt": "0.00", "ServiceFeeAmt": "0.00", "CurrentUnpaidBalance": "16.22", "NextLateFeeDate": "2018-06-01T00:00:00", "LateFeeStatusCD": "N", "PaidByAutoDebit": "true" }]}, "TransactionHistoryList": {"Transaction": [ { "TransactionType": "P", "TransactionDate": "2018-05-14T00:00:00", "TransactionDesc": "PCACHWD", "ReceivedDate": "2018-05-25T00:00:00", "CheckNbr": "441744", "TransactionAmt": "16.22", "PaymentAccount": { "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderName": "NATTAWAT RODENBURG", "CardHolderAddress": {} } } }]}, "PremiumInfo": { "Premium": "16.22", "AdditionalSupplementalCharges": "0.00", "PenaltyAmt": "0.00", "SubsidyAmt": "0.00", "Fees": "0.00", "OtherAmt": "0.00", "DiscountAmt": "0.00", "NetPremium": "16.22" }, "PayorList": {"Payor": [ { "Type": "PRIMARY", "PersonID": "0766071910252834", "FirstName": "NATTAWAT", "LastName": "MIENE" }]} }]} }]}}}';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
            BillingProfile_LC_HUM.sRequest = jsonRequest;
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfile_LDTO_HUM objResponseDTO = new BillingProfile_LDTO_HUM();
            objResponseDTO=(BillingProfile_LDTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfile_LDTO_HUM.class);             
            Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
            string reqType = '';
            List<string>labelGetmember = new List<string>();
            for(String p : mapDetails.keyset())
            {
                reqType = p;
                labelGetmember.add(p);
            } 
            
            Test.setContinuationResponse(reqType,resBilling);
            BillingService_LS_HUM sbilling = new BillingService_LS_HUM();
            Billing_Wrapper_LDTO_HUM dataTransfer=new Billing_Wrapper_LDTO_HUM(objPolcyMem.Id, '', 'MemberPlan', 'LV', 'PEM1795');     
            BillingProfile_LC_HUM.parseBillingServiceResponse(labelGetmember,dataTransfer);
            
            oUser.Network_User_Id__c = '';
            update oUser;
            System.assertEquals('',oUser.Network_User_Id__c );                        
            
            String surl = BillingProfile_LC_HUM.getHSS_ONECLICK_URL();
            System.assertEquals('Sample url',surl);
            BillingProfile_LC_HUM obj = new BillingProfile_LC_HUM();
            obj.getThis();       
            
            BillingProfile_LC_HUM.getsPBSUrl();
            BillingProfile_LC_HUM.getsOnlineBillingUrl();
            BillingProfile_LC_HUM.getsEnvironment();
            BillingProfile_LC_HUM.getsPBSNew();
            BillingProfile_LC_HUM.getsPBSNewComments();
        }  
    }
}