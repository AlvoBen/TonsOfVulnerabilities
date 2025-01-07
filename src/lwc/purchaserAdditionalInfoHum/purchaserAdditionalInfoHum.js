/* 
Function   : Controller for purchaserAdditionalInfoHum.html

Modification Log:
* Developer Name                Date                       Description
* Supriya                       08/24/2021                 US: 2366119
*/
import { LightningElement, api, track } from 'lwc';
import getPurchaserPlan from '@salesforce/apex/PurchaserPlanDetail_LC_HUM.getPurchaserPlan';
import { getLabels } from "c/crmUtilityHum";

export default class PurchaserAdditionalInfoHum extends LightningElement {

    @api recordId;
    @track bDataLoaded;
    @track productTypeApi;
    @track labels = getLabels();
    
    connectedCallback() {
        try {
            getPurchaserPlan({ sRecId: this.recordId }).then((response) => {
                if (response) {
                    this.bDataLoaded = true;
                    response = { ...response[0] };
                    this.productTypeApi = (response.Product__r.Name === 'VIS') ? 'Product_Type__c' : 'Major_LOB__c';
                    this.template.querySelector('c-custom-record-form-hum').setFieldValues(response, this.getGrpLayout());
                }
            })
        } catch (err) {
            console.error("Error", err);
        }
    }

    getGrpLayout() {
        return [
            { label: 'Group Name', mapping: 'Name', value: '', wrapper: 'Payer' },
            { label: 'Group Number', mapping: 'Group_Number__c', value: '', wrapper: 'Payer' },
            { label: 'Product Type Code', mapping: this.productTypeApi, value: '', wrapper: '' },
            { label: 'Issue State', mapping: 'Issue_State__c', value: '', wrapper: '' },
            { label: 'Exchange Indicator', mapping: 'Exchange_Indicator__c', value: '', wrapper: '', checkbox: 'true' },
            { label: 'Exchange', mapping: 'Exchange__c', value: '', wrapper: '' },
            { label: 'Exchange Type', mapping: 'Exchange_Type__c', value: '', wrapper: '' },
            { label: 'Metallic Tier', mapping: 'Metallic_Tier__c', value: '', wrapper: '' }
        ];
    }
}