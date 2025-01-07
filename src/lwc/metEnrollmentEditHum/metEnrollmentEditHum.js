/*******************************************************************************************************************************
LWC JS Name : MetEnrollmentEditHum.js
Function    : This JS serves as controller to MetEnrollmentEditHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                           12/22/2022                 initial version(azure #)
* Pooja Kumbhar                                           01/17/2023                 DF - 6973 defect fix
* pooja kumbhar											  02/02/2023				 US 4224131 - Lightning - MET Case Edit - Sorting  & Case Details  Refresh MET Component
*********************************************************************************************************************************/

import { LightningElement, track, wire, api } from 'lwc';
import { getRecord } from 'lightning/uiRecordApi';
import uId from '@salesforce/user/Id'; //this is how you will retreive the USER ID of current in user.
import NAME_FIELD from '@salesforce/schema/User.Name';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {
    getMETEnrollTableLayout
} from './ConfigLayout';
import getCaseRecordsData from '@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.getCaseRecordsData';
import getMETRecords from '@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.getMETRecords';
import loadMETTask from "@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.loadMETTask";
import loadMETAction from "@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.loadMETAction";
import loadMETSource from "@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.loadMETSource";
import getMultipleMETEntries from '@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.getMultipleMETEntries';
import getMultipleMETTasks from '@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.getMultipleMETTasks';

export default class MetEnrollmentEditHum extends LightningElement {



    @track popOverCss = 'custom-help-text slds-popover slds-popover_tooltip slds-fall-into-ground hc-popover-top';
    @track showOrHidestyle = 'slds-size_2-of-12';
    helpText = 'Copy to Clipboard';
    sldsTable = "slds-table slds-table_cell-buffer slds-table_bordered slds-table_col-bordered ";
    warningMsg = 'Maximum of 5 Entries can be entered before hitting save.'
    metEntryAssoLabel = 'MET Entries Association';

    @track METData = [];
    @track AddedMETData = [];
    @track NewMETData = [];
    @track lstRows = [];
    @track METTaskData = [];
    @track METActionData = [];
    @track METSourceData = [];
    @track MetEntryAssoOptions = [];
    @track METcolumns = [];

    @track NewDataAvailable = false;
    @track NoMETData = false;
    @track isTrackID = false;
    @track showMETEntry = false;
    @track disableMETEntries = false;
    @track bIsWarningMsg = false;
    @track showTrackID = true;
    @track disableTask = false;
    @track disableAction = true;
    @track disableSource = true;
    @track disableAddEntry = false;
    @track isTaskError = false;
    @track isActionError = false;
    @track isSourceError = false;

    @track TrackID;
    @track Origin;
    @track Type;
    @track Subtype;
    @track dateTime;
    @track buttonclicked;
    @track buttonClickedCount = 0;
    @track UName;
    @track Taskvalue;
    @track Actionvalue;
    @track Sourcevalue;
    @track isSourceEMMEMap;
    @track cssTaskID;
    @track cssActionID;
    @track cssSourceID;
    MetEntryAssoVal;

    layoutData = {};

    @api recordId;
    @api
    get caseType() {
        return {}
    }
    set caseType(data) {
        if (data != '' && data != null && data != undefined) {
            this.Type = data.split('_')[0];
            if (this.bIsWarningMsg == true) this.bIsWarningMsg = false;
            if (this.type == '') {
                this.showMETEntry = false;
            } else {
                this.loadMETEnrollmentTasks(this.Type, this.Subtype);
                this.loadMultipleMETEntries(this.Type, this.Subtype)
            }


        }
    }

