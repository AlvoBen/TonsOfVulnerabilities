/******************************************************************************************************************************
LWC Name        : CoachingCompoundTableHum.js
Function        : LWC to display print compound structure of the re-usable table cell

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari               03/09/2021                    Original Version 
******************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import crmserviceHelper from 'c/coachServiceHelper';
import { getLocaleDate, getLabels } from 'c/coachUtilityHum';
export default class CoachingCompoundTableHum extends NavigationMixin(crmserviceHelper) {

    @api compoundvalue;
    @api compoundvaluex;
    @api iconval;
    @api iconproperty;
    @track dataList;
    @track Idval;
    @track labels = getLabels();
    @track linkwithtooltip = true;
    @api nameofscreen;
    /*Ritik created this api variable for send the record to backend for provider/agency search
       on click of record */
    @api jsonRecordData;

    connectedCallback() {
        if (this.compoundvaluex) {
            let idval;
            let compoundStructure = JSON.parse(JSON.stringify(this.compoundvaluex));
            compoundStructure.forEach(function (item) {
                if (item.icon) {
                    if (item.value === 'Active') {
                        item.cssClass = 'status-active';
                    } else if (item.value === 'Termed') {
                        item.cssClass = 'status-termed';
                    } else {
                        item.cssClass = 'status-future';
                    }
                }
                if (item.Id) {
                    idval = item.value;
                }
            });
            this.Idval = idval;
            this.dataList = compoundStructure;
        }
    }

    hyperlinkclick(event) {
        if (this.Idval) {
            this.navigateToViewAccountDetail(this.Idval, 'Account', 'view');
        }
    }

    /* this method will run when upsert of provider/agency already done and 
        now we have an id
     */
    @api
    navigateToDetailPage(recordId) {
        this.Idval = recordId;
    }
}