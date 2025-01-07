/******************************************************************************************************************************
LWC Name        : archivedCaseRecordInfoFormHum.js
Function        : LWC to display Archived case detail

Modification Log:
* Developer Name                                Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar/Kajal Namdev                     07/18/2022                    Original Version 
******************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { getCaseFormLayout } from './layoutConfig';
import crmserviceHelper from 'c/crmserviceHelper';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
export default class archivedCaseRecordInfoFormHum extends crmserviceHelper {
    @api isClassic;
    @api customLabels;
    @track caseModel = [];
    @api caseData;
    @track result;
    @track activeSections = [];

    constructor() {
        super();
        // loading css
        loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css');
    }
    connectedCallback() {
        try {
            this.caseModel = getCaseFormLayout();
            if (this.caseData) {
                let caseResponse = this.caseData;
                //below for each is to fill the values in caseModel JSON
                this.caseModel.forEach((section) => {
                    this.activeSections.push(section.title);
                    section.fields.forEach((field) => {
                        if (field.isLink) { // check if field is link type
                            field.recordId = caseResponse[field.mapping];
                            field.value = caseResponse[field.mappingName];
                        } else if (field.showDate) {
                            field.dateValue = caseResponse[field.dateMapping];
                        } else if (field.checkbox) {
                            field.value = caseResponse[field.mapping] == "true" ? true : false;
                        } else {
                            field.value = caseResponse[field.mapping];
                        }
                     
                        // belwo if/else is added to show policy member in classic and member plan in lightning
                        if (this.isClassic && field.mapping === this.customLabels.HUMArchival_Member_Plan_Id) { //'sMember_Plan_Id'
                            field.input = false;
                        } else if (!this.isClassic && field.mapping === this.customLabels.HUMArchival_sPolicy_Member) {//'sPolicy_Member'
                            field.input = false;
                        }
                    });

                });
            }
        } catch (error) {
            console.log('error == ', JSON.stringify(error));
        }
    }

    /**
    * Description - open a new sub tab on click of link
    * @param {*} event - current element 
    */
    onLinkClick(event) {
        const action = event.currentTarget.getAttribute('data-action');
        const openNewTab = event.currentTarget.getAttribute('open-tab');
        //below if/else is added to check if the component is in classic or lightning to open sub tab.
        if (!this.isClassic) {
            if (openNewTab) {
                window.open(action, "_blank");
            } else {
                this.navigateToViewAccountDetail(action, 'MemberPlan', 'view');
            }
        } else {
            if (openNewTab) {
                this.dispatchEvent(new CustomEvent('dcnLinkevent', { detail: action, bubbles: true, composed: true }));
            } else {
                this.dispatchEvent(new CustomEvent('openRecordDetailsPage', { detail: action, bubbles: true, composed: true }));
            }
        }

    }

}