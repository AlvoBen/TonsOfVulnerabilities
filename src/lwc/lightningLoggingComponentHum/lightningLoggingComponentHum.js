/*
LWC Name        : lightningLoggingComponentHum.js
Function        : LWC to enable and disable logging functionality.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar              2/16/2022	                   initial version
* Abhishek Mangutkar              6/15/2022	                   DF - 5124
* Abhishek Mangutkar              8/17/2022	                   DF - 5893
* Nirmal Garg                      09/02/2022                  US-3759633 changes.
* G Sagar                         11/07/2022                  3771949 - CRM Service Billing Systems Integration: Lightning - ID Cards- Logging on Lightning & Classic (Surge) 
* Abhishek Mangutkar              2/28/2023                   US - 4274215 - Update UI Logging Framework to Support Attaching Interaction to Existing Case
* Divya Bhamre                    3/14/2023                    US - 4368532 - T1PRJ0865978 - MF 25609 -Automatically prefill the interacting with type and case origin from the interaction log on new cases-  UI logging
* Vishal Shinde                   27/09/2023                   DF- 8143
**************************************************************************************************************************/

import { LightningElement, api, wire, track } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/loggingLMSChannel__c';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import sendLogData from '@salesforce/apex/Logging_LC_HUM.createLog';
import createNewCase from '@salesforce/apex/Logging_LC_HUM.createNewCase';
import { openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import pubSubHum from 'c/loggingPubSubHum';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { NavigationMixin } from 'lightning/navigation';
import { setEventListener, getLoggingKey } from 'c/loggingUtilityHum';
import { attachInteractionToCase, getInteractionId } from 'c/genericCaseActionHum';
import GenericCaseActionHum from 'c/genericCaseActionHum';

const START_LOGGING = 'StartLogging';
const START_LOGGING_MSG = 'Start Logging';
const STOP_LOGGING = 'StopLogging';
const STOP_LOGGING_MSG = 'Stop Logging';
const LOGO_HOVER = 'HoverLogo';
const LOGO_ACTIVE = 'ActiveLogo';
const LOGO_DEFAULT = 'DefaultLogo';
const LOGGING_DATA = 'LoggingData';
const LOGGING_STATUS = 'GetLoggingStatus';
const BOTTON_LOG_AND_FINISH = 'LogAndFinishButton';

export default class LightningLoggingComponentHum extends GenericCaseActionHum {

    @api recordId;
    @api relatedField;
    @api autoLogging;
    @api allFieldsLogging;
    @api selectedFieldsLogging;
    @api type;
    @api subtype;
    @api attachmentkey;
    @api attachmentdescription
    viewLoggingPanel = false;
    logTo = true;
    showStartLogging = true;
    recordIdToSend;
    viewExistingCasePanel = false;
    currentPageReference;
    urlStateParameters;
    viewLogButtonClicked = false;
    showLogFinishButton = true;
    disableLogButton = true;
    disableCancelButton = false;
    disableLogFinishButton = true;
    disableLogTo = false;
    logCollectedData = [];
    finalLogCollectionData = [];
    loggingFlag = false;
    existingCaseId;
    newCaseId;
    @track attributeId;
    showLogButton = true;
    logoName = LOGO_DEFAULT;
    showIconHoverMessage = false;
    logToExisting = true;
    showActiveLoggingMessage = false;
    @track standardTableData = [];
    @track existingCaseNumber;
    @track Loggeddata = {};
    @track LoggedSearchData = {};
    @track loggingKey;
    @track finallogdata = {};
    @track pageRef;



    connectedCallback() {
        getLoggingKey(this.currentPageReference).then(result => {
            this.loggingKey = result;
        }).catch(error => {
            console.log(error);
        });

        if (this.recordId == undefined || this.recordId == null) {
            this.setRecordIdFromURL();
        }
        else {
            this.recordIdToSend = this.recordId;
        }

        if (this.autoLogging) {
            this.startLoggingOnPageLoad();
        }
        this.subscribeToMessageChannel();
        pubSubHum.registerListener('loggingDataEvent', this.loadData.bind(this), this);
        pubSubHum.registerListener('loggingSearchCriteriaEvent', this.loadSearchCriteria.bind(this), this);
        pubSubHum.registerListener('clearLoggingSearchCriteriaEvent', this.clearSearchLogData.bind(this), this);
    }

    loadSearchCriteria(eventData) {
        let dataToAdd = eventData;
        if (this.LoggedSearchData.hasOwnProperty(dataToAdd.Section)) {
            if (Array.isArray(this.LoggedSearchData[dataToAdd.Section])) {
                let result = this.checkForExistingSearch(this.LoggedSearchData[dataToAdd.Section], dataToAdd);
            }
        } else {
            this.LoggedSearchData[dataToAdd.Section] = this.createchilddata(dataToAdd);
        }
    }


    loadData(eventData) {
        this.pushDataToLogArray(eventData, false);
    }

    @wire(MessageContext)
    messageContext;

    //set current page url ref.
    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.pageRef = currentPageReference;
            this.currentPageReference = currentPageReference;
            this.urlStateParameters = currentPageReference.state;
            this.attributeId = currentPageReference.attributes != null && currentPageReference.attributes.attributes != null
                && currentPageReference.attributes.attributes.Id != null ? currentPageReference.attributes.attributes.Id : null;
            if (!this.attributeId) {
                var tmpId = currentPageReference.attributes != null && currentPageReference.attributes.url != null ? currentPageReference.attributes.url.split('=') : null;
                this.attributeId = tmpId != null && tmpId.length > 0 ? tmpId[1] : null
            }
        }
    }

    //get parameters from url.
    setRecordIdFromURL() {
        this.recordIdToSend = this.urlStateParameters.c__Id != null ? this.urlStateParameters.c__Id : this.urlStateParameters.C__Id != null ? this.urlStateParameters.C__Id : this.urlStateParameters.C__id != null ? this.urlStateParameters.C__id : this.attributeId != null ? this.attributeId : null;
    }

    startLoggingOnPageLoad() {
        this.handleStartLogging();
    }
    openLoggingPanel() {
        if (this.viewLoggingPanel) {
            this.viewLoggingPanel = false;
        }
        else {
            this.viewLoggingPanel = true;
        }
    }

    handleLoggingIconClick(event) {
        this.viewExistingCasePanel = true;
        this.viewLogButtonClicked = !this.viewLogButtonClicked;
        if (this.viewLogButtonClicked) {
            this.logoName = LOGO_HOVER;
            this.viewLoggingPanel = true;
            if (this.logTo) {
                this.showHideExistingCase(false);
                this.viewLoggingPanel = true;
            }
            else {
                this.viewLoggingPanel = false;
                this.viewExistingCasePanel = true;
                this.showHideExistingCase(true);
            }
        }
        else {
            if (this.showStartLogging) {
                this.logoName = LOGO_DEFAULT;
            }
            this.viewLoggingPanel = false;
            this.showHideExistingCase(false);
        }

    }

    closeExistingCaseLogPanel() {
        this.showHideExistingCase(false);
        this.viewLogButtonClicked = false;
    }


    toggleLogTo() {
        if (this.logTo) {
            this.logTo = false;
            this.logToExisting = true;
            if (this.logCollectedData.length > 0 && this.existingCaseId) {
                //enable log buttons
                this.disableLogButton = false;
                this.disableLogFinishButton = false;
            }
            else {
                this.disableLogButton = true;
                this.disableLogFinishButton = true;
            }
            this.viewLoggingPanel = false;
            this.showHideExistingCase(true);
        }
        else {
            this.logTo = true;
            this.logToExisting = false;
            if (this.logCollectedData.length > 0) {
                //enable log buttons
                this.disableLogButton = false;
                this.disableLogFinishButton = false;
            }
            else {
                this.disableLogButton = true;
                this.disableLogFinishButton = true;
            }
            this.showHideExistingCase(false);
            this.viewLoggingPanel = true;
        }
    }

    handleStartLogging() {
        this.showStartLogging = false;
        this.logoName = LOGO_ACTIVE;
        this.loggingFlag = true;
        this.viewLogButtonClicked = false;
        //fire lms event
        this.publishLoggingEvent(START_LOGGING, START_LOGGING_MSG);
        this.viewLoggingPanel = false;
        this.showHideExistingCase(false);
        this.publishEvent(START_LOGGING, START_LOGGING_MSG);
    }

    publishEvent(msgName, msgData) {
        if (this.loggingKey) {
            setEventListener(this.loggingKey, msgData);
        } else {
            getLoggingKey(this.currentPageReference).then(result => {
                this.loggingKey = result;
                setEventListener(this.loggingKey, msgData);
            }).catch(error => {
                console.log(error);
            });
        }
    }

    handleCancelLogging() {
        this.stopLogging();
        this.viewLogButtonClicked = false;
    }

    publishLoggingEvent(msgName, msgData) {
        let message = { MessageName: msgName, MessageDetails: msgData };
        publish(this.messageContext, messageChannel, message);
    }

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                messageChannel,
                (message) => this.handleMessage(message)
            );
        }
    }

    handleMessage(message) {
        if (message.MessageName === LOGGING_DATA) {
            this.pushDataToLogArray(message.MessageDetails, false);
        }
        else if (message.MessageName === 'LoggingDataWithoutRelatedField') {
            this.pushDataToLogArray(message.MessageDetails, true);
        }
        else if (message.MessageName === LOGGING_STATUS) {
            if (this.loggingFlag) {
                this.publishLoggingEvent(START_LOGGING, START_LOGGING_MSG);
                this.publishEvent(START_LOGGING, START_LOGGING_MSG);
            }
        }
    }

    pushDataToLogArray(dataToAdd, addRelatedField) {
        let logDataExist = false;
        this.showLogButton = true;
        this.showLogFinishButton = true;

        if (this.disableLogButton) {
            if (this.logTo) {
                this.disableLogButton = false;
                this.disableLogFinishButton = false;
            }
            else if (this.existingCaseId) {
                this.disableLogButton = false;
                this.disableLogFinishButton = false;
            }
        }
        if (addRelatedField) {
            let logData = { LableName: dataToAdd.LableName, LabelValue: dataToAdd.LabelValue, RelatedField: this.relatedField, Section: dataToAdd.Section };
            dataToAdd = logData;
        }

        if (this.Loggeddata.hasOwnProperty(dataToAdd.Section)) {
            if (Array.isArray(this.Loggeddata[dataToAdd.Section])) {
                let result = this.checkForExisting(this.Loggeddata[dataToAdd.Section], dataToAdd);
            }
        } else {
            this.Loggeddata[dataToAdd.Section] = this.createchilddata(dataToAdd);
        }

        if (this.logCollectedData.length > 0) {
            this.logCollectedData.forEach((a, index) => {
                if ((JSON.stringify(a).toString().toLocaleLowerCase().includes(JSON.stringify(dataToAdd).toString().toLocaleLowerCase()))) {
                    logDataExist = true;
                    //Remove from log data
                    this.logCollectedData.splice(index, 1);
                    if (this.logCollectedData.length == 0) {
                        this.disableLogButton = true;
                        this.disableLogFinishButton = true;
                    }
                }
            });
            if (!logDataExist) {
                this.logCollectedData.push(dataToAdd);
            }
        }
        else {
            this.logCollectedData.push(dataToAdd);
        }
        //this.enableDisbleLogAndFinishButtons();
    }


    checkForExistingSearch(existingdata, newdata) {
        let logDataExist = false;
        if (existingdata) {
            if (this.LoggedSearchData[newdata.Section].length > 0) {
                this.LoggedSearchData[newdata.Section].forEach((a, index) => {
                    if ((JSON.stringify(a).toString().toLocaleLowerCase().includes(JSON.stringify(newdata.LabelValue).toString().toLocaleLowerCase()))) {
                        logDataExist = true;
                    }
                });
            }
            if (!logDataExist) {
                this.LoggedSearchData[newdata.Section].push({
                    relatedField: this.createRelatedField(newdata),
                    loggedFields: this.createdloggeditems(newdata, null)
                });
            }
        }
    }


    checkForExisting(existingdata, newdata) {
        if (newdata.hasOwnProperty("TableIndex")) {
            if (existingdata) {
                let index = existingdata.findIndex(h => JSON.stringify(h.relatedField.RelatedField).toLocaleLowerCase() === JSON.stringify(newdata.RelatedField).toLocaleLowerCase()
                    && h.relatedField.TableIndex.rowIndex === newdata.TableIndex.rowIndex);
                if (index >= 0) {
                    this.Loggeddata[newdata.Section][index].loggedFields = this.createdloggeditems(newdata, this.Loggeddata[newdata.Section][index].loggedFields);
                } else {
                    this.Loggeddata[newdata.Section].push({
                        relatedField: this.createRelatedField(newdata),
                        loggedFields: this.createdloggeditems(newdata, null)
                    })
                }
            }
        } else {
            if (existingdata) {
                let index = existingdata.findIndex(h => JSON.stringify(h.relatedField.RelatedField).toLocaleLowerCase() === JSON.stringify(newdata.RelatedField).toLocaleLowerCase());
                if (index >= 0) {
                    this.Loggeddata[newdata.Section][index].loggedFields = this.createdloggeditems(newdata, this.Loggeddata[newdata.Section][index].loggedFields);
                } else {
                    this.Loggeddata[newdata.Section].push({
                        relatedField: this.createRelatedField(newdata),
                        loggedFields: this.createdloggeditems(newdata, null)
                    });
                }
            }
        }
    }

    createchilddata(dataToAdd) {
        let temp = [];
        temp.push({
            relatedField: this.createRelatedField(dataToAdd),
            loggedFields: this.createdloggeditems(dataToAdd, null)
        });
        return temp;
    }

    createRelatedField(dataToAdd) {
        let rFields = {};
        rFields.RelatedField = dataToAdd.RelatedField;
        if (dataToAdd.hasOwnProperty('TableIndex') && Object.values(dataToAdd['TableIndex']).length > 0) {
            rFields.TableIndex = dataToAdd.TableIndex
        }
        return rFields;
    }

    createdloggeditems(dataToAdd, existingData) {
        let temp = [];
        let sLabel = dataToAdd && dataToAdd?.LableName ? dataToAdd.LableName : dataToAdd && dataToAdd?.LabelName ? dataToAdd.LabelName : '';
        let existingele = false;
        if (existingData && Array.isArray(existingData) && existingData.length > 0) {
            existingData.forEach((k, index) => {
                if (k.label === sLabel && k.value === dataToAdd.LabelValue) {
                    //existingData = existingData.splice(index,1);
                    if (dataToAdd?.LogId && k?.logid && dataToAdd.LogId === k.logid) {
                        existingData = existingData.filter(t => t.label != sLabel && t.value != dataToAdd.LabelValue);
                        existingele = true;
                    } else if (dataToAdd.LogId === null || dataToAdd.LogId === undefined) {
                        existingData = existingData.filter(t => t.label != sLabel && t.value != dataToAdd.LabelValue);
                        existingele = true;
                    }
                    else {
                        existingele = false;
                    }
                }
            })
            if (!existingele) {
                existingData.push({
                    label: sLabel,
                    value: dataToAdd.LabelValue,
                    logid: dataToAdd.LogId
                })
            }
            return existingData;
        } else {
            temp.push({
                label: sLabel,
                value: dataToAdd.LabelValue,
                logid: dataToAdd.LogId
            })
        }
        return temp;
    }


    createFinalLoggedFields(data) {
        let temp = [];
        if (data && Array.isArray(data) && data.length > 0) {
            data.forEach(k => {
                temp.push({
                    label: k.label,
                    value: k.value
                })
            })
        }
        return temp;
    }

    handleLogClick() {
        if (!this.newCaseId) {
            this.createNewCaseId('Log');
        }
        this.showLogButton = false;
        this.showLogFinishButton = false;
        this.disableCancelButton = true;
        this.disableLogTo = true;
        this.showHideExistingCase(false);
        this.viewLogButtonClicked = false;
    }

    disconnectedCallback() {
        sessionStorage.removeItem(this.loggingKey);
        this.unsubscribeToMessageChannel();
    }


    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }

    handleFinishClick() {
        this.captureLogValues();
        this.createLog();
        this.logTo = true;
    }

    handleLogFinishClick() {
        this.captureLogValues();
        if (this.existingCaseId && this.logToExisting && !this.logTo) {
            this.createLog();
        }
        else {
            this.createNewCaseId(BOTTON_LOG_AND_FINISH);
            this.viewLoggingPanel = false;
            this.viewExistingCasePanel = false;
            this.showHideExistingCase(false);
        }
        this.logTo = true;
    }

    stopLogging() {
        this.showStartLogging = true;
        this.showLogButton = true;
        this.showLogFinishButton = true;
        this.disableLogButton = true;
        this.disableLogFinishButton = true;
        this.loggingFlag = false;
        this.disableLogTo = false;
        this.disableCancelButton = false;
        this.publishLoggingEvent(STOP_LOGGING, STOP_LOGGING_MSG);
        this.publishEvent(STOP_LOGGING, STOP_LOGGING_MSG);
        this.viewLoggingPanel = false;
        this.viewExistingCasePanel = false;
        this.showHideExistingCase(false);
        this.logoName = LOGO_DEFAULT;
        this.logTo = true;
        this.viewLogButtonClicked = false;
        if (this.logCollectedData.length > 0) {
            this.logCollectedData = [];
        }
        if (this.finalLogCollectionData.length > 0) {
            this.finalLogCollectionData = [];
        }
        if (Object.keys(this.Loggeddata).length > 0) {
            this.Loggeddata = {};
        }
    }

    handleMouseEnter(event) {
        this.showIconHoverMessage = true;
        if (this.loggingFlag || this.logoName === LOGO_ACTIVE) {
            this.logoName = LOGO_ACTIVE;
        }
        else {
            this.logoName = LOGO_HOVER;
        }
    }

    handleMouseLeave(event) {
        this.showIconHoverMessage = false;
        if (this.loggingFlag || this.logoName === LOGO_ACTIVE) {
            this.logoName = LOGO_ACTIVE;
        }
        else {
            this.logoName = LOGO_DEFAULT;
        }
    }

    createLog() {
        if (Object.keys(this.finallogdata).length > 0) {
            let logCaseId;
            if (this.logTo) {
                logCaseId = this.newCaseId;
            }
            else {
                logCaseId = this.existingCaseId;
            }
            sendLogData({
                logValues: JSON.stringify(this.finallogdata), caseId: logCaseId, keyvalue: this.attachmentkey,
                desvalue: this.attachmentdescription, type: this.type, subtype: this.subtype
            }).then(result => {
                if (result) {
                    this.showToast("Success!", 'Logging Finished', "success");
                    this.stopLogging();
                    if (this.autoLogging) {
                        this.startLoggingOnPageLoad();
                    }
                    //attach interaction to existing case
                    if (this.existingCaseId) {
                        this.attachInteraction(logCaseId);
                    }
                    //navigate new tab
                    this.navigateToPage(logCaseId);
                }
            }).catch(err => {
                console.log('Error Occured: ', err);
                this.stopLogging();
            });
        }
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }

    createFinalLog(finaldata, objkey) {
        let objtemp = {};
        objtemp[objkey] = finaldata;
        return objtemp;
    }

    captureLogValues() {
        let logdata = [];
        if (this.Loggeddata && Object.keys(this.Loggeddata).length > 0) {
            Object.keys(this.Loggeddata).forEach(k => {
                let temp = [];
                if (this.Loggeddata[k] && Array.isArray(this.Loggeddata[k])) {
                    this.Loggeddata[k].forEach(t => {
                        let logobject = {};
                        let sectionDetails = [];
                        let sectionObject = {};
                        if (t.loggedFields && Array.isArray(t.loggedFields)
                            && t.loggedFields.length > 0) {
                            logobject.Name = k;
                            let relatedField = [{
                                label: t.relatedField.RelatedField[0].label,
                                value: t.relatedField.RelatedField[0].value,

                                loggedFields: this.createFinalLoggedFields(t.loggedFields)
                            }];
                            logobject.relatedField = relatedField;
                            sectionDetails.push(logobject);
                            sectionObject.SectionDetails = sectionDetails;
                            logdata.push(sectionObject);
                        }
                    })
                }
                temp = null;
            })
        }
        if (this.LoggedSearchData && Object.keys(this.LoggedSearchData).length > 0) {
            Object.keys(this.LoggedSearchData).forEach(k => {
                let temp = [];
                if (this.LoggedSearchData[k] && Array.isArray(this.LoggedSearchData[k])) {
                    this.LoggedSearchData[k].forEach(t => {
                        let logobject = {};
                        let sectionDetails = [];
                        let sectionObject = {};
                        if (t.loggedFields && Array.isArray(t.loggedFields)
                            && t.loggedFields.length > 0) {
                            logobject.Name = k;
                            if (t.loggedFields[0].value) {

                            }
                            let relatedField = [{
                                label: t.relatedField.RelatedField[0].label,
                                value: t.relatedField.RelatedField[0].value,
                                loggedFields: t.loggedFields
                            }];
                            logobject.relatedField = relatedField;
                            sectionDetails.push(logobject);
                            sectionObject.SectionDetails = sectionDetails;
                            logdata.push(sectionObject);
                        }
                    })
                }
                temp = null;
            })
        }
        this.LoggedSearchData = [];
        this.finallogdata.LogData = logdata;
    }

    createNewCaseId(calledFrom) {
        if (this.logTo) {
            let interactionId = getInteractionId();
            createNewCase({ sObjectId: this.recordIdToSend, calledfrom: 'Logging', newInteractionId: interactionId })
                .then(result => {
                    if (result) {
                        let arrResult = result.toString().split('-');
                        this.newCaseId = arrResult[0];
                        this.showToast("Information", 'Currently logging to New Case ' + arrResult[1], "info");
                        if (calledFrom === BOTTON_LOG_AND_FINISH) {
                            this.createLog();
                        }
                    }
                })
                .catch(err => {
                    console.log('Error Occured: ', err);
                });
        }
        else {
            if (this.existingCaseId) {
                this.showToast("Information", 'Currently logging to Existing Case ' + this.existingCaseNumber, "info");
            }
        }
    }

    async navigateToPage(caseid) {
        if (this.newCaseId) {
            this.newCaseId = this.newCaseId ? null : this.newCaseId;
            let casedata = {};
            casedata.Id = caseid;
            casedata.objApiName = 'Case';
            openLWCSubtab('caseInformationComponentHum', casedata, { label: 'Edit Case', icon: 'standard:case' });
        }
        else if (this.existingCaseId) {
            this.existingCaseId = this.existingCaseId ? null : this.existingCaseId;
            this.navigateToCaseDetailsPage(caseid);
        }
    }

    async navigateToCaseDetailsPage(caseid) {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: caseid,
                objectApiName: 'Case',
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

    handleCaseCheckBoxSelect(event) {
        if (event && event?.detail) {
            this.newCaseId = null;
            let isChecked = event.detail.checked;
            if (isChecked) {
                this.existingCaseId = event.detail.selectedCaseId;
                this.existingCaseNumber = event.detail.selectedCaseNumber;
            }
            else {
                this.existingCaseId = null;
                this.existingCaseNumber = null;
            }

            if (this.logCollectedData.length > 0 && this.existingCaseId) {
                this.disableLogButton = false;
                this.disableLogFinishButton = false;
            } else {
                this.disableLogButton = true;
                this.disableLogFinishButton = true;
            }
        }
    }

    showHideExistingCase(showExistingCaseBox) {
        let existingCaseBox = this.template.querySelector(`[data-id="viewExistingCasePanelDiv"]`);
        if (existingCaseBox && showExistingCaseBox) {
            existingCaseBox.classList.replace("slds-hide", "slds-show");
        }
        if (existingCaseBox && !showExistingCaseBox) {
            existingCaseBox.classList.replace("slds-show", "slds-hide");
        }
    }

    async attachInteraction(logCaseId) {
        let attachInteractionResponse;
        attachInteractionToCase(logCaseId).then(result => {
            if (result) {
                attachInteractionResponse = result;
            }
        }).catch(error => {
            console.log(error);
        });
    }

    clearSearchLogData(eventData) {
        if (this.Loggeddata[eventData.Section]?.length > 0) {
            delete this.Loggeddata[eventData.Section];
            delete this.LoggedSearchData[eventData.Section];
            this.enableDisbleLogAndFinishButtons();
        }
    }

    enableDisbleLogAndFinishButtons() {
        let dataispresent = false;
        for (let obj in this.Loggeddata) {
            if (Object.prototype.hasOwnProperty.call(this.Loggeddata, obj)) {
                if (obj) {
                    if (this.Loggeddata[obj]) {
                        this.Loggeddata[obj].forEach(a => {
                            if (a?.loggedFields?.length > 0) {
                                dataispresent = true;
                            }
                        })
                    }
                }
            }
        }
        if (!dataispresent) {
            this.disableLogButton = true;
            this.disableLogFinishButton = true;
        }
    }
}