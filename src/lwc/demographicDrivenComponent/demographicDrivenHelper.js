import { pubsub } from 'c/pubsubComponent';
import US1529400SwitchLabel from '@salesforce/label/c.US1529400SwitchLabel';
import US1529400_None from '@salesforce/label/c.US1529400_None';
import US1529400_Declined from '@salesforce/label/c.US1529400_Declined';

export function deduceFieldDisplay(index) {
    this.errorTrace.stack = 'Method: deduceFieldDisplay, file: demographicDrivenHelper.js';
    let baseModel = this.flowRenderObject;
    let tempArray = [];
    for (const field of this.renderDataModel.dirty) {
        if(field.visibility.indexOf(+index) > -1) {
            field.display = true;
            tempArray.push(field);
        }
    }
    this[baseModel.model][baseModel.rightProperty] = [];
    this[baseModel.model][baseModel.rightProperty] = tempArray;
    this.editFieldList = tempArray;
}

export function checkFormValidity() {
    this.errorTrace.stack = 'Method: checkFormValidity, file: demographicDrivenHelper.js';
    let isValid = true;
    if(US1529400SwitchLabel.toUpperCase() === 'Y'){
        let elemArray = this.template.querySelectorAll(`[data-id="${this.emailOptionsList}"]`);
        if(typeof elemArray !== 'undefined') {
            for(const elem of elemArray) {
                this.emailOptionsList = elem.id;
                break;
            }
        }
    }
    for (const field of this.renderDataModel.reactive) {
        let elemArray = this.template.querySelectorAll(`[data-index="${field.order}"]`);
        for (const elem of elemArray) {
            if(elem.nodeName.toLowerCase().includes("combobox") && elem.required && elem.value === '-None-' && !elem.disabled) {
                elem.setCustomValidity('State is required');
            }
            else {
                elem.setCustomValidity('');
            }
            //Email dropdown error control logic
            if(US1529400SwitchLabel.toUpperCase() === 'Y'){
                if(typeof elem.getAttribute('data-typename') !== 'undefined' && 
                        elem.getAttribute('data-typename') !== null && 
                        elem.getAttribute('data-typename') !== '') {
                    if(elem.getAttribute('data-typename') === 'email') {
                        let regExp = new RegExp('^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}|^'+US1529400_None+'$|^'+US1529400_Declined+'$$', "i");
                        let continueOp = (elem.value !== '') ? regExp.test(elem.value) : true;
                        if(!continueOp) {
                            field.emailHasError = true;
                            field.emailErrorClass = 'slds-has-error';
                            isValid = false;
                            if(elem.getAttribute('class').indexOf('slds-has-error') === -1) {
                                elem.setAttribute('class', ( elem.getAttribute('class') + ' slds-has-error' ));
                            }
                        }
                        else {
                            elem.setCustomValidity('');
                            field.emailHasError = false;
                            field.emailErrorClass = '';
                            elem.setAttribute('class', elem.getAttribute('class').replace(' slds-has-error', ''));
                        }
                    }
                }
            }
            if(isValid){
                if(typeof elem.getAttribute("data-valid") !== "undefined" && 
                    elem.getAttribute("data-valid") !== null) {
                    isValid = (elem.getAttribute("data-valid") === 'true');
                }
            }
            if(typeof elem.checkValidity !== "undefined") {
                if(!elem.checkValidity()) {
                    isValid = false;
                }
                if(typeof elem.reportValidity !== "undefined") {
                    elem.reportValidity();
                }
            }
        }
        this.isFormInvalid = !isValid;
        pubsub.publish('toggleNextBtn', { 
            detail: {data: !isValid } 
        });
    }
    if(isValid) {
        let validCount = 0;
        for (const field of this.renderDataModel.reactive) {
            if(field.isSummary) {
                validCount++;
            }
        }
        if(validCount === 0 && this.renderDataModel.reactive.length > 0) {
            pubsub.publish('toggleNextBtn', { 
                detail: {data: isValid } 
            });
        }
    }
}

export function debounce(func, wait, immediate) {
    var timeout;
    return function() {
        var context = this, args = arguments;
        var later = function() {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
    };
}

export function processFieldsForUI() {
    this.errorTrace.stack = 'Method: processFieldsForUI, file: demographicDrivenHelper.js';
    for (const field of this.renderDataModel.dirty) {
        for (const temp of this.tempValue) {
            if(field.order === +temp.order) {
                field.value = temp.value;
                field.isSummary = deduceIsSummary.apply(this, [field]);
                if(!temp.processed) {
                    deduceAddressVerify.apply(this, [field.order, field.isSummary]);
                }
                field.expr.forEach(exp => {
                    if(exp.fnWhen === 'change') {
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
                                    for (const drivenField of this.renderDataModel.dirty) {
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
                temp.processed = true;
                break;
            }
        }
    }
}

export function deduceIsSummary() {
    let currentField = arguments[0];
    let returnValue = false;
    let originalFieldArr = this.renderDataModel.original.filter(oField => oField.order === currentField.order);
    for (const field of originalFieldArr) {
        if(currentField.order === field.order) {
            let oldFieldValue = (field.value === null || field.value === '' || field.value === undefined) ? '' : field.value;
            let currentFieldValue = (currentField.value === null || currentField.value === '' || currentField.value === undefined) ? '' : currentField.value;
            if(oldFieldValue !== currentFieldValue) {
                returnValue = true;
            }
            else {
                returnValue = false;
            }
            break;
        }
    }
    return returnValue;
}

export function deduceAddressVerify() {
    let fieldOrder = arguments[0];
    let isSummary = arguments[1];
    let isSummaried = [];
    let addrFieldOrders = [12, 13, 14, 15, 16];
    if(addrFieldOrders.indexOf(fieldOrder) > -1) {
        for (const field of this.renderDataModel.dirty) {
            if(addrFieldOrders.indexOf(field.order) > -1) {
                isSummaried.push(field.isSummary);
            }
        }
        if(isSummaried.indexOf(true) > -1) {
            pubsub.publish('isAddressBad', {
                detail: { data: isSummary, isAddressVerified: !isSummary }
            });
        }
        else {
            pubsub.publish('isAddressBad', {
                detail: { data: !isSummary, isAddressVerified: isSummary }
            });
        }
    }
}