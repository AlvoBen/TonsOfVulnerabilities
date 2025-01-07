/*
LWC Name        : SearchCodeSetHum.html
Function        : LWC to display Search Code set Screen.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                 01/05/2023                  initial version REQ- 4080893 
* Aishwarya Pawar                 01/16/2023                  Defect Fix - 6968 
* Aishwarya Pawar                 03/17/2023                  Defect Fix - 7414
* Swapnali Sonawane               07/27/2023                 US-4802828  Update UI Medical Network ID Search Screen
*------------------------------------------------------------------------------------------------------------------
*/

import { LightningElement, track, wire } from 'lwc';
import searchDetailsForCodesOnly from '@salesforce/apex/SearchCodeSet_LC_HUM.searchDetailsForCodesOnly';
import searchDetailsForAll from '@salesforce/apex/SearchCodeSet_LC_HUM.searchDetailsForAll';
import HUMNoRecords from "@salesforce/label/c.HUMNoRecords";
import SearchInfo from "@salesforce/label/c.SearchInformation";
import CodesOnlyInfo from "@salesforce/label/c.CodeOnlyToggleInformation";
const INITIAL_RECORDS = 5;
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';
import { CurrentPageReference } from 'lightning/navigation';
import { getUniqueId } from 'c/crmUtilityHum';
import { getModel } from './layoutConfig';
export default class SearchCodeSetHum extends LightningElement {

    @track totalSearchCodeRecords;
    @track SearchKeyword = '';
    @track codeOnlySearch = false;
    @track CodeSetValues = [];
    labels = {
        HUMNoRecords,
        SearchInfo,
        CodesOnlyInfo
    };
    @track filteredSCRecords = [];
    @track allSearchCodeRecords = [];
    @track totalCount;
    @track filteredCount;
    @track keyword = '';
    @track noRecordsAvailble = false;
    @track initialScreen = true;
    @track searchEntered = false;
    @track picklistFilterdata;
    @track bDisplaySearchInfo = false;
    @track bDisplayCodeSearchInfo = false;
    @track displayToastMessage = false;
    @track displayFilters = false;
    @track pageRef;
    @track filterCodeSet = false;
    @track selectedValues = [];
    @track filterCodeSetValue;
    @track columns = getModel();

    handleChange(event) {
        if (event.target.name == 'searchCiteria') {
            this.SearchKeyword = event.target.value;
        } else if (event.target.name == 'searchCode') {
            this.codeOnlySearch = !this.codeOnlySearch;
        }
    }

    connectedCallback() {
        this.initialSetup();
    }

    initialSetup() {
        this.selectedValues = [];
        if (window?.location?.href.includes('?')) {
            let paramString = window.location.href.split('?')[1];
            
            let queryString = new URLSearchParams(paramString);
            for (let pair of queryString.entries()) {
                if (pair && pair[0]) {
                    switch (pair[0]?.toLowerCase()?.replace("_", "")) {
                        case "mentorcode":
                            this.SearchKeyword = window.atob(pair[1]) ?? '';
                            this.codeOnlySearch = true;
                            break;
                    }

                }
            }
            this.performSearch();
        }
    }

    @wire(CurrentPageReference)
    getPageState(currentPageReference) {
        this.pageRef = currentPageReference;
        
    }

    displaySearchInfo() {
        this.bDisplaySearchInfo = !this.bDisplaySearchInfo;
    }
    displayCodeSearchInfo() {
        this.bDisplayCodeSearchInfo = !this.bDisplayCodeSearchInfo;
    }
    performSearch() {

        this.allSearchCodeRecords = [];
        this.totalSearchCodeRecords = [];
        this.filteredSCRecords = [];
        this.CodeSetValues = [];
        if (this.SearchKeyword.length >= 2) {
            this.displayToastMessage = false;
            let searchDetails;
            this.filteredCount = 0;
            this.totalCount = 0;
            if (this.codeOnlySearch) {
                searchDetailsForCodesOnly({ textTobeSearched: this.SearchKeyword })
                    .then(result => {
                        searchDetails = JSON.parse(result);
                        if (searchDetails && searchDetails.length > 0) {
                            this.processSearchCodeData(searchDetails);
                            this.noRecordsAvailble = false;
                            this.initialScreen = false;
                            this.displayFilters = true
                        } else {
                            this.noRecordsAvailble = true;
                            this.initialScreen = false;
                            this.displayFilters = false;
                        }

                    })
                    .catch(error => {
                        this.noRecordsAvailble = true;
                        this.initialScreen = false;
                        this.displayFilters = false;
                        console.log('Error occured in handleSearch() -->', error);
                    })
            } else {
                searchDetailsForAll({ textTobeSearched: this.SearchKeyword })
                    .then(result => {
                        searchDetails = JSON.parse(result);
                        if (searchDetails && searchDetails.length > 0) {
                            this.processSearchCodeData(searchDetails);
                            this.noRecordsAvailble = false;
                            this.initialScreen = false;
                            this.displayFilters = true;
                        } else {
                            this.noRecordsAvailble = true;
                            this.initialScreen = false;
                            this.displayFilters = false;
                        }

                    })
                    .catch(error => {
                        this.noRecordsAvailble = true;
                        this.initialScreen = false;
                        this.displayFilters = false;
                        console.log('Error occured in handleSearch() -->', error);
                    })
            }


        } else {

            this.displayToastMessage = true;
            this.noRecordsAvailble = false;
            this.filteredSCRecords = [];
            this.filteredCount = 0;
            this.totalCount = 0;
            this.initialScreen = true;

        }
    }
    handleSearch() {

        this.performSearch();
    }


