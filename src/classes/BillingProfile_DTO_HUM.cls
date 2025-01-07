/*******************************************************************************************************************************************************
Apex Class Name  : BillingProfile_DTO_HUM
Version          : 1.0
Created Date     : 11/18/2015
Function         : This is datacontract to send data from one class to other
Test Class       : BillingProfiles_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            29052                    11/18/2015                 Original Version
* Santhi Mandava            29422                    01/19/2016                 Implemented billing phase 2 stories. 
* Sumit Pharande            243694                   6/13/2018                  Billing Service V2 
* Atia Uzma                                          10/06/2020                 User Story 1564000: PR00091574 - MF 1 - INC0621496 fix - Ability to log on Payment Option field on Billing Profile Summary page (CRM)
* Prasanthi Kandula                                  07/26/2021                 US 2484356 T1PRJ0036776_MF_3SF : Member Billing Profile Page - Profile Selection  
************************************************************************************************************************************************/

public with sharing class BillingProfile_DTO_HUM implements Comparable
{
    public String sUserID{get;set;}
    public String sPlatformCode {get;set;}
    public String sIDType{get;set;}
    public String sSearchID{get;set;}
    public Integer iTimeout{get;set;}
    
    
    /*************************Profile Acc Info***********************************/
    public String sGroupPIDNumber{get;set;}
    public Integer iProfileNumber{get;set;}
    public Integer iAccountNumber{get;set;}
    public String sProfileAccountName{get;set;}
    public String sEffectiveDate{get;set;}
    public String sEndDate{get;set;}
    public String sStatus{get;set;}
    public String sTermReasonCode{get;set;}
    public String sPartyKey{get;set;}
    
     
      
    public String sProfileType{get;set;}
    public String sBillBlock{get;set;}
    public String sProductType{get;set;}
    public String sBillingPlatform{get;set;}
    public String sBillMethod{get;set;}
    public String sRecurringPaymentOption{get;set;}
    public String sRecurringPaymentOptionDescription{get;set;}
    public String sLastBillPeriodEndDate{get;set;}
    public String sNextBillDate{get;set;}
    public String sNextRecurringDate{get;set;}
    public String sBillingFrequency{get;set;}
    public String sProfBillingMode{get;set;}
    public String sDelinquentDate{get;set;}
    public String sPaidThruDate{get;set;}
    public String sCollectionStatusCode{get;set;}
    public String sProtectUntilDate{get;set;}
    public String sBillHold{get;set;}
    public String sBillHoldReason{get;set;}
    /***************************************************/
    
    /*************************Balance Info***********************************/
    public Decimal dCurrentAccountBalance{get;set;}
    public Decimal dCurrentAccBalNonSSA{get;set;}
    public Decimal dPastDueBalance{get;set;}
    public Decimal dPastDueBalanceNonSSA{get;set;}
    /***************************************************/
    
    /*************************Last Payment Info***********************************/
    public String sAllocationDate{get;set;}
    public Decimal dPaymentAmount{get;set;}
    public Integer iCheckNumber{get;set;}
    public String sPaymentType{get;set;}
    public String sReceivedDate{get;set;}
    /***************************************************/
    
    /*************************Collection Info***********************************/
    public String sNextActionCD{get;set;}
    public String sNextActionDate{get;set;}
    public String sExpectedTermActionDate{get;set;}
    public String sExpectedTermDate{get;set;}
    /***************************************************/
    
    /*************************Rate Info***********************************/
    public String sBillingMode{get;set;}
    public Decimal dPremium{get;set;}
    public Decimal dOptionalSupplementalPremium{get;set;}
    public Decimal dDiscounts{get;set;}
    public Decimal dSubsidies{get;set;}
    public Decimal dPenalties{get;set;}
    public Decimal dFees{get;set;}
    public Decimal dOthers{get;set;}
    public Decimal dNetMonthlyPremiumAmount{get;set;}
    /***************************************************/
    
    /*************************Last Invoice Summary***********************************/
    public String sInvoiceDueDate{get;set;}
    public Decimal dAmountDueFromLastInvoice{get;set;}
    public Decimal dTotalPaymentsReceived{get;set;}
    public Decimal dAmountPastDue{get;set;}
    public Decimal dPremiumsThisPeriod{get;set;}
    public Decimal dMemberAdjustments{get;set;}
    public Decimal dFeesAndOtherAdjustments{get;set;}
    public Decimal dTotalAmountDue{get;set;}
    public String sCoverageBeginDate{get;set;}
    public String sCreateDate{get;set;}
    /***************************************************/
    
     /***************Address List********************/
    public String sAddress{get;set;}
        /*************************************/
    /*
    * Method Name   :    compareTo
    * Description   :    This method is used to sort the list of this class.    
    * Return Type   :    Integer
    * Parameters    :    Object
    */
    public Integer compareTo(Object compareTo) 
    {
        BillingProfile_DTO_HUM compareToProf = (BillingProfile_DTO_HUM)compareTo;
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