/*
LWC Name        : caseProcessDisplayMsgHum.html
Function        : LWC to display Next Step Message.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Pavan Kumar M.                  06/27/2022                   initial version
* Muthukumar                      09/08/2022                 US-3279519 update plan demographic
****************************************************************************************************************************/

import { LightningElement,api,track } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';


export default class caseProcessDisplayMsgHum extends NavigationMixin(LightningElement) {
    @api message;
    @api headerMessage;
    @api iconType;
    @api helpTextMsg;
    @track showIcon;
    @track showHelpText;
    @track iconColour;

    
    connectedCallback(){
        if(this.iconType){
            this.showIcon = this.iconType;
            this.showHelpText = this.helpTextMsg;
            if(this.iconType ==='utility:warning'){
                this.iconColour = 'warning-icon-color';
            }
            if(this.iconType ==='utility:success'){
                this.iconColour = 'success-icon-color';
            }
        }else{
            this.showIcon = 'utility:info';
            this.showHelpText = 'Info';
        }
    }
}