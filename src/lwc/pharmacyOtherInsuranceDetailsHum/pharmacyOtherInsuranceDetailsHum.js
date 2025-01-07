/*******************************************************************************************************************************
LWC JS Name : PharmacyOtherInsuranceDetailsHum.js
Function    : This LWC component used to render Other Insurance detail on new subtabs.

Modification Log: 
Developer Name                             Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Swapnali Sonawane                       09/02/2021                  UserStory:2508657 HP- Ability to add LIS and Other Insurance Details to the Plan Member card
*********************************************************************************************************************************/
import { LightningElement,api,track} from 'lwc';
import getOI from '@salesforce/apexContinuation/Pharmacy_LC_HUM.getOIData';
import { otherinsuranceEM, otherinsuranceLV } from './pharmacyOtherInsuranceModel';
import { getLabels } from "c/crmUtilityHum";
export default class PharmacyOtherInsuranceDetailsHum extends LightningElement
 {

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
            
            getOI({ sMemberPlanId: config.recordId,sEnterpriceId: config.enterpriceId}).then(res => {
                
                let oArray = [];
                if (res.validOIWrapperList) {
                    oArray = [...res.validOIWrapperList];
                    console.log("Arry",oArray);
                    if (oArray.length>0)
                    {
                        oArray.sort((a, b) => {
                            let da = new Date(a.sEndDate), db = new Date(b.sCreatedDate);
                            return db - da;
                        });
                    } 
                }
                this.count = oArray.length;
                this.oIntLst = oArray;
                this.isDataLoaded = true;
            })
        } catch (err) {
            console.error("Error", err);
        
    }}
}