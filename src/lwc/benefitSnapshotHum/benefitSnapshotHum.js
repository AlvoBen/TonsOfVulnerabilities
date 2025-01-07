/*

LWC Name        : benefitSnapshotHum.js

Function        : LWC to display Benefitsnapshot.

* Developer Name                  Date                         Description

*------------------------------------------------------------------------------------------------------------------------------

* Divya Bhamre                    26/05/2022                    US - 3017471 
* Divya Bhamre                    11/02/2022                    US - 3833519
* Aishwarya Pawar                 03/01/2023                    US - 4286514
* Vishal Shinde                   06/09/2023                    US - 4542629
* Kinal Mangukiya                 05/25/2023                    US-4585855
* Swapnali Sonawane               06/01/2023                    US - 4647537 Dental UI Logging Snapshot
* Vishal Shinde                   06/07/2023                    US -4542629-Dental Plan- update headers per standards
* Vishal Shinde                   06/13/2023                    US -4716977-Dental Benefits Snapshot Records mismatch - Defect 7738
* Kalyani Pachpol                 06/16/2023                    US- 4705839
* Kalyani Pachpol                 06/21/2023                    DF-7777
* Vishal Shinde                   06/29/2023                    US - 4755856-Medical Plan - Update Headers
****************************************************************************************************************************/
import { invokeWorkspaceAPI, openLWCSubtab } from "c/workSpaceUtilityComponentHum";
import { LightningElement, wire, track, api } from 'lwc';
import { getModel } from './layoutConfig';
import HUMNoRecords from "@salesforce/label/c.HUMNoRecords";
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { performTableLogging, getLoggingKey, checkloggingstatus } from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
const INITIAL_RECORDS = 5;
const BENEFIT_SUB_CATEGORY_CODE = '999';
const DENTAL_LIMITATION_LIST = ['ANNUAL MAXIMUM', 'LIFETIME ORTHODONTIC MAXIMUM', 'ORTHODONTICS - HABIT APPLIANCE'];
export default class benefitSnapshotHum extends LightningElement {

    @api
    producttype;
    @api memberplanname;
    @api recordId;
    @api pberesponse;
    @api serviceerror;
    @api message;
    @track totalCount = 0;
    @track filteredCount = 0;
    @track totalBenefitSnapshotData = [];
    @track loaded = false;
    @track columns = getModel('benefitSnapshotColumns');
    @track dentalcol = getModel('benefitSnapshotDentalColumns');
    @track copayOfficeVisitInNetworkMap = new Map();
    @track totalCopayOfficeVisitData = [];
    @track totalBenefits = [];
    @track filteredBenefits = [];
    @track CopayOfficeVisitData = [];
    @track dataFound;
    @track isDentalBool = false;
    @track fieldname;
    @track direction = 'asc';
    @track denCount = 0;
    @track medCount = 0;
    @track screenName;
    @track loggingkey;
    @track pageRef;
    labels = {
        HUMNoRecords
    };
    @track benefitCategories;
    @track benefitSubCategories;

    connectedCallback() {
        this.initialSetup();
    }

    initialSetup() {
        this.loadCommonCSS();
        getLoggingKey(this.pageRef).then(result => {
            this.loggingkey = result;
        });
        if (this.relatedInputField[0].value == undefined) this.relatedInputField[0].value = this.memberplanname
    }

