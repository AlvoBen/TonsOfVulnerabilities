/*******************************************************************************************************************************
Apex Class Name : BillingProfile_V2_T_HUM
Version         : 1.0
Created On      : 08/25/2016
Function        : Test class for BillingProfile_V2_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi         36948                   08/25/2016                 Original version
* Santhi Mandava               40488                   09/19/2016                 Increased Code coverage
* Yogesh Gupta                                         07/19/2019                 Req-402887 Applauncher hum constant inserting
* Sumit Pharande                                       11/05/2019                 Billing Service SOAP to Rest conversion
* Prasanthi Kandula                                    07/26/2021                 US 2484356 T1PRJ0036776_MF_3SF : Member Billing Profile Page - Profile Selection  
*******************************************************************************************************************************/

@isTest
private class BillingProfile_V2_T_HUM
{
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
            BillingTestDataSetup_T_HUM.createTestData();
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
            
            BillingProfile_V2_C_HUM objProf = new BillingProfile_V2_C_HUM();
            objProf.sProfileStatus = 'All';
            objProf.sAkaName='abc';
            objProf.sRowNumber='123';
            objProf.sBillingProfileJson='xyz';
            objProf.sTermDate='20/07/2005';
            
            PageReference pageRef = Page.GroupBillingProfile_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            ApexPages.StandardController oStdController = new ApexPages.StandardController(acc);
            