    hideToastMessage() {
        this.displayToastMessage = false;
    }
    renderedCallback() {
        document.title = 'Search Code Set';
    }
    handleReset() {
        this.filterCodeSet = false;
        this.filterCodeSetValue = null;
        this.filteredSCRecords = [];
        this.CodeSetValues = [];
        this.filteredCount = 0;
        this.totalCount = 0;
        this.initialScreen = true;
        this.codeOnlySearch = false;
        this.selectedValues = [];
        this.picklistFilterdata = null;
        let inputelements = this.template.querySelectorAll('.toggle-input');
        inputelements.forEach(function (item) {
            item.checked = false;
        });
        this.SearchKeyword = '';
        inputelements = this.template.querySelectorAll('.search-input');
        inputelements.forEach(function (item) {
            item.value = '';
        });
        this.displayToastMessage = false;
        this.keyword = '';
    }

    handleEnter(event) {
        if (event.keyCode === 13) {
            this.performSearch();
        }
    }
    processSearchCodeData(searchDetails) {
        let temp;
        searchDetails.forEach((searchCode, i) => {
            temp = {};
            temp.id = getUniqueId();
            temp.sCode = searchCode.Name ? searchCode.Name : '';
            temp.sCodeset = searchCode.Code ? searchCode.Code: '';
            temp.sDescription = searchCode.CodeDescription ? searchCode.CodeDescription: '';
            temp.sStatus = searchCode.Status__c ? searchCode.Status__c : '';
            this.totalSearchCodeRecords.push(temp);
        });
        this.allSearchCodeRecords = this.totalSearchCodeRecords;
        if (this.template.querySelector("c-generic-multiselect-picklist-hum") != null) {
            this.template
                .querySelectorAll("c-generic-multiselect-picklist-hum")
                .forEach((k) => {
                    k.clearDropDowns();
                });
        }
        //this.CodeSetValues = getPickListValues(['sCodeset'], this.totalSearchCodeRecords);


        // if (this.CodeSetValues && this.CodeSetValues?.sCodeset && Array.isArray(this.CodeSetValues?.sCodeset) &&
        //     this.CodeSetValues?.sCodeset.length > 0) {
        //     if (this.filterCodeSetValue) {
        //         this.CodeSetValues.sCodeset.push({
        //             label: this.filterCodeSetValue,
        //             value: this.filterCodeSetValue
        //         })
        //     }
        //     // if (this.CodeSetValues.sCodeset.findIndex(k => k?.label === this.filterCodeSetValue) >= 0) {
        //     //     this.selectedValues.push({
        //     //         label: this.filterCodeSetValue,
        //     //         value: this.filterCodeSetValue
        //     //     })
        //     // }
        // }
        this.getSortedCodeSets();
        this.keyword = '';
        this.columns.forEach((col) => {
            col.iconname = 'utility:arrowup';
            col.sorting = col.label == "Code" ? true : false;
        });
        this.performFilter();
        //this.getInitalRecords();
    }

    getSortedCodeSets() {
        if (this.totalSearchCodeRecords && Array.isArray(this.totalSearchCodeRecords)
            && this.totalSearchCodeRecords?.length > 0) {
            this.totalSearchCodeRecords.forEach(k => {
                if (k && k?.sCodeset) {
                    if (this.CodeSetValues.findIndex(h => h?.label === k?.sCodeset) < 0) {
                        this.CodeSetValues.push({
                            label: k?.sCodeset ?? '',
                            value: k?.sCodeset ?? ''
                        })
                    }
                }
            })
        }
        if (this.CodeSetValues && Array.isArray(this.CodeSetValues) && this.CodeSetValues?.length > 0) {
            this.CodeSetValues.sort((a, b) => {
                const sCodeCompare = a?.label?.localeCompare(b?.label);
                return sCodeCompare;
            })
        }
    }

    handlePicklistFilter(event) {
        this.picklistFilterdata = event.detail;
        this.selectedValues = [];
        this.performFilter();
    }

