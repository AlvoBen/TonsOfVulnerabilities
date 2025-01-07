/*
JS Controller        : pharmacyInactivateRxRequestSummaryHum
Version              : 1.0
Created On           : 12/15/2023
Function             : Component to display to pharmacy inactivate rx request flow.

Modification Log: 
* Developer Name                      Date                         Description
* Isaac Chung                        12/13/2023                   Original Version
*------------------------------------------------------------------------------------------------------------------------------
*/

import { LightningElement, track, api } from 'lwc';

export default class PharmacyInactivateRxRequestSummaryHum extends LightningElement {
    @api flowParams

    @track isToggleChecked = false;

    get prescriptions() {
        return JSON.parse(this.flowParams)
    }

    get isFinishDisabled() {
        return !this.isToggleChecked
    }

    handleCancel() {
        this.dispatchEvent(new CustomEvent('back'))
    }

    handleToggleChange(event) {
        this.isToggleChecked = event.target.checked;
    }

    handleFinish() {
        this.dispatchEvent(new CustomEvent('finish'))
    }

}