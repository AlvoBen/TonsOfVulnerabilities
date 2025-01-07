import { commonSelector, flowRenderObject, flowButtonRenderObject, modalUSPSErrorModel, modalUSPSDataModel, copyFieldMap } from './demographicMedicareAvfInitialLightning';
import { phoneFormat, phoneFormatter, phoneSecondaryFormat, objectMapperSimple, showToastEvent, isJsonString } from 'c/crmserviceHelper';
import { addressStandardizedResponseDTO, mailingAddressStandardizedResponseDTO, tempAddressStandardizedResponseDTO, 
    AddressListResponse, MailingAddressListResponse, TempAddressListResponse, 
    addressDisplayData, OSARequestModel, OSAReplyModel, summaryDataModel } from './demographicMedicareAvfModelLightning';
import retrieveAddrStdzResult from '@salesforce/apex/AddressStandardizeList_D_HUM.retrieveAddrStdzResult';
import getPlanID from '@salesforce/apex/DemographicUpdateController_LC_HUM.getPlanID'; 
import preparePlanID from '@salesforce/apex/DemographicUpdateController_LC_HUM.preparePlanID';
import retrieveOSAResult from '@salesforce/apex/OSAService_D_HUM.retrieveOSAResult';
import { pubsub } from 'c/pubsubComponent';
import uspsBadRequestMsg from '@salesforce/label/c.uspsBadRequestMsg';
import uspsAddressServiceDownMsg from '@salesforce/label/c.uspsAddressServiceDownMsg';
import MMPAddressLengthCap from '@salesforce/label/c.MMPAddressLengthCap';
import US1755360SwitchLabel from '@salesforce/label/c.US1755360SwitchLabel';
import US0015656SwitchLabel from '@salesforce/label/c.US0015656SwitchLabel';
import checkIfDateInFuture from '@salesforce/apex/GenericHelper_LightningStrides_LH_HUM.checkIfDateInFuture';
import US1905759_SwitchLabel from '@salesforce/label/c.US1905759_SwitchLabel';
import US2871243_SwitchLabel from '@salesforce/label/c.US2871243_SwitchLabel';

const checkFormValidity = function(selector) {
    this.errorTrace.stack = 'Method: checkFormValidity, file: demographicMedicareAVFHelper.js';
    let isValid = true;
    window.setTimeout(function() {
        let elemArray = this.template.querySelectorAll(`${selector}`);
        elemArray.forEach(item => {
            if(item.nodeName.toLowerCase().includes("combobox") && item.required && item.value === '-None-') {
                item.setCustomValidity('State is required');
            }
            else {
                item.setCustomValidity('');
            }
            if(isValid){
                if(typeof item.getAttribute("data-valid") !== "undefined" && 
                    item.getAttribute("data-valid") !== null) {
                    isValid = (item.getAttribute("data-valid") === 'true');
                }
            }
            if(typeof item.checkValidity !== "undefined") {
                if(!item.checkValidity()) {
                    isValid = false;
                }
                if(typeof item.reportValidity !== "undefined") {
                    item.reportValidity();
                }
            }
        });

        let baseModel = flowRenderObject;
        let pageModel = this[baseModel.model][baseModel.leftProperty][this.currentPageNumber - 1];
        if(typeof this.isAddressVerified[pageModel.pageName.toLowerCase()] !== 'undefined') {
            if(this.journeyParams.mode[this.journeyParams.mode.length -2].toLowerCase() !== (pageModel.pageName + 'temporary')) {
                if(!this.isAddressVerified[pageModel.pageName.toLowerCase()].status) {
                    isValid = false;
                }
            }
        }

        if(this.isBadRequest && this.badPageNumber === this.currentPageNumber) {
            isValid = false;
        }
        
        this.isFormInvalid = !isValid;
    }.bind(this), 200);
};

const deducePredecessor = function() {
    this.errorTrace.stack = 'Method: deducePredecessor, file: demographicMedicareAVFHelper.js';
    let returnValue = 0;
    let baseModel = flowRenderObject;
    let pageModel = this[baseModel.model][baseModel.leftProperty][this.currentPageNumber - 1];
    let pageIndex = 0;
    for(let p = 0; p < this.journeyParams.mode.length; p++) {
        if(this.journeyParams.mode[p].toLowerCase().startsWith(pageModel.pageName.toLowerCase())) {
            pageIndex = p;
            break;
        }
    }
    let previousPageIndex = (pageIndex > -1) ? (pageIndex === 0) ? 0 : pageIndex - 1 : 0;
    let previousPageName = this.journeyParams.mode[previousPageIndex];
    if(typeof previousPageName === "undefined") {
        return returnValue;
    }
    let previousPageModel = 
        this[baseModel.model][baseModel.leftProperty]
        .filter(page => previousPageName.toLowerCase().startsWith(page.pageName.toLowerCase()));
    
    for(let i = 0; i < previousPageModel.length; i++) {
        returnValue = previousPageModel[i].pageNumber;
        break;
    }
    
    return returnValue;
}

const deduceSuccessorFromLanding = function(options) {
    let returnValue = 0;
    this.errorTrace.stack = 'Method: deduceSuccessorFromLanding, file: demographicMedicareAVFHelper.js';
    let baseModel = flowRenderObject;
    let pageModel = this[baseModel.model][baseModel.leftProperty][this.currentPageNumber - 1];
    let pageIndex = 0;
    for(let p = 0; p < this.journeyParams.mode.length; p++) {
        if(this.journeyParams.mode[p].toLowerCase().startsWith(pageModel.pageName.toLowerCase())) {
            pageIndex = p;
            break;
        }
    }
    let nextPageIndex = (pageIndex > -1) ? (pageIndex === 0) ? "first" : pageIndex + 1 : "last";
    let journeyName = this.journeyParams.name.toLowerCase();
    let nextPageName = this.journeyParams.mode[nextPageIndex];
    if(nextPageIndex !== "first" && nextPageIndex !== "last" && typeof nextPageName !== 'undefined') {
        nextPageName = this.journeyParams.mode[nextPageIndex];
    }
    else if(nextPageIndex === "first") {
        nextPageName = journeyName;
    }
    else {
        nextPageName = "summary";
    }
    let nextPageModel = 
        this[baseModel.model][baseModel.leftProperty]
            .filter(page => nextPageName.toLowerCase().startsWith(page.pageName.toLowerCase()));
    for(let i = 0; i < nextPageModel.length; i++) {
        returnValue = nextPageModel[i].pageNumber;
        break;
    }
    return returnValue;
}

const deduceSuccessorFromTemporary = function() {
    this.errorTrace.stack = 'Method: deduceSuccessorFromTemporary, file: demographicMedicareAVFHelper.js';
    let returnValue = 0;
    let baseModel = flowRenderObject;
    let currPageName = this[baseModel.model][baseModel.leftProperty][this.currentPageNumber - 1].pageName;
    if(this.journeyParams.name.toLowerCase() === "temporary" && currPageName.toLowerCase() === "temporary") {
        returnValue = 2;
        this.journeyParams.mode.push("residential");
    }
    else {
        returnValue = 5;
        this.journeyParams.mode.push("summary");
    }
    return returnValue;
}

