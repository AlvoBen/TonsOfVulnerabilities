/*
LWC Name        : custom_dashboard_SupHum.js
Function        : Parent LWC component for custom_filter, Custom_dashboard_table. Used to show case/ Task dashboard page for Supervisor profile

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh B                      07/03/2022                    Original Version 
* Gowthami T                      07/03/2022                    Original Version 
*****************************************************************************************************************************/
const obj = {
    label: '',
    value: '',
    checked: false
}
import { api, LightningElement, track, wire } from 'lwc';
import transferCase_QAAerror from '@salesforce/label/c.transferCase_QAAerror';
import transferCase_updateError from '@salesforce/label/c.transferCase_updateError';
import checkUserQueue from '@salesforce/apex/Hum_CaseTasks_LWC.checkUserQueue';
import checkQAA from '@salesforce/apex/Hum_CaseTasks_LWC.checkQAA';
import getQueueList from '@salesforce/apex/HUMQueueSelection_LWC.getQueueList';
import changeCaseOwner from '@salesforce/apex/Hum_CaseTasks_LWC.changeCaseOwner';
import transferCase_SuccessMsg from '@salesforce/label/c.transferCase_SuccessMsg';
import ccprofileLabel from '@salesforce/label/c.HUMAgencyCCSupervisor';
import saveQueue from '@salesforce/apex/HUMQueueSelection_LWC.saveQueue';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import CloseCase_LimitError from '@salesforce/label/c.CloseCase_LimitError';
import HUMWorkQueueNotPresentMessage from '@salesforce/label/c.HUMWorkQueueNotPresentMessage';
import transferCase_LimitError from '@salesforce/label/c.transferCase_LimitError';
import Case_Task_AcceptSelected_Hum from '@salesforce/label/c.Case_Task_AcceptSelected_Hum';
import Close_case_update_error from '@salesforce/label/c.Close_case_update_error';
import Hum_Accept_25_Validation from '@salesforce/label/c.Hum_Accept_25_Validation';
import NoWork_CaseTaskView_HUM from '@salesforce/label/c.NoWork_CaseTaskView_HUM';
import NoWork_CCSupervisor_CaseTaskView_HUM from '@salesforce/label/c.NoWork_CCSupervisor_CaseTaskView_HUM';
import sPharmacyUserSupervisorYesLabel from '@salesforce/label/c.PharmacyUserSupervisorYes_HUM';
import transferCase_QMSerr from '@salesforce/label/c.transferCase_QMSerr';
import CloseCase_SaveErrMsg from '@salesforce/label/c.CloseCase_SaveErrMsg';
import getPicklistValues from '@salesforce/apex/ChangeCaseOwner_C_HUM_LWC.getPicklistValuesWrp';
import getTransferReason from '@salesforce/apex/ChangeCaseOwner_C_HUM_LWC.getTransferReason';
import { getColOptionalFields, getColStaticFields, getTotalColumns } from 'c/dashboardUtilityHum';
import Transfer_Tooltip_Hum from '@salesforce/label/c.Transfer_Tooltip_Hum';
export default class custom_dashboard_SupHum extends LightningElement {
    label = {
        CloseCase_LimitError,
        Close_case_update_error,
        Case_Task_AcceptSelected_Hum,
        transferCase_QAAerror,
        transferCase_updateError,
        ccprofileLabel,
        NoWork_CaseTaskView_HUM,
        NoWork_CCSupervisor_CaseTaskView_HUM,
        sPharmacyUserSupervisorYesLabel,
        transferCase_SuccessMsg,
        transferCase_LimitError,
        transferCase_QMSerr,
        CloseCase_SaveErrMsg,
        HUMWorkQueueNotPresentMessage,
        Hum_Accept_25_Validation
    };
    @api isUserSupervisor;
    value = [];
    @track sColumnSet = [];
    _userName = null;
    _userId = null;
    mapCaseDTO = {};
    disableTopic = true;
    selectedValues = [];
    filterFlag;
    JsonMap = {};
    sSecurityGroup;
    @track changeOwnerCombo = {};
    @track newColumns = [];
    isSupervisor;
    isTaskSelected;
    Transfer_Tooltip_Hum =Transfer_Tooltip_Hum;
    pageCount = 1;
    totalCount;
    disableUserLookup = false;
    unclosedCases = [];
    @track isLoading = false;
    idsToPush = [];
    @track valueForClosedStatus = 'Closed';
    isCaseClose = false;
    isCaseTransfer = false;
    isModalOpen = false;
    isQueueModalOpen = false;
    enableCloseButton = true;
    enableTransferButton = true;
    @track selectedRecords;
    caseCount = 0;
    isDialogVisible = false;
    sPharmacyUser;
    sSelectedWorkQueue;
    buttonName;
    popUpError = false;
    popUpErrorMessage;
    @track PicklistValues;
    isEditUnable = false;
    @track column1 = [];
    isLoading = true;
    caseUpdateErrorMessage;
    showCaseUpdateError = false;
    @track unclosedCases = [];
    caseCountExceeded = false;
    storeCaseCmmt = '';
    //introduces to update search results when Tranfer case happens
    originalCaseTaskList = []
    @track selectedRecord=null;
    caseCountColorClass= '';
    taskCountColorClass = '';

