/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfile_LDTO_HUM
Version          : 1.0
Created Date     : 05/23/2022
Function         : This is datacontract to send data from one class to other
Test Class       : BillingProfile_LT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                                        05/23/2022                 Original Version
* Prashant Moghe                                     05/23/2022                 User Story 3271229:MF-2923225 - CRM Service Billing Systems Integration: Member Billing Profile Account Billing APP -Billing List
************************************************************************************************************************************************/

public with sharing class BillingProfile_LDTO_HUM implements Comparable
{
    @AuraEnabled public String sUserID{get;set;}
    @AuraEnabled public String sPlatformCode {get;set;}
    @AuraEnabled public String sIDType{get;set;}
    @AuraEnabled public String sSearchID{get;set;}
    @AuraEnabled public Integer iTimeout{get;set;}
    @AuraEnabled public string sBillingDetailLink{get;set;}
    @AuraEnabled public string seBillingLink{get;set;}
    @AuraEnabled public string sPBSNewLink{get;set;}
    @AuraEnabled public string sPBSNewCommentsLinks{get;set;}
    /*************************Profile Acc Info***********************************/
    @AuraEnabled public String sGroupPIDNumber{get;set;}
    @AuraEnabled public Integer iProfileNumber{get;set;}
    @AuraEnabled public Integer iAccountNumber{get;set;}
    @AuraEnabled public String sProfileAccountName{get;set;}
    @AuraEnabled public String sEffectiveDate{get;set;}
    @AuraEnabled public String sEndDate{get;set;}
    @AuraEnabled public String sStatus{get;set;}
    @AuraEnabled public String sTermReasonCode{get;set;}
    @AuraEnabled public String sPartyKey{get;set;}
    
     
      
    @AuraEnabled public String sProfileType{get;set;}
    @AuraEnabled public String sBillBlock{get;set;}
    @AuraEnabled public String sProductType{get;set;}
    @AuraEnabled public String sBillingPlatform{get;set;}
    @AuraEnabled public String sBillMethod{get;set;}
    @AuraEnabled public String sRecurringPaymentOption{get;set;}
    @AuraEnabled public String sRecurringPaymentOptionDescription{get;set;}
    @AuraEnabled public String sLastBillPeriodEndDate{get;set;}
    @AuraEnabled public String sNextBillDate{get;set;}
    @AuraEnabled public String sNextRecurringDate{get;set;}
    @AuraEnabled public String sBillingFrequency{get;set;}
    @AuraEnabled public String sProfBillingMode{get;set;}
    @AuraEnabled public String sDelinquentDate{get;set;}
    @AuraEnabled public String sPaidThruDate{get;set;}
    @AuraEnabled public String sCollectionStatusCode{get;set;}
    @AuraEnabled public String sProtectUntilDate{get;set;}
    @AuraEnabled public String sBillHold{get;set;}
    @AuraEnabled public String sBillHoldReason{get;set;}
    /***************************************************/
    
    /*************************Balance Info***********************************/
    @AuraEnabled public String dCurrentAccountBalance{get;set;}
    @AuraEnabled public String dCurrentAccBalNonSSA{get;set;}
    @AuraEnabled public String dPastDueBalance{get;set;}
    @AuraEnabled public String dPastDueBalanceNonSSA{get;set;}
    /***************************************************/
    
    /*************************Last Payment Info***********************************/
    @AuraEnabled public String sAllocationDate{get;set;}
    @AuraEnabled public Decimal dPaymentAmount{get;set;}
    @AuraEnabled public Integer iCheckNumber{get;set;}
    @AuraEnabled public String sPaymentType{get;set;}
    @AuraEnabled public String sReceivedDate{get;set;}
    /***************************************************/
    
    /*************************Collection Info***********************************/
    @AuraEnabled public String sNextActionCD{get;set;}
    @AuraEnabled public String sNextActionDate{get;set;}
    @AuraEnabled public String sExpectedTermActionDate{get;set;}
    @AuraEnabled public String sExpectedTermDate{get;set;}
    /***************************************************/
    
    /*************************Rate Info***********************************/
    @AuraEnabled public String sBillingMode{get;set;}
    @AuraEnabled public String dPremium{get;set;}
    @AuraEnabled public String dOptionalSupplementalPremium{get;set;}
    @AuraEnabled public String dDiscounts{get;set;}
    @AuraEnabled public String dSubsidies{get;set;}
    @AuraEnabled public String dPenalties{get;set;}
    @AuraEnabled public String dFees{get;set;}
    @AuraEnabled public String dOthers{get;set;}
    @AuraEnabled public String dNetMonthlyPremiumAmount{get;set;}
    /***************************************************/
    
    /*************************Last Invoice Summary***********************************/
    @AuraEnabled public String sInvoiceDueDate{get;set;}
    @AuraEnabled public String dAmountDueFromLastInvoice{get;set;}
    @AuraEnabled public String dTotalPaymentsReceived{get;set;}
    @AuraEnabled public String dAmountPastDue{get;set;}
    @AuraEnabled public String dPremiumsThisPeriod{get;set;}
    @AuraEnabled public String dMemberAdjustments{get;set;}
    @AuraEnabled public String dFeesAndOtherAdjustments{get;set;}
    @AuraEnabled public String dTotalAmountDue{get;set;}
    @AuraEnabled public String sCoverageBeginDate{get;set;}
    @AuraEnabled public String sCreateDate{get;set;}
    /***************************************************/
    
     /***************Address List********************/
    @AuraEnabled public String sAddress{get;set;}
        /*************************************/
    /*
    * Method Name   :    compareTo
    * Description   :    This method is used to sort the list of this class.    
    * Return Type   :    Integer
    * Parameters    :    Object
    */
    public Integer compareTo(Object compareTo) 
    {
        BillingProfile_LDTO_HUM compareToProf = (BillingProfile_LDTO_HUM)compareTo;
        Integer returnValue = 0;
        
        if (this.iProfileNumber > compareToProf.iProfileNumber) 
        {
            returnValue = 1;
        } else if (this.iProfileNumber < compareToProf.iProfileNumber) 
        {
            returnValue = -1;
        }
        return returnValue;       
    }
}