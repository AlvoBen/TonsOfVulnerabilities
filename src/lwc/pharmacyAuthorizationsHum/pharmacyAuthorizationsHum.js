/*
JS Controller        : PharmacyAuthorizationsDetailHum
Version              : 1.0
Created On           : 10/14/2021
Function             : Component to display to pharmacy authorization filter

Modification Log: 
* Developer Name                         Date                         Description
* Swapnali Sonawane                      10/14/2021                   Original Version
* Swapnali Sonawane                      10/22/2021                   Defect 3954 After removing the text from the "Filter By" field, records are not loaded by back
* Swapnali Sonawane                      10/28/2021                   Defect 3987 The text added below the Filter fields does not clear even after unchecking the values from "Outcome" & "Event Type" fields 
* Swapnali Sonawane                      10/29/2021                   Defect 3993 Filter By does not returns only all the matching records but other records as well, when filter criteria selected has more then 1 matching records
* Swapnali Sonawane                      12/03/2021                   US 2864657 DF - 4062 
* Kalyani Pachpol	                     06/30/2022					  US 3377044
* Pinky Vijur						     04/03/2023					  User Story 4401068: C04; Authorization/Referral Verification; LIGHTNING--T1PRJ0891742-CRM IOP-2022-6037203—Pharmacy Authorization Details: add ‘Department’, ‘Coverage Start Date’ & ‘Coverage End Date’
* Nirmal Garg				             06/23/2023                   US - 4762833 - Fix Pharmacy authorization scrolling and keyword search issue
* Vishal Shinde                          08/28/2023                   US- 4833055-User Story 4833055 Mail Order Management: Pharmacy - Iconology- Authorization (Lightning)
* Vishal Shinde                          10/10/2023                   User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
*------------------------------------------------------------------------------------------------------------------------------
*/

