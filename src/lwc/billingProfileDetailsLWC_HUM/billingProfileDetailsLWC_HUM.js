/******************************************************************************************************************************
LWC Name        : billingProfileDetailsLWC_HUM.js
Function        : LWC to display Billing details

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                    05/24/2022                    Original Version 
* Sagar Gulleve                  07/08/2022                    added line 97-100 for defect DF-5274 
* Raj Paliwal					 12/22/2022				       User Story 4003680:MF 3624855-CRM Service Billing Systems Integration: Lightning- Billing Info/Error/Toast Messages
* Suraj Patil 					 03/29/2023					   Defect fix: 7452
******************************************************************************************************************************/
import { api, LightningElement, track, wire } from 'lwc';
import fetchDetails from '@salesforce/apexContinuation/BillingDetails_LC_HUM.getProfileDetails';
import OvernightBillingAddress from '@salesforce/label/c.Overnight_Billing_Address';
import {performLogging,setEventListener,checkloggingstatus,clearLoggedValues,getLoggingKey} from 'c/loggingUtilityHum';
import { getLabels } from 'c/customLabelsHum';
import { CurrentPageReference } from 'lightning/navigation';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import { fireEvent } from 'c/pubsubLinkFramework';
import DetailServiceError from '@salesforce/label/c.BillingProfileDetailServiceError_Account_HUM';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import getAccountDetails from '@salesforce/apex/ClaimDetailsService_LC_HUM.getAccountDetails';

export default class BillingProfileDetailsLWC_HUM extends LightningElement {
    @track testobj = {};
    @track error;
    label = {
        OvernightBillingAddress,
    };
	LWCVariables = [];
    @api pageHeaderName = 'Member Billing Profile Detail';
    @track memberId;
     
    recId;
    pageType = 'Billing';
    pageTypeDetail;

    @track loggingkey;
	showloggingicon = true;
    autoLogging = true;
	personid;
	@track accountId;

