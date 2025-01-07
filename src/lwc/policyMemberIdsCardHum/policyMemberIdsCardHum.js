/*******************************************************************************************************************************
LWC JS Name : policyMemberIdsCardHum.js
Function    : show MemberId card
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                      06/29/2020                    initial version
* Ritik Agarwal                      09/29/2021                    code optmization
*********************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';
import fetchMemberIdCardData from '@salesforce/apex/MemberIdsCard_LC_HUM.fetchMemberIdCardData';
import { getUserGroup, hcConstants, getLabels } from 'c/crmUtilityHum';

export default class CustomRecordFormHum extends LightningElement {
    @api recordId;
    @track labels = getLabels();
    @track sResponse;
    @track title = '';
    @track show = false;
    @track noData = false;

    connectedCallback() {
        const { bPharmacy, bProvider, bRcc, bGeneral, bGbo } = getUserGroup();
        if (bPharmacy || bProvider || bRcc || bGbo || bGeneral) {
            this.loadMemberIdCard(this.recordId);
            this.show = true;
        }
    }

    /**
     * loadMemberIdCard = param - {sRecordId}
     * Description - method for fetch Member Ids card data on load of page
     */
    async loadMemberIdCard(sRecordId) {
        try {
            let sResp = await fetchMemberIdCardData({ membpln: sRecordId, pageName: 'MemberPlan' });
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