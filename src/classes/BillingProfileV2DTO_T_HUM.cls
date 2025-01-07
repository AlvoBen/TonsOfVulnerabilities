/******************************************************************************************************************
Apex Class Name :   BillingProfileV2DTO_T_HUM 
Version: 1.0
Function           : Test Class to Cover BillingProfileV2 DTO Class
Created On         : May 14 2018

Modification Log:  
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala              220187         5/14/2018           Original Version -> REQ - 361942
*    1.1                Sumit Pharande			      243694	     6/13/2018           Billing Service V2 custom setting
------------------------------------------------------------------------------------------------------------------*/
@isTest
Private Class BillingProfileV2DTO_T_HUM {
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
            BillingProfileRequestV2_DTO_HUM objGetProfileRequest = new BillingProfileRequestV2_DTO_HUM();
            BillingProfileRequestV2_DTO_HUM.PremiumInfo objGetPremiumInfo = new BillingProfileRequestV2_DTO_HUM.PremiumInfo();
            BillingProfileRequestV2_DTO_HUM.IncludeCriteria objGetIncludeCriteria = new BillingProfileRequestV2_DTO_HUM.IncludeCriteria();
            BillingProfileRequestV2_DTO_HUM.InvoiceInfo objGetInvoiceInfo = new BillingProfileRequestV2_DTO_HUM.InvoiceInfo();
            BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequest objGetBillingAccRequest = new BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequest();
            BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequestCriteria objGetBillingAccRequestCriteria = new BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequestCriteria();
            BillingProfileRequestV2_DTO_HUM.SearchCriteriaList objGetSearchCriteiaList = new BillingProfileRequestV2_DTO_HUM.SearchCriteriaList();
            BillingProfileRequestV2_DTO_HUM.RequestContext objGetRequestContext = new BillingProfileRequestV2_DTO_HUM.RequestContext();
            BillingProfileRequestV2_DTO_HUM.SearchCriteria objGetSearchCriteria = new BillingProfileRequestV2_DTO_HUM.SearchCriteria();
            
            BillingProfileResponseV2_DTO_HUM objGetProfile = new BillingProfileResponseV2_DTO_HUM();
            BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponse objAccInfoResponse = new BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponse();
            BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseList objAccInfoResponseList = new BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseList();
            BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseDetail objAccInfoResponseDetail = new BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseDetail();
            BillingProfileResponseV2_DTO_HUM.ResponseStatus objResponseStatus = new BillingProfileResponseV2_DTO_HUM.ResponseStatus();
            BillingProfileResponseV2_DTO_HUM.SearchCriteria objSearchCriteria = new BillingProfileResponseV2_DTO_HUM.SearchCriteria();
            BillingProfileResponseV2_DTO_HUM.BillingAccountList objAccList = new BillingProfileResponseV2_DTO_HUM.BillingAccountList();
            BillingProfileResponseV2_DTO_HUM.BillAccount objBill = new BillingProfileResponseV2_DTO_HUM.BillAccount();
            BillingProfileResponseV2_DTO_HUM.InvoiceSummary objInv= new BillingProfileResponseV2_DTO_HUM.InvoiceSummary();
            BillingProfileResponseV2_DTO_HUM.Address objAdd= new BillingProfileResponseV2_DTO_HUM.Address();
            BillingProfileResponseV2_DTO_HUM.CollectionInfo objCollection= new BillingProfileResponseV2_DTO_HUM.CollectionInfo();
            BillingProfileResponseV2_DTO_HUM.CollectionCategoryInfo objCollectionCat= new BillingProfileResponseV2_DTO_HUM.CollectionCategoryInfo();
            BillingProfileResponseV2_DTO_HUM.PremiumInfo objPremInfo = new BillingProfileResponseV2_DTO_HUM.PremiumInfo();
            BillingProfileResponseV2_DTO_HUM.PaymentAccount objPayAcc = new BillingProfileResponseV2_DTO_HUM.PaymentAccount();
            BillingProfileResponseV2_DTO_HUM.BankAccount objBankAcc = new BillingProfileResponseV2_DTO_HUM.BankAccount();
            BillingProfileResponseV2_DTO_HUM.CreditCard objCreditCard = new BillingProfileResponseV2_DTO_HUM.CreditCard();
            BillingProfileResponseV2_DTO_HUM.RecurringPaymentMethodInfo objRecPayInfo = new BillingProfileResponseV2_DTO_HUM.RecurringPaymentMethodInfo();
            BillingProfileResponseV2_DTO_HUM.BillingProfile objBillingProfile = new BillingProfileResponseV2_DTO_HUM.BillingProfile();
            BillingProfileResponseV2_DTO_HUM.BalanceList objBalanceList = new BillingProfileResponseV2_DTO_HUM.BalanceList();
            BillingProfileResponseV2_DTO_HUM.PayorList objPayorList = new BillingProfileResponseV2_DTO_HUM.PayorList();
            BillingProfileResponseV2_DTO_HUM.BillAccount objBillAcc = new BillingProfileResponseV2_DTO_HUM.BillAccount();
            BillingProfileResponseV2_DTO_HUM.Balance objBalance = new BillingProfileResponseV2_DTO_HUM.Balance();
            BillingProfileResponseV2_DTO_HUM.Payor objPayor = new BillingProfileResponseV2_DTO_HUM.Payor();
            BillingProfileResponseV2_DTO_HUM.TransactionDetails objTran= new BillingProfileResponseV2_DTO_HUM.TransactionDetails();
            
            Test.StopTest();
        }
    }
    
}