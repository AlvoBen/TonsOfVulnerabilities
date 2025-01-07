/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                  user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
*****************************************************************************************************************************/
import { LightningElement, wire, api, track } from 'lwc';

export default class PharmacyUpdateCapTypeHum extends LightningElement {
    @api capTypeFlag;

    get capType() {
        return `You are about to change the Cap Type to <b>${this.capTypeFlag ? 'E - Easy' : 'S - Safety'}</b>. <br>Are you sure?`;
    }

    closeModal() {
        this.dispatchEvent(new CustomEvent('closemodal'));
    }

    updateCapTypeValue() {
        const capTypeEvent = new CustomEvent('updatecaptype', {
            detail: {
                capType: this.capTypeFlag ? 'E' : 'S'
            }
        });
        this.dispatchEvent(capTypeEvent);
    }
}