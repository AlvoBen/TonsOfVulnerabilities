/*
LWC Name        : displayLoggedInfoHum.js
Function        : LWC to display logged cases.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     03/17/2022                   initial version - US - 3017464
* Aishwarya Pawar                 03/17/2022                   US-3150160 - Add limits
* Nilesh Gadkar                   09/27/2022                   US-3760906
* Aishwarya Pawar               	 03/01/2023                     US - 4286514
* Atul Patil                       06/07/2023				US - 4648135
* Atul Patil                       06/02/2023				US - 4648135
* Nirmal Garg                     06/07/2023                    US-4715843
* Atul Patil                        06/14/2023                  DF-7758
* Atul Patil                        06/15/2023                  DF-7749
* Vishal Shinde                   09/15/2023                   US - 5061087
****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getBenefitSearch from '@salesforce/apexContinuation/Benefits_LC_HUM.getBenefitSearch';
import { getModal } from './layoutConfig';
import { performTableLogging, performLogging, performFilterLogging, getLoggingKey, checkloggingstatus, clearSearchCriteriaLog } from 'c/loggingUtilityHum';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { CurrentPageReference } from 'lightning/navigation';
import benefisearchfiltererror from '@salesforce/label/c.BENEFITSEARCH_FILTER_ERROR';
import benefitsearcherror from '@salesforce/label/c.BENEFITSEARCH_ERROR';
import { invokePBEService } from 'c/benefitsServiceHelperHum';
import { getCustomMetadataValues } from 'c/genericReadCustomMetadataValues';
const ParCodes = [
    { label: 'Both', value: 'Both' },
    { label: 'Non Par', value: 'No' },
    { label: 'Par', value: 'Yes' }]
const INITIAL_LOAD_RECODRS = 10;
const INITIAL_LOAD_MESSAGE = 'No data available in table.';

export default class ContinuationComponent extends LightningElement {

    @track BenefitsResponseData = {};
    @api memName;
    @api isMedical;
    @api isDental;
    @api platformCode;
    @api pberesponse;
    @api productId;
    @api serviceerror;
    @api message;
    @track isLVPlatform;
    @track causeCodesOptions = [];
    @track selectedCauseCode;
    @track serviceTypes = [];
    @track serviceCategoryOptions = [];
    @track selectedCategoryCode;
    @track selectedTypeOfService;
    @track selectedPARCode;
    @track selectedPlaceOfService;
    @track displayFilters = false;
    @track typeOfServiceOptions = [];
    @track placeOfServiceOptions = [];
    @track parCodeOptions = [];
    @track initialLoad = true;
    @track isCAS = false;
    @track lstServiceCategoryCode;
    @track BenefitSearchResponse;
    @track showPillFilterValues = false;
    @track SelectedFilters = [];
    @track enableApply = false;
    @track columns = [];
    @track benefitModel;
    @track isLoading = false;

    @track message;
    @track isError = false;
    @track totalCount = 0;
    @track filteredcount;
    @track expanded = false;
    @track benefitLimits = {};
    @track bebenefitLimitComments = {};
    @track selectedRowIds = [];
    @track datafound = false;
    @track disableFilterButton = true;
    @track loaded = false;
    @track totalBenefits = [];
    @track filteredBenefits = [];
    @track loggingkey;
    @track tableData;
    @track colBool=false;

    @track relatedInputField = [{
        label: "Plan Member Id",
        value: this.memName
    }, {
        label: "Section",
        value: "Search"
    }, {
        label: "Index",
        mappingField: "Index"
    }];

    @track screenName = "Medical Benefits Search";
    @track filteredBenefits = [];
    @track filteredTotalBenefits = [];
    @track filterconfig = [
        {
            label: 'Cause Code',
            name: 'Cause Code',
            dataid: 'CauseCode',
            placeholder: 'Select Cause Code',
            visible: true,
            required: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            style: "width:15%;",
            class: 'slds-m-right_small slds-m-left_xx-small',
            disabled: false
        },
        {
            label: 'Service Category',
            name: 'Service Category',
            dataid: 'ServiceCategory',
            placeholder: 'Select Service Category',
            visible: true,
            required: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: 'slds-m-right_small',
            disabled: false,
            style: "width:15%;"
        }, {
            label: 'Type of Service',
            name: 'Type of Service',
            dataid: 'TypeOfService',
            placeholder: 'Select Type of Service',
            visible: true,
            required: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: 'slds-m-right_small',
            disabled: false,
            style: "width:15%;"
        }, {
            label: 'Place of Service',
            name: 'Place of Service',
            dataid: 'PlaceOfService',
            placeholder: 'Select Place of Service',
            visible: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: 'slds-m-right_small',
            disabled: false,
            style: "width:15%;"
        }, {
            label: 'PAR Code',
            name: 'PAR Code',
            dataid: 'PARCode',
            placeholder: 'Select PAR Code',
            visible: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: 'slds-m-right_small',
            disabled: false,
            style: "width:15%;"
        }, {
            label: 'Search',
            name: 'Search',
            dataid: 'Search',
            variant: 'brand',
            brand: 'brand',
            title: 'Search',
            button: true,
            style: "width:6%;",
            class: 'slds-m-right_small',
            disabled: true
        }, {
            label: 'Reset',
            name: 'Reset',
            dataid: 'Reset',
            variant: 'brand-outline',
            brand: 'brand-outline',
            title: 'Reset',
            button: true,
            style: "width:5%;",
            class: 'slds-m-right_small',
            disabled: false
        }, {
            filter: true,
            style: "width:3%;",
            class: 'slds-m-right_x-small'
        }
    ];


    get generateLogId() {
        return Math.random().toString(16).slice(2);
    }

    get expandCollapseIcon() {
        return this.expanded ? 'utility:chevrondown' : 'utility:chevronright';
    }

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        this.loggingkey = getLoggingKey(this.pageRef);
    }


    createRelatedField() {
        return [{
            label: 'Related Field',
            value: this.relatedInputField
        }];
    }


    renderedCallback() {
        Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    createDetailRelatedField() {
        return [{
            label: 'Plan Member Id',
            value: this.memName
        }];
    }

    handleDetailLogging(event) {
        let evt = event.detail.eventdetail;
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                evt,
                this.createDetailRelatedField(),
                this.screenName,
                this.loggingkey,
                this.pageRef
            );
          

        } else {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(
                        evt,
                        this.createDetailRelatedField(),
                        this.screenName,
                        this.loggingkey,
                        this.pageRef
                    );
                   
                }
            });
        }
    }


    handleChange(event) {
        let comboId = event.detail.name;
        switch (comboId) {
            case "CauseCode":
                this.selectedCauseCode = event.detail.value;
                this.placeOfServiceOptions = [];
                this.typeOfServiceOptions = [];
                this.serviceCategoryOptions = [];
                this.parCodeOptions = [];
                this.selectedCategoryCode = '';
                this.selectedTypeOfService = '';
                this.selectedPlaceOfService = '';
                this.selectedPARCode = '';
                this.updateFilterConfig('Cause Code', this.selectedCauseCode, this.causeCodesOptions ? this.causeCodesOptions : '');
                this.prepareServiceCategoryData(null);
                break;
            case "ServiceCategory":
                this.selectedCategoryCode = event.detail.value;
                this.selectedTypeOfService = '';
                this.selectedPlaceOfService = '';
                this.selectedPARCode = '';
                this.typeOfServiceOptions = [];
                this.placeOfServiceOptions = [];
                this.parCodeOptions = [];
                this.prepareTypeOfServiceData();
                this.updateFilterConfig('Service Category', this.selectedCategoryCode, this.serviceCategoryOptions ? this.serviceCategoryOptions : '');
                break;
            case "TypeOfService":
                this.selectedTypeOfService = event.detail.value;
                this.selectedPlaceOfService = '';
                this.selectedPARCode = '';
                this.placeOfServiceOptions = [];
                this.parCodeOptions = [];
                this.preparePlaceOfServiceData();
                this.preparePARData();
                this.updateFilterConfig('Type of Service', this.selectedTypeOfService, this.typeOfServiceOptions ? this.typeOfServiceOptions : '');
                break;
            case "PlaceOfService":
                this.selectedPlaceOfService = event.detail.value;
                this.selectedPARCode = '';
                this.updateFilterConfig('Place of Service', this.selectedPlaceOfService, this.placeOfServiceOptions ? this.placeOfServiceOptions : '');
                break;
            case "PARCode":
                this.selectedPARCode = event.detail.value;
                this.updateFilterConfig('PAR Code', this.selectedPARCode, this.parCodeOptions ? this.parCodeOptions : '');
                break;
        }
    }


    updateFilterConfig(name, value, options) {
        let allvalid = false;
        let index = this.filterconfig.findIndex(k => k.label === name);
        if (index >= 0) {
            this.filterconfig[index].value = value;
            this.filterconfig[index].options = options;
        }
        if (this.filterconfig && Array.isArray(this.filterconfig) && this.filterconfig?.length > 0) {
            this.filterconfig.forEach(k => {
                if (k?.required)
                    allvalid = k && k?.required && k?.value?.length > 0 ? true : false;
            })
        }
        if (allvalid) {
            this.filterconfig.find(k => k?.label === 'Search' && k?.button === true).disabled = false;
        }
    }


    clearSearchData() {
        this.clearSearchFilter();
    }

    handleFilterBtnClick(event) {
        if (event && event?.detail) {
            switch (event.detail.name) {
                case "Search":
                    this.getBenefitSearchData();
                    break;
                case "Reset":
                    this.clearFilters();
                    break;
                case "Clear":
                    this.clearSearchFilter();
                    break;
            }
        }
    }


    clearSearchFilter() {
        this.filteredcount = 0;
        this.filteredTotalBenefits = [];
        this.filteredBenefits = [];
        if (this.filteredcount != undefined && this.totalBenefits?.length != undefined) {
            if ((this.filteredcount + INITIAL_LOAD_RECODRS) >= this.totalBenefits?.length) {
                this.filteredcount = this.totalBenefits?.length;
                this.filteredBenefits = this.totalBenefits;
            } else {
                this.filteredcount = this.filteredcount + INITIAL_LOAD_RECODRS;
                this.filteredBenefits = this.totalBenefits.slice(0, this.filteredcount);
            }
        } else {
            this.datafound = false;
        }
        this.totalCount = this.totalBenefits?.length;
        this.initialLoad = this.totalCount == 0 ? true : false;
    }


    handleKeyWordValue({ detail }) {
        if (detail && detail?.value && detail?.value?.length >= 3) {
            this.filteredTotalBenefits = [];
            this.filteredcount = 0;
            this.filteredBenefits = [];
            if (this.totalBenefits && Array.isArray(this.totalBenefits) && this.totalBenefits.length > 0) {
                this.totalBenefits.forEach(k => {
                    if (k) {
                        Object.values(k).forEach(t => {
                            if (t && String(t)?.toLowerCase().includes(detail?.value?.toLowerCase()) && !this.filteredTotalBenefits.includes(k)) {
                                this.filteredTotalBenefits.push(k);
                            }
                        })
                    }
                })
            }
            if (this.filteredcount != undefined && this.filteredTotalBenefits?.length != undefined) {
                if ((this.filteredcount + INITIAL_LOAD_RECODRS) >= this.filteredTotalBenefits?.length) {
                    this.filteredcount = this.filteredTotalBenefits?.length;
                    this.filteredBenefits = this.filteredTotalBenefits;
                } else {
                    this.filteredcount = this.filteredcount + INITIAL_LOAD_RECODRS;
                    this.filteredBenefits = this.filteredTotalBenefits.slice(0, this.filteredcount);
                }
            } else {
                this.datafound = false;
            }
            this.totalCount = this.filteredTotalBenefits?.length;
            this.initialLoad = this.totalCount == 0 ? true : false;
            this.setScrollbarPosition();
        }
    }

    getIndex(element, event, classname) {
        let indexValue;
        try {
            if (element != null && element?.classList && element?.classList?.value
                && element?.classList?.value?.includes(classname)) {
                indexValue = element.getAttribute('data-item');
                this.handleLogging(event, indexValue);
            } else {
                if (element && element?.parentNode) {
                    this.getIndex(element.parentNode, event, classname);
                }
            }
        }
        catch (e) {
            console.log('Error: ' + e);
        }

    }

    handleSearchBenefitsLogging(event) {
        this.getIndex(event.target, event, 'benefitsearchtablerow');
    }

    handleLogging(event, rowIndex) {
        let evt;
        let tableData;
        let col;
        if (event.detail.eventdetail && event.detail.limitsdata) {
            evt = event.detail.eventdetail;
            tableData = event.detail.limitsdata;
            col = event.detail.columns;
            rowIndex = event.detail.index;
        }
        else {
            evt = event;
            tableData = this.filteredBenefits;
            col = this.columns;
        }

        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performTableLogging(evt, tableData, this.relatedInputField, col, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey, rowIndex);
          
        } else {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performTableLogging(evt, tableData, this.relatedInputField, col, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey, rowIndex);
                  
                }
            });
        }
    }

    handleLimitLogging(event) {
        this.handleLogging(event, null);
    }


    toggleDetailSection(event) {
        let divele = this.template.querySelector(`[data-key="${event.target.dataset.rowid}"]`);
        if (divele) {
            divele.classList.toggle('slds-hide');
        }
        if (event.target.iconName === 'utility:chevronright') {
            event.target.iconName = 'utility:chevrondown';
        } else {
            event.target.iconName = 'utility:chevronright';
        }
    }

    labels = {
        benefisearchfiltererror,
        benefitsearcherror
    }

    connectedCallback() {
        this.initialSetUp();
    }

    initialSetUp() {
        if (this.filterconfig && Array.isArray(this.filterconfig) && this.filterconfig?.length > 0) {
            this.filterconfig.forEach(k => {
                k.value = '';
                k.options = null;
            });
        }

        if (!this.serviceerror) {
            this.prepareData();
        }
        getLoggingKey(this.pageRef).then(result => {
            this.loggingkey = result;
        });
        if (this.relatedInputField[0].value == undefined) this.relatedInputField[0].value = this.memName;
        this.loaded = true;
    }

    prepareData() {
        this.benefitModel = getModal('benefit');
        this.columns = this.benefitModel;
        this.message = INITIAL_LOAD_MESSAGE;
        this.isLVPlatform = this.platformCode === 'EM' ? false : (this.platformCode === 'LV' || this.platformCode === 'CI' || this.platformCode === 'CAS') ? true : false;
        if (this.isLVPlatform) {
            this.prepareCauseCodesData();
        } else {
            this.colBool=true;
            this.columns = this.columns.filter(k => k?.label !== 'Cause Code');
            this.filterconfig.shift();
            this.prepareServiceCategoryData('pageload');
        }
        this.disableFilterButton = false;
    }


    @api
    setPBEData(pberesponse, isMedical, isDental, platformCode, productCode) {
        this.isMedical = isMedical;
        this.isDental = isDental;
        this.platformCode = platformCode;
        this.pberesponse = pberesponse;
        this.productId = productCode;
        this.prepareData();
        this.loaded = true;
    }


    @api
    displayErrorMessage(serviceError, message) {
        this.loaded = true;
        this.serviceerror = serviceError;
        this.message = message;
    }


    prepareServiceCategoryData(calledfrom) {
        let sData = [];
        this.serviceCategoryOptions = [];
        if (calledfrom) {
            sData = this.pberesponse?.ServiceCategoryList?.ServiceCategory ?? null;
            if (sData) {
                this.serviceCategoryOptions = sData.map((item) => ({
                    label: item.Name,
                    value: item.CodeList != null && item.CodeList.Code != null
                        && Array.isArray(item.CodeList.Code) ? item.CodeList.Code.join(',') : ''
                }))
            }
        } else {
            sData = this.pberesponse?.ServiceTypeList?.ServiceType.filter(t => t.CauseCode === this.selectedCauseCode) ?? null;
            const sCategoryList = new Set();
            if (sData) {
                sData.forEach(k => {
                    if (!sCategoryList.has(k.ServiceCategoryCode)) {
                        sCategoryList.add(k.ServiceCategoryCode);
                    }
                })
                sData = this.pberesponse?.ServiceCategoryList?.ServiceCategory ?? null;
                if (sData) {
                    sData.forEach(o => {
                        let sCode = o.CodeList != null && o.CodeList.Code != null
                            && Array.isArray(o.CodeList.Code) ? o.CodeList.Code.join(',') : '';
                        if (sCode && sCode != '') {
                            if (sCategoryList.has(sCode)) {
                                this.serviceCategoryOptions.push({
                                    label: o.Name,
                                    value: sCode
                                });
                            }
                        }
                    })
                }
            }
        }
        if (this.serviceCategoryOptions.length > 0) {
            this.serviceCategoryOptions.sort(function (a, b) {
                return a.label > b.label ? 1 : -1
            })
        }
        this.updateFilterConfig('Service Category', null, this.serviceCategoryOptions ? this.serviceCategoryOptions : '');
    }


    prepareCauseCodesData() {
        this.causeCodesOptions = [];
        if (this.platformCode == "LV" || this.platformCode == "CI" || this.platformCode == "CAS") {
            if (this.pberesponse?.CauseCodeList?.CauseCode && Array.isArray(this.pberesponse?.CauseCodeList?.CauseCode)) {
                this.causeCodesOptions =
                    this.pberesponse?.CauseCodeList?.CauseCode.map((item) => ({
                        label: `${item.Name} (${item.Code})`,
                        value: item.Code
                    }))
            }
        }
        if (this.causeCodesOptions.length > 0) {
            this.causeCodesOptions.sort(function (a, b) {
                return a.label > b.label ? 1 : -1
            })
        }
        this.updateFilterConfig('Cause Code', null, this.causeCodesOptions ? this.causeCodesOptions : '');
    }


    handleChange(event) {
        let comboId = event.detail.name;
        switch (comboId) {
            case "CauseCode":
                this.selectedCauseCode = event.detail.value;
                this.placeOfServiceOptions = [];
                this.typeOfServiceOptions = [];
                this.serviceCategoryOptions = [];
                this.parCodeOptions = [];
                this.selectedCategoryCode = '';
                this.selectedTypeOfService = '';
                this.selectedPlaceOfService = '';
                this.selectedPARCode = '';
                this.updateFilterConfig('Cause Code', this.selectedCauseCode, this.causeCodesOptions ? this.causeCodesOptions : '');
                this.prepareServiceCategoryData(null);
		        this.prepareTypeOfServiceData(null);
                this.preparePlaceOfServiceData(null);
                this.preparePARData(null);
                break;
            case "ServiceCategory":
                this.selectedCategoryCode = event.detail.value;
                this.selectedTypeOfService = '';
                this.selectedPlaceOfService = '';
                this.selectedPARCode = '';
                this.typeOfServiceOptions = [];
                this.placeOfServiceOptions = [];
                this.parCodeOptions = [];
                this.prepareTypeOfServiceData();
                this.updateFilterConfig('Service Category', this.selectedCategoryCode, this.serviceCategoryOptions ? this.serviceCategoryOptions : '');
		        this.preparePlaceOfServiceData(null);
                this.preparePARData(null);
                break;
            case "TypeOfService":
                this.selectedTypeOfService = event.detail.value;
                this.selectedPlaceOfService = '';
                this.selectedPARCode = '';
                this.placeOfServiceOptions = [];
                this.parCodeOptions = [];
                this.preparePlaceOfServiceData();
                this.preparePARData();
                this.updateFilterConfig('Type of Service', this.selectedTypeOfService, this.typeOfServiceOptions ? this.typeOfServiceOptions : '');
                break;
            case "PlaceOfService":
                this.selectedPlaceOfService = event.detail.value;
                this.selectedPARCode = '';
                this.updateFilterConfig('Place of Service', this.selectedPlaceOfService, this.placeOfServiceOptions ? this.placeOfServiceOptions : '');
                break;
            case "PARCode":
                this.selectedPARCode = event.detail.value;
                this.updateFilterConfig('PAR Code', this.selectedPARCode, this.parCodeOptions ? this.parCodeOptions : '');
                break;
        }
    }


    preparePARData() {
        this.parCodeOptions = [];
        let parServiceNode = [];
        if (this.selectedTypeOfService != null && this.selectedTypeOfService != undefined && (this.platformCode === 'LV' || this.platformCode === "CI" || this.platformCode === "CAS")) {
            parServiceNode = this.pberesponse?.ServiceTypeList?.ServiceType.filter(k => k.ServiceTypeId === this.selectedTypeOfService) ?? null;
            if (parServiceNode && Array.isArray(parServiceNode) && parServiceNode.length > 0) {
                parServiceNode.forEach(m => {
                    let placeOfServiceCodes = m.ParCodeIDList.Code
                    placeOfServiceCodes.forEach(o => {
                        this.parCodeOptions.push({
                            label: this.pberesponse?.ParcodeList?.ParCode.find(h => h.Code === o)?.Name + ' (' + o + ')',
                            value: o
                        });
                    })
                })
            }
            if (this.parCodeOptions.length > 0) {
                this.parCodeOptions.sort(function (a, b) {
                    return a.label > b.label ? 1 : -1
                })
            }
        } else {
            this.parCodeOptions = ParCodes;
        }
        this.updateFilterConfig('PAR Code', null, this.parCodeOptions ? this.parCodeOptions : '');

    }


    preparePlaceOfServiceData() {
        let sData = [];
        this.placeOfServiceOptions = [];
        sData = this.pberesponse?.ServiceTypeList?.ServiceType.filter(k => k.ServiceTypeId === this.selectedTypeOfService) ?? null;
        if (sData && Array.isArray(sData) && sData.length > 0) {
            sData.forEach(k => {
                let placeOfServiceNode = k.PlaceOfServiceCodeList.Code
                placeOfServiceNode.forEach(o => {
                    this.placeOfServiceOptions.push({
                        label: this?.pberesponse?.PlaceOfServiceList?.PlaceOfService.find(h => h.Code === o)?.Name,
                        value: o
                    });
                })
            })
        }
        if (this.placeOfServiceOptions.length > 0) {
            this.placeOfServiceOptions.sort(function (a, b) {
                return a.label > b.label ? 1 : -1
            })
        }
        this.updateFilterConfig('Place of Service', null, this.placeOfServiceOptions ? this.placeOfServiceOptions : '');

    }

    prepareTypeOfServiceData() {
        let sData = [];
        this.typeOfServiceOptions = [];
        let serviceCodes = this.selectedCategoryCode?.split(',') ?? null;
        if (this.isLVPlatform) {
            if (serviceCodes && Array.isArray(serviceCodes) && serviceCodes?.length > 0) {
                serviceCodes.forEach(h => {
                    sData =
                        this.pberesponse?.ServiceTypeList?.ServiceType.filter(
                            (k) =>
                                k.CauseCode === this.selectedCauseCode &&
                                k.ServiceCategoryCode === h
                        ) ?? null;
                    if (sData && Array.isArray(sData) && sData.length > 0) {
                        sData.forEach((o) => {
                            this.typeOfServiceOptions.push({
                                label: o.ServiceName + "-" + o.ServiceTypeId,
                                value: o.ServiceTypeId,
                            });
                        });
                    }
                })
            }
        } else {
            if (serviceCodes && Array.isArray(serviceCodes) && serviceCodes?.length > 0) {
                serviceCodes.forEach(h => {
                    sData =
                        this.pberesponse?.ServiceTypeList?.ServiceType.filter(
                            (k) => k.ServiceCategoryCode === h
                        ) ?? null;
                    if (sData && Array.isArray(sData) && sData.length > 0) {
                        sData.forEach((o) => {
                            this.typeOfServiceOptions.push({
                                label: o.ServiceName + "-" + o.ServiceTypeId,
                                value: o.ServiceTypeId,
                            });
                        });
                    }
                })
            }
        }
        if (this.typeOfServiceOptions.length > 0) {
            this.typeOfServiceOptions.sort(function (a, b) {
                return a.label > b.label ? 1 : -1
            })
        }
        this.updateFilterConfig('Type of Service', null, this.typeOfServiceOptions ? this.typeOfServiceOptions : '');

    }


    closeFilterSection() {
        this.displayFilters = false;
    }


    displayFilterSection() {
        this.displayFilters = true;
    }

    getBenefitSearchData() {
        this.initialLoad = false;
        this.isLoading = true;
        this.isError = false;
        this.lstServiceCategoryCode = [];
        this.isCAS = (this.platformCode === 'LV' || this.platformCode === 'CI' || this.platformCode === 'CAS') ? true : false;
        this.addPillFilter();
        this.showPillFilterValues = true;
        let serviceCodes = this.selectedCategoryCode.split(',');
        if (this.isCAS) {
            serviceCodes.forEach(k => {
                this.lstServiceCategoryCode.push(k);
            })
        } else {
            let categoryCode = this.pberesponse?.ServiceTypeList?.ServiceType?.find(k => k?.ServiceTypeId === this.selectedTypeOfService)?.ServiceCategoryCode ?? '';
            this.lstServiceCategoryCode.push(categoryCode);
        }
        this.loaded = false;
        this.clearSearchLogData();

        invokePBEService('benefitsearch', {
            lstServiceCategoryCode: this.lstServiceCategoryCode,
            selectedTypeOfService: this.selectedTypeOfService,
            selectedPlaceOfService: this.selectedPlaceOfService,
            selectedPARCode: this.selectedPARCode,
            platformCode: this.platformCode,
            productId: this.productId,
            refdate: '',
            isCAS: this.isCAS,
            ADACode: ''
        }).then(result => {
            this.isLoading = false;
            this.isError = false;
            this.BenefitSearchResponse = JSON.parse(result);
            this.processResponse();
            this.datafound = true;
            this.loaded = true;
        }).catch(error => {
            this.isLoading = false;
            this.BenefitSearchResponse = error;
            this.message = this.labels.benefitsearcherror;
            this.isError = true;
            this.datafound = false;
            console.log(error);
            this.loaded = true;
        });
        this.closeFilterSection();
    }


    setScrollbarPosition() {
        let divElement = this.template.querySelector('[data-id="result"]');
        if (divElement) {
            divElement.scrollTop = 0;
        }
    }


    processResponse() {
        this.totalBenefits = [];
        this.filteredBenefits = [];
        this.totalCount = 0;
        this.filteredcount = 0;
        if (this.BenefitSearchResponse && this.BenefitSearchResponse?.LimitFalseLst) {
            let limitNode = this.BenefitSearchResponse.LimitFalseLst;
            if (limitNode && Array.isArray(limitNode) && limitNode.length > 0) {
                this.setScrollbarPosition();
                limitNode.forEach((k, index) => {
                    let benefitData = {};
                    let benefittype;
                    benefitData.Index = index;
                    benefitData.IsExpand = true;
                    benefitData.BenefitDescription = this.getbenefitDescription(k.BenefitDescription, k.benefitType);
                    benefittype = k.benefitType && k.benefitType.includes('_Service') ? k.benefitType.split('_')[0] : k.benefitType;
                    benefitData.BenefitType = benefittype === 'Not Covered' ? '' : benefittype;
                    benefitData.CoverageType = k.CoverageType;
                    benefitData.PlaceOfService = this.pberesponse?.PlaceOfServiceList?.PlaceOfService &&
                        Array.isArray(this.pberesponse?.PlaceOfServiceList?.PlaceOfService) ? this.pberesponse.PlaceOfServiceList.PlaceOfService.find(h => h.Code === k.placeOfServiceCode)?.Name : '';
                    benefitData.TierNumber = k.TierNumber != '' ? k.TierNumber : '';
                    benefitData.Par = k.benefitType === 'Not Covered' ? '' : k.Par;
                    benefitData.NonPar = k.benefitType === 'Not Covered' ? '' : k.NonPar;
                    benefitData.ProviderSubNetworkNumber = k.ProviderSubNetworkNumber === '' ? '' : k.ProviderSubNetworkNumber;
                    benefitData.Notes = k.notes === '' ? '' : k.notes;
                    benefitData.NodeType = k.nodeType;
                    benefitData.serviceComments = this.BenefitSearchResponse && this.BenefitSearchResponse?.lstOfServiceComments && Object.keys(this.BenefitSearchResponse.lstOfServiceComments).length > 0 && this.BenefitSearchResponse.lstOfServiceComments[k.ServiceTypeID] != undefined ? this.BenefitSearchResponse.lstOfServiceComments[k.ServiceTypeID].length > 0 ? this.BenefitSearchResponse.lstOfServiceComments[k.ServiceTypeID] : '' : '';
                    benefitData.ServiceTypeID = k.ServiceTypeID;
                    benefitData.ServiceCatCode = k.ServiceCatCode;
                    benefitData.PARNonPar = benefitData.Par + '/' + benefitData.NonPar;
                    benefitData.IsExpanded = false;
                    benefitData.benefitLimits = this.BenefitSearchResponse?.lstOfLimits?.Service && Array.isArray(this.BenefitSearchResponse?.lstOfLimits?.Service)
                        && this.BenefitSearchResponse?.lstOfLimits?.Service?.length > 0 ? this.BenefitSearchResponse.lstOfLimits.Service.filter(h => h.placeOfServiceCode === k.placeOfServiceCode && h.TierNumber === k.TierNumber && h.ServiceTypeID === k.ServiceTypeID && (h.limitAmount == k.Par || h.limitAmount == k.NonPar) && (h.BenefitDescription == benefitData.BenefitDescription || h.BenefitDescription == k.BenefitDescription)) : [];
 		            
                    benefitData.TypeOfService =
                        this.typeOfServiceOptions &&
                            Array.isArray(this.typeOfServiceOptions)
                            ? this.typeOfServiceOptions.find(
                                (h) => h.value === this.selectedTypeOfService
                            )?.label
                            : "";

                    benefitData.CategoryType = this.serviceCategoryOptions && 
                                Array.isArray(this.serviceCategoryOptions)
                                 ? this.serviceCategoryOptions.find(
                                    (h) => h.value === this.selectedCategoryCode
                                )?.label
                                : ""; 

                    benefitData.CauseCodeValue = this.causeCodesOptions && 
                                Array.isArray(this.causeCodesOptions)
                                 ? this.causeCodesOptions.find(
                                    (h) => h.value === this.selectedCauseCode
                                )?.label
                                : ""; 
                                
                    this.totalBenefits.push(benefitData);
                    this.totalCount = this.totalBenefits.length;
                });
                if (this.totalBenefits.length > INITIAL_LOAD_RECODRS) {
                    this.filteredBenefits = this.totalBenefits.slice(0, 10);
                    this.filteredcount = INITIAL_LOAD_RECODRS;
                } else {
                    this.filteredBenefits = this.totalBenefits;
                    this.filteredcount = this.totalCount;
                }

            } else {
                this.message = this.labels.benefisearchfiltererror;
                this.isError = true;
            }

        } else {
            this.message = this.labels.benefisearchfiltererror;
            this.isError = true;
        }
    }

    getbenefitDescription(benDescription, benType) {
        let parCodeFound = false;
        let benDescriptionName;
        if (!this.isCAS) {
            return benDescription;
        } else {
            if (benType && benType === 'Not Covered') {
                return 'Not Covered';
            } else {
                if (this.pberesponse?.ParcodeList?.ParCode && Array.isArray(this?.pberesponse?.ParcodeList?.ParCode) &&
                    this?.pberesponse?.ParcodeList?.ParCode.length > 0) {
                    benDescriptionName = this.pberesponse.ParcodeList.ParCode.find(k => k.Code === benDescription)?.Name ?? null;
                    parCodeFound = benDescriptionName ? true : false;
                }
                if (!parCodeFound) {
                    benDescriptionName = benDescription === 'Not Covered' ? 'Not Covered' : '';
                }
                return benDescriptionName;
            }
        }
    }

    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.filteredcount != undefined && this.totalCount != undefined) {
                if ((this.filteredcount + INITIAL_LOAD_RECODRS) >= this.totalCount) {
                    this.filteredcount = this.totalCount;
                    this.filteredBenefits = this.totalBenefits;
                } else {
                    this.filteredcount = this.filteredcount + INITIAL_LOAD_RECODRS;
                    this.filteredBenefits = this.totalBenefits.slice(0, this.filteredcount);
                }
            }
        }
    }

    get mandatoryFieldsSelected() {
        if ((this.isLVPlatform && Boolean(this.selectedCauseCode) && Boolean(this.selectedCategoryCode) && Boolean(this.selectedTypeOfService)) || (!this.isLVPlatform && Boolean(this.selectedCategoryCode) && Boolean(this.selectedTypeOfService))) {
            this.enableApply = true;
            return this.enableApply;
        } else {
            this.enableApply = false;
            return this.enableApply;
        }
    }



    clearFilters() {
        this.selectedCauseCode = null;
        this.selectedCategoryCode = null;
        this.selectedTypeOfService = null;
        this.selectedPlaceOfService = null;
        this.selectedPARCode = null;
        this.updateFilterConfig('Service Category', null, this.serviceCategoryOptions ? this.serviceCategoryOptions : '');
        this.updateFilterConfig('PAR Code', null, this.parCodeOptions ? this.parCodeOptions : '');
        this.updateFilterConfig('Place of Service', null, this.placeOfServiceOptions ? this.placeOfServiceOptions : '');
        this.updateFilterConfig('Type of Service', null, this.typeOfServiceOptions ? this.typeOfServiceOptions : '');
        this.updateFilterConfig('Cause Code', null, this.causeCodesOptions ? this.causeCodesOptions : '');
        this.cleardata();
        if (this.isLVPlatform) {
            this.serviceCategoryOptions = [];
            this.typeOfServiceOptions = [];
            this.placeOfServiceOptions = [];
            this.parCodeOptions = [];
        } else {
            this.typeOfServiceOptions = [];
            this.placeOfServiceOptions = [];
            this.parCodeOptions = [];
        }
        this.filterconfig.find(k => k?.label === 'Search' && k?.button === true).disabled = true;
    }


    handleRemoveFilter(event) {
        this.SelectedFilters.splice(event.target.dataset.index, 1);
        if (event.target.dataset.key == 'Place Of Service') {
            this.selectedPlaceOfService = '';
            this.getBenefitSearchData();
        } else if (event.target.dataset.key == 'PAR Code') {
            this.selectedPARCode = '';
            this.getBenefitSearchData();
        }
        else if (event.target.dataset.key == 'Cause Code') {
            this.displayFilters = true;
            this.selectedCauseCode = null;
            this.selectedCategoryCode = null;
            this.selectedTypeOfService = null;
            this.selectedPlaceOfService = null;
            this.selectedPARCode = null;
            this.serviceCategoryOptions = [];
            this.typeOfServiceOptions = [];
            this.placeOfServiceOptions = [];
            this.parCodeOptions = [];
            this.cleardata();
        }
        else if (event.target.dataset.key == 'Service Category') {
            this.displayFilters = true;
            this.selectedCategoryCode = null;
            this.selectedTypeOfService = null;
            this.selectedPlaceOfService = null;
            this.selectedPARCode = null;
            this.typeOfServiceOptions = [];
            this.placeOfServiceOptions = [];
            this.parCodeOptions = [];
            this.cleardata();
        }
        else if (event.target.dataset.key == 'Type of Service') {
            this.displayFilters = true;
            this.selectedTypeOfService = null;
            this.selectedPlaceOfService = null;
            this.selectedPARCode = null;
            this.placeOfServiceOptions = [];
            this.parCodeOptions = [];
            this.cleardata();
        }

    }

    cleardata() {
        this.totalBenefits = [];
        this.filteredBenefits = [];
        this.SelectedFilters = [];
        this.enableApply = false;
        this.showPillFilterValues = false;
        this.totalCount = 0;
        this.filteredcount = 0;
        this.isError = false;
        this.initialLoad = true;
    }


    addPillFilter() {
        this.SelectedFilters = [];
        if (this.selectedCauseCode) {
            this.addPillValue('Cause Code', this.causeCodesOptions, this.selectedCauseCode);
        }
        if (this.selectedCategoryCode) {
            this.addPillValue('Service Category', this.serviceCategoryOptions, this.selectedCategoryCode);
        }
        if (this.selectedTypeOfService) {
            this.addPillValue('Type of Service', this.typeOfServiceOptions, this.selectedTypeOfService);
        }
        if (this.selectedPlaceOfService) {
            this.addPillValue('Place Of Service', this.placeOfServiceOptions, this.selectedPlaceOfService);
        }
        if (this.selectedPARCode) {
            this.addPillValue('PAR Code', this.parCodeOptions, this.selectedPARCode);
        }
    }


    addPillValue(label, data, selectvalue) {
        let pillvalue = {
            label: label,
            value: `${label} : ${data.find(k => k.value === selectvalue)?.label ?? null}`
        };
        if (pillvalue && pillvalue?.value) {
            this.SelectedFilters.push(pillvalue);
        }

    }


    get getStyle() {
        let maxheight = window.innerHeight - 160;
        return ' border: 1px solid #dddbda ;width:320px;height: ' + maxheight + 'px;';
    }


    get getResultStyle() {
        if (this.datafound) {
            return "height:290px;";
        } else {
            return "height:100px;";
        }
    }

    async clearSearchLogData() {
        await clearSearchCriteriaLog(this.screenName, this.pageRef);
    }
}