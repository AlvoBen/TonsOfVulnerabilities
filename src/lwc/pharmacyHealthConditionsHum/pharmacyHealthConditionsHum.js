/*
Function        : LWC to display health condition details .

Modification Log:
* Developer Name                  Date                         Description
****************************************************************************************************************************
* Atul Patil                    						 08/21/2023                User Story US4889468
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';

export default class PharmacyHealthConditionsHum extends LightningElement {
    @track healthconditions = '';
    @api profileDetails;
    connectedCallback() {
        this.setHealthConditions();
    }

    @api setProfileDetails(data) {
        this.profileDetails = data;
        this.setHealthConditions();
    }

    setHealthConditions() {
        if (this.profileDetails && this.profileDetails?.HealthConditions && this.profileDetails?.HealthConditions?.healthConditions
            && Array.isArray(this.profileDetails?.HealthConditions?.healthConditions) && this.profileDetails?.HealthConditions?.healthConditions?.length > 0) {
            let activeHealthCondtions = this.profileDetails?.HealthConditions?.healthConditions?.filter(k => k?.status?.code === 'A');
            if (activeHealthCondtions && Array.isArray(activeHealthCondtions) && activeHealthCondtions?.length > 0) {
                if (activeHealthCondtions?.length === 1) {
                    if (activeHealthCondtions[0]?.description?.toUpperCase() == 'NO KNOWN DRUG ALLERGY') {
                        this.healthconditions = 'No Known Drug Allergy';
                        this.healthConditionNoDataMessage();
                    } else {
                        activeHealthCondtions.forEach(element => {
                            this.healthconditions += `${element?.description ?? ''}\n`;
                        });
                    }
                } else {
                    activeHealthCondtions.forEach(element => {
                        this.healthconditions += `${element?.description ?? ''}\n`;
                    });
                }
            }
        }
    }

    healthConditionNoDataMessage() {
        if (null != this.template.querySelector('.healthconditions'))
            this.template.querySelector('.healthconditions').style.color = 'red';
    }
}