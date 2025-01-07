/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                  user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
* Jonathan Dickinson             02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
*****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { getFormatDate, getCalculatedDate, toastMsge } from 'c/crmUtilityHum';
import { updateMemberConsentDetails } from 'c/pharmacyHPIEIntegrationHum';
import { addFamilyNote } from 'c/genericPharmacyLogNotesIntegrationHum';
const PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM = 'CONSENT';
const PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM = 'Member consent given on ';
const PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM = ' at ';
const PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERFIRSTHALF_HUM = 'Member consent removed on ';
const PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERSECONDHALF_HUM = ' est.';

export default class PharmacyMemberConsentHum extends LightningElement {
    @api enterpriseId;
    @api userId;
    @api organization;
    @api accData;
    @track _preferenceDetails;
    @track memberConsentStatus;
    @track memberConsentStatusFlag;
    @track displayMemberConsentPopup = false;
    @track loaded = false;

    @api 
    get preferenceDetails() {
        return this._preferenceDetails;
    }

    set preferenceDetails(value) {
        this._preferenceDetails = value;
        this.displayMemberConsent();
    }

    displayMemberConsent() {
        this.memberConsentStatusFlag = this.preferenceDetails?.preference?.consents?.memberConsentRequired === true
            && this.preferenceDetails?.preference?.consents?.consentBeginDate
            && this.preferenceDetails?.preference?.consents?.consentEndDate
            && new Date(this.preferenceDetails?.preference?.consents?.consentBeginDate).getTime() <= new Date().getTime()
            && new Date(this.preferenceDetails?.preference?.consents?.consentEndDate) >= new Date().getTime()
            ? true : false;
        this.memberConsentStatus = this.memberConsentStatusFlag
            ? `Approved ${getFormatDate(this.preferenceDetails?.preference?.consents?.consentBeginDate)}`
            : 'Request Consent for each order.';
        
        this.loaded = true;
    }

    connectedCallback() {
        this.displayMemberConsent();
    }

    handleCloseModal() {
        this.displayMemberConsentPopup = false;
    }

    toggleMemberConsent(event) {
        this.displayMemberConsentPopup = true;
    }

    handleUpdateMemberConsent(event) {
        this.loaded = false;
        let memberConsentFlag = event?.detail?.memberConsentFlag ?? null;
        this.displayMemberConsentPopup = false;
        if (memberConsentFlag) {
            this.updateConsentBeginDate();
        } else {
            this.updateConsentEndDate();
        }
    }

    updateConsentBeginDate() {
        let consentBeginDate = getFormatDate(new Date(), 'yyyy-mm-dd');

        updateMemberConsentDetails(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', consentBeginDate, true)
            .then(result => {
                this.callPreferenceService();
                toastMsge('Success', 'Member Consent Updated', 'success', 'dismissible');
                this.addLogNote(true);
            }).catch(error => {
                this.loaded = true;
                console.log(error);
                toastMsge('Failed', 'Failed to Update Member Consent', 'error', 'dismissible');
            })
    }

    updateConsentEndDate() {
        updateMemberConsentDetails(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', null, false)
            .then(result => {
                this.callPreferenceService();
                toastMsge('Success', 'Member Consent Updated', 'success', 'dismissible');
                this.addLogNote(false);
            }).catch(error => {
                this.loaded = true;
                console.log(error);
                toastMsge('Failed', 'Failed to Update Member Consent', 'error', 'dismissible');
            })
    }

    addLogNote(memberConsent) {
        let notes = [];
        let message = memberConsent ? `${PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM} ${getFormatDate(new Date())}${PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM}${new Date().toLocaleTimeString()}`
        : `${PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERFIRSTHALF_HUM} ${getFormatDate(new Date())}${PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM}${new Date().toLocaleTimeString()} ${PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERSECONDHALF_HUM}`;
        notes.push({
            noteCode:PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM,
            logNote: message
        });

        addFamilyNote(this.userId, this.enterpriseId, this.organization ?? 'HUMANA', this.accData, notes)
            .then(result => {
                this.handleLogNoteAdded();
            }).catch(error => {
                console.log(error);
            })
    }

    callPreferenceService() {
        this.dispatchEvent(new CustomEvent('callpreference'));
    }

    handleLogNoteAdded() {
        this.dispatchEvent(new CustomEvent('lognoteadded'));
    }
}