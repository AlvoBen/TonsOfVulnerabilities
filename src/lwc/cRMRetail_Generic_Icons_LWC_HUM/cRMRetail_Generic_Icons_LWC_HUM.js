import { LightningElement, api } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {labels,allConstants} from 'c/crmretail_interactionutility_LWC_HUM';
import getIconValue from '@salesforce/apex/CRMRetail_Generic_Icon_LC_HUM.showIcons';
var customLabels = labels;
var constants = allConstants;
export default class CRMRetail_Generic_Icons_LWC_HUM extends LightningElement {
    @api recordId;
    iconArr;
    switchValue = false;

    connectedCallback(){
        this.processInitial();
    }
    async processInitial()
    {
        try{       
            this.fetchIconValue();
        }catch(error){
            this.generateToastMessage(customLabels.CRMRetail_Error_Label , customLabels.UNEXPECTED_ERROR,customLabels.ERROR_VARIANT,constants.STICKY); 
        }        
    }

    fetchIconValue(){
        getIconValue({recId: this.recordId})
        .then((res) => {
            if(!res.isError)
            {
                let list = [];
                let response = JSON.parse(res.sResult);
                Object.keys(response).forEach(element =>
                {
                    if (response[element].isError == 'true') {
                        this.generateToastMessage( customLabels.CRMRetail_Error_Label, response[element].errorMsg,customLabels.ERROR_VARIANT,constants.STICKY);    
                    }
                    else {
                        list.push(
                            {
                                label  : element,
                                icon   : response[element].icon,
                                show   : (response[element].show == 'true'),
                                value  : response[element].value,
                                isIcon : (response[element].icon ? true  :false),
                                style  : '--sds-c-icon-color-foreground-default : ' + response[element].color,
                                order  : parseInt(response[element].order)
                            }
                        ); 
                    }                                       
                })               
                this.iconArr = list.sort((a, b) => a.order - b.order);                                
            }
            else{
                this.generateToastMessage(customLabels.CRMRetail_Error_Label , customLabels.UNEXPECTED_ERROR,customLabels.ERROR_VARIANT,constants.STICKY);    
            }
            
        })
        .catch(error =>{
            this.generateToastMessage(customLabels.CRMRetail_Error_Label , customLabels.UNEXPECTED_ERROR,customLabels.ERROR_VARIANT,constants.STICKY);  
        });
    }

    generateToastMessage(title, msg, type,sMode) {
        const event = new ShowToastEvent({
            title: title,
            message: msg,
            variant : type,            
            mode : sMode
        });
        this.dispatchEvent(event);
    }
}