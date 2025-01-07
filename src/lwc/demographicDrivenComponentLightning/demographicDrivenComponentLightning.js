import { LightningElement, api, track  } from 'lwc';
import { pubsub } from 'c/pubsubComponent';
import { objectMapperSimple } from 'c/crmserviceHelper';
import { deduceFieldDisplay, checkFormValidity, debounce, processFieldsForUI } from './demographicDrivenHelperLightning';
import retrieveAddrStdzResult from '@salesforce/apex/AddressStandardizeList_D_HUM.retrieveAddrStdzResult';
import uspsAddressNotConfirmedMsg from '@salesforce/label/c.uspsAddressNotConfirmedMsg';
import uspsAddressServiceDownMsg from '@salesforce/label/c.uspsAddressServiceDownMsg';
import MMPAddressLengthCap from '@salesforce/label/c.MMPAddressLengthCap';
import US1755360SwitchLabel from '@salesforce/label/c.US1755360SwitchLabel';
import US1529400SwitchLabel from '@salesforce/label/c.US1529400SwitchLabel';
import {uConstants} from 'c/updatePlanDemographicConstants'; 

export default class DemographicDrivenComponentLightning extends LightningElement {
    @api renderDataModel = {
        reactive: [], dirty: [], original: []
    };
    @api buttonModel = [];
    @api stateOptions;
    @api editFieldList;
    @api hideDriven = false;
    @api flowRenderObject = {
        model: "renderDataModel",
        leftProperty: "dirty",
        rightProperty: "reactive",
    };
    currentToggleIndex;
    tempValue = [];
    handleFieldChangeDebounce = debounce(this.processFieldValue, 100);
    uspsModel;
    errorTrace = {
        stack: undefined
    };
    @api messageHolder = [];
    @api emailOptionsList = 'optionList';
    @api emailOptionsId = 'optionList';
    @api US1529400Switch = (US1529400SwitchLabel.toUpperCase() === 'Y' ? true : false);
    @track emailInvalidMsg = uConstants.Email_invalidMsg; 

