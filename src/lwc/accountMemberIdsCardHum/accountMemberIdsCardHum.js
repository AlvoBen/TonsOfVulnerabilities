/*******************************************************************************************************************************
LWC JS Name : accountMemberIdsCardHum.js
Function    : show MemberId card
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                  10/07/2021                    initial version
*********************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';
import { getUserGroup, hcConstants, getLabels } from 'c/crmUtilityHum';
import fetchAccountMemberIdData from '@salesforce/apex/AccountMemberIds_LC_HUM.fetchAccountMemberIdData';

export default class CustomRecordFormHum extends LightningElement {
    @api recordId;
    @track labels = getLabels();
    @track sResponse;
    @track title = '';
    @track show = false;
    @track noData = false;
    @api hideAccordian = false;
    
    connectedCallback() {
        const { bPharmacy, bProvider, bRcc, bGeneral, bGbo } = getUserGroup();
        if (bPharmacy || bProvider || bRcc || bGbo || bGeneral) {
            this.loadAccountMemberIdCard(this.recordId);
            this.show = true;
        }
    }

    /**
     * loadMemberIdCard = param - {sRecordId}
     * Description - method for fetch Member Ids card data on load of page
     */
    async loadAccountMemberIdCard(sRecordId) {
        try {
            let sResp = await fetchAccountMemberIdData({acctid: sRecordId});
            if (sResp) {
                this.sResponse = sResp;
            }
            this.title = hcConstants.MEMBER_ID;
            this.noData = this.sResponse ? false : true;
        } catch (error) {
            console.log('error in MemberId card record form', error);
        }
    }
}