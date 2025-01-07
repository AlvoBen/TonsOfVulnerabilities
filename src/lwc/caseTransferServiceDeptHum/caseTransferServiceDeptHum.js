import { LightningElement, track, api } from 'lwc';
import fetchTopics from '@salesforce/apex/CaseTransfer_LC_HUM.fetchTopics';
import reAssignToCreator from '@salesforce/apex/CaseTransfer_LC_HUM.reAssignToCreator';
import reAssignToSender from '@salesforce/apex/CaseTransfer_LC_HUM.reAssignToSender';
import caseValidations from "c/caseValidationsHum";
import customcss from "@salesforce/resourceUrl/LightningCRMAssets_SR_HUM";
import { loadStyle } from "lightning/platformResourceLoader";

export default class CaseTransferServiceDeptHum extends caseValidations {

    @api serviceDept; //this variable is used to store service and department related section information from caseTransferContainerHum
    @api userPopulaionInfo; //used for fetch data that is being used in user popuation autocomplete
    @api oCaseObject; //used to store whole case data;
    @api customLabels;
    @api bReAssignToCreatorCalled = false; // this variable used to identify that reassign to creaotr button is called
    @api bReAssignToSenderCalled = false; // this variable used to identify that reassign to sender button is called

    @track lstTopic = [];
    @track lstDepartment = [];
    @track lstService = [];
    @track lstPriority = [];
    @track lstEscIndValue = [];
    escValue;
    @track bDeptDisabled = true;  // variable used to disbale and enable department picklist
    @track bTopicDisabled; // variable used to disbale and enable topic 
    @track queueName; // variable used to store the work_queue_name corrospond to serivce and department from workQueuesetup data object
    @track lstWorkQueueJunction;
    @track case = {};
    @track topicGuidanceMsg;
    @track guidanceMsg;
    @track reassignCaseOwner = ""; //variable used to store user name on reassign creator clicked if any
    @track reassignCaseId; //variable used to store user id on reassign creator clicked if any
    @track bTopicMsgonReassign = false; //variable used to show that now topic msg should visible if present wu=hile click on reassign buttons
    lstInteractingAboutType =['Agent', 'Group', 'Member', 'Provider', 'Unknown-Agent', 'Unknown-Group', 'Unknown-Member', 'Unknown-Provider'];
    lstInteractionOrigin=['Service Inquiry', 'NINA Web Chat', 'Internal Process', 'Watson Voice', 'Watson Web Chat','Vantage', 'Cancelled','Correspondence'];
    isEscIndicatorDisplay=false;
	@track isErrorExists;
	@api
    get pageError() {
        return {}
    }
    set pageError(value) {
        this.isErrorExists = value;
    }

    connectedCallback() {
        if (this.bReAssignToCreatorCalled) {
            this.callReassignToCreator();
        } else if (this.bReAssignToSenderCalled) {
            this.callReassignToSender();
        } else {
            this.prepareComboBoxData();
        }
        this.showEsclIndicator(this.oCaseObject.Interacting_About_Type__c,this.oCaseObject.Origin,this.oCaseObject.Classification_Type__c);
    }

    /**
     * Method Name:showEsclIndicator 
     * @param {*} interactingAbout ,caseOrigin,classificationType     
     * Function used to check if Escalation Indicator to be shown in the UI or not
     */
    showEsclIndicator(interactingAbout,caseOrigin,classificationType){

        if((this.lstInteractingAboutType.includes(interactingAbout)) && (!this.lstInteractionOrigin.includes(caseOrigin))){
            this.isEscIndicatorDisplay =true;
          if(caseOrigin === 'IVR' && classificationType != 'Calls (RSO)'){
            this.isEscIndicatorDisplay = false;
          }  
        }
    }


    /**
    * Method Name: callReassignToCreator
    * Function:used when user click on reassign to creator button
    */
    async callReassignToCreator() {
        try {
            let result = await reAssignToCreator({ caseObj: JSON.stringify(this.oCaseObject) });
            if (!this.isErrorExists) {
			if (this.oCaseObject.CreatedById == result.currentUserID) { // this if to handle if user is creator of case then close popup so that user can not reassign case to creator
                this.closeTransferPopup('closeform', 'This case cannot be transferred to creator because you are the creator');
                return;
            } else if (result.bIsETLUser) { // this if to handle if user is creator of case then close popup so that user can not reassign case to creator
                this.closeTransferPopup('closeform', 'Unable to reassign to creator because the creator is a system user. Please follow your normal Process.');
                return;
            } else if (result.isCreatorDisabled) {
                    this.closeTransferPopup('closeform', 'Unable to reassign to creator because you don\'t have appropriate security to transfer the case to Creator. Please transfer case manually by selecting Service Center, Department, Topic and User fields as appropriate.');
                    return;
                }
            }
            this.serviceDept = result;
            this.handleUserPopulationOnCreator(result);  // method used to handle user population
            this.prepareComboBoxData();
        } catch (error) {
            console.log('error--', error);
        }
    }

