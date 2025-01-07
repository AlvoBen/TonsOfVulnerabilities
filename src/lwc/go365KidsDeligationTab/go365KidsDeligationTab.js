/*
LWC Name        : Go365 Kids Delegation.js
Function        : LWC to display Go365 Kids Delegation Details.

Modification Log:
* Developer Name                  Date                         Description
* Pallavi Shewale				  02/25/2022				   US-2557646 Original Version
* Swapnali Sonawane			      07/13/2023				   US-4812119
***************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import getKidsDelegateDetails from '@salesforce/apexContinuation/Go365KidsDelegate_LC_HUM.getKidsDelegateDetails';
import getMemDelList from '@salesforce/apexContinuation/Go365KidsDelegate_LC_HUM.getMemDelLst';
import { getFormatDate, ageCalculator } from 'c/crmUtilityHum';
import { getModel } from './layoutConfig';
const INITIAL_LOAD_RECORDS = 10;
export default class Go365KidsDeligationLWC extends LightningElement {
    @api bDate;
    @api entId;
    @track columns = getModel();
    @track totalCount = 0;
    @track filteredCount = 0;
    @track sortedDirection;
    @track sortedBy;
    defaultSortDirection = 'desc';
    isSorted = false;
    @track totalKidsDelegations = [];
    @track filteredKidsDelegation = [];
    @track loaded = false;
    @track bDataPresent = false;

    connectedCallback() {
        this.totalCount = this.totalKidsDelegations?.length ?? 0;
        let Age = ageCalculator(this.bDate);
        this.getKidsDelegate(Age, this.entId);
    }

    getKidsDelegate(Age, EntId) {
        let lstResult;
        let lstEntId = [];
        this.totalKidsDelegations = [];
        this.filteredKidsDelegation = [];
        getKidsDelegateDetails({ sEntId: EntId, sAge: Age })
            .then(result => {
                if (result && result != 'null') {
                    let parsedResult = JSON.parse(result);
                    if (parsedResult && parsedResult?.GetDelegateResponseDTO && parsedResult?.GetDelegateResponseDTO?.DelegatesCollection && parsedResult?.GetDelegateResponseDTO?.DelegatesCollection?.Delegate) {
                        lstResult = parsedResult?.GetDelegateResponseDTO?.DelegatesCollection?.Delegate ?? null;
                        if (lstResult != null && Array.isArray(lstResult) && lstResult?.length > 0) {
                            lstResult.forEach(k => {
                                if (this.totalKidsDelegations.findIndex(t => t?.personId === k?.PersonId) < 0) {
                                    this.totalKidsDelegations.push({
                                        personId: k?.PersonId,
                                        startDate: k?.StartDate ? getFormatDate(k?.StartDate) : ''
                                    });
                                    lstEntId.push(k?.PersonId);
                                }
                            });
                            this.getMemberDetails(lstEntId);
                            this.bDataPresent = true;
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

    performSorting() {
        if (this.totalKidsDelegations != null) {
            if (!this.isSorted) {
                let fieldName = 'startDate';
                let key = (a) => {
                    return a[fieldName];
                };
                this.totalKidsDelegations.sort(function (a, b) {
                    a = new Date(key(a));
                    b = new Date(key(b));
                    return -1 * ((a > b) - (b > a));
                });
                this.isSorted = true;
            }
        }
    }

    getMemberDetails(lstEntId) {
        if (lstEntId != null && Array.isArray(lstEntId) && lstEntId.length > 0) {
            getMemDelList({ serRespMap: lstEntId }).then(result => {
                if (result && Array.isArray(result) && result.length > 0) {
                    let lstAccount = result;
                    this.createFinalDTO(lstAccount);
                    this.performSorting();
                    this.filterdetails();
                }
            });
        }
    }


    createFinalDTO(lstAccMemDel) {
        this.totalKidsDelegations = this.totalKidsDelegations.map(item => ({
            ...item,
            name: this.getMemberName(lstAccMemDel.find(k => k?.Enterprise_ID__c === item?.personId)),
            dateOfBirth: lstAccMemDel.find(k => k?.Enterprise_ID__c === item?.personId)?.Birthdate__c ?? '',
            age: ageCalculator(lstAccMemDel.find(k => k?.Enterprise_ID__c === item?.personId)?.Birthdate__c ?? '')
        }));
    }

    getMemberName(data) {
        return data ? `${data?.Individual_First_Name__c ?? ''} ${data?.Individual_Last_Name__c ?? ''}` : '';
    }


    filterdetails() {
        if (this.totalKidsDelegations != null) {
            if (this.totalKidsDelegations.length <= INITIAL_LOAD_RECORDS) {
                this.filteredKidsDelegation = this.totalKidsDelegations;
                this.totalCount = this.totalKidsDelegations.length;
                this.filteredCount = this.totalKidsDelegations.length;
            } else {
                this.totalKidsDelegations = this.totalKidsDelegations.slice(0, INITIAL_LOAD_RECORDS);
                this.totalCount = this.totalKidsDelegations.length;
                this.filteredCount = INITIAL_LOAD_RECORDS;
            }
        }
    }


    handleLoadMore() {
        if (this.filteredCount != undefined && this.totalCount != undefined) {
            if ((this.filteredCount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                this.filteredCount = this.totalCount;
                this.filteredKidsDelegation = this.totalKidsDelegations;
            } else {
                this.filteredCount = this.filteredCount + INITIAL_LOAD_RECORDS;
                this.filteredKidsDelegation = this.totalKidsDelegations.slice(0, this.filteredCount);
            }
        }
    }

    onHandleSort(event) {
        let fieldName = event.detail.fieldName;
        let sortDirection = event.detail.sortDirection;
        const cloneData = [...this.totalKidsDelegations];
        this.filteredKidsDelegation = [];
        this.totalKidsDelegations = [];
        this.totalKidsDelegations = sortDirection === 'asc' ?
            fieldName === 'dateOfBirth' || fieldName === 'startDate' ?
                cloneData.sort(function (a, b) {
                    return new Date(a[fieldName]) > new Date(b[fieldName]) ? 1 : -1;
                }) : cloneData.sort(function (a, b) {
                    return a[fieldName] > b[fieldName] ? 1 : -1;
                }) : fieldName === 'dateOfBirth' || fieldName === 'startDate' ?
                cloneData.sort(function (a, b) {
                    return new Date(a[fieldName]) > new Date(b[fieldName]) ? -1 : 1;
                }) : cloneData.sort(function (a, b) {
                    return a[fieldName] > b[fieldName] ? -1 : 1;
                })
        this.totalKidsDelegations = cloneData;
        this.filteredCount = 0;
        this.totalCount = this.totalKidsDelegations?.length ?? 0;
        this.filterdetails();
        this.sortedDirection = sortDirection;
        this.sortedBy = fieldName;
    }
}