            GroupBillingProfile_C_HUM objMain =  new GroupBillingProfile_C_HUM(oStdController);
            system.assert(objMain != Null);
            objProf.sRecId = acc.id;
            objProf.oPageController = objMain;
            objProf.initiateBillingDetails();
            System.assertEquals('Account',objProf.sObjName);
                          
               
            Continuation continueObj = (Continuation)objProf.getBillingProfiles();
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
            String objres ='{"GetBillingAccountInfoResponse": {"GetBillingAccountInfoResponseList": {"GetBillingAccountInfoResponseDetail": [{ "ResponseStatus": { "ReturnCD": "00", "ReturnMessage": "SUCCESS" }, "SearchCriteria": { "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711", "ProfileSequenceNbr": "0" }, "BillingAccountList": {"BillAccount": [ { "BillingProfile": { "DisplayID": "0766071910252834", "BillingPartyKey": "441711", "DisplayIDPlatformCD": "EM", "PartyKey": "441711", "ProfileSequenceNbr": "1", "ProfileName": "ATLANTIC COAST FEDERAL C", "AccountKey": "441711", "AdminOrganizationID": "0", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "ProfileStatusCD": "A", "ComponentTypeCD": "GI", "ProductTypeCD": "DHM", "ProfileTypeCD": "SB", "BillMethodCD": "NB", "BillLevelCD": "D", "BillBlockCD": "SMDEN", "IndividualBillIndicator": "N", "CustomerAccountBalanceAmt": "16.22", "PaidThroughDate": "2018-05-31T00:00:00", "BillModeCD": "MNTH", "BillFrequencyCD": "MNTH", "AdminDivisionCD": "FL", "NextBillDate": "2018-06-25T00:00:00", "LastBillDate": "2018-06-30T00:00:00", "LastInvoiceDate": "2018-06-01T00:00:00", "OldestOpenInvoiceDate": "0001-01-01T00:00:00", "CommercialOrMedicareIndicator": "C", "GroupOrIndividualBillIndicator": "I", "SelfBillIndicator": "N", "ReconMethodCD": "M", "ReconMethodDate": "0001-01-01T00:00:00", "LagDayCount": "0", "FinancialCompanyCD": "69", "FinancialMarketCD": "0905", "BillingPlatformCD": "EM", "ProrationPremiumCD": "D", "RouteInternalIndicator": "N", "TemplatePartyKey": "912975835", "TemplateProfileNbr": "2" }, "AddressList": {"Address": [ { "Type": "PRIMARY", "Line1": "1811 FAIRFAX DRIVE", "City": "WAYCROSS", "State": "GA", "Zip": "31501", "ZipPlus": "6954" }]}, "BalanceList": {"Balance": [ { "Type": "Current Balance", "Amount": "16.22" }, { "Type": "Current SSA Balance", "Amount": "0.00" }, { "Type": "Current Non-SSA Balance", "Amount": "16.22" }, { "Type": "Past Due", "Amount": "0.00" }, { "Type": "SSA Past Due", "Amount": "0.00" }, { "Type": "Non-SSA Past Due", "Amount": "0.00" }, { "Type": "Adjusted Balance", "Amount": "0.00" }, { "Type": "Initial Premium", "Amount": "0.00" } ]}, "CollectionInfo": { "DelinquentDate": "2018-06-01T00:00:00", "TotalPastDueAmt": "0.00", "TotalNonSsaPastDueAmt": "0.00", "TotalSsaPastDueAmt": "0.00", "CollectionStatusCD": "UN", "NextCollectionActionDate": "0001-01-01T00:00:00", "ProtectUntilDate": "0001-01-01T00:00:00", "ProtectedDate": "0001-01-01T00:00:00", "MaoPercentage": "20.0000", "MaoAmt": "3.24", "ExpectedTermDate": "0001-01-01T00:00:00", "ExpectedTermActionDate": "0001-01-01T00:00:00" }, "RecurringPaymentMethodInfo": { "PaymentOption": "ACH", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "RecurringAmountTypeCD": "ADJD BAL", "NextRecurringDate": "2018-06-10T00:00:00", "EarliestEndDate": "2015-12-31T00:00:00", "PaymentAccount": { "PaymentAccountType": "ACH", "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountNbr": "123456789", "AccountType": "C", "TypeDescription": "ACH", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderAddress": {"Type": "BILLING"} } }, "DebitDay": "10", "DebitDayTypeCd": "POST", "CBSentDate": "0001-01-01T00:00:00", "CBIssuedFromDate": "0001-01-01T00:00:00", "CBIssuedToDate": "0001-01-01T00:00:00" }, "InvoiceList": {"InvoiceSummary": [ { "InvoiceNbr": "441717", "InvoiceDate": "2018-06-01T00:00:00", "CreatedDate": "2018-05-25T00:00:00", "DueDate": "2018-06-01T00:00:00", "Category": "LATEST", "InvoiceType": "INDCOM", "LevelCD": "S", "CycleBeginDate": "2018-06-01T00:00:00", "CycleEndDate": "2018-06-30T00:00:00", "CreatedByRequest": "false", "ExemptFromCollection": "false", "TotalDueAmt": "16.22", "TotalBalanceFwdAmt": "16.22", "TotalCharges": "16.22", "TotalMemberAdjustments": "0.00", "TotalPayments": "-16.22", "TotalOtherAdjustments": "0.00", "TotalWellnessIncentive": "0.00", "VBSuspPayAmt": "0.00", "VBOverPayAmt": "0.00", "VBPastDueAmt": "0.00", "VBCurrentDueAmt": "0.00", "VBOtherAdjustmentsAmt": "0.00", "VBDueAmt": "0.00", "CoreBalanceForwardAmt": "0.00", "CorePaymentsAppliedAmt": "0.00", "CorePastDueAmt": "0.00", "CoreChargesAmt": "0.00", "CoreMemberAppliedAmt": "0.00", "CoreOtherAdjustmentsAmt": "0.00", "CoreDueAmt": "0.00", "ServiceFeeAmt": "0.00", "CurrentUnpaidBalance": "16.22", "NextLateFeeDate": "2018-06-01T00:00:00", "LateFeeStatusCD": "N", "PaidByAutoDebit": "true" }]}, "TransactionHistoryList": {"Transaction": [ { "TransactionType": "P", "TransactionDate": "2018-05-14T00:00:00", "TransactionDesc": "PCACHWD", "ReceivedDate": "2018-05-25T00:00:00", "CheckNbr": "441744", "TransactionAmt": "16.22", "PaymentAccount": { "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderName": "NATTAWAT RODENBURG", "CardHolderAddress": {} } } }]}, "PremiumInfo": { "Premium": "16.22", "AdditionalSupplementalCharges": "0.00", "PenaltyAmt": "0.00", "SubsidyAmt": "0.00", "Fees": "0.00", "OtherAmt": "0.00", "DiscountAmt": "0.00", "NetPremium": "16.22" }, "PayorList": {"Payor": [ { "Type": "PRIMARY", "PersonID": "0766071910252834", "FirstName": "NATTAWAT", "LastName": "MIENE" }]} }]} }]}}}';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
            objProf.sRequest = jsonRequest;
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfileResponseV2_DTO_HUM objResponseDTO = new BillingProfileResponseV2_DTO_HUM();
            objResponseDTO=(BillingProfileResponseV2_DTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfileResponseV2_DTO_HUM.class);             
            Test.setContinuationResponse(objProf.sRequest,resBilling);
            objProf.parseBillingServiceResponse();
            System.assertEquals(true,objProf.bIsDataAvailable );
                    
            objProf.displayErrorMessage(true,'');
            System.assertEquals(false,objProf.bIsDataAvailable);
            
            objProf.displayErrorMessage(false,'');
            System.assertEquals(true,objProf.bIsDataAvailable);
            
            oUser.Network_User_Id__c = '';
            update oUser;
            System.assertEquals('',oUser.Network_User_Id__c );
                         
