import { LightningElement,api,track } from 'lwc';
import required_msg_HUM from '@salesforce/label/c.required_msg_HUM';
export default class GenericComboBoxHum extends LightningElement {
    @api required;
    @api options;
    @api message;
    @api header;
    @api placeholder;
    @api bShowErrorMsg;
    @track optionsSelected;
    @track optionData = [];
    @track message;
    @track showDropdown = false;
    @track value;

    labels = {
        required_msg_HUM
    }
    
    connectedCallback(){
        this.placeholder = this.placeholder ?? 'Select an Option';
    }

    selectItem(event){
        this.bShowErrorMsg = false;
        if (event?.currentTarget?.dataset?.value) {
            let optionvalues = JSON.parse(JSON.stringify(this.options));
            for (let i = 0; i < optionvalues.length; i++) {                
                if (optionvalues[i].value === event.currentTarget.dataset.value) {
                    optionvalues[i].selected = optionvalues[i].selected ? false : true;
                    if(optionvalues[i].selected){
                        this.value = optionvalues[i].value;
                    }else{
                        this.value=null;
                    }
                }
            }
            this.options = optionvalues;
            this.dispatchEvent(new CustomEvent('selectitem',{
                detail : {
                    label : event.currentTarget.dataset.label,
                    value : event.currentTarget.dataset.value
                }
            }))
        }
        this.optionsSelected = event?.currentTarget?.dataset?.label??thi.placeholder;
        this.showDropdown=false;
    }

    showOptions() {
        if (this.options != null) {
            if (this.options.length > 0) {
                let optionvalues = JSON.parse(JSON.stringify(this.options));
                for (let i = 0; i < optionvalues.length; i++) {
                    optionvalues[i].isVisible = true;
                    optionvalues[i].selected = this.value && this.value === optionvalues[i].value ? true : false;
                }
                if (optionvalues.length > 0) {
                    this.showDropdown = true;
                }
                this.options = optionvalues;
            }
        }

    }

    blurEvent(event){        
        this.showDropdown=false;
    }

    @api
    displayErrorMessage(){
        this.bShowErrorMsg = true;
    }
    
}