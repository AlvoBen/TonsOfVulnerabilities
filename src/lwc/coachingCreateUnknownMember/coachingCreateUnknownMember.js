/*******************************************************************************************************************************
LWC JS Name : coachingCreateUnknownMember.js
Function    : This LWC component is used to Launch the Unknown Member Creation screen for Humana Wellness Coaching App.

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                   06/04/2021                 Initial version created for USER STORY 2081412.
*********************************************************************************************************************************/
import { LightningElement, api } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';

const BUTTON_CLASS_BRAND = 'slds-button slds-button_brand';
const BUTTON_CLASS_NEUTRAL = 'slds-button slds-button_neutral';

export default class CoachingCreateUnknownMember extends NavigationMixin(LightningElement) {
    //public property to control the button display style
    @api isButtonBrand;
    
    //getter method to set the button style class
    get buttonClass() {        
        return this.isButtonBrand ? BUTTON_CLASS_BRAND : BUTTON_CLASS_NEUTRAL;
    }

    /**
     * Handle the create Unknown Member Button Click event
     * @param {*} event     
     */
    handleCreateUnkownMember(event) {        
        // Navigation to lightning Aura component
        this[NavigationMixin.Navigate]({
            "type": "standard__component",
            "attributes": {                
                "componentName": "c__CoachingUnknownMemWrap_LCMP_HUM"                
            }
        });
    }
}