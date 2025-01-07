/*
LWC Name        : benefitSearchDentalHum.js
Function        : LWC to display benefit search details.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Kalyani Pachpol                 06/01/2023                   initial version - US - 4654362
* Kinal Mangukiya                 07/07/2023                   US 4658257 - Dental UI Logging Search
* Kalyani Pachpol                 06/12/2023                   DF-7747
* Kalyani Pachpol                 06/14/2023                   DF-7757
* Monali Jagtap                   15/06/2023                   DF-7762
* Atul Patil					  09/14/2023				   T1PRJ0865978 - MF 28289 - C02, Contact Servicing/Benefits- Add "Category Type" and " Type of Service" to Table Results after Search - DENTAL
****************************************************************************************************************************/

import { LightningElement, api, track, wire } from "lwc";
import { getModal } from "./layoutConfig";
import benefisearchfiltererror from "@salesforce/label/c.BENEFITSEARCH_FILTER_ERROR";
import benefitsearcherror from "@salesforce/label/c.BENEFITSEARCH_ERROR";
import serviceerrormessage from "@salesforce/label/c.PharmacyAuth_Error_ServiceCallout";
import { performTableLogging, performLogging, getLoggingKey, checkloggingstatus } from 'c/loggingUtilityHum';
import { CurrentPageReference } from "lightning/navigation";
import loggingcss from "@salesforce/resourceUrl/LightningCRMAssets_SR_HUM";
import { loadStyle } from "lightning/platformResourceLoader";
const ParCodes = [
    { label: "Both", value: "Both" },
    { label: "Non Par", value: "No" },
    { label: "Par", value: "Yes" },
];
const INITIAL_LOAD_RECODRS = 10;
import { invokePBEService } from "c/benefitsServiceHelperHum";
export default class BenefitSearchDentalHum extends LightningElement {
    @api platformCode;
    @api pberesponse;
    @api productId;
    @api serviceerror;
    @api message;
    @api memberplanname;
    @track causeCodesOptions = [];
    @track selectedCauseCode;
    @track serviceCategoryOptions = [];
    @track selectedCategoryCode;
    @track selectedTypeOfService;
    @track selectedPARCode;
    @track selectedPlaceOfService;
    @track selectedAdaCode;
    @track displayFilters = true;
    @track typeOfServiceOptions = [];
    @track placeOfServiceOptions = [];
    @track adaCodeOptions = [];
    @track parCodeOptions = [];
    @track initialLoad = true;
    @track isCAS = false;
    @track productId;
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
    @track datafound = false;
    @track disableFilterButton = true;
    @api pbeService;
    @track loaded = false;
    @track serviceError = false;
    @track totalBenefits = [];
    @track filteredBenefits = [];
    @track filteredTotalBenefits = [];
    @track disabledropdowns = false;
    @track loggingkey;
    @track filterconfig = [
        {
            label: "Category Code",
            name: "Category Code",
            dataid: "CategoryCode",
            placeholder: "Select",
            visible: true,
            required: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: "slds-m-right_small",
            style: "width:16%;",
            disabled: false,
        },
        {
            label: "Type of Service",
            name: "Type of Service",
            dataid: "TypeOfService",
            placeholder: "Select",
            visible: true,
            required: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: "slds-m-right_small",
            style: "width:16%;",
            disabled: false,
        },
        {
            label: "Place of Service",
            name: "Place of Service",
            dataid: "PlaceOfService",
            placeholder: "Select",
            visible: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: "slds-m-right_small",
            style: "width:16%;",
            disabled: false,
        },
        {
            label: "PAR/Non-PAR/Both",
            name: "PAR/Non-PAR/Both",
            dataid: "PARCode",
            placeholder: "Select",
            visible: true,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: "slds-m-right_small",
            style: "width:16%;",
            disabled: false,
        },
        {
            label: "ADA Code",
            name: "ADA Code",
            dataid: "ADACode",
            placeholder: "Select",
            visible: true,
            required: false,
            multipicklist: false,
            combobox: true,
            options: [],
            values: [],
            class: "slds-m-right_small",
            style: "width:16%;",
            disabled: true,
        },
        {
            label: "Search",
            name: "Search",
            dataid: "Search",
            variant: "brand",
            brand: "brand",
            title: "Search",
            button: true,
            style: "width:5%;",
            class: "slds-m-right_small",
            disabled: true,
        },
        {
            label: "Reset",
            name: "Reset",
            dataid: "Reset",
            variant: "brand-outline",
            brand: "brand-outline",
            title: "Reset",
            button: true,
            style: "width:5%;",
            class: "slds-m-right_small",
            disabled: false,
        },
        {
            filter: true,
            style: "width:2%;",
            class: "slds-m-right_small",
        },
    ];


