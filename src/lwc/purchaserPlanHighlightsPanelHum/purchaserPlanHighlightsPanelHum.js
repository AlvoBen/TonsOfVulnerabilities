import { LightningElement, track, api } from 'lwc';
import getPurchaserPlanForGroup from '@salesforce/apex/GroupSearchPolicies_LC_HUM.getPurchaserPlanForGroup';
import { getUserGroup } from 'c/crmUtilityHum';
import { getModal } from './formFieldsModel';

export default class PurchaserPlanHighlightsPanelHum extends LightningElement {
    @track isNotPurhcaserPlan;
    @api recordId;
    @track header = {};
    @track groupAccountName = '';
    @track showPlanPanel=false;

    connectedCallback() {
        this.isNotPurhcaserPlan = true;
        const { bRcc, bProvider, bGbo, bPharmacy, bGeneral } = getUserGroup();
        if (bPharmacy || bRcc || bGbo || bGeneral || bProvider) {
            this.showPlanPanel = true;
            this.fetchPlanDetails(this.recordId);
        }
       
    }


    async fetchPlanDetails(sRecordId) {
        try {
                const result = await getPurchaserPlanForGroup({ sPlanId: sRecordId });
                if (result) {
                this.groupAccountName = result.accountName;
                this.loadPanel(result);
            }
        } catch (error) {
            console.log('error in purchaserplan comp of fetchPlanDetails--',error);
        }
    }

    loadPanel(sResult) {
        const modal = getModal();
        this.header = modal.header;
        this.template.querySelector('c-policy-highlights-panels-hum').processData(sResult, modal);
    }
}