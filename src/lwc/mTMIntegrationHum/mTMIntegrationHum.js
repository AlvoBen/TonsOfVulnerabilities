/*
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane               09/01/2023                  US: 5012557 Pharmacy - MTM Eligibility
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getMtmIndicator from '@salesforce/apexContinuation/MTM_Integration_LC_HUM.getMtmIndicator';
import { MTMRequest } from './mtmRequest'
export default class MTMIntegrationHum extends LightningElement {}
export function getMtmData(mebGenKey) {   
    let request = new MTMRequest(mebGenKey);    
    return new Promise((resolve, reject) => {
        getMtmIndicator({ mTMrequest: JSON.stringify(request) })
            .then(result => {
                resolve(JSON.parse(result));
            }).catch(error => {
                reject(error);
            })
    });
}