    @track screenName = "Dental Benefits Search";

    @track buttonConfig = [
        {
            label: "Search",
            variant: "brand",
            disabled: false,
            visible: false,
            class: "slds-col-size_1-of-12",
        },
    ];


    get generateLogId() {
        return Math.random().toString(16).slice(2);
    }


    get expandCollapseIcon() {
        return this.expanded ? "utility:chevrondown" : "utility:chevronright";
    }

    toggleDetailSection(event) {
        let divele = this.template.querySelector(
            `[data-key="${event.target.dataset.rowid}"]`
        );
        if (divele) {
            divele.classList.toggle("slds-hide");
        }
        if (event.target.iconName === "utility:chevronright") {
            event.target.iconName = "utility:chevrondown";
        } else {
            event.target.iconName = "utility:chevronright";
        }
    }

    labels = {
        benefisearchfiltererror,
        benefitsearcherror,
        serviceerrormessage,
    };


    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    @track relatedInputField = [{
        label: "Plan Member Id",
        value: this.memberplanname
    }, {
        label: "Section",
        value: this.screenName
    }, {
        label: "Index",
        mappingField: "Index"
    }];

    createRelatedField() {
        return [{
            label: 'Related Field',
            value: this.relatedInputField
        }];
    }

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        this.loggingkey = getLoggingKey(this.pageRef);
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


    handleSearchBenefitsLogging(event) {
        this.getIndex(event.target, event, 'benefitsearchtablerow');

    }


    handleChildLogging(evt) {
        let event = evt.detail.eventdetail;
        this.handleLogging(event);
    }


    connectedCallback() {
        this.initialSetUp();
    }

    initialSetUp() {
        this.filterconfig.forEach((k) => {
            k.value = "";
            k.options = null;
        });
        if (!this.serviceerror) {
            this.prepareData();
        }
        getLoggingKey(this.pageRef).then(result => {

            this.loggingkey = result;

        });
        if (this.relatedInputField[0].value == undefined) this.relatedInputField[0].value = this.memberplanname;
        this.loaded = true;
    }

    prepareData() {
        this.benefitModel = getModal("benefit");
        this.columns = this.benefitModel;
        this.isLVPlatform =
            this.platformCode === "EM"
                ? false
                : this.platformCode === "LV" ||
                    this.platformCode === "CI" ||
                    this.platformCode === "CAS"
                    ? true
                    : false;
        this.prepareServiceCategoryData("pageload");
        this.disableFilterButton = false;
    }

    updateFilterConfig(name, value, options) {
        let allvalid = false;
        let index = this.filterconfig.findIndex((k) => k.label === name);
        if (index >= 0) {
            this.filterconfig[index].value = value;
            this.filterconfig[index].options = options;
        }
        if (
            this.filterconfig &&
            Array.isArray(this.filterconfig) &&
            this.filterconfig?.length > 0
        ) {
            this.filterconfig.forEach((k) => {
                if (k?.required)
                    allvalid = k && k?.required && k?.value?.length > 0 ? true : false;
            });
        }
        if (allvalid) {
            this.filterconfig.find(
                (k) => k?.label === "Search" && k?.button === true
            ).disabled = false;
        }
    }

    updateButtonConfig(name) {
        let index = this.buttonConfig.findIndex((k) => k.label === name);
        if (index >= 0) {
            this.buttonConfig[index].visible =
                name == "Search" ? !this.isFilterAdded : this.isFilterAdded;
        }
    }

