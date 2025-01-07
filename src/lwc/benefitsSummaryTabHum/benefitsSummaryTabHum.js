/*
LWC Name        : BenefitsSummaryTabHum.js
Function        : JS file for benefit summary.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane                03/17/2022                   US-3017787_3191923 - Benefit Summary
* Swapnali Sonawane                06/27/2022                   Defct 5222 Network Description does not match Classic to Lightning
* Swapnali Sonawane                08/23/2022                   US#3631288 Use purchaser plan object
*  Divya Bhamre                    11/02/2022                     US - 3833519
* Aishwarya Pawar               	 03/01/2023                     US - 4286514
* Sagar G                          08/05/2023                   US - 4534112
* Swapnali Sonawane                18/05/2023                   US - 4586746 Enablement of the medical benefits UI logging - Summary
* Swapnali Sonawane                01/06/2023                   US - 4658275 Dental UI Logging Summary
* Nirmal Garg		               01/06/2023                   US - 4556179 - Dental Plan - Dental Waiting Periods
* Nirmal Garg		               01/06/2023                   US - 4556179 - Dental Plan - Dental Waiting Periods
* Apurva Urkude			   06/05/2023                   User Story- 4697275
* Apurva Urkude                    06/20/2023                   Defect Fix-7771
* Swapnali Sonawane                07/27/2023                   US-4802828  Update UI Medical Network ID Search Screen
****************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import getGBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokeGBEService';
import getMBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokeMBEService';
import getBillingProfiles from '@salesforce/apexContinuation/Benefits_LC_HUM.getBillingProfiles';
import Deductible_Type from '@salesforce/label/c.Deductible_Type';
import HDHP from '@salesforce/label/c.HDHP';
import MHVenderCode from '@salesforce/label/c.MHVenderCode';
import BenefitsPeriod from '@salesforce/label/c.BenefitsPeriod';
import BenefitsServiceError from '@salesforce/label/c.BenefitsServiceError';
import BenefitMBEServiceError from '@salesforce/label/c.BenefitMBEServiceError';
import NonCommercialMedicalPol from '@salesforce/label/c.NonCommercialMedicalPol';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { NavigationMixin } from 'lightning/navigation';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import benefitSummaryCommercial from './templates/benefitSummaryCommercial.html';
import benefitSummaryNonCommercial from './templates/benefitSummaryNonCommercial.html';
import benefitSummaryCommercialDental from './templates/benefitSummaryCommercialDental.html';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
const VOB_PLATFORM_EM_HUM = 'EM';
const VOB_PLATFORM_LV_HUM = 'LV';
const VOB_PLATFORM_MTV_HUM = 'MTV';
const VOB_PLATFORM_CI_HUM = 'CI';
const VOB_PLATFORM_CAS_HUM = 'CAS';
const USER_FIELDS = [NETWORK_ID_FIELD];
export default class BenefitsSummaryTabHum extends NavigationMixin(LightningElement)
{
    @api recordId;
    @track netWorkId;
    @api pberesponse;
    @api memberPlanData;
    @api serviceerror;
    @api message;
    @track MBEData = {};
    @track GBEData = {};
    @track PBEData = {};
    @track cobraValue = 'False';
    @track hidePaidThruDate = false;
    @track benefitCoverage;
    @track GBEResponse = {};
    @track BillingData = {};
    @track hideShowLink;
    @track isHide = false;
    @track isGBELoading = true;
    @track isMBELoading = true;
    @api isDental;
    @api sGroupNumber;
    userRecord;
    @track platformValue;
    @track loggingkey;
    @track screenName;
    @track paidThruDateMBE;
    labels = {
        Deductible_Type,
        HDHP,
        MHVenderCode,
        BenefitsPeriod,
        NonCommercialMedicalPol,
        BenefitsServiceError,
        BenefitMBEServiceError
    };
    autoLogging = true;
    pageRef;
    gbeEventFired = false;
    mbeEvenbtFired = false;

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        this.loggingkey = getLoggingKey(this.pageRef);
    }


    connectedCallback() {
        this.processPBEData();
        if (this.gberesponse && Object.keys(this.gberesponse)?.length > 0) {
            this.processGBEData();
        } else {
            this.fireGBEEvent();
        }
        if (this.mberesponse && Object.keys(this.mberesponse)?.length > 0) {
            this.processMBEData();
        } else {
            this.fireMBEEvent();
        }
        this.screenName = this.isDental ? "Dental Benefits Summary" : "Medical Benefits Summary";
        if (this.autoLogging) {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
            });
        }
    }

    fireGBEEvent() {
        if (!this.gbeEventFired) {
            this.dispatchEvent(new CustomEvent('gbedata'));
            this.gbeEventFired = true;
        }
    }

    fireMBEEvent() {
        if (!this.mbeEvenbtFired) {
            this.dispatchEvent(new CustomEvent('mbedata'));
            this.mbeEvenbtFired = true;
        }
    }


    @api setGBEData(data, recordId) {
        this.recordId = recordId;
        this.gberesponse = data;
        this.processGBEData();
    }

    @api setMBEData(data, recordId) {
        this.recordId = recordId;
        this.mberesponse = data;
        this.processMBEData();
    }

    get generateLogId() {
        return Math.random().toString(16).slice(2);
    }


    createRelatedField() {
        return [{
            label: 'Plan Member Id',
            value: this.memberPlanData.Name
        }];
    }


    renderedCallback() {
        Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }


    handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                event,
                this.createRelatedField(),
                this.screenName,
                this.loggingkey,
                this.pageRef
            );
        } else {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(
                        event,
                        this.createRelatedField(),
                        this.screenName,
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }


    render() {
        if (this.labels.NonCommercialMedicalPol.includes(this.memberPlanData?.Plan?.Source_Major_LOB__c)
            || this.labels.NonCommercialMedicalPol.includes(this.memberPlanData?.Plan?.Major_LOB__c)) {
            return benefitSummaryNonCommercial;
        } else if (this.isDental) {
            this.screenName = 'Dental Benefits Summary';
            return benefitSummaryCommercialDental
        } else {
            this.screenName = 'Medical Benefits Summary';
            return benefitSummaryCommercial;
        }
    }

    processPBEData() {
        this.PBEData = {};
        this.PBEData.DeductibleType = this.pberesponse?.Deductible?.TypeCode ?? '';
        this.PBEData.hdhp = this.pberesponse?.IsHDHP ?? '';
        this.PBEData.planOption = this.pberesponse?.planOption ?? '';
        this.PBEData.packageinfo = this.pberesponse?.PackageInfo?.AccumulationPeriod ?? '';
    }


    setPBEData(pberesponse, recordId, memberplandata) {
        this.pberesponse = pberesponse;
        this.recordId = recordId;
        this.memberPlanData = memberplandata;
        this.processPBEData();
    }


    @api
    displayErrorMessage(serviceError, message) {
        this.serviceerror = serviceError;
        this.message = message;
        this.isMBELoading = false;
        this.isGBELoading = false;
    }


    @wire
        (getRecord, { recordId: USER_ID, fields: USER_FIELDS })
    wireUserRecord({ error, data }) {
        if (data) {
            try {
                this.userRecord = data;
                this.netWorkId = data.fields.Network_User_Id__c.value;
                if (this.netWorkId != undefined && (!this.labels.NonCommercialMedicalPol.includes(this.memberPlanData?.Plan?.Major_LOB__c)
                    && !this.labels.NonCommercialMedicalPol.includes(this.memberPlanData?.Plan?.Source_Major_LOB__c))) {
                    this.getBillingData();
                }
            } catch (e) {
                console.log('An error occured when handling the retrieved user record data');
            }
        } else if (error) {
            console.log('An error occured when retrieving the user record data: ' + JSON.stringify(error));
        }
    }




    getBillingData() {
        this.getPlatformValue(this.memberPlanData.Policy_Platform__c);
        getBillingProfiles({
            sNetworkID: this.netWorkId, sSearchID: this.memberPlanData.Source_Coverage_ID__c, sPlatform: this.platformValue,
            sExchangeType: this.memberPlanData.Exchange_Type__c,
            sProductType: this.memberPlanData.Product_Type__c, sProduct: this.memberPlanData.Product__c
        }).then(result => {
            if (result == null || result == undefined || result == '') {
                this.BillingData.PaidThroughDate = '';
            }
            else {
                this.BillingData.PaidThroughDate = JSON.parse(result);
            }
        }).catch(err => {
            console.log("Error occured in getBillingData- " + JSON.stringify(err));
        });
    }

    getPlatformValue(platformCode) {
        if (platformCode == VOB_PLATFORM_LV_HUM || platformCode == VOB_PLATFORM_CI_HUM || platformCode == VOB_PLATFORM_CAS_HUM) {
            this.platformValue = VOB_PLATFORM_CAS_HUM;
        }
        else if (platformCode == VOB_PLATFORM_EM_HUM || platformCode == VOB_PLATFORM_MTV_HUM) {
            this.platformValue = VOB_PLATFORM_MTV_HUM;
        }
    }

    async navigateToPlanPage() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.recordId,
                objectApiName: 'MemberPlan',
                actionName: 'view'
            },
        }
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                pageReference: pageref
            });
        }
    }


    processMBEData() {
        this.isMBELoading = false;
        if (this.mberesponse) {
            if (this.mberesponse?.serviceError) {
                this.serviceerror = true;
                this.message = this.labels.BenefitMBEServiceError;
            } else {
                this.MBEData = {};
                //default value for Retire and Cobra
                this.MBEData.Retire = 'False';
                this.MBEData.Cobra = 'False';
                this.MBEData.paidThruDate = this.mberesponse.paidThruDate ? this.mberesponse.paidThruDate : '';
                setTimeout(() => {
                    if (this.isDental) {
                        if ((this.BillingData.PaidThroughDate != '') &&
                            (this.memberPlanData.GroupNumber != '684668' && this.memberPlanData.Product_Type_Code__c != 'HM1TRP')) {
                            this.paidThruDateMBE = this.BillingData.PaidThroughDate;
                        }
                        else if (this.memberPlanData.GroupNumber === '684668' && this.memberPlanData.Product_Type_Code__c === 'HM1TRP') {
                            this.paidThruDateMBE = this.MBEData.paidThruDate;
                        }
                        else {

                            this.paidThruDateMBE = this.MBEData.paidThruDate;
                        }
                    }
                }, 1000);
                Object.keys(this.mberesponse).forEach(k => {
                    switch (k) {
                        case "IndicatorList":
                            if (this.mberesponse && this.mberesponse?.IndicatorList && this.mberesponse?.IndicatorList?.Indicator
                                && Array.isArray(this.mberesponse.IndicatorList.Indicator)) {
                                this.mberesponse.IndicatorList.Indicator.forEach(k => {
                                    switch (k.Name) {
                                        case "OutOfAreaIndicator":
                                            this.MBEData.OutOfAreaIndicator = k.Value;
                                            break;
                                        case "Retire":
                                            this.MBEData.Retire = k.Value;
                                            break;
                                    }
                                })
                            }
                            break;
                        case "Network":
                            if (this.mberesponse && this.mberesponse?.Network) {
                                this.MBEData.NetworkDesc = [];
                                Object.keys(this.mberesponse.Network).forEach(k => {
                                    switch (k) {
                                        case "CASH1Network":
                                            this.MBEData.NetworkDesc.push({
                                                label: 'Hospital Network 1 ',
                                                value: this.mberesponse?.Network[k] ?? 'null'
                                            })
                                            break;
                                        case "CASH2Network":
                                            this.MBEData.NetworkDesc.push({
                                                label: 'Hospital Network 2 ',
                                                value: this.mberesponse?.Network[k] ?? 'null'
                                            })
                                            break;
                                        case "CASD1Network":
                                            this.MBEData.NetworkDesc.push({
                                                label: 'Provider Network 1 ',
                                                value: this.mberesponse?.Network[k] ?? 'null'
                                            })
                                            break;
                                        case "CASD2Network":
                                            this.MBEData.NetworkDesc.push({
                                                label: 'Provider Network 2 ',
                                                value: this.mberesponse?.Network[k] ?? 'null'
                                            })
                                            break;
                                    }
                                })
                                this.MBEData.NetworkDesc.sort(this.compareValues('label', 'asc'))
                                if (this.MBEData.NetworkDesc.length > 0) this.hideShowLink = "Hide";
                            }
                            break
                        case "CoverageType":
                            this.MBEData.CoverageType = this.mberesponse[k];
                            break;
                        case "PolicyIndicatorList":
                            if (this.mberesponse && this.mberesponse?.PolicyIndicatorList &&
                                this.mberesponse?.PolicyIndicatorList?.Indicator && Array.isArray(this.mberesponse.PolicyIndicatorList.Indicator)) {
                                this.mberesponse.PolicyIndicatorList.Indicator.forEach(k => {
                                    if (k.Name === 'CobraIndicator') {
                                        this.MBEData.Cobra = k.Value;
                                    }
                                })
                            }
                    }
                })
            }
        }
    }

    processGBEData() {
        this.isGBELoading = false;
        if (this.gberesponse) {
            this.GBEData.Certificate = this.gberesponse?.Certificate ?? '';
            this.GBEData.MHVenderCode = this.gberesponse?.MHVenderCode ?? '';
            this.GBEData.MaxDependentAge = this.gberesponse?.MaxDependentAge ?? '';
            this.GBEData.MaxStudentAge = this.gberesponse?.MaxStudentAge ?? '';
            this.GBEData.Market = this.gberesponse?.Market ?? '';
            this.GBEData.SellingLedgerNumber = this.gberesponse?.SellingLedgerNumber ?? '';
            this.GBEData.SellingLedgerDescription = this.gberesponse?.SellingLedgerDescription ?? '';
            this.GBEData.LastRenewalDate = this.gberesponse?.LastRenewalDate ?? '';
            this.GBEData.GBENetworkDesc = this.gberesponse?.GBENetworkDesc ?? '';
        }
    }

    onHideClick() {
        let divele = this.template.querySelector(`[data-key="NetworkHide"]`);
        if (divele) {
            divele.classList.toggle('slds-hide');
        }
        let divele1 = this.template.querySelector(`[data-key="NetworkShow"]`);
        if (divele1) {
            divele1.classList.toggle('slds-hide');
        }
        this.hideShowLink = "Show";
    }

    onShowClick() {
        let divele = this.template.querySelector(`[data-key="NetworkShow"]`);
        if (divele) {
            divele.classList.toggle('slds-hide');
        }
        let divele1 = this.template.querySelector(`[data-key="NetworkHide"]`);
        if (divele1) {
            divele1.classList.toggle('slds-hide');
        }
        this.hideShowLink = "Hide";
    }


    onNetworkClick(event) {
        let mentorCode = event.currentTarget.dataset.val;
        this.openHumanaCodesWindow(mentorCode);
    }


    openHumanaCodesWindow(sMentorCodeParam) {
        if (sMentorCodeParam) {
            let sMentorCode = sMentorCodeParam.trim();
            let documentUrl = '/c/searchcodesetapp.app?mentor_Code=' + window.btoa(sMentorCode) + '&filter_CodeSet=' + window.btoa('Super Netwk Nbrs');
            window.open(documentUrl, "_blank", "top=200 left=200 width=900, height=500, toolbar=no,resizable=yes,scrollbars=yes,location=no");
        }
    }


    compareValues(key, order = 'asc') {
        let sKey = key;
        return function innerSort(a, b) {
            if (!a.hasOwnProperty(key) || !b.hasOwnProperty(key)) {
                return 0;
            }
            let comparison = 0;


            const varA = (typeof a[key] === 'string')
                ? a[key].toUpperCase() : a[key];
            const varB = (typeof b[key] === 'string')
                ? b[key].toUpperCase() : b[key];


            if (varA > varB) {
                comparison = 1;
            } else if (varA < varB) {
                comparison = -1;
            }


            return (
                (order === 'desc') ? (comparison * -1) : comparison
            );
        };
    }
}