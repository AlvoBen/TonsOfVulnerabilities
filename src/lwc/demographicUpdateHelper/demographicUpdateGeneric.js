import { SSN, Electronic, Phone, Address, AddressVerificationMap, OldMemberDataMap, Members, MemberCriterion, PlatformMap,
    updateMemberRequestModel } from './demographicUpdateModel';
import getGenericsForRequest from '@salesforce/apex/DemographicUpdateController_LTNG_C_HUM.getGenericsForRequest';
import retrieveUpdateResponse from '@salesforce/apex/UpdateMemberDemographics_D_HUM.retrieveUpdateResponse';
import processTemplateData from '@salesforce/apex/DemographicUpdateTemplateInsertion_D_HUM.processTemplateData';
import retrieveSearchResponse from '@salesforce/apex/SearchMember_D_HUM.retrieveSearchResponse';
import { pubsub } from 'c/pubsubComponent';
import { showToastEvent } from 'c/crmserviceHelper';
import MBESearchMemberErrorLabel from '@salesforce/label/c.MBESearchMemberErrorLabel';
import MBESearchMemberNotFound from '@salesforce/label/c.MBESearchMemberNotFound';
import US1645972SwitchLabel from '@salesforce/label/c.US1645972SwitchLabel';
import MMPAddressLengthCap from '@salesforce/label/c.MMPAddressLengthCap';
import MMPTemplateAddressFieldNames from '@salesforce/label/c.MMPTemplateAddressFieldNames';
import MMPTemplateList from '@salesforce/label/c.MMPTemplateList';
import GBO_Failure_Message from '@salesforce/label/c.GBO_Failure_Message';
import GBO_Success_Message from '@salesforce/label/c.GBO_Success_Message';
import RSO_Success_Message from '@salesforce/label/c.RSO_Success_Message';
import RSO_Failure_Message from '@salesforce/label/c.RSO_Failure_Message';
import RSO_Future_Address_Only_Msg from '@salesforce/label/c.RSO_Future_Address_Only_Msg';
import RSO_Future_Failure_Message from '@salesforce/label/c.RSO_Future_Failure_Message';
import RSO_Future_Success_Message from '@salesforce/label/c.RSO_Future_Success_Message';
import US0015656SwitchLabel from '@salesforce/label/c.US0015656SwitchLabel';
import US0015656Scope from '@salesforce/label/c.US0015656Scope';
import US1905759_SwitchLabel from '@salesforce/label/c.US1905759_SwitchLabel';
import US1905759_SwitchScope from '@salesforce/label/c.US1905759_SwitchScope';
import processTemplateDataAfterResponse from '@salesforce/apex/DemographicUpdateTemplateInsertion_D_HUM.processTemplateDataAfterResponse';
import US2568799SwitchLabel from '@salesforce/label/c.US2568799SwitchLabel'; 
import US3431258SwicthLabel from '@salesforce/label/c.US3431258SwicthLabel';

export function concatAddress() {
    let leftAddress = arguments[0];
    let rightAddressOrder = arguments[1];
    let rightAddressProperty = arguments[2];
    let modeName = arguments[3];
    let rightAddress = '';
    let concatenatedAddress = '';

    for (const key in this.updatedDataModel) {
        if (this.updatedDataModel.hasOwnProperty(key)) {
            if(key === modeName) {
                let dataArray = this.updatedDataModel[key];
                for (const field of dataArray) {
                    if(field.order === +rightAddressOrder) {
                        rightAddress = field[rightAddressProperty];
                        break;
                    }
                }
                break;
            }
        }
    }

    return (rightAddress === '') ? leftAddress : (leftAddress + ' ' + rightAddress);
}

export function deriveValue() {
    let fieldValue = arguments[0];
    return fieldValue;
}

export function generateDateString() {
    let currDate = new Date();
    //currDate = new Date(currDate.toLocaleDateString("en-us", {timeZone: "America/New_York"}));
    currDate.setTime(currDate.getTime() + currDate.getTimezoneOffset() * 60 * 1000);
    let offset = -240;
    let estDate = new Date(currDate.getTime() + offset * 60 * 1000);
    let currDateString = `${estDate.getFullYear()}-${((estDate.getMonth() + 1) < 10) ? '0'+(estDate.getMonth()+1) : estDate.getMonth() + 1}-${(estDate.getDate() < 10) ? '0'+estDate.getDate() : estDate.getDate()}`;
    return currDateString;
}

export function deriveRequestValue() {
    let fieldValue = arguments[0];
    let fieldOrder = arguments[1];
    let memberNodeName = arguments[2];
    let nodePropertyName = arguments[3];
    let currDate = new Date();
    let currDateString = `${currDate.getUTCFullYear()}-${(currDate.getUTCMonth() < 10) ? '0'+(currDate.getUTCMonth()+1) : currDate.getUTCMonth()}-${(currDate.getUTCDate() < 10) ? '0'+currDate.getUTCDate() : currDate.getUTCDate()}`;

    this[arguments[4]][memberNodeName][nodePropertyName] = fieldValue;
    this[arguments[4]][memberNodeName].changeindicator = 'Y';
    this[arguments[4]][memberNodeName].effectivedate = generateDateString.apply(this);  //currDateString;
    this[arguments[4]][memberNodeName].enddate = '9999-12-31';
}

export function deriveBirthdateRequestValue() {
    let fieldValue = arguments[0];
    let fieldOrder = arguments[1];
    let memberNodeName = arguments[2];
    let nodePropertyName = arguments[3];
    let currDate = new Date();
    let currDateString = `${currDate.getUTCFullYear()}-${(currDate.getUTCMonth() < 10) ? '0'+(currDate.getUTCMonth()+1) : currDate.getUTCMonth()}-${(currDate.getUTCDate() < 10) ? '0'+currDate.getUTCDate() : currDate.getUTCDate()}`;
    let fieldValueArray = fieldValue.split('/');

    this[arguments[4]][memberNodeName][nodePropertyName] = `${fieldValueArray[2]}-${fieldValueArray[0]}-${fieldValueArray[1]}`;
    this[arguments[4]][memberNodeName].changeindicator = 'Y';
    this[arguments[4]][memberNodeName].effectivedate = generateDateString.apply(this);  //currDateString;
    this[arguments[4]][memberNodeName].enddate = '9999-12-31';
}

export function deriveSSNRequestValue() {
    let fieldValue = arguments[0];
    let fieldOrder = arguments[1];
    let memberNodeName = arguments[2];
    let nodePropertyName = arguments[3];
    let ssnClass = new SSN();
    let ssnObj = ssnClass.generateSSNModel.apply(this);
    let propertyName = arguments[5];

    ssnObj[propertyName] = fieldValue.replace(/\D/g, '');
    ssnObj.changeindicator = 'Y';
    this[arguments[4]][memberNodeName][nodePropertyName] = ssnObj;
}

