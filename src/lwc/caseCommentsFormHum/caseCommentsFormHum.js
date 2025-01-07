/*******************************************************************************************************************************
LWC JS Name : CaseCommentsFormHum.js
Function    : This JS serves as helper to CaseCommentsFormHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                          06/20/2022                   initial version
* M K Manoj                                               07/27/2022          US-3577116,3577295 ,Case Comments- Character Limitation Warning Message -Close Case Popup- HP and Non-HP Case Record Types
* Prasuna Pattabhi                                        31/10/2022          US-3934433 Close Case Medicare Case Calls Cooments 
* Kalyani Pachpol                                         02/17/2023          US-4256639
* Kalyani Pachpol                                         03/06/2023          DF-7304
* Kalyani Pachpol                                          05/04/2023                  US-4486808
* Nirmal Garg																							09/01/2023										US-4908765
* Jonathan Dickinson                                      02/27/2024                 User Story 5738539: T1PRJ1374973: DF 8518 - 8519 - 8520; C06 Case Management; Lightning - Case Comments - Error when adding comment before closing or transferring a case and notes reflected in incorrect section
*********************************************************************************************************************************/

import { LightningElement, api, track, wire } from "lwc";
import sendRequestLogNotes from "@salesforce/apexContinuation/CaseCommentsDataTable_LC_HUM.sendRequestLogNotes";
import saveCaseCommentData from "@salesforce/apex/CaseCommentsDataTable_LC_HUM.saveCaseCommentData";
import startSaveCommentOperation from "@salesforce/apex/CaseCommentsDataTable_LC_HUM.startSaveCommentOperation";
import caseValidations from "c/caseValidationsHum";
import customcss from "@salesforce/resourceUrl/LightningCRMAssets_SR_HUM";
import { loadStyle } from "lightning/platformResourceLoader";
import checkMedicareCalls from "@salesforce/apex/CaseDetails_LC_Hum.checkMedicareCalls";
import getCCMedicareCalls from "@salesforce/apex/CaseDetails_LC_Hum.getCCMedicareCalls";
import createUpdateCaseCommentRecord from "@salesforce/apex/MedicareCallsCaseComments_H_HUM_LWC.createUpdateCaseCommentRecord";
import casecommentfaulterror from '@salesforce/label/c.CASECOMMENT_PHARMACY_FAULTERROR_HUM';
import casecommentmembernotfound from '@salesforce/label/c.CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM';
import casecommentwarningmsg from '@salesforce/label/c.CASECOMMENT_PHARMACY_WARNING_HUM';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import { toastMsge } from "c/crmUtilityHum";
const HP_MEMBER_CASE = 'HP Member Case';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import { createLogNote } from 'c/genericPharmacyLogNotesIntegrationHum';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
const USER_FIELDS = [NETWORK_ID_FIELD];
export default class CaseCommentsFormHum extends caseValidations {
    @api oUserGroup;
    @api commentData; // variable used to get casecomment related data from closeCaseHum LWC
    @api oCaseObject; // variable used to store case related data from closeCaseHum LWC

    @track lstCodes; // this is used to store logcodes that will be visibe for pharmacy user
    @track bLogCodeVisible = false; // this is used to control the logcode visibility on HTML
    @track isCommentSaved; // flag to validate whether comment saved or not
    @track commentLimit; // this will used to hold the comment limit based on LOBs
    lstHpCases = ['Closed HP Agent/Broker Case', 'Closed HP Group Case', 'Closed HP Member Case', 'Closed HP Provider Case', 'Closed HP Unknown Case', 'HP Agent/Broker Case', 'HP Group Case', 'HP Member Case', 'HP Provider Case', 'HP Unknown Case'];
    bIsHpMemCase = false;
    @track maxLimitMsg;
    @track bDisplayLimitMsg = true;
    caseComments = true;
    medicareCallsComments = false;
    bMedicareCalls = false;
    bMediCCUpdate = false;
    @track isPharmacy = false;
    @track membernotfoundmsg = false;
    @track servicedown = false;
    @track hpComments;
    @track pharmacyLogCode;
    @api hpcasecommentview;
    @api hplogcode;
    @api hplogcodes;
    @api updatedrecordtype;
    @track newrecordType;
    @track networkId;
    @track userRecord;
    errorClass = 'slds-scoped-notification slds-media slds-media_center slds-theme_error';
    iconErrorClass = 'slds-icon_container slds-icon-utility-error';
    errAssistiveText = 'Error';
    iconname = 'utility:error';

    label = {
        casecommentfaulterror,
        casecommentmembernotfound,
        casecommentwarningmsg
    }