    @api
    get caseSubtype() {
        return {}
    }
    set caseSubtype(data) {
        if (data != '' && data != null && data != undefined) {
            this.Subtype = data.split('_')[0];
            if (this.bIsWarningMsg == true) this.bIsWarningMsg = false;
            if (this.Subtype == '') {
                this.disableAddEntry = true;
                this.disableTask = true;
            } else {
                this.disableAddEntry = false;
                this.disableTask = false;
                this.loadMETEnrollmentTasks(this.Type, this.Subtype);
                this.loadMultipleMETEntries(this.Type, this.Subtype);
            }

        }

    }
    @api
    get saveCase() {
        return {}
    }
    set saveCase(data) {
        if (data.split('_')[0] == 'true') {
            let validatemsg = '';
            validatemsg = this.isValidate('OnSave');
            if (validatemsg == true) {
                this.dispatchEvent(new CustomEvent('datafeed', {
                    detail: { error: '', msgOnsave: true }
                }));
                if (this.NewMETData.length > 0) {
                    this.dispatchEvent(new CustomEvent('datafeed', {
                        detail: { data: "{\"listMETTaskDTO\":" + JSON.stringify(this.NewMETData) + "}" }
                    }));
                }
            } else {
                this.dispatchEvent(new CustomEvent('datafeed', {
                    detail: { error: validatemsg, msgOnsave: true }
                }));
            }
        }
    }

    @wire(getRecord, {
        recordId: uId,
        fields: [NAME_FIELD]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.UName = data.fields.Name.value;
        }
    }

    METEnrollmentTableLayout() {
        this.layoutData = {};
        this.layoutData.CancelButton = {};
        if (this.NoMETData != true) {
            this.layoutData.CancelButton = {
                type: 'button-icon',
                label: 'Update Status',
                typeAttributes: {
                    iconName: { fieldName: 'iconName' },
                    label: { fieldName: 'label' },
                    name: { fieldName: 'name' },
                    title: { fieldName: 'title' },
                    disabled: { fieldName: 'StatusDisabled' },
                    value: { fieldName: 'value' },
                    iconPosition: 'left',
                    fixedWidth: 50

                }
            }

        } else {
            this.layoutData.CancelButton = { hideDefaultActions: true };
        }

        this.METcolumns = getMETEnrollTableLayout(this.layoutData);
    }

    get Taskoptions() {
        return this.METTaskData;
    }

    get Actionoptions() {
        return this.METActionData;
    }

    get Sourceoptions() {
        return this.METSourceData;
    }

