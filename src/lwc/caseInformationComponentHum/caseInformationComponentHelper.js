/*******************************************************************************************************************************
LWC JS Name : CaseInformationComponentHum.js
Function    : This JS serves as helper to caseInformationComponentHum.html

Modification Log:
Developer Name           Code Review                      Date                         Description
* Nirmal Garg                                             09/07/2023                   US-4908765
* Pooja Kumbhar                                     09/14/2023            US - 4900963 - T1PRJ0865978 - C06; Case Mgt- Lightning - Disable Save and Transfer buttons for Complaint cases that auto-route
* Pooja Kumbhar                                     09/14/2023            US - 4932577 - T1PRJ0865978 - C06; Case Mgt Lightning - Update Toast Error Message on click of buttons Auto Routed Templates- CASE DETAIL PAGE
* Ajay Chakradhar                                   09/17/2023            US - 4874911 - Lightning - Mentor Documents
* Apurva Urkude                                     10/27/2023            US - 4932577 - Lightning - switch code implementation
* Prasuna Pattabhi                                  10/13/2023            US 4828071 - Switch Revisited
* Nilesh Gadkar                                     10/13/2023            Us - 4918290 - Added Switch
* Santhi Mandava                                    10/13/2023            US4884468 : Added enableRelatedSection in helper to resolve limit issue
* Prasuna Pattabhi                                  3/1/2024              US 5373580 :  Find Case related JS files which might run into size limitation
* Jonathan Dickinson                                02/27/2024            User Story 5738539: T1PRJ1374973: DF 8518 - 8519 - 8520; C06 Case Management; Lightning - Case Comments - Error when adding comment before closing or transferring a case and notes reflected in incorrect section
*--------------------------------------------------------------------------------------------------------------------------------*/

import { createLogNote } from 'c/genericPharmacyLogNotesIntegrationHum';
import pubsub from 'c/pubSubHum';
import { toastMsge } from "c/crmUtilityHum";
import getMentorLinks from '@salesforce/apex/QuickStart_LC_Hum.getMentorLinks';
import getBusinessGroupName from '@salesforce/apex/QuickStart_LC_Hum.getBusinessGroupName';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';
import hasCRMS_CSERT_Complaints from '@salesforce/customPermission/CRMS_CSERT_Complaints'; 
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import callBenifitCategoryView from '@salesforce/apex/CaseDetails_LC_Hum.callBenifitCategoryView';
import getCallBenefitCategoryValues from '@salesforce/apex/CaseDetails_LC_Hum.getCallBenefitCategoryValues';
import { getUserGroup} from 'c/crmUtilityHum';
import populateAccountRecordType from "@salesforce/apex/CaseDetails_LC_Hum.populateAccountRecordTypeName";
import USER_ID from '@salesforce/user/Id';
import {getCaseLayout} from './layoutConfig';
import {getUnknownCaseLayout} from './unknownLayoutConfig';
export function callRSService(caseid) {
    this.sendCaseCommentsToEpost(caseid)
        .then((result) => {
            let bErrorCaseComment = 'false';
            if (result) {
                if (result[0] === 'true') {
                    bErrorCaseComment = '';
                } else if (result[0] === 'false') {
                    bErrorCaseComment = '';
                } else if (result[1] === "true") {
                    bErrorCaseComment = "true";
                } else if (result[1] === "false") {
                    bErrorCaseComment = "false";
                }
                if (bErrorCaseComment == '') {
                    // pubsub to refresh case comment custom LWC component
                    pubsub.fireCrossEvent(this.resultData.objCase.Id, 'refresh-case-comments', this.resultData.objCase.Id);
                }
                if (bErrorCaseComment == 'true') {
                    toastMsge("", this.Commentlabels.CASECOMMENT_MEMBERNOTFOUND_HUM, "error", "pester");
                    this.addCaseComment(caseid, this.Commentlabels.CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM);
                    pubsub.fireCrossEvent(caseid, 'case-comment-refresh', caseid);
                }
                else if (bErrorCaseComment == 'false') {
                    toastMsge("", this.Commentlabels.CASECOMMENT_FAULTERROR_HUM, "error", "pester");
                    this.addCaseComment(caseid, this.Commentlabels.CASECOMMENT_PHARMACY_FAULTERROR_HUM);
                    pubsub.fireCrossEvent(caseid, 'case-comment-refresh', caseid);
                }
            }
        })
        .catch((error) => {
            console.log("error in EPOST", error);

        });
}

export function callHpieService(caseid) {
        createLogNote(this.netId, this.enterpriseId, this.organization ?? 'HUMANA', this.pharmacyLogCode, this.comment)
        .then(result => {
            pubsub.fireCrossEvent(this.resultData.objCase.Id, 'refresh-case-comments', this.resultData.objCase.Id);
        }).catch(error => {
            if (error?.message?.toLowerCase()?.includes('patient not found')) {
                toastMsge("", this.Commentlabels.CASECOMMENT_MEMBERNOTFOUND_HUM, "error", "pester");
                this.addCaseComment(caseid, this.Commentlabels.CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM);
                pubsub.fireCrossEvent(caseid, 'case-comment-refresh', caseid);
            } else {
                toastMsge("", this.Commentlabels.CASECOMMENT_FAULTERROR_HUM, "error", "pester");
                this.addCaseComment(caseid, this.Commentlabels.CASECOMMENT_PHARMACY_FAULTERROR_HUM);
                pubsub.fireCrossEvent(caseid, 'case-comment-refresh', caseid);
            }
            console.log(error);
        })
}

/**
 * Method: MHKAutoRouteButtonDisable
 * @param {field,data} field - on chage of field name, Data - its value
 * Function: this method is used to disable close case button and save and Transfer button for MHK auto routing cases.
 **/