    prepareServiceCategoryData(calledfrom) {
        let sData = [];
        this.serviceCategoryOptions = [];
        if (calledfrom) {
            sData = this.pberesponse?.ServiceCategoryList?.ServiceCategory ?? null;
            if (sData) {
                this.serviceCategoryOptions = sData.map((item) => ({
                    label: item.Name,
                    value:
                        item?.CodeList &&
                            item?.CodeList?.Code &&
                            Array.isArray(item?.CodeList?.Code)
                            ? item.CodeList.Code.join(',')
                            : "",
                }));
            }
        } else {
            sData =
                this.pberesponse?.ServiceTypeList?.ServiceType.filter(
                    (t) => t.CauseCode === this.selectedCauseCode
                ) ?? null;
            const sCategoryList = new Set();
            if (sData) {
                sData.forEach((k) => {
                    if (!sCategoryList.has(k.ServiceCategoryCode)) {
                        sCategoryList.add(k.ServiceCategoryCode);
                    }
                });
                sData = this.pberesponse?.ServiceCategoryList?.ServiceCategory ?? null;
                if (sData) {
                    sData.forEach((o) => {
                        let sCode =
                            o.CodeList != null &&
                                o.CodeList.Code != null &&
                                Array.isArray(o.CodeList.Code)
                                ? o.CodeList.Code.join(',')
                                : "";
                        if (sCode && sCode != "") {
                            if (sCategoryList.has(sCode)) {
                                this.serviceCategoryOptions.push({
                                    label: o.Name,
                                    value: sCode,
                                });
                            }
                        }
                    });
                }
            }
        }
        if (this.serviceCategoryOptions.length > 0) {
            this.serviceCategoryOptions.sort(function (a, b) {
                return a.label > b.label ? 1 : -1;
            });
        }
        this.updateFilterConfig(
            "Category Code",
            null,
            this.serviceCategoryOptions ? this.serviceCategoryOptions : ""
        );
    }

    handleChange(event) {
        let comboId = event.detail.name;
        switch (comboId) {
            case "CategoryCode":
                this.selectedCategoryCode = event.detail.value;
                this.selectedTypeOfService = "";
                this.selectedPlaceOfService = "";
                this.selectedPARCode = "";
                this.typeOfServiceOptions = [];
                this.placeOfServiceOptions = [];
                this.parCodeOptions = [];
                this.adaCodeOptions = [];
                this.prepareTypeOfServiceData();
                this.prepareADACodeData();
                this.updateFilterConfig(
                    "Category Code",
                    this.selectedCategoryCode,
                    this.serviceCategoryOptions ? this.serviceCategoryOptions : ""
                );
                break;
            case "TypeOfService":
                this.selectedTypeOfService = event.detail.value;
                this.selectedPlaceOfService = "";
                this.selectedPARCode = "";
                this.placeOfServiceOptions = [];
                this.parCodeOptions = [];
                this.preparePlaceOfServiceData();
                this.preparePARData();
                this.updateFilterConfig(
                    "Type of Service",
                    this.selectedTypeOfService,
                    this.typeOfServiceOptions ? this.typeOfServiceOptions : ""
                );
                break;
            case "PlaceOfService":
                this.selectedPlaceOfService = event.detail.value;
                this.selectedPARCode = "";
                this.updateFilterConfig(
                    "Place of Service",
                    this.selectedPlaceOfService,
                    this.placeOfServiceOptions ? this.placeOfServiceOptions : ""
                );
                break;
            case "ADACode":
                this.selectedAdaCode = event.detail.value;
                this.updateFilterConfig(
                    "ADA Code",
                    this.selectedAdaCode,
                    this.adaCodeOptions ?? null
                );
                break;
            case "PARCode":
                this.selectedPARCode = event.detail.value;
                this.updateFilterConfig(
                    "PAR/Non-PAR/Both",
                    this.selectedPARCode,
                    this.parCodeOptions ? this.parCodeOptions : ""
                );

                break;
        }
    }


