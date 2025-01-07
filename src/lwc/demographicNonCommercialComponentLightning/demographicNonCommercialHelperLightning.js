﻿import { pubsub } from 'c/pubsubComponent';
import { objectMapperSimple, objectMapperSimpleReturn, stringConcatenator, showToastEvent } from 'c/crmserviceHelper';
import getEligiblePolicyMemberId from '@salesforce/apex/DemographicUpdateController_LC_HUM.getEligiblePolicyMemberId';
import getEligiblePolicyMemberIds from '@salesforce/apex/DemographicUpdateController_LC_HUM.getEligiblePolicyMemberIds';
import getEligibleDetails from '@salesforce/apex/DemographicUpdateController_LC_HUM.getEligibleDetails';
import getEligibleDetailsList from '@salesforce/apex/DemographicUpdateController_LC_HUM.getEligibleDetailsList';
import US1900555SwicthLabel from '@salesforce/label/c.US1900555SwicthLabel';
import US1900555ScopeLabel from '@salesforce/label/c.US1900555ScopeLabel';
import US1598491SwicthLabel from '@salesforce/label/c.US1598491SwicthLabel';
import hasNonPDPPermission from '@salesforce/customPermission/CRMS_1220_Pharmacy_Demographic_Update_Permission';
import US1441116SwitchLabel from '@salesforce/label/c.US1441116_Switch_Label';
import { uConstants } from 'c/updatePlanDemographicConstants';

export function deriveDisplayValue() {
    this.errorTrace.stack = 'Method: deriveDisplayValue, file: demographicNonCommercialHelper.js';
    for (const field of this.displayFieldModel) {
        if (field.order === arguments[2]) {
            objectMapperSimple(this[arguments[0]], arguments[1], field, arguments[3]);
            break;
        }
    }
}

export function deriveEditDisplayValue() {
    this.errorTrace.stack = 'Method: deriveEditDisplayValue, file: demographicNonCommercialHelper.js';
    for (const field of this.drivenFieldModel) {
        if (field.order === arguments[2]) {
            objectMapperSimple(this[arguments[0]], arguments[1], field, arguments[3]);
            break;
        }
    }
}

export function deriveTypeBasedDisplayValue() {
    this.errorTrace.stack = 'Method: deriveTypeBasedDisplayValue, file: demographicNonCommercialHelper.js';
    let left, right;
    left = arguments[arguments.length - 2];
    right = arguments[arguments.length - 1];
    for (const field of this.drivenFieldModel) {
        if (field.order === arguments[2]) {
            objectMapperSimple(this[arguments[0]], arguments[1], field, arguments[3], left, right);
            break;
        }
    }
}

