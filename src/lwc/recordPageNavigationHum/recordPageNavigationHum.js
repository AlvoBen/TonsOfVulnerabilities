/********************************************************************************************************************************
File Name       : recordPageNavigationHum.js
Version         : 1.0
Created On      : 12/09/2021
Function        : Navigate to open the subtab

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*******************************************************************************************************************************
* Ranadheer Alwal                                      12/07/2021                  T1PRJ0002606- MF6 - SF - TECH - DF-3873 Link parameter is not passed from link framework when a LWC component is opened in a subtab 
******************************************************************************************************************************/

import { LightningElement, api, wire } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import { publish, MessageContext } from 'lightning/messageService';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/closeSubTabHUM__c';
export default class RecordPageNavigationHum extends NavigationMixin(LightningElement) {
    @api recordId;
    @api objectApiName;

    @wire(MessageContext)
    messageContext;

    connectedCallback(){
                
    }

    renderedCallback(){
        const payload = { recordId :this.recordId };
        publish(this.messageContext, CONNECTOR_CHANNEL, payload);
        
    }
}