import { pubsub } from 'c/pubsubComponent';
import { objectMapperSimple, stringConcatenator, showToastEvent } from 'c/crmserviceHelper';
import getEligiblePolicyMemberId from '@salesforce/apex/DemographicUpdateController_LTNG_C_HUM.getEligiblePolicyMemberId';
import getEligiblePolicyMemberIds from '@salesforce/apex/DemographicUpdateController_LTNG_C_HUM.getEligiblePolicyMemberIds';
import getEligibleDetails from '@salesforce/apex/DemographicUpdateController_LTNG_C_HUM.getEligibleDetails';
import getEligibleDetailsList from '@salesforce/apex/DemographicUpdateController_LTNG_C_HUM.getEligibleDetailsList';
import US1900555SwicthLabel from '@salesforce/label/c.US1900555SwicthLabel';
import US1900555ScopeLabel from '@salesforce/label/c.US1900555ScopeLabel';
import US1598491SwicthLabel from '@salesforce/label/c.US1598491SwicthLabel';

export function deriveDisplayValue() {
    this.errorTrace.stack = 'Method: deriveDisplayValue, file: demographicCommercialHelper.js';
    for (const field of this.displayFieldModel) {
        if(field.order === arguments[2]) {
            objectMapperSimple(this[arguments[0]], arguments[1], field, arguments[3]);
            break;
        }
    }
}

export function deriveEditDisplayValue() {
    this.errorTrace.stack = 'Method: deriveEditDisplayValue, file: demographicCommercialHelper.js';
    for (const field of this.drivenFieldModel) {
        if(field.order === arguments[2]) {
            objectMapperSimple(this[arguments[0]], arguments[1], field, arguments[3]);
            break;
        }
    }
}

export function deriveTypeBasedDisplayValue() {
    this.errorTrace.stack = 'Method: deriveTypeBasedDisplayValue, file: demographicCommercialHelper.js';
    let left, right;
    left = arguments[arguments.length - 2];
    right = arguments[arguments.length - 1];

    for (const field of this.drivenFieldModel) {
        if(field.order === arguments[2]) {
            if(typeof field.source !== 'undefined') {
                field.source = this.currentPlatform;
            }
            objectMapperSimple(this[arguments[0]], arguments[1], field, arguments[3], left, right);
            break;
        }
    }
}

export function deriveAddress() {
    this.errorTrace.stack = 'Method: deriveAddress, file: demographicCommercialHelper.js';
    let addrObj = {};
    let returnValue;
    let left, right;
    left = arguments[arguments.length - 3];
    right = arguments[arguments.length - 2];
    for(let i = 0; i < arguments.length; i++) {
        if(i < arguments.length - 2) {
            addrObj[arguments[i]] = '';
        }
    }
    addrObj.source = this[arguments[arguments.length - 1]];

    for (const key in addrObj) {
        if(addrObj.hasOwnProperty(key)) {
            objectMapperSimple(this.responseMBE, key, addrObj, key, left, right);
        }
    }

    returnValue = stringConcatenator(
        [
            (addrObj[arguments[0]] !== "" && addrObj[arguments[0]] !== null && addrObj[arguments[0]] !== undefined) ? addrObj[arguments[0]] : "N/A", 
            (addrObj[arguments[0]] !== "" && addrObj[arguments[0]] !== null && addrObj[arguments[0]] !== undefined) ? "\n" : "N/A", 
            (addrObj[arguments[1]] !== "" && addrObj[arguments[1]] !== null && addrObj[arguments[1]] !== undefined) ? addrObj[arguments[1]]: "N/A",
            (addrObj[arguments[1]] !== "" && addrObj[arguments[1]] !== null && addrObj[arguments[1]] !== undefined) ? ", " : "N/A",
            (addrObj[arguments[2]] !== "" && addrObj[arguments[2]] !== null && addrObj[arguments[2]] !== undefined) ? addrObj[arguments[2]] : "N/A",
            (addrObj[arguments[2]] !== "" && addrObj[arguments[2]] !== null && addrObj[arguments[2]] !== undefined) ? " " : "N/A",
            (addrObj[arguments[3]] !== "" && addrObj[arguments[3]] !== null && addrObj[arguments[3]] !== undefined) ? addrObj[arguments[3]] : "N/A",
            (addrObj[arguments[3]] !== "" && addrObj[arguments[3]] !== null && addrObj[arguments[3]] !== undefined) ? "\n" : "N/A",
            (addrObj[arguments[4]] !== "" && addrObj[arguments[4]] !== null && addrObj[arguments[4]] !== undefined) ? addrObj[arguments[4]] : "N/A",
            (addrObj[arguments[4]] !== "" && addrObj[arguments[4]] !== null && addrObj[arguments[4]] !== undefined) ? "\n" : "N/A"//,
            // (addrObj[arguments[4]] !== "") ? addrObj[arguments[4]] : "N/A"
        ]
    );

    return returnValue;
}

