/*
LWC Name        : BenefitsWaitingPeriodHum.js
Function        : JS file for Benefits Waiting Period.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar             06/01/2023                   US - 4556179 - Dental Plan - Dental Waiting Periods
* Nirmal Garg                    06/01/2023                   US - 4648707 - Dental UI Logging Riders - Waiting periods
****************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import HUMNoRecords from "@salesforce/label/c.HUMNo_records_to_display";
import { getCustomMetadataValues } from 'c/genericReadCustomMetadataValues';
import { loadStyle } from 'lightning/platformResourceLoader';
import { performTableLogging, getLoggingKey, checkloggingstatus } from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { getModel } from './layoutConfig';
import BenefitMBEServiceError from '@salesforce/label/c.BenefitMBEServiceError';

const INITIAL_RECORDS = 10;
export default class BenefitsWaitingPeriodHum extends LightningElement {

    @api serviceerror;
    @api message;
    @api mberesponse;
    @api memberplanname;
    @track data = [];
    @track initialload = true;
    @track isDataAvailable = false;
    @track filteredCount = 0;
    @track totalCount = 0;
    @track totalWaitingPeriodList = [];
    @track filteredWaitingPeriodList = [];
    @track loaded = false;
    @track columns;
    @track screenName = "Dental Benefits Waiting Periods";
    @track loggingkey;
    @track pageRef;
    labels = {
        HUMNoRecords,
        BenefitMBEServiceError
    };

    constructor() {
        super();
        this.getCustomMetadata();
    }

    getCustomMetadata() {
        this.columns = getModel();
    }

    connectedCallback() {
        this.initialSetUp();
    }


    initialSetUp() {
        this.loadCommonCSS();
        getLoggingKey(this.pageRef).then(result => {
            this.loggingkey = result;
        });
        if (this.relatedInputField[0].value == undefined) this.relatedInputField[0].value = this.memberplanname;
        if (!this.serviceerror && this.mberesponse && Object.keys(this.mberesponse).length > 0) {
            this.setWaitingPeriodData();
        } else if (this.serviceerror === null || this.serviceerror === undefined || !this.serviceerror) {
            this.fireEventForData();
        }
        this.loaded = true;
    }

    //load css
    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    @track relatedInputField = [{
        label: "Plan Member Id",
        value: this.memberplanname
    }, {
        label: "Section",
        value: "Waiting Periods"
    }, {
        label: "Description",
        mappingField: "Description"
    }];

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        getLoggingKey(this.pageRef).then(result => {
            this.loggingkey = result;
        });
    }

    setWaitingPeriodData() {
        this.loaded = true;
        this.processMBEData();
    }

    fireEventForData() {
        this.dispatchEvent(new CustomEvent('mbedata'));
    }

    processMBEData() {
        this.totalWaitingPeriodList = [];
        this.filteredWaitingPeriodList = [];
        if (this.mberesponse && this.mberesponse?.serviceError) {
            //show error
            this.message = this.labels.BenefitMBEServiceError;
            this.serviceerror = true;
            this.loaded = true;
        }
        else {
            if (this.mberesponse && this.mberesponse?.WaitingPeriodList && this.mberesponse?.WaitingPeriodList?.WaitingPeriod
                && Array.isArray(this.mberesponse?.WaitingPeriodList?.WaitingPeriod) && this.mberesponse?.WaitingPeriodList?.WaitingPeriod?.length > 0) {
                //map data
                this.dataFound = true;
                this.loaded = true;
                this.mberesponse.WaitingPeriodList.WaitingPeriod.forEach(k => {
                    this.totalWaitingPeriodList.push({
                        Id: this.getUniqueId(),
                        description: k?.ServiceCategoryDescription ?? '',
                        startDate: k?.BeginDate ?? '',
                        endDate: k?.EndDate ?? '',
                        waiveAccident: k?.WaitingPeriodIndicatorList?.Indicator[0]?.Value ?? '',
                        personPriorCoverageReduction: k?.WaitingPeriodIndicatorList?.Indicator[1]?.Value ?? '',
                        memberEligibiltyReduction: k?.WaitingPeriodIndicatorList?.Indicator[2]?.Value ?? ''
                    })
                });
                this.getFilteredWaitingPeriods();
            }
            else {
                this.dataFound = false;
                this.loaded = true;
            }
        }
    }

    getFilteredWaitingPeriods() {
        if (this.totalWaitingPeriodList.length != 0) {
            this.dataFound = true;
            if (this.totalWaitingPeriodList.length > 0 && this.totalWaitingPeriodList.length <= INITIAL_RECORDS) {
                this.filteredWaitingPeriodList = this.totalWaitingPeriodList;
                this.totalCount = this.totalWaitingPeriodList.length;
                this.filteredCount = this.totalCount;
            } else {
                this.filteredWaitingPeriodList = this.totalWaitingPeriodList.slice(0, INITIAL_RECORDS);
                this.totalCount = this.totalWaitingPeriodList.length;
                this.filteredCount = this.totalCount > 0 ? INITIAL_RECORDS : 0;
            }
        }
    }

    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.totalWaitingPeriodList.length <= (this.filteredCount + INITIAL_RECORDS)) {
                this.totalCount = this.totalWaitingPeriodList.length;
                this.filteredCount = this.totalCount;
                this.filteredWaitingPeriodList = this.totalWaitingPeriodList;
            } else {
                this.totalCount = this.totalWaitingPeriodList.length;
                this.filteredCount = (this.filteredCount + INITIAL_RECORDS)
                this.filteredWaitingPeriodList = this.totalWaitingPeriodList.slice(0, (this.filteredCount + INITIAL_RECORDS));
            }
        }
    }

    getUniqueId() {
        return Math.random().toString(16).slice(2);
    }

    @api
    setMBEData(mbeServiceData) {
        this.mberesponse = mbeServiceData;
        this.processMBEData();
    }

    @api
    displayErrorMessage(serviceError, message) {
        this.serviceerror = serviceError;
        this.message = message;
        this.loaded = true;
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    createRelatedField() {
        return [{
            label: 'Related Field',
            value: this.relatedInputField
        }];
    }


    handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performTableLogging(event, this.filteredWaitingPeriodList, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);

        } else {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performTableLogging(event, this.filteredWaitingPeriodList, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);
                }
            });
        }
    }
}