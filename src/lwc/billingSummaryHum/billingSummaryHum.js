/*******************************************************************************************************************************
LWC JS Name : billingSummaryHum.js
Function    : This JS serves as helper to billingSummaryHum.html

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
*Anuradha Gajbhe                                          05/23/2022                   User Story 3266251:MF 2923212 - CRM Service Billing Systems Integration: Member Billing Profile Account-Billing List
*Prashant Moghe                                           05/23/2022                   User Story 3271229:MF-2923225 - CRM Service Billing Systems Integration: Member Billing Profile Account Billing APP -Billing List   
*Sagar G                                                  06/23/2022                   User Story 3366783:MF 2922840 -  CRM Service Billing Systems Integration: Lightning- Billing/Member Summary & Details-Billing logging- Details auto pop
*Sagar G                                                  07/13/2022                   Bug Fix 3366783: Member billing detail tab title not displaying correctly
*Raj Paliwal										      12/22/2022				   User Story 4003680:MF 3624855-CRM Service Billing Systems Integration: Lightning- Billing Info/Error/Toast Messages
*Anuradha Gajbhe                                          03/06/2023                   US#4302387 - Lightning-Phonebook- Secure Payment UI Controls-EBilling (Surge) Genesys impact
*Anuradha Gajbhe                                          03/24/2023                   Defect-Fix: 7445
*********************************************************************************************************************************/
import { api, track, wire, LightningElement } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import { CurrentPageReference } from 'lightning/navigation';
import billingSummaryRequest from '@salesforce/apexContinuation/BillingProfile_LC_HUM.getBillingProfiles';
import {getBillingSummaryStructure} from './layoutConfig';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
import loggingLMSChannel from '@salesforce/messageChannel/loggingLMSChannel__c';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/connectorHUM__c';
import {performLogging,setEventListener,checkloggingstatus,clearLoggedValues,getLoggingKey} from 'c/loggingUtilityHum';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import pubSubHum from 'c/pubSubHum';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import getSObjName from '@salesforce/apex/BillingProfile_LC_HUM.getSObjName';
import chkSecurePayFlagDetails from '@salesforce/apex/BillingProfile_LC_HUM.getSecurePayFlagDetails';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import PBS_Error from '@salesforce/label/c.BillingMemberProfileServiceError_Account_HUM';
import Service_Error from '@salesforce/label/c.BillingProfileServiceError_Account_HUM';
import NetworkId_Error from '@salesforce/label/c.BillingNetworkIdError';
import SCKError from '@salesforce/label/c.BillingSCKError';
import SCIDError from '@salesforce/label/c.BillingSCIDError';

export default class billingSummaryHum extends NavigationMixin(LightningElement) {

    @api billingSummaryResponse = [];
    @api tempBillingSummaryResponse;   
    @track billingSummaryItemModel;     
    @api showViewAll = false;
    @track bResponse;
    @track bNoResponse;
    @api bInfiniteScroll;
    @api title;
    @track oViewAllParams = {};   
    @track keyword;   
    @track bLoading= false;
    @track resultsTrue = false;
    @track CurrentlyShowingRecords = 0;
    @api totalNumberOfRecords = 0;
    @track isNotPurhcaserPlan;
    @track header = {};
    @track groupAccountName = '';
    @track policyName = '';
    @track showPlanPanel=false;
    @api recordId;   
    @track SObjName;
    @track sAkaName;
    @track sHeaderGroupPIDNumber;
    @track profileName;
    @track netWorkId;
    @track workQueue;
	  @track loggingScreenName = 'Billing Summary';
    @track bSecurePaybuttonFlag;

    showLoggingIcon = true;
    startLogging = false;
    collectedLoggedData = [];
    autoLogging = true;
	  personid;
    
        keyIndex = 0;
        @track itemList = [
            {
                id: 0
            }
        ];

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

        @wire(getRecord, {
            recordId: '$recordId', fields: ['Account.Name','Account.Enterprise_ID__c']
        })
        wiredAccount({
            error,
            data
        }) {
            if (data) {
                this.personid = data.fields.Enterprise_ID__c.value;
            }
            else if (error) {
                console.log('error in wire--', error);
            }
        }  

  @wire(getRecord, {
            recordId: '$recordId', fields: ['MemberPlan.Name','MemberPlan.Policy_Platform__c','MemberPlan.SubscriberPlanId__c', 'MemberPlan.SubscriberPlanId__r.Name']
        })
        wiredMemPlan({
            error,
            data
        }) {
            if (data) {
                this.MemberIdBase = data.fields.Name.value ? data.fields.Name.value: '';
                this.PlatformCode = data.fields.Policy_Platform__c.value ? data.fields.Policy_Platform__c.value : '';
				this.SubscriberIDBase = (data.fields.SubscriberPlanId__c.value != null) ? data.fields.SubscriberPlanId__r.value.fields.Name.value : '';
            }
            else if (error) {
                console.log('error in wire##', error);
            }
        } 
        @wire(MessageContext)
        messageContext;
   
        @wire(getSObjName,{recID:'$recordId'})
        wSObjName({data,error}){
            if(data){
                this.SObjName=data
            }else{
                this.SObjName=error
            }
        }
    @wire(CurrentPageReference)
        currentPageReference(pageRef){
        this.pageRef = pageRef;   
        }

