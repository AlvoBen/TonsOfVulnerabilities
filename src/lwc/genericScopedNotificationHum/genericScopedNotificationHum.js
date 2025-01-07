/******************************************************************************************************************
LWC Name           : genericScopedNotificationHum.js
Version            : 1.0
Function           : Component to display generic scoped notification.
Created On         : 05/13/2022
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal garg                                     05/13/2022                Original Version
*******************************************************************************************************************/

import { api, LightningElement, track } from 'lwc';

export default class GenericScopedNotificationHum extends LightningElement {
    @api
    iconname;

    @api
    message;

    @api
    type;

    @track variant='';

    get customcss(){
      switch(this.type){
        case "info" :
            return "slds-scoped-notification slds-media slds-media_center slds-scoped-notification_light";
        case "error" :
            this.variant = 'inverse';
            return "slds-scoped-notification slds-media slds-media_center slds-theme_error";
        case "warning" :
            this.variant = 'inverse';
            return "slds-scoped-notification slds-media slds-media_center slds-theme_warning";
        case "success" :
            this.variant = 'inverse';
            return "slds-scoped-notification slds-media slds-media_center slds-theme_success";
        default :
            return "slds-scoped-notification slds-media slds-media_center slds-scoped-notification_light";
        }
    }
}