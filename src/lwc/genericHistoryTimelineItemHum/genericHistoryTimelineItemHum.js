/*
LWC Name        : PharmacyHistoryTimelineFilterHum.html
Function        : LWC to display pharmacy history timeline data;

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     11/09/2021                   initial version - US - 2527241
* M K Manoj                       07/27/2022                 US-3522143,3495639 For passing Case Record Type to child cmp
* Aishwarya Pawar                 09/02/2022                 initial version - US - 3668178
* Ashish/Kajal                    08/11/2022                    Added condition for archived case comments
* Prasuna Pattabhi                 08/24/23                 US 4412371 Market Credentialing Task Fields
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import updateRecord from '@salesforce/apex/CaseCommentsDataTable_LC_HUM.updateRecord';
import deleteRecord from '@salesforce/apex/CaseCommentsDataTable_LC_HUM.deleteRecord';
import getCaseStatus from '@salesforce/apex/CaseCommentsDataTable_LD_HUM.getCaseRecordStatus';
import { toastMsge } from 'c/crmUtilityHum';
import credentialingClosable from '@salesforce/apex/CaseCommentsDataTable_LD_HUM.credentialingClosable'; 
import Credentialing_ERROR_MSG from '@salesforce/label/c.Credentialing_ERROR_MSG';

export default class GenericHistoryTimelineItemHum extends LightningElement {
    isCaseOpen;
    @api isArchived = false;
    @track lastmodifiedby = 'Last Modified By: ';
    @track createdby = 'Created By: '
    @api hasComments;
    @api caseId;
    @api bLogCodeVisible;
    @api sCaseRecordTypeName;
    @api lstCodes;
    @api
    historylineitemdata;
    @api
    expanded = false;
    enablePopup = false;
    editForm = false;
    deleteForm = false;
    showSpinner = false;
    CredentialingErrorMsg = Credentialing_ERROR_MSG;
    //this is identifier attribute tells flow is invokes from task activity section, it iwll be set to true
    @api showTaskNumberURL =false;
    editFormButtonsConfig = [{
        text: 'Cancel',
        isTypeBrand: false,
        eventName: 'close'
    }, {
        text: 'Save',
        isTypeBrand: true,
        eventName: 'save'
    }];
    deleteFormButtonsConfig = [{
        text: 'Cancel',
        isTypeBrand: false,
        eventName: 'close'
    }, {
        text: 'Delete',
        isTypeBrand: true,
        eventName: 'delete'
    }];
    @api expandevent;
    @api 
    refreshLineitem(toggle){
        this.expanded = toggle;
    }
    get expandCollapseIcon(){
        return this.expanded ? 'utility:switch' : 'utility:chevronright';    
    }

    toggleDetailSection() {
        if (!this.expanded && this.expandevent) {
            this.dispatchEvent(new CustomEvent('expandclick', {
                detail: {
                    data: this.historylineitemdata
                }, bubbles: true, composed: true
            }))
        }
        this.expanded = !this.expanded;
    } 

    connectedCallback() {
        this.expanded = (this.historylineitemdata && this.historylineitemdata?.expanded) ? true : false;
        if(!this.isArchived)
        {
         this.checkisCaseOpen();
        }
        
        if(this.caseId && this.caseId.startsWith('00T')){
            this.isItTask = true;
        }
        else{
            this.isItTask = false;
        }  
    }
    get showTimeLineConnectorColor(){
        return this.showTaskNumberURL ?  'slds-timeline__item_event slds-timeline__item_expandable slds-is-open' :
                             'slds-timeline__item_task slds-timeline__item_expandable slds-is-open'
    }
    navigateToRecordPage(event){
        event.preventDefault();
        let event1 = new CustomEvent('passcommentid', { detail: { value: this.historylineitemdata.commentID }, bubbles: true, composed: true });
        this.dispatchEvent(event1);
    }
    get subheaderlinedetails(){
        return this.historylineitemdata.subheaderline != null && this.historylineitemdata.subheaderline.length > 100 ?
            this.historylineitemdata.subheaderline.substring(0, 100) : this.historylineitemdata.subheaderline;
    }
    get headerLineClass() {
        return (this.historylineitemdata && this.historylineitemdata?.wrapheaderline) ? "slds-cell-wrap" : "slds-truncate";
    }
    get headerlinedetails(){
    	if (this.hasComments) {
            if(this.showTaskNumberURL){
                return  this.historylineitemdata && this.historylineitemdata?.headerline ? this.historylineitemdata.headerline : ''
            }
            else{
                return this.historylineitemdata && this.historylineitemdata?.headerline ? this.createdby + this.historylineitemdata.headerline : ''
            }
        } else {
            return this.historylineitemdata && this.historylineitemdata?.headerline ? this.historylineitemdata.headerline : ''
        }
    	
    }
    
    get createddatetime(){
    	return this.historylineitemdata && this.historylineitemdata?.createddatetime ? this.historylineitemdata.createddatetime : '';
    }
    
    get createdbydatetime(){
    	return this.historylineitemdata && this.historylineitemdata?.createdbydatetime ? this.historylineitemdata.createdbydatetime : '';
    }
    deleteHandler() {
        this.enablePopup = true;
        this.deleteForm = true;
        this.editForm = false;
    }

    editHandler() {
        this.enablePopup = true;
        this.editForm = true;
    }

    get formTitle() {
        return this.editForm ? 'Edit Comment' : 'Delete Comment';
    }

    get buttonsConfig() {
        return this.editForm ? this.editFormButtonsConfig : this.deleteFormButtonsConfig;
    }

    @api
    closeModal() {
        this.showSpinner = false;
        this.enablePopup = false;
        this.editForm = false;
        this.deleteForm = false;
    }

    deleteComment() {
        this.showSpinner = true;
        let sObjectToDelete;
        if(this.isItTask){
            sObjectToDelete = 'Task_Field_History__c';
        }
        else{
            sObjectToDelete = 'CaseComment';
        }
        deleteRecord({
            deleteID: this.historylineitemdata.commentID,
            objectName: sObjectToDelete
        }).then((result => {
            if (result) {
                let event = new CustomEvent('deletecomment', { detail: { value: "" }, bubbles: true, composed: true });
                this.dispatchEvent(event);
                this.closeModal();
            }
        }));
    }

    saveForm() {
        this.template.querySelector('c-comments-form-hum').hasData();
    }
    async handleTaskActivityActions(event){
        const buttonIndex = event.currentTarget.getAttribute('data-id');
        if(buttonIndex=='closetask'){
            let result = await credentialingClosable({Id:this.historylineitemdata.commentID});
            if(result){
                toastMsge('', this.CredentialingErrorMsg, 'error', 'dismissable');
                return;
            }
        }
        let cevent = new CustomEvent(buttonIndex, { detail: { value: this.historylineitemdata.commentID }, bubbles: true, composed: true });
        this.dispatchEvent(cevent);
    }

    /**
   * Process user inputs on new comments
   * modal on click of save
   * @param {*} event 
   */
    async modifiedHandler(event) {
        
        if (event.detail) {
            const inputComment = Object.values(event.detail)[1] ? '('+Object.values(event.detail)[0]+') '+Object.values(event.detail)[1] : Object.values(event.detail)[0] ;
            this.showSpinner = true;
            updateRecord({
                updateId: this.historylineitemdata.commentID,
                sCommentBody: inputComment,
                sCaseTask: this.caseId
            }).then((result => {
                if (result) {
                    let event = new CustomEvent('saveedit', { detail: { value: "" }, bubbles: true, composed: true });
                    this.dispatchEvent(event);
                    setTimeout(() =>  this.closeModal());
                    toastMsge("", "Comment was successfully saved", "success", "dismissable");
                }
            }));
        }
    }

@api
 checkisCaseOpen(){
        //console.log('case id inside isCaseopen',this.caseId);
         getCaseStatus({objID: this.caseId}).then(result =>{
             if(result == 'Closed'  || result == 'Cancelled'){ 
                this.isCaseOpen =  false;
            }
            else {
                this.isCaseOpen =  true;
            } 
              
        })
    }
}