export function MHKAutoRouteButtonDisable(caseData) {
    if (caseData.Interacting_About_Type__c != null && caseData.Interacting_About_Type__c == 'Member') {
        this.isMHKIntearctingAbout = true;
    } else {
        this.isMHKIntearctingAbout = false;
    }
    if (caseData.Interacting_With_Type__c != null && this.lstInteractingwithType.includes(caseData.Interacting_With_Type__c)) {
        this.isMHKIntearctingWith = true;
    } else {
        this.isMHKIntearctingWith = false;
    }
    if (caseData.Complaint__c != null) {
        if (caseData.Complaint__c == 'Yes - Medicaid') {
            this.isMHKComplaintHPCases = false;
            this.isMHKComplaint = true;
        } else if (caseData.Complaint__c == 'Yes - Medicare Part C' || caseData.Complaint__c == 'Yes - Medicare Part D') {
            this.isMHKComplaint = true;
            this.isMHKComplaintHPCases = true;
        } else {
            this.isMHKComplaintHPCases = false;
            this.isMHKComplaint = false;
        }
    }
    if (caseData.Status != null && caseData.Status == 'Closed') {
        this.isMHKStatus = true;
    }
    if (this.serviceModel == 'Insurance/Plan') {
        this.isMHKIntnet = true;
    }
    if (this.isMHKIntearctingAbout == true && this.isMHKIntearctingWith == true) {
        if ((!this.resultData.prefillValues.caseRecordTypeName.includes('HP') && !this.resultData.prefillValues.caseRecordTypeName.includes('Unknown')) && this.isMHKComplaint) {
            this.isMHKAutoRoutingCase = true;
        } else if (this.resultData.prefillValues.caseRecordTypeName == 'HP Member Case' && this.isMHKIntnet && this.isMHKComplaintHPCases) {
            this.isMHKAutoRoutingCase = true;
        } else if (this.resultData.prefillValues.caseRecordTypeName == 'Closed HP Member Case' && this.isMHKIntnet && this.isMHKComplaintHPCases && this.isMHKStatus && (this.resultData.objCase.Case_Owner__C != 'Louisville RSO Oral Grievance Oversight' || this.userCurrentQueue != 'Louisville RSO Oral Grievance Oversight')) {
            this.isMHKAutoRoutingCase = true;
        } else {
            this.isMHKAutoRoutingCase = false;
        }
    } else {
        this.isMHKAutoRoutingCase = false;
    }
    if (this.userAccess.CRMS_400_Grievance_Appeals == true) {
        if (this.isMHKAutoRoutingCase && this.resultData.prefillValues.profileName == this.CCSpec && USER_ID != this.resultData.objCase.OwnerId && this.isCloseCase) {
            this.errorMessage = this.labels.EditAutoRouteToastMessage;
        } else if (this.isMHKAutoRoutingCase && (this.resultData.prefillValues.profileName == this.HPSpe || (this.resultData.prefillValues.profileName == this.HPSpe && this.userAccess.CRMS_300_Humana_Pharmacy_Supervisor))) {
            this.errorMessage = this.labels.EditAutoRouteToastMessage;
        } else {
            this.isMHKAutoRoutingCase = false;
        }
    } else {
        if (this.isMHKAutoRoutingCase && (this.resultData.prefillValues.profileName == this.CCSpec || this.resultData.prefillValues.profileName == this.CCSup || this.resultData.prefillValues.profileName == this.HPSpe || (this.resultData.prefillValues.profileName == this.HPSpe && this.userAccess.CRMS_300_Humana_Pharmacy_Supervisor))) {
            this.errorMessage = this.labels.EditAutoRouteToastMessage;
        } else {
            this.isMHKAutoRoutingCase = false;
        }
    }
    if (!this.isMHKAutoRoutingCase) {
        this.errorMessage = '';
    }
    return this.errorMessage;
}

/**
 * Method: getMentorDocumentsData
 * @param {field,data} field - on chage of field name, Data - its value
 * Function: this method is used to fetch mentor documents links for selected C&I.
 **/
export function getMentorDocumentsData(classificationLabel, intentLabel, idClassification, businessGroup) {
    getMentorLinks({
            classificationSelected: classificationLabel,
            intentSelected: intentLabel,
            idClassificationType: idClassification,
            sBusinessGroup: businessGroup
        })
        .then(MentorData => {
        this.lstMentorDocuments = [];
        if (MentorData != null) {
            this.lstMentorDocuments = MentorData;
            this.bIsShowDocumentSec = true;
            this.bShowMentorDocs = true;
        } else {
            this.lstMentorDocuments = [];
            if (this.lstReferenceDocuments == '' || this.lstReferenceDocuments == null || this.lstReferenceDocuments == undefined) {
                this.bIsShowDocumentSec = false;
            }
        }
        this.ciMentorlinkdata = {
            lstmentordocuments: this.lstMentorDocuments,
        };
    })

}

/**
 * Method: getCaseFieldValues
 * @param {field,data} field - on change of field name, Data - its value
 * Function: this method is used to fetch businessgroup details.
 **/
export function getCaseFieldValues(caseId, classificationLabel, intentLabel) {
    if (this.b4874911Switch) {
        getBusinessGroupName({
            caseId: caseId,
            sClassificationSelected: classificationLabel,
            sIntentSelected: intentLabel
        })
            .then(CaseData => {
                CaseData = JSON.parse(CaseData);
                if (CaseData != null) {
                    this.classificationLabel = CaseData.sClassificationName;
                    this.intentLabel = CaseData.sIntentName;
                    this.idClassification = CaseData.idClassificationType;
                    this.businessGroup = CaseData.sBusinessGroup;
                }
                if (this.classificationLabel != null && this.intentLabel != null && this.idClassification != null && this.businessGroup != null) {
                    getMentorDocumentsData.call(this, this.classificationLabel, this.intentLabel, this.idClassification, this.businessGroup);
                }
            })
    }
}

