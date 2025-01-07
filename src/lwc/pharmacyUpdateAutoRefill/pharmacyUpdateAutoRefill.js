/*
Function        : LWC to update the auto refill for prescription.

Modification Log:
* Developer Name                  Date                         Description
****************************************************************************************************************************
* Nirmal Garg                    07/18/2022                 	 Initial version US#5071365
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { updatePrescriptionDetails } from 'c/pharmacyHPIEIntegrationHum';
import { toastMsge } from "c/crmUtilityHum";
export default class PharmacyUpdateAutoRefill extends LightningElement {
    @api autoFillFlag;
    @api enterpriseId;
    @api userId;
    @api organization;
    @api prescriptionKey;
    @api archived;

    handleCloseModal() {
        this.fireEventToParent('closeautofill', null);
    }

    updateAutoFill() {
        this.callService();
    }

    callService() {
        this.fireEventToParent('closeautofill', {
            displayLoader: true
        });
        updatePrescriptionDetails(this.prescriptionKey, this.enterpriseId, this.userId, this.organization ?? 'HUMANA', !this.autoFillFlag, this.archived)
            .then(result => {
                toastMsge('Success', `Auto refill Updated for ${this.prescriptionKey}`, 'success', 'dismissible');
                if (result && Object.keys(result)?.length > 0) {
                    this.fireEventToParent('updateautofill', {
                        rxNumber: this.prescriptionKey,
                        autoFillFlag: !this.autoFillFlag
                    });
                }
            }).catch(error => {
                toastMsge('Failed', `Failed to update auto refill for ${this.prescriptionKey}`, 'error', 'dismissible');
                console.log(error);
            })
    }

    fireEventToParent(eventName, data) {
        this.dispatchEvent(new CustomEvent(eventName, {
            detail: data
        }))
    }
}