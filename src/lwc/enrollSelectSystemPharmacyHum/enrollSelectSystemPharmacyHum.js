/* 
LWC Name        : enrollSelectSystemPharmacyHum
Function        : Enrollment Search 

*Modification Log:
* Developer Name                  Date                         Description
*
* RajKishore                      11/21/2020                Original Version 
* */
import { LightningElement, track } from 'lwc';
import {hcConstants} from "c/crmUtilityHum";
export default class EnrollSelectSystemPharmacyHum extends LightningElement {

    inputType = 'Radio';
    screenType = 'Enrollment';
    returnarry = [hcConstants.TES,hcConstants.CBIS,hcConstants.CIM,hcConstants.MARKETSEARCH,hcConstants.AUTOENROLL,hcConstants.TRR,hcConstants.APPSEARCH];
    value;
    @track selectedValue;
    
    get options() {
        return this.returnarry.map(arr => ({ label: arr, value: arr }));
    }
    handleSelected(event) {
        const systemselect = new CustomEvent('systemselect', {
            detail: event.target.value // this.selectedValue
        }); this.dispatchEvent(systemselect);
    }

}