const deduceSuccessorFromMailing = function() {
    this.errorTrace.stack = 'Method: deduceSuccessorFromMailing, file: demographicMedicareAVFHelper.js';
    let returnValue = 0;
    let baseModel = flowRenderObject;
    let currPageName = this[baseModel.model][baseModel.leftProperty][this.currentPageNumber - 1].pageName;
    if(this.journeyParams.name.toLowerCase() === "temporary" && currPageName.toLowerCase() === "mailing") {
        returnValue = 5;
        if(this.journeyParams.mode.indexOf('summary') === -1) {
            this.journeyParams.mode.push("summary");
        }
    }
    else {
        returnValue = this[baseModel.model][baseModel.leftProperty][this.currentPageNumber - 1].pageSuccessor;
    }
    return returnValue;
}

const deriveReactiveBtnModel = function(baseBtnModel) {
    this.errorTrace.stack = 'Method: deriveReactiveBtnModel, file: demographicMedicareAVFHelper.js';
    this[baseBtnModel.model][baseBtnModel.leftProperty].forEach(button => {
        if(this.currentPageNumber === button.pageNumber) {
            if(typeof button.expr !== "undefined") {
                button.expr.forEach(expr => {
                    if(expr.fnType.toLowerCase() === "return" && 
                        expr.fnWhen.toLowerCase() === "render") {
                        let argList = [];
                        expr.fnArgs.forEach(fnArg => {
                            if(!fnArg.literal) {
                                argList.push(button[fnArg.propertyName]);
                            }
                            else {
                                argList.push(fnArg.propertyName);
                            }
                        });
                        if(expr.fnOutSource.toLowerCase() === "self") {
                            button[expr.fnOut] = button[expr.fnName].apply(this, argList);
                        }
                    }
                });
            }
        }
    });

    let dirtyArray = this[baseBtnModel.model][baseBtnModel.leftProperty];
    let currentPageNum = this.currentPageNumber;
    let dirtyCurrPageBtnArray = dirtyArray.filter(item => item.pageNumber === currentPageNum);

    this[baseBtnModel.model][baseBtnModel.rightProperty] = dirtyCurrPageBtnArray;
};

const deriveReactiveModel = function(baseModel) {
    this.errorTrace.stack = 'Method: deriveReactiveModel, file: demographicMedicareAVFHelper.js';
    this[baseModel.model][baseModel.leftProperty].forEach(page => {
        if(this.currentPageNumber === page.pageNumber) {
            page.pageFeatures.fields.forEach(field => {
                if(typeof field.expr !== "undefined") {
                    field.expr.forEach(expr => {
                        if(expr.fnType.toLowerCase() === "return" && 
                            expr.fnWhen.toLowerCase() === "render") {
                            let argList = [];
                            expr.fnArgs.forEach(fnArg => {
                                if(!fnArg.literal) {
                                    argList.push(field[fnArg.propertyName]);
                                }
                                else {
                                    argList.push(fnArg.propertyName);
                                }
                            });
                            if(expr.fnOutSource.toLowerCase() === "self") {
                                field[expr.fnOut] = field[expr.fnName].apply(this, argList);
                            }
                            else {
                                page[expr.fnOut] = field[expr.fnName].apply(this, argList);
                            }
                        }
                    });
                }
            });
        }
    });
    
    let dirtyArray = this[baseModel.model][baseModel.leftProperty];
    let currentPageNum = this.currentPageNumber;
    let dirtyCurrPageArray = dirtyArray.filter(item => item.pageNumber === currentPageNum);

    dirtyCurrPageArray.forEach(dirtyPage => {
        dirtyPage.pageFeatures.fields.forEach(field => {
            if(typeof field.visibility !== "undefined") {
                let currPageName = dirtyPage.pageName.toLowerCase();
                let pageIndex = 0;
                let tempArray = this.journeyParams.mode.filter(temp => temp.startsWith(currPageName));
                let targetItem = "";
                if(tempArray.length > 1) {
                    targetItem = tempArray[tempArray.length - 1].toLowerCase();
                }
                else {
                    targetItem = tempArray[tempArray.length - 1].toLowerCase();
                }
                for(let p = 0; p < this.journeyParams.mode.length; p++) {
                    if(this.journeyParams.mode[p].toLowerCase() === targetItem) {
                        pageIndex = p;
                        break;
                    }
                }
                let visibilityJourney = this.journeyParams.name.toLowerCase();
                let visibilityMode = 
                    this.journeyParams.mode[pageIndex].toLowerCase();
                let visibilityArray = 
                    field.visibility.filter(item => item.journey.toLowerCase() === visibilityJourney);
                for(let i = 0; i < visibilityArray.length; i++) {
                    if(this.journeyParams.name.toLowerCase() === visibilityArray[i].journey.toLowerCase()) {
                        if(visibilityMode.toLowerCase() === visibilityArray[i].mode.toLowerCase()) {
                            field.display = true;
                            break;
                        }
                        else {
                            field.display = false;
                        }
                    }
                }
            }
            else {
                field.display = true;
            }
        });
    });

    this[baseModel.model][baseModel.rightProperty] = dirtyCurrPageArray;
    checkFormValidity.apply(this, [commonSelector]);
};

const retrofitValues = function() {
    console.log('check');
}

