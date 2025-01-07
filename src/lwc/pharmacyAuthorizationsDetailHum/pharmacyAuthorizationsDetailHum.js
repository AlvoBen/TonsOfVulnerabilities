/*
JS Controller        : PharmacyAuthorizationsDetailHum
Version              : 1.0
Created On           : 10/14/2021
Function             : Component to display to pharmacy authorization details

Modification Log: 
* Developer Name                    Date                         Description
* Nirmal Garg                       10/14/2021                   Original Version
* Divya Bhamre                    11/02/2022                     US - 3833519
* Apurva Urkude                    11/08/2022                    US-  3747520
* Pinky Vijur						04/03/2023					  User Story 4401068: C04; Authorization/Referral Verification; LIGHTNING--T1PRJ0891742-CRM IOP-2022-6037203—Pharmacy Authorization Details: add ‘Department’, ‘Coverage Start Date’ & ‘Coverage End Date’
* Nirmal Garg				        06/23/2023                   US - 4762833 - Fix Pharmacy authorization scrolling and keyword search issue
*------------------------------------------------------------------------------------------------------------------------------
*/

import { api, LightningElement, track, wire } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import { MessageContext } from 'lightning/messageService';
import getHighlightPanelDetails from '@salesforce/apex/AuthSummary_LC_HUM.getMemberAccount';
import paHUBUS4250871SwitchValue from "@salesforce/apex/PharmacyBenefits_LC_HUM.paHUBUS4250871SwitchValue";
//Global Constants for this JS controller
const CHAR_LIMIT = 80;

export default class PharmacyAuthorizationsDetailHum extends LightningElement {
    @api auth;
    @track recordId;
    @track isReadMoreVisible = false;
    @track RelatedName
    @track displayPopOver = false;
    @track loggingkey;
    showloggingicon = true;
    autoLogging = true;
    @track startLogging = false
    @track showDateAndDept = true;
    @api calledfrompharmacy = false;

    @wire(CurrentPageReference)
    pageRef;


    connectedCallback() {
        if (this.autoLogging) {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
            });
        }
        paHUBUS4250871SwitchValue().then((result) => {
            this.showDateAndDept = result;
        });
        this.recordId = this.pageRef?.attributes?.attributes?.C__Id ?? null;
        if (this.recordId && this.autoLogging) {
            this.getAuthorizationsData();
        }
    }


    getAuthorizationsData() {
        const me = this;
        const oData = getHighlightPanelDetails({
            sRecId: this.recordId
        }).then((oData) => {
            this.RelatedName = oData.Name;
        }).catch((error) => {
            console.log('Error occured: ', error);
        });
    }

    closePopOver() {
        this.displayPopOver = false;
    }

    displayReadMore() {
        this.displayPopOver = true;
    }

    get getdrugname() {
        if (this.auth?.drugName) {
            let index = this.auth?.drugName?.indexOf(" ") ?? 0;
            return this.auth?.drugName?.substring(0, index)?.trim() ?? '';
        }
        return '';
    }

    get getdrugcontent() {
        if (this.auth?.drugName) {
            let index = this.auth?.drugName?.indexOf(" ") ?? 0;
            return this.auth?.drugName?.substr(index)?.trim() ?? '';
        }
        return '';
    }

    get getrxdata() {
        if (this.auth?.drugName) {
            let index_a = this.auth?.drugName?.indexOf(" ") ?? 0;
            let drugname = this.auth?.drugName?.substring(0, index_a)?.trim() ?? '';
            let index_b = this.auth?.drugName?.indexOf(" ") ?? 0;
            let drugcontent = this.auth?.drugName?.substr(index_b)?.trim() ?? '';
            let recdate = this.auth?.creationDate ?? '';
            let decdate = this.auth.decisionDate;
            let rxdata = drugname + '/' + drugcontent + ',' + recdate + ',' + decdate;
            return rxdata;
        }
        return '';
    }


    @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    pageRef;

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => { });
    }

    handleLogging(event) {
        if (!this.calledfrompharmacy) {
            if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                performLogging(
                    event,
                    this.createRelatedField(),
                    'Authorizations',
                    this.loggingkey,
                    this.pageRef
                );
            } else {
                getLoggingKey(this.pageRef).then((result) => {
                    this.loggingkey = result;
                    if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                        performLogging(
                            event,
                            this.createRelatedField(),
                            'Authorizations',
                            this.loggingkey,
                            this.pageRef
                        );
                    }
                });
            }
        }
    }

    createRelatedField() {
        return [
            {
                label: 'Plan Member Name',
                value: this.RelatedName
            }
        ]
    }

    get qtysupplydata() {
        if (this.auth?.quantity && this.auth?.daysSupply) {
            let data = (this.auth?.quantity ?? '') + '/' + (this.auth?.daysSupply ?? '');
            return data;
        }
        return '';
    }

    get generateLogId() {
        return Math.random().toString(16).slice(2);
    }

    get getOutcomeVerbiage() {
        if (this.auth?.denialLanguage) {
            return this.auth?.denialLanguage?.length > CHAR_LIMIT ? this.auth?.denialLanguage.substring(0, CHAR_LIMIT) + '...' : this.auth.sDenialLanguage;
        }
        return '';
    }

    get getOutcomeValue() {
        if (this.auth?.authDescription?.toUpperCase() == 'APPROVED'
            || this.auth?.authDescription?.toUpperCase() == 'PARTIALLY APPROVED') {
            return true;
        }
        else { return false; }
    }
}