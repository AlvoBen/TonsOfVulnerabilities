import { pubsub } from 'c/coachPubSubComponent';
import { NavigationMixin } from 'lightning/navigation';
import { LightningElement, api } from 'lwc';

const ssnFormat = "XXX-XX-";
const ssnNumberFormat = "\\([0-9]{3}\\)-[0-9]{2}-[0-9]{4}";
const ssnSecondaryFormat = "[0-9]{9,9}";
const ssnFormatString = "XXX-XX-XXXX";
const ssnFormatExpr = "[0-9]{3}-[0-9]{2}-[0-9]{4}";
const phoneSecondaryFormat = "[0-9]{10,10}";
const phoneFormat = "\\([0-9]{3}\\) [0-9]{3}-[0-9]{4}";
const phoneFormatString = "(XXX) XXX-XXXX";
const zipcodeFormat = "^[0-9]{5,5}$|^[0-9]{9,9}$";
const phoneExtFormat = "^[0-9]{0,4}$";

export function isJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

const showToastEvent = function (title, message) {
    try {
        pubsub.publish('showError', {
            detail: { errorMessage: message, errorTitle: title }
        });
        pubsub.publish('toggleLoader', {
            detail: { showLoader: false }
        });
    }
    catch (e) {
        console.log(e.message);
    }
}

