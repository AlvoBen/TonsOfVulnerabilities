/*
LWC Name        : benefitsAccumsDetailbHum.html
Function        : LWC to display Benefit Current and Previous Tab 

Modification Log:
* Developer Name                  Date                         Description
* Swapnali Sonawane               05/27/2022                   US-3143662 - Medical Plan - Benefit Accumulators
* Swapnali Sonawane               07/12/2022                   US-3312671 -  Benefits-  Lightning Build of Medical Plan: Logging for Accums
* Jonathan Dickinson              07/29/2022                   US-3580751 - Dental Plan - Benefit Accumulators
* Jonathan Dickinson		      08/15/2022		           US-3699864
* Swapnali Sonawane               08/16/2022                   DF - 5849 On page load, not able to select logable fields. 
* Abhishek Mangutkar              06/14/2022                   US - 4703750 - Fix Table design for accums  per standard 
*************************************************************************************************************************** */
import { LightningElement, api, track, wire } from 'lwc';
import getAccumsResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.getAccumsDataREST';
import { performTableLogging, getLoggingKey, checkloggingstatus } from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import HUMNoRecords from "@salesforce/label/c.HUMNo_records_to_display";
import { getModel } from './layoutConfig';
const SORT_DESC = 'utility:arrowdown';
const SORT_ASC = 'utility:arrowup';

export default class BenefitsAccumsDetailHum extends LightningElement {
    @api memberId;
    @track accumsResponse;
    @api recId;
    @api benefitType;
    @track benefitCurrent = false;
    @track isLoading = true;
    @track columns = getModel();
    @track loggingkey;
    @track tableData;
    @track screenName = 'Benefits - Accums';
    showAccumsTable = false;
    sortIconType;
    @api isDental;

    @track relatedInputField = [{
        label: "Plan Member Id",
        value: this.memberId
    }, {
        label: "Section",
        value: "Benefits - Current"
    }, {
        label: "Accumulater",
        mappingField: "sAccumulator"
    }];

    @track labels = {
        HUMNoRecords
    };
    @track totalCount = 0;


    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        this.loggingkey = getLoggingKey(this.pageRef);
    }

    connectedCallback() {
        this.loadCommonCSS();
        if (this.benefitType == 'Current') {
            this.benefitCurrent = true;
            this.screenName = 'Benefits Current';
        }
        else { this.screenName = 'Benefits Previous'; }
        this.getAccumsData();
        if (this.relatedInputField[0].value == undefined) this.relatedInputField[0].value = this.memberId;
    }

    handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performTableLogging(event, this.tableData, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);

        } else {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performTableLogging(event, this.tableData, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);
                }
            });
        }
    }

    //load css
    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
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


    getAccumsData() {
        getAccumsResponse({ personId: this.recId, typeOfbenefit: this.benefitType, refDate: '' })
            .then(result => {
                if (result) {
                    let accumData = JSON.parse(result);
                    let accumList = accumData?.lstAccumsData ?? null;
                    if (accumList?.length > 0) {
                        accumList.forEach(function (item) {
                            if (item.sLimit == '$9999999.99')
                                item.sLimit = 'No Limit';
                        });
                        this.sortAccumsTable(accumList, 'DESC');
                        this.sortIconType = SORT_DESC;
                        let fromColumn = this.columns.find(item => item.label === 'From');
                        if (fromColumn) {
                            fromColumn.showSortIcon = true;
                        }
                        this.accumsResponse = accumList;
                    }
                    this.isLoading = false;
                } else {
                    this.isLoading = false;
                    this.accumsResponse = [];
                }
                this.tableData = this.accumsResponse;
                this.showAccumsTable = this.accumsResponse?.length > 0 ? true : false;
                this.isLoading = false;
                let tCount = Array.isArray(this.accumsResponse) ? this.accumsResponse?.length : 0;
                this.totalCount = tCount > 6 ? '6+' : tCount;
            })
            .catch(err => {
                this.isLoading = false;
                console.log("Error occured in getAccumsData- " + JSON.stringify(err));
            });
    }
    /**
    * Applies pre-selected filters to subtab table
    * and CSS from utility commonstyles file
    * after DOM is rendered
    */
    renderedCallback() {
        Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    handleSort() {
        this.sortIconType = this.sortIconType === SORT_DESC ? SORT_ASC : SORT_DESC;
        if (this.accumsResponse?.length > 0) {
            let sortType = this.sortIconType === SORT_DESC ? 'DESC' : 'ASC';
            this.sortAccumsTable(this.accumsResponse, sortType);
        }
    }

    sortAccumsTable(data, order) {
        let dateComparisonMultiplier = order === 'DESC' ? -1 : 1;
        data.sort((a, b) => {
            if (a.sFrom && b.sFrom) {
                return dateComparisonMultiplier * (new Date(a.sFrom) - new Date(b.sFrom)) || a.sAccumulator.localeCompare(b.sAccumulator);
            } else {
                if (!a.sFrom && !b.sFrom) {
                    return a.sAccumulator.localeCompare(b.sAccumulator)
                } else if (!a.sFrom) {
                    return 1;
                } else if (!b.sFrom) {
                    return -1;
                }
            }
        });
    }
}