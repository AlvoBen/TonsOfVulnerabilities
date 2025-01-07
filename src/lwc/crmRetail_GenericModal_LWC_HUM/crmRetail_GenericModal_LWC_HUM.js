import { api, LightningElement } from 'lwc';
export default class CrmRetail_GenericModal_LWC_HUM extends LightningElement {

    @api openModal;
    @api noHeader;
    @api noFooter;
    @api title;
    @api showClose;
    
    renderedCallback()
    {
        console.log('this.title+',this.title);
    }
    
    closeModal()
    {
        this.openModal = false;
        this.dispatchEvent(new CustomEvent('close'));
    }
}