    /**
    * Method Name: callReassignToSender
    * Function:used when user click on reassign to sender button
    */
    async callReassignToSender() {
        try {
            let result = await reAssignToSender({ caseObj: JSON.stringify(this.oCaseObject) });
            if (!this.isErrorExists) {
                if (result.isSenderDisabled) { // this if to handle if user is previous work queue  of case is blank then close popup so that user can not reassign case to sender
                    this.closeTransferPopup('closeform', 'This case cannot be reassigned to sender since previous work queue is not available.');
                    return;
                } else if (result.isSecuritySenderDisabled) {
                    this.closeTransferPopup('closeform', 'Unable to reassign to sender because you don\'t have appropriate security to transfer the case to  Sender. Please transfer case manually by selecting Service Center, Department, Topic and User fields as appropriate.');
                    return;
                }
            }
            
            this.serviceDept = result;
            this.handleUserPopulationOnCreator(result);  // method used to handle user population
            this.prepareComboBoxData();
        } catch (error) {
            console.log('error--', error);
        }
    }

    /**
    * Method Name: closeTransferPopup
    * Function:used for fire the any generic event
    */
    closeTransferPopup(defaultEventName, payload) {
        this.dispatchEvent(new CustomEvent(defaultEventName, {
            detail: payload
        }));
    }

