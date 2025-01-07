/*
LWC Name        : pcpExistingPopover.html
Function        : LWC to display existing case data for pcp;

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     08/25/2022                   initial version - US - 3602368
* Jonathan Dickinson              06/14/2023                  User Story 4705843: T1PRJ0891339 2023 Arch Remediation-SF-Tech-Filter cases having template attached from existing case history logging for process logging
*****************************************************************************************************************************/
import { LightningElement,api,track } from 'lwc';
import CASE_NOTE from '@salesforce/label/c.Case_New_Existing_Note';
export default class PcpExistingCasePopover extends LightningElement {
    @api recordId;
    attachCaseNote = CASE_NOTE;
    existingCaseId;
    existingCaseNumber;
    isFinishDisabled = true;
    @api displayPrevious;
    @api allvalid=false;
    @track toggleValue = true;
    filterCasesHavingTemplate = true;
    handleToggle(event) {
        if (!event.target.checked) {
            this.handleExistingCasePanelClose();
        }

    }

    handleExistingCasePanelClose() {
        this.dispatchEvent(new CustomEvent('closeexistingcase'));
    }

    handleLogFinish() {
        this.dispatchEvent(new CustomEvent('attachexisting', {
            detail: {
                caseId:  this.existingCaseId,
            }
        }));
        
    }

    handlePrev(){
        this.dispatchEvent(new CustomEvent('prevclick'));
    }

    handleCaseCheckBoxSelect(event) {
        if (event && event?.detail) {
            let isChecked = event.detail.checked;
            if(isChecked){
                this.existingCaseId = event.detail.selectedCaseId;
                this.existingCaseNumber = event.detail.selectedCaseNumber;
                this.isFinishDisabled = !this.allvalid ? false : true;
            }
            else{
                this.existingCaseId = null;
                this.existingCaseNumber = null;
                this.isFinishDisabled = true;
            }
        }

    }
}