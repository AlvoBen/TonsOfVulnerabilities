/******************************************************************************************************************
Apex Class Name :   BillingProfile_LDTO_LT_HUM 
Version: 1.0
Function           : Test Class to Cover BillingProfile_LDTO_LT_HUM Class
Created On         : 05/23/2022

Modification Log:  
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0               Sagar Gulleve                                    05/23/2022           Original Version 

------------------------------------------------------------------------------------------------------------------*/
@isTest
Private Class BillingProfile_LDTO_LT_HUM {
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
    }
    /*
    * Method Name : testBillingProfileDTO
    * Description : This method used To Test DTO For Billing Profile
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testBillingProfileDTO()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.StartTest();
            BillingProfileRequest_LDTO_HUM objGetProfileRequest = new BillingProfileRequest_LDTO_HUM();
            BillingProfileRequest_LDTO_HUM.PremiumInfo objGetPremiumInfo = new BillingProfileRequest_LDTO_HUM.PremiumInfo();
            BillingProfileRequest_LDTO_HUM.IncludeCriteria objGetIncludeCriteria = new BillingProfileRequest_LDTO_HUM.IncludeCriteria();
            BillingProfileRequest_LDTO_HUM.InvoiceInfo objGetInvoiceInfo = new BillingProfileRequest_LDTO_HUM.InvoiceInfo();
            BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequest objGetBillingAccRequest = new BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequest();
            BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequestCriteria objGetBillingAccRequestCriteria = new BillingProfileRequest_LDTO_HUM.GetBillingAccountInfoRequestCriteria();
            BillingProfileRequest_LDTO_HUM.SearchCriteriaList objGetSearchCriteiaList = new BillingProfileRequest_LDTO_HUM.SearchCriteriaList();
            BillingProfileRequest_LDTO_HUM.RequestContext objGetRequestContext = new BillingProfileRequest_LDTO_HUM.RequestContext();
            BillingProfileRequest_LDTO_HUM.SearchCriteria objGetSearchCriteria = new BillingProfileRequest_LDTO_HUM.SearchCriteria();
            
            BillingProfileResponse_LDTO_HUM objGetProfile = new BillingProfileResponse_LDTO_HUM();
            BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponse objAccInfoResponse = new BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponse();
            BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseList objAccInfoResponseList = new BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseList();
            BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseDetail objAccInfoResponseDetail = new BillingProfileResponse_LDTO_HUM.GetBillingAccountInfoResponseDetail();
            BillingProfileResponse_LDTO_HUM.ResponseStatus objResponseStatus = new BillingProfileResponse_LDTO_HUM.ResponseStatus();
            BillingProfileResponse_LDTO_HUM.SearchCriteria objSearchCriteria = new BillingProfileResponse_LDTO_HUM.SearchCriteria();
            BillingProfileResponse_LDTO_HUM.BillingAccountList objAccList = new BillingProfileResponse_LDTO_HUM.BillingAccountList();
            BillingProfileResponse_LDTO_HUM.BillAccount objBill = new BillingProfileResponse_LDTO_HUM.BillAccount();
            BillingProfileResponse_LDTO_HUM.InvoiceSummary objInv= new BillingProfileResponse_LDTO_HUM.InvoiceSummary();
            BillingProfileResponse_LDTO_HUM.Address objAdd= new BillingProfileResponse_LDTO_HUM.Address();
            BillingProfileResponse_LDTO_HUM.CollectionInfo objCollection= new BillingProfileResponse_LDTO_HUM.CollectionInfo();
            BillingProfileResponse_LDTO_HUM.CollectionCategoryInfo objCollectionCat= new BillingProfileResponse_LDTO_HUM.CollectionCategoryInfo();
            BillingProfileResponse_LDTO_HUM.PremiumInfo objPremInfo = new BillingProfileResponse_LDTO_HUM.PremiumInfo();
            BillingProfileResponse_LDTO_HUM.PaymentAccount objPayAcc = new BillingProfileResponse_LDTO_HUM.PaymentAccount();
            BillingProfileResponse_LDTO_HUM.BankAccount objBankAcc = new BillingProfileResponse_LDTO_HUM.BankAccount();
            BillingProfileResponse_LDTO_HUM.CreditCard objCreditCard = new BillingProfileResponse_LDTO_HUM.CreditCard();
            BillingProfileResponse_LDTO_HUM.RecurringPaymentMethodInfo objRecPayInfo = new BillingProfileResponse_LDTO_HUM.RecurringPaymentMethodInfo();
            BillingProfileResponse_LDTO_HUM.BillingProfile objBillingProfile = new BillingProfileResponse_LDTO_HUM.BillingProfile();
            BillingProfileResponse_LDTO_HUM.BalanceList objBalanceList = new BillingProfileResponse_LDTO_HUM.BalanceList();
            BillingProfileResponse_LDTO_HUM.PayorList objPayorList = new BillingProfileResponse_LDTO_HUM.PayorList();
            BillingProfileResponse_LDTO_HUM.BillAccount objBillAcc = new BillingProfileResponse_LDTO_HUM.BillAccount();
            BillingProfileResponse_LDTO_HUM.Balance objBalance = new BillingProfileResponse_LDTO_HUM.Balance();
            BillingProfileResponse_LDTO_HUM.Payor objPayor = new BillingProfileResponse_LDTO_HUM.Payor();
            BillingProfileResponse_LDTO_HUM.TransactionDetails objTran= new BillingProfileResponse_LDTO_HUM.TransactionDetails();
            
            Test.StopTest();
        }
    }
    
}