    /**
    * Method Name: handleUserPopulationOnCreator
    * Function:used for populate user data on user field once reassign to creator called
    */
    handleUserPopulationOnCreator(result) {
        this.reassignCaseOwner = result.reassignCaseOwner;
        this.reassignCaseId = result.reassignCaseId;
        if (this.reassignCaseId) { //this if to make sure that there is some user present
            let event = { detail: { userId: this.reassignCaseId, userName: this.reassignCaseOwner } };
            this.handleSelectedUserDetails(event);
        }
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + "/CRM_Assets/styles/commonStyles.css")
        ]).catch((error) => { });

    }

    /**
    * Method Name: prepareComboBoxData
    * Function:used for prepare service picklist data
    */
    prepareComboBoxData() {
        // prepare service center picklist on page load
        this.lstService = [...[{ label: "--None--", value: "--None--" }], ...Object.keys(this.serviceDept.lstServiceCenter).length > 0 ? Object.keys(this.serviceDept.lstServiceCenter).map(item => ({
            label: item, value: item
        })) : []];

        // populate none value on department 
        this.lstDepartment = [{ label: "--None--", value: "--None--" }];
        // this if will run when there is some service center and department on load of page from apex excluding none values 
        if (this.serviceDept.defaultService !== '--None--' && this.serviceDept.defaultDepartment !== '--None--') {
            let deptList = this.serviceDept.lstServiceCenter[this.serviceDept.defaultService];
            this.lstDepartment = [...this.lstDepartment, ...deptList ? deptList.map(item => ({
                label: item, value: item
            })) : []];

            this.bDeptDisabled = !(this.lstDepartment.length > 1) // means with none there is some other value also present
            
            setTimeout(() => { // add timeout to render latest values on DOM when apex is called from reassign buttons so in department method latest service and departmwnt and other values fetched from DOM to make new Apex call
                this.handleDeptCentChange(this.serviceDept.defaultDepartment, this.serviceDept.defaultService);
            }, 100);
        }

        // prepare topic picklist on page load
        this.lstTopic = [...[{ label: "Select a Topic", value: "Select a Topic" }], ...this.serviceDept.lstTopics];
        // handle disability of topic
        this.bTopicDisabled = !(this.serviceDept.lstTopics.length > 0);

        //populate escalation indicator
        this.lstEscIndValue = [...[{ label: "--None--", value: "" }], ...this.serviceDept.lstEscIndicator];
        if(this.oCaseObject.Escalation_Indicator__c === undefined){
            this.escValue='';
        }else{
            this.escValue=this.oCaseObject.Escalation_Indicator__c;
        }
    }

    /**
   * Method Name: handleChange
   * Function:this method will run while any picklist value get change
   */
    handleChange(event) {
        let comboBoxName = event.target.label;
        //below if will run when service center picklist value has changed
        if (comboBoxName === 'Service Center') {
            this.handleServiceCentChange(event.target.value);
        } else if (comboBoxName === 'Department') {
            this.handleDeptCentChange(event.target.value);
        } else if (comboBoxName === 'Topic') {
            this.handleTopicChange(event.target.value);
        } else if (comboBoxName === 'Transfer Reason') {
            this.handleTRChange(event.target.value);
        } else if (comboBoxName === 'Priority') {
            this.handlePriorityChange(event.target.value);
        } else if (comboBoxName === 'Escalation Indicator') {
            this.handleEscalationChange(event.target.value);
        }
    }

    /**
   * Method Name: handleServiceCentChange
   * Function:used for prepare department picklist data
   */
    handleServiceCentChange(serviceVal) {
        this.clearInformationalMsge();

        let dept = this.template.querySelector('[data-id="dept-picklist"]');
        // if will run when value is not none else department will be again none
        if (serviceVal !== '--None--') {
            dept.value = "";
            let deptValue = this.serviceDept.lstServiceCenter[serviceVal];
            this.lstDepartment = [...[{ label: "--None--", value: "--None--" }], ...deptValue ? deptValue.map(item => ({
                label: item, value: item
            })) : []];
        }
        else {
            this.lstDepartment = [{ label: "--None--", value: "--None--" }];
            dept.value = "--None--";
            const evt = new Event("change", { "bubbles": true, "cancelable": false });
            dept.dispatchEvent(evt);
        }
        // this method is used to disable user field on service and department value basis 
        this.disableUserField(serviceVal, '');
        this.bDeptDisabled = (this.serviceDept.lstServiceCenter[serviceVal]) ? false : true;
    }

    /**
  * Method Name: handleDeptCentChange
  * Function:used for prepare topic picklist data
  */
    async handleDeptCentChange(deptVal, serviceVal = undefined) {
        this.clearInformationalMsge();

        let serviceCenter = this.template.querySelector('[data-id="service-picklist"]');
        // this if to make sure that this method is running from on load of page becoz there is some default value present in service and department
        if (!serviceCenter) {
            serviceCenter = {};
            serviceCenter.value = serviceVal;
        }
        let topics = this.template.querySelector('[data-id="topic-picklist"]');
        let dbTopics;
        // if will run when department value is not none else topic will be again none
        if (deptVal !== '--None--') {
            try {
                dbTopics = await fetchTopics({ sServiceCenter: serviceCenter.value, sDepartment: deptVal, isAdhocCall: true, objCase: JSON.stringify(this.oCaseObject) });
                if (dbTopics && dbTopics.lstTopics.length > 0) {
                    this.lstTopic = [...[{ label: "Select a Topic", value: "Select a Topic" }], ...dbTopics.lstTopics];
                } else {
                    this.lstTopic = [...[{ label: "Select a Topic", value: "Select a Topic" }], ...[]];
                    topics ? topics.value = "Select a Topic" : null;
                }
                // this is used to store workqueuejunction data so while selecting topic we can show topicguidance
                this.lstWorkQueueJunction = dbTopics.lstWorkQueueJunction;
            } catch (error) {
                console.log('error in fetching topic--', error);
            }
            // this is to fetch workqueuename so that it will used in user autopopulate feature
            let workQueueSetup = this.serviceDept.mapUniqueServCenDeptQueueSetup[serviceCenter.value + '#' + deptVal];
            //this will used in userpopulation and topic guidance show after query on workqueujunction with help of this workqueuesetup name
            this.queueName = workQueueSetup ? workQueueSetup.Work_Queue_Name__c : null;
            // this is to show guidance informational msge on UI after department is slected
            this.guidanceMsg = workQueueSetup ? workQueueSetup.Work_Queue_Guidance__c : null;
            // this if to show guidance msge
            if (this.guidanceMsg) {
                this.showInformationalMsge();
            }
        }
        else {// else to disable topic if department is none
            this.lstTopic = [...[{ label: "Select a Topic", value: "Select a Topic" }], ...[]];
            topics.value = "Select a Topic";
        }
        // this method is used to disable user field on service and department value basis 
        this.disableUserField(serviceCenter.value, deptVal);
        this.bTopicDisabled = !(dbTopics && dbTopics.lstTopics.length > 0);

        if (!(this.bTopicDisabled) && topics.value == "Select a Topic" && (this.bReAssignToCreatorCalled || this.bReAssignToSenderCalled)) { // this if needs to shpw topic msge when click on reassin to creator button and topic is enable
            this.bTopicMsgonReassign = true;
        }
    }

    /* Method Name: disableUserField
   * Function:used for disable user field based on service and department
   */
    disableUserField(serviceCenter, deptVal) {
        if (this.customLabels.Case_Transfer_Service_Flag === '1') {
            if (serviceCenter === 'Louisville' && (deptVal === 'Grievance and Appeals' || deptVal === 'Grievance and Appeal and Correspondence Screening')) {
                this.template.querySelector('c-case-transfer-user-auto-complete-hum').disabledUserField(null, true);
                let event = { detail: { userId: null, userName: null } };
                this.handleSelectedUserDetails(event);
            } else {
                this.template.querySelector('c-case-transfer-user-auto-complete-hum').disabledUserField('NOT_ERASE', false);
            }
        }
    }

    /**
   * Method Name: handleTopicChange
   * Function:used for topic picklist change operation
   */
    handleTopicChange(topicVal) {
        // this if is to display topic guidance
        if (topicVal !== 'Select a Topic') {
            let newQueueName = this.queueName ? this.queueName.replace(/_/g, ' ') : null;
            let workQueuJunction = this.lstWorkQueueJunction.filter((item) => {
                return (
                    item.hasOwnProperty('Work_Queue_Setup_Name__r') && item.Work_Queue_Setup_Name__r.Name === newQueueName && item.hasOwnProperty('Work_Queue_Topic_Name__r') && item.Work_Queue_Topic_Name__r.Name === topicVal
                );
            });
            // this is to show topic guidance informational msge on UI
            this.topicGuidanceMsg = (workQueuJunction && workQueuJunction.length > 0) ? workQueuJunction[0].Work_Queue_Topic_Guidance__c : null;
            // this if to show topic msge
            if (this.topicGuidanceMsg) {
                this.showInformationalMsge();
            }
        }else{
            this.topicGuidanceMsg = null;
            this.clearInformationalMsge();
        }
    }

    /**
 * Method Name: handleTRChange
 * Function:used for transfer reasonc picklist change operation
 */
    handleTRChange(TransferReasonVal) {

    }

    /**
 * Method Name: handlePriorityChange
 * Function:used for priority picklist change operation
 */
    handlePriorityChange(priorVal) {

    }
    /**
     * Method Name: handleEscalationChange
     * Function:used for Escalation indicator picklist change operation
     */
    handleEscalationChange(priorVal) {
    this.escValue=priorVal;
    }

    /**
* Method Name: clearInformationalMsge
* Function: this method is used to clear informational msge from UI
*/
    clearInformationalMsge() {
        this.dispatchEvent(new CustomEvent("removeinfomsg", { // event fire on container to remove information msges
            detail: false
        }));
    }

    /**
* Method Name: showInformationalMsge
* Function: this method is used to show informational msge from UI
* param : infoMsge
*/
    showInformationalMsge() {
        this.dispatchEvent(new CustomEvent("showinfomsg", { // event fire on container to show information msges
            detail: { guidanceInfo: this.guidanceMsg, topicInfo: this.topicGuidanceMsg, showInforMsge: true }
        }));
    }

    /**
* Method Name: handleSelectedUserDetails
* Function: this will run when user select the value in user search box field and will fetch selected user Id
*/
    handleSelectedUserDetails(event, defaultEventName = "selecteduserid") {
        this.dispatchEvent(new CustomEvent(defaultEventName, {
            detail: event.detail
        }));
    }

    /**
* Method Name: saveCaseTransferSection
* Function: this will run on clciking of save button from casetransfercontainerhum
*/
    @api
    saveCaseTransferSection() {
        let topicValidationFailed = false;

        // this is used to fetch value present in user field on UI
        let userAutoComplete = this.template.querySelector('c-case-transfer-user-auto-complete-hum').fetchUserValue();
        let event = { detail: userAutoComplete };
        this.handleSelectedUserDetails(event, "fetchuserfieldvalue"); //fire an event on container component on click of save 

        if (!this.bTopicDisabled) { // this if to make sure if topic is disable then not apply validation on it
            let fldSelector = this.template.querySelector('[data-id="topic-picklist"]');
            if (fldSelector.value === 'Select a Topic' || fldSelector.value === '' || fldSelector.value === undefined) {
                topicValidationFailed = true;
            }
        }
        // this if is for handle required field values
        if (!(this.handleRequiredFieldsValidation({ "lightning-combobox": "label" }, ['Service Center', 'Department']))) {
            return 'VALIDATION_FAILED';
        }

        if (topicValidationFailed) {
            return 'TOPIC_VALIDATION_FAILED';
        }

        let labelAndFieldApi = { 'Service Center': 'Service_Center__c', 'Department': 'Department__c', 'Topic': 'Topic__c', 'Priority': 'Priority', 'Transfer Reason': 'Transfer_Reason__c' ,'Escalation Indicator': 'Escalation_Indicator__c'};
        const recordData = this.template.querySelectorAll(
            "lightning-combobox"
        );
        if (recordData) {
            recordData.forEach((field) => {
                this.case[labelAndFieldApi[field.label]] = field.value;
            });
        }
        return this.case;
    }

    /**
* Method Name: handleRequiredFieldsValidation
* Function: this method is used to show field level error message on required fields
* params : selectorValidate- type of field to validate like combobox or input etc....
           fieldsToValidate - name of field to validate
*/
    handleRequiredFieldsValidation(selectorValidate, fieldsToValidate) {
        const selectorToValidate = this.checkNullValues(selectorValidate, fieldsToValidate);
        return this.updateFieldValidation(selectorToValidate, 'Complete this field', '', false);
    }

}