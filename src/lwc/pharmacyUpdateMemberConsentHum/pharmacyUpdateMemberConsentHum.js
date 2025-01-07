/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                  user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
* Jonathan Dickinson             02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
*****************************************************************************************************************************/
import { LightningElement, api } from 'lwc';
import pharmacyApproveConsentMessage from '@salesforce/label/c.pharmacyApproveConsentMessage';
import pharmacyRemoveConsentMessage from '@salesforce/label/c.pharmacyRemoveConsentMessage';

export default class PharmacyUpdateMemberConsentHum extends LightningElement {
    @api memberConsentFlag
    get question() {
        return this.memberConsentFlag ? pharmacyRemoveConsentMessage : pharmacyApproveConsentMessage;
    }

    closeModal() {
        this.dispatchEvent(new CustomEvent('closemodal'));
    }

    updateMemberConsent() {
        this.dispatchEvent(new CustomEvent('updatememberconsent', {
            detail: {
                memberConsentFlag: !this.memberConsentFlag
            }
        }));
    }
}