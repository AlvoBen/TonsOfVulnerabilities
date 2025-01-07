/*
Function        : LWC to display allergies details .

Modification Log:
* Developer Name                  Date                         Description
****************************************************************************************************************************
* Atul Patil                    						 08/21/2023                User Story US4889468
*****************************************************************************************************************************
*/
import { LightningElement, wire, api, track } from 'lwc';

export default class PharmacyAllergiesHum extends LightningElement {
    @track allergies = '';
    @api profileDetails;
    connectedCallback() {
        this.setAllergiesDetails();
    }

    @api setProfileDetails(data) {
        this.profileDetails = data;
        this.setAllergiesDetails();
       
    }

    @api setAllergiesDetails() {
        if (this.profileDetails && this.profileDetails?.HealthConditions && this.profileDetails?.HealthConditions?.allergies
            && Array.isArray(this.profileDetails?.HealthConditions?.allergies) && this.profileDetails?.HealthConditions?.allergies?.length > 0) {
            let activeAllergies = this.profileDetails?.HealthConditions?.allergies?.filter(k => k?.status?.code === 'A');
            if (activeAllergies && Array.isArray(activeAllergies) && activeAllergies?.length > 0) {
                if (activeAllergies?.length === 1) {
                    if (activeAllergies[0]?.description?.toUpperCase() == 'NO KNOWN DRUG ALLERGY') {
                        this.allergies = 'No Known Drug Allergy';
                        this.allergiesNoDataMessage();
                    } else {
                        activeAllergies.forEach(element => {
                            this.allergies += `${element?.description ?? ''}\n`;
                        });
                    }
                } else {
                    activeAllergies.forEach(element => {
                        this.allergies += `${element?.description ?? ''}\n`;
                    });
                }
            }
        }
    }

    allergiesNoDataMessage() {
        if (null != this.template.querySelector('.allergies'))
            this.template.querySelector('.allergies').style.color = 'red';
    }
}