export function deriveEmail() {
    this.errorTrace.stack = 'Method: deriveEmail, file: demographicCommercialHelper.js';
    let emailObj = {};
    for(let i = 0; i < arguments.length; i++) {
        if(i < arguments.length - 1) {
            emailObj[arguments[i]] = '';
        }
    }

    for (const key in emailObj) {
        if(emailObj.hasOwnProperty(key)) {
            objectMapperSimple(this.responseMBE, key, emailObj, key, arguments[arguments.length - 2], arguments[arguments.length - 1]);
        }
    }

    return emailObj[arguments[0]];
}

export function derivePhoneNumber() {
    this.errorTrace.stack = 'Method: derivePhoneNumber, file: demographicCommercialHelper.js';
    let phoneObj = {};
    for(let i = 0; i < arguments.length; i++) {
        if(i < arguments.length - 1) {
            phoneObj[arguments[i]] = '';
        }
    }

    for (const key in phoneObj) {
        if(phoneObj.hasOwnProperty(key)) {
            objectMapperSimple(this.responseMBE, key, phoneObj, key, arguments[arguments.length - 2], arguments[arguments.length - 1]);
        }
    }

    return phoneObj[arguments[0]];
}

export function deriveOptionVisibility() {
    this.errorTrace.stack = 'Method: deriveOptionVisibility, file: demographicCommercialHelper.js';
    let returnValue;
    returnValue = this[arguments[0]];
    return returnValue;
}

export function deriveOptionDisability() {
    this.errorTrace.stack = 'Method: deriveOptionDisability, file: demographicCommercialHelper.js';
    let returnValue;
    returnValue = this[arguments[0]];
    return returnValue;
}

export function deriveComboBoxValue() {
    this.errorTrace.stack = 'Method: deriveComboBoxValue, file: demographicCommercialHelper.js';
    let returnValue = arguments[0];
    if(returnValue === '') {
        //return '-None-';
        returnValue = '-None-';
    }
    //return arguments[0];
    return returnValue;
}

export function deriveAVRequestObj() {
    this.errorTrace.stack = 'Method: deriveAVRequestObj, file: demographicCommercialHelper.js';
    let avfRequestObj = arguments[0];
    let fieldModel = this[arguments[1]];
    let elementOrderArray = arguments[2];
    for (const field of fieldModel) {
        if(elementOrderArray.indexOf(field.order) > -1) {
            avfRequestObj[field.fieldName] = field.value;
        }
    }
    return avfRequestObj;
}

export function updateAVRequestObj() {
    this.errorTrace.stack = 'Method: updateAVRequestObj, file: demographicCommercialHelper.js';
    let buttonModel = arguments[0];
    let fieldModel = this[arguments[1]][arguments[2]];
    let elementOrderArray = arguments[3];
    for (const button of buttonModel) {
        for (const field of fieldModel) {
            if(elementOrderArray.indexOf(field.order) > -1) {
                button[arguments[4]][field.fieldName] = field.value;
            }
        }
    }
    return buttonModel;
}

export function checkValidity() {
    this.errorTrace.stack = 'Method: checkValidity, file: demographicCommercialHelper.js';
    let returnValue = false;
    let avfRequestObj = arguments[0];
    let keys = arguments[1];

    for(let key in avfRequestObj) {
        if(keys.indexOf(key) > -1 && avfRequestObj[key] === '') {
            return true;
        }
    }
    return returnValue;
}