    connectedCallback() {
        this.isSupervisor = this.isUserSupervisor;
    }
    @wire(getTransferReason)
    wiredData({ error, data }) {
        if (data) {
            this.changeOwnerCombo['Transfer_Reason'] = this.getListvalues(data);
            this.changeOwnerCombo['transValue'] = this.changeOwnerCombo['Transfer_Reason'][0].value;
            this.isLoading = false;
        } else if (error) {
        }
    }

    get columns() {
        return this.column1;
    }

    /*
    * method to prepare label, value object format, format - value , label
    */
    getListvalues(passedObj) {
        let queue = [];
        for (let key of passedObj) {
            const emp = Object.create(obj);
            emp.label = key;
            emp.value = key;
            queue.push(emp);
        }
        return queue;
    }

    handlechildClick(event) {
        this.newColumns = [];
        event.detail.multiple.forEach(element => {
            if (element.selected && element.hasOwnProperty('selected') && !this.newColumns.some(el => el.label === element.label) && element.value !== 'selectall') {
                this.newColumns.push({
                    label: element.label,
                    fieldName: element.value,
                    initialWidth: element.hasOwnProperty('initialWidth') ? element.initialWidth : '',
                    type: element.hasOwnProperty('type') ? element.type : 'text',
                    typeAttributes: element.hasOwnProperty('typeAttributes') ? element.typeAttributes : '',
                    cellAttributes: element.hasOwnProperty('cellAttributes') ? element.cellAttributes : '',
                    sortable: true,
                    wrapText: true,
                });
            }
        });
        const objChild = this.template.querySelector('c-custom_dashboard_table_-sup-hum');
        objChild.addDeleteColumns(this.newColumns);
    }

    /*
    * Method to get current profile and other values through event
    */
    handleProfile(e) {
        this.showCaseUpdateError = false;

        if (e.detail.currentProfile) {
            this.currentProfile = e.detail.currentProfile;

        }
        if (e.detail.sPharmacyUser)
            this.sPharmacyUser = e.detail.sPharmacyUser;
        if (e.detail.sSecurityGroup)
            this.sSecurityGroup = e.detail.sSecurityGroup;

        //profile wise permissions and fields 
        let columns = {};
        getTotalColumns(this.currentProfile).then(result => {
            columns = result;
        }).then(() => {
            this.column1 = columns.staticColumns;

            let options = columns.optColumns;
            this.template.querySelector('c-custom-multi-select-combobox-hum')?.setMenuItems(options);
            this.column1.forEach(item => {
                if (item.hasOwnProperty('typeAttributes')) {
                    item.typeAttributes.hasOwnProperty('label') &&
                        item.typeAttributes.label.hasOwnProperty('fieldName') ?
                        this.sColumnSet.push(item.typeAttributes.label.fieldName) : this.sColumnSet.push(item.fieldName);
                }
                else if (item.hasOwnProperty('cellAttributes')) { //for adding red color to view
                    item.cellAttributes.hasOwnProperty('class') &&
                        item.cellAttributes.class.hasOwnProperty('fieldName') ?
                        this.sColumnSet.push(item.cellAttributes.class.fieldName) : this.sColumnSet.push(item.fieldName);
                }
                else {
                    this.sColumnSet.push('' + item.fieldName + '');
                }

            });
            this.filterFlag = e.detail.filterFlag;
            if (e.detail.totalCount)
                this.totalCount = e.detail.totalCount;
            this.pageCount = 1;
            //clear existing rows
            this.clearRows();
            this.template.querySelector('lightning-input').value = null;
        }).catch(error => {
            console.log(error);
        });

    }