    preparePARData() {
        this.parCodeOptions = [];
        let parServiceNode = [];
        if (
            this.selectedTypeOfService != null &&
            this.selectedTypeOfService != undefined &&
            (this.platformCode === "LV" ||
                this.platformCode === "CI" ||
                this.platformCode === "CAS")
        ) {
            parServiceNode =
                this.pberesponse?.ServiceTypeList?.ServiceType.filter(
                    (k) => k.ServiceTypeId === this.selectedTypeOfService
                ) ?? null;
            if (
                parServiceNode &&
                Array.isArray(parServiceNode) &&
                parServiceNode.length > 0
            ) {
                parServiceNode.forEach((m) => {
                    let placeOfServiceCodes = m.ParCodeIDList.Code;
                    placeOfServiceCodes.forEach((o) => {
                        this.parCodeOptions.push({
                            label:
                                this.pberesponse?.ParcodeList?.ParCode.find((h) => h.Code === o)
                                    ?.Name +
                                " (" +
                                o +
                                ")",
                            value: o,
                        });
                    });
                });
            }
            if (this.parCodeOptions.length > 0) {
                this.parCodeOptions.sort(function (a, b) {
                    return a.label > b.label ? 1 : -1;
                });
            }
        } else {
            this.parCodeOptions = ParCodes;
        }
        this.updateFilterConfig(
            "PAR/Non-PAR/Both",
            null,
            this.parCodeOptions ? this.parCodeOptions : ""
        );
    }

    prepareADACodeData() {
        let sData = [];
        this.adaCodeOptions = [];
        sData = this.pberesponse?.ServiceTypeList?.ServiceType.filter(k => k.ServiceCategoryCode === this.selectedCategoryCode) ?? null;
        if (sData && Array.isArray(sData) && sData.length > 0) {
            sData.forEach(k => {
                let adaCodeList = k?.ServiceAdaCodeList?.Code ?? null;
                if (adaCodeList && Array.isArray(adaCodeList) && adaCodeList?.length > 0) {
                    adaCodeList.forEach(o => {
                        this.adaCodeOptions.push({
                            label: o,
                            value: o
                        });
                    })
                }
            })
        }
        if (this.adaCodeOptions.length > 0) {
            this.filterconfig.find((k) => k?.label === "ADA Code").disabled = false;
            this.adaCodeOptions.sort(function (a, b) {
                return a.label > b.label ? 1 : -1;
            });
        }
        else {
            this.filterconfig.find(k => k?.label === 'ADA Code').disabled = true;
        }
        this.updateFilterConfig(
            "ADA Code",
            null,
            this.adaCodeOptions ? this.adaCodeOptions : ""
        );
        this.updateButtonConfig("Search");
    }

