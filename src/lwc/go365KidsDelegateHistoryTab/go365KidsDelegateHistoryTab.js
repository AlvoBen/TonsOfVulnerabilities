/*******************************************************************************************************************************
LWC JS Name : Go365KidsDelegateHistoryTab.js
Function    : This LWC component display Go365 History details

Modification Log: 
Developer Name                             Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Swapnali Sonawane                 	  01/10/2022                  REQ# 2588478 -Go365 - Ability to View Go365 Kids Delegation History in a Lightning Experience
* Swapnali Sonawane                     07/14/2023                  US# 4812119  Critical- Lightning - Go365
*********************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import getDelegateHistory from '@salesforce/apexContinuation/Go365KidsDelegateHistory_LC_HUM.getKidsDelegateHistory';
import getMemDelList from '@salesforce/apexContinuation/Go365KidsDelegateHistory_LC_HUM.getMemDelLst';
import { getFormatDate, ageCalculator } from 'c/crmUtilityHum';
import { getModel } from './layoutConfig';
const INITIAL_LOAD_RECORDS = 10;

export default class Go365KidsDelegateHistoryTab extends LightningElement {
    @api bDate;
    @api entId;
    totalCount = 0;
    filteredcount = 0;
    sortedDirection;
    sortedBy;
    defaultSortDirection = 'desc';
    @track kidsHistorydata = [];
    @track totalHistorydata = [];
    @track columns = getModel();
    @track loaded = false;
    @track bDataPresent = false;


    connectedCallback() {
        let sAgeHis = Math.trunc(ageCalculator(this.bDate));
        this.getKidsDelegateHistory(sAgeHis, this.entId);
    }

    getKidsDelegateHistory(sAge, sEntId) {
        let lstResult;
        this.totalHistorydata = [];
        let lstEntId = [];
        getDelegateHistory({ sEntIdHis: sEntId, sAgeHis: sAge })
            .then(result => {
                if (result && result != 'null') {
                    let parsedResult = JSON.parse(result);
                    if (parsedResult && parsedResult?.GetDelegateResponseDTO && parsedResult?.GetDelegateResponseDTO?.DelegatesCollection && parsedResult?.GetDelegateResponseDTO?.DelegatesCollection?.Delegate) {
                        lstResult = parsedResult?.GetDelegateResponseDTO?.DelegatesCollection?.Delegate ?? null;
                        if (lstResult != null && Array.isArray(lstResult) && lstResult.length > 0) {
                            lstResult.forEach(k => {
                                if (this.totalHistorydata.findIndex(t => t?.personId === k?.PersonId) < 0) {
                                    this.totalHistorydata.push({
                                        personId: k?.PersonId ?? '',
                                        effectiveDate: k?.StartDate ? getFormatDate(k?.StartDate) : '',
                                        action: k?.Status ? k?.Status : ''
                                    });
                                    lstEntId.push(k?.PersonId);
                                }
                            });
                            this.getMemberDetails(lstEntId);
                        }
                    }
                }
                this.loaded = true;
            })
            .catch(err => {
                console.log("Error occured - " + JSON.stringify(err));
                this.loaded = true;
            });
    }


    getMemberDetails(lstEntId) {
        if (lstEntId != null && Array.isArray(lstEntId) && lstEntId.length > 0) {
            getMemDelList({ serRespMap: lstEntId }).then(result => {
                if (result && Array.isArray(result) && result.length > 0) {
                    let lstAccount = result;
                    this.createFinalDTO(lstAccount);
                    this.totalHistorydata.map(obj => ({
                        ...obj,
                        "cellBorder": "slds-border_right"
                    }));
                    this.bDataPresent = this.totalHistorydata && Array.isArray(this.totalHistorydata)
                        && this.totalHistorydata?.length > 0 ? true : false;
                    this.performSorting();
                    this.filterdetails();
                }
            });
        }
    }

    performSorting() {
        this.totalHistorydata.sort(function (a, b) {
            let dateA = new Date(a.effectiveDate);
            let dateB = new Date(b.effectiveDate);
            return dateA > dateB ? -1 : 1;
        });
    }

    createFinalDTO(lstAccMemDel) {
        this.totalHistorydata = this.totalHistorydata.map(item => ({
            ...item,
            delegate: this.getMemberName(lstAccMemDel.find(k => k?.Enterprise_ID__c === item?.personId))
        }));
    }


    getMemberName(data) {
        return data ? `${data?.Individual_First_Name__c ?? ''} ${data?.Individual_Last_Name__c ?? ''}` : '';
    }

    onHandleSort(event) {
        let fieldName = event.detail.fieldName;
        let sortDirection = event.detail.sortDirection;
        const cloneData = [...this.totalHistorydata];
        this.kidsHistorydata = [];
        this.totalHistorydata = [];
        this.totalHistorydata = sortDirection === 'asc' ?
            fieldName === 'effectiveDate' ?
                cloneData.sort(function (a, b) {
                    return new Date(a[fieldName]) > new Date(b[fieldName]) ? 1 : -1;
                }) : cloneData.sort(function (a, b) {
                    return a[fieldName] > b[fieldName] ? 1 : -1;
                }) : fieldName === 'effectiveDate' ?
                cloneData.sort(function (a, b) {
                    return new Date(a[fieldName]) > new Date(b[fieldName]) ? -1 : 1;
                }) : cloneData.sort(function (a, b) {
                    return a[fieldName] > b[fieldName] ? -1 : 1;
                });
        this.totalHistorydata = cloneData;
        this.filteredcount = 0;
        this.totalCount = this.totalHistorydata != null ? this.totalHistorydata.length : 0;
        this.filterdetails();
        this.sortedDirection = sortDirection;
        this.sortedBy = fieldName;
    }

    filterdetails() {
        if (this.totalHistorydata != null) {
            if (this.totalHistorydata.length <= INITIAL_LOAD_RECORDS) {
                this.kidsHistorydata = this.totalHistorydata;
                this.totalCount = this.totalHistorydata.length;
                this.filteredcount = this.totalHistorydata.length;
            } else {
                this.kidsHistorydata = this.totalHistorydata.slice(0, INITIAL_LOAD_RECORDS);
                this.totalCount = this.totalHistorydata.length;
                this.filteredcount = INITIAL_LOAD_RECORDS;
            }
        }
    }

    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.filteredcount != undefined && this.totalCount != undefined) {
                if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                    this.filteredcount = this.totalCount;
                    this.kidsHistorydata = this.totalHistorydata;
                } else {
                    this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                    this.kidsHistorydata = this.totalHistorydata.slice(0, this.filteredcount);
                }
            }
        }
    }

    loadMoreData(event) {
        if (this.filteredcount != undefined && this.totalCount != undefined) {
            if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                this.filteredcount = this.totalCount;
                this.kidsHistorydata = this.totalHistorydata;
            } else {
                this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                this.kidsHistorydata = this.totalHistorydata.slice(0, this.filteredcount);
            }
        }
    }
}