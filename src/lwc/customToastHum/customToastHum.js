/******************************************************************************************************************************
LWC Name        : customToastHum.html
Function        :  To diplay the Info,Warning,Success,Error Toast message.
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Pavan Kumar M                 03/01/2022                Original Version 
****************************************************************************************************************************/
import { LightningElement,api} from 'lwc'; 
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
export default class SampleToast extends LightningElement {
@api title;
@api variant;
@api message ;
@api  showEventMsg() {
     const toastModel = this.template.querySelector('[data-id="toastModel"]');
     console.log(toastModel);
     toastModel.className = 'slds-show';
}
@api closeModel() {
            const toastModel = this.template.querySelector('[data-id="toastModel"]');
            toastModel.className = 'slds-hide';
}  
get mainDivClass() { 
   return 'slds-notify slds-notify_toast slds-theme_'+this.variant;
}

get messageDivClass() { 
    return 'slds-icon_container slds-icon-utility-'+this.variant+' slds-icon-utility-success slds-m-right_small slds-no-flex slds-align-top';
}
get iconName() {
    return 'utility:'+this.variant;
}
        
}