    @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD,NETWORK_ID_FIELD,CURRENT_QUEUE]
      }) wireuser({
        error,
        data
      }) {
        if (error) {
          this.error = error;
        } else if (data) {
          this.profilename = data.fields.Profile.value.fields.Name.value;
          this.netWorkId = data.fields.Network_User_Id__c.value;
          this.workQueue = data.fields.Current_Queue__c.value;
          
        }
      }
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    @wire(getRecord, {
        recordId: '$recordId', fields: ['Account.Name','Account.Enterprise_ID__c']
    })
    wiredAccount({
        error,
        data
    }) {
        if (data) {
            this.dataWire=data;
			this.personid = data.fields.Enterprise_ID__c.value;
        }
        else if (error) {
            console.log('error in wire--', error);
        }
    }

    async connectedCallback() {
        let url = '';
        url = this.pageRef.attributes.url;
        let navData = url ? url.split('&') : '';
        let newObj = {
        };
        if(navData.length > 0){
            navData.map(item => {
                let splittedData = item.split('=');
                newObj[splittedData[0]] = splittedData[1];
            });
        }
        this.memberId = newObj.C__AccKey;
		this.pageTypeDetail = 'Billing:' + this.memberId;
        this.recId =   newObj.C__Id
		await getAccountDetails({MemberPlanID: this.recId})
        .then(result => {
            this.accountId = result.MemberId;
        })
        fetchDetails({PlatformCd: newObj.C__PlatformCd,
        sUserId: newObj.C__UserId,
        sAccNumber: newObj.C__AccKey
    })
            .then(result => {
				//this.testobj = result[0];
				if(result==null){
                    this.showToast("", DetailServiceError, "error","sticky");
                }
				if (result !=null) {
                    this.LWCVariables.push({ 'ProfileDisplayId': result[0].sGroupPIDNumber });
                    this.LWCVariables.push({ 'ProfileNumber': result[0].iProfileNumber });
                    fireEvent(this.pageRef, 'LinkVariableEvent', this.LWCVariables);
                }
				this.mapCurrencySymbol(result);
                this.error = undefined;
            })
            .catch(error => {
                console.log('the error is' + error);
                this.error = error;
                this.rtvData = undefined;
            });
			if(this.autoLogging){
                getLoggingKey(this.pageRef).then(result =>{
                    this.loggingkey = result;
                });
            }
    }
	
	showToast(strTitle, strMessage, strStyle, strMode) {
        this.dispatchEvent(
            new ShowToastEvent({
                title: strTitle,
                message: strMessage,
                variant: strStyle,
                mode: strMode
            })
        );
    }

    handleLogging(event) {
		if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            performLogging(event,this.createRelatedField(),'Billing Details',this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    performLogging(event,this.createRelatedField(),'Billing Details',this.loggingkey,this.pageRef);
                }
            })
        }
	}
	
	createRelatedField(){
        return [{
            label : 'Group/PID',
            value : this.memberId
        }];
    }
	
	mapCurrencySymbol(data){
        const finalData = data.map((ele)=>({
                  dPaymentAmount : this.getCurrencyValue(ele,'dPaymentAmount'),
            dCurrentAccBalNonSSA : this.getCurrencyValue(ele,'dCurrentAccBalNonSSA'),
          dCurrentAccountBalance : this.getCurrencyValue(ele,'dCurrentAccountBalance'),
                      dDiscounts : this.getCurrencyValue(ele,'dDiscounts'),
                           dFees : this.getCurrencyValue(ele,'dFees'),
        dNetMonthlyPremiumAmount : this.getCurrencyValue(ele,'dNetMonthlyPremiumAmount'),
    dOptionalSupplementalPremium : this.getCurrencyValue(ele,'dOptionalSupplementalPremium'),
                         dOthers : this.getCurrencyValue(ele,'dOthers'),
                 dPastDueBalance : this.getCurrencyValue(ele,'dPastDueBalance'),
           dPastDueBalanceNonSSA : this.getCurrencyValue(ele,'dPastDueBalanceNonSSA'),
                      dPenalties : this.getCurrencyValue(ele,'dPenalties'),
                        dPremium : this.getCurrencyValue(ele,'dPremium'),
                      dSubsidies : this.getCurrencyValue(ele,'dSubsidies'),
                 dTotalAmountDue : this.getCurrencyValue(ele,'dTotalAmountDue'),
                  dAmountPastDue : this.getCurrencyValue(ele,'dAmountPastDue'),
              dMemberAdjustments : this.getCurrencyValue(ele,'dMemberAdjustments'),
       dAmountDueFromLastInvoice : this.getCurrencyValue(ele,'dAmountDueFromLastInvoice'),
             dPremiumsThisPeriod : this.getCurrencyValue(ele,'dPremiumsThisPeriod'),
 testobjdFeesAndOtherAdjustments : this.getCurrencyValue(ele,'testobjdFeesAndOtherAdjustments'),
          dTotalPaymentsReceived : this.getCurrencyValue(ele,'dTotalPaymentsReceived'),      
                  iAccountNumber : ele.iAccountNumber,   
                  iProfileNumber : ele.iProfileNumber,
                        sAddress : ele.sAddress,
                      sBillBlock : ele.sBillBlock,
                       sBillHold : ele.sBillHold,
              sBillingDetailLink : ele.sBillingDetailLink,
               sBillingFrequency : ele.sBillingFrequency,
                sBillingPlatform : ele.sBillingPlatform,
                     sBillMethod : ele.sBillMethod,
           sCollectionStatusCode : ele.sCollectionStatusCode,
                 sDelinquentDate : ele.sDelinquentDate,
                   seBillingLink : ele.seBillingLink,
                  sEffectiveDate : ele.sEffectiveDate,
                        sEndDate : ele.sEndDate,                 
         sExpectedTermActionDate : ele.sExpectedTermActionDate,
               sExpectedTermDate : ele.sExpectedTermDate,
                 sGroupPIDNumber : ele.sGroupPIDNumber,
          sLastBillPeriodEndDate : ele.sLastBillPeriodEndDate,
                 sNextActionDate : ele.sNextActionDate,
                   sNextBillDate : ele.sNextBillDate,
              sNextRecurringDate : ele.sNextRecurringDate,
                   sPaidThruDate : ele.sPaidThruDate,
                       sPartyKey : ele.sPartyKey,
            sPBSNewCommentsLinks : ele.sPBSNewCommentsLinks,
                     sPBSNewLink : ele.sPBSNewLink,
                    sProductType : ele.sProductType,
                sProfBillingMode : ele.sProfBillingMode,
             sProfileAccountName : ele.sProfileAccountName,
                    sProfileType : ele.sProfileType,
               sProtectUntilDate : ele.sProtectUntilDate,
         sRecurringPaymentOption : ele.sRecurringPaymentOption,
sRecurringPaymentOptionDescription : ele.sRecurringPaymentOptionDescription,
                         sStatus : ele.sStatus
                                                      
        }));
        this.testobj = finalData[0];
    }

    getCurrencyValue(record,attribute){
      const data = record[attribute] ? record[attribute].toString() : '$0.00';
      return  !data.includes('$') && !data.includes('-') 
            ? '$'+Number(data).toFixed(2) 
            : data.includes('$-') ||  data.includes('-') 
            ? '-$'+Number(data.split('-')[1]).toFixed(2) 
            : !data.includes('$')
            ? '$'+Number(data).toFixed(2)
            : '$'+Number(data.split('$')[1]).toFixed(2);
    }

}