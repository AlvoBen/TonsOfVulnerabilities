/*
Function        : LWC PharmacyHpieAddPrescriptionCardHum.js

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                  29/02/2024                     US - 5142800-Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions – Create Order
*****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';

export default class PharmacyHpieAddPrescriptionCardHum extends LightningElement {
    @api item;
    @api checked;

    connectedCallback(){
    }

    get iconContainer() {
        return this.item?.addedToCart ? 'slds-var-m-around_x-small slds-checkbox-button slds-checkbox-button_is-checked' : 'slds-var-m-around_x-small slds-checkbox-button';
    } 

    get selectIcon() {
         return this.checked ? 'utility:check' : 'utility:add'; 
        
    }

    handleChange(event) {
        if (this.checked == false) {
            this.checked = true;          
        }
        else {
            this.checked = false;                  
        }
        const selectedEvent = new CustomEvent("updateprescriptions", {
            detail: {
                key: this.item.key,
                check : this.checked
            }, bubbles: true, composed: true
        });
        this.dispatchEvent(selectedEvent);
    }
}