    preparePlaceOfServiceData() {
        let sData = [];
        this.placeOfServiceOptions = [];
        sData =
            this.pberesponse?.ServiceTypeList?.ServiceType.filter(
                (k) => k.ServiceTypeId === this.selectedTypeOfService
            ) ?? null;
        if (sData && Array.isArray(sData) && sData.length > 0) {
            sData.forEach((k) => {
                let placeOfServiceNode = k.PlaceOfServiceCodeList.Code;
                placeOfServiceNode.forEach((o) => {
                    this.placeOfServiceOptions.push({
                        label: this?.pberesponse?.PlaceOfServiceList?.PlaceOfService.find(
                            (h) => h.Code === o
                        )?.Name,
                        value: o,
                    });
                });
            });
        }
        if (this.placeOfServiceOptions.length > 0) {
            this.placeOfServiceOptions.sort(function (a, b) {
                return a.label > b.label ? 1 : -1;
            });
        }
        this.placeOfServiceOptions.forEach(k => {
            if (k.value == "44") {
                this.selectedPlaceOfService = '44';
                this.updateFilterConfig('Place of Service', this.selectedPlaceOfService, this.placeOfServiceOptions ? this.placeOfServiceOptions : '');

            }
            else {
                this.updateFilterConfig('Place of Service', null, this.placeOfServiceOptions ? this.placeOfServiceOptions : '');
            }
        })
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
                return a.label > b.label ? 1 : -1;
            });
        }
        let generalOptions = {
            label: 'General',
            value: 'General'
        };
        this.typeOfServiceOptions.unshift(generalOptions);
        this.updateFilterConfig(
            "Type of Service",
            null,
            this.typeOfServiceOptions ? this.typeOfServiceOptions : ""
        );
    }

    closeFilterSection() {
        this.displayFilters = false;
    }


    displayFilterSection() {
        this.displayFilters = true;
    }

    displayPreviousSection() {
        this.disabledropdowns = false;
        this.updateFilterConfig(
            "Category Code",
            this.selectedCategoryCode,
            this.serviceCategoryOptions ? this.serviceCategoryOptions : ""
        );
        this.updateFilterConfig(
            "PAR/Non-PAR/Both",
            this.selectedPARCode,
            this.parCodeOptions ? this.parCodeOptions : ""
        );
        this.updateFilterConfig(
            "Place of Service",
            this.selectedPlaceOfService,
            this.placeOfServiceOptions ? this.placeOfServiceOptions : ""
        );
        this.updateFilterConfig(
            "Type of Service",
            this.selectedTypeOfService,
            this.typeOfServiceOptions ? this.typeOfServiceOptions : ""
        );
        this.updateFilterConfig(
            "ADA Code",
            this.selectedAdaCode,
            this.adaCodeOptions ?? null
        );
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
            )
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

    createDetailRelatedField() {
        return [{
            label: 'Plan Member Id',
            value: this.memberplanname
        }];
    }


    handleLimitLogging(event) {
        this.handleLogging(event, null);
    }

    setScrollbarPosition() {
        let divElement = this.template.querySelector('[data-id="result"]');
        if (divElement) {
            divElement.scrollTop = 0;
        }
        let containerElement = this.template.querySelector(".container");
        if (containerElement) {
            containerElement.scrollTop = 0;
        }
    }

    processResponse() {
        this.totalBenefits = [];
        this.filteredBenefits = [];
        this.totalCount = 0;
        this.filteredcount = 0;
        this.filteredTotalBenefits = [];
        this.datafound = false;
        
        if (
            this.BenefitSearchResponse &&
            this.BenefitSearchResponse?.LimitFalseLst
        ) {
            let limitNode = this.BenefitSearchResponse.LimitFalseLst;
            if (limitNode && Array.isArray(limitNode) && limitNode.length > 0) {
                this.setScrollbarPosition();

                limitNode.forEach((k, index) => {
                    let benefitData = {};
                    let benefittype;
                    benefitData.Index = index;
                    benefitData.IsExpand = true;
                    benefitData.BenefitDescription = this.getbenefitDescription(
                        k?.BenefitDescription ?? '', k?.benefitType ?? ''
                    );
                    
                    if(this.selectedTypeOfService ==''){
                        benefitData.TypeOfService =
                        this.typeOfServiceOptions &&
                            Array.isArray(this.typeOfServiceOptions)
                            ? this.typeOfServiceOptions.find(
                                (h) => h.value === k.ServiceTypeID
                            )?.label
                            : "";
                    }else{
                        benefitData.TypeOfService =
                        this.typeOfServiceOptions &&
                            Array.isArray(this.typeOfServiceOptions)
                            ? this.typeOfServiceOptions.find(
                                (h) => h.value === this.selectedTypeOfService
                            )?.label
                            : "";
                    }
                   
                    benefitData.CategoryType =
                    this.serviceCategoryOptions &&
                        Array.isArray(this.serviceCategoryOptions)
                        ? this.serviceCategoryOptions.find(
                            (h) => h.value === this.selectedCategoryCode
                        )?.label
                        : ""; 

                    benefittype =
                        k.benefitType && k.benefitType.includes("_Service")
                            ? k.benefitType.split("_")[0]
                            : k.benefitType;
                    benefitData.BenefitType =
                        benefittype === "Not Covered" ? "" : benefittype;
                    benefitData.CoverageType = k.CoverageType;
                    benefitData.PlaceOfService =
                        this.pberesponse?.PlaceOfServiceList?.PlaceOfService &&
                            Array.isArray(this.pberesponse?.PlaceOfServiceList?.PlaceOfService)
                            ? this.pberesponse.PlaceOfServiceList.PlaceOfService.find(
                                (h) => h.Code === k.placeOfServiceCode
                            )?.Name
                            : "";
                    benefitData.TierNumber = k.TierNumber != "" ? k.TierNumber : "";
                    benefitData.Par = k.benefitType === "Not Covered" ? "" : k.Par;
                    benefitData.NonPar = k.benefitType === "Not Covered" ? "" : k.NonPar;
                    benefitData.ProviderSubNetworkNumber =
                        k.ProviderSubNetworkNumber === "" ? "" : k.ProviderSubNetworkNumber;
                    benefitData.Notes = k.notes === "" ? "" : k.notes;
                    benefitData.NodeType = k.nodeType;
                    benefitData.serviceComments =
                        this.BenefitSearchResponse &&
                            this.BenefitSearchResponse?.lstOfServiceComments &&
                            Object.keys(this.BenefitSearchResponse.lstOfServiceComments)
                                .length > 0 &&
                            this.BenefitSearchResponse.lstOfServiceComments[k.ServiceTypeID] !=
                            undefined
                            ? this.BenefitSearchResponse.lstOfServiceComments[k.ServiceTypeID]
                                .length > 0
                                ? this.BenefitSearchResponse.lstOfServiceComments[
                                k.ServiceTypeID
                                ]
                                : ""
                            : "";
                    benefitData.ServiceTypeID = k.ServiceTypeID;
                    benefitData.ServiceCatCode = k.ServiceCatCode;
                    benefitData.PARNonPar = benefitData.Par + "/" + benefitData.NonPar;
                    benefitData.AdaCode = k.AdaCode;
                    benefitData.IsExpanded = false;
                    benefitData.benefitLimits =
                        this.BenefitSearchResponse &&
                            this.BenefitSearchResponse?.lstOfLimits &&
                            this.BenefitSearchResponse?.lstOfLimits?.Service &&
                            Array.isArray(this.BenefitSearchResponse.lstOfLimits.Service) &&
                            this.BenefitSearchResponse.lstOfLimits.Service.length > 0
                            ? this.BenefitSearchResponse.lstOfLimits.Service.filter(
                                (h) =>
                                    h.placeOfServiceCode === k.placeOfServiceCode &&
                                    h.TierNumber === k.TierNumber &&
                                    h.ServiceTypeID === k.ServiceTypeID &&
                                    (h.limitAmount == k.Par || h.limitAmount == k.NonPar) &&
                                    (h.BenefitDescription == benefitData.BenefitDescription ||
                                        h.BenefitDescription == k.BenefitDescription)
                            )
                            : [];
                    this.totalBenefits.push(benefitData);
                    this.totalCount = this.totalBenefits.length;
                    if (this.totalCount > 0) {
                        this.datafound = true;
                    }
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
                if (
                    this.parCodeList &&
                    this.parCodeList.ParCode &&
                    Array.isArray(this.parCodeList.ParCode) &&
                    this.parCodeList.ParCode.length > 0
                ) {
                    benDescriptionName = this.parCodeList.ParCode.find(
                        (k) => k.Code === benDescription
                    ).Name;
                    parCodeFound = benDescriptionName ? true : false;
                }
                if (!parCodeFound) {
                    benDescriptionName =
                        benDescription === "Not Covered" ? "Not Covered" : "";
                }
                return benDescriptionName;
            }
        }
    }

    handleScroll(event) {
        if (
            event.target.scrollHeight - Math.round(event.target.scrollTop) ===
            event.target.clientHeight ||
            event.target.scrollHeight - Math.round(event.target.scrollTop) ===
            event.target.clientHeight - 1 ||
            event.target.scrollHeight - Math.round(event.target.scrollTop) ===
            event.target.clientHeight + 1
        ) {
            if (this.filteredcount != undefined && this.totalCount != undefined) {
                this.datafound = true;
                if (this.filteredcount + INITIAL_LOAD_RECODRS >= this.totalCount) {
                    this.filteredcount = this.totalCount;
                    this.filteredBenefits =
                        this.filteredTotalBenefits?.length > 0
                            ? this.filteredTotalBenefits
                            : this.totalBenefits;
                } else {
                    this.filteredcount = this.filteredcount + INITIAL_LOAD_RECODRS;
                    this.filteredBenefits =
                        this.filteredTotalBenefits?.length > 0
                            ? this.filteredTotalBenefits.slice(0, this.filteredcount)
                            : this.totalBenefits.slice(0, this.filteredcount);
                }
            }
        }
    }

    get mandatoryFieldsSelected() {
        if (
            (this.isLVPlatform &&
                Boolean(this.selectedCauseCode) &&
                Boolean(this.selectedCategoryCode) &&
                Boolean(this.selectedTypeOfService)) ||
            (!this.isLVPlatform &&
                Boolean(this.selectedCategoryCode) &&
                Boolean(this.selectedTypeOfService))
        ) {
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
        this.selectedAdaCode = null;
        this.updateFilterConfig(
            "Category Code",
            null,
            this.serviceCategoryOptions ? this.serviceCategoryOptions : ""
        );
        this.updateFilterConfig(
            "PAR/Non-PAR/Both",
            null,
            this.parCodeOptions ? this.parCodeOptions : ""
        );
        this.updateFilterConfig(
            "Place of Service",
            null,
            this.placeOfServiceOptions ? this.placeOfServiceOptions : ""
        );
        this.updateFilterConfig(
            "Type of Service",
            null,
            this.typeOfServiceOptions ? this.typeOfServiceOptions : ""
        );
        this.updateFilterConfig('ADA Code', this.selectedAdaCode, this.adaCodeOptions ?? null);
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
        this.filterconfig.find(
            (k) => k?.label === "Search" && k?.button === true
        ).disabled = true;
        this.filterconfig.find
            ((k) => k?.filter === true).disabled = true;
        this.filterconfig.find(k => k?.label === 'ADA Code').disabled = true;
    }


    handleRemoveFilter(event) {
        this.SelectedFilters.splice(event.target.dataset.index, 1);
        if (event.target.dataset.key == "Place Of Service") {
            this.selectedPlaceOfService = "";
            this.getDentalSearchData();
        } else if (event.target.dataset.key == "PAR/Non-PAR/Both") {
            this.selectedPARCode = "";
            this.getDentalSearchData();
        } else if (event.target.dataset.key == "Service Category") {
            this.displayFilters = true;
            this.selectedCategoryCode = null;
            this.selectedTypeOfService = null;
            this.selectedPlaceOfService = null;
            this.selectedPARCode = null;
            this.typeOfServiceOptions = [];
            this.placeOfServiceOptions = [];
            this.parCodeOptions = [];
            this.disabledropdowns = false;
            this.cleardata();
        } else if (event.target.dataset.key == "Type of Service") {
            this.displayFilters = true;
            this.selectedTypeOfService = null;
            this.selectedPlaceOfService = null;
            this.selectedPARCode = null;
            this.placeOfServiceOptions = [];
            this.parCodeOptions = [];
            this.disabledropdowns = false;
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
        this.filteredBenefits = [];
        this.filteredTotalBenefits = [];
        this.datafound = false;
    }

    clearSearchFilter() {
        this.filteredcount = 0;
        this.filteredTotalBenefits = [];
        this.filteredBenefits = [];
        if (
            this.filteredcount != undefined &&
            this.totalBenefits?.length != undefined
        ) {
            this.datafound = true;
            if (
                this.filteredcount + INITIAL_LOAD_RECODRS >=
                this.totalBenefits?.length
            ) {
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
    }

    handleFilterBtnClick(event) {
        if (event && event?.detail) {
            switch (event.detail.name) {
                case "Search":
                    this.getDentalSearchData();
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

    clearSearchData() {
        this.clearSearchFilter();
    }

    handleKeyWordValue({ detail }) {
        if (detail && detail?.value && detail?.value?.length >= 3) {
            this.filteredTotalBenefits = [];
            this.filteredcount = 0;
            this.filteredBenefits = [];
            if (
                this.totalBenefits &&
                Array.isArray(this.totalBenefits) &&
                this.totalBenefits.length > 0
            ) {
                this.totalBenefits.forEach((k) => {
                    if (k) {
                        Object.values(k).forEach((t) => {
                            if (
                                t &&
                                String(t)
                                    ?.toLowerCase()
                                    .includes(detail?.value?.toLowerCase()) &&
                                !this.filteredTotalBenefits.includes(k)
                            ) {
                                this.filteredTotalBenefits.push(k);
                            }
                        });
                    }
                });
            }
            if (
                this.filteredcount != undefined &&
                this.filteredTotalBenefits?.length != undefined
            ) {
                if (
                    this.filteredcount + INITIAL_LOAD_RECODRS >=
                    this.filteredTotalBenefits?.length
                ) {
                    this.filteredcount = this.filteredTotalBenefits?.length;
                    this.filteredBenefits = this.filteredTotalBenefits;
                } else {
                    this.filteredcount = this.filteredcount + INITIAL_LOAD_RECODRS;
                    this.filteredBenefits = this.filteredTotalBenefits.slice(
                        0,
                        this.filteredcount
                    );
                }
            } else {
                this.datafound = false;
            }
            this.totalCount = this.filteredTotalBenefits?.length;
            this.datafound = this.totalCount == 0 ? false : true;
            this.setScrollbarPosition();
        }
    }

    addPillFilter() {
        this.SelectedFilters = [];
        let pillValue;
        if (this.selectedCategoryCode) {
            pillValue = {
                label: "Category Code",
                value:
                    "Category Code: " +
                    this.serviceCategoryOptions.find(
                        (h) => h.value === this.selectedCategoryCode
                    )?.label ?? '',
            };
            this.SelectedFilters.push(pillValue);
        }
        if (this.selectedTypeOfService) {
            pillValue = {
                label: "Type of Service",
                value:
                    "Type of Service: " +
                    this.typeOfServiceOptions.find(
                        (h) => h.value === this.selectedTypeOfService
                    )?.label ?? '',
            };
            this.SelectedFilters.push(pillValue);
        }
        if (this.selectedPlaceOfService) {
            pillValue = {
                label: "Place Of Service",
                value:
                    "Place Of Service: " +
                    this.placeOfServiceOptions.find(
                        (h) => h.value === this.selectedPlaceOfService
                    )?.label ?? '',
            };
            this.SelectedFilters.push(pillValue);
        }
        if (this.selectedPARCode) {
            pillValue = {
                label: "PAR/Non-PAR/Both",
                value:
                    "PAR/Non-PAR/Both: " +
                    this.parCodeOptions.find((h) => h.value === this.selectedPARCode)
                        ?.label ?? '',
            };
            this.SelectedFilters.push(pillValue);
        }
        if (this.selectedAdaCode) {
            pillValue = {
                label: "ADA Code",
                value:
                    "ADA Code: " +
                    this.adaCodeOptions.find((h) => h.value === this.selectedAdaCode)
                        ?.label ?? '',
            };
            this.SelectedFilters.push(pillValue);
        }
    }


    get getStyle() {
        let maxheight = window.innerHeight - 160;
        return (
            " border: 1px solid #dddbda ;width:320px;height: " + maxheight + "px;"
        );
    }


    get getResultStyle() {
        if (this.datafound) {
            return "height:290px;background-color:white;width:100%;padding-top: 0%;";
        } else {
            return "height:100px;background-color:white;width:100%;padding-top: 0%;";
        }
    }


    getDentalSearchData() {
        this.loaded = false;
        this.disabledropdowns = true;
        this.initialLoad = false;
        this.isLoading = true;
        this.isError = false;
        this.lstServiceCategoryCode = [];
        this.isCAS =
            this.platformCode === "LV" ||
                this.platformCode === "CI" ||
                this.platformCode === "CAS"
                ? true
                : false;
        this.showPillFilterValues = true;
        let serviceCodes = this.selectedCategoryCode.split(',');
        if (this.isCAS) {
            serviceCodes.forEach(k => {
                this.lstServiceCategoryCode.push(k);
            })
        } else {
            if (this.selectedTypeOfService != 'General') {
                let categoryCode = this.pberesponse?.ServiceTypeList?.ServiceType?.find(k => k?.ServiceTypeId === this.selectedTypeOfService)?.ServiceCategoryCode ?? '';
                this.lstServiceCategoryCode.push(categoryCode);
            } else {
                serviceCodes.forEach(k => {
                    this.lstServiceCategoryCode.push(k);
                })
            }
        }
        if (this.selectedTypeOfService === 'General') {
            this.selectedPlaceOfService = '';
            this.selectedTypeOfService = '';
        }
        this.addPillFilter();
        invokePBEService("benefitsearch", {
            lstServiceCategoryCode: this.lstServiceCategoryCode,
            selectedTypeOfService: this.selectedTypeOfService,
            selectedPlaceOfService: this.selectedPlaceOfService,
            selectedPARCode: this.selectedPARCode,
            platformCode: this.platformCode,
            productId: this.productId,
            refdate: "",
            isCAS: this.isCAS,
            adaCode: this.selectedAdaCode,
        }).then((result) => {
            this.isLoading = false;
            this.isError = false;
            this.BenefitSearchResponse = JSON.parse(result);
            this.processResponse();
            this.loaded = true;
        }).catch((error) => {
            this.isLoading = false;
            this.BenefitSearchResponse = error;
            this.message = this.labels.benefitsearcherror;
            this.isError = true;
            this.datafound = false;
            this.loaded = true;
        });
        this.closeFilterSection();
    }
}