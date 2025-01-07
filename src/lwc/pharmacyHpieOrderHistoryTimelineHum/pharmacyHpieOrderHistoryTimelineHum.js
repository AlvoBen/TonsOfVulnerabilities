/*
JS Controller        : pharmacyHpieOrderHistoryTimelineHum
Version              : 1.0
Created On           : 09/06/2023
Function             : Component to display to pharmacy order history timeline

Modification Log: 
* Developer Name                      Date                         Description
* Nirmal Garg                         09/06/2023                   Original Version
* Jonathan Dickinson			      03/01/2024				   User Story 5645671: T1PRJ1374973 - DF8442-8443-8445; REGRESSION_Lightning_HPIE; History Timeline on Order Summary Detail populates all results instead of groups of 10 and History Timeline does not expand case comments
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, track, api } from 'lwc';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { getDateDiffInDays } from 'c/crmUtilityHum';
const INITIAL_LOAD_RECORDS = 10;
export default class PharmacyHpieOrderHistoryTimelineHum extends LightningElement {

    @api orderId;
    @api orderCreatedDate;
    @track patientlognotes = [];
    @track familylognotes = [];
    @track casedetails = [];
    @track totalResults = [];
    @track loaded = false;
    @track dataFound = false;
    @track filteredCount;
    @track totalCount;
    @track historyTimelineData = [];
    @track showbutton = true;
    @track caseprocessed = false;
    @track patientprocessed = false;
    @track familyprocessed = false;


    connectedCallback() {
        this.fireEventToParent();
    }

    fireEventToParent() {
        this.dispatchEvent(new CustomEvent('callservice', {
            detail: {
                days: this.getDays()
            }
        }))
    }

    getDays() {
        return getDateDiffInDays(new Date(), this.orderCreatedDate);
    }

    @api processData(data) {
        this.patientlognotes = data && data?.has('patient') ? data.get('patient') : null;
        this.familylognotes = data && data?.has('family') ? data.get('family') : null;
        this.casedetails = data && data?.has('cases') ? data.get('cases') : null;
        this.generateFilterData();
        this.dataFound = this.totalResults?.length > 0 ? true : false;
        this.loaded = true;
    }

    generateFilterData() {
        this.generateFilteredPatientData();
        this.generateFilteredFamilyData();
        this.generateFilteredCaseData();
    }

    generateFilteredPatientData() {
        let tmp = [];
        if (this.patientlognotes && Array.isArray(this.patientlognotes)
            && this.patientlognotes.length > 0 && !this.patientprocessed) {
            this.patientprocessed = true;
            this.patientlognotes.forEach(k => {
                if (k && k?.compoundvalues?.footer?.fieldvalue &&
                    k?.compoundvalues?.footer?.fieldvalue?.toString().toLowerCase().includes(this.orderId)
                    && !tmp.includes(k)) {
                    tmp.push(k)
                }
            });
        }
        this.totalResults = tmp?.length > 0 ? this.totalResults.concat(tmp) : this.totalResults;
        tmp = [];
        this.filterResults();
    }

    generateFilteredFamilyData() {
        let tmp = [];
        if (this.familylognotes && Array.isArray(this.familylognotes)
            && this.familylognotes?.length > 0 && !this.familyprocessed) {
            this.familyprocessed = true;
            this.familylognotes.forEach(k => {
                if (k && k?.compoundvalues?.footer?.fieldvalue &&
                    k?.compoundvalues?.footer?.fieldvalue?.toString().toLowerCase().includes(this.orderId)
                    && !tmp.includes(k)) {
                    tmp.push(k)
                }
            });
        }
        this.totalResults = tmp?.length > 0 ? this.totalResults.concat(tmp) : this.totalResults;
        tmp = [];
        this.filterResults();
    }

    generateFilteredCaseData() {
        let tmp = [];
        if (this.casedetails && Array.isArray(this.casedetails)
            && this.casedetails?.length > 0 && !this.caseprocessed) {
            this.caseprocessed = true;
            this.casedetails.forEach(k => {
                if (k && k?.compoundvalues?.footer?.fieldvalue &&
                    k?.compoundvalues?.footer?.fieldvalue?.toString().toLowerCase().includes(this.orderId)
                    && !tmp.includes(k)) {
                    tmp.push(k)
                }
            });
        }
        this.totalResults = tmp?.length > 0 ? this.totalResults.concat(tmp) : this.totalResults;
        tmp = [];
        this.filterResults();
    }

    filterResults() {
        this.totalCount = this.totalResults?.length ?? 0;
        this.filteredCount = this.totalCount < INITIAL_LOAD_RECORDS ? this.totalCount : INITIAL_LOAD_RECORDS;
        this.showbutton = this.totalCount > 0 ? this.totalCount <= INITIAL_LOAD_RECORDS ? true : false : true;
        this.totalResults = this.sortResult(this.totalResults);
        this.historyTimelineData = this.totalResults?.length > INITIAL_LOAD_RECORDS ? this.totalResults.slice(0, INITIAL_LOAD_RECORDS) : this.totalResults;
        this.dataFound = this.historyTimelineData?.length > 0 ? true : false;
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
}