export function checkBtnEligibility() {
    this.errorTrace.stack = 'Method: checkBtnEligibility, file: demographicCommercialHelper.js';
    let targetFieldOrderArray = arguments[2];
    let targetBtnOrder =  arguments[3];
    if(typeof this[arguments[0]] !== "undefined") {
        for (const field of this[arguments[0]]) {
            if(targetFieldOrderArray.indexOf(field.order) > - 1) {
                if(field.disabled) {
                    for (const button of this[arguments[1]]) {
                        if(button.order === +targetBtnOrder) {
                            button.setDisable = true;
                            break;
                        }
                    }
                }
                break;
            }
        }
    }
}

export function checkValidityAfterUpdate() {
    this.errorTrace.stack = 'Method: checkValidityAfterUpdate, file: demographicCommercialHelper.js';
    let buttonModel = arguments[0];
    let keys = arguments[1];
    let fieldOrderArray = arguments[3];
    let isInvalid = false;

    for (const field of this.renderDataModel.dirty) {
        if(fieldOrderArray.indexOf(field.order) > -1) {
            let elem = this.template.querySelector(`[data-index="${field.order}"]`);
            if(typeof elem !== "undefined" && elem !== null) {
                if(typeof elem.checkValidity !== "undefined") {
                    if(!elem.checkValidity()) {
                        if(elem.nodeName.toLowerCase().includes("combobox") && elem.required && elem.value === '-None-') {
                            isInvalid = true;
                        }
                        else if(elem.nodeName.toLowerCase().includes("combobox") && elem.required && elem.value !== '-None-') {
                            isInvalid = false;
                        }
                        else {
                            isInvalid = true;
                        }
                        break;
                    }
                }
            }
        }
    }

    if(!isInvalid) {
        for (const button of buttonModel) {
            for(let key in button.requestAVObj) {
                if(button.requestAVObj.hasOwnProperty(key)) {
                    if(button.responseAVObj.hasOwnProperty(key)) {
                        if(button.responseAVObj[key] !== button.requestAVObj[key]) {
                            isInvalid = false;
                            break;
                        }
                        else {
                            isInvalid = true;
                        }
                    }
                }
            }
        }
    }

    for (const button of buttonModel) {
        
        for(let key in button[arguments[2]]) {
            if(keys.indexOf(key) > -1 && button[arguments[2]][key] === '') {
                button.setDisable = true;
                break;
            }
            else {
                if(key === 'StateCode' && keys.indexOf(key) > -1 && button[arguments[2]][key] === '-None-') {
                    button.setDisable = true;
                    break;
                }
                else {
                    button.setDisable = isInvalid;
                }
            }
        }
    }
    
    return buttonModel;
}

export function injectToField() {
    this.errorTrace.stack = 'Method: injectToField, file: demographicCommercialHelper.js';
    let targetFieldOrder = arguments[0];
    let targetBtnOrder =  arguments[3];
    for (const field of this[arguments[1]]) {
        if(field.order === +targetFieldOrder) {
            field.buttonModel = this[arguments[2]].filter(button => button.order === +targetBtnOrder);
        }
    }
}

export function updateSSNValue() {
    this.errorTrace.stack = 'Method: updateSSNValue, file: demographicCommercialHelper.js';
    let fieldModel = this[arguments[0]][arguments[1]];
    let fieldOrder = arguments[2];
    let propertyName = arguments[3];

    for(const field of fieldModel) {
        if(field.order === fieldOrder) {
            field[propertyName] = field.value;
            break;
        }
    }
}

export function updateButtonWrapper() {
    this.errorTrace.stack = 'Method: updateButtonWrapper, file: demographicCommercialHelper.js';
    let fieldModel = this[arguments[1]][arguments[2]];
    let targetOrder = arguments[6];
    let elementOrderArray = arguments[3];
    let keys = arguments[5];
    let keyName = arguments[0];
    let cKeyName = arguments[4];

    let targetFieldModel = fieldModel.filter(field => field.order === targetOrder);
    
    let buttonModel = targetFieldModel[0][keyName];

    let newButtonModel = updateAVRequestObj.apply(this, [buttonModel, arguments[1], arguments[2], elementOrderArray, cKeyName]);
    newButtonModel = checkValidityAfterUpdate.apply(this, [newButtonModel, keys, cKeyName, elementOrderArray]);
    for (const field of this[arguments[1]][arguments[2]]) {
        if(field.order === targetOrder) {
            field.buttonModel = newButtonModel;
        }
    }
}