            BillingProfile_V2_C_HUM obj = objProf.getThis();
                       
            String surl = objProf.getHSS_ONECLICK_URL();
            System.assertEquals('Sample url',surl);
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
            Policy_Member__c objPolcyMem = [select id,Name, Subscriber__r.Name, subscriber__c,Source_Coverage_ID__c ,Policy_Platform__c from Policy_Member__c where Source_Coverage_ID__c = '12345'];
            System.assertEquals('12345',objPolcyMem.Source_Coverage_ID__c );
            BillingService_S_HUM s = new BillingService_S_HUM();
            BillingProfile_V2_C_HUM objProf = new BillingProfile_V2_C_HUM();
            objProf.sProfileStatus = 'All';
            objProf.sAkaName='abc';
            objProf.sRowNumber='123';
            objProf.sBillingProfileJson='xyz';
            objProf.sTermDate='20/07/2005';
            objProf.sSubscriberIDBase='123456';
            objProf.sMemberIdBase='123456';
            objProf.sSectionHeader = 'Member Billing';
            
            PageReference pageRef = Page.MemberBillingProfile_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.CurrentPage().getParameters().put('id',objPolcyMem.id);
            ApexPages.StandardController oStdController1 = new ApexPages.StandardController(objPolcyMem);
            MemberBillingProfile_C_HUM obj1 = new MemberBillingProfile_C_HUM(oStdController1);
            
            objProf.sRecId = objPolcyMem.id;
            objProf.oPageController = obj1;
            objProf.initiateBillingDetails();
               