export function enableRelatedSection(me) {
    const recordTypeName = me.resultData.prefillValues.caseRecordTypeName;
    if (me.resultData.objCase.Type == 'SIU' || me.resultData.objCase.Type == 'PPI' || (me.resultData.objCase.Type == 'Correspondence Inquiry' && me.resultData.objCase.Subtype__c == null)) {
        me.bIntWithType = true;
    }
    else
        me.bIntWithType = false;
    if (me.resultData.objCase.Origin == 'IVR' && me.resultData.objCase.Type == 'IVR Afterhours') {
        me.bIsIVRcase = true;
    }
    else
        me.bIsIVRcase = false;
    if (me.resultData.objCase.Interacting_About_Type__c == 'Member') {
        me.bEditMemberPlan = true;
    }
    else
        me.bEditMemberPlan = false;
    if (recordTypeName == 'Member Case' || recordTypeName == 'Provider Case' || recordTypeName == 'Unknown Case') {
        if (me.resultData.objCase.Origin == 'Correspondence' && me.resultData.objCase.Type == 'MHK Dispute Task') {
            me.bIntWithTypeOrigin = true;
        }
        else
            me.bIntWithTypeOrigin = false;
    }
    if (me.bIsIVRcase || me.bIntWithType || me.bIntWithTypeOrigin ||
        (recordTypeName == 'Medicare Case') &&
        (me.resultData.objCase.Interacting_About_Type__c == 'Provider' || me.resultData.objCase.Interacting_About_Type__c == 'Unknown-Provider' || me.resultData.objCase.Interacting_About_Type__c == 'Member' || me.resultData.objCase.Interacting_About_Type__c == 'Unknown-Member')
    ) {
        me.bEditAccName = true;
    }
    else
        me.bEditAccName = false;
}

/* Function: this method is used to fetch Switch details.
 **/   
export function getSwitchData() {
    return new Promise((resolve, reject) => {
        isCRMFunctionalityONJS({ sStoryNumber: ['4932577','4828071','4918290','4874911'] })
            .then(result => {
                this.bisSwitchOn4932577 = result['4932577'];
                this.bisSwitchOn4828071 = result['4828071'];
                this.bisSwitchOn4918290 = result['4918290'];
                this.b4874911Switch = result['4874911'];
                resolve(true);
            }).catch(error => {
                console.log(error);
                reject(false);
            })
    })
}

/**
    * Method Name:showEsclIndicator
    * @param {*} interactingAbout ,caseOrigin,classificationType
    * Function used to check if Escalation Indicator to be shown in the UI or not
    */
export function showEsclIndicator(interactingAbout, caseOrigin, classificationType) {
    if ((this.lstInteractingAboutType.includes(interactingAbout)) && (!this.lstInteractionOrigin.includes(caseOrigin))) {
        this.isEscIndicatorDisplay = true;
        if (caseOrigin === 'IVR' && classificationType != 'Calls (RSO)') {
            this.isEscIndicatorDisplay = false;
        }
    }
    const escIndDateValue = this.resultData.objCase.Escalation_Indicator_Date__c;
    if (escIndDateValue != null) {
        this.isEscIndicatorDateDisplay = true;
    }
}
export function showDCNUnknown() {
    if (this.resultData.objCase.Type == 'Correspondence Inquiry' || this.resultData.objCase.Type == 'MHK Dispute Task') {
        if (this.resultData.objCase.DCN__c != null && this.resultData.objCase.DCN__c != '') {
            this.isDCNLink = true;
            this.isDCNDisplay = false;
        } else {
            this.isDCNDisplayUnknown = true;
            this.isDCNDisplay = true;
        }
    } else {
        this.isDCNDisplayUnknown = false;
    }
}
/**
       * Method Name: getCommonLayout
       * Function: Used to build the layout with the conditional rendering fields
    */