    performFilter() {
        let tmp = [];
        if (this.picklistFilterdata != null && this.allSearchCodeRecords && this.picklistFilterdata?.selectedvalues &&
            Array.isArray(this.allSearchCodeRecords) && this.allSearchCodeRecords.length > 0 && Object.values(this.picklistFilterdata?.selectedvalues).length > 0) {
            this.totalSearchCodeRecords = this.allSearchCodeRecords.filter(sc => Object.values(this.picklistFilterdata?.selectedvalues).includes(sc?.sCodeset));
        } else if (this.allSearchCodeRecords && this.picklistFilterdata?.selectedvalues && Array.isArray(this.allSearchCodeRecords) && this.allSearchCodeRecords.length > 0 && Object.values(this.picklistFilterdata?.selectedvalues).length == 0) {
            this.totalSearchCodeRecords = this.allSearchCodeRecords;
        }
        this.totalSearchCodeRecords.forEach(a => {
            Object.values(a).forEach(b => {
                let tempNode = JSON.stringify(b);
                if (null != tempNode && tempNode.toLowerCase().includes(this.keyword.toLowerCase()) && !tmp.includes(a)) {
                    tmp.push(a);
                }
            })
        });
        this.totalSearchCodeRecords = tmp.length > 0 ? tmp : [];
        this.getInitalRecords();
    }


    findByWord(event) {
        let tname = event.target.name;
        let tvalue = event.target.value
        this.keyword = event.target.value;
        this.searchEntered = true;
        this.handleKeywordSearch(tvalue);
    }

    clearSearchData() {
        this.searchEntered = false;
        this.clearTextBox();
        this.performFilter();
        this.getInitalRecords();
    }

    clearTextBox() {
        if (this.keyword && this.keyword.length > 0) {
            this.keyword = "";
        }
    }

    handleKeywordSearch(value) {
        let tmp = [];
        this.keyword = value;
        this.performFilter();
        this.totalSearchCodeRecords.forEach(a => {
            Object.values(a).forEach(b => {
                let tempNode = JSON.stringify(b);
                if (null != tempNode && tempNode.toLowerCase().includes(this.keyword.toLowerCase()) && !tmp.includes(a)) {
                    tmp.push(a);
                }
            })
        });

        this.totalSearchCodeRecords = tmp.length > 0 ? tmp : [];
        this.getInitalRecords();
    }


    getInitalRecords() {
        if (this.totalSearchCodeRecords && Array.isArray(this.totalSearchCodeRecords) && this.totalSearchCodeRecords.length > 0) {
            this.noRecordsAvailble = false;
            if (this.totalSearchCodeRecords.length > 0 && this.totalSearchCodeRecords.length <= INITIAL_RECORDS) {
                this.filteredSCRecords = this.totalSearchCodeRecords;
                this.totalCount = this.totalSearchCodeRecords.length;
                this.filteredCount = this.totalCount;
            } else {
                this.filteredSCRecords = this.totalSearchCodeRecords.slice(0, INITIAL_RECORDS);
                this.totalCount = this.totalSearchCodeRecords.length;
                this.filteredCount = this.totalCount > 0 ? INITIAL_RECORDS : 0;
            }
        } else {
            this.noRecordsAvailble = true;
            this.filteredSCRecords = [];
        }
    }

    onHandleSort(event) {
        if (this.totalSearchCodeRecords && this.totalSearchCodeRecords.length > 0) {
            event.preventDefault();
            let header = event.currentTarget.dataset.label;
            let sortedBy = event.currentTarget.getAttribute('data-id');
            let sortDirection = event.currentTarget.dataset.iconname === ICON_ARROW_DOWN ? 'asc' : 'desc';
            this.columns.forEach(element => {
                if (element.label === header) {
                    element.mousehover = false;
                    element.sorting = true;
                    element.iconname = element.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
                } else {
                    element.mousehover = false;
                    element.sorting = false;
                }
            });
            const cloneData = [...this.totalSearchCodeRecords];
            cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
            this.totalSearchCodeRecords = cloneData;
            this.getInitalRecords();
        }
    }

    sortBy(field, reverse, primer) {
        const key = primer
            ? function (x) {
                return primer(x[field]);
            }
            : function (x) {
                return x[field];
            };
        return function (a, b) {
            a = key(a);
            b = key(b);
            return reverse * ((a > b) - (b > a));
        };
    }

    handleMouseEnter(event) {
        let header = event.target.dataset.label;
        this.columns.forEach(element => {
            if (element.label === header) {
                element.mousehover = true,
                    element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
            }
        });
    }

    handleMouseLeave(event) {
        let header = event.target.dataset.label;
        this.columns.forEach(element => {
            if (element.label === header) {
                element.mousehover = false
            }
        });
    }

    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.totalSearchCodeRecords.length <= this.filteredCount + INITIAL_RECORDS) {
                this.totalCount = this.totalSearchCodeRecords.length;
                this.filteredCount = this.totalCount;
                this.filteredSCRecords = this.totalSearchCodeRecords;
            } else {
                this.totalCount = this.totalSearchCodeRecords.length;
                this.filteredCount = this.filteredCount + INITIAL_RECORDS;
                this.filteredSCRecords = this.totalSearchCodeRecords.slice(0, this.filteredCount + INITIAL_RECORDS);
            }
        }
    }
}