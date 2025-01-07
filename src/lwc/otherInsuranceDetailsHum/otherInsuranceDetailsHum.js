/*******************************************************************************************************************************
LWC JS Name : otherInsuranceDetailsHum.js
Function    : This JS serves as controller to otherInsuranceDetailsHum.html. 
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya Shastri                                         08/03/2021                  US: 2149972
* Supriya Shastri                                         08/19/2021                  PolicyPlatform based rendering
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import startIDS from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.getOIData';
import { otherinsuranceEM, otherinsuranceLV } from './otherInsuranceModel';
import { getLabels } from "c/crmUtilityHum";

export default class OtherInsuranceDetailsHum extends LightningElement {
    @api config;
    @api nameOfScreen;
    @api bInfiniteScroll;
    @track oIntLst;
    @track isDataLoaded = false;
    @track otherinsuranceLayout;
    @track labels = getLabels();
    count;

    connectedCallback() {
        const { config } = this;
        this.otherinsuranceLayout = (config.platformType === 'EM') ? otherinsuranceEM : otherinsuranceLV;
        try {
            startIDS({ sRecId: config.recordId }).then(res => {
                let oArray = [];
                if (res.isOnSwitch) {
                    oArray = [...res.validOIWrapperList];
                    oArray.sort((a, b) => {
                        let da = new Date(a.sEndDate), db = new Date(b.sCreatedDate);
                        return db - da;
                    });
                    this.count = oArray.length;
                    this.oIntLst = oArray;
                    this.isDataLoaded = true;
                }
            })
        } catch (err) {
            console.error("Error", err);
        }
    }
}