export function getCommonLayout(recordTypeName) {

    const isClosed = recordTypeName.indexOf('Closed') > -1 ? true : false;

    this.layoutData = {};
    this.layoutData.Election_Type_Code__c = {};
    this.layoutData.Call_Benefit_Category__c = {};
    this.layoutData.Due_Date__c = {};
    this.layoutData.Verbal_Consent_Obtained__c = {};
    this.layoutData.Escalation_Indicator__c = {};
    this.layoutData.Escalation_Indicator_Date__c = {};
    this.layoutData.OGO_Resolution_Type__c = {};
    this.layoutData.OGO_Resolution_Date__c = {};
    this.layoutData.Rx_Complaint_date__c = {};
    this.layoutData.Rx_Complaint_origin__c = {};
    this.layoutData.Rx_Complaint_category__c = {};
    this.layoutData.Rx_Complaint_reason__c = {};
    this.layoutData.DCN = {};
    this.layoutData.DocType = {};
    this.layoutData.pendKey = {};
    this.layoutData.account = {};
    this.layoutData.metTrackingId = {};
    this.layoutData.memberPlan = {};
    this.layoutData.medicareId = {};
    this.layoutData.Oral_Grievance_Category__c = {};
    this.layoutData.Oral_Grievance_Sub_Category__c = {};
    this.layoutData.caseComments = {};
    this.layoutData.METEnrollmentSection = {};
    this.layoutData.Interacting_About_Type__c = {};
    this.layoutData.Interacting_With_Name__c = {};
    this.layoutData.accountName = {};

    this.requiredFields = {};
    this.requiredFields.compliantDate = "";
    this.requiredFields.compliantOrigin = "";
    this.requiredFields.compliantCategory = "";
    this.requiredFields.compliantReason = "";
    this.requiredFields.medicarePartcPartD = "";
    this.requiredFields.accountId = "";
    if (this.isEscIndicatorDisplay) {
        if (isClosed) {
            this.layoutData.Escalation_Indicator__c = { label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c', required: false, outputField: true, readOnly: true }
        } else {
            this.layoutData.Escalation_Indicator__c = { label: 'Escalation Indicator', mapping: 'Escalation_Indicator__c', required: false, value: '', input: true }
        }
    }
    if (this.isEscIndicatorDateDisplay) {
        if (isClosed) {
            this.layoutData.Escalation_Indicator_Date__c = { label: 'Escalation Indicator Date', mapping: 'Escalation_Indicator_Date__c', required: false, outputField: true, readOnly: true }
        }
        else {
            this.layoutData.Escalation_Indicator_Date__c = { label: 'Escalation Indicator Date', mapping: 'Escalation_Indicator_Date__c', required: false, value: '', input: true }
        }
    }

    if (this.isEdit == true && this.userAccess.CRMS_630_MedicareElectionTracking_EnrollmentEdit == true && recordTypeName == 'Medicare Case') {
        this.layoutData.METEnrollmentSection = {
            title: 'MET Enrollment',
            onerow: true,
            fields: [],
            isMETSec: true
        }
    }


    if (this.showMedicareCallsComments == true) {
        this.layoutData.caseComments = {
            title: 'Medicare Calls Case Comments',
            onerow: true,
            fields: [
                { label: 'Medicare Calls Issue', mapping: 'caseComment', medicareCallsTextareaIss: true },
                { label: 'Medicare Calls Resolution', mapping: 'caseComment', medicareCallsTextareaRes: true }
            ]
        };
    } else {
        this.layoutData.caseComments = {
            title: 'Case Comment',
            onerow: true,
            fields: [
                { label: 'Case Comments', mapping: 'caseComment', textarea: true }
            ]
        }
    }

    if (this.userAccess.Oral_Grievance_Oversight_OGO == true) {
        if (isClosed) {
            this.layoutData.Oral_Grievance_Category__c = { label: 'Oral Grievance Category', mapping: 'Oral_Grievance_Category__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
            this.layoutData.Oral_Grievance_Sub_Category__c = { label: 'Oral Grievance Sub-Category', mapping: 'Oral_Grievance_Sub_Category__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
        } else {
            this.layoutData.Oral_Grievance_Category__c = { label: 'Oral Grievance Category', mapping: 'Oral_Grievance_Category__c', input: true, value: '' };
            this.layoutData.Oral_Grievance_Sub_Category__c = { label: 'Oral Grievance Sub-Category', mapping: 'Oral_Grievance_Sub_Category__c', input: true, value: '' };
        }
    }
    if (this.userAccess.RapidForceUser_AHT_HUM && (
        hasCRMS_684_Medicare_Customer_Service_Access || hasCRMS_205_CCSPDPPharmacyPilot || this.userAccess.MedicareElectionTrackingEnrollmentUser_HUM)) {
        if (isClosed) {
            this.layoutData.Verbal_Consent_Obtained__c = {
                label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c', radio: true, hasHelp: false, helpText: "", readOnly: true,
                radioFields: [{ label: 'Yes', value: false, readOnly: true }, { label: 'No', value: false, readOnly: true }, { label: 'Not Required', value: false, readOnly: true }]
            }
        } else {
            this.layoutData.Verbal_Consent_Obtained__c = {
                label: 'Verbal Consent Obtained', mapping: 'Verbal_Consent_Obtained__c', radio: true, hasHelp: false, helpText: "",
                radioFields: [{ label: 'Yes', value: false }, { label: 'No', value: false }, { label: 'Not Required', value: false }]
            }
        }
    }
    if (this.userAccess.CRMS_400_Grievance_Appeals == true) {
        if (isClosed) {
            this.layoutData.OGO_Resolution_Type__c = { label: 'Resolution Type', mapping: 'OGO_Resolution_Type__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
            this.layoutData.OGO_Resolution_Date__c = { label: 'Resolution Date', mapping: 'OGO_Resolution_Date__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
        } else {
            this.layoutData.OGO_Resolution_Type__c = { label: 'Resolution Type', mapping: 'OGO_Resolution_Type__c', input: true, value: '' };
            this.layoutData.OGO_Resolution_Date__c = { label: 'Resolution Date', mapping: 'OGO_Resolution_Date__c', input: true, value: '' };
        }
    }

    if (hasCRMS_CSERT_Complaints) {
        if (isClosed) {
            this.layoutData.Rx_Complaint_date__c = { label: 'Complaint Date', mapping: 'Rx_Complaint_date__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
            this.layoutData.Rx_Complaint_origin__c = { label: 'Complaint Origin', mapping: 'Rx_Complaint_origin__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
            this.layoutData.Rx_Complaint_category__c = { label: 'Complaint Category', mapping: 'Rx_Complaint_category__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
            this.layoutData.Rx_Complaint_reason__c = { label: 'Complaint Reason', mapping: 'Rx_Complaint_reason__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
        } else {
            this.layoutData.Rx_Complaint_date__c = { label: 'Complaint Date', mapping: 'Rx_Complaint_date__c', input: true, value: '', required: true };
            this.layoutData.Rx_Complaint_origin__c = { label: 'Complaint Origin', mapping: 'Rx_Complaint_origin__c', input: true, value: '', required: true };
            this.layoutData.Rx_Complaint_category__c = { label: 'Complaint Category', mapping: 'Rx_Complaint_category__c', input: true, value: '', required: true };
            this.layoutData.Rx_Complaint_reason__c = { label: 'Complaint Reason', mapping: 'Rx_Complaint_reason__c', input: true, value: '', required: true };

            this.requiredFields.compliantDate = "Rx_Complaint_date__c";
            this.requiredFields.compliantOrigin = "Rx_Complaint_origin__c";
            this.requiredFields.compliantCategory = "Rx_Complaint_category__c";
            this.requiredFields.compliantReason = "Rx_Complaint_reason__c";
        }
    }

    if (this.isEdit == true && recordTypeName == 'Member Case') {
        if (this.resultData.objCase.Origin == 'Correspondence' && this.resultData.objCase.Type == 'MHK Dispute Task') {
            this.layoutData.Origin = { label: 'Case Origin', mapping: 'Origin', outputField: true, readOnly: true };
            this.layoutData.Type = { label: 'Type', mapping: 'Type', outputField: true, readOnly: true, identifier: 'ctype' };
        } else {
            this.layoutData.Origin = { label: 'Case Origin', mapping: 'Origin', required: true, value: '', input: true };
            this.layoutData.Type = { label: 'Type', mapping: 'Type', input: true, value: '', identifier: 'ctype' };
        }

    }

    if (this.isEdit == true && (this.bIntWithTypeOrigin == true || this.bIsIVRcase == true || this.bIntWithType == true)) {
        this.layoutData.Interacting_About_Type__c = { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', input: true, value: '' };
    }
    else {
        this.layoutData.Interacting_About_Type__c = { label: 'Interacting About Type', mapping: 'Interacting_About_Type__c', outputField: true, value: '', readOnly: true };
    }
    if (this.isEdit == true && (this.bIntWithType == true || this.bIsIVRcase == true)) {
        this.layoutData.Interacting_With_Name__c = { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', input: true, value: '' };
    }
    else {
        this.layoutData.Interacting_With_Name__c = { label: 'Interacting With Name', mapping: 'Interacting_With_Name__c', outputFieldHelpText: true, value: '', readOnly: true };
    }
    if (this.isEdit == true && (this.bEditAccName == true || this.bIntWithType == true || this.bIsIVRcase == true)) {
        if((recordTypeName == 'Member Case' || recordTypeName == 'Provider Case') && this.switch_4884468){
            this.layoutData.accountName = { label: 'Account Name', mapping: 'AccountId', accNameLookup: true, required: true, identifier: 'case-accname' }
            this.requiredFields.accountId = "AccountId";
        }else{
            this.layoutData.accountName = { label: 'Account Name', mapping: 'AccountId', lookupInput: true, required: true, identifier: 'case-accname' }
            this.requiredFields.accountId = "AccountId";
        }
    }
    else {
        this.layoutData.accountName = { label: 'Account Name', source: 'prefillValues', mapping: 'accountName', customOutput: true, value: '', readOnly: true, identifier: 'case-accname' };
    }
    if (this.isEdit == true && this.bEditMemberPlan == true) {
        this.layoutData.memberPlan = { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: false, identifier: 'case-memberplan', outputFieldSize: 'slds-p-horizontal_xx-small' };
    }
    else if (this.bIntWithTypeOrigin == true || this.bIsIVRcase == true || this.bIntWithType == true) {
        this.layoutData.memberPlan = { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: true, identifier: 'case-memberplan', outputFieldSize: 'slds-p-horizontal_xx-small' };
    }
    else {
        this.layoutData.memberPlan = { label: 'MemberPlanHide', mapping: 'Member_Plan_Id__c', outputField: true, value: '', readOnly: true, outputFieldSize: 'slds-hidden' };
    }

    if (this.resultData.objCase.DCN__c != null && this.resultData.objCase.DCN__c != '') {
        this.isDCNDisplay = false;
        this.layoutData.DCN = { label: 'DCN', mapping: 'dcnFormula', link: true, value: '', source: 'additionalInfo', name: 'DCN__c' }; // Check this display it should be link
    } else {
        if (isClosed) {
            this.layoutData.DCN = { label: 'DCN', mapping: 'DCN__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
            this.isDCNDisplay = false;
        } else {
            if (this.resultData.objCase.Type == 'Correspondence Inquiry' || this.resultData.objCase.Type == 'MHK Dispute Task') {
                this.layoutData.DCN = { label: 'DCN', mapping: 'DCN__c', input: true, value: '', identifier: 'case-dcnid', disabled: false };
            } else {
                this.layoutData.DCN = { label: 'DCN', mapping: 'DCN__c', input: true, value: '', identifier: 'case-dcnid', disabled: true };
            }
            this.isDCNDisplay = true;
        }
    }

    if (this.resultData.objCase.Type == 'Customer Inquiry') {
        this.layoutData.pendKey = { label: 'Pend Key', mapping: 'Pend_Key__c', identifier: 'case-pendkey', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' }
    }

    if (this.resultData.objCase.Type == 'Correspondence Inquiry' && this.resultData.objCase.Origin == 'Correspondence') {
        this.layoutData.DocType = { label: 'Doc Type', mapping: 'Doc_Type__c', identifier: 'case-doctype', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
    }
    if (this.resultData.objCase.Origin == 'Inbound Call' && hasCRMS_684_Medicare_Customer_Service_Access && !this.lstHpCases.includes(recordTypeName)) {
        if (this.isCallBenefitCategoryView) {
            if (isClosed) {
                this.layoutData.Call_Benefit_Category__c = { label: 'Medicare Call Part C or Part D', mapping: 'Call_Benefit_Category__c', picklistWithHelptextReadonly: true, readonly: true, helpText: '•Medicare Part C is used for Medicare medical calls •Medicare Part D is used for Medicare prescription drug calls •Not Medicare is used for non-Medicare calls', outputFieldSize: 'slds-p-horizontal_xx-small' };
                this.requiredFields.medicarePartcPartD = "false";
            } else {
                this.layoutData.Call_Benefit_Category__c = { label: 'Medicare Call Part C or Part D', mapping: 'medicarePartcPartD', identifier: 'case-medicare-part-c-d', source: 'additionalInfo', required: true, picklistWithHelptext: true, options: [], selectedValue: 'selectedMedicarePartcPartD', hasHelp: true, helpText: '•Medicare Part C is used for Medicare medical calls •Medicare Part D is used for Medicare prescription drug calls •Not Medicare is used for non-Medicare calls' };
                this.requiredFields.medicarePartcPartD = "true";
            }
        } else if (this.resultData.additionalInfo.interactingAbtType == 'Member') {
            if (isClosed) {
                this.requiredFields.medicarePartcPartD = "false";
                this.layoutData.Call_Benefit_Category__c = { label: 'Medicare Call Part C or Part D', mapping: 'Call_Benefit_Category__c', picklistWithHelptextReadonly: true, readonly: true, helpText: '•Medicare Part C is used for Medicare medical calls •Medicare Part D is used for Medicare prescription drug calls •Not Medicare is used for non-Medicare calls', outputFieldSize: 'slds-p-horizontal_xx-small' };
            } else {
                this.layoutData.Call_Benefit_Category__c = { label: 'Medicare Call Part C or Part D', mapping: 'medicarePartcPartD', identifier: 'case-medicare-part-c-d', source: 'additionalInfo', required: true, picklistWithHelptext: true, options: [], selectedValue: 'selectedMedicarePartcPartD', hasHelp: true, helpText: '•Medicare Part C is used for Medicare medical calls •Medicare Part D is used for Medicare prescription drug calls •Not Medicare is used for non-Medicare calls' };
                this.requiredFields.medicarePartcPartD = "true";
            }
        }
    }
    if (recordTypeName == 'Closed Medicare Case' || recordTypeName == 'Medicare Case') {

        if (this.userAccess.CRMS_630_MedicareElectionTracking_EnrollmentEdit == true) {
            if (isClosed) {
                this.layoutData.Election_Type_Code__c = { label: 'Election Type Code(ETC)', mapping: 'Election_Type_Code__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' }
            } else {
                this.layoutData.Election_Type_Code__c = { label: 'Election Type Code(ETC)', mapping: 'Election_Type_Code__c', input: true, value: '' }
            }
        }

        if (this.resultData.objCase.Origin == 'DEAA') {
            this.layoutData.Due_Date__c = { label: 'Expiration Date', mapping: 'Due_Date__c', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
        }

        if (this.resultData.objCase.Type == 'Customer Inquiry') {
            this.layoutData.pendKey = { label: 'Pend Key', mapping: 'Pend_Key__c', identifier: 'case-pendkey', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' }
        }

        if (!isClosed) {
            this.layoutData.accountName = { label: 'Account Name', mapping: 'AccountId', lookupInput: true, required: true, identifier: 'case-accname' }
            this.requiredFields.accountId = "AccountId";
        } else {
            this.layoutData.accountName = { label: 'Account Name', mapping: 'AccountId', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
        }

        if (!isClosed) {
            this.layoutData.memberPlan = { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, identifier: 'case-memberplan', readOnly: false };
        } else {
            this.layoutData.memberPlan = { label: 'MemberPlan', mapping: 'Member_Plan_Id__c', customlookup: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
        }
        this.layoutData.medicareId = {};
        this.layoutData.medicareId = { label: 'Medicare ID', source: 'prefillValues', mapping: 'medicareId', value: '', customOutput: true, readOnly: true, identifier: 'case-medicareid', outputFieldSize: 'slds-p-horizontal_xx-small' };

        this.layoutData.taxId = {};
        this.layoutData.npiId = {};
        if (!isClosed && (this.resultData.additionalInfo.interactingAbtType == 'Unknown-Member')) {
            this.layoutData.memberPlan = {};
        }
        if (!isClosed && (this.resultData.additionalInfo.interactingAbtType == 'Provider' || this.resultData.additionalInfo.interactingAbtType == 'Unknown-Provider')) {
            this.layoutData.taxId = { label: 'Tax ID', mapping: 'Tax_ID__c', outputField: true, value: '', readOnly: true };
            this.layoutData.npiId = { label: 'NPI ID', mapping: 'NPI_ID__c', outputField: true, value: '', readOnly: true };
            this.layoutData.memberPlan = {};
            this.layoutData.medicareId = {};
            this.layoutData.Call_Benefit_Category__c = {};
            this.requiredFields.medicarePartcPartD = "false";
        }

        if (
            !isClosed && (
                this.resultData.additionalInfo.interactingAbtType == 'Agent' ||
                this.resultData.additionalInfo.interactingAbtType == 'Group' ||
                this.resultData.additionalInfo.interactingAbtType == 'Unknown-Group' ||
                this.resultData.additionalInfo.interactingAbtType == 'Unknown-Agent')
        ) {
            this.layoutData.accountName = { label: 'Account Name', mapping: 'AccountId', outputField: true, readOnly: true, outputFieldSize: 'slds-p-horizontal_xx-small' };
            this.layoutData.memberPlan = {};
            this.layoutData.medicareId = {};
            this.layoutData.Call_Benefit_Category__c = {};
            this.requiredFields.medicarePartcPartD = "false";
        }
    }

}
/**
       * Method Name: setVisibilities
       * Function: Used to Set the disable property based on the memebr plan id selected
    */

export async function setVisibilities(fieldName, value) {

    if (fieldName == 'Member_Plan_Id__c' && this.resultData.objCase.Origin == 'Inbound Call' && hasCRMS_684_Medicare_Customer_Service_Access) {

        const medicarePartcd = this.template.querySelector('[data-id="case-medicare-part-c-d"]');
        if (medicarePartcd != undefined && medicarePartcd != null) {
            if (value) {
                let callBenefitCategoryView = await callBenifitCategoryView({
                    memplanId: value,
                    interactingAbtType: this.resultData.additionalInfo.interactingAbtType,
                    sOrigin: this.resultData.objCase.Origin
                });
                if (callBenefitCategoryView) {
                    medicarePartcd.disabled = false;
                    medicarePartcd.required = true;
                    let updatedData = await getCallBenefitCategoryValues({ memplanId: value });
                    medicarePartcd.options = [...updatedData];
                    this.requiredFields.medicarePartcPartD = "true";

                } else {
                    medicarePartcd.disabled = true;
                    medicarePartcd.required = false;
                    medicarePartcd.options = [...[{ label: '--None--', value: '--None--' }]];
                    this.requiredFields.medicarePartcPartD = "false";
                }
            } else if (this.isCallBenefitCategoryView || this.resultData.additionalInfo.interactingAbtType == 'Member') {
                medicarePartcd.disabled = false;
                medicarePartcd.required = true;
                let updatedData = await getCallBenefitCategoryValues({ memplanId: value });
                medicarePartcd.options = [...updatedData];
                this.requiredFields.medicarePartcPartD = "true";
            } else {
                medicarePartcd.disabled = true;
                medicarePartcd.required = false;
                medicarePartcd.options = [...[{ label: '--None--', value: '--None--' }]];
                this.requiredFields.medicarePartcPartD = "false";
            }

            medicarePartcd.value = '--None--';

        }
    }
}
/**
     * Method Name: preFillValues
     * @param {*} result is the response coming from apex
     * Function: populate value for prefill fields, this method is being called from caseDetaildata
     */
export async function preFillValues(result) {
    const AccountRecordName = await populateAccountRecordType({ objectID: this.encodedData });
    const recordTypeName = result.prefillValues.caseRecordTypeName;
    this.caseRecType = recordTypeName;
    const checkProfileName = result.prefillValues.profileName;

    if (result.prefillValues.Subtype != undefined && result.prefillValues.Subtype != null && result.prefillValues.Subtype != '') {
        this.prevSubType = result.prefillValues.Subtype;
        this.caseSubtype = this.prevSubType + '_' + Date.now();
    }
    else {
        this.prevSubType = '';
        this.caseSubtype = ''
    }

    if (result.prefillValues.caseType != undefined && result.prefillValues.caseType != null && result.prefillValues.caseType != '') {
        this.caseType = result.prefillValues.caseType + '_' + Date.now();
    }
    else {
        this.caseType = ''
    }

    if (this.lstHpCases.includes(recordTypeName) && (checkProfileName === "Humana Pharmacy Specialist" || checkProfileName === "Customer Care Specialist" || checkProfileName === "Customer Care Supervisor")) {
        this.commentLimit = 1900;
        this.maxLimitMsg = this.commentLimit + ' characters remaining';
    } else {
        this.commentLimit = 2900;
        this.maxLimitMsg = this.commentLimit + ' characters remaining';
    }
    let jsonModel;
    this.isCallBenefitCategoryView = result.additionalInfo.showCallBenifitCategoryView;
    this.userAccess = result.additionalInfo.userAccess;
    if (this.isFromQS && this.medicarePartCPartDQS != '') {
        result.additionalInfo.selectedMedicarePartcPartD = this.medicarePartCPartDQS;
        this.case.Call_Benefit_Category__c = this.medicarePartCPartDQS;
    }
    let bMedicareCalls = result.additionalInfo.bMedicareCalls;
    this.bMediCCUpdate = result.additionalInfo.bMediCCUpdate;
    this.showMedicareCallsComments = false;
    if (bMedicareCalls == true
        && (checkProfileName === "Customer Care Specialist" || checkProfileName === "Customer Care Supervisor")
        && (recordTypeName != 'Medicare Case' || recordTypeName != 'Closed Medicare Case') && !this.lstHpCases.includes(recordTypeName)) {
        this.showMedicareCallsComments = true;
    }
    if (this.showMedicareCallsComments == true) {
        this.commentLimit = 2000;
        this.maxLimitMsgIss = this.commentLimit + ' characters remaining';
        this.maxLimitMsgRes = this.commentLimit + ' characters remaining';
    }
    if (!(this.isEdit && (checkProfileName === "Customer Care Supervisor" || (USER_ID == result.objCase.OwnerId)))) {
        if (this.recordId) {
            this.closeCaseDisabled = true;
            this.saveTransferDisabled = true;
        }
    }

    getCommonLayout.call(this,recordTypeName);
    if (recordTypeName === 'Unknown Case' || recordTypeName === 'HP Unknown Case' || recordTypeName === 'Closed HP Unknown Case' || recordTypeName === 'Closed Unknown Case') {
        jsonModel = getUnknownCaseLayout(this.recordId, recordTypeName, getUserGroup(), result.prefillValues.profileName, AccountRecordName, this.isEscIndicatorDisplay, this.bIntWithType, this.bIsIVRcase, this.bIntWithTypeOrigin, this.bEditMemberPlan, this.bEditAccName, this.isDCNDisplayUnknown, this.isDCNLink);
    } else {
        jsonModel = getCaseLayout(this.recordId, recordTypeName, getUserGroup(), result.prefillValues.profileName, this.layoutData, this.isEscIndicatorDisplay);
    }

    let caseModel = jsonModel ? JSON.parse(JSON.stringify(jsonModel)) : [];
    caseModel.forEach((item) => {
        item.itemSize = "slds-col slds-size_1-of-1 slds-p-around_small";
        item.fieldSize = "slds-col slds-size_1-of-3";
        switch (item.title) {
            case this.labels.HUMRelatedAccounts:
                if (recordTypeName === 'Unknown Case' || recordTypeName === 'HP Unknown Case') {
                    item.itemSize = this.isEdit
                        ? "slds-col slds-size_1-of-1 slds-p-around_small"
                        : "slds-col slds-size_1-of-1 slds-p-around_small";
                } else {
                    item.itemSize = this.isEdit
                        ? "slds-col slds-size_1-of-1 slds-p-around_small"
                        : "slds-col slds-size_1-of-1 slds-p-around_small";
                }
                break;
            case this.labels.HUMSystemInformation:
                item.isCase = false;
                item.itemSize =
                    "slds-col slds-size_1-of-1 slds-p-around_small system-info slds-m-bottom_xx-large system";
                item.fieldSize = "slds-col slds-size_1-of-3";
                break;
            case "Case Comment":
                item.isCase = true;
                item.itemSize = "slds-col slds-size_1-of-1 slds-p-around_small";
                break;
            case "Case Comments":
                item.isCase = true;
                item.itemSize = "slds-col slds-size_1-of-1 slds-p-around_small";
                break;
            case "Medicare Calls Case Comments":
                item.isCase = true;
                item.itemSize = "slds-col slds-size_1-of-1 slds-p-around_small";
                break;
            case "MET Enrollment":
                item.isCase = true;
                item.itemSize = "slds-col slds-size_1-of-1 slds-p-around_small";
                break;
        }
        item.fields.forEach((fl) => {
            const classificatioToIntent =
                this.resultData.ctciModel.classificationToIntent;
            if (fl.picklist) {
                fl.options = result[fl.source][fl.mapping]
                    ? [
                        ...[{ label: "--None--", value: "--None--" }],
                        ...result[fl.source][fl.mapping]
                    ]
                    : [];
                fl.readOnly = !(fl.options.length > 1);
                if (fl.mapping == 'classificationToIntentValues' && this.processResultDetails != undefined && this.processResultDetails.bHasCTCI == "true") {
                    fl.readOnly = true;
                }
                //below if is to check if intent picklist has values. if so autopopulate it on the case edit screen.
                if (
                    result[fl.source][fl.selectedValue] &&
                    fl.selectedValue === "intentName"
                ) {
                    fl.options = result[fl.source].classificationName
                        ?
                        [
                            ...[{ label: "--None--", value: "--None--" }],
                            ...classificatioToIntent[result[fl.source].classificationName] ?
                                [
                                    ...classificatioToIntent[result[fl.source].classificationName]
                                ] :
                                [{ label: "--None--", value: "--None--" }]
                        ]
                        : [{ label: "--None--", value: "--None--" }];
                    fl.readOnly = !(fl.options.length > 1);
                    if (fl.mapping == 'CTCI_List__c' && this.processResultDetails != undefined && this.processResultDetails.bHasCTCI == "true") {
                        fl.readOnly = true;
                    }
                    const intentMap =
                        this.resultData.ctciModel.mpOfclassificationIntentToCTCIId[
                            result[fl.source].classificationName] ?
                            this.resultData.ctciModel.mpOfclassificationIntentToCTCIId[
                            result[fl.source].classificationName] : '';
                    fl.value = intentMap[result[fl.source][fl.selectedValue]];
                }
                // below if is to check if classification picklist has values. if so autopopulate it on case edit screen.
                if (result[fl.source][fl.selectedValue]) {
                    fl.value = result[fl.source][fl.selectedValue];
                } else {
                    fl.value = "--None--";
                }
            } else if (fl.customOutput) {
                fl.value = result[fl.source][fl.mapping] ? result[fl.source][fl.mapping] : '';
            } else if (fl.radio) {
                // this condition is to autopopulate response status radio on edit screen if slelected while creating the case
                let rds = fl.radioFields.filter((item) => {
                    return item.label === result.objCase[fl.mapping];
                });
                rds[0] instanceof Object ? (rds[0].value = true) : "";
            } else if (fl.picklistWithHelptext) {
                fl.options = result[fl.source][fl.mapping] ? [...result[fl.source][fl.mapping]] : [];
                let hasOptions = (fl.options.length > 1);
                fl.readOnly = !hasOptions;
                if (result[fl.source][fl.selectedValue] && hasOptions) {
                    fl.value = result[fl.source][fl.selectedValue];
                } else {
                    fl.value = "--None--";
                    this.requiredFields.medicarePartcPartD = "false";
                    fl.required = false;
                }
            } else if (fl.picklistWithHelptextReadonly) {
                this.requiredFields.medicarePartcPartD = "false";
            } else if (fl.link) {
                fl.mapping = result[fl.source][fl.mapping] ? result[fl.source][fl.mapping] : '';
                fl.value = result.objCase[fl.name];
            } else if (fl.customLabelInput && result.objCase[fl.mapping]) {
                fl.value = result.objCase[fl.mapping];
            }
            else if (result.objCase[fl.mapping]) {
                fl.value = result.objCase[fl.mapping];
                if (fl.mapping == 'Status' && this.processResultDetails != undefined && this.processResultDetails.bDisableCaseStatus == "true") {
                    fl.disabled = true;
                }
            }

            if (this.processResultDetails != undefined) {
                if ((fl.mapping == 'Type' && this.processResultDetails.bDisableType != undefined && this.processResultDetails.bDisableType == "true") ||
                    (fl.mapping == 'Subtype__c' && this.processResultDetails.bDisableSubtype != undefined && this.processResultDetails.bDisableSubtype == "true")) { fl.disabled = true; }
                if (this.processResultDetails.bdisablecloseCancel == 'true') {
                    this.closeCaseDisabled = true;
                    this.cancelCaseDisabled = true;
                    this.SaveCaseDisabled = false;
                }
                if (this.processResultDetails.bTransferBtnDisabled == 'true') {
                    this.saveTransferDisabled = true;
                }
            }
        });
    });

    //Below condition is added to set the classificationIntent Map and preparing parameter for handleLogCodeVisibility method to set the visibility of HP log code.
    if (this.recordId && this.resultData.ctciModel.intentName) {
        this.ClassificationIntentMap = { "classification": this.resultData.ctciModel.classificationName, "intent": this.resultData.ctciModel.intentName };
    }

    this.caseForm = (caseModel.length > 0) ? caseModel : null;
}