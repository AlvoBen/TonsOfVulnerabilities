/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                  user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
*****************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';

export default class PharmacyHpiePrescriptionDetailsHum extends LightningElement {

    @api prckey;
    @api icon;
    @api prescription;
    @track addOrderEnable;
    @track detail;
    @track drugName;
    @track isEnable;
    @track autoRefillEnrolled;
    @api isMemberConsentRequired = false;

    @api setPrescriptionDetails(data) {
        this.prescription = data;
        this.addOrderEnable = !this.prescription?.orderEligible ?? false;
        this.autoRefillEnrolled = this.prescription?.autoRefillEnrolled === true
            || this.prescription?.autoRefillEnrolled?.toString()?.toLowerCase() === 'yes' ? 'Yes' : 'No';
    }

    @api setPrescriptionAutoRefillDetails(data) {
        if (this.prescription?.hasOwnProperty('autoRefillEnrolled')) {
            this.autoRefillEnrolled = data && data === true ? 'Yes' : 'No';
        }
    }

    connectedCallback() {
        this.addOrderEnable = !this.prescription?.orderEligible ?? false;
        this.autoRefillEnrolled = this.prescription?.autoRefillEnrolled === true ? 'Yes' : 'No';
    }

    handleCloseClick(evt) {
        const event = new CustomEvent('cardclose', {
            detail: { selectedKey: this.prckey }
        });
        this.dispatchEvent(event);
    }

    handleAddOrderClick() {
        this.addtocart = true;
        const addOrderEvent = new CustomEvent('addorder', {
            detail: {
                prescriptioncolor: this.icon.icontype,
                prescriptionKey: this.prescription.key,
                addedprescription: this.prescription
            }
        });
        this.dispatchEvent(addOrderEvent);
        this.popOver = false;
    }
}