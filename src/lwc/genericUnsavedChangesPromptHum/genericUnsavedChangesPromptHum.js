/******************************************************************************************************************
LWC Name           : genericUnsavedChangesPromptHum.html
Version            : 1.0
Function           : Component to display generic unsaved changes 
Created On         : 05/13/2022
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal garg                                     05/13/2022                Original Version
*******************************************************************************************************************/

import { LightningElement, api } from 'lwc';

export default class GenericUnsavedChangesPromptHum extends LightningElement {

    @api 
    buttonConfig;

    @api
    header;

    @api
    promptclass;

    @api
    message;

    
    handleClick(event){
        let buttonLabel = event.target.label;
        let eventName = event.target.dataset.event;
        this.dispatchEvent(new CustomEvent(eventName));
    }
}