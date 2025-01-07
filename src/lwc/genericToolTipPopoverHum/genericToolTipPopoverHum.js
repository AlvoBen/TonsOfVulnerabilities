/*
LWC Name        : genericToolTipPopOverHum
Function        : LWC component created to display tooltip

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                07/14/2022                    Original Version
*****************************************************************************************************************************/
import { api, LightningElement } from 'lwc';

export default class GenericToolTipPopoverHum extends LightningElement {

    @api
    tooltipbody;

    @api
    tooltipclass;

    @api
    tooptipstyle;

    get toolTipClass() {
        if(this.tooltipclass){
            return `slds-popover slds-popover_tooltip ${this.tooltipclass}`;
        }else{
            return 'slds-popover slds-popover_tooltip slds-nubbin_bottom-right slds-slide-from-top-to-bottom';
        }
        
    }

}