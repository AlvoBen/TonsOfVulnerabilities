import { LightningElement, api,track,wire} from 'lwc';
import getRelatedFiles from '@salesforce/apex/CaseAttachments_LC_Hum.getRelatedFiles'
import verifyLegacyDelete from '@salesforce/apex/CaseAttachments_LC_Hum.verifyLegacyDelete'
import deleteSelectedRow from '@salesforce/apex/CaseAttachments_LC_Hum.deleteSelectedRow'
import {ShowToastEvent} from 'lightning/platformShowToastEvent';
import { openSubTab  } from 'c/workSpaceUtilityComponentHum';
import strUserId from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import {getRecord} from 'lightning/uiRecordApi';
import CASE_NUMBER from '@salesforce/schema/Case.CaseNumber';
import HUMNoRecords from "@salesforce/label/c.HUMNoRecords";

const actionsSup = [    
    { label: 'Delete', name: 'delete' },
];
const actionsSpe = [    
    { label: 'No Action Available', name: 'No Action Available' },
];

export default class CaseAttachHum extends LightningElement  {

    @api recordslength;
    @api issubtab;
    @api recordId;
    @track oViewAllParams;    
    filesList =[];
    attachmentSize;
    viewAllVisibility=false;
    viewDataTableNoRows=false;    
    subTabView=true;
    profileVisibility=false;
    uploadButtonVisibility = true;
    @track profileName;
    @track caseNo;
    goTOCaseLabel;
    NoRecordsToDisplayMsg;
    userId = strUserId;

    columnsSup = [
        { label: 'File Name', fieldName: 'sUrl', type: 'button',target:'_blank',
        typeAttributes: {label: { fieldName: 'sName' }, name : 'fileAction',variant:'base'}, hideDefaultActions: true},
        { label: 'Created Date', fieldName: 'sCreatedDate', type: 'text', hideDefaultActions: true},
        { label: 'Created By', fieldName: 'sUrlCreated', type: 'url',
        typeAttributes: {label: { fieldName: 'sCreatedByName' }, },hideDefaultActions: true},
        { label: 'Created By Queue', fieldName: 'sCreatedByQueue', type: 'text', hideDefaultActions: true},
        { type: 'action', typeAttributes: { rowActions: actionsSup } }, 
        ];
        columns = [
            { label: 'File Name', fieldName: 'sUrl', type: 'button',target:'_top',
            typeAttributes: {label: { fieldName: 'sName' }, name : 'fileAction',variant:'base' }, hideDefaultActions: true},
            { label: 'Created Date', fieldName: 'sCreatedDate', type: 'text', hideDefaultActions: true},
            { label: 'Created By', fieldName: 'sUrlCreated', type: 'url',
            typeAttributes: {label: { fieldName: 'sCreatedByName' }, }, hideDefaultActions: true},
            { label: 'Created By Queue', fieldName: 'sCreatedByQueue', type: 'text', hideDefaultActions: true},
            { type: 'action', typeAttributes: { rowActions: actionsSpe } }, 
        ];

    connectedCallback(){
        
        this.getAttachments();
        this.getVerifyLegacyDelete();
        
              
    }
    @wire(getRecord, { recordId: '$recordId', fields: [CASE_NUMBER] })
   
   wireCaseNumber({
    error,
    data
        }) {
            if (error) {
            this.error = error ; 
            } else if (data) {
                
                    this.caseNo =data.fields.CaseNumber.value
                    this.goTOCaseLabel = 'Case > '+this.caseNo;
              
            }
}

