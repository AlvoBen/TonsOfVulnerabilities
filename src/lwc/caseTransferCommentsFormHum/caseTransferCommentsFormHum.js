/*******************************************************************************************************************************
LWC JS Name : caseTransferCommentsFormHum.js
Function    : This JS serves as helper to caseTransferCommentsFormHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Kalyani Pachpol                                         02/17/2022          					US-4196961
* Nirmal Garg																							09/01/2023										US-4908765
* Jonathan Dickinson                                      02/27/2024                 User Story 5738539: T1PRJ1374973: DF 8518 - 8519 - 8520; C06 Case Management; Lightning - Case Comments - Error when adding comment before closing or transferring a case and notes reflected in incorrect section
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from "lwc";
import sendRequestLogNotes from "@salesforce/apexContinuation/CaseCommentsDataTable_LC_HUM.sendRequestLogNotes";
import reassignToCreatorComment from "@salesforce/apex/CaseCommentsDataTable_LC_HUM.reassignToCreatorComment";
import saveCaseCommentData from "@salesforce/apex/CaseCommentsDataTable_LC_HUM.saveCaseCommentData";
import caseValidations from "c/caseValidationsHum";
import customcss from "@salesforce/resourceUrl/LightningCRMAssets_SR_HUM";
import { loadStyle } from "lightning/platformResourceLoader";
import checkMedicareCalls from "@salesforce/apex/CaseDetails_LC_Hum.checkMedicareCalls";
import getCCMedicareCalls from "@salesforce/apex/CaseDetails_LC_Hum.getCCMedicareCalls";
import createUpdateCaseCommentRecord from "@salesforce/apex/MedicareCallsCaseComments_H_HUM_LWC.createUpdateCaseCommentRecord";
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import casecommentwarningmsg from '@salesforce/label/c.CASECOMMENT_PHARMACY_WARNING_HUM';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import { createLogNote } from 'c/genericPharmacyLogNotesIntegrationHum';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
const USER_FIELDS = [NETWORK_ID_FIELD];
export default class CaseTransferCommentsFormHum extends caseValidations {

    @api oUserGroup;
    @api commentData; //this variable used to get casecomment related data from caseTransferContainerHum LWC
    @api oCaseObject; // this variable used to store whole case related data from caseTransferContainerHum LWC
    @api reassginToCreator = false; //variable used to determine whether creaotr button is clciked or not
    @api reassginToSender = false; //variable used to determine whether sender button is clciked or not
    @api ctciObject; //variable used to store CTCI data from CTCI junction object

    @track pharmacyLogCode;
    @track lstCodes; // this is used to store logcodes that will visibe for pharmacy user
    @track bLogCodeVisible; // this is used to control the logcode visibility on HTML
    @track isCommentSaved; // flag to validate whether comment saved or not
    @track commentLimit; // this will used for hold the comment limit based on LOBs
    lstHpCases = ['Closed HP Agent/Broker Case', 'Closed HP Group Case', 'Closed HP Member Case', 'Closed HP Provider Case', 'Closed HP Unknown Case', 'HP Agent/Broker Case', 'HP Group Case', 'HP Member Case', 'HP Provider Case', 'HP Unknown Case'];
    caseComments = true;
    medicareCallsComments = false;
    bMedicareCalls = false;
    bMediCCUpdate = false;
    @track bPharmacy;
    @track maxLimitMsg;
    @track bDisplayLimitMsg = true;
    @track isHPharmacyUser;
    @track networkId;
    @track userRecord;
    @track caseAccountId;
    label = {
        casecommentwarningmsg
    }
    connectedCallback() {
        this.caseAccountId = this.oCaseObject?.AccountId ?? null;
        this.loadOptions();
    }

    @wire
        (getRecord, { recordId: USER_ID, fields: USER_FIELDS })
    wireUserRecord({ error, data }) {
        if (data) {
            try {
                this.userRecord = data;
                this.networkId = data.fields.Network_User_Id__c.value ?? '';
            } catch (e) {
                console.log('An error occured when handling the retrieved user record data');
            }
        } else if (error) {
            console.log('An error occured when retrieving the user record data: ' + JSON.stringify(error));
        }
    }

    @wire(getRecord, {
        recordId: '$caseAccountId',
        fields: ['Account.Enterprise_ID__c']
    })
    wiredAccount({ error, data }) {
        if (data) {
            this.enterpriseId = data.fields.Enterprise_ID__c.value;
        } else if (error) {
            console.log('error in wire--', error);
        }
    }

    /**
     * Method Name: loadOptions
     * Function: used to show logocdes and its visiblity on UI
     */
    loadOptions() {
        // Below const is used to store values for Service Model Type field of CTCI object to hide log code for HP member case
        const serviceModelTypes = ['Insurance/Plan', 'Humana Pharmacy'];
        // below const used to store service model type from case object
        const serviceModelType = this.oCaseObject?.CTCI_List__r?.Service_Model_Type__c ?? null;

        // Below const is used to store values for Classification_Type__c field of case object to hide log code for HP member case
        const classficationTypes = ['Calls (RSO)', 'HP Clinical Services', 'HP Finance Ops', 'HP RxE Calls', 'HP Specialty Calls', 'Humana Pharmacy Calls', 'Humana Pharmacy Web Chat'];
        const classifcationType = this.oCaseObject && this.oCaseObject.hasOwnProperty('Classification_Type__c') ? this.oCaseObject.Classification_Type__c : null;

        try {
            this.lstCodes = [{ label: '--None--', value: '--None--' }];
            this.lstCodes = [...this.lstCodes, ...this.commentData.logCodes.map((item) => ({
                label: item,
                value: item
            }))];
            // bLogCodeVisible variable used to control the logcode picklist visibility for HP and non-hp users
            this.bLogCodeVisible = (this.commentData?.sProfileName === "Humana Pharmacy Specialist" || ((this.commentData?.sProfileName === "Customer Care Specialist" || this.commentData?.sProfileName === "Customer Care Supervisor") && hasCRMS_206_CCSHumanaPharmacyAccess))
                && this.lstCodes.length > 0 && this.oCaseObject?.RecordType?.Name === 'HP Member Case' && serviceModelTypes?.includes(serviceModelType)
                && classficationTypes?.includes(classifcationType);

            if (this.commentData?.sProfileName === "Humana Pharmacy Specialist" || ((this.commentData?.sProfileName === "Customer Care Specialist" || this.commentData?.sProfileName === "Customer Care Supervisor") && hasCRMS_206_CCSHumanaPharmacyAccess)) {
                this.bPharmacy = true;
            }
            else {
                this.bPharmacy = false;
            }

            if ((this.commentData?.sProfileName === "Humana Pharmacy Specialist") || ((this.commentData?.sProfileName === "Customer Care Supervisor" || this.commentData?.sProfileName === "Customer Care Specialist") && hasCRMS_206_CCSHumanaPharmacyAccess)) {
                this.commentLimit = '1900';
                this.maxLimitMsg = this.commentLimit + ' characters remaining';
            } else {
                this.commentLimit = '2959';
            }
            checkMedicareCalls({ memplanId: this.oCaseObject.Member_Plan_Id__c, sOrigin: this.oCaseObject.Origin })
                .then(bMedicareCalls => {
                    let recordTypeName = this.oCaseObject.RecordType.Name;
                    if (bMedicareCalls == true && recordTypeName != 'Medicare Case' && recordTypeName != 'Closed Medicare Case' && !this.lstHpCases.includes(recordTypeName)
                        && (this.commentData.sProfileName === "Customer Care Specialist" || this.commentData.sProfileName === "Customer Care Supervisor")) {
                        this.commentLimit = '2000';
                        this.bMedicareCalls = true;
                        this.caseComments = false;
                        this.medicareCallsComments = true;
                        getCCMedicareCalls({ caseId: this.oCaseObject.Id }).then(bMediCCUpdate => {
                            this.bMediCCUpdate = bMediCCUpdate;
                        });
                    } else {
                        this.bMedicareCalls = false;
                        this.caseComments = true;
                        this.medicareCallsComments = false;
                    }
                });
        } catch (err) {
            console.error("Error in loadOptions", err);
        }
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + "/CRM_Assets/styles/commonStyles.css")
        ]).catch((error) => { });

    }

    /**
     * Method Name: fetchDOMValues
     * Function: this is used for sending comment values to parent container 
     */
    @api
    fetchDOMValues() {
        return this.template.querySelector("[data-id='comments']"); // get comment in JS
    }

    /**
     * Method Name: handleCommentValidation
     * Function: this is used for validating comment field if log code is entered and it will only run for pharmacy users
     */
    @api
    handleCommentValidation() {
        let errMsg = 'CC::';
        if (this.bMedicareCalls == true) {
            errMsg = 'MCC::';
        }
        if (this.bMedicareCalls == true && this.bMediCCUpdate == false) {
            const commentsiss = this.template.querySelector("[data-id='commentsiss']");
            const commentsres = this.template.querySelector("[data-id='commentsres']");
            let issue = commentsiss != null ? commentsiss.value : '';
            let resolution = commentsres != null ? commentsres.value : '';
            if (issue == null || issue == '' || resolution == null || resolution == '') {
                errMsg += 'Enter Medicare Calls Case Comments';
            } else if (issue.length < 10 || resolution.length < 10) {
                errMsg += 'A minimum of 10 characters are required, with a maximum of 2000';
            }
        } else if (this.commentData.sProfileName === "Humana Pharmacy Specialist") {
            const inputCode = this.template.querySelectorAll("lightning-combobox"); // get logcode in JS
            const inputComment = this.template.querySelector("[data-id='comments']"); // get comment in JS
            if (inputCode[0] && inputCode[0].value && inputCode[0].value !== '--None--' && inputCode[0].value !== '') {
                let res = inputComment && inputComment.value && !(/^\s*$/.test(inputComment.value));
                if (!res) {
                    errMsg += 'Case Comment: You must enter a value';
                }
            }
        } else if (this.oCaseObject.Origin === "Correspondence" && this.oCaseObject.Type === "MHK Dispute Task" && this.oCaseObject.RecordType.Name === 'Member Case') {
            const inputComment = this.template.querySelector("[data-id='comments']");
            let res = inputComment && inputComment.value && !(/^\s*$/.test(inputComment.value));
            if (!res) {
                errMsg += 'Please enter at least one comment to proceed with this transfer to MHK.';
            }
        }
        return errMsg;
    }

    /**
     * Method Name: handleLogCodeValidation
     * Function: this is used for validating log code that log ocde is mandatory if visible on UI for pharmacy it will only run for pharmacy users
     */
    @api
    handleLogCodeValidation() {
        if (this.bLogCodeVisible) {
            const inputCode = this.template.querySelectorAll("lightning-combobox"); // get logcode in JS
            const inputComment = this.template.querySelector("[data-id='comments']"); // get comment in JS
            if (!(inputCode[0] && inputCode[0].value && inputCode[0].value !== '--None--' && inputCode[0].value !== '')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method Name: sendRequestToEpost
     * Function: this is used for sending comment and logocedes to EPOST once hit save and then save casecomment to DB
     */
    @api async sendRequestToEpost() {
        const inputCode = this.template.querySelectorAll("lightning-combobox"); // get logcode in JS
        const inputComment = this.template.querySelector("[data-id='comments']"); // get comment in JS
        const commentsiss = this.template.querySelector("[data-id='commentsiss']");
        const commentsres = this.template.querySelector("[data-id='commentsres']");
        let commentBody;
        if (this.bLogCodeVisible) { // to make sure that user is HP and log code is visible then only hit EPOST code
            let oParams = {};
            oParams = {
                sCaseId: this.oCaseObject.Id,
                sCaseCommentBody: inputComment.value,
                sCode: inputCode[0].value,
                bRedirect: false,
                bErrorCaseComment: "false"
            };
            const switchVal = getCustomSettingValue('Switch', 'HPIE Switch');
            switchVal.then(result => {
                if (result && result?.IsON__c && result?.IsON__c === true) {
                    this.callHpieService(oParams);
                } else {
                    this.callRSService(oParams, inputComment, inputCode);
                }
            })
        } else {
            try {
                let lstOfCaseComment = [];
                let caseComment = {};
                let caseComment1 = {};
                if (inputComment != null) {
                    commentBody = inputComment.value;
                    caseComment.CommentBody = !(/^\s*$/.test(inputComment.value)) ? inputComment.value : undefined;
                    if (this.reassginToCreator) {
                        caseComment.CommentBody ? caseComment.CommentBody += '\n#RTC' : caseComment.CommentBody = '#RTC';
                        if (caseComment.CommentBody.length > 2000) {
                            caseComment.CommentBody = '#RTC';
                            caseComment1.CommentBody = inputComment.value;
                            lstOfCaseComment = [...[caseComment], ...[caseComment1]];
                        } else {
                            if (!(/^\s*$/.test(caseComment.CommentBody))) {
                                lstOfCaseComment.push(caseComment);
                            }
                        }
                    } else {// creator else start means creator or sender is not clicked
                        if (inputComment.value !== undefined && !(/^\s*$/.test(inputComment.value))) { // if to make sure that if there is not comment entered in UI in case of change owner thendon't push
                            lstOfCaseComment.push(caseComment);
                        }
                    }
                }
                if (this.bMedicareCalls == true && this.bMediCCUpdate == false) {
                    let iscmtsaved = await createUpdateCaseCommentRecord({
                        sMedicareCallsIssue: commentsiss.value,
                        sMedicareCallsRes: commentsres.value,
                        CaseId: this.oCaseObject.Id
                    });
                    commentBody = commentsiss.value + ' ' + commentsres.value;
                    if (iscmtsaved) {
                        this.isCommentSaved = 'Comment inserted';
                    }
                }

                if (lstOfCaseComment.length > 0) {
                    this.isCommentSaved = await saveCaseCommentData({
                        sCaseId: this.oCaseObject.Id,
                        caseCommentData: JSON.stringify(lstOfCaseComment)
                    });
                } else {
                    //dummy event to notify user that u don't have any comment on UI so close the form 
                    this.dispatchEvent(new CustomEvent("commentsave", { detail: { saveMsg: 'Comment inserted' } }));
                }

                if (this.isCommentSaved === 'Comment inserted') { // if to make sure real comment is inserted in DB with some values
                    this.afterSaveComment({ sCaseCommentBody: commentBody });
                }
            } catch (err) {
                console.error("Error in Comment save DB", err);
            }
        }
    }

    callRSService(oParams, inputComment, inputCode) {
        sendRequestLogNotes({
            sComment: inputComment.value,
            sCode: inputCode[0].value,
            sCaseId: this.oCaseObject.Id,
        }).then((result) => {
            if (result) {
                if (result[0] === 'true') {
                    oParams.bErrorCaseComment = '';
                } else if (result[0] === 'false') {
                    oParams.bErrorCaseComment = 'false';
                } else if (result[1] === "true") {
                    oParams.bErrorCaseComment = "true";
                } else if (result[1] === "false") {
                    oParams.bErrorCaseComment = "false";
                }
                this.callSaveComment(oParams);
            }
        }).catch((error) => {
            console.log("error in EPOST", error);
        });
    }

    callHpieService(oParams) {
            createLogNote(this.networkId, this.enterpriseId, this.organization ?? 'HUMANA', oParams?.sCode ?? '', oParams?.sCaseCommentBody ?? '')
            .then(result => {
                if (result) {
                    oParams.bErrorCaseComment = '';
                }
                this.callSaveComment(oParams);
            }).catch(error => {
                if (error?.message?.toLowerCase()?.includes('patient not found')) {
                    oParams.bErrorCaseComment = "true";
                } else {
                    oParams.bErrorCaseComment = "false";
                }
                console.log(error);
                this.callSaveComment(oParams);
            })
    }

    /**
     * Method Name: fetchCommentIssue
     * Function: this is used for sending comment Issue to parent container 
     */
    @api
    fetchCommentIssue() {
        return this.template.querySelector("[data-id='commentsiss']"); // get comment in JS Medicare Calls Case Comments Prasuna Query selector chagned to data id
    }
    /**
     * Method Name: fetchCommentResolution
     * Function: this is used for sending comment Resolution to parent container 
     */
    @api
    fetchCommentResolution() {
        return this.template.querySelector("[data-id='commentsres']"); // get comment in JS Medicare Calls Case Comments Prasuna Query selector chagned to data id
    }
    /**
     * Method Name: callSaveComment
     * Function: this is used for save casecomment to DB based on response comes from EPOST
     */
    async callSaveComment(oParams) {
        try {
            if (this.reassginToCreator) { //if to make sure that creaotr is called and #RTC should also inserted as comment in DB
                oParams.sHashTagIRCT = '#RTC';
            } else if (this.reassginToSender) { //if to make sure that sender is called and #RTS should also inserted as comment in DB
                oParams.sHashTagIRCT = '#RTS';
            }
            this.isCommentSaved = await reassignToCreatorComment(oParams);
            if (this.isCommentSaved === 'Comment inserted') {
                this.afterSaveComment(oParams);
            }
        } catch (err) {
            console.error("Error in comment save", err);
        }
    }

    afterSaveComment(commentData) {
        let commentDataObj = {}
        let detail = {};

        commentDataObj.ParentId = this.oCaseObject.Id;
        commentDataObj.CommentBdoy = commentData.sCaseCommentBody

        detail.commentDataObj = commentDataObj;
        detail.saveMsg = this.isCommentSaved;

        const selectedEvent = new CustomEvent("commentsave", {
            detail: detail
        });
        this.dispatchEvent(selectedEvent);
    }

    checkCharLimit() {
        let sCaseComment = this.template.querySelector(`[data-id='comments']`);
        this.maxLimitMsg = (this.commentLimit - sCaseComment.value.length) + ' characters remaining';
        if (this.commentLimit === sCaseComment.value.length) {
            sCaseComment.setCustomValidity('Warning: Exceeded character limit');
            this.bDisplayLimitMsg = false;
        } else {
            sCaseComment.setCustomValidity("");
            this.bDisplayLimitMsg = true;
        }
        sCaseComment.reportValidity();
    }

    handleChange(event) {
        this.pharmacyLogCode = event.target.value === '--None--' ? null : event.target.value;
    }
}