    connectedCallback() {
        try {
            this.subscriptionEngine();
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    subscriptionEngine() {
        try {
            pubsub.subscribe('renderDrivenComponent', this.renderUI.bind(this));
            pubsub.subscribe('toggleFields', this.toggleFields.bind(this));
            pubsub.subscribe('processMainNext', this.toggleUI.bind(this));
            pubsub.subscribe('uspsAccept', this.processModal.bind(this));
            pubsub.subscribe('isOverrided', this.processModalOverride.bind(this));
            pubsub.subscribe('consumeMessages', this.consumeMessages.bind(this));
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    renderUI(event) {
        try {
            this.renderDataModel.original = event.detail.displayData;
            this.renderDataModel.dirty = [];
            for (const field of this.renderDataModel.original) {
                let fieldObj = {};
                for(let key in field) {
                    if(field.hasOwnProperty(key)) {
                        fieldObj[key] = field[key];
                    }
                }
                this.renderDataModel.dirty.push(fieldObj);
            }
            this.stateOptions = event.detail.stateOptions;
            this.buttonModel = event.detail.buttonData;
            this.uspsModel = event.detail.uspsData;
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    toggleFields(event) {
        try {
            this.currentToggleIndex = event.detail.index;
            deduceFieldDisplay.apply(this, [this.currentToggleIndex]);
            pubsub.publish('emissionFromDriven', {
                detail: { reactiveData: this.renderDataModel.reactive, dirtyData: this.renderDataModel.dirty }
            });
            for (const message of this.messageHolder) {
                if(message.option.indexOf(this.currentToggleIndex) > - 1) {
                    message.display = (message.message.length > 0) ? true : false;
                }
                else {
                    message.display = false;
                }
            }
            pubsub.publish('emitCurrentIndex', {
                detail: { data: this.currentToggleIndex }
            });
            //pubsub.publish('isAddressBad', {
            //    detail: { data: (this.currentToggleIndex === 1) ? true : false }
            //});
            setTimeout(checkFormValidity.bind(this), 1000);
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }   

    toggleUI() {
        try {
            processFieldsForUI.apply(this);
            this.hideDriven = !this.hideDriven;
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    consumeMessages(event) {
        try {
            let messages = event.detail.data;
            this.messageHolder.push({
                display: false, header: true, footer: false, message: messages.cod, option: [1]
            });
            this.messageHolder.push({
                display: false, header: true, footer: false, message: messages.crd, option: [2]
            });
            this.messageHolder.push({
                display: false, header: false, footer: true, message: messages.routing, option: [1, 2]
            });
        }
        catch(error) {
            throw new Error(`${error.message} error at consumeMessages in demographicDrivenComponent.js`);
        }
    }

    handleFieldChange(e) {
        try {
            this.dispatchEvent(new CustomEvent('toggleDirty', {
                detail: { data:  true},
                bubbles: true,
                composed: true,
            }));
            this.prepareChangeArray.apply(this, [e.target.dataset.index, e.target.value]);
            this.handleFieldChangeDebounce.apply(this, [this.tempValue]);
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
        
    }

    processFieldValue() {
        try {
            processFieldsForUI.apply(this);
            deduceFieldDisplay.apply(this, [this.currentToggleIndex]);
            setTimeout(checkFormValidity.bind(this), 100);
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    handleButtonClick(e) {
        try {
            pubsub.publish('toggleLoader', {
                detail: { showLoader: true }
            });
            let fieldOrder = e.target.dataset.fieldid;
            let fieldModel = this.renderDataModel.dirty.filter(field => field.order === +fieldOrder);
            let buttonOrder = e.target.dataset.id;
            let buttonModel = [];
            let requestObj;
            let responseObj;
    
            for (const field of fieldModel) {
                buttonModel = field.buttonModel;
            }
    
            for (const button of buttonModel) {
                if(button.order === +buttonOrder) {
                    requestObj = button.requestAVObj;
                    responseObj = button.responseAVObj;
                    break;
                }
            }
            
            if(US1755360SwitchLabel.toUpperCase() === 'Y') {
                for(let aKey in requestObj) {
                    if(requestObj.hasOwnProperty(aKey)) {
                        if(aKey === 'AddressLine2' && requestObj[aKey] === null) {
                            requestObj[aKey] = '';
                        }
                    }
                }
            }

            retrieveAddrStdzResult({addressInput: JSON.stringify(requestObj)})
                .then(result => {
                    let response = JSON.parse(result);
                    if(!response.calloutErrored) {
                        for(let key in responseObj) {
                            if(responseObj.hasOwnProperty(key)) {
                                objectMapperSimple(response, key, responseObj, key);
                            }
                        }
                        this.handleAVResponse.apply(this, [responseObj, fieldOrder, buttonModel, buttonOrder, requestObj]);
                        pubsub.publish('toggleLoader', {
                            detail: { showLoader: false }
                        });
                    }
                    else {
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
                        };
                        for(let key in responseObj) {
                            if(responseObj.hasOwnProperty(key)) {
                                objectMapperSimple(blankAddressObj, key, responseObj, key);
                            }
                        }
                        this.handleAVResponse.apply(this, [responseObj, fieldOrder, buttonModel, buttonOrder, requestObj]);
                        pubsub.publish('toggleLoader', {
                            detail: { showLoader: false }
                        });
                        // let errorString = response.serviceCalloutError.replace('SERVICE_FAILURE*SERVICEERROR', '');
                        // errorString = errorString.replace('}#', '}');
                        // let errorMessage = JSON.parse(errorString);
                        // pubsub.publish("showError", {
                        //     detail: { errorMessage: response.serviceCalloutError, errorTitle: 'Error in Address Standardization Service' }
                        // });
                        // pubsub.publish('toggleLoader', {
                        //     detail: { showLoader: false }
                        // });
                    }
                })
                .catch(error => {
                    pubsub.publish("showError", {
                        detail: { errorMessage: error, errorTitle: 'Error in Address Standardization Service' }
                    });
                    pubsub.publish('toggleLoader', {
                        detail: { showLoader: false }
                    });
                });
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    handleAVResponse() {
        try {
            let responseObj = arguments[0];
            let fieldOrder = arguments[1];
            let buttonModel = arguments[2];
            let buttonOrder = arguments[3];
            let requestObj = arguments[4];
            let addressDisplayData = {
                AddressLine1: "",
                AddressLine2: "",
                City: "",
                StateCode: "",
                ZipCode: ""
            };
            let addressDisplayDataLeft = {
                AddressLine1: "",
                AddressLine2: "",
                City: "",
                StateCode: "",
                ZipCode: ""
            };
            let displayData = '';
            let displayDataLeft = '';
            for(let key in requestObj) {
                if(requestObj.hasOwnProperty(key)) {
                    if(addressDisplayDataLeft.hasOwnProperty(key)) {
                        addressDisplayDataLeft[key] = requestObj[key];
                    }
                }
            }
            displayDataLeft = this.generateHTML(addressDisplayDataLeft);

            switch(responseObj.ChangeIndicator.toLowerCase()) {
                case "change":
                    for(let key in responseObj) {
                        if(responseObj.hasOwnProperty(key)) {
                            if(addressDisplayData.hasOwnProperty(key)) {
                                addressDisplayData[key] = responseObj[key];
                            }
                        }
                    }
                    if(addressDisplayData.AddressLine2 === null || addressDisplayData.AddressLine2 === undefined) {
                        addressDisplayData.AddressLine2 = '';
                    }

                    displayData = this.generateHTML(addressDisplayData);

                    this.uspsModel.dataList.forEach(data => {
                        data.displayData = displayData;
                        data.displayDataLeft = displayDataLeft;
                    });
                    this.uspsModel.buttons.forEach(btn => {
                        if(btn.eventName.toLowerCase() === 'uspsaccept') {
                            if(addressDisplayData.AddressLine1.length > +MMPAddressLengthCap || addressDisplayData.AddressLine2.length > +MMPAddressLengthCap) {
                                btn.internal = true;
                                this.purgeAVResponseObject.apply(this, [fieldOrder, buttonOrder]);
                            }
                            else {
                                btn.internal = false;
                            }
                        }
                    });
                    pubsub.publish('renderUSPSComponent', {
                        detail: { displayModal: true, modalPopUpData: this.uspsModel, isError: false, currentFieldOrder: arguments[1], currentButtonOrder: arguments[3] }
                    });
                    pubsub.publish('isAddressBad', {
                        detail: { data: false, isAddressVerified: true }
                    });
                    break;
                case "good":
                    for(let key in responseObj) {
                        if(responseObj.hasOwnProperty(key)) {
                            if(addressDisplayData.hasOwnProperty(key)) {
                                addressDisplayData[key] = responseObj[key];
                            }
                        }
                    }
                    if(addressDisplayData.AddressLine2 === null || addressDisplayData.AddressLine2 === undefined) {
                        addressDisplayData.AddressLine2 = '';
                    }
                    pubsub.publish('isAddressBad', {
                        detail: { data: false, isAddressVerified: true }
                    });
                    if(addressDisplayData.AddressLine1.length > +MMPAddressLengthCap || addressDisplayData.AddressLine2.length > +MMPAddressLengthCap) {
                        displayData = this.generateHTML(addressDisplayData);
                        this.uspsModel.dataList.forEach(data => {
                            data.displayData = displayData;
                            data.displayDataLeft = displayDataLeft;
                        });
                        this.uspsModel.buttons.forEach(btn => {
                            if(btn.eventName.toLowerCase() === 'uspsaccept') {
                                btn.internal = true;
                            }
                        });
                        this.purgeAVResponseObject.apply(this, [fieldOrder, buttonOrder]);
                        pubsub.publish('renderUSPSComponent', {
                            detail: { displayModal: true, modalPopUpData: this.uspsModel, isError: false, currentFieldOrder: arguments[1] }
                        });
                    }
                    else {
                        this.injectResponseToField.apply(this, [responseObj, fieldOrder, buttonModel, buttonOrder]);
                        pubsub.publish('isOverrided', {
                            detail: { isAddressOverrided: false, fieldOrder: fieldOrder, buttonOrder: buttonOrder }
                        });
                    }
                    break;
                case "bad":
                    displayData = `<strong>${uspsAddressNotConfirmedMsg}</strong>`;
                    //this.uspsModel.error.message = displayData;
                    this.uspsModel.dataList.forEach(data => {
                        data.displayData = displayData;
                        data.displayDataLeft = displayDataLeft;
                    });
                    this.uspsModel.buttons.forEach(btn => {
                        if(btn.eventName.toLowerCase() === 'uspsaccept') {
                            btn.internal = true;
                        }
                    });
                    pubsub.publish('renderUSPSComponent', {
                        detail: { displayModal: true, modalPopUpData: this.uspsModel, isError: false, currentFieldOrder: arguments[1] }
                    });
                    break;
                case "error":
                    displayData = `<strong>${uspsAddressServiceDownMsg}</strong>`;
                    this.uspsModel.dataList.forEach(data => {
                        data.displayData = displayData;
                        data.displayDataLeft = displayDataLeft;
                    });
                    this.uspsModel.buttons.forEach(btn => {
                        if(btn.eventName.toLowerCase() === 'uspsaccept') {
                            btn.internal = true;
                        }
                    });
                    pubsub.publish('renderUSPSComponent', {
                        detail: { displayModal: true, modalPopUpData: this.uspsModel, isError: false, currentFieldOrder: arguments[1] }
                    });
                    break;
                default:
                    break;
            }
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    purgeAVResponseObject() {
        let fieldOrder = arguments[0];
        let buttonOrder = arguments[1];
        for(let fModel of this.renderDataModel.dirty.filter(field => +field.order === +fieldOrder)) {
            for (const button of fModel.buttonModel) {
                if(button.order === +buttonOrder) {
                    for(let rKey in button.responseAVObj) {
                        if(button.responseAVObj.hasOwnProperty(rKey)) {
                            button.responseAVObj[rKey] = '';
                        }
                    }
                    break;
                }
            }
        }
    }

    generateHTML(displayData) {
        try {
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
            return '<div class="slds-grid slds-wrap">' + data + '</div>';
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    injectResponseToField() {
        try {
            let responseObj = arguments[0];
            let fieldOrder = arguments[1];
            let buttonModel = arguments[2];
            let buttonOrder = arguments[3];
            let targetFieldOrderArray = [];

            buttonModel.forEach(button => {
                if(button.order === +buttonOrder) {
                    button.expr.forEach(exp => {
                        if(exp.fnWhen === 'click') {
                            targetFieldOrderArray = exp.fnName.apply(this);
                        }
                    });
                }
            });
            
            if(responseObj.AddressLine2 === null || typeof responseObj.AddressLine2 === 'undefined') { 
                responseObj.AddressLine2 = ''; 
            }
            
            this.renderDataModel.dirty.forEach(field => {
                if(targetFieldOrderArray.indexOf(field.order) > -1) {
                    if(responseObj.hasOwnProperty(field.fieldName)) {
                        this.prepareChangeArray.apply(this, [field.order, responseObj[field.fieldName]]);
                        this.processFieldValue.apply(this);
                    }
                }
            });

            pubsub.publish('isAddressBad', {
                detail: { data: false, isAddressVerified: true }
            });
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    processModal(event) {
        try {
            let buttonModel = [], responseObj;
            let currentFieldOrder = event.detail.fieldOrder;
            let buttonOrder = event.detail.buttonOrder;

            for (const field of this.renderDataModel.dirty) {
                if(field.order === +currentFieldOrder) {
                    buttonModel = field.buttonModel;
                    break;
                }
                
            }

            for (const button of buttonModel) {
                if(button.order === +buttonOrder) {
                    responseObj = button.responseAVObj;
                    break;
                }
            }

            this.injectResponseToField.apply(this, [responseObj, currentFieldOrder, buttonModel, buttonOrder]);
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    processModalOverride(event) {
        try {
            let buttonModel = [];
            let currentFieldOrder = event.detail.fieldOrder;
            let buttonOrder = event.detail.buttonOrder;
            let isAddressOverrided = event.detail.isAddressOverrided;

            for (const field of this.renderDataModel.dirty) {
                if(field.order === +currentFieldOrder) {
                    buttonModel = field.buttonModel;
                    break;
                }
            }

            for (const button of buttonModel) {
                if(+button.order === +buttonOrder) {
                    button.standardStatus = !isAddressOverrided;
                    break;
                }
            }
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    prepareChangeArray() {
        let existing = false;
        try {
            if(this.tempValue.length > 0) {
                for (const temp of this.tempValue) {
                    if(+temp.order === +arguments[0]) {
                        temp.value = arguments[1];
                        temp.processed = false;
                        existing = true;
                    }
                }
                if(!existing) {
                    this.tempValue.push({ order: arguments[0], value: arguments[1], processed: false });
                }
            }
            else {
                this.tempValue.push({ order: arguments[0], value: arguments[1], processed: false });
            }
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }
    
}