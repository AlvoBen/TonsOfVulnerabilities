/*******************************************************************************************************************************
LWC Name    : previouslyLoggedCmpHum.js
Function    : this checks for previously logged items and display a notification

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ceasar Sabarre                                          02/20/2023                   US#4268275 T1PRJ0865978 - MF 23891-   Lightning- Design& Implementation- "Previously Logged" items message on the Non pharmacy views
* Ceasar sabarre                                              03/29/2023                DF7452 Fix
*********************************************************************************************************************************/ 

import getAttachmentLogs from '@salesforce/apex/Logging_LC_HUM.getAttachmentLogs';
import getCaseComments from '@salesforce/apex/Logging_LC_HUM.getCaseComments';
import { LightningElement, api, track } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';


    

export default class PreviouslyLoggedCmpHum extends NavigationMixin(LightningElement) {
    @track attachmentLogs;
    @track isExisting = false;
    @track cases = new Set();
    @track relatedFieldValues = new Map();
    @track customMessage;
    @api sMemberId;
    @api sType; // matches the attachment type
    @api sUniqueVal;
    @api sOrderNumber;
	@api subtype;
	@api customtypename;

    connectedCallback()
    {
        if(this.sMemberId){
			if(this.customtypename) {
                this.checkExistingLog(); // non Pharmacy views require sType
                this.customMessage = `${this.customtypename} was previously logged to the following case(s): `;
            }
            else if(this.sType) {
                this.checkExistingLog(); // non Pharmacy views require sType
                this.customMessage = `This ${this.sType} was previously logged to the following case(s): `;
            }
            else if(this.sOrderNumber) { // pharmacy views require order number
                this.customMessage = 'This order was previously logged to the following case(s): ';
                this.checkExistingCaseComment(); 
            }
        }
    }

    checkExistingLog(){
        getAttachmentLogs({sMemberId: this.sMemberId})
            .then(result => {
                const caseIds = new Set();
                this.attachmentLogs = result;
                this.attachmentLogs.forEach(item => {
                    let sSelectedValue = '';
                        if(item.User_Selected_Value__c) {
                            sSelectedValue = item.User_Selected_Value__c.trim();
                        }
                        if(item.User_Selected_Value_EXT__c){ 
                            sSelectedValue =+ item.User_Selected_Value_EXT__c.trim();
                        }
                        const relatedFieldValue =this.findRelatedFields(sSelectedValue);
                        const hasUniqueVal = sSelectedValue.includes(this.sUniqueVal);
                        if((item.Attachment_Sub_type__c && item.Attachment_Sub_type__c === this.subtype) || hasUniqueVal || relatedFieldValue === this.sUniqueVal) {
                            // resgister into the unique array
                                this.relatedFieldValues.set(relatedFieldValue, item);
                                if(!caseIds.has(item.Case__c))
                                {
                                    caseIds.add(item.Case__c);
                                    this.cases.add(item.Case__r);
                                    this.isExisting = true;
                                }
                        }
                    
                });
            })
            .catch(error => {
            })
    }

    checkExistingCaseComment(){
        getCaseComments({sMemberId: this.sMemberId})
        .then(result => {
            const caseIds = new Set();
            result.forEach(item => {
                if(item.CommentBody.includes(this.sOrderNumber.trim())){
                if(!caseIds.has(item.ParentId))
                    {
                    caseIds.add(item.ParentId);
                    this.cases.add(item.Parent);
                    this.isExisting = true;
                    }
                }
            })
        })
        .catch(error => {
        })
    }

    // NAVIGATION 
    openCaseSubTab(event)
    {
        const caseId = event.target.dataset.caseid;
        this[NavigationMixin.Navigate]({
            type: 'standard__recordPage',
            attributes: {
                recordId: caseId,
                actionName: 'view'
            },
            state: {
                navigationLocation: 'RELATED_TAB'
            }
        });
    }
// JSON HANDLING
    findRelatedFields(data) {
        var relatedField = null;

        function searchForRelatedField(obj){
        if(!relatedField && typeof obj === 'object') {
            if(obj.hasOwnProperty('relatedField')) {
                relatedField = JSON.stringify(obj.relatedField[0].value);
                return obj.relatedField[0].value;
            } else {
                for (let key in obj) {
                    searchForRelatedField(obj[key]);
                }
            }
        }
        }
        try {
            searchForRelatedField(JSON.parse(data));
        } catch(e) {
        }
        return relatedField;
    }
  
    closeAlert()
    {
        this.isExisting = false;
    }
 

}