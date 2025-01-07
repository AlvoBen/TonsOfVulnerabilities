/*******************************************************************************************************************************
LWC JS Name : caseAddClaimNumbersHum.js
Function    : This JS serves as helper to caseAddClaimNumbersHum.html

Modification Log:
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Sagar.G     		                                02/20/2023				US-4177643 Add claims Buttons EDIT CASE  
*********************************************************************************************************************************/

import {track, LightningElement,api } from 'lwc';
import LightningModal from 'lightning/modal';
import { getFieldLayout } from './layout';
import UnsavedChangesHeader from '@salesforce/label/c.UnsavedChangesHeader';
import UnsavedModalMsgHum from '@salesforce/label/c.unsavedModalMsgHum';

export default class CaseAddClaimNumbersHum extends LightningElement {
    @api modalStatus=false;
@track fieldLayoutData;
warningMsgbuttonsConfig = [{
    label: "Cancel",
    class: "slds-var-m-right_small",
    eventname: "cancel",
    variant: "brand-outline"
}, {
    label: "Continue",
    class: "slds-var-m-right_small",
    eventname: "continue",
    variant: "brand-outline"
}];
promptclass = "slds-modal__header slds-theme_error slds-theme_alert-texture";
showCloseMessage=false;
labels = {
    UnsavedChangesHeader,
    UnsavedModalMsgHum
}


connectedCallback() {
this.fieldLayoutData=getFieldLayout();

}
  handleSave(){
    var data = this.fieldLayoutData;
    var claimArr=[];
    for(var key in data){
        if(data[key].value!=''){
            claimArr.push(data[key].value);
        }
    }
    const selectedEvent = new CustomEvent("handlesave", {
        detail: claimArr
      });
    this.dispatchEvent(selectedEvent);
  }
  handleCancel(){
    this.showCloseMessage = true;
   
  }
  handleModal(){
    this.modalStatus=true;
  }
  handleInputChange(event) {
    var textValue = event.detail.value;
    var fieldIndex =event.currentTarget.dataset.id;
    this.fieldLayoutData[fieldIndex].value=textValue;
}
closeClosePopupModal(){
    this.showCloseMessage = false;

}
handleCloseClosePopupModal(){
    this.showCloseMessage = false;
    this.modalStatus=false;
    this.fieldLayoutData=getFieldLayout();
    const selectedEvent = new CustomEvent("handleclosepopup", {
        detail: false
      });
    this.dispatchEvent(selectedEvent);
}
}