import { api, LightningElement, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import getAuthorizations from '@salesforce/apexContinuation/PharmacyBenefits_LC_HUM.invokeGetpAuthService';
import { getPickListValues, getUniqueId } from 'c/crmUtilityHum';
import serviceerrormessage from '@salesforce/label/c.Lightning_ErrorMessage';

//global constants for this JS controller
const MIN_CHAR_SEARCH = 3;
const INITIAL_LOAD_RECORDS = 5;
const CHAR_LIMIT = 80;
export default class PharmacyAuthorizationsHum extends LightningElement {

    @api
    recordId;

    //constants
    searchByWord = 'searchByWord';
    key = 'key';
    multiselectcmpname = 'c-generic-multiselect-picklist-hum';
    input = 'input'
    dropdownkey = ['authDescription', 'eventType'];

    @api enterpriseId;
    @api calledfrompharmacy = false;
    @track authorizations = [];
    @track finalauthorizations;
    @track filteredauthorrizations = [];
    @track filterFldValues = [];
    @track totalcount = 0;
    @track filteredcount = 0;
    @track enableshowmore = false;
    @track totalfilterresult = [];
    @track filterObj = {};
    @track loaded = false;
    @track pillFilterValues = [];
    @track dataFound = false;
    @track keyword = '';
    @track searchEntered = false;
    @track serviceError = false;
    memberid;
    @api pharmacyAuthorizations;
    @api pharmAuthServiceError;

    connectedCallback() {
        const me = this;
    if(!this.calledfrompharmacy){
        me.getAuthorizationsData();
 	}
        else if(this.calledfrompharmacy){
            me.getAuthorizationDataFromContainer(); 
        }
    }

    labels = {
        serviceerrormessage
    }

    getAuthorizationsData() {
        const me = this;
        getAuthorizations({ sMemID: me.enterpriseId })
            .then(data => {
                let response = JSON.parse(data);
                this.getAuthorizationDetails(response);
                })
                .catch(error => {
                me.loaded = true;
                this.serviceError = true;
                console.log('Auth Error : ' + JSON.stringify(error))
                 this.dispatchEvent(new ShowToastEvent({
                     title: 'Error!',
                     message: this.labels.serviceerrormessage,
                     variant: 'error',
                     mode: 'dismissable'
                 }));
            })
    }
    getAuthorizationDataFromContainer(){
        this.getAuthorizationDetails(this.pharmacyAuthorizations);
        if(this.pharmAuthServiceError){
            this.loaded = true;
            this.serviceError = true;
        }
    }

    getAuthorizationDetails(response){
        const me= this;
                if (null != response) {
                    if (response && response?.GetPriorAuthStatusHistoryResponse && response?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails &&
                        Array.isArray(response?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails) && response?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails?.length > 0) {
                        response?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails.forEach(k => {
                            if (k && k?.AgadiaAuthDetails && Array.isArray(k?.AgadiaAuthDetails) && k?.AgadiaAuthDetails?.length > 0) {
                                k?.AgadiaAuthDetails.forEach(t => {
                                    this.authorizations.push({
                                        quantity: t && t?.DrugDetails && Array.isArray(t?.DrugDetails) && t?.DrugDetails?.length > 0
                                            ? (t?.DrugDetails[0]?.Quantity ?? '') : '',
                                        daysSupply: t && t?.DrugDetails && Array.isArray(t?.DrugDetails) && t?.DrugDetails?.length > 0
                                            ? (t?.DrugDetails[0]?.DrugDaysSupply ?? '') : '',
                                        drugName: t?.DrugName ?? '',
                                        eventType: t?.HCSEventType ?? '',
                                        decisionDate: t?.EOCDecisionDate ?? '',
                                        creationDate: t?.EOCCreationDate ?? '',
                                        mdo: `${t?.PrescriberFirstName ?? ''} ${t?.PrescriberLastName ?? ''}`,
                                        phone: t?.PrescriberFaxNumber ?? '',
                                        coverageStartDate: t && t?.DrugDetails && Array.isArray(t?.DrugDetails) && t?.DrugDetails?.length > 0
                                            ? (t?.DrugDetails[0]?.CoverageStartDate ?? '') : '',
                                        coverageEndDate: t && t?.DrugDetails && Array.isArray(t?.DrugDetails) && t?.DrugDetails?.length > 0
                                            ? (t?.DrugDetails[0]?.CoverageEndDate ?? '') : '',
                                        departmentCode: t && t?.DrugDetails && Array.isArray(t?.DrugDetails) && t?.DrugDetails?.length > 0
                                            ? (t?.DrugDetails[0]?.DepartmentCode ?? '') : '',
                                        hasValidDepartment: t && t?.DrugDetails && Array.isArray(t?.DrugDetails) && t?.DrugDetails?.length > 0
                                            ? t?.DrugDetails[0]?.DepartmentCode === 'MITO' ? true : false : false,
                                        denialLanguage: t?.DenialLanguage ?? '',
                                        authDescription: t?.AuthDescription ?? '',
                                        isReadMoreEligible: t?.DenialLanguage?.length > CHAR_LIMIT ? true : false
                                    })
                                })
                            }
                        })
                    }
                    me.filterFldValues = getPickListValues(me.dropdownkey, me.authorizations);
                    me.authorizations = me.performSorting(me.authorizations);
                }
                me.totalcount = this.authorizations?.length ?? 0;
                this.dataFound = this.totalcount > 0 ? true : false;
                me.generateFilterData();
                me.loaded = true;
          
    }

    performSorting(data) {
        if (data?.length > 0) {
            data.sort(function (a, b) {
                let nameA = a?.drugName?.toUpperCase() ?? '';
                let nameB = b?.drugName?.toUpperCase() ?? '';
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                return 0;
            });
        }
        return data;
    }

    handleshowmore() {
        const me = this;
        if ((me.filteredcount + INITIAL_LOAD_RECORDS) < me.totalcount) {
            me.filteredauthorrizations = me.authorizations.slice(0, (me.filteredcount + INITIAL_LOAD_RECORDS))
        } else {
            me.filteredcount = me.totalcount;
            me.filteredauthorrizations = me.authorizations.slice(0, me.totalcount);
            me.enableshowmore = false;
        }
        me.finalauthorizations = me.filteredauthorrizations;
    }

    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.totalfilterresult.length <= this.filteredcount + INITIAL_LOAD_RECORDS) {
                this.totalcount = this.totalfilterresult.length;
                this.filteredcount = this.totalcount;
                this.finalauthorizations = this.totalfilterresult;
            } else {
                this.totalcount = this.totalfilterresult.length;
                this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                this.finalauthorizations = this.totalfilterresult.slice(0, this.filteredcount + INITIAL_LOAD_RECORDS);
            }
        }
    }

    generateFilterData() {
        const me = this;
        const scrolldiv = this.template.querySelector('[data-id="scrolldiv"]');
        if (scrolldiv) {
            scrolldiv.scrollTop = 0;
        }
        try {
            me.filteredauthorrizations = me.authorizations.length > 0 ? JSON.parse(JSON.stringify(me.authorizations)) : [];
            if (me.filterObj && Object.keys(me.filterObj).length > 0) {
                Object.keys(me.filterObj).forEach(c => {
                    if (me.filterObj[c].length > 0) {
                        me.performfilter(c, me.filterObj[c]);
                    }
                });
            } else {
                me.filteredauthorrizations = me.authorizations;
            }
            //console.log('filter ' + JSON.stringify(me.filteredauthorrizations));
            me.totalfilterresult = me.filteredauthorrizations;
            if (me.totalfilterresult != null) {
                if (me.totalfilterresult.length > INITIAL_LOAD_RECORDS) {
                    me.filteredauthorrizations = me.totalfilterresult.slice(0, INITIAL_LOAD_RECORDS);
                    me.filteredcount = INITIAL_LOAD_RECORDS;
                    me.enableshowmore = true;
                } else {
                    me.filteredauthorrizations = me.totalfilterresult;
                }
            }
            me.totalcount = me.totalfilterresult != null ? me.totalfilterresult.length : 0;
            me.filteredcount = me.filteredauthorrizations != null ? me.filteredauthorrizations.length : 0;
            me.finalauthorizations = me.filteredauthorrizations;
            me.finalauthorizations = this.performSorting(me.finalauthorizations);
        }
        catch (err) {
            console.log('Error : ' + err);
        }

    }

    findByWord(event) {
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value.length >= MIN_CHAR_SEARCH) {
            this.filterObj[name] = value;
            this.filterObj = {
                ...this.filterObj,
                searchByWord: value,
            };
            this.generateFilterData();
            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != name);
            this.pillFilterValues.push({
                key: name,
                value: value,
            });
        } else {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
            }
            this.generateFilterData();
            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != name);
        }
    }

    clearTextBox() {
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

    handlePillRemove(event) {
        const me = this;
        if (null != me.pillFilterValues && me.pillFilterValues.length > 0)
            me.pillFilterValues.splice(event.target.dataset.index, 1);
        Object.keys(me.filterObj).forEach(t => {
            if (t === event.target.dataset.key) {
                if (event.target.dataset.key === 'searchByWord') {
                    delete me.filterObj['searchByWord'];
                    this.clearTextBox();
                    this.searchEntered = false;
                    if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
                        this.template.querySelector('c-generic-keyword-search-hum').clearSearchData();
                    }
                } else {
                    const ind = me.filterObj[t].indexOf(event.target.dataset.value);
                    if (Object.keys(this.filterObj).length > 0) {
                        if (me.filterObj[t].length > 0) {
                            me.filterObj[t].splice(ind, 1);
                        }
                    }
                }
            }
        });
        me.generateFilterData();
        if (event.target.dataset.key != 'searchByWord') {
            if (me.template.querySelector('c-generic-multiselect-picklist-hum') != null) {
                let payload = { keyname: event.target.dataset.key, value: event.target.dataset.value };
                me.template.querySelectorAll('c-generic-multiselect-picklist-hum').forEach(k => {
                    k.clearSelection(payload);
                })
            }
        }
    }

    performfilter(keyname, keyvalues) {
        const me = this;
        let isSerach = false;
        Object.keys(me.filterObj).forEach(c => {
            if (me.filterObj[c].length > 0) {
                if (me.filteredauthorrizations.length > 0) {
                    if (c === 'searchByWord') {
                        let tmp = [];
                        me.filteredauthorrizations.forEach(a => {
                            Object.values(a).forEach(h => {
                                if (null != h && h.toString().toLowerCase().includes(me.filterObj[c].toLowerCase())
                                    && !tmp.includes(a)) {
                                    tmp.push(a);
                                }
                            })
                        })
                        if (tmp != undefined || tmp.length > 0) {
                            me.filteredauthorrizations = tmp;
                        } else {
                            this.filteredauthorrizations = [];
                        }
                        tmp = [];
                        isSerach = true;
                    }
                    else {
                        let tmp = [];
                        me.filteredauthorrizations.forEach(f => {
                            if (f.hasOwnProperty(c) && me.filterObj[c].includes(f[c])) {
                                if (!tmp.includes(f))
                                    tmp.push(f);
                            }
                        });
                        me.filteredauthorrizations = tmp.length > 0 ? tmp : [];
                    }
                }
            }
        });
        me.filteredauthorrizations = JSON.parse(JSON.stringify(me.filteredauthorrizations));
    }

    handlePicklistFilter(event) {
        const me = this;
        let filterdata = event.detail;
        if (filterdata) {
            if (me.filterObj.hasOwnProperty(filterdata.keyname)) {
                if (filterdata.selectedvalues.length > 0) {
                    me.filterObj[filterdata.keyname] = Object.values(filterdata.selectedvalues);
                } else {
                    delete me.filterObj[filterdata.keyname];
                }
            } else {
                if (filterdata.selectedvalues.length > 0) {
                    me.filterObj[filterdata.keyname] = Object.values(filterdata.selectedvalues);
                }
            }
        }
        me.pillFilterValues = me.pillFilterValues.filter(k => k.key != filterdata.keyname);
        Object.values(filterdata.selectedvalues).forEach(k => {
            me.pillFilterValues.push({
                key: filterdata.keyname,
                value: k
            });
        })
        me.generateFilterData();
    }
}