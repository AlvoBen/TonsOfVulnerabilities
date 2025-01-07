/*******************************************************************************************************************************
LWC JS Name : mtvMemberRemarkHum.js
Function    : This JS serves as controller to mtvMemberRemarkHum.html. 
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya Shastri                                         07/02/2021                Original Version 
* Vardhman Jain                                           09/02/2022                US: 3043287 Member Plan Logging stories Changes.
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import initiateMTVRequest from '@salesforce/apexContinuation/MTVRemarksMember_LC_HUM.initiateMTVRequest';
import { getLabels, getUserGroup } from "c/crmUtilityHum";

export default class MtvMemberRemarkHum extends LightningElement {
    @api recordId;
    @api nameOfScreen;
    @api bInfiniteScroll;
    @track isGbo;
    @track oIntLst;
    @track isDataLoaded = false;
    @track labels = getLabels();
	@track loggingRelatedField = [{
        label : "MTV Member Remark",
        mappingField : "sIdentifierId"
    }];
    connectedCallback() {
        const { bGbo } = getUserGroup();
        this.isGbo = bGbo ? true : false;
        const { recordId } = this;
        initiateMTVRequest({ memberPlanId: recordId }).then(res => {
            let oArray = [];
            if (res.isOnSwitch) {
                oArray = [...res.mtvMemberPremarkList];
                oArray.sort((a, b) => {
                    let da = new Date(a.sCreatedDate), db = new Date(b.sCreatedDate);
                    return db - da;
                });
                this.oIntLst = oArray;
                this.isDataLoaded = true;
            }
        }).catch(error => {
            if (error) {
                console.log('error', error);
            }
        });

    }
}