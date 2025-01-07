/*
LWC Name        : GenericIllustrationComponentHum
Function        : This is generic component created to display illustration 

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
Nirmal Garg                       07/14/2022                     Original Version
Jonathan Dickinson                02/01/2023                     US-3939434
*****************************************************************************************************************************/

import { api, LightningElement, track } from 'lwc';

export default class GenericIllustrationComponentHum extends LightningElement {
    @api
    type;

    @api
    message;

    @api
    showMessageAtTop;

    @api
    illustrationSizeClass;

    @track nodatamessage=false;

    setIllustrationSize() {
        if (!this.illustrationSizeClass) {
            this.illustrationSizeClass = 'slds-illustration_small';
        }
        const illustration = this.template.querySelector('.slds-illustration');
        if (illustration) {
            illustration.classList.add(this.illustrationSizeClass);
        }
    }

    connectedCallback(){
        switch(this.type){
            case  "nodata" :
                this.nodatamessage = true;
                break;
        }
    }
    
    renderedCallback() {
        this.setIllustrationSize();
    }
}