/*
Function        : LWC for History timeline filter component.

Modification Log:
* Developer Name                  Date                         Description
*-----------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                    31/8/2023                    US-4908765-Mail Order Management - Pharmacy - OMS Originated Notes and profile fix
* Jonathan Dickinson               02/29/2024                   User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
* Jonathan Dickinson               03/01/2024                   User Story 5645671: T1PRJ1374973 - DF8442-8443-8445; REGRESSION_Lightning_HPIE; History Timeline on Order Summary Detail populates all results instead of groups of 10 and History Timeline does not expand case comments
**************************************************************************************************************************************************/
import { LightningElement, wire, api, track } from 'lwc';
import { getHistoryTimelineModel } from './layoutConfig';
import generateQueryString from '@salesforce/apex/CaseHistoryComponent_LC_HUM.generatepharmacycasehistory';
import archivedLinkSwitchValue from "@salesforce/apex/CaseHistoryComponent_LC_HUM.archivedLinkSwitchValue";
import { getPickListValues, getLabels, hcConstants } from 'c/crmUtilityHum';
import { invokeWorkspaceAPI, openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import { CurrentPageReference } from 'lightning/navigation';
import caseHistoryInfoMsg from "@salesforce/label/c.HUMArchival_CaseHistoryInfo";
import { getPatientLognotesData, getFamilyLognotesData } from 'c/pharmacyHPIEIntegrationHum';
import { getCalculatedDate, getCalculatedDateDays, getFormatDate, getUniqueId } from 'c/crmUtilityHum';
const INITIAL_LOAD_RECORDS = 10;
export default class PharmacyHpieHistoryTimelineHum extends LightningElement {
    @api accountId;
    @api enterpriseId;
    @api userId;
    @api userProfile;
    @api recordId;
    @track logdata;
    @track casedetails;
    @track loaded = false;
    @track dateFilters = getHistoryTimelineModel("dateFilter");
    @track historyTimelineData = [];
    @track filteredcasedetails = [];
    @track filteredPatientNotes = [];
    @track filteredFamilyNotes = [];
    @track filterObj = {};
    @track pillFilterValues = [];
    @track showArchivedCase = true;
    @track dataFound = false;
    @api showbutton = false;
    @track filteredCount;
    @track totalCount;
    defaultDateFilterValue = "14";
    defaultLabelValue = "14";
    @track count = 0;
    sLabel;
    patientlognotes = [];
    familylognotes = [];
    @track showArchivalInfoMsg = caseHistoryInfoMsg;
    @wire(CurrentPageReference) pageRef;
    @track keyword = '';
    @track labels = getLabels();
    @track filterFldValues = {};
    @track searchEntered = false;
    @track startDate = getFormatDate(getCalculatedDate(new Date(), 0, -14, 0), 'yyyy-mm-dd');
    @track endDate = getFormatDate(getCalculatedDate(new Date(), 0, 0, 0), 'yyyy-mm-dd');
    @track totalResults = [];
    connectedCallback() {
        archivedLinkSwitchValue().then((result) => {
            this.showArchivedCase = result;
        });
        this.filterObj.sDate = {
            label: this.dateFilters.find(k => k.value === this.defaultLabelValue).label,
            value: this.defaultLabelValue
        };
        this.fireEventToParent(this.filterObj?.sDate?.value ?? 14);
        this.sLabel = this.dateFilters.find(k => k.value === this.defaultDateFilterValue).label;

    }

    fireEventToParent(days) {
        this.dispatchEvent(new CustomEvent('callservice', {
            detail: {
                days: days
            }
        }))
    }

    @api processData(data) {
        this.patientlognotes = data && data?.has('patient') ? data.get('patient') : null;
        this.familylognotes = data && data?.has('family') ? data.get('family') : null;
        this.casedetails = data && data?.has('cases') ? data.get('cases') : null;
        this.generateFilterData();
    }


    openArchivedCases() {
        openLWCSubtab('archivedCaseSearchHum', this.recordId, { label: 'Archived Case History', icon: 'standard:account' });
    }

    getDates() {
        let days = this.filterObj.hasOwnProperty('sDate') ? this.filterObj['sDate']?.value : 14;
        days = days === 'All' ? 179 : days;
        this.startDate = getFormatDate(getCalculatedDateDays(new Date(), 0, days, 0), 'yyyy-mm-dd');
    }

    addPillValues() {
        this.pillFilterValues = [];
        for (const [key, value] of Object.entries(this.filterObj)) {
            if (key === 'sDate') {
                this.pillFilterValues.push({
                    key: key,
                    value: value?.label ?? ''
                });
            } else {
                if (value && Array.isArray(value) && value?.length > 0) {
                    value.forEach(k => {
                        this.pillFilterValues.push({
                            key: key,
                            value: k
                        });
                    })
                } else if (typeof (value) === 'object') {
                    this.pillFilterValues.push({
                        key: key,
                        value: value?.value ?? ''
                    })
                } else {
                    this.pillFilterValues.push({
                        key: key,
                        value: value
                    })
                }
            }
        }
    }

    generateFilterData(data) {
        this.setDefaultValues();
        this.addPillValues();
        this.historyTimelineData = [];
        this.totalResults = [];
        this.generateFilteredCaseData();
        this.generateFilteredPatientData();
        this.generateFilteredFamilyData();
        this.filterResults();
        this.loaded = true;
    }

    setDefaultValues() {
        if (!this.filterObj.hasOwnProperty('sDate')) {
            this.filterObj.sDate = {
                label: this.dateFilters.find(k => k.value === this.defaultLabelValue).label,
                value: this.defaultLabelValue
            };
            this.sLabel = this.dateFilters.find(k => k.value === this.defaultLabelValue).label;
            this.defaultDateFilterValue = this.defaultLabelValue;
        }
    }

    filterResults() {
        this.totalCount = this.totalResults?.length ?? 0;
        this.filteredCount = this.totalCount < INITIAL_LOAD_RECORDS ? this.totalCount : INITIAL_LOAD_RECORDS;
        this.showbutton = this.totalCount <= INITIAL_LOAD_RECORDS ? true : false;
        this.totalResults = this.sortResult(this.totalResults);
        this.historyTimelineData = this.totalResults?.length > INITIAL_LOAD_RECORDS ? this.totalResults.slice(0, INITIAL_LOAD_RECORDS) : this.totalResults;
        this.dataFound = this.historyTimelineData?.length > 0 ? true : false;
        this.sLabel = this.filterObj["sDate"].label;
        this.defaultDateFilterValue = this.filterObj["sDate"].value;
        this.loaded = true;
    }

    sortResult(inputdata) {
        inputdata.sort(this.sortfunction);
        return inputdata;
    }

    sortfunction(a, b) {
        let dateA = new Date(a.createddatetime.split('|').reverse().join(' ').trim()).getTime();
        let dateB = new Date(b.createddatetime.split('|').reverse().join(' ').trim()).getTime();
        return dateA > dateB ? -1 : 1;
    }

    generateFilteredPatientData(data) {
        this.filteredPatientNotes = [];
        let tmp = [];
        if (this.patientlognotes && Array.isArray(this.patientlognotes) && this.patientlognotes?.length > 0) {
            if (this.filterObj.hasOwnProperty("sDate")) {
                if (this.filterObj["sDate"].value === "All") {
                    this.filteredPatientNotes = this.patientlognotes
                } else {
                    let pastdate = new Date();
                    pastdate.setDate(pastdate.getDate() - this.filterObj["sDate"].value);
                    if (this.patientlognotes && Array.isArray(this.patientlognotes) && this.patientlognotes?.length > 0) {
                        this.patientlognotes.forEach((c) => {
                            let omsdate = new Date(c?.createddatetime?.split("|")[1].trim() + 'UTC').getTime();
                            if (omsdate >= new Date(pastdate + 'UTC').getTime()) {
                                this.filteredPatientNotes.push(c);
                            }
                        });
                    }
                }
            }
            if (this.filterObj.hasOwnProperty("searchByWord")) {
                if (this.filteredPatientNotes && Array.isArray(this.filteredPatientNotes) && this.filteredPatientNotes.length > 0) {
                    let searchText = this.filterObj["searchByWord"].toLowerCase();
                    this.filteredPatientNotes.forEach(k => {
                        Object.values(k).forEach((h) => {
                            if (
                                h &&
                                JSON.stringify(h).toLocaleLowerCase().includes(searchText) &&
                                !tmp.includes(k)
                            ) {
                                tmp.push(k);
                            }
                        });
                    })
                    this.filteredPatientNotes = tmp.length > 0 ? tmp : [];
                }
            }
            if (
                (this.filterObj.hasOwnProperty("sClassification") &&
                    this.filterObj["sClassification"].length > 0) ||
                (this.filterObj.hasOwnProperty("sIntent") &&
                    this.filterObj["sIntent"].length > 0) ||
                (this.filterObj.hasOwnProperty("sStatus") &&
                    this.filterObj["sStatus"].length > 0)
            ) {
                this.filteredPatientNotes = [];
            }
            tmp = [];
            this.totalResults = this.totalResults.concat(this.filteredPatientNotes);
            this.filterResults();
        }

    }

    dateTimeFormat(dateTime) {
        let sDate = new Date(dateTime);
        let formatOptions = {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "numeric",
            minute: "numeric",
            hour12: true,
            timeZone: "GMT"
        }
        sDate = new Intl.DateTimeFormat('en-US', formatOptions).format(sDate);
        return `${sDate?.split(',')[1]?.trim()} | ${sDate?.split(',')[0]?.substring(0, 10)?.trim()}`
    }

    generateFilteredFamilyData() {
        this.filteredFamilyNotes = [];
        let tmp = [];
        if (this.familylognotes && Array.isArray(this.familylognotes) && this.familylognotes?.length > 0) {
            if (this.filterObj.hasOwnProperty("sDate")) {
                if (this.filterObj["sDate"].value === "All") {
                    this.filteredFamilyNotes = this.familylognotes;
                } else {
                    let pastdate = new Date();
                    pastdate.setDate(pastdate.getDate() - this.filterObj["sDate"].value);
                    if (this.familylognotes && Array.isArray(this.familylognotes) && this.familylognotes?.length > 0) {
                        this.familylognotes.forEach((c) => {
                            let omsdate = new Date(c?.createddatetime?.split("|")[1].trim() + 'UTC').getTime();
                            if (omsdate >= new Date(pastdate + 'UTC').getTime()) {
                                this.filteredFamilyNotes.push(c);
                            }
                        });
                    }
                }
            }
            if (this.filterObj.hasOwnProperty("searchByWord")) {
                if (this.filteredFamilyNotes && Array.isArray(this.filteredFamilyNotes) && this.filteredFamilyNotes?.length > 0) {
                    let searchText = this.filterObj["searchByWord"].toLowerCase();
                    this.filteredFamilyNotes.forEach(k => {
                        Object.values(k).forEach((h) => {
                            if (
                                h &&
                                JSON.stringify(h).toLocaleLowerCase().includes(searchText) &&
                                !tmp.includes(k)
                            ) {
                                tmp.push(k);
                            }
                        });
                    })
                    this.filteredFamilyNotes = tmp.length > 0 ? tmp : [];
                }
            }
            if (
                (this.filterObj.hasOwnProperty("sClassification") &&
                    this.filterObj["sClassification"].length > 0) ||
                (this.filterObj.hasOwnProperty("sIntent") &&
                    this.filterObj["sIntent"].length > 0) ||
                (this.filterObj.hasOwnProperty("sStatus") &&
                    this.filterObj["sStatus"].length > 0)
            ) {
                this.filteredFamilyNotes = [];
            }
            tmp = [];
            this.totalResults = this.totalResults.concat(this.filteredFamilyNotes);
            this.filterResults();
        }

    }

    generateFilteredCaseData(data) {
        this.filteredcasedetails = [];
        this.filterFldValues = [];
        let tmp = [];
        if (this.casedetails && Array.isArray(this.casedetails) && this.casedetails?.length > 0) {
            if (this.filterObj.hasOwnProperty("sDate")) {
                if (this.filterObj["sDate"].value === "All") {
                    this.filteredcasedetails = this.casedetails;
                } else {
                    let pastdate = new Date();
                    pastdate.setDate(pastdate.getDate() - this.filterObj["sDate"].value);
                    if (this.casedetails && Array.isArray(this.casedetails)
                        && this.casedetails.length > 0) {
                        this.casedetails.forEach((c) => {
                            let casedate = new Date(c?.createddatetime.split("|")[1].trim() + 'UTC').getTime();
                            if (casedate >= new Date(pastdate + 'UTC').getTime()) {
                                this.filteredcasedetails.push(c);
                            }
                        });
                    }
                }
            }
            if (this.filteredcasedetails && this.filteredcasedetails.length > 0) {
                this.filterFldValues = getPickListValues(
                    ["sClassification", "sIntent", "sStatus"],
                    this.filteredcasedetails
                );
            }
            if (this.filterObj.hasOwnProperty("searchByWord")) {
                if (this.filteredcasedetails && this.filteredcasedetails.length > 0) {
                    let searchText = this.filterObj["searchByWord"].toLowerCase();
                    this.filteredcasedetails.forEach((k) => {
                        Object.values(k).forEach((h) => {
                            if (
                                h &&
                                JSON.stringify(h).toLocaleLowerCase().includes(searchText) &&
                                !tmp.includes(k)
                            ) {
                                tmp.push(k);
                            }
                        });
                    });
                }
                this.filteredcasedetails = tmp.length > 0 ? tmp : [];
            }
            tmp = [];
            this.filteredcasedetails = this.performfilter(
                "sClassification",
                this.filteredcasedetails
            );
            this.filteredcasedetails = this.performfilter(
                "sIntent",
                this.filteredcasedetails
            );
            this.filteredcasedetails = this.performfilter(
                "sStatus",
                this.filteredcasedetails
            );
            this.totalResults = this.totalResults.concat(this.filteredcasedetails);
            this.filterResults();
        }
    }

    showMore() {
        if (this.filteredCount != undefined && this.totalCount != undefined) {
            if (this.filteredCount != undefined && this.totalCount != undefined) {
                if ((this.filteredCount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                    this.filteredCount = this.totalCount;
                    this.historyTimelineData = this.totalResults;
                    this.showbutton = true;
                } else {
                    this.filteredCount = this.filteredCount + INITIAL_LOAD_RECORDS;
                    this.historyTimelineData = this.totalResults.slice(0, this.filteredCount);
                }
            }
        }
    }

    handleShowMore() {
        this.showMore();
    }

    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            this.showMore();
        }
    }

    async handleLinkClick(event) {
        if (event && event?.detail) {
            let pageref = {
                type: 'standard__recordPage',
                attributes: {
                    recordId: event.detail.recordId,
                    objectApiName: event.detail.objectname,
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
    }

    clearTextBox() {
        let inputfields = this.template.querySelectorAll("lightning-input");
        inputfields.forEach(function (item) {
            item.value = "";
        });
        let inputelements = this.template.querySelectorAll("input");
        inputelements.forEach(function (item) {
            item.value = "";
        });
        if (this.keyword && this.keyword.length > 0) {
            this.keyword = "";
        }
    }

    clearSearchData() {
        let count = 1;
        if (this.filterObj.hasOwnProperty("searchByWord")) {
            delete this.filterObj["searchByWord"];
            count = 0;
        }
        this.generateFilterData();
        let ind = this.pillFilterValues.indexOf(
            this.pillFilterValues.find((element) => element.key === "searchByWord")
        );
        if (ind >= 0) {
            this.pillFilterValues.splice(ind, 1);
        }
        this.clearTextBox();
        this.searchEntered = false;

    }

    handleKeywordSearch(event) {
        let count = 1;
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value.length >= 3) {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
                this.filterObj[name] = value ?? null
            } else {
                this.filterObj[name] = value ?? null;
            }
            this.generateFilterData();
        } else {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
            }
            this.generateFilterData();
        }
        this.addPillValues();
    }

    handleChange(event) {
        this.historyTimelineData = [];
        this.filteredcasedetails = [];
        this.defaultDateFilterValue = event.detail.value;
        if (event) {  //on selection of filters by user
            let filterName = event.target.name;
            let filterValue = event.detail.value;
            if (filterName === "sDate") {
                this.sLabel = this.dateFilters.find(k => k.value === event.detail.value).label;
                this.fireEventToParent(event?.detail?.value ?? 14);
            }
        }
        if (this.filterObj.hasOwnProperty(event.target.name)) {
            this.filterObj[event.target.name] = {
                label: this.dateFilters.find(k => k.value === event.detail.value).label,
                value: event.detail.value
            }
        }
        else {
            this.filterObj[event.target.name] = {
                label: this.dateFilters.find(k => k.value === event.detail.value).label,
                value: event.detail.value
            }
        }

        this.addPillValues();
        this.generateFilterData();
    }


    handlePicklistFilter(event) {
        let filterdata = event.detail;
        if (filterdata != null && filterdata.selectedvalues.length > 0) {
            if (this.filterObj.hasOwnProperty(filterdata.keyname)) {
                this.filterObj[filterdata.keyname] = filterdata.selectedvalues;
            } else {
                this.filterObj[filterdata.keyname] = filterdata.selectedvalues;
            }

            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != filterdata.keyname);
            filterdata.selectedvalues.forEach(k => {
                this.pillFilterValues.push({
                    key: filterdata.keyname,
                    value: k
                })
            })
        }
        else {
            this.pillFilterValues = this.pillFilterValues.filter(option => option["key"] != filterdata.keyname);
            delete this.filterObj[filterdata.keyname];
        }

        this.generateFilterData();
    }

    performfilter(property, data) {
        let tmp = [];
        if (
            this.filterObj.hasOwnProperty(property) &&
            this.filterObj[property].length > 0
        ) {
            this.filterObj[property].forEach((c) => {
                if (data && data.length > 0) {
                    data.forEach((k) => {
                        if (
                            k &&
                            k.hasOwnProperty(property) &&
                            k[property] === c &&
                            !tmp.includes(k)
                        ) {
                            tmp.push(k);
                        }
                    });
                }
            });
        } else {
            tmp = data;
        }
        return tmp;
    }

    handleClearFilter() {
        this.pillFilterValues = [];
        this.clearTextBox();
        if (this.template.querySelector("c-generic-multiselect-picklist-hum") != null) {
            this.template
                .querySelectorAll("c-generic-multiselect-picklist-hum")
                .forEach((k) => {
                    k.clearDropDowns();
                });
        }
        this.filterObj = {};
        this.filterObj.sDate = {
            label: this.dateFilters.find(k => k.value === this.defaultLabelValue).label,
            value: this.defaultLabelValue
        }
        this.defaultDateFilterValue = this.defaultLabelValue;
        this.generateFilterData();
    }

    handlePillRemove(event) {
        this.pillFilterValues = [];
        Object.keys(this.filterObj).forEach(t => {
            if (t === event.target.dataset.key) {
                switch (event?.target?.dataset?.key) {
                    case "searchByWord":
                        delete this.filterObj['searchByWord'];
                        this.keyword = '';
                        if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
                            this.template.querySelector('c-generic-keyword-search-hum').clearSearchData();
                        }
                        break;
                    case "sDate":
                        delete this.filterObj['sDate'];
                        break;
                    default:
                        if (this.filterObj[event?.target?.dataset?.key]?.length > 1) {
                            this.filterObj[event?.target?.dataset?.key] = this.filterObj[event?.target?.dataset?.key]?.filter(t => t !== event?.target.dataset?.value);
                        } else {
                            delete this.filterObj[event?.target?.dataset?.key];
                        }
                        break;
                }
            }
        });
        this.generateFilterData();
        this.clearMultiSelect(event);
    }

    clearMultiSelect(event) {
        let payload = {
            keyname: event.target.dataset.key,
            value: event.target.dataset.value,
        };
        if (
            this.template.querySelector("c-generic-multiselect-picklist-hum") != null
        ) {
            this.template
                .querySelectorAll("c-generic-multiselect-picklist-hum")
                .forEach((k) => {
                    k.clearSelection(payload);
                });
        }
        if (this.template.querySelector('c-generic-filter-cmp-hum') != null) {
            this.template.querySelector('c-generic-filter-cmp-hum').clearMultiselect(payload);
        }
    }

    @api
    updateHistory() {
        this.fireEventToParent(this.defaultDateFilterValue ?? 14);
    }
}