/******************************************************************************************************************************
LWC Name        : ArchivedCaseHighlightPanelHum.js
Function        : LWC to display the highlights panel

Modification Log:
* Developer Name                                Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev /Ashish Kumar                   07/18/2022                    Original Version 
******************************************************************************************************************************/

import { LightningElement,track,api } from 'lwc';
import { getHighlightPanelLayout } from "./layoutConfig";

export default class ArchivedCaseHighlightPanelHum extends LightningElement {
    @track highlightModel = [];
    @api recordId;
    @api caseData;
    @track caseNum;
    connectedCallback(){
        this.highlightModel = getHighlightPanelLayout();
        if(this.caseData){
            this.highlightModel.forEach((field) => {
                field.value = this.caseData[field.mapping];
            });

        var caseNumber= this.caseData.sCaseNumber;
        if(caseNumber !='' && caseNumber != null)
        {
           this.caseNum = caseNumber.substring(0, 3) + '-' + caseNumber.substring(3, 6) + '-' + caseNumber.substring(6, 9) + '-'  + caseNumber.substring(9, 13);
        }
        }
    }
}