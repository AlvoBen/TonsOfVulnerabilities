/*******************************************************************************************************************************
LWC JS Name : accountDetailQuickFindHum.js
Function    : This JS serves as controller to accountDetailQuickFindHum.html. 

Modification Log: 
Developer Name                          Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                        12/18/2020                    initial version
* Ritik Agarwal                       04/05/2021                    Applied check for undefined values 
*********************************************************************************************************************************/
import { LightningElement, api, wire, track } from 'lwc';
import { getLinks } from './linkConfig';
import { getLabels } from 'c/crmUtilityHum';

export default class AccountDetailQuickFindHum extends LightningElement {
    @track oLinks = [];
    @track bLinksLoaded = false;
    @track oError;
    @track bIsViewAllEligible = false;
    @track labels = getLabels();

    @api oUserGroup;
    @api sRecordTypeName;
    oTimeoutTracker;
    oMasterLinks;

    connectedCallback() {
        let arrayLinks = getLinks(this.sRecordTypeName, this.oUserGroup);
        if(arrayLinks){
        if (arrayLinks.length > 10) this.bIsViewAllEligible = true;
        if (arrayLinks.length > 0) this.bLinksLoaded = true;
        this.oLinks = this.oMasterLinks = arrayLinks;
        this.oLinks = this.oLinks.slice(0, 10);
        this.oError = undefined;
        }
    }

    onKeyDownHandler(event) {
        clearTimeout(this.oTimeoutTracker);
        this.oTimeoutTracker = setTimeout(this.filterArray.bind(this, event.target), 500);
    }

    filterArray(target) {
        let oFilterArray;
        if (target.value && target.value != '') {
            oFilterArray = this.oMasterLinks.filter((arrEl) => {
                return arrEl.toLowerCase().indexOf((target.value).toLowerCase()) >= 0;
            });
        } else {
            oFilterArray = this.oMasterLinks;
        }
        this.bIsViewAllEligible = oFilterArray.length > 10;
        this.oLinks = oFilterArray.slice(0, 10);
    }

    handleBtnClick(event) {
        event.preventDefault();
        this.bIsViewAllEligible = false;
        this.oLinks = this.oMasterLinks;
    }
}