    handlePageCount(event) {
        this.pageCount = event.detail.pageCount;
        this.totalCount = event.detail.totalRecountCount;
    }
    /*
    * Method handles Accepted Selected functionality
    */
    handleAcceptSelected(event) {
        //clear validations on page
        this.showCaseUpdateError = false;
        let obj = {};
        if (this.caseCount === 0 || this.caseCount === undefined) {
            this.showToast('Error', this.label.Case_Task_AcceptSelected_Hum, 'Error', 'dismissible', null);
            this.clearRows();

            return;
        }
        if (this.caseCount <= 25 && this.caseCount > 0) {
            obj = this.template.querySelector('c-custom_filter_-sup-hum').filteredValuesToParent();
            this.template.querySelector('c-custom_dashboard_table_-sup-hum').handleAcceptSelectedChild(obj);
            //clear search text
            this.template.querySelector('lightning-input[data-attr = "cmt"]').value = null;

        } else {
            //show error if more than 25 cases are selected
            this.showToast('Error', this.label.Hum_Accept_25_Validation, 'Error', 'dismissible', null);
            this.unclosedCases = [];
            //clear selected rows
            this.clearRows();
        }
    }
    /*
    * Method called to enable/disable case close and change case owner buttons
    */
    onRowSelectEvent(event) {
        this.caseCount = event.detail.cCount;
        this.isTaskSelected = event.detail.isTaskSelected;
        this.idsToPush = event.detail.idsToPush;
        this.mapCaseDTO = event.detail.mapCaseDTO;
        this.changeOwnerCombo['transValue'] = this.changeOwnerCombo['Transfer_Reason'][0].value;
        this.sSelectedWorkQueue = null;
        //disable topic on row select 
        this.disableTopic = true;
        if (this.caseCount === 0) {

        }
        //we need count of selected rows >1 and all cases should be selected thats why !this.isTaskSelected
        if (this.caseCount >= 2 && !this.isTaskSelected && this.filterFlag) {
            this.enableCloseButton = false;
            this.enableTransferButton = false;
        } else if (this.caseCount >= 2 && !this.isTaskSelected) {
            this.enableCloseButton = false;
            this.enableTransferButton = true;
        } else {
            this.enableCloseButton = true;
            this.enableTransferButton = true;
        }
    }

