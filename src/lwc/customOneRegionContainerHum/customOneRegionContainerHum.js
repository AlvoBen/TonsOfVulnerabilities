/* 
Function   : Controller for customOneRegionContainerHum.html

Modification Log:
* Developer Name                Date                       Description
* Mohan Kumar N                 07/08/2021                 US: 2364782- Dual eligibity screen
*/

import { LightningElement, api, track } from 'lwc';
import { hcConstants } from 'c/crmUtilityHum';

export default class CustomOneRegionContainerHum extends LightningElement {
    @api config;
    @track bShowDualEligibity;
    @track bShowOtherInsurance;
    connectedCallback(){
        const me = this;
        const { type } = me.config;
        const { DUAL_STATUS, OTHER_INSURANCE } = hcConstants;
        // Render sctions based ob the type parameter
        switch(type){
            case DUAL_STATUS: 
                me.bShowDualEligibity = true;
                break;
            case OTHER_INSURANCE:
                me.bShowOtherInsurance = true;
                break;
            default:
        }
    }
}