/*******************************************************************************************************************************************************
Apex Class Name  : BillingDetails_LC_T_HUM
Version          : 1.0
Created Date     : 06/09/2022
Function         : Test class for BillingDetails_LC_HUM
                    This is used to display billing details.
*****************************************************************************************************************************************************
Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj patil            		                    06/09/2022                 Original Version
**********************************************************************************************************************************************/
@isTest
public class BillingDetails_LC_T_HUM {
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
            BillingTestDataSetup_LT_HUM.createTestData();
        }
    }
    
      /*
    * Method Name : testServiceNegative
    * Description : This method is used to test the main service output.
    * Return type : void
    * Parameter   : NA
    */
    
  static testMethod void testServiceNegative()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid,AccountNumber  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            BillingDetails_LC_HUM objDetls = new  BillingDetails_LC_HUM();
            
            Continuation continueObj = (Continuation)BillingDetails_LC_HUM.getProfileDetails('LV',oUser.Network_User_Id__c,'000441711');
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
           String objres ='{"GetBillingAccountInfoResponse": {"GetBillingAccountInfoResponseList": {"GetBillingAccountInfoResponseDetail": [{ "ResponseStatus": { "ReturnCD": "00", "ReturnMessage": "SUCCESS" }, "SearchCriteria": { "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711", "ProfileSequenceNbr": "0" }, "BillingAccountList": {"BillAccount": [ { "BillingProfile": { "DisplayID": "0766071910252834", "BillingPartyKey": "441711", "DisplayIDPlatformCD": "EM", "PartyKey": "441711", "ProfileSequenceNbr": "1", "ProfileName": "ATLANTIC COAST FEDERAL C", "AccountKey": "441711", "AdminOrganizationID": "0", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "ProfileStatusCD": "A", "ComponentTypeCD": "GI", "ProductTypeCD": "DHM", "ProfileTypeCD": "SB", "BillMethodCD": "NB", "BillLevelCD": "D", "BillBlockCD": "SMDEN", "IndividualBillIndicator": "N", "CustomerAccountBalanceAmt": "16.22", "PaidThroughDate": "2018-05-31T00:00:00", "BillModeCD": "MNTH", "BillFrequencyCD": "MNTH", "AdminDivisionCD": "FL", "NextBillDate": "2018-06-25T00:00:00", "LastBillDate": "2018-06-30T00:00:00", "LastInvoiceDate": "2018-06-01T00:00:00", "OldestOpenInvoiceDate": "0001-01-01T00:00:00", "CommercialOrMedicareIndicator": "C", "GroupOrIndividualBillIndicator": "I", "SelfBillIndicator": "N", "ReconMethodCD": "M", "ReconMethodDate": "0001-01-01T00:00:00", "LagDayCount": "0", "FinancialCompanyCD": "69", "FinancialMarketCD": "0905", "BillingPlatformCD": "EM", "ProrationPremiumCD": "D", "RouteInternalIndicator": "N", "TemplatePartyKey": "912975835", "TemplateProfileNbr": "2" }, "AddressList": {"Address": [ { "Type": "PRIMARY", "Line1": "1811 FAIRFAX DRIVE", "City": "WAYCROSS", "State": "GA", "Zip": "31501", "ZipPlus": "6954" }]}, "BalanceList": {"Balance": [ { "Type": "Current Balance", "Amount": "16.22" }, { "Type": "Current SSA Balance", "Amount": "0.00" }, { "Type": "Current Non-SSA Balance", "Amount": "16.22" }, { "Type": "Past Due", "Amount": "0.00" }, { "Type": "SSA Past Due", "Amount": "0.00" }, { "Type": "Non-SSA Past Due", "Amount": "0.00" }, { "Type": "Adjusted Balance", "Amount": "0.00" }, { "Type": "Initial Premium", "Amount": "0.00" } ]}, "CollectionInfo": { "DelinquentDate": "2018-06-01T00:00:00", "TotalPastDueAmt": "0.00", "TotalNonSsaPastDueAmt": "0.00", "TotalSsaPastDueAmt": "0.00", "CollectionStatusCD": "UN", "NextCollectionActionDate": "0001-01-01T00:00:00", "ProtectUntilDate": "0001-01-01T00:00:00", "ProtectedDate": "0001-01-01T00:00:00", "MaoPercentage": "20.0000", "MaoAmt": "3.24", "ExpectedTermDate": "0001-01-01T00:00:00", "ExpectedTermActionDate": "0001-01-01T00:00:00" }, "RecurringPaymentMethodInfo": { "PaymentOption": "ACH", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "RecurringAmountTypeCD": "ADJD BAL", "NextRecurringDate": "2018-06-10T00:00:00", "EarliestEndDate": "2015-12-31T00:00:00", "PaymentAccount": { "PaymentAccountType": "ACH", "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountNbr": "123456789", "AccountType": "C", "TypeDescription": "ACH", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderAddress": {"Type": "BILLING"} } }, "DebitDay": "10", "DebitDayTypeCd": "POST", "CBSentDate": "0001-01-01T00:00:00", "CBIssuedFromDate": "0001-01-01T00:00:00", "CBIssuedToDate": "0001-01-01T00:00:00" }, "InvoiceList": {"InvoiceSummary": [ { "InvoiceNbr": "441717", "InvoiceDate": "2018-06-01T00:00:00", "CreatedDate": "2018-05-25T00:00:00", "DueDate": "2018-06-01T00:00:00", "Category": "LATEST", "InvoiceType": "INDCOM", "LevelCD": "S", "CycleBeginDate": "2018-06-01T00:00:00", "CycleEndDate": "2018-06-30T00:00:00", "CreatedByRequest": "false", "ExemptFromCollection": "false", "TotalDueAmt": "16.22", "TotalBalanceFwdAmt": "16.22", "TotalCharges": "16.22", "TotalMemberAdjustments": "0.00", "TotalPayments": "-16.22", "TotalOtherAdjustments": "0.00", "TotalWellnessIncentive": "0.00", "VBSuspPayAmt": "0.00", "VBOverPayAmt": "0.00", "VBPastDueAmt": "0.00", "VBCurrentDueAmt": "0.00", "VBOtherAdjustmentsAmt": "0.00", "VBDueAmt": "0.00", "CoreBalanceForwardAmt": "0.00", "CorePaymentsAppliedAmt": "0.00", "CorePastDueAmt": "0.00", "CoreChargesAmt": "0.00", "CoreMemberAppliedAmt": "0.00", "CoreOtherAdjustmentsAmt": "0.00", "CoreDueAmt": "0.00", "ServiceFeeAmt": "0.00", "CurrentUnpaidBalance": "16.22", "NextLateFeeDate": "2018-06-01T00:00:00", "LateFeeStatusCD": "N", "PaidByAutoDebit": "true" }]}, "TransactionHistoryList": {"Transaction": [ { "TransactionType": "P", "TransactionDate": "2018-05-14T00:00:00", "TransactionDesc": "PCACHWD", "ReceivedDate": "2018-05-25T00:00:00", "CheckNbr": "441744", "TransactionAmt": "16.22", "PaymentAccount": { "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderName": "NATTAWAT RODENBURG", "CardHolderAddress": {} } } }]}, "PremiumInfo": { "Premium": "16.22", "AdditionalSupplementalCharges": "0.00", "PenaltyAmt": "0.00", "SubsidyAmt": "0.00", "Fees": "0.00", "OtherAmt": "0.00", "DiscountAmt": "0.00", "NetPremium": "16.22" }, "PayorList": {"Payor": [ { "Type": "PRIMARY", "PersonID": "0766071910252834", "FirstName": "NATTAWAT", "LastName": "MIENE" }]} }]} }]}}}';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "Invoi  BillingDetails_LC_HUM.sRequest = jsonRequest';
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfileResponse_LDTO_HUM objResponseDTO = new BillingProfileResponse_LDTO_HUM();
            objResponseDTO=(BillingProfileResponse_LDTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfileResponse_LDTO_HUM.class);            
            Test.setContinuationResponse(BillingDetails_LC_HUM.sRequest,resBilling);
            List<String>lst = new List<String>();
            lst.add(BillingDetails_LC_HUM.sRequest);
            BillingDetails_LC_HUM.parseBillingServiceResponse(lst,'State');
            
         }
    }
    
    
     /*
    * Method Name : testServicePositiveCont
    * Description : This method is used to test the main service output.
    * Return type : void
    * Parameter   : NA
    */
    
    
    static testMethod void testServicePositiveCont()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid,AccountNumber  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            BillingDetails_LC_HUM objDetls = new  BillingDetails_LC_HUM();
            
            Continuation continueObj = (Continuation)BillingDetails_LC_HUM.getProfileDetails('LV',oUser.Network_User_Id__c,'000441711');
            continueObj.continuationMethod = 'parseBillingServiceResponse';
            HttpResponse resBilling = new Httpresponse(); 
            String objres ='{"GetBillingAccountInfoResponse": {"GetBillingAccountInfoResponseList": {"GetBillingAccountInfoResponseDetail": [{ "ResponseStatus": { "ReturnCD": "00", "ReturnMessage": "SUCCESS" }, "SearchCriteria": { "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711", "ProfileSequenceNbr": "0" }, "BillingAccountList": {"BillAccount": [ { "BillingProfile": { "DisplayID": "0766071910252834", "BillingPartyKey": "441711", "DisplayIDPlatformCD": "EM", "PartyKey": "441711", "ProfileSequenceNbr": "1", "ProfileName": "ATLANTIC COAST FEDERAL C", "AccountKey": "441711", "AdminOrganizationID": "0", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "ProfileStatusCD": "A", "ComponentTypeCD": "GI", "ProductTypeCD": "DHM", "ProfileTypeCD": "SB", "BillMethodCD": "NB", "BillLevelCD": "D", "BillBlockCD": "SMDEN", "IndividualBillIndicator": "N", "CustomerAccountBalanceAmt": "16.22", "PaidThroughDate": "2018-05-31T00:00:00", "BillModeCD": "MNTH", "BillFrequencyCD": "MNTH", "AdminDivisionCD": "FL", "NextBillDate": "2018-06-25T00:00:00", "LastBillDate": "2018-06-30T00:00:00", "LastInvoiceDate": "2018-06-01T00:00:00", "OldestOpenInvoiceDate": "0001-01-01T00:00:00", "CommercialOrMedicareIndicator": "C", "GroupOrIndividualBillIndicator": "I", "SelfBillIndicator": "N", "ReconMethodCD": "M", "ReconMethodDate": "0001-01-01T00:00:00", "LagDayCount": "0", "FinancialCompanyCD": "69", "FinancialMarketCD": "0905", "BillingPlatformCD": "EM", "ProrationPremiumCD": "D", "RouteInternalIndicator": "N", "TemplatePartyKey": "912975835", "TemplateProfileNbr": "2" }, "AddressList": {"Address": [ { "Type": "PRIMARY", "Line1": "1811 FAIRFAX DRIVE", "City": "WAYCROSS", "State": "GA", "Zip": "31501", "ZipPlus": "6954" }]}, "BalanceList": {"Balance": [ { "Type": "Current Balance", "Amount": "16.22" }, { "Type": "Current SSA Balance", "Amount": "0.00" }, { "Type": "Current Non-SSA Balance", "Amount": "16.22" }, { "Type": "Past Due", "Amount": "0.00" }, { "Type": "SSA Past Due", "Amount": "0.00" }, { "Type": "Non-SSA Past Due", "Amount": "0.00" }, { "Type": "Adjusted Balance", "Amount": "0.00" }, { "Type": "Initial Premium", "Amount": "0.00" } ]}, "CollectionInfo": { "DelinquentDate": "2018-06-01T00:00:00", "TotalPastDueAmt": "0.00", "TotalNonSsaPastDueAmt": "0.00", "TotalSsaPastDueAmt": "0.00", "CollectionStatusCD": "UN", "NextCollectionActionDate": "0001-01-01T00:00:00", "ProtectUntilDate": "0001-01-01T00:00:00", "ProtectedDate": "0001-01-01T00:00:00", "MaoPercentage": "20.0000", "MaoAmt": "3.24", "ExpectedTermDate": "0001-01-01T00:00:00", "ExpectedTermActionDate": "0001-01-01T00:00:00" }, "RecurringPaymentMethodInfo": { "PaymentOption": "ACH", "EffectiveDate": "2017-09-01T00:00:00", "EndDate": "9999-12-31T00:00:00", "RecurringAmountTypeCD": "ADJD BAL", "NextRecurringDate": "2018-06-10T00:00:00", "EarliestEndDate": "2015-12-31T00:00:00", "PaymentAccount": { "PaymentAccountType": "ACH", "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountNbr": "123456789", "AccountType": "C", "TypeDescription": "ACH", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderAddress": {"Type": "BILLING"} } }, "DebitDay": "10", "DebitDayTypeCd": "POST", "CBSentDate": "0001-01-01T00:00:00", "CBIssuedFromDate": "0001-01-01T00:00:00", "CBIssuedToDate": "0001-01-01T00:00:00" }, "InvoiceList": {"InvoiceSummary": [ { "InvoiceNbr": "441717", "InvoiceDate": "2018-06-01T00:00:00", "CreatedDate": "2018-05-25T00:00:00", "DueDate": "2018-06-01T00:00:00", "Category": "LATEST", "InvoiceType": "INDCOM", "LevelCD": "S", "CycleBeginDate": "2018-06-01T00:00:00", "CycleEndDate": "2018-06-30T00:00:00", "CreatedByRequest": "false", "ExemptFromCollection": "false", "TotalDueAmt": "16.22", "TotalBalanceFwdAmt": "16.22", "TotalCharges": "16.22", "TotalMemberAdjustments": "0.00", "TotalPayments": "-16.22", "TotalOtherAdjustments": "0.00", "TotalWellnessIncentive": "0.00", "VBSuspPayAmt": "0.00", "VBOverPayAmt": "0.00", "VBPastDueAmt": "0.00", "VBCurrentDueAmt": "0.00", "VBOtherAdjustmentsAmt": "0.00", "VBDueAmt": "0.00", "CoreBalanceForwardAmt": "0.00", "CorePaymentsAppliedAmt": "0.00", "CorePastDueAmt": "0.00", "CoreChargesAmt": "0.00", "CoreMemberAppliedAmt": "0.00", "CoreOtherAdjustmentsAmt": "0.00", "CoreDueAmt": "0.00", "ServiceFeeAmt": "0.00", "CurrentUnpaidBalance": "16.22", "NextLateFeeDate": "2018-06-01T00:00:00", "LateFeeStatusCD": "N", "PaidByAutoDebit": "true" }]}, "TransactionHistoryList": {"Transaction": [ { "TransactionType": "P", "TransactionDate": "2018-05-14T00:00:00", "TransactionDesc": "PCACHWD", "ReceivedDate": "2018-05-25T00:00:00", "CheckNbr": "441744", "TransactionAmt": "16.22", "PaymentAccount": { "AccountSequenceNbr": "1", "BankAccount": { "BankName": "TEST BANK", "TransitNbr": "000000000", "AccountHolderName": "NATTAWAT RODENBURG" }, "CreditCard": { "ExpiryDate": "0001-01-01T00:00:00", "CardHolderName": "NATTAWAT RODENBURG", "CardHolderAddress": {} } } }]}, "PremiumInfo": { "Premium": "16.22", "AdditionalSupplementalCharges": "0.00", "PenaltyAmt": "0.00", "SubsidyAmt": "0.00", "Fees": "0.00", "OtherAmt": "0.00", "DiscountAmt": "0.00", "NetPremium": "16.22" }, "PayorList": {"Payor": [ { "Type": "PRIMARY", "PersonID": "0766071910252834", "FirstName": "NATTAWAT", "LastName": "MIENE" }]} }]} }]}}}';
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "Invoi BillingDetails_LC_HUM.sRequest = jsonRequest';
            resBilling.setBody(objres);
            resBilling.setStatusCode(200);
            system.assert(resBilling.getBody() == objres);
            BillingProfileResponse_LDTO_HUM objResponseDTO = new BillingProfileResponse_LDTO_HUM();
            objResponseDTO=(BillingProfileResponse_LDTO_HUM)System.JSON.deserialize(resBilling.getbody(), BillingProfileResponse_LDTO_HUM.class);            
            Test.setContinuationResponse(BillingDetails_LC_HUM.sRequest,resBilling);
            List<String>lst = new List<String>();
            lst.add(BillingDetails_LC_HUM.sRequest);
            
            BillingDetails_LC_HUM.parseBillingServiceResponse(lst,'State');
            
         }
    }
    
    
     /*
    * Method Name : testService
    * Description : This method is used to test the main service output.
    * Return type : void
    * Parameter   : NA
    */ 
    
    static testMethod void testService()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid,AccountNumber  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            
            BillingDetails_LC_HUM objDetls = new BillingDetails_LC_HUM();
            
            BillingDetails_LC_HUM.getProfileDetails('LV',oUser.Network_User_Id__c,'000441711');

            objDetls.objResponse = new BillingProfileResponse_LDTO_HUM();                    
            List<String>lst = new List<String>();
            lst.add(BillingDetails_LC_HUM.sRequest);
            BillingDetails_LC_HUM.parseBillingServiceResponse(lst,'State');
           // System.assertEquals(false,BillingDetails_LC_HUM.isProfileDetailsVisible );
            
            List<SelectOption> lstStatus = BillingProfileDetails_H_HUM.getProfileStatusValues();
            System.assertequals(3,lstStatus.size());
            
            List<BillingProfile_LDTO_HUM> lstValues = BillingTestDataSetup_LT_HUM.createBillingWrapperList();
            List<BillingProfile_LDTO_HUM> lstTemp = BillingProfileDetails_LH_HUM.Search('All','',lstValues );
            System.assertEquals(lstValues.size(),lstTemp.size());
            
            lstTemp  = BillingProfileDetails_LH_HUM.Search('Active','',lstValues );
            System.assertEquals(4,lstTemp.size());
            
            lstTemp = BillingProfileDetails_LH_HUM.Search('All','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp .size());
            
            lstTemp = BillingProfileDetails_LH_HUM.Search('Active','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp.size());
            
            lstTemp = BillingProfileDetails_LH_HUM.Search('Termed','12/31/2014',lstValues );
            System.assertEquals(0,lstTemp.size());
            
            List<SelectOption> listSelect = BillingProfileDetailsV2_H_HUM.getProfileStatusValues();
            
            
        }
    }
     /*
    * Method Name : testService
    * Description : This method is used to test the request service and response.
    * Return type : void
    * Parameter   : NA
    */ 
    static testMethod void testServiceResponse()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);
                           
        
        System.runAs(oUser)
        {
             Test.startTest(); 
            
                String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
                BillingProfileRequest_LDTO_HUM objRequest= new BillingProfileRequest_LDTO_HUM();
                objRequest= (BillingProfileRequest_LDTO_HUM)System.JSON.deserialize(jsonRequest, BillingProfileRequest_LDTO_HUM.class);                                  
                BillingService_LS_HUM objBillingProfile = new BillingService_LS_HUM();
                
                BillingProfileResponse_LDTO_HUM.TransactionDetails tranDet = new BillingProfileResponse_LDTO_HUM.TransactionDetails();
                List<BillingProfileResponse_LDTO_HUM.TransactionDetails> lsttranDet = new List<BillingProfileResponse_LDTO_HUM.TransactionDetails>();
                    tranDet.TransactionDate ='2018-05-14T00:00:00';
                    tranDet.TransactionAmt = '16.22';
                    tranDet.CheckNbr = '441714';
                    tranDet.TransactionDesc = 'P';
                    tranDet.ReceivedDate = '2018-05-14T00:00:00';
                    lsttranDet.add(tranDet);
                    BillingProfile_LDTO_HUM objBillingDTO = new BillingProfile_LDTO_HUM();
                    objBillingDTO=objBillingProfile.getTransactionDetails(lsttranDet,objBillingDTO);
               Test.stopTest();
            
        }
    }
     /*
    * Method Name : testServiceResponseNegative
    * Description : This method is used to test the request service and response.
    * Return type : void
    * Parameter   : NA
    */ 
     static testMethod void testServiceResponseNegative()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU1', oUser.Current_Queue__c);  
        System.runAs(oUser)
        {
            Test.startTest(); 
            String jsonRequest ='{ "GetBillingAccountInfoRequest": { "GetBillingAccountInfoRequestCriteria": { "RequestContext": { "CallingSystemID": "CRMS", "UserID": "SXM6622", "UserIDType": "INTL", "ProcessDate": "2015-12-01" }, "SearchCriteria": { "SearchCriteria": [{ "PlatformCD": "EM", "IDType": "ACCOUNT", "SearchID": "000441711" }] }, "IncludeCriteria": { "PaymentOption": "true", "PayorInfo": "true", "CollectionInfo": "true", "BillingAddressInfo": "true", "BillingMode": "true", "BillingFrequency": "true", "BalanceInfo": "true", "InvoiceInfo": { "IncludeInvoiceInformation": "true", "NbrOfInvoices": "1" }, "PremiumInfo": { "IncludePremiumInformation": "true", "RateLevel": "Account" }, "TransactionInfo": "true", "TransactionType": "P", "NbrOfTransactions": "1" } } } } ';
            BillingProfileRequest_LDTO_HUM objRequest= new BillingProfileRequest_LDTO_HUM();
            objRequest= (BillingProfileRequest_LDTO_HUM)System.JSON.deserialize(jsonRequest, BillingProfileRequest_LDTO_HUM.class);                                  
            BillingService_LS_HUM objBillingProfile = new BillingService_LS_HUM();
            HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Active__c,Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c where name='BillingServiceV2' Limit 1];
            objBillingProfile.builtHttpRequest(oSvcHeaderPayment );
            BillingProfile_LDTO_HUM objWrapper1 = BillingProfileDetails_LH_HUM.getBillingProfilesInput('PXK','LV','12345','ACCOUNT','ACCOUNT');
            objBillingProfile.RequestStructureBillingService(objWrapper1);
            List<BillingProfile_LDTO_HUM> lstValues = BillingTestDataSetup_LT_HUM.createBillingWrapperList();
            objBillingProfile.getActiveProfile(lstValues);
            objBillingProfile.getActiveProfile(null);
            objBillingProfile.formatDate(null);
            objBillingProfile.getProfileDetails(null,null);
            List<BillingProfile_LDTO_HUM> lstValues1 = new List<BillingProfile_LDTO_HUM>();
            BillingProfile_LDTO_HUM objWrapper =  new BillingProfile_LDTO_HUM();
            objWrapper.sUserID = 'SXS4444';
            objWrapper.sPlatformCode = 'EM';
            objWrapper.sIDType ='1';
            objWrapper.sSearchID ='2';
            objWrapper.sGroupPIDNumber = '12345';
            objWrapper.iProfileNumber = 1;
            objWrapper.iAccountNumber = 1234;
            objWrapper.sProfileAccountName = 'Global corporation';
            objWrapper.sEffectiveDate = '12/22/2013';
            objWrapper.sEndDate = '12/22/2014';
            objWrapper.sStatus = 'Active';
            objWrapper.sTermReasonCode = 'TC0033';
            objWrapper.sAllocationDate = '11/12/2012';
            objWrapper.iCheckNumber = 12334;
            objWrapper.sNextActionDate = '11/12/2012';
            lstValues1.add(objWrapper);
            objBillingProfile.getActiveProfile(lstValues1);
            Test.stopTest();
        }
    }
}