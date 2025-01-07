/*******************************************************************************************************************************
Function    : This JS serves as controller to CaseLoggingDetailTableContainerHum.html. 
Modification Log: 
Developer Name             Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi       07/04/2022                    Original Version
*********************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';

export default class CaseLoggingDetailTableContainerHum extends LightningElement {

    @api encodedData;
    @track bShowLogAttachments;
    @track breadCrumbItems = [];

    connectedCallback() {
        switch (this.encodedData.relatedListName) {
            case 'Logged Information':
                this.bShowLogAttachments = true;
                break;       
            default:
        } 
        this.breadCrumbItems = [
            {"label":'Cases',"href":'',"eventname":''},
            {"label":  this.encodedData.caseNumber,"href":'', "eventname":''}
        ];
    }
    
}