            Continuation continueObj = (Continuation)objProf.getBillingProfiles();
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
            String objres ='{"GetBillingAccountInfoResponse": {"GetBillingAccountInfoResponseList": {"GetBillingAccountInfoResponseDetail": [{ "ResponseStatus": { "ReturnCD": "00", "ReturnMessage": "SUCCESS" }, "SearchCriteria": { "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711", "ProfileSequenceNbr": "0" }, "BillingAccountList": {"BillAccount": [ { "BillingProfile": { "DisplayID": "0766071910252834", "BillingPartyKey": "441711", "DisplayIDPlatformCD": "EM", "PartyKey": "441711", "ProfileSequenceNbr": "1", "ProfileName": "ATLANTIC COAST FEDERAL C", "AccountKey": "441711", "AdminOrganizationID": "0", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "ProfileStatusCD": "A", "ComponentTypeCD": "GI", "ProductTypeCD": "DHM", "ProfileTypeCD": "SB", "BillMethodCD": "NB", "BillLevelCD": "D", "BillBlockCD": "SMDEN", "IndividualBillIndicator": "N", "CustomerAccountBalanceAmt": "16.22", "PaidThroughDate": "2018-05-31T00:00:00", "BillModeCD": "MNTH", "BillFrequencyCD": "MNTH", "AdminDivisionCD": "FL", "NextBillDate": "2018-06-25T00:00:00", "LastBillDate": "2018-06-30T00:00:00", "LastInvoiceDate": "2018-06-01T00:00:00", "OldestOpenInvoiceDate": "0001-01-01T00:00:00", "CommercialOrMedicareIndicator": "C", "GroupOrIndividualBillIndicator": "I", "SelfBillIndicator": "N", "ReconMethodCD": "M", "ReconMethodDate": "0001-01-01T00:00:00", "LagDayCount": "0", "FinancialCompanyCD": "69", "FinancialMarketCD": "0905", "BillingPlatformCD": "EM", "ProrationPremiumCD": "D", "RouteInternalIndicator": "N", "TemplatePartyKey": "912975835", "TemplateProfileNbr": "2" }, "AddressList": {"Address": [ { "Type": "PRIMARY", "Line1": "1811 FAIRFAX DRIVE", "City": "WAYCROSS", "State": "GA", "Zip": "31501", "ZipPlus": "6954" }]}, "BalanceList": {"Balance": [ { "Type": "Current Balance", "Amount": "16.22" }, { "Type": "Current SSA Balance", "Amount": "0.00" }, { "Type": "Current Non-SSA Balance", "Amount": "16.22" }, { "Type": "Past Due", "Amount": "0.00" }, { "Type": "SSA Past Due", "Amount": "0.00" }, { "Type": "Non-SSA Past Due", "Amount": "0.00" }, { "Type": "Adjusted Balance", "Amount": "0.00" }, { "Type": "Initial Premium", "Amount": "0.00" } ]}, "CollectionInfo": { "DelinquentDate": "2018-06-01T00:00:00", "TotalPastDueAmt": "0.00", "TotalNonSsaPastDueAmt": "0.00", "TotalSsaPastDueAmt": "0.00", "CollectionStatusCD": "UN", "NextCollectionActionDate": "0001-01-01T00:00:00", "ProtectUntilDate": "0001-01-01T00:00:00", "ProtectedDate": "0001-01-01T00:00:00", "MaoPercentage": "20.0000", "MaoAmt": "3.24", "ExpectedTermDate": "0001-01-01T00:00:00", "ExpectedTermActionDate": "0001-01-01T00:00:00" }, "RecurringPaymentMethodInfo": { "PaymentOption": "ACH", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "RecurringAmountTypeCD": "ADJD BAL", "NextRecurringDate": "2018-06-10T00:00:00", "EarliestEndDate": "2015-12-31T00:00:00", "PaymentAccount": { "PaymentAccountType": "ACH", "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountNbr": "123456789", "AccountType": "C", "TypeDescription": "ACH", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderAddress": {"Type": "BILLING"} } }, "DebitDay": "10", "DebitDayTypeCd": "POST", "CBSentDate": "0001-01-01T00:00:00", "CBIssuedFromDate": "0001-01-01T00:00:00", "CBIssuedToDate": "0001-01-01T00:00:00" }, "InvoiceList": {"InvoiceSummary": [ { "InvoiceNbr": "441717", "InvoiceDate": "2018-06-01T00:00:00", "CreatedDate": "2018-05-25T00:00:00", "DueDate": "2018-06-01T00:00:00", "Category": "LATEST", "InvoiceType": "INDCOM", "LevelCD": "S", "CycleBeginDate": "2018-06-01T00:00:00", "CycleEndDate": "2018-06-30T00:00:00", "CreatedByRequest": "false", "ExemptFromCollection": "false", "TotalDueAmt": "16.22", "TotalBalanceFwdAmt": "16.22", "TotalCharges": "16.22", "TotalMemberAdjustments": "0.00", "TotalPayments": "-16.22", "TotalOtherAdjustments": "0.00", "TotalWellnessIncentive": "0.00", "VBSuspPayAmt": "0.00", "VBOverPayAmt": "0.00", "VBPastDueAmt": "0.00", "VBCurrentDueAmt": "0.00", "VBOtherAdjustmentsAmt": "0.00", "VBDueAmt": "0.00", "CoreBalanceForwardAmt": "0.00", "CorePaymentsAppliedAmt": "0.00", "CorePastDueAmt": "0.00", "CoreChargesAmt": "0.00", "CoreMemberAppliedAmt": "0.00", "CoreOtherAdjustmentsAmt": "0.00", "CoreDueAmt": "0.00", "ServiceFeeAmt": "0.00", "CurrentUnpaidBalance": "16.22", "NextLateFeeDate": "2018-06-01T00:00:00", "LateFeeStatusCD": "N", "PaidByAutoDebit": "true" }]}, "TransactionHistoryList": {"Transaction": [ { "TransactionType": "P", "TransactionDate": "2018-05-14T00:00:00", "TransactionDesc": "PCACHWD", "ReceivedDate": "2018-05-25T00:00:00", "CheckNbr": "441744", "TransactionAmt": "16.22", "PaymentAccount": { "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderName": "NATTAWAT RODENBURG", "CardHolderAddress": {} } } }]}, "PremiumInfo": { "Premium": "16.22", "AdditionalSupplementalCharges": "0.00", "PenaltyAmt": "0.00", "SubsidyAmt": "0.00", "Fees": "0.00", "OtherAmt": "0.00", "DiscountAmt": "0.00", "NetPremium": "16.22" }, "PayorList": {"Payor": [ { "Type": "PRIMARY", "PersonID": "0766071910252834", "FirstName": "NATTAWAT", "LastName": "MIENE" }]} }]} }]}}}';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
            objProf.sRequest = jsonRequest;
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfileResponseV2_DTO_HUM objResponseDTO = new BillingProfileResponseV2_DTO_HUM();
            objResponseDTO=(BillingProfileResponseV2_DTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfileResponseV2_DTO_HUM.class);             
            Test.setContinuationResponse(objProf.sRequest,resBilling);
            objProf.parseBillingServiceResponse();
            System.assertEquals(true,objProf.bIsDataAvailable );
                    
            objProf.displayErrorMessage(true,'');
            System.assertEquals(false,objProf.bIsDataAvailable);
            
            objProf.displayErrorMessage(false,'');
            System.assertEquals(true,objProf.bIsDataAvailable);
            
            oUser.Network_User_Id__c = '';
            update oUser;
            System.assertEquals('',oUser.Network_User_Id__c );
                         
            BillingProfile_V2_C_HUM obj2 = objProf.getThis();
                       
            String surl = objProf.getHSS_ONECLICK_URL();
            System.assertEquals('Sample url',surl);
        }  
    }
   
}