export function deriveElectronicRequestValue() {
    let fieldValue = arguments[0];
    let fieldOrder = arguments[1];
    let memberNodeName = arguments[2];
    let nodePropertyEmailName = arguments[3];
    let nodePropertyTypeName = arguments[5];
    let nodePropertyTypeValue = arguments[6];
    let nodeKeyName =  arguments[7];
    let currDate = new Date();
    let currDateString = `${currDate.getUTCFullYear()}-${(currDate.getUTCMonth() < 10) ? '0'+(currDate.getUTCMonth()+1) : currDate.getUTCMonth()}-${(currDate.getUTCDate() < 10) ? '0'+currDate.getUTCDate() : currDate.getUTCDate()}`;

    let electronicClass = new Electronic();
    let electronicObj = electronicClass.generateElectronic.apply(this);
    electronicObj[nodePropertyEmailName] = (fieldValue === '') ? 'none@declined.com' : fieldValue;
    electronicObj[nodePropertyTypeName] = nodePropertyTypeValue;
    electronicObj.changeindicator = 'Y';
    electronicObj.effectivedate = generateDateString.apply(this);  //currDateString;
    electronicObj.enddate = '9999-12-31';
    this[arguments[4]][memberNodeName][nodeKeyName].push(electronicObj);
}

export function derivePhoneRequestValue() {
    let fieldValue = arguments[0];
    let fieldOrder = arguments[1];
    let memberNodeName = arguments[2];
    let nodePropertyNumberName = arguments[3];
    let nodePropertyTypeName = arguments[5];
    let nodePropertyTypeValue = arguments[6];
    let nodeKeyName =  arguments[7];
    let currDate = new Date();
    let currDateString = `${currDate.getUTCFullYear()}-${(currDate.getUTCMonth() < 10) ? '0'+(currDate.getUTCMonth()+1) : currDate.getUTCMonth()}-${(currDate.getUTCDate() < 10) ? '0'+currDate.getUTCDate() : currDate.getUTCDate()}`;

    let phoneClass = new Phone();
    let phoneObj = phoneClass.generatePhone.apply(this);
    phoneObj[nodePropertyNumberName] =(fieldValue === '') ? '9999999999' : fieldValue.replace(/\D/g, '');
    phoneObj[nodePropertyTypeName] = nodePropertyTypeValue;
    phoneObj.changeindicator = 'Y';
    phoneObj.effectivedate = generateDateString.apply(this);  //currDateString;
    phoneObj.enddate = '9999-12-31';
    this[arguments[4]][memberNodeName][nodeKeyName].push(phoneObj);
}

export function derivePhoneExtRequestValue() {
    let fieldValue = arguments[0];
    let fieldOrder = arguments[1];
    let memberNodeName = arguments[2];
    let nodePropertyExtnName = arguments[3];
    let nodePropertyTypeName = arguments[5];
    let nodePropertyTypeValue = arguments[6];
    let nodeKeyName = arguments[7];

    for(const phone of this[arguments[4]][memberNodeName][nodeKeyName]) {
        if(phone.type === nodePropertyTypeValue) {
            phone[nodePropertyExtnName] = fieldValue;
            break;
        }
    }
}

export function deriveAddressRequestValue() {
    let fieldOrder = arguments[0];
    let targetKeyName = arguments[1];
    let memberNodeName = arguments[2];
    let nodeKeyName = arguments[4];
    let nodePropertyTypeName = arguments[5];
    let nodePropertyTypeValue = arguments[6];
    let typeName = (nodePropertyTypeValue === "03") ? 'residential' : 'mailing';
    let key = arguments[7];
    let currDate = new Date();
    let currDateString = `${currDate.getUTCFullYear()}-${(currDate.getUTCMonth() < 10) ? '0'+(currDate.getUTCMonth()+1) : currDate.getUTCMonth()}-${(currDate.getUTCDate() < 10) ? '0'+currDate.getUTCDate() : currDate.getUTCDate()}`;

    let addressClass = new Address();
    let addressObj = addressClass.generateAddress.apply(this);

    let mapClass = new AddressVerificationMap();
    let mapObject = mapClass.generateMap.apply(this);

    let line1Value = '';
    let line2Value = '';
    let line1Index;
    let line2Index;

    for (const field of this.updatedDataModel[key]) {
        if(fieldOrder.indexOf(field.order) > -1) {
            if(targetKeyName[fieldOrder.indexOf(field.order)] === 'zipcode') {
                if(field.value.length === 9) {
                    addressObj[targetKeyName[fieldOrder.indexOf(field.order)]] = field.value.substring(0, 5);
                    addressObj.zipcodeplus = field.value.substring(5, 9);
                }
                else {
                    addressObj[targetKeyName[fieldOrder.indexOf(field.order)]] = field.value;
                }
            }
            else {
                if(targetKeyName[fieldOrder.indexOf(field.order)] === 'line1') {
                    line1Value = field.value;
                    line1Index = fieldOrder.indexOf(field.order);
                }
                if(targetKeyName[fieldOrder.indexOf(field.order)] === 'line2') {
                    line2Value = field.value;
                    line2Index = fieldOrder.indexOf(field.order);
                }
                addressObj[targetKeyName[fieldOrder.indexOf(field.order)]] = field.value;
            }
        }
    }

    if(line2Value !== '' && line2Value !== null && line2Value !== undefined && 
        MMPTemplateList.toUpperCase().includes(this.templateName.toUpperCase())) {
        addressObj[targetKeyName[line1Index]] = line2Value;
        addressObj[targetKeyName[line2Index]] = line1Value;
    }

    addressObj[nodePropertyTypeName] = nodePropertyTypeValue;
    addressObj.changeindicator = 'Y';
    addressObj.effectivedate = generateDateString.apply(this);  //currDateString;
    addressObj.enddate = '9999-12-31';
    addressObj.status = 'new';

    let avObj = this.addressVerificationModel[key][typeName].data;
    let avStatus = this.addressVerificationModel[key][typeName].status;
    let stdStatus = this.addressVerificationModel[key][typeName].standardStatus;
    addressObj.processedbystandardizationmodule = (stdStatus) ? 'Y' : 'N';

    if(avStatus) {
        for(let mKey in mapObject) {
            if(mapObject.hasOwnProperty(mKey)) {
                if(avObj.hasOwnProperty(mapObject[mKey])) {
                    addressObj[mKey] = avObj[mapObject[mKey]];
                }
            }
        }
    }

    if(US1905759_SwitchLabel.toUpperCase() === 'Y' && addressObj.processedbystandardizationmodule === 'Y') {
        //GBO
        if(US1905759_SwitchScope.indexOf(this.templateName) > -1) {
            addressObj.countyname = avObj.CountyName;
            addressObj.countycode = avObj.CountyID;
        }
        //RSO
        else {
            addressObj.countycode = avObj.CountyID;
        }
    }

    if(addressObj.processedbystandardizationmodule === 'N') {
        addressObj.countycode = '';
    }
    
    this[arguments[3]][memberNodeName][nodeKeyName].push(addressObj);
}

