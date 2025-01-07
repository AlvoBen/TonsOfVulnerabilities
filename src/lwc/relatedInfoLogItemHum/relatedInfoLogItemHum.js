/*******************************************************************************************************************************
LWC JS Name : RelatedInfoLogItemHum.js
Function    : This JS serves as helper to relatedInfoLogItemHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                           10/10/2022                   initial version
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getAttachmentLogItems from '@salesforce/apex/CaseRelatedInfo_LC_HUM.getAttachmentLogItems';

export default class RelatedInfoLogItemHum extends LightningElement {
    @api caseId;
    @api attLogId;
    loggedItemData;
    noLogItemData = false;
    logItemColumns = [
        { label: "Name" },
        { label: "Value" },
    ];

    connectedCallback() {
        this.getCCPGCPAttachmentItems();
    }

    getCCPGCPAttachmentItems() {
        try {
            getAttachmentLogItems({ sAttLogId: this.attLogId })
                .then(result => {
                    if (result != null && result != undefined) {
                        this.loggedItemData = result;
                        if (this.loggedItemData.length == 0) {
                            this.noLogItemData = true;
                        }
                    }
                })
        }
        catch (error) {
            console.log(error);
        }
    }
}