    connectedCallback() {
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

    /**
     * Method Name: loadOptions
     * Function: used to show logocdes and its visiblity on UI
     */
    loadOptions() {
        const serviceModelTypes = ['Insurance/Plan', 'Humana Pharmacy'];
        // below const used to store service model type from case object
        const serviceModelType = this.oCaseObject?.CTCI_List__r?.Service_Model_Type__c ?? null;
        // Below const is used to store values for Classification_Type__c field of case object to hide log code for HP member case
        const classficationTypes = ['Calls (RSO)', 'HP Clinical Services', 'HP Finance Ops', 'HP RxE Calls', 'HP Specialty Calls', 'Humana Pharmacy Calls', 'Humana Pharmacy Web Chat'];
        const classifcationType = this.oCaseObject && this.oCaseObject.hasOwnProperty('Classification_Type__c') ? this.oCaseObject.Classification_Type__c : null;


        try {
            this.lstCodes = this.commentData.lstLogCodes.map((item) => ({
                label: item,
                value: item
            }));
            // bLogCodeVisible variable used to control the logcode picklist visibility for HP and non-hp users

            this.bLogCodeVisible = this.isPharmacyUser()
                && this.lstCodes.length > 0 && this.oCaseObject?.RecordType?.Name === HP_MEMBER_CASE && serviceModelTypes?.includes(serviceModelType)
                && classficationTypes?.includes(classifcationType);

            this.lstCodes = [...[{ lable: '--None--', value: '--None--' }], ...this.lstCodes];

            this.hpComments = this.oCaseObject?.RecordType?.Name === HP_MEMBER_CASE ? true : false;

            this.commentLimit = this.isPharmacyUser() && this.lstHpCases.includes(this.oCaseObject?.RecordType?.Name) ? 1900 : 2900;
            this.maxLimitMsg = `${this.commentLimit} characters remaining`;

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

    isPharmacyUser() {
        if (this.commentData?.sProfileName === "Humana Pharmacy Specialist" || ((this.commentData?.sProfileName === "Customer Care Specialist" || this.commentData?.sProfileName === "Customer Care Supervisor") && hasCRMS_206_CCSHumanaPharmacyAccess)) {
            this.isPharmacy = true;
            return true;
        } else {
            this.isPharmacy = false;
            return false;
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
    fetchCommentValue() {
        return this.template.querySelector("[data-id='comments']"); // get comment in JS
    }

    @api
    fetchLogCode() {
        return this.pharmacyLogCode;

    }

    /**
     * Method Name: sendRequestToEpost
     * Function: this is used for sending comment and logocedes to EPOST once hit save and then save casecomment to DB
     */
    @api async sendRequestToEpost() {
        const inputCode = this.template.querySelectorAll("lightning-combobox"); // get logcode in JS
        const inputComment = this.template.querySelector("[data-id='comments']"); // get comment in JS

        if (this.bLogCodeVisible) { // to make sure that user is HP and log code is visible then only hit EPOST code
            let oParams = {};// to make sure that user is HP and log code is visible then only hit EPOST code
            oParams = {
                sCaseId: this.oCaseObject.Id,
                sCaseCommentBody: inputComment != null ? inputComment.value : '',
                sCode: this.pharmacyLogCode,
                bRedirect: false,
                bErrorCaseComment: "false"
            };
            const switchVal = getCustomSettingValue('Switch', 'HPIE Switch');
            switchVal.then(result => {
                if (result && result?.IsON__c && result?.IsON__c === true) {
                    this.callHpieService(oParams);
                } else {
                    this.callRSService(oParams, inputComment);
                }
            })            
        } else {
            try {
                let commentBody = '';
                if (this.bMedicareCalls == true && this.bMediCCUpdate == false) {
                    const commentsiss = this.template.querySelector("[data-id='commentsiss']");
                    const commentsres = this.template.querySelector("[data-id='commentsres']");
                    let iscmtsaved = await createUpdateCaseCommentRecord({
                        sMedicareCallsIssue: commentsiss.value,
                        sMedicareCallsRes: commentsres.value,
                        CaseId: this.oCaseObject.Id
                    });
                    commentBody = commentsiss.value + ' ' + commentsres.value;
                    if (iscmtsaved) {
                        this.isCommentSaved = 'Comment inserted';
                    }
                } else {
                    commentBody = inputComment.value;
                    let lstOfCaseComment = [{ CommentBody: inputComment.value }];
                    this.isCommentSaved = await saveCaseCommentData({
                        sCaseId: this.oCaseObject.Id,
                        caseCommentData: JSON.stringify(lstOfCaseComment)
                    });
                }
                if (this.isCommentSaved === 'Comment inserted') { // if to make sure real comment is inserted in DB with some values
                    this.afterSaveComment({ sCaseCommentBody: commentBody });
                }
            } catch (err) {
                console.error("Error in Comment save DB", err);
            }
        }
    }

    callRSService(oParams, inputComment) {
        sendRequestLogNotes({
            sComment: inputComment != null ? inputComment.value : '',
            sCode: this.pharmacyLogCode,
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
                if (oParams.bErrorCaseComment == 'true') {
                    toastMsge("", this.label.casecommentmembernotfound, "error", "pester");
                }
                else if (oParams.bErrorCaseComment == 'false') {
                    toastMsge("", this.label.casecommentfaulterror, "error", "pester");
                }
                this.callSaveComment(oParams);
            }
        }).catch((error) => {
            console.log("error in EPOST", error);
        });
    }

    callHpieService(oParams) {
            createLogNote(this.networkId, this.oCaseObject?.Account?.Enterprise_ID__c ?? '', this.organization ?? 'HUMANA', this.pharmacyLogCode, oParams?.sCaseCommentBody ?? '')
            .then(result => {
                if (result) {
                    oParams.bErrorCaseComment = '';
                }
                this.callSaveComment(oParams);
            }).catch(error => {
                if (error?.message?.toLowerCase()?.includes('patient not found')) {
                    oParams.bErrorCaseComment = "true";
                    toastMsge("", this.label.casecommentmembernotfound, "error", "pester");
                } else {
                    oParams.bErrorCaseComment = "false";
                    toastMsge("", this.label.casecommentfaulterror, "error", "pester");
                }
                console.log(error);
                this.callSaveComment(oParams);
            })
    }

    /**
     * Method Name: callSaveComment
     * Function: this is used for save casecomment to DB based on response comes from EPOST
     */
    async callSaveComment(oParams) {
        try {
            this.isCommentSaved = await startSaveCommentOperation(oParams);
            if (this.isCommentSaved === 'Comment inserted') {
                this.afterSaveComment({ sCaseCommentBody: oParams.sCaseCommentBody });
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

    /**
     * Method Name: checkCharLimit
     * Function: used to check character limit onchange on comment box
     */
    checkCharLimit() {
        let sCaseComment = this.template.querySelector(`[data-id='comments']`);
        this.maxLimitMsg = (this.commentLimit - sCaseComment.value.length) + ' characters remaining';
        if (this.commentLimit === sCaseComment.value.length) {
            sCaseComment.setCustomValidity('Warning: Exceeded character limit');
            this.bDisplayLimitMsg = true;
        } else {
            sCaseComment.setCustomValidity("");
            this.bDisplayLimitMsg = true;
        }
        sCaseComment.reportValidity();

    }

    /**
     * Method Name: checkMedicareCallsCaseComments
     * Function: this is used check the case is medicare calls case or not
     */
    @api
    checkMedicareCase() {
        let errMsg = 'CC:';
        if (this.bMedicareCalls == true && this.bMediCCUpdate == false) {
            errMsg = 'MCC:'
            const commentsiss = this.template.querySelector("[data-id='commentsiss']");
            const commentsres = this.template.querySelector("[data-id='commentsres']");
            let issue = commentsiss != null ? commentsiss.value : '';
            let resolution = commentsres != null ? commentsres.value : '';
            if (issue == null || issue == '' || resolution == null || resolution == '') {
                errMsg = 'MCC:Enter Medicare Calls Case Comments';
            } else if (issue.length < 10 || resolution.length < 10) {
                errMsg = 'MCC:A minimum of 10 characters are required, with a maximum of 2000';
            }
        }
        return errMsg;
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

    @api
    handleLogCodeValidation() {
        if (this.bLogCodeVisible) {
            const allValid = [...this.template.querySelectorAll('.validateLogCode')]
                .reduce((validSoFar, inputCmp) => {
                    inputCmp.reportValidity();
                    return validSoFar && inputCmp.checkValidity();
                }, true);
            const inputCode = this.template.querySelectorAll("lightning-combobox");
            const inputComment = this.template.querySelector("[data-id='comments']"); // get logcode in JS
            let res = inputComment && inputComment.value && !(/^\s*$/.test(inputComment.value));
            let code = inputCode[0] && inputCode[0].value && inputCode[0].value !== '--None--' && inputCode[0].value !== '';
            if (!(code && res)) {
                return false;
            }
        }
        return true;
    }

    @api
    handleCommentValidation() {
        let errMsg = 'CC::';
        const allValid = [...this.template.querySelectorAll('.validateCaseComment')]
            .reduce((validSoFar, inputCmp) => {
                inputCmp.reportValidity();
                return validSoFar && inputCmp.checkValidity();
            }, true);
        if (this.newrecordType === HP_MEMBER_CASE || this.oCaseObject?.RecordType?.Name === HP_MEMBER_CASE) {
            const inputComment = this.template.querySelector("[data-id='comments']"); // get comment in JS
            let res = inputComment && inputComment.value && !(/^\s*$/.test(inputComment.value));
            if (!res) {
                errMsg += 'Case Comment: You must enter a value';
            }
        }

        return errMsg;
    }

    handleChange(event) {
        if (event.target.name === 'pharmacyLogCode') {
            this.handlePharmacyLogCode(event);
        }
    }

    handlePharmacyLogCode(event) {
        this.pharmacyLogCode = event.target.value === '--None--' ? null : event.target.value;
    }

    @api
    casecommentvisibility(hpcasecommentview, hplogcodes, updatedrecordtype) {
        this.hpComments = hpcasecommentview ? true : false;
        this.commentLimit = hpcasecommentview ? 1900 : 2900;
        this.maxLimitMsg = `${this.commentLimit} characters remaining`;
        this.pharmacyLogCode = hplogcodes;
        this.newrecordType = updatedrecordtype;
    }

    @api
    hplogcodeclassification(hplogcode) {
        this.pharmacyLogCode = hplogcode;
    }
}