/*******************************************************************************************************************************
LWC JS Name : Authsummary_medical_authorization_filter_lwc_hum.js
Function    : This JS serves as controller to Authsummary_medical_authorization_filter_lwc_hum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Rajesh Narode                                           14/07/2022                    User story 3362701 Authorization Summary table filter.
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
const PLACEHOLDER = 'Placeholder';
export default class Authsummary_medical_authorization_filter_lwc_hum extends LightningElement {
    @api overallStatus;
    @api authTypes;
    @api admissionServiceType;
    @api filteredData;
    @track filters = {
        keyword: '',
        dropDown: []
    };
    @track filterValue = '';
    status;
    isEventChange = false;
    @track atPlaceholder = PLACEHOLDER;
    @track osPlaceholder = PLACEHOLDER;
    @track adPlaceholder = PLACEHOLDER;
    @track placeholderMessages = {
        authMessage: 'Auth Type',
        overallStatusMessage: 'Overall Status',
        adServiceTypeMessage: 'Admission/Service Type'
    };

    @track selectedPicklistValues = {
        authValue: [],
        statusValues: [],
        serviceTypeValues: []
    };
    @track keyArray = [];

    get isFilterAdded() {
        if (!this.isEventChange) {
            this.populateFilter();
        }

        return (
            this.filters.keyword ||
            this.filters.dropDown.some(
                (ele) => ele.selectedvalues.length > 0
            ) ||
            (this.filteredData &&
                this.filteredData.dropDown.length > 0 &&
                (this.filteredData.keyword ||
                    this.filteredData.dropDown.some(
                        (ele) => ele.selectedvalues.length > 0
                    ))) ||
            this.filterValue
        );
    }

    applyFilter() {
        if (this.filterValue) {
            this.filters.keyword = this.filterValue;
        }

        if (this.filteredData && this.filteredData.dropDown.length > 0) {
            this.filteredData.dropDown.forEach((ele) => {
                if (ele.keyname === 'authType') {
                    if (
                        ele.selectedvalues &&
                        ele.selectedvalues.length > 0 &&
                        this.filters &&
                        this.filters.dropDown.length > 0
                    ) {
                        this.filters.dropDown.forEach((item) => {
                            if (!this.keyArray.includes(ele.keyname)) {
                                const arr = {
                                    keyname: ele.keyname,
                                    selectedvalues: ele.selectedvalues
                                };
                                this.filters.dropDown.push(arr);
                            }
                        });
                    }
                } else if (ele.keyname === 'overallStatus') {
                    if (
                        ele.selectedvalues &&
                        ele.selectedvalues.length > 0 &&
                        this.filters &&
                        this.filters.dropDown.length > 0
                    ) {
                        this.filters.dropDown.forEach((item) => {
                            if (!this.keyArray.includes(ele.keyname)) {
                                const arr = {
                                    keyname: ele.keyname,
                                    selectedvalues: ele.selectedvalues
                                };
                                this.filters.dropDown.push(arr);
                            }
                        });
                    }
                } else if (ele.keyname === 'adServiceType') {
                    if (
                        ele.selectedvalues &&
                        ele.selectedvalues.length > 0 &&
                        this.filters &&
                        this.filters.dropDown.length > 0
                    ) {
                        this.filters.dropDown.forEach((item) => {
                            if (!this.keyArray.includes(ele.keyname)) {
                                const arr = {
                                    keyname: ele.keyname,
                                    selectedvalues: ele.selectedvalues
                                };
                                this.filters.dropDown.push(arr);
                            }
                        });
                    }
                }
            });
        }

        this.handleEventDispatch();
    }

    handleChange(event) {
        this.filterValue = event.target.value;
        this.filters.keyword = this.filterValue;
        this.isEventChange = true;
        if (
            this.filters.dropDown.length === 0 &&
            this.filteredData &&
            this.filteredData.dropDown.length > 0
        ) {
            this.filteredData.dropDown.forEach((ele) => {
                this.filters.dropDown.push(ele);
            });
        }
        if (!this.filterValue) {
            this.handleEventDispatch();
        }
    }

    handleClearFilter() {
        this.filterValue = '';
        this.filters.keyword = '';
        this.filters.dropDown = [];
        this.atPlaceholder = PLACEHOLDER;
        this.osPlaceholder = PLACEHOLDER;
        this.adPlaceholder = PLACEHOLDER;

        this.handleEventDispatch();
    }

    handleEventDispatch() {
        this.dispatchEvent(
            new CustomEvent('handlefilter', { detail: this.filters })
        );
    }

    handleClose() {
        this.dispatchEvent(new CustomEvent('handleclose'));
    }

    populateFilter() {
        if (this.filteredData && this.filteredData.keyword) {
            this.filterValue = this.filteredData.keyword;
        }

        if (this.filteredData && this.filteredData.dropDown.length > 0) {
            this.filteredData.dropDown.forEach((ele) => {
                if (ele.keyname === 'authType') {
                    this.selectedPicklistValues.authValue = ele.selectedvalues;
                    this.atPlaceholder = ele.selectedvalues[0];
                } else if (ele.keyname === 'overallStatus') {
                    this.selectedPicklistValues.statusValues =
                        ele.selectedvalues;
                    this.osPlaceholder = ele.selectedvalues[0];
                } else if (ele.keyname === 'adServiceType') {
                    this.selectedPicklistValues.serviceTypeValues =
                        ele.selectedvalues;
                    this.adPlaceholder = ele.selectedvalues[0];
                }
            });
        }
    }

    handleSelectedValue({ detail }) {
        this.isEventChange = true;
        if (detail.title === this.placeholderMessages.authMessage) {
            this.atPlaceholder = detail.value;
            const arr = { keyname: 'authType', selectedvalues: [detail.value] };
            this.filters.dropDown.push(arr);
            this.keyArray.push(arr.keyname);
        }
        if (detail.title === this.placeholderMessages.overallStatusMessage) {
            this.osPlaceholder = detail.value;
            const arr = {
                keyname: 'overallStatus',
                selectedvalues: [detail.value]
            };
            this.filters.dropDown.push(arr);
            this.keyArray.push(arr.keyname);
        }
        if (detail.title === this.placeholderMessages.adServiceTypeMessage) {
            this.adPlaceholder = detail.value;
            const arr = {
                keyname: 'adServiceType',
                selectedvalues: [detail.value]
            };
            this.filters.dropDown.push(arr);
            this.keyArray.push(arr.keyname);
        }
    }
}