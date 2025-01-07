/*
LWC Name        : rtiExistingCaseAttachPopoverHum
Function        : LWC Component for rti - case logging -  attach to existing case 

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
Jonathan Dickinson               07/14/2022                     REQ-3406802
Abhishek Mangutkar               10/12/2022                     US-3837985 RTI - attach to case upon resend  functionality
*****************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import CASE_NOTE from '@salesforce/label/c.Case_New_Existing_Note';

export default class RtiExistingCaseAttachPopoverHum extends LightningElement {
    
    @api recordId;
    attachCaseNote = CASE_NOTE;
    existingCaseId;
    existingCaseNumber;
    isFinishDisabled = true;
	@api calledFrom;
    showVerifyResend = false;

    connectedCallback() {
        this.showVerifyResend = this.calledFrom ? (this.calledFrom == 'PrintAttachCase' ? true : false) : false;
    }

    handleToggle(event) {        
        if (event.target.checked) {
            this.handleExistingCasePanelClose();
			event.target.checked = false;
        }
       
    }

    handleExistingCasePanelClose() {
        this.dispatchEvent(new CustomEvent('closeexistingcase'));
    }

    handleFinish() {
        this.dispatchEvent(new CustomEvent('attachexisting', {        
            detail: {
                caseId:  this.existingCaseId,
            }
        }));
        this.handleExistingCasePanelClose();
    }

    handleCaseCheckBoxSelect(event) {
        if (event && event?.detail) {
            let isChecked = event.detail.checked;
            if(isChecked){
                this.existingCaseId = event.detail.selectedCaseId;
                this.existingCaseNumber = event.detail.selectedCaseNumber;
                this.isFinishDisabled = false;
            }
            else{
                this.existingCaseId = null;
                this.existingCaseNumber = null;
                this.isFinishDisabled = true;
            }
        }
    }
}