        connectedCallback(){
            this.recordId = this.pageRef.state.C__Id;
            
            this.bLoading= true;
            this.getSecurePayFlag();
            this.callBillingService();
            this.loggingkey = getLoggingKey();
            this.startLogging = checkloggingstatus(this.loggingkey ? this.loggingkey : getLoggingKey());
            pubSubHum.registerListener('loggingevent', this.loggingEventHandler.bind(this), this);
        }

        loggingEventHandler(data){ 
            if (data.MessageName === 'StartLogging') {
                this.startLogging = true;
            }
            if (data.MessageName === 'StopLogging') {
                this.startLogging = false;
                clearLoggedValues(this.loggingkey);
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

    async callBillingService(){
        const res = await billingSummaryRequest({sRecordId:this.recordId})
            .then((result) => {
				if(result === PBS_Error ){
                    this.bNoResponse=true;
                    this.showToast("Error:", PBS_Error, "error","sticky");
                  }
                else if(result === Service_Error ){
                    this.bNoResponse=true;
                    this.showToast("",Service_Error, "error","sticky");
                }
                else if(result === NetworkId_Error ){
                    this.bNoResponse=true;
                    this.showToast("",NetworkId_Error, "error","sticky");
                }
                else if(result === SCKError ){
					          this.bNoResponse=true;
                    this.showToast("",SCKError, "error","sticky");
                }
                else if(result === SCIDError ){
                    this.bNoResponse=true;
                    this.showToast("",SCIDError, "error","sticky");
                }
                else if (result == null || result == undefined || JSON.stringify(result) === '{}'){
                    this.bLoading= false;
                    this.bNoResponse=true;
                }
              else{
                this.tempBillingSummaryResponse = JSON.stringify(result);
                let temparray = [];
                temparray.push(result);
                let billingData = this.mapCurrencySymbol(temparray);
                this.buildPhoneBookEventData(billingData);
                    
                this.billingSummaryResponse=billingData.map((item)=>{
                    return {...item, "BillingDetail": "Billing Detail","eBilling": "eBilling", "PBS": "PBS","PBSComments": "PBS Comments", "bSecurePaybuttonFlag": this.bSecurePaybuttonFlag, "SecurePay": this.objBillingProfileInfo}
                });
				        var tabName = 'Profile'+this.billingSummaryResponse[0].iProfileNumber+': '+this.pageRef.state.C__Name;
                this.billingSummaryResponse[0].sBillingDetailLink=this.billingSummaryResponse[0].sBillingDetailLink?this.billingSummaryResponse[0].sBillingDetailLink.replace('Member Billing Detail',tabName):'';
                this.sHeaderGroupPIDNumber = this.billingSummaryResponse[0].sGroupPIDNumber;
                this.totalNumberOfRecords = this.billingSummaryResponse.length;
                this.CurrentlyShowingRecords = this.totalNumberOfRecords;
                this.bResponse=true;
                this.bLoading = false;
                    if (this.totalNumberOfRecords == 1) {
                        try {
                            const payload = {
                                url: this.billingSummaryResponse[0].sBillingDetailLink,
                                tabname: tabName,
								bfocussubtab:true
                            };
                            publish(this.messageContext, CONNECTOR_CHANNEL, payload);
                        } catch (e) {
                            console.log("error" + e.error);
                        }

                    }
              }  

            this.billingSummaryItemModel = getBillingSummaryStructure();
            })   
            .catch(e=>{
                console.log('catch exception'+e);        
            }) 
			
    }
    
    buildPhoneBookEventData(sbillingData) {
        let lnkData = sbillingData[0].sBillingDetailLink ? sbillingData[0].sBillingDetailLink.split('&') : '';
        let newObj = {};
        if(lnkData.length > 0){
            lnkData.map(item => {
                let splittedData = item.split('=');
                newObj[splittedData[0]] = splittedData[1];
            });
        }
        this.recIdJS = newObj.C__Id;
        this.partyKey = sbillingData[0].sPartyKey;
        this.iProfileNumber =   sbillingData[0].iProfileNumber;
        
        this.objBillingProfileInfo = {};
        this.objBillingProfileInfo.TargetSystem = 'PBS';
        this.objBillingProfileInfo.ActionType = 'A';
        this.objBillingProfileInfo.PartyKey = this.partyKey;
        this.objBillingProfileInfo.ProfileSequenceNumber = this.iProfileNumber;
        this.objBillingProfileInfo.MemberIdBase = this.MemberIdBase;
        this.objBillingProfileInfo.SubscriberIDBase = this.SubscriberIDBase;
        this.objBillingProfileInfo.PolicyMemberId = this.recIdJS;
        this.objBillingProfileInfo.PlatformCd = this.PlatformCode;
        this.objBillingProfileInfo.BillingCardFirstName = '';
        this.objBillingProfileInfo.BillingCardLastName = '';
    }

      async getSecurePayFlag(){
        const res = await chkSecurePayFlagDetails({sRecordId:this.recordId})
        .then((result) => {
            this.bSecurePaybuttonFlag = result;
        })   
        .catch(e=>{
            console.log('catch exception',e);        
        })
    }
      
    handleLogging(event) {
        if(this.startLogging){ 
            performLogging(event,this.createRelatedField(),'Billing Summary',this.loggingkey ? this.loggingkey : getLoggingKey(),this.pageRef);
        }
    }

    createRelatedField(){
        return [{
            label : 'Group/PID',
            value : this.billingSummaryResponse[0].sGroupPIDNumber            
        }];
    }

    get pagename() {
        return PAGE_NAME;
    }
	
	mapCurrencySymbol(data){
        let finalData = data.map((ele)=>({
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
        return finalData;
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