    /*
    * Method called when close case / change case owner button clicked
    */
    handleButtonClick(event) {
        this.isLoading = true;
        //clear the existing value
        this.changeOwnerCombo['sValue'] = [];
        this.changeOwnerCombo['dValue'] = null;
        this.changeOwnerCombo['tValue'] = null;
        this.buttonName = event.target.dataset.name;
        this.valueForClosedStatus = 'Closed';
        this.showCaseUpdateError = false;

        if (this.caseCount <= 25) {
            if (this.buttonName == 'transferCase') {
                //for this case, first call checkQAA is soql returns 0 rows call  this.checkUserQueue(buttonName);
                checkQAA({
                    lstCaseDTO: this.idsToPush
                }).then(result => {
                    if (result != null) {
                        if (result.length > 0) {
                            //giver error msg with links
                            this.showCaseUpdateError = true;
                            this.caseUpdateErrorMessage = this.label.transferCase_QAAerror;
                            let unclosedCases = [];
                            for (let i = 0; i < result.length; i++) {
                                let res = result[i].split('+');
                                unclosedCases.push({
                                    'id': res[0],
                                    'number': res[1],
                                    'viewUrl': '/lightning/r/Case/' + res[0] + '/view'
                                });

                            }
                            this.unclosedCases = unclosedCases;
                            //this.template.querySelector('c-custom_dashboard_table_-sup-hum').clearRows();
                            this.clearRows();

                        } else {
                            this.checkUserQueue(this.buttonName);
                        }
                    } else {
                        this.showToast('Warning', this.label.transferCase_QAAerror, 'warning', 'dismissible', null);
                    }
                    this.isLoading = false;
                })
                    .catch(error => {

                        this.isLoading = false;
                    })
            } else if (this.buttonName == 'closeCase') {
                this.checkUserQueue(this.buttonName);
            }
        } else {
            //error message Case_LimitError
            this.isLoading = false
            if (this.buttonName === 'closeCase')
                this.showToast('Error', this.label.CloseCase_LimitError, 'Error', 'dismissible', null);
            if (this.buttonName === 'transferCase')
                this.showToast('Error', this.label.transferCase_LimitError, 'Error', 'dismissible', null);
            this.unclosedCases = [];
            //make close and transfer disable
            this.enableCloseButton = true;
            this.enableTransferButton = true;
            //clear rows
            this.clearRows();
        }
    }
    /*
    * Method handles close modal
    */
    closeModal() {
        // to close modal set ModalOpen track value as false
        this.isModalOpen = false;
        this.isQueueModalOpen = false;
    }
    /*
    * Method handles onclick of Cancel/continue buttons on pop up
    */
    handleClick(event) {
        let buttonName = event.target.dataset.name;
        if (buttonName === 'cancel') {
            this.isDialogVisible = false;
            this.isModalOpen = true;
            this.popUpError = false;

        } else if (buttonName === 'continue') {

            this.storeCaseCmmt = null;
            this.isDialogVisible = false;
            this.isModalOpen = false;
            // clear selectd rows and clear the error messages
            this.clearRows();
            //disable buttons
            this.enableCloseButton = true;
            this.enableTransferButton = true
            this.popUpError = false;
            this.popUpErrorMessage = null;
            this.selectedRecord = null;
        }
    }
    /*
    * Method handles dept value if not selected
    */
    handleBlurDeptValidation(event){
        let deptCmp = this.template.querySelector('lightning-combobox[data-id="'+event.target.dataset.id+'"]');

        if (deptCmp.value == '' ||  deptCmp.value == null) {
            let _msgToDispl = '';
            if(event.target.dataset.id === 'Service Center')
                _msgToDispl = 'Service Center value is required'
            else if(event.target.dataset.id === 'Department')
                _msgToDispl = 'Department value is required';

            deptCmp.setCustomValidity(_msgToDispl);
            this.isValidationOccured = true;
            deptCmp.reportValidity();
        }

    }
    /*
    * Method handles populating picklist values on change case owner pop up
    */
    handleChangePicklist(event) {
        this.isLoading = true;
        //this.checkFieldValidation();
        if (event.target.dataset.id === 'Service Center') {
            let searchCmp = this.template.querySelector('lightning-combobox[data-id="Service Center"]');
            //let deptCmp = this.template.querySelector('lightning-combobox[data-id="Department"]');

            if (!searchCmp.value || (Array.isArray(searchCmp.value) && searchCmp.value.length === 0)) {
                searchCmp.setCustomValidity("Service Center value is required");
                this.isValidationOccured = true;
            } else {
                searchCmp.setCustomValidity(""); // clear previous value
                this.isValidationOccured = false;
            }
            searchCmp.reportValidity();

            this.changeOwnerCombo['sValue'] = event.detail.value;
            this.changeOwnerCombo['dValue'] = null
            this.getChangeOwnerPicklists(event.detail.value, this.changeOwnerCombo['dValue'] !== null ? this.changeOwnerCombo['dValue'] : 'None', false)
        } else if (event.target.dataset.id === 'Department') {
            let deptCmp = this.template.querySelector('lightning-combobox[data-id="Department"]');
            if (!deptCmp.value) {
                deptCmp.setCustomValidity("Department value is required");
                this.isValidationOccured = true;
            } else {
                deptCmp.setCustomValidity(""); // clear previous value
                this.isValidationOccured = false;
            }
            deptCmp.reportValidity();


            this.changeOwnerCombo['dValue'] = event.detail.value;
            this.changeOwnerCombo['Topic'] = null
            this.getChangeOwnerPicklists(this.changeOwnerCombo['sValue'] !== null ? this.changeOwnerCombo['sValue'] : 'None', event.detail.value, true)
        } else if (event.target.dataset.id === 'Topic') {
            this.changeOwnerCombo['tValue'] = event.detail.value;
            this.getChangeOwnerPicklists(this.changeOwnerCombo['sValue'], this.changeOwnerCombo['dValue'], false)
        } else if (event.target.dataset.id === 'Transfer Reason') {
            this.changeOwnerCombo['transValue'] = event.detail.value
        }
        //disable user lookup for below combination
        if (this.changeOwnerCombo['sValue'] == 'Louisville' &&
            (this.changeOwnerCombo['dValue'] == 'Grievance and Appeals' || this.changeOwnerCombo['dValue'] == 'Grievance and Appeal and Correspondence Screening')) {
            this.disableUserLookup = true;
        }
        else {
            this.disableUserLookup = false;
        }
        this.isLoading = false;
    }
    /*
    * Method handles when picklist values on change case owner pop up is changed
    */
    getChangeOwnerPicklists(sServiceCenter, sDepartment, onDeptClicked) {
        getPicklistValues({
            sFilter: this.sSecurityGroup[0],
            sServiceCenter: sServiceCenter,
            sDepartment: sDepartment,
            onDeptClicked: onDeptClicked
        }).
            then(result => {
                if (result != null) {
                    this.changeOwnerCombo['Service_Center'] = this.getListvalues(result.cServiceCenter);
                    this.changeOwnerCombo['Department'] = this.getListvalues(result.cDepartment);
                    this.changeOwnerCombo['Topic'] = this.getListvalues(result.cTopic);
                    //to Disable Topic when no record is there
                    if (this.changeOwnerCombo['dValue'] !== null && this.changeOwnerCombo['sValue'] !== null &&  this.changeOwnerCombo['Topic'].length > 0) {
                        this.disableTopic = false;
                    } else {
                        this.disableTopic = true;
                    }
                    if (result.JsonMap) {
                        this.JsonMap = result.JsonMap;
                        this.sSelectedWorkQueue = JSON.parse(result.JsonMap).Work_Queue_Name__c;
                    }

                }
            })
            .catch(error => {
            })
    }
    /*
    * Method called to check current queue is populated or not
    */
    checkUserQueue(buttonName) {
        checkUserQueue({}).then(result => {
            if (result != null) {
                if (result === true) {
                    if (buttonName == 'closeCase') {
                        this.openCloseCasePopUp();
                    } else if (buttonName == 'transferCase') {
                        this.openCaseTransferPopUp(); // open case transer popup
                    }
                    this.isLoading = false;
                } else {
                    this.populateQueueForUser();
                    this.isLoading = false;
                }
            }

        })
    }
    /*
    * Method used to clear and add error to service center and dept
    */
    isValidationOccured = false;
    checkFieldValidation(){
        let searchCmp = this.template.querySelector('lightning-combobox[data-id="Service Center"]');
        let deptCmp = this.template.querySelector('lightning-combobox[data-id="Department"]');

        if (!searchCmp.value || (Array.isArray(searchCmp.value) && searchCmp.value.length === 0)) {
            searchCmp.setCustomValidity("Service Center value is required");
            this.isValidationOccured = true;
        } else {
            searchCmp.setCustomValidity(""); // clear previous value
            this.isValidationOccured = false;
        }
        searchCmp.reportValidity();
        if (!deptCmp.value) {
            deptCmp.setCustomValidity("Department value is required");
            this.isValidationOccured = true;
        } else {
            deptCmp.setCustomValidity(""); // clear previous value
            this.isValidationOccured = false;
        }
        deptCmp.reportValidity();
    }
    /*
    * Method called for user auto select
    */
    handleTransferUserAutoSelect(event) {
        this.selectedRecord = event.detail.selectedRecord;
    }
    /* 
     * Method to check case comment count to trigger validation
    */
    checkCountValidation(event){
        this.storeCaseCmmt = event.detail.value;
    }
    /*
    * Method called when clicked Save button on pop up for close case/change case owner
    */
    popUpSaveBtn(event) {
        let obj = {};
        //close popup immedietely
        obj = this.template.querySelector('c-custom_filter_-sup-hum').filteredValuesToParent();
        let caseStatus = this.template.querySelector('lightning-combobox').value;
        let casecmt = this.template.querySelector('lightning-textarea').value;
        //show error is character is more than 1900
        if (casecmt && casecmt.length > 1900) {
            this.popUpError = true;
            this.popUpErrorMessage = this.label.CloseCase_SaveErrMsg;
            this.caseCountExceeded = true;
        }
        else{
            this.popUpError = false;
            this.caseCountExceeded = false;
        }
        if (this.buttonName == 'closeCase') {
            if(!this.caseCountExceeded)
                this.updateUnassignedOwnerclose(casecmt, caseStatus, obj);
        } else if (this.buttonName == 'transferCase') {
            let searchCmp = this.template.querySelector('lightning-combobox[data-id="Service Center"]');
            let deptCmp = this.template.querySelector('lightning-combobox[data-id="Department"]');
            let topicValue = this.template.querySelector('lightning-combobox[data-id="Topic"]').value;
            let transferReasonValue = this.template.querySelector('lightning-combobox[data-id="Transfer Reason"]').value;
            let searchValue = searchCmp.value;
            let dtValue = deptCmp.value;
            //call transfer save method from here
            this.transferCaseOwner(obj, topicValue, transferReasonValue, casecmt, searchValue, dtValue);
        }
    }
    /*
    * handles transfer case logic
    */
    transferCaseOwner(obj, topicValue, transferReasonValue, casecmt, searchValue, dtValue) {

        this.checkFieldValidation()
        if (searchValue == 'Market' && (dtValue == 'Quality Operations Compliance' || dtValue == 'Home Office Quality Operations Compliance')) {
            if (this.popUpErrorMessage) {
                this.popUpErrorMessage += '<br/>' + this.label.transferCase_QMSerr;
            } else {
                this.popUpErrorMessage = this.label.transferCase_QMSerr;
                this.popUpError = true;
                this.isValidationOccured = true;
            }
        }

        if (!this.isValidationOccured && !this.caseCountExceeded) {
            this.isLoading = true;
            changeCaseOwner({
                allWorkQueueList: obj.allWorkQueueList,
                lstWorkQ: obj.lstWorkQ,
                allQueueViewList: obj.allQueueViewList,
                lstView: obj.lstView,
                lstFilterBy: obj.lstFilterBy,
                lstWorkItems: obj.lstWorkItems,
                lstSecurityGrp: obj.lstSecurityGrp,
                lstItemAge: obj.lstItemAge,
                lstOfUnassignedRecords: this.idsToPush,
                mapCaseDTO1: JSON.stringify(this.mapCaseDTO),
                scasecom: casecmt,
                sServiceCenter: searchValue,
                sDepartment: dtValue,
                sTopic: topicValue,
                sOwnerName: this.selectedRecord !== null ? this.selectedRecord.label : null,
                sOwnerId: this.selectedRecord !== null ? this.selectedRecord.value : null,
                sTransferReason: transferReasonValue,
                JsonMap: this.JsonMap,
                lstTeamMembers: obj.lstTeamMembers
            })
                .then((result) => {
                    //clear rows
                    //show error if any single case is failed otherwise show success message for case transfer
                    if (result.length > 0) {
                        this.isModalOpen = false;
                        this.popUpError = false;
                        this.popUpErrorMessage = null;
                        //giver error msg with links
                        let isSingleFailed = false;
                        this.caseUpdateErrorMessage = this.label.transferCase_updateError;
                        let unclosedCases = [];

                        for (let i = 0; i < result.length; i++) {
                            if (result[i].bTransferFail == true) {
                                isSingleFailed = true;
                                unclosedCases.push({
                                    'id': result[i].sCaseTaskId,
                                    'number': result[i].sCaseTaskNumber,
                                    'viewUrl': '/lightning/r/Case/' + result[i].sCaseTaskId + '/view'
                                });
                            }
                        }

                        if (isSingleFailed) {
                            this.showCaseUpdateError = true;
                            this.unclosedCases = unclosedCases;
                        }
                        else
                            this.showToast('Success', this.label.transferCase_SuccessMsg, 'success', 'dismissible', null);
                        const objChild = this.template.querySelector('c-custom_dashboard_table_-sup-hum').showTablePagination(result);
                        this.originalCaseTaskList = result;
                        this.isLoading = false;
                    } else {
                        if (this.currentProfile == this.label.ccprofileLabel || this.sPharmacyUser == this.label.sPharmacyUserSupervisorYesLabel)
                            this.showToast('Error', this.label.NoWork_CCSupervisor_CaseTaskView_HUM, 'error', 'dismissible', null);
                        else
                            this.showToast('Error', this.label.NoWork_CaseTaskView_HUM, 'error', 'dismissible', null);
                        this.isLoading = false;
                    }
                    //disable buttons
                    this.enableCloseButton = true;
                    this.enableTransferButton = true;
                    this.storeCaseCmmt = null;
                    this.selectedRecord = null;
                    this.clearRows();
                    //clear the search bar
                    this.template.querySelector('lightning-input[data-attr = "cmt"]').value = null;

                })
                .catch((error) => {
                })
            this.isModalOpen = false;
        }
    }
    /*
    * Method called when user current queue is empty
    */
    populateQueueForUser() {
        this.isQueueModalOpen = true;
        this.getPicklistValuesForUserQ();
    }
    /*
    * Method to get picklist values for queue selection popup
    */
    getPicklistValuesForUserQ() {
        getQueueList({})
            .then((result) => {
                let options1 = [];
                if (result) {
                    result.forEach(el => {
                        options1.push({
                            label: el,
                            value: el,
                        });
                    });
                }
                this.PicklistValues = options1;
                this.value = result[0];
                if (this.PicklistValues.length <= 1) {
                    this.isEditUnable = true;
                } else {
                    this.isEditUnable = false;
                }
                if (this.PicklistValues.length === 0) {
                    this.showToast('Error', this.label.HUMWorkQueueNotPresentMessage, 'Error', 'dismissible', null);
                }
                return this.PicklistValues;
            })
            .catch((error) => {
            });
    }
    /*
    * Common method called when Save button cliked for Close case/Change case owner
    */
    saveQueue() {
        let selectedQueue = this.template.querySelector('lightning-combobox').value;
        saveQueue({
            sSelectedQueue: selectedQueue
        }).then(result => {
            if (result == true) {
                if (this.buttonName == 'closeCase') {
                    this.openCloseCasePopUp();
                } else if (this.buttonName == 'transferCase') {
                    this.openCaseTransferPopUp();
                }
                this.isQueueModalOpen = false;
            }
        })
            .catch((error) => {
            });
    }
    /*
    * Method called when click on Close Case button
    */
    openCloseCasePopUp() {
        /*let rows = this.template.querySelector('c-custom_dashboard_table_-sup-hum').SelectedRows();
        if (rows && Array.isArray(rows) && rows.length > 25) {
            this.showToast('Error', this.label.CloseCase_LimitError, 'Error', 'dismissible', null);
            return;
        }*/
        this.isCaseClose = true;
        this.isModalOpen = true;
        this.isLoading = false;
        this.isCaseTransfer = false;
    }
    /*
    * Method called when click on Change Case Owner button
    */
    openCaseTransferPopUp() {
        /*let rows = this.template.querySelector('c-custom_dashboard_table_-sup-hum').SelectedRows();
        if (rows && Array.isArray(rows) && rows.length > 25) {
            this.showToast('Error', this.label.transferCase_LimitError, 'Error', 'dismissible', null);
            return;
        }*/
        this.isCaseClose = false;
        this.isModalOpen = true;
        this.isCaseTransfer = true;
        this.isLoading = false
        if (this.buttonName === 'transferCase') {
            this.getChangeOwnerPicklists(null, null, false);
        }
    }
    /*
     * Generic method to handle toast messages
     * @param {*} title 
     * @param {*} message 
     * @param {*} variant 
    */
    showToast(strTitle, strMessage, strStyle, strMode, qaaErrorCasesLink) {
        this.dispatchEvent(
            new ShowToastEvent({
                'title': strTitle,
                'message': strMessage,
                'variant': strStyle, //warning,error,success
                'mode': strMode, //dismissible, pester
                'messageData': qaaErrorCasesLink,
            })
        );
    }
    /*
    * Method handles close case save
    */
    updateUnassignedOwnerclose(casecmt, caseStatus, obj) {
        this.template.querySelector('c-custom_dashboard_table_-sup-hum').handleCaseCloseSaveChild(obj, casecmt, caseStatus);
        this.enableCloseButton = true;
        this.isModalOpen = false;
        this.storeCaseCmmt = null;
        //clear the search bar
        this.template.querySelector('lightning-input[data-attr = "cmt"]').value = null;
    }
    /*
    * Method called to cancel error messages on popup when clicked Cancel button/cross mark
    */
    cancelErrMsg() {
        this.isDialogVisible = true;
        this.isModalOpen = false;
    }
    /*
    * Method used to get Status picklist options
    */
    get optionsForStatus() {
        return [{
            label: 'Closed',
            value: 'Closed'
        },
        {
            label: 'Cancelled',
            value: 'Cancelled'
        },
        ];
    }
    /*
    * Method executes when status picklist is changed
    */
    handleStatusChangePicklist(event) {
        this.valueForClosedStatus = event.detail.value;
    }
    /*
    * Method used to handle caseUpdateErrorMessage
    */
    handleCaseCloseError(event) {
        let unclosedCases = event.detail.caseIds;
        //Close_case_update_error
        this.caseUpdateErrorMessage = this.label.Close_case_update_error;
        this.showCaseUpdateError = true;
        this.unclosedCases = unclosedCases;
        this.enableCloseButton = true; //this will disable close case button after displaying error msg
        //this will clear the selected rows
        this.clearRows();

    }
    handleInputChange(event) {
        this.template.querySelector('c-custom_dashboard_table_-sup-hum').onSearchKeyUp(event.detail.value);
    }
    /*
    * Method used to clear selected rows
    */
    clearRows() {
        this.template.querySelector('c-custom_dashboard_table_-sup-hum').clearRows();
        this.caseCount = 0;
        this.enableCloseButton = true;
        this.enableTransferButton = true
    }
    /*
    * Method used to set showCaseUpdateError value to close warning message
    */
    closeWaringMsgPopup() {
        this.showCaseUpdateError = false;
    }
    handleFetchOpenCaseTaskCount(event){
        let totalOpenCaseTask = 0;
        this.totalOpenCaseCount = event.detail.caseCount;
        this.totalOpenTaskCount = event.detail.taskCount;
        this.totalOpenCaseTask = this.totalOpenCaseCount + this.totalOpenTaskCount;

        if(this.totalOpenCaseTask <= 2 ){
            this.caseCountColorClass = 'slds-is-open-green';
        }
        else if(this.totalOpenCaseTask >= 3 && this.totalOpenCaseTask <= 6){
            this.caseCountColorClass = 'slds-is-open-yellow';
        }
        else if(this.totalOpenCaseTask > 6){
            this.caseCountColorClass = 'slds-is-open-red';
        }
    }


}