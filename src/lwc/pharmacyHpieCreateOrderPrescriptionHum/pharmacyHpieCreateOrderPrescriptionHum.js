/*
Function        : LWC PharmacyHpieCreateOrderPrescriptionHum.js 

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Vishal Shinde                  01/03/2024                     US - 5142800 -Createorder
*****************************************************************************************************************************/
import { LightningElement, api } from 'lwc';

export default class PharmacyHpieCreateOrderPrescriptionHum extends LightningElement {
    @api item;

    get checkrefilllength() { 
        return this.item?.refillsRemaining != null
            && this.item?.refillsRemaining?.toString()?.length >= 2
            ? true : false;
    }

    removePrescription() {
        let detail = {
            key: this.item.key 
        }
        this.dispatchEvent(new CustomEvent('removepres', {
            detail: detail,
            bubbles: true,
            composed: true
        }))
    }
}