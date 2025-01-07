/**********************************************************************************************************************
Apex Class   : BillingTestDataSetup_LT_HUM
Description  : Utility class for creating test data that can be used in Test Classes across the Application.
               This class is made public since it is referenced by other classes.
Created By   : Sagar Gulleve
Created On   : 05/23/2022

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer             Code Review            Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Sagar Gulleve                                05/23/2022              Original version
***********************************************************************************************************************/
@isTest
public class BillingTestDataSetup_LT_HUM
{
    /*
    * Method Name : createTestData
    * Description : This method is used to create account records and custom setting values.
    * Return type : NA
    * Parameter   : NA
    */
    @testSetup public static void createTestData()
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
        
       
        
        HUM_Webservice_Callout__c svcHeaders2 = new HUM_Webservice_Callout__c();
        svcHeaders2.Name = 'BillingServiceV2';
        svcHeaders2.Certificate_Name__c = 'Humana_GuidanceCare_Hoth';
        svcHeaders2.Timeout__c = '2000';
        svcHeaders2.Request_Type__c = 'POST';
        svcHeaders2.Content_Type__c ='application/json';
        svcHeaders2.End_Point_URL__c = 'https://int-crmservicews.humana.com/BillingService_V2.0';
        insert svcHeaders2;
        system.assertequals('BillingServiceV2',svcHeaders2.Name );
    }
    
   
    
    
    
    /*
    * Method Name : createBillingWrapperList
    * Description : This method is used to create BillingProfile_DTO_HUM list
    * Return type : List<BillingProfile_DTO_HUM>
    * Parameter   : NA 
    */
    public static List<BillingProfile_LDTO_HUM> createBillingWrapperList()
    {
        List<BillingProfile_LDTO_HUM> lstValues = new List<BillingProfile_LDTO_HUM>();
        BillingProfile_LDTO_HUM objWrapper;
        For(Integer i=1; i < 5; i++)
        {
            objWrapper = new BillingProfile_LDTO_HUM();
            objWrapper.sUserID = 'SXS4444';
            objWrapper.sPlatformCode = 'EM';
            objWrapper.sIDType ='1';
            objWrapper.sSearchID ='2';
            objWrapper.sGroupPIDNumber = '12345';
            objWrapper.iProfileNumber = i;
            objWrapper.iAccountNumber = 1234;
            objWrapper.sProfileAccountName = 'Global corporation';
            objWrapper.sEffectiveDate = '12/22/2013';
            objWrapper.sEndDate = '12/22/2014';
            objWrapper.sStatus = 'Active';
            objWrapper.sTermReasonCode = 'TC0033';
            objWrapper.sAllocationDate = '11/12/2012';
            objWrapper.iCheckNumber = 12334;
            objWrapper.sNextActionDate = '11/12/2012';

            objWrapper.iTimeout  = 1200;
            objWrapper.sProfileType = 'Test';
            objWrapper.sBillBlock ='BC';
            objWrapper.sProductType = 'MTV';
            objWrapper.sBillingPlatform ='LV';
            objWrapper.sBillMethod = 'MNTH';
            objWrapper.sRecurringPaymentOption = 'SC';
            objWrapper.sLastBillPeriodEndDate = '11/12/2012';
            objWrapper.sNextBillDate = '11/12/2012';
            objWrapper.sBillingFrequency = 'B';
            objWrapper.sProfBillingMode = 'MNTH';
            objWrapper.sDelinquentDate = '11/12/2012';
            objWrapper.sPaidThruDate = '11/12/2012';
            objWrapper.sCollectionStatusCode = 'S';
            objWrapper.sProtectUntilDate = '11/12/2012';
            objWrapper.sBillHold = 'SC';
            objWrapper.sBillHoldReason = '10';
            objWrapper.dCurrentAccountBalance = '144';
            objWrapper.dCurrentAccBalNonSSA = '678';
            objWrapper.dPastDueBalance = '987';
            objWrapper.dPastDueBalanceNonSSA = '2345';
            objWrapper.sAllocationDate = '11/12/2012';
            objWrapper.dPaymentAmount = 1256;
            objWrapper.sPaymentType = 'TR';
            objWrapper.sNextActionCD = 'RRF';
            objWrapper.sExpectedTermActionDate = '11/12/2012';
            objWrapper.sExpectedTermDate = '11/12/2012';
            objWrapper.sBillingMode = 'B';
            objWrapper.dPremium = '1256';
            objWrapper.dOptionalSupplementalPremium = '1567';
            objWrapper.dDiscounts ='8756';
            objWrapper.dSubsidies = '66';
            objWrapper.dPenalties = '345';
            objWrapper.dFees = '0';
            objWrapper.dOthers = '345';
            objWrapper.dNetMonthlyPremiumAmount = '456';
            objWrapper.sInvoiceDueDate = '11/12/2012';
            objWrapper.dAmountDueFromLastInvoice = '546';
            objWrapper.dTotalPaymentsReceived = '345';
            objWrapper.dAmountPastDue = '7234';
            objWrapper.dPremiumsThisPeriod = '254';
            objWrapper.dMemberAdjustments = '5435';
            objWrapper.dFeesAndOtherAdjustments = '243';
            objWrapper.dTotalAmountDue = '234';
            objWrapper.sCoverageBeginDate = '11/12/2012';
            objWrapper.sCreateDate = '11/12/2012';
            lstValues.Add(objWrapper);
        }
        lstValues[2].iProfileNumber = 7;
        System.assertEquals(2,lstValues[1].iProfileNumber );
        return lstValues;
    }
}