export function deriveRequestUpdateType() {
    let fieldValue = arguments[0];
    let fieldOrder = arguments[1];
    let memberNodeName = arguments[2];
    let nodePropertyName = arguments[3];
    let requestTypeValue = arguments[5];

    if(fieldValue) {
        for (const criterion of this[arguments[4]][memberNodeName]) {
            if(criterion[nodePropertyName].indexOf(requestTypeValue) === -1) {
                criterion[nodePropertyName].push(requestTypeValue);
            }
        }
    }
}

export function mapOriginalMemberData() {
    let oldMap = new OldMemberDataMap();
    let oldObj = oldMap.generateMap.apply(this);

    for(let oKey in oldObj) {
        if(oldObj.hasOwnProperty(oKey)) {
            for(let key in this.member) {
                if(this.member.hasOwnProperty(key)) {
                    if(key === oKey) {
                        for (const field of this.displayDataModel) {
                            if(field.order === oldObj[oKey]) {
                                this.member[key] = (field.order === 7) ? field.ssnValue : field.value;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

}

export function procureMiddleName() {
    let canProceed = false;
    let isNameUpdated = false;
    let resultData = this.eligibleInfoList.data;
    let memberId = '';
    let platformValue = '';
    let enterpriseId = '';

    for(let oKey in this.member) {
        if(this.member.hasOwnProperty(oKey) && oKey === 'membercriterion') {
            for (const criterion of this.member.membercriterion) {
                if(criterion.updaterequesttype.indexOf('BIOGRAPHICS') > -1) {
                    if(this.member.biographics.firstname !== '') {
                        isNameUpdated = true;
                        for(let key in resultData) {
                            if(resultData.hasOwnProperty(key)) {
                                if(key === 'self') {
                                    for(const oItem of resultData[key]) {
                                        if(oItem.Id === this.policyMemberId) {
                                            memberId = oItem.Name;
                                            platformValue = oItem.Policy_Platform__c;
                                            enterpriseId = oItem.Member__r.Enterprise_ID__c;
                                            canProceed = true;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            break;
        }
    }

    if(isNameUpdated && canProceed) {
        retrieveSearchResponse({ memberId: memberId, platformValue: platformValue })
            .then(result => {
                let data = (result) ? JSON.parse(result) : { calloutErrored: true, serviceCalloutError: MBESearchMemberErrorLabel };
                if(data.calloutErrored) {
                    showToastEvent.apply(this, ['fetchMiddleName APEX Error', data.serviceCalloutError]);
                }
                else if(data.searchResponseDTO === null || data.searchResponseDTO === undefined || data.searchResponseDTO === '') {
                    showToastEvent.apply(this, ['fetchMiddleName APEX Error', MBESearchMemberErrorLabel]);
                }
                else {
                    for(const searchMember of data.searchResponseDTO.SearchMemberResponse.MemberList) {
                        if(searchMember.MasterId === enterpriseId) {
                            this.middleName = searchMember.MiddleName;
                            this.middleNameFound = true;
                            break;
                        }
                    }
                }
                
                if(!this.middleNameFound) {
                    showToastEvent.apply(this, ['fetchMiddleName APEX Error', MBESearchMemberNotFound]);
                }
                else {
                    procureEligibleMemberDetailsList.apply(this);
                }

            })
            .catch(error => {
                showToastEvent.apply(this, ['fetchMiddleName APEX Error', error.message]);
            })
    }
    else if(!isNameUpdated && !canProceed) {
        procureEligibleMemberDetailsList.apply(this);
    }
}

//Mega update block - starts
export function checkProcumentStatus(event) {
    let canProceed = true;
    this.procurementStatus[event.detail.key] = event.detail.value;
    for(let key in this.procurementStatus) {
        if(this.procurementStatus.hasOwnProperty(key)) {
            if(this.procurementStatus[key] !== 'completed') {
                canProceed = false;
            }
        }
    }
    if(canProceed) {
        if(US0015656SwitchLabel.toUpperCase() === 'Y') {
            let isProceed = 
                (this.isDateInFuture && US0015656Scope.toUpperCase().indexOf(this.templateName) > -1 && deduceIfOnlyAddressUpdate.apply(this)) ?
                false : true;
            if(this.primaryUpdReqModel.length > 0 && isProceed) {
                for (let i = 0; i < this.primaryUpdReqModel.length; i++) {
                    if(i === this.primaryUpdReqModel.length - 1) {
                        triggerDepUpdateService.apply(this, [this.primaryUpdReqModel[i], true, true, i, 'sequenceUpdateStatePrimary']);
                    }
                    else {
                        triggerDepUpdateService.apply(this, [this.primaryUpdReqModel[i], false, true, i, 'sequenceUpdateStatePrimary']);
                    }
                }
            }

            if(US2568799SwitchLabel.toUpperCase() === 'N'){
            triggerTemplateSave.apply(this, [true, true]);
            }

            if(deduceIfOnlyAddressUpdate.apply(this) && this.isDateInFuture) {
                toggleAddressMessages.apply(this, [[this.primaryMemberName], [false], false, true]);
            }
        }
        else {
            if(this.primaryUpdReqModel.length > 0) {
                for (let i = 0; i < this.primaryUpdReqModel.length; i++) {
                    if(i === this.primaryUpdReqModel.length - 1) {
                        triggerDepUpdateService.apply(this, [this.primaryUpdReqModel[i], true, true, i, 'sequenceUpdateStatePrimary']);
                    }
                    else {
                        triggerDepUpdateService.apply(this, [this.primaryUpdReqModel[i], false, true, i, 'sequenceUpdateStatePrimary']);
                    }
                }
            }
        }
    }
}

export function triggerDependentsSubmit(event) {
    let canProceed = event.detail.value;
    if(canProceed) {
        if(this.dependentUpdReqModel.length > 0) {
            for (let i = 0; i < this.dependentUpdReqModel.length; i++) {
                if(i === this.dependentUpdReqModel.length - 1) {
                    triggerDepUpdateService.apply(this, [this.dependentUpdReqModel[i], true, false, i, 'sequenceUpdateStateDependent']);
                }
                else {
                    triggerDepUpdateService.apply(this, [this.dependentUpdReqModel[i], false, false, i, 'sequenceUpdateStateDependent']);
                }
            }
        }
        else {
            this.dispatchEvent(new CustomEvent('toggleDirty', {
                detail: { data:  false},
                bubbles: true,
                composed: true,
            }));
        }
    }
    else {
        this.dispatchEvent(new CustomEvent('toggleDirty', {
            detail: { data:  false},
            bubbles: true,
            composed: true,
        }));
    }
}

export function triggerProcessFinish(e) {
    let triggerFinish = true;
    for (const item of this[e.detail.container]) {
        if(item.status === 'started') {
            triggerFinish = false;
        }
    }
    if(triggerFinish) {
        finishMessages.apply(this, [e.detail.isPrimary, e.detail.loopCount]);
    }
}

export function finishMessages() {
    let data = '';
    let isPrimary = arguments[0];
    let loopCount = arguments[1];
    let success = [];
    let mmName = [];
    let isTrigger = false;
    for (const msgMap of this.platformMemberMsgMap) {
        for(let key in msgMap) {
            if(msgMap.hasOwnProperty(key)) {
                if(msgMap[key].primary === isPrimary) {
                    success.push(msgMap[key].success);
                    mmName.push(msgMap[key].name);
                    isTrigger = true;
                }
            }
        }   
    }
    if(isTrigger) {
        if(US0015656SwitchLabel.toUpperCase() === 'Y') {
            toggleAddressMessages.apply(this, [mmName, success, isPrimary, deduceIfOnlyAddressUpdate.apply(this)]);
        }
        else {
            for(let i = 0; i < mmName.length; i++) {
                if(!success[i]) {
                 if(this.templateName === 'RSO') {
                        data = '<div class="member-name">' + mmName[i] + '</div>' + 
                                '<div>Updates made were not successful. Make updates in <a>CI/PAPI</a>.</div>' +
                                '<div>After closing the tab, close CRM case per established process.</div>';
                    }
                    else {
                        data =  '<div class="member-name">' + mmName[i] + '</div>' + 
                                '<div>Updates were not successful.</div>' +
                                '<div>After closing the tab, refer to Mentor for next steps.</div>';
                    }
                }
                else {
                    data = '<div class="member-name">' + mmName[i] + '</div>' + 
                            '<div>Updates made were successful.</div>' +
                            '<div>After closing the tab, close CRM case per established process.</div>';
                }
                if(isPrimary) {
                    pubsub.publish('launchFinishModal', {
                        detail: { display: true, initial: false, data: data, success: success[i], idRequest: this.idCardRequired, template: this.templateName }
                    });
                }
                else {
                    pubsub.publish('launchFinishModal', {
                    detail: { display: true, initial: false, data: data, success: success[i] }
                    });
                }
            }
        }
        isTrigger = false;
    }
}

export function toggleAddressMessages(memNameArray, successArray, isPrimary, isOnlyAddress) {
    let data = null;
    function formatCustomLabel(format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    }
    for(let i = 0; i < memNameArray.length; i++) {
        if(!successArray[i]) {
            let responseStatusTemp = 'FAILED';
            if(this.templateName === 'RSO') {

                if(this.isDateInFuture && isOnlyAddress){
                    if(US2568799SwitchLabel.toUpperCase() === 'Y') {
                        triggerTemplateSaveAfterResponse.apply(this, [true, true, this.mmpUpdateResponseDetails, responseStatusTemp]);
                    }
                    this.dispatchEvent(new CustomEvent('toggleDirty', {
                        detail: { data:  false},
                        bubbles: true,
                        composed: true,
                    }));
                }
                data = (this.isDateInFuture) ? (isOnlyAddress) ? 
                        formatCustomLabel(RSO_Future_Address_Only_Msg, [memNameArray[i]]) :
                        formatCustomLabel(RSO_Future_Failure_Message, [memNameArray[i]]) :
                        formatCustomLabel(RSO_Failure_Message, [memNameArray[i]]);
            }
            else if(this.templateName === 'GBO'){
                data = formatCustomLabel(GBO_Failure_Message, [memNameArray[i]]);
                if(US2568799SwitchLabel.toUpperCase() === 'Y') {
                    triggerTemplateSaveAfterResponse.apply(this, [true, true, this.mmpUpdateResponseDetails, responseStatusTemp]);
                }
            }
        }
        else {
            let responseStatusTemp = 'SUCCESS';
            if(this.templateName === 'RSO') {
                data = (this.isDateInFuture) ? 
                        formatCustomLabel(RSO_Future_Success_Message, [memNameArray[i]]) :
                        formatCustomLabel(RSO_Success_Message, [memNameArray[i]]);
            }
            else if(this.templateName === 'GBO') {
                data = formatCustomLabel(GBO_Success_Message, [memNameArray[i]]);
                if(US2568799SwitchLabel.toUpperCase() === 'Y') {
                    triggerTemplateSaveAfterResponse.apply(this, [true, true, this.mmpUpdateResponseDetails, responseStatusTemp]);
                }
            }
        }
        if(isPrimary) {
            pubsub.publish('launchFinishModal', {
                detail: { display: true, initial: false, data: data, success: successArray[i], idRequest: this.idCardRequired, template: this.templateName }
            });
        }
        else {
            pubsub.publish('launchFinishModal', {
                detail: { display: true, initial: false, data: data, success: successArray[i] }
            });
        }
    }
    pubsub.publish('toggleLoader', {
        detail: { showLoader: false }
    });
}

export function deduceIfOnlyAddressUpdate() {
    let returnValue = true;
    for(let key in this.updatedDataModel) {
        if(this.updatedDataModel.hasOwnProperty(key)) {
            if(key === 'crd' || key === 'cod') {
                if(this.updatedDataModel[key].length > 0) {
                    returnValue = false;
                    break;
                }
            }
        }
    }
    return returnValue;
}

export function procureEligibleMemberDetailsList() {
    let isDemographics = false;
    let resultData = this.eligibleInfoList.data;
    let genericsData = this.eligibleInfo.generics;
    let platfromMapClass = new PlatformMap();
    let platformMapObj = platfromMapClass.generateMap.apply(this);
    if(this.templateName === 'GBO') {
        for(const criteria of this.member.membercriterion) {
            if(criteria.updaterequesttype.indexOf('DEMOGRAPHICS') > - 1) {
                isDemographics = true;
                break;
            }
        }
    }

    for(let key in resultData) {
        if(resultData.hasOwnProperty(key)) {
            if(key === 'self') {
                let count = resultData[key].length;
                fetchGenericsForRequestPrimary.apply(this, [count, resultData[key], platformMapObj]);
            }
        }
    }

    if(isDemographics && resultData.hasOwnProperty('dependents')) {
        for(let key in resultData) {
            if(resultData.hasOwnProperty(key)) {
                if(key === 'dependents') {
                    let count = resultData[key].length;
                    fetchGenericsForRequest.apply(this, [count, resultData[key], platformMapObj]);
                }
            }
        }
    }
    else {
        pubsub.publish('checkProcurement', {
            detail: { key: 'dependents', value: 'completed' }
        });
    }
}
//Mega update block - ends

//obsolete method
export function procureEligibleMemberDetails() {
    let isDemographics = false;
    let resultData = this.eligibleInfo.data;
    let genericsData = this.eligibleInfo.generics;
    let platfromMapClass = new PlatformMap();
    let platformMapObj = platfromMapClass.generateMap.apply(this);
    if(this.templateName === 'GBO') {
        for(const criteria of this.member.membercriterion) {
            if(criteria.updaterequesttype.indexOf('DEMOGRAPHICS') > - 1) {
                isDemographics = true;
                break;
            }
        }
    }

    for(let key in resultData) {
        if(resultData.hasOwnProperty(key)) {
            if(key === 'self') {
                for (const item of resultData[key]) {
                    if(this.policyMemberId === item.Id) {
                        let sourcePersonId = '';
                        for (const coverage of this.coverageInfo) {
                            if(coverage.hasOwnProperty(item.Member__r.Enterprise_ID__c)) {
                                if(coverage.source === item.Policy_Platform__c) {
                                    sourcePersonId = coverage[item.Member__r.Enterprise_ID__c];
                                    break;
                                }
                            }
                        }
                        this.member.membercriterion[0].membersourcepersonid = (sourcePersonId !== '') ? sourcePersonId : item.Member__r.Enterprise_ID__c;
                        this.member.membercriterion[0].subscribersourcepersonid = (sourcePersonId !== '') ? sourcePersonId : item.Member__r.Enterprise_ID__c;
                        this.member.membercriterion[0].platform = platformMapObj[item.Policy_Platform__c];
                        // this.member.membercriterion[0].groupid = item.Policy__r.Source_Cust_Cov_Key__c.substring(0, 6);
                        this.member.membercriterion[0].relationship = item.Relationship__c;
                        let udpateRequestType = this.member.membercriterion[0].updaterequesttype;
                        if(udpateRequestType.indexOf("SSN") === -1) {
                            this.member.criticalbiographics = null;
                        }
                        if(udpateRequestType.indexOf("DEMOGRAPHICS") === -1) {
                            this.member.demographics = null;
                        }
                        if(udpateRequestType.indexOf("BIOGRAPHICS") === -1) {
                            this.member.biographics = null;
                        }
                        else {
                            if(this.member.biographics.firstname !== '') {
                                this.idCardRequired = true;
                                this.member.biographics.nameprefix = (item.Member__r.Salutation) ? item.Member__r.Salutation : '';
				                this.member.biographics.namesuffix = (item.Member__r.Suffix) ? item.Member__r.Suffix: '';
			                }
			            }
                        this.updateRequestModel.UpdateMemberRequest.timestamp = genericsData.timestamp;
                        this.updateRequestModel.UpdateMemberRequest.requestid = genericsData.requestId;
                        this.updateRequestModel.UpdateMemberRequest.consumer = genericsData.consumer;
                        if(!isDemographics) {
                            triggerUpdateService.apply(this);
                        }
                        break;
                    }
                }
            }
        }
    }

    if(isDemographics) {
        if(resultData.hasOwnProperty('dependents')) {
            for(let key in resultData) {
                if(resultData.hasOwnProperty(key)) {
                    if(key === 'dependents') {
                        let count = resultData[key].length;
                        fetchGenericsForRequest.apply(this, [count, resultData[key], platformMapObj]);
                    }
                }
            }
        }
        else {
            triggerUpdateService.apply(this);
        }
    }
}

//Mega update block - starts
export function fetchGenericsForRequestPrimary() {
    let platformMapObj = arguments[2];
    getGenericsForRequest({ loopCount: arguments[0] })
        .then(result => {
            let count = 0;
            for (const item of arguments[1]) {
                if(this.platformMemberMap[item.Policy_Platform__c.toLowerCase()].indexOf(item.Member__r.Enterprise_ID__c) === -1) {
                    let updateRequestClass = new updateMemberRequestModel();
                    let updateRequestModel = updateRequestClass.generateRequestModel.apply(this);
                    let memberClass = new Members();
                    let memberObj = memberClass.generateMembers.apply(this);
                    let memberCriteriaClass = new MemberCriterion();
                    let memberCriterionObj = memberCriteriaClass.generateCriteria.apply(this);
                    let sourcePersonId = '';
                    let subscriberId = '';
                    let originalMember = {};

                    for(let oKey in this.member) {
                        if(this.member.hasOwnProperty(oKey) && oKey !== 'membercriterion') {
                            originalMember[oKey] = this.member[oKey];
                        }
                    }

                    for(let mKey in originalMember) {
                        if(originalMember.hasOwnProperty(mKey)) {
                            memberObj[mKey] = originalMember[mKey];
                        }
                    }

                    memberObj.membercriterion = [];

                    for (const coverage of this.coverageInfo) {
                        if(coverage.hasOwnProperty(item.Member__r.Enterprise_ID__c)) {
                            if(coverage.source === item.Policy_Platform__c) {
                                sourcePersonId = coverage[item.Member__r.Enterprise_ID__c];
                                subscriberId = coverage.subscriber;
                                break;
                            }
                        }
                    }

                    memberCriterionObj.membersourcepersonid = (sourcePersonId !== '') ? sourcePersonId : item.Member__r.Enterprise_ID__c;
                    memberCriterionObj.subscribersourcepersonid = (subscriberId !== '') ? subscriberId : item.Member__r.Enterprise_ID__c;
                    memberCriterionObj.platform = platformMapObj[item.Policy_Platform__c];
                    memberCriterionObj.relationship = item.Relationship__c;
                    memberCriterionObj.updaterequesttype = [];
                    for(let oKey in this.member) {
                        if(this.member.hasOwnProperty(oKey) && oKey === 'membercriterion') {
                            for (const criterion of this.member.membercriterion) {
                                for (const requestType of criterion.updaterequesttype) {
                                    memberCriterionObj.updaterequesttype.push(requestType);
                                }
                            }
                            break;
                        }
                    }
                    
                    memberObj.membercriterion.push(memberCriterionObj);
                    updateRequestModel.UpdateMemberRequest.members.push(memberObj);
                    
                    updateRequestModel.UpdateMemberRequest.timestamp = result[count].timestamp;
                    updateRequestModel.UpdateMemberRequest.requestid = result[count].requestId;
                    updateRequestModel.UpdateMemberRequest.consumer = result[count].consumer;
                    let udpateRequestType = memberObj.membercriterion[0].updaterequesttype;
                    if(udpateRequestType.indexOf("SSN") === -1) {
                        memberObj.criticalbiographics = null;
                    }
                    if(udpateRequestType.indexOf("DEMOGRAPHICS") === -1) {
                        memberObj.demographics = null;
                    }
                    if(US0015656SwitchLabel.toUpperCase() === 'Y' && 
                        this.isDateInFuture && memberObj.demographics !== null && 
                        US0015656Scope.toUpperCase().indexOf(this.templateName) > -1) {
                        memberObj.demographics.address = null;
                    }
                    this.primaryMemberName = `${memberObj.firstname} ${memberObj.lastname}`;
                    if(udpateRequestType.indexOf("BIOGRAPHICS") === -1) {
                        memberObj.biographics = null;
                    }
                    else {
                        if(memberObj.biographics.firstname !== '') {
                            this.idCardRequired = true;
                            memberObj.biographics.nameprefix = (item.Member__r.Salutation) ? item.Member__r.Salutation : '';
                            memberObj.biographics.namesuffix = (item.Member__r.Suffix) ? item.Member__r.Suffix: '';
                            if(US1645972SwitchLabel.toUpperCase() === 'Y') {
                                memberObj.biographics.middleinitial = memberObj.biographics.middlename;
                                if(US3431258SwicthLabel.toUpperCase() === 'Y') {
                                    // To Send Updated Middle Name value instead of Old Value in middlename field as part of Member Update Request
                                    memberObj.biographics.middlename = memberObj.biographics.middlename;    
                                }
                                else {
                                    memberObj.biographics.middlename = this.middleName;
                                }
                            }
                        }
                    }
                    this.primaryUpdReqModel.push(updateRequestModel);
                    this.platformMemberMap[item.Policy_Platform__c.toLowerCase()].push(item.Member__r.Enterprise_ID__c);
                }
                count++;
            }
            pubsub.publish('checkProcurement', {
                detail: { key: 'self', value: 'completed' }
            });
        })
        .catch(error => {
            pubsub.publish('toggleLoader', {
                detail: { showLoader: false }
            });
            pubsub.publish('checkProcurement', {
                detail: { key: 'self', value: 'failed' }
            });
            showToastEvent.apply(this, ['fetchGenericsForRequest APEX Error', error.message]);
        });
}
//Mega update block - ends

export function fetchGenericsForRequest() {
    let platformMapObj = arguments[2];
    getGenericsForRequest({ loopCount: arguments[0] })
        .then(result => {
            let count = 0;
            for (const item of arguments[1]) {
                if(this.platformMemberMap[item.Policy_Platform__c.toLowerCase()].indexOf(item.Member__r.Enterprise_ID__c) === -1) {
                    let birthDateStr = item.Member__r.Birthdate__c;
                    let birthDate = new Date(birthDateStr.replace(/-/g, '/').replace(/T.+/, ''));
                    let currDate = new Date();
                    //let diff = Math.abs(currDate.getUTCFullYear() - birthDate.getUTCFullYear());
                    let diff = Math.floor((currDate.getTime() - birthDate.getTime())/(1000 * 60 * 60 * 24 * 365.25));

                    if(diff < 18) {
                        let updateRequestClass = new updateMemberRequestModel();
                        let updateRequestModel = updateRequestClass.generateRequestModel.apply(this);
                        let memberClass = new Members();
                        let memberObj = memberClass.generateMembers.apply(this);
                        let memberCriteriaClass = new MemberCriterion();
                        let memberCriterionObj = memberCriteriaClass.generateCriteria.apply(this);
                        let sourcePersonId = '';
                        let subscriberId = '';

                        memberObj.lastname = item.Member__r.LastName;
                        memberObj.firstname = item.Member__r.FirstName;
                        memberObj.demographics = this.member.demographics;
                        memberObj.biographics = null;
                        memberObj.criticalbiographics = null;

                        for (const coverage of this.coverageInfo) {
                            if(coverage.hasOwnProperty(item.Member__r.Enterprise_ID__c)) {
                                if(coverage.source === item.Policy_Platform__c) {
                                    sourcePersonId = coverage[item.Member__r.Enterprise_ID__c];
                                    subscriberId = coverage.subscriber;
                                    break;
                                }
                            }
                        }

                        memberCriterionObj.membersourcepersonid = (sourcePersonId !== '') ? sourcePersonId : item.Member__r.Enterprise_ID__c;
                        memberCriterionObj.subscribersourcepersonid = (subscriberId !== '') ? subscriberId : item.Member__r.Enterprise_ID__c;
                        memberCriterionObj.platform = platformMapObj[item.Policy_Platform__c];
                        // memberCriterionObj.groupid = item.Policy__r.Source_Cust_Cov_Key__c.substring(0, 6);
                        memberCriterionObj.relationship = item.Relationship__c;
                        memberCriterionObj.updaterequesttype.push('DEMOGRAPHICS');
                        
                        memberObj.membercriterion.push(memberCriterionObj);
                        updateRequestModel.UpdateMemberRequest.members.push(memberObj);

                        updateRequestModel.UpdateMemberRequest.timestamp = result[count].timestamp;
                        updateRequestModel.UpdateMemberRequest.requestid = result[count].requestId;
                        updateRequestModel.UpdateMemberRequest.consumer = result[count].consumer;

                        if(memberCriterionObj.updaterequesttype.indexOf("SSN") === -1) {
                            memberObj.criticalbiographics = null;
                        }
                        if(memberCriterionObj.updaterequesttype.indexOf("DEMOGRAPHICS") === -1) {
                            memberObj.demographics = null;
                        }
                        if(memberCriterionObj.updaterequesttype.indexOf("BIOGRAPHICS") === -1) {
                            memberObj.biographics = null;
                        }
                        this.dependentUpdReqModel.push(updateRequestModel);
                        this.platformMemberMap[item.Policy_Platform__c.toLowerCase()].push(item.Member__r.Enterprise_ID__c);
                    }
                }
                count++;
            }
            pubsub.publish('checkProcurement', {
                detail: { key: 'dependents', value: 'completed' }
            });
        })
        .catch(error => {
            pubsub.publish('toggleLoader', {
                detail: { showLoader: false }
            });
            pubsub.publish('checkProcurement', {
                detail: { key: 'dependents', value: 'failed' }
            });
            showToastEvent.apply(this, ['fetchGenericsForRequest APEX Error', error.message]);
        });
}

//obsolete method
export function triggerUpdateService() {
    retrieveUpdateResponse({ requestInput: JSON.stringify(this.updateRequestModel) } )
        .then(result => {
            let data = '';
            let success = '';
            let responseObj = JSON.parse(result);
            if(responseObj.calloutErrored) {
                if(this.templateName === 'RSO') {
                    data = '<div class="member-name">' + this.updateRequestModel.UpdateMemberRequest.members[0].firstname + ' ' + this.updateRequestModel.UpdateMemberRequest.members[0].lastname + '</div>' + 
                            '<div>Updates made were not successful. Make updates in <a>CI/PAPI</a>.</div>' +
                            '<div>After closing the tab, close CRM case per established process.</div>';
                }
                else {
                    data = '<div class="member-name">' + this.updateRequestModel.UpdateMemberRequest.members[0].firstname + ' ' + this.updateRequestModel.UpdateMemberRequest.members[0].lastname + '</div>' + 
                            '<div>Updates were not successful.</div>' +
                            '<div>After closing the tab, refer to Mentor for next steps.</div>';
                }
                success = false;
            }
            else {
                if (responseObj.updateResponseDTO.UpdateMemberResponse.responseCode === 'S') {
                    data = '<div class="member-name">' + this.updateRequestModel.UpdateMemberRequest.members[0].firstname + ' ' + this.updateRequestModel.UpdateMemberRequest.members[0].lastname + '</div>' + 
                            '<div>Updates made were successfull.</div>' +
                            '<div>After closing the tab, close CRM case per established process.</div>';
                    success = true;
                }
                else {
                    // let errMessage = responseObj.updateResponseDTO.UpdateMemberResponse.members[0].cdmmsgack;
                    if(this.templateName === 'RSO') {
                        data = '<div class="member-name">' + this.updateRequestModel.UpdateMemberRequest.members[0].firstname + ' ' + this.updateRequestModel.UpdateMemberRequest.members[0].lastname + '</div>' + 
                                '<div>Updates made were not successful. Make updates in <a>CI/PAPI</a>.</div>' +
                                '<div>After closing the tab, close CRM case per established process.</div>';
                    }
                    else {
                        data = '<div class="member-name">' + this.updateRequestModel.UpdateMemberRequest.members[0].firstname + ' ' + this.updateRequestModel.UpdateMemberRequest.members[0].lastname + '</div>' + 
                                '<div>Updates were not successful.</div>' +
                                '<div>After closing the tab, refer to Mentor for next steps.</div>';
                    }
                    success = false;
                }
            }
            triggerTemplateSave.apply(this, [data, success]);
            if(this.dependentUpdReqModel.length > 0) {
                for(let i = 0; i < this.dependentUpdReqModel.length; i++) {
                    if(i === this.dependentUpdReqModel.length - 1) {
                        triggerDepUpdateService.apply(this, [this.dependentUpdReqModel[i], true]);
                    }
                    else {
                        triggerDepUpdateService.apply(this, [this.dependentUpdReqModel[i], false]);
                    }
                }
            }
            else {
                this.dispatchEvent(new CustomEvent('toggleDirty', {
                    detail: { data:  false},
                    bubbles: true,
                    composed: true,
                }));
            }
            pubsub.publish('launchFinishModal', {
                detail: { display: true, initial: false, data: data, success: success, idRequest: this.idCardRequired, template: this.templateName }
            });
        })
        .catch(error => {
            showToastEvent.apply(this, ['DepUdpateDemographics APEX Error', error.message]);
        });
}

//Mega update - starts
export function triggerDepUpdateService() {
    this[arguments[4]].push({
        index: arguments[3],
        status: 'started'
    });
    retrieveUpdateResponse({ requestInput: JSON.stringify(arguments[0]) } )
        .then(result => {
            let data = '';
            let success = '';
            let responseStatus = '';
            let responseObj = JSON.parse(result);
            this.mmpUpdateResponseDetails = JSON.stringify(responseObj);
            console.log('RetrieveUpdateResponse:');
            if(responseObj.calloutErrored) {
                success = false;
            }
            else {
                if (responseObj.updateResponseDTO.UpdateMemberResponse.responseCode === 'S') {
                    success = true;
                    responseStatus = responseObj.updateResponseDTO.UpdateMemberResponse.result;
                    if(US2568799SwitchLabel.toUpperCase() === 'Y') {
                       if(this.templateName === 'RSO'){
                       triggerTemplateSaveAfterResponse.apply(this, [true, true, JSON.stringify(responseObj.updateResponseDTO), responseStatus]);
                       }
                    }
                }
                else {
                    success = false;
                    responseStatus = responseObj.updateResponseDTO.UpdateMemberResponse.result;
                    if(US2568799SwitchLabel.toUpperCase() === 'Y') {
                        if(this.templateName === 'RSO'){
                        triggerTemplateSaveAfterResponse.apply(this, [true, true, JSON.stringify(responseObj.updateResponseDTO), responseStatus]);
                        }
                    }
                }
            }
            let mmpid = arguments[0].UpdateMemberRequest.members[0].membercriterion[0].membersourcepersonid;
            let entId = '';
            for (const coverage of this.coverageInfo) {
                for(let key in coverage) {
                    if(coverage.hasOwnProperty(key)) {
                        if(coverage[key] === mmpid && key !== 'subscriber') {
                            entId = key;
                            break;
                        }
                    }
                }
            }
            entId = (entId !== '') ? entId : mmpid;
            let mmName = arguments[0].UpdateMemberRequest.members[0].firstname + ' ' + arguments[0].UpdateMemberRequest.members[0].lastname;
            let tempObj = {};
            let isPush = false;
            /*QA Fix - Starts */
            let entExists = false;
            if(this.platformMemberMsgMap.length > 0) {
                for (const item of this.platformMemberMsgMap) {
                    if(item.hasOwnProperty(entId)) {
                        entExists = true;
                    }
                }
            }
            /*QA Fix - Ends */
            if(this.platformMemberMsgMap.length > 0) {
                for (const item of this.platformMemberMsgMap) {
                    if(entExists) {
                        if(typeof item[entId] !== 'undefined') {
                            if(item[entId].success) {
                                item[entId].success = success;
                            }
                        }
                    }
                    else {
                        tempObj[entId] = { primary: arguments[2], name: mmName, success: success };
                        isPush = true;
                    }
                }
            }
            else {
                tempObj[entId] = { primary: arguments[2], name: mmName, success: success };
                isPush = true;
            }
            if(isPush) {
                this.platformMemberMsgMap.push(tempObj);
            }
            for (const arg of this[arguments[4]]) {
                if(arg.index === arguments[3]) {
                    arg.status = 'completed';
                    break;
                }
            }
            if(arguments[1]) {
                if(arguments[2]) { //isPrimary
                    pubsub.publish('triggerSubmitForDependent', {
                        detail: { value: true }
                    });
                   if(US0015656SwitchLabel.toUpperCase() !== 'Y') {
                    if(US2568799SwitchLabel.toUpperCase() === 'Y') {
                        triggerTemplateSaveAfterResponse.apply(this, [data, success, JSON.stringify(responseObj.updateResponseDTO), responseStatus]);
                    }
                    if(US2568799SwitchLabel.toUpperCase() === 'N'){
                        triggerTemplateSave.apply(this, [data, success]);
                    }
                }
                    //finishMessages.apply(this, [arguments[2], 1]);
                }
                else { //dependnent
                    pubsub.publish('triggerSubmitForDependent', {
                        detail: { value: false }
                    });
                    //finishMessages.apply(this, [arguments[2], this.dependentUpdReqModel.length]);
                }
            }
            pubsub.publish('checkThenProcessFinish', {
                detail: {
                    isPrimary: arguments[2], 
                    loopCount: (arguments[4] === 'sequenceUpdateStatePrimary') ? 1 : this.dependentUpdReqModel.length,
                    container: arguments[4]
                }
            });
        })
        .catch(error => {
            showToastEvent.apply(this, ['UdpateDemographics APEX Error', error.message]);
        });
}
//Mega update - ends

export function triggerTemplateSave() {
    processTemplateData({ caseNumber: this.recordCaseId, templateData: JSON.stringify({ templateBase: this.templateDataModel }), templateName: this.templateName, policyId: this.policyMemberId, isSuccess: arguments[1] })
        .then(result => {
            // pubsub.publish('launchFinishModal', {
            //     detail: { display: true, initial: false, data: arguments[0], success: arguments[1] }
            // });
            if(US0015656SwitchLabel.toUpperCase() !== 'Y') {
                pubsub.publish('toggleLoader', {
                    detail: { showLoader: false }
                });
            }
        })
        .catch(error => {
            showToastEvent.apply(this, ['TemplateDemographics APEX Error', error.message]);
        });
}

export function triggerTemplateSaveAfterResponse() {
    console.log('TemplateSaveAfterResponse');
    processTemplateDataAfterResponse({ caseNumber: this.recordCaseId, templateData: JSON.stringify({ templateBase: this.templateDataModel }), templateName: this.templateName, policyId: this.policyMemberId, isSuccess: arguments[1], responseStatusMap: arguments[2], responseStatus: arguments[3]})
        .then(result => {
        })
        .catch(error => {
            showToastEvent.apply(this, ['TemplateDemographicsUpdate APEX Error', error.message]);
        });
}

export function populateDataModel() {
    let key = arguments[0];
    // if (this.updatedDataModel.hasOwnProperty(key)) {
    this.updatedDataModel[key].forEach((field) => {
        if(typeof field.templateModel !== "undefined") {
            field.templateModel.expr.forEach(exp => {
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
                            for (const displayField of this.updatedDataModel[key]) {
                                if(displayField.order === source) {
                                    displayField.templateModel[exp.fnOut[srcIndex]] = this[exp.fnName].apply(this, argList);
                                    break;
                                }
                            }
                        });
                        break;
                    case "void":
                        this[exp.fnName].apply(this, argList);
                        break;
                    default:
                        break;
                }
            });
        }
    });
    // }
}

export function populateRequestDataModel() {
    let key = arguments[0];
    // if (this.updatedDataModel.hasOwnProperty(key)) {
    this.updatedDataModel[key].forEach((field) => {
        if(field.isSummary) {
            if(typeof field.updateModel !== "undefined") {
                field.updateModel.expr.forEach(exp => {
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
                        case "void":
                            this[exp.fnName].apply(this, argList);
                            break;
                        default:
                            break;
                    }
                });
            }
        }
    });
    // }
}

export function procureTemplateModel() {
    let key = arguments[0];
    let templateMethodMap = this.templateModelClass.mapperModel.apply(this);
    let templateMethodName = templateMethodMap[this.templateName.toLowerCase()][key];
    let templateModelObj = this.templateModelClass[templateMethodName].apply(this);
    let templateNameObj = this.templateModelClass.generateTemplateNameMap.apply(this);
    let keyName = `${this.templateName.toLowerCase()}${key.toUpperCase()}`;
    let pushObj = {};
    let summaryObj= {
        title: (key === 'cod') ? 'Contact Demographic Update Summary' : 
            (key === 'crd') ? 'Critical Demographic Update Summary' :
            (key === 'mau') ? 'Medicare Address Update Summary' : 
            (key === 'mdu') ? 'Medicaid Address Update Summary' : '',
        sections: []
    };

    this.updatedDataModel[key].forEach((field) => {
        if(typeof field.templateModel !== "undefined") {
            if(MMPTemplateAddressFieldNames.includes(field.templateModel.avfRequestFieldName.toLowerCase())) {
                templateModelObj[field.templateModel.avfRequestFieldName] = deduceAddressLineValue.apply(this, [field.templateModel.avfValue]);
            }
            else {
                templateModelObj[field.templateModel.avfRequestFieldName] = field.templateModel.avfValue;
            }
        }
    });

    pushObj[keyName] = templateModelObj;

    this.updatedDataModel[key].forEach((field) => {
        if(typeof field.templateModel !== "undefined" && field.isSummary) {
            let isSectionExist = false;
            if(summaryObj.sections.length === 0) {
                isSectionExist = false;
            }
            else {
                for (const section of summaryObj.sections) {
                    if(section.title === field.templateModel.avfSummary.sectionName) {
                        isSectionExist = true;
                    }
                }
            }
            if(!isSectionExist) {
                summaryObj.sections.push({
                    title: field.templateModel.avfSummary.sectionName,
                    fields: []
                });
            }
        }
    });

    this.updatedDataModel[key].forEach((field) => {
        if(typeof field.templateModel !== "undefined" && field.isSummary) {
            for (const section of summaryObj.sections) {
                if(section.title === field.templateModel.avfSummary.sectionName) {
                    section.fields.push({
                        label: field.label,
                        value: field.templateModel.avfValue
                    });
                }
            }
        }
    });

    pushObj[keyName].UserInterfaceData = JSON.stringify(summaryObj);

    this.templateDataModel.push({
        data: pushObj, templateName: templateNameObj[key].apply(this), key: key
    });
}

function deduceAddressLineValue() {
    let fieldValue = arguments[0];
    let returnValue = '';

    if(typeof fieldValue !== 'undefined' && fieldValue !== null && fieldValue !== '') {
        returnValue = (fieldValue.length > +MMPAddressLengthCap) ? fieldValue.substr(0, +MMPAddressLengthCap) : fieldValue;
    }

    return returnValue;
}