export function updateSummaryGroup() {
    this.errorTrace.stack = 'Method: updateSummaryGroup, file: demographicCommercialHelper.js';
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

        if(oldFieldValue !== currFieldValue) {
            isDirty.push(true);
        }
        else {
            isDirty.push(false);
        }
    }

    if(isDirty.indexOf(true) > -1) {
        for (const field of renderDirtyModel.dirty) {
            if(field.order === currentOrder) {
                field.isSummary = true;
                break;
            }
        }
    }
    else {
        for (const field of renderDirtyModel.dirty) {
            if(targetOrderArray.indexOf(field.order) > -1) {
                field.isSummary = currFieldSummaryValue;
            }
        }
    }
}

export function deriveEligibility() {
    this.errorTrace.stack = 'Method: deriveEligibility, file: demographicCommercialHelper.js';
    let editableModel = this[arguments[0]];
    let keyName = arguments[1];
    let currFieldOrder = arguments[2];
    let fieldPropertyName = arguments[3];
    for (const field of this.drivenFieldModel) {
        if(field.order === +currFieldOrder) {
            let editableMode = editableModel[keyName];
            switch(editableMode.toLowerCase().trim()) {
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
    this.errorTrace.stack = 'Method: fetchEligibleModel, file: demographicCommercialHelper.js';
    let ehObj = this.eligibleHeirarchy;
    let argObj = {};
    let isProceed = false;
    for(let key in ehObj) {
        if(ehObj.hasOwnProperty(key)) {
            switch(key) {
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

    if(isProceed) {
        buildEligibilityModel.apply(this, [argObj, true]);
    }
    else {
        showToastEvent.apply(this, ['Eligibility Processing Error', 'There are no Valid Policies to proceed further!']);
        return;
    }
}

export function buildEligibilityModel() {
    this.errorTrace.stack = 'Method: buildEligibilityModel, file: demographicCommercialHelper.js';
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
            if(eligibility.template === this.templateName) {
                heirarchyArray.push(eligibility.heirarchy);
            }
        }
    }

    if(heirarchyArray.length > 0) {
        heirarchyArray.sort(function(a, b) { return a - b; });
        let heirarchyNumber = heirarchyArray[0];
        for (const member of this.responseEligibility.ValidateEligibilityResponse.members) {
            let eligibilityModel = this.eligibilityModelClass.eligibility;
            //Mega update Block - starts
            for (const eligibility of member.eligibilities) {
                if(eligibility.template === this.templateName) {
                    if(eligiblePlatformArr.indexOf(eligibility.groupRecord.platformCode) === -1) {
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
            if(argOn) {
                for(let key in argObj.eligibilityDataModel[0]) {
                    if(argObj.eligibilityDataModel[0].hasOwnProperty(key)) {
                        objectMapperSimple(argObj.eligibilityDataModel[0], key, eligibilityModel, key);
                    }
                }
                
                for (const platProd of eligiblePlatProdMap) {
                    if(argObj.eligibilityDataModel[0].groupRecord.platformCode === platProd.platform) {
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
                    if(+heirarchyNumber === +eligibility.heirarchy && eligibility.template === this.templateName) {
                        //Mega update Block - starts
                        for (const platProd of eligiblePlatProdMap) {
                            if(eligibility.groupRecord.platformCode === platProd.platform) {
                                platProd.primary = true;
                                break;
                            }
                        }
                        //Mega update Block - ends
                        for(let key in eligibility) {
                            if(eligibility.hasOwnProperty(key)) {
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
            if(this.displayMessageModel.cod.length === 0) {
                this.displayMessageModel.cod.push(eligibility.contactHierarchyMessage);
            }
            if(this.displayMessageModel.crd.length === 0) {
                this.displayMessageModel.crd.push(eligibility.criticalHierarchyMessage);
            }
            /*QA Fix - Starts */
            deduceHierarchyAndRoutingMessages.apply(this, [this.templateName, this.responseEligibility.ValidateEligibilityResponse.members, eligibility.heirarchy, eligibility.routingMessage]);
            /*QA Fix - Ends */
            break;
        }

        getEligiblePolicyMemberId({ groupRecordObject: groupRecordInput, recordId: this.recordCaseId } )
            .then(result => {
                let value = result;
                if(value === 'No Policies Available') {
                    this.noPoliciesToProceed = true;
                    showToastEvent.apply(this, ['Eligibility Error', 'There are no Valid Policies to proceed further!']);
                }
                else {
                    this.noPoliciesToProceed = false;
                    this.eligiblePolicyMemberId = value;
                    procureEligibleDetails.apply(this);
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
                showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
            });
        //Mega update Block - starts
        getEligiblePolicyMemberIds({ groupOfRecordsObject: JSON.stringify({ details: this.EligiblePlatProdModel }), recordId: this.recordCaseId } )
            .then(result => {
                let eligibleDetails = JSON.parse(result);
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
                showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
            });
        //Mega update Block - ends
    }
    else {
        showToastEvent.apply(this, ['Eligibility Error', `There are no eligible products related to template ${this.templateName}`]);
    }
}

export function deduceHierarchyAndRoutingMessages() {
    let templateName = arguments[0];
    let eligibilityArray = arguments[1];
    let shortlistedHierarchy = arguments[2];
    let shortlistedRouteMsg = arguments[3];

    for (const member of eligibilityArray) {
        for (const eligibility of member.eligibilities) {
            if(eligibility.template !== templateName) {
                let isOk = (eligibility.routingMessage !== null && eligibility.routingMessage !== undefined && eligibility.routingMessage !== '') ? true : false;
                if(this.displayMessageModel.routing.indexOf(eligibility.routingMessage) === -1 && isOk) {
                    this.displayMessageModel.routing.push(eligibility.routingMessage);
                }
            }
        }
    }

    /**QA Fix - Starts */
    let isCrossMsgProcess = false;
    if(US1598491SwicthLabel.toUpperCase() == 'Y') {
        isCrossMsgProcess = (US1900555ScopeLabel.toUpperCase().indexOf(templateName) > -1) ? true : false;
    }
    else {
        isCrossMsgProcess = (this.displayMessageModel.routing.length === 0 && US1900555ScopeLabel.toUpperCase().indexOf(templateName) > -1) ? true : false;
    }
    if(isCrossMsgProcess) {
        for (const member of eligibilityArray) {
            for (const eligibility of member.eligibilities) {
                if(+eligibility.heirarchy !== +shortlistedHierarchy && eligibility.routingMessage !== shortlistedRouteMsg) {
                    let isOk = (eligibility.routingMessage !== null && eligibility.routingMessage !== undefined && eligibility.routingMessage !== '') ? true : false;
                    if(this.displayMessageModel.routing.indexOf(eligibility.routingMessage) === -1 && isOk) {
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
    this.errorTrace.stack = 'Method: procureEligibleDetails, file: demographicCommercialHelper.js';
    getEligibleDetails({ policyMemberId: this.eligiblePolicyMemberId, isGBO: (this.templateName === 'GBO') ? true : false } )
        .then(result => {
            this.eligibleDetails.data = JSON.parse(result.data);
            this.eligibleDetails.generics = JSON.parse(result.generics);
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
    getEligibleDetailsList({ policyMemberIds: this.EligibilePolicyMemIdList, isGBO: (this.templateName === 'GBO') ? true : false } )
        .then(result => {
            this.eligibleDetailsList.data = JSON.parse(result.data);
            this.eligibleDetailsList.generics = JSON.parse(result.generics);
            if(this.eligibleDetailsList.data.hasOwnProperty('dependents')) {
                this.displayMessageModel.cod.push('If EE makes a Contact Demographic update, plan members under the age of 18 will receive the same update. Due to Privacy regulations, plan members over the age of 18 will need to make their own updates');
            }
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

export function buildFieldDataModel() {
    this.errorTrace.stack = 'Method: buildFieldDataModel, file: demographicCommercialHelper.js';
    buildDisplayFieldModel.apply(this);
    buildDriverFieldModel.apply(this);
    buildDrivenFieldModel.apply(this);
    buildDrivenButtonModel.apply(this);
    buildUSPSDataModel.apply(this);
    

    pubsub.publish('renderDisplayComponent', {
        detail: { 
            displayData: this.displayFieldModel
        }
    });
    //US1441116 - Sending Template Name as an additional parameter
    pubsub.publish('renderDriverComponent', {
        detail: { 
            displayData: this.driverFieldModel, displayNote: false, sequence: false, templateName: this.templateName
        }
    });
    pubsub.publish('renderDrivenComponent', {
        detail: { 
            displayData: this.drivenFieldModel, stateOptions: this.stateOptions, buttonData: this.drivenButtonModel, uspsData: this.uspsModel
        }
    });
    if(this.isServiceFailed) {
        pubsub.publish("showError", {
            detail: { errorMessage: this.faultMBE.memberCalloutError, errorTitle: 'Error in MBE+ Service' }
        });
    }
}

function buildDisplayFieldModel() {
    this.errorTrace.stack = 'Method: buildDisplayFieldModel, file: demographicCommercialHelper.js';
    this.displayFieldModel = this.displayComponentClass.displayFields();

    this.displayFieldModel.forEach((field) => {
        field.expr.forEach(exp => {
            let argList = [];
            exp.fnArgs.forEach(arg => {
                if(arg.literal) {
                    argList.push(arg.propertyName);
                }
                else {
                    argList.push(field[arg.propertyName]);
                }
            });
            switch(exp.fnType.toLowerCase()) {
                case "return":
                    exp.fnOutSource.forEach((source, srcIndex) => {
                        for (const displayField of this.displayFieldModel) {
                            if(displayField.order === source) {
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
    this.errorTrace.stack = 'Method: buildDriverFieldModel, file: demographicCommercialHelper.js';
    this.driverFieldModel = this.driverComponentClass.displayOptions();

    this.driverFieldModel.forEach(field => {
        field.expr.forEach(exp => {
            let argList = [];
            exp.fnArgs.forEach(arg => {
                if(arg.literal) {
                    argList.push(arg.propertyName);
                }
                else {
                    argList.push(field[arg.propertyName]);
                }
            });
            switch(exp.fnType.toLowerCase()) {
                case "return":
                    exp.fnOutSource.forEach((source, srcIndex) => {
                        for (const driverField of this.driverFieldModel) {
                            if(driverField.order === source) {
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
    this.errorTrace.stack = 'Method: buildDrivenFieldModel, file: demographicCommercialHelper.js';
    this.drivenFieldModel = this.drivenComponentClass.displayFields();

    this.drivenFieldModel.forEach(field => {
        field.expr.forEach(exp => {
            if(exp.fnWhen === 'render') {
                let argList = [];
                exp.fnArgs.forEach(arg => {
                    if(arg.literal) {
                        argList.push(arg.propertyName);
                    }
                    else {
                        argList.push(field[arg.propertyName]);
                    }
                });
                switch(exp.fnType.toLowerCase()) {
                    case "return":
                        exp.fnOutSource.forEach((source, srcIndex) => {
                            for (const drivenField of this.drivenFieldModel) {
                                if(drivenField.order === source) {
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

function buildDrivenButtonModel() {
    this.errorTrace.stack = 'Method: buildDrivenButtonModel, file: demographicCommercialHelper.js';
    this.drivenButtonModel = this.drivenComponentClass.displayButtons();

    this.drivenButtonModel.forEach(field => {
        field.expr.forEach(exp => {
            if(exp.fnWhen === 'render') {
                let argList = [];
                exp.fnArgs.forEach(arg => {
                    if(arg.literal) {
                        argList.push(arg.propertyName);
                    }
                    else {
                        argList.push(field[arg.propertyName]);
                    }
                });
                switch(exp.fnType.toLowerCase()) {
                    case "return":
                        exp.fnOutSource.forEach((source, srcIndex) => {
                            for (const drivenButton of this.drivenButtonModel) {
                                if(drivenButton.order === source) {
                                    drivenButton[exp.fnOut[srcIndex]] = exp.fnName.apply(this, argList);
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

function buildUSPSDataModel() {
    this.errorTrace.stack = 'Method: buildUSPSDataModel, file: demographicCommercialHelper.js';
    this.uspsModel = this.drivenComponentClass.modalUSPSDataModel();
}