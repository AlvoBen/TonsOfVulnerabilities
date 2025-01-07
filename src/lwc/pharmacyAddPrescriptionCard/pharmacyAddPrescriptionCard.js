/*******************************************************************************************************************************
LWC JS Name : pharmacyAddPresciptionCard.js
Function    : This JS serves as controller to pharmacyAddPresciptionCard.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                                         11/16/2021                initial version US-2762056
*********************************************************************************************************************************/

import { LightningElement , api, track} from 'lwc';

export default class PharmacyAddPrescriptionCard extends LightningElement {
    @api item;
    @api icon;
    @api key;    
    @api checked;
    @api orderEligible;
    @api addedToCart;
    
	get iconContainer() {
        if (this.checked) {
            return 'slds-m-around_x-small slds-checkbox-button slds-checkbox-button_is-checked';
        } else {
            return 'slds-m-around_x-small slds-checkbox-button';
        }
    }

    get SelectIcon() {
        if (this.checked) {
            return "utility:check";
        } else {
            return "utility:add";
        }
    }
	
    handleChange(event)
    {
        if (this.checked == false) {
            this.checked = true;          
        }
        else {
            this.checked = false;                  
        }
        let eveDetail = {selectedPrescription: this.item, isSelected : this.Checked , prescriptionnumber : this.item.RXNumber };
        const selectedEvent = new CustomEvent("prescriptionselect", {
            detail: eveDetail
        });

        // Dispatches the event.
        this.dispatchEvent(selectedEvent);
            
    }    
}