export function deriveResidentialAddress() {
    this.errorTrace.stack = 'Method: deriveResidentialAddress, file: demographicNonCommercialHelper.js';
    let resAddrArray = [];
    let returnValue;
    let addrObj = {};
    let addrObjTemp = {};
    let left, right;
    let activeAddrArray = [];
    let inactiveAddrArray = [];

    left = arguments[arguments.length - 3];
    right = arguments[arguments.length - 2];
    for (let i = 0; i < arguments.length; i++) {
        if (i < arguments.length - 2) {
            addrObj[arguments[i]] = '';
            addrObjTemp[arguments[i]] = '';
        }
    }
    addrObj.source = this[arguments[arguments.length - 1]];
    addrObjTemp.source = this[arguments[arguments.length - 1]];

    for (const key in addrObjTemp) {
        if (addrObjTemp.hasOwnProperty(key)) {
            objectMapperSimpleReturn(this.responseMBE, key, addrObjTemp, key, left, right, resAddrArray);
            break;
        }
    }

    if (resAddrArray.length > 0) {
        let startDate, endDate, todayDate, diffTime, diffDays, isFuture = false;
        for (const resAddr of resAddrArray) {
            if (resAddr.hasOwnProperty('StartDate') && resAddr.hasOwnProperty('EndDate')) {
                startDate = new Date(resAddr.StartDate);
                endDate = new Date(resAddr.EndDate);
                todayDate = new Date();
                if (resAddr.PersonIdentifier.PersonIdentifierKey.Source === addrObj.source) {
                    if (todayDate.getTime() <= endDate.getTime() && todayDate.getTime() >= startDate.getTime()) {
                        activeAddrArray.push(resAddr);
                    }
                    else {
                        isFuture = (todayDate - endDate) < 0 ? true : false;
                        diffTime = Math.abs(todayDate - endDate);
                        diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                        diffDays = (isFuture) ? (-1 * diffDays) : diffDays;
                        inactiveAddrArray.push({ address: resAddr, days: diffDays });
                    }
                }
            }
        }

        if (activeAddrArray.length > 0) {
            for (const activeAddr of activeAddrArray) {
                for (let key in activeAddr) {
                    if (activeAddr.hasOwnProperty(key)) {
                        objectMapperSimple(activeAddr, key, addrObj, key);
                    }
                }
                break;
            }
        }
        else if (inactiveAddrArray.length > 0) {
            let inactiveAddrMap = [];
            inactiveAddrMap = inactiveAddrArray.filter(addr => addr.days >= 0).map(function (addr) { return addr.days }).sort();
            if (inactiveAddrMap.length > 0) {
                for (const inactiveAddr of inactiveAddrArray) {
                    if (+inactiveAddr.days === +inactiveAddrMap[0]) {
                        for (let key in inactiveAddr.address) {
                            if (inactiveAddr.address.hasOwnProperty(key)) {
                                objectMapperSimple(inactiveAddr.address, key, addrObj, key);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }


    returnValue = stringConcatenator(
        [
            (addrObj[arguments[0]] !== "" && addrObj[arguments[0]] !== null && addrObj[arguments[0]] !== undefined) ? addrObj[arguments[0]] : "N/A",
            (addrObj[arguments[0]] !== "" && addrObj[arguments[0]] !== null && addrObj[arguments[0]] !== undefined) ? "\n" : "N/A",
            (addrObj[arguments[1]] !== "" && addrObj[arguments[1]] !== null && addrObj[arguments[1]] !== undefined) ? addrObj[arguments[1]] : "N/A",
            (addrObj[arguments[1]] !== "" && addrObj[arguments[1]] !== null && addrObj[arguments[1]] !== undefined) ? "\n" : "N/A",
            (addrObj[arguments[2]] !== "" && addrObj[arguments[2]] !== null && addrObj[arguments[2]] !== undefined) ? addrObj[arguments[2]] : "N/A",
            (addrObj[arguments[2]] !== "" && addrObj[arguments[2]] !== null && addrObj[arguments[2]] !== undefined) ? ", " : "N/A",
            (addrObj[arguments[3]] !== "" && addrObj[arguments[3]] !== null && addrObj[arguments[3]] !== undefined) ? addrObj[arguments[3]] : "N/A",
            (addrObj[arguments[3]] !== "" && addrObj[arguments[3]] !== null && addrObj[arguments[3]] !== undefined) ? " " : "N/A",
            (addrObj[arguments[4]] !== "" && addrObj[arguments[4]] !== null && addrObj[arguments[4]] !== undefined) ? addrObj[arguments[4]] : "N/A",
            (addrObj[arguments[4]] !== "" && addrObj[arguments[4]] !== null && addrObj[arguments[4]] !== undefined) ? "\n" : "N/A",
            (addrObj[arguments[5]] !== "" && addrObj[arguments[5]] !== null && addrObj[arguments[5]] !== undefined) ? addrObj[arguments[5]] : "N/A"
        ]
    );

    return returnValue;
}

export function deriveAddress() {
    this.errorTrace.stack = 'Method: deriveAddress, file: demographicNonCommercialHelper.js';
    let returnValue;
    let addrObj = {};
    let left, right;
    left = arguments[arguments.length - 3];
    right = arguments[arguments.length - 2];
    for (let i = 0; i < arguments.length; i++) {
        if (i < arguments.length - 2) {
            addrObj[arguments[i]] = '';
        }
    }
    addrObj.source = this[arguments[arguments.length - 1]];

    for (const key in addrObj) {
        if (addrObj.hasOwnProperty(key)) {
            objectMapperSimple(this.responseMBE, key, addrObj, key, left, right);
        }
    }

    returnValue = stringConcatenator(
        [
            (addrObj[arguments[0]] !== "" && addrObj[arguments[0]] !== null && addrObj[arguments[0]] !== undefined) ? addrObj[arguments[0]] : "N/A",
            (addrObj[arguments[0]] !== "" && addrObj[arguments[0]] !== null && addrObj[arguments[0]] !== undefined) ? "\n" : "N/A",
            (addrObj[arguments[1]] !== "" && addrObj[arguments[1]] !== null && addrObj[arguments[1]] !== undefined) ? addrObj[arguments[1]] : "N/A",
            (addrObj[arguments[1]] !== "" && addrObj[arguments[1]] !== null && addrObj[arguments[1]] !== undefined) ? "\n" : "N/A",
            (addrObj[arguments[2]] !== "" && addrObj[arguments[2]] !== null && addrObj[arguments[2]] !== undefined) ? addrObj[arguments[2]] : "N/A",
            (addrObj[arguments[2]] !== "" && addrObj[arguments[2]] !== null && addrObj[arguments[2]] !== undefined) ? ", " : "N/A",
            (addrObj[arguments[3]] !== "" && addrObj[arguments[3]] !== null && addrObj[arguments[3]] !== undefined) ? addrObj[arguments[3]] : "N/A",
            (addrObj[arguments[3]] !== "" && addrObj[arguments[3]] !== null && addrObj[arguments[3]] !== undefined) ? " " : "N/A",
            (addrObj[arguments[4]] !== "" && addrObj[arguments[4]] !== null && addrObj[arguments[4]] !== undefined) ? addrObj[arguments[4]] : "N/A",
            (addrObj[arguments[4]] !== "" && addrObj[arguments[4]] !== null && addrObj[arguments[4]] !== undefined) ? "\n" : "N/A",
            (addrObj[arguments[5]] !== "" && addrObj[arguments[5]] !== null && addrObj[arguments[5]] !== undefined) ? addrObj[arguments[5]] : "N/A"
        ]
    );

    return returnValue;
}

export function deriveEmail() {
    this.errorTrace.stack = 'Method: deriveEmail, file: demographicNonCommercialHelper.js';
    let emailObj = {};
    for (let i = 0; i < arguments.length; i++) {
        if (i < arguments.length - 1) {
            emailObj[arguments[i]] = '';
        }
    }

    for (const key in emailObj) {
        if (emailObj.hasOwnProperty(key)) {
            objectMapperSimple(this.responseMBE, key, emailObj, key, arguments[arguments.length - 2], arguments[arguments.length - 1]);
        }
    }

    return emailObj[arguments[0]];
}

export function derivePhoneNumber() {
    this.errorTrace.stack = 'Method: derivePhoneNumber, file: demographicNonCommercialHelper.js';
    let phoneObj = {};
    for (let i = 0; i < arguments.length; i++) {
        if (i < arguments.length - 1) {
            phoneObj[arguments[i]] = '';
        }
    }

    for (const key in phoneObj) {
        if (phoneObj.hasOwnProperty(key)) {
            objectMapperSimple(this.responseMBE, key, phoneObj, key, arguments[arguments.length - 2], arguments[arguments.length - 1]);
        }
    }

    return phoneObj[arguments[0]];
}

export function deriveOptionVisibility() {
    this.errorTrace.stack = 'Method: deriveOptionVisibility, file: demographicNonCommercialHelper.js';
    let returnValue;
    returnValue = this[arguments[0]];
    return returnValue;
}

export function deriveOptionDisability() {
    this.errorTrace.stack = 'Method: deriveOptionDisability, file: demographicNonCommercialHelper.js';
    let returnValue;
    returnValue = this[arguments[0]];
    return returnValue;
}

export function deriveComboBoxValue() {
    this.errorTrace.stack = 'Method: deriveComboBoxValue, file: demographicNonCommercialHelper.js';
    let returnValue = arguments[0];
    if (returnValue === '') {
        returnValue = '-None-';
    }

    return returnValue;
}

export function deriveAVRequestObj() {
    this.errorTrace.stack = 'Method: deriveAVRequestObj, file: demographicNonCommercialHelper.js';
    let avfRequestObj = arguments[0];
    let fieldModel = this[arguments[1]];
    let elementOrderArray = arguments[2];
    for (const field of fieldModel) {
        if (elementOrderArray.indexOf(field.order) > -1) {
            avfRequestObj[field.fieldName] = field.value;
        }
    }
    return avfRequestObj;
}

export function updateAVRequestObj() {
    this.errorTrace.stack = 'Method: updateAVRequestObj, file: demographicNonCommercialHelper.js';
    let buttonModel = arguments[0];
    let fieldModel = this[arguments[1]][arguments[2]];
    let elementOrderArray = arguments[3];
    for (const button of buttonModel) {
        for (const field of fieldModel) {
            if (elementOrderArray.indexOf(field.order) > -1) {
                button[arguments[4]][field.fieldName] = field.value;
            }
        }
    }
    return buttonModel;
}

export function checkValidity() {
    this.errorTrace.stack = 'Method: checkValidity, file: demographicNonCommercialHelper.js';
    let returnValue = false;
    let avfRequestObj = arguments[0];
    let keys = arguments[1];

    for (let key in avfRequestObj) {
        if (keys.indexOf(key) > -1 && avfRequestObj[key] === '') {
            return true;
        }
    }
    return returnValue;
}

export function checkValidityAfterUpdate() {
    this.errorTrace.stack = 'Method: checkValidityAfterUpdate, file: demographicNonCommercialHelper.js';
    let buttonModel = arguments[0];
    let keys = arguments[1];
    for (const button of buttonModel) {
        for (let key in button[arguments[2]]) {
            if (keys.indexOf(key) > -1 && button[arguments[2]][key] === '') {
                button.setDisable = true;
                break;
            }
            else {
                if (key === 'StateCode' && keys.indexOf(key) > -1 && button[arguments[2]][key] === '-None-') {
                    button.setDisable = true;
                    break;
                }
                else {
                    button.setDisable = false;
                }
            }
        }
    }
    return buttonModel;
}

export function injectToField() {
    this.errorTrace.stack = 'Method: injectToField, file: demographicNonCommercialHelper.js';
    let targetFieldOrder = arguments[0];
    let targetBtnOrder = arguments[3];
    for (const field of this[arguments[1]]) {
        if (field.order === +targetFieldOrder) {
            field.buttonModel = this[arguments[2]].filter(button => button.order === +targetBtnOrder);
        }
    }
}

export function updateSSNValue() {
    let fieldModel = this[arguments[0]][arguments[1]];
    let fieldOrder = arguments[2];
    let propertyName = arguments[3];

    for (const field of fieldModel) {
        if (field.order === fieldOrder) {
            field[propertyName] = field.value;
            break;
        }
    }
}

export function updateButtonWrapper() {
    this.errorTrace.stack = 'Method: updateButtonWrapper, file: demographicNonCommercialHelper.js';
    let fieldModel = this[arguments[1]][arguments[2]];
    let targetOrder = arguments[6];
    let elementOrderArray = arguments[3];
    let keys = arguments[5];
    let keyName = arguments[0];
    let cKeyName = arguments[4];

    let targetFieldModel = fieldModel.filter(field => field.order === targetOrder);

    let buttonModel = targetFieldModel[0][keyName];

    let newButtonModel = updateAVRequestObj.apply(this, [buttonModel, arguments[1], arguments[2], elementOrderArray, cKeyName]);
    newButtonModel = checkValidityAfterUpdate.apply(this, [newButtonModel, keys, cKeyName]);
    for (const field of this[arguments[1]][arguments[2]]) {
        if (field.order === targetOrder) {
            field.buttonModel = newButtonModel;
        }
    }
}

export function dateManipulator() {
    let currentDateValue = arguments[0];
    if (currentDateValue.indexOf('/') > - 1) {
        let date = new Date(currentDateValue);
        currentDateValue = `${date.getMonth() + 1}-${date.getDay()}-${date.getFullYear}`;
    }
    return currentDateValue;
}

export function updateSummaryGroup() {
    let renderDirtyModel = this[arguments[0]];
    let targetOrderArray = arguments[1];
    let currFieldSummaryValue = arguments[2];
    let currentOrder = arguments[3];
    let isDirty = [];

    for (const order of targetOrderArray) {
        let ctargetFieldArray = renderDirtyModel.dirty.filter(field => field.order === order);
        let oTargetFieldArray = renderDirtyModel.original.filter(field => field.order === order);
        let oldFieldValue = (oTargetFieldArray[0].value === null || oTargetFieldArray[0].value === '' || oTargetFieldArray[0].value === undefined) ?
            '' : oTargetFieldArray[0].value;
        let currFieldValue = (ctargetFieldArray[0].value === null || ctargetFieldArray[0].value === '' || ctargetFieldArray[0].value === undefined) ?
            '' : ctargetFieldArray[0].value;

        if (oldFieldValue !== currFieldValue) {
            isDirty.push(true);
        }
        else {
            isDirty.push(false);
        }
    }

    if (isDirty.indexOf(true) > -1) {
        for (const field of renderDirtyModel.dirty) {
            if (field.order === currentOrder) {
                field.isSummary = true;
                break;
            }
        }
    }
    else {
        for (const field of renderDirtyModel.dirty) {
            if (targetOrderArray.indexOf(field.order) > -1) {
                field.isSummary = currFieldSummaryValue;
            }
        }
    }
}

export function deriveEligibility() {
    let editableModel = this[arguments[0]];
    let keyName = arguments[1];
    let currFieldOrder = arguments[2];
    let fieldPropertyName = arguments[3];
    for (const field of this.drivenFieldModel) {
        if (field.order === +currFieldOrder) {
            let editableMode = editableModel[keyName];
            switch (editableMode.toLowerCase().trim()) {
                case "read":
                    field[fieldPropertyName] = true;
                    break;
                case "edit":
                    field[fieldPropertyName] = false;
                    break;
                default:
                    field[fieldPropertyName] = true;
                    break;
            }
            break;
        }
    }
}

export function fetchEligibleModel() {
    this.errorTrace.stack = 'Method: fetchEligibleModel, file: demographicNonCommercialHelper.js';
    let ehObj = this.eligibleHeirarchy;
    let argObj = {};
    let isProceed = false;
    for (let key in ehObj) {
        if (ehObj.hasOwnProperty(key)) {
            switch (key) {
                case "shortlistedPolicyMemberId":
                    argObj[key] = ehObj[key];
                    break;
                case "eligibilityDataModel":
                    isProceed = (ehObj[key] !== undefined && ehObj[key] !== null && ehObj[key] !== '') ?
                        ((ehObj[key].length > 0) ? true : false) :
                        false;
                    argObj[key] = ehObj[key];
                    break;
                case "eligiblePlatformList":
                    argObj[key] = ehObj[key];
                    break;
                case "eligiblePlatProdModel":
                    argObj[key] = ehObj[key];
                    break;
                default:
                    break;
            }
        }
    }

    if (isProceed) {
        buildEligibilityModel.apply(this, [argObj, true]);
    }
    else {
        //Eligibility Error', 'There are no Valid Policies to proceed further!
        showToastEvent.apply(this, ['Eligibility Error', 'There are no Valid Policies to proceed further!']);
        this.isLoadOverlay = false;
        return;
    }
}

export function buildEligibilityModel() {
    this.errorTrace.stack = 'Method: buildEligibilityModel, file: demographicNonCommercialHelper.js';
    let heirarchyArray = [];
    let groupRecordInput = {};
    //Mega Update Block - starts
    let eligiblePlatformArr = [];
    let eligiblePlatProdMap = [];
    //Mega update Block - ends
    let argObj = arguments[0] || null;
    let argOn = arguments[1] || false;

    //deduceHierarchyAndRoutingMessages.apply(this, [this.templateName, this.responseEligibility.ValidateEligibilityResponse.members]);

    for (const member of this.responseEligibility.ValidateEligibilityResponse.members) {
        for (const eligibility of member.eligibilities) {
            if (eligibility.template === this.templateName) {
                heirarchyArray.push(+eligibility.heirarchy);
            }
        }
    }

    if (heirarchyArray.length > 0) {
        heirarchyArray.sort(function (a, b) { return a - b; });
        let heirarchyNumber = heirarchyArray[0];
        for (const member of this.responseEligibility.ValidateEligibilityResponse.members) {
            let eligibilityModel = this.eligibilityModelClass.eligibility;
            //Mega update Block - starts
            for (const eligibility of member.eligibilities) {
                if (eligibility.template === this.templateName) {
                    if (eligiblePlatformArr.indexOf(eligibility.groupRecord.platformCode) === -1) {
                        eligiblePlatformArr.push(eligibility.groupRecord.platformCode);
                    }
                    eligiblePlatProdMap.push({
                        platform: eligibility.groupRecord.platformCode,
                        product: eligibility.groupRecord,
                        policyMemberId: undefined,
                        primary: false
                    });
                }
            }
            //Mega update Block - ends
            if (argOn) {
                for (let key in argObj.eligibilityDataModel[0]) {
                    if (argObj.eligibilityDataModel[0].hasOwnProperty(key)) {
                        objectMapperSimple(argObj.eligibilityDataModel[0], key, eligibilityModel, key);
                    }
                }

                for (const platProd of eligiblePlatProdMap) {
                    if (argObj.eligibilityDataModel[0].groupRecord.platformCode === platProd.platform) {
                        platProd.primary = true;
                        break;
                    }
                }
                this.EligibilityDataModel.push(eligibilityModel);
                this.EligiblePlatProdModel = eligiblePlatProdMap;
                this.EligiblePlatformList = eligiblePlatformArr;
            }
            else {
                for (const eligibility of member.eligibilities) {
                    if (+heirarchyNumber === +eligibility.heirarchy && eligibility.template === this.templateName) {
                        //Mega update Block - starts
                        for (const platProd of eligiblePlatProdMap) {
                            if (eligibility.groupRecord.platformCode === platProd.platform) {
                                platProd.primary = true;
                                break;
                            }
                        }
                        //Mega update Block - ends
                        for (let key in eligibility) {
                            if (eligibility.hasOwnProperty(key)) {
                                objectMapperSimple(eligibility, key, eligibilityModel, key);
                            }
                        }
                        this.EligibilityDataModel.push(eligibilityModel);
                        //Mega update Block - starts
                        this.EligiblePlatProdModel = eligiblePlatProdMap;
                        this.EligiblePlatformList = eligiblePlatformArr;
                        //Mega update Block - ends
                        break;
                    }
                }
            }
        }

        for (const eligibility of this.EligibilityDataModel) {
            groupRecordInput = JSON.stringify(eligibility.groupRecord);
            this.currentPlatform = eligibility.groupRecord.platformCode;
            this.eligibileEditModel = eligibility.editableDemographicType;
            this.ruleName = eligibility.ruleName;
            if (eligibility.groupRecord.productType === 'MCD' || eligibility.groupRecord.productType === 'MES') {
                this.isModeMedicaid = true;
            }
            else {
                this.isModeMedicaid = false;
            }
            if (this.displayMessageModel.cod.length === 0) {
                this.displayMessageModel.cod.push(eligibility.contactHierarchyMessage);
            }
            if (this.displayMessageModel.crd.length === 0) {
                this.displayMessageModel.crd.push(eligibility.criticalHierarchyMessage);
            }
            this.isMedicaid = (this.isModeMedicaid ? true : false);
            this.isMedicare = (this.isModeMedicaid ? false : true);
            /*QA Fix - Starts */
            deduceHierarchyAndRoutingMessages.apply(this, [this.templateName, this.responseEligibility.ValidateEligibilityResponse.members, eligibility.heirarchy, eligibility.routingMessage]);
            /*QA Fix - Ends */
            break;
        }

        getEligiblePolicyMemberId({ groupRecordObject: groupRecordInput, recordId: this.pageName && this.pageName === 'New Case' ? this.recordPersonId : this.recordCaseId })
            .then(result => {
                console.log('inside getEligiblePolicy method', result);
                let value = result;
                if (value === 'No Policies Available') {
                    this.noPoliciesToProceed = true;
                    showToastEvent.apply(this, ['Eligibility Error', 'There are no Valid Policies to proceed further!']);
                    this.isLoadOverlay = false;
                }
                else {
                    this.noPoliciesToProceed = false;
                    this.eligiblePolicyMemberId = value;
                    procureEligibleDetails.apply(this);
                    console.log('are we triggering multiple times to call service')
                    pubsub.publish('triggerMBEService');
                }
                pubsub.publish('toggleLoader', {
                    detail: { showLoader: false }
                });
                this.isLoadOverlay = false;
            })
            .catch(error => {
                pubsub.publish('toggleLoader', {
                    detail: { showLoader: false }
                });
                this.isLoadOverlay = false;
                this.noPoliciesToProceed = true;
                showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
            });
        //Mega update Block - starts
        getEligiblePolicyMemberIds({ groupOfRecordsObject: JSON.stringify({ details: this.EligiblePlatProdModel }), recordId: this.pageName && this.pageName === 'New Case' ? this.recordPersonId : this.recordCaseId })
            .then(result => {
                let eligibleDetails = JSON.parse(result);
                console.log('ploicy member Id', JSON.stringify(result));
                for (const detail of eligibleDetails.details) {
                    this.EligibilePolicyMemIdList.push(detail.policyMemberId);
                }
                procureEligibleDetailsList.apply(this);
            })
            .catch(error => {
                pubsub.publish('toggleLoader', {
                    detail: { showLoader: false }
                });
                this.isLoadOverlay = false;
                this.noPoliciesToProceed = true;
                showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
            });
        //Mega update Block - ends
    }
    else {
        showToastEvent.apply(this, ['Eligibility Error', `There are no eligible products related to template ${this.templateName}`]);
        this.isLoadOverlay = false;
    }
}

export function deduceHierarchyAndRoutingMessages() {
    let templateName = arguments[0];
    let eligibilityArray = arguments[1];
    let shortlistedHierarchy = arguments[2];
    let shortlistedRouteMsg = arguments[3];

    for (const member of eligibilityArray) {
        for (const eligibility of member.eligibilities) {
            if (eligibility.template !== templateName) {
                let isOk = (eligibility.routingMessage !== null && eligibility.routingMessage !== undefined && eligibility.routingMessage !== '') ? true : false;
                if (this.displayMessageModel.routing.indexOf(eligibility.routingMessage) === -1 && isOk) {
                    this.displayMessageModel.routing.push(eligibility.routingMessage);
                }
            }
        }
    }

    /**QA Fix - Starts */
    let isCrossMsgProcess = false;
    if (US1598491SwicthLabel.toUpperCase() == 'Y') {
        isCrossMsgProcess = (US1900555ScopeLabel.toUpperCase().indexOf(templateName) > -1) ? true : false;
    }
    else {
        isCrossMsgProcess = (this.displayMessageModel.routing.length === 0 && US1900555ScopeLabel.toUpperCase().indexOf(templateName) > -1) ? true : false;
    }
    if (isCrossMsgProcess) {
        for (const member of eligibilityArray) {
            for (const eligibility of member.eligibilities) {
                if (+eligibility.heirarchy !== +shortlistedHierarchy && eligibility.routingMessage !== shortlistedRouteMsg) {
                    let isOk = (eligibility.routingMessage !== null && eligibility.routingMessage !== undefined && eligibility.routingMessage !== '') ? true : false;
                    if (this.displayMessageModel.routing.indexOf(eligibility.routingMessage) === -1 && isOk) {
                        this.displayMessageModel.routing.push(eligibility.routingMessage);
                    }
                }
            }
        }
    }
    /**QA Fix - Ends */

    pubsub.publish('consumeMessages', {
        detail: {
            data: this.displayMessageModel
        }
    });
}

export function procureEligibleDetails() {
    getEligibleDetails({ policyMemberId: this.eligiblePolicyMemberId, isGBO: (this.templateName === 'GBO') ? true : false })
        .then(result => {
            console.log('eligible data', JSON.stringify(result.data));
            this.eligibleDetails.data = JSON.parse(result.data);
            this.eligibleDetails.generics = JSON.parse(result.generics);
            if (this.eligibleDetails.data.hasOwnProperty('dependents')) {
                //this.displayDependent.show = true;
                //this.displayDependent.msg = 'If EE makes a Contact Demographic update, plan members under the age of 18 will receive the same update. Due to Privacy regulations, plan members over the age of 18 will need to make their own updates';
                this.displayMessageModel.cod.push(uConstants.procure_Eligible_Message);
            }
            else {
                //this.displayDependent.show = false;
                //this.displayDependent.msg = undefined;
            }
        })
        .catch(error => {
            pubsub.publish('toggleLoader', {
                detail: { showLoader: false }
            });
            showToastEvent.apply(this, ['getEligibleDetails APEX Error', error.message]);
        });
}

//Mega update Block - starts
export function procureEligibleDetailsList() {
    //Mega update Block - starts
    getEligibleDetailsList({ policyMemberIds: this.EligibilePolicyMemIdList, isGBO: (this.templateName === 'GBO') ? true : false })
        .then(result => {
            this.eligibleDetailsList.data = JSON.parse(result.data);
            this.eligibleDetailsList.generics = JSON.parse(result.generics);
        })
        .catch(error => {
            // pubsub.publish('toggleLoader', {
            //     detail: { showLoader: false }
            // });
            showToastEvent.apply(this, ['getEligibleDetails APEX Error', error.message]);
        });
    //Mega update Block - ends
}
//Mega update Block - ends

//US1441116 - This function checks whether user assigned with CRMS_1220_Pharmacy_Demographic_Update permission set or not
export function derivePermissionSetCheck() {
    let returnValue = false;
    if (hasNonPDPPermission && US1441116SwitchLabel.toUpperCase() == 'Y') {
        returnValue = true;
        this.hasPermission = true;
    }
    return returnValue;
}

export function buildFieldDataModel() {
    this.errorTrace.stack = 'Method: buildFieldDataModel, file: demographicNonCommercialHelper.js';
    buildDisplayFieldModel.apply(this);
    buildDriverFieldModel.apply(this);
    buildDrivenFieldModel.apply(this);


    pubsub.publish('renderDisplayComponent', {
        detail: {
            displayData: this.displayFieldModel
        }
    });
    //US1441116 - Sending Template Name as an additional parameter
    pubsub.publish('renderDriverComponent', {
        detail: {
            displayData: this.driverFieldModel, displayNote: false, sequence: true, templateName: this.templateName
        }
    });
    //US1441116 - End
    pubsub.publish('renderDrivenComponent', {
        detail: {
            displayData: this.drivenFieldModel, stateOptions: this.stateOptions, buttonData: null, uspsData: null
        }
    });
    if (this.isServiceFailed) {
        pubsub.publish("showError", {
            detail: { errorMessage: this.faultMBE.memberCalloutError, errorTitle: 'Error in MBE+ Service' }
        });
    }
}

function buildDisplayFieldModel() {
    this.errorTrace.stack = 'Method: buildDisplayFieldModel, file: demographicNonCommercialHelper.js';
    this.displayFieldModel = this.displayComponentClass.displayFields();
    this.displayFieldModel.forEach((field) => {
        field.expr.forEach(exp => {
            let argList = [];
            exp.fnArgs.forEach(arg => {
                if (arg.literal) {
                    argList.push(arg.propertyName);
                }
                else {
                    argList.push(field[arg.propertyName]);
                }
            });
            switch (exp.fnType.toLowerCase()) {
                case "return":
                    exp.fnOutSource.forEach((source, srcIndex) => {
                        for (const displayField of this.displayFieldModel) {
                            if (displayField.order === source) {
                                displayField[exp.fnOut[srcIndex]] = exp.fnName.apply(this, argList);
                                break;
                            }
                        }
                    });
                    break;
                case "void":
                    exp.fnName.apply(this, argList);
                    break;
                default:
                    break;
            }
        });
    });
}

function buildDriverFieldModel() {
    this.errorTrace.stack = 'Method: buildDriverFieldModel, file: demographicNonCommercialHelper.js';
    this.driverFieldModel = this.driverComponentClass.displayOptions();

    this.driverFieldModel.forEach(field => {
        field.expr.forEach(exp => {
            let argList = [];
            exp.fnArgs.forEach(arg => {
                if (arg.literal) {
                    argList.push(arg.propertyName);
                }
                else {
                    argList.push(field[arg.propertyName]);
                }
            });
            switch (exp.fnType.toLowerCase()) {
                case "return":
                    exp.fnOutSource.forEach((source, srcIndex) => {
                        for (const driverField of this.driverFieldModel) {
                            if (driverField.order === source) {
                                driverField[exp.fnOut[srcIndex]] = exp.fnName.apply(this, argList);
                                break;
                            }
                        }
                    });
                    break;
                case "void":
                    exp.fnName.apply(this, argList);
                    break;
                default:
                    break;
            }
        });
    });
}

function buildDrivenFieldModel() {
    this.errorTrace.stack = 'Method: buildDrivenFieldModel, file: demographicNonCommercialHelper.js';
    this.drivenFieldModel = this.drivenComponentClass.displayFields();

    this.drivenFieldModel.forEach(field => {
        field.expr.forEach(exp => {
            if (exp.fnWhen === 'render') {
                let argList = [];
                exp.fnArgs.forEach(arg => {
                    if (arg.literal) {
                        argList.push(arg.propertyName);
                    }
                    else {
                        argList.push(field[arg.propertyName]);
                    }
                });
                switch (exp.fnType.toLowerCase()) {
                    case "return":
                        exp.fnOutSource.forEach((source, srcIndex) => {
                            for (const drivenField of this.drivenFieldModel) {
                                if (drivenField.order === source) {
                                    drivenField[exp.fnOut[srcIndex]] = exp.fnName.apply(this, argList);
                                    break;
                                }
                            }
                        });
                        break;
                    case "void":
                        exp.fnName.apply(this, argList);
                        break;
                    default:
                        break;
                }
            }
        });
    });
}