    //load css
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
        value: "Snapshot"
    }, {
        label: "Description",
        mappingField: "Description"
    }];

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        getLoggingKey(this.pageRef).then(result => {
            this.loggingkey = result;
        });
    }

    getFilteredBenefits() {
        this.getSortedTotalBenefits();
        if (this.totalBenefits.length != 0) {
            this.dataFound = true;            
            if (this.totalBenefits?.length > 0 && this.totalBenefits?.length <= INITIAL_RECORDS) {
                this.filteredBenefits = this.totalBenefits;
                this.totalCount = this.totalBenefits?.length ?? 0;
                this.filteredCount = this.totalCount;
            } else {
                this.filteredBenefits = this.totalBenefits.slice(0, INITIAL_RECORDS);
                this.totalCount = this.totalBenefits?.length ?? 0;
                this.filteredCount = this.totalCount > 0 ? INITIAL_RECORDS : 0;
            }
            this.denCount = this.isDentalBool && this.totalCount <= 6 ? this.totalCount : '6+';
            this.medCount = !this.isDentalBool && this.totalCount <= 6 ? this.totalCount : '6+';
        }
    }

    getSortedTotalBenefits() {
        if (this.totalBenefits && this.totalBenefits?.length > 0) {
            this.totalBenefits.sort((a, b) => {
                const compareCategoryCode = a?.CategoryCode?.localeCompare(b?.CategoryCode);
                const compareSubCategoryCode = a?.SubCategoryCode?.localeCompare(b?.SubCategoryCode);
                const compareDescription = a?.Description?.localeCompare(b?.Description);
                return compareCategoryCode || compareSubCategoryCode || compareDescription;
            })
        }
    }

    handleScroll(event) {
        this.getSortedTotalBenefits();
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {            
            if (this.totalBenefits.length <= (this.filteredCount +  INITIAL_RECORDS)) {
                this.totalCount = this.totalBenefits.length;
                this.filteredCount = this.totalCount;
                this.filteredBenefits = this.totalBenefits;
            } else {
                this.totalCount = this.totalBenefits.length;
                this.filteredCount = this.filteredCount + INITIAL_RECORDS;
                this.filteredBenefits = this.totalBenefits.slice(0, (this.filteredCount + INITIAL_RECORDS));
            }
        }
    }

    getCategoryCode(label) {
        if (label && label?.length > 0) {
            return this.benefitCategories?.find(k => k?.label?.toLowerCase() === label?.toLowerCase())?.code ?? '';
        } else {
            return '';
        }
    }

    getSubCategoryCode(label, benDescription) {
        if (label && benDescription) {
            let codes = this.benefitSubCategories?.find(k => k?.category?.toLocaleLowerCase() === label?.toLocaleLowerCase())?.codes ?? null;
            if (codes && Array.isArray(codes) && codes?.length > 0) {
                return codes?.find(k => k?.label?.toLocaleLowerCase() === benDescription?.toLocaleLowerCase())?.code ?? BENEFIT_SUB_CATEGORY_CODE;
            } else {
                return BENEFIT_SUB_CATEGORY_CODE;
            }
        } else {
            return BENEFIT_SUB_CATEGORY_CODE;
        }
    }

    processMedicalBenefits(model) {
        if (model && Array.isArray(model)) {
            model.forEach(k => {
                switch (k.label) {
                    case "Copay":
                        if (this.pberesponse?.CopayOfficeVisit?.CopayOfficeVisitList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.CopayOfficeVisit.CopayOfficeVisitList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Deductible":
                        if (this.pberesponse?.Deductible?.DeductibleList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.Deductible.DeductibleList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "CoInsurance":
                        if (this.pberesponse?.CoInsurance?.CoInsuranceList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.CoInsurance.CoInsuranceList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Out of Pocket":
                        if (this.pberesponse?.OutOfPocket?.OutOfPocketList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.OutOfPocket.OutOfPocketList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Max out of Pocket":
                        if (this.pberesponse?.MaxOutOfPocket?.MaxOutOfPocketList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.MaxOutOfPocket.MaxOutOfPocketList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "SuperMoop":
                        if (this.pberesponse?.SuperMoop?.SuperMoopList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.SuperMoop.SuperMoopList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Limitation":
                        if (this.pberesponse?.Limitation?.LimitationList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.Limitation.LimitationList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "BenefitIndicator":
                        if (this.pberesponse?.BenefitIndicatorList?.BenefitIndicator && Array.isArray(this.pberesponse?.BenefitIndicatorList?.BenefitIndicator)) {
                            this.pberesponse.BenefitIndicatorList.BenefitIndicator.forEach(t => {
                                if (t.BenefitIndicatorType.toUpperCase() === 'DeductibleReduceOOP'.toUpperCase() ||
                                    t.BenefitIndicatorType.toUpperCase() === 'CopayReduceOOP'.toUpperCase()) {
                                    this.totalBenefits.push({
                                        key: t.BenefitIndicatorType,
                                        type: k.label,
                                        typeDescription: k.Description,
                                        Description: t.BenefitIndicatorType,
                                        Tier: '',
                                        InNetwork: t.Value,
                                        OutNetwork: t.Value,
                                        CategoryCode: this.getCategoryCode(k?.label),
                                        SubCategoryCode: BENEFIT_SUB_CATEGORY_CODE
                                    })
                                }
                            })
                        }
                        break;
                }
            })
        }
        this.getFilteredBenefits();
    }

    processDentalBenefits(model) {
        if (model && Array.isArray(model)) {
            model.forEach(k => {
                switch (k.label) {
                    case "Deductible":
                        if (this.pberesponse?.Deductible?.DeductibleList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.Deductible.DeductibleList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Out of Pocket":
                        if (this.pberesponse?.OutOfPocket?.OutOfPocketList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.OutOfPocket.OutOfPocketList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Max out of Pocket":
                        if (this.pberesponse?.MaxOutOfPocket?.MaxOutOfPocketList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.MaxOutOfPocket.MaxOutOfPocketList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Co-Insurance":
                        if (this.pberesponse?.CoInsurance?.CoInsuranceList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.CoInsurance.CoInsuranceList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "SuperMoop":
                        if (this.pberesponse?.SuperMoop?.SuperMoopList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.SuperMoop.SuperMoopList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Copay":
                        if (this.pberesponse?.CopayOfficeVisit?.CopayOfficeVisitList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.CopayOfficeVisit.CopayOfficeVisitList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "Limitation":
                        if (this.pberesponse?.Limitation?.LimitationList?.Coverage) {
                            this.processCoverageNodes(this.pberesponse.Limitation.LimitationList.Coverage, k, this.getCategoryCode(k?.label));
                        }
                        break;
                    case "BenefitIndicator":
                        if (this.pberesponse?.BenefitIndicatorList?.BenefitIndicator && Array.isArray(this.pberesponse?.BenefitIndicatorList?.BenefitIndicator)) {
                            this.pberesponse.BenefitIndicatorList.BenefitIndicator.forEach(t => {
                                if (t.BenefitIndicatorType.toUpperCase() === 'DeductibleReduceOOP'.toUpperCase() ||
                                    t.BenefitIndicatorType.toUpperCase() === 'CopayReduceOOP'.toUpperCase()) {
                                    this.totalBenefits.push({
                                        key: t.BenefitIndicatorType,
                                        type: k.label,
                                        typeDescription: k.Description,
                                        Description: t.BenefitIndicatorType,
                                        Tier: '',
                                        InNetwork: t.Value,
                                        OutNetwork: t.Value,
                                        CategoryCode: this.getCategoryCode(k?.label),
                                        SubCategoryCode: BENEFIT_SUB_CATEGORY_CODE
                                    })
                                }
                            })
                        }
                        break;
                }
            })
        }
        this.getFilteredBenefits();
    }

    sortData(fieldname) {
        let parseData = this.totalBenefits;
        let keyValue = (a) => {
            return a[fieldname];
        };

        this.direction = this.direction === 'asc' ? 'asc' : 'asc';
        let isReverse = this.direction === 'asc' ? 1 : -1;
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : '';
            y = keyValue(y) ? keyValue(y) : '';
            return isReverse * ((x > y) - (y > x));
        });
    }


    processCoverageNodes(coverageNode, modelElement, categorycode) {
        if (coverageNode && Array.isArray(coverageNode) && coverageNode.length > 0) {
            coverageNode.forEach(t => {
                this.processBenefitNodes(t.BenefitAmountList.BenefitAmount, t.TierNumber, modelElement, t.NetworkType, categorycode);
            })
        }
    }

    checkBenefitDescription(benDescription, bLimitation) {
        let bCheckBD = false;
        if (bLimitation) {
            bCheckBD = benDescription && DENTAL_LIMITATION_LIST.includes(benDescription?.toLocaleUpperCase()) ? true : false;
        }
        return bCheckBD;
    }

    processBenefitNodes(benefits, tierNumber, modelElement, networktype, categorycode) {
        if (benefits && Array.isArray(benefits) && benefits.length > 0) {
            benefits.forEach(k => {
                if (k) {
                    if (modelElement?.label !== 'Limitation') {
                        this.processBenefitNode(k, tierNumber, modelElement, networktype, categorycode);
                    }
                    else {
                        if (this.checkBenefitDescription(k?.BenefitDescription, true)) {
                            this.processBenefitNode(k, tierNumber, modelElement, networktype, categorycode);
                        }
                    }
                }
            })
        }
    }

    processBenefitNode(k, tierNumber, modelElement, networktype, categorycode) {
        let coveragetype = k.CoverageType;
        let coveragetypeDen = k.CoverageType;
        let benDescription = k.BenefitDescription;
        if (coveragetype && coveragetype.toLowerCase() === 'none' && modelElement.label != 'Copay') {
            coveragetype = benDescription;
        } else {
            coveragetype = benDescription + " " + coveragetypeDen;
        }
        if (modelElement.label === 'Copay') {
            coveragetype = benDescription;
        }
        if (tierNumber) {
            coveragetype = coveragetype + '-$' + tierNumber;
        }
        coveragetype = this.updateToLowercase(coveragetype);
        let unitType = k.UnitDetail.UnitType;
        let quantity = this.checkAndAddUnitType(unitType, k.UnitDetail.Quantity);
        let subcategorycode = this.getSubCategoryCode(modelElement?.label, benDescription);
        this.processBenefitData(quantity, modelElement, networktype, coveragetype, tierNumber, categorycode, subcategorycode);
    }

    updateToLowercase(convertText) {
        if (convertText && convertText?.length > 0) {
            convertText = convertText.toLocaleLowerCase().split(' ').map((item) => item.charAt(0).toLocaleUpperCase() + item.slice(1)).join(' ')
            return convertText;
        }
    }

    checkAndAddUnitType(vBSUnitType, vBSQuantity) {
        if (vBSUnitType.toLowerCase() == 'percentage') {
            vBSQuantity = vBSQuantity + '%';
        }
        else if (vBSUnitType.toLowerCase() == 'dollar') {
            vBSQuantity = '$' + vBSQuantity;
        }
        return vBSQuantity;
    }

    processBenefitData(quantity, modelElement, networktype, coveragetype, tierNumber, categorycode, subcategorycode) {
        if (networktype && (networktype.toLowerCase() == 'innetwork' || networktype.toLowerCase() == 'none')) {
            if (this.totalBenefits && this.totalBenefits.length > 0) {
                let benefit = this.totalBenefits.findIndex(k => k.key === coveragetype && k.type === modelElement.label &&
                    k.typeDescription === modelElement.Description);
                if (benefit >= 0) {
                    let benefitData = this.totalBenefits[benefit];
                    if (benefitData) {
                        benefitData.InNetwork = benefitData.InNetwork != null && benefitData.InNetwork != undefined
                            && benefitData.InNetwork != '' ? benefitData.InNetwork + '/ ' + quantity : quantity;
                    }
                    this.totalBenefits[benefit] = benefitData;
                } else {
                    this.totalBenefits.push({
                        key: coveragetype,
                        type: modelElement.label,
                        typeDescription: modelElement.Description,
                        Description: `${modelElement.Description} ${' '} ${coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[0] : coveragetype}`,
                        Tier: coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[1] : tierNumber,
                        InNetwork: quantity,
                        OutNetwork: '',
                        CategoryCode: categorycode,
                        SubCategoryCode: subcategorycode
                    });
                }
            } else {
                this.totalBenefits.push({
                    key: coveragetype,
                    type: modelElement.label,
                    typeDescription: modelElement.Description,
                    Description: `${modelElement.Description} ${' '} ${coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[0] : coveragetype}`,
                    Tier: coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[1] : tierNumber,
                    InNetwork: quantity,
                    OutNetwork: '',
                    CategoryCode: categorycode,
                    SubCategoryCode: subcategorycode
                });
            }

        } else if (networktype && (networktype.toLowerCase() == 'outofnetwork')) {
            if (this.totalBenefits && this.totalBenefits.length > 0) {
                let benefit = this.totalBenefits.findIndex(k => k.key === coveragetype && k.type === modelElement.label &&
                    k.typeDescription === modelElement.Description);
                if (benefit >= 0) {
                    let benefitData = this.totalBenefits[benefit];
                    if (benefitData) {
                        benefitData.OutNetwork = benefitData.OutNetwork != null && benefitData.OutNetwork != undefined
                            && benefitData.OutNetwork != '' ? benefitData.OutNetwork + '/ ' + quantity : quantity;
                    }
                    this.totalBenefits[benefit] = benefitData;
                } else {
                    this.totalBenefits.push({
                        key: coveragetype,
                        type: modelElement.label,
                        typeDescription: modelElement.Description,
                        Description: `${modelElement.Description} ${' '} ${coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[0] : coveragetype}`,
                        Tier: coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[1] : tierNumber,
                        InNetwork: '',
                        OutNetwork: quantity,
                        CategoryCode: categorycode,
                        SubCategoryCode: subcategorycode
                    });
                }
            } else {
                this.totalBenefits.push({
                    key: coveragetype,
                    type: modelElement.label,
                    typeDescription: modelElement.Description,
                    Description: `${modelElement.Description} ${' '} ${coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[0] : coveragetype}`,
                    Tier: coveragetype && coveragetype.includes('-$') ? coveragetype.split('-$')[1] : tierNumber,
                    InNetwork: '',
                    OutNetwork: quantity,
                    CategoryCode: categorycode,
                    SubCategoryCode: subcategorycode
                });
            }
        }
    }

    @api
    displayErrorMessage(serviceError, message, producttype) {
        this.loaded = true;
        this.serviceerror = serviceError;
        this.message = message;
        this.dataFound = false;
        this.producttype = producttype;
        this.isDentalBool = this.producttype.toLowerCase() === 'dental' ? true : false;
    }


    @api setPBEData(pberesponse, producttype, memberplanname, recordid) {
        this.pberesponse = pberesponse;
        this.producttype = producttype;
        this.memberplanname = memberplanname;
        this.recordId = recordid;
        if (this.producttype && (this.producttype.toLowerCase() === 'medical' || this.producttype === undefined)) {
            this.isDentalBool = false;
            this.screenName = 'Medical Benefits Snapshot';
            let medicalmodel = getModel('benefitSnapshotMedical');
            this.benefitCategories = getModel('medicalBenefitCategoryCodes');
            this.processMedicalBenefits(medicalmodel);
        }
        if (this.producttype && (this.producttype.toLowerCase() === 'dental' || this.producttype === undefined)) {
            this.isDentalBool = true;
            this.screenName = 'Dental Benefits Snapshot';
            let dentalmodel = getModel('benefitSnapshotDental');
            this.benefitCategories = getModel('dentalBenefitCategoryCodes');
            this.benefitSubCategories = getModel('benefitSubCategories');
            this.processDentalBenefits(dentalmodel);
        }
        this.loaded = true;
    }

    async navigateToPlanPage() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.recordId,
                objectApiName: 'MemberPlan',
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

    renderedCallback() {
        Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
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


    handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performTableLogging(event, this.filteredBenefits, this.relatedInputField, this.isDentalBool ? this.dentalcol : this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);

        } else {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performTableLogging(event, this.filteredBenefits, this.relatedInputField, this.isDentalBool ? this.dentalcol : this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);
                }
            });
        }
    }

}