    async connectedCallback() {
        if (this.recordId != '' && this.recordId != null && this.recordId != undefined) {
            await getCaseRecordsData({ sCaseRecordId: this.recordId })
                .then((data) => {
                    data = JSON.parse(data);
                    this.TrackID = data.Medicare_Track_ID__c;
                    if (this.TrackID != '' && this.TrackID != undefined && this.TrackID != null) {
                        this.isTrackID = true;
                    }
                    this.Origin = data.Origin;
                    if (this.Origin == "CRMTRR" && this.Type == "TRR Inquiry" && this.Subtype == "TRR") {
                        this.showTrackID = false;
                    }
                }).catch(error => {
                    this.dispatchEvent(
                        new ShowToastEvent({
                            title: 'Error! ' + error.message,
                            message: error.message,
                            variant: 'error',
                        }),
                    );
                });
            await this.loadMultipleMETEntries(this.Type, this.Subtype);

            await getMETRecords({ sCaseRecordId: this.recordId })
                .then((data) => {
                    data = JSON.parse(data);
                    let TempMETData = [];
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            let elt = {};
                            elt.id = data[i].sId;
                            elt.sTaskId = data[i].sTaskId;
                            elt.TaskName = data[i].sTask;
                            elt.ActionName = data[i].sAction;
                            elt.SourceName = data[i].sSource;
                            elt.sStatus = data[i].sStatus;
                            if (elt.sStatus == 'Cancelled') {
                                elt.StatusDisabled = true;

                            } else {
                                elt.StatusDisabled = false;

                            }
                            elt.iconName = 'utility:clear';
                            elt.label = 'Cancel';
                            elt.name = 'Cancel';
                            elt.title = 'Cancel';
                            elt.value = 'Cancel';
                            elt.sCreatedDate = this.formatDate(data[i].sCreatedByDate, false);
                            elt.CreatedByName = data[i].sCreatedByName + ' ' + elt.sCreatedDate;
                            elt.sCreatedByName = data[i].sCreatedByName;
                            elt.sCreatedBy = '/' + data[i].sCreatedById;
                            elt.sCreatedById = data[i].sCreatedById;
                            elt.sLastModifiedDate = this.formatDate(data[i].sLastModifiedByDate, false);
                            elt.LastModifiedByName = data[i].sLastModifiedByName + ' ' + elt.sLastModifiedDate;
                            elt.sLastModifiedByName = data[i].sLastModifiedByName
                            elt.sLastModifiedBy = '/' + data[i].sLastModifiedbyId;
                            elt.sLastModifiedbyId = data[i].sLastModifiedbyId;
                            elt.sCreatedDate_Sort = this.formatDate(data[i].sCreatedDate_Sort, true);
                            if (data[i].sCancelledByName != '' && data[i].sCancelledByName != null && data[i].sCancelledByName != undefined) {
                                elt.sCancelledByDate = this.formatDate(data[i].sCancelledByDate, false);
                                elt.CancelledByName = data[i].sCancelledByName + ' ' + elt.sCancelledByDate;
                                elt.sCancelledByName = data[i].sCancelledByName
                                elt.sCancelledBy = '/' + data[i].sCancelledById;
                                elt.sCancelledById = data[i].sCancelledById;
                            } else {
                                elt.sCancelledByDate = '';
                                elt.CancelledByName = '';
                                elt.sCancelledByName = '';
                                elt.sCancelledBy = '';
                                elt.sCancelledById = '';
                            }
                            TempMETData.push(elt);
                        }
                        this.totalRecords = data.length; // update total records count  
                        this.METData = TempMETData;
                        this.METData.sort(this.compare);

                    } else {
                        this.METData = [];
                        this.NoMETData = true;
                        this.sldsTable = '';
                    }

                }).catch(error => {
                    this.dispatchEvent(
                        new ShowToastEvent({
                            title: 'Error! ' + error.message,
                            message: error.message,
                            variant: 'error',
                        }),
                    );
                });
            await this.loadMETEnrollmentTasks(this.Type, this.Subtype);
            await this.METEnrollmentTableLayout();

        }
    }

    compare(a, b) {
        if (a.sCreatedDate_Sort < b.sCreatedDate_Sort) {
            return -1;
        }
        if (a.sCreatedDate_Sort > b.sCreatedDate_Sort) {
            return 1;
        }
        return 0;
    }

    loadMETEnrollmentTasks(Type, Subtype) {
        loadMETTask({ caseType: Type, caseSubType: Subtype })
            .then(result => {
                if (result) {
                    this.METTaskData = [];
                    for (let key in result) {
                        this.METTaskData.push({ label: result[key], value: key });
                    }
                    if (this.METTaskData.length == 0) {
                        this.METTaskData = [{ label: '--None--', value: '--None--' }];
                    }

                }
            })
            .catch(error => {
                this.error = error;
            });
    }

    loadMultipleMETEntries(Type, Subtype) {
        getMultipleMETEntries({ sCaseType: Type, sCaseSubType: Subtype })
            .then((data) => {
                this.MetEntryAssoOptions = [];
                if (data != '') {
                    this.showMETEntry = true;
                    this.MetEntryAssoOptions.push({ label: '--None--', value: '--None--' })
                    for (var i = 0; i < data.length; i++) {
                        this.MetEntryAssoOptions = this.MetEntryAssoOptions.concat({ label: data[i], value: data[i] });
                    }
                } else {
                    this.showMETEntry = false;
                }
            }).catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error! ' + error.message,
                        message: error.message,
                        variant: 'error',
                    }),
                );
            });

    }

    MetEntryAssoChange(event) {
        this.MetEntryAssoVal = event.detail.value;
        this.loadMETEnrollmentTasks(this.Type, this.Subtype);
        this.getMETTaskWithAssociation(this.MetEntryAssoVal);

    }

    getMETTaskWithAssociation(METAssociationvalueSelected) {
        this.METTaskData = [];
        getMultipleMETTasks({ sCaseType: this.Type, sCaseSubType: this.Subtype, sSelectedValue: METAssociationvalueSelected })
            .then((data) => {
                data = JSON.parse(data);
                this.AddedMETData = [];
                if (data.length > 0) {
                    if (this.NewMETData.length + data.length > 5) {
                        this.bIsWarningMsg = true;
                    } else {
                        this.bIsWarningMsg = false;
                        for (var i = 0; i < data.length; i++) {
                            this.dateTime = this.getCurrentDateTime();
                            this.buttonClickedCount++;
                            let elt = {};
                            elt.id = this.NewMETData.length + 1;
                            elt.sRowNum = this.NewMETData.length + 1;
                            elt.TaskName = data[i].sTaskName;
                            elt.ActionName = data[i].sActionName;
                            elt.SourceName = data[i].sSourceName;
                            elt.sTask = data[i].sTaskId;
                            elt.sAction = data[i].sActionId;
                            elt.sSource = data[i].sSourceId;
                            elt.isSourceEMMEMap = data[i].bLaunchEMME
                            elt.sStatus = 'Completed';
                            elt.sTaskId = null;
                            if (elt.sStatus == 'Cancelled') {
                                elt.StatusDisabled = true;

                            } else {
                                elt.StatusDisabled = false;

                            }
                            elt.iconName = 'utility:clear';
                            elt.label = 'Cancel';
                            elt.name = 'Cancel';
                            elt.title = 'Cancel';
                            elt.value = 'Cancel';
                            elt.sCreatedDate = this.formatDate(this.dateTime, false);
                            elt.CreatedByName = this.UName + ' ' + elt.sCreatedDate;
                            elt.sCreatedByName = this.UName;
                            elt.sCreatedBy = '/' + uId;
                            elt.sCreatedById = uId;
                            elt.sLastModifiedDate = this.formatDate(this.dateTime, false);
                            elt.LastModifiedByName = this.UName + ' ' + elt.sLastModifiedDate;
                            elt.sLastModifiedByName = this.UName;
                            elt.sLastModifiedBy = '/' + uId;
                            elt.sLastModifiedById = uId;

                            elt.CancelledByName = '';
                            elt.sCancelledByName = '';
                            elt.sCancelledBy = '';
                            elt.sCancelledById = '';
                            elt.sCreatedDate_Sort = this.getCurrentDateTime() + ':' + (new Date()).getMilliseconds();
                            if (this.AddedMETData.length > 0) {
                                this.AddedMETData = this.AddedMETData.concat(elt);
                            } else {
                                this.AddedMETData.push(elt);
                            }
                            if (this.NewMETData.length > 0) {
                                this.NewMETData = this.NewMETData.concat(elt);
                            } else {
                                this.NewMETData.push(elt);
                            }
                        }
                    }
                    let temMETData = [];
                    if (this.AddedMETData.length > 0) {
                        this.NewDataAvailable = true;

                        if (this.NoMETData == true) {
                            this.METData = [];
                            this.METData = this.AddedMETData;
                            this.NoMETData = false;
                            this.METEnrollmentTableLayout()
                        } else {

                            temMETData = this.METData;
                            temMETData = temMETData.concat(this.AddedMETData);
                            this.METData = [];
                            this.METData = temMETData;
                        }

                    }
                    if (this.buttonClickedCount > 4) {
                        this.buttonClicked = false;
                        this.disableAddEntry = true;
                        this.disableTask = true;
                        this.disableMETEntries = true;
                    }
                }
            }).catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error! ' + error.message,
                        message: error.message,
                        variant: 'error',
                    }),
                );
            });
    }

    async handleTaskChange(event) {

        this.Taskvalue = event.detail.value;
        this.METTask = event.target.options.find(opt => opt.value === event.detail.value).label;
        this.Actionvalue = '';
        this.Sourcevalue = '';
        this.isSourceEMMEMap = '';
        this.cssTaskID = '';
        this.isTaskError = false;

        if (this.Taskvalue != '' && this.METTask != '' && this.Taskvalue != '--None--') {
            this.disableAction = false;
            await loadMETAction({ caseType: this.Type, caseSubType: this.Subtype, selectedMETTaskId: this.Taskvalue })
                .then(result => {
                    if (result) {
                        this.METActionData = [];
                        for (let key in result) {
                            this.METActionData.push({ label: result[key], value: key });
                        }

                        if (this.METActionData.length == 1) {
                            this.Actionvalue = this.METActionData[0].value;
                            this.METAction = this.METActionData[0].label;
                            this.cssActionID = '';
                            this.isActionError = false;
                            this.disableSource = false;

                            loadMETSource({ caseType: this.Type, caseSubType: this.Subtype, selectedMETTaskId: this.Taskvalue, selectedMETActionId: this.Actionvalue })
                                .then(result => {
                                    if (result) {
                                        let value = '';
                                        this.METSourceData = [];
                                        for (let key in result) {
                                            value = key;
                                            for (let skey in result[key]) {
                                                this.METSourceData.push({ label: skey, value: value, isSourceEMMEMap: result[key][skey] });
                                            }
                                        }
                                        if (this.METSourceData.length == 1) {
                                            this.Sourcevalue = this.METSourceData[0].value;
                                            this.METSource = this.METSourceData[0].label;
                                            this.isSourceEMMEMap = this.METSourceData[0].isSourceEMMEMap;
                                            this.cssSourceID = '';
                                            this.isSourceError = false;
                                        }

                                    }
                                })
                                .catch(error => {
                                    this.error = error;
                                });

                        }


                    }
                })
                .catch(error => {
                    this.error = error;
                });
        }


    }


    async handleActionChange(event) {
        this.Actionvalue = event.detail.value;
        this.METAction = event.target.options.find(opt => opt.value === event.detail.value).label;
        this.Sourcevalue = '';
        this.cssActionID = '';
        this.isSourceEMMEMap = '';
        this.isActionError = false;

        if (this.Actionvalue != '' && this.METAction != '') {
            this.disableSource = false;

            await loadMETSource({ caseType: this.Type, caseSubType: this.Subtype, selectedMETTaskId: this.Taskvalue, selectedMETActionId: this.Actionvalue })
                .then(result => {
                    if (result) {
                        let value = '';
                        this.METSourceData = [];
                        for (let key in result) {
                            value = key;
                            for (let skey in result[key]) {
                                this.METSourceData.push({ label: skey, value: value, isSourceEMMEMap: result[key][skey] });
                            }
                        }
                        if (this.METSourceData.length == 1) {
                            this.Sourcevalue = this.METSourceData[0].value;
                            this.METSource = this.METSourceData[0].label;
                            this.isSourceEMMEMap = this.METSourceData[0].isSourceEMMEMap;
                            this.cssSourceID = '';
                            this.isSourceError = false;
                        }

                    }
                })
                .catch(error => {
                    this.error = error;
                });
        }

    }

    handleSourceChange(event) {
        this.Sourcevalue = event.detail.value;
        this.METSource = event.target.options.find(opt => opt.value === event.detail.value).label;
        this.isSourceEMMEMap = event.target.options.find(opt => opt.value === event.detail.value).isSourceEMMEMap;
        this.cssSourceID = '';
        this.isSourceError = false;
    }

    async handleNewEntryClick() {
        let validatemsg = '';
        validatemsg = this.isValidate('OnAddEntry');
        if (validatemsg == true) {
            this.buttonClickedCount++;
            this.lstRows.push({ id: this.buttonClickedCount });
            if (this.buttonClickedCount >= 0 && this.buttonClickedCount <= 5) {
                if (this.bIsWarningMsg == true) this.bIsWarningMsg = false;
                await this.loadMETEnrollmentTasks(this.Type, this.Subtype);
                if (this.METTask != '' && this.METAction != '' && this.METSource != '') {
                    this.dateTime = this.getCurrentDateTime();
                    this.AddedMETData = [];
                    let elt = {};
                    elt.id = this.buttonClickedCount;
                    elt.sRowNum = this.buttonClickedCount;
                    elt.TaskName = this.METTask;
                    elt.ActionName = this.METAction;
                    elt.SourceName = this.METSource;
                    elt.sTask = this.Taskvalue;
                    elt.sAction = this.Actionvalue;
                    elt.sSource = this.Sourcevalue;
                    elt.isSourceEMMEMap = this.isSourceEMMEMap
                    elt.sStatus = 'Completed';
                    elt.sTaskId = null;
                    if (elt.sStatus == 'Cancelled') {
                        elt.StatusDisabled = true;

                    } else {
                        elt.StatusDisabled = false;

                    }
                    elt.iconName = 'utility:clear';
                    elt.label = 'Cancel';
                    elt.name = 'Cancel';
                    elt.title = 'Cancel';
                    elt.value = 'Cancel';
                    elt.sCreatedDate = this.formatDate(this.dateTime, false);
                    elt.CreatedByName = this.UName + ' ' + elt.sCreatedDate;
                    elt.sCreatedByName = this.UName;
                    elt.sCreatedBy = '/' + uId;
                    elt.sCreatedById = uId;
                    elt.sLastModifiedDate = this.formatDate(this.dateTime, false);
                    elt.LastModifiedByName = this.UName + ' ' + elt.sLastModifiedDate;
                    elt.sLastModifiedByName = this.UName;
                    elt.sLastModifiedBy = '/' + uId;
                    elt.sLastModifiedById = uId;

                    elt.CancelledByName = '';
                    elt.sCancelledByName = '';
                    elt.sCancelledBy = '';
                    elt.sCancelledById = '';
                    elt.sCreatedDate_Sort = this.getCurrentDateTime() + ':' + (new Date()).getMilliseconds();

                    this.AddedMETData.push(elt);
                    this.NewMETData.push(elt);
                }
                let temMETData = [];
                if (this.AddedMETData.length > 0) {
                    this.NewDataAvailable = true;
                    if (this.NoMETData == true) {
                        this.METData = [];
                        this.METData = this.AddedMETData;
                        this.NoMETData = false;
                        this.METEnrollmentTableLayout()
                    } else {

                        temMETData = this.METData;
                        temMETData = temMETData.concat(this.AddedMETData);
                        this.METData = [];
                        this.METData = temMETData;
                    }
                    this.Taskvalue = '';
                    this.METTask = '';
                    this.Actionvalue = '';
                    this.METAction = '';
                    this.Sourcevalue = '';
                    this.METSource = '';
                    this.isSourceEMMEMap = '';
                    this.disableAction = true;
                    this.disableSource = true;
                    this.METActionData = [];
                    this.METSourceData = [];
                }
            }
            this.dispatchEvent(new CustomEvent('datafeed', {
                detail: { error: '', msgOnsave: false }
            }));
        } else {
            this.dispatchEvent(new CustomEvent('datafeed', {
                detail: { error: validatemsg, msgOnsave: false }
            }));

        }
        if (this.buttonClickedCount > 4) {
            this.buttonClicked = false;
            this.disableAddEntry = true;
            this.disableTask = true;
            this.disableMETEntries = true;
        }

    }

    formatDate(dateInStr, forSort) {
        var finalDateTime = '';
        if (dateInStr != '' && dateInStr != null) {
            var dateTimeSplit = dateInStr.split(" ");
            var dateInStr = dateTimeSplit[0].split("-");
            var timeInStr = dateTimeSplit[1].split(":");
            var vDate = new Date(dateInStr[0], dateInStr[1] - 1, dateInStr[2]);
            var format = "AM";
            var hour = timeInStr[0];
            var min = timeInStr[1];
            var sec = timeInStr[2];
            if (forSort) {
                var millisec = timeInStr[3];
                finalDateTime = (new Date(dateInStr[0], dateInStr[1] - 1, dateInStr[2], hour, min, sec, millisec)).getTime();
            } else {
                if (hour > 11) format = "PM";
                if (hour > 12) hour = hour - 12;
                if (hour == 0) hour = 12;
                finalDateTime = (vDate.getMonth() + 1) + "/" + vDate.getDate() + "/" + vDate.getFullYear() + " " + hour + ":" + min + ":" + sec + " " + format;
            }
        }
        return finalDateTime;
    }

    getCurrentDateTime() {
        var currentDate = new Date();
        var date = currentDate.getFullYear() + '-' + this.addZero(currentDate.getMonth() + 1) + '-' + this.addZero(currentDate.getDate());
        var time = currentDate.getHours() + ':' + this.addZero(currentDate.getMinutes()) + ':' + this.addZero(currentDate.getSeconds());
        var dateTimeStamp = date + " " + time;
        return dateTimeStamp;
    }
    addZero(inpStr) {
        if (inpStr < 10)
            return '0' + inpStr;
        else
            return inpStr;
    }

    handleRowAction(event) {
        if (event.detail.row.iconName === 'utility:clear') {
            event.detail.row.sStatus = 'Cancelled';
            event.detail.row.StatusDisabled = false;
            event.detail.row.iconName = 'utility:success';
            event.detail.row.label = 'Complete';
            event.detail.row.name = 'Complete';
            event.detail.row.title = 'Complete';
            event.detail.row.value = 'Complete';
            event.detail.row.colortext = 'slds-text-color_error';
            this.METData = [...this.METData];
            if (event.detail.row.id >= 0 && event.detail.row.id <= 4) {
                this.NewMETData = [...this.NewMETData];
            } else {
                if (this.NewMETData.length > 0) {
                    this.NewMETData = this.NewMETData.concat(event.detail.row);
                } else {
                    this.NewMETData.push(event.detail.row);
                }
                this.NewMETData = [...this.NewMETData];
            }

            this.dispatchEvent(
                new ShowToastEvent({
                    title: 'Success!',
                    message: 'Task status updated to cancelled',
                    variant: 'Success',
                }),
            );
        } else if (event.detail.row.iconName === 'utility:success') {
            event.detail.row.sStatus = 'Completed';
            event.detail.row.StatusDisabled = false;
            event.detail.row.iconName = 'utility:clear';
            event.detail.row.label = 'Cancel';
            event.detail.row.name = 'Cancel';
            event.detail.row.title = 'Cancel';
            event.detail.row.value = 'Cancel';
            event.detail.row.colortext = 'slds-text-color_success';
            this.METData = [...this.METData];
            if (event.detail.row.id >= 0 && event.detail.row.id <= 4) {
                this.NewMETData = [...this.NewMETData];
            } else {
                if (this.NewMETData.length > 0) {
                    for (var i = 0; i < this.NewMETData.length; i++) {
                        if (this.NewMETData[i].sTaskId == event.detail.row.sTaskId) {
                            this.NewMETData.splice(i, 1);
                            break;
                        }
                    }
                }
            }

            this.dispatchEvent(
                new ShowToastEvent({
                    title: 'Success!',
                    message: 'Task status updated to completed',
                    variant: 'Success',
                }),
            );
        }

    }

    isValidate(Frombutton) {
        if (Frombutton == 'OnSave') {
            if ((this.Taskvalue != '' && this.Taskvalue != null && this.Taskvalue != undefined) && (this.Actionvalue == '' || this.Actionvalue == null || this.Actionvalue == undefined)) {
                this.cssTaskID = '';
                this.cssActionID = 'slds-has-error';
                this.cssSourceID = 'slds-has-error';
                this.isTaskError = false;
                this.isActionError = true;
                this.isSourceError = true;
                return 'Required fields do not have values';
            } else if ((this.Actionvalue != '' && this.Actionvalue != null && this.Actionvalue != undefined) && (this.Sourcevalue == '' || this.Sourcevalue == null || this.Sourcevalue == undefined)) {
                this.cssTaskID = '';
                this.cssActionID = '';
                this.cssSourceID = 'slds-has-error';
                this.isTaskError = false;
                this.isActionError = false;
                this.isSourceError = true;
                return 'Required fields do not have values';
            } else {
                this.cssTaskID = '';
                this.cssActionID = '';
                this.cssSourceID = '';
                this.isTaskError = false;
                this.isActionError = false;
                this.isSourceError = false;
                return true;
            }
        }
        if (Frombutton == 'OnAddEntry') {
            if (this.Taskvalue == '' || this.Taskvalue == null || this.Taskvalue == undefined) {
                this.cssTaskID = 'slds-has-error';
                this.cssActionID = 'slds-has-error';
                this.cssSourceID = 'slds-has-error';
                this.isTaskError = true;
                this.isActionError = true;
                this.isSourceError = true;
                return 'A selection is needed in the required fields: Task,  Action, Source';

            } else if ((this.Taskvalue != '' && this.Taskvalue != null && this.Taskvalue != undefined) && (this.Actionvalue == '' || this.Actionvalue == null || this.Actionvalue == undefined)) {
                this.cssTaskID = '';
                this.cssActionID = 'slds-has-error';
                this.cssSourceID = 'slds-has-error';
                this.isTaskError = false;
                this.isActionError = true;
                this.isSourceError = true;
                return 'A selection is needed in the required fields: Action, Source';
            } else if ((this.Actionvalue != '' && this.Actionvalue != null && this.Actionvalue != undefined) && (this.Sourcevalue == '' || this.Sourcevalue == null || this.Sourcevalue == undefined)) {
                this.cssTaskID = '';
                this.cssActionID = '';
                this.cssSourceID = 'slds-has-error';
                this.isTaskError = false;
                this.isActionError = false;
                this.isSourceError = true;
                return 'A selection is needed in the required fields: Source';
            } else {
                this.cssTaskID = '';
                this.cssActionID = '';
                this.cssSourceID = '';
                this.isTaskError = false;
                this.isActionError = false;
                this.isSourceError = false;
                return true;
            }
        }

    }
    CopyToClipboard() {
        let CopyMe = this.template.querySelector('.copy-Me');
        CopyMe = CopyMe.value;
        var hiddenInput = document.createElement("input");
        hiddenInput.setAttribute("value", CopyMe);

        //Append Hidden Input to the body
        document.body.appendChild(hiddenInput);
        hiddenInput.select();

        // Executing the copy command
        document.execCommand("copy");
        document.body.removeChild(hiddenInput);

        this.dispatchEvent(
            new ShowToastEvent({
                title: 'This text has been copied',

                variant: 'Success',
            }),
        );

    }

    onMouseOut() {
        this.bMouseOutTracker = true;
        this.toggleToolTip('slds-fall-into-ground', 'slds-rise-from-ground');
    }

    omMouseOver() {
        this.bMouseOutTracker = false;
        this.toggleToolTip('slds-rise-from-ground', 'slds-fall-into-ground');
    }
    toggleToolTip(addClass, removeClass) {

        if (this.helpText) {
            const tooltipEle = this.template.querySelector('[data-custom-help="custom-help"]');
            tooltipEle.classList.add(addClass);
            tooltipEle.classList.remove(removeClass);
        }
    }



}