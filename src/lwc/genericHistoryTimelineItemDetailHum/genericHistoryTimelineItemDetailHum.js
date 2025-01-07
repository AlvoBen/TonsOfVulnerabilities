/*
LWC Name        : PharmacyHistoryTimelineFilterHum.html
Function        : LWC to display pharmacy history timeline data;

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     11/09/2021                   initial version - US - 2527241
* Aishwarya Pawar                 09/02/2022                 initial version - US - 3668178
*****************************************************************************************************************************/
import { LightningElement, api } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import CASE_NOTE from '@salesforce/label/c.Case_New_Existing_Note';
export default class GenericHistoryTimelineItemDetailHum  extends NavigationMixin(LightningElement) {
    @api
    lineitemdetails;
    @api cardComment;
    attachCaseNote = CASE_NOTE;


    // Navigate to Case Record Page
     navigateToRecodPage(event) {
        let element = this.template.querySelector('[data-id='+event.target.dataset.name + 'Id]');
        if(element != null && element != undefined){
            let rId = element.dataset.value;
            this.dispatchEvent(new CustomEvent('linkclick',{
                detail : {
                    objectname : event.target.dataset.name,
                    recordId : rId
                },
                bubbles : true,
                composed : true
            }))
        }
        
    }
    get bodyOuterDivClass() {
        return (this.lineitemdetails && this.lineitemdetails?.footer) ? "slds-grid slds-wrap slds-gutters slds-border_bottom" : "slds-grid slds-wrap slds-gutters ";
    }
    handleButtonIconMouseEnter(event){
        let element = this.template.querySelector(`[data-key="${event.target.dataset.id}"]`);
        if(element){
            if(element.classList.value.includes('slds-hide')){
                element.classList.remove('slds-hide')
                element.classList.add('slds-show')
            }
        }
    }
    
    handleButtonIconMouseLeave(event){
        let element = this.template.querySelector(`[data-key="${event.target.dataset.id}"]`);
        if(element){
            if(element.classList.value.includes('slds-show')){
                element.classList.remove('slds-show')
                element.classList.add('slds-hide')
            }
        }
    }
    
    handleToggle(event) {
        if (!event.target.checked) {
            let interactionKey= this.lineitemdetails.body.find( ({ fieldname }) => fieldname === 'Interaction Id').fieldvalue;
            this.dispatchEvent(new CustomEvent('existingcase', {
                bubbles: true,
                composed: true,        
                detail: {
                    interactionId: interactionKey
                }
            }));
        }
        event.target.checked = true;
    }
    
    handleFinish() {
        let interactionKey= this.lineitemdetails.body.find( ({ fieldname }) => fieldname === 'Interaction Id').fieldvalue;
        this.dispatchEvent(new CustomEvent('attachnew', {        
            bubbles: true,
            composed: true,
            detail: {
                interactionId: interactionKey
            }
        }));
    }
    
    handleCommunication(event){
        let interactionKey= this.lineitemdetails.body.find( ({ fieldname }) => fieldname === 'Interaction Id').fieldvalue;
        if(event.target.dataset.id==="View"||event.target.dataset.id==="Resend"){
        		const ButtonClickEvent= new CustomEvent('viewresendbuttonclick',{detail:{action:event.target.dataset.id,key:interactionKey},bubbles:true,composed: true})
            this.dispatchEvent(ButtonClickEvent);
        }
    }
}