/*
LWC Name: GenericCopyIconHum
Function: generic component to provide copy to clipboard functionality ;

Modification Log:
* Developer Name                  Date                         Description
* ------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     9/7/2022                   initial version
 
**************************************************************************************************************************** */
import { LightningElement ,api} from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
export default class GenericCopyIconHum extends LightningElement {

    @api copycontent;

    handleClick(event) {
        const txtarea = document.createElement('textarea')
        txtarea.value = this.copycontent;
        document.body.appendChild(txtarea);
        txtarea.select();
        document.execCommand('copy');
        document.body.removeChild(txtarea);
        const evt = new ShowToastEvent({
            title: "This text has been copied.",
            message: "",
            variant: "success",
            mode : 'pester'
        });
        this.dispatchEvent(evt);
    }
}