const ssnEncrypter = function (ssnInput) {
    let ssnNumber, ssnLength, ssnLastDigits, ssnFormattedString = "";
    try {
        if (!ssnInput) {
            return ssnFormattedString;
        }
        ssnNumber = ssnInput.toString();
        ssnLength = ssnNumber.length;
        ssnLastDigits = ssnNumber.substr(ssnLength - 4);
        ssnFormattedString = ssnFormat + ssnLastDigits;
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return ssnFormattedString;
}

const dateDeformatter = function (dateString) {
    let dDate, date, newDate, morphedDate = "";
    try {
        dDate = new Date(dateString);
        date = dDate.getDate();
        const offset = dDate.getTimezoneOffset();
        dDate = new Date(dDate.getTime() + (offset * 60 * 1000));
        newDate = dDate.getDate(dDate);
        if (date < newDate) {
            dDate.setDate(dDate.getDate() - 1);
        }
        else if (date > newDate) {
            dDate.setDate(dDate.getDate() + 1);
        }
        morphedDate = dDate.toISOString().split('T')[0];
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return morphedDate;
}

const formatString = function (mask, inputString) {
    let formattedString = "";
    try {
        let s = '' + inputString;
        for (let im = 0, is = 0; im < mask.length && is < s.length; im++) {
            formattedString += mask.charAt(im) === 'X' ? s.charAt(is++) : mask.charAt(im);
        }
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return formattedString;
}

const ssnFormatter = function (ssnInput) {
    let ssnFormattedString = '';
    try {
        if (typeof ssnInput !== 'undefined' && ssnInput !== null && ssnInput !== '') {
            if (!ssnInput.match(ssnNumberFormat) && ssnInput !== "") {
                if (ssnInput.match(ssnSecondaryFormat)) {
                    ssnFormattedString = formatString(ssnFormatString, ssnInput);
                }
                else {
                    ssnFormattedString = ssnInput;
                }

            }
        }
        else {
            ssnFormattedString = ssnInput;
        }
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return ssnFormattedString;
}

const phoneFormatter = function (phoneString) {
    let formattedValue = "";
    try {
        if (typeof phoneString !== 'undefined' && phoneString !== null && phoneString !== '') {
            if (!phoneString.match(phoneFormat) && phoneString !== "") {
                if (phoneString.match(phoneSecondaryFormat)) {
                    formattedValue = formatString(phoneFormatString, phoneString);
                }
                else {
                    formattedValue = phoneString;
                }

            }
            else {
                formattedValue = phoneString;
            }
        }
        else {
            formattedValue = phoneString;
        }
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return formattedValue;
}

const stringConcatenator = function (inputArg) {
    var returnValue = "";
    try {
        inputArg.forEach(item => {
            if (item !== "N/A") {
                returnValue = returnValue + item;
            }
        });
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return returnValue;
}

const phoneRetriever = function (inputPhoneObj, phoneType) {
    let returnValue = "";
    var toBeItem = {};
    try {
        if (typeof inputPhoneObj !== "undefined") {
            if (Array.isArray(inputPhoneObj)) {
                inputPhoneObj.forEach(item => {
                    if (item.Type.toLowerCase() === phoneType.toLowerCase()) {
                        toBeItem = item;
                    }
                });
            }
        }

        if (Object.getOwnPropertyNames(toBeItem).length > 0) {
            returnValue = phoneFormatter(toBeItem.PhoneNumber);
        }
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return returnValue;
}

const emailRetriever = function (inputEmailObj, emailType) {
    let returnValue = "";
    var toBeItem = {};
    try {
        if (typeof inputEmailObj !== "undefined") {
            if (Array.isArray(inputEmailObj)) {
                inputEmailObj.forEach(item => {
                    if (item.Type.toLowerCase() === emailType.toLowerCase()) {
                        toBeItem = item;
                    }
                });
            }
        }

        if (Object.getOwnPropertyNames(toBeItem).length > 0) {
            returnValue = toBeItem.Address;
        }
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return returnValue;
}

const addressRetriever = function (inputAddressObj, addressType) {
    var returnValue = "";
    var toBeItem = {};
    try {
        if (typeof inputAddressObj !== "undefined") {
            if (Array.isArray(inputAddressObj)) {
                inputAddressObj.forEach(item => {
                    if (item.Type.toLowerCase() === addressType.toLowerCase()) {
                        toBeItem = item;
                    }
                });
            }

            if (Object.getOwnPropertyNames(toBeItem).length > 0) {
                let street = (toBeItem.AddressLine1 !== "undefined" &&
                    toBeItem.AddressLine1 !== "" &&
                    toBeItem.AddressLine1 !== null) ? toBeItem.AddressLine1 : "N/A";
                let city = (toBeItem.City !== "undefined" &&
                    toBeItem.City !== "" &&
                    toBeItem.City !== null) ? toBeItem.City : "N/A";
                let state = (toBeItem.StateCode !== "undefined" &&
                    toBeItem.StateCode !== "" &&
                    toBeItem.StateCode !== null) ? toBeItem.StateCode : "N/A";
                let postalCode = (toBeItem.ZipCode !== "undefined" &&
                    toBeItem.ZipCode !== "" &&
                    toBeItem.ZipCode !== null) ? toBeItem.ZipCode : "N/A";
                let country = (toBeItem.County !== "undefined" &&
                    toBeItem.County !== "" &&
                    toBeItem.County !== null) ? toBeItem.County : "N/A";

                returnValue = stringConcatenator(
                    [
                        (street !== "N/A") ? street : "N/A",
                        (street !== "N/A") ? "\n" : "N/A",
                        (city !== "N/A") ? city : "N/A",
                        (city !== "N/A") ? ", " : "N/A",
                        (state !== "N/A") ? state : "N/A",
                        (state !== "N/A") ? " " : "N/A",
                        (postalCode !== "N/A") ? postalCode : "N/A",
                        (postalCode !== "N/A") ? "\n" : "N/A",
                        (country !== "N/A") ? country : "N/A"
                    ]
                );
            }
        }
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return returnValue;
}

const ageCalculator = function (birthday) {
    var age = "";
    try {
        if (!birthday) {
            return age;
        }
        let currDate = new Date();
        let birthDate = new Date(birthday);
        age = currDate.getFullYear() - birthDate.getFullYear();
        age = age.toString();
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return age;
}

const isObjectsEqual = function (a, b) {
    var returnValue = true;
    try {
        let aProps = Object.getOwnPropertyNames(a);
        let bProps = Object.getOwnPropertyNames(b);

        if (aProps.length !== bProps.length) {
            returnValue = false;
        }

        for (let i = 0; i < aProps.length; i++) {
            let propName = aProps[i];
            if (a[propName] !== b[propName]) {
                returnValue = false;
            }
        }
    }
    catch (e) {
        showToastEvent("JavaScript Error!", e.message);
    }
    return returnValue;
}

const objectMapper = function (obj, targetProp, finalResults, fn) {

    function getObject(theObject) {
        if (theObject instanceof Array) {
            for (let i = 0; i < theObject.length; i++) {
                getObject(theObject[i]);
            }
        }
        else {
            for (let prop in theObject) {
                if (theObject.hasOwnProperty(prop)) {
                    if (prop === targetProp) {
                        fn.apply(null, [theObject, targetProp, finalResults]);
                    }
                    if (theObject[prop] instanceof Object || theObject[prop] instanceof Array) {
                        getObject(theObject[prop]);
                    }
                }
            }
        }
    }
    getObject(obj);
}

const objectMapperSimple = function (obj, targetProp, resultObj, resultProp) {
    let left;
    let right;

    if (typeof arguments[4] !== "undefined" && typeof arguments[5] !== "undefined") {
        left = arguments[4];
        right = arguments[5];
    }

    function getObject(theObject) {
        if (theObject instanceof Array) {
            for (let i = 0; i < theObject.length; i++) {
                getObject(theObject[i]);
            }
        }
        else {
            for (let prop in theObject) {
                if (theObject.hasOwnProperty(prop)) {
                    if (prop === targetProp) {
                        if (typeof left !== "undefined" && typeof right !== "undefined") {
                            if (theObject[left] === right) {
                                switch (right.toLowerCase()) {
                                    case "platform":
                                        if (typeof theObject.PersonIdentifier !== 'undefined') {
                                            if (typeof theObject.PersonIdentifier.PersonIdentifierKey !== 'undefined') {
                                                if (theObject.PersonIdentifier.PersonIdentifierKey.Source === resultObj.source) {
                                                    resultObj[resultProp] = theObject[prop];
                                                }
                                            }
                                        }
                                        break;
                                    case "residential":
                                        if (typeof theObject.PersonIdentifier !== 'undefined') {
                                            if (typeof theObject.PersonIdentifier.PersonIdentifierKey !== 'undefined') {
                                                if (theObject.PersonIdentifier.PersonIdentifierKey.Source === resultObj.source) {
                                                    resultObj[resultProp] = theObject[prop];
                                                }
                                            }
                                        }
                                        break;
                                    case "home":
                                        resultObj[resultProp] = theObject[prop];
                                        break;
                                    default:
                                        resultObj[resultProp] = theObject[prop];
                                        break;
                                }
                            }
                        }
                        else {
                            resultObj[resultProp] = theObject[prop];
                        }
                    }
                    if (theObject[prop] instanceof Object || theObject[prop] instanceof Array) {
                        getObject(theObject[prop]);
                    }
                }
            }
        }
    }
    getObject(obj);
}

export function objectMapperSimpleReturn(obj, targetProp, resultObj, resultProp, letfProp, rightProp, resAddrArray) {
    let left;
    let right;

    if (typeof arguments[4] !== "undefined" && typeof arguments[5] !== "undefined") {
        left = arguments[4];
        right = arguments[5];
    }

    function getObject(theObject) {
        if (theObject instanceof Array) {
            for (let i = 0; i < theObject.length; i++) {
                getObject(theObject[i]);
            }
        }
        else {
            for (let prop in theObject) {
                if (theObject.hasOwnProperty(prop)) {
                    if (prop === targetProp) {
                        if (typeof left !== "undefined" && typeof right !== "undefined") {
                            if (theObject[left] === right) {
                                switch (right.toLowerCase()) {
                                    case "platform":
                                        if (typeof theObject.PersonIdentifier !== 'undefined') {
                                            if (typeof theObject.PersonIdentifier.PersonIdentifierKey !== 'undefined') {
                                                if (theObject.PersonIdentifier.PersonIdentifierKey.Source === resultObj.source) {
                                                    resultObj[resultProp] = theObject[prop];
                                                }
                                            }
                                        }
                                        break;
                                    case "residential":
                                        if (resAddrArray.length > 0) {
                                            for (const resAddr of resAddrArray) {
                                                if (JSON.stringify(resAddr) !== JSON.stringify(theObject)) {
                                                    resAddrArray.push(theObject);
                                                    break;
                                                }
                                            }
                                        }
                                        else {
                                            resAddrArray.push(theObject);
                                        }
                                        if (typeof theObject.PersonIdentifier !== 'undefined') {
                                            if (typeof theObject.PersonIdentifier.PersonIdentifierKey !== 'undefined') {
                                                if (theObject.PersonIdentifier.PersonIdentifierKey.Source === resultObj.source) {
                                                    resultObj[resultProp] = theObject[prop];
                                                }
                                            }
                                        }
                                        break;
                                    case "home":
                                        resultObj[resultProp] = theObject[prop];
                                        break;
                                    default:
                                        resultObj[resultProp] = theObject[prop];
                                        break;
                                }
                            }
                        }
                        else {
                            resultObj[resultProp] = theObject[prop];
                        }
                    }
                    if (theObject[prop] instanceof Object || theObject[prop] instanceof Array) {
                        getObject(theObject[prop]);
                    }
                }
            }
        }
    }
    getObject(obj);
}

const debounce = function (func, wait, immediate) {
    var timeout;
    return function () {
        var context = this, args = arguments;
        var later = function () {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
    };
};

export default class crmserviceHelper extends NavigationMixin(LightningElement) {
    navigateToViewAccountDetail(recordID, objectAPI, action) {
        this[NavigationMixin.Navigate]({
            type: 'standard__recordPage',
            attributes: {
                recordId: recordID,
                objectApiName: objectAPI,
                actionName: action
            },
        });
    }
}

export {
    dateDeformatter,
    phoneFormatter,
    isObjectsEqual,
    ageCalculator,
    addressRetriever,
    emailRetriever,
    phoneRetriever,
    ssnEncrypter,
    ssnFormat,
    phoneFormat,
    phoneFormatString,
    zipcodeFormat,
    phoneExtFormat,
    showToastEvent,
    objectMapper,
    objectMapperSimple,
    debounce,
    phoneSecondaryFormat,
    stringConcatenator,
    ssnFormatter,
    ssnFormatExpr
};