    // get the profile name of the logged in user from wire
        @wire(getRecord, {
         recordId: strUserId,
         fields: [PROFILE_NAME_FIELD]
     }) wireuser({
         error,
         data
     }) {
         if (error) {
            this.error = error ; 
         } else if (data) {
             this.profileName =data.fields.Profile.value.fields.Name.value; 
             this.profileVisibility = (this.profileName =='Customer Care Supervisor') ? true : false;
                     
         }
     }

    

    
    
    
    handleUploadFinished(event) {
        // Get the list of uploaded files
        const uploadedFiles = event.detail.files;
        let uploadedFileNames = '';
        for(let i = 0; i < uploadedFiles.length; i++) {
            uploadedFileNames += uploadedFiles[i].name + ', ';
        }
           
        this.dispatchEvent(
            new ShowToastEvent({
                title: 'Success',
                message: uploadedFiles.length + ' Files uploaded Successfully ',
                variant: 'success',
            }),
        );
        this.getAttachments();        
        
        
        
       
    }

    

   async getAttachments(){
     // get all the custom attachment records associated with the case
     
        let getAttRes =  await getRelatedFiles ({recordId: this.recordId})
        this.filesList = getAttRes;
        // to get the url
        this.filesList = getAttRes.map(item => ({ 
                ...item,
                sUrl : item.sUrl
        }))
        // to display the size of the attachment based on main and subtab
        this.attachmentSize = getAttRes ? getAttRes.length : 0; 
        if(this.attachmentSize > 6 && this.issubtab==undefined){
            this.attachmentSize='6+';
            this.filesList=this.filesList.slice(0,6); 
        }else{ 
            if(this.issubtab==true){
            this.attachmentSize=getAttRes.length;
            this.filesList=getAttRes;
            }
        }
        // to show the view all link
        if(this.filesList.length > 0){ 
            this.recordslength=true;
            this.viewAllVisibility=true;
        }else{
            this.viewAllVisibility=false;
        }
        // to pass parameters to subtab
        this.oViewAllParams = {
            sRecordId: this.recordId,
            sRecordsLength: false,
        }
        // to display no records to display message
        if(this.filesList.length < 1){
           this.viewDataTableNoRows=true;
           this.NoRecordsToDisplayMsg=HUMNoRecords;
        }else{
           this.viewDataTableNoRows=false;    
        }
    }
        //This method is to check whether upload button should be disabled.  
        async getVerifyLegacyDelete(){
            let getLegacyDelete =  await verifyLegacyDelete ({recordId: this.recordId})
            if(getLegacyDelete == true){
                this.uploadButtonVisibility=false;
            }   
        }
       
   
     
/**
   * Method: handleViewAllURL
   * @param {*} event 
   * Function: this method is used to navigate and open new tab on clicked view all
*/
    async onViewAllClick(event) {       
        openSubTab({
            nameOfScreen: 'CaseAttachment',
            title: 'Attachments',
            oParams: {
                ...this.oViewAllParams
            },
            icon: 'standard:attach',
        }, undefined, this);
    }
    // open the url in a new window and to perform delete
    handleRowAction( event ) {
                
        const actionName = event.detail.action.name;
        const row = event.detail.row;
            
        let sAttid =JSON.stringify(row.sAttId);
        if(event.detail.action.name == 'fileAction'){
            window.open(row.sUrl)
        }
  
        if ( event.detail.action.name == 'delete' ) {
            this.deleteRow(sAttid);            
        }

    }
    // to perfrom delete of an attachment
    async deleteRow(sAttid){
        const res = await deleteSelectedRow({sAttIdDel :sAttid})
        if(res == true){
            this.dispatchEvent(
                new ShowToastEvent({
                    title: 'Success',
                    message: 'Files Successfully Deleted',
                    variant: 'success',
                }),
            ); 
        }
        this.getAttachments();
       }
    
      // to return to the main tab when clicked on case number in subtab
      goBackToCase(event){
        event.preventDefault();
        this.onHyperLinkClick();        
      }

     onHyperLinkClick(event){
        let data = {title: 'Case',nameOfScreen:'Case'};
        let pageReference = {
               type: 'standard__recordPage',
               attributes: {
                   recordId: this.recordId,
                   objectApiName: 'case',
                   actionName: 'view'
               }
        } 
        // open subtab
        openSubTab(data, undefined, this, pageReference, {openSubTab:true,isFocus:true,callTabLabel:false,callTabIcon:false});
        this.getAttachments();
    }
 

}