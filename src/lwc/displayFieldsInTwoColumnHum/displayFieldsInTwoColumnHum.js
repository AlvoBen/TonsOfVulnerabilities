/*******************************************************************************************************************************************
LWC Name        : displayFieldsInTwoColumnHum.js
Function        : To show fields in two column format

Modification Log:
* Developer Name                     Date                         Description
* Muthukumar                         02/05/2022                   US-3255798 Original Version 
* Prasuna Pattabhi             02/01/2023                      US-4178418 : TRR Process Template on Case Details Page
******************************************************************************************************************************************/

import { LightningElement,api,track } from 'lwc';

export default class DisplayFieldsInTwoColumnHum extends LightningElement {
    @api sectionHeader;
    @api sectionData; 
    @track summarydetails = false;
	@track twosection = true;    
    @track trrSummaryDetails = false;

    connectedCallback(){
       if(this.sectionHeader ==='Summary of Coverages') {
			this.summarydetails = true;
			this.twosection = false;
		}else if(this.sectionHeader === 'TRRTemplate'){
            this.trrSummaryDetails = true;
            this.twosection = false;
        }
    }
}