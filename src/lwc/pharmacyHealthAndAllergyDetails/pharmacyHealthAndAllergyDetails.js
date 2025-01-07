import { api, LightningElement } from 'lwc';

export default class PharmacyHealthAndAllergyDetails extends LightningElement {
    
    pharmacyhealthAllergyDetails;
    healthconditions = '';
    allergies = '';


    displayHealthAlleryData(){
        const me = this;
        if (me.pharmacyhealthAllergyDetails != undefined) {
            if (me.pharmacyhealthAllergyDetails.lstHealthConditions != undefined
                && me.pharmacyhealthAllergyDetails.lstHealthConditions.length > 0) {
                    if (me.pharmacyhealthAllergyDetails.lstHealthConditions.length == 1) {
                        if (me.pharmacyhealthAllergyDetails.lstHealthConditions[0].toUpperCase() == 'NO KNOWN MEDICAL HISTORY') {
                            me.healthconditions = 'No Known Medical History';
                            me.healthConditionNoDataMessage();
                        }
                        else{
                            me.pharmacyhealthAllergyDetails.lstHealthConditions.forEach(element => {
                                me.healthconditions += element + '\n'
                            });
                        }
                    }
                    else{
                        me.pharmacyhealthAllergyDetails.lstHealthConditions.forEach(element => {
                            me.healthconditions += element + '\n'
                        });
                    }
            }
            if (me.pharmacyhealthAllergyDetails.lstAllergies != undefined
                && me.pharmacyhealthAllergyDetails.lstAllergies.length > 0) {
                    if (me.pharmacyhealthAllergyDetails.lstAllergies.length == 1) {
                        if (me.pharmacyhealthAllergyDetails.lstAllergies[0].toUpperCase() == 'NO KNOWN DRUG ALLERGY') {
                            me.allergies = 'No Known Drug Allergy';
                            me.allergiesNoDataMessage();
                        }
                        else{
                            me.pharmacyhealthAllergyDetails.lstAllergies.forEach(element => {
                                me.allergies += element + '\n'
                            });
                        }
                    }
                else{
                    me.pharmacyhealthAllergyDetails.lstAllergies.forEach(element => {
                        me.allergies += element + '\n'
                    });
                }
            }
        }
    }
    allergiesNoDataMessage() {
        const me = this;
        if (null != me.template.querySelector('.allergies'))
            me.template.querySelector('.allergies').style.color = 'red';
    }
    healthConditionNoDataMessage() {
        const me = this;
        if (null != me.template.querySelector('.healthconditions'))
            me.template.querySelector('.healthconditions').style.color = 'red';
    }

    @api 
    pharmacydata(value)
    {
        const me= this;
        me.pharmacyhealthAllergyDetails = value;
        me.displayHealthAlleryData();
    }
}