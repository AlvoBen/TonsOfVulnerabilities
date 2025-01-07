/******************************************************************************************************************************
LWC Name        : ArchivedCaseDetailViewHum.js
Function        : LWC to show case information

Modification Log:
* Developer Name                                Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev /Ashish                    07/18/2022                    Original Version 
******************************************************************************************************************************/

import { LightningElement,track, api } from 'lwc';
import { getAccountDetailsLightningLayout, getAccountDetailsClassicLayout} from "./layoutConfig";
import crmserviceHelper from 'c/crmserviceHelper';

export default class ArchivedCaseDetailViewHum extends crmserviceHelper {
@track accDetailModel;
@api isClassic;
@api caseData;
@api recordId;
@api customLabels;
    connectedCallback(){
        this.accDetailModel = this.isClassic ? getAccountDetailsClassicLayout() : getAccountDetailsLightningLayout();

        this.accDetailModel.forEach((section) => {
            section.fields.forEach((field) =>{
                if (field.isLink) { // check if field is link type
                    field.recordId = this.caseData[field.mapping];
                    field.value = this.caseData[field.mappingName];
                } else {
                    field.value = this.caseData[field.mapping];
                }
            });
            
        });
    }

    /**
    * Description - open a new sub tab on click of link
    * @param {*} event - current element 
    */
   onLinkClick(event) {
    const action = event.currentTarget.getAttribute('data-action');
    const openNewTab = event.currentTarget.getAttribute('open-tab');

    //this.isClassic = true;
    console.log('data action ==> ',action, ' is classic==> ',this.isClassic);
    //below if/else is added to check if the component is in classic or lightning to open sub tab.
    if(!this.isClassic){
        if(openNewTab){
            window.open(action, "_blank");
        }else{
            this.navigateToViewAccountDetail(action, 'MemberPlan', 'view');
        }
    }else{
        if(openNewTab){
            this.dispatchEvent(new CustomEvent('dcnLinkevent' , { detail : action ,  bubbles: true, composed: true }));
        }else{
            this.dispatchEvent(new CustomEvent('openRecordDetailsPage' , { detail : action ,  bubbles: true, composed: true }));
        }

    }
}
}