const processFieldChange = function(e, baseModel) {
    this.errorTrace.stack = 'Method: processFieldChange, file: demographicMedicareAVFHelper.js';
    let propertyName = "", propertyValue = "", comboValue = "", propInterim = "";
    let elementOrder = e.target.getAttribute("data-index");
    let currentPageNum = this.currentPageNumber;

    if(typeof e.detail !== "undefined") {
        propertyName = "value";
        propertyValue = e.detail.value;
    }
    else {
        let elemType = e.target.type;
        switch(elemType.toLowerCase()) {
            case "radio":
                propertyName = "value";
                propertyValue = e.target.value;
                propInterim = e.target.getAttribute("data-label");
                break;
            case "combobox":
                propertyName = "combobox";
                propertyValue = e.detail.value;
                comboValue = e.target.getAttribute("data-value");
                break;
            default:
                break;
        }
    }
    
    this[baseModel.model][baseModel.leftProperty].forEach(dirtyPage => {
        if(dirtyPage.pageNumber === currentPageNum) {
            dirtyPage.pageFeatures.fields.forEach(field => {
                if(field.order === parseInt(elementOrder)) {
                    field.value = propertyValue;
                    if(propInterim !== "") {
                        field.valueLabel = propInterim;
                    }
                    if(comboValue !== "") {
                        field.valueCode = comboValue;
                    }
                    if(typeof field.expr !== "undefined") {
                        field.expr.forEach(expr => {
                            if(expr.fnType.toLowerCase() === "return" && 
                                expr.fnWhen.toLowerCase() === "change") {
                                let argList = [];
                                expr.fnArgs.forEach(fnArg => {
                                    if(!fnArg.literal) {
                                        argList.push(field[fnArg.propertyName]);
                                    }
                                    else {
                                        argList.push(fnArg.propertyName);
                                    }
                                });
                                if(expr.fnOutSource.toLowerCase() === "self") {
                                    field[expr.fnOut] = field[expr.fnName].apply(this, argList);
                                }
                                else {
                                    dirtyPage[expr.fnOut] = field[expr.fnName].apply(this, argList);
                                }
                            }
                            else if(expr.fnType.toLowerCase() === "void" && 
                                    expr.fnWhen.toLowerCase() === "change") {
                                let argList = [];
                                expr.fnArgs.forEach(fnArg => {
                                    if(!fnArg.literal) {
                                        argList.push(field[fnArg.propertyName]);
                                    }
                                    else {
                                        argList.push(fnArg.propertyName);
                                    }
                                });
                                if(US0015656SwitchLabel.toUpperCase() === 'Y') {
                                    if(argList.length > 0) {
                                        field[expr.fnName].apply(this, argList);
                                    }
                                    else {
                                        field[expr.fnName].apply(this);
                                    }
                                }
                                else {
                                    if(argList.length > 0) {
                                        field[expr.fnName].apply(this);
                                    }
                                    else {
                                        field[expr.fnName].apply(this,argList);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    });
    deriveReactiveModel.apply(this, [baseModel]);
};

const isThereMismatch = function(baseModel) {
    this.errorTrace.stack = 'Method: isThereMismatch, file: demographicMedicareAVFHelper.js';
    let returnValue = false;
    let currPageNum = this.currentPageNumber;
    this[baseModel.model][baseModel.leftProperty].forEach(page => {
        if(page.pageNumber === currPageNum) {
            let responseObj = this.isAddressVerified[page.pageName.toLowerCase()].data;
            page.pageFeatures.fields.forEach(field => {
                for(let key in responseObj) {
                    if(key.toLowerCase() === field.fieldName.toLowerCase()) {
                        if(responseObj[key].toLowerCase() !== field.value.toLowerCase()) {
                            returnValue = true;
                            break;
                        }
                    }
                }
            });
        }
    });
    return returnValue;
};

const performRefresh = function() {
    this.errorTrace.stack = 'Method: performRefresh, file: demographicMedicareAVFHelper.js';
    checkFormValidity.apply(this, [commonSelector]);
    deriveReactiveBtnModel.apply(this, [flowButtonRenderObject]);
}

const triggerOSACall = function(osaRequestObjString, baseModel) {
    this.errorTrace.stack = 'Method: triggerOSACall, file: demographicMedicareAVFHelper.js';
    retrieveOSAResult({osaInput: osaRequestObjString})
        .then(result => {
            let responseObj = JSON.parse(result);
            console.log('OSA service response',responseObj);
            let responseModel = OSAReplyModel;
        
            for(let key in responseModel) {
                if(key) {
                    objectMapperSimple.apply(this, [responseObj, key, responseModel , key]);
                }
            }

            for(let key in responseModel) {
                if(responseModel.hasOwnProperty(key)) {
                    if(key === 'osaServiceResponse') {
                        for(let cKey in responseModel[key]) {
                            if(responseModel[key].hasOwnProperty(cKey)) {
                                if(cKey === 'isOSAResponse') {
                                    for (const item of responseModel[key][cKey]) {
                                        item.OSA = (item.OSA === 'true') ? true : ((item.OSA === 'false') ? false: undefined);
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        
            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                if(page.pageNumber === this.currentPageNumber) {
                    page.pageFeatures.fields.forEach(field => {
                        if(typeof field.osaRender !== "undefined") {
                            //Changes to hide information sec if we get OSA service error start
                            if(!responseModel.calloutErrored){
                                field.osaRender = true;
                            }
                            //Changes to hide information sec if we get OSA service error end
                            objectMapperSimple.apply(this, [responseModel, "OSA", field.osa , "isOSA"]);
                            if(field.osa.isOSA) {
                                field.osa.class = 'slds-p-bottom_small osa-holder osa-bad-msg-holder';
                                if(US2871243_SwitchLabel.toUpperCase() === 'Y') {
                                    field.osa.message = `The ${page.pageName} address you entered is out of the service area (OSA). Refer to the appropriate process for guidance.`;
                                }else {
                                    field.osa.message = `The ${page.pageName} address you entered is out of the service area (OSA).Guidance: Transfer member to Sales, so the member can chose a policy new area. If the ${page.pageName} is outside of the member's service area, they will be disenrolled at the end of current month. Unless it is a future move. For a future move date the current plan will be disenrolled at the end of the month the move occurs.`;
                                }
                            }
                            else if(responseModel.calloutErrored) {
                                showToastEvent.apply(this, ['OSA Service Error', responseModel.serviceCalloutError]);
                            }
                            else if(!field.osa.isOSA) {
                                field.osa.class = 'slds-p-bottom_small osa-holder osa-good-msg-holder';
                                field.osa.message = `The ${page.pageName} address you entered is within the Service Area`;
                            }
                        }
                    });
                }
            });

            performRefresh.apply(this);
            this.isLoading = false;
        })
        .catch(error => {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
            this.isLoading = false;
        });
    
};

const triggerOSA = function(baseModel) {
    this.errorTrace.stack = 'Method: triggerOSA, file: demographicMedicareAVFHelper.js';
    this.isLoading = true;
    let currPageNum = this.currentPageNumber;
    let currPageArray = this[baseModel.model][baseModel.leftProperty].filter(page => page.pageNumber === currPageNum);
    let currPageName = "";
    let responseObj = {};
    let osaRequestObj = OSARequestModel;
    let isOSARequired = false;

    for(let i = 0; i < currPageArray.length; i++) {
        currPageName = currPageArray[i].pageName.toLowerCase();
        isOSARequired = currPageArray[i].isOSARequired;
        break;
    }

    if(isOSARequired) {
        responseObj = this.isAddressVerified[currPageName].data;
        let eligibleData = this.eligibleData.data;
        let polMemberID = '';
        let planID = '';
        for(let key in eligibleData) {
            if(eligibleData.hasOwnProperty(key)) {
                if(key === 'self') {
                    for (const record of eligibleData[key]) {
                        //planID = `${record.Policy__r.Contract_Number__c}-${record.Policy__r.PBP_Code__c}-${record.Policy__r.Medicare_Segment_ID__c}-${record.Policy__r.Coverage_Plan_Effective_Date__c.split('/')[2]}`;
                        //planID = `${record.Plan.Contract_Number__c}-${record.Plan.PBP_Code__c}-${record.Plan.Medicare_Segment_ID__c}-${record.Plan.EffectiveFrom.substring(0, 4)}`; 
                        polMemberID = `${record.Id}`;
                        break;
                    }
                    break;
                }
            }
        }
        preparePlanID({ recordId: polMemberID } )
        .then(result => {
            planID = result;
            console.log('planId----',planID);
            if(planID !== ""){
        osaRequestObj.isOSARequest.PlanID.push(planID);
        osaRequestObj.isOSARequest.FipsCode = responseObj.StateID + responseObj.CountyID;
        osaRequestObj.isOSARequest.ZipCode = responseObj.ZipCode;
        if(this.ruleName === 'Group Medicare') {
            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                if(page.pageNumber === this.currentPageNumber) {
                    page.pageFeatures.fields.forEach(field => {
                        if(typeof field.osaRender !== "undefined") {
                            field.osaRender = true;
                            field.osa.class = 'slds-p-bottom_small osa-holder osa-bad-msg-holder';
                            field.osa.message = 'Out of Service Area (OSA) is not determined for Group Medicare. As a result, OSA determination will not be made. Use the GMAI Market Search Tool or Customer Number Grid to determine Out of Service Area (OSA)';
                        }
                    });
                }
            }); 
            this.isLoading = false;
        }
        else {
            console.log('osa call happening');
            triggerOSACall.apply(this, [JSON.stringify(osaRequestObj), baseModel]);
            }
        }
    })
    }
    else {
        this.isLoading = false;
    }
}

const processModalAction = function(baseModel, refresh, mode) {
    this.errorTrace.stack = 'Method: processModalAction, file: demographicMedicareAVFHelper.js';
    let currPageNum = this.currentPageNumber;
    this[baseModel.model][baseModel.leftProperty].forEach(page => {
        if(page.pageNumber === currPageNum) {
            let responseObj = {};
            responseObj = this.isAddressVerified[page.pageName.toLowerCase()].data;
            page.pageFeatures.fields.forEach(field => {
                switch(mode.toLowerCase()) {
                    case "accept":
                        objectMapperSimple.apply(this, [responseObj, field.fieldName, field, "value"]);
                        // if(field.fieldName === 'AddressLine2') {
                        //     field.value = '';
                        // }
                        this.isAddressVerified[page.pageName.toLowerCase()].data = responseObj;
                        this.isAddressVerified[page.pageName.toLowerCase()].standardStatus = true;
                        break;
                    case "reject":
                        if(typeof field.osaRender !== "undefined") {
                            field.osaRender = false;
                        }
                        this.isBadRequest = false;
                        for(let key in responseObj) {
                            if(responseObj.hasOwnProperty(key)) {
                                responseObj[key] = '';
                            }
                        }
                        this.isAddressVerified[page.pageName.toLowerCase()].data = responseObj;
                        this.isAddressVerified[page.pageName.toLowerCase()].standardStatus = false;
                        break;
                    default:
                        break;
                }
            });
            this.isAddressVerified[page.pageName.toLowerCase()].status = true;
        }
    });
    if(mode.toLowerCase() === "accept") {
        triggerOSA.apply(this, [baseModel]);
    }
    if(refresh) {
        performRefresh.apply(this);
    }
}

const setChangeModalGUI = function(resultAddrObj, requestAddrObj, isBad) {
    this.errorTrace.stack = 'Method: setChangeModalGUI, file: demographicMedicareAVFHelper.js';
    let displayData = addressDisplayData;
    let displayDataLeft = {
        AddressLine1: "",
        AddressLine2: "",
        City: "",
        StateCode: "",
        ZipCode: "",
        CountyName: "",
    };
    
    for(let key in displayData) {
        if(key) {
            objectMapperSimple.apply(
                this, 
                [resultAddrObj, key, displayData, key]
            );
        }
    }
    for(let key in displayDataLeft) {
        if(key) {
            objectMapperSimple.apply(
                this, 
                [requestAddrObj, key, displayDataLeft, key]
            );
        }
    }

    let dataLeft = "";
    for(let key in displayDataLeft) {
        if(dataLeft === "") {
            dataLeft = '<div class="slds-col slds-size_2-of-4 slds-p-around_xx-small"><strong class="slds-float_right">' + 
                    key + 
                    '</strong></div><div class="slds-col slds-size_2-of-4 slds-p-around_xx-small">' + 
                    displayDataLeft[key] + 
                    '</div>';
        }
        else {
            dataLeft = dataLeft + 
                    '<br/><div class="slds-col slds-size_2-of-4 slds-p-around_xx-small"><strong class="slds-float_right">' + 
                    key + 
                    '</strong></div><div class="slds-col slds-size_2-of-4 slds-p-around_xx-small">' + 
                    displayDataLeft[key] + 
                    '</div>';
        }
    }

    let data = "";
    for(let key in displayData) {
        if(data === "") {
            data = '<div class="slds-col slds-size_2-of-4 slds-p-around_xx-small"><strong class="slds-float_right">' + 
                    key + 
                    '</strong></div><div class="slds-col slds-size_2-of-4 slds-p-around_xx-small">' + 
                    displayData[key] + 
                    '</div>';
        }
        else {
            data = data + 
                    '<br/><div class="slds-col slds-size_2-of-4 slds-p-around_xx-small"><strong class="slds-float_right">' + 
                    key + 
                    '</strong></div><div class="slds-col slds-size_2-of-4 slds-p-around_xx-small">' + 
                    displayData[key] + 
                    '</div>';
        }
    }
    this.popupData.dataList[0].displayDataLeft = '<div class="slds-grid slds-wrap">' + dataLeft + '</div>';
    if(!isBad) {
        this.popupData.dataList[0].displayData = '<div class="slds-grid slds-wrap">' + data + '</div>';
    }
    this.isShowModal = true;
    this.isLoadOverlay = true;
};

const processStandardizationResult = function(result, baseModel, btnDisplayMode, request) {
    this.errorTrace.stack = 'Method: processStandardizationResult, file: demographicMedicareAVFHelper.js';
    let currPageNum = this.currentPageNumber;
    let responseObj = (+currPageNum === 2) ? addressStandardizedResponseDTO : (+currPageNum === 3) ? mailingAddressStandardizedResponseDTO : tempAddressStandardizedResponseDTO;
    let resultObj = JSON.parse(result);
    
    let requestAddrObj = {
        AddressLine1: "",
        AddressLine2: "",
        City: "",
        StateCode: "",
        ZipCode: "",
        CountyName: "",
    }
    let requestObj = JSON.parse(request);
    for(let key in requestAddrObj) {
        if(requestAddrObj.hasOwnProperty(key)) {
            objectMapperSimple.apply(
                this, 
                [requestObj, key, requestAddrObj, key]
            );
        }
    }

    for(const cPage of this[baseModel.model][baseModel.leftProperty]) {
        if(+cPage.pageNumber === +this.currentPageNumber) {
            for(const cField of cPage.pageFeatures.fields) {
                if(cField.fieldName.toLowerCase() === 'countyname') {
                    requestAddrObj[cField.fieldName] = cField.value;
                    break;
                }
            }
            break;
        }
    }

    let resultStatusObj = {
        status: "", fieldName: "ChangeIndicator"
    };
    let resultAddrObj = (+currPageNum === 2) ? AddressListResponse : (+currPageNum === 3) ? MailingAddressListResponse : TempAddressListResponse;

    if(!resultObj.callOutErrored) {
        for(let key in responseObj) {
            if(resultObj.hasOwnProperty(key)) {
                responseObj[key] = resultObj[key];
            }
        }
        objectMapperSimple.apply(
            this, 
            [responseObj, resultStatusObj.fieldName, resultStatusObj, "status"]
        );
        for(let key in resultAddrObj) {
            if(key) {
                objectMapperSimple.apply(
                    this, 
                    [responseObj, key, resultAddrObj, key]
                );
            }
        }
    }
    else {
        this.isShowModal = true;
        this.isLoadOverlay = true;
    }

    if(resultAddrObj.AddressLine2 === null || resultAddrObj.AddressLine2 === undefined) {
        resultAddrObj.AddressLine2 = '';
    }

    switch(resultStatusObj.status.toLowerCase()) {
        case "good":
            this.isBadRequest = false;
            this.isAddressVerified[btnDisplayMode].data = resultAddrObj;
            this.popupData = modalUSPSDataModel();
            if(resultAddrObj.AddressLine1.length > +MMPAddressLengthCap || resultAddrObj.AddressLine2.length > +MMPAddressLengthCap) {
                for(const pButton of this.popupData.buttons) {
                    if(typeof pButton.eventName !== 'undefined') {
                        if(pButton.eventName.toLowerCase() === 'uspsaccept') {
                            pButton.display = false;
                        }
                    }
                    if(typeof pButton.fallback !== 'undefined') {
                        if(pButton.fallback.eventName.toLowerCase() === 'uspsreject') {
                            pButton.showMessage = true;
                            pButton.eventName = pButton.fallback.eventName;
                            pButton.dispatchEventName = pButton.fallback.dispatchEventName;
                            pButton.internal = false;
                        }
                    }
                }
                setChangeModalGUI.apply(this, [resultAddrObj, requestAddrObj, false]);
            }
            else if(isThereMismatch.apply(this, [baseModel])) {
                setChangeModalGUI.apply(this, [resultAddrObj, requestAddrObj, false]);
            }
            else {
                processModalAction.apply(this, [baseModel, false, "accept"]);
            }
            break;
        case "change":
            this.isBadRequest = false;
            this.isAddressVerified[btnDisplayMode].data = resultAddrObj;
            this.popupData = modalUSPSDataModel();
            if(resultAddrObj.AddressLine1.length > +MMPAddressLengthCap || resultAddrObj.AddressLine2.length > +MMPAddressLengthCap) {
                for(const pButton of this.popupData.buttons) {
                    if(typeof pButton.eventName !== 'undefined') {
                        if(pButton.eventName.toLowerCase() === 'uspsaccept') {
                            pButton.display = false;
                        }
                    }
                    if(typeof pButton.fallback !== 'undefined') {
                        if(pButton.fallback.eventName.toLowerCase() === 'uspsreject') {
                            pButton.showMessage = true;
                            pButton.eventName = pButton.fallback.eventName;
                            pButton.dispatchEventName = pButton.fallback.dispatchEventName;
                            pButton.internal = false;
                        }
                    }
                }
            }
            setChangeModalGUI.apply(this, [resultAddrObj, requestAddrObj, false]);
            break;
        case "bad":
            this.isBadRequest = true;
            this.badPageNumber = currPageNum;
            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                if(page.pageNumber === currPageNum) {
                    page.pageFeatures.fields.forEach(field => {
                        if(typeof field.osaRender !== "undefined") {
                            field.osaRender = false;
                        }
                        for(let key in resultAddrObj) {
                            if(key.toLowerCase() === field.fieldName.toLowerCase()) {
                                resultAddrObj[key] = field.value;
                                break;
                            }
                        }
                    });
                }
            });
            this.isAddressVerified[btnDisplayMode].data = resultAddrObj;
            this.popupData = modalUSPSDataModel();
            for(const pButton of this.popupData.buttons) {
                if(typeof pButton.eventName !== 'undefined') {
                    if(pButton.eventName.toLowerCase() === 'uspsaccept') {
                        pButton.display = false;
                    }
                }
                if(typeof pButton.fallback !== 'undefined') {
                    if(pButton.fallback.eventName.toLowerCase() === 'uspsreject') {
                        pButton.showMessage = true;
                        pButton.eventName = pButton.fallback.eventName;
                        pButton.dispatchEventName = pButton.fallback.dispatchEventName;
                        pButton.internal = false;
                    }
                }
            }
            this.popupData.dataList[0].displayData = `<div class="slds-grid slds-wrap"><strong>${uspsBadRequestMsg}</strong></div>`;
            setChangeModalGUI.apply(this, [resultAddrObj, requestAddrObj, true]);
            break;
        case "error":
            this.isBadRequest = true;
            this.badPageNumber = currPageNum;
            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                if(page.pageNumber === currPageNum) {
                    page.pageFeatures.fields.forEach(field => {
                        if(typeof field.osaRender !== "undefined") {
                            field.osaRender = false;
                        }
                        for(let key in resultAddrObj) {
                            if(key.toLowerCase() === field.fieldName.toLowerCase()) {
                                resultAddrObj[key] = field.value;
                                break;
                            }
                        }
                    });
                }
            });
            this.isAddressVerified[btnDisplayMode].data = resultAddrObj;
            //this.popupData = modalUSPSErrorModel();
            this.popupData = modalUSPSDataModel();
            for(const pButton of this.popupData.buttons) {
                if(typeof pButton.eventName !== 'undefined') {
                    if(pButton.eventName.toLowerCase() === 'uspsaccept') {
                        pButton.display = false;
                    }
                }
                if(typeof pButton.fallback !== 'undefined') {
                    if(pButton.fallback.eventName.toLowerCase() === 'uspsreject') {
                        pButton.showMessage = true;
                        pButton.eventName = pButton.fallback.eventName;
                        pButton.dispatchEventName = pButton.fallback.dispatchEventName;
                        pButton.internal = false;
                    }
                }
            }
            this.popupData.dataList[0].displayData = `<div class="slds-grid slds-wrap"><strong>${uspsAddressServiceDownMsg}</strong></div>`;
            setChangeModalGUI.apply(this, [resultAddrObj, requestAddrObj, true]);
            break;
        default:
            break;
    }

    checkFormValidity.apply(this, [commonSelector]);
    deriveReactiveBtnModel.apply(this, [flowButtonRenderObject]);
}

const processAddressStandardization = function(e, baseModel) {
    this.errorTrace.stack = 'Method: processAddressStandardization, file: demographicMedicareAVFHelper.js';
    let callOutComplete = false;
    let btnDisplayMode = e.target.getAttribute("data-display");
    let btnOrder = e.target.getAttribute("data-id");
    let pageBtnModel = this[baseModel.model][baseModel.leftProperty].filter(
        item => item.pageNumber === this.currentPageNumber
    );
    let btnModel = pageBtnModel.filter(btn => +btn.order === +btnOrder);
    let addressObj = {};
    for(let i = 0; i < btnModel.length; i++) {
        addressObj = btnModel[i].requestAVObj;
        break;
    }
    
    if(US1755360SwitchLabel.toUpperCase() === 'Y') {
        for(let aKey in addressObj) {
            if(addressObj.hasOwnProperty(aKey)) {
                if(aKey === 'AddressLine2' && addressObj[aKey] === null) {
                    addressObj[aKey] = '';
                }
            }
        }
    }
    
    this.isLoading = true;
    retrieveAddrStdzResult({addressInput: JSON.stringify(addressObj)})
        .then(result => {
            let responseObj = (isJsonString(result)) ? JSON.parse(result) : { calloutErrored: true, serviceCalloutError: 'Invalid Response' };
            callOutComplete = true;
            this.isLoading = false;
            if(responseObj.calloutErrored) {
                // pubsub.publish('showError', {
                //     detail: { errorMessage: responseObj.serviceCalloutError, errorTitle: 'Address Standardization Service Error!!!' }
                // });
                // this.isLoadOverlay = false;
                let blankAddressObj = {
                    Id: "",
                    SuccessFlag: "",
                    AddressLine1: "",
                    AddressLine2: "",
                    City: "",
                    StateCode: "",
                    ZipCode: "",
                    StateID: "",
                    ZipCodePlus: "",
                    CountyName: "",
                    CountyID: "",
                    Longitude: "",
                    Latitude: "",
                    Deliverable: "",
                    ReturnCode: "",
                    Valid: "",
                    ChangeIndicator: "error"
                }
                let blankAddressResponseObj = {
                    addressStandardizedResponse: {
                        StandardizeAddressResponseList: {
                            AddressList: [blankAddressObj]
                        }
                    }
                }
                processStandardizationResult.apply(this, [JSON.stringify(blankAddressResponseObj), flowRenderObject, btnDisplayMode, JSON.stringify(addressObj)]);
            }
            else {
                processStandardizationResult.apply(this, [result, flowRenderObject, btnDisplayMode, JSON.stringify(addressObj)]);
            }
        })
        .catch(error => {
            this.isAddressVerified[btnDisplayMode].status = false;
            callOutComplete = true;
            this.isLoading = false;
            this.isLoadOverlay = false;
            pubsub.publish('showError', {
                detail: { errorMessage: 'Please contact your System Admin', errorTitle: 'Address Standardization Service Error!!!' }
            });
        });
}

const summaryGenerator = function(dirtyModel) {
    this.errorTrace.stack = 'Method: summaryGenerator, file: demographicMedicareAVFHelper.js';
    let summaryModel = summaryDataModel;

    summaryModel.forEach(section => {
        section.fields.forEach(field => {
            dirtyModel.forEach(page => {
                page.pageFeatures.fields.forEach(pField => {
                    if(field.foreignKey === pField.order) {
                        field.display = (page.pageName === 'temporary') ? true : pField.display;
                        if(pField.type === 'date') {
                            if(pField.value !== 'NaN/NaN/NaN' && pField.value !== '' && pField.value !== null && pField.value !== undefined) {
                            let date = new Date(pField.value.replace(/-/g, '/').replace(/T.+/, ''));
                            let month = date.getMonth() + 1;
                            let year = date.getFullYear();
                            let day = date.getDate();
                            field.value = `${(month < 10) ? '0': ''}${month}/${(day < 10) ? '0': ''}${day}/${(year < 10) ? '0': ''}${year}`;
                            }
                            else if(pField.value === 'NaN/NaN/NaN') {
                                field.value = '';
                            }
                        }
                        else {
                            field.value = (pField[field.propName] !== undefined) ? pField[field.propName] : "";
                        }
                        if(typeof pField.copied !== "undefined" && (field.display === false || typeof field.display === "undefined")) {
                            if(pField.copied) {
                                field.display = true;
                            }
                            else {
                                field.display = false;
                            }
                        }
                    }
                });
            })
        });
    });

    this.summaryDataModel.avfModel = summaryModel;
   
}

const processButtonClick = function(e, baseModel) {
    this.errorTrace.stack = 'Method: processButtonClick, file: demographicMedicareAVFHelper.js';
    let eventName = e.target.getAttribute("data-mode");
    let currentPageNum = this.currentPageNumber;
    let pagePredecessor = 
        this[baseModel.model][baseModel.leftProperty][currentPageNum - 1].pagePredecessor;
    let pageSuccessor = 
        this[baseModel.model][baseModel.leftProperty][currentPageNum - 1].pageSuccessor;
    let pubData = [];
    let pageModel = this[baseModel.model][baseModel.leftProperty][currentPageNum - 1];
    let argsList = [];
    let resultObtained = false;
    let targetKey = '';
    let sourceKey = '';
    let verifictionUpdateResult = 2;

    switch(eventName.toLowerCase()) {
        case "next":
            for(const iField of pageModel.pageFeatures.fields) {
                if(iField.display && typeof iField.expr !== 'undefined') {
                    for(const iExpr of iField.expr) {
                        if(iExpr.fnName === "deduceCheck") {
                            for(const iArgs of iExpr.fnArgs) {
                                if(iArgs.literal) {
                                    argsList.push(iArgs.propertyName);
                                }
                                else {
        
                                    argsList.push(iField[iArgs.propertyName]);
                                }
                            }
                            if(iExpr.fnType === 'return') {
                                if(iExpr.fnOutSource === 'self') {
                                    iField[iExpr.fnOut] = iField[iExpr.fnName].apply(this, argsList);
                                }
                                else {
                                    pageModel[iExpr.fnOut] = iField[iExpr.fnName].apply(this, argsList);
                                }
                            }
                            else {
                                iField[iExpr.fnName].apply(this, argsList);
                            }
                            break;
                        }
                    }
                }
				if(iField.value === 'mailingTemporary' && iField.label === 'Does the member have a mailing address different from their permanent residential address?'){
                   this.isAddressVerified['mailing'].status = true ;
                }
                if(iField.value === 'residentialTemporary' && iField.label === 'Does the member have a residential address different from their mailing address provided?'){
                    this.isAddressVerified['residential'].status = true ;
                }
				if(!this.isSwitchAddressVerified && iField.value === 'mailing' && iField.label === 'Does the member have a mailing address different from their permanent residential address?'){
                    this.isAddressVerified['mailing'].status = false ;
					this.isSwitchAddressVerified = true;
                 }
                 if(!this.isSwitchAddressVerified2 && iField.value === 'residential' && iField.label === 'Does the member have a residential address different from their mailing address provided?'){
                    this.isAddressVerified['residential'].status = false ;
					this.isSwitchAddressVerified2 = true;
                 }
				 if(!this.isSwitchAddressVerified3 && iField.label === 'What address is the Member calling to update?' && iField.value === 'residential'){
                    this.isAddressVerified['mailing'].status = false ;
                    this.isSwitchAddressVerified3 = true;
                 }
                 if(!this.isSwitchAddressVerified4 && iField.label === 'What address is the Member calling to update?' && iField.value === 'mailing'){
                    this.isAddressVerified['residential'].status = false ;
                    this.isSwitchAddressVerified4 = true;
                 }
            } 
            this.currentPageNumber = pageSuccessor;
            if(pageSuccessor === 5) {
                let pageName = this.journeyParams.mode[this.journeyParams.mode.length - 2];
                let predPageANumber = this[baseModel.model][baseModel.leftProperty].filter(page => pageName.startsWith(page.pageName))[0].pageNumber;
                this[baseModel.model][baseModel.leftProperty][pageSuccessor - 1].pagePredecessor = predPageANumber;
                summaryGenerator.apply(this, [this[baseModel.model][baseModel.leftProperty]]);
                this.summaryDataModel.showSummary = true;
            }
            else {
                this.summaryDataModel.showSummary = false;
            }
            break;
        case "previous":
            this.summaryDataModel.showSummary = false;
           
        if(this.currentPageNumber === 1){
            let preEventName = "previousEvent"
            pubsub.publish('triggernextprevious', {
                    detail: { eventName: preEventName, summaryData:''}
                });
                pubsub.publish('emissionFromSummary', {
                    detail: { eventName: preEventName, order: 1 }
                });
                this.display=false;
                
        }
        else{
            this.currentPageNumber = pagePredecessor;
        }
            break;
        case "standardizeaddress":
            processAddressStandardization.apply(this, [e, flowButtonRenderObject]);
            break;
        case "finish":
            for (const page of this[baseModel.model][baseModel.leftProperty]) {
                for(const field of page.pageFeatures.fields) {
                    if(field.isSummary) {
                        pubData.push(field);
                    }
                    else {
                        if(field.fieldName === "personPhone") {
                            pubData.push(field);
                        }
                        if(field.fieldName === "addressUdpateType") {
                            pubData.push(field);
                        }
                    }

                    if(field.copied && US1905759_SwitchLabel.toUpperCase() === 'Y' && !resultObtained) {
                        for (const visibility of field.visibility) {
                            if(visibility.mode.includes('mailing')) {
                                targetKey = 'mailing';
                                sourceKey = 'residential';
                                resultObtained = true;
                                break;
                            }
                            else if(visibility.mode.includes('residential')) {
                                targetKey = 'residential';
                                sourceKey = 'mailing';
                                resultObtained = true;
                                break;
                            }
                        }
                    }
                }
            }

            if(targetKey !== '' && sourceKey !== '' && US1905759_SwitchLabel.toUpperCase() === 'Y') {
                verifictionUpdateResult = deduceAddressVerified.apply(this, [sourceKey, targetKey]);
            }

            if(verifictionUpdateResult === 0 && US1905759_SwitchLabel.toUpperCase() === 'Y') {
                pubsub.publish('showError', {
                    detail: { errorMessage: 'Please contact your System Admin', errorTitle: 'Address Verified Model update malfunction!!!' }
                });
            }

            pubsub.publish('initiateUpdateModel', {
                detail: { visibility: 3, avfData: pubData, addressData: this.isAddressVerified }
            });
            pubsub.publish('toggleSummary', {
                detail: { display: true, source: 'avf' }
            });  
            break;
        default:
            break;
    }

    deriveReactiveModel.apply(this, [baseModel]);
}

export function deduceAddressVerified(sourceKey = '', targetKey = '') {
    let sourceData = {};
    let sourceStandardStatus = false;
    let sourceStatus = false;

    //1. Check for initial values to avoid code error
    if(targetKey === '') {
        return 0;
    }
    if(sourceKey === '') {
        return 0;
    }
    if(typeof this.isAddressVerified[sourceKey] === 'undefined') {
        return 0;
    }
    if(typeof this.isAddressVerified[targetKey] === 'undefined') {
        return 0;
    }

    //2. Process the address copy logic here
    sourceData = this.isAddressVerified[sourceKey].data;
    sourceStandardStatus = this.isAddressVerified[sourceKey].standardStatus;
    sourceStatus = this.isAddressVerified[sourceKey].status;

    this.isAddressVerified[targetKey].data = sourceData;
    this.isAddressVerified[targetKey].status = sourceStatus;
    this.isAddressVerified[targetKey].standardStatus = sourceStandardStatus;

    return 1;
}

const checkValidity = function(options, requiredObj) {
    this.errorTrace.stack = 'Method: checkValidity, file: demographicMedicareAVFHelper.js';
    let isValid = false;
    for(let i = 0; i < options.length; i++) {
        if(options[i].checked) {
            isValid = true;
            break;
        }
    }
    if(!isValid) {
        if(!requiredObj.value) {
            isValid = true;
        }
    }
    return {
        valid: isValid,
        class: (isValid) ? "slds-form-element" : "slds-form-element slds-has-error",
        labelClass: (isValid) ? "slds-form-element__label valid-item" : 
                                "slds-form-element__label invalid-item",
        message: (isValid) ? "" : requiredObj.message
    }
}

const deduceCheckHelper = function(options, value) {
    this.errorTrace.stack = 'Method: deduceCheckHelper, file: demographicMedicareAVFHelper.js';
    let returnOptions = options;
    let selectedValue = "";
    let unSelectedValue = "";
    let baseModel = flowRenderObject;
    let pageModels = this[baseModel.model][baseModel.leftProperty];
    let pageModel = this[baseModel.model][baseModel.leftProperty][this.currentPageNumber - 1];
    let currentSelectedDifferent = true;

    for(const iOption of options) {
        if(iOption.checked) {
            currentSelectedDifferent = (iOption.value.toLowerCase() === value.toLowerCase()) ? false : true;
            break;
        }
    }

    if(typeof arguments[2] !== "undefined") {
        if(arguments[2] === "purge" && currentSelectedDifferent) {
            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                if(page.pageNumber !== 1) {
                    for(let pageKey in this.isAddressVerified) {
                        if(this.isAddressVerified.hasOwnProperty(pageKey)) {
                            if(pageKey.toLowerCase() === page.pageName.toLowerCase()) {
                                this.isAddressVerified[pageKey].status = false;
                                this.isAddressVerified[pageKey].data = {};
                                break;
                            }
                        }
                    }
                    page.pageFeatures.buttons.forEach(button => {
                        if(button.identifier.value === 'next') {
                            button.setDisable = true;
                        }
                    });
                    page.pageFeatures.fields.forEach(field => {
                        if(typeof field.valueLabel !== "undefined") {
                            field.valueLabel = "";
                        }
                        if(typeof field.value !== "undefined") {
                            field.value = "";
                        }
                        if(typeof field.display !== "undefined") {
                            field.display = false;
                        }
                        if(typeof field.copied !== "undefined") {
                            field.copied = false;
                        }
                        if(typeof field.osaRender !== "undefined") {
                            field.osaRender = false;
                        }
                        if(typeof field.renderButton !== "undefined") {
                            if(field.renderButton) {
                                field.setDisable = undefined;
                            }
                        }
                    });
                }
            });
        }
    }

    for(let i = 0; i < returnOptions.length; i++) {
        if(value.toLowerCase() === returnOptions[i].value.toLowerCase()) {
            returnOptions[i].checked = true;
            selectedValue = returnOptions[i].value.toLowerCase();
            if(typeof arguments[3] !== "undefined") {
                if(returnOptions[i].id === arguments[3]) {
                    let mapObj = copyFieldMap[arguments[2]];
                    let sourceObj = {};
                    for(let key in mapObj) {
                        if(key) {
                            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                                page.pageFeatures.fields.forEach(field => {
                                    if(+field.order === +key) {
                                        sourceObj[mapObj[key]] = field.value;
                                    }
                                });
                            });
                        }
                    }
                    for (let sKey in sourceObj) {
                        if(sKey) {
                            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                                page.pageFeatures.fields.forEach(field => {
                                    if(+field.order === +sKey) {
                                        field.value = sourceObj[sKey];
                                        field.copied = true;
                                    }
                                });
                            });
                        }
                    }
                }
                else {
                    let mapObj = copyFieldMap[arguments[2]];
                    for(let key in mapObj) {
                        if(key) {
                            this[baseModel.model][baseModel.leftProperty].forEach(page => {
                                page.pageFeatures.fields.forEach(field => {
                                    if(+field.order === +mapObj[key] && field.copied) {
                                        field.value = "";
                                        field.copied = false;
                                    }
                                });
                            });
                        }
                    }
                }
            }
        }
        else {
            if(currentSelectedDifferent) {
                returnOptions[i].checked = false;
                unSelectedValue = returnOptions[i].value.toLowerCase();
                let index = this.journeyParams.mode.indexOf(unSelectedValue);
                let len = this.journeyParams.mode.length;
                if(index > - 1) {
                    let abc = [];
                    if(index < len - 1) {
                        abc = this.journeyParams.mode.splice(index, len - index);
                    }
                    if(index === len - 1) {
                        abc = this.journeyParams.mode.splice(index);
                    }


                    this[baseModel.model][baseModel.leftProperty].forEach(model => {
                        model.pageFeatures.fields.forEach(field => {
                            if(typeof field.visibility !== "undefined") {
                                field.visibility.forEach(visibility => {
                                    if(visibility.journey.toLowerCase() === 
                                        this.journeyParams.name.toLowerCase()) {
                                        if(abc.includes(visibility.mode.toLowerCase())) {
                                            field.value = "";
                                            if(typeof field.options !== "undefined") {
                                                field.options.forEach(option => {
                                                    option.checked = false;
                                                });
                                            }
                                            if(typeof field.display !== "undefined") {
                                                field.display = false;
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    });
                }
            }
        }
    }

    if(currentSelectedDifferent) {
        if(pageModel.pageLanding) {
            this.journeyParams.name = selectedValue;
            this.journeyParams.mode = [];
            this.journeyParams.mode.push(pageModel.pageName);
            if(selectedValue !== "") {
                if(this.journeyParams.mode.indexOf(selectedValue) === -1) {
                    this.journeyParams.mode.push(selectedValue);
                }
            }
        }
        else {
            if(selectedValue !== "") {
                if(this.journeyParams.mode.indexOf(selectedValue) === -1) {
                    this.journeyParams.mode.push(selectedValue);
                }
            }
        }
    }
    return returnOptions;
};

const formatPhoneNumber = function(fieldValue){
    this.errorTrace.stack = 'Method: formatPhoneNumber, file: demographicMedicareAVFHelper.js';
    let fieldReturnValue = "";
    if(fieldValue.match(phoneSecondaryFormat) && 
        !fieldValue.match(phoneFormat)) {
        fieldReturnValue = phoneFormatter(fieldValue);
        return fieldReturnValue;
    }
    if(!fieldValue.match(phoneSecondaryFormat) && 
        fieldValue.match(phoneFormat)) {
        fieldReturnValue = phoneFormatter(fieldValue);
        return fieldReturnValue;
    }
    fieldReturnValue = fieldValue;
    return fieldReturnValue;
}

const checkBtnValidity = function(fieldOrder, dependentFieldOrders, displayMode) {
    this.errorTrace.stack = 'Method: checkBtnValidity, file: demographicMedicareAVFHelper.js';
    let returnValue = false;
    let isOk = false, isModified = false;
    let baseModel = flowRenderObject;
    let currPageModel = this[baseModel.model][baseModel.rightProperty];
    let fieldArray = [];
    currPageModel.forEach(page => {
        fieldArray = page.pageFeatures.fields.filter(
            field => parseInt(field.order) === parseInt(fieldOrder)
        );
    });
    let depFieldArray = [];
    currPageModel.forEach(page => {
        depFieldArray = page.pageFeatures.fields.filter(
            field => dependentFieldOrders.includes(field.order.toString())
        );
    });
    for(let j = 0; j < depFieldArray.length; j++) {
        if(depFieldArray[j].value !== undefined && depFieldArray[j].value !== null && 
            depFieldArray[j].value !== "") {

            if(this.isAddressVerified[displayMode.toLowerCase()].status) {
                let oldAvData = this.isAddressVerified[displayMode.toLowerCase()].data;
                for(let key in oldAvData) {
                    if(key.toLowerCase() === depFieldArray[j].fieldName.toLowerCase()) {
                        let fieldValue = (typeof  depFieldArray[j].valueCode !== "undefined") ? 
                            depFieldArray[j].valueCode : depFieldArray[j].value;
                        if(oldAvData[key] !== fieldValue) {
                            isModified = true;
                            break;
                        }
                    }
                }
            }
            
            if(typeof depFieldArray[j].pattern !== "undefined") {
                if(typeof depFieldArray[j].pattern.value !== "undefined") {
                    if(depFieldArray[j].value.match(depFieldArray[j].pattern.value)) {
                        isOk = true;
                    }
                    else {
                        isOk = false;
                        returnValue = true;
                        break;
                    }
                }
            }
            
        }
        else {
            isOk = false;
            returnValue = true;
            break;
        }
    }
    if(isOk) {
        if(this.isAddressVerified[displayMode.toLowerCase()].status) {
            if(!isModified) {
                returnValue = true;
            }
            else {
                returnValue = false;
            }
        }
    }

    if(this.isBadRequest) {
        returnValue = false;
    }

    for(let k = 0; k < depFieldArray.length; k++) {
        if(depFieldArray[k].type === 'combobox' && depFieldArray[k].value === '-None-') {
            returnValue = true;
            break; 
        }
    }

    return returnValue;
}

const deduceVerifyBtnClass = function(isEnabled) {
    this.errorTrace.stack = 'Method: deduceVerifyBtnClass, file: demographicMedicareAVFHelper.js';
    let returnValue = "";
    if(isEnabled) {
        returnValue = "slds-m-left_x-small verify-address-btn";
    }
    else {
        returnValue = "slds-m-left_x-small verify-address-btn"; // verify-addr-anime";
    }
    return returnValue;
}

const deduceAVRequestObj = function(addressObj) {
    this.errorTrace.stack = 'Method: deduceAVRequestObj, file: demographicMedicareAVFHelper.js';
    let baseModel = flowRenderObject;
    let currentPageNum = this.currentPageNumber;
    let pageModel = this[baseModel.model][baseModel.leftProperty][currentPageNum - 1];
    pageModel.pageFeatures.fields.forEach(field => {
        for(let key in addressObj) {
            if(key.toLowerCase() === field.fieldName.toLowerCase()) {
                addressObj[key] = (typeof field.valueCode === "undefined") ? 
                    field.value : field.valueCode;
                break;
            }
        }
    });
    return addressObj;
}

const validateAbsenceEndDate = function() {
    this.errorTrace.stack = 'Method: validateAbsenceEndDate, file: demographicMedicareAVFHelper.js';
    let sourceFieldValue = '';
    let baseModel = flowRenderObject;
    let currentPageNum = this.currentPageNumber;
    let pageModel = this[baseModel.model][baseModel.leftProperty][currentPageNum - 1];
    let sourceFieldOrder = arguments[0];
    
    for (const field of pageModel.pageFeatures.fields) {
        if(field.order === +sourceFieldOrder) {
            sourceFieldValue = field.value;
            break;
        }
    }
    return sourceFieldValue;
}

export function deduceIfInFuture() {
    
    let fieldValue = arguments[0];
    let isFuture = false;

    if(US0015656SwitchLabel.toUpperCase() === 'Y') {
        checkIfDateInFuture({dateInput: fieldValue})
            .then(result => {
                isFuture = result;
                
                pubsub.publish("futureDateVerification", {
                    detail: {isDateInFuture: isFuture} 
                });
            })
            .catch(error => {
                
                showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
            });

    }
    
}
export {
    checkFormValidity,
    deriveReactiveModel,
    processFieldChange,
    processButtonClick,
    deducePredecessor,
    deduceSuccessorFromLanding,
    deduceSuccessorFromTemporary,
    deduceSuccessorFromMailing,
    checkValidity,
    deduceCheckHelper,
    formatPhoneNumber,
    deriveReactiveBtnModel,
    checkBtnValidity,
    deduceVerifyBtnClass,
    deduceAVRequestObj,
    processAddressStandardization,
    processStandardizationResult,
    processModalAction,
    validateAbsenceEndDate,
    retrofitValues
}